package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class FieldExtractorTest {
    @Test
    void applyCallsExtract() {
        FieldExtractor extractor = mock(FieldExtractor.class, CALLS_REAL_METHODS);

        extractor.apply(Object.class);

        verify(extractor).extract(Object.class);
    }
}