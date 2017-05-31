package de.exlll.configlib;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FieldFilterTest {

    @Test
    public void filterTestsFields() throws Exception {
        Predicate<Field> test = FieldFilter.INSTANCE;
        Class<?> cls = TestClass.class;

        assertThat(cls.getDeclaredFields().length, is(9));

        assertThat(test.test(cls.getDeclaredField("a")), is(false));
        assertThat(test.test(cls.getDeclaredField("b")), is(false));
        assertThat(test.test(cls.getDeclaredField("c")), is(false));
        assertThat(test.test(cls.getDeclaredField("d")), is(true));
        assertThat(test.test(cls.getDeclaredField("e")), is(true));
        assertThat(test.test(cls.getDeclaredField("f")), is(true));
        assertThat(test.test(cls.getDeclaredField("g")), is(true));
        assertThat(test.test(cls.getDeclaredField("h")), is(true));
        assertThat(test.test(cls.getDeclaredField("i")), is(true));

        cls = TestClassSynthetic.class;
        assertThat(test.test(cls.getDeclaredField("a")), is(true));
        assertThat(test.test(cls.getDeclaredField("this$0")), is(false));
    }

    private static final class TestClass {
        /* filtered fields */
        private static int a;
        private final int b = 1;
        private transient int c;

        /* not filtered fields */
        private int d;
        protected int e;
        int f;
        public int g;
        double h;
        volatile int i;
    }

    private final class TestClassSynthetic {
        int a;
    }
}