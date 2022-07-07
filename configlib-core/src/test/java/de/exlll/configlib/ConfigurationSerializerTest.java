package de.exlll.configlib;

import de.exlll.configlib.ConfigurationProperties.Builder;
import de.exlll.configlib.Serializers.*;
import de.exlll.configlib.configurations.*;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static de.exlll.configlib.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("FieldMayBeFinal")
class ConfigurationSerializerTest {
    private static <T> ConfigurationSerializer<T> newSerializer(Class<T> cls) {
        return newSerializer(cls, builder -> builder);
    }

    private static <T> ConfigurationSerializer<T> newSerializer(
            Class<T> cls,
            Function<Builder<?>, Builder<?>> propertiesConfigurer
    ) {
        var builder = ConfigurationProperties.newBuilder()
                .addSerializer(Point.class, TestUtils.POINT_SERIALIZER);
        builder.addSerializer(Point.class, TestUtils.POINT_SERIALIZER);
        builder = propertiesConfigurer.apply(builder);
        return new ConfigurationSerializer<>(cls, builder.build());
    }

    @Test
    void buildSerializerMapFiltersFields() {
        Map<String, Serializer<?, ?>> serializers = newSerializer(ExampleConfigurationA2.class)
                .buildSerializerMap();

        assertThat(serializers.get("a1_staticFinalInt"), nullValue());
        assertThat(serializers.get("a1_staticInt"), nullValue());
        assertThat(serializers.get("a1_finalInt"), nullValue());
        assertThat(serializers.get("a1_transientInt"), nullValue());
        assertThat(serializers.get("a1_ignoredInt"), nullValue());
        assertThat(serializers.get("a1_ignoredString"), nullValue());
        assertThat(serializers.get("a1_ignoredListString"), nullValue());

        assertThat(serializers.get("a2_staticFinalInt"), nullValue());
        assertThat(serializers.get("a2_staticInt"), nullValue());
        assertThat(serializers.get("a2_finalInt"), nullValue());
        assertThat(serializers.get("a2_transientInt"), nullValue());
        assertThat(serializers.get("a2_ignoredInt"), nullValue());
        assertThat(serializers.get("a2_ignoredString"), nullValue());
        assertThat(serializers.get("a2_ignoredListString"), nullValue());
    }

    @Test
    void buildSerializerMapIgnoresFormatter() {
        Map<String, Serializer<?, ?>> serializers = newSerializer(
                ExampleConfigurationA2.class,
                props -> props.setFieldFormatter(FieldFormatters.UPPER_UNDERSCORE)
        ).buildSerializerMap();

        assertThat(serializers.get("A2_PRIM_BOOL"), nullValue());
        assertThat(serializers.get("a2_primBool"), instanceOf(BooleanSerializer.class));
    }

