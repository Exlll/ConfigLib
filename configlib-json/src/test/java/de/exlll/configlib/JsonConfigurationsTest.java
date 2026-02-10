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

class JsonConfigurationsTest {
    private static final FieldFilter includeI = field -> field.getName().equals("i");
    private final FileSystem fs = Jimfs.newFileSystem();
    private final Path jsonFile = fs.getPath(createPlatformSpecificFilePath("/tmp/config.json"));
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(jsonFile.getParent());
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
    void saveJsonConfiguration1() {
        Config configuration = new Config();

        JsonConfigurations.save(jsonFile, Config.class, configuration);
        assertEquals("{\n  \"i\" : 10,\n  \"j\" : 11\n}\n", TestUtils.readFile(jsonFile));

        configuration.i = 20;
        JsonConfigurations.save(jsonFile, Config.class, configuration);
        assertEquals("{\n  \"i\" : 20,\n  \"j\" : 11\n}\n", TestUtils.readFile(jsonFile));
    }

    @Test
    void writeJsonConfiguration1() {
        Config configuration = new Config();

        JsonConfigurations.write(outputStream, Config.class, configuration);
        assertEquals("{\n  \"i\" : 10,\n  \"j\" : 11\n}\n", outputStream.toString());

        outputStream.reset();

        configuration.i = 20;
        JsonConfigurations.write(outputStream, Config.class, configuration);
        assertEquals("{\n  \"i\" : 20,\n  \"j\" : 11\n}\n", outputStream.toString());
    }


    @Test
    void saveJsonConfiguration2() {
        Config configuration = new Config();

        JsonConfigurations.save(
                jsonFile, Config.class, configuration,
                builder -> builder.setFieldFilter(includeI)
        );
        assertEquals("{\n  \"i\" : 10\n}\n", TestUtils.readFile(jsonFile));
    }

    @Test
    void writeJsonConfiguration2() {
        Config configuration = new Config();

        JsonConfigurations.write(
                outputStream, Config.class, configuration,
                builder -> builder.setFieldFilter(includeI)
        );
        assertEquals("{\n  \"i\" : 10\n}\n", outputStream.toString());
    }

    @Test
    void saveJsonConfiguration3() {
        Config configuration = new Config();

        JsonConfigurations.save(
                jsonFile, Config.class, configuration,
                JsonConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );
        assertEquals("{\n  \"i\" : 10\n}\n", TestUtils.readFile(jsonFile));
    }


    @Test
    void writeJsonConfiguration3() {
        Config configuration = new Config();

        JsonConfigurations.write(
                outputStream, Config.class, configuration,
                JsonConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );
        assertEquals("{\n  \"i\" : 10\n}\n", outputStream.toString());
    }

    @Test
    void loadJsonConfiguration1() {
        writeStringToFile("{\"i\": 20, \"k\": 30}");
        Config config = JsonConfigurations.load(jsonFile, Config.class);
        assertConfigEquals(config, 20, 11);

        writeStringToFile("{\"i\": 20, \"j\": 30}");
        config = JsonConfigurations.load(jsonFile, Config.class);
        assertConfigEquals(config, 20, 30);
    }

    @Test
    void readJsonConfiguration1() {
        writeStringToStream("{\"i\": 20, \"k\": 30}");
        Config config = JsonConfigurations.read(inputFromOutput(), Config.class);
        assertConfigEquals(config, 20, 11);

        outputStream.reset();

        writeStringToStream("{\"i\": 20, \"j\": 30}");
        config = JsonConfigurations.read(inputFromOutput(), Config.class);
        assertConfigEquals(config, 20, 30);
    }

    @Test
    void loadJsonConfiguration2() {
        writeStringToFile("{\"i\": 20, \"j\": 30}");
        Config config = JsonConfigurations.load(
                jsonFile, Config.class,
                builder -> builder.setFieldFilter(includeI)
        );
        assertConfigEquals(config, 20, 11);
    }

    @Test
    void readJsonConfiguration2() {
        writeStringToStream("{\"i\": 20, \"j\": 30}");
        Config config = JsonConfigurations.read(
                inputFromOutput(), Config.class,
                builder -> builder.setFieldFilter(includeI)
        );
        assertConfigEquals(config, 20, 11);
    }

