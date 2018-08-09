package de.exlll.configlib.format;

import java.util.function.Function;

@FunctionalInterface
public interface FieldNameFormatter extends Function<String, String> {
    String fromFieldName(String fieldName);

    @Override
    default String apply(String s) {
        return fromFieldName(s);
    }
}
