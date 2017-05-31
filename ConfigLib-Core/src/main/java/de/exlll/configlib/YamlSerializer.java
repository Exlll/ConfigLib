package de.exlll.configlib;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.util.Map;
import java.util.Objects;

final class YamlSerializer {
    private final Yaml yaml;

    /**
     * @param baseConstructor BaseConstructor which is used to configure the {@code Yaml} object.
     * @param representer     Representer which is used to configure the {@code Yaml} object.
     * @param dumperOptions   DumperOptions which is used to configure the {@code Yaml} object.
     * @param resolver        Resolver which is used to configure the {@code Yaml} object.
     * @see org.yaml.snakeyaml.constructor.BaseConstructor
     * @see org.yaml.snakeyaml.representer.Representer
     * @see org.yaml.snakeyaml.DumperOptions
     * @see org.yaml.snakeyaml.resolver.Resolver
     */
    YamlSerializer(BaseConstructor baseConstructor,
                   Representer representer,
                   DumperOptions dumperOptions,
                   Resolver resolver) {
        Objects.requireNonNull(baseConstructor);
        Objects.requireNonNull(representer);
        Objects.requireNonNull(dumperOptions);
        Objects.requireNonNull(resolver);
        yaml = new Yaml(baseConstructor, representer, dumperOptions, resolver);
    }

    /**
     * Serializes a Map.
     *
     * @param map Map to serialize
     * @return a serialized representation of the Map
     * @throws NullPointerException if {@code map} is null.
     */
    String serialize(Map<String, ?> map) {
        return yaml.dump(map);
    }

    /**
     * Deserializes a serialized Map.
     *
     * @param serializedMap a serialized YAML representation of a Map
     * @return deserialized Map
     * @throws ClassCastException   if {@code serializedMap} doesn't represent a Map
     * @throws NullPointerException if {@code serializedMap} is null
     * @throws ParserException      if {@code serializedMap} is invalid YAML
     */
    Map<String, Object> deserialize(String serializedMap) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) yaml.load(serializedMap);
        return map;
    }
}
