package de.exlll.configlib;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.exlll.configlib.Validator.requireRecord;

final class Reflect {
    private static final Map<Class<?>, Object> DEFAULT_VALUES = initDefaultValues();

    private Reflect() {}

    private static Map<Class<?>, Object> initDefaultValues() {
        return Stream.of(
                        boolean.class,
                        char.class,
                        byte.class,
                        short.class,
                        int.class,
                        long.class,
                        float.class,
                        double.class
                )
                .collect(Collectors.toMap(
                        type -> type,
                        type -> Array.get(Array.newInstance(type, 1), 0)
                ));
    }

    static <T> T getDefaultValue(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T defaultValue = (T) DEFAULT_VALUES.get(clazz);
        return defaultValue;
    }

    static <T> T callConstructor(Class<T> cls, Class<?>[] argumentTypes, Object... arguments) {
        try {
            Constructor<T> constructor = cls.getDeclaredConstructor(argumentTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(arguments);
        } catch (NoSuchMethodException e) {
            String msg = "Type '%s' doesn't have a constructor with parameters: %s."
                    .formatted(cls.getSimpleName(), argumentTypeNamesJoined(argumentTypes));
            throw new RuntimeException(msg, e);
        } catch (IllegalAccessException e) {
            // cannot happen because we set the constructor to be accessible.
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            String msg = "Type '%s' is not instantiable.".formatted(cls.getSimpleName());
            throw new RuntimeException(msg, e);
        } catch (InvocationTargetException e) {
            String msg = "Constructor of type '%s' with parameters '%s' threw an exception."
                    .formatted(cls.getSimpleName(), argumentTypeNamesJoined(argumentTypes));
            throw new RuntimeException(msg, e);
        }
    }

    private static String argumentTypeNamesJoined(Class<?>[] argumentTypes) {
        return Arrays.stream(argumentTypes)
                .map(Class::getName)
                .collect(Collectors.joining(", "));
    }

    static boolean hasConstructor(Class<?> type, Class<?>... argumentTypes) {
        final Predicate<Constructor<?>> predicate =
                ctor -> Arrays.equals(ctor.getParameterTypes(), argumentTypes);
        return Arrays.stream(type.getDeclaredConstructors()).anyMatch(predicate);
    }

    static <T> T callNoParamConstructor(Class<T> cls) {
        try {
            Constructor<T> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            String msg = "Type '%s' doesn't have a no-args constructor."
                    .formatted(cls.getSimpleName());
            throw new RuntimeException(msg, e);
        } catch (IllegalAccessException e) {
            /* This exception should not be thrown because
             * we set the constructor to be accessible. */
            String msg = "No-args constructor of type '%s' not accessible."
                    .formatted(cls.getSimpleName());
            throw new RuntimeException(msg, e);
        } catch (InstantiationException e) {
            String msg = "Type '%s' is not instantiable.".formatted(cls.getSimpleName());
            throw new RuntimeException(msg, e);
        } catch (InvocationTargetException e) {
            String msg = "No-args constructor of type '%s' threw an exception."
                    .formatted(cls.getSimpleName());
            throw new RuntimeException(msg, e);
        }
    }

    // We could use <R extends Record> as a bound here and for the other methods below
    // but that would require casts elsewhere.
    static <R> R callCanonicalConstructor(Class<R> recordType, Object... constructorArguments) {
        try {
            Constructor<R> constructor = getCanonicalConstructor(requireRecord(recordType));
            constructor.setAccessible(true);
            return constructor.newInstance(constructorArguments);
        } catch (NoSuchMethodException e) {
            // cannot happen because we select the constructor based on the component types
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // cannot happen because we set the constructor to be accessible.
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            // cannot happen because records are instantiable
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            String msg = "The canonical constructor of record type '%s' threw an exception."
                    .formatted(recordType.getSimpleName());
            throw new RuntimeException(msg, e);
        }
    }

    static <R> R callCanonicalConstructorWithDefaultValues(Class<R> recordType) {
        final Object[] args = Arrays.stream(recordParameterTypes(requireRecord(recordType)))
                .map(Reflect::getDefaultValue)
                .toArray(Object[]::new);
        return Reflect.callCanonicalConstructor(recordType, args);
    }

    static <R> Constructor<R> getCanonicalConstructor(Class<R> recordType)
            throws NoSuchMethodException {
        Class<?>[] parameterTypes = recordParameterTypes(requireRecord(recordType));
        return recordType.getDeclaredConstructor(parameterTypes);
    }

    private static <R> Class<?>[] recordParameterTypes(Class<R> recordType) {
        return Arrays.stream(recordType.getRecordComponents())
                .map(RecordComponent::getType)
                .toArray(Class<?>[]::new);
    }

    static <T> T[] newArray(Class<T> componentType, int length) {
        // The following cast won't fail because we just created an array of that type
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(componentType, length);
        return array;
    }

    static boolean hasDefaultConstructor(Class<?> type) {
        return Arrays.stream(type.getDeclaredConstructors())
                .anyMatch(ctor -> ctor.getParameterCount() == 0);
    }

    static Object getValue(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            /* This exception should not be thrown because we set the field to be accessible. */
            String msg = "Illegal access of field '%s' on object %s.".formatted(field, instance);
            throw new RuntimeException(msg, e);
        }
    }

    static Object getValue(RecordComponent component, Object recordInstance) {
        final Method accessor = component.getAccessor();
        try {
            accessor.setAccessible(true);
            return accessor.invoke(recordInstance);
        } catch (IllegalAccessException e) {
            /* Should not be thrown because we set the method to be accessible. */
            String msg = "Illegal access of method '%s' on record '%s'."
                    .formatted(accessor, recordInstance);
            throw new RuntimeException(msg, e);
        } catch (InvocationTargetException e) {
            String msg = "Invocation of method '%s' on record '%s' failed."
                    .formatted(accessor, recordInstance);
            throw new RuntimeException(msg, e);
        }
    }

    static void setValue(Field field, Object instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            /* This exception should not be thrown because we set the field to be accessible. */
            String msg = "Illegal access of field '%s' on object %s.".formatted(field, instance);
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
