package de.exlll.configlib;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import java.util.Map;

enum YamlSerializer {
    ;
    private static final Yaml yaml = createYaml();

    private static Yaml createYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        return new Yaml(options);
    }

    static String serialize(Map<String, Object> mapToDump) {
        return yaml.dump(mapToDump);
    }

    /**
     * @throws ParserException    if invalid YAML
     * @throws ClassCastException if parsed Object is not a {@code Map}
     */
    static Map<String, Object> deserialize(String stringToLoad) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) yaml.load(stringToLoad);
        return map;
    }
}
