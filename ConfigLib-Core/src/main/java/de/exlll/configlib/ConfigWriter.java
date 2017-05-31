package de.exlll.configlib;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

enum ConfigWriter {
    ;

    static void write(Path path, String text) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            writer.write(text);
        }
    }
}
