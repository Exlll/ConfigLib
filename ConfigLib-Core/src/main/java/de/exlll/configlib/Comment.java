package de.exlll.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code Comment} annotation can be used to add comments to a configuration file.
 * <p>
 * Each {@code String} is written into a new line.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {
    String[] value();
}
