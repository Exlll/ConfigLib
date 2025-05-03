package de.exlll.configlib;

import de.exlll.configlib.ConfigurationElements.FieldElement;
import de.exlll.configlib.ConfigurationElements.RecordComponentElement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static de.exlll.configlib.Validator.requireNonNull;
import static de.exlll.configlib.Validator.requireTargetTypeRet;

sealed abstract class TypeSerializer<T, E extends ConfigurationElement<?>>
        implements Serializer<T, Map<?, ?>>
        permits ConfigurationSerializer, RecordSerializer {
    protected final Class<T> type;
    protected final ConfigurationProperties properties;
    protected final NameFormatter formatter;
    protected final Map<String, Serializer<?, ?>> serializers;
    protected final UnaryOperator<T> postProcessor;

    protected TypeSerializer(Class<T> type, ConfigurationProperties properties) {
        this.type = requireNonNull(type, "type");
        this.properties = requireNonNull(properties, "configuration properties");
        this.formatter = properties.getNameFormatter();
        this.serializers = buildSerializerMap();
        this.postProcessor = createPostProcessorFromAnnotatedMethod();
        requireSerializableElements();
    }

    static <T> TypeSerializer<T, ?> newSerializerFor(
            Class<T> type,
            ConfigurationProperties properties
    ) {
        return type.isRecord()
                ? new RecordSerializer<>(type, properties)
                : new ConfigurationSerializer<>(type, properties);
    }

    Map<String, Serializer<?, ?>> buildSerializerMap() {
        final var selector = new SerializerSelector(properties);
        try {
            return elements().stream().collect(Collectors.toMap(
                    ConfigurationElement::name,
                    selector::select
            ));
        } catch (StackOverflowError error) {
            String msg = "Recursive type definitions are not supported.";
            throw new ConfigurationException(msg, error);
        }
    }

    @Override
    public final Map<?, ?> serialize(T configuration) {
        final Map<String, Object> result = new LinkedHashMap<>();

        for (final E element : elements()) {
            final Object elementValue = element.value(configuration);

            if ((elementValue == null) && !properties.outputNulls())
                continue;

            final Object serializedValue = serializeElement(element, elementValue);
            final String formattedName = formatter.format(element.name());
            result.put(formattedName, serializedValue);
        }

        return result;
    }

    protected final Object serializeElement(E element, Object value) {
        if (value == null) return null;

        // This cast can lead to a ClassCastException if an element of type X is
        // serialized by a custom serializer that expects a different type Y.
        @SuppressWarnings("unchecked")
        final var serializer = (Serializer<Object, Object>) serializers.get(element.name());
        try {
            final Object serialized = serializer.serialize(value);
            validateTargetType(element, value, serialized);
            return serialized;
        } catch (ClassCastException e) {
            String msg = ("Serialization of value '%s' for element '%s' of type '%s' failed.\n" +
                          "The type of the object to be serialized does not match the type " +
                          "the custom serializer of type '%s' expects.")
                    .formatted(
                            value,
                            element.element(),
                            element.declaringType(),
                            serializer.getClass()
                    );
            throw new ConfigurationException(msg, e);
        }
    }

    private static void validateTargetType(
            ConfigurationElement<?> element,
            Object value,
            Object serialized
    ) {
        try {
            requireTargetTypeRet(serialized);
        } catch (ConfigurationException e) {
            String msg = ("Serialization of value '%s' for element '%s' of type '%s' failed. " +
                          "The serializer produced an invalid target type.")
                    .formatted(
                            value,
                            element.element(),
                            element.declaringType()
                    );
            throw new ConfigurationException(msg, e);
        }
    }

    protected final Object deserialize(E element, Object value) {
        // This unchecked cast leads to an exception if the type of the object which
        // is deserialized is not a subtype of the type the deserializer expects.
        @SuppressWarnings("unchecked")
        final var serializer = (Serializer<Object, Object>)
                serializers.get(element.name());

        final Object deserialized;
        try {
            deserialized = serializer.deserialize(value);
        } catch (ClassCastException e) {
            String msg = baseDeserializeExceptionMessage(element, value) + "\n" +
                         "The type of the object to be deserialized does not " +
                         "match the type the deserializer expects.";
            throw new ConfigurationException(msg, e);
        } catch (RuntimeException e) {
            String msg = baseDeserializeExceptionMessage(element, value);
            throw new ConfigurationException(msg, e);
        }
        return deserialized;
    }

    protected final Object[] deserializeConfigurationElements(
            Map<?, ?> serializedConfiguration
    ) {
        final var elements = elements();
        final var result = new Object[elements.size()];

        for (int i = 0, size = elements.size(); i < size; i++) {
            final var element = elements.get(i);
            final var formattedName = formatter.format(element.name());

            if (!serializedConfiguration.containsKey(formattedName)) {
                final Object defaultValue = getDefaultValueOf(element);
                result[i] = applyPostProcessorForElement(element, defaultValue);
                continue;
            }

            final var serializedValue = serializedConfiguration.get(formattedName);

            if ((serializedValue == null) && properties.inputNulls()) {
                // This statement (and hence the whole block) could be removed,
                // but in my opinion the code is clearer this way.
                result[i] = null;
            } else if (serializedValue == null) {
                result[i] = getDefaultValueOf(element);
            } else {
                result[i] = deserialize(element, serializedValue);
            }

            result[i] = applyPostProcessorForElement(element, result[i]);
        }

        return result;
    }

    private Object applyPostProcessorForElement(
            ConfigurationElement<?> element,
            Object deserializeValue
    ) {
        Object result = deserializeValue;

        boolean postProcessed = false;
        for (final var entry : properties.getPostProcessorsByCondition().entrySet()) {
            final var condition = entry.getKey();

            if (!condition.test(element)) continue;

            final var postProcessor = entry.getValue();
            result = tryApplyPostProcessorForElement(element, postProcessor, result);
            postProcessed = true;
        }

        if ((result == null) && postProcessed)
            requirePostProcessorDoesNotReturnNullForPrimitiveElement(element);
        else if (result == null)
            requireNonPrimitiveType(element);

        return result;
    }

    private static Object tryApplyPostProcessorForElement(
            ConfigurationElement<?> element,
            UnaryOperator<?> postProcessor,
            Object value
    ) {
        try {
            // This cast can lead to a ClassCastException if an element of type X is
            // annotated with a post-processor that takes values of some other type Y.
            @SuppressWarnings("unchecked")
            final var pp = (UnaryOperator<Object>) postProcessor;
            return pp.apply(value);
        } catch (ClassCastException e) {
            String msg = ("Deserialization of value '%s' for element '%s' of type '%s' failed.\n" +
                          "The type of the object to be deserialized does not match the type " +
                          "post-processor '%s' expects.")
                    .formatted(value, element.element(), element.declaringType(), postProcessor);
            throw new ConfigurationException(msg, e);
        }
    }

    private static void requirePostProcessorDoesNotReturnNullForPrimitiveElement(
            ConfigurationElement<?> element
    ) {
        if (!element.type().isPrimitive()) return;

        if (element instanceof RecordComponentElement recordComponentElement) {
            final RecordComponent component = recordComponentElement.element();
            String msg = """
                         Post-processors must not return null for primitive record \
                         components but some post-processor of component '%s' of \
                         record type '%s' does.\
                         """.formatted(component, component.getDeclaringRecord());
            throw new ConfigurationException(msg);
        }

        if (element instanceof FieldElement fieldElement) {
            final Field field = fieldElement.element();
            String msg = ("Post-processors must not return null for primitive fields " +
                          "but some post-processor of field '%s' does.")
                    .formatted(field);
            throw new ConfigurationException(msg);
        }

        throw new ConfigurationException("Unhandled ConfigurationElement: " + element);
    }

    private static void requireNonPrimitiveType(ConfigurationElement<?> element) {
        if (!element.type().isPrimitive()) return;

        if (element instanceof RecordComponentElement recordComponentElement) {
            final RecordComponent component = recordComponentElement.element();
            String msg = ("Cannot set component '%s' of record type '%s' to null. " +
                          "Primitive types cannot be assigned null values.")
                    .formatted(component, component.getDeclaringRecord());
            throw new ConfigurationException(msg);
        }

        if (element instanceof FieldElement fieldElement) {
            final Field field = fieldElement.element();
            String msg = ("Cannot set field '%s' to null value. " +
                          "Primitive types cannot be assigned null.")
                    .formatted(field);
            throw new ConfigurationException(msg);
        }

        throw new ConfigurationException("Unhandled ConfigurationElement: " + element);
    }

    final UnaryOperator<T> createPostProcessorFromAnnotatedMethod() {
        final List<Method> list = Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(PostProcess.class))
                .filter(Predicate.not(Method::isSynthetic))
                .filter(Predicate.not(this::isAccessorMethod))
                .toList();

        if (list.isEmpty())
            return UnaryOperator.identity();
        if (list.size() > 1) {
            String methodNames = String.join(
                    "\n  ",
                    list.stream().map(Method::toString).sorted().toList()
            );
            String msg = "Configuration types must not define more than one method for " +
                         "post-processing but type '%s' defines %d:\n  %s"
                                 .formatted(type, list.size(), methodNames);
            throw new ConfigurationException(msg);
        }

        final Method method = list.get(0);
        final int modifiers = method.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers)) {
            String msg = "Post-processing methods must be neither abstract nor static, " +
                         "but post-processing method '%s' of type '%s' is."
                                 .formatted(method, type);
            throw new ConfigurationException(msg);
        }

        final int parameterCount = method.getParameterCount();
        if (parameterCount > 0) {
            String msg = "Post-processing methods must not define any parameters but " +
                         "post-processing method '%s' of type '%s' defines %d."
                                 .formatted(method, type, parameterCount);
            throw new ConfigurationException(msg);
        }

        final Class<?> returnType = method.getReturnType();
        if ((returnType != void.class) && (returnType != type)) {
            String msg = "The return type of post-processing methods must either be 'void' or " +
                         "the same type as the configuration type in which the post-processing " +
                         "method is defined. The return type of the post-processing method of " +
                         "type '%s' is neither 'void' nor '%s'."
                                 .formatted(type, type.getSimpleName());
            throw new ConfigurationException(msg);
        }

        return object -> {
            if (method.getReturnType() == void.class) {
                Reflect.invoke(method, object);
                return object;
            }
            // The following cast won't fail because our last two checks from above
            // guarantee that the return type of the method equals T at this point.
            @SuppressWarnings("unchecked")
            T result = (T) Reflect.invoke(method, object);
            return result;
        };
    }

    final boolean isAccessorMethod(Method method) {
        if (!type.isRecord()) return false;
        if (!method.getDeclaringClass().equals(type)) return false;
        if (method.getParameterCount() > 0) return false;
        return Arrays.stream(type.getRecordComponents())
                .map(RecordComponent::getName)
                .anyMatch(s -> s.equals(method.getName()));
    }

    protected abstract void requireSerializableElements();

    protected abstract String baseDeserializeExceptionMessage(E element, Object value);

    protected abstract List<E> elements();

    /**
     * Returns the default value of a field or record component before any
     * post-processing has been performed.
     *
     * @param element the configuration element
     * @return the default value for that element
     */
    protected abstract Object getDefaultValueOf(E element);

    abstract T newDefaultInstance();
}
