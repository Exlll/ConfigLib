package de.exlll.configlib;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serializer for {@link Sound} objects.
 * <p>
 * String format: {@code <sound_id> [pitch] [volume] [source]}
 * <p>
 * Example: {@code "minecraft:entity.player.levelup 1.0 1.0 MASTER"}
 */
public final class AdventureSoundSerializer implements Serializer<Sound, String> {
    /**
     * The delimiter used to separate sound components in the serialized string.
     */
    public static final String DELIMITER = " ";

    private static final Pattern SOUND_PATTERN = Pattern.compile(buildRegex());

    private final Sound.Source defaultSource;

    /**
     * Creates a new SoundSerializer with the default source set to
     * {@link Sound.Source#MASTER}.
     */
    public AdventureSoundSerializer() {
        this.defaultSource = Sound.Source.MASTER;
    }

    /**
     * Creates a new SoundSerializer with the specified default source.
     *
     * @param defaultSource the default sound source to use when deserializing
     */
    public AdventureSoundSerializer(Sound.Source defaultSource) {
        Objects.requireNonNull(defaultSource, "defaultSource must not be null");
        this.defaultSource = defaultSource;
    }

    @Override
    public String serialize(Sound element) {
        StringBuilder builder = new StringBuilder(element.name().asString());
        if (element.source() != defaultSource) {
            builder.append(DELIMITER).append(formatFloatSimple(element.pitch()));
            builder.append(DELIMITER).append(formatFloatSimple(element.volume()));
            builder.append(DELIMITER).append(element.source().name());
        } else if (element.volume() != 1f) {
            builder.append(DELIMITER).append(formatFloatSimple(element.pitch()));
            builder.append(DELIMITER).append(formatFloatSimple(element.volume()));
        } else if (element.pitch() != 1f) {
            builder.append(DELIMITER).append(formatFloatSimple(element.pitch()));
        }

        return builder.toString();
    }

    @Override
    public Sound deserialize(String element) {
        Matcher matcher = SOUND_PATTERN.matcher(element);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid sound format: " + element);
        }

        String keyString = matcher.group("key");
        Key key = Key.key(keyString);

        float pitch = matcher.group("pitch") != null
                ? Float.parseFloat(matcher.group("pitch"))
                : 1.0f;

        float volume = matcher.group("volume") != null
                ? Float.parseFloat(matcher.group("volume"))
                : 1.0f;

        Sound.Source source = defaultSource;
        if (matcher.group("source") != null) {
            String sourceName = matcher.group("source");
            try {
                source = Sound.Source.valueOf(sourceName.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ConfigurationException("Invalid sound source '" + sourceName + "' in: " + element);
            }
        }

        return Sound.sound(key, source, volume, pitch);
    }

    // If the float is a whole number, remove the decimal part for simplicity
    private static String formatFloatSimple(float value) {
        String s = String.valueOf(value);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }

    private static String buildRegex() {
        String deliminator = Pattern.quote(DELIMITER);
        return "^(?<key>[a-zA-Z0-9:._-]+)" +
                "(?:" + deliminator + "+(?<pitch>\\d+(?:\\.\\d+)?))?" +
                "(?:" + deliminator + "+(?<volume>\\d+(?:\\.\\d+)?))?" +
                "(?:" + deliminator + "+(?<source>[a-zA-Z_]+))?" +
                deliminator + "*$";
    }
}
