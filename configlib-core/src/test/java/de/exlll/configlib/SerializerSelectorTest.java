package de.exlll.configlib;

import de.exlll.configlib.Serializers.*;
import de.exlll.configlib.configurations.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.Point;
import java.io.File;
import java.lang.annotation.*;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.Field;
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
import java.util.function.Function;
import java.util.function.Predicate;

import static de.exlll.configlib.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("unused")
class SerializerSelectorTest {
    private static final ConfigurationProperties DEFAULT_PROPS =
            ConfigurationProperties.newBuilder().build();
    private static final SerializerSelector SELECTOR = new SerializerSelector(DEFAULT_PROPS);
    private static final SerializerSelector SELECTOR_POINT = new SerializerSelector(
            ConfigurationProperties.newBuilder().addSerializer(Point.class, POINT_SERIALIZER).build()
    );

    private static ConfigurationElement<?> findByCondition(Predicate<Field> condition) {
        for (Field field : ExampleConfigurationA2.class.getDeclaredFields()) {
            if (condition.test(field))
                return new ConfigurationElements.FieldElement(field);
        }
        throw new RuntimeException("missing field");
    }

    private static ConfigurationElement<?> findByType(Class<?> type) {
        return findByCondition(field -> field.getType() == type);
    }

    private static ConfigurationElement<?> findByName(String name) {
        return findByCondition(field -> field.getName().equals(name));
    }

