package de.exlll.configlib;

import java.util.Objects;

enum TypeConverter {
    ;

    static Object convertValue(Class<?> target, Object value) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(value);

        if (target == value.getClass()) {
            return value;
        }
        if (value instanceof Number) {
            return convertNumber(target, (Number) value);
        }
        if (value instanceof String) {
            return convertString((String) value);
        }
        return value;
    }

    static Character convertString(String s) {
        int len = s.length();
        if (len != 1) {
            String msg = "String '" + s + "' cannot be converted to a character." +
                    " Length of s: " + len + " Required length: 1";
            throw new IllegalArgumentException(msg);
        }
        return s.charAt(0);
    }

    static Number convertNumber(Class<?> targetType, Number number) {
        if (isByteClass(targetType)) {
            return number.byteValue();
        } else if (isShortClass(targetType)) {
            return number.shortValue();
        } else if (isIntegerClass(targetType)) {
            return number.intValue();
        } else if (isLongClass(targetType)) {
            return number.longValue();
        } else if (isFloatClass(targetType)) {
            return number.floatValue();
        } else if (isDoubleClass(targetType)) {
            return number.doubleValue();
        } else {
            String msg = "Number cannot be converted to target type " +
                    "'" + targetType + "'";
            throw new IllegalArgumentException(msg);
        }
    }

    static boolean isBooleanClass(Class<?> cls) {
        return (cls == Boolean.class) || (cls == Boolean.TYPE);
    }

    static boolean isByteClass(Class<?> cls) {
        return (cls == Byte.class) || (cls == Byte.TYPE);
    }

    static boolean isShortClass(Class<?> cls) {
        return (cls == Short.class) || (cls == Short.TYPE);
    }

    static boolean isIntegerClass(Class<?> cls) {
        return (cls == Integer.class) || (cls == Integer.TYPE);
    }

    static boolean isLongClass(Class<?> cls) {
        return (cls == Long.class) || (cls == Long.TYPE);
    }

    static boolean isFloatClass(Class<?> cls) {
        return (cls == Float.class) || (cls == Float.TYPE);
    }

    static boolean isDoubleClass(Class<?> cls) {
        return (cls == Double.class) || (cls == Double.TYPE);
    }

    static boolean isCharacterClass(Class<?> cls) {
        return (cls == Character.class) || (cls == Character.TYPE);
    }
}
