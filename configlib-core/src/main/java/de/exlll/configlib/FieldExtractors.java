package de.exlll.configlib;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

enum FieldExtractors implements FieldExtractor {
    /**
     * Extracts the declared fields of the given configuration and all its super classes up to the
     * first class that is not a configuration.
     * <p>
     * The order of the fields is reversed such that the field of super classes are listed first.
     */
    CONFIGURATION {
        @Override
        public Stream<Field> extract(Class<?> cls) {
            Validator.requireNonNull(cls, "configuration class");
            Validator.requireConfiguration(cls);

            List<Class<?>> classes = extractClassesWhile(cls, Reflect::isConfiguration);
            List<Field> fields = classes.stream()
                    .flatMap(c -> Arrays.stream(c.getDeclaredFields()))
                    .filter(FieldFilters.DEFAULT)
                    .toList();
            requireNoShadowing(fields);
            return fields.stream();
        }
    };

    private static void requireNoShadowing(List<Field> fields) {
        Map<String, Class<?>> map = new LinkedHashMap<>();

        for (Field field : fields) {
            var fieldName = field.getName();
            var fieldClass = field.getDeclaringClass();

            if (map.containsKey(fieldName)) {
                Class<?> superClass = map.get(fieldName);
                String msg = "Shadowing of fields is not supported. Field '" + fieldName + "' " +
                             "of class " + fieldClass.getSimpleName() + " shadows field '" +
                             fieldName + "' of class " + superClass.getSimpleName() + ".";
                throw new ConfigurationException(msg);
            }
            map.put(fieldName, fieldClass);
        }
    }

    private static List<Class<?>> extractClassesWhile(Class<?> cls, Predicate<Class<?>> condition) {
        List<Class<?>> classes = new ArrayList<>();
        Class<?> current = cls;
        while (condition.test(current)) {
            classes.add(current);
            current = current.getSuperclass();
        }
        Collections.reverse(classes); // we want the fields of the super classes to come first
        return classes;
    }
}
