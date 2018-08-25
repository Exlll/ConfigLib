package de.exlll.configlib;

import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.annotation.ElementType;
import de.exlll.configlib.classes.TestSubClass;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

import static de.exlll.configlib.FieldMapperHelpers.*;
import static de.exlll.configlib.util.CollectionFactory.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("unused")
public class ValidatorTest {
    @Test
    void instanceFromMapRequiresMapToInitializeCustomClass() {
        class A {
            TestSubClass c = new TestSubClass();
        }

        Map<String, Object> map = mapOf(
                "c", "s"
        );
        String msg = "Initializing field 'c' requires a Map<String, Object> " +
                "but the given object is not a map." +
                "\nType: 'String'\tValue: 's'";
        assertIfmCfgExceptionMessage(new A(), map, msg);
    }

    @Test
    void instanceFromMapChecksEnumValuesAreString() {
        class A {
            LocalTestEnum t = LocalTestEnum.T;
        }
        Map<String, Object> map = mapOf(
                "t", 1
        );
        String msg = "Initializing enum 't' requires a string but '1' is of " +
                "type 'Integer'.";
        assertIfmCfgExceptionMessage(new A(), map, msg);
    }

    @Test
    void instanceFromMapRequiresMapWithStringsAsKeys() {
        class A {
            TestSubClass c = new TestSubClass();
        }

        Map<String, Object> map = mapOf(
                "c", mapOf(1, 200, "string", "s")
        );
        String msg = "Initializing field 'c' requires a Map<String, Object> " +
                "but the given map contains non-string keys." +
                "\nAll entries: " + map.get("c");
        assertIfmCfgExceptionMessage(new A(), map, msg);
    }


    @Test
    void instanceToMapRequiresNonNullMapKeys() {
        class A {
            TestSubClass c = new TestSubClass();
        }
        Map<String, Object> m1 = new HashMap<>();
        m1.put(null, "null");
        Map<String, Object> m2 = mapOf("c", m1);
        String msg = "Initializing field 'c' requires a Map<String, Object> " +
                "but the given map contains non-string keys." +
                "\nAll entries: {null=null}";
        assertIfmCfgExceptionMessage(new A(), m2, msg);
    }

    @Test
    void instanceFromMapRequiresCustomClassToHaveNoArgsConstructors() {
        class A {
            Sub3 s = new Sub3(1);
        }
        Map<String, Object> map = mapOf("s", mapOf());
        String msg = "Type 'Sub3' of field 's' doesn't have a no-args constructor.";
        assertIfmCfgExceptionMessage(new A(), map, msg);
    }

    @Test
    void instanceFromMapRequiresCustomClassToBeConfigurationElements() {
        class A {
            Sub1 s = new Sub1();
        }
        Map<String, Object> map = mapOf("s", mapOf());
        String msg = "Type 'Sub1' of field 's' is not annotated " +
                "as a configuration element.";
        assertIfmCfgExceptionMessage(new A(), map, msg);
    }

    @Test
    void instanceFromMapChecksThatContainerTypesMatch() {
        class A {
            CopyOnWriteArrayList<?> l = new CopyOnWriteArrayList<>();
        }
        class B {
            ConcurrentSkipListSet<?> s = new ConcurrentSkipListSet<>();
        }
        class C {
            ConcurrentHashMap<?, ?> m = new ConcurrentHashMap<>();
        }
        Map<String, Object> m = mapOf("l", listOf("s"));
        String msg = "Can not set field 'l' with type 'CopyOnWriteArrayList' " +
                "to 'ArrayList'.";
        assertIfmCfgExceptionMessage(new A(), m, msg);

        m = mapOf("s", setOf("s"));
        msg = "Can not set field 's' with type 'ConcurrentSkipListSet' " +
                "to 'LinkedHashSet'.";
        assertIfmCfgExceptionMessage(new B(), m, msg);

        m = mapOf("m", mapOf(1, "s"));
        msg = "Can not set field 'm' with type 'ConcurrentHashMap' " +
                "to 'LinkedHashMap'.";
        assertIfmCfgExceptionMessage(new C(), m, msg);

    }

