package de.exlll.configlib.configs.yaml;

import com.google.common.jimfs.Jimfs;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.classes.TestClass;
import de.exlll.configlib.configs.yaml.YamlConfiguration.YamlProperties;
import de.exlll.configlib.format.FieldNameFormatters;
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
    void loadAndSaveExecutesPostLoadHook() {
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
    void loadAndSaveSavesConfiguration() {
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
    void loadAndSaveLoadsConfiguration() {
        new TestClass(configPath, TestClass.TEST_VALUES).save();

        YamlConfiguration configuration = new TestClass(configPath);
        configuration.loadAndSave();
        assertThat(configuration, is(TestClass.TEST_VALUES));
        assertThat(Files.exists(configPath), is(true));
    }

    @Test
    void loadLoadsConfig() {
        setupConfigPath();
        Configuration configuration = new TestClass(configPath);
        assertThat(configuration, is(not(TestClass.TEST_VALUES)));
        configuration.load();
        assertThat(configuration, is((TestClass.TEST_VALUES)));
    }

    private void setupConfigPath() {
        Configuration configuration = new TestClass(
                configPath, TestClass.TEST_VALUES
        );
        configuration.save();
    }

    @Test
    void loadThrowsExceptionIfTypesDoNotMatch() {
        Configuration configuration = new TestClass(configPath);
        configuration.save();
        assertThrows(IllegalArgumentException.class, configuration::load);
    }

    @Test
    void saveCreatesConfig() {
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

    @Test
    void saveDumpsFormattedFieldComments() throws IOException {
        class A extends YamlConfiguration {
            @Comment("aB")
            private int aB = 1;
            @Comment({"cD", "dC"})
            private int cD = 2;

            protected A(YamlProperties properties) {
                super(testPath, properties);
            }
        }

        YamlProperties properties = YamlProperties.builder()
                .setFormatter(FieldNameFormatters.LOWER_UNDERSCORE)
                .build();

        new A(properties).save();
        assertThat(readConfig(testPath), is(FORMATTED_FIELD_COMMENTS_YML));
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

    private static final String FORMATTED_FIELD_COMMENTS_YML = "# aB\n" +
            "a_b: 1\n" +
            "# cD\n" +
            "# dC\n" +
            "c_d: 2";

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
            "  list:\n" +
            "  - list\n" +
            "  set: !!set\n" +
            "    set: null\n" +
            "  map:\n" +
            "    map: 1\n" +
            "  testSubSubClass:\n" +
            "    primInt: 14\n" +
            "    string: '14'\n" +
            "    list:\n" +
            "    - '1414'\n" +
            "    set: !!set\n" +
            "      '1414': null\n" +
            "    map:\n" +
            "      '1414': 14\n" +
            "  subClassList:\n" +
            "  - primInt: 15\n" +
            "    string: '15'\n" +
            "    list:\n" +
            "    - '1515'\n" +
            "    set: !!set\n" +
            "      '1515': null\n" +
            "    map:\n" +
            "      '1515': 15\n" +
            "  - primInt: 16\n" +
            "    string: '16'\n" +
            "    list:\n" +
            "    - '1616'\n" +
            "    set: !!set\n" +
            "      '1616': null\n" +
            "    map:\n" +
            "      '1616': 16\n" +
            "  subClassSet: !!set\n" +
            "    ? primInt: 18\n" +
            "      string: '18'\n" +
            "      list:\n" +
            "      - '1818'\n" +
            "      set: !!set\n" +
            "        '1818': null\n" +
            "      map:\n" +
            "        '1818': 18\n" +
            "    : null\n" +
            "    ? primInt: 17\n" +
            "      string: '17'\n" +
            "      list:\n" +
            "      - '1717'\n" +
            "      set: !!set\n" +
            "        '1717': null\n" +
            "      map:\n" +
            "        '1717': 17\n" +
            "    : null\n" +
            "  subClassMap:\n" +
            "    map:\n" +
            "      primInt: 19\n" +
            "      string: '19'\n" +
            "      list:\n" +
            "      - '1919'\n" +
            "      set: !!set\n" +
            "        '1919': null\n" +
            "      map:\n" +
            "        '1919': 19\n" +
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
            "    list:\n" +
            "    - '1'\n" +
            "    set: !!set\n" +
            "      '1': null\n" +
            "    map:\n" +
            "      '1': 1\n" +
            "    testSubSubClass:\n" +
            "      primInt: 1\n" +
            "      string: '1'\n" +
            "      list:\n" +
            "      - '11'\n" +
            "      set: !!set\n" +
            "        '11': null\n" +
            "      map:\n" +
            "        '11': 1\n" +
            "    subClassList:\n" +
            "    - primInt: 100\n" +
            "      string: '1'\n" +
            "      list:\n" +
            "      - '11'\n" +
            "      set: !!set\n" +
            "        '11': null\n" +
            "      map:\n" +
            "        '11': 100\n" +
            "    subClassSet: !!set\n" +
            "      ? primInt: 101\n" +
            "        string: '1'\n" +
            "        list:\n" +
            "        - '11'\n" +
            "        set: !!set\n" +
            "          '11': null\n" +
            "        map:\n" +
            "          '11': 101\n" +
            "      : null\n" +
            "    subClassMap:\n" +
            "      '1':\n" +
            "        primInt: 102\n" +
            "        string: '1'\n" +
            "        list:\n" +
            "        - '11'\n" +
            "        set: !!set\n" +
            "          '11': null\n" +
            "        map:\n" +
            "          '11': 102\n" +
            "  : null\n" +
            "  ? primInt: 2\n" +
            "    string: '2'\n" +
            "    list:\n" +
            "    - '2'\n" +
            "    set: !!set\n" +
            "      '2': null\n" +
            "    map:\n" +
            "      '2': 2\n" +
            "    testSubSubClass:\n" +
            "      primInt: 2\n" +
            "      string: '2'\n" +
            "      list:\n" +
            "      - '22'\n" +
            "      set: !!set\n" +
            "        '22': null\n" +
            "      map:\n" +
            "        '22': 2\n" +
            "    subClassList:\n" +
            "    - primInt: 200\n" +
            "      string: '2'\n" +
            "      list:\n" +
            "      - '22'\n" +
            "      set: !!set\n" +
            "        '22': null\n" +
            "      map:\n" +
            "        '22': 200\n" +
            "    subClassSet: !!set\n" +
            "      ? primInt: 202\n" +
            "        string: '2'\n" +
            "        list:\n" +
            "        - '22'\n" +
            "        set: !!set\n" +
            "          '22': null\n" +
            "        map:\n" +
            "          '22': 202\n" +
            "      : null\n" +
            "    subClassMap:\n" +
            "      '2':\n" +
            "        primInt: 204\n" +
            "        string: '2'\n" +
            "        list:\n" +
            "        - '22'\n" +
            "        set: !!set\n" +
            "          '22': null\n" +
            "        map:\n" +
            "          '22': 204\n" +
            "  : null\n" +
            "subClassList:\n" +
            "- primInt: 3\n" +
            "  string: '3'\n" +
            "  list:\n" +
            "  - '3'\n" +
            "  set: !!set\n" +
            "    '3': null\n" +
            "  map:\n" +
            "    '3': 3\n" +
            "  testSubSubClass:\n" +
            "    primInt: 3\n" +
            "    string: '3'\n" +
            "    list:\n" +
            "    - '33'\n" +
            "    set: !!set\n" +
            "      '33': null\n" +
            "    map:\n" +
            "      '33': 3\n" +
            "  subClassList:\n" +
            "  - primInt: 300\n" +
            "    string: '3'\n" +
            "    list:\n" +
            "    - '33'\n" +
            "    set: !!set\n" +
            "      '33': null\n" +
            "    map:\n" +
            "      '33': 300\n" +
            "  subClassSet: !!set\n" +
            "    ? primInt: 303\n" +
            "      string: '3'\n" +
            "      list:\n" +
            "      - '33'\n" +
            "      set: !!set\n" +
            "        '33': null\n" +
            "      map:\n" +
            "        '33': 303\n" +
            "    : null\n" +
            "  subClassMap:\n" +
            "    '3':\n" +
            "      primInt: 306\n" +
            "      string: '3'\n" +
            "      list:\n" +
            "      - '33'\n" +
            "      set: !!set\n" +
            "        '33': null\n" +
            "      map:\n" +
            "        '33': 306\n" +
            "- primInt: 4\n" +
            "  string: '4'\n" +
            "  list:\n" +
            "  - '4'\n" +
            "  set: !!set\n" +
            "    '4': null\n" +
            "  map:\n" +
            "    '4': 4\n" +
            "  testSubSubClass:\n" +
            "    primInt: 4\n" +
            "    string: '4'\n" +
            "    list:\n" +
            "    - '44'\n" +
            "    set: !!set\n" +
            "      '44': null\n" +
            "    map:\n" +
            "      '44': 4\n" +
            "  subClassList:\n" +
            "  - primInt: 400\n" +
            "    string: '4'\n" +
            "    list:\n" +
            "    - '44'\n" +
            "    set: !!set\n" +
            "      '44': null\n" +
            "    map:\n" +
            "      '44': 400\n" +
            "  subClassSet: !!set\n" +
            "    ? primInt: 404\n" +
            "      string: '4'\n" +
            "      list:\n" +
            "      - '44'\n" +
            "      set: !!set\n" +
            "        '44': null\n" +
            "      map:\n" +
            "        '44': 404\n" +
            "    : null\n" +
            "  subClassMap:\n" +
            "    '4':\n" +
            "      primInt: 408\n" +
            "      string: '4'\n" +
            "      list:\n" +
            "      - '44'\n" +
            "      set: !!set\n" +
            "        '44': null\n" +
            "      map:\n" +
            "        '44': 408\n" +
            "subClassMap:\n" +
            "  '5':\n" +
            "    primInt: 5\n" +
            "    string: '5'\n" +
            "    list:\n" +
            "    - '5'\n" +
            "    set: !!set\n" +
            "      '5': null\n" +
            "    map:\n" +
            "      '5': 5\n" +
            "    testSubSubClass:\n" +
            "      primInt: 5\n" +
            "      string: '5'\n" +
            "      list:\n" +
            "      - '55'\n" +
            "      set: !!set\n" +
            "        '55': null\n" +
            "      map:\n" +
            "        '55': 5\n" +
            "    subClassList:\n" +
            "    - primInt: 500\n" +
            "      string: '5'\n" +
            "      list:\n" +
            "      - '55'\n" +
            "      set: !!set\n" +
            "        '55': null\n" +
            "      map:\n" +
            "        '55': 500\n" +
            "    subClassSet: !!set\n" +
            "      ? primInt: 505\n" +
            "        string: '5'\n" +
            "        list:\n" +
            "        - '55'\n" +
            "        set: !!set\n" +
            "          '55': null\n" +
            "        map:\n" +
            "          '55': 505\n" +
            "      : null\n" +
            "    subClassMap:\n" +
            "      '5':\n" +
            "        primInt: 510\n" +
            "        string: '5'\n" +
            "        list:\n" +
            "        - '55'\n" +
            "        set: !!set\n" +
            "          '55': null\n" +
            "        map:\n" +
            "          '55': 510\n" +
            "  '6':\n" +
            "    primInt: 6\n" +
            "    string: '6'\n" +
            "    list:\n" +
            "    - '6'\n" +
            "    set: !!set\n" +
            "      '6': null\n" +
            "    map:\n" +
            "      '6': 6\n" +
            "    testSubSubClass:\n" +
            "      primInt: 6\n" +
            "      string: '6'\n" +
            "      list:\n" +
            "      - '66'\n" +
            "      set: !!set\n" +
            "        '66': null\n" +
            "      map:\n" +
            "        '66': 6\n" +
            "    subClassList:\n" +
            "    - primInt: 600\n" +
            "      string: '6'\n" +
            "      list:\n" +
            "      - '66'\n" +
            "      set: !!set\n" +
            "        '66': null\n" +
            "      map:\n" +
            "        '66': 600\n" +
            "    subClassSet: !!set\n" +
            "      ? primInt: 606\n" +
            "        string: '6'\n" +
            "        list:\n" +
            "        - '66'\n" +
            "        set: !!set\n" +
            "          '66': null\n" +
            "        map:\n" +
            "          '66': 606\n" +
            "      : null\n" +
            "    subClassMap:\n" +
            "      '6':\n" +
            "        primInt: 612\n" +
            "        string: '6'\n" +
            "        list:\n" +
            "        - '66'\n" +
            "        set: !!set\n" +
            "          '66': null\n" +
            "        map:\n" +
            "          '66': 612\n" +
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
            "- - primInt: 7\n" +
            "    string: '7'\n" +
            "    list:\n" +
            "    - '7'\n" +
            "    set: !!set\n" +
            "      '7': null\n" +
            "    map:\n" +
            "      '7': 7\n" +
            "    testSubSubClass:\n" +
            "      primInt: 7\n" +
            "      string: '7'\n" +
            "      list:\n" +
            "      - '77'\n" +
            "      set: !!set\n" +
            "        '77': null\n" +
            "      map:\n" +
            "        '77': 7\n" +
            "    subClassList:\n" +
            "    - primInt: 700\n" +
            "      string: '7'\n" +
            "      list:\n" +
            "      - '77'\n" +
            "      set: !!set\n" +
            "        '77': null\n" +
            "      map:\n" +
            "        '77': 700\n" +
            "    subClassSet: !!set\n" +
            "      ? primInt: 707\n" +
            "        string: '7'\n" +
            "        list:\n" +
            "        - '77'\n" +
            "        set: !!set\n" +
            "          '77': null\n" +
            "        map:\n" +
            "          '77': 707\n" +
            "      : null\n" +
            "    subClassMap:\n" +
            "      '7':\n" +
            "        primInt: 714\n" +
            "        string: '7'\n" +
            "        list:\n" +
            "        - '77'\n" +
            "        set: !!set\n" +
            "          '77': null\n" +
            "        map:\n" +
            "          '77': 714\n" +
            "  - primInt: 8\n" +
            "    string: '8'\n" +
            "    list:\n" +
            "    - '8'\n" +
            "    set: !!set\n" +
            "      '8': null\n" +
            "    map:\n" +
            "      '8': 8\n" +
            "    testSubSubClass:\n" +
            "      primInt: 8\n" +
            "      string: '8'\n" +
            "      list:\n" +
            "      - '88'\n" +
            "      set: !!set\n" +
            "        '88': null\n" +
            "      map:\n" +
            "        '88': 8\n" +
            "    subClassList:\n" +
            "    - primInt: 800\n" +
            "      string: '8'\n" +
            "      list:\n" +
            "      - '88'\n" +
            "      set: !!set\n" +
            "        '88': null\n" +
            "      map:\n" +
            "        '88': 800\n" +
            "    subClassSet: !!set\n" +
            "      ? primInt: 808\n" +
            "        string: '8'\n" +
            "        list:\n" +
            "        - '88'\n" +
            "        set: !!set\n" +
            "          '88': null\n" +
            "        map:\n" +
            "          '88': 808\n" +
            "      : null\n" +
            "    subClassMap:\n" +
            "      '8':\n" +
            "        primInt: 816\n" +
            "        string: '8'\n" +
            "        list:\n" +
            "        - '88'\n" +
            "        set: !!set\n" +
            "          '88': null\n" +
            "        map:\n" +
            "          '88': 816\n" +
            "subClassSetsSet: !!set\n" +
            "  ? !!set\n" +
            "    ? primInt: 10\n" +
            "      string: '10'\n" +
            "      list:\n" +
            "      - '10'\n" +
            "      set: !!set\n" +
            "        '10': null\n" +
            "      map:\n" +
            "        '10': 10\n" +
            "      testSubSubClass:\n" +
            "        primInt: 10\n" +
            "        string: '10'\n" +
            "        list:\n" +
            "        - '1010'\n" +
            "        set: !!set\n" +
            "          '1010': null\n" +
            "        map:\n" +
            "          '1010': 10\n" +
            "      subClassList:\n" +
            "      - primInt: 1000\n" +
            "        string: '10'\n" +
            "        list:\n" +
            "        - '1010'\n" +
            "        set: !!set\n" +
            "          '1010': null\n" +
            "        map:\n" +
            "          '1010': 1000\n" +
            "      subClassSet: !!set\n" +
            "        ? primInt: 1010\n" +
            "          string: '10'\n" +
            "          list:\n" +
            "          - '1010'\n" +
            "          set: !!set\n" +
            "            '1010': null\n" +
            "          map:\n" +
            "            '1010': 1010\n" +
            "        : null\n" +
            "      subClassMap:\n" +
            "        '10':\n" +
            "          primInt: 1020\n" +
            "          string: '10'\n" +
            "          list:\n" +
            "          - '1010'\n" +
            "          set: !!set\n" +
            "            '1010': null\n" +
            "          map:\n" +
            "            '1010': 1020\n" +
            "    : null\n" +
            "    ? primInt: 9\n" +
            "      string: '9'\n" +
            "      list:\n" +
            "      - '9'\n" +
            "      set: !!set\n" +
            "        '9': null\n" +
            "      map:\n" +
            "        '9': 9\n" +
            "      testSubSubClass:\n" +
            "        primInt: 9\n" +
            "        string: '9'\n" +
            "        list:\n" +
            "        - '99'\n" +
            "        set: !!set\n" +
            "          '99': null\n" +
            "        map:\n" +
            "          '99': 9\n" +
            "      subClassList:\n" +
            "      - primInt: 900\n" +
            "        string: '9'\n" +
            "        list:\n" +
            "        - '99'\n" +
            "        set: !!set\n" +
            "          '99': null\n" +
            "        map:\n" +
            "          '99': 900\n" +
            "      subClassSet: !!set\n" +
            "        ? primInt: 909\n" +
            "          string: '9'\n" +
            "          list:\n" +
            "          - '99'\n" +
            "          set: !!set\n" +
            "            '99': null\n" +
            "          map:\n" +
            "            '99': 909\n" +
            "        : null\n" +
            "      subClassMap:\n" +
            "        '9':\n" +
            "          primInt: 918\n" +
            "          string: '9'\n" +
            "          list:\n" +
            "          - '99'\n" +
            "          set: !!set\n" +
            "            '99': null\n" +
            "          map:\n" +
            "            '99': 918\n" +
            "    : null\n" +
            "  : null\n" +
            "subClassMapsMap:\n" +
            "  1:\n" +
            "    '1':\n" +
            "      primInt: 11\n" +
            "      string: '11'\n" +
            "      list:\n" +
            "      - '11'\n" +
            "      set: !!set\n" +
            "        '11': null\n" +
            "      map:\n" +
            "        '11': 11\n" +
            "      testSubSubClass:\n" +
            "        primInt: 11\n" +
            "        string: '11'\n" +
            "        list:\n" +
            "        - '1111'\n" +
            "        set: !!set\n" +
            "          '1111': null\n" +
            "        map:\n" +
            "          '1111': 11\n" +
            "      subClassList:\n" +
            "      - primInt: 1100\n" +
            "        string: '11'\n" +
            "        list:\n" +
            "        - '1111'\n" +
            "        set: !!set\n" +
            "          '1111': null\n" +
            "        map:\n" +
            "          '1111': 1100\n" +
            "      subClassSet: !!set\n" +
            "        ? primInt: 1111\n" +
            "          string: '11'\n" +
            "          list:\n" +
            "          - '1111'\n" +
            "          set: !!set\n" +
            "            '1111': null\n" +
            "          map:\n" +
            "            '1111': 1111\n" +
            "        : null\n" +
            "      subClassMap:\n" +
            "        '11':\n" +
            "          primInt: 1122\n" +
            "          string: '11'\n" +
            "          list:\n" +
            "          - '1111'\n" +
            "          set: !!set\n" +
            "            '1111': null\n" +
            "          map:\n" +
            "            '1111': 1122\n" +
            "  2:\n" +
            "    '2':\n" +
            "      primInt: 12\n" +
            "      string: '12'\n" +
            "      list:\n" +
            "      - '12'\n" +
            "      set: !!set\n" +
            "        '12': null\n" +
            "      map:\n" +
            "        '12': 12\n" +
            "      testSubSubClass:\n" +
            "        primInt: 12\n" +
            "        string: '12'\n" +
            "        list:\n" +
            "        - '1212'\n" +
            "        set: !!set\n" +
            "          '1212': null\n" +
            "        map:\n" +
            "          '1212': 12\n" +
            "      subClassList:\n" +
            "      - primInt: 1200\n" +
            "        string: '12'\n" +
            "        list:\n" +
            "        - '1212'\n" +
            "        set: !!set\n" +
            "          '1212': null\n" +
            "        map:\n" +
            "          '1212': 1200\n" +
            "      subClassSet: !!set\n" +
            "        ? primInt: 1212\n" +
            "          string: '12'\n" +
            "          list:\n" +
            "          - '1212'\n" +
            "          set: !!set\n" +
            "            '1212': null\n" +
            "          map:\n" +
            "            '1212': 1212\n" +
            "        : null\n" +
            "      subClassMap:\n" +
            "        '12':\n" +
            "          primInt: 1224\n" +
            "          string: '12'\n" +
            "          list:\n" +
            "          - '1212'\n" +
            "          set: !!set\n" +
            "            '1212': null\n" +
            "          map:\n" +
            "            '1212': 1224\n" +
            "e1: NON_DEFAULT\n" +
            "enums:\n" +
            "- DEFAULT\n" +
            "- NON_DEFAULT\n" +
            "converterSubClass: '13:13'\n" +
            "excludedClass: !!de.exlll.configlib.classes.TestExcludedClass\n" +
            "  primInt: 1\n" +
            "  string: string";
}