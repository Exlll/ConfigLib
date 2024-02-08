package de.exlll.configlib;

import de.exlll.configlib.ConfigurationProperties.Builder;
import de.exlll.configlib.configurations.ExampleConfigurationB1;
import de.exlll.configlib.configurations.ExampleConfigurationB2;
import de.exlll.configlib.configurations.ExampleEnum;
import de.exlll.configlib.configurations.ExampleInitializer;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static de.exlll.configlib.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("FieldMayBeFinal")
class ConfigurationSerializerTest {
    private static <T> ConfigurationSerializer<T> newSerializer(Class<T> cls) {
        return newSerializer(cls, builder -> {});
    }

    private static <T> ConfigurationSerializer<T> newSerializer(
            Class<T> cls,
            Consumer<Builder<?>> propertiesConfigurer
    ) {
        var builder = ConfigurationProperties.newBuilder();
        builder.addSerializer(Point.class, TestUtils.POINT_SERIALIZER);
        propertiesConfigurer.accept(builder);
        return new ConfigurationSerializer<>(cls, builder.build());
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

    @Configuration
    static final class A {
        int value1 = 1;
        int someValue2 = 2;
    }

    @Test
    void serializeAppliesFormatter() {
        ConfigurationSerializer<A> serializer = newSerializer(
                A.class,
                builder -> builder.setNameFormatter(NameFormatters.UPPER_UNDERSCORE)
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
                builder -> builder.setNameFormatter(NameFormatters.UPPER_UNDERSCORE)
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
                    "Cannot set field '" + getField(B2.class, fieldName) + "' to null value. " +
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
                "Deserialization of value '3' with type 'class java.lang.Byte' for field " +
                "'java.lang.String de.exlll.configlib.ConfigurationSerializerTest$B3.s' " +
                "failed.\nThe type of the object to be deserialized does not match the type " +
                "the deserializer expects."
        );
        assertThrowsConfigurationException(
                () -> serializer.deserialize(Map.of("l", List.of(List.of(3)))),
                "Deserialization of value '[[3]]' with type 'class " +
                "java.util.ImmutableCollections$List12' for field 'java.util.List " +
                "de.exlll.configlib.ConfigurationSerializerTest$B3.l' failed.\n" +
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

    @Configuration
    static final class B14 {
        private int i1;
        private int i2 = 10;
        private String s1;
        private String s2 = null;
        private String s3 = "s3";
    }

    @Test
    void getDefaultValueOf() {
        final var serializer = newSerializer(B14.class);

        assertThat(serializer.getDefaultValueOf(fieldElementFor(B14.class, "i1")), is(0));
        assertThat(serializer.getDefaultValueOf(fieldElementFor(B14.class, "i2")), is(10));
        assertThat(serializer.getDefaultValueOf(fieldElementFor(B14.class, "s1")), nullValue());
        assertThat(serializer.getDefaultValueOf(fieldElementFor(B14.class, "s2")), nullValue());
        assertThat(serializer.getDefaultValueOf(fieldElementFor(B14.class, "s3")), is("s3"));
    }

    private static ConfigurationElements.FieldElement fieldElementFor(Class<?> type, String fieldName) {
        try {
            final Field field = type.getDeclaredField(fieldName);
            return new ConfigurationElements.FieldElement(field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Configuration
    static final class B9 {
        int i;

        @PostProcess
        private B9 postProcess() {
            B9 b = new B9();
            b.i = i + 20;
            return b;
        }
    }

    @Configuration
    static final class B10 {
        int i;

        @PostProcess
        private void postProcess() {
            i += 20;
        }
    }

    @Test
    void postProcessorIsAppliedInClassDeserializer() {
        B9 b9 = newSerializer(B9.class).deserialize(Map.of(
                "i", 50
        ));
        assertThat(b9.i, is(70));


        B10 b10 = newSerializer(B10.class).deserialize(Map.of(
                "i", 10
        ));
        assertThat(b10.i, is(30));
    }


    @Configuration
    static class B11 {
        int k;

        @PostProcess
        void postProcess() {
            k = k * 4;
        }
    }

    @Configuration
    static class B12 {
        int j;
        B11 b11;

        @PostProcess
        void postProcess() {
            j = j * 3;
            b11.k += 1;
        }
    }

    @Configuration
    static class B13 {
        int i;
        B12 b12;

        @PostProcess
        void postProcess() {
            i = i * 2;
            b12.j += 1;
            b12.b11.k *= 2;
        }
    }

    @Test
    void postProcessNestedClasses() {
        B13 b13 = newSerializer(B13.class).deserialize(Map.of(
                "i", 1,
                "b12", Map.of(
                        "j", 2,
                        "b11", Map.of("k", 3)
                )
        ));

        assertThat(b13.i, is(2));
        assertThat(b13.b12.j, is(7));
        assertThat(b13.b12.b11.k, is(26));
    }

    @Configuration
    static final class PP_1 {
        @PostProcess(key = "key1")
        private int a1 = 10;
        @PostProcess(key = "key1")
        private int a2 = 20;

        @PostProcess(key = "key2")
        private int b1 = 10;
        @PostProcess(key = "key2")
        private int b2 = 20;

        @PostProcess
        private int c1 = 10;
        @PostProcess
        private int c2 = 20;

        private int d1 = 10;
        private int d2 = 20;
    }

    @Test
    void postProcessFieldByKey1() {
        final var serializer = newSerializer(
                PP_1.class,
                builder -> builder.addPostProcessor(
                        ConfigurationElementFilter.byPostProcessKey("key1"),
                        (Integer x) -> x * 2
                )
        );
        PP_1 deserialized = serializer.deserialize(Map.of(
                "a1", 10, "a2", 20,
                "b1", 10, "b2", 20,
                "c1", 10, "c2", 20,
                "d1", 10, "d2", 20
        ));
        assertThat(deserialized.a1, is(20));
        assertThat(deserialized.a2, is(40));
        assertThat(deserialized.b1, is(10));
        assertThat(deserialized.b2, is(20));
        assertThat(deserialized.c1, is(10));
        assertThat(deserialized.c2, is(20));
        assertThat(deserialized.d1, is(10));
        assertThat(deserialized.d2, is(20));
    }

    @Test
    void postProcessFieldByEmptyKey() {
        final var serializer = newSerializer(
                PP_1.class,
                builder -> builder.addPostProcessor(
                        ConfigurationElementFilter.byPostProcessKey(""),
                        (Integer x) -> x * 2
                )
        );
        PP_1 deserialized = serializer.deserialize(Map.of(
                "a1", 10, "a2", 20,
                "b1", 10, "b2", 20,
                "c1", 10, "c2", 20,
                "d1", 10, "d2", 20
        ));
        assertThat(deserialized.a1, is(10));
        assertThat(deserialized.a2, is(20));
        assertThat(deserialized.b1, is(10));
        assertThat(deserialized.b2, is(20));
        assertThat(deserialized.c1, is(20));
        assertThat(deserialized.c2, is(40));
        assertThat(deserialized.d1, is(10));
        assertThat(deserialized.d2, is(20));
    }

    @Configuration
    static final class PP_2 {
        private PP_1 pp1_1 = new PP_1();
        @PostProcess(key = "key3")
        private PP_1 pp1_2 = new PP_1();
    }

    @Test
    void postProcessNestedFieldByKey2And3() {
        final var serializer = newSerializer(
                PP_2.class,
                builder -> builder
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey("key2"),
                                (Integer x) -> x * 2
                        )
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey("key3"),
                                (PP_1 pp1) -> {
                                    pp1.a1 *= 10;
                                    pp1.a2 *= 10;
                                    pp1.b1 *= 10;
                                    pp1.b2 *= 10;
                                    pp1.c1 *= 10;
                                    pp1.c2 *= 10;
                                    pp1.d1 *= 10;
                                    pp1.d2 *= 10;
                                    return pp1;
                                }
                        )
        );
        PP_2 deserialized = serializer.deserialize(Map.of(
                "pp1_1", Map.of(
                        "a1", 10, "a2", 20,
                        "b1", 10, "b2", 20,
                        "c1", 10, "c2", 20,
                        "d1", 10, "d2", 20
                ),
                "pp1_2", Map.of(
                        "a1", 10, "a2", 20,
                        "b1", 10, "b2", 20,
                        "c1", 10, "c2", 20,
                        "d1", 10, "d2", 20
                )
        ));
        assertThat(deserialized.pp1_1.a1, is(10));
        assertThat(deserialized.pp1_1.a2, is(20));
        assertThat(deserialized.pp1_1.b1, is(20));
        assertThat(deserialized.pp1_1.b2, is(40));
        assertThat(deserialized.pp1_1.c1, is(10));
        assertThat(deserialized.pp1_1.c2, is(20));
        assertThat(deserialized.pp1_1.d1, is(10));
        assertThat(deserialized.pp1_1.d2, is(20));

        assertThat(deserialized.pp1_2.a1, is(100));
        assertThat(deserialized.pp1_2.a2, is(200));
        assertThat(deserialized.pp1_2.b1, is(200));
        assertThat(deserialized.pp1_2.b2, is(400));
        assertThat(deserialized.pp1_2.c1, is(100));
        assertThat(deserialized.pp1_2.c2, is(200));
        assertThat(deserialized.pp1_2.d1, is(100));
        assertThat(deserialized.pp1_2.d2, is(200));
    }

    @Test
    void postProcessFieldDefaultValueIfSerializationMissing() {
        final var serializer = newSerializer(
                PP_1.class,
                builder -> builder
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey("key1"),
                                (Integer x) -> x * 2
                        )
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey("key2"),
                                (Integer x) -> x * 5
                        )
        );
        PP_1 deserialized = serializer.deserialize(Map.of(
                "a1", 700,
                "b1", 800
        ));
        assertThat(deserialized.a1, is(1400));
        assertThat(deserialized.a2, is(40));
        assertThat(deserialized.b1, is(4000));
        assertThat(deserialized.b2, is(100));
        assertThat(deserialized.c1, is(10));
        assertThat(deserialized.c2, is(20));
        assertThat(deserialized.d1, is(10));
        assertThat(deserialized.d2, is(20));
    }

    @Configuration
    static final class PP_3 {
        @PostProcess
        private int i;

        @PostProcess
        private void postProcess() {
            i++;
        }
    }

    @Test
    void postProcessMethodAppliedAfterPostProcessAnnotation() {
        final var serializer = newSerializer(
                PP_3.class,
                builder -> builder.addPostProcessor(
                        ConfigurationElementFilter.byPostProcessKey(""),
                        (Integer x) -> x * 2
                )
        );
        PP_3 deserialized = serializer.deserialize(Map.of("i", 10));
        assertThat(deserialized.i, is(21));
    }

    @Configuration
    static final class PP_Ignored {
        @PostProcess(key = "key1")
        @Ignore
        private int a1 = 10;
        @PostProcess(key = "key1")
        @Ignore
        private int a2 = 20;

        @PostProcess(key = "key2")
        private final int b1 = 10;
        @PostProcess(key = "key2")
        private final int b2 = 20;

        @PostProcess
        private transient int c1 = 10;
        @PostProcess
        private transient int c2 = 20;

        private int d1 = 10;
        private int d2 = 20;
    }

    @Test
    void ignoredFieldsAreNotPostProcessed() {
        final var serializer = newSerializer(
                PP_Ignored.class,
                builder -> builder
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey("key1"),
                                (Integer x) -> x * 2
                        )
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey("key2"),
                                (Integer x) -> x * 5
                        )
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey(""),
                                (Integer x) -> x * 7
                        )
        );
        PP_Ignored deserialized = serializer.deserialize(Map.of(
                "a1", 10, "a2", 20,
                "b1", 10, "b2", 20,
                "c1", 10, "c2", 20,
                "d1", 10, "d2", 20
        ));
        assertThat(deserialized.a1, is(10));
        assertThat(deserialized.a2, is(20));
        assertThat(deserialized.b1, is(10));
        assertThat(deserialized.b2, is(20));
        assertThat(deserialized.c1, is(10));
        assertThat(deserialized.c2, is(20));
        assertThat(deserialized.d1, is(10));
        assertThat(deserialized.d2, is(20));
    }

    @Configuration
    static final class PP_Null {
        @PostProcess(key = "integer")
        private Integer i1;
        @PostProcess(key = "integer")
        private Integer i2;
        @PostProcess(key = "integer")
        private Integer i3 = 1;
        private Integer i4;
        @PostProcess(key = "string")
        private String s1;
        @PostProcess(key = "string")
        private String s2;
        @PostProcess(key = "string")
        private String s3 = "a";
        private String s4;
    }

    @Test
    void postProcessFieldsThatAreAssignedNullValues() {
        final var serializer = newSerializer(
                PP_Null.class,
                builder -> builder
                        .inputNulls(true)
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey("integer"),
                                (Integer x) -> (x == null) ? -1 : x * 2
                        )
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey("string"),
                                (String s) -> (s == null) ? "empty" : s.repeat(2)
                        )
        );
        PP_Null deserialized = serializer.deserialize(TestUtils.asMap(
                "i1", null, "i4", null,
                "s1", null, "s4", null

        ));
        assertThat(deserialized.i1, is(-1));
        assertThat(deserialized.i2, is(-1));
        assertThat(deserialized.i3, is(2));
        assertThat(deserialized.i4, nullValue());
        assertThat(deserialized.s1, is("empty"));
        assertThat(deserialized.s2, is("empty"));
        assertThat(deserialized.s3, is("aa"));
        assertThat(deserialized.s4, nullValue());
    }

    @Configuration
    static final class PP_Null_2 {
        @PostProcess(key = "integer")
        private Integer i1 = 1;
        @PostProcess(key = "integer")
        private Integer i2 = 2;
        @PostProcess(key = "integer")
        private Integer i3 = null;
        @PostProcess(key = "integer")
        private Integer i4 = null;
        @PostProcess(key = "string")
        private String s1 = "a";
        @PostProcess(key = "string")
        private String s2 = "b";
        @PostProcess(key = "string")
        private String s3 = null;
        @PostProcess(key = "string")
        private String s4 = null;
    }

    @Test
    void postProcessSerializedNullValuesWithInputNullsBeingFalse() {
        final var serializer = newSerializer(
                PP_Null_2.class,
                builder -> builder
                        .inputNulls(false)
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey("integer"),
                                (Integer x) -> (x == null) ? -1 : x * 2
                        )
                        .addPostProcessor(
                                ConfigurationElementFilter.byPostProcessKey("string"),
                                (String s) -> (s == null) ? "empty" : s.repeat(2)
                        )
        );
        PP_Null_2 deserialized = serializer.deserialize(TestUtils.asMap(
                "i1", null, "i3", null,
                "s1", null, "s3", null

        ));
        assertThat(deserialized.i1, is(2));
        assertThat(deserialized.i2, is(4));
        assertThat(deserialized.i3, is(-1));
        assertThat(deserialized.i4, is(-1));
        assertThat(deserialized.s1, is("aa"));
        assertThat(deserialized.s2, is("bb"));
        assertThat(deserialized.s3, is("empty"));
        assertThat(deserialized.s4, is("empty"));
    }
}