package de.exlll.configlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated field should be ignored and is not used in the config.
 * <p>
 * This may be useful if you want to use a regular class in combination with
 * the {@link ConfigurationElement} annotation, but not serialize all properties of the class.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {}
