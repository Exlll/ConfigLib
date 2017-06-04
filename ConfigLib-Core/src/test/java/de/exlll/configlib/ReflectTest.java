package de.exlll.configlib;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ReflectTest {
    private static final Class<TestClass> cls1 = TestClass.class;
    private static final Class<NotDefaultConstructor> cls2 = NotDefaultConstructor.class;
    private final List<String> list = new ConfigList<>(String.class);
    private final Set<String> set = new ConfigSet<>(String.class);
    private final Map<?, String> map = new ConfigMap<>(String.class, String.class);
    private final Class<?>[] containerClasses = {List.class, Set.class, Map.class};
    private final Class<?>[] simpleClasses = {
            boolean.class, char.class, byte.class, short.class,
            int.class, long.class, float.class, double.class,
            Boolean.class, String.class, Character.class,
            Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class,
    };
    private final String errorMessage = "Class NotDefaultConstructor doesn't have a default constructor.";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void checkType() throws Exception {
        Reflect.checkType(new HashMap<>(), Map.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid type!\n" +
                "Object 'a' is of type String. Expected type: Map");
        Reflect.checkType("a", Map.class);
    }

    @Test
    public void checkMapEntriesChecksKeys() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        Reflect.checkMapEntries(map, String.class, Integer.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid type!\n" +
                "Object 'a' is of type String. Expected type: Integer");
        Reflect.checkMapEntries(map, Integer.class, Integer.class);
    }

    @Test
    public void checkMapEntriesChecksValues() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        Reflect.checkMapEntries(map, String.class, Integer.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid type!\n" +
                "Object '1' is of type Integer. Expected type: String");
        Reflect.checkMapEntries(map, String.class, String.class);
    }

    @Test
    public void isDefault() throws Exception {
        for (Class<?> cls : containerClasses) {
            assertThat(Reflect.isDefault(cls), is(true));
        }
        for (Class<?> cls : simpleClasses) {
            assertThat(Reflect.isDefault(cls), is(true));
        }
    }

    @Test
    public void isSimpleType() throws Exception {
        for (Class<?> cls : simpleClasses) {
            assertThat(Reflect.isSimpleType(cls), is(true));
        }
    }

    @Test
    public void isConfigList() throws Exception {
        assertThat(Reflect.isConfigList(list.getClass()), is(true));
        assertThat(Reflect.isConfigList(set.getClass()), is(false));
        assertThat(Reflect.isConfigList(map.getClass()), is(false));
    }

    @Test
    public void isConfigSet() throws Exception {
        assertThat(Reflect.isConfigSet(list.getClass()), is(false));
        assertThat(Reflect.isConfigSet(set.getClass()), is(true));
        assertThat(Reflect.isConfigSet(map.getClass()), is(false));
    }

    @Test
    public void isConfigMap() throws Exception {
        assertThat(Reflect.isConfigMap(list.getClass()), is(false));
        assertThat(Reflect.isConfigMap(set.getClass()), is(false));
        assertThat(Reflect.isConfigMap(map.getClass()), is(true));
    }

    @Test
    public void isContainerType() throws Exception {
        assertThat(Reflect.isContainerType(list.getClass()), is(true));
        assertThat(Reflect.isContainerType(set.getClass()), is(true));
        assertThat(Reflect.isContainerType(map.getClass()), is(true));
        assertThat(Reflect.isContainerType(Object.class), is(false));
    }

    @Test
    public void getValueGetsValue() throws Exception {
        TestClass testClass = new TestClass();

        Field s = TestClass.class.getDeclaredField("s");
        assertThat(Reflect.getValue(s, testClass), is("s"));
    }

    @Test
    public void setValueSetsValue() throws Exception {
        TestClass testClass = new TestClass();

        Field s = TestClass.class.getDeclaredField("s");
        Reflect.setValue(s, testClass, "t");
        assertThat(testClass.s, is("t"));
    }

    @Test
    public void checkForDefaultConstructorsThrowsExceptionIfNoDefault() throws Exception {
        Reflect.checkDefaultConstructor(cls1);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(errorMessage);
        Reflect.checkDefaultConstructor(cls2);
    }

    @Test
    public void hasDefaultConstructor() throws Exception {
        assertThat(Reflect.hasDefaultConstructor(cls1), is(true));
        assertThat(Reflect.hasDefaultConstructor(cls2), is(false));
    }

    @Test
    public void getDefaultConstructor() throws Exception {
        assertThat(Reflect.getDefaultConstructor(cls1), is(cls1.getDeclaredConstructor()));
        expectedException.expect(RuntimeException.class);
        Reflect.getDefaultConstructor(cls2);
    }

    @Test
    public void newInstanceChecksForDefaultConstructor() throws Exception {
        Reflect.newInstance(TestClass.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(errorMessage);
        Reflect.newInstance(NotDefaultConstructor.class);
    }

    @Test
    public void newInstanceCreatesNewInstance() throws Exception {
        TestClass t = (TestClass) Reflect.newInstance(TestClass.class);
    }

    private static final class NotDefaultConstructor {
        public NotDefaultConstructor(String a) {
        }
    }

    private static final class TestClass {
        private String s = "s";
    }
}