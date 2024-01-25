package de.exlll.configlib;

import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.representer.StandardRepresenter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static de.exlll.configlib.Validator.requireNonNull;

/**
 * A configuration store for YAML configurations. This class provides two pairs of methods:
 * One pair for loading configurations from and saving them as YAML text files, and a second pair
 * for reading configurations from input streams and writing them to output streams.
 *
 * @param <T> the configuration type
 */
public final class YamlConfigurationStore<T> implements
        FileConfigurationStore<T>,
        IOStreamConfigurationStore<T> {

    private static final Dump YAML_DUMPER = newYamlDumper();
    private static final Load YAML_LOADER = newYamlLoader();
    private final YamlConfigurationProperties properties;
    private final TypeSerializer<T, ?> serializer;
    private final CommentNodeExtractor extractor;

    /**
     * Constructs a new store.
     *
     * @param configurationType the type of configuration
     * @param properties        the properties
     * @throws NullPointerException if any argument is null
     */
    public YamlConfigurationStore(Class<T> configurationType, YamlConfigurationProperties properties) {
        requireNonNull(configurationType, "configuration type");
        this.properties = requireNonNull(properties, "properties");
        this.serializer = TypeSerializer.newSerializerFor(configurationType, properties);
        this.extractor = new CommentNodeExtractor(properties);
    }


    @Override
    public void write(T configuration, OutputStream outputStream) {
        requireNonNull(configuration, "configuration");
        requireNonNull(outputStream, "output stream");
        var extractedCommentNodes = extractor.extractCommentNodes(configuration);
        var yamlFileWriter = new YamlWriter(outputStream, properties);
        var dumpedYaml = tryDump(configuration);
        yamlFileWriter.writeYaml(dumpedYaml, extractedCommentNodes);
    }

    @Override
    public void save(T configuration, Path configurationFile) {
        requireNonNull(configuration, "configuration");
        requireNonNull(configurationFile, "configuration file");
        tryCreateParentDirectories(configurationFile);
        var extractedCommentNodes = extractor.extractCommentNodes(configuration);
        var yamlFileWriter = new YamlWriter(configurationFile, properties);
        var dumpedYaml = tryDump(configuration);
        yamlFileWriter.writeYaml(dumpedYaml, extractedCommentNodes);
    }

    private void tryCreateParentDirectories(Path configurationFile) {
        Path parent = configurationFile.getParent();
        if (!Files.exists(parent) && properties.createParentDirectories()) {
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
            return YAML_DUMPER.dumpToString(serializedConfiguration);
        } catch (YamlEngineException e) {
            String msg = "The given configuration could not be converted into YAML. \n" +
                         "Do all custom serializers produce valid target types?";
            throw new ConfigurationException(msg, e);
        }
    }

    @Override
    public T read(InputStream inputStream) {
        requireNonNull(inputStream, "input stream");
        try {
            var yaml = YAML_LOADER.loadFromInputStream(inputStream);
            var conf = requireYamlMapForRead(yaml);
            return serializer.deserialize(conf);
        } catch (YamlEngineException e) {
            String msg = "The input stream does not contain valid YAML.";
            throw new ConfigurationException(msg, e);
        }
    }

    private Map<?, ?> requireYamlMapForRead(Object yaml) {
        if (yaml == null) {
            String msg = "The input stream is empty or only contains null.";
            throw new ConfigurationException(msg);
        }

        if (!(yaml instanceof Map<?, ?> map)) {
            String msg = "The contents of the input stream do not represent a configuration. " +
                         "A valid configuration contains a YAML map but instead a " +
                         "'" + yaml.getClass() + "' was found.";
            throw new ConfigurationException(msg);
        }

        return map;
    }

    @Override
    public T load(Path configurationFile) {
        requireNonNull(configurationFile, "configuration file");
        try (var reader = Files.newBufferedReader(configurationFile, properties.getCharset())) {
            var yaml = YAML_LOADER.loadFromReader(reader);
            var conf = requireYamlMapForLoad(yaml, configurationFile);
            return serializer.deserialize(conf);
        } catch (YamlEngineException e) {
            String msg = "The configuration file at %s does not contain valid YAML.";
            throw new ConfigurationException(msg.formatted(configurationFile), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<?, ?> requireYamlMapForLoad(Object yaml, Path configurationFile) {
        if (yaml == null) {
            String msg = "The configuration file at %s is empty or only contains null.";
            throw new ConfigurationException(msg.formatted(configurationFile));
        }

        if (!(yaml instanceof Map<?, ?> map)) {
            String msg = "The contents of the YAML file at %s do not represent a configuration. " +
                         "A valid configuration file contains a YAML map but instead a " +
                         "'" + yaml.getClass() + "' was found.";
            throw new ConfigurationException(msg.formatted(configurationFile));
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
        return defaultConfiguration;
    }

    static Dump newYamlDumper() {
        DumpSettings settings = DumpSettings.builder()
                .setDefaultFlowStyle(FlowStyle.BLOCK)
                .setIndent(2)
                .build();
        return new Dump(settings, new YamlConfigurationRepresenter(settings));
    }

    static Load newYamlLoader() {
        LoadSettings settings = LoadSettings.builder().build();
        return new Load(settings);
    }

    /**
     * A custom representer that prevents aliasing.
     */
    static final class YamlConfigurationRepresenter extends StandardRepresenter {
        public YamlConfigurationRepresenter(DumpSettings settings) {
            super(settings);
        }

        @Override
        protected Node representSequence(Tag tag, Iterable<?> sequence, FlowStyle flowStyle) {
            Node node = super.representSequence(tag, sequence, flowStyle);
            representedObjects.clear();
            return node;
        }

        @Override
        protected Node representMapping(Tag tag, Map<?, ?> mapping, FlowStyle flowStyle) {
            Node node = super.representMapping(tag, mapping, flowStyle);
            representedObjects.clear();
            return node;
        }
    }
}
