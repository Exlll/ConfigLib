package de.exlll.configlib;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static de.exlll.configlib.Validator.requireNonNull;

/**
 * A collection of values used to configure the serialization of configurations.
 */
public class ConfigurationProperties {
    private final Map<Class<?>, Serializer<?, ?>>
            serializersByType;
    private final Map<Class<?>, Function<? super SerializerContext, ? extends Serializer<?, ?>>>
            serializerFactoriesByType;
    private final Map<Predicate<? super Type>, Serializer<?, ?>>
            serializersByCondition;
    private final Map<Predicate<? super ConfigurationElement<?>>, UnaryOperator<?>>
            postProcessorsByCondition;
    private final NameFormatter formatter;
    private final FieldFilter filter;
    private final boolean outputNulls;
    private final boolean inputNulls;
    private final boolean serializeSetsAsLists;
    private final EnvVarResolutionConfiguration envVarResolutionConfiguration;

    /**
     * Constructs a new instance of this class with values taken from the given builder.
     *
     * @param builder the builder used to initialize the fields of this class
     * @throws NullPointerException if the builder or any of its values is null
     */
    protected ConfigurationProperties(Builder<?> builder) {
        this.serializersByType = Map.copyOf(builder.serializersByType);
        this.serializerFactoriesByType = Map.copyOf(builder.serializerFactoriesByType);
        this.serializersByCondition = Collections.unmodifiableMap(
                new LinkedHashMap<>(builder.serializersByCondition)
        );
        this.postProcessorsByCondition = Collections.unmodifiableMap(
                new LinkedHashMap<>(builder.postProcessorsByCondition)
        );
        this.formatter = requireNonNull(builder.formatter, "name formatter");
        this.filter = requireNonNull(builder.filter, "field filter");
        this.outputNulls = builder.outputNulls;
        this.inputNulls = builder.inputNulls;
        this.serializeSetsAsLists = builder.serializeSetsAsLists;
        this.envVarResolutionConfiguration = requireNonNull(
                builder.envVarResolutionConfiguration,
                "environment variable resolution configuration"
        );
    }

    /**
     * Constructs a new {@code Builder} with default values.
     *
     * @return newly constructed {@code Builder}
     */
    public static Builder<?> newBuilder() {
        return new BuilderImpl();
    }

    /**
     * Creates a new builder and initializes it with values taken from this properties object.
     *
     * @return new builder
     */
    public Builder<?> toBuilder() {
        return new BuilderImpl(this);
    }

    private static final class BuilderImpl extends Builder<BuilderImpl> {
        private BuilderImpl() {}

        private BuilderImpl(ConfigurationProperties properties) {super(properties);}

        @Override
        protected BuilderImpl getThis() {return this;}

        @Override
        public ConfigurationProperties build() {return new ConfigurationProperties(this);}
    }

    /**
     * A builder class for constructing {@code ConfigurationProperties}.
     *
     * @param <B> the type of builder
     */
    public static abstract class Builder<B extends Builder<B>> {
        private final Map<Class<?>, Serializer<?, ?>>
                serializersByType = new HashMap<>();
        private final Map<Class<?>, Function<? super SerializerContext, ? extends Serializer<?, ?>>>
                serializerFactoriesByType = new HashMap<>();
        private final Map<Predicate<? super Type>, Serializer<?, ?>>
                serializersByCondition = new LinkedHashMap<>();
        private final Map<Predicate<? super ConfigurationElement<?>>, UnaryOperator<?>>
                postProcessorsByCondition = new LinkedHashMap<>();
        private NameFormatter formatter = NameFormatters.IDENTITY;
        private FieldFilter filter = FieldFilters.DEFAULT;
        private boolean outputNulls = false;
        private boolean inputNulls = false;
        private boolean serializeSetsAsLists = true;
        private EnvVarResolutionConfiguration envVarResolutionConfiguration
                = EnvVarResolutionConfiguration.disabled();

        /** Default constructor */
        protected Builder() {}

        /**
         * A constructor that initializes the values of this builder with the values
         * of the given {@code ConfigurationProperties} object.
         *
         * @param properties the properties used to initialize the values of this builder
         */
        protected Builder(ConfigurationProperties properties) {
            this.serializersByType.putAll(properties.serializersByType);
            this.serializerFactoriesByType.putAll(properties.serializerFactoriesByType);
            this.serializersByCondition.putAll(properties.serializersByCondition);
            this.postProcessorsByCondition.putAll(properties.postProcessorsByCondition);
            this.formatter = properties.formatter;
            this.filter = properties.filter;
            this.outputNulls = properties.outputNulls;
            this.inputNulls = properties.inputNulls;
            this.serializeSetsAsLists = properties.serializeSetsAsLists;
            this.envVarResolutionConfiguration = properties.envVarResolutionConfiguration;
        }

