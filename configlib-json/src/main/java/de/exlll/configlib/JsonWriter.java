package de.exlll.configlib;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static de.exlll.configlib.Validator.requireNonNull;

/**
 * A writer that writes JSON to a file, capable of injecting comments.
 * <p>
 * Note: Standard JSON does not support comments. This writer produces "JSON with Comments"
 * (similar to JSON5 or Jackson's ALLOW_JAVA_COMMENTS feature). Ensure your reader
 * is configured to allow comments.
 */
final class JsonWriter {
    private final OutputStream outputStream;
    private final JsonConfigurationProperties properties;

    JsonWriter(OutputStream outputStream, JsonConfigurationProperties properties) {
        this.outputStream = requireNonNull(outputStream, "output stream");
        this.properties = requireNonNull(properties, "configuration properties");
    }

    JsonWriter(Path configurationFile, JsonConfigurationProperties properties) {
        requireNonNull(configurationFile, "configuration file");
        try {
            this.outputStream = Files.newOutputStream(configurationFile);
            this.properties = properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeJson(String json) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(outputStream, properties.getCharset()))) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
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