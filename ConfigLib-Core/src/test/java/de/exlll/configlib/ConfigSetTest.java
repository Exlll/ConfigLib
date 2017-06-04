package de.exlll.configlib;

import de.exlll.configlib.classes.ConfigTypeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static de.exlll.configlib.classes.ConfigTypeClass.*;
import static de.exlll.configlib.classes.ConfigTypeClass.from;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ConfigSetTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructorRequiresNonNullClass() throws Exception {
        expectedException.expect(NullPointerException.class);
        new ConfigSet<>(null);
    }

    @Test
    public void constructorRequiresClassWithDefaultConstructor() throws Exception {
        new ConfigSet<>(String.class);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Class ConfigSet doesn't have a default constructor.");
        new ConfigSet<>(ConfigSet.class);
    }

    @Test
    public void toDefaultReturnsLinkedHashSet() throws Exception {
        Set<?> set = new ConfigSet<>(String.class).toDefault();
        assertThat(set, instanceOf(LinkedHashSet.class));
    }

    @Test
    public void toDefaultReturnsSimpleTypes() throws Exception {
        ConfigTypeClass c = new ConfigTypeClass();
        Set<?> s = c.configSetSimple.toDefault();
        assertThat(s, is(c.configSetSimple.getSet()));
    }

    @Test
    public void toDefaultReturnsSerializedObjects() throws Exception {
        ConfigTypeClass c = new ConfigTypeClass();
        Set<?> s = c.configSet.toDefault();
        for (A a : c.configSet) {
            Object mapped = FieldMapper.instanceToMap(a);
            assertThat(s, contains(mapped));
        }
    }

    @Test
    public void fromDefaultAddsSimpleTypes() throws Exception {
        Set<String> set = new HashSet<>(Arrays.asList("a", "b"));
        ConfigSet<String> configSet = new ConfigSet<>(String.class);
        configSet.fromDefault(set);
        assertThat(configSet.getSet(), is(set));
    }

    @Test
    public void fromDefaultAddsDeserializedObjects() throws Exception {
        A a = from("a");
        A b = from("b");
        Map<String, ?> map1 = FieldMapper.instanceToMap(a);
        Map<String, ?> map2 = FieldMapper.instanceToMap(b);

        Set<Map<String, ?>> set = new HashSet<>(Arrays.asList(map1, map2));
        ConfigSet<A> configSet = new ConfigSet<>(A.class);
        configSet.fromDefault(set);

        assertThat(configSet.getSet(), is(new HashSet<>(Arrays.asList(a, b))));
    }
}