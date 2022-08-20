package de.exlll.configlib;

import java.lang.reflect.AnnotatedType;
import java.util.function.Function;

/**
 * Instances of this class provide contextual information for custom serializers.
 * References to such instances can be obtained when adding serializer factories through
 * the {@link ConfigurationProperties.Builder#addSerializerFactory(Class, Function)}
 * method.
 * <p>
 * Custom serializers used with {@code @SerializeWith} are allowed to declare a constructor
 * with one parameter of type {@code SerializerContext}. If such a constructor exists, a
 * context object is injected into it when the serializer is instantiated.
 *
 * <pre>
 * {@code
 * public final class PointSerializer implements Serializer<Point, String> {
 *     private final SerializerContext context;
 *
 *     public PointSerializer(SerializerContext context) {
 *         this.context = context;
 *     }
 *     // implementation ...
 * }
 *
 * YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
 *         .addSerializerFactory(Point.class, PointSerializer::new)
 *         .build();
 * }
 * </pre>
 */
public interface SerializerContext {
    /**
     * Returns the {@code ConfigurationProperties} object in use when the serializer was
     * selected.
     *
     * @return properties object in use when the serializer was selected
     */
    ConfigurationProperties properties();

    /**
     * Returns the {@code ConfigurationElement} for which this serializer was selected.
     *
     * @return element for which this serializer was selected
     */
    ConfigurationElement<?> element();

    /**
     * Returns the {@code AnnotatedType} which led to the selection of the serializer. The
     * annotated type returned by this method might be different from the one returned by
     * {@link ConfigurationElement#annotatedType()}. Specifically, the type is different
     * when the serializer is applied to a nested type via {@link SerializeWith} in which
     * case the annotated type represents the type at that nesting level.
     *
     * @return annotated type which led to the selection of the serializer
     */
    AnnotatedType annotatedType();
}

