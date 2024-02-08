package de.exlll.configlib;

import de.exlll.configlib.ConfigurationElements.FieldElement;
import de.exlll.configlib.ConfigurationElements.RecordComponentElement;
import org.junit.jupiter.api.Test;

import static de.exlll.configlib.TestUtils.assertThrowsNullPointerException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationElementFilterTest {
    static final class FieldElementHolder {
        private final int x = 10;
        @PostProcess
        private final int y = 10;
        @PostProcess(key = "key1")
        private final int z = 10;
        @PostProcess(key = "key2")
        private final int w = 10;

        private final double dbl = 10;
        private final Object obj = new Object();
    }

    record RecordComponentElementHolder(
            int x,
            @PostProcess int y,
            @PostProcess(key = "key1") int z,
            @PostProcess(key = "key2") int w,
            double dbl,
            Object obj
    ) {}

    private static final Class<?> FE_HOLDER_TYPE = FieldElementHolder.class;
    private static final Class<?> RCE_HOLDER_TYPE = RecordComponentElementHolder.class;
    private static final FieldElement FE_X;
    private static final FieldElement FE_Y;
    private static final FieldElement FE_Z;
    private static final FieldElement FE_W;
    private static final FieldElement FE_DBL;
    private static final FieldElement FE_OBJ;
    private static final RecordComponentElement RCE_X;
    private static final RecordComponentElement RCE_Y;
    private static final RecordComponentElement RCE_Z;
    private static final RecordComponentElement RCE_W;
    private static final RecordComponentElement RCE_DBL;
    private static final RecordComponentElement RCE_OBJ;

    static {
        try {
            FE_X = new FieldElement(FE_HOLDER_TYPE.getDeclaredField("x"));
            FE_Y = new FieldElement(FE_HOLDER_TYPE.getDeclaredField("y"));
            FE_Z = new FieldElement(FE_HOLDER_TYPE.getDeclaredField("z"));
            FE_W = new FieldElement(FE_HOLDER_TYPE.getDeclaredField("w"));
            FE_DBL = new FieldElement(FE_HOLDER_TYPE.getDeclaredField("dbl"));
            FE_OBJ = new FieldElement(FE_HOLDER_TYPE.getDeclaredField("obj"));
            RCE_X = new RecordComponentElement(RCE_HOLDER_TYPE.getRecordComponents()[0]);
            RCE_Y = new RecordComponentElement(RCE_HOLDER_TYPE.getRecordComponents()[1]);
            RCE_Z = new RecordComponentElement(RCE_HOLDER_TYPE.getRecordComponents()[2]);
            RCE_W = new RecordComponentElement(RCE_HOLDER_TYPE.getRecordComponents()[3]);
            RCE_DBL = new RecordComponentElement(RCE_HOLDER_TYPE.getRecordComponents()[4]);
            RCE_OBJ = new RecordComponentElement(RCE_HOLDER_TYPE.getRecordComponents()[5]);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void byPostProcessKeyRequiresNonNull() {
        assertThrowsNullPointerException(
                () -> ConfigurationElementFilter.byPostProcessKey(null),
                "post-process key"
        );
    }

    @Test
    void byPostProcessKeyOnFields() throws NoSuchFieldException {
        final var filter = ConfigurationElementFilter.byPostProcessKey("key2");
        assertFalse(filter.test(FE_X));
        assertFalse(filter.test(FE_Y));
        assertFalse(filter.test(FE_Z));
        assertTrue(filter.test(FE_W));
        assertFalse(filter.test(FE_DBL));
        assertFalse(filter.test(FE_OBJ));
    }

    @Test
    void byTypeOnFields() {
        final var filter = ConfigurationElementFilter.byType(int.class);
        assertTrue(filter.test(FE_X));
        assertTrue(filter.test(FE_Y));
        assertTrue(filter.test(FE_Z));
        assertTrue(filter.test(FE_W));
        assertFalse(filter.test(FE_DBL));
        assertFalse(filter.test(FE_OBJ));
    }

    @Test
    void byPostProcessKeyOnRecordComponents() {
        final var filter = ConfigurationElementFilter.byPostProcessKey("key2");
        assertFalse(filter.test(RCE_X));
        assertFalse(filter.test(RCE_Y));
        assertFalse(filter.test(RCE_Z));
        assertTrue(filter.test(RCE_W));
        assertFalse(filter.test(RCE_DBL));
        assertFalse(filter.test(RCE_OBJ));
    }

    @Test
    void byTypeOnRecordComponents() {
        final var filter = ConfigurationElementFilter.byType(Object.class);
        assertFalse(filter.test(RCE_X));
        assertFalse(filter.test(RCE_Y));
        assertFalse(filter.test(RCE_Z));
        assertFalse(filter.test(RCE_W));
        assertFalse(filter.test(RCE_DBL));
        assertTrue(filter.test(RCE_OBJ));
    }
}