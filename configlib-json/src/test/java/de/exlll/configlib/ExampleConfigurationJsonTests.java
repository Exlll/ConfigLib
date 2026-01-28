package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import de.exlll.configlib.configurations.ExampleConfigurationA2;
import de.exlll.configlib.configurations.ExampleConfigurationCustom;
import de.exlll.configlib.configurations.ExampleConfigurationNulls;
import de.exlll.configlib.configurations.ExampleInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static de.exlll.configlib.TestUtils.*;
import static de.exlll.configlib.configurations.ExampleEqualityAsserter.*;

final class ExampleConfigurationJsonTests {
    private final FileSystem fs = Jimfs.newFileSystem();
    private final Path jsonFile = fs.getPath(createPlatformSpecificFilePath("/tmp/config.json"));

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(jsonFile.getParent());
    }

    @AfterEach
    void tearDown() throws IOException {
        fs.close();
    }

    @Test
    void jsonStoreSavesAndLoadsExampleConfigurationA2() {
        var properties = JsonConfigurationProperties.newBuilder()
                .addSerializer(Point.class, POINT_SERIALIZER)
                .build();
        var store = new JsonConfigurationStore<>(ExampleConfigurationA2.class, properties);
        ExampleConfigurationA2 cfg1 = ExampleInitializer.newExampleConfigurationA2();
        store.save(cfg1, jsonFile);
        ExampleConfigurationA2 cfg2 = store.load(jsonFile);
        assertExampleConfigurationsA2Equal(cfg1, cfg2);
    }

    @Test
    void jsonStoreSavesAndLoadsExampleConfigurationNullsWithNullCollectionElements1() {
        var properties = JsonConfigurationProperties.newBuilder()
                .addSerializer(Point.class, POINT_SERIALIZER)
                .outputNulls(true)
                .inputNulls(true)
                .build();
        var store = new JsonConfigurationStore<>(ExampleConfigurationNulls.class, properties);
        ExampleConfigurationNulls cfg1 = ExampleInitializer
                .newExampleConfigurationNullsWithNullCollectionElements1();
        store.save(cfg1, jsonFile);
        ExampleConfigurationNulls cfg2 = store.load(jsonFile);
        assertExampleConfigurationsNullsEqual(cfg1, cfg2);
    }

    @Test
    void jsonStoreSavesAndLoadsExampleConfigurationNullsWithoutNullCollectionElements1() {
        var properties = JsonConfigurationProperties.newBuilder()
                .addSerializer(Point.class, POINT_SERIALIZER)
                .build();
        var store = new JsonConfigurationStore<>(ExampleConfigurationNulls.class, properties);
        ExampleConfigurationNulls cfg1 = ExampleInitializer
                .newExampleConfigurationNullsWithoutNullCollectionElements1();
        store.save(cfg1, jsonFile);
        ExampleConfigurationNulls cfg2 = store.load(jsonFile);
        assertExampleConfigurationsNullsEqual(cfg1, cfg2);
    }

    @Test
    void jsonStoreSavesAndLoadsExampleConfigurationCustom() {
        var properties = JsonConfigurationProperties.newBuilder().build();
        var store = new JsonConfigurationStore<>(ExampleConfigurationCustom.class, properties);
        ExampleConfigurationCustom config1 = new ExampleConfigurationCustom();
        store.save(config1, jsonFile);
        ExampleConfigurationCustom config2 = store.load(jsonFile);
        assertExampleConfigurationsCustomEqual(config1, config2);
    }
}