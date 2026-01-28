package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import de.exlll.configlib.ConfigurationProperties.EnvVarResolutionConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static de.exlll.configlib.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class JsonConfigurationStoreTest {
    private final FileSystem fs = Jimfs.newFileSystem();

    private final String jsonFilePath = createPlatformSpecificFilePath("/tmp/config.json");
    private final String abcFilePath = createPlatformSpecificFilePath("/a/b/c.json");
    private final Path jsonFile = fs.getPath(jsonFilePath);
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(jsonFile.getParent());
    }

    @AfterEach
    void tearDown() throws IOException {
        fs.close();
    }

    @Configuration
    static final class A {
        String s = "S1";
        @Comment("A comment")
        Integer i = null;
    }

    @Test
    void saveRequiresNonNullArguments() {
        JsonConfigurationStore<A> store = newDefaultStore(A.class);

        assertThrowsNullPointerException(
                () -> store.save(null, jsonFile),
                "configuration"
        );

        assertThrowsNullPointerException(
                () -> store.save(new A(), null),
                "configuration file"
        );
    }

    @Test
    void writeRequiresNonNullArguments() {
        JsonConfigurationStore<A> store = newDefaultStore(A.class);

        assertThrowsNullPointerException(
                () -> store.write(null, new ByteArrayOutputStream()),
                "configuration"
        );

        assertThrowsNullPointerException(
                () -> store.write(new A(), null),
                "output stream"
        );
    }

    @Test
    void loadRequiresNonNullArguments() {
        JsonConfigurationStore<A> store = newDefaultStore(A.class);

        assertThrowsNullPointerException(
                () -> store.load(null),
                "configuration file"
        );
    }

    @Test
    void readRequiresNonNullArguments() {
        JsonConfigurationStore<A> store = newDefaultStore(A.class);

        assertThrowsNullPointerException(
                () -> store.read(null),
                "input stream"
        );
    }

    @Test
    void updateRequiresNonNullArguments() {
        JsonConfigurationStore<A> store = newDefaultStore(A.class);

        assertThrowsNullPointerException(
                () -> store.update(null),
                "configuration file"
        );
    }

    @Test
    void saveAndWrite() {
        JsonConfigurationProperties properties = JsonConfigurationProperties.newBuilder()
                .header("The\nHeader")
                .footer("The\nFooter")
                .outputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();

        JsonConfigurationStore<A> store = new JsonConfigurationStore<>(A.class, properties);

        store.save(new A(), jsonFile);
        store.write(new A(), outputStream);

        String expected =
                """
                {
                  "S" : "S1",
                  "I" : null
                }
                """;

        assertEquals(expected, readFile(jsonFile));
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void saveAndWriteRecord() {
        record R(String s, @Comment("A comment") Integer i) {}
        JsonConfigurationProperties properties = JsonConfigurationProperties.newBuilder()
                .header("The\nHeader")
                .footer("The\nFooter")
                .outputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();
        JsonConfigurationStore<R> store = new JsonConfigurationStore<>(R.class, properties);

        store.save(new R("S1", null), jsonFile);
        store.write(new R("S1", null), outputStream);

        String expected =
                """
                {
                  "S" : "S1",
                  "I" : null
                }
                """;

        assertEquals(expected, readFile(jsonFile));
        assertEquals(expected, outputStream.toString());
    }

    @Configuration
    static final class B {
        String s = "S1";
        String t = "T1";
        Integer i = 1;
    }

    @Test
    void loadAndRead() throws IOException {
        JsonConfigurationProperties properties = JsonConfigurationProperties.newBuilder()
                .inputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();
        JsonConfigurationStore<B> store = new JsonConfigurationStore<>(B.class, properties);

        String actual = """
                        {
                          "S" : "S2",
                          "t" : "T2",
                          "I" : null
                        }
                        """;
        Files.writeString(jsonFile, actual);
        outputStream.writeBytes(actual.getBytes());

        B config1 = store.load(jsonFile);
        assertEquals("S2", config1.s);
        assertEquals("T1", config1.t);
        assertNull(config1.i);

        B config2 = store.read(inputFromOutput());
        assertEquals("S2", config2.s);
        assertEquals("T1", config2.t);
        assertNull(config2.i);
    }

    @Test
    void loadAndReadRecord() throws IOException {
        record R(String s, String t, Integer i) {}
        JsonConfigurationProperties properties = JsonConfigurationProperties.newBuilder()
                .inputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();
        JsonConfigurationStore<R> store = new JsonConfigurationStore<>(R.class, properties);

        String actual = """
                        {
                          "S" : "S2",
                          "t" : "T2",
                          "I" : null
                        }
                        """;
        Files.writeString(jsonFile, actual);
        outputStream.writeBytes(actual.getBytes());

        R config1 = store.load(jsonFile);
        assertEquals("S2", config1.s);
        assertNull(config1.t);
        assertNull(config1.i);

        R config2 = store.read(inputFromOutput());
        assertEquals("S2", config2.s);
        assertNull(config2.t);
        assertNull(config2.i);
    }

    @Configuration
    static final class C {
        int i;
    }

    @Test
    void loadAndReadInvalidJson() throws IOException {
        JsonConfigurationStore<C> store = newDefaultStore(C.class);

        String actual = "{ invalid json";

        Files.writeString(jsonFile, actual);
        outputStream.writeBytes(actual.getBytes());

        assertThrowsConfigurationException(
                () -> store.load(jsonFile),
                String.format("The configuration file at %s could not be loaded.", jsonFilePath)
        );
        assertThrowsConfigurationException(
                () -> store.read(inputFromOutput()),
                "The input stream does not contain valid JSON."
        );
    }

    @Test
    void loadAndReadEmptyJson() throws IOException {
        JsonConfigurationStore<C> store = newDefaultStore(C.class);

        Files.writeString(jsonFile, "null");
        outputStream.writeBytes("null".getBytes());

        assertThrowsConfigurationException(
                () -> store.load(jsonFile),
                "The JSON content is empty or null."
        );
        assertThrowsConfigurationException(
                () -> store.read(inputFromOutput()),
                "The JSON content is empty or null."
        );
    }

    @Test
    void loadAndReadNonMapJson() throws IOException {
        JsonConfigurationStore<C> store = newDefaultStore(C.class);

        Files.writeString(jsonFile, "\"a\"");
        outputStream.writeBytes("\"a\"".getBytes());

        assertThrowsConfigurationException(
                () -> store.load(jsonFile),
                "The JSON content does not represent a configuration. " +
                        "A valid configuration must be a JSON object (Map) but found " +
                        "'String'."
        );
        assertThrowsConfigurationException(
                () -> store.read(inputFromOutput()),
                "The JSON content does not represent a configuration. " +
                        "A valid configuration must be a JSON object (Map) but found " +
                        "'String'."
        );
    }

    @Configuration
    static final class D {
        Point point = new Point(1, 2);
    }

    @Test
    void saveAndWriteConfigurationWithInvalidTargetType() {
        JsonConfigurationProperties properties = JsonConfigurationProperties.newBuilder()
                .addSerializer(Point.class, POINT_IDENTITY_SERIALIZER)
                .build();
        JsonConfigurationStore<D> store = new JsonConfigurationStore<>(D.class, properties);

        String exceptionMessage =
                "Serialization of value 'java.awt.Point[x=1,y=2]' for element " +
                        "'java.awt.Point de.exlll.configlib.JsonConfigurationStoreTest$D.point' of " +
                        "type 'class de.exlll.configlib.JsonConfigurationStoreTest$D' failed. " +
                        "The serializer produced an invalid target type.";
        assertThrowsConfigurationException(() -> store.save(new D(), jsonFile), exceptionMessage);
        assertThrowsConfigurationException(() -> store.write(new D(), outputStream), exceptionMessage);
    }

    @Test
    void saveCreatesParentDirectoriesIfPropertyTrue() {
        JsonConfigurationStore<A> store = newDefaultStore(A.class);

        Path file = fs.getPath(abcFilePath);
        store.save(new A(), file);

        assertTrue(Files.exists(file.getParent()));
        assertTrue(Files.exists(file));
    }

    @Test
    void saveDoesNotCreateParentDirectoriesIfPropertyFalse() {
        JsonConfigurationProperties properties = JsonConfigurationProperties.newBuilder()
                .createParentDirectories(false)
                .build();
        JsonConfigurationStore<A> store = new JsonConfigurationStore<>(A.class, properties);

        Path file = fs.getPath(abcFilePath);
        assertThrowsRuntimeException(
                () -> store.save(new A(), file),
                String.format("java.nio.file.NoSuchFileException: %s", abcFilePath)
        );
    }

    @Configuration
    static final class E {
        int i = 10;
        int j = 11;

        public E() {}

        public E(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    @Test
    void updateCreatesConfigurationFileIfItDoesNotExist() {
        JsonConfigurationStore<E> store = newDefaultStore(E.class);

        assertFalse(Files.exists(jsonFile));
        E config = store.update(jsonFile);
        assertEquals("{\n  \"i\" : 10,\n  \"j\" : 11\n}\n", readFile(jsonFile));
        assertEquals(10, config.i);
        assertEquals(11, config.j);
    }

    @Test
    void updateCreatesConfigurationFileIfItDoesNotExistRecord() {
        record R(int i, char c, String s) {}
        JsonConfigurationStore<R> store = new JsonConfigurationStore<>(
                R.class,
                JsonConfigurationProperties.newBuilder().outputNulls(true).build()
        );

        assertFalse(Files.exists(jsonFile));
        R config = store.update(jsonFile);
        assertEquals(
                """
                {
                  "i" : 0,
                  "c" : "\\u0000",
                  "s" : null
                }
                """,
                readFile(jsonFile)
        );
        assertEquals(0, config.i);
        assertEquals('\0', config.c);
        assertNull(config.s);
    }

    @Test
    void updateCreatesConfigurationFileIfItDoesNotExistRecordNoParamCtor() {
        record R(int i, char c, String s) {
            R() {this(10, 'c', "s");}
        }
        JsonConfigurationStore<R> store = newDefaultStore(R.class);

        assertFalse(Files.exists(jsonFile));
        R config = store.update(jsonFile);
        assertEquals(
                """
                {
                  "i" : 10,
                  "c" : "c",
                  "s" : "s"
                }
                """,
                readFile(jsonFile)
        );
        assertEquals(10, config.i);
        assertEquals('c', config.c);
        assertEquals("s", config.s);
    }

    @Test
    void updateLoadsConfigurationFileIfItDoesExist() throws IOException {
        JsonConfigurationStore<E> store = newDefaultStore(E.class);

        Files.writeString(jsonFile, "{\"i\": 20}");
        E config = store.update(jsonFile);
        assertEquals(20, config.i);
        assertEquals(11, config.j);
    }

    @Test
    void updateLoadsConfigurationFileIfItDoesExistRecord() throws IOException {
        record R(int i, int j) {}
        JsonConfigurationStore<R> store = newDefaultStore(R.class);

        Files.writeString(jsonFile, "{\"i\": 20}");
        R config = store.update(jsonFile);
        assertEquals(20, config.i);
        assertEquals(0, config.j);
    }

    @Test
    void updateUpdatesFile() throws IOException {
        JsonConfigurationStore<E> store = newDefaultStore(E.class);

        Files.writeString(jsonFile, "{\"i\": 20, \"k\": 30}");
        E config = store.update(jsonFile);
        assertEquals(20, config.i);
        assertEquals(11, config.j);
        assertEquals("{\n  \"i\" : 20,\n  \"j\" : 11\n}\n", readFile(jsonFile));
    }

    @Test
    void updateUpdatesFileRecord() throws IOException {
        record R(int i, int j) {}
        JsonConfigurationStore<R> store = newDefaultStore(R.class);

        Files.writeString(jsonFile, "{\"i\": 20, \"k\": 30}");
        R config = store.update(jsonFile);
        assertEquals(20, config.i);
        assertEquals(0, config.j);
        assertEquals("{\n  \"i\" : 20,\n  \"j\" : 0\n}\n", readFile(jsonFile));
    }

    private static <T> JsonConfigurationStore<T> newDefaultStore(Class<T> configType) {
        JsonConfigurationProperties properties = JsonConfigurationProperties.newBuilder().build();
        return new JsonConfigurationStore<>(configType, properties);
    }

    private InputStream inputFromOutput() {
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @Test
    void allJsonIntegersAreLoadedAsLongs() throws IOException {
        final var mapper = JsonConfigurationStore.newObjectMapper();
        final var map = mapper.readValue(
                """
                {
                  "a": 1,
                  "b": 2147483647,
                  "c": 2147483648,
                  "d": -2147483648,
                  "e": -2147483649
                }
                """,
                Map.class
        );
        assertThat(map.get("a"), is(1L));
        assertThat(map.get("b"), is(2147483647L));
        assertThat(map.get("c"), is(2147483648L));
        assertThat(map.get("d"), is(-2147483648L));
        assertThat(map.get("e"), is(-2147483649L));
    }

    @Configuration
    static final class F {
        String s = "S1";
        Integer i = 2;
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void updateResolvesEnvVarsIfFileDoesOrDoesNotExist(boolean createFile) {
        JsonConfigurationProperties properties = JsonConfigurationProperties.newBuilder()
                .outputNulls(true)
                .setEnvVarResolutionConfiguration(EnvVarResolutionConfiguration.resolveEnvVarsWithPrefix("PREFIX", false))
                .build();
        JsonConfigurationStore<F> store = new JsonConfigurationStore<>(
                F.class,
                properties,
                new MapEnvironment(Map.of(
                        "PREFIX_S", "S2",
                        "PREFIX_I", "10"
                ))
        );
        if (createFile) store.save(new F(), jsonFile);
        F config = store.update(jsonFile);
        assertThat(config.s, is("S2"));
        assertThat(config.i, is(10));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void tryCreateParentDirectoriesDoesNotThrowIfParentIsNull(boolean createParentDirectories) {
        JsonConfigurationProperties properties = JsonConfigurationProperties.newBuilder()
                .createParentDirectories(createParentDirectories)
                .build();
        JsonConfigurationStore<A> store = new JsonConfigurationStore<>(
                A.class,
                properties
        );

        Path path = fs.getPath("config.json");
        assertDoesNotThrow(() -> store.tryCreateParentDirectories(path));
    }

    private record ThrowingWhileSerializingSerializer()
            implements Serializer<String, String> {

        @Override
        public String serialize(String element) {
            throw new UnsupportedOperationException(element);
        }

        @Override
        public String deserialize(String element) {
            return element;
        }
    }

    @Configuration
    private static final class G {
        private String content = "-";
    }

    @Test
    void saveDoesNotOverwriteConfigurationFileContentsOnJsonDumpFailure() throws IOException {
        JsonConfigurationStore<G> store = new JsonConfigurationStore<>(
                G.class,
                JsonConfigurationProperties.newBuilder()
                        .addSerializer(String.class, new ThrowingWhileSerializingSerializer())
                        .build()
        );

        String content = "{\"content\": \"abcde\"}";
        Files.writeString(jsonFile, content);

        G config = store.load(jsonFile);
        assertThat(config.content, is("abcde"));
        assertThrows(
                UnsupportedOperationException.class,
                () -> store.save(config, jsonFile)
        );
        assertThat(readFile(jsonFile), is(content));
    }

    @Test
    void writeDoesNotOverwriteStreamContentsOnJsonDumpFailure() {
        JsonConfigurationStore<G> store = new JsonConfigurationStore<>(
                G.class,
                JsonConfigurationProperties.newBuilder()
                        .addSerializer(String.class, new ThrowingWhileSerializingSerializer())
                        .build()
        );

        String content = "{\"content\": \"abcde\"}";
        outputStream.writeBytes(content.getBytes());

        G config = store.read(inputFromOutput());
        assertThat(config.content, is("abcde"));
        assertThrows(
                UnsupportedOperationException.class,
                () -> store.write(config, outputStream)
        );
        assertThat(outputStream.toString(), is(content));
    }
}