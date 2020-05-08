package de.exlll.configlib.classes;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.Convert;
import de.exlll.configlib.annotation.ElementType;
import de.exlll.configlib.annotation.NoConvert;
import de.exlll.configlib.configs.yaml.YamlConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.exlll.configlib.util.CollectionFactory.*;

@SuppressWarnings("FieldCanBeLocal")
@Comment({"A", "", "B", "C"})
public class TestClass extends YamlConfiguration {
    public static final TestClass TEST_VALUES;

    public enum TestEnum {
        DEFAULT, NON_DEFAULT
    }

    public static final TestClass initTestClass(TestClass testClass) {
        testClass.primBool = true;
        testClass.refBool = true;
        testClass.primByte = 1;
        testClass.refByte = 2;
        testClass.primChar = 'c';
        testClass.refChar = 'd';
        testClass.primShort = 3;
        testClass.refShort = 4;
        testClass.primInt = 5;
        testClass.refInt = 6;
        testClass.primLong = 7;
        testClass.refLong = 8L;
        testClass.primFloat = 9.0f;
        testClass.refFloat = 10.0f;
        testClass.primDouble = 11.0;
        testClass.refDouble = 12.0;
        testClass.string = "string";
        /* other types */
        testClass.subClass = TestSubClass.TEST_VALUES;
        /* containers of simple types */
        testClass.ints = setOf(1, 2, 3);
        testClass.strings = listOf("a", "b", "c");
        testClass.doubleByBool = mapOf(true, 1.0, false, 2.0);
        /* containers of other types */
        testClass.subClassSet = setOf(
                TestSubClass.of(1, "1"),
                TestSubClass.of(2, "2")
        );
        testClass.subClassList = listOf(
                TestSubClass.of(3, "3"),
                TestSubClass.of(4, "4")
        );
        testClass.subClassMap = mapOf(
                "5", TestSubClass.of(5, "5"),
                "6", TestSubClass.of(6, "6")
        );
        /* nested containers of simple types */
        testClass.listsList = listOf(
                listOf(1, 2), listOf(3, 4)
        );
        testClass.setsSet = setOf(
                setOf("a", "b"), setOf("c", "d")
        );
        testClass.mapsMap = mapOf(
                1, mapOf("1", 1), 2, mapOf("2", 2)
        );
        /* nested containers of custom types */
        testClass.subClassListsList = listOf(
                listOf(TestSubClass.of(7, "7"), TestSubClass.of(8, "8"))
        );
        testClass.subClassSetsSet = setOf(setOf(
                TestSubClass.of(9, "9"), TestSubClass.of(10, "10")
        ));
        testClass.subClassMapsMap = mapOf(
                1, mapOf("1", TestSubClass.of(11, "11")),
                2, mapOf("2", TestSubClass.of(12, "12"))
        );
        testClass.e1 = TestEnum.NON_DEFAULT;
        testClass.enums = listOf(TestEnum.DEFAULT, TestEnum.NON_DEFAULT);
        testClass.converterSubClass = TestSubClass.of(13, "13");
        testClass.excludedClass = TestExcludedClass.TEST_VALUES;

        return testClass;
    }

    static {
        TEST_VALUES = initTestClass(new TestClass());
    }

