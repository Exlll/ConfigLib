package de.exlll.configlib;

import java.util.Objects;

final class Validator {
    private Validator() {}

    static <T> T requireNonNull(T object, String argumentName) {
        String msg = "The " + argumentName + " must not be null.";
        return Objects.requireNonNull(object, msg);
    }

    static <T> T requireNonNullArrayElement(T element, String type, int index) {
        if (element == null) {
            String msg = "The " + type + " element at index " + index + " must not be null.";
            throw new ConfigurationException(msg);
        }
        return element;
    }

    static <T> Class<T> requireConfiguration(Class<T> cls) {
        if (!Reflect.isConfiguration(cls)) {
            String msg = "Class '" + cls.getSimpleName() + "' must be a configuration.";
            throw new ConfigurationException(msg);
        }
        return cls;
    }

    static <T> Class<T> requireRecord(Class<T> cls) {
        if (!cls.isRecord()) {
            String msg = "Class '" + cls.getSimpleName() + "' must be a record.";
            throw new ConfigurationException(msg);
        }
        return cls;
    }

    static void requirePrimitiveOrWrapperNumberType(Class<?> cls) {
        if (!Reflect.isIntegerType(cls) && !Reflect.isFloatingPointType(cls)) {
            String msg = "Class " + cls.getSimpleName() + " is not a byte, short, int, long, " +
                         "float, double, or a wrapper type of one of the primitive number types.";
            throw new IllegalArgumentException(msg);
        }
    }
}