    @Test
    void instanceToMapThrowsExceptionIfDefaultValueIsNull() {
        class A {
            String string;
        }
        String msg = "The value of field 'string' is null.\n" +
                "Please assign a non-null default value or remove this field.";
        assertItmCfgExceptionMessage(new A(), msg);
    }

    @Test
    void instanceFromMapThrowsExceptionIfDefaultValueIsNull() {
        class A {
            String string;
        }
        Map<String, Object> map = mapOf("string", "s");
        String msg = "The value of field 'string' is null.\n" +
                "Please assign a non-null default value or remove this field.";
        assertIfmCfgExceptionMessage(new A(), map, msg);
    }

    @Test
    void instanceToMapRequiresListsWithoutElementTypeToContainSimpleTypes() {
        class A {
            List<TestSubClass> l = new ArrayList<>(listOf(
                    TestSubClass.TEST_VALUES
            ));
        }
        class B {
            List<Set<Map<Integer, TestSubClass>>> l = new ArrayList<>(listOf(
                    setOf(mapOf(1, TestSubClass.TEST_VALUES))
            ));
        }

        String asString = TestSubClass.TEST_VALUES.toString();

        A a = new A();
        String msg = "The type of an element of list 'l' is not a simple type " +
                "but list 'l' is missing the ElementType annotation.\n" +
                "All elements: [" + asString + "]";
        assertItmCfgExceptionMessage(a, msg);

        B b = new B();
        msg = "The type of an element of list 'l' is not a simple type " +
                "but list 'l' is missing the ElementType annotation.\n" +
                "All elements: [[{1=" + asString + "}]]";
        assertItmCfgExceptionMessage(b, msg);
    }

    @Test
    void instanceToMapRequiresSetsWithoutElementTypeToContainSimpleTypes() {
        class A {
            Set<TestSubClass> s = new HashSet<>(setOf(
                    TestSubClass.TEST_VALUES
            ));
        }
        class B {
            Set<List<Map<Integer, TestSubClass>>> s = new HashSet<>(setOf(
                    listOf(mapOf(1, TestSubClass.TEST_VALUES))
            ));
        }

        String asString = TestSubClass.TEST_VALUES.toString();

        A a = new A();
        String msg = "The type of an element of set 's' is not a simple type " +
                "but set 's' is missing the ElementType annotation.\n" +
                "All elements: [" + asString + "]";
        assertItmCfgExceptionMessage(a, msg);

        B b = new B();
        msg = "The type of an element of set 's' is not a simple type " +
                "but set 's' is missing the ElementType annotation.\n" +
                "All elements: [[{1=" + asString + "}]]";
        assertItmCfgExceptionMessage(b, msg);
    }

    @Test
    void instanceToMapRequiresMapsWithoutElementTypeToContainSimpleTypes() {
        class A {
            Map<Integer, TestSubClass> m = new HashMap<>(mapOf(
                    1, TestSubClass.TEST_VALUES
            ));
        }
        class B {
            Map<Integer, Set<List<TestSubClass>>> m = new HashMap<>(mapOf(
                    1, setOf(listOf(TestSubClass.TEST_VALUES))
            ));
        }

        String asString = TestSubClass.TEST_VALUES.toString();

        A a = new A();
        String msg = "The type of a value of map 'm' is not a simple type " +
                "but map 'm' is missing the ElementType annotation.\n" +
                "All entries: {1=" + asString + "}";
        assertItmCfgExceptionMessage(a, msg);

        B b = new B();
        msg = "The type of a value of map 'm' is not a simple type " +
                "but map 'm' is missing the ElementType annotation.\n" +
                "All entries: {1=[[" + asString + "]]}";
        assertItmCfgExceptionMessage(b, msg);
    }

