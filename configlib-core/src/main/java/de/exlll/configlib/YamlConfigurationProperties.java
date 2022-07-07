package de.exlll.configlib;

/**
 * An extension of the {@code FileConfigurationProperties} class that allows configuring properties
 * that are more specific to YAML files.
 */
public final class YamlConfigurationProperties extends FileConfigurationProperties {
    /**
     * Constructs a new instance of this class with values taken from the given builder.
     *
     * @param builder the builder used to initialize the fields of this class
     * @throws NullPointerException if the builder or any of its values is null
     */
    public YamlConfigurationProperties(Builder<?> builder) {
        super(builder);
    }

    /**
     * Constructs a new {@code Builder} with default values.
     *
     * @return newly constructed {@code Builder}
     */
    public static Builder<?> newBuilder() {
        return new BuilderImpl();
    }

    private static final class BuilderImpl extends Builder<BuilderImpl> {
        @Override
        protected BuilderImpl getThis() {return this;}

        @Override
        public YamlConfigurationProperties build() {return new YamlConfigurationProperties(this);}
    }

    /**
     * A builder class for constructing {@code YamlConfigurationProperties}.
     *
     * @param <B> the type of builder
     */
    public static abstract class Builder<B extends Builder<B>>
            extends FileConfigurationProperties.Builder<B> {

        /**
         * A constructor that can be overridden by subclasses.
         */
        protected Builder() {}

        /**
         * Builds a {@code ConfigurationProperties} instance.
         *
         * @return newly constructed {@code ConfigurationProperties}
         */
        public abstract YamlConfigurationProperties build();

        /**
         * Returns this builder.
         *
         * @return this builder
         */
        protected abstract B getThis();
    }
}