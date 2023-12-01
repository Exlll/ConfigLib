package de.exlll.configlib;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Instances of this class read and write configurations from input streams and to output streams,
 * respectively.
 * <p>
 * The details of how configurations are serialized and deserialized are defined by the
 * implementations of this interface.
 *
 * @param <T> the configuration type
 */
public interface IOStreamConfigurationStore<T> {
    /**
     * Writes a configuration instance to the given output stream.
     *
     * @param configuration the configuration
     * @param outputStream  the output stream the configuration is written to
     * @throws ConfigurationException if the configuration contains invalid values or
     *                                cannot be serialized
     * @throws NullPointerException   if any argument is null
     * @throws RuntimeException       if writing the configuration throws an exception
     */
    void write(T configuration, OutputStream outputStream);

    /**
     * Reads a configuration from the given input stream.
     *
     * @param inputStream the input stream the configuration is read from
     * @return a newly created configuration initialized with values read from {@code inputStream}
     * @throws ConfigurationException if the configuration cannot be deserialized
     * @throws NullPointerException   if {@code inputStream} is null
     * @throws RuntimeException       if reading the input stream throws an exception
     */
    T read(InputStream inputStream);
}
