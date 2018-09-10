package de.exlll.configlib;

import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.annotation.Convert;
import de.exlll.configlib.annotation.Format;
import de.exlll.configlib.annotation.NoConvert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

enum Reflect {
    ;
    private static final Set<Class<?>> SIMPLE_TYPES = new HashSet<>(Arrays.asList(
            Boolean.class,
            Byte.class,
            Character.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            String.class
    ));

    static boolean isSimpleType(Class<?> cls) {
        return cls.isPrimitive() || SIMPLE_TYPES.contains(cls);
    }

    static boolean isContainerType(Class<?> cls) {
        return List.class.isAssignableFrom(cls) ||
                Set.class.isAssignableFrom(cls) ||
                Map.class.isAssignableFrom(cls);
    }

    static boolean isEnumType(Class<?> cls) {
        return cls.isEnum();
    }

    static <T> T newInstance(Class<T> cls) {
        try {
            Constructor<T> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            String msg = "Class " + cls.getSimpleName() + " doesn't have a " +
                    "no-args constructor.";
            throw new ConfigurationException(msg, e);
        } catch (IllegalAccessException e) {
            /* This exception should not be thrown because
             * we set the field to be accessible. */
            String msg = "No-args constructor of class " + cls.getSimpleName() +
                    " not accessible.";
            throw new ConfigurationException(msg, e);
        } catch (InstantiationException e) {
            String msg = "Class " + cls.getSimpleName() + " not instantiable.";
            throw new ConfigurationException(msg, e);
        } catch (InvocationTargetException e) {
            String msg = "Constructor of class " + cls.getSimpleName() +
                    " has thrown an exception.";
            throw new ConfigurationException(msg, e);
        }
    }

    static Object getValue(Field field, Object inst) {
        try {
            field.setAccessible(true);
            return field.get(inst);
        } catch (IllegalAccessException e) {
            /* This exception should not be thrown because
             * we set the field to be accessible. */
            String msg = "Illegal access of field '" + field + "' " +
                    "on object " + inst + ".";
            throw new ConfigurationException(msg, e);
        }
    }

    static void setValue(Field field, Object inst, Object value) {
        try {
            field.setAccessible(true);
            field.set(inst, value);
        } catch (IllegalAccessException e) {
            String msg = "Illegal access of field '" + field + "' " +
                    "on object " + inst + ".";
            throw new ConfigurationException(msg, e);
        }
    }

    static boolean hasConverter(Field field) {
        return field.isAnnotationPresent(Convert.class);
    }

    static boolean hasNoConvert(Field field) {
        return field.isAnnotationPresent(NoConvert.class);
    }

    static boolean hasFormatter(Class<?> cls) {
        return cls.isAnnotationPresent(Format.class);
    }

    static boolean isConfigurationElement(Class<?> cls) {
        return cls.isAnnotationPresent(ConfigurationElement.class);
    }

    static boolean hasNoArgConstructor(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredConstructors())
                .anyMatch(c -> c.getParameterCount() == 0);
    }
}
