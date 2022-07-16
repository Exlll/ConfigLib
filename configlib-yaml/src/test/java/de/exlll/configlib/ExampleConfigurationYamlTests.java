package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import de.exlll.configlib.configurations.ExampleConfigurationA2;
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

import static de.exlll.configlib.configurations.ExampleEqualityAsserter.assertExampleConfigurationsA2Equal;
import static de.exlll.configlib.configurations.ExampleEqualityAsserter.assertExampleConfigurationsNullsEqual;

final class ExampleConfigurationYamlTests {
    private static final ConfigurationProperties PROPERTIES_ALLOW_NULL = ConfigurationProperties.newBuilder()
            .addSerializer(Point.class, TestUtils.POINT_SERIALIZER)
            .outputNulls(true)
            .inputNulls(true)
            .build();
    private static final ConfigurationProperties PROPERTIES_DENY_NULL = ConfigurationProperties.newBuilder()
            .addSerializer(Point.class, TestUtils.POINT_SERIALIZER)
            .outputNulls(false)
            .inputNulls(false)
            .build();

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

    @Test
    void yamlStoreSavesAndLoadsExampleConfigurationA2() {
        var properties = YamlConfigurationProperties.newBuilder()
                .addSerializer(Point.class, TestUtils.POINT_SERIALIZER)
                .build();
        var store = new YamlConfigurationStore<>(ExampleConfigurationA2.class, properties);
        ExampleConfigurationA2 cfg1 = ExampleInitializer.newExampleConfigurationA2();
        store.save(cfg1, yamlFile);
        ExampleConfigurationA2 cfg2 = store.load(yamlFile);
        assertExampleConfigurationsA2Equal(cfg1, cfg2);
    }

    @Test
    void yamlStoreSavesAndLoadsExampleConfigurationNullsWithNullCollectionElements1() {
        var properties = YamlConfigurationProperties.newBuilder()
                .addSerializer(Point.class, TestUtils.POINT_SERIALIZER)
                .outputNulls(true)
                .inputNulls(true)
                .build();
        var store = new YamlConfigurationStore<>(ExampleConfigurationNulls.class, properties);
        ExampleConfigurationNulls cfg1 = ExampleInitializer
                .newExampleConfigurationNullsWithNullCollectionElements1();
        store.save(cfg1, yamlFile);
        ExampleConfigurationNulls cfg2 = store.load(yamlFile);
        assertExampleConfigurationsNullsEqual(cfg1, cfg2);
    }

    @Test
    void yamlStoreSavesAndLoadsExampleConfigurationNullsWithoutNullCollectionElements1() {
        var properties = YamlConfigurationProperties.newBuilder()
                .addSerializer(Point.class, TestUtils.POINT_SERIALIZER)
                .build();
        var store = new YamlConfigurationStore<>(ExampleConfigurationNulls.class, properties);
        ExampleConfigurationNulls cfg1 = ExampleInitializer
                .newExampleConfigurationNullsWithoutNullCollectionElements1();
        store.save(cfg1, yamlFile);
        ExampleConfigurationNulls cfg2 = store.load(yamlFile);
        assertExampleConfigurationsNullsEqual(cfg1, cfg2);
    }
}
