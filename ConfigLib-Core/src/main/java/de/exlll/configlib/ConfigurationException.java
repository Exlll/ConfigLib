package de.exlll.configlib;

/**
 * Signals that an error occurred during the (de-)serialization of a configuration.
 * <p>
 * The cause of this exception is most likely some misconfiguration.
 */
public final class ConfigurationException extends RuntimeException {
    ConfigurationException(String message) {
        super(message);
    }

    ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
