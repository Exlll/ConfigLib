package de.exlll.configlib;

import de.exlll.configlib.annotation.Convert;
import de.exlll.configlib.annotation.ElementType;
import de.exlll.configlib.classes.TestSubClass;
import de.exlll.configlib.classes.TestSubClassConverter;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.exlll.configlib.FieldMapperHelpers.*;
import static de.exlll.configlib.util.CollectionFactory.listOf;
import static de.exlll.configlib.util.CollectionFactory.mapOf;
import static de.exlll.configlib.util.CollectionFactory.setOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings({"unused", "ThrowableNotThrown"})
public class FieldMapperConverterTest {

    private static class Point2D {
        protected int x = 1;
        protected int y = 2;

        private Point2D() {}

        protected Point2D(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private static Point2D of(int x, int y) {
            return new Point2D(x, y);
        }

    }

    private static final class PointToListConverter
            implements Converter<Point2D, List<Integer>> {
        @Override
        public List<Integer> convertTo(Point2D element, ConversionInfo info) {
            return listOf(element.x, element.y);
        }

        @Override
        public Point2D convertFrom(List<Integer> element, ConversionInfo info) {
            Point2D point = new Point2D();
            point.x = element.get(0);
            point.y = element.get(1);
            return point;
        }
    }

    private static final class PointToMapConverter
            implements Converter<Point2D, Map<String, String>> {
        @Override
        public Map<String, String> convertTo(Point2D element, ConversionInfo info) {
            int x = element.x;
            int y = element.y;
            return mapOf("p", x + ":" + y);
        }

        @Override
        public Point2D convertFrom(Map<String, String> element, ConversionInfo info) {
            String p = element.get("p");
            String[] split = p.split(":");
            return Point2D.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
        }
    }

    private static final class IntToStringConverter
            implements Converter<Integer, String> {

        @Override
        public String convertTo(Integer element, ConversionInfo info) {
            return element.toString();
        }

        @Override
        public Integer convertFrom(String element, ConversionInfo info) {
            return Integer.valueOf(element);
        }
    }

    @Test
    void instanceToMapRequiresNoArgsConverter() {
        class A {
            @Convert(MultiArgsConverter.class)
            int i;
        }
        class B {
            @Convert(SubConverter.class)
            int i;
        }
        class C {
            @Convert(EnumConverter.class)
            int i;
        }
        String msg = "Converter 'MultiArgsConverter' used on field 'i' doesn't " +
                "have a no-args constructor.";
        assertItmCfgExceptionMessage(new A(), msg);
        msg = "Converter 'SubConverter' used on field 'i' doesn't " +
                "have a no-args constructor.";
        assertItmCfgExceptionMessage(new B(), msg);
        msg = "Converter 'EnumConverter' used on field 'i' doesn't " +
                "have a no-args constructor.";
        assertItmCfgExceptionMessage(new C(), msg);
    }

    private static final class MultiArgsConverter
            implements Converter<Object, Object> {
        private MultiArgsConverter(int i) {}

        @Override
        public Object convertTo(Object element, ConversionInfo info) {
            return null;
        }

        @Override
        public Object convertFrom(Object element, ConversionInfo info) {
            return null;
        }
    }

    private static final class NullConverter
            implements Converter<String, String> {

        @Override
        public String convertTo(String element, ConversionInfo info) {
            return null;
        }

        @Override
        public String convertFrom(String element, ConversionInfo info) {
            return null;
        }
    }

    @Test
    void instanceToMapThrowsExceptionIfConverterReturnsNull() {
        class A {
            @Convert(NullConverter.class)
            String s = "string";
        }
        String msg = "Failed to convert value 'string' of field 's' because " +
                "the converter returned null.";
        assertItmCfgExceptionMessage(new A(), msg);
    }

    @Test
    void instanceFromMapKeepsDefaultValueIfConverterReturnsNull() {
        class A {
            @Convert(NullConverter.class)
            String s = "string";
        }
        Map<String, Object> map = mapOf("s", "value");
        A a = instanceFromMap(new A(), map);
        assertThat(a.s, is("string"));
    }

    private interface SubConverter extends Converter<Object, Object> {}

    private enum EnumConverter implements Converter<Object, Object> {
        ;

        @Override
        public Object convertTo(Object element, ConversionInfo info) {
            return null;
        }

        @Override
        public Object convertFrom(Object element, ConversionInfo info) {
            return null;
        }
    }

    @Test
    void instanceToMapUsesConverterForSimpleTypes() {
        class A {
            @Convert(IntToStringConverter.class)
            int i = 1;
        }
        Map<String, Object> map = instanceToMap(new A());
        assertThat(map.get("i"), is("1"));
    }

    @Test
    void instanceFromMapUsesConverterForSimpleTypes() {
        class A {
            @Convert(IntToStringConverter.class)
            int i = 1;
        }
        A i = instanceFromMap(new A(), mapOf("i", "10"));
        assertThat(i.i, is(10));
    }

    @Test
    void instanceToMapConvertsCustomTypesUsingConverters() {
        class A {
            @Convert(PointToListConverter.class)
            Point2D p1 = new Point2D();
            @Convert(PointToMapConverter.class)
            Point2D p2 = new Point2D();
        }
        Map<String, Object> map = instanceToMap(new A());
        assertThat(map.get("p1"), is(listOf(1, 2)));
        assertThat(map.get("p2"), is(mapOf("p", "1:2")));
    }

