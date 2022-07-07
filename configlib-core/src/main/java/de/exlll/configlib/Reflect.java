package de.exlll.configlib;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class Reflect {
    private Reflect() {}

    static <T> T newInstance(Class<T> cls) {
        try {
            Constructor<T> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            String msg = "Class " + cls.getSimpleName() + " doesn't have a " +
                         "no-args constructor.";
            throw new RuntimeException(msg, e);
        } catch (IllegalAccessException e) {
            /* This exception should not be thrown because
             * we set the constructor to be accessible. */
            String msg = "No-args constructor of class " + cls.getSimpleName() +
                         " not accessible.";
            throw new RuntimeException(msg, e);
        } catch (InstantiationException e) {
            String msg = "Class " + cls.getSimpleName() + " is not instantiable.";
            throw new RuntimeException(msg, e);
        } catch (InvocationTargetException e) {
            String msg = "Constructor of class " + cls.getSimpleName() + " threw an exception.";
            throw new RuntimeException(msg, e);
        }
    }

    static <T> T[] newArray(Class<T> componentType, int length) {
        // The following cast won't fail because we just created an array of that type
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(componentType, length);
        return array;
    }

    static Object getValue(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            /* This exception should not be thrown because
             * we set the field to be accessible. */
            String msg = "Illegal access of field '" + field + "' " +
                         "on object " + instance + ".";
            throw new RuntimeException(msg, e);
        }
    }

    static void setValue(Field field, Object instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            /* This exception should not be thrown because
             * we set the field to be accessible. */
            String msg = "Illegal access of field '" + field + "' " +
                         "on object " + instance + ".";
            throw new RuntimeException(msg, e);
        }
    }

    static boolean isIntegerType(Class<?> cls) {
        return (cls == byte.class) || (cls == Byte.class) ||
               (cls == short.class) || (cls == Short.class) ||
               (cls == int.class) || (cls == Integer.class) ||
               (cls == long.class) || (cls == Long.class);
    }

    static boolean isFloatingPointType(Class<?> cls) {
        return (cls == float.class) || (cls == Float.class) ||
               (cls == double.class) || (cls == Double.class);
    }

    static boolean isEnumType(Class<?> cls) {
        return cls.isEnum();
    }

    static boolean isArrayType(Class<?> cls) {
        return cls.isArray();
    }

    static boolean isListType(Class<?> cls) {
        return List.class.isAssignableFrom(cls);
    }

    static boolean isSetType(Class<?> cls) {
        return Set.class.isAssignableFrom(cls);
    }

    static boolean isMapType(Class<?> cls) {
        return Map.class.isAssignableFrom(cls);
    }

    static boolean isConfiguration(Class<?> cls) {
        return cls.getAnnotation(Configuration.class) != null;
    }

    static boolean isIgnored(Field field) {
        return field.getAnnotation(Ignore.class) != null;
    }
}
