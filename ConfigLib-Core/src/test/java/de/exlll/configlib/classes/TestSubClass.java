package de.exlll.configlib.classes;

import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.annotation.ElementType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.exlll.configlib.util.CollectionFactory.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@SuppressWarnings("FieldCanBeLocal")
@ConfigurationElement
public final class TestSubClass {
    public static final TestSubClass TEST_VALUES;

    static {
        TEST_VALUES = new TestSubClass();
        TEST_VALUES.primInt = 1;
        TEST_VALUES.string = "string";
        TEST_VALUES.list = listOf("list");
        TEST_VALUES.set = setOf("set");
        TEST_VALUES.map = mapOf("map", 1);
        TEST_VALUES.testSubSubClass = TestSubSubClass.of(14, "14");
        TEST_VALUES.subClassList = listOf(
                TestSubSubClass.of(15, "15"), TestSubSubClass.of(16, "16")
        );
        TEST_VALUES.subClassSet = setOf(
                TestSubSubClass.of(17, "17"), TestSubSubClass.of(18, "18")
        );
        TEST_VALUES.subClassMap = mapOf("map", TestSubSubClass.of(19, "19"));
    }

    private final int finalInt = 1;
    private int primInt;
    private String string = "";
    private List<String> list = Collections.emptyList();
    private Set<String> set = Collections.emptySet();
    private Map<String, Integer> map = Collections.emptyMap();
    private TestSubSubClass testSubSubClass = new TestSubSubClass();
    @ElementType(TestSubSubClass.class)
    private List<TestSubSubClass> subClassList = Collections.emptyList();
    @ElementType(TestSubSubClass.class)
    private Set<TestSubSubClass> subClassSet = Collections.emptySet();
    @ElementType(TestSubSubClass.class)
    private Map<String, TestSubSubClass> subClassMap = Collections.emptyMap();

    public static TestSubClass of(int primInt, String string) {
        TestSubClass cls = new TestSubClass();
        cls.primInt = primInt;
        cls.string = string;
        cls.list = listOf(string);
        cls.set = setOf(string);
        cls.map = mapOf(string, primInt);
        cls.testSubSubClass = TestSubSubClass.of(primInt, string);
        cls.subClassList = listOf(
                TestSubSubClass.of(primInt * 100, string)
        );
        cls.subClassSet = setOf(
                TestSubSubClass.of(primInt * 101, string)
        );
        cls.subClassMap = mapOf(string, TestSubSubClass.of(primInt * 102, string));
        return cls;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> asMap = mapOf("primInt", primInt, "string", string);
        asMap.put("list", list);
        asMap.put("set", set);
        asMap.put("map", map);
        asMap.put("testSubSubClass", testSubSubClass.asMap());
        asMap.put(
                "subClassList", subClassList.stream()
                        .map(TestSubSubClass::asMap)
                        .collect(toList())
        );
        asMap.put(
                "subClassSet", subClassSet.stream()
                        .map(TestSubSubClass::asMap)
                        .collect(toSet())
        );
        asMap.put(
                "subClassMap", subClassMap.entrySet().stream()
                        .map(e -> mapEntry(e.getKey(), e.getValue().asMap()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        return asMap;
    }

    public int getPrimInt() {
        return primInt;
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return "TestSubClass{" +
                "primInt=" + primInt +
                ", string='" + string + '\'' +
                ", list=" + list +
                ", set=" + set +
                ", map=" + map +
                ", testSubSubClass=" + testSubSubClass +
                ", subClassList=" + subClassList +
                ", subClassSet=" + subClassSet +
                ", subClassMap=" + subClassMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestSubClass that = (TestSubClass) o;

        if (primInt != that.primInt) return false;
        if (!string.equals(that.string)) return false;
        if (!list.equals(that.list)) return false;
        if (!set.equals(that.set)) return false;
        if (!map.equals(that.map)) return false;
        if (!testSubSubClass.equals(that.testSubSubClass)) return false;
        if (!subClassList.equals(that.subClassList)) return false;
        if (!subClassSet.equals(that.subClassSet)) return false;
        return subClassMap.equals(that.subClassMap);
    }

    @Override
    public int hashCode() {
        int result = finalInt;
        result = 31 * result + primInt;
        result = 31 * result + string.hashCode();
        result = 31 * result + list.hashCode();
        result = 31 * result + set.hashCode();
        result = 31 * result + map.hashCode();
        result = 31 * result + testSubSubClass.hashCode();
        result = 31 * result + subClassList.hashCode();
        result = 31 * result + subClassSet.hashCode();
        result = 31 * result + subClassMap.hashCode();
        return result;
    }
}
