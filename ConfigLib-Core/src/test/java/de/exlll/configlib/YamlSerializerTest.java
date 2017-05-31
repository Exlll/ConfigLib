package de.exlll.configlib;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class YamlSerializerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private DumperOptions dumperOptions;
    private Map<String, Object> map;
    private String serializedMap;

    @Before
    public void setUp() throws Exception {
        dumperOptions = new DumperOptions();
        dumperOptions.setIndent(2);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2.2);
        map.put("3", "3");
        map.put("4", new ArrayList<>());
        map.put("5", Arrays.asList("5", "5", "5"));
        map.put("6", new HashMap<>());

        Map<String, Object> subMap = new HashMap<>();
        subMap.put("1", 1);
        subMap.put("2", 2.2);
        subMap.put("3", "3");
        subMap.put("4", new ArrayList<>());
        subMap.put("5", Arrays.asList("5", "5", "5"));
        subMap.put("6", new HashMap<>());

        map.put("7", subMap);

        serializedMap = "'1': 1\n" +
                "'2': 2.2\n" +
                "'3': '3'\n" +
                "'4': []\n" +
                "'5':\n" +
                "- '5'\n" +
                "- '5'\n" +
                "- '5'\n" +
                "'6': {}\n" +
                "'7':\n" +
                "  '1': 1\n" +
                "  '2': 2.2\n" +
                "  '3': '3'\n" +
                "  '4': []\n" +
                "  '5':\n" +
                "  - '5'\n" +
                "  - '5'\n" +
                "  - '5'\n" +
                "  '6': {}\n";
    }

    @Test
    public void constructorRequiresNonNullBaseConstructor() throws Exception {
        expectedException.expect(NullPointerException.class);
        new YamlSerializer(null, new Representer(), new DumperOptions(), new Resolver());
    }

    @Test
    public void constructorRequiresNonNullRepresenter() throws Exception {
        expectedException.expect(NullPointerException.class);
        new YamlSerializer(new Constructor(), new Representer(), null, new Resolver());
    }

    @Test
    public void constructorRequiresNonNullDumperOptions() throws Exception {
        expectedException.expect(NullPointerException.class);
        new YamlSerializer(new Constructor(), null, new DumperOptions(), new Resolver());
    }

    @Test
    public void constructorRequiresNonNullResolver() throws Exception {
        expectedException.expect(NullPointerException.class);
        new YamlSerializer(new Constructor(), new Representer(), new DumperOptions(), null);
    }

    @Test
    public void serialize() throws Exception {
        String s = new YamlSerializer(
                new Constructor(), new Representer(), dumperOptions, new Resolver()
        ).serialize(map);
        assertThat(s, is(serializedMap));
    }

    @Test
    public void deserialize() throws Exception {
        assertThat(new YamlSerializer(
                new Constructor(), new Representer(), dumperOptions, new Resolver()
        ).deserialize(serializedMap), is(map));
    }
}