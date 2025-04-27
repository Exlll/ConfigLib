package de.exlll.configlib;

import de.exlll.configlib.ConfigurationProperties.EnvVarResolutionConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static de.exlll.configlib.Validator.requireConfigurationType;
import static de.exlll.configlib.Validator.requireNonNull;

/**
 * A serializer for executing pre- and postprocessing operations.
 * This serializer is used for operations that should only be applied once,
 * to the root of a configuration. The actual serialization is delegated
 * to a {@code TypeSerializer}.
 *
 * @param <T> the type of the root configuration
 */
final class RootSerializer<T> implements Serializer<T, Map<?, ?>> {
    private final TypeSerializer<T, ?> serializer;
    private final ConfigurationProperties properties;
    private final Environment environment;

    RootSerializer(
            Class<T> type,
            ConfigurationProperties properties,
            Environment environment
    ) {
        requireConfigurationType(type);
        this.properties = requireNonNull(properties, "configuration properties");
        this.environment = requireNonNull(environment, "environment");
        this.serializer = TypeSerializer.newSerializerFor(type, properties);
    }

    @Override
    public Map<?, ?> serialize(T rootConfiguration) {
        return this.serializer.serialize(rootConfiguration);
    }

    @Override
    public T deserialize(Map<?, ?> serializedConfiguration) {
        final var envVarPreprocessor = new EnvironmentVariablePreprocessor(
                environment,
                properties.getEnvVarResolutionConfiguration()
        );
        envVarPreprocessor.preprocess(serializedConfiguration);
        return this.serializer.deserialize(serializedConfiguration);
    }

    T newDefaultInstance() {
        return this.serializer.newDefaultInstance();
    }

    private static final class EnvironmentVariablePreprocessor {
        private final Environment environment;
        private final EnvVarResolutionConfiguration envVarConfig;

        private EnvironmentVariablePreprocessor(
                Environment environment,
                EnvVarResolutionConfiguration envVarConfig
        ) {
            this.environment = environment;
            this.envVarConfig = envVarConfig;
        }

        public void preprocess(Map<?, ?> serializedConfig) {
            if (!envVarConfig.resolveEnvVars()) return;
            // The serialized configuration only contains elements of valid
            // target types. The recursive steps replace these elements only
            // with elements of the same type.
            @SuppressWarnings("unchecked")
            final var map = (Map<Object, Object>) serializedConfig;
            final var initialPrefix = envVarConfig.prefix();
            preprocessEnvVarMap(initialPrefix, map);
        }

        private void preprocessEnvVarMap(String prefix, Map<Object, Object> map) {
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                final Object key = entry.getKey();
                if (key == null) continue;
                final Object val = entry.getValue();
                String envVar = prefix.isEmpty()
                        ? key.toString()
                        : prefix + '_' + key;
                envVar = envVarConfig.caseSensitive()
                        ? envVar
                        : envVar.toUpperCase();
                final String envVarVal = environment.getValue(envVar);
                preprocessRecursively(val, envVar, envVarVal, entry::setValue);
            }
        }

        private void preprocessEnvVarList(String prefix, List<Object> list) {
            for (int i = 0; i < list.size(); i++) {
                final Object val = list.get(i);
                String envVar = prefix.isEmpty()
                        ? String.valueOf(i)
                        : prefix + '_' + i;
                envVar = envVarConfig.caseSensitive()
                        ? envVar
                        : envVar.toUpperCase();
                final String envVarVal = environment.getValue(envVar);
                final int index = i;
                preprocessRecursively(val, envVar, envVarVal, o -> list.set(index, o));
            }
        }

        private void preprocessRecursively(
                Object value,
                String envVar,
                String envVarVal,
                Consumer<Object> updater
        ) {
            if (value instanceof Map<?, ?> map) {
                requireEnvVarNull(envVar, envVarVal, "map");
                @SuppressWarnings("unchecked")
                Map<Object, Object> r = (Map<Object, Object>) map;
                preprocessEnvVarMap(envVar, r);
            } else if (value instanceof List<?> list) {
                requireEnvVarNull(envVar, envVarVal, "list");
                @SuppressWarnings("unchecked")
                List<Object> l = (List<Object>) list;
                preprocessEnvVarList(envVar, l);
            } else if (value instanceof Set<?>) {
                requireEnvVarNull(envVar, envVarVal, "set");
                // do nothing because we cannot target specific elements since
                // sets are unordered.
            } else if (envVarVal != null) {
                Object updatedValue = tryConvertEnvVarValueToTypeOfCurrentValue(
                        envVar, envVarVal, value
                );
                updater.accept(updatedValue);
            }
        }

        private static void requireEnvVarNull(
                String envVar,
                String envVarVal,
                String collectionType
        ) {
            if (envVarVal == null) return;
            final String msg =
                    ("Environment variables cannot be used to replace collections, but " +
                     "environment variable '%s' with value '%s' tries to replace a %s.")
                            .formatted(envVar, envVarVal, collectionType);
            throw new ConfigurationException(msg);
        }

        private static Object tryConvertEnvVarValueToTypeOfCurrentValue(
                String envVar,
                String envVarValue,
                Object currentValue
        ) {
            if (currentValue == null) {
                final String msg =
                        """
                        Value '%s' of environment variable '%s' cannot be converted \
                        because the value that is supposed to be replaced is null.\
                        """.formatted(envVarValue, envVar);
                throw new ConfigurationException(msg);
            }

            if (currentValue instanceof String)
                return envVarValue;

            try {
                if (currentValue instanceof Boolean) {
                    return parseBoolean(envVarValue);
                } else if (currentValue instanceof Long) {
                    return Long.valueOf(envVarValue);
                } else if (currentValue instanceof Double) {
                    return Double.valueOf(envVarValue);
                }
            } catch (RuntimeException e) {
                final String simpleName = currentValue.getClass().getSimpleName();
                final String msg =
                        """
                        Value '%s' of environment variable '%s' cannot be converted \
                        to type '%s'. The value the environment variable is supposed \
                        to replace is '%s'.\
                        """.formatted(envVarValue, envVar, simpleName, currentValue);
                throw new ConfigurationException(msg, e);
            }

            // should not happen because the configuration only contains
            // valid target types
            final String msg = "Unexpected value '%s' of type '%s'"
                    .formatted(currentValue, currentValue.getClass());
            throw new ConfigurationException(msg);
        }

        private static Boolean parseBoolean(String val) {
            if ("true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val)) {
                return Boolean.valueOf(val);
            }
            String msg = "The string '" + val + "' is not a valid boolean value. " +
                         "Only the values 'true' and 'false' (ignoring their case) " +
                         "are supported.";
            throw new RuntimeException(msg);
        }
    }
}
