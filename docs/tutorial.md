This tutorial is intended to show most of the features of this library, so let's say that
we want to create the following configuration file for some kind of game:

```yaml
# Valid color codes: &4, &c, &e
win_message: '&4YOU  WON'
blocked_users:
- root
- john
team_members:
- - Pete
  - Mary
  - Alice
  - Leo
- - Eli
  - Eve
  - Paul
  - Patrick
moderator:
  credentials:
    username: alex
    password: '123'
  email: a@b.c
users_by_name:
  Patrick:
    credentials:
      username: Patrick
      password: '579'
    email: patrick@example.com
  Eli:
    credentials:
      username: Eli
      password: '246'
    email: eli@example.com
  Pete:
    credentials:
      username: Pete
      password: '123'
    email: pete@example.com
  Eve:
    credentials:
      username: Eve
      password: '357'
    email: eve@example.com
  Alice:
    credentials:
      username: Alice
      password: '789'
    email: alice@example.com
  Leo:
    credentials:
      username: Leo
      password: '135'
    email: leo@example.com
  Paul:
    credentials:
      username: Paul
      password: '468'
    email: paul@example.com
  Mary:
    credentials:
      username: Mary
      password: '456'
    email: mary@example.com
first_prize:
  ==: org.bukkit.inventory.ItemStack
  type: DIAMOND_AXE
  meta:
    ==: ItemMeta
    meta-type: UNSPECIFIC
    enchants:
      DIG_SPEED: 5
      DURABILITY: 3
      MENDING: 1
consolation_prizes:
- ==: org.bukkit.inventory.ItemStack
  type: STICK
  amount: 2
- ==: org.bukkit.inventory.ItemStack
  type: ROTTEN_FLESH
  amount: 3
- ==: org.bukkit.inventory.ItemStack
  type: CARROT
  amount: 4
prohibited_items:
- BEDROCK
- AIR
- LAVA

# Configure the arena:
arena_height: 40
arena_center: world;0;128;0

# Remember to play fair!
```

#### 1. Extend `BukkitYamlConfiguration`

The first thing we have to do is to extend `BukkitYamlConfiguration`. We use
`BukkitYamlConfiguration` instead of `YamlConfiguration` because it can properly
(de-)serialize Bukkit classes like `ItemStack`s.

```java
public final class GameConfig extends BukkitYamlConfiguration {}
```

#### 2. Create constructor

