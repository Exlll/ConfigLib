package de.exlll.configlib;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;

public abstract class Configuration {
    private final Path configPath;
    private final CommentAdder adder;
    private YamlSerializer serializer;

    /**
     * Constructs a new {@code Configuration} instance.
     * <p>
     * You can use {@link java.io.File#toPath()} get a {@link Path} object
     * from a {@link java.io.File}.
     *
     * @param configPath location of the configuration file
     * @throws NullPointerException if {@code configPath} is null
     */
    protected Configuration(Path configPath) {
        this.configPath = configPath;
        this.adder = new CommentAdder(new Comments(getClass()));
    }

    private void initSerializer() {
        if (serializer == null) {
            this.serializer = new YamlSerializer(
                    createConstructor(), createRepresenter(),
                    createDumperOptions(), createResolver()
            );
        }
    }

    /**
     * Loads {@code this} configuration from a configuration file. The file is
     * located at the path pointed to by the {@code Path} object used to create
     * {@code this} instance.
     * <p>
     * The values of the fields of this instance are updated as follows:<br>
     * For each non-{@code final}, non-{@code static} and non-{@code transient}
     * field of {@code this} configuration instance: <br>
     * - If the field's value is null, throw a {@code NullPointerException} <br>
     * - If the file contains the field's name, update the field's value with
     * the value from the file. Otherwise, keep the default value. <br>
     * This algorithm is applied recursively for any non-default field.
     *
     * @throws ClassCastException   if parsed Object is not a {@code Map}
     * @throws IOException          if an I/O error occurs when loading the configuration file
     * @throws NullPointerException if a value of a field of this instance is null
     * @throws ParserException      if invalid YAML
     */
    public final void load() throws IOException {
        Map<String, Object> deserializedMap = readAndDeserialize();
        FieldMapper.instanceFromMap(this, deserializedMap);
        postLoadHook();
    }

    private Map<String, Object> readAndDeserialize() throws IOException {
        initSerializer();
        String yaml = ConfigReader.read(configPath);
        return serializer.deserialize(yaml);
    }

    /**
     * Saves the comments of {@code this} class as well as the names,
     * values and comments of its non-{@code final}, non-{@code static}
     * and non-{@code transient} fields to a configuration file. If this
     * class uses versioning, the current version is saved, too.
     * <p>
     * The file used to save this configuration is located at the path pointed
     * to by the {@code Path} object used to create {@code this} instance.
     * <p>
     * The default algorithm used to save {@code this} configuration to a file
     * is as follows:<br>
     * <ol>
     * <li>If the file doesn't exist, it is created.</li>
     * <li>For each non-{@code final}, non-{@code static} and non-{@code transient}
     * field of {@code this} configuration instance:
     * <ul>
     * <li>If the file doesn't contain the field's name, the field's name and
     * value are added. Otherwise, the value is simply updated.</li>
     * </ul>
     * </li>
     * <li>If the file contains field names that don't match any name of a field
     * of this class, the file's field names together with their values are
     * removed from the file.</li>
     * <li>(only with versioning) The current version is updated.</li>
     * </ol>
     * The default behavior can be overridden using <i>versioning</i>.
     *
     * @throws ConfigException     if a name clash between a field name and the version
     *                             field name occurs (can only happen if versioning is used)
     * @throws NoSuchFileException if the old version contains illegal file path characters
     * @throws IOException         if an I/O error occurs when saving the configuration file
     * @throws ParserException     if invalid YAML
     */
    public final void save() throws IOException {
        initSerializer();
        createParentDirectories();
        Map<String, Object> map = FieldMapper.instanceToMap(this);
        version(map);
        String serializedMap = serializer.serialize(map);
        ConfigWriter.write(configPath, adder.addComments(serializedMap));
    }

