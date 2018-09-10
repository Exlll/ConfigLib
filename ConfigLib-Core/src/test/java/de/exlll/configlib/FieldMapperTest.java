package de.exlll.configlib;

import de.exlll.configlib.Converter.ConversionInfo;
import de.exlll.configlib.FieldMapper.MappingInfo;
import de.exlll.configlib.annotation.ElementType;
import de.exlll.configlib.annotation.Format;
import de.exlll.configlib.annotation.NoConvert;
import de.exlll.configlib.classes.TestClass;
import de.exlll.configlib.classes.TestSubClass;
import de.exlll.configlib.classes.TestSubClassConverter;
import de.exlll.configlib.configs.mem.InSharedMemoryConfiguration;
import de.exlll.configlib.configs.yaml.YamlConfiguration.YamlProperties;
import de.exlll.configlib.format.FieldNameFormatter;
import de.exlll.configlib.format.FieldNameFormatters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static de.exlll.configlib.Converters.ENUM_CONVERTER;
import static de.exlll.configlib.Converters.SIMPLE_TYPE_CONVERTER;
import static de.exlll.configlib.FieldMapperHelpers.*;
import static de.exlll.configlib.util.CollectionFactory.*;
import static java.util.stream.Collectors.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"unused", "ThrowableNotThrown"})
class FieldMapperTest {
    private static final TestClass t = TestClass.TEST_VALUES;
    private static final Configuration.Properties DEFAULT =
            Configuration.Properties.builder().build();
    private static final YamlProperties WITH_UPPER_FORMATTER = YamlProperties
            .builder()
            .setFormatter(FieldNameFormatters.UPPER_UNDERSCORE)
            .build();
    private static final Map<String, Object> map = instanceToMap(
            TestClass.TEST_VALUES, DEFAULT
    );

    private TestClass tmp;

    @BeforeEach
    void setUp() {
        tmp = new TestClass();
        MappingInfo mappingInfo = MappingInfo.from(tmp);
        FieldMapper.instanceFromMap(tmp, map, mappingInfo);
    }

    @Test
    void instanceFromMapSetsSimpleTypes() {
        assertAll(
                () -> assertThat(tmp.getPrimBool(), is(t.getPrimBool())),
                () -> assertThat(tmp.getRefBool(), is(t.getRefBool())),
                () -> assertThat(tmp.getPrimByte(), is(t.getPrimByte())),
                () -> assertThat(tmp.getRefByte(), is(t.getRefByte())),
                () -> assertThat(tmp.getPrimChar(), is(t.getPrimChar())),
                () -> assertThat(tmp.getRefChar(), is(t.getRefChar())),
                () -> assertThat(tmp.getPrimShort(), is(t.getPrimShort())),
                () -> assertThat(tmp.getRefShort(), is(t.getRefShort())),
                () -> assertThat(tmp.getPrimInt(), is(t.getPrimInt())),
                () -> assertThat(tmp.getRefInt(), is(t.getRefInt())),
                () -> assertThat(tmp.getPrimLong(), is(t.getPrimLong())),
                () -> assertThat(tmp.getRefLong(), is(t.getRefLong())),
                () -> assertThat(tmp.getPrimFloat(), is(t.getPrimFloat())),
                () -> assertThat(tmp.getRefFloat(), is(t.getRefFloat())),
                () -> assertThat(tmp.getPrimDouble(), is(t.getPrimDouble())),
                () -> assertThat(tmp.getRefDouble(), is(t.getRefDouble())),
                () -> assertThat(tmp.getString(), is(t.getString()))
        );
    }

    @Test
    void instanceFromMapSetsEnums() {
        assertThat(tmp.getE1(), is(t.getE1()));
    }

    @Test
    void instanceFromMapSetsContainersOfSimpleTypes() {
        assertAll(
                () -> assertThat(tmp.getInts(), is(t.getInts())),
                () -> assertThat(tmp.getStrings(), is(t.getStrings())),
                () -> assertThat(tmp.getDoubleByBool(), is(t.getDoubleByBool()))
        );
    }

    @Test
    void instanceFromMapsConvertsMapsToTypes() {
        assertThat(tmp.getSubClass(), is(t.getSubClass()));
    }

    @Test
    void instanceFromMapsConvertsExcludedClasses() {
        assertThat(tmp.getExcludedClass(), is(t.getExcludedClass()));
    }

