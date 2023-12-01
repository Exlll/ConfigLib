package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static de.exlll.configlib.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class YamlConfigurationStoreTest {
    private final FileSystem fs = Jimfs.newFileSystem();

    private final String yamlFilePath = createPlatformSpecificFilePath("/tmp/config.yml");
    private final String abcFilePath = createPlatformSpecificFilePath("/a/b/c.yml");
    private final Path yamlFile = fs.getPath(yamlFilePath);
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(yamlFile.getParent());
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
        YamlConfigurationStore<A> store = newDefaultStore(A.class);

        assertThrowsNullPointerException(
                () -> store.save(null, yamlFile),
                "configuration"
        );

        assertThrowsNullPointerException(
                () -> store.save(new A(), null),
                "configuration file"
        );
    }

    @Test
    void writeRequiresNonNullArguments() {
        YamlConfigurationStore<A> store = newDefaultStore(A.class);

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
        YamlConfigurationStore<A> store = newDefaultStore(A.class);

        assertThrowsNullPointerException(
                () -> store.load(null),
                "configuration file"
        );
    }

    @Test
    void readRequiresNonNullArguments() {
        YamlConfigurationStore<A> store = newDefaultStore(A.class);

        assertThrowsNullPointerException(
                () -> store.read(null),
                "input stream"
        );
    }

    @Test
    void updateRequiresNonNullArguments() {
        YamlConfigurationStore<A> store = newDefaultStore(A.class);

        assertThrowsNullPointerException(
                () -> store.update(null),
                "configuration file"
        );
    }

    @Test
    void saveAndWrite() {
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .header("The\nHeader")
                .footer("The\nFooter")
                .outputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();

        YamlConfigurationStore<A> store = new YamlConfigurationStore<>(A.class, properties);

        store.save(new A(), yamlFile);
        store.write(new A(), outputStream);

        String expected =
                """
                # The
                # Header
                                
                S: S1
                # A comment
                I: null
                                
                # The
                # Footer
                """;

        assertEquals(expected, readFile(yamlFile));
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void saveAndWriteRecord() {
        record R(String s, @Comment("A comment") Integer i) {}
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .header("The\nHeader")
                .footer("The\nFooter")
                .outputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();
        YamlConfigurationStore<R> store = new YamlConfigurationStore<>(R.class, properties);

        store.save(new R("S1", null), yamlFile);
        store.write(new R("S1", null), outputStream);

        String expected =
                """
                # The
                # Header
                                
                S: S1
                # A comment
                I: null
                                
                # The
                # Footer
                """;

        assertEquals(expected, readFile(yamlFile));
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
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .inputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();
        YamlConfigurationStore<B> store = new YamlConfigurationStore<>(B.class, properties);

        String actual = """
                        # The
                        # Header
                                        
                        S: S2
                        t: T2
                        I: null
                                        
                        # The
                        # Footer
                        """;
        Files.writeString(yamlFile, actual);
        outputStream.writeBytes(actual.getBytes());

        B config1 = store.load(yamlFile);
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
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .inputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();
        YamlConfigurationStore<R> store = new YamlConfigurationStore<>(R.class, properties);

        String actual = """
                        # The
                        # Header
                                        
                        S: S2
                        t: T2
                        I: null
                                        
                        # The
                        # Footer
                        """;
        Files.writeString(yamlFile, actual);
        outputStream.writeBytes(actual.getBytes());

        R config1 = store.load(yamlFile);
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
    void loadAndReadInvalidYaml() throws IOException {
        YamlConfigurationStore<C> store = newDefaultStore(C.class);

        String actual = """
                         - - - - - a
                           a
                        """;

        Files.writeString(yamlFile, actual);
        outputStream.writeBytes(actual.getBytes());

        assertThrowsConfigurationException(
                () -> store.load(yamlFile),
                String.format("The configuration file at %s does not contain valid YAML.", yamlFilePath)
        );
        assertThrowsConfigurationException(
                () -> store.read(inputFromOutput()),
                "The input stream does not contain valid YAML."
        );
    }

    @Test
    void loadAndReadEmptyYaml() throws IOException {
        YamlConfigurationStore<C> store = newDefaultStore(C.class);

        Files.writeString(yamlFile, "null");
        outputStream.writeBytes("null".getBytes());

        assertThrowsConfigurationException(
                () -> store.load(yamlFile),
                String.format("The configuration file at %s is empty or only contains null.", yamlFilePath)
        );
        assertThrowsConfigurationException(
                () -> store.read(inputFromOutput()),
                "The input stream is empty or only contains null."
        );
    }

    @Test
    void loadAndReadNonMapYaml() throws IOException {
        YamlConfigurationStore<C> store = newDefaultStore(C.class);

        Files.writeString(yamlFile, "a");
        outputStream.writeBytes("a".getBytes());

        assertThrowsConfigurationException(
                () -> store.load(yamlFile),
                String.format(
                        "The contents of the YAML file at %s do not represent a " +
                        "configuration. A valid configuration file contains a YAML map but instead a " +
                        "'class java.lang.String' was found.", yamlFilePath)
        );
        assertThrowsConfigurationException(
                () -> store.read(inputFromOutput()),
                "The contents of the input stream do not represent a configuration. " +
                "A valid configuration contains a YAML map but instead a " +
                "'class java.lang.String' was found."
        );
    }

    @Configuration
    static final class D {
        Point point = new Point(1, 2);
    }

    @Test
    void saveAndWriteConfigurationWithInvalidTargetType() {
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .addSerializer(Point.class, POINT_IDENTITY_SERIALIZER)
                .build();
        YamlConfigurationStore<D> store = new YamlConfigurationStore<>(D.class, properties);

        String exceptionMessage = "The given configuration could not be converted into YAML. \n" +
                                  "Do all custom serializers produce valid target types?";
        assertThrowsConfigurationException(() -> store.save(new D(), yamlFile), exceptionMessage);
        assertThrowsConfigurationException(() -> store.write(new D(), outputStream), exceptionMessage);
    }

    @Test
    void saveCreatesParentDirectoriesIfPropertyTrue() {
        YamlConfigurationStore<A> store = newDefaultStore(A.class);

        Path file = fs.getPath(abcFilePath);
        store.save(new A(), file);

        assertTrue(Files.exists(file.getParent()));
        assertTrue(Files.exists(file));
    }

    @Test
    void saveDoesNotCreateParentDirectoriesIfPropertyFalse() {
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .createParentDirectories(false)
                .build();
        YamlConfigurationStore<A> store = new YamlConfigurationStore<>(A.class, properties);

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
        YamlConfigurationStore<E> store = newDefaultStore(E.class);

        assertFalse(Files.exists(yamlFile));
        E config = store.update(yamlFile);
        assertEquals("i: 10\nj: 11\n", readFile(yamlFile));
        assertEquals(10, config.i);
        assertEquals(11, config.j);
    }

    @Test
    void updateCreatesConfigurationFileIfItDoesNotExistRecord() {
        record R(int i, char c, String s) {}
        YamlConfigurationStore<R> store = new YamlConfigurationStore<>(
                R.class,
                YamlConfigurationProperties.newBuilder().outputNulls(true).build()
        );

        assertFalse(Files.exists(yamlFile));
        R config = store.update(yamlFile);
        assertEquals(
                """
                i: 0
                c: "\\0"
                s: null
                """,
                readFile(yamlFile)
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
        YamlConfigurationStore<R> store = newDefaultStore(R.class);

        assertFalse(Files.exists(yamlFile));
        R config = store.update(yamlFile);
        assertEquals(
                """
                i: 10
                c: c
                s: s
                """,
                readFile(yamlFile)
        );
        assertEquals(10, config.i);
        assertEquals('c', config.c);
        assertEquals("s", config.s);
    }

    @Test
    void updateLoadsConfigurationFileIfItDoesExist() throws IOException {
        YamlConfigurationStore<E> store = newDefaultStore(E.class);

        Files.writeString(yamlFile, "i: 20");
        E config = store.update(yamlFile);
        assertEquals(20, config.i);
        assertEquals(11, config.j);
    }

    @Test
    void updateLoadsConfigurationFileIfItDoesExistRecord() throws IOException {
        record R(int i, int j) {}
        YamlConfigurationStore<R> store = newDefaultStore(R.class);

        Files.writeString(yamlFile, "i: 20");
        R config = store.update(yamlFile);
        assertEquals(20, config.i);
        assertEquals(0, config.j);
    }

    @Test
    void updateUpdatesFile() throws IOException {
        YamlConfigurationStore<E> store = newDefaultStore(E.class);

        Files.writeString(yamlFile, "i: 20\nk: 30");
        E config = store.update(yamlFile);
        assertEquals(20, config.i);
        assertEquals(11, config.j);
        assertEquals("i: 20\nj: 11\n", readFile(yamlFile));
    }

    @Test
    void updateUpdatesFileRecord() throws IOException {
        record R(int i, int j) {}
        YamlConfigurationStore<R> store = newDefaultStore(R.class);

        Files.writeString(yamlFile, "i: 20\nk: 30");
        R config = store.update(yamlFile);
        assertEquals(20, config.i);
        assertEquals(0, config.j);
        assertEquals("i: 20\nj: 0\n", readFile(yamlFile));
    }

    private static <T> YamlConfigurationStore<T> newDefaultStore(Class<T> configType) {
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder().build();
        return new YamlConfigurationStore<>(configType, properties);
    }

    private InputStream inputFromOutput() {
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
