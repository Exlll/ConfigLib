package de.exlll.configlib.classes;

public class TestExcludedClass {
    public static final TestExcludedClass TEST_VALUES;
    private int primInt;
    private String string = "";

    static {
        TEST_VALUES = new TestExcludedClass();
        TEST_VALUES.primInt = 1;
        TEST_VALUES.string = "string";
    }

    public int getPrimInt() {
        return primInt;
    }

    public String getString() {
        return string;
    }

    public void setPrimInt(int primInt) {
        this.primInt = primInt;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestExcludedClass that = (TestExcludedClass) o;

        if (primInt != that.primInt) return false;
        return string.equals(that.string);
    }

    @Override
    public int hashCode() {
        int result = primInt;
        result = 31 * result + string.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TestExcludedClass{" +
                "primInt=" + primInt +
                ", string='" + string + '\'' +
                '}';
    }
}
