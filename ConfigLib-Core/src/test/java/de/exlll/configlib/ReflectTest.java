package de.exlll.configlib;

import de.exlll.configlib.classes.TestClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static de.exlll.configlib.util.CollectionFactory.setOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ReflectTest {
    private static final Set<Class<?>> ALL_SIMPLE_TYPES = setOf(
            boolean.class, Boolean.class,
            byte.class, Byte.class,
            char.class, Character.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            String.class
    );

    @Test
    void isSimpleType() {
        for (Class<?> cls : ALL_SIMPLE_TYPES) {
            assertThat(Reflect.isSimpleType(cls), is(true));
        }
        assertThat(Reflect.isSimpleType(Object.class), is(false));
    }

    @Test
    void isContainerType() {
        assertThat(Reflect.isContainerType(Object.class), is(false));
        assertThat(Reflect.isContainerType(HashMap.class), is(true));
        assertThat(Reflect.isContainerType(HashSet.class), is(true));
        assertThat(Reflect.isContainerType(ArrayList.class), is(true));
    }

    @Test
    void getValue() throws NoSuchFieldException {
        TestClass testClass = TestClass.TEST_VALUES;
        Field f1 = TestClass.class.getDeclaredField("string");
        Field f2 = TestClass.class.getDeclaredField("primLong");
        Field f3 = TestClass.class.getDeclaredField("staticFinalInt");

        Object value = Reflect.getValue(f1, testClass);
        assertThat(value, is(testClass.getString()));

        value = Reflect.getValue(f2, testClass);
        assertThat(value, is(testClass.getPrimLong()));

        value = Reflect.getValue(f3, testClass);
        assertThat(value, is(TestClass.getStaticFinalInt()));
    }
}