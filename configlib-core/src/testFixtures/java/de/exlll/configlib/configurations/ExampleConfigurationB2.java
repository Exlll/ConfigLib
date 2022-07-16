package de.exlll.configlib.configurations;

import de.exlll.configlib.Ignore;

import java.awt.Point;
import java.math.BigInteger;
import java.util.*;

import static de.exlll.configlib.TestUtils.collectionOfArraysDeepEquals;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public final class ExampleConfigurationB2 extends ExampleConfigurationB1 {
    /* IGNORED FIELDS */
    private static final int b2_staticFinalInt = 1;
    private static int b2_staticInt = 2;
    private final int b2_finalInt = 3;
    private transient int b2_transientInt = 4;
    @Ignore
    private int b2_ignoredInt = 5;
    @Ignore
    private String b2_ignoredString = "ignoredString";
    @Ignore
    private List<String> b2_ignoredListString = List.of("ignored", "list", "string");

    /* PRIMITIVE TYPES */
    private char b2_primChar;
    private Boolean b2_refBool;
    private BigInteger b2_bigInteger;
    private List<Short> b2_listShort;
    private Integer[] b2_arrayInteger;
    private Set<Long> b2_setLong;
    private BigInteger[] b2_arrayEmpty;
    private Map<Float, Float> b2_mapFloatFloat;
    private Set<Double[]> b2_setArrayDouble;
    private List<Point> b2_listPoint;

    public static int getB2_staticFinalInt() {
        return b2_staticFinalInt;
    }

    public static int getB2_staticInt() {
        return b2_staticInt;
    }

    public static void setB2_staticInt(int b2_staticInt) {
        ExampleConfigurationB2.b2_staticInt = b2_staticInt;
    }

    public int getB2_finalInt() {
        return b2_finalInt;
    }

    public int getB2_transientInt() {
        return b2_transientInt;
    }

    public void setB2_transientInt(int b2_transientInt) {
        this.b2_transientInt = b2_transientInt;
    }

    public int getB2_ignoredInt() {
        return b2_ignoredInt;
    }

    // TODO: Set to different value to check that it's really ignored
    public void setB2_ignoredInt(int b2_ignoredInt) {
        this.b2_ignoredInt = b2_ignoredInt;
    }

    public String getB2_ignoredString() {
        return b2_ignoredString;
    }

    public void setB2_ignoredString(String b2_ignoredString) {
        this.b2_ignoredString = b2_ignoredString;
    }

    public List<String> getB2_ignoredListString() {
        return b2_ignoredListString;
    }

    public void setB2_ignoredListString(List<String> b2_ignoredListString) {
        this.b2_ignoredListString = b2_ignoredListString;
    }

    public char getB2_primChar() {
        return b2_primChar;
    }

    public void setB2_primChar(char b2_primChar) {
        this.b2_primChar = b2_primChar;
    }

    public Boolean getB2_refBool() {
        return b2_refBool;
    }

    public void setB2_refBool(Boolean b2_refBool) {
        this.b2_refBool = b2_refBool;
    }

    public BigInteger getB2_bigInteger() {
        return b2_bigInteger;
    }

    public void setB2_bigInteger(BigInteger b2_bigInteger) {
        this.b2_bigInteger = b2_bigInteger;
    }

    public List<Short> getB2_listShort() {
        return b2_listShort;
    }

    public void setB2_listShort(List<Short> b2_listShort) {
        this.b2_listShort = b2_listShort;
    }

    public Integer[] getB2_arrayInteger() {
        return b2_arrayInteger;
    }

    public void setB2_arrayInteger(Integer[] b2_arrayInteger) {
        this.b2_arrayInteger = b2_arrayInteger;
    }

    public Set<Long> getB2_setLong() {
        return b2_setLong;
    }

    public void setB2_setLong(Set<Long> b2_setLong) {
        this.b2_setLong = b2_setLong;
    }

    public BigInteger[] getB2_arrayEmpty() {
        return b2_arrayEmpty;
    }

    public void setB2_arrayEmpty(BigInteger[] b2_arrayEmpty) {
        this.b2_arrayEmpty = b2_arrayEmpty;
    }

    public Map<Float, Float> getB2_mapFloatFloat() {
        return b2_mapFloatFloat;
    }

    public void setB2_mapFloatFloat(Map<Float, Float> b2_mapFloatFloat) {
        this.b2_mapFloatFloat = b2_mapFloatFloat;
    }

    public Set<Double[]> getB2_setArrayDouble() {
        return b2_setArrayDouble;
    }

    public void setB2_setArrayDouble(Set<Double[]> b2_setArrayDouble) {
        this.b2_setArrayDouble = b2_setArrayDouble;
    }

    public List<Point> getB2_listPoint() {
        return b2_listPoint;
    }

    public void setB2_listPoint(List<Point> b2_listPoint) {
        this.b2_listPoint = b2_listPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExampleConfigurationB2 that = (ExampleConfigurationB2) o;
        return b2_transientInt == that.b2_transientInt &&
               b2_ignoredInt == that.b2_ignoredInt &&
               b2_primChar == that.b2_primChar &&
               Objects.equals(b2_ignoredString, that.b2_ignoredString) &&
               Objects.equals(b2_ignoredListString, that.b2_ignoredListString) &&
               Objects.equals(b2_refBool, that.b2_refBool) &&
               Objects.equals(b2_bigInteger, that.b2_bigInteger) &&
               Objects.equals(b2_listShort, that.b2_listShort) &&
               Arrays.equals(b2_arrayInteger, that.b2_arrayInteger) &&
               Objects.equals(b2_setLong, that.b2_setLong) &&
               Arrays.equals(b2_arrayEmpty, that.b2_arrayEmpty) &&
               Objects.equals(b2_mapFloatFloat, that.b2_mapFloatFloat) &&
               collectionOfArraysDeepEquals(b2_setArrayDouble, that.b2_setArrayDouble, HashSet::new) &&
               Objects.equals(b2_listPoint, that.b2_listPoint);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
