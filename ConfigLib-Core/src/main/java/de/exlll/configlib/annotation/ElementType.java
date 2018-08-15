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
 */
@Target(java.lang.annotation.ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementType {
    Class<?> value();
}