    @Test
    void instanceFromMapsConvertsContainersOfMaps() {
        assertThat(tmp.getSubClassList(), is(t.getSubClassList()));
        assertThat(tmp.getSubClassSet(), is(t.getSubClassSet()));
        assertThat(tmp.getSubClassMap(), is(t.getSubClassMap()));
        assertThat(tmp.getSubClassListsList(), is(t.getSubClassListsList()));
        assertThat(tmp.getSubClassSetsSet(), is(t.getSubClassSetsSet()));
        assertThat(tmp.getSubClassMapsMap(), is(t.getSubClassMapsMap()));
    }

    @Test
    void instanceFromMapDoesNotSetFinalStaticOrTransientFields() {
        Map<String, Object> map = mapOf(
                "staticFinalInt", 10,
                "staticInt", 10,
                "finalInt", 10,
                "transientInt", 10
        );
        TestClass cls = instanceFromMap(new TestClass(), map);
        assertThat(TestClass.getStaticFinalInt(), is(1));
        assertThat(TestClass.getStaticInt(), is(2));
        assertThat(cls.getFinalInt(), is(3));
        assertThat(cls.getTransientInt(), is(4));
    }

    @Test
    void instanceFromMapConvertsAllFields() {
        assertThat(tmp, is(t));
    }

    @Test
    void instanceFromMapThrowsExceptionIfEnumConstantDoesNotExist() {
        class A {
            LocalTestEnum t = LocalTestEnum.T;
        }
        Map<String, Object> map = mapOf(
                "t", "R"
        );

        String msg = "Cannot initialize enum 't' because there is no enum " +
                "constant 'R'.\nValid constants are: [S, T]";
        assertThrows(
                IllegalArgumentException.class,
                () -> ((Converter<Enum<?>, String>) ENUM_CONVERTER)
                        .convertFrom("R", newInfo("t", new A())),
                msg
        );
    }

    @Test
    void instanceFromMapIgnoresNullValues() {
        class A {
            TestSubClass c = new TestSubClass();
        }
        Map<String, Object> map = mapOf("c", mapOf("primInt", 20));

        A a = new A();
        assertThat(a.c.getPrimInt(), is(0));
        assertThat(a.c.getString(), is(""));
        instanceFromMap(a, map);
        assertThat(a.c.getPrimInt(), is(20));
        assertThat(a.c.getString(), is(""));
    }

    @Test
    void instanceFromMapSetsField() {
        TestClass ins = TestClass.TEST_VALUES;
        TestClass def = new TestClass();
        assertThat(ins, is(not(def)));
        instanceFromMap(def, map);
        assertThat(ins, is(def));
    }

    @Test
    void instanceFromMapCreatesConcreteInstances() {
        class A {
            LocalTestInterface l = new LocalTestInterfaceImpl();
        }
        class B {
            LocalTestAbstractClass l = new LocalTestAbstractClassImpl();
        }
        instanceFromMap(new A(), mapOf("l", mapOf()));
        instanceFromMap(new B(), mapOf("l", mapOf()));
    }


    @Test
    void instanceToMapUsesFieldNameFormatter() {
        Configuration.Properties.Builder builder =
                Configuration.Properties.builder();
        Map<String, Object> map = instanceToMap(
                TestClass.TEST_VALUES,
                builder.setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build()
        );
        assertThat(map.get("primBool"), nullValue());
        assertThat(map.get("prim_bool"), is(true));
    }

    @Test
    void instanceToMapContainsAllFields() {
        assertThat(map.size(), is(34));
    }

    @Test
    void instanceToMapDoesNotContainFinalStaticOrTransientFields() {
        assertAll(
                () -> assertThat(map.get("staticFinalInt"), is(nullValue())),
                () -> assertThat(map.get("staticInt"), is(nullValue())),
                () -> assertThat(map.get("finalInt"), is(nullValue())),
                () -> assertThat(map.get("transientInt"), is(nullValue()))
        );
    }

