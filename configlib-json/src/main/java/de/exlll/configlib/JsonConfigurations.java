package de.exlll.configlib;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * This class contains convenience methods for reading, writing, loading, saving,
 * and updating JSON configurations using Jackson.
 */
public final class JsonConfigurations {
    private JsonConfigurations() {}

    /**
     * Loads a configuration of the given type from the specified JSON file using a
     * {@code JsonConfigurationProperties} object with default values.
     *
     * @param configurationFile the file the configuration is loaded from
     * @param configurationType the type of configuration
     * @param <T>               the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @see JsonConfigurationStore#load(Path)
     */
    public static <T> T load(Path configurationFile, Class<T> configurationType) {
        final var properties = JsonConfigurationProperties.newBuilder().build();
        return load(configurationFile, configurationType, properties);
    }

    /**
     * Loads a configuration of the given type from the specified JSON file using a
     * {@code JsonConfigurationProperties} object that is built by a builder.
     *
     * @param configurationFile    the file the configuration is loaded from
     * @param configurationType    the type of configuration
     * @param propertiesConfigurer the consumer used to configure the builder
     * @param <T>                  the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @see JsonConfigurationStore#load(Path)
     */
    public static <T> T load(
            Path configurationFile,
            Class<T> configurationType,
            Consumer<JsonConfigurationProperties.Builder<?>> propertiesConfigurer
    ) {
        final var builder = JsonConfigurationProperties.newBuilder();
        propertiesConfigurer.accept(builder);
        return load(configurationFile, configurationType, builder.build());
    }

    /**
     * Loads a configuration of the given type from the specified JSON file using the given
     * {@code JsonConfigurationProperties} object.
     *
     * @param configurationFile the file the configuration is loaded from
     * @param configurationType the type of configuration
     * @param properties        the configuration properties
     * @param <T>               the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @see JsonConfigurationStore#load(Path)
     */
    public static <T> T load(
            Path configurationFile,
            Class<T> configurationType,
            JsonConfigurationProperties properties
    ) {
        final var store = new JsonConfigurationStore<>(configurationType, properties);
        return store.load(configurationFile);
    }

    /**
     * Reads a configuration of the given type from the given input stream using a
     * {@code JsonConfigurationProperties} object with default values.
     *
     * @param inputStream       the input stream the configuration is read from
     * @param configurationType the type of configuration
     * @param <T>               the configuration type
     * @return a newly created configuration initialized with values read from {@code inputStream}
     * @see JsonConfigurationStore#read(InputStream)
     */
    public static <T> T read(InputStream inputStream, Class<T> configurationType) {
        final var properties = JsonConfigurationProperties.newBuilder().build();
        return read(inputStream, configurationType, properties);
    }

    /**
     * Reads a configuration of the given type from the given input stream using a
     * {@code JsonConfigurationProperties} object that is built by a builder.
     *
     * @param inputStream          the input stream the configuration is read from
     * @param configurationType    the type of configuration
     * @param propertiesConfigurer the consumer used to configure the builder
     * @param <T>                  the configuration type
     * @return a newly created configuration initialized with values read from {@code inputStream}
     * @see JsonConfigurationStore#read(InputStream)
     */
    public static <T> T read(
            InputStream inputStream,
            Class<T> configurationType,
            Consumer<JsonConfigurationProperties.Builder<?>> propertiesConfigurer
    ) {
        final var builder = JsonConfigurationProperties.newBuilder();
        propertiesConfigurer.accept(builder);
        return read(inputStream, configurationType, builder.build());
    }

    /**
     * Reads a configuration of the given type from the given input stream using the given
     * {@code JsonConfigurationProperties} object.
     *
     * @param inputStream       the input stream the configuration is read from
     * @param configurationType the type of configuration
     * @param properties        the configuration properties
     * @param <T>               the configuration type
     * @return a newly created configuration initialized with values read from {@code inputStream}
     * @see JsonConfigurationStore#read(InputStream)
     */
    public static <T> T read(
            InputStream inputStream,
            Class<T> configurationType,
            JsonConfigurationProperties properties
    ) {
        final var store = new JsonConfigurationStore<>(configurationType, properties);
        return store.read(inputStream);
    }

    /**
     * Updates a JSON configuration file with a configuration of the given type using a
     * {@code JsonConfigurationProperties} object with default values.
     *
     * @param configurationFile the configuration file that is updated
     * @param configurationType the type of configuration
     * @param <T>               the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @see JsonConfigurationStore#update(Path)
     */
    public static <T> T update(Path configurationFile, Class<T> configurationType) {
        final var properties = JsonConfigurationProperties.newBuilder().build();
        return update(configurationFile, configurationType, properties);
    }

    /**
     * Updates a JSON configuration file with a configuration of the given type using a
     * {@code JsonConfigurationProperties} object that is built by a builder.
     *
     * @param configurationFile    the configuration file that is updated
     * @param configurationType    the type of configuration
     * @param propertiesConfigurer the consumer used to configure the builder
     * @param <T>                  the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @see JsonConfigurationStore#update(Path)
     */
    public static <T> T update(
            Path configurationFile,
            Class<T> configurationType,
            Consumer<JsonConfigurationProperties.Builder<?>> propertiesConfigurer
    ) {
        final var builder = JsonConfigurationProperties.newBuilder();
        propertiesConfigurer.accept(builder);
        return update(configurationFile, configurationType, builder.build());
    }

