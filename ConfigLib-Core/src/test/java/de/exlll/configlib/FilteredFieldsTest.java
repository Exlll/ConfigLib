package de.exlll.configlib;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FilteredFieldsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructorRequiresNonNullArray() throws Exception {
        expectedException.expect(NullPointerException.class);
        new FilteredFields(null, field -> true);
    }

    @Test
    public void constructorRequiresNonNullPredicate() throws Exception {
        expectedException.expect(NullPointerException.class);
        new FilteredFields(FilteredFields.class.getDeclaredFields(), null);

    }

    @Test
    public void constructorAppliesFilter() throws Exception {
        FilteredFields ff = new FilteredFields(
                TestClass.class.getDeclaredFields(), field -> {
            int mods = field.getModifiers();
            return Modifier.isPublic(mods);
        });

        for (Field f : ff) {
            int mods = f.getModifiers();
            assertThat(Modifier.isPublic(mods), is(true));
        }
    }

    private static final class TestClass {
        private int a;
        protected int b;
        int c;
        public int d;
    }

}