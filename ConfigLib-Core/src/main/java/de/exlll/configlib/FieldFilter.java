package de.exlll.configlib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

enum FieldFilter implements Predicate<Field> {
    INSTANCE;

    /**
     * Tests if a field is not final, static, synthetic and transient.
     *
     * @param field Field that is tested
     * @return true if {@code field} is not final, static, synthetic or transient
     */
    @Override
    public boolean test(Field field) {
        int mods = field.getModifiers();
        boolean fst = Modifier.isFinal(mods) ||
                Modifier.isStatic(mods) ||
                Modifier.isTransient(mods);
        return !fst && !field.isSynthetic();
    }
}
