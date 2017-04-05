package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

final class FieldMapper {
    private final FilteredFieldStreamSupplier streamSupplier;

    FieldMapper(FilteredFieldStreamSupplier streamSupplier) {
        Objects.requireNonNull(streamSupplier);
        this.streamSupplier = streamSupplier;
    }

    Map<String, Object> mapFieldNamesToValues(Object instance) {
        return streamSupplier.get().collect(
                toMap(Field::getName,
                        field -> getValue(field, instance),
                        (f1, f2) -> f1,
                        LinkedHashMap::new));
    }

    private Object getValue(Field field, Object instance) {
        try {
            field.setAccessible(true);
            Object value = field.get(instance);
            checkNull(field, value);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            /* cannot happen */
            throw new AssertionError(e);
        }
    }

    private void checkNull(Field field, Object o) {
        if (o == null) {
            String msg = String.format("The value of field %s is null.\n" +
                    "Please assign a non-null default value or remove this field.", field);
            throw new NullPointerException(msg);
        }
    }

    void mapValuesToFields(Map<String, Object> valuesByFieldNames, Object instance) {
        for (Field field : streamSupplier.toList()) {
            String fieldName = field.getName();
            if (valuesByFieldNames.containsKey(fieldName)) {
                Object value = valuesByFieldNames.get(fieldName);
                setField(field, instance, value);
            }
        }
    }

    private void setField(Field field, Object instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            /* This exception is only thrown when the field is "static final".
             * Since this library filters "static final" fields out, the
             * exception is never thrown in production. */
            throw new RuntimeException(e);
        }
    }
}
