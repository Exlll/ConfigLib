package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static de.exlll.configlib.TestUtils.createPlatformSpecificFilePath;
import static org.junit.jupiter.api.Assertions.*;

class AdventureComponentTests {
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

    @Test
    void testTranslatableComponentRoundTrip() {
        AdventureComponentSerializer serializer = new AdventureComponentSerializer(
                AdventureComponentFormat.TRANSLATION_KEY);

        TranslatableComponent original = Component.translatable("item.minecraft.diamond_sword");
        TranslatableComponent originalWithDefaultMessage = Component.translatable(
                "item.minecraft.diamond_sword",
                "Default Message");

        String serialized = serializer.serialize(original);

        String serializedWithDefaultMessage = serializer.serialize(originalWithDefaultMessage);

        // Deserialize and validate returned TranslatableComponent
        TranslatableComponent deserialized = assertInstanceOf(TranslatableComponent.class,
                serializer.deserialize(serialized));
        TranslatableComponent deserializedWithDefaultMessage = assertInstanceOf(TranslatableComponent.class,
                serializer.deserialize(serializedWithDefaultMessage));

        // Basic round-trip equality check
        assertEquals(original, deserialized,
                "Deserialized component does not match the original");

        // The default message should not be preserved in this format
        assertNotEquals(originalWithDefaultMessage, deserializedWithDefaultMessage,
                "Deserialized component should not match the original with default message");
        assertEquals(originalWithDefaultMessage.key(), deserializedWithDefaultMessage.key());
    }

    @Test
    void testMultipleFormats() {
        List<AdventureComponentFormat> baseFormats = new ArrayList<>(List.of(
                AdventureComponentFormat.MINI_MESSAGE,
                AdventureComponentFormat.MINECRAFT_JSON,
                AdventureComponentFormat.LEGACY_AMPERSAND,
                AdventureComponentFormat.LEGACY_SECTION));

        List<List<AdventureComponentFormat>> allPermutations = generatePermutations(baseFormats);

        for (List<AdventureComponentFormat> deserializeOrder : allPermutations) {
            runTestWithOrder(deserializeOrder);
        }
    }

    private void runTestWithOrder(List<AdventureComponentFormat> deserializeOrder) {
        YamlConfigurationProperties.Builder<?> builder = YamlConfigurationProperties
                .newBuilder();
        // Set a fixed serialize order, we are testing deserialization.
        AdventureConfigLib.addDefaults(builder,
                AdventureComponentFormat.MINI_MESSAGE, deserializeOrder);
        YamlConfigurationProperties properties = builder.build();

        YamlConfigurationStore<MixedConfiguration> store = new YamlConfigurationStore<>(
                MixedConfiguration.class, properties);

        // Manually create the YAML content to ensure specific formats are present
        // We use double quotes to ensure characters like & and § are preserved and
        // parsed as strings
        String yamlContent = """
                json: '{"text":"strict_json","color":"yellow"}'
                miniMessage: '<green>strict_mini'
                legacyAmpersand: '&bstrict_ampersand'
                legacySection: '§dstrict_section'
                """;

        try {
            Files.writeString(yamlFile, yamlContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MixedConfiguration loaded = store.load(yamlFile);

        assertEquals(
                Component.text("strict_json", NamedTextColor.YELLOW),
                loaded.json,
                "JSON mismatch with order: " + deserializeOrder);
        assertEquals(
                Component.text("strict_mini", NamedTextColor.GREEN),
                loaded.miniMessage,
                "MiniMessage mismatch with order: " + deserializeOrder);
        assertEquals(
                Component.text("strict_ampersand", NamedTextColor.AQUA),
                loaded.legacyAmpersand,
                "Legacy Ampersand mismatch with order: " + deserializeOrder);
        assertEquals(
                Component.text("strict_section", NamedTextColor.LIGHT_PURPLE),
                loaded.legacySection,
                "Legacy Section mismatch with order: " + deserializeOrder);
    }

    @Configuration
    static class MixedConfiguration {
        Component json;
        Component miniMessage;
        Component legacyAmpersand;
        Component legacySection;
    }

    private <E> List<List<E>> generatePermutations(List<E> original) {
        if (original.isEmpty()) {
            List<List<E>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        E firstElement = original.get(0);
        List<List<E>> returnValue = new ArrayList<>();
        List<List<E>> permutations = generatePermutations(
                original.subList(1, original.size()));
        for (List<E> smallerPermutated : permutations) {
            for (int index = 0; index <= smallerPermutated.size(); index++) {
                List<E> temp = new ArrayList<>(smallerPermutated);
                temp.add(index, firstElement);
                returnValue.add(temp);
            }
        }
        return returnValue;
    }
}
