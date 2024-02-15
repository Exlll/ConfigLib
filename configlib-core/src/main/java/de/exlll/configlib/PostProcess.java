package de.exlll.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element should be post-processed. This
 * annotation can be applied to configuration elements and methods.
 * <p>
 * Applying this annotation to configuration elements allows filtering these
 * elements easily via {@code ConfigurationElementFilter.byPostProcessKey}.
 * There mere presence of this annotation on a configuration element, however,
 * does not cause any post-processing to be triggered automatically.
 * <p>
 * Applying this annotation to some (non-static/abstract) method of a
 * configuration type, results in that method being called after a configuration
 * instance of that type has been initialized. If the return type of the
 * annotated method is 'void', then the method is simply called. If the return
 * type equals the configuration type, the instance is replaced by the return
 * value of that method call.
 *
 * @see ConfigurationElementFilter#byPostProcessKey(String)
 * @see ConfigurationProperties.Builder#addPostProcessor
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface PostProcess {
    /**
     * Returns some key that can be used for filtering configuration elements.
     * <p>
     * Specifying a key if this annotation is used on a method currently has no
     * effect but may be changed in the future.
     *
     * @return some arbitrary (developer-chosen) value
     */
    String key() default "";
}
