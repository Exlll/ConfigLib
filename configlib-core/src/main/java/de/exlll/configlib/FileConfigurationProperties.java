package de.exlll.configlib;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * An extension of the {@code ConfigurationProperties} class that allows configuring properties
 * that are more specific to files.
 */
public class FileConfigurationProperties extends ConfigurationProperties {
    private final String header;
    private final String footer;
    private final boolean createParentDirectories;
    private Charset charset;

    /**
     * Constructs a new instance of this class with values taken from the given builder.
     *
     * @param builder the builder used to initialize the fields of this class
     * @throws NullPointerException if the builder or any of its values is null
     */
    protected FileConfigurationProperties(Builder<?> builder) {
        super(builder);
        this.header = builder.header;
        this.footer = builder.footer;
        this.createParentDirectories = builder.createParentDirectories;
        this.charset = builder.charset;
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

        private BuilderImpl(FileConfigurationProperties properties) {super(properties);}

        @Override
        protected BuilderImpl getThis() {return this;}

        @Override
        public FileConfigurationProperties build() {return new FileConfigurationProperties(this);}
    }

    /**
     * A builder class for constructing {@code FileConfigurationProperties}.
     *
     * @param <B> the type of builder
     */
    public static abstract class Builder<B extends Builder<B>>
            extends ConfigurationProperties.Builder<B> {
        private String header = null;
        private String footer = null;
        private boolean createParentDirectories = true;
        private Charset charset = StandardCharsets.UTF_8;

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
        protected Builder(FileConfigurationProperties properties) {
            super(properties);
            this.header = properties.header;
            this.footer = properties.footer;
            this.createParentDirectories = properties.createParentDirectories;
            this.charset = properties.charset;
        }

        /**
         * Sets the header. The header is written as a comment before the actual configuration.
         * If the header is set to null (the default), nothing is written.
         *
         * @param header the header
         * @return this builder
         */
        public final B header(String header) {
            this.header = header;
            return getThis();
        }

        /**
         * Sets the footer. The footer is written as a comment after the actual configuration.
         * If the footer is set to null (the default), nothing is written.
         *
         * @param footer the footer
         * @return this builder
         */
        public final B footer(String footer) {
            this.footer = footer;
            return getThis();
        }

        /**
         * Sets whether parent directories of a configuration file should be created.
         * <p>
         * The default value is {@code true}.
         *
         * @param createParentDirectories whether to create parent directories
         * @return this builder
         */
        public final B createParentDirectories(boolean createParentDirectories) {
            this.createParentDirectories = createParentDirectories;
            return getThis();
        }

        /**
         * Sets the charset used to read and write configuration files.
         * <p>
         * The default value is {@code StandardCharsets.UTF_8}.
         *
         * @param charset the charset
         * @return this builder
         */
        public final B charset(Charset charset) {
            this.charset = charset;
            return getThis();
        }

        /**
         * Builds a {@code ConfigurationProperties} instance.
         *
         * @return newly constructed {@code ConfigurationProperties}
         */
        public abstract FileConfigurationProperties build();

        /**
         * Returns this builder.
         *
         * @return this builder
         */
        protected abstract B getThis();
    }

    /**
     * Returns the header.
     *
     * @return the header
     */
    public final String getHeader() {
        return header;
    }

    /**
     * Returns the footer.
     *
     * @return the footer
     */
    public final String getFooter() {
        return footer;
    }


    /**
     * Returns whether to create parent directories.
     *
     * @return whether to create parent directories
     */
    public final boolean createParentDirectories() {
        return createParentDirectories;
    }

    /**
     * Returns the charset used to read and write configuration files.
     *
     * @return the charset
     */
    public final Charset getCharset() {
        return charset;
    }

}
