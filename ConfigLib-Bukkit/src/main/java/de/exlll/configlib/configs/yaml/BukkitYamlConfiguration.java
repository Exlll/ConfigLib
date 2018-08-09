package de.exlll.configlib.configs.yaml;

import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;

import java.nio.file.Path;

/**
 * A {@code BukkitYamlConfiguration} is a specialized form of a
 * {@code YamlConfiguration} that uses better default values.
 */
public abstract class BukkitYamlConfiguration extends YamlConfiguration {
    protected BukkitYamlConfiguration(Path path, BukkitYamlProperties properties) {
        super(path, properties);
    }

    protected BukkitYamlConfiguration(Path path) {
        this(path, BukkitYamlProperties.DEFAULT);
    }

    public static class BukkitYamlProperties extends YamlProperties {
        public static final BukkitYamlProperties DEFAULT = builder().build();

        private BukkitYamlProperties(Builder<?> builder) {
            super(builder);
        }

        public static Builder<?> builder() {
            return new Builder() {
                @Override
                protected Builder<?> getThis() {
                    return this;
                }
            };
        }

        public static abstract class
        Builder<B extends BukkitYamlProperties.Builder<B>>
                extends YamlProperties.Builder<B> {

            protected Builder() {
                setConstructor(new YamlConstructor());
                setRepresenter(new YamlRepresenter());
            }

            /**
             * Builds a new {@code BukkitYamlProperties} instance using the values set.
             *
             * @return new {@code BukkitYamlProperties} instance
             */
            public BukkitYamlProperties build() {
                return new BukkitYamlProperties(this);
            }
        }
    }
}
