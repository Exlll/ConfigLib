package de.exlll.configlib;

import de.exlll.configlib.ConfigurationElements.FieldElement;

import java.util.List;
import java.util.Map;

final class ConfigurationSerializer<T> extends TypeSerializer<T, FieldElement> {
    ConfigurationSerializer(Class<T> configurationType, ConfigurationProperties properties) {
        super(Validator.requireConfigurationClass(configurationType), properties);
    }

    @Override
    public T deserialize(Map<?, ?> serializedConfiguration) {
        final var deserializedElements = deserializeConfigurationElements(serializedConfiguration);
        final var elements = elements();
        final T result = newDefaultInstance();
        for (int i = 0; i < deserializedElements.length; i++) {
            final FieldElement fieldElement = elements.get(i);
            Reflect.setValue(fieldElement.element(), result, deserializedElements[i]);
        }
        return postProcessor.apply(result);
    }

    @Override
    protected void requireSerializableElements() {
        if (serializers.isEmpty()) {
            String msg = "Configuration class '" + type.getSimpleName() + "' " +
                         "does not contain any (de-)serializable fields.";
            throw new ConfigurationException(msg);
        }
    }

    @Override
    protected String baseDeserializeExceptionMessage(FieldElement element, Object value) {
        return "Deserialization of value '%s' with type '%s' for field '%s' failed."
                .formatted(value, value.getClass(), element.element());
    }

    @Override
    protected List<FieldElement> elements() {
        return FieldExtractors.CONFIGURATION.extract(type)
                .filter(properties.getFieldFilter())
                .map(FieldElement::new)
                .toList();
    }

    @Override
    T newDefaultInstance() {
        return Reflect.callNoParamConstructor(type);
    }

    Class<T> getConfigurationType() {
        return type;
    }

    // This object must only be used for the `getDefaultValueOf` method below.
    private final T defaultInstance = newDefaultInstance();

    @Override
    protected Object getDefaultValueOf(FieldElement element) {
        return Reflect.getValue(element.element(), defaultInstance);
    }
}
