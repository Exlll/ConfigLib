package de.exlll.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element should be serialized with the given serializer. Serializers
 * provided by this annotation take precedence over all other serializers.
 * <p>
 * If the annotated element is an array, list, set, or map a nesting level can be set to apply the
 * serializer not to the top-level type but to its elements. For maps, the serializer is applied to
 * the values and not the keys.
 * <p>
 * The following example shows how {@code nesting} can be used to apply the serializer at
 * different levels.
 *
 * <pre>
 * {@code
 * // MyListSerializer is applied to 'list'
 * @SerializeWith(serializer = MyListSerializer.class)
 * List<Set<String>> list;
 *
 * // MySetSerializer is applied to the Set<String> elements of 'list'
 * @SerializeWith(serializer = MySetSerializer.class, nesting = 1)
 * List<Set<String>> list;
 *
 * // MyStringSerializer is applied to the strings within the set elements of 'list'
 * @SerializeWith(serializer = MyStringSerializer.class, nesting = 2)
 * List<Set<String>> list;
 *
 * // MyMap0Serializer is applied to 'map'
 * @SerializeWith(serializer = MyMap0Serializer.class)
 * Map<Integer, Map<String, Double>> map;
 *
 * // MyMap1Serializer is applied to the Map<String, Double> values of 'map'
 * @SerializeWith(serializer = MyMap1Serializer.class, nesting = 1)
 * Map<Integer, Map<String, Double>> map;
 *
 * // MyDoubleSerializer is applied to the doubles within the nested values of 'map'
 * @SerializeWith(serializer = MyDoubleSerializer.class, nesting = 2)
 * Map<Integer, Map<String, Double>> map;
 * }
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeWith {
    /**
     * Returns the type of the serializer to be used.
     *
     * @return the type of serializer to use
     */
    Class<? extends Serializer<?, ?>> serializer();

    /**
     * Returns the nesting level at which to apply the serializer.
     *
     * @return the nesting level
     */
    int nesting() default 0;
}
