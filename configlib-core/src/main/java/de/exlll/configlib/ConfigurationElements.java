package de.exlll.configlib;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;

final class ConfigurationElements {
    private ConfigurationElements() {}

    record FieldElement(Field element) implements ConfigurationElement<Field> {
        @Override
        public String name() {
            return element.getName();
        }

        @Override
        public Class<?> type() {
            return element.getType();
        }

        @Override
        public AnnotatedType annotatedType() {
            return element.getAnnotatedType();
        }

        @Override
        public Object value(Object elementHolder) {
            return Reflect.getValue(element, elementHolder);
        }

        @Override
        public Class<?> declaringType() {
            return element.getDeclaringClass();
        }
    }

    record RecordComponentElement(RecordComponent element)
            implements ConfigurationElement<RecordComponent> {
        @Override
        public String name() {
            return element.getName();
        }

        @Override
        public Class<?> type() {
            return element.getType();
        }

        @Override
        public AnnotatedType annotatedType() {
            return element.getAnnotatedType();
        }

        @Override
        public Object value(Object elementHolder) {
            return Reflect.getValue(element, elementHolder);
        }

        @Override
        public Class<?> declaringType() {
            return element.getDeclaringRecord();
        }
    }
}
