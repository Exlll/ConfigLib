package de.exlll.configlib;

import java.lang.reflect.AnnotatedType;

import static de.exlll.configlib.Validator.requireNonNull;

record SerializerContextImpl(
        ConfigurationProperties properties,
        TypeComponent<?> component,
        AnnotatedType annotatedType
) implements SerializerContext {
    SerializerContextImpl {
        properties = requireNonNull(properties, "configuration properties");
        component = requireNonNull(component, "type component");
        annotatedType = requireNonNull(annotatedType, "annotated type");
    }
}

