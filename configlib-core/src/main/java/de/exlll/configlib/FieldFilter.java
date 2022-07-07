package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.function.Predicate;

/**
 * Implementations of this interface test fields for specific conditions.
 */
@FunctionalInterface
public interface FieldFilter extends Predicate<Field> {
    @Override
    default FieldFilter and(Predicate<? super Field> other) {
        return field -> test(field) && other.test(field);
    }
}