    @Test
    void loadJsonConfiguration3() {
        writeStringToFile("{\"i\": 20, \"j\": 30}");

        Config config = JsonConfigurations.load(
                jsonFile, Config.class,
                JsonConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );

        assertConfigEquals(config, 20, 11);
    }

    @Test
    void readJsonConfiguration3() {
        writeStringToStream("{\"i\": 20, \"j\": 30}");

        Config config = JsonConfigurations.read(
                inputFromOutput(), Config.class,
                JsonConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );

        assertConfigEquals(config, 20, 11);
    }

    @Test
    void updateJsonConfiguration1() {
        Config config = JsonConfigurations.update(jsonFile, Config.class);
        assertConfigEquals(config, 10, 11);
        assertEquals("{\n  \"i\" : 10,\n  \"j\" : 11\n}\n", TestUtils.readFile(jsonFile));

        writeStringToFile("{\"i\": 20, \"k\": 30}");
        config = JsonConfigurations.update(jsonFile, Config.class);
        assertConfigEquals(config, 20, 11);
        assertEquals("{\n  \"i\" : 20,\n  \"j\" : 11\n}\n", TestUtils.readFile(jsonFile));
    }

    @Test
    void updateJsonConfiguration2() {
        Config config = JsonConfigurations.update(
                jsonFile, Config.class,
                builder -> builder.setFieldFilter(includeI)
        );
        assertConfigEquals(config, 10, 11);
        assertEquals("{\n  \"i\" : 10\n}\n", TestUtils.readFile(jsonFile));
    }

    @Test
    void updateJsonConfiguration3() {
        Config config = JsonConfigurations.update(
                jsonFile, Config.class,
                JsonConfigurationProperties.newBuilder().setFieldFilter(includeI).build()
        );
        assertConfigEquals(config, 10, 11);
        assertEquals("{\n  \"i\" : 10\n}\n", TestUtils.readFile(jsonFile));
    }

    private static void assertConfigEquals(Config config, int i, int j) {
        assertEquals(i, config.i);
        assertEquals(j, config.j);
    }

    private void writeStringToFile(String string) {
        try {
            Files.writeString(jsonFile, string);
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
    void loadJsonConfigurationDoublesAllDecimal() {
        writeStringToFile(
                """
                {
                  "d": 10.0,
                  "boxed": 20.0,
                  "list": [
                    1.0,
                    2.0,
                    3.0
                  ]
                }
                """
        );
        DoublesConfig config = JsonConfigurations.load(jsonFile, DoublesConfig.class);
        assertEquals(10.0, config.d);
        assertEquals(20.0, config.boxed);
        assertEquals(asList(1.0, 2.0, 3.0), config.list);
    }

    @Test
    void loadJsonConfigurationDoublesUnboxed() {
        writeStringToFile("{\"d\": 10}");
        DoublesConfig config = JsonConfigurations.load(jsonFile, DoublesConfig.class);
        assertEquals(10.0, config.d);
    }

    @Test
    void loadJsonConfigurationDoublesBoxed() {
        writeStringToFile("{\"boxed\": 20}");
        DoublesConfig config = JsonConfigurations.load(jsonFile, DoublesConfig.class);
        assertEquals(20.0, config.boxed);
    }

    @Test
    void loadJsonConfigurationDoublesCollection() {
        writeStringToFile(
                """
                {
                  "list": [
                    1.0,
                    2,
                    3.0
                  ]
                }
                """
        );
        DoublesConfig config = JsonConfigurations.load(jsonFile, DoublesConfig.class);
        assertEquals(asList(1.0, 2.0, 3.0), config.list);
    }

    @Test
    void loadJsonConfigurationDoublesWithNulls() {
        writeStringToFile(
                """
                {
                  "boxed": null,
                  "list": [
                    null,
                    null,
                    1.0,
                    2
                  ]
                }
                """
        );
        DoublesConfig config = JsonConfigurations.load(
                jsonFile,
                DoublesConfig.class,
                builder -> builder.inputNulls(true)
        );
        assertEquals(0.0, config.d);
        assertNull(config.boxed);
        assertEquals(asList(null, null, 1.0, 2.0), config.list);
    }
}