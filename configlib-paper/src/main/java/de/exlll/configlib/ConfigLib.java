package de.exlll.configlib;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The plugin class that loads this library and its dependencies.
 */
public final class ConfigLib extends JavaPlugin {
    /**
     * A {@code YamlConfigurationProperties} object that provides serializers for several Bukkit
     * classes like {@link  ItemStack} and other {@link ConfigurationSerializable} types.
     * <p>
     * You can configure these properties further by creating a new builder using the
     * {@code toBuilder()} method of this object.
     */
    public static final YamlConfigurationProperties BUKKIT_DEFAULT_PROPERTIES =
            initializeBukkitDefaultProperties();

    private static YamlConfigurationProperties initializeBukkitDefaultProperties() {
        return YamlConfigurationProperties
                .newBuilder()
                .addSerializerByCondition(
                        type -> type instanceof Class<?> cls &&
                                ConfigurationSerializable.class.isAssignableFrom(cls),
                        BukkitConfigurationSerializableSerializer.DEFAULT
                )
                .build();
    }
}
