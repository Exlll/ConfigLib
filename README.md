# ConfigLib
This library facilitates the creation, saving and loading of YAML configuration files. It does so
 by using Reflection on configuration classes and automatically saving and loading their fields,
 creating the configuration file and its parent directories if neccessary.

## Features
- automatic creation, saving and loading of YAML configurations
- automatic creation of parent directories
- option to add explanatory comments by adding annotations to the class and its fields
- option to exclude fields by making them final, static or transient

## How-to
##### Creating a configuration
To create a new configuration, create a class which extends `Configuration`. Fields which are
added to this class and which are not `final`, `static` or `transient` can automatically be saved
 to the corresponding configuration file.

##### Saving and loading a configuration
Instances of your configuration class have a `load`, `save` and `loadAndSave` method:
- `save` dumps all fields which are not `final`, `static` or `transient` to a configuration file.
  If the file exists, it is overriden; otherwise, it is created.
- `load` reads the configuration file and updates the instance's values.
- `loadAndSave` loads the configuration file and then calls `save` to update the file's values.
If the file doesn't exist, it is saved.

##### Adding and removing fields
In order to add or to remove fields, you just need to add them to or remove them from your
configuration class. The next time `save` or `loadAndSave` is called, changes are saved to the 
configuration file.

##### Comments
By using the `@Comment` annotation, you can add comments to your configuration file. The 
annotation can be applied to classes or fields. Each `String` is written into a new line.

#### Example class
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

    public DatabaseConfig(Path configPath) {
        super(configPath);
    }
    /* optional GETTER and SETTER methods */
}
```
#### Example usage
```java
public class Plugin {
    public static void main(String[] args) {
        Path path = Paths.get("/dir1/dir2/config.yml");
    
        DatabaseConfig config = new DatabaseConfig(path);
        try {
            config.loadAndSave();
        } catch (IOException e) {
            /* do something with exception */
        }
        
        int port = config.getPort();
    }
}
```
