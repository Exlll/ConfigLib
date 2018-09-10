package de.exlll.configlib.annotation;

import de.exlll.configlib.format.FieldNameFormatter;
import de.exlll.configlib.format.FieldNameFormatters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a specific {@code FieldNameFormatter} is used. If a
 * {@link #formatterClass()} is specified, the {@code FieldNameFormatters}
 * returned by {@link #value()} is ignored. The {@code formatterClass} must
 * be instantiable and must have a no-args constructor.
 * <p>
 * This annotation takes precedence over the value set in properties object.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Format {
    /**
     * Returns the {@code FieldNameFormatters} instance to be for formatting.
     * The return value of this method is ignored if a {@code formatterClass()}
     * is set.
     *
     * @return {@code FieldNameFormatters} instance
     */
    FieldNameFormatters value() default FieldNameFormatters.IDENTITY;

    /**
     * Returns the class of a {@code FieldNameFormatter} implementation.
     * The class must be instantiable and must have a no-args constructor.
     *
     * @return class of {@code FieldNameFormatter} implementation
     */
    Class<? extends FieldNameFormatter> formatterClass() default FieldNameFormatter.class;
}
