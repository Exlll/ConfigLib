package de.exlll.configlib;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

enum ConfigReader {
    ;

    static String read(Path path) throws IOException {
        try (Scanner scanner = new Scanner(path)) {
            scanner.useDelimiter("\\z");
            return scanner.next();
        }
    }
}
