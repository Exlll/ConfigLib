package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static de.exlll.configlib.TestUtils.assertThrowsNullPointerException;
import static de.exlll.configlib.TestUtils.getField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FieldFiltersTest {
    @Test
    void filterDefaultRequiresNonNullStream() {
        assertThrowsNullPointerException(
                () -> FieldFilters.DEFAULT.test(null),
                "field"
        );
    }

    @Test
    void filterDefault() {
        class A {
            class B {
                private final int i = 1;
                @Ignore
                private int j;
                private static int k;
                private transient int l;
                private int m;
            }
        }
        List<Field> actual = Arrays.stream(A.B.class.getDeclaredFields())
                .filter(FieldFilters.DEFAULT)
                .toList();
        List<Field> expected = List.of(getField(A.B.class, "m"));
        assertThat(actual, is(expected));
    }
}