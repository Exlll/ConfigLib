This tutorial is intended to show how to add a custom configuration source. So, let's say you
want to implement your own `Configuration` type, an `InMemoryConfiguration`, for example.

#### 1. Extend `Configuration`

The first thing you have to do is to let your `InMemoryConfiguration` class
extend `Configuration<InMemoryConfiguration>`.

```java
import de.exlll.configlib.Configuration;
import de.exlll.configlib.ConfigurationSource;

public class InMemoryConfiguration extends Configuration<InMemoryConfiguration> {}
```

#### 2. Create a `ConfigurationSource`

The second step is to create a class that implements
`ConfigurationSource<InMemoryConfiguration>`.

```java
final class InMemoryConfigurationSource
    implements ConfigurationSource<InMemoryConfiguration> {}
```

#### 3. Implement `save-/loadConfiguration`

Next implement its methods.

```java
final class InMemoryConfigurationSource
        implements ConfigurationSource<InMemoryConfiguration> {
    private Map<String, Object> configAsMap = new HashMap<>();

    /* The 'config' parameter is the Configuration instance that
     * requested the save/load */
    @Override
    public void saveConfiguration(
            InMemoryConfiguration config, Map<String, Object> map
    ) {
        this.configAsMap = map;
    }

    @Override
    public Map<String, Object> loadConfiguration(
            InMemoryConfiguration config
    ) {
        return configAsMap;
    }
}
```
#### 4. Extend `Properties` and `Builder`

Within your `InMemoryConfiguration` class, create an `InMemoryProperties` class
that extends `Configuration.Properties`. Within the `InMemoryProperties` class,
create a `Builder` class that extends `Properties.Builder`.

```java
public class InMemoryConfiguration extends Configuration<InMemoryConfiguration> {

    public static final class InMemoryProperties extends Properties {
        protected InMemoryProperties(Builder<?> builder) {
            super(builder);
        }

        public static Builder<?> builder() {
            return new Builder() {
                @Override
                protected Builder getThis() {
                    return this;
                }
            };
        }

        public static abstract class Builder<B extends Builder<B>>
                extends Properties.Builder<B> {
            public InMemoryProperties build() {
                return new InMemoryProperties(this);
            }
        }
    }
}
```

#### 5. Add properties

Add properties to your `InMemoryProperties` class that can configure your
`InMemoryConfiguration` in some meaningful way. For example:

```java
public static final class InMemoryProperties extends Properties {
    private final int minMemory;
    private final int maxMemory;

    protected InMemoryProperties(Builder<?> builder) {
        super(builder);
        this.minMemory = builder.minMemory;
        this.maxMemory = builder.maxMemory;
    }

    public int getMinMemory() { return minMemory; }

    public int getMaxMemory() { return maxMemory; }

    public static Builder<?> builder() {
        return new Builder() {
            @Override protected Builder getThis() { return this; }
        };
    }

    public static abstract class Builder<B extends Builder<B>>
            extends Properties.Builder<B> {
        private int minMemory = 0;
        private int maxMemory = 1024;

        public B setMinMemory(int minMemory) {
            this.minMemory = minMemory;
            return getThis();
        }

        public B setMaxMemory(int maxMemory) {
            this.maxMemory = maxMemory;
            return getThis();
        }

        public InMemoryProperties build() {
            return new InMemoryProperties(this);
        }
    }
}
```

#### 6. Implement `getSource/getThis`

The last step is to implement the `getSource` and `getThis` methods and override the
constructor of your `InMemoryConfiguration`.

```java
public class InMemoryConfiguration extends Configuration<InMemoryConfiguration> {
    private final InMemoryConfigurationSource source =
            new InMemoryConfigurationSource();

    protected InMemoryConfiguration(InMemoryProperties properties) {
        super(properties);
    }

    @Override
    protected ConfigurationSource<InMemoryConfiguration> getSource() {
        return source;
    }

    @Override
    protected InMemoryConfiguration getThis() {
        return this;
    }
}
```

#### 7. Use your new `InMemoryConfiguration`

```java
final class InMemoryDatabaseConfig extends InMemoryConfiguration {
    private final String host = "localhost";
    // ...

    public InMemoryDatabaseConfig(InMemoryProperties properties) {
        super(properties);
    }
    // ...

    public static void main(String[] args) {
        InMemoryProperties properties = InMemoryProperties.builder()
                .setMinMemory(123)
                .setMaxMemory(456)
                .build();

        InMemoryDatabaseConfig config = new InMemoryDatabaseConfig(properties);
        config.save();
    }
}
```