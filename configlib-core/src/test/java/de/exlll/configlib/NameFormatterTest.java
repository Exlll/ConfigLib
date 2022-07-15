package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class NameFormatterTest {
    @Test
    void applyCallsFormat() {
        NameFormatter formatter = mock(NameFormatter.class, CALLS_REAL_METHODS);
        String name = "i";
        formatter.apply(name);
        verify(formatter).format(name);
    }
}