package de.exlll.configlib;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.*;

import static java.util.stream.Collectors.toMap;

final class Comments {
    private final List<String> classComments;
    private final Map<String, List<String>> commentsByFieldNames;

    Comments(List<String> classComments,
             Map<String, List<String>> commentsByFieldName) {
        this.classComments = classComments;
        this.commentsByFieldNames = commentsByFieldName;
    }

    static Comments from(FilteredFieldStreamSupplier supplier) {
        Objects.requireNonNull(supplier);

        List<String> classComments = getComments(supplier.getSupplyingClass());
        Map<String, List<String>> commentsByFieldNames = supplier
                .get()
                .filter(Comments::hasCommentAnnotation)
                .collect(toMap(Field::getName, Comments::getComments));
        return new Comments(classComments, commentsByFieldNames);
    }

    static List<String> getComments(AnnotatedElement element) {
        Comment comment = element.getAnnotation(Comment.class);
        return (comment != null) ?
                Arrays.asList(comment.value()) :
                Collections.emptyList();
    }

    static boolean hasCommentAnnotation(AnnotatedElement element) {
        return element.isAnnotationPresent(Comment.class);
    }

    List<String> getClassComments() {
        return classComments;
    }

    Map<String, List<String>> getCommentsByFieldNames() {
        return commentsByFieldNames;
    }
}