    @Test
    void instanceToMapContainsAllSimpleFields() {
        assertAll(
                () -> assertThat(map.get("primBool"), is(t.getPrimBool())),
                () -> assertThat(map.get("refBool"), is(t.getRefBool())),
                () -> assertThat(map.get("primByte"), is(t.getPrimByte())),
                () -> assertThat(map.get("refByte"), is(t.getRefByte())),
                () -> assertThat(map.get("primChar"), is(t.getPrimChar())),
                () -> assertThat(map.get("refChar"), is(t.getRefChar())),
                () -> assertThat(map.get("primShort"), is(t.getPrimShort())),
                () -> assertThat(map.get("refShort"), is(t.getRefShort())),
                () -> assertThat(map.get("primInt"), is(t.getPrimInt())),
                () -> assertThat(map.get("refInt"), is(t.getRefInt())),
                () -> assertThat(map.get("primLong"), is(t.getPrimLong())),
                () -> assertThat(map.get("refLong"), is(t.getRefLong())),
                () -> assertThat(map.get("primFloat"), is(t.getPrimFloat())),
                () -> assertThat(map.get("refFloat"), is(t.getRefFloat())),
                () -> assertThat(map.get("primDouble"), is(t.getPrimDouble())),
                () -> assertThat(map.get("refDouble"), is(t.getRefDouble())),
                () -> assertThat(map.get("string"), is(t.getString()))
        );
    }

    @Test
    void instanceToMapContainsAllContainersOfSimpleTypes() {
        assertAll(
                () -> assertThat(map.get("ints"), is(t.getInts())),
                () -> assertThat(map.get("strings"), is(t.getStrings())),
                () -> assertThat(map.get("doubleByBool"), is(t.getDoubleByBool()))
        );
    }

    @Test
    void instanceToMapConvertsTypesToMaps() {
        assertThat(map.get("subClass"), is(t.getSubClass().asMap()));
    }

    @Test
    void instanceToMapConvertsExcludedClasses() {
        assertThat(map.get("excludedClass"), is(t.getExcludedClass()));
    }

    @Test
    void instanceToMapConvertsEnumsContainersToStringContainers() {
        class A {
            @ElementType(LocalTestEnum.class)
            List<LocalTestEnum> el = listOf(LocalTestEnum.S, LocalTestEnum.T);
            @ElementType(LocalTestEnum.class)
            Set<LocalTestEnum> es = setOf(LocalTestEnum.S, LocalTestEnum.T);
            @ElementType(LocalTestEnum.class)
            Map<String, LocalTestEnum> em = mapOf(
                    "1", LocalTestEnum.S, "2", LocalTestEnum.T
            );
        }
        Map<String, Object> map = instanceToMap(new A());
        assertThat(map.get("el"), is(listOf("S", "T")));
        assertThat(map.get("es"), is(setOf("S", "T")));
        assertThat(map.get("em"), is(mapOf("1", "S", "2", "T")));
    }

    @Test
    void instanceFromMapConvertsStringContainersToEnumContainers() {
        class A {
            @ElementType(LocalTestEnum.class)
            List<LocalTestEnum> el = listOf();
            @ElementType(LocalTestEnum.class)
            Set<LocalTestEnum> es = setOf();
            @ElementType(LocalTestEnum.class)
            Map<String, LocalTestEnum> em = mapOf();
        }
        Map<String, Object> map = mapOf(
                "el", listOf("S", "T"),
                "es", setOf("S", "T"),
                "em", mapOf("1", "S", "2", "T")
        );
        A a = instanceFromMap(new A(), map);
        assertThat(a.el, is(listOf(LocalTestEnum.S, LocalTestEnum.T)));
        assertThat(a.es, is(setOf(LocalTestEnum.S, LocalTestEnum.T)));
        assertThat(a.em, is(mapOf(
                "1", LocalTestEnum.S, "2", LocalTestEnum.T
        )));
    }


