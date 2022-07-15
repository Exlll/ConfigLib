package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ConfigurationSerializer<T> extends TypeSerializer<T, Field> {
    ConfigurationSerializer(Class<T> configurationType, ConfigurationProperties properties) {
        super(Validator.requireConfiguration(configurationType), properties);
    }

    @Override
    public Map<?, ?> serialize(T element) {
        final Map<String, Object> result = new LinkedHashMap<>();

        for (final Field field : filterFields()) {
            final Object fieldValue = Reflect.getValue(field, element);

            if ((fieldValue == null) && !properties.outputNulls())
                continue;

            final Object serializedValue = serialize(field.getName(), fieldValue);

            final String formattedField = properties.getNameFormatter().format(field.getName());
            result.put(formattedField, serializedValue);
        }

        return result;
    }

    @Override
    public T deserialize(Map<?, ?> element) {
        final T result = Reflect.newInstance(type);

        for (final Field field : filterFields()) {
            final String fieldFormatted = properties.getNameFormatter().format(field.getName());

            if (!element.containsKey(fieldFormatted))
                continue;

            final Object serializedValue = element.get(fieldFormatted);

            if (serializedValue == null && properties.inputNulls()) {
                requireNonPrimitiveFieldType(field);
                Reflect.setValue(field, result, null);
            } else if (serializedValue != null) {
                final Object deserialized = deserialize(field, field.getName(), serializedValue);
                Reflect.setValue(field, result, deserialized);
            }
        }

        return result;
    }

    @Override
    protected void requireSerializableParts() {
        if (serializers.isEmpty()) {
            String msg = "Configuration class '" + type.getSimpleName() + "' " +
                         "does not contain any (de-)serializable fields.";
            throw new ConfigurationException(msg);
        }
    }

    @Override
    protected String baseDeserializeExceptionMessage(Field field, Object value) {
        return "Deserialization of value '%s' with type '%s' for field '%s' failed."
                .formatted(value, value.getClass(), field);
    }

    private static void requireNonPrimitiveFieldType(Field field) {
        if (field.getType().isPrimitive()) {
            String msg = ("Cannot set field '%s' to null value. Primitive types " +
                          "cannot be assigned null.").formatted(field);
            throw new ConfigurationException(msg);
        }
    }

    private List<Field> filterFields() {
        return FieldExtractors.CONFIGURATION.extract(type)
                .filter(properties.getFieldFilter())
                .toList();
    }

    Class<T> getConfigurationType() {
        return type;
    }
}
