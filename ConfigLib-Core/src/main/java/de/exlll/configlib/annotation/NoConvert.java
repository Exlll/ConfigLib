package de.exlll.configlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated field should not be converted but instead used as is.
 * <p>
 * This may be useful if the configuration knows how to (de-)serialize
 * instances of that type. For example, a {@code BukkitYamlConfiguration}
 * knows how to serialize {@code ItemStack} instances.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoConvert {}