        /**
         * Sets the field filter. The given filter is applied in addition to and
         * after the default filter.
         *
         * @param filter the filter
         * @return this builder
         * @throws NullPointerException if {@code filter} is null
         */
        public final B setFieldFilter(FieldFilter filter) {
            this.filter = requireNonNull(filter, "field filter");
            return getThis();
        }

        /**
         * Sets the name formatter.
         * <p>
         * The default value is a formatter that returns the same name that was given to it.
         *
         * @param formatter the formatter
         * @return this builder
         * @throws NullPointerException if {@code formatter} is null
         */
        public final B setNameFormatter(NameFormatter formatter) {
            this.formatter = requireNonNull(formatter, "name formatter");
            return getThis();
        }

        /**
         * Adds a serializer for the given type.
         * <p>
         * If this library already provides a serializer for the given type
         * (e.g. {@code BigInteger}, {@code LocalDate}, etc.) the serializer
         * added by this method takes precedence.
         * <p>
         * If a factory is added via the {@link #addSerializerFactory(Class, Function)} method for
         * the same type, the serializer created by that factory takes precedence.
         *
         * @param serializedType the class of the type that is serialized
         * @param serializer     the serializer
         * @param <T>            the type that is serialized
         * @return this builder
         * @throws NullPointerException if any argument is null
         * @see #addSerializerFactory(Class, Function)
         */
        public final <T> B addSerializer(
                Class<T> serializedType,
                Serializer<? super T, ?> serializer
        ) {
            requireNonNull(serializedType, "serialized type");
            requireNonNull(serializer, "serializer");
            serializersByType.put(serializedType, serializer);
            return getThis();
        }

        /**
         * Adds a serializer factory for the given type.
         * <p>
         * If this library already provides a serializer for the given type
         * (e.g. {@code BigInteger}, {@code LocalDate}, etc.) the serializer
         * created by the factory takes precedence.
         * <p>
         * If a serializer is added via the {@link #addSerializer(Class, Serializer)} method
         * for the same type, the serializer created by the factory that was added by this
         * method takes precedence.
         *
         * @param serializedType    the class of the type that is serialized
         * @param serializerFactory the factory that creates a new serializer
         * @param <T>               the type that is serialized
         * @return this builder
         * @throws NullPointerException if any argument is null
         * @see #addSerializer(Class, Serializer)
         */
        public final <T> B addSerializerFactory(
                Class<T> serializedType,
                Function<? super SerializerContext, ? extends Serializer<T, ?>> serializerFactory
        ) {
            requireNonNull(serializedType, "serialized type");
            requireNonNull(serializerFactory, "serializer factory");
            serializerFactoriesByType.put(serializedType, serializerFactory);
            return getThis();
        }

        /**
         * Adds a serializer for the condition. The serializer is selected when the condition
         * evaluates to true. The {@code test} method of the condition object is invoked with
         * the generic element type. The conditions are checked in the order in which they were
         * added.
         *
         * @param condition  the condition
         * @param serializer the serializer
         * @return this builder
         * @throws NullPointerException if any argument is null
         */
        final B addSerializerByCondition(
                Predicate<? super Type> condition,
                Serializer<?, ?> serializer
        ) {
            requireNonNull(condition, "condition");
            requireNonNull(serializer, "serializer");
            serializersByCondition.put(condition, serializer);
            return getThis();
        }

        /**
         * Defines a post-processor for each configuration element that fulfils
         * the given condition. Multiple post-processors are applied if an
         * element fulfills more than one condition. The conditions are checked
         * in the order in which they were added.
         * <p>
         * <b>NOTE</b>:
         * It is the developer's responsibility to ensure that the type of the
         * configuration element matches the type the post-processor expects.
         *
         * @param condition     the condition that is checked
         * @param postProcessor the post-processor to be applied if the
         *                      condition is true
         * @return this builder
         * @throws NullPointerException if any argument is null
         * @see ConfigurationElementFilter
         */
        public final B addPostProcessor(
                Predicate<? super ConfigurationElement<?>> condition,
                UnaryOperator<?> postProcessor
        ) {
            requireNonNull(condition, "condition");
            requireNonNull(postProcessor, "post-processor");
            this.postProcessorsByCondition.put(condition, postProcessor);
            return getThis();
        }

