package de.exlll.configlib;

import de.exlll.configlib.format.FieldNameFormatter;
import de.exlll.configlib.format.FieldNameFormatters;

import java.io.IOException;
import java.util.*;

/**
 * Parent class of all configurations.
 * <p>
 * This class contains the most basic methods that every configuration needs.
 *
 * @param <C> type of the configuration
 */
public abstract class Configuration<C extends Configuration<C>> {
    /**
     * {@code Comments} object containing all class and field comments
     * of this configuration
     */
    protected final Comments comments;
    private final Properties props;

    /**
     * Constructs a new {@code Configuration} object.
     *
     * @param properties {@code Properties} used to configure this configuration
     * @throws NullPointerException if {@code properties} is null
     */
    protected Configuration(Properties properties) {
        this.props = Objects.requireNonNull(properties);
        this.comments = Comments.ofClass(getClass());
    }

    /**
     * Saves this {@code Configuration}.
     *
     * @throws ConfigurationException      if any field is not properly configured
     * @throws ConfigurationStoreException if an I/O error occurred while loading
     *                                     this configuration
     */
    public final void save() {
        try {
            preSave();
            Map<String, Object> map = FieldMapper.instanceToMap(this, props);
            getSource().saveConfiguration(getThis(), map);
        } catch (IOException e) {
            throw new ConfigurationStoreException(e);
        }
    }

    /**
     * Loads this {@code Configuration}.
     *
     * @throws ConfigurationException      if values cannot be converted back to their
     *                                     original representation
     * @throws ConfigurationStoreException if an I/O error occurred while loading
     *                                     this configuration
     */
    public final void load() {
        try {
            Map<String, Object> map = getSource().loadConfiguration(getThis());
            FieldMapper.instanceFromMap(this, map, props);
            postLoad();
        } catch (IOException e) {
            throw new ConfigurationStoreException(e);
        }
    }

    /**
     * Returns the {@link ConfigurationSource} used for saving and loading this
     * {@code Configuration}.
     *
     * @return {@code ConfigurationSource} used for saving and loading
     */
    protected abstract ConfigurationSource<C> getSource();

    /**
     * Returns this {@code Configuration}.
     *
     * @return this {@code Configuration}
     */
    protected abstract C getThis();

    /**
     * Hook that is executed right before this {@code Configuration} is saved.
     * <p>
     * The default implementation of this method does nothing.
     */
    protected void preSave() {}

    /**
     * Hook that is executed right after this {@code Configuration} has
     * successfully been loaded.
     * <p>
     * The default implementation of this method does nothing.
     */
    protected void postLoad() {}

    /**
     * Instances of a {@code Properties} class are used to configure different
     * aspects of a configuration.
     */
    protected static class Properties {
        private final FieldNameFormatter formatter;

        /**
         * Constructs a new {@code Properties} object.
         *
         * @param builder {@code Builder} used for construction
         * @throws NullPointerException if {@code builder} is null
         */
        protected Properties(Builder<?> builder) {
            this.formatter = builder.formatter;
        }

        static Builder<?> builder() {
            return new Builder() {
                @Override
                protected Builder<?> getThis() {
                    return this;
                }
            };
        }

        /**
         * Returns the {@code FieldNameFormatter} of a configuration.
         *
         * @return {@code FieldNameFormatter} of a configuration
         */
        public final FieldNameFormatter getFormatter() {
            return formatter;
        }

        /**
         * Builder classes are used for constructing {@code Properties}.
         *
         * @param <B> type of the builder
         */
        protected static abstract class Builder<B extends Builder<B>> {
            private FieldNameFormatter formatter = FieldNameFormatters.IDENTITY;

            protected Builder() {}

            /**
             * Returns this {@code Builder}.
             *
             * @return this {@code Builder}
             */
            protected abstract B getThis();

            /**
             * Sets the {@link FieldNameFormatter} for a configuration.
             *
             * @param formatter formatter for configuration
             * @return this {@code Builder}
             * @throws NullPointerException if {@code formatter ist null}
             */
            public final B setFormatter(FieldNameFormatter formatter) {
                this.formatter = Objects.requireNonNull(formatter);
                return getThis();
            }

            /**
             * Builds a new {@code Properties} instance using the values set.
             *
             * @return new {@code Properties} instance
             */
            public Properties build() {
                return new Properties(this);
            }
        }
    }
}