    @Test
    void instanceFromMapConvertsCustomTypesUsingConverters() {
        class A {
            @Convert(PointToListConverter.class)
            Point2D p1 = new Point2D();
            @Convert(PointToMapConverter.class)
            Point2D p2 = new Point2D();
        }
        Map<String, Object> map = mapOf(
                "p1", listOf(10, 11),
                "p2", mapOf("p", "11:12")
        );
        A i = instanceFromMap(new A(), map);
        assertThat(i.p1.x, is(10));
        assertThat(i.p1.y, is(11));

        assertThat(i.p2.x, is(11));
        assertThat(i.p2.y, is(12));
    }

    private static final class CountingConverter
            implements Converter<Object, Object> {
        static int instanceCount;

        public CountingConverter() {
            instanceCount++;
        }

        @Override
        public Object convertTo(Object element, ConversionInfo info) {
            return element;
        }

        @Override
        public Object convertFrom(Object element, ConversionInfo info) {
            return element;
        }
    }

    @Test
    void convertersUseCache() {
        class A {
            @Convert(CountingConverter.class)
            Point2D a = new Point2D();
            @Convert(CountingConverter.class)
            Point2D b = new Point2D();
        }
        Map<String, Object> map = instanceToMap(new A());
        assertThat(CountingConverter.instanceCount, is(1));
        instanceFromMap(new A(), map);
        assertThat(CountingConverter.instanceCount, is(1));
    }
    
    @Test
    void instanceToMapCatchesClassCastException() {
        class A {
            @Convert(TestSubClassConverter.class)
            String s = "string";
        }
        String msg = "Converter 'TestSubClassConverter' cannot convert value " +
                "'string' of field 's' because it expects a different type.";
        assertItmCfgExceptionMessage(new A(), msg);
    }

    @Test
    void instanceFromMapCatchesClassCastExceptionOfCustomClasses() {
        class A {
            @Convert(TestSubClassConverter.class)
            TestSubClass a = new TestSubClass();
        }
        Map<String, Object> map = mapOf(
                "a", 1
        );
        String msg = "The value for field 'a' with type 'TestSubClass' " +
                "cannot be converted back to its original representation because " +
                "a type mismatch occurred.";
        assertIfmCfgExceptionMessage(new A(), map, msg);
    }

    @Test
    void instanceFromMapCatchesClassCastExceptionOfChars() {
        class C {
            char c;
        }
        class D {
            char d;
        }
        Map<String, Object> map = mapOf(
                "c", "", "d", "12"
        );
        String msg = "The value for field 'c' with type 'char' " +
                "cannot be converted back to its original representation because " +
                "a type mismatch occurred.";
        assertIfmCfgExceptionMessage(new C(), map, msg);

        msg = "The value for field 'd' with type 'char' " +
                "cannot be converted back to its original representation because " +
                "a type mismatch occurred.";
        assertIfmCfgExceptionMessage(new D(), map, msg);
    }

    @Test
    void instanceFromMapCatchesClassCastExceptionOfStrings() {
        class B {
            String b = "string";
        }
        Map<String, Object> map = mapOf(
                "b", 2
        );
        String msg = "The value for field 'b' with type 'String' " +
                "cannot be converted back to its original representation because " +
                "a type mismatch occurred.";
        assertIfmCfgExceptionMessage(new B(), map, msg);
    }

    @Test
    void instanceFromMapCatchesClassCastExceptionOfUnknownEnumConstants() {
        class A {
            LocalTestEnum e = LocalTestEnum.T;
        }
        Map<String, Object> map = mapOf(
                "e", "V"
        );
        String msg = "The value for field 'e' with type 'LocalTestEnum' " +
                "cannot be converted back to its original representation because " +
                "a type mismatch occurred.";
        assertIfmCfgExceptionMessage(new A(), map, msg);
    }

    @Test
    void instanceFromMapCatchesClassCastExceptionOfUnknownEnumConstantsInLists() {
        class A {
            @ElementType(value = LocalTestEnum.class, nestingLevel = 1)
            List<List<LocalTestEnum>> l = listOf();
        }
        Map<String, Object> map = mapOf(
                "l", listOf(listOf("Q", "V"))
        );
        ConfigurationException ex = assertIfmThrowsCfgException(new A(), map);
        Throwable cause = ex.getCause();

        String msg = "Cannot initialize an enum element of list 'l' because there " +
                "is no enum constant 'Q'.\nValid constants are: [S, T]";
        MatcherAssert.assertThat(cause.getMessage(), is(msg));
    }

    @Test
    void instanceFromMapCatchesClassCastExceptionOfUnknownEnumConstantsInSets() {
        class A {
            @ElementType(value = LocalTestEnum.class, nestingLevel = 1)
            Set<List<LocalTestEnum>> s = setOf();
        }
        Map<String, Object> map = mapOf(
                "s", setOf(listOf("Q", "V"))
        );
        ConfigurationException ex = assertIfmThrowsCfgException(new A(), map);
        Throwable cause = ex.getCause();

        String msg = "Cannot initialize an enum element of set 's' because there " +
                "is no enum constant 'Q'.\nValid constants are: [S, T]";
        MatcherAssert.assertThat(cause.getMessage(), is(msg));
    }

    @Test
    void instanceFromMapCatchesClassCastExceptionOfUnknownEnumConstantsInMaps() {
        class A {
            @ElementType(value = LocalTestEnum.class, nestingLevel = 1)
            Map<Integer, List<LocalTestEnum>> m = mapOf();
        }
        Map<String, Object> map = mapOf(
                "m", mapOf(1, listOf("Q", "V"))
        );
        ConfigurationException ex = assertIfmThrowsCfgException(new A(), map);
        Throwable cause = ex.getCause();

        String msg = "Cannot initialize an enum element of map 'm' because there " +
                "is no enum constant 'Q'.\nValid constants are: [S, T]";
        MatcherAssert.assertThat(cause.getMessage(), is(msg));
    }
}
