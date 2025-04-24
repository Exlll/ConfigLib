package de.exlll.configlib;

/**
 * Implementations of this interface convert instances of type {@code T1} to a serializable type
 * {@code T2} and vice versa.
 * <p>
 * Which types {@code T2} are serializable depends on the underlying storage system. Currently,
 * all storage systems support the following target types:
 * <ul>
 *     <li>{@code Boolean}</li>
 *     <li>{@code Long}</li>
 *     <li>{@code Double}</li>
 *     <li>{@code String}</li>
 *     <li>(Nested) {@code List}s of the other types</li>
 *     <li>(Nested) {@code Map}s of the other types</li>
 * </ul>
 * <p>
 * For all custom serializers, {@code T2} must be one of the six types listed above.
 *
 * @param <T1> the type of the objects that should be serialized
 * @param <T2> the serializable type
 */
public interface Serializer<T1, T2> {
    /**
     * Serializes an element of type {@code T1} into an element of type {@code T2}.
     * Type {@code T2} must be a valid target type.
     *
     * @param element the element of type {@code T1} that is serialized
     * @return the serialized element of type {@code T2}
     */
    T2 serialize(T1 element);

    /**
     * Deserializes an element of type {@code T2} into an element of type {@code T1}.
     *
     * @param element the element of type {@code T2} that is deserialized
     * @return the deserialized element of type {@code T1}
     */
    T1 deserialize(T2 element);
}
