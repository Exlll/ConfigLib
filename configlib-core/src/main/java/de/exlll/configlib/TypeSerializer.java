package de.exlll.configlib;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static de.exlll.configlib.Validator.requireNonNull;

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

            final Object serializedValue = serialize(element, elementValue);
            final String formattedName = formatter.format(element.name());
            result.put(formattedName, serializedValue);
        }

        return result;
    }

    protected final Object serialize(E element, Object value) {
        // The following cast won't cause a ClassCastException because the serializers
        // are selected based on the element type.
        @SuppressWarnings("unchecked")
        final var serializer = (Serializer<Object, Object>)
                serializers.get(element.name());
        return (value != null) ? serializer.serialize(value) : null;
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

    final UnaryOperator<T> createPostProcessorFromAnnotatedMethod() {
        final List<Method> list = Arrays.stream(type.getDeclaredMethods())
                .filter(Predicate.not(Method::isSynthetic))
                .filter(method -> method.isAnnotationPresent(PostProcess.class))
                .toList();

        if (list.isEmpty())
            return UnaryOperator.identity();
        if (list.size() > 1) {
            String methodNames = String.join("\n  ", list.stream().map(Method::toString).toList());
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
            // The following cast won't fail because our last check above guarantees
            // that the return type of the method equals T at this point.
            @SuppressWarnings("unchecked")
            T result = (T) Reflect.invoke(method, object);
            return result;
        };
    }

    protected abstract void requireSerializableElements();

    protected abstract String baseDeserializeExceptionMessage(E element, Object value);

    protected abstract List<E> elements();

    abstract T newDefaultInstance();
}