    @Test
    void instanceToMapRequiresNonNullListElements() {
        class A {
            @ElementType(TestSubClass.class)
            List<TestSubClass> l1 = new ArrayList<>();
            List<Integer> l2 = new ArrayList<>();
        }
        A a = new A();
        a.l1.add(null);
        a.l2.add(null);

        String msg = "An element of list 'l1' is null.\n" +
                "Please either remove or replace this element.\n" +
                "All elements: [null]";
        assertItmCfgExceptionMessage(a, msg);

        a.l1.clear();
        msg = "An element of list 'l2' is null.\n" +
                "Please either remove or replace this element.\n" +
                "All elements: [null]";
        assertItmCfgExceptionMessage(a, msg);
    }

    @Test
    void instanceToMapRequiresNonNullListElementsRecursively() {
        class A {
            @ElementType(TestSubClass.class)
            List<List<TestSubClass>> bla = new ArrayList<>();
        }
        A o = new A();
        o.bla.add(new ArrayList<>());
        o.bla.get(0).add(null);
        String msg = "An element of list 'bla' is null.\n" +
                "Please either remove or replace this element.\n" +
                "All elements: [[null]]";
        assertItmCfgExceptionMessage(o, msg);
    }

    @Test
    void instanceToMapRequiresNonNullSetElements() {
        class A {
            @ElementType(TestSubClass.class)
            Set<TestSubClass> s1 = new HashSet<>();
            Set<Integer> s2 = new HashSet<>();
        }
        A a = new A();
        a.s1.add(null);
        a.s2.add(null);

        String msg = "An element of set 's1' is null.\n" +
                "Please either remove or replace this element.\n" +
                "All elements: [null]";
        assertItmCfgExceptionMessage(a, msg);

        a.s1.clear();
        msg = "An element of set 's2' is null.\n" +
                "Please either remove or replace this element.\n" +
                "All elements: [null]";
        assertItmCfgExceptionMessage(a, msg);
    }

    @Test
    void instanceToMapRequiresNonNullSetElementsRecursively() {
        class A {
            @ElementType(TestSubClass.class)
            Set<List<TestSubClass>> bla = new HashSet<>();
        }
        A o = new A();
        o.bla.add(new ArrayList<>());
        o.bla.iterator().next().add(null);
        String msg = "An element of set 'bla' is null.\n" +
                "Please either remove or replace this element.\n" +
                "All elements: [[null]]";
        assertItmCfgExceptionMessage(o, msg);
    }


    @Test
    void instanceToMapRequiresNonNullMapValues() {
        class A {
            @ElementType(TestSubClass.class)
            Map<Integer, TestSubClass> m1 = new HashMap<>();
            Map<Integer, TestSubClass> m2 = new HashMap<>();
        }
        A a = new A();
        a.m1.put(1, null);
        a.m2.put(1, null);

        String msg = "A value of map 'm1' is null.\n" +
                "Please either remove or replace this element.\n" +
                "All entries: {1=null}";
        assertItmCfgExceptionMessage(a, msg);

        a.m1.clear();
        msg = "A value of map 'm2' is null.\n" +
                "Please either remove or replace this element.\n" +
                "All entries: {1=null}";
        assertItmCfgExceptionMessage(a, msg);
    }

    @Test
    void instanceToMapRequiresNonNullMapValuesRecursively() {
        class A {
            @ElementType(TestSubClass.class)
            Map<Integer, List<TestSubClass>> bla = new HashMap<>();
        }
        A o = new A();
        o.bla.put(1, new ArrayList<>());
        o.bla.get(1).add(null);
        String msg = "A value of map 'bla' is null.\n" +
                "Please either remove or replace this element.\n" +
                "All entries: {1=[null]}";
        assertItmCfgExceptionMessage(o, msg);
    }

    @Test
    void instanceToMapRequiresSimpleMapKeys() {
        class A {
            Map<TestSubClass, Integer> m = new HashMap<>();
        }
        A a = new A();
        a.m.put(TestSubClass.TEST_VALUES, 1);

        String msg = "The keys of map 'm' must be simple types.";
        assertItmCfgExceptionMessage(a, msg);
    }