    @ParameterizedTest
    @ValueSource(classes = {boolean.class, Boolean.class})
    void selectSerializerBoolean(Class<?> cls) {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(cls));
        assertThat(serializer, instanceOf(BooleanSerializer.class));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            byte.class, Byte.class, short.class, Short.class,
            int.class, Integer.class, long.class, Long.class,
            float.class, Float.class, double.class, Double.class
    })
    void selectSerializerNumber(Class<?> cls) {
        NumberSerializer serializer = (NumberSerializer) SELECTOR.select(findByType(cls));
        assertThat(serializer.getNumberClass(), equalTo(cls));
    }

    @ParameterizedTest
    @ValueSource(classes = {char.class, Character.class})
    void selectSerializerChar(Class<?> cls) {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(cls));
        assertThat(serializer, instanceOf(CharacterSerializer.class));
    }

    @Test
    void selectSerializerString() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(String.class));
        assertThat(serializer, instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerBigInteger() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(BigInteger.class));
        assertThat(serializer, instanceOf(BigIntegerSerializer.class));
    }

    @Test
    void selectSerializerBigDecimal() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(BigDecimal.class));
        assertThat(serializer, instanceOf(BigDecimalSerializer.class));
    }

    @Test
    void selectSerializerLocalDate() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(LocalDate.class));
        assertThat(serializer, instanceOf(LocalDateSerializer.class));
    }

    @Test
    void selectSerializerLocalTime() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(LocalTime.class));
        assertThat(serializer, instanceOf(LocalTimeSerializer.class));
    }

    @Test
    void selectSerializerLocalDateTime() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(LocalDateTime.class));
        assertThat(serializer, instanceOf(LocalDateTimeSerializer.class));
    }

    @Test
    void selectSerializerInstant() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(Instant.class));
        assertThat(serializer, instanceOf(InstantSerializer.class));
    }

    @Test
    void selectSerializerUuid() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(UUID.class));
        assertThat(serializer, instanceOf(UuidSerializer.class));
    }

    @Test
    void selectSerializerFile() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(File.class));
        assertThat(serializer, instanceOf(FileSerializer.class));
    }

    @Test
    void selectSerializerPath() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(Path.class));
        assertThat(serializer, instanceOf(PathSerializer.class));
    }

    @Test
    void selectSerializerUrl() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(URL.class));
        assertThat(serializer, instanceOf(UrlSerializer.class));
    }

    @Test
    void selectSerializerUri() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(URI.class));
        assertThat(serializer, instanceOf(UriSerializer.class));
    }

    @Test
    void selectSerializerEnum() {
        EnumSerializer serializer = (EnumSerializer) SELECTOR.select(findByType(ExampleEnum.class));
        assertThat(serializer.getEnumCls(), equalTo(ExampleEnum.class));
    }

    @Test
    void selectSerializerArray() {
        var serializer = (ArraySerializer<?, ?>) SELECTOR.select(findByType(String[][].class));
        assertThat(serializer.getComponentType(), equalTo(String[].class));

        var elementSerializer = (ArraySerializer<?, ?>) serializer.getElementSerializer();
        assertThat(elementSerializer.getComponentType(), equalTo(String.class));
        assertThat(elementSerializer.getElementSerializer(), instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerPrimitiveBooleanArray() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(boolean[].class));
        assertThat(serializer, instanceOf(PrimitiveBooleanArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveCharacterArray() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(char[].class));
        assertThat(serializer, instanceOf(PrimitiveCharacterArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveByteArray() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(byte[].class));
        assertThat(serializer, instanceOf(PrimitiveByteArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveShortArray() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(short[].class));
        assertThat(serializer, instanceOf(PrimitiveShortArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveIntegerArray() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(int[].class));
        assertThat(serializer, instanceOf(PrimitiveIntegerArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveLongArray() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(long[].class));
        assertThat(serializer, instanceOf(PrimitiveLongArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveFloatArray() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(float[].class));
        assertThat(serializer, instanceOf(PrimitiveFloatArraySerializer.class));
    }

    @Test
    void selectSerializerPrimitiveDoubleArray() {
        Serializer<?, ?> serializer = SELECTOR.select(findByType(double[].class));
        assertThat(serializer, instanceOf(PrimitiveDoubleArraySerializer.class));
    }

    @Test
    void selectSerializerConfiguration() {
        var serializer1 = (ConfigurationSerializer<?>) SELECTOR_POINT
                .select(findByType(ExampleConfigurationB1.class));
        var serializer2 = (ConfigurationSerializer<?>) SELECTOR_POINT
                .select(findByType(ExampleConfigurationB2.class));
        assertThat(serializer1.getConfigurationType(), equalTo(ExampleConfigurationB1.class));
        assertThat(serializer2.getConfigurationType(), equalTo(ExampleConfigurationB2.class));
    }

    @Test
    void selectSerializerRecord() {
        var serializer1 = (RecordSerializer<?>) SELECTOR_POINT
                .select(findByType(ExampleRecord1.class));
        var serializer2 = (RecordSerializer<?>) SELECTOR_POINT
                .select(findByType(ExampleRecord2.class));
        assertThat(serializer1.getRecordType(), equalTo(ExampleRecord1.class));
        assertThat(serializer2.getRecordType(), equalTo(ExampleRecord2.class));
    }

    @Test
    void selectSerializerMissingType() {
        class A {
            Object object;
        }
        assertThrowsConfigurationException(
                () -> SELECTOR.select(fieldAsElement(A.class, "object")),
                "Missing serializer for type class java.lang.Object.\nEither annotate the type with " +
                "@Configuration, make it a Java record, or provide a custom serializer for it."
        );
    }

    @Test
    void selectSerializerByCustomType() {
        var pointSerializer = SELECTOR_POINT.select(findByType(Point.class));
        assertThat(pointSerializer, sameInstance(POINT_SERIALIZER));
    }

    @Test
    void selectSerializerByCustomTypeTakesPrecedence() {
        var properties = ConfigurationProperties.newBuilder()
                .addSerializer(BigInteger.class, CUSTOM_BIG_INTEGER_SERIALIZER)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var bigIntegerSerializer = selector.select(findByType(BigInteger.class));
        assertThat(bigIntegerSerializer, instanceOf(TestUtils.CustomBigIntegerSerializer.class));
        assertThat(bigIntegerSerializer, sameInstance(CUSTOM_BIG_INTEGER_SERIALIZER));
    }

    @Test
    void selectSerializerFactoryByCustomType() {
        final var configurationElement = findByType(Point.class);

        Function<SerializerContext, Serializer<Point, ?>> factory = ctx -> {
            assertThat(ctx.element(), is(configurationElement));
            assertThat(ctx.annotatedType(), is(configurationElement.annotatedType()));
            return POINT_SERIALIZER;
        };

        var properties = ConfigurationProperties.newBuilder()
                .addSerializerFactory(Point.class, factory)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var pointSerializer = selector.select(configurationElement);
        assertThat(pointSerializer, sameInstance(POINT_SERIALIZER));
    }

    @Test
    void selectSerializerFactoryByCustomTypeTakesPrecedence() {
        var properties = ConfigurationProperties.newBuilder()
                .addSerializerFactory(BigInteger.class, ignored -> CUSTOM_BIG_INTEGER_SERIALIZER)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var bigIntegerSerializer = selector.select(findByType(BigInteger.class));
        assertThat(bigIntegerSerializer, instanceOf(TestUtils.CustomBigIntegerSerializer.class));
        assertThat(bigIntegerSerializer, sameInstance(CUSTOM_BIG_INTEGER_SERIALIZER));
    }

    @Test
    void selectSerializerFactoryTakesPrecedence() {
        var serializer1 = IdentifiableSerializer.of(1);
        var serializer2 = IdentifiableSerializer.of(2);
        var properties = ConfigurationProperties.newBuilder()
                .addSerializer(int.class, serializer1)
                .addSerializerFactory(int.class, ignored -> serializer2)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var serializer = selector.select(findByType(int.class));
        assertThat(serializer, instanceOf(IdentifiableSerializer.class));
        assertThat(serializer, sameInstance(serializer2));
    }

    @Test
    void selectSerializerFactoryRequiresNonNull() {
        var properties = ConfigurationProperties.newBuilder()
                .addSerializerFactory(Point.class, ignored -> null)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        assertThrowsConfigurationException(
                () -> selector.select(findByType(Point.class)),
                "Serializer factories must not return null."
        );
    }

    @Test
    void selectSerializerByCondition() {
        var properties = ConfigurationProperties.newBuilder()
                .addSerializerByCondition(t -> t == Point.class, POINT_SERIALIZER)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var pointSerializer = selector.select(findByType(Point.class));
        assertThat(pointSerializer, sameInstance(POINT_SERIALIZER));
    }

    @Test
    void selectSerializerByConditionTakesPrecedence() {
        var properties = ConfigurationProperties.newBuilder()
                .addSerializerByCondition(t -> t == BigInteger.class, CUSTOM_BIG_INTEGER_SERIALIZER)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var bigIntegerSerializer = selector.select(findByType(BigInteger.class));
        assertThat(bigIntegerSerializer, instanceOf(TestUtils.CustomBigIntegerSerializer.class));
        assertThat(bigIntegerSerializer, sameInstance(CUSTOM_BIG_INTEGER_SERIALIZER));
    }

    @Test
    void selectSerializerByCustomTypeTakesPrecedenceOverCondition() {
        var serializer1 = IdentifiableSerializer.of(1);
        var serializer2 = IdentifiableSerializer.of(2);
        var properties = ConfigurationProperties.newBuilder()
                .addSerializerByCondition(t -> t == int.class, serializer1)
                .addSerializer(int.class, serializer2)
                .build();
        SerializerSelector selector = new SerializerSelector(properties);
        var serializer = selector.select(findByType(int.class));
        assertThat(serializer, instanceOf(IdentifiableSerializer.class));
        assertThat(serializer, sameInstance(serializer2));
    }

    @Test
    void selectSerializerList() {
        var serializer = (ListSerializer<?, ?>) SELECTOR.select(findByName("a2_listString"));
        assertThat(serializer.getElementSerializer(), instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerListNested() {
        class A {
            List<List<String>> lls;
        }
        var serializer = (ListSerializer<?, ?>) SELECTOR.select(findByName("a2_listListByte"));
        var elementSerializer = (ListSerializer<?, ?>) serializer.getElementSerializer();
        var numberSerializer = (NumberSerializer) elementSerializer.getElementSerializer();
        assertThat(numberSerializer.getNumberClass(), equalTo(Byte.class));
    }

    @Test
    void selectSerializerSetsAsSets() {
        SerializerSelector selector = new SerializerSelector(
                ConfigurationProperties.newBuilder().serializeSetsAsLists(false).build()
        );
        var serializer = (SetSerializer<?, ?>) selector.select(findByName("a2_setString"));
        assertThat(serializer.getElementSerializer(), instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerSetsAsLists() {
        var serializer = (SetAsListSerializer<?, ?>) SELECTOR.select(findByName("a2_setString"));
        assertThat(serializer.getElementSerializer(), instanceOf(StringSerializer.class));
    }

    @Test
    void selectSerializerMap() {
        var serializer = (MapSerializer<?, ?, ?, ?>) SELECTOR_POINT.select(findByName("a2_mapStringR1"));
        var stringSerializer = (StringSerializer) serializer.getKeySerializer();
        var recordSerializer = (RecordSerializer<?>) serializer.getValueSerializer();
        assertThat(recordSerializer.getRecordType(), equalTo(ExampleRecord1.class));
    }

    @Test
    void selectSerializerMapNested() {
        enum E {}
        class A {
            Map<E, Set<List<E>>> mesle;
        }
        var serializer = (MapSerializer<?, ?, ?, ?>) SELECTOR.select(fieldAsElement(A.class, "mesle"));

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
        ConfigurationElement<?> element = fieldAsElement(A.class, "mlss");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(element),
                ("Cannot select serializer for type '%s'.\n" +
                 "Map keys can only be of simple or enum type.")
                        .formatted(element.annotatedType().getType())
        );
    }

    @Test
    void selectSerializerMapInvalidKeyType2() {
        class A {
            Map<Point, String> mps;
        }
        ConfigurationElement<?> element = fieldAsElement(A.class, "mps");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(element),
                ("Cannot select serializer for type '%s'.\n" +
                 "Map keys can only be of simple or enum type.")
                        .formatted(element.annotatedType().getType())
        );
    }

    @Test
    void selectSerializerOtherParameterizedType() {
        class Box<T> {}
        class A {
            Box<String> box;
        }
        ConfigurationElement<?> element = fieldAsElement(A.class, "box");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(element),
                ("Cannot select serializer for type '%s'.\n" +
                 "Parameterized types other than lists, sets, and maps cannot be serialized.")
                        .formatted(element.annotatedType().getType())
        );
    }

    @Test
    void selectSerializerGenericArrayType() {
        class A {
            List<?>[] ga;
        }
        ConfigurationElement<?> element = fieldAsElement(A.class, "ga");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(element),
                "Cannot select serializer for type 'java.util.List<?>[]'.\n" +
                "Generic array types cannot be serialized."
        );
    }

    @Test
    void selectSerializerBoundedWildcardType() {
        class A {
            List<? extends String> les;
        }
        ConfigurationElement<?> element = fieldAsElement(A.class, "les");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(element),
                "Cannot select serializer for type '? extends java.lang.String'.\n" +
                "Wildcard types cannot be serialized."
        );
    }

    @Test
    void selectSerializerWildcardType() {
        class A {
            List<?> lw;
        }
        ConfigurationElement<?> element = fieldAsElement(A.class, "lw");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(element),
                "Cannot select serializer for type '?'.\n" +
                "Wildcard types cannot be serialized."
        );
    }

    @Test
    void selectSerializerTypeVariable() {
        class A<T> {
            T t;
        }
        ConfigurationElement<?> element = fieldAsElement(A.class, "t");
        assertThrowsConfigurationException(
                () -> SELECTOR.select(element),
                "Cannot select serializer for type 'T'.\n" +
                "Type variables cannot be serialized."
        );
    }

    static final class SerializeWithOnConfigurationElementsTests {
        static class Z {
            @SerializeWith(serializer = IdentitySerializer.class)
            String string;
            @SerializeWith(serializer = IdentitySerializer.class)
            List<Set<String>> list1;
            @SerializeWith(serializer = IdentitySerializer.class, nesting = 1)
            List<Set<String>> list2;
            @SerializeWith(serializer = IdentitySerializer.class, nesting = 2)
            List<Set<String>> list3;
            @SerializeWith(serializer = IdentitySerializer.class)
            Set<List<String>> set1;
            @SerializeWith(serializer = IdentitySerializer.class, nesting = 1)
            Set<List<String>> set2;
            @SerializeWith(serializer = IdentitySerializer.class, nesting = 2)
            Set<List<String>> set3;
            @SerializeWith(serializer = IdentitySerializer.class)
            Map<Integer, Map<String, Double>> map1;
            @SerializeWith(serializer = IdentitySerializer.class, nesting = 1)
            Map<Integer, Map<String, Double>> map2;
            @SerializeWith(serializer = IdentitySerializer.class, nesting = 2)
            Map<Integer, Map<String, Double>> map3;
            @SerializeWith(serializer = IdentitySerializer.class)
            String[][] array1;
            @SerializeWith(serializer = IdentitySerializer.class, nesting = 1)
            String[][] array2;
            @SerializeWith(serializer = IdentitySerializer.class, nesting = 2)
            String[][] array3;
        }

        @Test
        void selectCustomSerializerfieldAsElement() {
            var serializer = SELECTOR.select(fieldAsElement(Z.class, "string"));
            assertThat(serializer, instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForListsWithNesting0() {
            var serializer = SELECTOR.select(fieldAsElement(Z.class, "list1"));
            assertThat(serializer, instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForListsWithNesting1() {
            var serializer = (ListSerializer<?, ?>) SELECTOR.select(fieldAsElement(Z.class, "list2"));
            assertThat(serializer.getElementSerializer(), instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForListsWithNesting2() {
            var serializer1 = (ListSerializer<?, ?>) SELECTOR.select(fieldAsElement(Z.class, "list3"));
            var serializer2 = (SetAsListSerializer<?, ?>) serializer1.getElementSerializer();
            assertThat(serializer2.getElementSerializer(), instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForSetsWithNesting0() {
            var serializer = SELECTOR.select(fieldAsElement(Z.class, "set1"));
            assertThat(serializer, instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForSetsWithNesting1() {
            var serializer = (SetAsListSerializer<?, ?>) SELECTOR.select(fieldAsElement(Z.class, "set2"));
            assertThat(serializer.getElementSerializer(), instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForSetsWithNesting2() {
            var serializer1 = (SetAsListSerializer<?, ?>) SELECTOR.select(fieldAsElement(Z.class, "set3"));
            var serializer2 = (ListSerializer<?, ?>) serializer1.getElementSerializer();
            assertThat(serializer2.getElementSerializer(), instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForMapsWithNesting0() {
            var serializer = SELECTOR.select(fieldAsElement(Z.class, "map1"));
            assertThat(serializer, instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForMapsWithNesting1() {
            var serializer = (MapSerializer<?, ?, ?, ?>) SELECTOR.select(fieldAsElement(Z.class, "map2"));
            assertThat(serializer.getKeySerializer(), instanceOf(NumberSerializer.class));
            assertThat(serializer.getValueSerializer(), instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForMapsWithNesting2() {
            var serializer1 = (MapSerializer<?, ?, ?, ?>) SELECTOR.select(fieldAsElement(Z.class, "map3"));
            var serializer2 = (MapSerializer<?, ?, ?, ?>) serializer1.getValueSerializer();
            assertThat(serializer2.getKeySerializer(), instanceOf(StringSerializer.class));
            assertThat(serializer2.getValueSerializer(), instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForArraysWithNesting0() {
            var serializer = SELECTOR.select(fieldAsElement(Z.class, "array1"));
            assertThat(serializer, instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForArraysWithNesting1() {
            var serializer = (ArraySerializer<?, ?>) SELECTOR.select(fieldAsElement(Z.class, "array2"));
            assertThat(serializer.getElementSerializer(), instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerForArraysWithNesting2() {
            var serializer1 = (ArraySerializer<?, ?>) SELECTOR.select(fieldAsElement(Z.class, "array3"));
            var serializer2 = (ArraySerializer<?, ?>) serializer1.getElementSerializer();
            assertThat(serializer2.getElementSerializer(), instanceOf(IdentitySerializer.class));
        }

        @Test
        void selectCustomSerializerWithInvalidNestingNotSelected() {
            class A {
                @SerializeWith(serializer = IdentitySerializer.class, nesting = -1)
                String s1;
                @SerializeWith(serializer = IdentitySerializer.class)
                String s2;
                @SerializeWith(serializer = IdentitySerializer.class, nesting = 1)
                String s3;
                @SerializeWith(serializer = IdentitySerializer.class, nesting = 2)
                List<String> list;
            }
            assertThat(SELECTOR.select(fieldAsElement(A.class, "s1")), instanceOf(StringSerializer.class));
            assertThat(SELECTOR.select(fieldAsElement(A.class, "s2")), instanceOf(IdentitySerializer.class));
            assertThat(SELECTOR.select(fieldAsElement(A.class, "s3")), instanceOf(StringSerializer.class));
            var serializer = (ListSerializer<?, ?>) SELECTOR.select(fieldAsElement(A.class, "list"));
            assertThat(serializer.getElementSerializer(), instanceOf(StringSerializer.class));
        }

        @Test
        void selectCustomSerializerWithContext() {
            class A {
                @SerializeWith(serializer = SerializerWithContext.class)
                String s;
            }

            var element = fieldAsElement(A.class, "s");
            var field = getField(A.class, "s");
            var serializer = (SerializerWithContext) SELECTOR.select(element);
            var context = serializer.ctx;

            assertThat(context.properties(), sameInstance(DEFAULT_PROPS));
            assertThat(context.element(), is(element));
            assertThat(context.annotatedType(), is(field.getAnnotatedType()));
        }

        @Test
        void selectCustomSerializerWithContextAndNesting() {
            class A {
                @SerializeWith(serializer = SerializerWithContext.class, nesting = 1)
                List<String> l;
            }

            var element = fieldAsElement(A.class, "l");
            var field = getField(A.class, "l");
            var outerSerializer = (ListSerializer<?, ?>) SELECTOR.select(element);
            var innerSerializer = (SerializerWithContext) outerSerializer.getElementSerializer();
            var context = innerSerializer.ctx;

            assertThat(context.properties(), sameInstance(DEFAULT_PROPS));
            assertThat(context.element(), is(element));

            var annotatedType = (AnnotatedParameterizedType) field.getAnnotatedType();
            var argument = annotatedType.getAnnotatedActualTypeArguments()[0];

            assertThat(context.annotatedType(), is(not(annotatedType)));
            assertThat(context.annotatedType(), is(argument));
        }

        private record SerializerWithContext(SerializerContext ctx)
                implements Serializer<String, String> {

            @Override
            public String serialize(String element) {return null;}

            @Override
            public String deserialize(String element) {return null;}
        }
    }

    static final class SerializeWithOnTypesTest {
        @SerializeWith(serializer = IdentitySerializer.class)
        static final class MyType1 {}

        @SerializeWith(serializer = IdentitySerializer.class)
        static abstract class MyType2 {}

        @SerializeWith(serializer = IdentitySerializer.class)
        interface MyType3 {}

        @SerializeWith(serializer = IdentitySerializer.class)
        record MyType4() {}

        @SerializeWith(serializer = IdentitySerializer.class)
        static class MyType5 {}

        static class MyType6 extends MyType5 {}

        record Config(
                MyType1 myType1,
                MyType2 myType2,
                MyType3 myType3,
                MyType4 myType4,
                MyType5 myType5,
                MyType6 myType6
        ) {}

        @ParameterizedTest
        @ValueSource(strings = {"myType1", "myType2", "myType3", "myType4", "myType5"})
        void selectCustomSerializerForTypes(String fieldName) {
            var element = fieldAsElement(Config.class, fieldName);
            var serializer = (IdentitySerializer) SELECTOR.select(element);
            assertThat(serializer.context().element(), is(element));
        }

        @Test
        void serializeWithNotInherited() {
            assertThrowsConfigurationException(
                    () -> SELECTOR.select(fieldAsElement(Config.class, "myType6")),
                    ("Missing serializer for type %s.\nEither annotate the type with " +
                     "@Configuration, make it a Java record, or provide a custom serializer for it.")
                            .formatted(MyType6.class)

            );
        }

        @Test
        void serializeWithHasLowerPrecedenceThanSerializersAddedViaConfigurationProperties() {
            var serializer = new IdentifiableSerializer<>(1);
            var properties = ConfigurationProperties.newBuilder()
                    .addSerializer(MyType1.class, serializer)
                    .build();
            var selector = new SerializerSelector(properties);
            var actual = (IdentifiableSerializer<?, ?>) selector.select(fieldAsElement(Config.class, "myType1"));
            assertThat(actual, sameInstance(serializer));
        }
    }

    static final class SerializeWithMetaAnnotationTest {
        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        @Inherited
        @SerializeWith(serializer = IdentitySerializer.class)
        @interface MetaSerializeWith {}

        @MetaSerializeWith
        static final class MyType1 {}

        @MetaSerializeWith
        static abstract class MyType2 {}

        @MetaSerializeWith
        interface MyType3 {}

        @MetaSerializeWith
        record MyType4() {}

        @MetaSerializeWith
        static class MyType5 {}

        static class MyType6 extends MyType5 {}

        @MetaSerializeWith
        @SerializeWith(serializer = PointSerializer.class)
        static final class MyType7 {}

        record Config(
                MyType1 myType1,
                MyType2 myType2,
                MyType3 myType3,
                MyType4 myType4,
                MyType5 myType5,
                MyType6 myType6,
                MyType7 myType7
        ) {}

        @ParameterizedTest
        @ValueSource(strings = {"myType1", "myType2", "myType3", "myType4", "myType5"})
        void selectCustomSerializerForTypes(String fieldName) {
            var element = fieldAsElement(Config.class, fieldName);
            var serializer = (IdentitySerializer) SELECTOR.select(element);
            assertThat(serializer.context().element(), is(element));
        }

        @Test
        void metaSerializeWithNotInherited() {
            assertThrowsConfigurationException(
                    () -> SELECTOR.select(fieldAsElement(Config.class, "myType6")),
                    ("Missing serializer for type %s.\nEither annotate the type with " +
                     "@Configuration, make it a Java record, or provide a custom serializer for it.")
                            .formatted(MyType6.class)

            );
        }

        @Test
        void metaSerializeWithHasLowerPrecedenceThanSerializeWith() {
            var serializer = SELECTOR.select(fieldAsElement(Config.class, "myType7"));
            assertThat(serializer, instanceOf(PointSerializer.class));
        }
    }
}
