package de.exlll.configlib;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class ConfigurationWriter {
    private final Path configPath;
    private final Comments comments;
    private Writer writer;

    ConfigurationWriter(Path configPath, Comments comments) {
        this.configPath = configPath;
        this.comments = comments;
    }

    void write(String dump) throws IOException {
        writer = Files.newBufferedWriter(configPath);

        writeClassComments();
        writeDump(dump);

        writer.close();
    }

    private void writeClassComments() throws IOException {
        List<String> classComments = comments.getClassComments();
        if (!classComments.isEmpty()) {
            writeComments(classComments);
            writer.write('\n');
        }
    }

    private void writeComments(List<String> comments) throws IOException {
        for (String comment : comments) {
            writer.write("# ");
            writer.write(comment);
            writer.write('\n');
        }
    }

    private void writeDump(String dump) throws IOException {
        String[] dumpLines = dump.split("\n");
        for (String dumpLine : dumpLines) {
            writeLine(dumpLine);
        }
    }

    private void writeLine(String line) throws IOException {
        writeFieldComment(line);
        writer.write(line);
        writer.write('\n');
    }

    private void writeFieldComment(String line) throws IOException {
        if (!line.contains(":")) {
            return;
        }
        Optional<List<String>> cmts = getFieldComments(line);
        if (cmts.isPresent()) {
            writeComments(cmts.get());
        }
    }

    private Optional<List<String>> getFieldComments(String line) {
        return comments.getCommentsByFieldNames()
                       .entrySet()
                       .stream()
                       .filter(entry -> line.startsWith(entry.getKey() + ":"))
                       .map(Map.Entry::getValue)
                       .findAny();
    }
}