    @Test
    void instanceToMapRequiresContainerTypesToMatchElementType() {
        class A {
            @ElementType(TestSubClass.class)
            List<Integer> l = new ArrayList<>();
            @ElementType(TestSubClass.class)
            Set<Integer> s = new HashSet<>();
            @ElementType(TestSubClass.class)
            Map<Integer, Integer> m = new HashMap<>();
        }
        A a = new A();
        a.l.add(1);
        a.s.add(1);
        a.m.put(1, 1);

        String msg = "The type of an element of list 'l' doesn't match the " +
                "type indicated by the ElementType annotation.\n" +
                "Required type: 'TestSubClass'\tActual type: 'Integer'\n" +
                "All elements: [1]";
        assertItmCfgExceptionMessage(a, msg);

        a.l.clear();
        msg = "The type of an element of set 's' doesn't match the " +
                "type indicated by the ElementType annotation.\n" +
                "Required type: 'TestSubClass'\tActual type: 'Integer'\n" +
                "All elements: [1]";
        assertItmCfgExceptionMessage(a, msg);

        a.s.clear();
        msg = "The type of a value of map 'm' doesn't match the " +
                "type indicated by the ElementType annotation.\n" +
                "Required type: 'TestSubClass'\tActual type: 'Integer'\n" +
                "All entries: {1=1}";
        assertItmCfgExceptionMessage(a, msg);
    }

    @Test
    void instanceToMapRequiresCustomClassesToBeConfigurationElements() {
        class A {
            Sub1 s = new Sub1();
        }
        class B {
            Sub2 s = new Sub2();
        }

        Map<String, Object> map = mapOf("s", Collections.emptyMap());

        assertThat(instanceToMap(new B()), is(map));

        String msg = "Type 'Sub1' of field 's' is not annotated " +
                "as a configuration element.";
        assertItmCfgExceptionMessage(new A(), msg);
    }

    @Test
    void instanceToMapRequiresElementTypesToBeConcreteType() {
        class A {
            @ElementType(LocalTestInterface.class)
            List<LocalTestInterface> l = new ArrayList<>();
        }
        class B {
            @ElementType(LocalTestAbstractClass.class)
            List<LocalTestAbstractClass> l = new ArrayList<>();
        }
        class C {
            @ElementType(int.class)
            List<LocalTestAbstractClass> l = new ArrayList<>();
        }
        class D {
            @ElementType(TestSubClass[].class)
            List<TestSubClass[]> l = new ArrayList<>();
        }
        class E {
            @ElementType(LocalTestEnum.class)
            List<LocalTestEnum> l = new ArrayList<>();
        }
        Map<String, Object> m = mapOf("l", listOf());

        String msg = "The element type of field 'l' must be a concrete class " +
                "but type 'LocalTestInterface' is an interface.";
        assertItmCfgExceptionMessage(new A(), msg);
        assertIfmCfgExceptionMessage(new A(), m, msg);

        msg = "The element type of field 'l' must be a concrete class " +
                "but type 'LocalTestAbstractClass' is an abstract class.";
        assertItmCfgExceptionMessage(new B(), msg);
        assertIfmCfgExceptionMessage(new B(), m, msg);

        msg = "The element type 'int' of field 'l' is not a configuration element.";
        assertItmCfgExceptionMessage(new C(), msg);
        assertIfmCfgExceptionMessage(new C(), m, msg);

        msg = "The element type 'TestSubClass[]' of field 'l' is " +
                "not a configuration element.";
        assertItmCfgExceptionMessage(new D(), msg);
        assertIfmCfgExceptionMessage(new D(), m, msg);
    }

    @Test
    void instanceToMapRequiresConfigurationElementsToHaveNoArgsConstructors() {
        @ConfigurationElement
        class Sub {
            Sub(int n) {}
        }

        class A {
            Sub s = new Sub(2);
        }

        String msg = "Type 'Sub' of field 's' doesn't have a no-args constructor.";
        assertItmCfgExceptionMessage(new A(), msg);
    }

