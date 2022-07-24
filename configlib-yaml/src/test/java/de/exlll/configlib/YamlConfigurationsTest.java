package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlConfigurationsTest {
    private static final FieldFilter includeI = field -> field.getName().equals("i");
    private final FileSystem fs = Jimfs.newFileSystem();
    private final Path yamlFile = fs.getPath("/tmp/config.yml");

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(yamlFile.getParent());
    }

    @AfterEach
    void tearDown() throws IOException {
        fs.close();
    }

    @Configuration
    private static final class Config {
        int i = 10;
        int j = 11;

        public Config() {}

        public Config(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    @Test
    void saveYamlConfiguration1() {
        Config configuration = new Config();

        YamlConfigurations.save(yamlFile, Config.class, configuration);
        assertEquals("i: 10\nj: 11", TestUtils.readFile(yamlFile));

        configuration.i = 20;
        YamlConfigurations.save(yamlFile, Config.class, configuration);
        assertEquals("i: 20\nj: 11", TestUtils.readFile(yamlFile));
    }

    @Test
    void saveYamlConfiguration2() {
        Config configuration = new Config();

        YamlConfigurations.save(
                yamlFile, Config.class, configuration,
                builder -> builder.setFieldFilter(includeI)
        );
        assertEquals("i: 10", TestUtils.readFile(yamlFile));
    }

    @Test
    void saveYamlConfiguration3() {
        Config configuration = new Config();

        YamlConfigurations.save(
                yamlFile, Config.class, configuration,
                YamlConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );
        assertEquals("i: 10", TestUtils.readFile(yamlFile));
    }

    @Test
    void loadYamlConfiguration1() {
        writeString("i: 20\nk: 30");
        Config config = YamlConfigurations.load(yamlFile, Config.class);
        assertConfigEquals(config, 20, 11);

        writeString("i: 20\nj: 30");
        config = YamlConfigurations.load(yamlFile, Config.class);
        assertConfigEquals(config, 20, 30);
    }

    @Test
    void loadYamlConfiguration2() {
        writeString("i: 20\nj: 30");
        Config config = YamlConfigurations.load(
                yamlFile, Config.class,
                builder -> builder.setFieldFilter(includeI)
        );
        assertConfigEquals(config, 20, 11);
    }

    @Test
    void loadYamlConfiguration3() {
        writeString("i: 20\nj: 30");

        Config config = YamlConfigurations.load(
                yamlFile, Config.class,
                YamlConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );

        assertConfigEquals(config, 20, 11);
    }

    @Test
    void updateYamlConfiguration1() {
        Config config = YamlConfigurations.update(yamlFile, Config.class);
        assertConfigEquals(config, 10, 11);
        assertEquals("i: 10\nj: 11", TestUtils.readFile(yamlFile));

        writeString("i: 20\nk: 30");
        config = YamlConfigurations.update(yamlFile, Config.class);
        assertConfigEquals(config, 20, 11);
        assertEquals("i: 20\nj: 11", TestUtils.readFile(yamlFile));
    }

    @Test
    void updateYamlConfiguration2() {
        Config config = YamlConfigurations.update(
                yamlFile, Config.class,
                builder -> builder.setFieldFilter(includeI)
        );
        assertConfigEquals(config, 10, 11);
        assertEquals("i: 10", TestUtils.readFile(yamlFile));
    }

    @Test
    void updateYamlConfiguration3() {
        Config config = YamlConfigurations.update(
                yamlFile, Config.class,
                YamlConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );
        assertConfigEquals(config, 10, 11);
        assertEquals("i: 10", TestUtils.readFile(yamlFile));
    }

    private static void assertConfigEquals(Config config, int i, int j) {
        assertEquals(i, config.i);
        assertEquals(j, config.j);
    }

    private void writeString(String string) {
        try {
            Files.writeString(yamlFile, string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}