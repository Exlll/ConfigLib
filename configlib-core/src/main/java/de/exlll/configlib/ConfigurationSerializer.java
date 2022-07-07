package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class ConfigurationSerializer<T> implements Serializer<T, Map<?, ?>> {
    private final Class<T> configurationType;
    private final ConfigurationProperties properties;
    private final Map<String, Serializer<?, ?>> serializers;

    ConfigurationSerializer(Class<T> configurationType, ConfigurationProperties properties) {
        this.configurationType = Validator.requireConfiguration(configurationType);
        this.properties = properties;
        this.serializers = buildSerializerMap();
        requireSerializableFields();
    }

    private void requireSerializableFields() {
        if (serializers.isEmpty()) {
            String msg = "Configuration class '" + configurationType.getSimpleName() + "' " +
                         "does not contain any (de-)serializable fields.";
            throw new ConfigurationException(msg);
        }
    }

    @Override
    public Map<?, ?> serialize(T element) {
        final Map<String, Object> result = new LinkedHashMap<>();

        for (final Field field : filterFields()) {
            final Object fieldValue = Reflect.getValue(field, element);

            if ((fieldValue == null) && !properties.outputNulls())
                continue;

            final Object serializedValue = serialize(field, fieldValue);

            final String formattedField = properties.getFieldFormatter().format(field);
            result.put(formattedField, serializedValue);
        }

        return result;
    }

    private Object serialize(Field field, Object fieldValue) {
        // The following cast won't cause a ClassCastException because we select the
        // serializers based on the field type.
        @SuppressWarnings("unchecked")
        final Serializer<Object, Object> serializer =
                (Serializer<Object, Object>) serializers.get(field.getName());

        return (fieldValue != null) ? serializer.serialize(fieldValue) : null;
    }

    @Override
    public T deserialize(Map<?, ?> element) {
        final T result = Reflect.newInstance(configurationType);
        for (final Field field : filterFields()) {
            final String formattedField = properties.getFieldFormatter().format(field);

            if (!element.containsKey(formattedField))
                continue;

            final Object value = element.get(formattedField);

            if (value == null && properties.inputNulls()) {
                requireNonPrimitiveFieldType(field);
                Reflect.setValue(field, result, null);
            } else if (value != null) {
                final Object deserialized = deserialize(field, value);
                Reflect.setValue(field, result, deserialized);
            }
        }
        return result;
    }

    private Object deserialize(Field field, Object value) {
        // This unchecked cast leads to an exception if the type of the object which
        // is deserialized is not a subtype of the type the deserializer expects.
        @SuppressWarnings("unchecked")
        final Serializer<Object, Object> serializer =
                (Serializer<Object, Object>) serializers.get(field.getName());

        final Object deserialized;
        try {
            deserialized = serializer.deserialize(value);
        } catch (ClassCastException e) {
            String msg = baseDeserializeExceptionMessage(field, value) + "\n" +
                         "The type of the object to be deserialized does not " +
                         "match the type the deserializer expects.";
            throw new ConfigurationException(msg, e);
        } catch (RuntimeException e) {
            String msg = baseDeserializeExceptionMessage(field, value);
            throw new ConfigurationException(msg, e);
        }
        return deserialized;
    }

    private static String baseDeserializeExceptionMessage(Field field, Object value) {
        return "Deserialization of value '" + value + "' with type " + value.getClass() + " " +
               "for field " + field + " failed.";
    }

    private static void requireNonPrimitiveFieldType(Field field) {
        if (field.getType().isPrimitive()) {
            String msg = "Cannot set " + field + " to null value.\n" +
                         "Primitive types cannot be assigned null.";
            throw new ConfigurationException(msg);
        }
    }

    private List<Field> filterFields() {
        return FieldExtractors.CONFIGURATION.extract(configurationType)
                .filter(properties.getFieldFilter())
                .toList();
    }

    Map<String, Serializer<?, ?>> buildSerializerMap() {
        SerializerSelector selector = new SerializerSelector(properties);
        try {
            return filterFields().stream()
                    .collect(Collectors.toMap(
                            Field::getName,
                            field -> selector.select(field.getGenericType()),
                            (serializer1, serializer2) -> serializer2
                    ));
        } catch (StackOverflowError error) {
            String msg = "Recursive type definitions are not supported.";
            throw new ConfigurationException(msg, error);
        }
    }

    Class<T> getConfigurationType() {
        return configurationType;
    }
}