    /* not converted */
    private static final int staticFinalInt = 1;
    private static int staticInt = 2;
    private final int finalInt = 3;
    private transient int transientInt = 4;
    /* simple types */
    @Comment({"A"})
    private boolean primBool;
    @Comment({"B", "C"})
    private Boolean refBool = false;
    @Comment({"D", "", "E"})
    private byte primByte;
    private Byte refByte = 0;
    @Comment("F")
    private char primChar;
    @Comment({"", "G"})
    private Character refChar = '\0';
    private short primShort;
    private Short refShort = 0;
    private int primInt;
    private Integer refInt = 0;
    private long primLong;
    private Long refLong = 0L;
    private float primFloat;
    private Float refFloat = 0F;
    private double primDouble;
    private Double refDouble = 0.0;
    private String string = "";
    /* other types */
    private TestSubClass subClass = new TestSubClass();
    /* containers of simple types */
    private Set<Integer> ints = new HashSet<>();
    private List<String> strings = new ArrayList<>();
    private Map<Boolean, Double> doubleByBool = new HashMap<>();
    /* containers of other types */
    @ElementType(TestSubClass.class)
    private Set<TestSubClass> subClassSet = new HashSet<>();
    @ElementType(TestSubClass.class)
    private List<TestSubClass> subClassList = new ArrayList<>();
    @ElementType(TestSubClass.class)
    private Map<String, TestSubClass> subClassMap = new HashMap<>();
    /* nested containers of simple types */
    private List<List<Integer>> listsList = new ArrayList<>();
    private Set<Set<String>> setsSet = new HashSet<>();
    private Map<Integer, Map<String, Integer>> mapsMap = new HashMap<>();
    /* nested containers of custom types */
    @ElementType(value = TestSubClass.class, nestingLevel = 1)
    private List<List<TestSubClass>> subClassListsList = new ArrayList<>();
    @ElementType(value = TestSubClass.class, nestingLevel = 1)
    private Set<Set<TestSubClass>> subClassSetsSet = new HashSet<>();
    @ElementType(value = TestSubClass.class, nestingLevel = 1)
    private Map<Integer, Map<String, TestSubClass>> subClassMapsMap
            = new HashMap<>();
    private TestEnum e1 = TestEnum.DEFAULT;
    @ElementType(TestEnum.class)
    private List<TestEnum> enums = new ArrayList<>();
    @Convert(TestSubClassConverter.class)
    private TestSubClass converterSubClass = new TestSubClass();
    @NoConvert
    private TestExcludedClass excludedClass = new TestExcludedClass();

    public TestClass(Path path, YamlProperties properties) {
        super(path, properties);
    }

    public TestClass(Path path) {
        super(path);
    }

    public TestClass() {
        this(Paths.get(""), YamlProperties.DEFAULT);
    }

    public TestClass(Path configPath, TestClass other) {
        this(configPath);
        this.transientInt = other.transientInt;
        this.primBool = other.primBool;
        this.refBool = other.refBool;
        this.primByte = other.primByte;
        this.refByte = other.refByte;
        this.primChar = other.primChar;
        this.refChar = other.refChar;
        this.primShort = other.primShort;
        this.refShort = other.refShort;
        this.primInt = other.primInt;
        this.refInt = other.refInt;
        this.primLong = other.primLong;
        this.refLong = other.refLong;
        this.primFloat = other.primFloat;
        this.refFloat = other.refFloat;
        this.primDouble = other.primDouble;
        this.refDouble = other.refDouble;
        this.string = other.string;
        this.subClass = other.subClass;
        this.ints = other.ints;
        this.strings = other.strings;
        this.doubleByBool = other.doubleByBool;
        this.subClassSet = other.subClassSet;
        this.subClassList = other.subClassList;
        this.subClassMap = other.subClassMap;
        this.listsList = other.listsList;
        this.setsSet = other.setsSet;
        this.mapsMap = other.mapsMap;
        this.subClassListsList = other.subClassListsList;
        this.subClassSetsSet = other.subClassSetsSet;
        this.subClassMapsMap = other.subClassMapsMap;
        this.e1 = other.e1;
        this.enums = other.enums;
        this.converterSubClass = other.converterSubClass;
        this.excludedClass = other.excludedClass;
    }

    public static int getStaticFinalInt() {
        return staticFinalInt;
    }

    public static int getStaticInt() {
        return staticInt;
    }

    public int getFinalInt() {
        return finalInt;
    }

    public int getTransientInt() {
        return transientInt;
    }

    public boolean getPrimBool() {
        return primBool;
    }

    public Boolean getRefBool() {
        return refBool;
    }

    public byte getPrimByte() {
        return primByte;
    }

