package de.exlll.configlib.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the type of elements a {@code Collection} or {@code Map} contains.
 * <p>
 * This annotation must only be used if a {@code Collection} or {@code Map} contains
 * elements whose type is not simple. Note that {@code Map} keys can only be of some
 * simple type.
 * <p>
 * If collections are nested, the {@code nestingLevel} must be set. Examples:
 * <ul>
 * <li>nestingLevel 1: {@code List<List<T>>}</li>
 * <li>nestingLevel 1: {@code List<Set<T>>}</li>
 * <li>nestingLevel 1: {@code List<Map<String, T>>}</li>
 * <li>nestingLevel 2: {@code List<List<List<T>>>}</li>
 * <li>nestingLevel 2: {@code List<Set<List<T>>>}</li>
 * <li>nestingLevel 2: {@code List<List<Map<String, T>>>}</li>
 * </ul>
 */
@Target(java.lang.annotation.ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementType {
    /**
     * Returns the type of elements a {@code Collection} or {@code Map} contains.
     *
     * @return type of elements.
     */
    Class<?> value();

    /**
     * Returns the nesting level
     *
     * @return nesting level
     */
    int nestingLevel() default 0;
}
