package de.exlll.configlib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.exlll.configlib.Validator.requireNonNull;

/**
 * A configuration store for JSON configurations using Jackson.
 * <p>
 * This store supports reading and writing JSON files. Unlike standard JSON parsers,
 * this store attempts to preserve or inject comments when saving configurations
 * by using {@link JsonWriter}.
 *
 * @param <T> the configuration type
 */
public final class JsonConfigurationStore<T> implements
        FileConfigurationStore<T>,
        IOStreamConfigurationStore<T> {

    private static final ObjectMapper OBJECT_MAPPER = newObjectMapper();
    private final JsonConfigurationProperties properties;
    private final RootSerializer<T> serializer;

    /**
     * Constructs a new store.
     *
     * @param configurationType the type of configuration
     * @param properties        the properties
     * @throws NullPointerException if any argument is null
     */
    public JsonConfigurationStore(
            Class<T> configurationType,
            JsonConfigurationProperties properties
    ) {
        this(configurationType, properties, new Environment.SystemEnvironment());
    }

    JsonConfigurationStore(
            Class<T> configurationType,
            JsonConfigurationProperties properties,
            Environment environment
    ) {
        requireNonNull(configurationType, "configuration type");
        this.properties = requireNonNull(properties, "properties");
        this.serializer = new RootSerializer<>(
                configurationType,
                properties,
                environment
        );
    }

    @Override
    public void write(T configuration, OutputStream outputStream) {
        requireNonNull(configuration, "configuration");
        requireNonNull(outputStream, "output stream");


        var json = tryDump(configuration);
        var jsonWriter = new JsonWriter(outputStream, properties);
        jsonWriter.writeJson(json);
    }

    @Override
    public void save(T configuration, Path configurationFile) {
        requireNonNull(configuration, "configuration");
        requireNonNull(configurationFile, "configuration file");

        tryCreateParentDirectories(configurationFile);

        var json = tryDump(configuration);
        var jsonWriter = new JsonWriter(configurationFile, properties);
        jsonWriter.writeJson(json);
    }

    void tryCreateParentDirectories(Path configurationFile) {
        Path parent = configurationFile.getParent();
        if (properties.createParentDirectories() && parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String tryDump(T configuration) {
        final Map<?, ?> serializedConfiguration = serializer.serialize(configuration);
        try {
            return OBJECT_MAPPER.writeValueAsString(serializedConfiguration);
        } catch (JsonProcessingException e) {
            String msg = "The given configuration could not be converted into JSON. \n" +
                    "Do all custom serializers produce valid target types?";
            throw new ConfigurationException(msg, e);
        }
    }

    @Override
    public T read(InputStream inputStream) {
        requireNonNull(inputStream, "input stream");
        try {
            var json = OBJECT_MAPPER.readValue(inputStream, Map.class);
            var conf = requireJsonMap(json);
            return serializer.deserialize(conf);
        } catch (IOException e) {
            String msg = "The input stream does not contain valid JSON.";
            throw new ConfigurationException(msg, e);
        }
    }

    @Override
    public T load(Path configurationFile) {
        requireNonNull(configurationFile, "configuration file");
        try (var reader = Files.newBufferedReader(configurationFile, properties.getCharset())) {
            var json = OBJECT_MAPPER.readValue(reader, Map.class);
            var conf = requireJsonMap(json);
            var processedConf = postProcessJsonMap(conf);
            return serializer.deserialize(processedConf);
        } catch (IOException e) {
            String msg = "The configuration file at %s could not be loaded.";
            throw new ConfigurationException(msg.formatted(configurationFile), e);
        }
    }

    /**
     * Recursively walks the JSON map and converts String keys to Longs if they are valid numbers.
     */
    private Map<?, ?> postProcessJsonMap(Map<?, ?> map) {
        Map<Object, Object> result = new LinkedHashMap<>(map.size());
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (key instanceof String keyStr) {
                key = attemptConvertToLong(keyStr);
            }

            if (value instanceof Map<?, ?> valMap) {
                value = postProcessJsonMap(valMap);
            } else if (value instanceof List<?> valList) {
                value = postProcessJsonList(valList);
            }

            result.put(key, value);
        }
        return result;
    }

    private Object attemptConvertToLong(String key) {
        try {
            return Long.parseLong(key);
        } catch (NumberFormatException e) {
            return key;
        }
    }

    private List<?> postProcessJsonList(List<?> list) {
        List<Object> result = new ArrayList<>(list.size());
        for (Object element : list) {
            if (element instanceof Map<?, ?> map) {
                result.add(postProcessJsonMap(map));
            } else if (element instanceof List<?> subList) {
                result.add(postProcessJsonList(subList));
            } else {
                result.add(element);
            }
        }
        return result;
    }

    private Map<?, ?> requireJsonMap(Object json) {
        if (json == null) {
            String msg = "The JSON content is empty or null.";
            throw new ConfigurationException(msg);
        }
        if (!(json instanceof Map<?, ?> map)) {
            String msg = "The JSON content does not represent a configuration. " +
                    "A valid configuration must be a JSON object (Map) but found '" +
                    json.getClass().getSimpleName() + "'.";
            throw new ConfigurationException(msg);
        }
        return map;
    }

    @Override
    public T update(Path configurationFile) {
        requireNonNull(configurationFile, "configuration file");
        if (Files.exists(configurationFile)) {
            T configuration = load(configurationFile);
            save(configuration, configurationFile);
            return configuration;
        }
        T defaultConfiguration = serializer.newDefaultInstance();
        save(defaultConfiguration, configurationFile);
        return load(configurationFile);
    }

    static ObjectMapper newObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        mapper.configure(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature(), true);
        // We use Long for ints to ensure consistency with how ConfigLib generally
        // handles numeric types (similar to the YamlConfigurationConstructor logic).
        mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        return mapper;
    }
}