    public Byte getRefByte() {
        return refByte;
    }

    public char getPrimChar() {
        return primChar;
    }

    public Character getRefChar() {
        return refChar;
    }

    public short getPrimShort() {
        return primShort;
    }

    public Short getRefShort() {
        return refShort;
    }

    public int getPrimInt() {
        return primInt;
    }

    public Integer getRefInt() {
        return refInt;
    }

    public long getPrimLong() {
        return primLong;
    }

    public Long getRefLong() {
        return refLong;
    }

    public float getPrimFloat() {
        return primFloat;
    }

    public Float getRefFloat() {
        return refFloat;
    }

    public double getPrimDouble() {
        return primDouble;
    }

    public Double getRefDouble() {
        return refDouble;
    }

    public String getString() {
        return string;
    }

    public TestSubClass getSubClass() {
        return subClass;
    }

    public Set<Integer> getInts() {
        return ints;
    }

    public List<String> getStrings() {
        return strings;
    }

    public Map<Boolean, Double> getDoubleByBool() {
        return doubleByBool;
    }

    public Set<TestSubClass> getSubClassSet() {
        return subClassSet;
    }

    public List<TestSubClass> getSubClassList() {
        return subClassList;
    }

    public Map<String, TestSubClass> getSubClassMap() {
        return subClassMap;
    }

    public List<List<Integer>> getListsList() {
        return listsList;
    }

    public Set<Set<String>> getSetsSet() {
        return setsSet;
    }

    public Map<Integer, Map<String, Integer>> getMapsMap() {
        return mapsMap;
    }

    public List<List<TestSubClass>> getSubClassListsList() {
        return subClassListsList;
    }

    public Set<Set<TestSubClass>> getSubClassSetsSet() {
        return subClassSetsSet;
    }

    public Map<Integer, Map<String, TestSubClass>> getSubClassMapsMap() {
        return subClassMapsMap;
    }

    public TestEnum getE1() {
        return e1;
    }

    public List<TestEnum> getEnums() {
        return enums;
    }

    public TestSubClass getConverterSubClass() {
        return converterSubClass;
    }

