# ConfigLib
This library facilitates creating, saving and loading YAML configuration files. It does so
by using Reflection on configuration classes and automatically saving and loading their
attribute values, creating the configuration file and its parent directories if necessary.

## Features
- automatic creation, saving and loading of YAML configurations
- automatic creation of parent directories
- option to add explanatory comments by adding annotations to the class and its fields
- option to exclude fields by making them final, static or transient
- option to change the style of the configuration file

## General information
#### What can be serialized?
If your configuration class uses the following types as attributes, it will be properly saved.
- `String`s
- primitive types (e.g. `int`, `char`) and their corresponding wrapper types
(e.g. `Integer`, `Character`)
- `Set`s, `List`s, and `Map`s containing the above (e.g. `Set<Integer>`, `Map<String, Double>`)
- any other class that consists of the above
#### Default and null values
All reference type fields of a configuration class must be assigned non-null default values.
If a value is `null` while saving takes place, a `NullPointerException` will be thrown.
#### Serialization of custom classes
You can add fields to your configuration class whose type is some custom class.
`@Comment`s added to the custom class or its fields are ignored and won't be
displayed in the configuration file.

## How-to
#### Creating a configuration
To create a new configuration, create a class which extends `Configuration`. Fields which are
added to this class and which are not `final`, `static` or `transient` can automatically be saved
 to the corresponding configuration file.
#### Saving and loading a configuration
Instances of your configuration class have a `load`, `save` and `loadAndSave` method:
- `load` updates all fields of an instance with the values read from the configuration file.
- `save` dumps all field names and values to a configuration file. If the file exists, it is
overridden; otherwise, it is created.
- `loadAndSave` first calls `load` and then `save`, which is useful when you have added or
removed fields from the class or you simply don't know if the configuration file exists.
#### Adding and removing fields
In order to add or to remove fields, you just need to add them to or remove them from your
configuration class. The changes are saved to the configuration file the next time `save` or 
`loadAndSave` is called.
#### Post load action
You can override `postLoadHook` to execute some action after the configuration has successfully
been loaded.
#### Comments
By using the `@Comment` annotation, you can add comments to your configuration file. The
annotation can be applied to classes or fields. Each `String` of the passed array is
written into a new line.
#### Custom configuration style
You can change the style of the configuration file by overriding the protected `create...` methods
of your configuration class. Overriding these methods effectively changes the behavior of the
underlying `Yaml` parser. Note that if one these methods returns `null`, a `NullPointerException`
will be thrown.

For more information, consult the official
[documentation](https://bitbucket.org/asomov/snakeyaml/wiki/Documentation).
## Examples
#### Example of a custom class
```java
public class Credentials {
    private String username = "minecraft";
    private String password = "secret";
}
```
#### Example database configuration
```java
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
/* other imports */

@Comment({
        "This is a multiline comment.",
        "It describes what the configuration is about."
})
public final class DatabaseConfig extends Configuration {
    /* ignored fields */
    private final String ignored1 = "";     // ignored because final
    private static String ignored2 = "";    // ignored because static
    private transient String ignored3 = ""; // ignored because transient
    /* included fields */
    private String host = "localhost";
    private int port = 3306;
    @Comment("This is a single-line comment.")
    private List<String> strings = Arrays.asList("root", "local");
    @Comment({
            "This is a multiline comment.",
            "It describes what this field does."
    })
    private Map<String, List<String>> listByStrings = new HashMap<>();
    private Credentials credentials = new Credentials();

    public DatabaseConfig(Path configPath) {
        super(configPath);
    }
    /* other methods */
}
```
#### Example Bukkit plugin
```java
public class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Path configPath = new File(getDataFolder(), "config.yml").toPath();

        DatabaseConfig config = new DatabaseConfig(configPath);
        try {
            config.loadAndSave();
        } catch (IOException e) {
            /* do something with exception */
        }

        int port = config.getPort();
    }
}
```

## Import
#### Maven
```xml
<repository>
    <id>de.exlll</id>
    <url>https://repo.exlll.de/artifactory/releases/</url>
</repository>

<!-- for Bukkit plugins -->
<dependency>
    <groupId>de.exlll</groupId>
    <artifactId>configlib-bukkit</artifactId>
    <version>1.2.0</version>
</dependency>

<!-- for Bungee plugins -->
<dependency>
    <groupId>de.exlll</groupId>
    <artifactId>configlib-bungee</artifactId>
    <version>1.2.0</version>
</dependency>
```
#### Gradle
```groovy
repositories {
    maven {
        url 'https://repo.exlll.de/artifactory/releases/'
    }
}
dependencies {
    // for Bukkit plugins
    compile group: 'de.exlll', name: 'configlib-bukkit', version: '1.2.0'

    // for Bungee plugins
    compile group: 'de.exlll', name: 'configlib-bungee', version: '1.2.0'
}
```
Additionally, you either have to import the Bukkit or BungeeCord API
or disable transitive lookups. This project uses both of these APIs, so if you
need an example of how to import them with Gradle, take a look at the `build.gradle`.

If, for some reason, you have SSL errors that you're unable to resolve, you can
use `http://exlll.de:8081/artifactory/releases/` as the repository instead.
