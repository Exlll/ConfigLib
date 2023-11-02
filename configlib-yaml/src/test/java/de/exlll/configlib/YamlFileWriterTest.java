package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import de.exlll.configlib.YamlConfigurationStore.YamlFileWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Dump;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
class YamlFileWriterTest {
    private final FileSystem fs = Jimfs.newFileSystem();
    private final Path yamlFile = fs.getPath(TestUtils.createPlatformSpecificFilePath("/tmp/config.yml"));

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
        String s = "";
    }

    @Test
    void writeYamlWithNoComments() {
        writeConfig(A.class);
        assertFileContentEquals("s: ''");
    }

    @Test
    void writeYamlWithHeaderAndFooter() {
        writeConfig(
                A.class,
                builder -> builder
                        .header("This is a \n\n \nheader.")
                        .footer("That is a\n\n \nfooter.")
        );
        assertFileContentEquals(
                """
                # This is a\s
                                
                # \s
                # header.

                s: ''

                # That is a
                                
                # \s
                # footer.\
                """
        );
    }

    @Configuration
    static final class B {
        @Comment("Hello")
        String s = "s";
    }

    @Test
    void writeYamlSingleComment() {
        writeConfig(B.class);
        assertFileContentEquals(
                """
                # Hello
                s: s\
                """
        );
    }

    @Configuration
    static final class C {
        @Comment({"Hello", "World"})
        Map<String, Integer> mapStringInteger = Map.of("1", 2);
        @Comment({"world", "hello"})
        Map<Integer, String> mapIntegerString = Map.of(2, "1");
    }

    @Test
    void writeYamlMultipleComments() {
        writeConfig(C.class);
        assertFileContentEquals(
                """
                # Hello
                # World
                mapStringInteger:
                  '1': 2
                # world
                # hello
                mapIntegerString:
                  2: '1'\
                """
        );
    }

    @Configuration
    static final class D {
        @Comment({"Hello", "", " ", "World"})
        String s1 = "s1";
        @Comment({"", "", " ", "How are ", "you?", ""})
        String s2 = "s2";
    }

    @Test
    void writeYamlEmptyComments() {
        writeConfig(D.class);
        assertFileContentEquals(
                """
                # Hello
                                
                # \s
                # World
                s1: s1
                    
                                
                # \s
                # How are\s
                # you?
                                
                s2: s2\
                """
        );
    }

    @Configuration
    static final class E1 {
        @Comment("m")
        Map<String, Map<String, Integer>> m = Map.of("c", Map.of("i", 1));
        @Comment("e2")
        E2 e2 = new E2();
    }

    @Configuration
    static final class E2 {
        Map<String, Integer> m = Map.of("i", 1);
        @Comment("e3")
        E3 e3 = new E3();
        @Comment("j")
        int j = 10;
    }

    @Configuration
    static final class E3 {
        @Comment("i")
        int i = 1;
    }

    @Test
    void writeYamlNestedComments1() {
        writeConfig(E1.class);
        assertFileContentEquals(
                """
                # m
                m:
                  c:
                    i: 1
                # e2
                e2:
                  m:
                    i: 1
                  # e3
                  e3:
                    # i
                    i: 1
                  # j
                  j: 10\
                """
        );
    }

    @Configuration
    static final class F1 {
        Map<String, Integer> m1 = Map.of("i", 1);
        F2 f2 = new F2();
        @Comment("f1.m2")
        Map<String, Integer> m2 = Map.of("i", 1);
    }

    @Configuration
    static final class F2 {
        @Comment("f2.i")
        int i;
    }

    @Test
    void writeYamlNestedComments2() {
        writeConfig(F1.class);
        assertFileContentEquals(
                """
                m1:
                  i: 1
                f2:
                  # f2.i
                  i: 0
                # f1.m2
                m2:
                  i: 1\
                """
        );
    }

    @Configuration
    static final class G1 {
        @Comment("g1.g2")
        G2 g2 = new G2();
    }

    @Configuration
    static final class G2 {
        G3 g3 = new G3();
    }

    @Configuration
    static final class G3 {
        G4 g4 = new G4();
    }

    @Configuration
    static final class G4 {
        @Comment({"g4.g3 1", "g4.g3 2"})
        int g3;
        @Comment("g4.g4")
        int g4;
    }

    @Test
    void writeYamlNestedComments3() {
        writeConfig(G1.class);
        assertFileContentEquals(
                """
                # g1.g2
                g2:
                  g3:
                    g4:
                      # g4.g3 1
                      # g4.g3 2
                      g3: 0
                      # g4.g4
                      g4: 0\
                """
        );
    }

    @Configuration
    static final class H1 {
        @Comment("h2.1")
        H2 h21 = new H2();
        @Comment("h2.2")
        H2 h22 = null;
    }

    @Configuration
    static final class H2 {
        @Comment("j")
        int j = 10;
    }

    @Test
    void writeYamlNullFields() {
        writeConfig(H1.class);
        assertFileContentEquals(
                """
                # h2.1
                h21:
                  # j
                  j: 10\
                """
        );
        writeConfig(H1.class, builder -> builder.outputNulls(true));
        assertFileContentEquals(
                """
                # h2.1
                h21:
                  # j
                  j: 10
                # h2.2
                h22: null\
                """
        );
    }

    @Configuration
    static class J1 {
        @Comment("sj1")
        String sJ1 = "sj1";
    }

    static final class J2 extends J1 {
        @Comment("sj2")
        String sJ2 = "sj2";
    }

    @Configuration
    static class K1 {
        @Comment("k1.j1")
        J1 k1J1 = new J1();
        @Comment("k1.j2")
        J2 k1J2 = new J2();
    }

    static final class K2 extends K1 {
        @Comment("k2.j1")
        J1 k2J1 = new J1();
        @Comment("k2.j2")
        J2 k2J2 = new J2();
    }

    @Test
    void writeYamlInheritance() {
        writeConfig(K2.class);
        assertFileContentEquals(
                """
                # k1.j1
                k1J1:
                  # sj1
                  sJ1: sj1
                # k1.j2
                k1J2:
                  # sj1
                  sJ1: sj1
                  # sj2
                  sJ2: sj2
                # k2.j1
                k2J1:
                  # sj1
                  sJ1: sj1
                # k2.j2
                k2J2:
                  # sj1
                  sJ1: sj1
                  # sj2
                  sJ2: sj2\
                """
        );
    }

    record R1(@Comment("Hello") int i, int j, @Comment("World") int k) {}

    @Configuration
    static class L1 {
        @Comment("l1")
        R1 r1 = new R1(1, 2, 3);
    }

    @Test
    void writeYamlConfigWithRecord() {
        writeConfig(L1.class);
        assertFileContentEquals(
                """
                # l1
                r1:
                  # Hello
                  i: 1
                  j: 2
                  # World
                  k: 3\
                """
        );
    }

    record R2(@Comment("r2i") int i, int j, @Comment("r2k") int k) {}

    record R3(@Comment("r3r2") R2 r2) {}

    record R4(@Comment("r4m1") M1 m1, @Comment("r4r3") R3 r3) {}

    @Configuration
    static class M1 {
        @Comment("m1r2")
        R2 r2 = new R2(1, 2, 3);
        @Comment("m1r3")
        R3 r3 = new R3(new R2(4, 5, 6));
    }

    @Configuration
    static class M2 {
        @Comment("m2r4")
        R4 r4 = new R4(new M1(), new R3(new R2(7, 8, 9)));
    }

    @Test
    void writeYamlConfigWithRecordNested() {
        writeConfig(M2.class);
        assertFileContentEquals(
                """
                # m2r4
                r4:
                  # r4m1
                  m1:
                    # m1r2
                    r2:
                      # r2i
                      i: 1
                      j: 2
                      # r2k
                      k: 3
                    # m1r3
                    r3:
                      # r3r2
                      r2:
                        # r2i
                        i: 4
                        j: 5
                        # r2k
                        k: 6
                  # r4r3
                  r3:
                    # r3r2
                    r2:
                      # r2i
                      i: 7
                      j: 8
                      # r2k
                      k: 9\
                """
        );
    }

    @Test
    void lengthCommonPrefix() {
        List<String> ab = List.of("a", "b");
        List<String> abc = List.of("a", "b", "c");
        List<String> abcd = List.of("a", "b", "c", "d");
        List<String> aef = List.of("a", "e", "f");
        List<String> def = List.of("d", "e", "f");

        assertEquals(2, YamlFileWriter.lengthCommonPrefix(ab, ab));
        assertEquals(2, YamlFileWriter.lengthCommonPrefix(abc, ab));
        assertEquals(2, YamlFileWriter.lengthCommonPrefix(ab, abc));
        assertEquals(2, YamlFileWriter.lengthCommonPrefix(ab, abcd));
        assertEquals(3, YamlFileWriter.lengthCommonPrefix(abc, abc));
        assertEquals(3, YamlFileWriter.lengthCommonPrefix(abc, abcd));

        assertEquals(1, YamlFileWriter.lengthCommonPrefix(ab, aef));
        assertEquals(1, YamlFileWriter.lengthCommonPrefix(abcd, aef));

        assertEquals(0, YamlFileWriter.lengthCommonPrefix(ab, def));
        assertEquals(0, YamlFileWriter.lengthCommonPrefix(abcd, def));
    }

    String readFile() {
        return TestUtils.readFile(yamlFile);
    }

    record YamlFileWriterArguments(
            String yaml,
            Queue<CommentNode> nodes,
            YamlConfigurationProperties properties
    ) {}

    void assertFileContentEquals(String expected) {
        assertEquals(expected, readFile());
    }

    void writeConfig(Class<?> cls) {
        writeConfig(cls, builder -> {});
    }

    <T> void writeConfig(Class<T> cls, Consumer<YamlConfigurationProperties.Builder<?>> configurer) {
        YamlFileWriterArguments args = argsFromConfig(
                cls,
                Reflect.callNoParamConstructor(cls),
                configurer
        );
        YamlFileWriter writer = new YamlFileWriter(yamlFile, args.properties);
        writer.writeYaml(args.yaml, args.nodes);
    }

    static <T> YamlFileWriterArguments argsFromConfig(
            Class<T> t,
            T c,
            Consumer<YamlConfigurationProperties.Builder<?>> configurer
    ) {
        YamlConfigurationProperties.Builder<?> builder = YamlConfigurationProperties.newBuilder();
        configurer.accept(builder);
        YamlConfigurationProperties properties = builder.build();

        ConfigurationSerializer<T> serializer = new ConfigurationSerializer<>(t, properties);
        Map<?, ?> serialize = serializer.serialize(c);
        Dump dump = YamlConfigurationStore.newYamlDumper();
        String yaml = dump.dumpToString(serialize);
        CommentNodeExtractor extractor = new CommentNodeExtractor(properties);
        Queue<CommentNode> nodes = extractor.extractCommentNodes(c);
        return new YamlFileWriterArguments(yaml, nodes, properties);
    }
}