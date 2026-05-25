package de.exlll.configlib;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Represents the different text formats supported for Adventure Component
 * serialization.
 */
public enum AdventureComponentFormat {
    /**
     * MiniMessage format with tags like {@code <red>} or {@code <bold>}.
     */
    MINI_MESSAGE(Patterns.MINI_MESSAGE_PATTERN.asPredicate()),
    /**
     * Translation key format for translatable components.
     */
    TRANSLATION_KEY(input -> true), // translation keys can be any format
    /**
     * Legacy format using ampersand ({@code &}) as the color code prefix.
     */
    LEGACY_AMPERSAND(input ->
            input.indexOf(LegacyComponentSerializer.AMPERSAND_CHAR) != -1),
    /**
     * Legacy format using section symbol ({@code §}) as the color code prefix.
     */
    LEGACY_SECTION(input ->
            input.indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1),
    /**
     * Minecraft JSON format for components.
     */
    MINECRAFT_JSON(input -> {
        input = input.trim();
        return input.startsWith("{") && input.endsWith("}");
    });

    // Hack to avoid compiler error while singleton pattern initialization
    private static class Patterns {
        // Pattern to detect any <tag> in a string
        static final Pattern MINI_MESSAGE_PATTERN =
                Pattern.compile("<[a-zA-Z0-9_:-]+(?::[^<>]+)?>");
    }

    private final Predicate<String> inputPredicate;

    AdventureComponentFormat(Predicate<String> inputPredicate) {
        this.inputPredicate = inputPredicate;
    }

    /**
     * Checks if the given input string matches this format.
     *
     * @param input the input string to check
     * @return true if the input matches this format, false otherwise
     */
    public boolean matches(String input) {
        if (input == null) {
            return false;
        }
        return inputPredicate.test(input);
    }
}
