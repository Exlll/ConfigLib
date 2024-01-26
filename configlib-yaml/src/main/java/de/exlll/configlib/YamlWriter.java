package de.exlll.configlib;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import static de.exlll.configlib.Validator.requireNonNull;

/**
 * A writer that writes YAML to a file.
 */
final class YamlWriter {
    private final OutputStream outputStream;
    private final YamlConfigurationProperties properties;
    private BufferedWriter writer;

    YamlWriter(OutputStream outputStream, YamlConfigurationProperties properties) {
        this.outputStream = requireNonNull(outputStream, "output stream");
        this.properties = requireNonNull(properties, "configuration properties");
    }

    YamlWriter(Path configurationFile, YamlConfigurationProperties properties) {
        requireNonNull(configurationFile, "configuration file");
        try {
            this.outputStream = Files.newOutputStream(configurationFile);
            this.properties = properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeYaml(String yaml, Queue<CommentNode> nodes) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(outputStream, properties.getCharset()))) {
            this.writer = writer;
            writeHeader();
            writeContent(yaml, nodes);
            writeFooter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.writer = null;
        }
    }

    private void writeHeader() throws IOException {
        if (properties.getHeader() != null) {
            writeAsComment(properties.getHeader());
            writer.newLine();
        }
    }

    private void writeFooter() throws IOException {
        if (properties.getFooter() != null) {
            writer.newLine();
            writeAsComment(properties.getFooter());
        }
    }

    private void writeAsComment(String comment) throws IOException {
        String[] lines = comment.split("\n");
        writeComments(Arrays.asList(lines), 0);
    }

    private void writeComments(List<String> comments, int indentLevel) throws IOException {
        String indent = "  ".repeat(indentLevel);
        for (String comment : comments) {
            if (comment.isEmpty()) {
                writer.newLine();
                continue;
            }
            String line = indent + "# " + comment;
            writeLine(line);
        }
    }

    private void writeLine(String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }

    private void writeContent(String yaml, Queue<CommentNode> nodes) throws IOException {
        if (nodes.isEmpty()) {
            writer.write(yaml);
        } else {
            writeCommentedYaml(yaml, nodes);
        }
    }

    private void writeCommentedYaml(String yaml, Queue<CommentNode> nodes)
            throws IOException {
        /*
         * The following algorithm is necessary since no Java YAML library seems
         * to properly support comments, at least not the way I want them.
         *
         * The algorithm writes YAML line by line and keeps track of the current
         * context with the help of elementNames lists which come from the nodes in
         * the 'nodes' queue. The 'nodes' queue contains nodes in the order in
         * which fields and records components were extracted, which happened in
         * DFS manner and with fields of a parent class being read before the fields
         * of a child. That order ultimately represents the order in which the
         * YAML file is structured.
         */
        var node = nodes.poll();
        var currentIndentLevel = 0;

        for (final String line : yaml.split("\n")) {
            if (node == null) {
                writeLine(line);
                continue;
            }

            final var elementNames = node.elementNames();
            final var indent = "  ".repeat(currentIndentLevel);

            final var lineStart = indent + elementNames.get(currentIndentLevel) + ":";
            if (!line.startsWith(lineStart)) {
                writeLine(line);
                continue;
            }

            final var commentIndentLevel = elementNames.size() - 1;
            if (currentIndentLevel++ == commentIndentLevel) {
                writeComments(node.comments(), commentIndentLevel);
                if ((node = nodes.poll()) != null) {
                    currentIndentLevel = lengthCommonPrefix(node.elementNames(), elementNames);
                }
            }

            writeLine(line);
        }
    }

    static int lengthCommonPrefix(List<String> l1, List<String> l2) {
        final int maxLen = Math.min(l1.size(), l2.size());
        int result = 0;
        for (int i = 0; i < maxLen; i++) {
            String s1 = l1.get(i);
            String s2 = l2.get(i);
            if (s1.equals(s2))
                result++;
            else return result;
        }
        return result;
    }
}
