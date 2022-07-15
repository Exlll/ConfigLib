package de.exlll.configlib;

import java.util.function.Function;

/**
 * Implementations of this interface format the names of class fields or record components.
 */
@FunctionalInterface
public interface NameFormatter extends Function<String, String> {
    /**
     * Formats the name of a class field or record component.
     *
     * @param name the name that is formatted
     * @return formatted name
     * @throws NullPointerException if {@code name} is null
     */
    String format(String name);

    /**
     * Formats the name of a class field or record component.
     *
     * @param name the name that is formatted
     * @return formatted name
     * @throws NullPointerException if {@code name} is null
     */
    @Override
    default String apply(String name) {
        return format(name);
    }
}

