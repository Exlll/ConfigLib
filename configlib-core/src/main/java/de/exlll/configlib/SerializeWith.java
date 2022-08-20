package de.exlll.configlib;

import java.lang.annotation.*;

/**
 * Indicates that the annotated configuration element or type should be serialized using the
 * referenced serializer.
 * <p>
 * If this annotation is applied to a configuration element, and that element is an array, list,
 * set, or map a nesting level can be set to apply the serializer not to the top-level type but to
 * its elements. For maps, the serializer is applied to the values and not the keys.
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
@Target({
        ElementType.ANNOTATION_TYPE, // usage as meta-annotation
        ElementType.TYPE,            // usage on types
        ElementType.FIELD,           // usage on configuration elements
        ElementType.RECORD_COMPONENT
})
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
     * <p>
     * If this annotation is applied to a type or another annotation, the value
     * returned by this method has no effect.
     *
     * @return the nesting level
     */
    int nesting() default 0;
}
