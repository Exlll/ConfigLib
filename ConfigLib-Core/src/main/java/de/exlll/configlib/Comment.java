package de.exlll.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element is saved together with explanatory
 * comments describing it.
 * <p>
 * When this annotation is used on a class, the comments returned by its
 * {@link #value()} method are saved at the beginning of the configuration file.
 * If it's used on a field, the comments are saved above the field name.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {
    /**
     * Returns the comments of the annotated type or field.
     * <p>
     * When the configuration is saved, every comment is written into a new line.
     * Empty comments function as newlines.
     *
     * @return class or field comments
     */
    String[] value();
}
