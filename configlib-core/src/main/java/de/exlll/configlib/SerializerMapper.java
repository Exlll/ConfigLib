package de.exlll.configlib;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.exlll.configlib.Validator.requireNonNull;

/**
 * A mapper that maps field or component names to serializers that are selected based on
 * the field or component type, respectively.
 */
final class SerializerMapper {
    private final Class<?> type;
    private final ConfigurationProperties properties;
    private final SerializerSelector selector;

    SerializerMapper(Class<?> type, ConfigurationProperties properties) {
        this.type = requireNonNull(type, "type");
        this.properties = requireNonNull(properties, "configuration properties");
        this.selector = new SerializerSelector(properties);
        requireConfigurationOrRecord();
    }

    private void requireConfigurationOrRecord() {
        if (!type.isRecord() && !Reflect.isConfiguration(type)) {
            String msg = "Type '%s' must be a configuration or record type."
                    .formatted(type.getSimpleName());
            throw new ConfigurationException(msg);
        }
    }

    public Map<String, Serializer<?, ?>> buildSerializerMap() {
        return type.isRecord()
                ? buildSerializerMapForRecord()
                : buildSerializerMapForConfiguration();

    }

    private Map<String, Serializer<?, ?>> buildSerializerMapForRecord() {
        return tryBuildSerializerMap(
                Arrays.stream(type.getRecordComponents()),
                RecordComponent::getName,
                RecordComponent::getGenericType
        );
    }

    private Map<String, Serializer<?, ?>> buildSerializerMapForConfiguration() {
        return tryBuildSerializerMap(filterFields(), Field::getName, Field::getGenericType);
    }

    private <T> Map<String, Serializer<?, ?>> tryBuildSerializerMap(
            Stream<T> stream,
            Function<T, String> nameExtractor,
            Function<T, Type> typeExtractor
    ) {
        try {
            return stream.collect(Collectors.toMap(
                    nameExtractor,
                    element -> selector.select(typeExtractor.apply(element))
            ));
        } catch (StackOverflowError error) {
            String msg = "Recursive type definitions are not supported.";
            throw new ConfigurationException(msg, error);
        }
    }

    private Stream<Field> filterFields() {
        return FieldExtractors.CONFIGURATION.extract(type)
                .filter(properties.getFieldFilter());
    }
}
