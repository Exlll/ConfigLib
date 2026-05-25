package de.exlll.configlib;

import net.kyori.adventure.key.Key;

import java.util.OptionalInt;

/**
 * Serializer for Adventure {@link Key} objects.
 */
public final class AdventureKeySerializer implements Serializer<Key, String> {

    private final String defaultNamespace;

    /**
     * Creates a new KeySerializer with the specified default namespace.
     *
     * @param defaultNamespace the default namespace to use when deserializing keys
     *                         without a namespace
     * @throws IllegalArgumentException if the namespace is invalid
     */
    public AdventureKeySerializer(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
        OptionalInt result = Key.checkNamespace(defaultNamespace);
        if (result.isPresent()) {
            throw new IllegalArgumentException(
                    "Invalid namespace at index " + result.getAsInt() + ": "
                            + defaultNamespace);
        }
    }

    /**
     * Creates a new KeySerializer using Adventure's default namespace (minecraft).
     */
    public AdventureKeySerializer() {
        this.defaultNamespace = null; // Use Adventure's default namespace
    }

    @Override
    public String serialize(Key element) {
        return element.asString();
    }

    @Override
    public Key deserialize(String element) {
        if (this.defaultNamespace == null) {
            return Key.key(element);
        }

        return Key.key(this.defaultNamespace, element);
    }
}
