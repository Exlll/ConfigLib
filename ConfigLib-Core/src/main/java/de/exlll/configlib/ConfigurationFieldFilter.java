package de.exlll.configlib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

enum ConfigurationFieldFilter implements Predicate<Field> {
    INSTANCE;

    @Override
    public boolean test(Field field) {
        int modifiers = field.getModifiers();
        boolean fst = Modifier.isFinal(modifiers) ||
                Modifier.isStatic(modifiers) ||
                Modifier.isTransient(modifiers);
        return !(field.isSynthetic() || fst);
    }
}
