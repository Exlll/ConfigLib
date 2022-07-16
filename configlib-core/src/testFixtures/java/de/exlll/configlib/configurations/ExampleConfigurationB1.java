package de.exlll.configlib.configurations;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.Ignore;

import java.awt.Point;
import java.util.*;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@Configuration
public class ExampleConfigurationB1 {
    /* IGNORED FIELDS */
    private static final int b1_staticFinalInt = 1;
    private static int b1_staticInt = 2;
    private final int b1_finalInt = 3;
    private transient int b1_transientInt = 4;
    @Ignore
    private int b1_ignoredInt = 5;
    @Ignore
    private String b1_ignoredString = "ignoredString";
    @Ignore
    private List<String> b1_ignoredListString = List.of("ignored", "list", "string");

    private boolean b1_primBool;
    private Character b1_refChar;
    private String b1_string;
    private List<Byte> b1_listByte;
    private Short[] b1_arrayShort;
    private Set<Integer> b1_setInteger;
    private List<Short> b1_listEmpty;
    private Map<Long, Long> b1_mapLongLong;
    private List<List<Byte>> b1_listListByte;
    private Point b1_point;

    public static int getB1_staticFinalInt() {
        return b1_staticFinalInt;
    }

    public static int getB1_staticInt() {
        return b1_staticInt;
    }

    public static void setB1_staticInt(int b1_staticInt) {
        ExampleConfigurationB1.b1_staticInt = b1_staticInt;
    }

    public int getB1_finalInt() {
        return b1_finalInt;
    }

    public int getB1_transientInt() {
        return b1_transientInt;
    }

    public void setB1_transientInt(int b1_transientInt) {
        this.b1_transientInt = b1_transientInt;
    }

    public int getB1_ignoredInt() {
        return b1_ignoredInt;
    }

    public void setB1_ignoredInt(int b1_ignoredInt) {
        this.b1_ignoredInt = b1_ignoredInt;
    }

    public String getB1_ignoredString() {
        return b1_ignoredString;
    }

    public void setB1_ignoredString(String b1_ignoredString) {
        this.b1_ignoredString = b1_ignoredString;
    }

    public List<String> getB1_ignoredListString() {
        return b1_ignoredListString;
    }

    public void setB1_ignoredListString(List<String> b1_ignoredListString) {
        this.b1_ignoredListString = b1_ignoredListString;
    }

    public boolean isB1_primBool() {
        return b1_primBool;
    }

    public void setB1_primBool(boolean b1_primBool) {
        this.b1_primBool = b1_primBool;
    }

    public Character getB1_refChar() {
        return b1_refChar;
    }

    public void setB1_refChar(Character b1_refChar) {
        this.b1_refChar = b1_refChar;
    }

    public String getB1_string() {
        return b1_string;
    }

    public void setB1_string(String b1_string) {
        this.b1_string = b1_string;
    }

    public List<Byte> getB1_listByte() {
        return b1_listByte;
    }

    public void setB1_listByte(List<Byte> b1_listByte) {
        this.b1_listByte = b1_listByte;
    }

    public Short[] getB1_arrayShort() {
        return b1_arrayShort;
    }

    public void setB1_arrayShort(Short[] b1_arrayShort) {
        this.b1_arrayShort = b1_arrayShort;
    }

    public Set<Integer> getB1_setInteger() {
        return b1_setInteger;
    }

    public void setB1_setInteger(Set<Integer> b1_setInteger) {
        this.b1_setInteger = b1_setInteger;
    }

    public List<Short> getB1_listEmpty() {
        return b1_listEmpty;
    }

    public void setB1_listEmpty(List<Short> b1_listEmpty) {
        this.b1_listEmpty = b1_listEmpty;
    }

    public Map<Long, Long> getB1_mapLongLong() {
        return b1_mapLongLong;
    }

    public void setB1_mapLongLong(Map<Long, Long> b1_mapLongLong) {
        this.b1_mapLongLong = b1_mapLongLong;
    }

    public List<List<Byte>> getB1_listListByte() {
        return b1_listListByte;
    }

    public void setB1_listListByte(List<List<Byte>> b1_listListByte) {
        this.b1_listListByte = b1_listListByte;
    }

    public Point getB1_point() {
        return b1_point;
    }

    public void setB1_point(Point b1_point) {
        this.b1_point = b1_point;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExampleConfigurationB1 that = (ExampleConfigurationB1) o;
        return b1_transientInt == that.b1_transientInt &&
               b1_ignoredInt == that.b1_ignoredInt &&
               b1_primBool == that.b1_primBool &&
               Objects.equals(b1_ignoredString, that.b1_ignoredString) &&
               Objects.equals(b1_ignoredListString, that.b1_ignoredListString) &&
               Objects.equals(b1_refChar, that.b1_refChar) &&
               Objects.equals(b1_string, that.b1_string) &&
               Objects.equals(b1_listByte, that.b1_listByte) &&
               Arrays.equals(b1_arrayShort, that.b1_arrayShort) &&
               Objects.equals(b1_setInteger, that.b1_setInteger) &&
               Objects.equals(b1_listEmpty, that.b1_listEmpty) &&
               Objects.equals(b1_mapLongLong, that.b1_mapLongLong) &&
               Objects.equals(b1_listListByte, that.b1_listListByte) &&
               Objects.equals(b1_point, that.b1_point);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}