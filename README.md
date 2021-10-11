# ConfigLib v2

**A Bukkit and BungeeCord library for storing and loading configurations**

This library facilitates creating, saving and loading configurations by reflectively converting configuration
instances to serializable `Map`s which can be transformed to different representations (e.g. YAML) before being
stored to files or other storage systems.

Currently this library only supports storing configurations as YAML. However, users may provide their own
storage systems.

For a step-by-step tutorial see: [Tutorial](https://github.com/Exlll/ConfigLib/wiki/Tutorial)

## Features
* automatic creation, saving, loading and updating of configurations
    * (_YAML_) automatic creation of files and directories
* support for all primitive types, their wrapper types and `String`s
* support for (nested) `List`s, `Set`s and `Map`s
* support for `Enum`s and POJOs
* option to add explanatory comments by annotating classes and their fields
* option to provide custom configuration sources
* option to exclude fields from being converted
* option to provide custom conversion mechanisms
* option to format field names before conversion
* option to execute action before/after loading/saving the configuration
* (_YAML_) option to change the style of the configuration file
* (_YAML_) option to prepend/append text (e.g. color codes)

## General information
#### Supported types
By default, the following types are converted automatically:
- simple types, i.e. primitive types, their wrapper types and `String`s
- `Enum`s
- any type that is annotated as a `ConfigurationElement`
- (nested) `List`s, `Set`s and `Map`s of all the above (e.g. `List<SomeType>`, `Map<String, List<SomeEnum>>`)
  - only simple types can be `Map` keys

For fields whose types are not any of the above, you have two other options:
* Add a custom `Converter` that converts the field's value to any of the above types and back from it.
* If the underlying storage system can handle the type, exclude the field from being converted.

#### Null values
This library does _not_ support `null` values. All non-primitive fields (e.g. `Integer`, `String`, `List`, `Enum`s)
must be assigned non-null default values.

#### Adding or removing configuration options
This library supports adding or removing configuration options by simply adding new fields to or removing old fields
from the configuration class. The next time the `save` or `loadAndSave` method is called, the changes will be saved.

#### Changing the type of configuration options
Changing the type of configuration options is **_not_** supported. **Don't do that.** This may lead to
`ClassCastException`s when loading or accessing the field. This is especially important for generic fields.
For example, you should never change a `List<String>` to a `List<Integer>`.

If you need the type of an option to change, add a new field with a different name and the desired type and then
remove the old one.

#### Subclassing configurations
Currently, subclassing configurations is not supported. If you have an instance of class `B` where `B` is a
subclass of `A` and `A` is a subclass of `YamlConfiguration` and you save or load that instance, then only the
fields of class `B` will be saved or loaded, respectively.

## How-to (_YAML_)

For a step-by-step tutorial see: [Tutorial](https://github.com/Exlll/ConfigLib/wiki/Tutorial)

#### Creating configurations
To create a YAML configuration, create a new class and extend `YamlConfiguration`. If you write a Bukkit plugin, 
you can alternatively extend `BukkitYamlConfiguration` which is a subclass of `YamlConfiguration` and can 
properly convert Bukkit classes like `Inventory` and `ItemStack` to YAML.

#### Instantiating configurations
* To instantiate a `YamlConfiguration`, you need to pass a `Path` and optionally a `YamlConfiguration.YamlProperties`
object to its constructor.
* To instantiate a `BukkitYamlConfiguration`, you need to pass a `Path` and optionally a
`BukkitYamlConfiguration.BukkitYamlProperties` object to its constructor.

If you don't pass a `(Bukkit-)YamlProperties` object, the `(Bukkit-)YamlProperties.DEFAULT` instance will be used.

#### Instantiating (Bukkit-)YamlProperties
To instantiate a new `(Bukkit-)YamlProperties` object, call `(Bukkit-)YamlProperties.builder()`,
configure the builder and then call its `build()` method.

Note: The `BukkitYamlProperties` is a subclass of `YamlProperties` but doesn't add any new methods to it. 
Its sole purpose is to provide more appropriate defaults to the underlying YAML parser.

#### Saving and loading configurations
Instances of your configuration class have a `load`, `save` and `loadAndSave` method:
- `load` first tries to load the configuration file and then updates the values of all fields of the configuration
instance with the values it read from the file. 
   * If the file contains an entry that doesn't have a corresponding field, the entry is ignored.
   * If the instance contains a field for which no entry was found, the default value you assigned to that field is kept.
- `save` first converts the configuration instance with its current values to YAML and then tries to dump that YAML
to a configuration file.
   * The configuration file is completely overwritten. This means any entries it contains are lost afterwards.
- `loadAndSave` is a convenience method that first calls `load` and then `save`.
   * If the file doesn't exist, the configuration instance keeps its default values. Otherwise, the values are
   updated with the values read from the file.
   * Subsequently the instance is saved so that the values of any newly added fields are also added
   to configuration file.

#### Adding and removing fields
Adding and removing fields is supported. However, changing the type of field is not.

For example, you can change the following `YamlConfiguration`
```java
class MyConfiguration extends YamlConfiguration {
    private String s = "1";
    private double d = 4.2;
    // ...
}
```
to this:
```java
class MyConfiguration extends YamlConfiguration {
    private String s = "2";
    private int i = 1;
    // ...
}
```
But you are not allowed to change the type of the variable `d` to `int` (or any other type).

#### Simple, enum and custom types
The following types are simple types (remember that `null` values are not allowed):
 
```java
class MyConfiguration extends YamlConfiguration {
    private boolean primBool;
    private Boolean refBool = false;
    private byte primByte;
    private Byte refByte = 0;
    private char primChar;
    private Character refChar = '\0';
    private short primShort;
    private Short refShort = 0;
    private int primInt;
    private Integer refInt = 0;
    private long primLong;
    private Long refLong = 0L;
    private float primFloat;
    private Float refFloat = 0F;
    private double primDouble;
    private Double refDouble = 0.0;
    private String string = "";
    // ...
}
```

Enums are supported:

```java
class MyConfiguration extends YamlConfiguration {
    private Material material = Material.AIR;
    //...
}
```

Custom classes are supported if they are annotated as `ConfigurationElement`s and if they have a no-args constructor.
Custom classes can have fields whose values are also instances of custom classes.

```java
@ConfigurationElement
class MyCustomClass1 {/* fields etc.*/}

@ConfigurationElement
class MyCustomClass2 {
    private MyCustomClass1 cls1 = new MyCustomClass1();
    // ...
}

class MyConfiguration extends YamlConfiguration {
    private MyCustomClass2 cls2 = new MyCustomClass2();
    // ...
}
```

#### `List`s, `Set`s, `Map`s
Lists, sets and maps of simple types can be used as is and don't need any special treatment.
```java
class MyConfiguration extends YamlConfiguration {
    private Set<Integer> ints = new HashSet<>();
    private List<String> strings = new ArrayList<>();
    private Map<Boolean, Double> doubleByBool = new HashMap<>();
    // ...
}
```

Note: Even though sets are supported, their YAML-representation is 'pretty ugly', so it's better to use lists instead.
If you need set behavior, you can internally use lists and convert them to sets using the `preSave/postLoad`-hooks.

Lists, sets and maps that contain other types (e.g. custom types or enums) must use the `@ElementType` annotation.
Only simple types can be used as map keys.

```java
@ConfigurationElement
class MyCustomClass {/* fields etc.*/}

class MyConfiguration extends YamlConfiguration {
    @ElementType(Material.class)
    private List<Material> materials = new ArrayList<>();

    @ElementType(MyCustomClass.class)
    private Set<MyCustomClass> customClasses = new HashSet<>();
    
    @ElementType(MyCustomClass.class)
    private Map<String, MyCustomClass> customClassesMap = new HashMap<>();
    // ...
}
```

Lists, sets and maps can be nested. If nested collections contain custom types, you must specify the
nesting level using the `@ElementType` annotation. Examples:

* `List<T>` requires a nesting level of 0, which is the default value, so you don't have to set it
* `List<List<T>>` requires a nesting level of 1
* `List<List<List<T>>>` requires a nesting level of 2
* `List<Map<String, T>>` requires a nesting level of 1
* `List<Map<String, Map<String, T>>>` requires a nesting level of 2

```java
@ConfigurationElement
class MyCustomClass {/* fields etc.*/}

class MyConfiguration extends YamlConfiguration {
    private List<List<Integer>> listsList = new ArrayList<>();
    private Set<Set<String>> setsSet = new HashSet<>();
    private Map<Integer, Map<String, Integer>> mapsMap = new HashMap<>();
    
    @ElementType(value = MyCustomClass.class, nestingLevel = 1)
    private List<List<MyCustomClass>> customClassListsList = new ArrayList<>();
    
    @ElementType(value = MyCustomClass.class, nestingLevel = 1)
    private Set<Set<MyCustomClass>> customClassSetsSet = new HashSet<>();
    
    @ElementType(value = MyCustomClass.class, nestingLevel = 1)
    private Map<Integer, Map<String, MyCustomClass>> customClassMapsMap = new HashMap<>();
    // ...
}
```
#### Adding comments
You can add comments to a configuration class or a its field by using the `@Comment` annotation.
Class comments are saved at the beginning of a configuration file.

```java
@Comment({"A", "", "B"})
class MyConfiguration extends YamlConfiguration {
    @Comment("the x")
    private int x;
    @Comment({"", "the y"})
    private int y;
    // ...
}
```
Empty strings are represented as newlines (i.e. lines that don't start with '# ').

#### Executing pre-save and post-load actions
To execute pre-save and post-load actions, override `preSave()` and `postLoad()`, respectively.
```java
class MyConfiguration extends YamlConfiguration {
   @Override
   protected void preSave(){ /* do something ... */}
   
   @Override
   protected void postLoad(){ /* do something ... */}
   // ...
}
```

#### Excluding fields from being converted
To exclude fields from being converted, annotate them with the `@NoConvert` annotation. This may be useful if the
configuration knows how to (de-)serialize instances of that type. For example, a `BukkitYamlConfiguration` knows how
to serialize `ItemStack` instances.

```java
class MyConfiguration extends BukkitYamlConfiguration {
   @NoConvert
   private ItemStack itemStack = new ItemStack(Material.STONE, 1);
   // ...
}
```

#### Changing configuration properties
To change the properties of a configuration, use the properties builder object. 

##### Formatting field names
To format field names before conversion, configure the properties builder to use a custom `FieldNameFormatter`.
You can either define your own `FieldNameFormatter` or use one from the `FieldNameFormatters` enum.

```java
YamlProperties properties = YamlProperties.builder()
                .setFormatter(FieldNameFormatters.LOWER_UNDERSCORE)
                // ...
                .build();
```

Alternatively, you can annotate your `Configuration` class with the `@Format` annotation. The `FieldNameFormatter`
returned by this annotation takes precedence over the `FieldNameFormatter` returned by a `Properties` object.

```java
@Format(FieldNameFormatters.UPPER_UNDERSCORE)
class MyConfiguration extends YamlConfiguration {
   // ...
}
```

Note: You should neither remove nor replace a formatter with one that has a different formatting style because this
could break existing configurations.

##### (_YAML_) Prepending/appending text
To prepend or append comments to a configuration file, use the `setPrependedComments` and `setAppendedComments` methods,
respectively.

```java
YamlProperties properties = YamlProperties.builder()
                .setPrependedComments(Arrays.asList("A", "B"))
                .setAppendedComments(Arrays.asList("C", "D"))
                // ...
                .build();
```

##### (_YAML_) Changing the style of the configuration file
To change the configuration style, use the `setConstructor`, `setRepresenter`, `setOptions` and `setResolver` methods.
These methods change the behavior of the underlying YAML-parser.
See [snakeyaml-Documentation](https://bitbucket.org/asomov/snakeyaml/wiki/Documentation).

```java
YamlProperties properties = YamlProperties.builder()
                .setConstructor(...)
                .setRepresenter(/* */)
                .setOptions(/* */)
                .setResolver(/* */)
                // ...
                .build();
```

Note: Changing the configuration style may break adding comments using the `@Comment` annotation.

##### Adding field filters
If your configuration has a lot of fields and you want to exclude some of these fields without
making them final, static or transient, you can configure your properties object to use additional
`FieldFilter`s. A `FieldFilter` filters the fields of a configuration class by a specified criterion.

For example, if you only want to include fields whose names don't start with _ignore_, you would add
the following filter:

```java
YamlProperties properties = YamlProperties.builder()
                .addFilter(field -> !field.getName().startsWith("ignore"))
                // ...
                .build();
```

Note: A filter is not evaluated for a field if the field has already been filtered or by some
other `FieldFilter`.

#### Adding custom converters
Any field can be converted using a custom converter. This can be useful if you don't like the default
conversion mechanism or if you have classes that cannot be annotated as `ConfigurationElement`s
(e.g. because they are not under your control).

To create a new converter, you have to implement the `Converter<F, T>` interface where `F` represents
the type of the field and `T` represents the type of the value to which the field value is converted.

```java
import java.awt.Point;

class PointStringConverter implements Converter<Point, String> {
    @Override
    public String convertTo(Point element, ConversionInfo info) {
        return element.x + ":" + element.y;
    }

    @Override
    public Point convertFrom(String element, ConversionInfo info) {
        String[] coordinates = element.split(":");
        int x = Integer.parseInt(coordinates[0]);
        int y = Integer.parseInt(coordinates[1]);
        return new Point(x, y);
    }
}
```

To use your custom converter, pass its class to the `@Convert` annotation.
```java
class MyConfiguration extends YamlConfiguration {
    @Convert(PointStringConverter.class)
    private Point point = new Point(2, 3);
    //...
}
```

Note: Only a single converter instance is created which is cached.

## Example

For a step-by-step tutorial of a more complex example see:
[Tutorial](https://github.com/Exlll/ConfigLib/wiki/Tutorial)

```java
import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration.BukkitYamlProperties;
import de.exlll.configlib.format.FieldNameFormatters;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@ConfigurationElement
class Credentials {
    private String username;
    private String password;

    // ConfigurationElements must have a no-args constructor
    Credentials() {
        this("", ""); // default values must be non-null
    }

    Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    String getUsername() { return username; }
}

@Comment("MAIN-DB CONFIG")
class DatabaseConfig extends BukkitYamlConfiguration {
    private String host = "localhost";
    @Comment("must be greater than 1024")
    private int port = 3306;
    private Credentials adminAccount = new Credentials("admin", "123");
    private List<String> blockedUsers = Arrays.asList("root", "john");

    /* You can use the other constructor instead which uses the
     * BukkitYamlProperties.DEFAULT instance. */
    DatabaseConfig(Path path, BukkitYamlProperties properties) {
        super(path, properties);
    }

    Credentials getAdminAccount() { return adminAccount; }
}

public final class DatabasePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        /* Creating a properties object is not necessary if the other
         * DatabaseConfig constructor is used. */
        BukkitYamlProperties props = BukkitYamlProperties.builder()
                .setPrependedComments(Arrays.asList("Author: Pete", "Version: 1.0"))
                .setFormatter(FieldNameFormatters.LOWER_UNDERSCORE)
                .build();

        Path configPath = new File(getDataFolder(), "config.yml").toPath();

        DatabaseConfig config = new DatabaseConfig(configPath, props);
        config.loadAndSave();

        System.out.println(config.getAdminAccount().getUsername());
    }
}
```
## Import
#### Maven
```xml
<repository>
    <id>de.exlll</id>
    <url>https://maven.pkg.github.com/Exlll/ConfigLib</url>
</repository>

<!-- for Bukkit plugins -->
<dependency>
    <groupId>de.exlll</groupId>
    <artifactId>configlib-bukkit</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- for Bungee plugins -->
<dependency>
    <groupId>de.exlll</groupId>
    <artifactId>configlib-bungee</artifactId>
    <version>2.2.0</version>
</dependency>
```
#### Gradle
```groovy
repositories { maven { url 'https://maven.pkg.github.com/Exlll/ConfigLib' } }

dependencies {
    // for Bukkit plugins
    implementation group: 'de.exlll', name: 'configlib-bukkit', version: '2.2.0'
    // for Bungee plugins
    implementation group: 'de.exlll', name: 'configlib-bungee', version: '2.2.0'
}
```

```kotlin
repositories { maven { url = uri("https://maven.pkg.github.com/Exlll/ConfigLib") } }

dependencies {
    // for Bukkit plugins
    implementation("de.exlll:configlib-bukkit:2.2.0")
    // for Bungee plugins
    implementation("de.exlll:configlib-bungee:2.2.0")
}
```