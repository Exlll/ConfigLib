package de.exlll.configlib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.Arrays;
import java.util.List;

/**
 * Serializer for Adventure {@link Component} objects.
 * Supports multiple formats including MiniMessage, legacy, and JSON.
 */
public final class AdventureComponentSerializer implements Serializer<Component, String> {
    private final AdventureComponentFormat serializeFormat;
    private final List<AdventureComponentFormat> deserializeOrder;

    /**
     * Creates a new ComponentSerializer with separate format orders for
     * serialization and deserialization.
     *
     * @param serializeFormat  the format to use when serializing
     * @param deserializeOrder the order of formats to try when deserializing
     */
    public AdventureComponentSerializer(AdventureComponentFormat serializeFormat,
            List<AdventureComponentFormat> deserializeOrder) {
        this.serializeFormat = serializeFormat;
        this.deserializeOrder = List.copyOf(deserializeOrder);
    }

    /**
     * Creates a new ComponentSerializer using the same format order for
     * both serialization and deserialization.
     *
     * @param serializeFormat    the format to use for serialization
     * @param deserializeFormats the formats to use for deserialization, in order of
     *                           preference
     */
    public AdventureComponentSerializer(AdventureComponentFormat serializeFormat,
            AdventureComponentFormat... deserializeFormats) {
        this(serializeFormat, deserializeFormats.length == 0
                ? List.of(serializeFormat)
                : Arrays.asList(deserializeFormats));
    }

    @Override
    public String serialize(Component element) {
        if (element == null) {
            return null;
        }

        return serialize(element, serializeFormat);
    }

    @Override
    public Component deserialize(String element) {
        if (element == null) {
            return null;
        }

        for (AdventureComponentFormat format : deserializeOrder) {
            if (!format.matches(element)) {
                continue;
            }

            return deserialize(element, format);
        }

        // Fallback to MiniMessage
        return MiniMessage.miniMessage().deserialize(element);
    }

    private String serialize(Component component, AdventureComponentFormat format) {
        return switch (format) {
            case MINI_MESSAGE -> MiniMessage.miniMessage().serialize(component);
            case LEGACY_AMPERSAND ->
                LegacyComponentSerializer.legacyAmpersand().serialize(component);
            case LEGACY_SECTION ->
                LegacyComponentSerializer.legacySection().serialize(component);
            case MINECRAFT_JSON -> GsonComponentSerializer.gson().serialize(component);
            case TRANSLATION_KEY ->
                component instanceof TranslatableComponent translatableComponent
                        ? translatableComponent.key()
                        : PlainTextComponentSerializer.plainText().serialize(component);
        };
    }

    private Component deserialize(String string, AdventureComponentFormat format) {
        return switch (format) {
            case MINI_MESSAGE ->
                MiniMessage.miniMessage().deserialize(string);
            case LEGACY_AMPERSAND ->
                LegacyComponentSerializer.legacyAmpersand().deserialize(string);
            case LEGACY_SECTION ->
                LegacyComponentSerializer.legacySection().deserialize(string);
            case MINECRAFT_JSON ->
                GsonComponentSerializer.gson().deserialize(string);
            case TRANSLATION_KEY ->
                Component.translatable(string);
        };
    }
}