    @Test
    void buildSerializerMap() {
        Map<String, Serializer<?, ?>> serializers = newSerializer(ExampleConfigurationA2.class)
                .buildSerializerMap();
        assertThat(serializers.get("a2_primBool"), instanceOf(BooleanSerializer.class));
        assertThat(serializers.get("a2_refChar"), instanceOf(CharacterSerializer.class));
        assertThat(serializers.get("a2_string"), instanceOf(StringSerializer.class));
        assertThat(serializers.get("a2_Enm"), instanceOf(EnumSerializer.class));

        ConfigurationSerializer<?> serializerB1 =
                (ConfigurationSerializer<?>) serializers.get("a2_b1");
        ConfigurationSerializer<?> serializerB2 =
                (ConfigurationSerializer<?>) serializers.get("a2_b2");

        assertThat(serializerB1.getConfigurationType(), equalTo(ExampleConfigurationB1.class));
        assertThat(serializerB2.getConfigurationType(), equalTo(ExampleConfigurationB2.class));

        ListSerializer<?, ?> serializerList =
                (ListSerializer<?, ?>) serializers.get("a2_listByte");
        ArraySerializer<?, ?> serializerArray =
                (ArraySerializer<?, ?>) serializers.get("a2_arrayString");
        SetAsListSerializer<?, ?> serializerSet =
                (SetAsListSerializer<?, ?>) serializers.get("a2_setBigInteger");
        MapSerializer<?, ?, ?, ?> serializerMap =
                (MapSerializer<?, ?, ?, ?>) serializers.get("a2_mapLocalTimeLocalTime");

        assertThat(
                serializers.get("a2_arrayPrimDouble"),
                instanceOf(PrimitiveDoubleArraySerializer.class)
        );

        assertThat(serializerList.getElementSerializer(), instanceOf(NumberSerializer.class));
        assertThat(serializerArray.getElementSerializer(), instanceOf(StringSerializer.class));
        assertThat(serializerSet.getElementSerializer(), instanceOf(BigIntegerSerializer.class));
        assertThat(serializerMap.getKeySerializer(), instanceOf(LocalTimeSerializer.class));
        assertThat(serializerMap.getValueSerializer(), instanceOf(LocalTimeSerializer.class));

        assertThat(serializers.get("a2_point"), sameInstance(TestUtils.POINT_SERIALIZER));
    }

    @Test
    void serializeAppliesFormatter() {
        @Configuration
        class A {
            int value1 = 1;
            int someValue2 = 2;
        }
        ConfigurationSerializer<A> serializer = newSerializer(
                A.class,
                builder -> builder.setFieldFormatter(FieldFormatters.UPPER_UNDERSCORE)
        );
        Map<?, ?> map = serializer.serialize(new A());
        assertThat(map.remove("VALUE1"), is(1L));
        assertThat(map.remove("SOME_VALUE2"), is(2L));
        assertTrue(map.isEmpty());
    }

    @Configuration
    private static final class B1 {
        int value1 = 1;
        int someValue2 = 2;
    }

    @Test
    void deserializeAppliesFormatter() {
        ConfigurationSerializer<B1> serializer = newSerializer(
                B1.class,
                builder -> builder.setFieldFormatter(FieldFormatters.UPPER_UNDERSCORE)
        );
        Map<String, ?> map = Map.of(
                "value1", 3,
                "someValue2", 4,
                "VALUE1", 5,
                "SOME_VALUE2", 6
        );
        B1 a = serializer.deserialize(map);
        assertThat(a.value1, is(5));
        assertThat(a.someValue2, is(6));
    }

    @Configuration
    private static final class B2 {
        boolean f1;
        char f2;
        byte f3;
        short f4;
        int f5;
        long f6;
        float f7;
        double f8;
    }

    @Test
    void deserializeNullForPrimitiveFields() {
        ConfigurationSerializer<B2> serializer = newSerializer(
                B2.class,
                builder -> builder.inputNulls(true)
        );
        for (int i = 1; i <= 8; i++) {
            String fieldName = "f" + i;
            Map<String, Object> map = asMap(fieldName, null);
            assertThrowsConfigurationException(
                    () -> serializer.deserialize(map),
                    "Cannot set " + getField(B2.class, fieldName) + " to null value.\n" +
                    "Primitive types cannot be assigned null."
            );
        }
    }

    @Configuration
    private static final class B3 {
        String s = "";
        List<List<String>> l = List.of();
    }

    @Test
    void deserializeInvalidType() {
        ConfigurationSerializer<B3> serializer = newSerializer(B3.class);
        assertThrowsConfigurationException(
                () -> serializer.deserialize(Map.of("s", (byte) 3)),
                "Deserialization of value '3' with type class java.lang.Byte for field " +
                "java.lang.String de.exlll.configlib.ConfigurationSerializerTest$B3.s " +
                "failed.\nThe type of the object to be deserialized does not match the type " +
                "the deserializer expects."
        );
        assertThrowsConfigurationException(
                () -> serializer.deserialize(Map.of("l", List.of(List.of(3)))),
                "Deserialization of value '[[3]]' with type class " +
                "java.util.ImmutableCollections$List12 for field java.util.List " +
                "de.exlll.configlib.ConfigurationSerializerTest$B3.l failed.\n" +
                "The type of the object to be deserialized does not match the type the " +
                "deserializer expects."
        );
    }


