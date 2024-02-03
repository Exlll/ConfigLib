package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static de.exlll.configlib.TestUtils.asMap;
import static de.exlll.configlib.TestUtils.assertThrowsConfigurationException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordSerializerTest {
    private static <R extends Record> RecordSerializer<R> newSerializer(Class<R> cls) {
        return newSerializer(cls, builder -> {});
    }

    private static <R extends Record> RecordSerializer<R> newSerializer(
            Class<R> cls,
            Consumer<ConfigurationProperties.Builder<?>> propertiesConfigurer
    ) {
        var builder = ConfigurationProperties.newBuilder();
        builder.addSerializer(Point.class, TestUtils.POINT_SERIALIZER);
        propertiesConfigurer.accept(builder);
        return new RecordSerializer<>(cls, builder.build());
    }

    @Test
    void ctorRequiresRecordWithComponents() {
        record Empty() {}

        assertThrowsConfigurationException(
                () -> newSerializer(Empty.class),
                "Record type 'Empty' does not define any components."
        );
    }

    record R1(int value1, int someValue2) {}

    @Test
    void serializeAppliesFormatter() {
        RecordSerializer<R1> serializer = newSerializer(
                R1.class,
                builder -> builder.setNameFormatter(NameFormatters.UPPER_UNDERSCORE)
        );
        Map<?, ?> map = serializer.serialize(new R1(1, 2));
        assertThat(map.remove("VALUE1"), is(1L));
        assertThat(map.remove("SOME_VALUE2"), is(2L));
        assertTrue(map.isEmpty());
    }

    @Test
    void deserializeAppliesFormatter() {
        RecordSerializer<R1> serializer = newSerializer(
                R1.class,
                builder -> builder.setNameFormatter(NameFormatters.UPPER_UNDERSCORE)
        );
        Map<String, ?> map = Map.of(
                "value1", 3,
                "someValue2", 4,
                "VALUE1", 5,
                "SOME_VALUE2", 6
        );
        R1 r1 = serializer.deserialize(map);
        assertThat(r1.value1, is(5));
        assertThat(r1.someValue2, is(6));
    }

    @Test
    void serializeOutputNullsTrue() {
        record R(Integer integer) {}
        RecordSerializer<R> serializer = newSerializer(R.class, b -> b.outputNulls(true));
        Map<?, ?> serialize = serializer.serialize(new R(null));
        assertThat(serialize, is(asMap("integer", null)));
    }

    @Test
    void serializeOutputNullsFalse() {
        record R(Integer integer) {}
        RecordSerializer<R> serializer = newSerializer(R.class, b -> b.outputNulls(false));
        Map<?, ?> serialize = serializer.serialize(new R(null));
        assertThat(serialize.entrySet(), empty());
    }

    record R2(
            boolean f1,
            char f2,
            byte f3,
            short f4,
            int f5,
            long f6,
            float f7,
            double f8,
            Integer f9,
            R1 f10
    ) {}

    @Test
    void deserializeMissingValuesAsDefaultValues() {
        RecordSerializer<R2> serializer = newSerializer(R2.class);
        R2 r2 = serializer.deserialize(Map.of());

        assertThat(r2.f1, is(false));
        assertThat(r2.f2, is('\0'));
        assertThat(r2.f3, is((byte) 0));
        assertThat(r2.f4, is((short) 0));
        assertThat(r2.f5, is(0));
        assertThat(r2.f6, is(0L));
        assertThat(r2.f7, is(0f));
        assertThat(r2.f8, is(0d));
        assertThat(r2.f9, nullValue());
        assertThat(r2.f10, nullValue());
    }

    @Test
    void deserializeNullValuesAsDefaultValuesIfInputNullsIsFalse() {
        RecordSerializer<R2> serializer = newSerializer(R2.class, b -> b.inputNulls(false));

        Map<String, Object> serialized = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            serialized.put("f" + i, null);
        }

        R2 r2 = serializer.deserialize(serialized);

        assertThat(r2.f1, is(false));
        assertThat(r2.f2, is('\0'));
        assertThat(r2.f3, is((byte) 0));
        assertThat(r2.f4, is((short) 0));
        assertThat(r2.f5, is(0));
        assertThat(r2.f6, is(0L));
        assertThat(r2.f7, is(0f));
        assertThat(r2.f8, is(0d));
        assertThat(r2.f9, nullValue());
        assertThat(r2.f10, nullValue());
    }

    @Test
    void deserializeNullValuesAsNullIfInputNullsIsTrue() {
        record R(Integer i, String s, R1 r1) {}
        RecordSerializer<R> serializer = newSerializer(R.class, b -> b.inputNulls(true));

        Map<String, Object> serialized = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            serialized.put("f" + i, null);
        }

        R r = serializer.deserialize(serialized);

        assertThat(r.i, nullValue());
        assertThat(r.s, nullValue());
        assertThat(r.r1, nullValue());
    }

    @Test
    void deserializeNullValuesAsNullIfInputNullsIsTrueFailsForPrimitiveFields() {
        RecordSerializer<R2> serializer = newSerializer(R2.class, builder -> builder.inputNulls(true));
        RecordComponent[] components = R2.class.getRecordComponents();

        Map<String, Object> serialized = new LinkedHashMap<>();

        // initialize map to be deserialized with default values
        for (int i = 1; i <= 10; i++) {
            RecordComponent component = components[i - 1];
            Class<?> componentType = component.getType();
            Object value = Reflect.getDefaultValue(componentType);
            // CharacterSerializer expects String
            serialized.put(component.getName(), componentType == char.class ? "\0" : value);
        }

        for (int i = 1; i <= 8; i++) {
            RecordComponent component = components[i - 1];
            Class<?> componentType = component.getType();
            Object tmp = serialized.remove(component.getName());
            serialized.put(component.getName(), null);
            assertThrowsConfigurationException(
                    () -> serializer.deserialize(serialized),
                    ("Cannot set component '%s %s' of record type " +
                     "'class de.exlll.configlib.RecordSerializerTest$R2' to null. " +
                     "Primitive types cannot be assigned null values.")
                            .formatted(componentType.getSimpleName(), component.getName())

            );
            serialized.put(component.getName(), tmp);
        }
    }

    @Test
    void deserializeInvalidType() {
        record B3(String s, List<List<String>> l) {}
        RecordSerializer<B3> serializer = newSerializer(B3.class);
        assertThrowsConfigurationException(
                () -> serializer.deserialize(Map.of("s", (byte) 3)),
                "Deserialization of value '3' with type 'class java.lang.Byte' for component " +
                "'java.lang.String s' of record 'class de.exlll.configlib.RecordSerializerTest$1B3' " +
                "failed.\nThe type of the object to be deserialized does not match the type " +
                "the deserializer expects."
        );
        assertThrowsConfigurationException(
                () -> serializer.deserialize(Map.of("l", List.of(List.of(3)))),
                "Deserialization of value '[[3]]' with type " +
                "'class java.util.ImmutableCollections$List12' for component " +
                "'java.util.List l' of record 'class de.exlll.configlib.RecordSerializerTest$1B3' " +
                "failed.\nThe type of the object to be deserialized does not match the type " +
                "the deserializer expects."
        );
    }

    @Test
    void newDefaultInstanceWithoutDefaultConstructor() {
        record R(int i, String s) {}
        R r = newSerializer(R.class).newDefaultInstance();
        assertThat(r.i, is(0));
        assertThat(r.s, nullValue());
    }

    @Test
    void newDefaultInstanceWithDefaultConstructor() {
        record R(int i, String s) {
            R() {this(10, "s");}
        }
        R r = newSerializer(R.class).newDefaultInstance();
        assertThat(r.i, is(10));
        assertThat(r.s, is("s"));
    }

    @Test
    void postProcessorIsAppliedInRecordDeserializer() {
        record R(int i, String s) {
            @PostProcess
            private R postProcess() {
                return new R(i + 20, s.repeat(2));
            }
        }

        R r = newSerializer(R.class).deserialize(Map.of(
                "i", 10,
                "s", "AB"
        ));
        assertThat(r.i, is(30));
        assertThat(r.s, is("ABAB"));
    }

    @Test
    void postProcessNestedRecords() {
        record R3(int k) {
            @PostProcess
            R3 postProcess() {
                return new R3(k * 4);
            }
        }
        record R2(int j, R3 r3) {
            @PostProcess
            R2 postProcess() {
                return new R2(j * 3, new R3(r3.k + 1));
            }
        }
        record R1(int i, R2 r2) {
            @PostProcess
            R1 postProcess() {
                return new R1(i * 2, new R2(r2.j + 1, new R3(r2.r3.k * 2)));
            }
        }

        R1 r1 = newSerializer(R1.class).deserialize(Map.of(
                "i", 1,
                "r2", Map.of(
                        "j", 2,
                        "r3", Map.of("k", 3)
                )
        ));

        assertThat(r1.i, is(2));
        assertThat(r1.r2.j, is(7));
        assertThat(r1.r2.r3.k, is(26));
    }
}