package de.exlll.configlib.filter;

import de.exlll.configlib.classes.ClassWithFinalStaticTransientField;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FieldFilterTest {
    private static final FieldFilter filter = FieldFilters.DEFAULT;
    private static final Class<ClassWithFinalStaticTransientField> CWFSTF =
            ClassWithFinalStaticTransientField.class;

    @Test
    void filteredFieldsFiltersFields() throws NoSuchFieldException {
        List<? extends Field> fields = filter.filterDeclaredFieldsOf(CWFSTF);
        assertThat(fields.size(), is(0));

        class A {
            private int i;
            private final int j = 0;
            private transient int k;
        }
        fields = filter.filterDeclaredFieldsOf(A.class);
        assertThat(fields.size(), is(1));
        assertThat(fields.get(0), is(A.class.getDeclaredField("i")));
    }

    @Test
    void defaultFilterFiltersSyntheticFields() {
        for (Field field : ClassWithSyntheticField.class.getDeclaredFields()) {
            assertThat(field.isSynthetic(), is(true));
            assertThat(filter.test(field), is(false));
        }
    }

    @Test
    void defaultFilterFiltersFinalStaticTransientFields()
            throws NoSuchFieldException {
        Field field = CWFSTF.getDeclaredField("i");
        assertThat(Modifier.isFinal(field.getModifiers()), is(true));
        assertThat(filter.test(field), is(false));

        field = CWFSTF.getDeclaredField("j");
        assertThat(Modifier.isStatic(field.getModifiers()), is(true));
        assertThat(filter.test(field), is(false));

        field = CWFSTF.getDeclaredField("k");
        assertThat(Modifier.isTransient(field.getModifiers()), is(true));
        assertThat(filter.test(field), is(false));
    }

    private final class ClassWithSyntheticField {}
}