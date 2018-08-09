package de.exlll.configlib;

import de.exlll.configlib.Converter.ConversionInfo;
import de.exlll.configlib.format.FieldNameFormatter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static de.exlll.configlib.Validator.*;
import static java.util.stream.Collectors.toList;

enum FieldMapper {
    ;

    static Map<String, Object> instanceToMap(
            Object inst, Configuration.Properties props
    ) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Field field : FieldFilter.filterFields(inst.getClass())) {
            Object val = toConvertibleObject(field, inst, props);
            FieldNameFormatter fnf = props.getFormatter();
            String fn = fnf.fromFieldName(field.getName());
            map.put(fn, val);
        }
        return map;
    }

    private static Object toConvertibleObject(
            Field field, Object instance, Configuration.Properties props
    ) {
        checkDefaultValueNull(field, instance);
        ConversionInfo info = ConversionInfo.of(field, instance, props);
        checkFieldWithElementTypeIsContainer(info);
        Object converted = Converters.convertTo(info);
        checkConverterNotReturnsNull(converted, info);
        return converted;
    }

    static void instanceFromMap(
            Object inst, Map<String, Object> instMap,
            Configuration.Properties props
    ) {
        for (Field field : FieldFilter.filterFields(inst.getClass())) {
            FieldNameFormatter fnf = props.getFormatter();
            String fn = fnf.fromFieldName(field.getName());
            Object mapValue = instMap.get(fn);
            if (mapValue != null) {
                fromConvertedObject(field, inst, mapValue, props);
            }
        }
    }

    private static void fromConvertedObject(
            Field field, Object instance, Object mapValue,
            Configuration.Properties props
    ) {
        checkDefaultValueNull(field, instance);
        ConversionInfo info = ConversionInfo.of(field, instance, mapValue, props);
        checkFieldWithElementTypeIsContainer(info);
        Object convert = Converters.convertFrom(info);

        if (convert == null) {
            return;
        }

        if (Reflect.isContainerType(info.getFieldType())) {
            checkFieldTypeAssignableFrom(convert.getClass(), info);
        }

        Reflect.setValue(field, instance, convert);
    }

    private static void checkDefaultValueNull(Field field, Object instance) {
        Object val = Reflect.getValue(field, instance);
        checkNotNull(val, field.getName());
    }

    enum FieldFilter implements Predicate<Field> {
        DEFAULT;

        static List<Field> filterFields(Class<?> cls) {
            Field[] fields = cls.getDeclaredFields();
            return Arrays.stream(fields)
                    .filter(DEFAULT)
                    .collect(toList());
        }

        @Override
        public boolean test(Field field) {
            if (field.isSynthetic()) {
                return false;
            }

            int mods = field.getModifiers();
            return !(Modifier.isFinal(mods) ||
                    Modifier.isStatic(mods) ||
                    Modifier.isTransient(mods));
        }
    }
}
