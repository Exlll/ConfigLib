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
}
