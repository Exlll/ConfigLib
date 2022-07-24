package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static de.exlll.configlib.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class YamlConfigurationStoreTest {
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

    @Configuration
    static final class A {
        String s = "S1";
        @Comment("A comment")
        Integer i = null;
    }

    @Test
    void save() {
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .header("The\nHeader")
                .footer("The\nFooter")
                .outputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();
        YamlConfigurationStore<A> store = new YamlConfigurationStore<>(A.class, properties);
        store.save(new A(), yamlFile);

        String expected =
                """
                # The
                # Header
                                
                S: S1
                # A comment
                I: null
                                
                # The
                # Footer\
                """;
        assertEquals(expected, TestUtils.readFile(yamlFile));
    }

    @Test
    void saveRecord() {
        record R(String s, @Comment("A comment") Integer i) {}
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .header("The\nHeader")
                .footer("The\nFooter")
                .outputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();
        YamlConfigurationStore<R> store = new YamlConfigurationStore<>(R.class, properties);
        store.save(new R("S1", null), yamlFile);

        String expected =
                """
                # The
                # Header
                                
                S: S1
                # A comment
                I: null
                                
                # The
                # Footer\
                """;
        assertEquals(expected, TestUtils.readFile(yamlFile));
    }

    @Configuration
    static final class B {
        String s = "S1";
        String t = "T1";
        Integer i = 1;
    }

    @Test
    void load() throws IOException {
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .inputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();
        YamlConfigurationStore<B> store = new YamlConfigurationStore<>(B.class, properties);

        Files.writeString(
                yamlFile,
                """
                # The
                # Header
                                
                S: S2
                t: T2
                I: null
                                
                # The
                # Footer\
                """
        );

        B config = store.load(yamlFile);
        assertEquals("S2", config.s);
        assertEquals("T1", config.t);
        assertNull(config.i);
    }

    @Test
    void loadRecord() throws IOException {
        record R(String s, String t, Integer i) {}
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .inputNulls(true)
                .setNameFormatter(String::toUpperCase)
                .build();
        YamlConfigurationStore<R> store = new YamlConfigurationStore<>(R.class, properties);

        Files.writeString(
                yamlFile,
                """
                # The
                # Header
                                
                S: S2
                t: T2
                I: null
                                
                # The
                # Footer\
                """
        );

        R config = store.load(yamlFile);
        assertEquals("S2", config.s);
        assertNull(config.t);
        assertNull(config.i);
    }

    @Configuration
    static final class C {
        int i;
    }

    @Test
    void loadInvalidYaml() throws IOException {
        YamlConfigurationStore<C> store = newDefaultStore(C.class);

        Files.writeString(
                yamlFile,
                """
                 - - - - - a
                   a
                """
        );

        assertThrowsConfigurationException(
                () -> store.load(yamlFile),
                "The configuration file at /tmp/config.yml does not contain valid YAML."
        );
    }

    @Test
    void loadEmptyYaml() throws IOException {
        YamlConfigurationStore<C> store = newDefaultStore(C.class);

        Files.writeString(yamlFile, "null");

        assertThrowsConfigurationException(
                () -> store.load(yamlFile),
                "The configuration file at /tmp/config.yml is empty or only contains null."
        );
    }

    @Test
    void loadNonMapYaml() throws IOException {
        YamlConfigurationStore<C> store = newDefaultStore(C.class);

        Files.writeString(yamlFile, "a");

        assertThrowsConfigurationException(
                () -> store.load(yamlFile),
                "The contents of the YAML file at /tmp/config.yml do not represent a " +
                "configuration. A valid configuration file contains a YAML map but instead a " +
                "'class java.lang.String' was found."
        );
    }

    @Configuration
    static final class D {
        Point point = new Point(1, 2);
    }

    @Test
    void saveConfigurationWithInvalidTargetType() {
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .addSerializer(Point.class, TestUtils.POINT_IDENTITY_SERIALIZER)
                .build();
        YamlConfigurationStore<D> store = new YamlConfigurationStore<>(D.class, properties);

        assertThrowsConfigurationException(
                () -> store.save(new D(), yamlFile),
                "The given configuration could not be converted into YAML. \n" +
                "Do all custom serializers produce valid target types?"
        );
    }

    @Test
    void saveCreatesParentDirectoriesIfPropertyTrue() {
        YamlConfigurationStore<A> store = newDefaultStore(A.class);

        Path file = fs.getPath("/a/b/c.yml");
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

        Path file = fs.getPath("/a/b/c.yml");
        assertThrowsRuntimeException(
                () -> store.save(new A(), file),
                "java.nio.file.NoSuchFileException: /a/b/c.yml"
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
        assertEquals("i: 10\nj: 11", readFile(yamlFile));
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
                s: null\
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
                s: s\
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
        assertEquals("i: 20\nj: 11", readFile(yamlFile));
    }

    @Test
    void updateUpdatesFileRecord() throws IOException {
        record R(int i, int j) {}
        YamlConfigurationStore<R> store = newDefaultStore(R.class);

        Files.writeString(yamlFile, "i: 20\nk: 30");
        R config = store.update(yamlFile);
        assertEquals(20, config.i);
        assertEquals(0, config.j);
        assertEquals("i: 20\nj: 0", readFile(yamlFile));
    }

    private static <T> YamlConfigurationStore<T> newDefaultStore(Class<T> configType) {
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder().build();
        return new YamlConfigurationStore<>(configType, properties);
    }
}
