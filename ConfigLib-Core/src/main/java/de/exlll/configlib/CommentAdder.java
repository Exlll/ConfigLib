package de.exlll.configlib;

import java.util.Collections;
import java.util.List;
import java.util.Map;

final class CommentAdder {
    private final Comments comments;
    private StringBuilder builder;

    CommentAdder(Comments comments) {
        this.comments = comments;
    }

    String addComments(String serializedMap) {
        builder = new StringBuilder();
        addClassComments();
        addMap(serializedMap);
        return builder.toString();
    }

    private void addClassComments() {
        List<String> clsComments = comments.getClassComments();
        if (!clsComments.isEmpty()) {
            addComments(clsComments);
            builder.append('\n');
        }
    }

    private void addComments(List<String> comments) {
        for (String comment : comments) {
            if (!comment.trim().isEmpty()) {
                builder.append("# ");
                builder.append(comment);
            }
            builder.append('\n');
        }
    }

    private void addMap(String map) {
        String[] lines = map.split("\n");
        for (String line : lines) {
            addLine(line);
        }
    }

    private void addLine(String line) {
        addLineComments(line);
        builder.append(line);
        builder.append('\n');
    }

    private void addLineComments(String line) {
        if (!line.contains(":")) {
            return;
        }
        List<String> comments = getLineComments(line);
        addComments(comments);
    }

    private List<String> getLineComments(String line) {
        Map<String, List<String>> entries = comments.getFieldComments();
        for (Map.Entry<String, List<String>> entry : entries.entrySet()) {
            String s = entry.getKey() + ":";
            if (line.startsWith(s)) {
                return entry.getValue();
            }
        }
        return Collections.emptyList();
    }
}
