package de.exlll.configlib;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;

/**
 * Represents an element of a serializable configuration type. The element can either be a
 * {@link Field} for configuration classes or a {@link RecordComponent} for records.
 *
 * @param <T> the type of the element
 */
public interface ConfigurationElement<T extends AnnotatedElement>
{
    /**
     * Returns the element itself.
     *
     * @return the element
     */
    T element();

    /**
     * Returns the name of the element.
     *
     * @return name of the element
     */
    String name();

    /**
     * Returns the type of the element.
     *
     * @return type of the element
     */
    Class<?> type();

    /**
     * Returns the annotated type of the element.
     *
     * @return annotated type of element
     */
    AnnotatedType annotatedType();

    /**
     * Given an instance of the configuration type which defines this element, returns the value
     * the element is holding.
     *
     * @param elementHolder an instance of the configuration type that defines this element
     * @return value the element is holding
     * @throws IllegalArgumentException if {@code elementHolder} is not an instance of the
     *                                  configuration type which defines this element
     */
    Object value(Object elementHolder);

    /**
     * Returns the configuration type that defines this element.
     *
     * @return the configuration type that defines this element
     */
    Class<?> declaringType();

    /**
     * Returns the annotation of the given type or null if the element is not annotated
     * with such an annotation.
     *
     * @param annotationType the type of annotation
     * @param <A>            the type of annotation
     * @return the annotation or null
     * @throws NullPointerException if {@code annotationType} is null
     */
    default <A extends Annotation> A annotation(Class<A> annotationType) {
        return element().getAnnotation(annotationType);
    }
}