    @Test
    void instanceToMapRequiresElementTypesToBeConfigurationElements() {
        class A {
            @ElementType(String.class)
            List<String> l = new ArrayList<>();
        }
        String msg = "The element type 'String' of field 'l' is not a " +
                "configuration element.";
        assertItmCfgExceptionMessage(new A(), msg);
    }

    @Test
    void instanceToMapRequiresElementTypesToHaveNoArgsConstructors() {
        class A {
            @ElementType(Sub3.class)
            List<Sub3> list = new ArrayList<>();
        }
        String msg = "The element type 'Sub3' of field 'list' " +
                "doesn't have a no-args constructor.";
        assertItmCfgExceptionMessage(new A(), msg);
    }

    @Test
    void instanceToAndFromMapRequireFieldsWithElementTypeToBeContainers() {
        class A {
            @ElementType(String.class)
            String s = "";
        }
        String msg = "Field 's' is annotated with the ElementType annotation but " +
                "is not a List, Set or Map.";
        assertItmCfgExceptionMessage(new A(), msg);
        assertIfmCfgExceptionMessage(new A(), mapOf("s", ""), msg);
    }

    @Test
    void instanceFromMapsRequiresElementTypeToBeEnumType() {
        class A {
            @ElementType(value = TestSubClass.class, nestingLevel = 1)
            List<List<TestSubClass>> l = listOf();
        }
        Map<String, Object> map = mapOf(
                "l", listOf(listOf("Q", "V"))
        );
        ConfigurationException ex = assertIfmThrowsCfgException(new A(), map);
        Throwable cause = ex.getCause();

        String msg = "Element type 'TestSubClass' of field 'l' is not an enum type.";
        assertThat(cause.getMessage(), is(msg));
    }

    @Test
    void instanceFromMapElementConverterRequiresObjectsOfTypeMapStringObject() {
        class A {
            @ElementType(value = TestSubClass.class, nestingLevel = 1)
            List<List<TestSubClass>> l = listOf();
        }
        Map<String, Object> map = mapOf(
                "l", listOf(listOf(1, 2))
        );
        String msg = "Field 'l' of class 'A' has a nesting level of 1 but element " +
                "'1' of type 'Integer' cannot be converted to 'TestSubClass'.";
        assertIfmCfgExceptionMessage(new A(), map, msg);
    }

    @Test
    void instanceToMapRequiresCorrectNestingLevelForLists() {
        TestSubClass testValues = TestSubClass.TEST_VALUES;
        class A {
            @ElementType(TestSubClass.class)
            List<List<TestSubClass>> l1 = listOf();

            @ElementType(TestSubClass.class)
            List<List<TestSubClass>> l2 = listOf(listOf(testValues));
        }
        class B {
            @ElementType(value = TestSubClass.class, nestingLevel = 1)
            List<List<List<TestSubClass>>> l = listOf(listOf(listOf(testValues)));
        }
        class C {
            @ElementType(value = TestSubClass.class, nestingLevel = 3)
            List<List<List<TestSubClass>>> l = listOf(listOf(listOf(testValues)));
        }
        class D {
            @ElementType(value = TestSubClass.class, nestingLevel = 1)
            List<List<TestSubClass>> l = listOf(listOf(
                    TestSubClass.of(11, "11"), TestSubClass.of(12, "12"),
                    TestSubClass.of(13, "13"), TestSubClass.of(14, "14")
            ));
        }

        String msg = "Field 'l2' of class 'A' has a nesting level of 0 but the " +
                "first object of type 'TestSubClass' was found on level 1.";
        assertItmCfgExceptionMessage(new A(), msg);

        msg = "Field 'l' of class 'B' has a nesting level of 1 but the " +
                "first object of type 'TestSubClass' was found on level 2.";
        assertItmCfgExceptionMessage(new B(), msg);

        msg = "Field 'l' of class 'C' has a nesting level of 3 but the " +
                "first object of type 'TestSubClass' was found on level 2.";
        assertItmCfgExceptionMessage(new C(), msg);

        Map<String, Object> map = instanceToMap(new D());
        D d = instanceFromMap(new D(), map);
        assertThat(d.l, is(new D().l));
    }


