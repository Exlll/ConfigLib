package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static de.exlll.configlib.TestUtils.getField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FieldFormattersTest {
    private static class A {
        String lowercase;
        String camelCase;
        String withNumber123;
        String with123Number;
        String with_$;
    }

    private static final Field f1 = getField(A.class, "lowercase");
    private static final Field f2 = getField(A.class, "camelCase");
    private static final Field f3 = getField(A.class, "withNumber123");
    private static final Field f4 = getField(A.class, "with123Number");
    private static final Field f5 = getField(A.class, "with_$");

    @Test
    void formatIdentity() {
        FieldFormatters formatter = FieldFormatters.IDENTITY;

        assertThat(formatter.format(f1), is("lowercase"));
        assertThat(formatter.format(f2), is("camelCase"));
        assertThat(formatter.format(f3), is("withNumber123"));
        assertThat(formatter.format(f4), is("with123Number"));
        assertThat(formatter.format(f5), is("with_$"));
    }

    @Test
    void formatLowerUnderscore() {
        FieldFormatters formatter = FieldFormatters.LOWER_UNDERSCORE;

        assertThat(formatter.format(f1), is("lowercase"));
        assertThat(formatter.format(f2), is("camel_case"));
        assertThat(formatter.format(f3), is("with_number123"));
        assertThat(formatter.format(f4), is("with123_number"));
        assertThat(formatter.format(f5), is("with_$"));
    }

    @Test
    void formatUpperUnderscore() {
        FieldFormatters formatter = FieldFormatters.UPPER_UNDERSCORE;

        assertThat(formatter.format(f1), is("LOWERCASE"));
        assertThat(formatter.format(f2), is("CAMEL_CASE"));
        assertThat(formatter.format(f3), is("WITH_NUMBER123"));
        assertThat(formatter.format(f4), is("WITH123_NUMBER"));
        assertThat(formatter.format(f5), is("WITH_$"));
    }
}