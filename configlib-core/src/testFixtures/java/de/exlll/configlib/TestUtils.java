package de.exlll.configlib;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TestUtils {
    public static final PointSerializer POINT_SERIALIZER = new PointSerializer();
    public static final PointIdentitySerializer POINT_IDENTITY_SERIALIZER =
            new PointIdentitySerializer();
    public static final CustomBigIntegerSerializer CUSTOM_BIG_INTEGER_SERIALIZER
            = new CustomBigIntegerSerializer();

    public static Field getField(Class<?> cls, String fieldName) {
        return Arrays.stream(cls.getDeclaredFields())
                .filter(field -> field.getName().equals(fieldName))
                .findAny()
                .orElseThrow();
    }

    public static void assertThrowsNullPointerException(Executable executable, String argumentName) {
        String msg = "The " + argumentName + " must not be null.";
        assertThrowsException(NullPointerException.class, executable, msg);
    }

    public static void assertThrowsIllegalArgumentException(
            Executable executable,
            String expectedExceptionMessage
    ) {
        assertThrowsException(IllegalArgumentException.class, executable, expectedExceptionMessage);
    }

    public static ConfigurationException assertThrowsConfigurationException(Executable executable) {
        return assertThrows(ConfigurationException.class, executable);
    }

    public static void assertThrowsConfigurationException(
            Executable executable,
            String expectedExceptionMessage
    ) {
        assertThrowsException(ConfigurationException.class, executable, expectedExceptionMessage);
    }

    public static void assertThrowsConfigurationException(
            Executable executable,
            Matcher<String> matcher
    ) {
        final var exception = assertThrows(ConfigurationException.class, executable);
        assertThat(exception.getMessage(), matcher);
    }

    public static void assertThrowsRuntimeException(
            Executable executable,
            String expectedExceptionMessage
    ) {
        assertThrowsException(RuntimeException.class, executable, expectedExceptionMessage);
    }

    public static <T extends Exception> void assertThrowsException(
            Class<T> exceptionType,
            Executable executable,
            String expectedExceptionMessage
    ) {
        T exception = Assertions.assertThrows(exceptionType, executable);
        assertThat(exception.getMessage(), is(expectedExceptionMessage));
    }

    public static final class CustomBigIntegerSerializer implements Serializer<BigInteger, String> {

        @Override
        public String serialize(BigInteger element) {
            return element.multiply(BigInteger.TWO).toString();
        }

        @Override
        public BigInteger deserialize(String element) {
            return new BigInteger(element).divide(BigInteger.TWO);
        }
    }

    public static final class PointSerializer implements Serializer<Point, String> {
        @Override
        public String serialize(Point element) {
            return element.x + ":" + element.y;
        }

        @Override
        public Point deserialize(String element) {
            String[] parts = element.split(":");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return new Point(x, y);
        }
    }

    public static final class IdentifiableSerializer<S, T> implements Serializer<S, T> {
        public int identifier;

        public IdentifiableSerializer(int identifier) {
            this.identifier = identifier;
        }

        public static IdentifiableSerializer<Integer, Integer> of(int identifier) {
            return new IdentifiableSerializer<>(identifier);
        }

        @Override
        public T serialize(S element) {
            return null;
        }

        @Override
        public S deserialize(T element) {
            return null;
        }
    }

    public static final class PointIdentitySerializer implements Serializer<Point, Point> {
        @Override
        public Point serialize(Point element) {
            return element;
        }

        @Override
        public Point deserialize(Point element) {
            return element;
        }
    }

    public record IdentitySerializer(SerializerContext context)
            implements Serializer<Object, Object> {

        @Override
        public Object serialize(Object element) {
            return element;
        }

        @Override
        public Object deserialize(Object element) {
            return element;
        }
    }

    public record ThrowingSerializer(SerializerContext context)
            implements Serializer<Object, Object> {

        public ThrowingSerializer {
            throw new UnsupportedOperationException(context.toString());
        }

        @Override
        public Object serialize(Object element) {
            throw new UnsupportedOperationException(element.toString());
        }

        @Override
        public Object deserialize(Object element) {
            throw new UnsupportedOperationException(element.toString());
        }
    }

    public static final class DoubleIntSerializer
            implements Serializer<Integer, Integer> {

        @Override
        public Integer serialize(Integer element) {
            return element * 2;
        }

        @Override
        public Integer deserialize(Integer element) {
            return element / 2;
        }
    }

    @SafeVarargs
    public static <E> List<E> asList(E... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    @SafeVarargs
    public static <E> Set<E> asSet(E... elements) {
        return new LinkedHashSet<>(Arrays.asList(elements));
    }

    public static <K, V> Map<K, V> asMap() {
        return new LinkedHashMap<>();
    }

    public static <K, V> Map<K, V> asMap(K k1, V v1) {
        final Map<K, V> result = new LinkedHashMap<>();
        result.put(k1, v1);
        return result;
    }

    public static <K, V> Map<K, V> asMap(K k1, V v1, K k2, V v2) {
        final Map<K, V> result = new LinkedHashMap<>();
        result.put(k1, v1);
        result.put(k2, v2);
        return result;
    }

    public static <K, V> Map<K, V> asMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        final Map<K, V> result = new LinkedHashMap<>();
        result.put(k1, v1);
        result.put(k2, v2);
        result.put(k3, v3);
        return result;
    }

    public static <K, V> Map<K, V> asMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        final Map<K, V> result = new LinkedHashMap<>();
        result.put(k1, v1);
        result.put(k2, v2);
        result.put(k3, v3);
        result.put(k4, v4);
        return result;
    }

    public static <K, V> Map<K, V> entriesAsMap(MEntry... entries) {
        final Map<Object, Object> result = new LinkedHashMap<>();
        Arrays.stream(entries).forEach(entry -> result.put(entry.getKey(), entry.getValue()));

        // Suppressing this warning might lead to an exception.
        // Using proper generics for the MEntry class is possible.
        // However, doing so increases the compilation time by several seconds
        @SuppressWarnings("unchecked")
        Map<K, V> returnResult = (Map<K, V>) result;
        return returnResult;
    }

    public static <K, V> Map<K, V> extend(Map<K, V> base, MEntry... entries) {
        final Map<K, V> result = new LinkedHashMap<>(base);
        final Map<K, V> ext = entriesAsMap(entries);
        result.putAll(ext);
        return result;
    }

    public static MEntry entry(Object key, Object val) {
        return new MEntry(key, val);
    }

    public static final class MEntry implements Map.Entry<Object, Object> {
        private final Object key;
        private final Object val;

        public MEntry(Object key, Object val) {
            this.key = key;
            this.val = val;
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return val;
        }

        @Override
        public Object setValue(Object value) {
            throw new UnsupportedOperationException("setValue not supported");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MEntry mEntry = (MEntry) o;
            return Objects.equals(key, mEntry.key) && Objects.equals(val, mEntry.val);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, val);
        }
    }


    public static <T, C extends Collection<T[]>> boolean collectionOfArraysDeepEquals(
            C collection1,
            C collection2,
            Supplier<Collection<List<T>>> collectionFactory
    ) {
        Collection<List<T>> c1 = collection1.stream().map(Arrays::asList)
                .collect(Collectors.toCollection(collectionFactory));
        Collection<List<T>> c2 = collection2.stream().map(Arrays::asList)
                .collect(Collectors.toCollection(collectionFactory));
        return c1.equals(c2);
    }

    public static String readFile(Path file, Charset charset) {
        try {
            return Files.readString(file, charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readFile(Path file) {
        return readFile(file, Charset.defaultCharset());
    }

    public static ConfigurationElement<?> fieldAsElement(Class<?> type, String fieldName) {
        Field field = getField(type, fieldName);
        return new ConfigurationElements.FieldElement(field);
    }

    public static Method getMethod(Class<?> type, String methodName) {
        final List<Method> methods = getMethods(type, methodName);
        assertThat(methods, hasSize(1));
        return methods.get(0);
    }

    public static List<Method> getMethods(Class<?> type, String methodName) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.getName().equals(methodName))
                .toList();
    }

    /*
        There were absolute path errors when trying to pass the unit tests
        on different platforms like Windows. Currently, Jimfs(1.3.0) lacks support
        for both absolutes paths and relative paths on Windows, see:
        - https://github.com/google/jimfs/issues/69
        - https://github.com/google/jimfs/blob/master/jimfs/src/main/java/com/google/common/jimfs/WindowsPathType.java

        So, in order to run unit tests on Windows. We have to translate the current
        path declarations to fulfill the non-unix system's needs.
    */
    public static String createPlatformSpecificFilePath(String path) {
        if (!isWindows()) return path;

        return String.format("C:%s", path.replace("/", File.separator));
    }

    public static boolean isWindows() {
        final String platform = System.getProperty("os.name");
        return platform.contains("Windows");
    }

    public static List<String> createListOfPlatformSpecificFilePaths(String... paths) {
        return Stream.of(paths).map(TestUtils::createPlatformSpecificFilePath).toList();
    }

    public record MapEnvironment(Map<String, String> map) implements Environment {
        @Override
        public String getValue(String environmentVariable) {
            return map.get(environmentVariable);
        }

        @Override
        public Map<String, String> getEnvironment() {
            return map;
        }
    }
}
