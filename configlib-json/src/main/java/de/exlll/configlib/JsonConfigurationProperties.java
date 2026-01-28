package de.exlll.configlib;

/**
 * An extension of the {@code FileConfigurationProperties} class that allows configuring properties
 * that are more specific to JSON files.
 */
public final class JsonConfigurationProperties extends FileConfigurationProperties {
    /**
     * Constructs a new instance of this class with values taken from the given builder.
     *
     * @param builder the builder used to initialize the fields of this class
     * @throws NullPointerException if the builder or any of its values is null
     */
    public JsonConfigurationProperties(Builder<?> builder) {
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

    public Builder<?> toBuilder() {
        return new BuilderImpl(this);
    }

    private static final class BuilderImpl extends Builder<BuilderImpl> {
        private BuilderImpl() {}

        private BuilderImpl(JsonConfigurationProperties properties) {super(properties);}

        @Override
        protected BuilderImpl getThis() {return this;}

        @Override
        public JsonConfigurationProperties build() {return new JsonConfigurationProperties(this);}
    }

    /**
     * A builder class for constructing {@code JsonConfigurationProperties}.
     *
     * @param <B> the type of builder
     */
    public static abstract class Builder<B extends Builder<B>>
            extends FileConfigurationProperties.Builder<B> {

        /**
         * The default constructor.
         */
        protected Builder() {}

        /**
         * A constructor that initializes this builder with values taken from the properties object.
         *
         * @param properties the properties object the values are taken from
         * @throws NullPointerException if {@code properties} is null
         */
        protected Builder(JsonConfigurationProperties properties) {
            super(properties);
        }

        /**
         * Builds a {@code JsonConfigurationProperties} instance.
         *
         * @return newly constructed {@code JsonConfigurationProperties}
         */
        public abstract JsonConfigurationProperties build();

        /**
         * Returns this builder.
         *
         * @return this builder
         */
        protected abstract B getThis();
    }
}