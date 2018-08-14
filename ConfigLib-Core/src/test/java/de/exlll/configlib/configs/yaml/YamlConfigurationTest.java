package de.exlll.configlib.configs.yaml;

import com.google.common.jimfs.Jimfs;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.classes.TestClass;
import de.exlll.configlib.configs.yaml.YamlConfiguration.YamlProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static de.exlll.configlib.util.CollectionFactory.listOf;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unused")
class YamlConfigurationTest {
    private FileSystem fileSystem;
    private Path testPath, configPath;

    @BeforeEach
    void setUp() {
        fileSystem = Jimfs.newFileSystem();
        testPath = fileSystem.getPath("/a/b/test.yml");
        configPath = fileSystem.getPath("/a/b/config.yml");
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void loadAndSaveExecutesPostLoadHook() throws IOException {
        class A extends YamlConfiguration {
            int i = 0;

            protected A() { super(configPath, YamlProperties.DEFAULT); }

            @Override
            protected void postLoad() {
                i++;
            }
        }

        A a = new A();
        a.loadAndSave();
        assertThat(a.i, is(1));
    }

    @Test
    void loadAndSaveSavesConfiguration() throws IOException {
        YamlConfiguration configuration = new TestClass(
                configPath, TestClass.TEST_VALUES
        );
        configuration.loadAndSave();
        assertThat(Files.exists(configPath), is(true));

        YamlConfiguration load = new TestClass(configPath);
        load.load();
        assertThat(load, is(TestClass.TEST_VALUES));
    }

    @Test
    void loadAndSaveLoadsConfiguration() throws IOException {
        new TestClass(configPath, TestClass.TEST_VALUES).save();

        YamlConfiguration configuration = new TestClass(configPath);
        configuration.loadAndSave();
        assertThat(configuration, is(TestClass.TEST_VALUES));
        assertThat(Files.exists(configPath), is(true));
    }

    @Test
    void loadLoadsConfig() throws IOException {
        setupConfigPath();
        Configuration configuration = new TestClass(configPath);
        assertThat(configuration, is(not(TestClass.TEST_VALUES)));
        configuration.load();
        assertThat(configuration, is((TestClass.TEST_VALUES)));
    }

    private void setupConfigPath() throws IOException {
        Configuration configuration = new TestClass(
                configPath, TestClass.TEST_VALUES
        );
        configuration.save();
    }

    @Test
    void loadThrowsExceptionIfTypesDontMatch() throws IOException {
        Configuration configuration = new TestClass(configPath);
        configuration.save();
        assertThrows(IllegalArgumentException.class, configuration::load);
    }

    @Test
    void saveCreatesConfig() throws IOException {
        assertThat(Files.exists(testPath), is(false));
        Configuration configuration = new TestClass(testPath);
        configuration.save();
        assertThat(Files.exists(testPath), is(true));
    }

    @Test
    void saveDumpsYaml() throws IOException {
        Configuration configuration = new TestClass(
                testPath, TestClass.TEST_VALUES
        );
        configuration.save();
        assertThat(readConfig(testPath), is(TEST_CLASS_YML));
    }

    @Test
    void saveDumpsPrependedAndAppendedComments() throws IOException {
        class A extends YamlConfiguration {
            int i;

            protected A(YamlProperties properties) {
                super(testPath, properties);
            }
        }
        YamlProperties properties = YamlProperties.builder()
                .setPrependedComments(listOf("AB", "", "CD"))
                .setAppendedComments(listOf("AB", "", "CD"))
                .build();
        new A(properties).save();
        assertThat(readConfig(testPath), is(PRE_AND_APPENDED_COMMENTS_YML));
    }

    @Test
    void saveDumpsClassComments() throws IOException {
        @Comment({"1", "", "2"})
        class A extends YamlConfiguration {
            @Comment("a")
            private int a = 1;
            @Comment({"b", "x"})
            private int b = 2;
            @Comment({"c", "", "y"})
            private int c = 3;
            private int d = 4;

            protected A() { super(testPath); }
        }
        new A().save();
        assertThat(readConfig(testPath), is(CLASS_COMMENTS_YML));
    }

    @Test
    void saveDumpsFieldComments() throws IOException {
        class A extends YamlConfiguration {
            @Comment("a")
            private int a = 1;
            @Comment({"b", "x"})
            private int b = 2;
            @Comment({"c", "", "y"})
            private int c = 3;
            private int d = 4;

            protected A() { super(testPath); }
        }
        new A().save();
        assertThat(readConfig(testPath), is(FIELD_COMMENTS_YML));
    }

    private String readConfig(Path path) throws IOException {
        return Files.lines(path).collect(joining("\n"));
    }

    private static final String PRE_AND_APPENDED_COMMENTS_YML = "# AB\n" +
            "\n" +
            "# CD\n" +
            "i: 0\n" +
            "# AB\n" +
            "\n" +
            "# CD";

    private static final String FIELD_COMMENTS_YML = "# a\n" +
            "a: 1\n" +
            "# b\n" +
            "# x\n" +
            "b: 2\n" +
            "# c\n" +
            "\n" +
            "# y\n" +
            "c: 3\n" +
            "d: 4";

    private static final String CLASS_COMMENTS_YML = "# 1\n" +
            "\n" +
            "# 2\n" +
            "# a\n" +
            "a: 1\n" +
            "# b\n" +
            "# x\n" +
            "b: 2\n" +
            "# c\n" +
            "\n" +
            "# y\n" +
            "c: 3\n" +
            "d: 4";

    private static final String TEST_CLASS_YML = "# A\n" +
            "\n" +
            "# B\n" +
            "# C\n" +
            "# A\n" +
            "primBool: true\n" +
            "# B\n" +
            "# C\n" +
            "refBool: true\n" +
            "# D\n" +
            "\n" +
            "# E\n" +
            "primByte: 1\n" +
            "refByte: 2\n" +
            "# F\n" +
            "primChar: c\n" +
            "\n" +
            "# G\n" +
            "refChar: d\n" +
            "primShort: 3\n" +
            "refShort: 4\n" +
            "primInt: 5\n" +
            "refInt: 6\n" +
            "primLong: 7\n" +
            "refLong: 8\n" +
            "primFloat: 9.0\n" +
            "refFloat: 10.0\n" +
            "primDouble: 11.0\n" +
            "refDouble: 12.0\n" +
            "string: string\n" +
            "subClass:\n" +
            "  primInt: 1\n" +
            "  string: string\n" +
            "ints: !!set\n" +
            "  1: null\n" +
            "  2: null\n" +
            "  3: null\n" +
            "strings:\n" +
            "- a\n" +
            "- b\n" +
            "- c\n" +
            "doubleByBool:\n" +
            "  true: 1.0\n" +
            "  false: 2.0\n" +
            "subClassSet: !!set\n" +
            "  ? primInt: 1\n" +
            "    string: '1'\n" +
            "  : null\n" +
            "  ? primInt: 2\n" +
            "    string: '2'\n" +
            "  : null\n" +
            "subClassList:\n" +
            "- primInt: 1\n" +
            "  string: '1'\n" +
            "- primInt: 2\n" +
            "  string: '2'\n" +
            "subClassMap:\n" +
            "  '1':\n" +
            "    primInt: 1\n" +
            "    string: '1'\n" +
            "  '2':\n" +
            "    primInt: 2\n" +
            "    string: '2'\n" +
            "listsList:\n" +
            "- - 1\n" +
            "  - 2\n" +
            "- - 3\n" +
            "  - 4\n" +
            "setsSet: !!set\n" +
            "  ? !!set\n" +
            "    a: null\n" +
            "    b: null\n" +
            "  : null\n" +
            "  ? !!set\n" +
            "    c: null\n" +
            "    d: null\n" +
            "  : null\n" +
            "mapsMap:\n" +
            "  1:\n" +
            "    '1': 1\n" +
            "  2:\n" +
            "    '2': 2\n" +
            "subClassListsList:\n" +
            "- - primInt: 1\n" +
            "    string: '1'\n" +
            "  - primInt: 2\n" +
            "    string: '2'\n" +
            "subClassSetsSet: !!set\n" +
            "  ? !!set\n" +
            "    ? primInt: 1\n" +
            "      string: '1'\n" +
            "    : null\n" +
            "    ? primInt: 2\n" +
            "      string: '2'\n" +
            "    : null\n" +
            "  : null\n" +
            "subClassMapsMap:\n" +
            "  1:\n" +
            "    '1':\n" +
            "      primInt: 1\n" +
            "      string: '2'\n" +
            "  2:\n" +
            "    '2':\n" +
            "      primInt: 2\n" +
            "      string: '2'\n" +
            "e1: NON_DEFAULT\n" +
            "enums:\n" +
            "- DEFAULT\n" +
            "- NON_DEFAULT\n" +
            "converterSubClass: '2:2'\n" +
            "excludedClass: !!de.exlll.configlib.classes.TestExcludedClass\n" +
            "  primInt: 1\n" +
            "  string: string";
}