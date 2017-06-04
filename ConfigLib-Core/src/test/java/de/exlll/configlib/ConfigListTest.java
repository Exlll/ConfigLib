package de.exlll.configlib;

import de.exlll.configlib.classes.ConfigTypeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static de.exlll.configlib.classes.ConfigTypeClass.A;
import static de.exlll.configlib.classes.ConfigTypeClass.from;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConfigListTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructorRequiresNonNullClass() throws Exception {
        expectedException.expect(NullPointerException.class);
        new ConfigList<>(null);
    }

    @Test
    public void constructorRequiresClassWithDefaultConstructor() throws Exception {
        new ConfigList<>(String.class);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Class ConfigList doesn't have a default constructor.");
        new ConfigList<>(ConfigList.class);
    }

    @Test
    public void toDefaultReturnsArrayList() throws Exception {
        List<?> list = new ConfigList<>(String.class).toDefault();
        assertThat(list, instanceOf(ArrayList.class));
    }

    @Test
    public void toDefaultReturnsSimpleTypes() throws Exception {
        ConfigTypeClass c = new ConfigTypeClass();
        List<?> l = c.configListSimple.toDefault();
        assertThat(l, is(c.configListSimple.getList()));
    }

    @Test
    public void toDefaultReturnsSerializedObjects() throws Exception {
        ConfigTypeClass c = new ConfigTypeClass();
        List<?> l = c.configList.toDefault();
        for (int i = 0; i < c.configList.size(); i++) {
            Object mapped = FieldMapper.instanceToMap(c.configList.get(i));
            assertThat(mapped, is(l.get(i)));
        }
    }

    @Test
    public void fromDefaultAddsSimpleTypes() throws Exception {
        List<String> list = Arrays.asList("a", "b");
        ConfigList<String> configList = new ConfigList<>(String.class);
        configList.fromDefault(list);
        assertThat(configList.getList(), is(list));
    }

    @Test
    public void fromDefaultAddsDeserializedObjects() throws Exception {
        A a = from("a");
        A b = from("b");
        Map<String, ?> map1 = FieldMapper.instanceToMap(a);
        Map<String, ?> map2 = FieldMapper.instanceToMap(b);

        List<Map<String, ?>> list = Arrays.asList(map1, map2);
        ConfigList<A> configList = new ConfigList<>(A.class);
        configList.fromDefault(list);

        assertThat(configList.getList(), is(Arrays.asList(a, b)));
    }
}