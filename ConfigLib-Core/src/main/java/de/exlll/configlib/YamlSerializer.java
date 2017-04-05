package de.exlll.configlib;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.representer.Representer;

import java.util.*;

final class YamlSerializer {
    static final Set<Class<?>> DEFAULT_CLASSES = new HashSet<>();
    static final DumperOptions DUMPER_OPTIONS = new DumperOptions();
    private final Constructor constructor = new Constructor();
    private final Representer representer = new Representer();
    private final Yaml yaml = new Yaml(constructor, representer, DUMPER_OPTIONS);
    private final Set<Class<?>> knownClasses = new HashSet<>();

    static {
        Class<?>[] classes = {
                Boolean.class, Long.class, Integer.class, Short.class, Byte.class,
                Double.class, Float.class, String.class, Character.class
        };
        DEFAULT_CLASSES.addAll(Arrays.asList(classes));

        DUMPER_OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        DUMPER_OPTIONS.setIndent(2);
    }

    String serialize(Map<String, Object> mapToDump) {
        mapToDump.forEach((fieldName, fieldValue) -> addTagIfClassUnknown(fieldValue.getClass()));
        return yaml.dump(mapToDump);
    }

    /**
     * @throws ParserException    if invalid YAML
     * @throws ClassCastException if parsed Object is not a {@code Map}
     */
    Map<String, Object> deserialize(String stringToLoad) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) yaml.load(stringToLoad);
        return map;
    }

    void addTagIfClassUnknown(Class<?> valueClass) {
        if (!isKnown(valueClass)) {
            knownClasses.add(valueClass);
            Tag tag = new Tag("!" + valueClass.getSimpleName());
            constructor.addTypeDescription(new TypeDescription(valueClass, tag));
            representer.addClassTag(valueClass, tag);
        }
    }

    boolean isDefaultInstance(Class<?> c) {
        return Set.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c) ||
                List.class.isAssignableFrom(c);
    }

    boolean isDefaultClass(Class<?> c) {
        return DEFAULT_CLASSES.contains(c);
    }

    boolean isKnown(Class<?> cls) {
        return knownClasses.contains(cls) || isDefaultClass(cls) || isDefaultInstance(cls);
    }
}