    /**
     * Updates a JSON configuration file with a configuration of the given type using the given
     * {@code JsonConfigurationProperties} object.
     *
     * @param configurationFile the configuration file that is updated
     * @param configurationType the type of configuration
     * @param properties        the configuration properties
     * @param <T>               the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @see JsonConfigurationStore#update(Path)
     */
    public static <T> T update(
            Path configurationFile,
            Class<T> configurationType,
            JsonConfigurationProperties properties
    ) {
        final var store = new JsonConfigurationStore<>(configurationType, properties);
        return store.update(configurationFile);
    }

    /**
     * Saves a configuration of the given type to the specified JSON file using a
     * {@code JsonConfigurationProperties} object with default values.
     *
     * @param configurationFile the file the configuration is saved to
     * @param configurationType the type of configuration
     * @param configuration     the configuration that is saved
     * @param <T>               the configuration type
     * @see JsonConfigurationStore#save(Object, Path)
     */
    public static <T> void save(
            Path configurationFile,
            Class<T> configurationType,
            T configuration
    ) {
        final var properties = JsonConfigurationProperties.newBuilder().build();
        save(configurationFile, configurationType, configuration, properties);
    }

    /**
     * Saves a configuration of the given type to the specified JSON file using a
     * {@code JsonConfigurationProperties} object that is built by a builder.
     *
     * @param configurationFile    the file the configuration is saved to
     * @param configurationType    the type of configuration
     * @param configuration        the configuration that is saved
     * @param propertiesConfigurer the consumer used to configure the builder
     * @param <T>                  the configuration type
     * @see JsonConfigurationStore#save(Object, Path)
     */
    public static <T> void save(
            Path configurationFile,
            Class<T> configurationType,
            T configuration,
            Consumer<JsonConfigurationProperties.Builder<?>> propertiesConfigurer
    ) {
        final var builder = JsonConfigurationProperties.newBuilder();
        propertiesConfigurer.accept(builder);
        save(configurationFile, configurationType, configuration, builder.build());
    }

    /**
     * Saves a configuration of the given type to the specified JSON file using the given
     * {@code JsonConfigurationProperties} object.
     *
     * @param configurationFile the file the configuration is saved to
     * @param configurationType the type of configuration
     * @param configuration     the configuration that is saved
     * @param properties        the configuration properties
     * @param <T>               the configuration type
     * @see JsonConfigurationStore#save(Object, Path)
     */
    public static <T> void save(
            Path configurationFile,
            Class<T> configurationType,
            T configuration,
            JsonConfigurationProperties properties
    ) {
        final var store = new JsonConfigurationStore<>(configurationType, properties);
        store.save(configuration, configurationFile);
    }

    /**
     * Writes a configuration instance to the given output stream using a
     * {@code JsonConfigurationProperties} object with default values.
     *
     * @param outputStream      the output stream the configuration is written to
     * @param configurationType the type of configuration
     * @param configuration     the configuration that is saved
     * @param <T>               the configuration type
     * @see JsonConfigurationStore#write(Object, OutputStream)
     */
    public static <T> void write(
            OutputStream outputStream,
            Class<T> configurationType,
            T configuration
    ) {
        final var properties = JsonConfigurationProperties.newBuilder().build();
        write(outputStream, configurationType, configuration, properties);
    }

    /**
     * Writes a configuration instance to the given output stream using a
     * {@code JsonConfigurationProperties} object that is built by a builder.
     *
     * @param outputStream         the output stream the configuration is written to
     * @param configurationType    the type of configuration
     * @param configuration        the configuration that is saved
     * @param propertiesConfigurer the consumer used to configure the builder
     * @param <T>                  the configuration type
     * @see JsonConfigurationStore#write(Object, OutputStream)
     */
    public static <T> void write(
            OutputStream outputStream,
            Class<T> configurationType,
            T configuration,
            Consumer<JsonConfigurationProperties.Builder<?>> propertiesConfigurer
    ) {
        final var builder = JsonConfigurationProperties.newBuilder();
        propertiesConfigurer.accept(builder);
        write(outputStream, configurationType, configuration, builder.build());
    }

    /**
     * Writes a configuration instance to the given output stream using the given
     * {@code JsonConfigurationProperties} object.
     *
     * @param outputStream      the output stream the configuration is written to
     * @param configurationType the type of configuration
     * @param configuration     the configuration that is saved
     * @param properties        the configuration properties
     * @param <T>               the configuration type
     * @see JsonConfigurationStore#write(Object, OutputStream)
     */
    public static <T> void write(
            OutputStream outputStream,
            Class<T> configurationType,
            T configuration,
            JsonConfigurationProperties properties
    ) {
        final var store = new JsonConfigurationStore<>(configurationType, properties);
        store.write(configuration, outputStream);
    }
}