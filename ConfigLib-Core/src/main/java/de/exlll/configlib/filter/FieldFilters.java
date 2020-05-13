package de.exlll.configlib.filter;

import de.exlll.configlib.annotation.Ignore;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public enum FieldFilters implements FieldFilter {
    DEFAULT {
        @Override
        public boolean test(Field field) {
            if (field.isSynthetic()) {
                return false;
            }

            if (field.isAnnotationPresent(Ignore.class)) {
                return false;
            }

            int mods = field.getModifiers();
            return !(Modifier.isFinal(mods) ||
                    Modifier.isStatic(mods) ||
                    Modifier.isTransient(mods));
        }
    }
}
