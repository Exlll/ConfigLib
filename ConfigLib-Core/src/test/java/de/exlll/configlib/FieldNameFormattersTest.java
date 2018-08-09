package de.exlll.configlib;

import de.exlll.configlib.format.FieldNameFormatter;
import de.exlll.configlib.format.FieldNameFormatters;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FieldNameFormattersTest {
    @Test
    void identityReturnsSameName() {
        FieldNameFormatter formatter = FieldNameFormatters.IDENTITY;

        assertThat(formatter.fromFieldName("fieldName"), is("fieldName"));
    }

    @Test
    void lowerUnderscoreConvertsFromAndToCamelCase() {
        FieldNameFormatter formatter = FieldNameFormatters.LOWER_UNDERSCORE;

        assertThat(formatter.fromFieldName("fieldNameFormat"), is("field_name_format"));
    }
}