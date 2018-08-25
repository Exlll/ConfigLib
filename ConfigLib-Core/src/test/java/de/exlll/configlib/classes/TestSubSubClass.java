package de.exlll.configlib.classes;

import de.exlll.configlib.annotation.ConfigurationElement;

import java.util.*;

import static de.exlll.configlib.util.CollectionFactory.listOf;
import static de.exlll.configlib.util.CollectionFactory.mapOf;
import static de.exlll.configlib.util.CollectionFactory.setOf;

@ConfigurationElement
public final class TestSubSubClass {
    public static final TestSubSubClass TEST_VALUES;

    static {
        TEST_VALUES = new TestSubSubClass();
        TEST_VALUES.primInt = 1;
        TEST_VALUES.string = "string";
        TEST_VALUES.list = listOf("list");
        TEST_VALUES.set = setOf("set");
        TEST_VALUES.map = mapOf("map", 1);
    }

    private int primInt;
    private String string = "";
    private List<String> list = listOf();
    private Set<String> set = setOf();
    private Map<String, Integer> map = mapOf();

    public static TestSubSubClass of(int primInt, String string) {
        TestSubSubClass cls = new TestSubSubClass();
        cls.primInt = primInt;
        cls.string = string;
        String concat = string + string;
        cls.list = listOf(concat);
        cls.set = setOf(concat);
        cls.map = mapOf(concat, primInt);
        return cls;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> asMap = mapOf("primInt", primInt, "string", string);
        asMap.put("list", list);
        asMap.put("set", set);
        asMap.put("map", map);
        return asMap;
    }

    @Override
    public String toString() {
        return "TestSubSubClass{" +
                "primInt=" + primInt +
                ", string='" + string + '\'' +
                ", list=" + list +
                ", set=" + set +
                ", map=" + map +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestSubSubClass that = (TestSubSubClass) o;

        if (primInt != that.primInt) return false;
        if (!string.equals(that.string)) return false;
        if (!list.equals(that.list)) return false;
        if (!set.equals(that.set)) return false;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        int result = primInt;
        result = 31 * result + string.hashCode();
        result = 31 * result + list.hashCode();
        result = 31 * result + set.hashCode();
        result = 31 * result + map.hashCode();
        return result;
    }
}
