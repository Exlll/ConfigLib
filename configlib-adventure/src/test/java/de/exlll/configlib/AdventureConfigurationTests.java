package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static de.exlll.configlib.TestUtils.createPlatformSpecificFilePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AdventureConfigurationTests {
    private FileSystem fs;
    private Path yamlFile;

    @BeforeEach
    void setUp() throws IOException {
        fs = Jimfs.newFileSystem();
        yamlFile = fs.getPath(createPlatformSpecificFilePath("/tmp/config.yml"));
        Files.createDirectories(yamlFile.getParent());
    }

    @AfterEach
    void tearDown() throws IOException {
        if (fs != null) {
            fs.close();
        }
    }

    // Test to make sure data is consistent after serialization and deserialization
    @Test
    void testSerializationAndDeserializationDefaults() {
        YamlConfigurationProperties.Builder<?> builder = YamlConfigurationProperties
                .newBuilder();
        AdventureConfigLib.addDefaults(builder);
        YamlConfigurationProperties properties = builder.build();

        YamlConfigurationStore<AdventureTestConfiguration> store = new YamlConfigurationStore<>(
                AdventureTestConfiguration.class, properties);

        AdventureTestConfiguration original = new AdventureTestConfiguration();
        store.save(original, yamlFile);

        AdventureTestConfiguration loaded = store.load(yamlFile);

        assertConfigsEqual(original, loaded);
    }

    private void assertConfigsEqual(AdventureTestConfiguration expected,
                                    AdventureTestConfiguration actual) {
        assertNotNull(actual);
        assertEquals(expected.simpleText,
                actual.simpleText,
                "Simple Text mismatch");
        assertEquals(expected.coloredText,
                actual.coloredText,
                "Colored Text mismatch");
        assertEquals(expected.decoratedText,
                actual.decoratedText,
                "Decorated Text mismatch");
        assertEquals(expected.clickLink,
                actual.clickLink,
                "Click Link mismatch");
        assertEquals(expected.hoverText,
                actual.hoverText,
                "Hover Text mismatch");

        assertEquals(serialize(expected.gradientText),
                serialize(actual.gradientText),
                "Gradient Text mismatch");
        assertEquals(serialize(expected.rainbowText),
                serialize(actual.rainbowText),
                "Rainbow Text mismatch");
        assertEquals(serialize(expected.formattedText),
                serialize(actual.formattedText),
                "Formatted Text mismatch");
        assertEquals(serialize(expected.clickText),
                serialize(actual.clickText),
                "Click Text mismatch");
        assertEquals(serialize(expected.hoverTextComplex),
                serialize(actual.hoverTextComplex),
                "Hover Text Complex mismatch");
        assertEquals(serialize(expected.keybindText),
                serialize(actual.keybindText),
                "Keybind Text (MM) mismatch");
        assertEquals(serialize(expected.translatableText),
                serialize(actual.translatableText),
                "Translatable Text (MM) mismatch");

        assertEquals(expected.translatable,
                actual.translatable,
                "Translatable mismatch");
        assertEquals(expected.keybind,
                actual.keybind,
                "Keybind mismatch");

        assertEquals(expected.simpleKey,
                actual.simpleKey,
                "Key mismatch");

        assertEquals(expected.simpleSound.name(),
                actual.simpleSound.name(),
                "Sound Name mismatch");
        assertEquals(expected.simpleSound.source(),
                actual.simpleSound.source(),
                "Sound Source mismatch");
        assertEquals(expected.simpleSound.volume(),
                actual.simpleSound.volume(),
                "Sound Volume mismatch");
        assertEquals(expected.simpleSound.pitch(),
                actual.simpleSound.pitch(),
                "Sound Pitch mismatch");

        assertEquals(expected.componentList,
                actual.componentList,
                "List mismatch");
        assertEquals(expected.componentMap,
                actual.componentMap,
                "Map mismatch");
    }

    private String serialize(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }
}
