package de.exlll.configlib.configurations;

import de.exlll.configlib.*;
import de.exlll.configlib.TestUtils.ThrowingSerializer;

import java.util.*;

import static de.exlll.configlib.TestUtils.*;

@SuppressWarnings("FieldMayBeFinal")
@Configuration
public final class ExampleConfigurationCustom {
    /* ******************** @SerializeWith ON CONFIGURATION ELEMENTS ******************** */
    @SerializeWith(serializer = ThrowingSerializer.class, nesting = -1)
    private List<List<String>> listListString0 = asList(asList("1"));
    @SerializeWith(serializer = DoublingListSerializer.class)
    private List<List<String>> listListString1 = listListString0;
    @SerializeWith(serializer = DoublingListSerializer.class, nesting = 1)
    private List<List<String>> listListString2 = listListString0;
    @SerializeWith(serializer = DoublingStringSerializer.class, nesting = 2)
    private List<List<String>> listListString3 = listListString0;
    @SerializeWith(serializer = ThrowingSerializer.class, nesting = 3)
    private List<List<String>> listListString4 = listListString0;

    @SerializeWith(serializer = ThrowingSerializer.class, nesting = -1)
    private Map<Integer, Map<Integer, String>> mapIntegerMapIntegerString0 =
            asMap(1, asMap(2, "3"));
    @SerializeWith(serializer = DoublingKeySerializer.class)
    private Map<Integer, Map<Integer, String>> mapIntegerMapIntegerString1 =
            mapIntegerMapIntegerString0;
    @SerializeWith(serializer = DoublingKeySerializer.class, nesting = 1)
    private Map<Integer, Map<Integer, String>> mapIntegerMapIntegerString2 =
            mapIntegerMapIntegerString0;
    @SerializeWith(serializer = DoublingStringSerializer.class, nesting = 2)
    private Map<Integer, Map<Integer, String>> mapIntegerMapIntegerString3 =
            mapIntegerMapIntegerString0;
    @SerializeWith(serializer = ThrowingSerializer.class, nesting = 3)
    private Map<Integer, Map<Integer, String>> mapIntegerMapIntegerString4 =
            mapIntegerMapIntegerString0;

    /* ******************** POLYMORPHIC TYPES ******************** */

    private Poly1 poly1_1 = new Poly1Impl1(10);
    private Poly2 poly2_1 = new Poly2Impl1(10);
    private Poly3 poly3_1 = new Poly3Impl1();
    private Poly4 poly4 = new Poly4();
    private Poly4 poly4_1 = new Poly4Impl1();
    private Poly5 poly5 = new Poly5();

    private List<Set<Poly1>> listSetPoly1 = asList(asSet(poly1_1), asSet(poly1_1, new Poly1Impl2("s")));
    private List<Set<Poly2>> listSetPoly2 = asList(asSet(poly2_1), asSet(poly2_1, new Poly2Impl2("s")));
    private List<Set<Poly3>> listSetPoly3 = asList(asSet(poly3_1), asSet(poly3_1, new Poly3Impl2()));
    private List<Set<Poly4>> listSetPoly4 = asList(asSet(poly4), asSet(poly4_1, new Poly4Impl2()));
    private List<Set<Poly5>> listSetPoly5 = asList(asSet(poly5), asSet(poly5));

    /* ******************** POLYMORPHIC TYPE DEFINITIONS ******************** */

    @Polymorphic
    interface Poly1 {}

    record Poly1Impl1(int i) implements Poly1 {}

    record Poly1Impl2(String s) implements Poly1 {}


    @Polymorphic(property = "==")
    @PolymorphicTypes({
            @PolymorphicTypes.Type(type = Poly2Impl1.class, alias = "POLY2_IMPL1"),
            @PolymorphicTypes.Type(type = Poly2Impl2.class, alias = "POLY2_IMPL2")
    })
    interface Poly2 {}

    record Poly2Impl1(int i) implements Poly2 {}

    record Poly2Impl2(String s) implements Poly2 {}


    @Polymorphic
    @Configuration
    static abstract class Poly3 {
        double d = 20;
    }

    static final class Poly3Impl1 extends Poly3 {
        int i = 10;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Poly3Impl1 that = (Poly3Impl1) o;
            return i == that.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    static final class Poly3Impl2 extends Poly3 {
        String s = "s";

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Poly3Impl2 that = (Poly3Impl2) o;
            return Objects.equals(s, that.s);
        }

        @Override
        public int hashCode() {
            return s != null ? s.hashCode() : 0;
        }
    }

    @Polymorphic
    @Configuration
    static class Poly4 {
        double d = 20;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Poly4 poly4 = (Poly4) o;
            return Double.compare(poly4.d, d) == 0;
        }

