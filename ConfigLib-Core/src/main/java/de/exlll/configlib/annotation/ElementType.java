package de.exlll.configlib.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the of elements a {@code Collection} or {@code Map} contains.
 * <p>
 * This annotation must be used if element type is not simple.
 */
@Target(java.lang.annotation.ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementType {
    Class<?> value();
}