        /**
         * Sets whether configuration elements, or collection elements whose value
         * is null should be output while serializing the configuration.
         * <p>
         * The default value is {@code false}.
         *
         * @param outputNulls whether to output null values
         * @return this builder
         */
        public final B outputNulls(boolean outputNulls) {
            this.outputNulls = outputNulls;
            return getThis();
        }

        /**
         * Sets whether configuration elements, or collection elements should
         * allow null values to bet set while deserializing the configuration.
         * <p>
         * If this option is set to false, null values read from a configuration
         * are treated as missing.
         * <p>
         * The default value is {@code false}.
         *
         * @param inputNulls whether to input null values
         * @return this builder
         */
        public final B inputNulls(boolean inputNulls) {
            this.inputNulls = inputNulls;
            return getThis();
        }

        /**
         * Sets whether sets should be serialized as lists.
         * <p>
         * The default value is {@code true}.
         *
         * @param serializeSetsAsLists whether to serialize sets as lists
         * @return this builder
         */
        final B serializeSetsAsLists(boolean serializeSetsAsLists) {
            this.serializeSetsAsLists = serializeSetsAsLists;
            return getThis();
        }

        /**
         * Configures whether and how environment variables should be resolved.
         * <p>
         * The default is {@link EnvVarResolutionConfiguration#disabled()} which
         * is a configuration that disables the resolution of environment variables.
         *
         * @param configuration the {@code EnvVarResolutionConfiguration}
         * @return this builder
         * @throws NullPointerException if {@code configuration} is null
         */
        public final B setEnvVarResolutionConfiguration(
                EnvVarResolutionConfiguration configuration
        ) {
            this.envVarResolutionConfiguration = requireNonNull(
                    configuration,
                    "environment variable resolution configuration"
            );
            return getThis();
        }

        /**
         * Builds a {@code ConfigurationProperties} instance.
         *
         * @return newly constructed {@code ConfigurationProperties}
         */
        public abstract ConfigurationProperties build();

        /**
         * Returns this builder.
         *
         * @return this builder
         */
        protected abstract B getThis();
    }

    /**
     * A collection of values used to configure the resolution of environment variables.
     */
    public static final class EnvVarResolutionConfiguration {
        private static final EnvVarResolutionConfiguration DISABLED =
                new EnvVarResolutionConfiguration();
        private final boolean resolveEnvVars;
        private final String prefix;
        private final boolean caseSensitive;

        /**
         * Returns a configuration object that disables the resolution of
         * environment variables.
         *
         * @return configuration object that disables the resolution of
         * environment variables.
         */
        public static EnvVarResolutionConfiguration disabled() {
            return DISABLED;
        }

        /**
         * Returns a configuration object that resolves environment variables
         * that start with the given prefix. Specifying a non-empty prefix that
         * is specific to your project is highly recommended.
         * <p>
         * On UNIX systems environment variables are case-sensitive.
         * On Windows they are case-insensitive.
         * <ul>
         * <li>
         * If you want to (or have to, because you are on Windows) target the
         * values of your configuration using uppercase environment variables,
         * then you have to disable case-sensitive resolution.
         * <p>
         * For example, if you have a YAML configuration with the following content
         * <pre>
         * alPHA:
         *   be_ta: 1
         * ga_mma:
         *   dELTa: 2
         * </pre>
         * then, with {@code caseSensitive} set to {@code false}, you can overwrite
         * the values 1 and 2 using the environment variables
         * {@code ALPHA_BE_TA} and {@code GA_MMA_DELTA}, respectively.
         * </li>
         * <li>
         * If your configuration contains paths that differ only in their case,
         * but are otherwise the same, and if you want to target these paths
         * individually, then you have to enable case-sensitive resolution.
         * <p>
         * For example, if you have a YAML configuration with the following content
         * <pre>
         * alpha:
         *   beta: 1
         * ALPHA:
         *   BETA: 2
         * </pre>
         * and you want to target the values of {@code beta} and {@code BETA}
         * individually, then you have to set {@code caseSensitive} to {@code true}.
         * After doing so you can overwrite the values 1 and 2 by using the environment
         * variables {@code alpha_beta} and {@code ALPHA_BETA}, respectively.
         * </li>
         * <li>
         * If you specify a non-empty prefix, then all variables that you want to
         * be resolved have to start with that prefix. For example, if you choose
         * {@code MY_PREFIX_} as your prefix, then the four variables listed
         * above have to be named {@code MY_PREFIX_ALPHA_BE_TA},
         * {@code MY_PREFIX_GA_MMA_DELTA}, {@code MY_PREFIX_alpha_beta}, and
         * {@code MY_PREFIX_ALPHA_BETA}, respectively.
         * </li>
         * </ul>
         *
         * @param prefix        string the environment variables have to be prefixed with
         * @param caseSensitive specifies whether the resolution should be case-sensitive
         * @return configuration object that resolves environment variables
         * that start with the given prefix
         * @throws NullPointerException if {@code prefix} is null
         */
        public static EnvVarResolutionConfiguration resolveEnvVarsWithPrefix(
                String prefix,
                boolean caseSensitive
        ) {
            return new EnvVarResolutionConfiguration(true, prefix, caseSensitive);
        }

