package de.exlll.configlib;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yaml.snakeyaml.parser.ParserException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class YamlSerializerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void desirializeRequiresValidYaml() throws Exception {
        exception.expect(ParserException.class);
        YamlSerializer.deserialize("{a");
    }

    @Test
    public void desirializeReturnsMaps() throws Exception {
        Map<String, Object> actual = YamlSerializer
                .deserialize("a: 1\nb: c");

        Map<String, Object> expected = new HashMap<>();
        expected.put("a", 1);
        expected.put("b", "c");

        assertThat(actual, is(expected));
    }
}