    @Test
    void instanceToMapRequiresCorrectNestingLevelForMaps() {
        TestSubClass testValues = TestSubClass.TEST_VALUES;
        class A {
            @ElementType(TestSubClass.class)
            Map<String, Map<String, TestSubClass>> m1 = mapOf();

            @ElementType(TestSubClass.class)
            Map<Integer, Map<String, TestSubClass>> m2 = mapOf(
                    1, mapOf("1", TestSubClass.of(11, "11")),
                    2, mapOf("2", TestSubClass.of(12, "12"))
            );
        }
        class B {
            @ElementType(value = TestSubClass.class, nestingLevel = 1)
            Map<String, Map<String, Map<String, TestSubClass>>> m = mapOf(
                    "1", mapOf("2", mapOf("3", testValues)),
                    "1", mapOf("2", mapOf("3", testValues))
            );
        }
        class C {
            @ElementType(value = TestSubClass.class, nestingLevel = 3)
            Map<String, Map<String, Map<String, TestSubClass>>> m = mapOf(
                    "1", mapOf("2", mapOf("3", testValues)),
                    "1", mapOf("2", mapOf("3", testValues))
            );
        }
        class D {
            @ElementType(value = TestSubClass.class, nestingLevel = 1)
            Map<Integer, Map<String, TestSubClass>> m = mapOf(
                    1, mapOf("1", TestSubClass.of(11, "11")),
                    2, mapOf("2", TestSubClass.of(12, "12")),
                    3, mapOf("3", TestSubClass.of(13, "13")),
                    4, mapOf("4", TestSubClass.of(14, "14"))
            );
        }

        String msg = "Field 'm2' of class 'A' has a nesting level of 0 but the " +
                "first object of type 'TestSubClass' was found on level 1.";
        assertItmCfgExceptionMessage(new A(), msg);

        msg = "Field 'm' of class 'B' has a nesting level of 1 but the " +
                "first object of type 'TestSubClass' was found on level 2.";
        assertItmCfgExceptionMessage(new B(), msg);

        msg = "Field 'm' of class 'C' has a nesting level of 3 but the " +
                "first object of type 'TestSubClass' was found on level 2.";
        assertItmCfgExceptionMessage(new C(), msg);

        Map<String, Object> map = instanceToMap(new D());
        D d = instanceFromMap(new D(), map);
        assertThat(d.m, is(new D().m));
    }

    /* The case that the nestingLevel is set to high cannot properly be detected. */
    @Test
    void instanceFromMapRequiresCorrectNestingLevelForLists() {
        class A {
            @ElementType(TestSubClass.class)
            List<TestSubClass> l = listOf();
        }
        class B {
            @ElementType(LocalTestEnum.class)
            List<LocalTestEnum> l = listOf();
        }
        class C {
            @ElementType(TestSubClass.class)
            List<List<TestSubClass>> l = listOf();
        }
        class D {
            @ElementType(LocalTestEnum.class)
            List<List<LocalTestEnum>> l = listOf();
        }
        Map<String, Object> m = TestSubClass.TEST_VALUES.asMap();
        instanceFromMap(new A(), mapOf("l", listOf(m)));
        instanceFromMap(new B(), mapOf("l", listOf("S", "T")));

        String elementAsString = m.toString();
        String msg = "Field 'l' of class 'C' has a nesting level of 0 but element '[" +
                elementAsString + "]' of type 'ArrayList' cannot be converted " +
                "to 'TestSubClass'.";
        assertIfmCfgExceptionMessage(new C(), mapOf("l", listOf(listOf(m))), msg);

        msg = "Field 'l' of class 'D' has a nesting level of 0 but element '[S, T]' of type " +
                "'ArrayList' cannot be converted to 'LocalTestEnum'.";
        assertIfmCfgExceptionMessage(new D(), mapOf("l", listOf(listOf("S", "T"))), msg);
    }
}
