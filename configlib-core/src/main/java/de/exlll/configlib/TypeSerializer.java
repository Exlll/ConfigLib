package de.exlll.configlib;

import java.util.Map;

import static de.exlll.configlib.Validator.requireNonNull;

abstract class TypeSerializer<T, P> implements Serializer<T, Map<?, ?>> {
    protected final Class<T> type;
    protected final ConfigurationProperties properties;
    protected final Map<String, Serializer<?, ?>> serializers;

    protected TypeSerializer(Class<T> type, ConfigurationProperties properties) {
        this.type = requireNonNull(type, "type");
        this.properties = requireNonNull(properties, "configuration properties");
        this.serializers = new SerializerMapper(type, properties).buildSerializerMap();
        requireSerializableParts();
    }

    protected final Object serialize(String partName, Object value) {
        // The following cast won't cause a ClassCastException because the serializers
        // are selected based on the part type.
        @SuppressWarnings("unchecked")
        final var serializer = (Serializer<Object, Object>) serializers.get(partName);
        return (value != null) ? serializer.serialize(value) : null;
    }

    protected final Object deserialize(P part, String partName, Object value) {
        // This unchecked cast leads to an exception if the type of the object which
        // is deserialized is not a subtype of the type the deserializer expects.
        @SuppressWarnings("unchecked")
        final var serializer = (Serializer<Object, Object>) serializers.get(partName);

        final Object deserialized;
        try {
            deserialized = serializer.deserialize(value);
        } catch (ClassCastException e) {
            String msg = baseDeserializeExceptionMessage(part, value) + "\n" +
                         "The type of the object to be deserialized does not " +
                         "match the type the deserializer expects.";
            throw new ConfigurationException(msg, e);
        } catch (RuntimeException e) {
            String msg = baseDeserializeExceptionMessage(part, value);
            throw new ConfigurationException(msg, e);
        }
        return deserialized;
    }

    protected abstract void requireSerializableParts();

    protected abstract String baseDeserializeExceptionMessage(P part, Object value);
}
