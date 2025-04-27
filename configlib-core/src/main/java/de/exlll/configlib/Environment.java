package de.exlll.configlib;

import java.util.Map;

interface Environment {
    /**
     * Gets the value of the specified environment variable.
     *
     * @param environmentVariable the variable
     * @return the value of the environment variable
     * @throws NullPointerException if {@code environmentVariable} is null
     */
    String getValue(String environmentVariable);

    /**
     * Returns an unmodifiable map of all environment variables
     * and their values.
     *
     * @return map of all environment variables and their values
     */
    Map<String, String> getEnvironment();

    final class SystemEnvironment implements Environment {
        public String getValue(String environmentVariable) {
            return System.getenv(environmentVariable);
        }

        public Map<String, String> getEnvironment() {
            return System.getenv();
        }
    }
}