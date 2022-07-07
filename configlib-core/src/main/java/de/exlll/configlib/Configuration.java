package de.exlll.configlib;

import java.lang.annotation.*;

/**
 * Indicates the annotated type is a configuration.
 * <p>
 * Configuration classes must have a no-args constructor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Configuration {}
