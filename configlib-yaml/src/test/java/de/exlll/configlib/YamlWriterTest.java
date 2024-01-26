package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Dump;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

import static de.exlll.configlib.TestUtils.createPlatformSpecificFilePath;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
class YamlWriterTest {
    private final FileSystem fs = Jimfs.newFileSystem();
    private final Path yamlFile = fs.getPath(createPlatformSpecificFilePath("/tmp/config.yml"));
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
        String s = "";
    }

    @Test
    void writeYamlWithNoComments() {
        writeConfigToFile(A.class);
        writeConfigToStream(A.class);

        assertFileContentEquals("s: ''\n");
        assertStreamContentEquals("s: ''\n");
    }

    @Test
    void writeYamlWithHeaderAndFooter() {
        Consumer<YamlConfigurationProperties.Builder<?>> builderConsumer = builder -> builder
                .header("This is a \n\n \nheader.")
                .footer("That is a\n\n \nfooter.");

        writeConfigToFile(A.class, builderConsumer);
        writeConfigToStream(A.class, builderConsumer);

        String expected = """
                          # This is a\s
                                          
                          # \s
                          # header.

                          s: ''

                          # That is a
                                          
                          # \s
                          # footer.
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
    }

    @Configuration
    static final class B {
        @Comment("Hello")
        String s = "s";
    }

    @Test
    void writeYamlSingleComment() {
        writeConfigToFile(B.class);
        writeConfigToStream(B.class);

        String expected = """
                          # Hello
                          s: s
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
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
        writeConfigToFile(C.class);
        writeConfigToStream(C.class);

        String expected = """
                          # Hello
                          # World
                          mapStringInteger:
                            '1': 2
                          # world
                          # hello
                          mapIntegerString:
                            2: '1'
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
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
        writeConfigToFile(D.class);
        writeConfigToStream(D.class);

        String expected = """
                          # Hello
                                          
                          # \s
                          # World
                          s1: s1
                              
                                          
                          # \s
                          # How are\s
                          # you?
                                          
                          s2: s2
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
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
        writeConfigToFile(E1.class);
        writeConfigToStream(E1.class);

        String expected = """
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
                            j: 10
                          """;
        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
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
        writeConfigToFile(F1.class);
        writeConfigToStream(F1.class);

        String expected = """
                          m1:
                            i: 1
                          f2:
                            # f2.i
                            i: 0
                          # f1.m2
                          m2:
                            i: 1
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
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
        writeConfigToFile(G1.class);
        writeConfigToStream(G1.class);

        String expected = """
                          # g1.g2
                          g2:
                            g3:
                              g4:
                                # g4.g3 1
                                # g4.g3 2
                                g3: 0
                                # g4.g4
                                g4: 0
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
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
    void writeYamlNullFields1() {
        writeConfigToFile(H1.class);
        writeConfigToStream(H1.class);

        String expected = """
                          # h2.1
                          h21:
                            # j
                            j: 10
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
    }

    @Test
    void writeYamlNullFields2() {
        writeConfigToFile(H1.class, builder -> builder.outputNulls(true));
        writeConfigToStream(H1.class, builder -> builder.outputNulls(true));

        String expected = """
                          # h2.1
                          h21:
                            # j
                            j: 10
                          # h2.2
                          h22: null
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
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
        writeConfigToFile(K2.class);
        writeConfigToStream(K2.class);

        String expected = """
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
                            sJ2: sj2
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
    }

    record R1(@Comment("Hello") int i, int j, @Comment("World") int k) {}

    @Configuration
    static class L1 {
        @Comment("l1")
        R1 r1 = new R1(1, 2, 3);
    }

    @Test
    void writeYamlConfigWithRecord() {
        writeConfigToFile(L1.class);
        writeConfigToStream(L1.class);

        String expected = """
                          # l1
                          r1:
                            # Hello
                            i: 1
                            j: 2
                            # World
                            k: 3
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
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
        writeConfigToFile(M2.class);
        writeConfigToStream(M2.class);

        String expected = """
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
                                k: 9
                          """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
    }

    @Test
    void lengthCommonPrefix() {
        List<String> ab = List.of("a", "b");
        List<String> abc = List.of("a", "b", "c");
        List<String> abcd = List.of("a", "b", "c", "d");
        List<String> aef = List.of("a", "e", "f");
        List<String> def = List.of("d", "e", "f");

        assertEquals(2, YamlWriter.lengthCommonPrefix(ab, ab));
        assertEquals(2, YamlWriter.lengthCommonPrefix(abc, ab));
        assertEquals(2, YamlWriter.lengthCommonPrefix(ab, abc));
        assertEquals(2, YamlWriter.lengthCommonPrefix(ab, abcd));
        assertEquals(3, YamlWriter.lengthCommonPrefix(abc, abc));
        assertEquals(3, YamlWriter.lengthCommonPrefix(abc, abcd));

        assertEquals(1, YamlWriter.lengthCommonPrefix(ab, aef));
        assertEquals(1, YamlWriter.lengthCommonPrefix(abcd, aef));

        assertEquals(0, YamlWriter.lengthCommonPrefix(ab, def));
        assertEquals(0, YamlWriter.lengthCommonPrefix(abcd, def));
    }

    String readFile(Charset charset) {
        return TestUtils.readFile(yamlFile, charset);
    }

    String readOutputStream() {
        return outputStream.toString();
    }

    void assertFileContentEquals(String expected, Charset charset) {
        assertEquals(expected, readFile(charset));
    }

    void assertFileContentEquals(String expected) {
        assertFileContentEquals(expected, Charset.defaultCharset());
    }

    void assertStreamContentEquals(String expected) {
        assertEquals(expected, readOutputStream());
    }

    void writeConfigToFile(Class<?> cls) {
        writeConfigToFile(cls, builder -> {});
    }

    <T> void writeConfigToFile(Class<T> cls, Consumer<YamlConfigurationProperties.Builder<?>> configurer) {
        YamlWriterArguments args = argsFromConfig(
                cls,
                Reflect.callNoParamConstructor(cls),
                configurer
        );
        YamlWriter writer = new YamlWriter(yamlFile, args.properties);
        writer.writeYaml(args.yaml, args.nodes);
    }

    void writeConfigToStream(Class<?> cls) {
        writeConfigToStream(cls, builder -> {});
    }

    <T> void writeConfigToStream(Class<T> cls, Consumer<YamlConfigurationProperties.Builder<?>> configurer) {
        YamlWriterArguments args = argsFromConfig(
                cls,
                Reflect.callNoParamConstructor(cls),
                configurer
        );
        YamlWriter writer = new YamlWriter(outputStream, args.properties);
        writer.writeYaml(args.yaml, args.nodes);
    }

    record YamlWriterArguments(
            String yaml,
            Queue<CommentNode> nodes,
            YamlConfigurationProperties properties
    ) {}

    static <T> YamlWriterArguments argsFromConfig(
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
        return new YamlWriterArguments(yaml, nodes, properties);
    }

    @Configuration
    static class N {
        @Comment("テスト")
        String s = "テスト test";
    }

    @Test
    void writeYamlToFileInUTF8WithUnicodeCharacters() {
        Consumer<YamlConfigurationProperties.Builder<?>> builderConsumer = builder -> builder
                .charset(StandardCharsets.UTF_8);

        writeConfigToFile(N.class, builderConsumer);

        String expected = """
                          # テスト
                          s: テスト test
                          """;

        assertFileContentEquals(expected, StandardCharsets.UTF_8);
    }

    @Test
    void writeYamlToFileInASCIIWithUnicodeCharacters() {
        Consumer<YamlConfigurationProperties.Builder<?>> builderConsumer = builder -> builder
                .charset(StandardCharsets.US_ASCII);

        writeConfigToFile(N.class, builderConsumer);

        // UTF-8 characters will be replaced with question mark points
        String expected = """
                          # ???
                          s: ??? test
                          """;

        assertFileContentEquals(expected, StandardCharsets.US_ASCII);
    }

}
