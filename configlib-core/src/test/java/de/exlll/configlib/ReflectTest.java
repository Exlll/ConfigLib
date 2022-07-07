package de.exlll.configlib;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static de.exlll.configlib.TestUtils.assertThrowsRuntimeException;
import static de.exlll.configlib.TestUtils.getField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class ReflectTest {

    static class B1 {
        B1(int i) {}
    }

    @Test
    void newInstanceRequiresNoArgsCtor() {
        assertThrowsRuntimeException(
                () -> Reflect.newInstance(B1.class),
                "Class B1 doesn't have a no-args constructor."
        );
    }

    static abstract class B2 {}

    @Test
    void newInstanceRequiresConcreteClass() {
        assertThrowsRuntimeException(
                () -> Reflect.newInstance(B2.class),
                "Class B2 is not instantiable."
        );
    }

    static class B3 {
        B3() {
            throw new RuntimeException();
        }
    }

    @Test
    void newInstanceRequiresNonThrowingCtor() {
        assertThrowsRuntimeException(
                () -> Reflect.newInstance(B3.class),
                "Constructor of class B3 threw an exception."
        );
    }

    static class B4 {
        int i = 10;
    }

    @Test
    void newInstance() {
        B4 inst = Reflect.newInstance(B4.class);
        assertThat(inst, notNullValue());
        assertThat(inst.i, is(10));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void newArray(int arrayLength) {
        String[] strings = Reflect.newArray(String.class, arrayLength);
        assertThat(strings.length, is(arrayLength));

        int[][] ints = Reflect.newArray(int[].class, arrayLength);
        assertThat(ints.length, is(arrayLength));
    }

    @Test
    void getValue() {
        class A {
            private int i = 10;
        }
        int value = (int) Reflect.getValue(getField(A.class, "i"), new A());
        assertThat(value, is(10));
    }

    @Test
    void setValue() {
        class A {
            private int i = 10;
        }
        A a = new A();
        Reflect.setValue(getField(A.class, "i"), a, 20);
        assertThat(a.i, is(20));
    }

    @Test
    void setValueDoesNotSetFinalField() {
        class A {
            private final int i = 10;
        }
        A a = new A();
        Reflect.setValue(getField(A.class, "i"), a, 20);
        assertThat(a.i, is(10));
    }

    @Test
    void isIntegerType() {
        assertThat(Reflect.isIntegerType(byte.class), is(true));
        assertThat(Reflect.isIntegerType(Byte.class), is(true));
        assertThat(Reflect.isIntegerType(short.class), is(true));
        assertThat(Reflect.isIntegerType(Short.class), is(true));
        assertThat(Reflect.isIntegerType(int.class), is(true));
        assertThat(Reflect.isIntegerType(Integer.class), is(true));
        assertThat(Reflect.isIntegerType(long.class), is(true));
        assertThat(Reflect.isIntegerType(Long.class), is(true));
        assertThat(Reflect.isIntegerType(float.class), is(false));
        assertThat(Reflect.isIntegerType(Float.class), is(false));
        assertThat(Reflect.isIntegerType(double.class), is(false));
        assertThat(Reflect.isIntegerType(Double.class), is(false));
        assertThat(Reflect.isIntegerType(String.class), is(false));
        assertThat(Reflect.isIntegerType(Object.class), is(false));
    }

    @Test
    void isFloatingPointType() {
        assertThat(Reflect.isFloatingPointType(byte.class), is(false));
        assertThat(Reflect.isFloatingPointType(Byte.class), is(false));
        assertThat(Reflect.isFloatingPointType(short.class), is(false));
        assertThat(Reflect.isFloatingPointType(Short.class), is(false));
        assertThat(Reflect.isFloatingPointType(int.class), is(false));
        assertThat(Reflect.isFloatingPointType(Integer.class), is(false));
        assertThat(Reflect.isFloatingPointType(long.class), is(false));
        assertThat(Reflect.isFloatingPointType(Long.class), is(false));
        assertThat(Reflect.isFloatingPointType(float.class), is(true));
        assertThat(Reflect.isFloatingPointType(Float.class), is(true));
        assertThat(Reflect.isFloatingPointType(double.class), is(true));
        assertThat(Reflect.isFloatingPointType(Double.class), is(true));
        assertThat(Reflect.isFloatingPointType(String.class), is(false));
        assertThat(Reflect.isFloatingPointType(Object.class), is(false));
    }

    @Test
    void isEnumType() {
        enum A {}
        class B {}
        assertThat(Reflect.isEnumType(A.class), is(true));
        assertThat(Reflect.isEnumType(B.class), is(false));
    }

    @Test
    void isArrayType() {
        enum A {}
        class B {}
        assertThat(Reflect.isArrayType(A.class), is(false));
        assertThat(Reflect.isArrayType(B.class), is(false));
        assertThat(Reflect.isArrayType(int.class), is(false));
        assertThat(Reflect.isArrayType(A[].class), is(true));
        assertThat(Reflect.isArrayType(B[].class), is(true));
        assertThat(Reflect.isArrayType(int[].class), is(true));
        assertThat(Reflect.isArrayType(A[][].class), is(true));
        assertThat(Reflect.isArrayType(B[][].class), is(true));
        assertThat(Reflect.isArrayType(int[][].class), is(true));
    }

    @Test
    void isListType() {
        assertThat(Reflect.isListType(Object.class), is(false));
        assertThat(Reflect.isListType(HashMap.class), is(false));
        assertThat(Reflect.isListType(HashSet.class), is(false));
        assertThat(Reflect.isListType(ArrayList.class), is(true));
    }

    @Test
    void isSetType() {
        assertThat(Reflect.isSetType(Object.class), is(false));
        assertThat(Reflect.isSetType(HashMap.class), is(false));
        assertThat(Reflect.isSetType(HashSet.class), is(true));
        assertThat(Reflect.isSetType(ArrayList.class), is(false));
    }

    @Test
    void isMapType() {
        assertThat(Reflect.isMapType(Object.class), is(false));
        assertThat(Reflect.isMapType(HashMap.class), is(true));
        assertThat(Reflect.isMapType(HashSet.class), is(false));
        assertThat(Reflect.isMapType(ArrayList.class), is(false));
    }

    @Test
    void isConfiguration() {
        class A {}
        class B extends A {}
        @Configuration
        class C {}
        class D extends C {}

        assertThat(Reflect.isConfiguration(A.class), is(false));
        assertThat(Reflect.isConfiguration(B.class), is(false));
        assertThat(Reflect.isConfiguration(C.class), is(true));
        assertThat(Reflect.isConfiguration(D.class), is(true));
    }

    @Test
    void isIgnored() {
        class A {
            @Ignore
            private int a;
            private int b;
        }

        Field fieldA = getField(A.class, "a");
        Field fieldB = getField(A.class, "b");

        assertThat(Reflect.isIgnored(fieldA), is(true));
        assertThat(Reflect.isIgnored(fieldB), is(false));
    }
}