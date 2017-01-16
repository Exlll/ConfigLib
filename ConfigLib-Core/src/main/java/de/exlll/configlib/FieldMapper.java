package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

final class FieldMapper {
    private final FilteredFieldStreamSupplier streamSupplier;

    FieldMapper(FilteredFieldStreamSupplier streamSupplier) {
        Objects.requireNonNull(streamSupplier);
        this.streamSupplier = streamSupplier;
    }

    Map<String, Object> mapFieldNamesToValues(Object instance) {
        Map<String, Object> valuesByFieldNames = new LinkedHashMap<>();
        List<Field> fields = streamSupplier.get().collect(Collectors.toList());

        for (Field field : fields) {
            Object value = getValue(field, instance);
            valuesByFieldNames.put(field.getName(), value);
        }

        return valuesByFieldNames;
    }

    private Object getValue(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            /* cannot happen */
            throw new AssertionError(e);
        }
    }

    void mapValuesToFields(Map<String, Object> valuesByFieldNames, Object instance) {
        List<Field> fields = streamSupplier.get().collect(Collectors.toList());

        for (Field field : fields) {
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
