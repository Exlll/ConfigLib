package de.exlll.configlib;

import java.util.Map;

interface Defaultable<T> {
    T toDefault();

    void fromDefault(Object value);

    default Object fromDefault(final Object instance, Class<?> cls) {
        Object newInstance = instance;
        if (!Reflect.isSimpleType(cls)) {
            Reflect.checkType(instance, Map.class);
            Reflect.checkMapEntries((Map<?, ?>) instance, String.class, Object.class);
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>) instance;
            newInstance = Reflect.newInstance(cls);
            FieldMapper.instanceFromMap(newInstance, map);
        }
        Reflect.checkType(newInstance, cls);
        return newInstance;
    }
}
