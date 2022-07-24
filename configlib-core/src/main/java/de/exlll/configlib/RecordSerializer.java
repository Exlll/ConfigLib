package de.exlll.configlib;

import de.exlll.configlib.TypeComponent.ConfigurationRecordComponent;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

final class RecordSerializer<R> extends
        TypeSerializer<R, ConfigurationRecordComponent> {
    RecordSerializer(Class<R> recordType, ConfigurationProperties properties) {
        super(Validator.requireRecord(recordType), properties);
    }

    @Override
    public R deserialize(Map<?, ?> element) {
        final var components = components();
        final var constructorArguments = new Object[components.size()];

        for (int i = 0, size = components.size(); i < size; i++) {
            final var component = components.get(i);
            final var formattedName = formatter.format(component.componentName());

            if (!element.containsKey(formattedName)) {
                constructorArguments[i] = Reflect.getDefaultValue(component.componentType());
                continue;
            }

            final var serializedValue = element.get(formattedName);
            final var recordComponent = component.component();

            if ((serializedValue == null) && properties.inputNulls()) {
                requireNonPrimitiveComponentType(recordComponent);
                constructorArguments[i] = null;
            } else if (serializedValue == null) {
                constructorArguments[i] = Reflect.getDefaultValue(component.componentType());
            } else {
                constructorArguments[i] = deserialize(component, serializedValue);
            }
        }

        return Reflect.callCanonicalConstructor(type, constructorArguments);
    }

    @Override
    protected void requireSerializableComponents() {
        if (serializers.isEmpty()) {
            String msg = "Record type '%s' does not define any components."
                    .formatted(type.getSimpleName());
            throw new ConfigurationException(msg);
        }
    }

    @Override
    protected String baseDeserializeExceptionMessage(ConfigurationRecordComponent component, Object value) {
        return "Deserialization of value '%s' with type '%s' for component '%s' of record '%s' failed."
                .formatted(value, value.getClass(), component.component(), component.declaringType());
    }

    @Override
    protected List<ConfigurationRecordComponent> components() {
        return Arrays.stream(type.getRecordComponents())
                .map(ConfigurationRecordComponent::new)
                .toList();
    }

    @Override
    R newDefaultInstance() {
        return Reflect.hasDefaultConstructor(type)
                ? Reflect.callNoParamConstructor(type)
                : Reflect.callCanonicalConstructorWithDefaultValues(type);
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