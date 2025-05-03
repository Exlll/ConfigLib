package de.exlll.configlib;

/**
 * Represents a (possibly nested) collection whose entries can be accessed
 * and modified with a {@link Key}.
 */
public interface KeyedCollection {
    /**
     * Returns the entry to which the specified key is mapped or returns an
     * entry that represents a missing entry.
     *
     * @param key key of the entry
     * @return an existing or missing entry
     * @throws NullPointerException if {@code key} is null
     */
    KeyedEntry get(Key key);

    /**
     * Removes and returns the entry to which the specified key is mapped or
     * returns an entry that represents a missing entry.
     *
     * @param key key of the entry
     * @return an existing entry that was removed or a missing entry
     * @throws NullPointerException if {@code key} is null
     */
    KeyedEntry remove(Key key);

    /**
     * Associates the specified value with the specified key.
     * <p>
     * If this collection previously contained an entry for the specified key,
     * the old value is replaced by the specified value.
     * <p>
     * If the first part of {@code key} is of the wrong type, then any
     * <p>
     * If {@code key} specifies more than one part and any part before the last
     * part references a simple value, that value is replaced by an empty
     * collection or map of the appropriate type.
     * TODO-
     * <p>
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws NullPointerException     if {@code key} is null
     * @throws IllegalArgumentException if {@code value} is neither null nor of
     *                                  a valid target type
     */
    KeyedEntry put(Key key, Object value);
}
