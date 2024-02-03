package de.exlll.configlib;

import de.exlll.configlib.ConfigurationElements.RecordComponentElement;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

final class RecordSerializer<R> extends TypeSerializer<R, RecordComponentElement> {
    RecordSerializer(Class<R> recordType, ConfigurationProperties properties) {
        super(Validator.requireRecord(recordType), properties);
    }

    @Override
    public R deserialize(Map<?, ?> serializedConfiguration) {
        final var elements = elements();
        final var constructorArguments = new Object[elements.size()];

        for (int i = 0, size = elements.size(); i < size; i++) {
            final var element = elements.get(i);
            final var formattedName = formatter.format(element.name());

            if (!serializedConfiguration.containsKey(formattedName)) {
                constructorArguments[i] = Reflect.getDefaultValue(element.type());
                continue;
            }

            final var serializedValue = serializedConfiguration.get(formattedName);
            final var recordComponent = element.element();

            if ((serializedValue == null) && properties.inputNulls()) {
                requireNonPrimitiveComponentType(recordComponent);
                constructorArguments[i] = null;
            } else if (serializedValue == null) {
                constructorArguments[i] = Reflect.getDefaultValue(element.type());
            } else {
                constructorArguments[i] = deserialize(element, serializedValue);
            }
        }

        final R result = Reflect.callCanonicalConstructor(type, constructorArguments);
        return postProcessor.apply(result);
    }

    @Override
    protected void requireSerializableElements() {
        if (serializers.isEmpty()) {
            String msg = "Record type '%s' does not define any components."
                    .formatted(type.getSimpleName());
            throw new ConfigurationException(msg);
        }
    }

    @Override
    protected String baseDeserializeExceptionMessage(RecordComponentElement element, Object value) {
        return "Deserialization of value '%s' with type '%s' for component '%s' of record '%s' failed."
                .formatted(value, value.getClass(), element.element(), element.declaringType());
    }

    @Override
    protected List<RecordComponentElement> elements() {
        return Arrays.stream(type.getRecordComponents())
                .map(RecordComponentElement::new)
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
