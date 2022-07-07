package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * Implementations of this interface format the names of fields.
 */
@FunctionalInterface
public interface FieldFormatter extends Function<Field, String> {
    /**
     * Formats the name of the given field.
     *
     * @param field the field
     * @return formatted field name
     * @throws NullPointerException if {@code field} is null
     */
    String format(Field field);

    @Override
    default String apply(Field field) {
        return format(field);
    }
}
