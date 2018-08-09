package de.exlll.configlib;

import java.io.IOException;
import java.util.Map;

/**
 * Implementations of this class save and load {@code Map<String, Object>} maps that
 * represent converted configurations.
 *
 * @param <C> type of the configuration
 */
public interface ConfigurationSource<C extends Configuration<C>> {
    /**
     * Saves the given map.
     *
     * @param config the configuration that the {@code map} object represents
     * @param map    map that is saved
     * @throws IOException if an I/O error occurs when saving the {@code map}
     */
    void saveConfiguration(C config, Map<String, Object> map)
            throws IOException;

    /**
     * Loads the map representing the given {@code Configuration}.
     *
     * @param config the configuration instance that requested the load
     * @return map representing the given {@code Configuration}
     * @throws IOException if an I/O error occurs when loading the map
     */
    Map<String, Object> loadConfiguration(C config)
            throws IOException;
}
