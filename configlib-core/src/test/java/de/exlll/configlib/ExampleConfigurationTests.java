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
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.exlll.configlib.configurations.ExampleConfigurationsSerialized.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

    private static void assertExampleConfigurationsNullsEqual(
            ExampleConfigurationNulls expected,
            ExampleConfigurationNulls actual
    ) {
        assertEquals(expected.getNullInteger(), actual.getNullInteger());
        assertEquals(expected.getNullString(), actual.getNullString());
        assertEquals(expected.getNullEnm(), actual.getNullEnm());
        assertEquals(expected.getNullB1(), actual.getNullB1());
        assertEquals(expected.getNullList(), actual.getNullList());
        assertEquals(expected.getNullArray(), actual.getNullArray());
        assertEquals(expected.getNullSet(), actual.getNullSet());
        assertEquals(expected.getNullMap(), actual.getNullMap());
        assertEquals(expected.getNullPoint(), actual.getNullPoint());
        assertEquals(expected.getListNullString(), actual.getListNullString());
        assertArrayEquals(expected.getArrayNullDouble(), actual.getArrayNullDouble());
        assertEquals(expected.getSetNullInteger(), actual.getSetNullInteger());
        assertEquals(expected.getMapNullEnmKey(), actual.getMapNullEnmKey());
        assertEquals(expected.getMapNullBigIntegerValue(), actual.getMapNullBigIntegerValue());
        assertEquals(expected, actual);
    }

    private static void assertExampleConfigurationsA1Equal(
            ExampleConfigurationA1 a1_1,
            ExampleConfigurationA1 a1_2
    ) {
        assertThat(ExampleConfigurationA1.getA1_staticFinalInt(), is(1));
        assertThat(ExampleConfigurationA1.getA1_staticInt(), is(2));

        assertThat(a1_1.getA1_finalInt(), is(a1_2.getA1_finalInt()));
        assertThat(a1_1.getA1_transientInt(), is(a1_2.getA1_transientInt()));
        assertThat(a1_1.getA1_ignoredInt(), is(a1_2.getA1_ignoredInt()));
        assertThat(a1_1.getA1_ignoredString(), is(a1_2.getA1_ignoredString()));
        assertThat(a1_1.getA1_ignoredListString(), is(a1_2.getA1_ignoredListString()));
        assertThat(a1_1.isA1_primBool(), is(a1_2.isA1_primBool()));
        assertThat(a1_1.getA1_primChar(), is(a1_2.getA1_primChar()));
        assertThat(a1_1.getA1_primByte(), is(a1_2.getA1_primByte()));
        assertThat(a1_1.getA1_primShort(), is(a1_2.getA1_primShort()));
        assertThat(a1_1.getA1_primInt(), is(a1_2.getA1_primInt()));
        assertThat(a1_1.getA1_primLong(), is(a1_2.getA1_primLong()));
        assertThat(a1_1.getA1_primFloat(), is(a1_2.getA1_primFloat()));
        assertThat(a1_1.getA1_primDouble(), is(a1_2.getA1_primDouble()));
        assertThat(a1_1.getA1_refBool(), is(a1_2.getA1_refBool()));
        assertThat(a1_1.getA1_refChar(), is(a1_2.getA1_refChar()));
        assertThat(a1_1.getA1_refByte(), is(a1_2.getA1_refByte()));
        assertThat(a1_1.getA1_refShort(), is(a1_2.getA1_refShort()));
        assertThat(a1_1.getA1_refInt(), is(a1_2.getA1_refInt()));
        assertThat(a1_1.getA1_refLong(), is(a1_2.getA1_refLong()));
        assertThat(a1_1.getA1_refFloat(), is(a1_2.getA1_refFloat()));
        assertThat(a1_1.getA1_refDouble(), is(a1_2.getA1_refDouble()));
        assertThat(a1_1.getA1_string(), is(a1_2.getA1_string()));
        assertThat(a1_1.getA1_bigInteger(), is(a1_2.getA1_bigInteger()));
        assertThat(a1_1.getA1_bigDecimal(), is(a1_2.getA1_bigDecimal()));
        assertThat(a1_1.getA1_localDate(), is(a1_2.getA1_localDate()));
        assertThat(a1_1.getA1_localTime(), is(a1_2.getA1_localTime()));
        assertThat(a1_1.getA1_localDateTime(), is(a1_2.getA1_localDateTime()));
        assertThat(a1_1.getA1_instant(), is(a1_2.getA1_instant()));
        assertThat(a1_1.getA1_uuid(), is(a1_2.getA1_uuid()));
        assertThat(a1_1.getA1_file(), is(a1_2.getA1_file()));
        assertThat(a1_1.getA1_path(), is(a1_2.getA1_path()));
        assertThat(a1_1.getA1_url(), is(a1_2.getA1_url()));
        assertThat(a1_1.getA1_uri(), is(a1_2.getA1_uri()));
        assertThat(a1_1.getA1_uuid(), is(a1_2.getA1_uuid()));
        assertThat(a1_1.getA1_Enm(), is(a1_2.getA1_Enm()));
        assertThat(a1_1.getA1_b1(), is(a1_2.getA1_b1()));
        assertThat(a1_1.getA1_b2(), is(a1_2.getA1_b2()));
        assertThat(a1_1.getA1_listBoolean(), is(a1_2.getA1_listBoolean()));
        assertThat(a1_1.getA1_listChar(), is(a1_2.getA1_listChar()));
        assertThat(a1_1.getA1_listByte(), is(a1_2.getA1_listByte()));
        assertThat(a1_1.getA1_listShort(), is(a1_2.getA1_listShort()));
        assertThat(a1_1.getA1_listInteger(), is(a1_2.getA1_listInteger()));
        assertThat(a1_1.getA1_listLong(), is(a1_2.getA1_listLong()));
        assertThat(a1_1.getA1_listFloat(), is(a1_2.getA1_listFloat()));
        assertThat(a1_1.getA1_listDouble(), is(a1_2.getA1_listDouble()));
        assertThat(a1_1.getA1_listString(), is(a1_2.getA1_listString()));
        assertThat(a1_1.getA1_listBigInteger(), is(a1_2.getA1_listBigInteger()));
        assertThat(a1_1.getA1_listBigDecimal(), is(a1_2.getA1_listBigDecimal()));
        assertThat(a1_1.getA1_listLocalDate(), is(a1_2.getA1_listLocalDate()));
        assertThat(a1_1.getA1_listLocalTime(), is(a1_2.getA1_listLocalTime()));
        assertThat(a1_1.getA1_listLocalDateTime(), is(a1_2.getA1_listLocalDateTime()));
        assertThat(a1_1.getA1_listInstant(), is(a1_2.getA1_listInstant()));
        assertThat(a1_1.getA1_listUuid(), is(a1_2.getA1_listUuid()));
        assertThat(a1_1.getA1_listFile(), is(a1_2.getA1_listFile()));
        assertThat(a1_1.getA1_listPath(), is(a1_2.getA1_listPath()));
        assertThat(a1_1.getA1_listUrl(), is(a1_2.getA1_listUrl()));
        assertThat(a1_1.getA1_listUri(), is(a1_2.getA1_listUri()));
        assertThat(a1_1.getA1_listEnm(), is(a1_2.getA1_listEnm()));
        assertThat(a1_1.getA1_listB1(), is(a1_2.getA1_listB1()));
        assertThat(a1_1.getA1_listB2(), is(a1_2.getA1_listB2()));
        assertThat(a1_1.getA1_arrayPrimBoolean(), is(a1_2.getA1_arrayPrimBoolean()));
        assertThat(a1_1.getA1_arrayPrimChar(), is(a1_2.getA1_arrayPrimChar()));
        assertThat(a1_1.getA1_arrayPrimByte(), is(a1_2.getA1_arrayPrimByte()));
        assertThat(a1_1.getA1_arrayPrimShort(), is(a1_2.getA1_arrayPrimShort()));
        assertThat(a1_1.getA1_arrayPrimInteger(), is(a1_2.getA1_arrayPrimInteger()));
        assertThat(a1_1.getA1_arrayPrimLong(), is(a1_2.getA1_arrayPrimLong()));
        assertThat(a1_1.getA1_arrayPrimFloat(), is(a1_2.getA1_arrayPrimFloat()));
        assertThat(a1_1.getA1_arrayPrimDouble(), is(a1_2.getA1_arrayPrimDouble()));
        assertThat(a1_1.getA1_arrayBoolean(), is(a1_2.getA1_arrayBoolean()));
        assertThat(a1_1.getA1_arrayChar(), is(a1_2.getA1_arrayChar()));
        assertThat(a1_1.getA1_arrayByte(), is(a1_2.getA1_arrayByte()));
        assertThat(a1_1.getA1_arrayShort(), is(a1_2.getA1_arrayShort()));
        assertThat(a1_1.getA1_arrayInteger(), is(a1_2.getA1_arrayInteger()));
        assertThat(a1_1.getA1_arrayLong(), is(a1_2.getA1_arrayLong()));
        assertThat(a1_1.getA1_arrayFloat(), is(a1_2.getA1_arrayFloat()));
        assertThat(a1_1.getA1_arrayDouble(), is(a1_2.getA1_arrayDouble()));
        assertThat(a1_1.getA1_arrayString(), is(a1_2.getA1_arrayString()));
        assertThat(a1_1.getA1_arrayBigInteger(), is(a1_2.getA1_arrayBigInteger()));
        assertThat(a1_1.getA1_arrayBigDecimal(), is(a1_2.getA1_arrayBigDecimal()));
        assertThat(a1_1.getA1_arrayLocalDate(), is(a1_2.getA1_arrayLocalDate()));
        assertThat(a1_1.getA1_arrayLocalTime(), is(a1_2.getA1_arrayLocalTime()));
        assertThat(a1_1.getA1_arrayLocalDateTime(), is(a1_2.getA1_arrayLocalDateTime()));
        assertThat(a1_1.getA1_arrayUuid(), is(a1_2.getA1_arrayUuid()));
        assertThat(a1_1.getA1_arrayEnm(), is(a1_2.getA1_arrayEnm()));
        assertThat(a1_1.getA1_arrayB1(), is(a1_2.getA1_arrayB1()));
        assertThat(a1_1.getA1_arrayB2(), is(a1_2.getA1_arrayB2()));
        assertThat(a1_1.getA1_setBoolean(), is(a1_2.getA1_setBoolean()));
        assertThat(a1_1.getA1_setChar(), is(a1_2.getA1_setChar()));
        assertThat(a1_1.getA1_setByte(), is(a1_2.getA1_setByte()));
        assertThat(a1_1.getA1_setShort(), is(a1_2.getA1_setShort()));
        assertThat(a1_1.getA1_setInteger(), is(a1_2.getA1_setInteger()));
        assertThat(a1_1.getA1_setLong(), is(a1_2.getA1_setLong()));
        assertThat(a1_1.getA1_setFloat(), is(a1_2.getA1_setFloat()));
        assertThat(a1_1.getA1_setDouble(), is(a1_2.getA1_setDouble()));
        assertThat(a1_1.getA1_setString(), is(a1_2.getA1_setString()));
        assertThat(a1_1.getA1_setBigInteger(), is(a1_2.getA1_setBigInteger()));
        assertThat(a1_1.getA1_setBigDecimal(), is(a1_2.getA1_setBigDecimal()));
        assertThat(a1_1.getA1_setLocalDate(), is(a1_2.getA1_setLocalDate()));
        assertThat(a1_1.getA1_setLocalTime(), is(a1_2.getA1_setLocalTime()));
        assertThat(a1_1.getA1_setLocalDateTime(), is(a1_2.getA1_setLocalDateTime()));
        assertThat(a1_1.getA1_setUuid(), is(a1_2.getA1_setUuid()));
        assertThat(a1_1.getA1_setEnm(), is(a1_2.getA1_setEnm()));
        assertThat(a1_1.getA1_setB1(), is(a1_2.getA1_setB1()));
        assertThat(a1_1.getA1_setB2(), is(a1_2.getA1_setB2()));
        assertThat(a1_1.getA1_mapBooleanBoolean(), is(a1_2.getA1_mapBooleanBoolean()));
        assertThat(a1_1.getA1_mapCharChar(), is(a1_2.getA1_mapCharChar()));
        assertThat(a1_1.getA1_mapByteByte(), is(a1_2.getA1_mapByteByte()));
        assertThat(a1_1.getA1_mapShortShort(), is(a1_2.getA1_mapShortShort()));
        assertThat(a1_1.getA1_mapIntegerInteger(), is(a1_2.getA1_mapIntegerInteger()));
        assertThat(a1_1.getA1_mapLongLong(), is(a1_2.getA1_mapLongLong()));
        assertThat(a1_1.getA1_mapFloatFloat(), is(a1_2.getA1_mapFloatFloat()));
        assertThat(a1_1.getA1_mapDoubleDouble(), is(a1_2.getA1_mapDoubleDouble()));
        assertThat(a1_1.getA1_mapStringString(), is(a1_2.getA1_mapStringString()));
        assertThat(a1_1.getA1_mapBigIntegerBigInteger(), is(a1_2.getA1_mapBigIntegerBigInteger()));
        assertThat(a1_1.getA1_mapBigDecimalBigDecimal(), is(a1_2.getA1_mapBigDecimalBigDecimal()));
        assertThat(a1_1.getA1_mapLocalDateLocalDate(), is(a1_2.getA1_mapLocalDateLocalDate()));
        assertThat(a1_1.getA1_mapLocalTimeLocalTime(), is(a1_2.getA1_mapLocalTimeLocalTime()));
        assertThat(a1_1.getA1_mapLocalDateTimeLocalDateTime(), is(a1_2.getA1_mapLocalDateTimeLocalDateTime()));
        assertThat(a1_1.getA1_mapUuidUuid(), is(a1_2.getA1_mapUuidUuid()));
        assertThat(a1_1.getA1_mapEnmEnm(), is(a1_2.getA1_mapEnmEnm()));
        assertThat(a1_1.getA1_mapIntegerB1(), is(a1_2.getA1_mapIntegerB1()));
        assertThat(a1_1.getA1_mapEnmB2(), is(a1_2.getA1_mapEnmB2()));
        assertThat(a1_1.getA1_listEmpty(), is(a1_2.getA1_listEmpty()));
        assertThat(a1_1.getA1_arrayEmpty(), is(a1_2.getA1_arrayEmpty()));
        assertThat(a1_1.getA1_setEmpty(), is(a1_2.getA1_setEmpty()));
        assertThat(a1_1.getA1_mapEmpty(), is(a1_2.getA1_mapEmpty()));
        assertThat(a1_1.getA1_listListByte(), is(a1_2.getA1_listListByte()));
        assertDeepEquals(a1_1.getA1_listArrayFloat(), a1_2.getA1_listArrayFloat(), ArrayList::new);
        assertThat(a1_1.getA1_listSetString(), is(a1_2.getA1_listSetString()));
        assertThat(a1_1.getA1_listMapEnmLocalDate(), is(a1_2.getA1_listMapEnmLocalDate()));
        assertThat(a1_1.getA1_setSetShort(), is(a1_2.getA1_setSetShort()));
        assertDeepEquals(a1_1.getA1_setArrayDouble(), a1_2.getA1_setArrayDouble(), HashSet::new);
        assertThat(a1_1.getA1_setListString(), is(a1_2.getA1_setListString()));
        assertThat(a1_1.getA1_setMapEnmLocalTime(), is(a1_2.getA1_setMapEnmLocalTime()));
        assertThat(a1_1.getA1_mapIntegerMapLongBoolean(), is(a1_2.getA1_mapIntegerMapLongBoolean()));
        assertThat(a1_1.getA1_mapStringListB1(), is(a1_2.getA1_mapStringListB1()));
        assertEquals(a1_2.getA1_mapBigIntegerArrayBigDecimal().keySet(), a1_1.getA1_mapBigIntegerArrayBigDecimal().keySet());
        assertDeepEquals(a1_2.getA1_mapBigIntegerArrayBigDecimal().values(), a1_2.getA1_mapBigIntegerArrayBigDecimal().values(), HashSet::new);
        assertThat(a1_1.getA1_mapEnmSetB2(), is(a1_2.getA1_mapEnmSetB2()));
        assertThat(a1_1.getA1_mapIntegerListMapShortSetB2(), is(a1_2.getA1_mapIntegerListMapShortSetB2()));
        assertThat(a1_1.getA1_arrayArrayPrimBoolean(), is(a1_2.getA1_arrayArrayPrimBoolean()));
        assertThat(a1_1.getA1_arrayArrayPrimChar(), is(a1_2.getA1_arrayArrayPrimChar()));
        assertThat(a1_1.getA1_arrayArrayPrimByte(), is(a1_2.getA1_arrayArrayPrimByte()));
        assertThat(a1_1.getA1_arrayArrayPrimShort(), is(a1_2.getA1_arrayArrayPrimShort()));
        assertThat(a1_1.getA1_arrayArrayPrimInteger(), is(a1_2.getA1_arrayArrayPrimInteger()));
        assertThat(a1_1.getA1_arrayArrayPrimLong(), is(a1_2.getA1_arrayArrayPrimLong()));
        assertThat(a1_1.getA1_arrayArrayPrimFloat(), is(a1_2.getA1_arrayArrayPrimFloat()));
        assertThat(a1_1.getA1_arrayArrayPrimDouble(), is(a1_2.getA1_arrayArrayPrimDouble()));
        assertThat(a1_1.getA1_arrayArrayBoolean(), is(a1_2.getA1_arrayArrayBoolean()));
        assertThat(a1_1.getA1_arrayArrayChar(), is(a1_2.getA1_arrayArrayChar()));
        assertThat(a1_1.getA1_arrayArrayByte(), is(a1_2.getA1_arrayArrayByte()));
        assertThat(a1_1.getA1_arrayArrayShort(), is(a1_2.getA1_arrayArrayShort()));
        assertThat(a1_1.getA1_arrayArrayInteger(), is(a1_2.getA1_arrayArrayInteger()));
        assertThat(a1_1.getA1_arrayArrayLong(), is(a1_2.getA1_arrayArrayLong()));
        assertThat(a1_1.getA1_arrayArrayFloat(), is(a1_2.getA1_arrayArrayFloat()));
        assertThat(a1_1.getA1_arrayArrayDouble(), is(a1_2.getA1_arrayArrayDouble()));
        assertThat(a1_1.getA1_arrayArrayString(), is(a1_2.getA1_arrayArrayString()));
        assertThat(a1_1.getA1_arrayArrayBigInteger(), is(a1_2.getA1_arrayArrayBigInteger()));
        assertThat(a1_1.getA1_arrayArrayBigDecimal(), is(a1_2.getA1_arrayArrayBigDecimal()));
        assertThat(a1_1.getA1_arrayArrayLocalDate(), is(a1_2.getA1_arrayArrayLocalDate()));
        assertThat(a1_1.getA1_arrayArrayLocalTime(), is(a1_2.getA1_arrayArrayLocalTime()));
        assertThat(a1_1.getA1_arrayArrayLocalDateTime(), is(a1_2.getA1_arrayArrayLocalDateTime()));
        assertThat(a1_1.getA1_arrayArrayUuid(), is(a1_2.getA1_arrayArrayUuid()));
        assertThat(a1_1.getA1_arrayArrayEnm(), is(a1_2.getA1_arrayArrayEnm()));
        assertThat(a1_1.getA1_arrayArrayB1(), is(a1_2.getA1_arrayArrayB1()));
        assertThat(a1_1.getA1_arrayArrayB2(), is(a1_2.getA1_arrayArrayB2()));
        assertThat(a1_1.getA1_point(), is(a1_2.getA1_point()));
        assertThat(a1_1.getA1_listPoint(), is(a1_2.getA1_listPoint()));
        assertThat(a1_1.getA1_arrayPoint(), is(a1_2.getA1_arrayPoint()));
        assertThat(a1_1.getA1_setPoint(), is(a1_2.getA1_setPoint()));
        assertThat(a1_1.getA1_mapEnmListPoint(), is(a1_2.getA1_mapEnmListPoint()));
    }

    static void assertExampleConfigurationsA2Equal(
            ExampleConfigurationA2 a2_1,
            ExampleConfigurationA2 a2_2
    ) {
        assertThat(ExampleConfigurationA2.getA1_staticFinalInt(), is(1));
        assertThat(ExampleConfigurationA2.getA1_staticInt(), is(2));

        assertExampleConfigurationsA1Equal(a2_1, a2_2);

        assertThat(a2_1.getA2_finalInt(), is(a2_2.getA2_finalInt()));
        assertThat(a2_1.getA2_transientInt(), is(a2_2.getA2_transientInt()));
        assertThat(a2_1.getA2_ignoredInt(), is(a2_2.getA2_ignoredInt()));
        assertThat(a2_1.getA2_ignoredString(), is(a2_2.getA2_ignoredString()));
        assertThat(a2_1.getA2_ignoredListString(), is(a2_2.getA2_ignoredListString()));
        assertThat(a2_1.isA2_primBool(), is(a2_2.isA2_primBool()));
        assertThat(a2_1.getA2_primChar(), is(a2_2.getA2_primChar()));
        assertThat(a2_1.getA2_primByte(), is(a2_2.getA2_primByte()));
        assertThat(a2_1.getA2_primShort(), is(a2_2.getA2_primShort()));
        assertThat(a2_1.getA2_primInt(), is(a2_2.getA2_primInt()));
        assertThat(a2_1.getA2_primLong(), is(a2_2.getA2_primLong()));
        assertThat(a2_1.getA2_primFloat(), is(a2_2.getA2_primFloat()));
        assertThat(a2_1.getA2_primDouble(), is(a2_2.getA2_primDouble()));
        assertThat(a2_1.getA2_refBool(), is(a2_2.getA2_refBool()));
        assertThat(a2_1.getA2_refChar(), is(a2_2.getA2_refChar()));
        assertThat(a2_1.getA2_refByte(), is(a2_2.getA2_refByte()));
        assertThat(a2_1.getA2_refShort(), is(a2_2.getA2_refShort()));
        assertThat(a2_1.getA2_refInt(), is(a2_2.getA2_refInt()));
        assertThat(a2_1.getA2_refLong(), is(a2_2.getA2_refLong()));
        assertThat(a2_1.getA2_refFloat(), is(a2_2.getA2_refFloat()));
        assertThat(a2_1.getA2_refDouble(), is(a2_2.getA2_refDouble()));
        assertThat(a2_1.getA2_string(), is(a2_2.getA2_string()));
        assertThat(a2_1.getA2_bigInteger(), is(a2_2.getA2_bigInteger()));
        assertThat(a2_1.getA2_bigDecimal(), is(a2_2.getA2_bigDecimal()));
        assertThat(a2_1.getA2_localDate(), is(a2_2.getA2_localDate()));
        assertThat(a2_1.getA2_localTime(), is(a2_2.getA2_localTime()));
        assertThat(a2_1.getA2_localDateTime(), is(a2_2.getA2_localDateTime()));
        assertThat(a2_1.getA2_instant(), is(a2_2.getA2_instant()));
        assertThat(a2_1.getA2_uuid(), is(a2_2.getA2_uuid()));
        assertThat(a2_1.getA2_file(), is(a2_2.getA2_file()));
        assertThat(a2_1.getA2_path(), is(a2_2.getA2_path()));
        assertThat(a2_1.getA2_url(), is(a2_2.getA2_url()));
        assertThat(a2_1.getA2_uri(), is(a2_2.getA2_uri()));
        assertThat(a2_1.getA2_Enm(), is(a2_2.getA2_Enm()));
        assertThat(a2_1.getA2_b1(), is(a2_2.getA2_b1()));
        assertThat(a2_1.getA2_b2(), is(a2_2.getA2_b2()));
        assertThat(a2_1.getA2_listBoolean(), is(a2_2.getA2_listBoolean()));
        assertThat(a2_1.getA2_listChar(), is(a2_2.getA2_listChar()));
        assertThat(a2_1.getA2_listByte(), is(a2_2.getA2_listByte()));
        assertThat(a2_1.getA2_listShort(), is(a2_2.getA2_listShort()));
        assertThat(a2_1.getA2_listInteger(), is(a2_2.getA2_listInteger()));
        assertThat(a2_1.getA2_listLong(), is(a2_2.getA2_listLong()));
        assertThat(a2_1.getA2_listFloat(), is(a2_2.getA2_listFloat()));
        assertThat(a2_1.getA2_listDouble(), is(a2_2.getA2_listDouble()));
        assertThat(a2_1.getA2_listString(), is(a2_2.getA2_listString()));
        assertThat(a2_1.getA2_listBigInteger(), is(a2_2.getA2_listBigInteger()));
        assertThat(a2_1.getA2_listBigDecimal(), is(a2_2.getA2_listBigDecimal()));
        assertThat(a2_1.getA2_listLocalDate(), is(a2_2.getA2_listLocalDate()));
        assertThat(a2_1.getA2_listLocalTime(), is(a2_2.getA2_listLocalTime()));
        assertThat(a2_1.getA2_listLocalDateTime(), is(a2_2.getA2_listLocalDateTime()));
        assertThat(a2_1.getA2_listInstant(), is(a2_2.getA2_listInstant()));
        assertThat(a2_1.getA2_listUuid(), is(a2_2.getA2_listUuid()));
        assertThat(a2_1.getA2_listFile(), is(a2_2.getA2_listFile()));
        assertThat(a2_1.getA2_listPath(), is(a2_2.getA2_listPath()));
        assertThat(a2_1.getA2_listUrl(), is(a2_2.getA2_listUrl()));
        assertThat(a2_1.getA2_listUri(), is(a2_2.getA2_listUri()));
        assertThat(a2_1.getA2_listEnm(), is(a2_2.getA2_listEnm()));
        assertThat(a2_1.getA2_listB1(), is(a2_2.getA2_listB1()));
        assertThat(a2_1.getA2_listB2(), is(a2_2.getA2_listB2()));
        assertThat(a2_1.getA2_arrayPrimBoolean(), is(a2_2.getA2_arrayPrimBoolean()));
        assertThat(a2_1.getA2_arrayPrimChar(), is(a2_2.getA2_arrayPrimChar()));
        assertThat(a2_1.getA2_arrayPrimByte(), is(a2_2.getA2_arrayPrimByte()));
        assertThat(a2_1.getA2_arrayPrimShort(), is(a2_2.getA2_arrayPrimShort()));
        assertThat(a2_1.getA2_arrayPrimInteger(), is(a2_2.getA2_arrayPrimInteger()));
        assertThat(a2_1.getA2_arrayPrimLong(), is(a2_2.getA2_arrayPrimLong()));
        assertThat(a2_1.getA2_arrayPrimFloat(), is(a2_2.getA2_arrayPrimFloat()));
        assertThat(a2_1.getA2_arrayPrimDouble(), is(a2_2.getA2_arrayPrimDouble()));
        assertThat(a2_1.getA2_arrayBoolean(), is(a2_2.getA2_arrayBoolean()));
        assertThat(a2_1.getA2_arrayChar(), is(a2_2.getA2_arrayChar()));
        assertThat(a2_1.getA2_arrayByte(), is(a2_2.getA2_arrayByte()));
        assertThat(a2_1.getA2_arrayShort(), is(a2_2.getA2_arrayShort()));
        assertThat(a2_1.getA2_arrayInteger(), is(a2_2.getA2_arrayInteger()));
        assertThat(a2_1.getA2_arrayLong(), is(a2_2.getA2_arrayLong()));
        assertThat(a2_1.getA2_arrayFloat(), is(a2_2.getA2_arrayFloat()));
        assertThat(a2_1.getA2_arrayDouble(), is(a2_2.getA2_arrayDouble()));
        assertThat(a2_1.getA2_arrayString(), is(a2_2.getA2_arrayString()));
        assertThat(a2_1.getA2_arrayBigInteger(), is(a2_2.getA2_arrayBigInteger()));
        assertThat(a2_1.getA2_arrayBigDecimal(), is(a2_2.getA2_arrayBigDecimal()));
        assertThat(a2_1.getA2_arrayLocalDate(), is(a2_2.getA2_arrayLocalDate()));
        assertThat(a2_1.getA2_arrayLocalTime(), is(a2_2.getA2_arrayLocalTime()));
        assertThat(a2_1.getA2_arrayLocalDateTime(), is(a2_2.getA2_arrayLocalDateTime()));
        assertThat(a2_1.getA2_arrayUuid(), is(a2_2.getA2_arrayUuid()));
        assertThat(a2_1.getA2_arrayEnm(), is(a2_2.getA2_arrayEnm()));
        assertThat(a2_1.getA2_arrayB1(), is(a2_2.getA2_arrayB1()));
        assertThat(a2_1.getA2_arrayB2(), is(a2_2.getA2_arrayB2()));
        assertThat(a2_1.getA2_setBoolean(), is(a2_2.getA2_setBoolean()));
        assertThat(a2_1.getA2_setChar(), is(a2_2.getA2_setChar()));
        assertThat(a2_1.getA2_setByte(), is(a2_2.getA2_setByte()));
        assertThat(a2_1.getA2_setShort(), is(a2_2.getA2_setShort()));
        assertThat(a2_1.getA2_setInteger(), is(a2_2.getA2_setInteger()));
        assertThat(a2_1.getA2_setLong(), is(a2_2.getA2_setLong()));
        assertThat(a2_1.getA2_setFloat(), is(a2_2.getA2_setFloat()));
        assertThat(a2_1.getA2_setDouble(), is(a2_2.getA2_setDouble()));
        assertThat(a2_1.getA2_setString(), is(a2_2.getA2_setString()));
        assertThat(a2_1.getA2_setBigInteger(), is(a2_2.getA2_setBigInteger()));
        assertThat(a2_1.getA2_setBigDecimal(), is(a2_2.getA2_setBigDecimal()));
        assertThat(a2_1.getA2_setLocalDate(), is(a2_2.getA2_setLocalDate()));
        assertThat(a2_1.getA2_setLocalTime(), is(a2_2.getA2_setLocalTime()));
        assertThat(a2_1.getA2_setLocalDateTime(), is(a2_2.getA2_setLocalDateTime()));
        assertThat(a2_1.getA2_setUuid(), is(a2_2.getA2_setUuid()));
        assertThat(a2_1.getA2_setEnm(), is(a2_2.getA2_setEnm()));
        assertThat(a2_1.getA2_setB1(), is(a2_2.getA2_setB1()));
        assertThat(a2_1.getA2_setB2(), is(a2_2.getA2_setB2()));
        assertThat(a2_1.getA2_mapBooleanBoolean(), is(a2_2.getA2_mapBooleanBoolean()));
        assertThat(a2_1.getA2_mapCharChar(), is(a2_2.getA2_mapCharChar()));
        assertThat(a2_1.getA2_mapByteByte(), is(a2_2.getA2_mapByteByte()));
        assertThat(a2_1.getA2_mapShortShort(), is(a2_2.getA2_mapShortShort()));
        assertThat(a2_1.getA2_mapIntegerInteger(), is(a2_2.getA2_mapIntegerInteger()));
        assertThat(a2_1.getA2_mapLongLong(), is(a2_2.getA2_mapLongLong()));
        assertThat(a2_1.getA2_mapFloatFloat(), is(a2_2.getA2_mapFloatFloat()));
        assertThat(a2_1.getA2_mapDoubleDouble(), is(a2_2.getA2_mapDoubleDouble()));
        assertThat(a2_1.getA2_mapStringString(), is(a2_2.getA2_mapStringString()));
        assertThat(a2_1.getA2_mapBigIntegerBigInteger(), is(a2_2.getA2_mapBigIntegerBigInteger()));
        assertThat(a2_1.getA2_mapBigDecimalBigDecimal(), is(a2_2.getA2_mapBigDecimalBigDecimal()));
        assertThat(a2_1.getA2_mapLocalDateLocalDate(), is(a2_2.getA2_mapLocalDateLocalDate()));
        assertThat(a2_1.getA2_mapLocalTimeLocalTime(), is(a2_2.getA2_mapLocalTimeLocalTime()));
        assertThat(a2_1.getA2_mapLocalDateTimeLocalDateTime(), is(a2_2.getA2_mapLocalDateTimeLocalDateTime()));
        assertThat(a2_1.getA2_mapUuidUuid(), is(a2_2.getA2_mapUuidUuid()));
        assertThat(a2_1.getA2_mapEnmEnm(), is(a2_2.getA2_mapEnmEnm()));
        assertThat(a2_1.getA2_mapIntegerB1(), is(a2_2.getA2_mapIntegerB1()));
        assertThat(a2_1.getA2_mapEnmB2(), is(a2_2.getA2_mapEnmB2()));
        assertThat(a2_1.getA2_listEmpty(), is(a2_2.getA2_listEmpty()));
        assertThat(a2_1.getA2_arrayEmpty(), is(a2_2.getA2_arrayEmpty()));
        assertThat(a2_1.getA2_setEmpty(), is(a2_2.getA2_setEmpty()));
        assertThat(a2_1.getA2_mapEmpty(), is(a2_2.getA2_mapEmpty()));
        assertThat(a2_1.getA2_listListByte(), is(a2_2.getA2_listListByte()));
        assertDeepEquals(a2_1.getA2_listArrayFloat(), a2_2.getA2_listArrayFloat(), ArrayList::new);
        assertThat(a2_1.getA2_listSetString(), is(a2_2.getA2_listSetString()));
        assertThat(a2_1.getA2_listMapEnmLocalDate(), is(a2_2.getA2_listMapEnmLocalDate()));
        assertThat(a2_1.getA2_setSetShort(), is(a2_2.getA2_setSetShort()));
        assertDeepEquals(a2_1.getA2_setArrayDouble(), a2_2.getA2_setArrayDouble(), HashSet::new);
        assertThat(a2_1.getA2_setListString(), is(a2_2.getA2_setListString()));
        assertThat(a2_1.getA2_setMapEnmLocalTime(), is(a2_2.getA2_setMapEnmLocalTime()));
        assertThat(a2_1.getA2_mapIntegerMapLongBoolean(), is(a2_2.getA2_mapIntegerMapLongBoolean()));
        assertThat(a2_1.getA2_mapStringListB1(), is(a2_2.getA2_mapStringListB1()));
        assertEquals(a2_2.getA2_mapBigIntegerArrayBigDecimal().keySet(), a2_1.getA2_mapBigIntegerArrayBigDecimal().keySet());
        assertDeepEquals(a2_2.getA2_mapBigIntegerArrayBigDecimal().values(), a2_2.getA2_mapBigIntegerArrayBigDecimal().values(), HashSet::new);
        assertThat(a2_1.getA2_mapEnmSetB2(), is(a2_2.getA2_mapEnmSetB2()));
        assertThat(a2_1.getA2_mapIntegerListMapShortSetB2(), is(a2_2.getA2_mapIntegerListMapShortSetB2()));
        assertThat(a2_1.getA2_arrayArrayPrimBoolean(), is(a2_2.getA2_arrayArrayPrimBoolean()));
        assertThat(a2_1.getA2_arrayArrayPrimChar(), is(a2_2.getA2_arrayArrayPrimChar()));
        assertThat(a2_1.getA2_arrayArrayPrimByte(), is(a2_2.getA2_arrayArrayPrimByte()));
        assertThat(a2_1.getA2_arrayArrayPrimShort(), is(a2_2.getA2_arrayArrayPrimShort()));
        assertThat(a2_1.getA2_arrayArrayPrimInteger(), is(a2_2.getA2_arrayArrayPrimInteger()));
        assertThat(a2_1.getA2_arrayArrayPrimLong(), is(a2_2.getA2_arrayArrayPrimLong()));
        assertThat(a2_1.getA2_arrayArrayPrimFloat(), is(a2_2.getA2_arrayArrayPrimFloat()));
        assertThat(a2_1.getA2_arrayArrayPrimDouble(), is(a2_2.getA2_arrayArrayPrimDouble()));
        assertThat(a2_1.getA2_arrayArrayBoolean(), is(a2_2.getA2_arrayArrayBoolean()));
        assertThat(a2_1.getA2_arrayArrayChar(), is(a2_2.getA2_arrayArrayChar()));
        assertThat(a2_1.getA2_arrayArrayByte(), is(a2_2.getA2_arrayArrayByte()));
        assertThat(a2_1.getA2_arrayArrayShort(), is(a2_2.getA2_arrayArrayShort()));
        assertThat(a2_1.getA2_arrayArrayInteger(), is(a2_2.getA2_arrayArrayInteger()));
        assertThat(a2_1.getA2_arrayArrayLong(), is(a2_2.getA2_arrayArrayLong()));
        assertThat(a2_1.getA2_arrayArrayFloat(), is(a2_2.getA2_arrayArrayFloat()));
        assertThat(a2_1.getA2_arrayArrayDouble(), is(a2_2.getA2_arrayArrayDouble()));
        assertThat(a2_1.getA2_arrayArrayString(), is(a2_2.getA2_arrayArrayString()));
        assertThat(a2_1.getA2_arrayArrayBigInteger(), is(a2_2.getA2_arrayArrayBigInteger()));
        assertThat(a2_1.getA2_arrayArrayBigDecimal(), is(a2_2.getA2_arrayArrayBigDecimal()));
        assertThat(a2_1.getA2_arrayArrayLocalDate(), is(a2_2.getA2_arrayArrayLocalDate()));
        assertThat(a2_1.getA2_arrayArrayLocalTime(), is(a2_2.getA2_arrayArrayLocalTime()));
        assertThat(a2_1.getA2_arrayArrayLocalDateTime(), is(a2_2.getA2_arrayArrayLocalDateTime()));
        assertThat(a2_1.getA2_arrayArrayUuid(), is(a2_2.getA2_arrayArrayUuid()));
        assertThat(a2_1.getA2_arrayArrayEnm(), is(a2_2.getA2_arrayArrayEnm()));
        assertThat(a2_1.getA2_arrayArrayB1(), is(a2_2.getA2_arrayArrayB1()));
        assertThat(a2_1.getA2_arrayArrayB2(), is(a2_2.getA2_arrayArrayB2()));
        assertThat(a2_1.getA2_point(), is(a2_2.getA2_point()));
        assertThat(a2_1.getA2_listPoint(), is(a2_2.getA2_listPoint()));
        assertThat(a2_1.getA2_arrayPoint(), is(a2_2.getA2_arrayPoint()));
        assertThat(a2_1.getA2_setPoint(), is(a2_2.getA2_setPoint()));
        assertThat(a2_1.getA2_mapEnmListPoint(), is(a2_2.getA2_mapEnmListPoint()));
    }


    private static void assertExampleConfigurationsB1Equal(
            ExampleConfigurationB1 b1_1,
            ExampleConfigurationB1 b1_2
    ) {
        assertThat(ExampleConfigurationB1.getB1_staticFinalInt(), is(1));
        assertThat(ExampleConfigurationB1.getB1_staticInt(), is(2));

        assertThat(b1_1.getB1_finalInt(), is(b1_2.getB1_finalInt()));
        assertThat(b1_1.getB1_transientInt(), is(b1_2.getB1_transientInt()));
        assertThat(b1_1.getB1_ignoredInt(), is(b1_2.getB1_ignoredInt()));
        assertThat(b1_1.getB1_ignoredString(), is(b1_2.getB1_ignoredString()));
        assertThat(b1_1.getB1_ignoredListString(), is(b1_2.getB1_ignoredListString()));
        assertThat(b1_1.isB1_primBool(), is(b1_2.isB1_primBool()));
        assertThat(b1_1.getB1_refChar(), is(b1_2.getB1_refChar()));
        assertThat(b1_1.getB1_string(), is(b1_2.getB1_string()));
        assertThat(b1_1.getB1_listByte(), is(b1_2.getB1_listByte()));
        assertThat(b1_1.getB1_arrayShort(), is(b1_2.getB1_arrayShort()));
        assertThat(b1_1.getB1_setInteger(), is(b1_2.getB1_setInteger()));
        assertThat(b1_1.getB1_listEmpty(), is(b1_2.getB1_listEmpty()));
        assertThat(b1_1.getB1_mapLongLong(), is(b1_2.getB1_mapLongLong()));
        assertThat(b1_1.getB1_listListByte(), is(b1_2.getB1_listListByte()));
        assertThat(b1_1.getB1_point(), is(b1_2.getB1_point()));
        assertEquals(b1_2, b1_1);
    }

    private static void assertExampleConfigurationsB2Equal(
            ExampleConfigurationB2 b2_1,
            ExampleConfigurationB2 b2_2
    ) {
        assertExampleConfigurationsB1Equal(b2_1, b2_2);

        assertThat(ExampleConfigurationB2.getB1_staticFinalInt(), is(1));
        assertThat(ExampleConfigurationB2.getB1_staticInt(), is(2));

        assertThat(b2_1.getB2_finalInt(), is(b2_2.getB2_finalInt()));
        assertThat(b2_1.getB2_transientInt(), is(b2_2.getB2_transientInt()));
        assertThat(b2_1.getB2_ignoredInt(), is(b2_2.getB2_ignoredInt()));
        assertThat(b2_1.getB2_ignoredString(), is(b2_2.getB2_ignoredString()));
        assertThat(b2_1.getB2_ignoredListString(), is(b2_2.getB2_ignoredListString()));
        assertThat(b2_1.getB2_primChar(), is(b2_2.getB2_primChar()));
        assertThat(b2_1.getB2_refBool(), is(b2_2.getB2_refBool()));
        assertThat(b2_1.getB2_bigInteger(), is(b2_2.getB2_bigInteger()));
        assertThat(b2_1.getB2_listShort(), is(b2_2.getB2_listShort()));
        assertThat(b2_1.getB2_arrayInteger(), is(b2_2.getB2_arrayInteger()));
        assertThat(b2_1.getB2_setLong(), is(b2_2.getB2_setLong()));
        assertThat(b2_1.getB2_arrayEmpty(), is(b2_2.getB2_arrayEmpty()));
        assertThat(b2_1.getB2_mapFloatFloat(), is(b2_2.getB2_mapFloatFloat()));
        assertDeepEquals(b2_1.getB2_setArrayDouble(), b2_2.getB2_setArrayDouble(), LinkedHashSet::new);
        assertThat(b2_1.getB2_listPoint(), is(b2_2.getB2_listPoint()));
        assertEquals(b2_2, b2_1);
    }

    private static <T, C extends Collection<T[]>> void assertDeepEquals(
            C collection1,
            C collection2,
            Supplier<Collection<List<T>>> collectionFactory
    ) {
        Collection<List<T>> c1 = collection1.stream().map(Arrays::asList)
                .collect(Collectors.toCollection(collectionFactory));
        Collection<List<T>> c2 = collection2.stream().map(Arrays::asList)
                .collect(Collectors.toCollection(collectionFactory));
        assertEquals(c2, c1);
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
