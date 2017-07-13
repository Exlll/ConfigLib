package de.exlll.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated {@link Configuration} has a version.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Version {
    /**
     * Returns the current version of a configuration.
     *
     * @return current configuration version
     */
    String version() default "1.0.0";

    /**
     * Returns the name of the version field.
     *
     * @return name of the version field
     */
    String fieldName() default "version";

    /**
     * Returns the comments describing the version field.
     *
     * @return comments describing the version field
     */
    String[] fieldComments() default {
            "", /* empty line */
            "The version of this configuration - DO NOT CHANGE!"
    };

    /**
     * Returns an {@link UpdateStrategy} describing the actions applied to different
     * versions of a configuration file when a version change is detected.
     *
     * @return {@code UpdateStrategy} applied to a configuration file
     */
    UpdateStrategy updateStrategy() default UpdateStrategy.DEFAULT;
}
