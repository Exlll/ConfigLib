package de.exlll.configlib;

import de.exlll.configlib.TypeComponent.ConfigurationField;

import java.lang.reflect.Field;
import java.util.Map;

final class ConfigurationSerializer<T> extends TypeSerializer<T, ConfigurationField> {
    ConfigurationSerializer(Class<T> configurationType, ConfigurationProperties properties) {
        super(Validator.requireConfiguration(configurationType), properties);
    }

    @Override
    public T deserialize(Map<?, ?> element) {
        final T result = Reflect.newInstance(type);

        for (final var component : components()) {
            final var formattedName = formatter.format(component.componentName());

            if (!element.containsKey(formattedName))
                continue;

            final var serializedValue = element.get(formattedName);
            final var field = component.component();

            if ((serializedValue == null) && properties.inputNulls()) {
                requireNonPrimitiveFieldType(field);
                Reflect.setValue(field, result, null);
            } else if (serializedValue != null) {
                Object deserializeValue = deserialize(component, serializedValue);
                Reflect.setValue(field, result, deserializeValue);
            }
        }

        return result;
    }

    @Override
    protected void requireSerializableComponents() {
        if (serializers.isEmpty()) {
            String msg = "Configuration class '" + type.getSimpleName() + "' " +
                         "does not contain any (de-)serializable fields.";
            throw new ConfigurationException(msg);
        }
    }

    @Override
    protected String baseDeserializeExceptionMessage(ConfigurationField component, Object value) {
        return "Deserialization of value '%s' with type '%s' for field '%s' failed."
                .formatted(value, value.getClass(), component.component());
    }

    @Override
    protected Iterable<ConfigurationField> components() {
        return FieldExtractors.CONFIGURATION.extract(type)
                .filter(properties.getFieldFilter())
                .map(ConfigurationField::new)
                .toList();
    }

    @Override
    T newDefaultInstance() {
        return Reflect.newInstance(type);
    }

    private static void requireNonPrimitiveFieldType(Field field) {
        if (field.getType().isPrimitive()) {
            String msg = ("Cannot set field '%s' to null value. Primitive types " +
                          "cannot be assigned null.").formatted(field);
            throw new ConfigurationException(msg);
        }
    }

    Class<T> getConfigurationType() {
        return type;
    }
}
