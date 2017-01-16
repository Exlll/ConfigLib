package de.exlll.configlib;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

class ConfigurationReader {
    private final Path path;

    ConfigurationReader(Path path) {
        this.path = path;
    }

    String read() throws IOException {
        try (Scanner scanner = new Scanner(path)) {
            return scanner.useDelimiter("\\z").next();
        }
    }
}
