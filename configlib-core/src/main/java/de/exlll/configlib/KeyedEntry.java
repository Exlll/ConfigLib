package de.exlll.configlib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.exlll.configlib.Validator.requireNonNull;

/**
 * Represents a (possibly missing) entry of a {@link KeyedCollection}.
 */
public sealed interface KeyedEntry {
    /**
     * Returns the key that was used to access this entry.
     *
     * @return key used to access this entry
     */
    Key key();

    /**
     * Represents an existing entry of a {@link KeyedCollection}.
     *
     * @param key   the key used to access this entry
     * @param value the value of this entry
     */
    record ExistingKeyedEntry(Key key, Object value) implements KeyedEntry {
        /**
         * Constructs a ExistingKeyedEntry.
         *
         * @param key   the key used to access this entry
         * @param value the value of this entry
         * @throws NullPointerException if {@code key} is null
         */
        public ExistingKeyedEntry {
            requireNonNull(key, "key");
        }

        /**
         * Returns {@code value} cast to a Boolean.
         *
         * @return value as a Boolean
         * @throws ClassCastException if {@code value} is not a boolean
         */
        public Boolean asBoolean() {
            return (Boolean) value;
        }

        /**
         * Returns {@code value} cast to a Long.
         *
         * @return value as a Long
         * @throws ClassCastException if {@code value} is not a Long
         */
        public Long asLong() {
            return (Long) value;
        }

        /**
         * Returns {@code value} cast to a Double.
         *
         * @return value as a Double
         * @throws ClassCastException if {@code value} is not a Double
         */
        public Double asDouble() {
            return (Double) value;
        }

        /**
         * Returns {@code value} cast to a String.
         *
         * @return value as a String
         * @throws ClassCastException if {@code value} is not a String
         */
        public String asString() {
            return (String) value;
        }

        /**
         * Returns {@code value} cast to a List.
         *
         * @return value as a List
         * @throws ClassCastException if {@code value} is not a List
         */
        public List<?> asList() {
            return (List<?>) value;
        }

        /**
         * Returns {@code value} cast to a Map.
         *
         * @return value as a Map
         * @throws ClassCastException if {@code value} is not a Map
         */
        public Map<?, ?> asMap() {
            return (Map<?, ?>) value;
        }
    }

    /**
     * Represents a missing entry of a {@link KeyedCollection}.
     * <p>
     * When a key points to an entry that does not exist in a {@code KeyedCollection}
     * object, then there is some part of the key that could not be resolved.
     * This part and all parts that follow it constitute the {@code missing} key.
     * All parts up until the first missing part make up the {@code existing} key.
     * <p>
     * If the very first part of {@code key} is missing, then {@code existing} is null.
     * <p>
     * It is guaranteed that the parts of the {@code existing} key concatenated with
     * the parts of the {@code missing} key give the parts of the {@code key} used
     * to access this entry.
     * <p>
     * An entry can be missing because some part of the {@code key} simply does
     * not exist or because some part before the last part of the {@code key} is
     * not a collection (and thus prevents further recursive lookup). These cases
     * can be distinguished with the {@code reason} argument.
     *
     * @param key      the key used to access this entry
     * @param existing a key of existing parts or null
     * @param missing  a key of missing parts
     * @param reason   the reason why this entry is missing
     */
    record MissingKeyedEntry(Key key, Key existing, Key missing, Reason reason)
            implements KeyedEntry {
        /**
         * Constructs a MissingKeyedEntry.
         *
         * @param key      the key used to access this entry
         * @param existing a key of existing parts or null
         * @param missing  a key of missing parts
         * @throws NullPointerException     if {@code key}, {@code missing},
         *                                  or {@code reason} is null
         * @throws IllegalArgumentException if the parts of {@code existing} concatenated
         *                                  with the parts of {@code missing} don't give
         *                                  the parts of {@code key}
         */
        public MissingKeyedEntry {
            requireNonNull(key, "key used to access this entry");
            requireNonNull(missing, "key of missing parts");
            requireNonNull(reason, "reason");

            final List<Object> allParts = (existing == null)
                    ? new ArrayList<>()
                    : new ArrayList<>(existing.getAllParts());
            allParts.addAll(missing.getAllParts());

            if (!key.equals(Key.key(allParts))) {
                String msg = "Full key: " + key + "; " +
                             "Existing key: " + existing + "; " +
                             "Missing key: " + missing;
                throw new IllegalArgumentException(msg);
            }
        }

        enum Reason {PART_MISSING, PART_WRONG_TYPE}
    }
}
