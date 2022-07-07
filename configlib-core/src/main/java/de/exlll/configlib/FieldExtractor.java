package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Implementations of this interface extract the fields of classes.
 */
@FunctionalInterface
interface FieldExtractor extends Function<Class<?>, Stream<Field>> {
    /**
     * Extracts the fields of a class.
     *
     * @param cls the class
     * @return a stream of fields
     * @throws NullPointerException if {@code cls} is null
     */
    Stream<Field> extract(Class<?> cls);

    @Override
    default Stream<Field> apply(Class<?> cls) {
        return extract(cls);
    }
}