Next we have to create a constructor that matches super. If we don't pass a
`BukkitYamlProperties` object to the super call, the `BukkitYamlProperties.DEFAULT`
instance is used. But because we want to format the field names of our configuration
(so that we can write 'winMessage' in Java which becomes 'win_message' in YAML), we
have to pass a `BukkitYamlProperties` object that uses a different
`FieldNameFormatter` (see [11.](https://github.com/Exlll/ConfigLib/wiki/Tutorial#11-use-gameconfig)).

```java
public final class GameConfig extends BukkitYamlConfiguration {
    public GameConfig(Path path, BukkitYamlProperties properties) {
        super(path, properties);
    }

    // uses BukkitYamlProperties.DEFAULT instance
    // public GameConfig(Path path) {
    //     super(path);
    // }
}
```

#### 3. Add `winMessage`

Because `winMessage` is a string with a comment, we add a field named `winMessage`
of type `String` to our configuration class and annotate it with the `@Comment`
annotation.

```java
public final class GameConfig extends BukkitYamlConfiguration  {
    @Comment("Valid color codes: &4, &c, &e")
    private String winMessage = "&4YOU  WON";
    // ...
}
```

#### 4. Add `blockedUsers`

Because `blockedUsers` is a list of strings, we add a field named `blockedUsers`
of type `List<String>` to our configuration class.

```java
public final class GameConfig extends BukkitYamlConfiguration  {
    // ...
    private List<String> blockedUsers = Arrays.asList("root", "john");
    // ...
}
```

Remember that `null` values are not allowed. All non-primitive fields must be
assigned some non-`null` default value.

#### 5. Add `teamMembers`

The `teamMembers` field is of type `List<List<String>>`.

```java
public final class GameConfig extends BukkitYamlConfiguration  {
    // ...
    private List<List<String>> teamMembers = Arrays.asList(
            Arrays.asList("Pete", "Mary", "Alice", "Leo"),
            Arrays.asList("Eli", "Eve", "Paul", "Patrick")
    );
    // ...
}
```

#### 6. Add `moderator`

Because our `moderator` represents a user that has credentials and an email address,
we create a `User` and a `Credentials` class and annotate them as
`ConfigurationElement`s:

```java
@ConfigurationElement
final class Credentials {
    private String username;
    private String password;

    // ConfigurationElements must have a no-args constructor (can be private)
    private Credentials() { this("", ""); }

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    // getter etc.
 }

@ConfigurationElement
final class User {
    private Credentials credentials;
    private String email;

    private User() { this("", "", ""); }

    public User(String username, String password, String email) {
        this.credentials = new Credentials(username, password);
        this.email = email;
    }
    // getter etc.
}
```

Now we can use the `User` class for our `moderator` field:

```java
public final class GameConfig extends BukkitYamlConfiguration {
    // ...
    private User moderator = new User("alex", "123", "a@b.c");
    // ...
}
```

`ConfigurationElement`s must have a no-args constructor which is used to create
instances of a given element.

#### 7. Add `usersByName`

The `usersByName` field is a map that maps user names to `User` instances.
That means we have to use the `@ElementType` annotation.

```java
public final class GameConfig extends BukkitYamlConfiguration {
    // ...
    @ElementType(User.class)
    private Map<String, User> usersByName = initUsersByName();
    // ...

    private Map<String, User> initUsersByName() {
        Map<String, User> usersByName = new HashMap<>();
        usersByName.put("Pete", new User("Pete", "123", "pete@example.com"));
        usersByName.put("Mary", new User("Mary", "456", "mary@example.com"));
        // ...
        return usersByName;
    }
}
```

#### 8. Add `firstPrize` and `consolationPrizes`

The types of `firstPrize` and `consolationPrizes` are `ItemStack` and `List<ItemStack>`,
respectively. Because a `BukkitYamlConfiguration` knows how to serialize `ItemStack`
instances, we need to tell the library not to try to convert them. This can be done
by using the `@NoConvert` annotation.

```java
public final class GameConfig extends BukkitYamlConfiguration {
    // ...
    @NoConvert
    private ItemStack firstPrize = initFirstPrize();
    @NoConvert
    private List<ItemStack> consolationPrizes = Arrays.asList(
            new ItemStack(Material.STICK, 2),
            new ItemStack(Material.ROTTEN_FLESH, 3),
            new ItemStack(Material.CARROT, 4)
    );
    // ...

    private ItemStack initFirstPrize() {
        ItemStack stack = new ItemStack(Material.DIAMOND_AXE);
        stack.addEnchantment(Enchantment.DURABILITY, 3);
        stack.addEnchantment(Enchantment.DIG_SPEED, 5);
        stack.addEnchantment(Enchantment.MENDING, 1);
        return stack;
    }
    // ...
}
```

#### 9. Add `prohibitedItems`

The `prohibitedItems` field is a list of `Material`s. Since this library supports
converting enums, we just have to use the `@ElementType` annotation.

```java
public final class GameConfig extends BukkitYamlConfiguration {
    // ...
    @ElementType(Material.class)
    private List<Material> prohibitedItems = Arrays.asList(
            Material.BEDROCK, Material.AIR, Material.LAVA
    );
    // ...
}
```

#### 10. Add `arenaHeight` and `arenaCenter`

The `arenaHeight` can simply be represented by an `int` field. The `arenaCenter`
is of type `Location`. We could again use the `@NoConvert` annotation but this
would result in a different representation. Instead, we are going to implement our
own `Converter`.

First we have to create a class that implements `Converter<Location, String>`:

```java
final class LocationStringConverter implements Converter<Location, String> {}
```

Then we must implement the `convertTo` and `convertFrom` methods:

```java
final class LocationStringConverter implements Converter<Location, String> {

    @Override
    public String convertTo(Location location, ConversionInfo conversionInfo) {
        String worldName = location.getWorld().getName();
        int blockX = location.getBlockX();
        int blockY = location.getBlockY();
        int blockZ = location.getBlockZ();
        return worldName + ";" + blockX + ";" + blockY + ";" + blockZ;
    }

    @Override
    public Location convertFrom(String s, ConversionInfo conversionInfo) {
        String[] split = s.split(";");
        World world = Bukkit.getWorld(split[0]);
        int x = Integer.parseInt(split[1]);
        int y = Integer.parseInt(split[2]);
        int z = Integer.parseInt(split[3]);
        return new Location(world, x, y, z);
    }
}
```

Finally we have to tell our configuration to use this converter for the
`arenaCenter` field. This is done using the `@Convert` annotation:

```java
public final class GameConfig extends BukkitYamlConfiguration {
    // ...
    @Comment({"", "Configure the arena:"})
    private int arenaHeight = 40;
    @Convert(LocationStringConverter.class)
    private Location arenaCenter = new Location(
            Bukkit.getWorld("world"), 0, 128, 0
    );
}
```

#### 11. Use `GameConfig`

Before we can use our new configuration, we have to instantiate it by passing
a `Path` and a `BukkitYamlProperties` object to its constructor. In this case
the `BukkitYamlProperties` is used to change the formatting of field names,
to append text to the configuration file and to add an additional field filter.

```java
public final class GamePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Path configPath = new File(getDataFolder(), "config.yml").toPath();

        BukkitYamlProperties properties = BukkitYamlProperties.builder()
                .addFilter(field -> !field.getName().startsWith("ignore"))
                .setFormatter(FieldNameFormatters.LOWER_UNDERSCORE)
                .setAppendedComments(Arrays.asList(
                        "", "Remember to play fair!"
                ))
                .build();
        GameConfig config = new GameConfig(configPath, properties);
        config.loadAndSave();
    }
}
```

### Full example

```java
import de.exlll.configlib.Converter;
import de.exlll.configlib.annotation.*;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration.BukkitYamlProperties;
import de.exlll.configlib.format.FieldNameFormatters;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GamePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Path configPath = new File(getDataFolder(), "config.yml").toPath();

        BukkitYamlProperties properties = BukkitYamlProperties.builder()
                .addFilter(field -> !field.getName().startsWith("ignore"))
                .setFormatter(FieldNameFormatters.LOWER_UNDERSCORE)
                .setAppendedComments(Arrays.asList(
                        "", "Remember to play fair!"
                ))
                .build();
        GameConfig config = new GameConfig(configPath, properties);
        config.loadAndSave();
    }
}

final class GameConfig extends BukkitYamlConfiguration {
    @Comment("Valid color codes: &4, &c, &e")
    private String winMessage = "&4YOU  WON";
    private User moderator = new User("alex", "123", "a@b.c");
    private List<String> blockedUsers = Arrays.asList("root", "john");
    private List<List<String>> teamMembers = Arrays.asList(
            Arrays.asList("Pete", "Mary", "Alice", "Leo"),
            Arrays.asList("Eli", "Eve", "Paul", "Patrick")
    );
    @ElementType(User.class)
    private Map<String, User> usersByName = initUsersByName();
    @NoConvert
    private ItemStack firstPrize = initFirstPrize();
    @NoConvert
    private List<ItemStack> consolationPrizes = Arrays.asList(
            new ItemStack(Material.STICK, 2),
            new ItemStack(Material.ROTTEN_FLESH, 3),
            new ItemStack(Material.CARROT, 4)
    );
    @ElementType(Material.class)
    private List<Material> prohibitedItems = Arrays.asList(
            Material.BEDROCK, Material.AIR, Material.LAVA
    );
    @Comment({"", "Configure the arena:"})
    private int arenaHeight = 40;
    @Convert(LocationStringConverter.class)
    private Location arenaCenter = new Location(
            Bukkit.getWorld("world"), 0, 128, 0
    );
    private String ignoreMe = "1";
    private String ignoreMeToo = "2";

    public GameConfig(Path path, BukkitYamlProperties properties) {
        super(path, properties);
    }

    private Map<String, User> initUsersByName() {
        Map<String, User> usersByName = new HashMap<>();
        usersByName.put("Pete", new User("Pete", "123", "pete@example.com"));
        usersByName.put("Mary", new User("Mary", "456", "mary@example.com"));
        usersByName.put("Alice", new User("Alice", "789", "alice@example.com"));
        usersByName.put("Leo", new User("Leo", "135", "leo@example.com"));
        usersByName.put("Eli", new User("Eli", "246", "eli@example.com"));
        usersByName.put("Eve", new User("Eve", "357", "eve@example.com"));
        usersByName.put("Paul", new User("Paul", "468", "paul@example.com"));
        usersByName.put("Patrick", new User("Patrick", "579", "patrick@example.com"));
        return usersByName;
    }

    private ItemStack initFirstPrize() {
        ItemStack stack = new ItemStack(Material.DIAMOND_AXE);
        stack.addEnchantment(Enchantment.DURABILITY, 3);
        stack.addEnchantment(Enchantment.DIG_SPEED, 5);
        stack.addEnchantment(Enchantment.MENDING, 1);
        return stack;
    }

    private static final class LocationStringConverter
            implements Converter<Location, String> {

        @Override
        public String convertTo(Location location, ConversionInfo conversionInfo) {
            String worldName = location.getWorld().getName();
            int blockX = location.getBlockX();
            int blockY = location.getBlockY();
            int blockZ = location.getBlockZ();
            return worldName + ";" + blockX + ";" + blockY + ";" + blockZ;
        }

        @Override
        public Location convertFrom(String s, ConversionInfo conversionInfo) {
            String[] split = s.split(";");
            World world = Bukkit.getWorld(split[0]);
            int x = Integer.parseInt(split[1]);
            int y = Integer.parseInt(split[2]);
            int z = Integer.parseInt(split[3]);
            return new Location(world, x, y, z);
        }
    }
}

@ConfigurationElement
final class User {
    private Credentials credentials;
    private String email;

    private User() { this("", "", ""); }

    public User(String username, String password, String email) {
        this.credentials = new Credentials(username, password);
        this.email = email;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public String getEmail() {
        return email;
    }
}

@ConfigurationElement
final class Credentials {
    private String username;
    private String password;

    // ConfigurationElements must have a no-args constructor (can be private)
    private Credentials() { this("", ""); }

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
```