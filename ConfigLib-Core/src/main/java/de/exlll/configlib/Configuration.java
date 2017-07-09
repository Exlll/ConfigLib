package de.exlll.configlib;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;

public abstract class Configuration {
    private final Path configPath;
    private final YamlSerializer serializer;
    private final CommentAdder adder;

    /**
     * Creates a new {@code Configuration} instance.
     * <p>
     * You can use {@link java.io.File#toPath()} to obtain a {@link Path} object
     * from a {@link java.io.File}.
     *
     * @param configPath location of the configuration file
     * @throws NullPointerException if {@code configPath} is null
     */
    protected Configuration(Path configPath) {
        this.configPath = configPath;
        this.serializer = new YamlSerializer(
                createConstructor(), createRepresenter(),
                createDumperOptions(), createResolver()
        );
        this.adder = new CommentAdder(new Comments(getClass()));
    }

    /**
     * Loads the configuration file from the specified {@code Path} and updates this attribute values.
     *
     * @throws ClassCastException if parsed Object is not a {@code Map}
     * @throws IOException        if an I/O error occurs when loading the configuration file.
     * @throws ParserException    if invalid YAML
     */
    public final void load() throws IOException {
        String yaml = ConfigReader.read(configPath);
        Map<String, Object> deserializedMap = serializer.deserialize(yaml);
        FieldMapper.instanceFromMap(this, deserializedMap);
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

        Map<String, Object> map = FieldMapper.instanceToMap(this);
        String serializedMap = serializer.serialize(map);
        ConfigWriter.write(configPath, adder.addComments(serializedMap));
    }

    private void createParentDirectories() throws IOException {
        Files.createDirectories(configPath.getParent());
    }

    /**
     * Loads and saves the configuration file.
     * <p>
     * This method first calls {@link #load()} and then {@link #save()}.
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
        } catch (NoSuchFileException | FileNotFoundException e) {
            postLoadHook();
            save();
        }
    }

    /**
     * Protected method invoked after all fields have been loaded.
     * <p>
     * The default implementation of this method does nothing.
     * <p>
     * Subclasses may override this method in order to execute some action
     * after all fields have been loaded.
     */
    protected void postLoadHook() {
    }

    /**
     * Creates a {@code BaseConstructor} which is used to configure a {@link Yaml} object.
     * <p>
     * Override this method to change the way the {@code Yaml} object is created.
     * <p>
     * This method may not return null.
     *
     * @return new {@code BaseConstructor}
     * @see org.yaml.snakeyaml.constructor.BaseConstructor
     */
    protected BaseConstructor createConstructor() {
        return new Constructor();
    }

    /**
     * Creates a {@code Representer} which is used to configure a {@link Yaml} object.
     * <p>
     * Override this method to change the way the {@code Yaml} object is created.
     * <p>
     * This method may not return null.
     *
     * @return new {@code Representer}
     * @see org.yaml.snakeyaml.representer.Representer
     */
    protected Representer createRepresenter() {
        return new Representer();
    }

    /**
     * Creates a {@code DumperOptions} object which is used to configure a {@link Yaml} object.
     * <p>
     * Override this method to change the way the {@code Yaml} object is created.
     * <p>
     * This method may not return null.
     *
     * @return new {@code DumperOptions}
     * @see org.yaml.snakeyaml.DumperOptions
     */
    protected DumperOptions createDumperOptions() {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return options;
    }

    /**
     * Creates a {@code Resolver} which is used to configure a {@link Yaml} object.
     * <p>
     * Override this method to change the way the {@code Yaml} object is created.
     * <p>
     * This method may not return null.
     *
     * @return new {@code Resolver}
     * @see org.yaml.snakeyaml.resolver.Resolver
     */
    protected Resolver createResolver() {
        return new Resolver();
    }
}
