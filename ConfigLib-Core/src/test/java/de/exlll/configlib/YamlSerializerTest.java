package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import de.exlll.configlib.configs.CustomConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yaml.snakeyaml.parser.ParserException;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.*;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class YamlSerializerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void deserializeRequiresValidYaml() throws Exception {
        exception.expect(ParserException.class);
        new YamlSerializer().deserialize("{a");
    }

    @Test
    public void deserializeReturnsMaps() throws Exception {
        Map<String, Object> actual = new YamlSerializer()
                .deserialize("a: 1\nb: c");

        Map<String, Object> expected = new HashMap<>();
        expected.put("a", 1);
        expected.put("b", "c");

        assertThat(actual, is(expected));
    }

    @Test
    public void isDefaultClassReturnsOnlyTrueForDefaultClasses() throws Exception {
        Class<?>[] classes = {
                Boolean.class, Long.class, Integer.class, Short.class, Byte.class,
                Double.class, Float.class, String.class, Character.class
        };

        assertThat(YamlSerializer.DEFAULT_CLASSES.size(), is(9));

        YamlSerializer serializer = new YamlSerializer();
        for (Class<?> cls : classes) {
            assertThat(serializer.isDefaultClass(cls), is(true));
        }
    }

    @Test
    public void isDefaultInstanceChecksDefaultInstances() throws Exception {
        YamlSerializer serializer = new YamlSerializer();

        assertThat(serializer.isDefaultInstance(HashMap.class), is(true));
        assertThat(serializer.isDefaultInstance(HashSet.class), is(true));
        assertThat(serializer.isDefaultInstance(ArrayList.class), is(true));
        assertThat(serializer.isDefaultInstance(Object.class), is(false));
    }

    @Test
    public void tagAddedIfUnknown() throws Exception {
        YamlSerializer serializer = new YamlSerializer();
        Class<?> unknown = Unknown.class;

        assertThat(serializer.isKnown(unknown), is(false));
        serializer.addTagIfClassUnknown(unknown);
        assertThat(serializer.isKnown(unknown), is(true));
    }

    @Test
    public void serializeAddsUnknownTags() throws Exception {
        YamlSerializer serializer = new YamlSerializer();
        Class<?> unknown = Unknown.class;
        assertThat(serializer.isKnown(unknown), is(false));

        Map<String, Object> map = new HashMap<>();
        serializer.serialize(map);
        assertThat(serializer.isKnown(unknown), is(false));

        map.put("", new Unknown());
        serializer.serialize(map);
        assertThat(serializer.isKnown(unknown), is(true));
    }

    @Test
    public void serializeSerializesCustomObjects() throws Exception {
        try (FileSystem system = Jimfs.newFileSystem()) {
            Path path = system.getPath("/a");

            CustomConfiguration saveConfig = new CustomConfiguration(path);
            // cs1
            saveConfig.getCs1().getMap().put("NEW KEY", Collections.singletonList("NEW VALUE"));
            saveConfig.getCs1().getO1().setS1("NEW VALUE 1");
            // cs2
            saveConfig.getCs2().getO2().setS2("NEW VALUE 2");
            saveConfig.getCs2().setI1(Integer.MAX_VALUE);
            // config
            saveConfig.setConfig("NEW CONFIG");
            saveConfig.save();

            CustomConfiguration loadConfig = new CustomConfiguration(path);
            loadConfig.load();

            assertThat(loadConfig.getCs1().getMap().get("NEW KEY"), hasItem("NEW VALUE"));
            assertThat(loadConfig.getCs1().getO1().getS1(), is("NEW VALUE 1"));
            assertThat(loadConfig.getCs2().getO2().getS2(), is("NEW VALUE 2"));
            assertThat(loadConfig.getCs2().getI1(), is(Integer.MAX_VALUE));
            assertThat(loadConfig.getConfig(), is("NEW CONFIG"));
        }
    }

    public static final class Unknown {
    }
}