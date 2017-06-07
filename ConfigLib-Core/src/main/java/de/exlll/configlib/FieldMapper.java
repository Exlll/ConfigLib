package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

enum FieldMapper {
    ;

    static Map<String, Object> instanceToMap(Object instance) {
        Map<String, Object> map = new LinkedHashMap<>();

        FilteredFields ff = FilteredFields.of(instance.getClass());

        for (Field field : ff) {
            Object value = Reflect.getValue(field, instance);
            checkNull(field, value);
            value = toSerializableObject(value);
            map.put(field.getName(), value);
        }

        return map;
    }

    static Object toSerializableObject(Object input) {
        if (input instanceof Defaultable<?>) {
            return ((Defaultable<?>) input).toDefault();
        } else if (Reflect.isDefault(input.getClass())) {
            return input;
        } else {
            return instanceToMap(input);
        }
    }

    static void instanceFromMap(Object instance, Map<String, ?> map) {
        FilteredFields ff = FilteredFields.of(instance.getClass());

        for (Field field : ff) {
            Object value = map.get(field.getName());
            fromSerializedObject(field, instance, value);
        }
    }

    static void fromSerializedObject(Field field, Object instance, Object serialized) {
        if (serialized == null) {
            return; // keep default value
        }
        Object fieldValue = Reflect.getValue(field, instance);
        checkNull(field, fieldValue);
        if (fieldValue instanceof Defaultable<?>) {
            ((Defaultable<?>) fieldValue).fromDefault(serialized);
        } else if (Reflect.isDefault(field.getType())) {
            Reflect.setValue(field, instance, serialized);
        } else {
            instanceFromMap(fieldValue, castToMap(serialized));
        }
    }

    private static void checkNull(Field field, Object o) {
        if (o == null) {
            String msg = "The value of field " + field.getName() + " is null.\n" +
                    "Please assign a non-null default value or remove this field.";
            throw new NullPointerException(msg);
        }
    }


    private static Map<String, Object> castToMap(Object mapObject) {
        Reflect.checkType(mapObject, Map.class);
        Reflect.checkMapEntries((Map<?, ?>) mapObject, String.class, Object.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> m = (Map<String, Object>) mapObject;
        return m;
    }
}
