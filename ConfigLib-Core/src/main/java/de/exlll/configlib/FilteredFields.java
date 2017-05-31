package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class FilteredFields implements Iterable<Field> {
    private final List<Field> filteredFields;

    FilteredFields(Field[] fields, Predicate<Field> filter) {
        Objects.requireNonNull(fields);
        Objects.requireNonNull(filter);

        this.filteredFields = Arrays.stream(fields)
                .filter(filter)
                .collect(Collectors.toList());
    }

    static FilteredFields of(Class<?> cls) {
        Field[] fields = cls.getDeclaredFields();
        return new FilteredFields(fields, FieldFilter.INSTANCE);
    }

    @Override
    public Iterator<Field> iterator() {
        return filteredFields.iterator();
    }
}
