package de.exlll.configlib;

import org.yaml.snakeyaml.parser.ParserException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public abstract class Configuration {
    private final Path configPath;
    private final FieldMapper fieldMapper;
    private final ConfigurationWriter writer;

    /**
     * Creates a new {@code Configuration} instance.
     * <p>
     * You can use {@link File#toPath()} to obtain a {@link Path} object from a {@link File}.
     *
     * @param configPath location of the configuration file
     * @throws NullPointerException if {@code configPath} is null
     */
    public Configuration(Path configPath) {
        Objects.requireNonNull(configPath);

        FilteredFieldStreamSupplier ffss = new FilteredFieldStreamSupplier(
                getClass(), ConfigurationFieldFilter.INSTANCE);
        Comments comments = Comments.from(ffss);

        this.configPath = configPath;
        this.fieldMapper = new FieldMapper(ffss);
        this.writer = new ConfigurationWriter(configPath, comments);
    }

    /**
     * Loads the configuration file from the specified {@code Path} and sets {@code this} fields.
     *
     * @throws ClassCastException if parsed Object is not a {@code Map}
     * @throws IOException        if an I/O error occurs when loading the configuration file.
     * @throws ParserException    if invalid YAML
     */
    public final void load() throws IOException {
        String dump = new ConfigurationReader(configPath).read();
        Map<String, Object> valuesByFieldNames = YamlSerializer.deserialize(dump);
        fieldMapper.mapValuesToFields(valuesByFieldNames, this);
        postLoadHook();
    }

    /**
     * Saves this instance and its {@code @Comment} annotations to a configuration file at the specified {@code Path}.
     * <p>
     * Fields which are {@code final}, {@code static} or {@code transient} are not saved.
     * <p>
     * If the file exists, it is overridden; otherwise, it is created.
     *
     * @throws IOException     if an I/O error occurs when saving the configuration file.
     * @throws ParserException if invalid YAML
     */
    public final void save() throws IOException {
        createParentDirectories();

        Map<String, Object> valuesByFieldNames = fieldMapper.mapFieldNamesToValues(this);
        String dump = YamlSerializer.serialize(valuesByFieldNames);
        writer.write(dump);
    }

    /**
     * Loads and saves the configuration file.
     *
     * @throws ClassCastException if parsed Object is not a {@code Map}
     * @throws IOException        if an I/O error occurs when loading or saving the configuration file.
     * @throws ParserException    if invalid YAML
     * @see #load()
     * @see #save()
     */
    public final void loadAndSave() throws IOException {
        try {
            load();
            save();
        } catch (NoSuchFileException e) {
            save();
            postLoadHook();
        }
    }

    /**
     * Can be overridden to do something after all fields have been loaded.
     */
    protected void postLoadHook() {
    }

    private void createParentDirectories() throws IOException {
        Files.createDirectories(configPath.getParent());
    }
}
