package de.exlll.configlib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

enum Reflect {
    ;
    private static final Class<?>[] SIMPLE_TYPES = {
            Boolean.class, String.class, Character.class,
            Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class
    };
    private static final Set<Class<?>> simpleTypes = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(SIMPLE_TYPES))
    );

    static boolean isDefault(Class<?> cls) {
        // default classes can be properly serialized by default
        return isSimpleType(cls) || isContainerType(cls);
    }

    static boolean isSimpleType(Class<?> cls) {
        return cls.isPrimitive() || simpleTypes.contains(cls);
    }

    static boolean isConfigList(Class<?> cls) {
        return ConfigList.class.isAssignableFrom(cls);
    }

    static boolean isConfigSet(Class<?> cls) {
        return ConfigSet.class.isAssignableFrom(cls);
    }

    static boolean isConfigMap(Class<?> cls) {
        return ConfigMap.class.isAssignableFrom(cls);
    }

    static boolean isContainerType(Class<?> cls) {
        return List.class.isAssignableFrom(cls) ||
                Set.class.isAssignableFrom(cls) ||
                Map.class.isAssignableFrom(cls);
    }

    static Object newInstance(Class<?> cls) {
        checkDefaultConstructor(cls);
        Constructor<?> constructor = getDefaultConstructor(cls);
        constructor.setAccessible(true);
        return newInstance(constructor);
    }

    private static Object newInstance(Constructor<?> constructor) {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    static void checkDefaultConstructor(Class<?> cls) {
        if (!hasDefaultConstructor(cls)) {
            String msg = "Class " + cls.getSimpleName() + " doesn't have a default constructor.";
            throw new IllegalArgumentException(msg);
        }
    }

    static Constructor<?> getDefaultConstructor(Class<?> cls) {
        try {
            return cls.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean hasDefaultConstructor(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredConstructors())
                .anyMatch(cst -> cst.getParameterCount() == 0);
    }

    static Object getValue(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            /* This exception is never thrown because we filter
             * inaccessible fields out. */
            throw new RuntimeException(e);
        }
    }

    static void setValue(Field field, Object instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            /* This exception is never thrown because we filter
             * inaccessible fields out. */
            throw new RuntimeException(e);
        }
    }

    static void checkType(Object object, Class<?> expectedType) {
        if (!expectedType.isAssignableFrom(object.getClass())) {
            String clsName = object.getClass().getSimpleName();
            String msg = "Invalid type!\n" +
                    "Object '" + object + "' is of type " + clsName + ". " +
                    "Expected type: " + expectedType.getSimpleName();
            throw new IllegalArgumentException(msg);
        }
    }

    static void checkMapEntries(Map<?, ?> map, Class<?> keyClass, Class<?> valueClass) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            checkType(entry.getKey(), keyClass);
            checkType(entry.getValue(), valueClass);
        }
    }
}
