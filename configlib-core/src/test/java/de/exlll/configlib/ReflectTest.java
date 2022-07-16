package de.exlll.configlib;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static de.exlll.configlib.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ReflectTest {

    @Test
    void defaultValues() {
        assertThat(Reflect.getDefaultValue(boolean.class), is(false));
        assertThat(Reflect.getDefaultValue(byte.class), is((byte) 0));
        assertThat(Reflect.getDefaultValue(char.class), is('\0'));
        assertThat(Reflect.getDefaultValue(short.class), is((short) 0));
        assertThat(Reflect.getDefaultValue(int.class), is(0));
        assertThat(Reflect.getDefaultValue(long.class), is(0L));
        assertThat(Reflect.getDefaultValue(float.class), is(0f));
        assertThat(Reflect.getDefaultValue(double.class), is(0d));

        assertThat(Reflect.getDefaultValue(Boolean.class), nullValue());
        assertThat(Reflect.getDefaultValue(Integer.class), nullValue());
        assertThat(Reflect.getDefaultValue(Character.class), nullValue());
        assertThat(Reflect.getDefaultValue(Double.class), nullValue());
        assertThat(Reflect.getDefaultValue(Object.class), nullValue());
    }

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
    void getValueRecord() {
        record R(float f) {}

        float value = (float) Reflect.getValue(R.class.getRecordComponents()[0], new R(10f));
        assertThat(value, is(10f));
    }

    @Test
    void getValueRecordThrowsException() {
        record R(float f) {
            public float f() {
                throw new ConfigurationException("TEST");
            }
        }
        assertThrowsRuntimeException(
                () -> Reflect.getValue(R.class.getRecordComponents()[0], new R(10f)),
                "Invocation of method 'public float de.exlll.configlib.ReflectTest$2R.f()' " +
                "on record 'R[f=10.0]' failed."
        );
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

    record R1(int i, float f) {
        R1(int i) {
            this(i, 0);
        }

        R1(float f) {
            this(0, f);
        }

        R1(int i, float f, String s) {
            this(i, f);
        }
    }

    @Test
    void getCanonicalConstructor() throws NoSuchMethodException {
        Constructor<R1> constructor = Reflect.getCanonicalConstructor(R1.class);
        Class<?>[] classes = constructor.getParameterTypes();
        assertThat(classes.length, is(2));
        assertThat(classes[0], equalTo(int.class));
        assertThat(classes[1], equalTo(float.class));
    }

    @Test
    void newRecord1() {
        R1 r = Reflect.newRecord(R1.class, 1, 2f);
        assertThat(r.i, is(1));
        assertThat(r.f, is(2f));
    }

    record R2() {}

    @Test
    void newRecord2() {
        Reflect.newRecord(R2.class);
    }

    record R3(String s) {
        R3 {
            throw new IllegalArgumentException("Illegal: " + s);
        }
    }

    @Test
    void newRecordWithThrowingCtor() {
        assertThrowsRuntimeException(
                () -> Reflect.newRecord(R3.class, ""),
                "The canonical constructor of record type 'R3' threw an exception."
        );
    }

    @Test
    void newRecordRequiresRecordType() {
        class A {}
        assertThrowsConfigurationException(
                () -> Reflect.newRecord(A.class),
                "Class 'A' must be a record."
        );
    }

    @Test
    void newRecordWithDefaultValues() {
        record E() {}
        record R(boolean a, char b, byte c, short d, int e, long f, float g, double h,
                 Boolean i, Character j, Integer k, Float l, E m, R n, Object o) {}
        R r = Reflect.newRecordDefaultValues(R.class);
        assertFalse(r.a);
        assertEquals('\0', r.b);
        assertEquals(0, r.c);
        assertEquals(0, r.d);
        assertEquals(0, r.e);
        assertEquals(0, r.f);
        assertEquals(0, r.g);
        assertEquals(0, r.h);
        assertNull(r.i);
        assertNull(r.j);
        assertNull(r.k);
        assertNull(r.l);
        assertNull(r.m);
        assertNull(r.n);
        assertNull(r.o);
    }
}