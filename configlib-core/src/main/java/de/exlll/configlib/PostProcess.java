package de.exlll.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element should be post-processed. This
 * annotation can be applied to fields and methods.
 * <p>
 * Applying this annotation to a field of a configuration class does not cause
 * any post-processing to be triggered automatically. However, both, the
 * presence of this annotation and the return value of its {@link #key()}
 * method can be used as a filter criterion when adding post-processors via a
 * {@code ConfigurationProperties} object.
 * <p>
 * When applied to a (non-static) method of a configuration type, the method is
 * called immediately after a configuration instance of that type has been
 * initialized. If the return type of the annotated method equals the
 * configuration type, the instance is replaced by the return value of that
 * method call. If the return type is {@code void}, then the method is simply
 * called on the given instance.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PostProcess {
    String key() default "";
}
