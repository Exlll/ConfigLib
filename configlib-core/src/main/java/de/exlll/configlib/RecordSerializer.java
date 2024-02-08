package de.exlll.configlib;

import de.exlll.configlib.ConfigurationElements.RecordComponentElement;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

final class RecordSerializer<R> extends TypeSerializer<R, RecordComponentElement> {
    RecordSerializer(Class<R> recordType, ConfigurationProperties properties) {
        super(Validator.requireRecord(recordType), properties);
    }

    @Override
    public R deserialize(Map<?, ?> serializedConfiguration) {
        final var ctorArgs = deserializeConfigurationElements(serializedConfiguration);
        final var result = Reflect.callCanonicalConstructor(type, ctorArgs);
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

    Class<R> getRecordType() {
        return type;
    }

    @Override
    protected Object getDefaultValueOf(RecordComponentElement element) {
        return Reflect.getDefaultValue(element.type());
    }
}
