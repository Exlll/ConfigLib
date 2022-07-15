package de.exlll.configlib;

import de.exlll.configlib.Serializers.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.Point;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static de.exlll.configlib.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SerializerSelectorTest {
    private static final SerializerSelector SELECTOR = new SerializerSelector(
            ConfigurationProperties.newBuilder().build()
    );

    private static Type getGenericType(Class<?> cls, String fieldName) {
        Field ls = getField(cls, fieldName);
        return ls.getGenericType();
    }

    @ParameterizedTest
    @ValueSource(classes = {boolean.class, Boolean.class})
    void selectSerializerBoolean(Class<?> cls) {
        Serializer<?, ?> serializer = SELECTOR.select(cls);
        assertThat(serializer, instanceOf(BooleanSerializer.class));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            byte.class, Byte.class, short.class, Short.class,
            int.class, Integer.class, long.class, Long.class,
            float.class, Float.class, double.class, Double.class
    })
    void selectSerializerNumber(Class<?> cls) {
        NumberSerializer serializer = (NumberSerializer) SELECTOR.select(cls);
        assertThat(serializer.getNumberClass(), equalTo(cls));
    }

    @ParameterizedTest
    @ValueSource(classes = {char.class, Character.class})
    void selectSerializerChar(Class<?> cls) {
        Serializer<?, ?> serializer = SELECTOR.select(cls);
        assertThat(serializer, instanceOf(CharacterSerializer.class));
    }

    @Test
    void selectSerializerString() {
        Serializer<?, ?> serializer = SELECTOR.select(String.class);
        assertThat(serializer, instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerBigInteger() {
        Serializer<?, ?> serializer = SELECTOR.select(BigInteger.class);
        assertThat(serializer, instanceOf(BigIntegerSerializer.class));
    }

    @Test
    void selectSerializerBigDecimal() {
        Serializer<?, ?> serializer = SELECTOR.select(BigDecimal.class);
        assertThat(serializer, instanceOf(BigDecimalSerializer.class));
    }

    @Test
    void selectSerializerLocalDate() {
        Serializer<?, ?> serializer = SELECTOR.select(LocalDate.class);
        assertThat(serializer, instanceOf(LocalDateSerializer.class));
    }

    @Test
    void selectSerializerLocalTime() {
        Serializer<?, ?> serializer = SELECTOR.select(LocalTime.class);
        assertThat(serializer, instanceOf(LocalTimeSerializer.class));
    }

    @Test
    void selectSerializerLocalDateTime() {
        Serializer<?, ?> serializer = SELECTOR.select(LocalDateTime.class);
        assertThat(serializer, instanceOf(LocalDateTimeSerializer.class));
    }

    @Test
    void selectSerializerInstant() {
        Serializer<?, ?> serializer = SELECTOR.select(Instant.class);
        assertThat(serializer, instanceOf(InstantSerializer.class));
    }

    @Test
    void selectSerializerUuid() {
        Serializer<?, ?> serializer = SELECTOR.select(UUID.class);
        assertThat(serializer, instanceOf(UuidSerializer.class));
    }

    @Test
    void selectSerializerFile() {
        Serializer<?, ?> serializer = SELECTOR.select(File.class);
        assertThat(serializer, instanceOf(FileSerializer.class));
    }

    @Test
    void selectSerializerPath() {
        Serializer<?, ?> serializer = SELECTOR.select(Path.class);
        assertThat(serializer, instanceOf(PathSerializer.class));
    }

    @Test
    void selectSerializerUrl() {
        Serializer<?, ?> serializer = SELECTOR.select(URL.class);
        assertThat(serializer, instanceOf(UrlSerializer.class));
    }

    @Test
    void selectSerializerUri() {
        Serializer<?, ?> serializer = SELECTOR.select(URI.class);
        assertThat(serializer, instanceOf(UriSerializer.class));
    }

    @Test
    void selectSerializerEnum() {
        enum E {}
        EnumSerializer serializer = (EnumSerializer) SELECTOR.select(E.class);
        assertThat(serializer.getEnumCls(), equalTo(E.class));
    }

    @Test
    void selectSerializerArray() {
        var serializer = (ArraySerializer<?, ?>) SELECTOR.select(String[][].class);
        assertThat(serializer.getComponentType(), equalTo(String[].class));

        var elementSerializer = (ArraySerializer<?, ?>) serializer.getElementSerializer();
        assertThat(elementSerializer.getComponentType(), equalTo(String.class));
        assertThat(elementSerializer.getElementSerializer(), instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerPrimitiveBooleanArray() {
        Serializer<?, ?> serializer = SELECTOR.select(boolean[].class);
        assertThat(serializer, instanceOf(PrimitiveBooleanArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveCharacterArray() {
        Serializer<?, ?> serializer = SELECTOR.select(char[].class);
        assertThat(serializer, instanceOf(PrimitiveCharacterArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveByteArray() {
        Serializer<?, ?> serializer = SELECTOR.select(byte[].class);
        assertThat(serializer, instanceOf(PrimitiveByteArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveShortArray() {
        Serializer<?, ?> serializer = SELECTOR.select(short[].class);
        assertThat(serializer, instanceOf(PrimitiveShortArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveIntegerArray() {
        Serializer<?, ?> serializer = SELECTOR.select(int[].class);
        assertThat(serializer, instanceOf(PrimitiveIntegerArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveLongArray() {
        Serializer<?, ?> serializer = SELECTOR.select(long[].class);
        assertThat(serializer, instanceOf(PrimitiveLongArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveFloatArray() {
        Serializer<?, ?> serializer = SELECTOR.select(float[].class);
        assertThat(serializer, instanceOf(PrimitiveFloatArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveDoubleArray() {
        Serializer<?, ?> serializer = SELECTOR.select(double[].class);
        assertThat(serializer, instanceOf(PrimitiveDoubleArraySerializer.class));
    }

    @Test
    void selectSerializerConfiguration() {
        @Configuration
        class A<T> {
            int i;
        }
        var serializer = (ConfigurationSerializer<?>) SELECTOR.select(A.class);
        assertThat(serializer.getConfigurationType(), equalTo(A.class));
    }

    @Test
    void selectSerializerRecord() {
        record R(int i) {}
        var serializer = (RecordSerializer<?>) SELECTOR.select(R.class);
        assertThat(serializer.getRecordType(), equalTo(R.class));
    }

    @Test
    void recordSerializerTakesPrecedenceOverConfigurationSerializer() {
        @Configuration
        record R(int i) {}
        var serializer = (RecordSerializer<?>) SELECTOR.select(R.class);
        assertThat(serializer.getRecordType(), equalTo(R.class));
    }

    @Test
    void selectSerializerMissingType() {
        assertThrowsConfigurationException(
                () -> SELECTOR.select(Object.class),
                "Missing serializer for type class java.lang.Object.\nEither annotate the type with " +
                "@Configuration or provide a custom serializer by adding it to the properties."
        );
    }

    @Test
    void selectSerializerByCustomType() {
        var properties = ConfigurationProperties.newBuilder()
                .addSerializer(Point.class, POINT_SERIALIZER)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var pointSerializer = selector.select(Point.class);
        assertThat(pointSerializer, sameInstance(POINT_SERIALIZER));
    }

    @Test
    void selectSerializerByCustomTypeTakesPrecedence() {
        var properties = ConfigurationProperties.newBuilder()
                .addSerializer(BigInteger.class, CUSTOM_BIG_INTEGER_SERIALIZER)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var bigIntegerSerializer = selector.select(BigInteger.class);
        assertThat(bigIntegerSerializer, instanceOf(TestUtils.CustomBigIntegerSerializer.class));
        assertThat(bigIntegerSerializer, sameInstance(CUSTOM_BIG_INTEGER_SERIALIZER));
    }

    @Test
    void selectSerializerByCondition() {
        var properties = ConfigurationProperties.newBuilder()
                .addSerializerByCondition(t -> t == Point.class, POINT_SERIALIZER)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var pointSerializer = selector.select(Point.class);
        assertThat(pointSerializer, sameInstance(POINT_SERIALIZER));
    }

    @Test
    void selectSerializerByConditionTakesPrecedence() {
        var properties = ConfigurationProperties.newBuilder()
                .addSerializerByCondition(t -> t == BigInteger.class, CUSTOM_BIG_INTEGER_SERIALIZER)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var bigIntegerSerializer = selector.select(BigInteger.class);
        assertThat(bigIntegerSerializer, instanceOf(TestUtils.CustomBigIntegerSerializer.class));
        assertThat(bigIntegerSerializer, sameInstance(CUSTOM_BIG_INTEGER_SERIALIZER));
    }

    @Test
    void selectSerializerByCustomTypeTakesPrecedenceOverCustomType() {
        var serializer1 = IdentifiableSerializer.of(1);
        var serializer2 = IdentifiableSerializer.of(2);
        var properties = ConfigurationProperties.newBuilder()
                .addSerializerByCondition(t -> t == int.class, serializer1)
                .addSerializer(int.class, serializer2)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var serializer = selector.select(int.class);
        assertThat(serializer, instanceOf(IdentifiableSerializer.class));
        assertThat(serializer, sameInstance(serializer2));
    }

    @Test
    void selectSerializerList() {
        class A {
            List<String> ls;
        }
        var serializer = (ListSerializer<?, ?>) SELECTOR.select(getGenericType(A.class, "ls"));
        assertThat(serializer.getElementSerializer(), instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerListNested() {
        class A {
            List<List<String>> lls;
        }
        var serializer = (ListSerializer<?, ?>) SELECTOR.select(getGenericType(A.class, "lls"));
        var elementSerializer = (ListSerializer<?, ?>) serializer.getElementSerializer();
        assertThat(elementSerializer.getElementSerializer(), instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerSetsAsSets() {
        class A {
            Set<String> ss;
        }
        SerializerSelector selector = new SerializerSelector(
                ConfigurationProperties.newBuilder().serializeSetsAsLists(false).build()
        );
        var serializer = (SetSerializer<?, ?>) selector.select(getGenericType(A.class, "ss"));
        assertThat(serializer.getElementSerializer(), instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerSetsAsLists() {
        class A {
            Set<String> ss;
        }
        var serializer = (SetAsListSerializer<?, ?>) SELECTOR.select(getGenericType(A.class, "ss"));
        assertThat(serializer.getElementSerializer(), instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerMap() {
        class A {
            Map<Integer, String> mis;
        }
        var serializer = (MapSerializer<?, ?, ?, ?>) SELECTOR.select(getGenericType(A.class, "mis"));
        var numberSerializer = (NumberSerializer) serializer.getKeySerializer();
        assertThat(numberSerializer.getNumberClass(), equalTo(Integer.class));
        assertThat(serializer.getValueSerializer(), instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerMapNested() {
        enum E {}
        class A {
            Map<E, Set<List<E>>> mesle;
        }
        var serializer = (MapSerializer<?, ?, ?, ?>) SELECTOR.select(getGenericType(A.class, "mesle"));

        var keySerializer = (EnumSerializer) serializer.getKeySerializer();
        assertThat(keySerializer.getEnumCls(), equalTo(E.class));

        var valSerializer = (SetAsListSerializer<?, ?>) serializer.getValueSerializer();
        var listSerializer = (ListSerializer<?, ?>) valSerializer.getElementSerializer();
        var enumSerializer = (EnumSerializer) listSerializer.getElementSerializer();
        assertThat(enumSerializer.getEnumCls(), equalTo(E.class));
    }

    @Test
    void selectSerializerMapInvalidKeyType1() {
        class A {
            Map<List<String>, String> mlss;
        }
        Type type = getGenericType(A.class, "mlss");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(type),
                "Cannot select serializer for type '" + type + "'.\nMap keys can only be " +
                "of simple or enum type."
        );
    }

    @Test
    void selectSerializerMapInvalidKeyType2() {
        class A {
            Map<Point, String> mps;
        }
        Type type = getGenericType(A.class, "mps");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(type),
                "Cannot select serializer for type '" + type + "'.\nMap keys can only be " +
                "of simple or enum type."
        );
    }

    @Test
    void selectSerializerOtherParameterizedType() {
        class Box<T> {}
        class A {
            Box<String> box;
        }
        Type type = getGenericType(A.class, "box");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(type),
                "Cannot select serializer for type '" + type + "'.\nParameterized " +
                "types other than lists, sets, and maps cannot be serialized."
        );
    }

    @Test
    void selectSerializerGenericArrayType() {
        class A {
            List<?>[] ga;
        }
        Type type = getGenericType(A.class, "ga");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(type),
                "Cannot select serializer for type 'java.util.List<?>[]'.\n" +
                "Generic array types cannot be serialized."
        );
    }

    @Test
    void selectSerializerBoundedWildcardType() {
        class A {
            List<? extends String> les;
        }
        ParameterizedType ptype = (ParameterizedType) getGenericType(A.class, "les");
        Type type = ptype.getActualTypeArguments()[0];
        assertThrowsConfigurationException(
                () -> SELECTOR.select(type),
                "Cannot select serializer for type '? extends java.lang.String'.\n" +
                "Wildcard types cannot be serialized."
        );
    }

    @Test
    void selectSerializerWildcardType() {
        class A {
            List<?> lw;
        }
        ParameterizedType ptype = (ParameterizedType) getGenericType(A.class, "lw");
        Type type = ptype.getActualTypeArguments()[0];
        assertThrowsConfigurationException(
                () -> SELECTOR.select(type),
                "Cannot select serializer for type '?'.\n" +
                "Wildcard types cannot be serialized."
        );
    }

    @Test
    void selectSerializerTypeVariable() {
        class A<T> {
            T t;
        }
        Type type = getGenericType(A.class, "t");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(type),
                "Cannot select serializer for type 'T'.\n" +
                "Type variables cannot be serialized."
        );
    }
}