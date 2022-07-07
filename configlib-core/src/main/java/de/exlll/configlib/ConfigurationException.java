package de.exlll.configlib;

/**
 * Signals that an error occurred during the serialization or deserialization of a configuration.
 */
public final class ConfigurationException extends RuntimeException {
    ConfigurationException(String message) {
        super(message);
    }

    ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
