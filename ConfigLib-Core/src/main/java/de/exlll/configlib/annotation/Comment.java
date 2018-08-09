package de.exlll.configlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element is saved together with explanatory
 * comments describing it.
 * <p>
 * For {@link de.exlll.configlib.configs.yaml.YamlConfiguration YamlConfiguration}s:
 * <ul>
 * <li>
 * If this annotation is used on a class, the comments returned by the
 * {@link #value()} method are saved at the beginning of the configuration file.
 * </li>
 * <li>
 * If this annotation is used on a field, the comments are saved above the field name.
 * </li>
 * </ul>
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {
    /**
     * Returns the comments of the annotated type or field.
     *
     * @return class or field comments
     */
    String[] value();
}
