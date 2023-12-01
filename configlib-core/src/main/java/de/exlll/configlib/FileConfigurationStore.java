package de.exlll.configlib;

import java.nio.file.Path;

/**
 * Instances of this class save and load configurations using files.
 *
 * @param <T> the configuration type
 */
public interface FileConfigurationStore<T> {
    /**
     * Saves a configuration instance to the given file.
     *
     * @param configuration     the configuration
     * @param configurationFile the file the configuration is saved to
     * @throws ConfigurationException if the configuration contains invalid values or
     *                                cannot be serialized
     * @throws NullPointerException   if any argument is null
     * @throws RuntimeException       if writing the configuration throws an exception
     */
    void save(T configuration, Path configurationFile);

    /**
     * Loads a configuration from the given file.
     *
     * @param configurationFile the file the configuration is loaded from
     * @return a newly created configuration initialized with values taken from the configuration file
     * @throws ConfigurationException   if the configuration cannot be deserialized
     * @throws IllegalArgumentException if the file does not exist or is not a regular file
     * @throws NullPointerException     if {@code configurationFile} is null
     * @throws RuntimeException         if reading the configuration throws an exception
     */
    T load(Path configurationFile);

    /**
     * Updates the configuration file.
     * <ul>
     * <li>
     * If the file does not exist, it is created and populated with the default values with which
     * the fields of the configuration have been initialized. If the configuration is of record type,
     * the default values are either chosen to be the default values of its component types
     * (i.e. zero for primitive numbers, null for references, etc) or, if the record defines a
     * constructor with no parameters, the values with which this constructor initializes the
     * components of the record.
     * </li>
     * <li>
     * Otherwise, if the file exists, a new configuration instance is created, initialized with the
     * values taken from the configuration file, and immediately saved to reflect potential changes
     * of the configuration type.
     * </li>
     * </ul>
     *
     * @param configurationFile the configuration file that is updated
     * @return a newly created configuration initialized with values taken from the configuration
     * file or a default configuration
     * @throws ConfigurationException if the configuration cannot be deserialized
     * @throws NullPointerException   if {@code configurationFile} is null
     * @throws RuntimeException       if loading or saving the configuration throws an exception
     */
    T update(Path configurationFile);
}
