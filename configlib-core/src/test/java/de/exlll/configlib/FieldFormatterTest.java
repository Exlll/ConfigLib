package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

class FieldFormatterTest {
    @Test
    void applyCallsFormat() {
        class A {
            int i;
        }
        FieldFormatter formatter = mock(FieldFormatter.class, CALLS_REAL_METHODS);

        Field field = TestUtils.getField(A.class, "i");
        formatter.apply(field);

        verify(formatter).format(field);
    }
}