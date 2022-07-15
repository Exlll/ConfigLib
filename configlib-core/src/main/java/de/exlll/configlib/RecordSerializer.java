package de.exlll.configlib;

import java.lang.reflect.RecordComponent;
import java.util.LinkedHashMap;
import java.util.Map;

final class RecordSerializer<R extends Record> extends TypeSerializer<R, RecordComponent> {
    RecordSerializer(Class<R> recordType, ConfigurationProperties properties) {
        super(recordType, properties);
    }

    @Override
    public Map<?, ?> serialize(R element) {
        final Map<String, Object> result = new LinkedHashMap<>();

        for (final RecordComponent component : type.getRecordComponents()) {
            final Object componentValue = Reflect.getValue(component, element);

            if (componentValue == null && !properties.outputNulls())
                continue;

            final Object resultValue = serialize(component.getName(), componentValue);

            final String compName = properties.getNameFormatter().format(component.getName());
            result.put(compName, resultValue);
        }

        return result;
    }

    @Override
    public R deserialize(Map<?, ?> element) {
        final var components = type.getRecordComponents();
        final var constructorArguments = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            final var component = components[i];
            final var componentFormatted = properties.getNameFormatter()
                    .format(component.getName());

            if (!element.containsKey(componentFormatted)) {
                constructorArguments[i] = Reflect.getDefaultValue(component.getType());
                continue;
            }

            final Object serializedArgument = element.get(componentFormatted);

            if (serializedArgument == null && properties.inputNulls()) {
                requireNonPrimitiveComponentType(component);
                constructorArguments[i] = null;
            } else if (serializedArgument == null) {
                constructorArguments[i] = Reflect.getDefaultValue(component.getType());
            } else {
                constructorArguments[i] = deserialize(
                        component,
                        component.getName(),
                        serializedArgument
                );
            }
        }

        return Reflect.newRecord(type, constructorArguments);
    }

    @Override
    protected void requireSerializableParts() {
        if (serializers.isEmpty()) {
            String msg = "Record type '%s' does not define any components."
                    .formatted(type.getSimpleName());
            throw new ConfigurationException(msg);
        }
    }

    @Override
    protected String baseDeserializeExceptionMessage(RecordComponent component, Object value) {
        return "Deserialization of value '%s' with type '%s' for component '%s' of record '%s' failed."
                .formatted(value, value.getClass(), component, component.getDeclaringRecord());
    }

    private static void requireNonPrimitiveComponentType(RecordComponent component) {
        if (component.getType().isPrimitive()) {
            String msg = ("Cannot set component '%s' of record type '%s' to null. Primitive types " +
                          "cannot be assigned null values.")
                    .formatted(component, component.getDeclaringRecord());
            throw new ConfigurationException(msg);
        }
    }

    Class<R> getRecordType() {
        return type;
    }
}