    @Test
    void instanceToMapConvertsContainerElementsToMaps() {
        List<Map<String, Object>> subClassList = t.getSubClassList().stream()
                .map(TestSubClass::asMap)
                .collect(toList());
        Set<Map<String, Object>> subClassSet = t.getSubClassSet().stream()
                .map(TestSubClass::asMap)
                .collect(toSet());
        Map<String, Map<String, Object>> subClassMap = t.getSubClassMap()
                .entrySet().stream()
                .map(e -> mapEntry(e.getKey(), e.getValue().asMap()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertAll(
                () -> assertThat(map.get("subClassSet"), is(subClassSet)),
                () -> assertThat(map.get("subClassMap"), is(subClassMap)),
                () -> assertThat(map.get("subClassList"), is(subClassList))
        );
    }

    @Test
    void instanceToMapContainsNestedContainersOfSimpleTypes() {
        assertAll(
                () -> assertThat(map.get("listsList"), is(t.getListsList())),
                () -> assertThat(map.get("setsSet"), is(t.getSetsSet())),
                () -> assertThat(map.get("mapsMap"), is(t.getMapsMap()))
        );
    }

    @Test
    void instanceToMapContainsNestedContainersOfCustomTypes() {
        List<List<Map<String, Object>>> lists = t.getSubClassListsList()
                .stream().map(list -> list.stream()
                        .map(TestSubClass::asMap)
                        .collect(toList()))
                .collect(toList());
        Set<Set<Map<String, Object>>> sets = t.getSubClassSetsSet()
                .stream().map(set -> set.stream()
                        .map(TestSubClass::asMap)
                        .collect(toSet()))
                .collect(toSet());
        Function<Map<String, TestSubClass>, Map<String, Map<String, Object>>> f =
                map -> map.entrySet().stream().collect(
                        toMap(Map.Entry::getKey, e -> e.getValue().asMap())
                );
        Map<Integer, Map<String, Map<String, Object>>> m = t.getSubClassMapsMap()
                .entrySet().stream()
                .map(e -> mapEntry(e.getKey(), f.apply(e.getValue())))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertThat(map.get("subClassListsList"), is(lists));
        assertThat(map.get("subClassSetsSet"), is(sets));
        assertThat(map.get("subClassMapsMap"), is(m));
    }

    @Test
    void instanceToMapContainsEnums() {
        assertThat(map.get("e1"), is(t.getE1().toString()));
    }

    @Test
    void instanceToMapContainsEnumLists() {
        List<String> list = t.getEnums().stream()
                .map(Enum::toString)
                .collect(toList());
        assertThat(map.get("enums"), is(list));
    }


    @Test
    void instanceToMapConvertsConvertTypes() {
        String s = new TestSubClassConverter()
                .convertTo(t.getConverterSubClass(), null);
        assertThat(map.get("converterSubClass"), is(s));
    }

    @Test
    void instanceToMapCreatesLinkedHashMap() {
        assertThat(instanceToMap(new Object()), instanceOf(LinkedHashMap.class));
    }

    private static Converter<Object, Object> converter = SIMPLE_TYPE_CONVERTER;

    private static ConversionInfo newInfo(String fieldName, Object o) {
        Field field = null;
        try {
            field = o.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        MappingInfo mappingInfo = new MappingInfo(null, WITH_UPPER_FORMATTER);
        return ConversionInfo.from(field, o, mappingInfo);
    }

    private static ConversionInfo newInfo(String fieldName) {
        return newInfo(fieldName, new TestClass());
    }

    @Test
    void typeConverterReturnsInstanceIfClassesMatch() {
        //noinspection RedundantStringConstructorCall
        String s = new String("123");
        Object val = converter.convertFrom(s, newInfo("string"));
        assertThat(val, sameInstance(s));
    }

    @Test
    void typeConverterConvertsStringToCharacter() {
        String s = "1";
        Object vc = converter.convertFrom(s, newInfo("primChar"));
        Object vd = converter.convertFrom(s, newInfo("refChar"));

        assertThat(vc, instanceOf(Character.class));
        assertThat(vd, instanceOf(Character.class));
    }

    @Test
    void typeConverterChecksInvalidStrings() {
        String msg = "An empty string cannot be converted to a character.";
        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertFrom("", newInfo("refChar")),
                msg
        );

        String value = "123";
        msg = "String '" + value + "' is too long to be converted to a character";
        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertFrom(value, newInfo("refChar")),
                msg
        );
    }

    @Test
    void typeConverterConvertsNumbers() {
        Number[] numbers = {
                (byte) 1, (short) 2, 3, (long) 4,
                (float) 5, (double) 6, 4L, 5f, 6d
        };
        String[] classes = {
                "refByte", "refShort", "refInt",
                "primLong", "refFloat", "refDouble"
        };

        for (String cls : classes) {
            for (Number number : numbers) {
                ConversionInfo info = newInfo(cls);
                Object converted = converter.convertFrom(number, info);
                assertThat(converted, instanceOf(info.getFieldType()));
            }
        }
    }

    @Test
    void typeConverterChecksInvalidNumbers() {
        String msg = "Number '1' cannot be converted to type 'String'";
        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertFrom(1, newInfo("string")),
                msg
        );
    }

    private static final class ExcludedClass {}

    @Test
    void instanceToMapSetsAutoConvertedInstancesAsIs() {
        class A {
            @NoConvert
            ExcludedClass ex = new ExcludedClass();
        }
        A a = new A();
        Map<String, Object> map = instanceToMap(a);
        assertThat(map.get("ex"), sameInstance(a.ex));
    }

