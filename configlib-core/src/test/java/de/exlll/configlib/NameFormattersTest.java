package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class NameFormattersTest {
    private static final String NAME_1 = "lowercase";
    private static final String NAME_2 = "camelCase";
    private static final String NAME_3 = "withNumber123";
    private static final String NAME_4 = "with123Number";
    private static final String NAME_5 = "with_$";

    @Test
    void formatIdentity() {
        NameFormatters formatter = NameFormatters.IDENTITY;

        assertThat(formatter.format(NAME_1), is("lowercase"));
        assertThat(formatter.format(NAME_2), is("camelCase"));
        assertThat(formatter.format(NAME_3), is("withNumber123"));
        assertThat(formatter.format(NAME_4), is("with123Number"));
        assertThat(formatter.format(NAME_5), is("with_$"));
    }

    @Test
    void formatLowerUnderscore() {
        NameFormatters formatter = NameFormatters.LOWER_UNDERSCORE;

        assertThat(formatter.format(NAME_1), is("lowercase"));
        assertThat(formatter.format(NAME_2), is("camel_case"));
        assertThat(formatter.format(NAME_3), is("with_number123"));
        assertThat(formatter.format(NAME_4), is("with123_number"));
        assertThat(formatter.format(NAME_5), is("with_$"));
    }

    @Test
    void formatUpperUnderscore() {
        NameFormatters formatter = NameFormatters.UPPER_UNDERSCORE;

        assertThat(formatter.format(NAME_1), is("LOWERCASE"));
        assertThat(formatter.format(NAME_2), is("CAMEL_CASE"));
        assertThat(formatter.format(NAME_3), is("WITH_NUMBER123"));
        assertThat(formatter.format(NAME_4), is("WITH123_NUMBER"));
        assertThat(formatter.format(NAME_5), is("WITH_$"));
    }
}