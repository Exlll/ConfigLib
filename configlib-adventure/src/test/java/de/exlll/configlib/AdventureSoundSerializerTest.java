package de.exlll.configlib;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdventureSoundSerializerTest {
    private final AdventureSoundSerializer serializer = new AdventureSoundSerializer();

    @Test
    void testDeserializeBasic() {
        String input = "minecraft:entity.player.levelup";
        Sound sound = serializer.deserialize(input);

        assertEquals(Key.key("minecraft", "entity.player.levelup"), sound.name());
        assertEquals(1.0f, sound.pitch());
        assertEquals(1.0f, sound.volume());
        assertEquals(Sound.Source.MASTER, sound.source());
    }

    @Test
    void testDeserializeWithPitch() {
        String input = "minecraft:test" + AdventureSoundSerializer.DELIMITER + "1.5";
        Sound sound = serializer.deserialize(input);

        assertEquals(Key.key("minecraft", "test"), sound.name());
        assertEquals(1.5f, sound.pitch());
        assertEquals(1.0f, sound.volume());
        assertEquals(Sound.Source.MASTER, sound.source());
    }

    @Test
    void testDeserializeWithPitchAndVolume() {
        String input = "minecraft:test" + AdventureSoundSerializer.DELIMITER + "0.5"
                + AdventureSoundSerializer.DELIMITER + "2.0";
        Sound sound = serializer.deserialize(input);

        assertEquals(Key.key("minecraft", "test"), sound.name());
        assertEquals(0.5f, sound.pitch());
        assertEquals(2.0f, sound.volume());
        assertEquals(Sound.Source.MASTER, sound.source());
    }

    @Test
    void testDeserializeFull() {
        String input = "minecraft:test" + AdventureSoundSerializer.DELIMITER + "0.8"
                + AdventureSoundSerializer.DELIMITER + "0.5"
                + AdventureSoundSerializer.DELIMITER + "MUSIC";
        Sound sound = serializer.deserialize(input);

        assertEquals(Key.key("minecraft", "test"), sound.name());
        assertEquals(0.8f, sound.pitch());
        assertEquals(0.5f, sound.volume());
        assertEquals(Sound.Source.MUSIC, sound.source());
    }

    @Test
    void testDeserializeComplexKey() {
        String input = "minecraft:complex.key.name" + AdventureSoundSerializer.DELIMITER + "1.2";
        Sound sound = serializer.deserialize(input);

        assertEquals(Key.key("minecraft", "complex.key.name"), sound.name());
        assertEquals(1.2f, sound.pitch());
    }

    @Test
    void testDeserializeAmbiguousKeyLookingLikeFloat() {
        String input = "custom" + AdventureSoundSerializer.DELIMITER + "1.5";
        Sound sound = serializer.deserialize(input);
        assertEquals(Key.key("minecraft", "custom"), sound.name());
        assertEquals(1.5f, sound.pitch());
    }

    @Test
    void testDeserializeAmbiguousKeyLookingLikeSource() {
        String input = "custom" + AdventureSoundSerializer.DELIMITER + "MUSIC";
        Sound sound = serializer.deserialize(input);
        assertEquals(Key.key("minecraft", "custom"), sound.name());
        assertEquals(Sound.Source.MUSIC, sound.source());
    }

    @Test
    void testRoundTrip() {
        Sound original = Sound.sound(
                Key.key("test:sound"), Sound.Source.AMBIENT, 0.5f, 1.2f);
        String serialized = serializer.serialize(original);
        Sound deserialized = serializer.deserialize(serialized);

        assertEquals(original.name(), deserialized.name());
        assertEquals(original.source(), deserialized.source());
        assertEquals(original.volume(), deserialized.volume());
        assertEquals(original.pitch(), deserialized.pitch());
    }

    @Test
    void testDeserializeInvalidSourceThrowsConfigurationException() {
        String input = "minecraft:test" + AdventureSoundSerializer.DELIMITER + "1.0"
                + AdventureSoundSerializer.DELIMITER + "1.0"
                + AdventureSoundSerializer.DELIMITER + "INVALID_SOURCE";

        ConfigurationException exception = org.junit.jupiter.api.Assertions.assertThrows(
                ConfigurationException.class,
                () -> serializer.deserialize(input));

        org.junit.jupiter.api.Assertions.assertTrue(
                exception.getMessage().contains("Invalid sound source 'INVALID_SOURCE'"),
                "Exception message should mention the invalid source");
    }
}
