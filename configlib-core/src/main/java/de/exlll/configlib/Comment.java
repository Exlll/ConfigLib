package de.exlll.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element is saved together with explanatory
 * comments describing it.
 */
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {
    /**
     * Returns the comments of the annotated field or record component.
     *
     * @return field or record component comments
     */
    String[] value();
}