    public TestExcludedClass getExcludedClass() {
        return excludedClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestClass)) return false;

        TestClass testClass = (TestClass) o;

        if (finalInt != testClass.finalInt) return false;
        if (transientInt != testClass.transientInt) return false;
        if (primBool != testClass.primBool) return false;
        if (primByte != testClass.primByte) return false;
        if (primChar != testClass.primChar) return false;
        if (primShort != testClass.primShort) return false;
        if (primInt != testClass.primInt) return false;
        if (primLong != testClass.primLong) return false;
        if (Float.compare(testClass.primFloat, primFloat) != 0) return false;
        if (Double.compare(testClass.primDouble, primDouble) != 0) return false;
        if (!refBool.equals(testClass.refBool)) return false;
        if (!refByte.equals(testClass.refByte)) return false;
        if (!refChar.equals(testClass.refChar)) return false;
        if (!refShort.equals(testClass.refShort)) return false;
        if (!refInt.equals(testClass.refInt)) return false;
        if (!refLong.equals(testClass.refLong)) return false;
        if (!refFloat.equals(testClass.refFloat)) return false;
        if (!refDouble.equals(testClass.refDouble)) return false;
        if (!string.equals(testClass.string)) return false;
        if (!subClass.equals(testClass.subClass)) return false;
        if (!ints.equals(testClass.ints)) return false;
        if (!strings.equals(testClass.strings)) return false;
        if (!doubleByBool.equals(testClass.doubleByBool)) return false;
        if (!subClassSet.equals(testClass.subClassSet)) return false;
        if (!subClassList.equals(testClass.subClassList)) return false;
        if (!subClassMap.equals(testClass.subClassMap)) return false;
        if (!listsList.equals(testClass.listsList)) return false;
        if (!setsSet.equals(testClass.setsSet)) return false;
        if (!mapsMap.equals(testClass.mapsMap)) return false;
        if (!subClassListsList.equals(testClass.subClassListsList)) return false;
        if (!subClassSetsSet.equals(testClass.subClassSetsSet)) return false;
        if (e1 != testClass.e1) return false;
        if (!enums.equals(testClass.enums)) return false;
        if (!converterSubClass.equals(testClass.converterSubClass)) return false;
        if (!excludedClass.equals(testClass.excludedClass)) return false;
        return subClassMapsMap.equals(testClass.subClassMapsMap);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = finalInt;
        result = 31 * result + transientInt;
        result = 31 * result + (primBool ? 1 : 0);
        result = 31 * result + refBool.hashCode();
        result = 31 * result + (int) primByte;
        result = 31 * result + refByte.hashCode();
        result = 31 * result + (int) primChar;
        result = 31 * result + refChar.hashCode();
        result = 31 * result + (int) primShort;
        result = 31 * result + refShort.hashCode();
        result = 31 * result + primInt;
        result = 31 * result + refInt.hashCode();
        result = 31 * result + (int) (primLong ^ (primLong >>> 32));
        result = 31 * result + refLong.hashCode();
        result = 31 * result + (primFloat != +0.0f ? Float.floatToIntBits(
                primFloat) : 0);
        result = 31 * result + refFloat.hashCode();
        temp = Double.doubleToLongBits(primDouble);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + refDouble.hashCode();
        result = 31 * result + string.hashCode();
        result = 31 * result + subClass.hashCode();
        result = 31 * result + ints.hashCode();
        result = 31 * result + strings.hashCode();
        result = 31 * result + doubleByBool.hashCode();
        result = 31 * result + subClassSet.hashCode();
        result = 31 * result + subClassList.hashCode();
        result = 31 * result + subClassMap.hashCode();
        result = 31 * result + listsList.hashCode();
        result = 31 * result + setsSet.hashCode();
        result = 31 * result + mapsMap.hashCode();
        result = 31 * result + subClassListsList.hashCode();
        result = 31 * result + subClassSetsSet.hashCode();
        result = 31 * result + subClassMapsMap.hashCode();
        result = 31 * result + e1.hashCode();
        result = 31 * result + enums.hashCode();
        result = 31 * result + converterSubClass.hashCode();
        result = 31 * result + excludedClass.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TestClass{" +
                "\nprimBool=" + primBool +
                ",\nrefBool=" + refBool +
                ",\nprimByte=" + primByte +
                ",\nrefByte=" + refByte +
                ",\nprimChar=" + primChar +
                ",\nrefChar=" + refChar +
                ",\nprimShort=" + primShort +
                ",\nrefShort=" + refShort +
                ",\nprimInt=" + primInt +
                ",\nrefInt=" + refInt +
                ",\nprimLong=" + primLong +
                ",\nrefLong=" + refLong +
                ",\nprimFloat=" + primFloat +
                ",\nrefFloat=" + refFloat +
                ",\nprimDouble=" + primDouble +
                ",\nrefDouble=" + refDouble +
                ",\nstring='" + string + '\'' +
                ",\nsubClass=" + subClass +
                ",\nints=" + ints +
                ",\nstrings=" + strings +
                ",\ndoubleByBool=" + doubleByBool +
                ",\nsubClassSet=" + subClassSet +
                ",\nsubClassList=" + subClassList +
                ",\nsubClassMap=" + subClassMap +
                ",\nlistsList=" + listsList +
                ",\nsetsSet=" + setsSet +
                ",\nmapsMap=" + mapsMap +
                ",\nsubClassListsList=" + subClassListsList +
                ",\nsubClassSetsSet=" + subClassSetsSet +
                ",\nsubClassMapsMap=" + subClassMapsMap +
                ",\ne1=" + e1 +
                ",\nenums=" + enums +
                ",\nconverterSubClass=" + converterSubClass +
                ",\nexcludedClass=" + excludedClass +
                '}';
    }
}
