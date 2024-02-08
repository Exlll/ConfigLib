package de.exlll.configlib;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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

        protected Builder() {}

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
}
