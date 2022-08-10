package de.exlll.configlib;

import java.lang.reflect.AnnotatedType;

/**
 * Instances of this class provide contextual information for custom serializers.
 * <p>
 * Custom serializers classes are allowed to declare a constructor with one parameter of
 * type {@code SerializerContext}. If such a constructor exists, an instance of this class is
 * passed to it when the serializer is instantiated by this library.
 */
public interface SerializerContext {
    /**
     * Returns the {@code ConfigurationProperties} object in use when the serializer was selected.
     *
     * @return properties object in use when the serializer was selected
     */
    ConfigurationProperties properties();

    /**
     * Returns the {@code TypeComponent} (i.e. the field or record component) which led to the
     * selection of the serializer.
     *
     * @return component which led to the selection of the serializer
     */
    TypeComponent<?> component();

    /**
     * Returns the {@code AnnotatedType} which led to the selection of the serializer. The annotated
     * type returned by this method might be different from the one returned by
     * {@link TypeComponent#annotatedType()}. Specifically, the type is different when the
     * serializer is applied to a nested type via {@link SerializeWith} in which case the annotated
     * type represents the type at that nesting level.
     *
     * @return annotated type which led to the selection of the serializer
     */
    AnnotatedType annotatedType();
}

