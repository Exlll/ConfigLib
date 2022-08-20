package de.exlll.configlib;

import java.util.LinkedHashMap;
import java.util.Map;

final class PolymorphicSerializer implements Serializer<Object, Map<?, ?>> {
    private final SerializerContext context;
    private final Class<?> polymorphicType;
    private final Polymorphic annotation;

    public PolymorphicSerializer(SerializerContext context) {
        this.context = context;
        // we know it's a class because of SerializerSelector#findMetaSerializerOnType
        this.polymorphicType = (Class<?>) context.annotatedType().getType();
        this.annotation = polymorphicType.getAnnotation(Polymorphic.class);
        requireNonBlankProperty();
    }

    private void requireNonBlankProperty() {
        if (annotation.property().isBlank()) {
            String msg = "The @Polymorphic annotation does not allow a blank property name but " +
                         "type '%s' uses one.".formatted(polymorphicType.getName());
            throw new ConfigurationException(msg);
        }
    }

    @Override
    public Map<?, ?> serialize(Object element) {
        // this cast won't cause any exceptions as we only pass objects of types the
        // serializer expects
        @SuppressWarnings("unchecked")
        final var serializer = (TypeSerializer<Object, ?>) TypeSerializer.newSerializerFor(
                element.getClass(),
                context.properties()
        );
        final var serialization = serializer.serialize(element);

        requireSerializationNotContainsProperty(serialization);

        final var result = new LinkedHashMap<>();
        result.put(annotation.property(), element.getClass().getName());
        result.putAll(serialization);
        return result;
    }

    private void requireSerializationNotContainsProperty(Map<?, ?> serialization) {
        if (serialization.containsKey(annotation.property())) {
            String msg = ("Polymorphic serialization for type '%s' failed. The type contains a " +
                          "configuration element with name '%s' but that name is " +
                          "used by the @Polymorphic property.")
                    .formatted(polymorphicType.getName(), annotation.property());
            throw new ConfigurationException(msg);
        }
    }


    @Override
    public Object deserialize(Map<?, ?> element) {
        requirePropertyPresent(element);

        final var typeIdentifier = element.get(annotation.property());
        requireTypeIdentifierString(typeIdentifier);

        Class<?> type = tryFindClass((String) typeIdentifier);
        TypeSerializer<?, ?> serializer = TypeSerializer.newSerializerFor(type, context.properties());
        return serializer.deserialize(element);
    }

    private Class<?> tryFindClass(String className) {
        try {
            return Reflect.getClassByName(className);
        } catch (RuntimeException e) {
            String msg = ("Polymorphic deserialization for type '%s' failed. " +
                          "The class '%s' does not exist.")
                    .formatted(polymorphicType.getName(), className);
            throw new ConfigurationException(msg, e);
        }
    }

    private void requirePropertyPresent(Map<?, ?> element) {
        if (element.get(annotation.property()) != null)
            return;
        String msg = """
                     Polymorphic deserialization for type '%s' failed. \
                     The property '%s' which holds the type is missing. \
                     Value to be deserialized:
                     %s\
                     """
                .formatted(
                        polymorphicType.getName(),
                        annotation.property(),
                        element
                );
        throw new ConfigurationException(msg);
    }

    private void requireTypeIdentifierString(Object typeIdentifier) {
        if (typeIdentifier instanceof String)
            return;
        String msg = ("Polymorphic deserialization for type '%s' failed. The type identifier '%s' " +
                      "which should hold the type is not a string but of type '%s'.")
                .formatted(
                        polymorphicType.getName(),
                        typeIdentifier,
                        typeIdentifier.getClass().getName()
                );
        throw new ConfigurationException(msg);
    }

    Class<?> getPolymorphicType() {
        return polymorphicType;
    }
}
