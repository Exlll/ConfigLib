package de.exlll.configlib;

/**
 * Implementations of this interface convert instances of type {@code T1} to a serializable type
 * {@code T2} and vice versa.
 * <p>
 * Which types {@code T2} are serializable depends on the underlying storage system. Currently,
 * all storage systems support the following types:
 * <ul>
 *     <li>{@code Boolean}</li>
 *     <li>{@code Long}</li>
 *     <li>{@code Double}</li>
 *     <li>{@code String}</li>
 *     <li>(Nested) {@code List}s of the other types</li>
 *     <li>(Nested) {@code Set}s of the other types</li>
 *     <li>(Nested) {@code Map}s of the other types</li>
 * </ul>
 * <p>
 * That means that if you want to support all currently available configuration store formats,
 * your {@code Serializer} implementation should convert an object of type {@code T1} into one
 * of the seven types listed above.
 *
 * @param <T1> the type of the objects that should be serialized
 * @param <T2> the serializable type
 */
public interface Serializer<T1, T2> {
    /**
     * Serializes an element of type {@code T1} into an element of type {@code T2}.
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
