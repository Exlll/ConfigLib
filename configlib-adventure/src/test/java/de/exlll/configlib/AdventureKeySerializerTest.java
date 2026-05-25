package de.exlll.configlib;

import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdventureKeySerializerTest {

    @Test
    void testSerialize() {
        AdventureKeySerializer serializer = new AdventureKeySerializer();
        Key key = Key.key("namespace", "value");
        assertEquals("namespace:value", serializer.serialize(key));
    }

    @Test
    void testDeserializeWithColon() {
        AdventureKeySerializer serializer = new AdventureKeySerializer();
        Key key = serializer.deserialize("namespace:value");
        assertEquals(Key.key("namespace", "value"), key);
    }

    @Test
    void testDeserializeWithoutColonAndNoDefaultNamespace() {
        AdventureKeySerializer serializer = new AdventureKeySerializer();
        // Adventure default namespace is "minecraft"
        Key key = serializer.deserialize("value");
        assertEquals(Key.key("minecraft", "value"), key);
    }

    @Test
    void testDeserializeWithoutColonAndDefaultNamespace() {
        AdventureKeySerializer serializer = new AdventureKeySerializer("default");
        Key key = serializer.deserialize("value");
        assertEquals(Key.key("default", "value"), key);
    }

    @Test
    void testDeserializeWithColonAndDefaultNamespace() {
        AdventureKeySerializer serializer = new AdventureKeySerializer("default");
        // Key.key("default", "namespace:value") will throw InvalidKeyException because
        // ':' is not allowed in value
        assertThrows(InvalidKeyException.class, () ->
                serializer.deserialize("namespace:value"));
    }
}
