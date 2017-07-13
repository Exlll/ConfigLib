## Creating a configuration

Let's say you want to create the following web chat configuration:
```yaml
# This is the default WebChat configuration
# Author: John Doe

ipAddress: 127.0.0.1
port: 12345
# Usernames mapped to users
users:
  User1:
    email: user1@example.com
    credentials:
      username: User1
      password: '12345'
  User2:
    email: user2@example.com
    credentials:
      username: User2
      password: '54321'
  User3:
    email: user3@example.com
    credentials:
      username: User3
      password: '51423'
channels:
- id: 1
  name: Channel1
  owner: User1
  members:
  - User2
  - User3
- id: 2
  name: Channel2
  owner: User2
  members:
  - User1

# Current version - DON'T TOUCH!
my_version: '1.2.3-alpha'
```
### 1. Create a configuration
Create a class which extends `de.exlll.configlib.Configuration`.
```java
import de.exlll.configlib.Configuration;

import java.nio.file.Path;

public final class WebchatConfig extends Configuration {
    public WebchatConfig(Path configPath) {
        super(configPath);
    }
}
```
### 2. Create custom classes
Create some classes to hold the necessary information and assign default values to their
fields. Be aware that custom classes must have a no-arguments constructor.
```java
import de.exlll.configlib.Configuration;

import java.nio.file.Path;
import java.util.List;

public final class WebchatConfig extends Configuration {
    public WebchatConfig(Path configPath) {
        super(configPath);
    }

    public static final class User {
        private String email = "";
        private Credentials credentials = new Credentials();
    }

    public static final class Credentials {
        private String username = "";
        private String password = "";
    }

    public static final class Channel {
        private int id;            // channel id
        private String name = "";  // channel name
        private String owner = ""; // username of the owner
        private List<String> members = new ArrayList<>(); // other usernames
    }
}
```
### 3. Add fields
You can add fields to a configuration class whose type is one of the following:
- a simple type, which are all primitive types (e.g. `boolean`, `int`), their wrapper types (e.g.
`Boolean`, `Integer`) and strings
- `List`s, `Set`s and `Map`s of simple types (e.g `List<Double>`) or other lists, sets and maps
(e.g. `List<List<Map<String, Integer>>>`)
- custom types which have a no-argument constructor,
- `ConfigList`s, `ConfigSet`s and `ConfigMap`s of custom types

If you want to use lists, sets or maps containing objects of custom types,
you have to use `ConfigList`, `ConfigSet` or `ConfigMap`, respectively. If you don't use these
special classes for storing custom objects, the stored objects won't be properly (de-)serialized.

If you don't want a field to be serialized, make it `final`, `static` or `transient`.

**NOTE:** all field values _must_ be non-null. If any value is `null`, serialization
will fail with a `NullPointerException`.
```java
import de.exlll.configlib.ConfigList;
import de.exlll.configlib.ConfigMap;
import de.exlll.configlib.Configuration;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class WebchatConfig extends Configuration {
    // private String s; // fails with a NullPointerException if not assigned a value
    private String ipAddress = "127.0.0.1";
    private int port = 12345;
    private Map<String, User> users = new ConfigMap<>(String.class, User.class);
    private List<Channel> channels = new ConfigList<>(Channel.class);

    public WebchatConfig(Path configPath) {
        super(configPath);
    }
    // ... remainder unchanged
}
```
### 4. Add comments
Comments can only be added to the configuration class or its fields.
Comments you add to other custom classes or their fields will be ignored.
```java
import de.exlll.configlib.Comment;
import de.exlll.configlib.ConfigList;
import de.exlll.configlib.ConfigMap;
import de.exlll.configlib.Configuration;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Comment({
        "This is the default WebChat configuration",
        "Author: John Doe"
})
public final class WebchatConfig extends Configuration {
    private String ipAddress = "127.0.0.1";
    private int port = 12345;
    @Comment("Usernames mapped to users")
    private Map<String, User> users = new ConfigMap<>(String.class, User.class);
    private List<Channel> channels = new ConfigList<>(Channel.class);

    public WebchatConfig(Path configPath) {
        super(configPath);
    }

   /* other classes and methods */
}
```
### 5. Add default values
Add some default values for lists, sets and maps.
```java
/* imports */
public final class WebchatConfig extends Configuration {
    /* fields */

    public WebchatConfig(Path configPath) {
        super(configPath);

        Channel channel1 = createNewChannel(1, "Channel1", "User1",
                Arrays.asList("User2", "User3"));
        Channel channel2 = createNewChannel(2, "Channel2", "User2",
                Arrays.asList("User1"));
        channels.add(channel1);
        channels.add(channel2);

        User user1 = createNewUser("user1@example.com", "User1", "12345");
        User user2 = createNewUser("user2@example.com", "User2", "54321");
        User user3 = createNewUser("user3@example.com", "User3", "51423");

        users.put(user1.credentials.username, user1);
        users.put(user2.credentials.username, user2);
        users.put(user3.credentials.username, user3);
    }

    private Channel createNewChannel(int id, String name, String owner,
                                     List<String> members) {
        Channel channel = new Channel();
        channel.id = id;
        channel.name = name;
        channel.owner = owner;
        channel.members = members;
        return channel;
    }

    private User createNewUser(String email, String username, String password) {
        User user = new User();
        user.email = email;
        user.credentials.username = username;
        user.credentials.password = password;
        return user;
    }
    
   /* other classes and methods */
}
```

### 6. Add the version
```java
import de.exlll.configlib.Configuration;
import de.exlll.configlib.Version;

import java.nio.file.Path;

@Version(
        version = "1.2.3-alpha",
        fieldName = "my_version",
        fieldComments = {
                "" /* empty line */,
                "Current version - DON'T TOUCH!"
        }
)
public final class WebchatConfig extends Configuration {
    // ... remainder unchanged
}
```

### 7. Create an instance of your configuration
Create a `java.nio.file.Path` object and pass it to the configuration constructor.
```java
/* imports */
public final class WebchatConfig extends Configuration {
    /*...*/
    public static void main(String[] args) {
        File configFolder = new File("folder");
        File configFile = new File(configFolder, "config.yml");
        Path path = configFile.toPath();
        /* of course, you can skip the above steps and directly
         * create a Path object using Paths.get(...) */
        WebchatConfig config = new WebchatConfig(path);
        try {
            config.loadAndSave();
            System.out.println(config.getIpAddress());
            System.out.println(config.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```