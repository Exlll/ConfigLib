package de.exlll.configlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static de.exlll.configlib.Validator.requireNonEmpty;
import static de.exlll.configlib.Validator.requireNonNull;

/**
 * Represents a key that can be used to access (possibly nested) values of a
 * collection or map. A key consists of one or more ordered parts.
 * All parts must either be null, of a valid, simple target type (i.e. Boolean,
 * Long, Double, or String), or instances of a special type that represent list
 * indices.
 */
public final class Key {
    private final List<Object> parts;

    /**
     * Creates a new {@code Key} from one or more parts.
     * <p>
     * All parts must either be null, of a valid, simple target type (i.e. Boolean, Long,
     * Double, or String), or instances of a special type that represent list indices.
     * <p>
     * For convenience, this method automatically converts {@code int}s,
     * and {@code Integer}s to {@code Long}s.
     *
     * @param firstPart  the required first part
     * @param otherParts the optional other parts
     * @return a new key
     * @throws NullPointerException     if {@code otherParts} is null
     * @throws IllegalArgumentException if the list is empty or if any list element
     *                                  has an invalid type
     */
    public static Key key(Object firstPart, Object... otherParts) {
        requireNonNull(otherParts, "array of other parts");

        final List<Object> allParts = new ArrayList<>(otherParts.length + 1);
        addPart(firstPart, allParts);
        for (Object otherPart : otherParts)
            addPart(otherPart, allParts);

        return new Key(allParts);
    }

    /**
     * Creates a new {@code Key} from a non-empty list of parts.
     * <p>
     * All parts must either be null, of a valid, simple target type (i.e. Boolean, Long,
     * Double, or String), or instances of a special type that represent list indices.
     * <p>
     * For convenience, this method automatically converts {@code int}s,
     * and {@code Integer}s to {@code Long}s.
     *
     * @param parts the list of parts
     * @return a new key
     * @throws NullPointerException     if {@code parts} is null
     * @throws IllegalArgumentException if the list is empty or if any list element
     *                                  has an invalid type
     */
    public static Key key(List<Object> parts) {
        requireNonNull(parts, "list of parts");

        final List<Object> allParts = new ArrayList<>(parts.size());
        for (Object part : parts)
            addPart(part, allParts);

        return new Key(allParts);
    }

    private static void addPart(Object part, List<Object> parts) {
        if (part instanceof Integer i) parts.add(i.longValue());
        else parts.add(part);
    }

    /**
     * Returns a special object that represents a list index and that can be
     * used as part of a {@code Key}.
     * <p>
     * The necessity for such an object arises as plain numbers are interpreted
     * as map keys.
     *
     * @param index the list index
     * @return a special object that represents a list index
     * @throws IllegalArgumentException if {@code index} is less than 0
     */
    public static Object listIdx(int index) {
        return new ListIndex(index);
    }

    Key(List<Object> parts) {
        requireNonNull(parts, "list of parts");
        requireNonEmpty(parts, "list of parts");
        requireValidParts(parts);
        this.parts = Collections.unmodifiableList(parts);
    }

    private static void requireValidParts(List<Object> parts) {
        for (int i = 0, partsSize = parts.size();
             i < partsSize;
             i++) {
            final Object partI = parts.get(i);
            if (isValidPart(partI)) continue;
            String msg = "Part '" + partI + "' at index " + i + " must be of a " +
                         "simple target type but its type is '" + partI.getClass() + "'";
            throw new IllegalArgumentException(msg);
        }
    }

    private static boolean isValidPart(Object part) {
        if (part == null) return true;
        if (part instanceof ListIndex) return true;
        return Reflect.isSimpleTargetType(part.getClass());
    }

    /**
     * Returns the part at the given index.
     *
     * @param index the index
     * @return part at that index
     * @throws IndexOutOfBoundsException if index is less than zero or greater
     *                                   than (number of parts - 1)
     */
    public Object getPart(int index) {
        return parts.get(index);
    }

    /**
     * Returns an unmodifiable list of all parts that constitute this key.
     *
     * @return unmodifiable list of all parts that constitute this key
     */
    public List<Object> getAllParts() {
        return parts;
    }

    @Override
    public String toString() {
        return "Key" + parts.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Key key = (Key) object;
        return Objects.equals(parts, key.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parts);
    }

    record ListIndex(int index) {
        ListIndex {
            if (index < 0) {
                String msg = "List indices must be at least zero but the number " +
                             "you provided is " + index + ".";
                throw new IllegalArgumentException(msg);
            }
        }
    }
}