    @Test
    void instanceFromMapSetsAutoConvertedInstancesAsIs() {
        class A {
            @NoConvert
            ExcludedClass ex = new ExcludedClass();
        }

        ExcludedClass cls = new ExcludedClass();
        Map<String, Object> map = mapOf("ex", cls);
        A a = instanceFromMap(new A(), map);
        assertThat(a.ex, sameInstance(cls));
    }

    @Test
    void fieldMapperUsesFiltersAdded() {
        class A {
            private int a = 1;
            private int b = 2;
            private int c = 3;
            private transient int d = 4;
            private final int e = 5;
        }
        Configuration.Properties props = Configuration.Properties.builder()
                .addFilter(field -> !field.getName().equals("a"))
                .addFilter(field -> !field.getName().equals("c"))
                .build();

        Map<String, Object> map = instanceToMap(new A(), props);
        assertThat(map.size(), is(1));
        assertThat(map, is(mapOf("b", 2)));

        map = mapOf("a", -1, "b", -2, "c", -3, "d", -4, "e", -5);
        A a = instanceFromMap(new A(), map, props);
        assertThat(a.a, is(1));
        assertThat(a.b, is(-2));
        assertThat(a.c, is(3));
        assertThat(a.d, is(4));
        assertThat(a.e, is(5));
    }

    @Test
    void selectFieldNameFormatterReturnsPropertiesIfNoFormatAnnotation() {
        class A extends InSharedMemoryConfiguration {
            protected A() { super(WITH_UPPER_FORMATTER); }
        }
        A a = new A();
        FieldNameFormatter fnf = FieldMapper.selectFormatter(MappingInfo.from(a));
        assertThat(fnf, sameInstance(FieldNameFormatters.UPPER_UNDERSCORE));
    }

    @Test
    void selectFieldNameFormatterReturnsIfFormatAnnotation() {
        @Format(FieldNameFormatters.LOWER_UNDERSCORE)
        class A extends InSharedMemoryConfiguration {
            protected A() { super(WITH_UPPER_FORMATTER); }
        }
        @Format(formatterClass = MyFormatter.class)
        class B extends InSharedMemoryConfiguration {
            protected B() { super(WITH_UPPER_FORMATTER); }
        }
        A a = new A();
        FieldNameFormatter fnf = FieldMapper.selectFormatter(MappingInfo.from(a));
        assertThat(fnf, sameInstance(FieldNameFormatters.LOWER_UNDERSCORE));

        B b = new B();
        fnf = FieldMapper.selectFormatter(MappingInfo.from(b));
        assertThat(fnf, instanceOf(MyFormatter.class));
    }

    @Test
    void formatterClassTakesPrecedence() {
        @Format(
                value = FieldNameFormatters.LOWER_UNDERSCORE,
                formatterClass = MyFormatter.class
        )
        class A extends InSharedMemoryConfiguration {
            protected A() { super(WITH_UPPER_FORMATTER); }
        }
        A a = new A();
        FieldNameFormatter fnf = FieldMapper.selectFormatter(MappingInfo.from(a));
        assertThat(fnf, instanceOf(MyFormatter.class));
    }

    private static final class MyFormatter implements FieldNameFormatter {
        @Override
        public String fromFieldName(String fieldName) {
            return fieldName;
        }
    }

    @Test
    void fieldMapperUsesSelectFieldNameFormatter() {
        @Format(FieldNameFormatters.LOWER_UNDERSCORE)
        class A extends InSharedMemoryConfiguration {
            private int abc = 1;
            private int eFg = 2;
            private int hIJ = 3;

            protected A() { super(WITH_UPPER_FORMATTER); }
        }

        A a = new A();
        MappingInfo mappingInfo = MappingInfo.from(a);
        Map<String, Object> map = FieldMapper.instanceToMap(a, mappingInfo);

        assertThat(map.get("abc"), is(1));
        assertThat(map.get("e_fg"), is(2));
        assertThat(map.get("h_i_j"), is(3));

        map = mapOf("abc", 4, "e_fg", 5, "h_i_j", 6);
        FieldMapper.instanceFromMap(a, map, mappingInfo);
        assertThat(a.abc, is(4));
        assertThat(a.eFg, is(5));
        assertThat(a.hIJ, is(6));
    }
}