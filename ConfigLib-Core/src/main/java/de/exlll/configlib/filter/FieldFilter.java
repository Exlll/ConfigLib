package de.exlll.configlib.filter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@FunctionalInterface
public interface FieldFilter extends Predicate<Field> {

    @Override
    default FieldFilter and(Predicate<? super Field> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }

    default List<? extends Field> filterDeclaredFieldsOf(Class<?> cls) {
        Field[] fields = cls.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(this)
                .collect(toList());
    }
}
