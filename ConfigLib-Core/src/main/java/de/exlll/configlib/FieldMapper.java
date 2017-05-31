package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum FieldMapper {
    ;
    private static final Set<Class<?>> defaultClasses = Stream.of(
            Boolean.class, String.class, Character.class,
            Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class
    ).collect(Collectors.toSet());

    static Map<String, Object> instanceToMap(Object instance) {
        Map<String, Object> map = new LinkedHashMap<>();

        FilteredFields ff = FilteredFields.of(instance.getClass());

        for (Field field : ff) {
            Object value = getValue(field, instance);
            checkNull(field, value);
            value = isDefault(field.getType()) ? value : instanceToMap(value);
            map.put(field.getName(), value);
        }

        return map;
    }

    static void instanceFromMap(Object instance, Map<String, ?> map) {
        FilteredFields ff = FilteredFields.of(instance.getClass());

        for (Field field : ff) {
            Object val = map.get(field.getName());
            if (val == null) {
                continue; // keep default value
            }
            if (isDefault(field.getType())) {
                setValue(field, instance, val);
            } else {
                Object inst = getValue(field, instance);
                instanceFromMap(inst, castToMap(val));
            }
        }
    }

    private static void checkNull(Field field, Object o) {
        if (o == null) {
            String msg = "The value of field " + field.getName() + " is null.\n" +
                    "Please assign a non-null default value or remove this field.";
            throw new NullPointerException(msg);
        }
    }


    private static Map<String, Object> castToMap(Object map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> m = (Map<String, Object>) map;
        return m;
    }

    private static boolean isDefault(Class<?> cls) {
        // all default types are properly handled by the YamlSerializer
        return cls.isPrimitive() || isDefaultClass(cls) || isDefaultType(cls);
    }

    private static boolean isDefaultClass(Class<?> cls) {
        return defaultClasses.contains(cls);
    }

    private static boolean isDefaultType(Class<?> cls) {
        return Map.class.isAssignableFrom(cls) ||
                Set.class.isAssignableFrom(cls) ||
                List.class.isAssignableFrom(cls);
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
}
