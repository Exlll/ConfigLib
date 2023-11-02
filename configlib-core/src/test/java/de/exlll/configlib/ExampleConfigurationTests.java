package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import de.exlll.configlib.configurations.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static de.exlll.configlib.configurations.ExampleConfigurationsSerialized.*;
import static de.exlll.configlib.configurations.ExampleEqualityAsserter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExampleConfigurationTests {
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
    private final Path yamlFile = fs.getPath(TestUtils.createPlatformSpecificFilePath("/tmp/config.yml"));

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(yamlFile.getParent());
    }

    @AfterEach
    void tearDown() throws IOException {
        fs.close();
    }

    @Test
    void serializeExampleConfigurationB1() {
        ConfigurationSerializer<ExampleConfigurationB1> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationB1.class, PROPERTIES_ALLOW_NULL);

        ExampleConfigurationB1 config1 = ExampleInitializer.newExampleConfigurationB1_1();
        Map<?, ?> serialized1 = serializer.serialize(config1);
        assertEquals(EXAMPLE_CONFIGURATION_B1_1, serialized1);

        ExampleConfigurationB1 config2 = ExampleInitializer.newExampleConfigurationB1_2();
        Map<?, ?> serialized2 = serializer.serialize(config2);
        assertEquals(EXAMPLE_CONFIGURATION_B1_2, serialized2);
    }

    @Test
    void deserializeExampleConfigurationB1() {
        ConfigurationSerializer<ExampleConfigurationB1> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationB1.class, PROPERTIES_ALLOW_NULL);

        assertExampleConfigurationsB1Equal(
                serializer.deserialize(EXAMPLE_CONFIGURATION_B1_1),
                ExampleInitializer.newExampleConfigurationB1_1()
        );

        assertExampleConfigurationsB1Equal(
                serializer.deserialize(EXAMPLE_CONFIGURATION_B1_2),
                ExampleInitializer.newExampleConfigurationB1_2()
        );
    }

    @Test
    void serializeExampleConfigurationB2() {
        ConfigurationSerializer<ExampleConfigurationB2> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationB2.class, PROPERTIES_ALLOW_NULL);

        ExampleConfigurationB2 config1 = ExampleInitializer.newExampleConfigurationB2_1();
        Map<?, ?> serialized1 = serializer.serialize(config1);
        assertEquals(EXAMPLE_CONFIGURATION_B2_1, serialized1);

        ExampleConfigurationB2 config2 = ExampleInitializer.newExampleConfigurationB2_2();
        Map<?, ?> serialized2 = serializer.serialize(config2);
        assertEquals(EXAMPLE_CONFIGURATION_B2_2, serialized2);
    }

    @Test
    void deserializeExampleConfigurationB2() {
        ConfigurationSerializer<ExampleConfigurationB2> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationB2.class, PROPERTIES_ALLOW_NULL);

        assertExampleConfigurationsB2Equal(
                serializer.deserialize(EXAMPLE_CONFIGURATION_B2_1),
                ExampleInitializer.newExampleConfigurationB2_1()
        );

        assertExampleConfigurationsB2Equal(
                serializer.deserialize(EXAMPLE_CONFIGURATION_B2_2),
                ExampleInitializer.newExampleConfigurationB2_2()
        );
    }

    @Test
    void serializeExampleConfigurationA1() {
        ConfigurationSerializer<ExampleConfigurationA1> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationA1.class, PROPERTIES_ALLOW_NULL);

        ExampleConfigurationA1 config = ExampleInitializer.newExampleConfigurationA2();
        Map<?, ?> serialized = serializer.serialize(config);
        assertEquals(EXAMPLE_CONFIGURATION_A1, serialized);
    }

    @Test
    void deserializeExampleConfigurationA1() {
        ConfigurationSerializer<ExampleConfigurationA1> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationA1.class, PROPERTIES_ALLOW_NULL);

        assertExampleConfigurationsA1Equal(
                serializer.deserialize(EXAMPLE_CONFIGURATION_A1),
                ExampleInitializer.newExampleConfigurationA2()
        );

        assertExampleConfigurationsA1Equal(
                serializer.deserialize(EXAMPLE_CONFIGURATION_A2),
                ExampleInitializer.newExampleConfigurationA2()
        );
    }

    @Test
    void serializeExampleConfigurationA2() {
        ConfigurationSerializer<ExampleConfigurationA2> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationA2.class, PROPERTIES_ALLOW_NULL);

        ExampleConfigurationA2 config = ExampleInitializer.newExampleConfigurationA2();
        Map<?, ?> serialized = serializer.serialize(config);
        assertEquals(EXAMPLE_CONFIGURATION_A2, serialized);
    }

    @Test
    void deserializeExampleConfigurationA2() {
        ConfigurationSerializer<ExampleConfigurationA2> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationA2.class, PROPERTIES_ALLOW_NULL);

        assertExampleConfigurationsA2Equal(
                serializer.deserialize(EXAMPLE_CONFIGURATION_A2),
                ExampleInitializer.newExampleConfigurationA2()
        );
    }

    @Test
    void serializeExampleConfigurationsNullsWithNullCollectionElements() {
        ConfigurationSerializer<ExampleConfigurationNulls> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationNulls.class, PROPERTIES_ALLOW_NULL);

        ExampleConfigurationNulls config1 = ExampleInitializer
                .newExampleConfigurationNullsWithNullCollectionElements1();
        Map<?, ?> serialized1 = serializer.serialize(config1);
        assertEquals(EXAMPLE_CONFIGURATION_NULLS_WITH_1, serialized1);

        ExampleConfigurationNulls config2 = ExampleInitializer
                .newExampleConfigurationNullsWithNullCollectionElements2();
        Map<?, ?> serialized2 = serializer.serialize(config2);
        assertEquals(EXAMPLE_CONFIGURATION_NULLS_WITH_2, serialized2);
    }

    @Test
    void deserializeExampleConfigurationNullsWithNullCollectionElements() {
        ConfigurationSerializer<ExampleConfigurationNulls> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationNulls.class, PROPERTIES_ALLOW_NULL);

        assertExampleConfigurationsNullsEqual(
                serializer.deserialize(EXAMPLE_CONFIGURATION_NULLS_WITH_1),
                ExampleInitializer.newExampleConfigurationNullsWithNullCollectionElements1()
        );

        assertExampleConfigurationsNullsEqual(
                serializer.deserialize(EXAMPLE_CONFIGURATION_NULLS_WITH_2),
                ExampleInitializer.newExampleConfigurationNullsWithNullCollectionElements2()
        );
    }

    @Test
    void serializeExampleConfigurationsNullsWithoutNullCollectionElements() {
        ConfigurationSerializer<ExampleConfigurationNulls> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationNulls.class, PROPERTIES_DENY_NULL);

        ExampleConfigurationNulls config1 = ExampleInitializer
                .newExampleConfigurationNullsWithoutNullCollectionElements1();
        Map<?, ?> serialized1 = serializer.serialize(config1);
        assertEquals(EXAMPLE_CONFIGURATION_NULLS_WITHOUT_1, serialized1);

        ExampleConfigurationNulls config2 = ExampleInitializer
                .newExampleConfigurationNullsWithoutNullCollectionElements2();
        Map<?, ?> serialized2 = serializer.serialize(config2);
        assertEquals(EXAMPLE_CONFIGURATION_NULLS_WITHOUT_2, serialized2);
    }

    @Test
    void deserializeExampleConfigurationNullsWithoutNullCollectionElements() {
        ConfigurationSerializer<ExampleConfigurationNulls> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationNulls.class, PROPERTIES_DENY_NULL);

        assertExampleConfigurationsNullsEqual(
                ExampleInitializer.newExampleConfigurationNullsWithoutNullCollectionElements1(),
                serializer.deserialize(EXAMPLE_CONFIGURATION_NULLS_WITHOUT_1)
        );

        assertExampleConfigurationsNullsEqual(
                ExampleInitializer.newExampleConfigurationNullsWithoutNullCollectionElements2(),
                serializer.deserialize(EXAMPLE_CONFIGURATION_NULLS_WITHOUT_2)
        );
    }

    @Test
    void serializeExampleRecord1() {
        RecordSerializer<ExampleRecord1> serializer =
                new RecordSerializer<>(ExampleRecord1.class, PROPERTIES_DENY_NULL);

        assertEquals(EXAMPLE_RECORD1_1, serializer.serialize(ExampleInitializer.EXAMPLE_RECORD1_1));
        assertEquals(EXAMPLE_RECORD1_2, serializer.serialize(ExampleInitializer.EXAMPLE_RECORD1_2));
    }

    @Test
    void deserializeExampleRecord1() {
        RecordSerializer<ExampleRecord1> serializer =
                new RecordSerializer<>(ExampleRecord1.class, PROPERTIES_DENY_NULL);

        ExampleRecord1 deserialize1 = serializer.deserialize(EXAMPLE_RECORD1_1);
        assertExampleRecord1Equal(ExampleInitializer.EXAMPLE_RECORD1_1, deserialize1);

        ExampleRecord1 deserialize2 = serializer.deserialize(EXAMPLE_RECORD1_2);
        assertExampleRecord1Equal(ExampleInitializer.EXAMPLE_RECORD1_2, deserialize2);
    }

    @Test
    void serializeExampleRecord2() {
        RecordSerializer<ExampleRecord2> serializer =
                new RecordSerializer<>(ExampleRecord2.class, PROPERTIES_DENY_NULL);

        assertEquals(EXAMPLE_RECORD2_1, serializer.serialize(ExampleInitializer.EXAMPLE_RECORD2_1));
        assertEquals(EXAMPLE_RECORD2_2, serializer.serialize(ExampleInitializer.EXAMPLE_RECORD2_2));
    }

    @Test
    void deserializeExampleRecord2() {
        RecordSerializer<ExampleRecord2> serializer =
                new RecordSerializer<>(ExampleRecord2.class, PROPERTIES_DENY_NULL);

        ExampleRecord2 deserialize1 = serializer.deserialize(EXAMPLE_RECORD2_1);
        assertExampleRecord2Equal(ExampleInitializer.EXAMPLE_RECORD2_1, deserialize1);

        ExampleRecord2 deserialize2 = serializer.deserialize(EXAMPLE_RECORD2_2);
        assertExampleRecord2Equal(ExampleInitializer.EXAMPLE_RECORD2_2, deserialize2);
    }

    @Test
    void serializeExampleConfigurationCustom() {
        ConfigurationSerializer<ExampleConfigurationCustom> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationCustom.class, PROPERTIES_ALLOW_NULL);

        ExampleConfigurationCustom config = new ExampleConfigurationCustom();
        Map<?, ?> serialized = serializer.serialize(config);
        assertEquals(EXAMPLE_CONFIGURATION_CUSTOM, serialized);
    }

    @Test
    void deserializeExampleConfigurationCustom() {
        ConfigurationSerializer<ExampleConfigurationCustom> serializer =
                new ConfigurationSerializer<>(ExampleConfigurationCustom.class, PROPERTIES_ALLOW_NULL);

        assertExampleConfigurationsCustomEqual(
                serializer.deserialize(EXAMPLE_CONFIGURATION_CUSTOM),
                new ExampleConfigurationCustom()
        );
    }
}
