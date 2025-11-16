package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static de.exlll.configlib.TestUtils.asList;
import static de.exlll.configlib.TestUtils.createPlatformSpecificFilePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class YamlConfigurationsTest {
    private static final FieldFilter includeI = field -> field.getName().equals("i");
    private final FileSystem fs = Jimfs.newFileSystem();
    private final Path yamlFile = fs.getPath(createPlatformSpecificFilePath("/tmp/config.yml"));
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

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
    }

    @Test
    void saveYamlConfiguration1() {
        Config configuration = new Config();

        YamlConfigurations.save(yamlFile, Config.class, configuration);
        assertEquals("i: 10\nj: 11\n", TestUtils.readFile(yamlFile));

        configuration.i = 20;
        YamlConfigurations.save(yamlFile, Config.class, configuration);
        assertEquals("i: 20\nj: 11\n", TestUtils.readFile(yamlFile));
    }

    @Test
    void writeYamlConfiguration1() {
        Config configuration = new Config();

        YamlConfigurations.write(outputStream, Config.class, configuration);
        assertEquals("i: 10\nj: 11\n", outputStream.toString());

        outputStream.reset();

        configuration.i = 20;
        YamlConfigurations.write(outputStream, Config.class, configuration);
        assertEquals("i: 20\nj: 11\n", outputStream.toString());
    }


    @Test
    void saveYamlConfiguration2() {
        Config configuration = new Config();

        YamlConfigurations.save(
                yamlFile, Config.class, configuration,
                builder -> builder.setFieldFilter(includeI)
        );
        assertEquals("i: 10\n", TestUtils.readFile(yamlFile));
    }

    @Test
    void writeYamlConfiguration2() {
        Config configuration = new Config();

        YamlConfigurations.write(
                outputStream, Config.class, configuration,
                builder -> builder.setFieldFilter(includeI)
        );
        assertEquals("i: 10\n", outputStream.toString());
    }

    @Test
    void saveYamlConfiguration3() {
        Config configuration = new Config();

        YamlConfigurations.save(
                yamlFile, Config.class, configuration,
                YamlConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );
        assertEquals("i: 10\n", TestUtils.readFile(yamlFile));
    }


    @Test
    void writeYamlConfiguration3() {
        Config configuration = new Config();

        YamlConfigurations.write(
                outputStream, Config.class, configuration,
                YamlConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );
        assertEquals("i: 10\n", outputStream.toString());
    }

    @Test
    void loadYamlConfiguration1() {
        writeStringToFile("i: 20\nk: 30");
        Config config = YamlConfigurations.load(yamlFile, Config.class);
        assertConfigEquals(config, 20, 11);

        writeStringToFile("i: 20\nj: 30");
        config = YamlConfigurations.load(yamlFile, Config.class);
        assertConfigEquals(config, 20, 30);
    }

    @Test
    void readYamlConfiguration1() {
        writeStringToStream("i: 20\nk: 30");
        Config config = YamlConfigurations.read(inputFromOutput(), Config.class);
        assertConfigEquals(config, 20, 11);

        outputStream.reset();

        writeStringToStream("i: 20\nj: 30");
        config = YamlConfigurations.read(inputFromOutput(), Config.class);
        assertConfigEquals(config, 20, 30);
    }

    @Test
    void loadYamlConfiguration2() {
        writeStringToFile("i: 20\nj: 30");
        Config config = YamlConfigurations.load(
                yamlFile, Config.class,
                builder -> builder.setFieldFilter(includeI)
        );
        assertConfigEquals(config, 20, 11);
    }

    @Test
    void readYamlConfiguration2() {
        writeStringToStream("i: 20\nj: 30");
        Config config = YamlConfigurations.read(
                inputFromOutput(), Config.class,
                builder -> builder.setFieldFilter(includeI)
        );
        assertConfigEquals(config, 20, 11);
    }

    @Test
    void loadYamlConfiguration3() {
        writeStringToFile("i: 20\nj: 30");

        Config config = YamlConfigurations.load(
                yamlFile, Config.class,
                YamlConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );

        assertConfigEquals(config, 20, 11);
    }

    @Test
    void readYamlConfiguration3() {
        writeStringToStream("i: 20\nj: 30");

        Config config = YamlConfigurations.read(
                inputFromOutput(), Config.class,
                YamlConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );

        assertConfigEquals(config, 20, 11);
    }

    @Test
    void updateYamlConfiguration1() {
        Config config = YamlConfigurations.update(yamlFile, Config.class);
        assertConfigEquals(config, 10, 11);
        assertEquals("i: 10\nj: 11\n", TestUtils.readFile(yamlFile));

        writeStringToFile("i: 20\nk: 30");
        config = YamlConfigurations.update(yamlFile, Config.class);
        assertConfigEquals(config, 20, 11);
        assertEquals("i: 20\nj: 11\n", TestUtils.readFile(yamlFile));
    }

    @Test
    void updateYamlConfiguration2() {
        Config config = YamlConfigurations.update(
                yamlFile, Config.class,
                builder -> builder.setFieldFilter(includeI)
        );
        assertConfigEquals(config, 10, 11);
        assertEquals("i: 10\n", TestUtils.readFile(yamlFile));
    }

    @Test
    void updateYamlConfiguration3() {
        Config config = YamlConfigurations.update(
                yamlFile, Config.class,
                YamlConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );
        assertConfigEquals(config, 10, 11);
        assertEquals("i: 10\n", TestUtils.readFile(yamlFile));
    }

    private static void assertConfigEquals(Config config, int i, int j) {
        assertEquals(i, config.i);
        assertEquals(j, config.j);
    }

    private void writeStringToFile(String string) {
        try {
            Files.writeString(yamlFile, string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeStringToStream(String string) {
        outputStream.writeBytes(string.getBytes());
    }

    private InputStream inputFromOutput() {
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @Configuration
    private static final class DoublesConfig {
        double d;
        Double boxed;
        List<Double> list;
    }

    @Test
    void loadYamlConfigurationDoublesAllDecimal() {
        writeStringToFile(
                """
                d: 10.0
                boxed: 20.0
                list:
                  - 1.0
                  - 2.0
                  - 3.0
                """
        );
        DoublesConfig config = YamlConfigurations.load(yamlFile, DoublesConfig.class);
        assertEquals(10.0, config.d);
        assertEquals(20.0, config.boxed);
        assertEquals(asList(1.0, 2.0, 3.0), config.list);
    }

    @Test
    void loadYamlConfigurationDoublesUnboxed() {
        writeStringToFile("d: 10");
        DoublesConfig config = YamlConfigurations.load(yamlFile, DoublesConfig.class);
        assertEquals(10.0, config.d);
    }

    @Test
    void loadYamlConfigurationDoublesBoxed() {
        writeStringToFile("boxed: 20");
        DoublesConfig config = YamlConfigurations.load(yamlFile, DoublesConfig.class);
        assertEquals(20.0, config.boxed);
    }

    @Test
    void loadYamlConfigurationDoublesCollection() {
        writeStringToFile(
                """
                list:
                  - 1.0
                  - 2
                  - 3.0
                """
        );
        DoublesConfig config = YamlConfigurations.load(yamlFile, DoublesConfig.class);
        assertEquals(asList(1.0, 2.0, 3.0), config.list);
    }

    @Test
    void loadYamlConfigurationDoublesWithNulls() {
        writeStringToFile(
                """
                boxed: null
                list:
                - null
                - null
                - 1.0
                - 2
                """
        );
        DoublesConfig config = YamlConfigurations.load(
                yamlFile,
                DoublesConfig.class,
                builder -> builder.inputNulls(true)
        );
        assertEquals(0.0, config.d);
        assertNull(config.boxed);
        assertEquals(asList(null, null, 1.0, 2.0), config.list);
    }
}
