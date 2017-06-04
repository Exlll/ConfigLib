package de.exlll.configlib;

import de.exlll.configlib.classes.ConfigTypeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static de.exlll.configlib.classes.ConfigTypeClass.*;
import static de.exlll.configlib.classes.ConfigTypeClass.from;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConfigMapTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructorRequiresNonNullKeyClass() throws Exception {
        expectedException.expect(NullPointerException.class);
        new ConfigMap<>(null, Integer.class);
    }

    @Test
    public void constructorRequiresNonNullValueClass() throws Exception {
        expectedException.expect(NullPointerException.class);
        new ConfigMap<>(String.class, null);
    }

    @Test
    public void constructorRequiresValueClassWithDefaultConstructor() throws Exception {
        new ConfigMap<>(String.class, String.class);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Class ConfigMap doesn't have a default constructor.");
        new ConfigMap<>(String.class, ConfigMap.class);
    }

    @Test
    public void constructorRequiresKeyClassWithSimpleType() throws Exception {
        new ConfigMap<>(String.class, ConfigListTest.class);

        String msg = "Class " + Map.class.getSimpleName() + " is not a simple type.\n" +
                "Only simple types can be used as keys in a map.";
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(msg);
        new ConfigMap<>(Map.class, ConfigListTest.class);
    }

    @Test
    public void toDefaultReturnsLinkedHashMap() throws Exception {
        Map<String, ?> newMap = new ConfigMap<>(String.class, Integer.class).toDefault();
        assertThat(newMap, instanceOf(LinkedHashMap.class));
    }

    @Test
    public void toDefaultReturnsSimpleTypes() throws Exception {
        ConfigTypeClass c = new ConfigTypeClass();
        Map<String, ?> m = c.configMapSimple.toDefault();
        assertThat(m, is(c.configMapSimple.getMap()));
    }

    @Test
    public void toDefaultReturnsSerializedObjects() throws Exception {
        ConfigTypeClass c = new ConfigTypeClass();
        Map<String, ?> s = c.configMap.toDefault();
        for (Map.Entry<String, ConfigTypeClass.A> entry : c.configMap.entrySet()) {
            Object mapped = FieldMapper.instanceToMap(entry.getValue());
            assertThat(mapped, is(s.get(entry.getKey())));
        }
    }

    @Test
    public void fromDefaultAddsSimpleTypes() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        ConfigMap<String, Integer> configMap = new ConfigMap<>(String.class, Integer.class);
        configMap.fromDefault(map);
        assertThat(configMap.getMap(), is(map));
    }

    @Test
    public void fromDefaultAddsDeserializedObjects() throws Exception {
        A a = from("a");
        A b = from("b");
        Map<String, ?> map1 = FieldMapper.instanceToMap(a);
        Map<String, ?> map2 = FieldMapper.instanceToMap(b);

        Map<String, Map<String, ?>> map = new HashMap<>();
        map.put("a", map1);
        map.put("b", map2);
        ConfigMap<String, A> configMap = new ConfigMap<>(String.class, A.class);
        configMap.fromDefault(map);

        Map<String, A> newMap = new HashMap<>();
        newMap.put("a", a);
        newMap.put("b", b);
        assertThat(configMap.getMap(), is(newMap));
    }
}