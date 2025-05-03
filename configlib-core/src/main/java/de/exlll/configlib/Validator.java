package de.exlll.configlib;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

final class Validator {
    private Validator() {}

    static <T> T requireNonNull(T object, String argumentName) {
        if (object == null) {
            String msg = "The " + argumentName + " must not be null.";
            throw new NullPointerException(msg);
        }
        return object;
    }

    static <T> T requireNonNullArrayElement(T element, String type, int index) {
        if (element == null) {
            String msg = "The " + type + " element at index " + index + " must not be null.";
            throw new ConfigurationException(msg);
        }
        return element;
    }

    static <T> T[] requireNonEmpty(T[] array, String argumentName) {
        if (array.length == 0) {
            String msg = "The " + argumentName + " must not be empty.";
            throw new IllegalArgumentException(msg);
        }
        return array;
    }

    static <T extends Collection<?>> T requireNonEmpty(T collection, String argumentName) {
        if (collection.isEmpty()) {
            String msg = "The " + argumentName + " must not be empty.";
            throw new IllegalArgumentException(msg);
        }
        return collection;
    }

    static <T> Class<T> requireConfigurationClass(Class<T> cls) {
        requireNonNull(cls, "type");
        if (!Reflect.isConfigurationClass(cls)) {
            String msg = "Class '" + cls.getSimpleName() + "' must be a configuration.";
            throw new ConfigurationException(msg);
        }
        return cls;
    }

    static <T> Class<T> requireRecord(Class<T> cls) {
        requireNonNull(cls, "type");
        if (!cls.isRecord()) {
            String msg = "Class '" + cls.getSimpleName() + "' must be a record.";
            throw new ConfigurationException(msg);
        }
        return cls;
    }

    static <T> Class<T> requireConfigurationType(Class<T> type) {
        requireNonNull(type, "type");
        if (!Reflect.isConfigurationType(type)) {
            String msg = "Class '" + type.getSimpleName() + "' must be a configuration or record.";
            throw new ConfigurationException(msg);
        }
        return type;
    }

    static void requirePrimitiveOrWrapperNumberType(Class<?> cls) {
        if (!Reflect.isIntegerType(cls) && !Reflect.isFloatingPointType(cls)) {
            String msg = "Class " + cls.getSimpleName() + " is not a byte, short, int, long, " +
                         "float, double, or a wrapper type of one of the primitive number types.";
            throw new IllegalArgumentException(msg);
        }
    }

    static Object requireListOrMap(Object object, String argumentName) {
        requireNonNull(object, argumentName);
        if (object instanceof Map<?, ?> || object instanceof List<?>) return object;
        String msg = "The " + argumentName + " must be a list or map but the given " +
                     "object '" + object + "' is of type " + object.getClass().getName() + ".";
        throw new IllegalArgumentException(msg);
    }

    static void requireTargetTypeArg(Object object) {
        requireTargetType(object, IllegalArgumentException::new);
    }

    static void requireTargetTypeRet(Object object) {
        requireTargetType(object, ConfigurationException::new);
    }

    private static void requireTargetType(
            Object object,
            Function<String, RuntimeException> f
    ) {
        if (object == null) return;

        final Class<?> cls = object.getClass();
        if (cls == Boolean.class ||
            cls == Long.class ||
            cls == Double.class ||
            cls == String.class) {
            return;
        }

        if (object instanceof List<?> list) {
            list.forEach((e) -> requireTargetType(e, f));
            return;
        }

        if (object instanceof Map<?, ?> map) {
            map.keySet().forEach((k) -> requireTargetType(k, f));
            map.values().forEach((v) -> requireTargetType(v, f));
            return;
        }

        final String msg =
                "Value '" + object + "' must be null or of a valid target type " +
                "but its type is " + object.getClass().getName() + ".";
        throw f.apply(msg);
    }
}