        private EnvVarResolutionConfiguration() {
            this(false, "", false);
        }

        // create 'Builder' class if more configuration options are added
        private EnvVarResolutionConfiguration(
                boolean resolveEnvVars,
                String prefix,
                boolean caseSensitive
        ) {
            this.resolveEnvVars = resolveEnvVars;
            this.prefix = requireNonNull(prefix, "prefix");
            this.caseSensitive = caseSensitive;
        }

        /**
         * Returns whether environment variables should be resolved.
         *
         * @return whether environment variables should be resolved
         */
        public boolean resolveEnvVars() {
            return resolveEnvVars;
        }

        /**
         * Returns the string with which the environment variables must begin
         * in order to be resolved.
         *
         * @return the string environment variables have to begin with to be resolved
         * @see EnvVarResolutionConfiguration#resolveEnvVarsWithPrefix(String, boolean)
         */
        public String prefix() {
            return prefix;
        }

        /**
         * Returns whether the resolution of environment variables should be
         * case-sensitive.
         *
         * @return whether the resolution of environment variables should be
         * case-sensitive
         * @see EnvVarResolutionConfiguration#resolveEnvVarsWithPrefix(String, boolean)
         */
        public boolean caseSensitive() {
            return caseSensitive;
        }
    }

    /**
     * Returns the field filter used to filter the fields of a configuration class.
     *
     * @return the field filter
     */
    public final FieldFilter getFieldFilter() {
        return filter;
    }

    /**
     * Returns the name formatter used to format the names of configuration elements.
     *
     * @return the formatter
     */
    public final NameFormatter getNameFormatter() {
        return formatter;
    }

    /**
     * Returns an unmodifiable map of serializers by type. The serializers returned by this
     * method take precedence over any default serializers provided by this library.
     *
     * @return serializers by type
     */
    public final Map<Class<?>, Serializer<?, ?>> getSerializers() {
        return serializersByType;
    }

    /**
     * Returns an unmodifiable map of serializer factories by type. The serializers created by the
     * factories take precedence over any default serializers provided by this library.
     *
     * @return serializer factories by type
     */
    public final Map<Class<?>, Function<? super SerializerContext, ? extends Serializer<?, ?>>>
    getSerializerFactories() {
        return serializerFactoriesByType;
    }

    /**
     * Returns an unmodifiable map of serializers by condition.
     *
     * @return serializers by condition
     */
    final Map<Predicate<? super Type>, Serializer<?, ?>> getSerializersByCondition() {
        return serializersByCondition;
    }

    /**
     * Returns an unmodifiable map of post-processors by condition.
     *
     * @return post-processors by condition
     */
    public final Map<Predicate<? super ConfigurationElement<?>>, UnaryOperator<?>>
    getPostProcessorsByCondition() {
        return postProcessorsByCondition;
    }

    /**
     * Returns whether null values should be output.
     *
     * @return whether to output null values
     */
    public final boolean outputNulls() {
        return outputNulls;
    }

    /**
     * Returns whether null values should be allowed as input.
     *
     * @return whether to input null values
     */
    public final boolean inputNulls() {
        return inputNulls;
    }

    /**
     * Returns whether sets should be serialized as lists.
     *
     * @return whether to serialize sets as lists
     */
    final boolean serializeSetsAsLists() {
        return serializeSetsAsLists;
    }

    /**
     * Returns the configuration that determines whether and how environment
     * variables should be resolved.
     *
     * @return the configuration
     */
    public final EnvVarResolutionConfiguration getEnvVarResolutionConfiguration() {
        return envVarResolutionConfiguration;
    }
}
