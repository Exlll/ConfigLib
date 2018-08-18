package de.exlll.configlib.configs.yaml;

import de.exlll.configlib.Comments;
import de.exlll.configlib.format.FieldNameFormatter;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

final class YamlComments {
    private final Comments comments;

    YamlComments(Comments comments) {
        this.comments = comments;
    }

    String classCommentsAsString() {
        List<String> classComments = comments.getClassComments();
        return commentListToString(classComments);
    }

    Map<String, String> fieldCommentAsStrings(FieldNameFormatter formatter) {
        Map<String, List<String>> fieldComments = comments.getFieldComments();
        return fieldComments.entrySet().stream()
                .map(e -> toFormattedStringCommentEntry(e, formatter))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<String, String> toFormattedStringCommentEntry(
            Map.Entry<String, List<String>> entry, FieldNameFormatter formatter
    ) {
        String fieldComments = commentListToString(entry.getValue());
        String formattedKey = formatter.fromFieldName(entry.getKey());
        return new MapEntry<>(formattedKey, fieldComments);
    }

    private String commentListToString(List<String> comments) {
        return comments.stream()
                .map(this::toCommentLine)
                .collect(joining("\n"));
    }

    private String toCommentLine(String comment) {
        return comment.isEmpty() ? "" : "# " + comment;
    }

    private static final class MapEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

}
