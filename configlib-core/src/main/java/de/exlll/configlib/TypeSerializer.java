package de.exlll.configlib;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.exlll.configlib.Validator.requireNonNull;

sealed abstract class TypeSerializer<T, TC extends TypeComponent<?>>
        implements Serializer<T, Map<?, ?>>
        permits ConfigurationSerializer, RecordSerializer {
    protected final Class<T> type;
    protected final ConfigurationProperties properties;
    protected final NameFormatter formatter;
    protected final Map<String, Serializer<?, ?>> serializers;

    protected TypeSerializer(Class<T> type, ConfigurationProperties properties) {
        this.type = requireNonNull(type, "type");
        this.properties = requireNonNull(properties, "configuration properties");
        this.formatter = properties.getNameFormatter();
        this.serializers = buildSerializerMap();
        requireSerializableComponents();
    }

    static <T> TypeSerializer<T, ?> newSerializerFor(
            Class<T> type,
            ConfigurationProperties properties
    ) {
        return type.isRecord()
                ? new RecordSerializer<>(type, properties)
                : new ConfigurationSerializer<>(type, properties);
    }

    Map<String, Serializer<?, ?>> buildSerializerMap() {
        final var selector = new SerializerSelector(properties);
        try {
            return components().stream().collect(Collectors.toMap(
                    TypeComponent::name,
                    selector::select
            ));
        } catch (StackOverflowError error) {
            String msg = "Recursive type definitions are not supported.";
            throw new ConfigurationException(msg, error);
        }
    }

    @Override
    public final Map<?, ?> serialize(T element) {
        final Map<String, Object> result = new LinkedHashMap<>();

        for (final TC component : components()) {
            final Object componentValue = component.value(element);

            if ((componentValue == null) && !properties.outputNulls())
                continue;

            final Object serializedValue = serialize(component, componentValue);
            final String formattedName = formatter.format(component.name());
            result.put(formattedName, serializedValue);
        }

        return result;
    }

    protected final Object serialize(TC component, Object value) {
        // The following cast won't cause a ClassCastException because the serializers
        // are selected based on the component type.
        @SuppressWarnings("unchecked")
        final var serializer = (Serializer<Object, Object>)
                serializers.get(component.name());
        return (value != null) ? serializer.serialize(value) : null;
    }

    protected final Object deserialize(TC component, Object value) {
        // This unchecked cast leads to an exception if the type of the object which
        // is deserialized is not a subtype of the type the deserializer expects.
        @SuppressWarnings("unchecked")
        final var serializer = (Serializer<Object, Object>)
                serializers.get(component.name());

        final Object deserialized;
        try {
            deserialized = serializer.deserialize(value);
        } catch (ClassCastException e) {
            String msg = baseDeserializeExceptionMessage(component, value) + "\n" +
                         "The type of the object to be deserialized does not " +
                         "match the type the deserializer expects.";
            throw new ConfigurationException(msg, e);
        } catch (RuntimeException e) {
            String msg = baseDeserializeExceptionMessage(component, value);
            throw new ConfigurationException(msg, e);
        }
        return deserialized;
    }

    protected abstract void requireSerializableComponents();

    protected abstract String baseDeserializeExceptionMessage(TC component, Object value);

    protected abstract List<TC> components();

    abstract T newDefaultInstance();
}