    @Configuration
    private static final class B4 {
        private static final ExampleConfigurationB1 B4_NULL_B1 =
                ExampleInitializer.newExampleConfigurationB1_1();
        private static final List<String> B4_NULL_LIST = List.of();
        private static final Double[] B4_NULL_ARRAY = new Double[0];
        private static final Set<ExampleConfigurationB2> B4_NULL_SET = Set.of();
        private static final Map<LocalDate, BigDecimal> B4_NULL_MAP = Map.of();
        private static final Point B4_NULL_POINT = new Point(0, 0);

        Integer nullInteger = 1;
        String nullString = "";
        ExampleEnum nullEnm = ExampleEnum.A;
        ExampleConfigurationB1 nullB1 = B4_NULL_B1;
        List<String> nullList = B4_NULL_LIST;
        Double[] nullArray = B4_NULL_ARRAY;
        Set<ExampleConfigurationB2> nullSet = B4_NULL_SET;
        Map<LocalDate, BigDecimal> nullMap = B4_NULL_MAP;
        Point nullPoint = B4_NULL_POINT;
    }

    @Test
    void deserializeWithoutNullDoesNotOverrideInitializedFields() {
        final Map<String, ?> map = entriesAsMap(
                entry("nullInteger", null),
                entry("nullString", null),
                entry("nullEnm", null),
                entry("nullB1", null),
                entry("nullList", null),
                entry("nullArray", null),
                entry("nullSet", null),
                entry("nullMap", null),
                entry("nullPoint", null)
        );
        ConfigurationSerializer<B4> serializer = newSerializer(B4.class);
        B4 config = serializer.deserialize(map);

        assertEquals(1, config.nullInteger);
        assertEquals("", config.nullString);
        assertEquals(ExampleEnum.A, config.nullEnm);
        assertSame(B4.B4_NULL_B1, config.nullB1);
        assertSame(B4.B4_NULL_LIST, config.nullList);
        assertSame(B4.B4_NULL_ARRAY, config.nullArray);
        assertSame(B4.B4_NULL_SET, config.nullSet);
        assertSame(B4.B4_NULL_MAP, config.nullMap);
        assertSame(B4.B4_NULL_POINT, config.nullPoint);
    }

    @Configuration
    private static final class B5 {
        @Ignore
        private int ignored = 1;
    }

    @Test
    void ctorRequiresConfigurationWithFields() {
        assertThrowsConfigurationException(
                () -> newSerializer(B5.class),
                "Configuration class 'B5' does not contain any (de-)serializable fields."
        );
    }

    private static final class B6 {
        @Ignore
        private int ignored = 1;
    }

    @Test
    void ctorRequiresConfiguration() {
        assertThrowsConfigurationException(
                () -> newSerializer(B6.class),
                "Class 'B6' must be a configuration."
        );
    }

    @Test
    void buildSerializerMapPreventsRecursiveDefinitions() {
        assertThrowsConfigurationException(
                () -> newSerializer(R1.class),
                "Recursive type definitions are not supported."
        );
    }

    @Configuration
    static final class R1 {
        R2 r2;
    }

    @Configuration
    static final class R2 {
        R1 r1;
    }

    @Test
    void serializeTypeWithAbstractParent() {
        ConfigurationSerializer<B8> serializer = newSerializer(B8.class);
        Map<?, ?> serialize = serializer.serialize(new B8());
        B8 deserialize = serializer.deserialize(serialize);
        assertEquals(1, deserialize.i);
        assertEquals(2, deserialize.j);
    }

    @Configuration
    static abstract class B7 {
        int i = 1;
    }

    static final class B8 extends B7 {
        int j = 2;
    }
}