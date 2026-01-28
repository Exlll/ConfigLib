package de.exlll.configlib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
class JsonWriterTest {
    private final FileSystem fs = Jimfs.newFileSystem();
    private final Path jsonFile = fs.getPath(createPlatformSpecificFilePath("/tmp/config.json"));
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
        String s = "";
    }

    @Test
    void writeJsonWithNoComments() {
        writeConfigToFile(A.class);
        writeConfigToStream(A.class);

        assertFileContentEquals("{\n  \"s\" : \"\"\n}\n");
        assertStreamContentEquals("{\n  \"s\" : \"\"\n}\n");
    }

    @Test
    void writeJsonWithHeaderAndFooter() {
        Consumer<JsonConfigurationProperties.Builder<?>> builderConsumer = builder -> builder
                .header("This is a \n\n \nheader.")
                .footer("That is a\n\n \nfooter.");

        writeConfigToFile(A.class, builderConsumer);
        writeConfigToStream(A.class, builderConsumer);

        String expected = """
                {
                  "s" : ""
                }
                """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
    }

    @Configuration
    static final class B {
        String s = "s";
    }

    @Configuration
    static final class C {
        Map<String, Integer> mapStringInteger = Map.of("1", 2);
        Map<Integer, String> mapIntegerString = Map.of(2, "1");
    }

    @Configuration
    static final class D {
        String s1 = "s1";
        String s2 = "s2";
    }

    @Test
    void writeJsonEmptyComments() {
        writeConfigToFile(D.class);
        writeConfigToStream(D.class);

        String expected = """
                {
                  "s1" : "s1",
                  "s2" : "s2"
                }
                """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
    }

    @Configuration
    static final class E1 {
        Map<String, Map<String, Integer>> m = Map.of("c", Map.of("i", 1));
        E2 e2 = new E2();
    }

    @Configuration
    static final class E2 {
        Map<String, Integer> m = Map.of("i", 1);
        E3 e3 = new E3();
        int j = 10;
    }

    @Configuration
    static final class E3 {
        int i = 1;
    }


    @Configuration
    static final class F1 {
        Map<String, Integer> m1 = Map.of("i", 1);
        F2 f2 = new F2();
        Map<String, Integer> m2 = Map.of("i", 1);
    }

    @Configuration
    static final class F2 {
        int i;
    }

    @Configuration
    static final class G1 {
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
        int g3;
        int g4;
    }


    @Configuration
    static final class H1 {
        H2 h21 = new H2();
        H2 h22 = null;
    }

    @Configuration
    static final class H2 {
        int j = 10;
    }

    @Test
    void writeJsonNullFields1() {
        writeConfigToFile(H1.class);
        writeConfigToStream(H1.class);

        String expected = """
                {
                  "h21" : {
                    "j" : 10
                  }
                }
                """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
    }

    @Test
    void writeJsonNullFields2() {
        writeConfigToFile(H1.class, builder -> builder.outputNulls(true));
        writeConfigToStream(H1.class, builder -> builder.outputNulls(true));

        String expected = """
                {
                  "h21" : {
                      "j" : 10
                  },
                  "h22" : null
                }
                """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
    }

    @Configuration
    static class J1 {
        String sJ1 = "sj1";
    }

    static final class J2 extends J1 {
        String sJ2 = "sj2";
    }

    @Configuration
    static class K1 {
        J1 k1J1 = new J1();
        J2 k1J2 = new J2();
    }

    static final class K2 extends K1 {
        J1 k2J1 = new J1();
        J2 k2J2 = new J2();
    }

    @Test
    void writeJsonInheritance() {
        writeConfigToFile(K2.class);
        writeConfigToStream(K2.class);

        String expected = """
                {
                  "k1J1" : {
                      "sJ1" : "sj1"
                  },
                  "k1J2" : {
                      "sJ1" : "sj1",
                      "sJ2" : "sj2"
                  },
                  "k2J1" : {
                      "sJ1" : "sj1"
                  },
                  "k2J2" : {
                      "sJ1" : "sj1",
                      "sJ2" : "sj2"
                  }
                }
                """;

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
    }

    record R1(@Comment("Hello") int i, int j, @Comment("World") int k) {
    }

    @Configuration
    static class L1 {
        R1 r1 = new R1(1, 2, 3);
    }

    @Test
    void writeJsonConfigWithRecord() {
        writeConfigToFile(L1.class);
        writeConfigToStream(L1.class);

        String expected = """
                {
                  "r1" : {
                    "i" : 1,
                    "j" : 2,
                    "k" : 3
                  }
                }""";

        assertFileContentEquals(expected);
        assertStreamContentEquals(expected);
    }

    record R2(@Comment("r2i") int i, int j, @Comment("r2k") int k) {
    }

    record R3(@Comment("r3r2") R2 r2) {
    }

    record R4(@Comment("r4m1") M1 m1, @Comment("r4r3") R3 r3) {
    }

    @Configuration
    static class M1 {
        R2 r2 = new R2(1, 2, 3);
        R3 r3 = new R3(new R2(4, 5, 6));
    }

    @Configuration
    static class M2 {
        R4 r4 = new R4(new M1(), new R3(new R2(7, 8, 9)));
    }

    @Test
    void writeJsonConfigWithRecordNested() {
        writeConfigToFile(M2.class);
        writeConfigToStream(M2.class);

        String expected = """
                {
                  "r4" : {
                      "m1" : {
                          "r2" : {
                              "i" : 1,
                        "j" : 2,
                              "k" : 3
                      },
                          "r3" : {
                              "r2" : {
                                  "i" : 4,
                          "j" : 5,
                                  "k" : 6
                        }
                      }
                    },
                      "r3" : {
                          "r2" : {
                              "i" : 7,
                        "j" : 8,
                              "k" : 9
                      }
                    }
                  }
                }
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

        assertEquals(2, JsonWriter.lengthCommonPrefix(ab, ab));
        assertEquals(2, JsonWriter.lengthCommonPrefix(abc, ab));
        assertEquals(2, JsonWriter.lengthCommonPrefix(ab, abc));
        assertEquals(2, JsonWriter.lengthCommonPrefix(ab, abcd));
        assertEquals(3, JsonWriter.lengthCommonPrefix(abc, abc));
        assertEquals(3, JsonWriter.lengthCommonPrefix(abc, abcd));

        assertEquals(1, JsonWriter.lengthCommonPrefix(ab, aef));
        assertEquals(1, JsonWriter.lengthCommonPrefix(abcd, aef));

        assertEquals(0, JsonWriter.lengthCommonPrefix(ab, def));
        assertEquals(0, JsonWriter.lengthCommonPrefix(abcd, def));
    }

    String readFile(Charset charset) {
        return TestUtils.readFile(jsonFile, charset);
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
        writeConfigToFile(cls, builder -> {
        });
    }

    <T> void writeConfigToFile(Class<T> cls, Consumer<JsonConfigurationProperties.Builder<?>> configurer) {
        JsonWriterArguments args = argsFromConfig(
                cls,
                Reflect.callNoParamConstructor(cls),
                configurer
        );
        JsonWriter writer = new JsonWriter(jsonFile, args.properties);
        writer.writeJson(args.json);
    }

    void writeConfigToStream(Class<?> cls) {
        writeConfigToStream(cls, builder -> {
        });
    }

    <T> void writeConfigToStream(Class<T> cls, Consumer<JsonConfigurationProperties.Builder<?>> configurer) {
        JsonWriterArguments args = argsFromConfig(
                cls,
                Reflect.callNoParamConstructor(cls),
                configurer
        );
        JsonWriter writer = new JsonWriter(outputStream, args.properties);
        writer.writeJson(args.json);
    }

    record JsonWriterArguments(
            String json,
            Queue<CommentNode> nodes,
            JsonConfigurationProperties properties
    ) {
    }

    static <T> JsonWriterArguments argsFromConfig(
            Class<T> t,
            T c,
            Consumer<JsonConfigurationProperties.Builder<?>> configurer
    ) {
        JsonConfigurationProperties.Builder<?> builder = JsonConfigurationProperties.newBuilder();
        configurer.accept(builder);
        JsonConfigurationProperties properties = builder.build();

        ConfigurationSerializer<T> serializer = new ConfigurationSerializer<>(t, properties);
        Map<?, ?> serialize = serializer.serialize(c);
        ObjectMapper mapper = JsonConfigurationStore.newObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(serialize);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        CommentNodeExtractor extractor = new CommentNodeExtractor(properties);
        Queue<CommentNode> nodes = extractor.extractCommentNodes(c);
        return new JsonWriterArguments(json, nodes, properties);
    }

    @Configuration
    static class N {
        String s = "テスト test";
    }

    @Test
    void writeJsonToFileInUTF8WithUnicodeCharacters() {
        Consumer<JsonConfigurationProperties.Builder<?>> builderConsumer = builder -> builder
                .charset(StandardCharsets.UTF_8);

        writeConfigToFile(N.class, builderConsumer);

        String expected = """
                {
                  "s" : "テスト test"
                }
                """;

        assertFileContentEquals(expected, StandardCharsets.UTF_8);
    }

    @Test
    void writeJsonToFileInASCIIWithUnicodeCharacters() {
        Consumer<JsonConfigurationProperties.Builder<?>> builderConsumer = builder -> builder
                .charset(StandardCharsets.US_ASCII);

        writeConfigToFile(N.class, builderConsumer);

        // UTF-8 characters will be replaced with question mark points
        String expected = """
                {
                  "s" : "??? test"
                }
                """;

        assertFileContentEquals(expected, StandardCharsets.US_ASCII);
    }

}