package de.exlll.configlib.classes;

import de.exlll.configlib.annotation.ConfigurationElement;

import java.util.Map;

@SuppressWarnings("FieldCanBeLocal")
@ConfigurationElement
public final class TestSubClass {
    public static final TestSubClass TEST_VALUES;

    static {
        TEST_VALUES = new TestSubClass();
        TEST_VALUES.primInt = 1;
        TEST_VALUES.string = "string";
    }

    private final int finalInt = 1;
    private int primInt;
    private String string = "";

    public static TestSubClass of(int primInt, String string) {
        TestSubClass subClass = new TestSubClass();
        subClass.primInt = primInt;
        subClass.string = string;
        return subClass;
    }

    public Map<String, Object> asMap() {
        return Map.of("primInt", primInt, "string", string);
    }

    public int getFinalInt() {
        return finalInt;
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
                "\nprimInt=" + primInt +
                ",\nstring='" + string + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestSubClass)) return false;

        TestSubClass subClass = (TestSubClass) o;

        if (primInt != subClass.primInt) return false;
        return string.equals(subClass.string);
    }

    @Override
    public int hashCode() {
        int result = primInt;
        result = 31 * result + string.hashCode();
        return result;
    }
}