        @Override
        public int hashCode() {
            long temp = Double.doubleToLongBits(d);
            return (int) (temp ^ (temp >>> 32));
        }
    }

    static final class Poly4Impl1 extends Poly4 {
        int i = 10;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            Poly4Impl1 that = (Poly4Impl1) o;
            return i == that.i;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + i;
            return result;
        }
    }

    static final class Poly4Impl2 extends Poly4 {
        String s = "s";

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            Poly4Impl2 that = (Poly4Impl2) o;
            return Objects.equals(s, that.s);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (s != null ? s.hashCode() : 0);
            return result;
        }
    }

    @Polymorphic
    @Configuration
    static final class Poly5 {
        double d = 20;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Poly5 poly5 = (Poly5) o;
            return Double.compare(poly5.d, d) == 0;
        }

        @Override
        public int hashCode() {
            long temp = Double.doubleToLongBits(d);
            return (int) (temp ^ (temp >>> 32));
        }
    }

    /* ******************** CUSTOM SERIALIZERS ******************** */
    public static final class DoublingKeySerializer<V>
            implements Serializer<Map<Integer, V>, Map<Integer, V>> {

        public DoublingKeySerializer(SerializerContext context) {
            if (context.element().type() != Map.class) {
                throw new RuntimeException(context.element().type().getName());
            }
        }

        @Override
        public Map<Integer, V> serialize(Map<Integer, V> element) {
            Map<Integer, V> result = new LinkedHashMap<>();
            element.forEach((integer, v) -> result.put(integer * 2, v));
            return result;
        }

        @Override
        public Map<Integer, V> deserialize(Map<Integer, V> element) {
            Map<Integer, V> result = new LinkedHashMap<>();
            element.forEach((integer, v) -> result.put(integer / 2, v));
            return result;
        }
    }

    public static final class DoublingListSerializer<T>
            implements Serializer<List<T>, List<T>> {

        public DoublingListSerializer(SerializerContext context) {
            if (context.element().type() != List.class) {
                throw new RuntimeException(context.element().type().getName());
            }
        }

        @Override
        public List<T> serialize(List<T> element) {
            List<T> result = new ArrayList<>(element);
            result.addAll(element);
            return result;
        }

        @Override
        public List<T> deserialize(List<T> element) {
            return element.subList(0, element.size() / 2);
        }
    }

    public static final class DoublingStringSerializer
            implements Serializer<String, String> {
        @Override
        public String serialize(String element) {
            return element + element;
        }

        @Override
        public String deserialize(String element) {
            return element.substring(0, element.length() / 2);
        }
    }

    public List<List<String>> getListListString0() {
        return listListString0;
    }

    public List<List<String>> getListListString1() {
        return listListString1;
    }

    public List<List<String>> getListListString2() {
        return listListString2;
    }

    public List<List<String>> getListListString3() {
        return listListString3;
    }

    public List<List<String>> getListListString4() {
        return listListString4;
    }

    public Map<Integer, Map<Integer, String>> getMapIntegerMapIntegerString0() {
        return mapIntegerMapIntegerString0;
    }

    public Map<Integer, Map<Integer, String>> getMapIntegerMapIntegerString1() {
        return mapIntegerMapIntegerString1;
    }

    public Map<Integer, Map<Integer, String>> getMapIntegerMapIntegerString2() {
        return mapIntegerMapIntegerString2;
    }

    public Map<Integer, Map<Integer, String>> getMapIntegerMapIntegerString3() {
        return mapIntegerMapIntegerString3;
    }

    public Map<Integer, Map<Integer, String>> getMapIntegerMapIntegerString4() {
        return mapIntegerMapIntegerString4;
    }

    public Poly1 getPoly1_1() {
        return poly1_1;
    }

    public Poly2 getPoly2_1() {
        return poly2_1;
    }

    public Poly3 getPoly3_1() {
        return poly3_1;
    }

    public Poly4 getPoly4() {
        return poly4;
    }

    public Poly4 getPoly4_1() {
        return poly4_1;
    }

    public Poly5 getPoly5() {
        return poly5;
    }

    public List<Set<Poly1>> getListSetPoly1() {
        return listSetPoly1;
    }

    public List<Set<Poly2>> getListSetPoly2() {
        return listSetPoly2;
    }

    public List<Set<Poly3>> getListSetPoly3() {
        return listSetPoly3;
    }

    public List<Set<Poly4>> getListSetPoly4() {
        return listSetPoly4;
    }

    public List<Set<Poly5>> getListSetPoly5() {
        return listSetPoly5;
    }
}
