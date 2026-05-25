package de.exlll.configlib;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * Utility class providing default serializers for Adventure library types.
 */
public final class AdventureConfigLib {
    // Use MiniMessage as the default format since MiniMessage covered all
    // component type
    private static final List<AdventureComponentFormat> DEFAULT_FORMAT_ORDER = List.of(
            AdventureComponentFormat.MINI_MESSAGE
        );

    private AdventureConfigLib() {
    }

    /**
     * Adds default Adventure serializers to the configuration builder.
     *
     * @param builder the configuration properties builder
     * @param <B>     the builder type
     * @return the builder with default serializers added
     */
    public static <B extends ConfigurationProperties.Builder<B>> 
    ConfigurationProperties.Builder<B> addDefaults(
            ConfigurationProperties.Builder<B> builder) {
        return addDefaults(builder, DEFAULT_FORMAT_ORDER.get(0), DEFAULT_FORMAT_ORDER);
    }

    /**
     * Adds default Adventure serializers to the configuration builder with custom
     * format orders.
     *
     * @param builder          the configuration properties builder
     * @param serializeFormat  the format to use when serializing
     *                         components
     * @param deserializeOrder the order of formats to try when deserializing
     *                         components
     * @param <B>              the builder type
     * @return the builder with default serializers added
     */
    public static <B extends ConfigurationProperties.Builder<B>> 
    ConfigurationProperties.Builder<B> addDefaults(
            ConfigurationProperties.Builder<B> builder,
            AdventureComponentFormat serializeFormat,
            List<AdventureComponentFormat> deserializeOrder) {
        builder.addSerializer(Component.class,
                new AdventureComponentSerializer(serializeFormat, deserializeOrder));
        builder.addSerializer(Key.class, new AdventureKeySerializer());
        builder.addSerializer(Sound.class, new AdventureSoundSerializer());
        return builder;
    }
}
