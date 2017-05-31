package de.exlll.configlib;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.*;

import static java.util.stream.Collectors.toMap;

final class Comments {
    private final List<String> classComments;
    private final Map<String, List<String>> fieldComments;

    Comments(Class<?> cls) {
        this.classComments = getComments(cls);
        this.fieldComments = getFieldComments(cls);
    }

    private List<String> getComments(AnnotatedElement element) {
        Comment comment = element.getAnnotation(Comment.class);
        return (comment != null) ? Arrays.asList(comment.value()) :
                Collections.emptyList();
    }

    private Map<String, List<String>> getFieldComments(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredFields())
                .filter(this::hasCommentAnnotation)
                .collect(toMap(Field::getName, this::getComments));
    }

    private boolean hasCommentAnnotation(AnnotatedElement element) {
        return element.isAnnotationPresent(Comment.class);
    }

    public List<String> getClassComments() {
        return classComments;
    }

    public Map<String, List<String>> getFieldComments() {
        return fieldComments;
    }
}
