package de.exlll.configlib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

enum FieldFilters implements FieldFilter {
    /**
     * The default {@code FieldFilter} that rejects fields that are final, static,
     * synthetic, transient, or annotated with {@code @Ignore}.
     */
    DEFAULT {
        @Override
        public boolean test(Field field) {
            Validator.requireNonNull(field, "field");

            if (field.isSynthetic())
                return false;
            if (Reflect.isIgnored(field))
                return false;

            int modifiers = field.getModifiers();
            return !Modifier.isFinal(modifiers) &&
                   !Modifier.isStatic(modifiers) &&
                   !Modifier.isTransient(modifiers);
        }
    }
}
