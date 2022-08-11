package de.exlll.configlib;

import java.lang.reflect.AnnotatedType;

import static de.exlll.configlib.Validator.requireNonNull;

record SerializerContextImpl(
        ConfigurationProperties properties,
        ConfigurationElement<?> element,
        AnnotatedType annotatedType
) implements SerializerContext {
    SerializerContextImpl {
        properties = requireNonNull(properties, "configuration properties");
        element = requireNonNull(element, "configuration element");
        annotatedType = requireNonNull(annotatedType, "annotated type");
    }
}