    private void version(Map<String, Object> map) throws IOException {
        final Version version = Reflect.getVersion(getClass());

        if (version == null) {
            return;
        }

        final String vfn = version.fieldName();
        if (map.containsKey(vfn)) {
            String msg = "Problem: Configuration '" + this + "' cannot be " +
                    "saved because one its fields has the same name as the " +
                    "version field: '" + vfn + "'.\nSolution: Rename the " +
                    "field or use a different version field name.";
            throw new ConfigException(msg);
        }
        map.put(vfn, version.version());
        version.updateStrategy().update(this, version);
    }

    private void createParentDirectories() throws IOException {
        Files.createDirectories(configPath.getParent());
    }

    /**
     * Loads and saves {@code this} configuration.
     * <p>
     * This method first calls {@link #load()} and then {@link #save()}.
     *
     * @throws ClassCastException   if parsed Object is not a {@code Map}
     * @throws IOException          if an I/O error occurs when loading or saving the configuration file
     * @throws NullPointerException if a value of a field of this instance is null
     * @throws ParserException      if invalid YAML
     * @see #load()
     * @see #save()
     */
    public final void loadAndSave() throws IOException {
        try {
            load();
            save();
        } catch (NoSuchFileException e) {
            postLoadHook();
            save();
        }
    }

    /**
     * Protected method invoked after all fields have successfully been loaded.
     * <p>
     * The default implementation of this method does nothing. Subclasses may
     * override this method in order to execute some action after all fields
     * have successfully been loaded.
     */
    protected void postLoadHook() {}

    /**
     * Returns a {@link BaseConstructor} which is used to configure a
     * {@link Yaml} object.
     * <p>
     * Override this method to change the way the {@code Yaml} object is created.
     * <p>
     * This method may not return null.
     *
     * @return a {@code BaseConstructor} object
     * @see org.yaml.snakeyaml.constructor.BaseConstructor
     * @see #createRepresenter()
     * @see #createDumperOptions()
     * @see #createResolver()
     */
    protected BaseConstructor createConstructor() {
        return new Constructor();
    }

    /**
     * Returns a {@link Representer} which is used to configure a {@link Yaml}
     * object.
     * <p>
     * Override this method to change the way the {@code Yaml} object is created.
     * <p>
     * This method may not return null.
     *
     * @return a {@code Representer} object
     * @see org.yaml.snakeyaml.representer.Representer
     * @see #createConstructor()
     * @see #createDumperOptions()
     * @see #createResolver()
     */
    protected Representer createRepresenter() {
        return new Representer();
    }

    /**
     * Returns a {@link DumperOptions} object which is used to configure a
     * {@link Yaml} object.
     * <p>
     * Override this method to change the way the {@code Yaml} object is created.
     * <p>
     * This method may not return null.
     *
     * @return a {@code DumperOptions} object
     * @see org.yaml.snakeyaml.DumperOptions
     * @see #createConstructor()
     * @see #createRepresenter()
     * @see #createResolver()
     */
    protected DumperOptions createDumperOptions() {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return options;
    }

    /**
     * Returns a {@link Resolver} which is used to configure a {@link Yaml} object.
     * <p>
     * Override this method to change the way the {@code Yaml} object is created.
     * <p>
     * This method may not return null.
     *
     * @return a {@code Resolver} object
     * @see org.yaml.snakeyaml.resolver.Resolver
     * @see #createConstructor()
     * @see #createRepresenter()
     * @see #createDumperOptions()
     */
    protected Resolver createResolver() {
        return new Resolver();
    }

    final String currentFileVersion() throws IOException {
        final Version version = Reflect.getVersion(getClass());
        return (version == null) ? null : readCurrentFileVersion(version);
    }

    private String readCurrentFileVersion(Version version) throws IOException {
        try {
            final Map<String, Object> map = readAndDeserialize();
            return (String) map.get(version.fieldName());
        } catch (NoSuchFileException ignored) {
            /* there is no file version if the file doesn't exist */
            return null;
        }
    }

    final Path getPath() {
        return configPath;
    }
}
