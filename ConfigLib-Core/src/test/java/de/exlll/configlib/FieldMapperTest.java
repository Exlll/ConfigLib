package de.exlll.configlib;

import de.exlll.configlib.classes.DefaultTypeClass;
import de.exlll.configlib.classes.NonDefaultTypeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class FieldMapperTest {
    private final Path path = Paths.get("a");

    @Test
    public void toSerializableObjectReturnsObjectForDefaultTypes() throws Exception {
        DefaultTypeClass instance = new DefaultTypeClass(path);
        for (Field f : DefaultTypeClass.class.getDeclaredFields()) {
            Object value = Reflect.getValue(f, instance);
            assertThat(FieldMapper.toSerializableObject(value), sameInstance(value));
        }
    }

    @Test
    public void toSerializableObjectReturnsMapForNonDefaultTypes() throws Exception {
        DefaultTypeClass instance = new DefaultTypeClass(path);

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) FieldMapper.toSerializableObject(instance);

        int counter = 0;
        for (Field field : DefaultTypeClass.class.getDeclaredFields()) {
            Object fieldValue = Reflect.getValue(field, instance);
            assertThat(map.get(field.getName()), is(fieldValue));
            counter++;
        }
        assertThat(map.size(), is(counter));
    }

    @Test
    public void fromSerializedObjectIgnoresNullValues() throws Exception {
        DefaultTypeClass instance = new DefaultTypeClass(path);

        for (Field field : DefaultTypeClass.class.getDeclaredFields()) {
            Object currentValue = Reflect.getValue(field, instance);
            FieldMapper.fromSerializedObject(field, instance, null);
            Object newValue = Reflect.getValue(field, instance);

            if (field.getType().isPrimitive()) {
                assertThat(currentValue, is(newValue));
            } else {
                assertThat(currentValue, sameInstance(newValue));
            }
        }
    }

    @Test
    public void fromSerializedObjectSetsValueIfDefaultType() throws Exception {
        DefaultTypeClass instance = new DefaultTypeClass(path);

        Map<String, Object> map = DefaultTypeClass.newValues();
        for (Field field : DefaultTypeClass.class.getDeclaredFields()) {
            String fieldName = field.getName();
            Object mapValue = map.get(fieldName);
            FieldMapper.fromSerializedObject(field, instance, mapValue);
            Object value = Reflect.getValue(field, instance);

            if (field.getType().isPrimitive()) {
                assertThat(mapValue, is(value));
            } else {
                assertThat(mapValue, sameInstance(value));
            }
        }
    }

    @Test
    public void fromSerializedObjectUpdatesValueIfNotDefaultType() throws Exception {
        NonDefaultTypeClass instance = new NonDefaultTypeClass(path);
        Field field = NonDefaultTypeClass.class.getDeclaredField("defaultTypeClass");

        Map<String, Object> map = DefaultTypeClass.newValues();
        FieldMapper.fromSerializedObject(field, instance, map);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Field f = DefaultTypeClass.class.getDeclaredField(entry.getKey());
            Object value = Reflect.getValue(f, instance.defaultTypeClass);
            assertThat(value, is(entry.getValue()));
        }
    }

    @Test
    public void instanceTopMapCreatesMap() throws Exception {
        TestClass t = new TestClass();
        Map<String, Object> map = FieldMapper.instanceToMap(t);

        assertThat(map.get("i"), is(1));
        assertThat(map.get("i"), instanceOf(Integer.class));

        assertThat(map.get("z"), is(0));
        assertThat(map.get("z"), instanceOf(Integer.class));

        assertThat(map.get("d"), is(2.0));
        assertThat(map.get("d"), instanceOf(Double.class));

        assertThat(map.get("s"), is("s"));
        assertThat(map.get("s"), instanceOf(String.class));

        assertThat(map.get("c"), is('c'));
        assertThat(map.get("c"), instanceOf(Character.class));

        assertThat(map.get("strings"), is(Arrays.asList("1", "2")));
        assertThat(map.get("strings"), instanceOf(List.class));

        Map<String, Integer> intMap = new HashMap<>();
        intMap.put("a", 1);
        intMap.put("b", 2);
        assertThat(map.get("objects"), is(intMap));
        assertThat(map.get("objects"), instanceOf(Map.class));

        Map<String, Object> bMap = new HashMap<>();
        bMap.put("j", -1);
        bMap.put("t", "t");
        assertThat(map.get("b"), is(bMap));
        assertThat(map.get("b"), instanceOf(Map.class));
    }

    @Test
    public void instanceFromMapKeepsDefaultValues() throws Exception {
        TestClass t = new TestClass();
        FieldMapper.instanceFromMap(t, new HashMap<>());
        assertThat(t.z, is(0));
        assertThat(t.i, is(1));
        assertThat(t.s, is("s"));
    }

    @Test
    public void instanceFromMapSetsValues() throws Exception {
        TestClass t = new TestClass();

        Map<String, Object> map = new HashMap<>();
        map.put("z", 2);
        map.put("i", 10);
        map.put("c", 'q');
        map.put("s", "t");
        map.put("strings", Arrays.asList("99", "100", "101"));

        Map<String, Object> objects = new HashMap<>();
        objects.put("a", 100);
        objects.put("b", 200);
        objects.put("c", 300);
        objects.put("d", 400);
        map.put("objects", objects);

        Map<String, Object> bMap = new HashMap<>();
        bMap.put("j", 20);
        bMap.put("t", "v");
        map.put("b", bMap);


        FieldMapper.instanceFromMap(t, map);
        assertThat(t.z, is(2));
        assertThat(t.i, is(10));
        assertThat(t.c, is('q'));
        assertThat(t.s, is("t"));
        assertThat(t.strings, is(Arrays.asList("99", "100", "101")));
        assertThat(t.objects, is(objects));
        assertThat(t.b.j, is(20));
        assertThat(t.b.t, is("v"));
    }

    private static final class TestClass {
        private int z;
        private int i = 1;
        private double d = 2.0;
        private String s = "s";
        private List<String> strings = Arrays.asList("1", "2");
        private Map<String, Object> objects = new HashMap<>();
        private char c = 'c';
        private TestClassB b = new TestClassB();

        public TestClass() {
            objects.put("a", 1);
            objects.put("b", 2);
        }
    }

    private static final class TestClassB {
        private int j = -1;
        private String t = "t";
    }
}