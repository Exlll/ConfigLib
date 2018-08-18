package de.exlll.configlib.configs.yaml;

import de.exlll.configlib.Comments;
import de.exlll.configlib.ConfigurationSource;
import de.exlll.configlib.configs.yaml.YamlConfiguration.YamlProperties;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        private static final Pattern PREFIX_PATTERN = Pattern.compile("^\\w+:.*");
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
                Matcher m = PREFIX_PATTERN.matcher(dumpLine);
                if (m.matches()) {
                    addFieldComment(dumpLine);
                }
                builder.append(dumpLine).append('\n');
            }
        }

        private void addFieldComment(String dumpLine) {
            Map<String, String> map = yamlComments.fieldCommentAsStrings(
                    props.getFormatter()
            );
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String prefix = entry.getKey() + ":";
                if (dumpLine.startsWith(prefix)) {
                    builder.append(entry.getValue()).append('\n');
                    break;
                }
            }

        }
    }
}
