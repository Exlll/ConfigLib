package de.exlll.configlib;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationFieldFilterTest {
    private static final Supplier<Stream<Field>> streamSupplier = new FilteredFieldStreamSupplier(
            TestClass.class, ConfigurationFieldFilter.INSTANCE);
    private Stream<Field> fieldSupplier;

    @Before
    public void setUp() throws Exception {
        fieldSupplier = streamSupplier.get();
    }

    @Test
    public void filterFiltersSyntheticFields() throws Exception {
        Supplier<Stream<Field>> streamSupplier = new FilteredFieldStreamSupplier(
                TestClassSynthetic.class, ConfigurationFieldFilter.INSTANCE);

        streamSupplier.get().forEach(field -> assertThat(field.isSynthetic(), is(false)));
    }

    @Test
    public void filterFiltersFinalFields() throws Exception {
        fieldSupplier.forEach(field -> assertThat(
                Modifier.isFinal(field.getModifiers()), is(false)));
    }

    @Test
    public void filterFiltersStaticFields() throws Exception {
        fieldSupplier.forEach(field -> assertThat(
                Modifier.isStatic(field.getModifiers()), is(false)));
    }

    @Test
    public void filterFiltersTransientFields() throws Exception {
        fieldSupplier.forEach(field -> assertThat(
                Modifier.isTransient(field.getModifiers()), is(false)));
    }

    private static final class TestClass {
        /* filtered fields */
        private static int a;
        private final int b = 1;
        private transient int c;

        /* not filtered fields */
        private int d;
        protected int e;
        int f;
        public int g;
        double h;
        volatile int i;
    }

    private final class TestClassSynthetic {
        int a;
    }
}