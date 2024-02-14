package de.exlll.configlib;

import de.exlll.configlib.Serializers.MapSerializer;
import de.exlll.configlib.Serializers.NumberSerializer;
import de.exlll.configlib.Serializers.SetAsListSerializer;
import de.exlll.configlib.Serializers.SetSerializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static de.exlll.configlib.TestUtils.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SerializersTest {

    private final String TMP_CONFIG_PATH = createPlatformSpecificFilePath("/tmp/config.yml");
    private final String TMP_WITH_UNDERSCORE_PATH = createPlatformSpecificFilePath("/tmp/with_underscore.yml");
    private final String TMP_PATH = createPlatformSpecificFilePath("/tmp");

    @Test
    void booleanSerializer() {
        Serializer<Boolean, Boolean> serializer = new Serializers.BooleanSerializer();

        Boolean element = true;

        Object serialized = serializer.serialize(element);
        assertThat(serialized, sameInstance(element));

        Object deserialized = serializer.deserialize(element);
        assertThat(deserialized, sameInstance(element));
    }

    @Test
    void numberSerializerRequiresValidNumberType() {
        assertThrowsNullPointerException(
                () -> new NumberSerializer(null),
                "number class"
        );
        assertThrowsIllegalArgumentException(
                () -> new NumberSerializer(BigInteger.class),
                "Class BigInteger is not a byte, short, int, long, float, double, or a " +
                "wrapper type of one of the primitive number types."
        );
    }

    @Test
    void numberSerializerDeserializeInvalidType() {
        NumberSerializer serializer = new NumberSerializer(int.class);

        assertThrowsConfigurationException(
                () -> serializer.deserialize(BigInteger.ONE),
                "Cannot deserialize element '1' of type BigInteger.\n" +
                "This serializer only supports primitive number types and their wrapper types."
        );
    }

    @ParameterizedTest
    @ValueSource(classes = {
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class
    })
    void numberSerializerSerializeIntegerType(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);

        int[] numbers = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE};

        for (int number : numbers) {
            Number serialized = serializer.serialize(number);
            assertThat(serialized, instanceOf(Long.class));
            assertThat(serialized, is((long) number));
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {
            float.class, Float.class,
            double.class, Double.class
    })
    void numberSerializerSerializeFloatingPointType(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);

        float[] numbers = {-Float.MAX_VALUE, -Float.MIN_VALUE, 0, Float.MIN_VALUE, Float.MAX_VALUE};

        for (float number : numbers) {
            Number serialized = serializer.serialize(number);
            assertThat(serialized, instanceOf(Double.class));
            assertThat(serialized, is((double) number));
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class
    })
    void numberSerializerDeserializeByteToIntegerType(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);

        byte[] numbers = {Byte.MIN_VALUE, 0, Byte.MAX_VALUE};

        for (byte number : numbers) {
            Number deserialized = serializer.deserialize(number);
            assertThat(deserialized, instanceOf(cls));
            assertThat(deserialized.longValue(), is((long) number));
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class
    })
    void numberSerializerDeserializeShortToIntegerType(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);

        short[] numbers = {Short.MIN_VALUE, 0, Short.MAX_VALUE};

        for (short number : numbers) {
            Number deserialized = serializer.deserialize(number);
            assertThat(deserialized, instanceOf(cls));
            assertThat(deserialized.longValue(), is((long) number));
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {int.class, Integer.class, long.class, Long.class})
    void numberSerializerDeserializeIntegerToIntegerType(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);

        int[] numbers = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE};

        for (int number : numbers) {
            Number deserialized = serializer.deserialize(number);
            assertThat(deserialized, instanceOf(cls));
            assertThat(deserialized.longValue(), is((long) number));
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {long.class, Long.class})
    void numberSerializerDeserializeLongToIntegerType(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);

        long[] numbers = {Long.MIN_VALUE, 0, Long.MAX_VALUE};

        for (long number : numbers) {
            Number deserialized = serializer.deserialize(number);
            assertThat(deserialized, instanceOf(cls));
            assertThat(deserialized, is(number));
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {byte.class, Byte.class})
    void numberSerializerDeserializeTooLargeIntegerTypeToByte(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);
        long[] values = {
                Long.MIN_VALUE, Integer.MIN_VALUE, Short.MIN_VALUE, Byte.MIN_VALUE - 1,
                Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE, Byte.MAX_VALUE + 1
        };
        for (long value : values) {
            String msg = "Number " + value + " cannot be converted to type " + cls.getSimpleName() +
                         ". It does not fit into the range of valid values [-128, 127].";
            assertThrowsConfigurationException(() -> serializer.deserialize(value), msg);
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {short.class, Short.class})
    void numberSerializerDeserializeTooLargeIntegerTypeToShort(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);
        long[] values = {
                Long.MIN_VALUE, Integer.MIN_VALUE, Short.MIN_VALUE - 1,
                Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE + 1,
        };
        for (long value : values) {
            String msg = "Number " + value + " cannot be converted to type " + cls.getSimpleName() +
                         ". It does not fit into the range of valid values [-32768, 32767].";
            assertThrowsConfigurationException(() -> serializer.deserialize(value), msg);
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {int.class, Integer.class})
    void numberSerializerDeserializeTooLargeIntegerTypeToInteger(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);
        long[] values = {
                Long.MIN_VALUE, Integer.MIN_VALUE - 1L,
                Long.MAX_VALUE, Integer.MAX_VALUE + 1L,
        };
        for (long value : values) {
            String msg = "Number " + value + " cannot be converted to type " + cls.getSimpleName() +
                         ". It does not fit into the range of valid values [-2147483648, 2147483647].";
            assertThrowsConfigurationException(() -> serializer.deserialize(value), msg);
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {float.class, Float.class, double.class, Double.class})
    void numberSerializerDeserializeFloatToFloatingPointType(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);

        float[] numbers = {-Float.MAX_VALUE, -Float.MIN_VALUE, 0, Float.MIN_VALUE, Float.MAX_VALUE};

        for (float number : numbers) {
            Number deserialized = serializer.deserialize(number);
            assertThat(deserialized, instanceOf(cls));
            assertThat(deserialized.doubleValue(), is((double) number));
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {double.class, Double.class})
    void numberSerializerDeserializeDoubleToFloatingPointType(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);

        double[] numbers = {-Double.MAX_VALUE, -Double.MIN_VALUE, 0, Double.MIN_VALUE, Double.MAX_VALUE};

        for (double number : numbers) {
            Number deserialized = serializer.deserialize(number);
            assertThat(deserialized, instanceOf(cls));
            assertThat(deserialized, is(number));
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {float.class, Float.class})
    void numberSerializerDeserializeTooLargeFloatingPointTypeToFloat(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);
        double ulpFloatMax = Math.ulp((double) Float.MAX_VALUE);
        double[] values = {
                +Double.MAX_VALUE,
                -Double.MAX_VALUE,
                (double) +Float.MAX_VALUE + ulpFloatMax,
                (double) -Float.MAX_VALUE - ulpFloatMax
        };
        for (double value : values) {
            String clsName = cls.getSimpleName();
            String msg = "Number " + value + " cannot be converted to type " + clsName + ". " +
                         "It is larger than the largest possible " + clsName + " value.";
            assertThrowsConfigurationException(() -> serializer.deserialize(value), msg);
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {float.class, Float.class})
    void numberSerializerDeserializeTooSmallFloatingPointTypeToFloat(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);
        double ulpFloatMin = Math.ulp((double) Float.MIN_VALUE);
        double[] values = {
                +Double.MIN_VALUE,
                -Double.MIN_VALUE,
                (double) +Float.MIN_VALUE - ulpFloatMin,
                (double) -Float.MIN_VALUE + ulpFloatMin,
        };
        for (double value : values) {
            String clsName = cls.getSimpleName();
            String msg = "Number " + value + " cannot be converted to type " + clsName + ". " +
                         "It is smaller than the smallest possible " + clsName + " value.";
            assertThrowsConfigurationException(() -> serializer.deserialize(value), msg);
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {float.class, Float.class})
    void numberSerializerDeserializeFloatSpecialValues(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);

        numberSerializerDeserializeFloatingPointSpecialValues(
                serializer,
                Float.class,
                Float.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY,
                Float.NaN
        );
    }

    @ParameterizedTest
    @ValueSource(classes = {double.class, Double.class})
    void numberSerializerDeserializeDoubleSpecialValues(Class<? extends Number> cls) {
        NumberSerializer serializer = new NumberSerializer(cls);

        numberSerializerDeserializeFloatingPointSpecialValues(
                serializer,
                Double.class,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.NaN
        );
    }

    private <T> void numberSerializerDeserializeFloatingPointSpecialValues(
            NumberSerializer serializer,
            Class<T> type,
            T positiveInfinity,
            T negativeInfinity,
            T nan
    ) {
        Number pinfDouble = serializer.deserialize(Double.POSITIVE_INFINITY);
        Number ninfDouble = serializer.deserialize(Double.NEGATIVE_INFINITY);
        Number nanDouble = serializer.deserialize(Double.NaN);

        Number pinfFloat = serializer.deserialize(Float.POSITIVE_INFINITY);
        Number ninfFloat = serializer.deserialize(Float.NEGATIVE_INFINITY);
        Number nanFloat = serializer.deserialize(Float.NaN);

        assertThat(pinfDouble, instanceOf(type));
        assertThat(ninfDouble, instanceOf(type));
        assertThat(nanDouble, instanceOf(type));

        assertThat(pinfFloat, instanceOf(type));
        assertThat(ninfFloat, instanceOf(type));
        assertThat(nanFloat, instanceOf(type));

        assertThat(pinfDouble, is(positiveInfinity));
        assertThat(ninfDouble, is(negativeInfinity));
        assertThat(nanDouble, is(nan));

        assertThat(pinfFloat, is(positiveInfinity));
        assertThat(ninfFloat, is(negativeInfinity));
        assertThat(nanFloat, is(nan));
    }

    @Test
    void stringSerializer() {
        Serializer<String, String> serializer = new Serializers.StringSerializer();

        String random = "RANDOM";

        assertThat(serializer.serialize(random), sameInstance(random));
        assertThat(serializer.deserialize(random), sameInstance(random));
    }

    @Test
    void characterSerializer() {
        Serializer<Character, String> serializer = new Serializers.CharacterSerializer();

        assertThat(serializer.serialize('c'), is("c"));
        assertThat(serializer.deserialize("c"), is('c'));
    }

    @Test
    void characterSerializerDeserializeInvalidString() {
        Serializer<Character, String> serializer = new Serializers.CharacterSerializer();
        assertThrowsConfigurationException(
                () -> serializer.deserialize(""),
                "An empty string cannot be converted to a character."
        );
        assertThrowsConfigurationException(
                () -> serializer.deserialize("ab"),
                "String 'ab' is too long to be converted to a character."
        );
    }

    @Test
    void bigIntegerSerializer() {
        Serializer<BigInteger, String> serializer = new Serializers.BigIntegerSerializer();

        final String result = "18446744073709551614";

        BigInteger integer = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TWO);
        assertThat(serializer.serialize(integer), is(result));

        integer = new BigInteger(result);
        assertThat(serializer.deserialize(result), is(integer));
    }

    @Test
    void bigDecimalSerializer() {
        Serializer<BigDecimal, String> serializer = new Serializers.BigDecimalSerializer();

        final String result = "3.23170060713109998320439596646649E+616";
        final BigDecimal doubleMax = BigDecimal.valueOf(Double.MAX_VALUE);

        BigDecimal decimal = doubleMax.multiply(doubleMax);

        assertThat(serializer.serialize(decimal), is(result));
        decimal = new BigDecimal(result);
        assertThat(serializer.deserialize(result), is(decimal));
    }


    @Test
    void localDateSerializer() {
        Serializer<LocalDate, String> serializer = new Serializers.LocalDateSerializer();

        LocalDate date = LocalDate.of(2000, Month.FEBRUARY, 29);

        assertThat(serializer.serialize(date), is("2000-02-29"));
        assertThat(serializer.deserialize("2000-02-29"), is(date));
    }

    @Test
    void localTimeSerializer() {
        Serializer<LocalTime, String> serializer = new Serializers.LocalTimeSerializer();

        LocalTime time = LocalTime.of(10, 11, 12, 13);

        assertThat(serializer.serialize(time), is("10:11:12"));

        time = time.truncatedTo(ChronoUnit.SECONDS);
        assertThat(serializer.deserialize("10:11:12"), is(time));
    }

    @Test
    void localDateTimeSerializer() {
        Serializer<LocalDateTime, String> serializer = new Serializers.LocalDateTimeSerializer();

        LocalDateTime dateTime = LocalDateTime.of(2000, Month.FEBRUARY, 29, 10, 11, 12, 13);

        assertThat(serializer.serialize(dateTime), is("2000-02-29T10:11:12"));

        dateTime = dateTime.truncatedTo(ChronoUnit.SECONDS);
        assertThat(serializer.deserialize("2000-02-29T10:11:12"), is(dateTime));
    }

    @Test
    void instantSerializer() {
        Serializer<Instant, String> serializer = new Serializers.InstantSerializer();

        Instant instant = LocalDateTime.of(2000, Month.FEBRUARY, 29, 10, 11, 12, 13)
                .toInstant(ZoneOffset.UTC);

        assertThat(serializer.serialize(instant), is("2000-02-29T10:11:12.000000013Z"));

        assertThat(serializer.deserialize("2000-02-29T10:11:12.000000013Z"), is(instant));
    }

    @Test
    void uuidSerializer() {
        Serializer<UUID, String> serializer = new Serializers.UuidSerializer();

        var uuid = UUID.randomUUID();
        var uuidString = uuid.toString();

        assertThat(serializer.serialize(uuid), is(uuidString));

        var deserialized = serializer.deserialize(uuidString);

        assertThat(uuid, is(deserialized));
    }

    @Test
    void fileSerializer() {
        Serializer<File, String> serializer = new Serializers.FileSerializer();

        File file1 = new File(TMP_CONFIG_PATH);
        File file2 = new File(TMP_WITH_UNDERSCORE_PATH);
        File file3 = new File(TMP_PATH);

        assertThat(serializer.serialize(file1), is(TMP_CONFIG_PATH));
        assertThat(serializer.serialize(file2), is(TMP_WITH_UNDERSCORE_PATH));
        assertThat(serializer.serialize(file3), is(TMP_PATH));

        assertThat(serializer.deserialize(TMP_CONFIG_PATH), is(file1));
        assertThat(serializer.deserialize(TMP_WITH_UNDERSCORE_PATH), is(file2));
        assertThat(serializer.deserialize(TMP_PATH), is(file3));
    }

    @Test
    void pathSerializer() {
        Serializer<Path, String> serializer = new Serializers.PathSerializer();

        Path file1 = Path.of(TMP_CONFIG_PATH);
        Path file2 = Path.of(TMP_WITH_UNDERSCORE_PATH);
        Path file3 = Path.of(TMP_PATH);

        assertThat(serializer.serialize(file1), is(TMP_CONFIG_PATH));
        assertThat(serializer.serialize(file2), is(TMP_WITH_UNDERSCORE_PATH));
        assertThat(serializer.serialize(file3), is(TMP_PATH));

        assertThat(serializer.deserialize(TMP_CONFIG_PATH), is(file1));
        assertThat(serializer.deserialize(TMP_WITH_UNDERSCORE_PATH), is(file2));
        assertThat(serializer.deserialize(TMP_PATH), is(file3));
    }

    @Test
    @Disabled("This test is disabled because the URL constructor sends a network " +
              "request that slows down the tests if the network is unresponsive.")
    void urlSerializer() throws Exception {
        Serializer<URL, String> serializer = new Serializers.UrlSerializer();

        String path1 = "https://example.com";
        String path2 = "https://example.com?query=yes";
        String path3 = "https://example.com?query=yes#fragment=true";

        URL url1 = new URL(path1);
        URL url2 = new URL(path2);
        URL url3 = new URL(path3);

        assertThat(serializer.serialize(url1), is(path1));
        assertThat(serializer.serialize(url2), is(path2));
        assertThat(serializer.serialize(url3), is(path3));

        assertThat(serializer.deserialize(path1), is(url1));
        assertThat(serializer.deserialize(path2), is(url2));
        assertThat(serializer.deserialize(path3), is(url3));
    }

    @Test
    void uriSerializer() {
        Serializer<URI, String> serializer = new Serializers.UriSerializer();

        String path1 = "https://example.com";
        String path2 = "https://example.com?query=yes";
        String path3 = "https://example.com?query=yes#fragment=true";

        URI uri1 = URI.create(path1);
        URI uri2 = URI.create(path2);
        URI uri3 = URI.create(path3);

        assertThat(serializer.serialize(uri1), is(path1));
        assertThat(serializer.serialize(uri2), is(path2));
        assertThat(serializer.serialize(uri3), is(path3));

        assertThat(serializer.deserialize(path1), is(uri1));
        assertThat(serializer.deserialize(path2), is(uri2));
        assertThat(serializer.deserialize(path3), is(uri3));
    }

    enum E {X, Y, Z}

    @ParameterizedTest
    @EnumSource(E.class)
    void enumSerializer(Enum<?> e) {
        Serializers.EnumSerializer serializer = new Serializers.EnumSerializer(E.class);

        assertThat(serializer.serialize(e), is(e.name()));
        assertThat(serializer.deserialize(e.name()), is(e));
    }

    @Test
    void enumSerializerMissingValue() {
        Serializers.EnumSerializer serializer = new Serializers.EnumSerializer(E.class);

        assertThrowsConfigurationException(
                () -> serializer.deserialize("A"),
                "Enum class E does not contain enum 'A'. Valid values are: [X, Y, Z]"
        );
    }

    @Test
    void listSerializer() {
        var serializer = new Serializers.ListSerializer<>(StringToIntSerializer.INSTANCE, false, false);

        List<Integer> integers = serializer.serialize(List.of("1", "2", "3"));
        assertThat(integers, is(List.of(1, 2, 3)));

        List<String> strings = serializer.deserialize(List.of(4, 5, 6));
        assertThat(strings, is(List.of("4", "5", "6")));
    }

    @Test
    void listSerializerNoNulls() {
        var serializer = new Serializers.ListSerializer<>(StringToIntSerializer.INSTANCE, false, false);

        List<Integer> integers = serializer.serialize(asList("1", null, "2", null, "3"));
        assertThat(integers, is(List.of(1, 2, 3)));

        List<String> strings = serializer.deserialize(asList(4, null, 5, null, 6));
        assertThat(strings, is(List.of("4", "5", "6")));
    }

    @Test
    void listSerializerOutputNulls() {
        var serializer = new Serializers.ListSerializer<>(StringToIntSerializer.INSTANCE, true, false);

        List<Integer> actual = serializer.serialize(asList("1", null, "3"));
        assertThat(actual, is(asList(1, null, 3)));
    }

    @Test
    void listSerializerInputNulls() {
        var serializer = new Serializers.ListSerializer<>(StringToIntSerializer.INSTANCE, false, true);

        List<String> actual = serializer.deserialize(asList(4, null, 5));
        assertThat(actual, is(asList("4", null, "5")));
    }

    @Test
    void setSerializer() {
        var serializer = new SetSerializer<>(StringToIntSerializer.INSTANCE, false, false);

        Set<Integer> integers = serializer.serialize(Set.of("1", "2", "3"));
        assertThat(integers, is(Set.of(1, 2, 3)));

        Set<String> strings = serializer.deserialize(Set.of(4, 5, 6));
        assertThat(strings, is(Set.of("4", "5", "6")));
    }

    @Test
    void setSerializerNoNulls() {
        var serializer = new SetSerializer<>(StringToIntSerializer.INSTANCE, false, false);

        Set<Integer> integers = serializer.serialize(asSet("1", null, "2", null, "3"));
        assertThat(integers, is(asSet(1, 2, 3)));

        Set<String> strings = serializer.deserialize(asSet(4, null, 5, null, 6));
        assertThat(strings, is(asSet("4", "5", "6")));
    }

    @Test
    void setSerializerOutputNulls() {
        var serializer = new SetSerializer<>(StringToIntSerializer.INSTANCE, true, false);

        Set<Integer> actual = serializer.serialize(asSet("1", null, "3"));
        assertThat(actual, is(asSet(1, null, 3)));
    }

    @Test
    void setSerializerInputNulls() {
        var serializer = new SetSerializer<>(StringToIntSerializer.INSTANCE, false, true);

        Set<String> actual = serializer.deserialize(asSet(4, null, 5));
        assertThat(actual, is(asSet("4", null, "5")));
    }


    @Test
    void setAsListSerializer() {
        var serializer = new SetAsListSerializer<>(StringToIntSerializer.INSTANCE, false, false);

        List<Integer> integers = serializer.serialize(asSet("1", "2", "3"));
        assertThat(integers, is(asList(1, 2, 3)));

        Set<String> strings = serializer.deserialize(List.of(4, 5, 6));
        assertThat(strings, is(Set.of("4", "5", "6")));
    }

    @Test
    void setAsListSerializerNoNulls() {
        var serializer = new SetAsListSerializer<>(StringToIntSerializer.INSTANCE, false, false);

        List<Integer> integers = serializer.serialize(asSet("1", null, "2", null, "3"));
        assertThat(integers, is(asList(1, 2, 3)));

        Set<String> strings = serializer.deserialize(asList(4, null, 5, null, 6));
        assertThat(strings, is(asSet("4", "5", "6")));
    }

    @Test
    void setAsListSerializerOutputNulls() {
        var serializer = new SetAsListSerializer<>(StringToIntSerializer.INSTANCE, true, false);

        List<Integer> actual = serializer.serialize(asSet("1", null, "3"));
        assertThat(actual, is(asList(1, null, 3)));
    }

    @Test
    void setAsListSerializerInputNulls() {
        var serializer = new SetAsListSerializer<>(StringToIntSerializer.INSTANCE, false, true);

        Set<String> actual = serializer.deserialize(asList(4, null, 5));
        assertThat(actual, is(asSet("4", null, "5")));
    }

    @Test
    void mapSerializer() {
        var serializer = new MapSerializer<>(
                StringToIntSerializer.INSTANCE,
                StringToIntSerializer.INSTANCE,
                false,
                false
        );

        Map<Integer, Integer> integers = serializer.serialize(Map.of("1", "2", "3", "4"));
        assertThat(integers, is(Map.of(1, 2, 3, 4)));

        Map<String, String> strings = serializer.deserialize(Map.of(5, 6, 7, 8));
        assertThat(strings, is(Map.of("5", "6", "7", "8")));
    }

    @Test
    void mapSerializerNoNulls() {
        var serializer = new MapSerializer<>(
                StringToIntSerializer.INSTANCE,
                StringToIntSerializer.INSTANCE,
                false,
                false
        );
        Map<Integer, Integer> integers = serializer.serialize(entriesAsMap(
                entry("1", "2"),
                entry(null, "4"),
                entry("5", null),
                entry(null, null),
                entry("9", "10")
        ));
        assertThat(integers, is(Map.of(1, 2, 9, 10)));

        Map<String, String> strings = serializer.deserialize(entriesAsMap(
                entry(11, 12),
                entry(null, 14),
                entry(15, null),
                entry(null, null),
                entry(19, 20)
        ));
        assertThat(strings, is(Map.of("11", "12", "19", "20")));
    }

    @Test
    void mapSerializerOutputNulls() {
        var serializer = new MapSerializer<>(
                StringToIntSerializer.INSTANCE,
                StringToIntSerializer.INSTANCE,
                true,
                false
        );
        Map<Integer, Integer> actual = serializer.serialize(entriesAsMap(
                entry("1", "2"),
                entry(null, "4"),
                entry("5", null),
                entry(null, null),
                entry("9", "10")
        ));
        Map<Integer, Integer> expected = entriesAsMap(
                entry(1, 2),
                entry(5, null),
                entry(null, null),
                entry(9, 10)
        );
        assertThat(actual, is(expected));
    }

    @Test
    void mapSerializerInputNulls() {
        var serializer = new MapSerializer<>(
                StringToIntSerializer.INSTANCE,
                StringToIntSerializer.INSTANCE,
                false,
                true
        );
        Map<String, String> actual = serializer.deserialize(entriesAsMap(
                entry(11, 12),
                entry(null, 14),
                entry(15, null),
                entry(null, null),
                entry(19, 20)
        ));
        Map<String, String> expected = entriesAsMap(
                entry("11", "12"),
                entry("15", null),
                entry(null, null),
                entry("19", "20")
        );
        assertThat(actual, is(expected));
    }

    @Test
    void arraySerializer() {
        var serializer = new Serializers.ArraySerializer<>(
                String.class,
                StringToIntSerializer.INSTANCE,
                false, false
        );

        List<Integer> integers = serializer.serialize(new String[]{"1", "2", "3"});
        assertThat(integers, is(List.of(1, 2, 3)));

        String[] strings = serializer.deserialize(List.of(4, 5, 6));
        assertThat(strings, is(new String[]{"4", "5", "6"}));
    }

    @Test
    void arraySerializerNested() {
        var serializer = new Serializers.ArraySerializer<>(
                String[].class,
                new Serializers.ArraySerializer<>(
                        String.class,
                        StringToIntSerializer.INSTANCE,
                        false, false
                ),
                false, false
        );

        String[][] input = {{"1"}, {"2", "3"}};
        List<List<Integer>> integers = serializer.serialize(input);
        assertThat(integers, is(List.of(List.of(1), List.of(2, 3))));

        String[][] output = serializer.deserialize(List.of(
                List.of(4),
                List.of(5, 6)
        ));
        assertThat(output, is(new String[][]{{"4"}, {"5", "6"}}));
    }

    @Test
    void arraySerializerNoNulls() {
        var serializer = new Serializers.ArraySerializer<>(
                String.class,
                StringToIntSerializer.INSTANCE,
                false, false
        );

        List<Integer> integers = serializer.serialize(new String[]{"1", null, "2", null, "3"});
        assertThat(integers, is(List.of(1, 2, 3)));

        String[] strings = serializer.deserialize(asList(4, null, 5, null, 6));
        assertThat(strings, is(new String[]{"4", "5", "6"}));
    }

    @Test
    void arraySerializerOutputNulls() {
        var serializer = new Serializers.ArraySerializer<>(
                String.class,
                StringToIntSerializer.INSTANCE,
                true, false
        );

        List<Integer> integers = serializer.serialize(new String[]{"1", null, "2", null, "3"});
        assertThat(integers, is(asList(1, null, 2, null, 3)));
    }

    @Test
    void arraySerializerInputNulls() {
        var serializer = new Serializers.ArraySerializer<>(
                String.class,
                StringToIntSerializer.INSTANCE,
                false, true
        );

        String[] strings = serializer.deserialize(asList(4, null, 5, null, 6));
        assertThat(strings, is(new String[]{"4", null, "5", null, "6"}));
    }

    @Test
    void primitiveBooleanArraySerializer() {
        var serializer = new Serializers.PrimitiveBooleanArraySerializer();

        List<Boolean> list = serializer.serialize(new boolean[]{true, false, true});
        assertThat(list, is(List.of(true, false, true)));

        boolean[] array = (boolean[]) serializer.deserialize(List.of(false, true, false));
        assertThat(array, is(new boolean[]{false, true, false}));
    }

    @Test
    void primitiveCharacterArraySerializer() {
        var serializer = new Serializers.PrimitiveCharacterArraySerializer();

        List<String> list = serializer.serialize(new char[]{'a', 'b', 'c'});
        assertThat(list, is(List.of("a", "b", "c")));

        char[] array = (char[]) serializer.deserialize(List.of("d", "e", "f"));
        assertThat(array, is(new char[]{'d', 'e', 'f'}));
    }

    @Test
    void primitiveByteArraySerializer() {
        var serializer = new Serializers.PrimitiveByteArraySerializer();

        List<Number> list = serializer.serialize(new byte[]{(byte) 1, (byte) 2, (byte) 3});
        assertThat(list, is(List.of(1L, 2L, 3L)));

        byte[] array = (byte[]) serializer.deserialize(List.of(3L, 4L, 5L));
        assertThat(array, is(new byte[]{(byte) 3, (byte) 4, (byte) 5}));
    }

    @Test
    void primitiveShortArraySerializer() {
        var serializer = new Serializers.PrimitiveShortArraySerializer();

        List<Number> list = serializer.serialize(new short[]{(short) 1, (short) 2, (short) 3});
        assertThat(list, is(List.of(1L, 2L, 3L)));

        short[] array = (short[]) serializer.deserialize(List.of(3L, 4L, 5L));
        assertThat(array, is(new short[]{(short) 3, (short) 4, (short) 5}));
    }

    @Test
    void primitiveIntegerArraySerializer() {
        var serializer = new Serializers.PrimitiveIntegerArraySerializer();

        List<Number> list = serializer.serialize(new int[]{1, 2, 3});
        assertThat(list, is(List.of(1L, 2L, 3L)));

        int[] array = (int[]) serializer.deserialize(List.of(3L, 4L, 5L));
        assertThat(array, is(new int[]{3, 4, 5}));
    }

    @Test
    void primitiveLongArraySerializer() {
        var serializer = new Serializers.PrimitiveLongArraySerializer();

        List<Number> list = serializer.serialize(new long[]{1L, 2L, 3L});
        assertThat(list, is(List.of(1L, 2L, 3L)));

        long[] array = (long[]) serializer.deserialize(List.of(3L, 4L, 5L));
        assertThat(array, is(new long[]{3L, 4L, 5L}));
    }

    @Test
    void primitiveFloatArraySerializer() {
        var serializer = new Serializers.PrimitiveFloatArraySerializer();

        List<Number> list = serializer.serialize(new float[]{1f, 2f, 3f});
        assertThat(list, is(List.of(1d, 2d, 3d)));

        float[] array = (float[]) serializer.deserialize(List.of(3d, 4d, 5d));
        assertThat(array, is(new float[]{3f, 4f, 5f}));
    }

    @Test
    void primitiveDoubleArraySerializer() {
        var serializer = new Serializers.PrimitiveDoubleArraySerializer();

        List<Number> list = serializer.serialize(new double[]{1d, 2d, 3d});
        assertThat(list, is(List.of(1d, 2d, 3d)));

        double[] array = (double[]) serializer.deserialize(List.of(3d, 4d, 5d));
        assertThat(array, is(new double[]{3d, 4d, 5d}));
    }

    @Test
    void primitiveBooleanArraySerializerDeserializeNullElement() {
        var serializer = new Serializers.PrimitiveBooleanArraySerializer();
        assertThrowsConfigurationException(
                () -> serializer.deserialize(asList(true, false, null)),
                "The boolean element at index 2 must not be null."
        );
    }

    @Test
    void primitiveCharacterArraySerializerDeserializeNullElement() {
        var serializer = new Serializers.PrimitiveCharacterArraySerializer();
        assertThrowsConfigurationException(
                () -> serializer.deserialize(asList("a", null, "b")),
                "The char element at index 1 must not be null."
        );
    }

    @Test
    void primitiveByteArraySerializerDeserializeNullElement() {
        var serializer = new Serializers.PrimitiveByteArraySerializer();
        assertThrowsConfigurationException(
                () -> serializer.deserialize(asList((byte) 1, (byte) 2, null)),
                "The byte element at index 2 must not be null."
        );
    }

    @Test
    void primitiveShortArraySerializerDeserializeNullElement() {
        var serializer = new Serializers.PrimitiveShortArraySerializer();
        assertThrowsConfigurationException(
                () -> serializer.deserialize(asList((short) 1, null, (short) 2)),
                "The short element at index 1 must not be null."
        );
    }

    @Test
    void primitiveIntegerArraySerializerDeserializeNullElement() {
        var serializer = new Serializers.PrimitiveIntegerArraySerializer();
        assertThrowsConfigurationException(
                () -> serializer.deserialize(asList(1, 2, null)),
                "The int element at index 2 must not be null."
        );
    }

    @Test
    void primitiveLongArraySerializerDeserializeNullElement() {
        var serializer = new Serializers.PrimitiveLongArraySerializer();
        assertThrowsConfigurationException(
                () -> serializer.deserialize(asList(1L, null, 2L)),
                "The long element at index 1 must not be null."
        );
    }

    @Test
    void primitiveFloatArraySerializerDeserializeNullElement() {
        var serializer = new Serializers.PrimitiveFloatArraySerializer();
        assertThrowsConfigurationException(
                () -> serializer.deserialize(asList(1f, 2f, null)),
                "The float element at index 2 must not be null."
        );
    }

    @Test
    void primitiveDoubleArraySerializerDeserializeNullElement() {
        var serializer = new Serializers.PrimitiveDoubleArraySerializer();
        assertThrowsConfigurationException(
                () -> serializer.deserialize(asList(1d, null, 2d)),
                "The double element at index 1 must not be null."
        );
    }

    private static final class StringToIntSerializer implements Serializer<String, Integer> {
        private static final StringToIntSerializer INSTANCE = new StringToIntSerializer();

        @Override
        public Integer serialize(String element) {
            return Integer.valueOf(element);
        }

        @Override
        public String deserialize(Integer element) {
            return element.toString();
        }
    }

    private static final class StringToIntSerializerWithCtx implements Serializer<String, Integer> {
        private static final StringToIntSerializer INSTANCE = new StringToIntSerializer();
        private final SerializerContext context;

        public StringToIntSerializerWithCtx(SerializerContext context) {
            this.context = context;
        }

        @Override
        public Integer serialize(String element) {
            return Integer.valueOf(element);
        }

        @Override
        public String deserialize(Integer element) {
            return element.toString();
        }
    }

    @Test
    void newCustomSerializerWithoutContext() {
        Serializer<String, Integer> serializer =
                Serializers.newCustomSerializer(StringToIntSerializer.class, null);
        assertThat(serializer, instanceOf(StringToIntSerializer.class));
    }

    @Test
    void newCustomSerializerWithContext() {
        SerializerContext ctx = Mockito.mock(SerializerContext.class);
        StringToIntSerializerWithCtx serializer =
                Serializers.newCustomSerializer(StringToIntSerializerWithCtx.class, ctx);
        assertThat(serializer, instanceOf(StringToIntSerializerWithCtx.class));
        assertThat(serializer.context, sameInstance(ctx));
    }

}