package de.exlll.configlib;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class FilteredFieldStreamSupplierTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void constructorRequiresNonNullPredicate() throws Exception {
        exception.expect(NullPointerException.class);
        new FilteredFieldStreamSupplier(getClass(), null);
    }

    @Test
    public void constructorRequiresNonNullClass() throws Exception {
        exception.expect(NullPointerException.class);
        new FilteredFieldStreamSupplier(null, field -> true);
    }

    @Test
    public void supplierReturnsStream() throws Exception {
        Supplier<Stream<Field>> supplier = new FilteredFieldStreamSupplier(
                getClass(), field -> true);

        Stream<Field> fieldStream = supplier.get();

        assertThat(fieldStream, is(notNullValue()));
    }

    @Test
    public void supplierApplysFilter() throws Exception {
        Supplier<Stream<Field>> supplier = new FilteredFieldStreamSupplier(
                TestClass.class, field -> !Modifier.isPublic(field.getModifiers()));

        Stream<Field> fieldStream = supplier.get();

        assertThat(fieldStream.count(), is(3L));
    }

    @Test
    public void toListReturnsFieldsAsList() throws Exception {
        FilteredFieldStreamSupplier supplier = new FilteredFieldStreamSupplier(
                TestClass.class, field -> true);

        List<Field> fields = supplier.toList();

        assertThat(fields.get(0), is(TestClass.class.getDeclaredField("i")));
        assertThat(fields.get(1), is(TestClass.class.getDeclaredField("j")));
        assertThat(fields.get(2), is(TestClass.class.getDeclaredField("k")));
        assertThat(fields.get(3), is(TestClass.class.getDeclaredField("l")));
    }

    private static final class TestClass {
        public int i;
        protected int j;
        int k;
        private int l;
    }

}