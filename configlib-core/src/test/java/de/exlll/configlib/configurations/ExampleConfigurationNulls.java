package de.exlll.configlib.configurations;

import de.exlll.configlib.Configuration;

import java.awt.Point;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Configuration
public final class ExampleConfigurationNulls {
    /* FIELDS SET TO NULL */
    private Integer nullInteger;
    private String nullString;
    private ExampleEnum nullEnm;
    private ExampleConfigurationB1 nullB1;
    private List<String> nullList;
    private Double[] nullArray;
    private Set<ExampleConfigurationB2> nullSet;
    private Map<LocalDate, BigDecimal> nullMap;
    private Point nullPoint;

    /* NULL VALUES IN CONTAINER */
    private List<String> listNullString;
    private Double[] arrayNullDouble;
    private Set<Integer> setNullInteger;
    private Map<ExampleEnum, BigInteger> mapNullEnmKey;
    private Map<ExampleEnum, BigInteger> mapNullBigIntegerValue;

    public Integer getNullInteger() {
        return nullInteger;
    }

    public void setNullInteger(Integer nullInteger) {
        this.nullInteger = nullInteger;
    }

    public String getNullString() {
        return nullString;
    }

    public void setNullString(String nullString) {
        this.nullString = nullString;
    }

    public ExampleEnum getNullEnm() {
        return nullEnm;
    }

    public void setNullEnm(ExampleEnum nullEnm) {
        this.nullEnm = nullEnm;
    }

    public ExampleConfigurationB1 getNullB1() {
        return nullB1;
    }

    public void setNullB1(ExampleConfigurationB1 nullB1) {
        this.nullB1 = nullB1;
    }

    public List<String> getNullList() {
        return nullList;
    }

    public void setNullList(List<String> nullList) {
        this.nullList = nullList;
    }

    public Double[] getNullArray() {
        return nullArray;
    }

    public void setNullArray(Double[] nullArray) {
        this.nullArray = nullArray;
    }

    public Set<ExampleConfigurationB2> getNullSet() {
        return nullSet;
    }

    public void setNullSet(Set<ExampleConfigurationB2> nullSet) {
        this.nullSet = nullSet;
    }

    public Map<LocalDate, BigDecimal> getNullMap() {
        return nullMap;
    }

    public void setNullMap(Map<LocalDate, BigDecimal> nullMap) {
        this.nullMap = nullMap;
    }

    public Point getNullPoint() {
        return nullPoint;
    }

    public void setNullPoint(Point nullPoint) {
        this.nullPoint = nullPoint;
    }

    public List<String> getListNullString() {
        return listNullString;
    }

    public void setListNullString(List<String> listNullString) {
        this.listNullString = listNullString;
    }

    public Double[] getArrayNullDouble() {
        return arrayNullDouble;
    }

    public void setArrayNullDouble(Double[] arrayNullDouble) {
        this.arrayNullDouble = arrayNullDouble;
    }

    public Set<Integer> getSetNullInteger() {
        return setNullInteger;
    }

    public void setSetNullInteger(Set<Integer> setNullInteger) {
        this.setNullInteger = setNullInteger;
    }

    public Map<ExampleEnum, BigInteger> getMapNullEnmKey() {
        return mapNullEnmKey;
    }

    public void setMapNullEnmKey(Map<ExampleEnum, BigInteger> mapNullEnmKey) {
        this.mapNullEnmKey = mapNullEnmKey;
    }

    public Map<ExampleEnum, BigInteger> getMapNullBigIntegerValue() {
        return mapNullBigIntegerValue;
    }

    public void setMapNullBigIntegerValue(Map<ExampleEnum, BigInteger> mapNullBigIntegerValue) {
        this.mapNullBigIntegerValue = mapNullBigIntegerValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExampleConfigurationNulls that = (ExampleConfigurationNulls) o;
        return Objects.equals(nullInteger, that.nullInteger) &&
               Objects.equals(nullString, that.nullString) &&
               nullEnm == that.nullEnm &&
               Objects.equals(nullB1, that.nullB1) &&
               Objects.equals(nullList, that.nullList) &&
               Arrays.equals(nullArray, that.nullArray) &&
               Objects.equals(nullSet, that.nullSet) &&
               Objects.equals(nullMap, that.nullMap) &&
               Objects.equals(nullPoint, that.nullPoint) &&
               Objects.equals(listNullString, that.listNullString) &&
               Arrays.equals(arrayNullDouble, that.arrayNullDouble) &&
               Objects.equals(setNullInteger, that.setNullInteger) &&
               Objects.equals(mapNullEnmKey, that.mapNullEnmKey) &&
               Objects.equals(mapNullBigIntegerValue, that.mapNullBigIntegerValue);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(
                nullInteger,
                nullString,
                nullEnm,
                nullB1,
                nullList,
                nullSet,
                nullMap,
                nullPoint,
                listNullString,
                setNullInteger,
                mapNullEnmKey,
                mapNullBigIntegerValue
        );
        result = 31 * result + Arrays.hashCode(nullArray);
        result = 31 * result + Arrays.hashCode(arrayNullDouble);
        return result;
    }
}
