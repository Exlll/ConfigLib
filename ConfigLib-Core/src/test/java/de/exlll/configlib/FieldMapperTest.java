package de.exlll.configlib;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class FieldMapperTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final FilteredFieldStreamSupplier streamSupplier =
            new FilteredFieldStreamSupplier(TestClass.class, x -> true);
    private static final FieldMapper mapper = new FieldMapper(streamSupplier);

    @Test
    public void constructorRequiresNonNullSupplier() throws Exception {
        exception.expect(NullPointerException.class);
        new FieldMapper(null);
    }

    @Test
    public void mapFieldNamesToValues() throws Exception {
        TestClass testClass = new TestClass();
        Map<String, Object> valuesByFieldNames = new LinkedHashMap<>();

        valuesByFieldNames.put("field1", "field1");
        valuesByFieldNames.put("field2", 2);
        valuesByFieldNames.put("field3", "field3");

        assertThat(valuesByFieldNames, is(mapper.mapFieldNamesToValues(testClass)));
    }

    @Test
    public void mapValuesToFields() throws Exception {
        Map<String, Object> valuesByFieldNames = new HashMap<>();
        valuesByFieldNames.put("field1", "new");
        valuesByFieldNames.put("field2", 10);

        TestClass testClass = new TestClass();
        mapper.mapValuesToFields(valuesByFieldNames, testClass);

        assertThat(testClass.field1, is("new"));
        assertThat(testClass.field2, is(10));
        assertThat(testClass.field3, is("field3"));
    }

    private static final class TestClass {
        @Comment("Comment1")
        private String field1 = "field1";
        @Comment({"Comment2", "Comment3"})
        private int field2 = 2;
        private String field3 = "field3";
    }
}