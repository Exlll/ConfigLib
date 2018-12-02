package de.exlll.configlib.configs.yaml;

import de.exlll.configlib.Comments;
import de.exlll.configlib.ConfigurationSource;
import de.exlll.configlib.configs.yaml.YamlConfiguration.YamlProperties;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

final class YamlSource implements ConfigurationSource<YamlConfiguration> {
    private final Path configPath;
    private final YamlProperties props;
    private final Yaml yaml;

    public YamlSource(Path configPath, YamlProperties props) {
        this.configPath = Objects.requireNonNull(configPath);
        this.props = props;
        this.yaml = new Yaml(
                props.getConstructor(), props.getRepresenter(),
                props.getOptions(), props.getResolver()
        );
    }

    @Override
    public void saveConfiguration(YamlConfiguration config, Map<String, Object> map)
            throws IOException {
        createParentDirectories();
        CommentAdder adder = new CommentAdder(
                yaml.dump(map), config.getComments(), props
        );
        String commentedDump = adder.getCommentedDump();
        Files.write(configPath, commentedDump.getBytes());
    }

    private void createParentDirectories() throws IOException {
        Path parentDir = configPath.getParent();
        if (!Files.isDirectory(parentDir)) {
            Files.createDirectories(parentDir);
        }
    }

    @Override
    public Map<String, Object> loadConfiguration(YamlConfiguration config)
            throws IOException {
        String cfg = readConfig();
        return yaml.load(cfg);
    }

    private String readConfig() throws IOException {
        return Files.lines(configPath).collect(joining("\n"));
    }

    private static final class CommentAdder {
        private static final Pattern PREFIX_PATTERN = Pattern.compile("(^\\w+):(.*)");
        private static final Pattern SPACES_PATTERN = Pattern.compile("^[ \\s\\n]+.*");
        private final String dump;
        private final Comments comments;
        private final YamlComments yamlComments;
        private final YamlProperties props;
        private final StringBuilder builder;

        private CommentAdder(String dump, Comments comments,
                             YamlProperties props
        ) {
            this.dump = dump;
            this.props = props;
            this.comments = comments;
            this.yamlComments = new YamlComments(comments);
            this.builder = new StringBuilder(dump.length());
        }

        public String getCommentedDump() {
            addComments(props.getPrependedComments());
            addClassComments();
            addFieldComments();
            addComments(props.getAppendedComments());
            return builder.toString();
        }

        private void addComments(List<String> comments) {
            for (String comment : comments) {
                if (!comment.isEmpty()) {
                    builder.append("# ").append(comment);
                }
                builder.append('\n');
            }
        }

        private void addClassComments() {
            if (comments.hasClassComments()) {
                builder.append(yamlComments.classCommentsAsString());
                builder.append("\n");
            }
        }

        private void addFieldComments() {
            if (comments.hasFieldComments()) {
                List<String> dumpLines = Arrays.asList(dump.split("\n"));
                addDumpLines(dumpLines);
            } else {
                builder.append(dump);
            }
        }

        private void addDumpLines(List<String> dumpLines) {
            for (String dumpLine : dumpLines) {
                Matcher m = PREFIX_PATTERN.matcher(trimStart(dumpLine));
                if (m.matches()) {
                    addFieldComment(dumpLine, trimStart(dumpLine), m);
                }
                builder.append(dumpLine).append('\n');
            }
        }

        private StringBuilder data = new StringBuilder();

        private void addFieldComment(String dumpLine, String dumpLineWithTrim, Matcher matcher) {
            Map<String, String> map = yamlComments.fieldCommentAsStrings(props.getFormatter());
            Matcher spacesMatch = SPACES_PATTERN.matcher(dumpLine);
            String value = matcher.group(2);
            if (value.equals("") || value.isEmpty()) {
                data.append(matcher.group(1)).append(".");
            } else {
                if (!spacesMatch.matches()) {
                    data = new StringBuilder();
                }
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String prefix = entry.getKey() + ":";
                String lData = data.toString().replaceFirst("[.]$", "");
                String[] splitPrefix = prefix.split("\\.");
                if (dumpLine.startsWith(prefix) || (lData.startsWith(splitPrefix[0]) && dumpLineWithTrim.startsWith(splitPrefix[splitPrefix.length - 1]))) {
                    String newDumpLine = dumpLine.replaceAll("^\\s+", "");
                    int count = dumpLine.length() - newDumpLine.length();
                    String[] valueSplit = entry.getValue().split("\n");
                    for (String val : valueSplit) {
                        int updateCount = val.length() + count;
                        char fill = ' ';
                        builder.append(new String(new char[updateCount - val.length()]).replace('\0', fill)).append(val).append("\n");
                    }
                    break;
                }
            }
        }

        private String trimStart(String value) {
            return value.replaceFirst("^\\s+", "");
        }
    }
}
