package de.exlll.configlib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public abstract class Configuration {
    private final Path configPath;
    private final Comments comments;
    private final FieldMapper fieldMapper;

    public Configuration(Path configPath) {
        Objects.requireNonNull(configPath);
        this.configPath = configPath;
        FilteredFieldStreamSupplier ffss = new FilteredFieldStreamSupplier(
                getClass(), ConfigurationFieldFilter.INSTANCE);
        this.comments = Comments.from(ffss);
        this.fieldMapper = new FieldMapper(ffss);
    }

    public final void save() throws IOException {
        createParentDirectories();

        Map<String, Object> valuesByFieldNames = fieldMapper
                .mapFieldNamesToValues(this);
        String dump = YamlSerializer.serialize(valuesByFieldNames);
        ConfigurationWriter writer = new ConfigurationWriter(configPath, comments);
        writer.write(dump);
    }

    private void createParentDirectories() throws IOException {
        Path parentDirectory = configPath.getParent();
        Files.createDirectories(parentDirectory);
    }

    public final void load() throws IOException {
        // TODO save if not existent
        String dump = new ConfigurationReader(configPath).read();
        Map<String, Object> valuesByFieldNames = YamlSerializer.deserialize(dump);
        fieldMapper.mapValuesToFields(valuesByFieldNames, this);
    }
}
