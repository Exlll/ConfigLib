package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class FilteredFieldStreamSupplier implements Supplier<Stream<Field>> {
    private final Class<?> cls;
    private final Supplier<Stream<Field>> streamSupplier;

    FilteredFieldStreamSupplier(Class<?> cls, Predicate<Field> fieldFilter) {
        Objects.requireNonNull(cls);
        Objects.requireNonNull(fieldFilter);
        this.cls = cls;
        Field[] fields = cls.getDeclaredFields();
        streamSupplier = () -> Arrays.stream(fields).filter(fieldFilter);
    }

    @Override
    public Stream<Field> get() {
        return streamSupplier.get();
    }

    public List<Field> toList() {
        return streamSupplier.get().collect(Collectors.toList());
    }

    public Class<?> getSupplyingClass() {
        return cls;
    }
}
