package de.exlll.configlib;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;

import static de.exlll.configlib.Validator.requireNonNull;

/**
 * Represents a component of a serializable type which can either be a {@link Field} for
 * configurations or a {@link RecordComponent} for records.
 *
 * @param <T> the type of the component
 */
sealed interface TypeComponent<T extends AnnotatedElement> {
    /**
     * Returns the component itself.
     *
     * @return the component
     */
    T component();

    /**
     * Returns the name of the component.
     *
     * @return name of the component
     */
    String componentName();

    /**
     * Returns the type of the component.
     *
     * @return type of the component
     */
    Class<?> componentType();

    /**
     * Returns the generic type of the component.
     *
     * @return generic type of the component
     */
    Type componentGenericType();

    /**
     * Returns the value the component is holding.
     *
     * @param componentHolder the holder to which this component belongs
     * @return value the component is holding
     * @throws IllegalArgumentException if {@code componentHolder} is not an instance of the type to
     *                                  which this component belongs
     */
    Object componentValue(Object componentHolder);

    /**
     * Returns the type that declares this component.
     *
     * @return the declaring type
     */
    Class<?> declaringType();

    /**
     * Returns the annotation of the given type or null if the component is not annotated
     * with such an annotation.
     *
     * @param annotationType the type of annotation
     * @param <A>            the type of annotation
     * @return the annotation or null
     * @throws NullPointerException if {@code annotationType} is null
     */
    default <A extends Annotation> A annotation(Class<A> annotationType) {
        return component().getAnnotation(annotationType);
    }

    record ConfigurationField(Field component) implements TypeComponent<Field> {
        public ConfigurationField(Field component) {
            this.component = requireNonNull(component, "component");
        }

        @Override
        public String componentName() {
            return component.getName();
        }

        @Override
        public Class<?> componentType() {
            return component.getType();
        }

        @Override
        public Type componentGenericType() {
            return component.getGenericType();
        }

        @Override
        public Object componentValue(Object componentHolder) {
            return Reflect.getValue(component, componentHolder);
        }

        @Override
        public Class<?> declaringType() {
            return component.getDeclaringClass();
        }
    }

    record ConfigurationRecordComponent(RecordComponent component)
            implements TypeComponent<RecordComponent> {
        public ConfigurationRecordComponent(RecordComponent component) {
            this.component = requireNonNull(component, "component");
        }

        @Override
        public String componentName() {
            return component.getName();
        }

        @Override
        public Class<?> componentType() {
            return component.getType();
        }

        @Override
        public Type componentGenericType() {
            return component.getGenericType();
        }

        @Override
        public Object componentValue(Object componentHolder) {
            return Reflect.getValue(component, componentHolder);
        }

        @Override
        public Class<?> declaringType() {
            return component.getDeclaringRecord();
        }
    }
}
