package de.exlll.configlib;

import de.exlll.configlib.Serializers.ListSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.exlll.configlib.Polymorphic.DEFAULT_PROPERTY;
import static de.exlll.configlib.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class PolymorphicSerializerTest {
    private static final ConfigurationProperties PROPERTIES = ConfigurationProperties.newBuilder().build();
    private static final SerializerSelector SELECTOR = new SerializerSelector(PROPERTIES);

    @Test
    void defaultPropertyNameIsType() {
        assertThat(DEFAULT_PROPERTY, is("type"));
    }

    @Polymorphic
    @Configuration
    static final class A {
        String type = "";
    }
    @Configuration
    @Polymorphic(property = "prop")
    static final class B {
        String prop = "";
    }

    @Test
    void serializeDoesNotAllowConfigurationElementWithSameNameAsProperty() {
        record Config(A a, B b, List<A> as) {}

        var serializerA = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a"));
        var serializerB = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "b"));
        @SuppressWarnings("unchecked")
        var serializerAs = (ListSerializer<A, Object>)
                SELECTOR.select(fieldAsElement(Config.class, "as"));

        String msg = "Polymorphic serialization for type '%s' failed. " +
                     "The type contains a configuration element with name '%s' but that name is " +
                     "used by the @Polymorphic property.";

        assertThrowsConfigurationException(
                () -> serializerA.serialize(new A()),
                msg.formatted(A.class.getName(), "type")
        );

        assertThrowsConfigurationException(
                () -> serializerB.serialize(new B()),
                msg.formatted(B.class.getName(), "prop")
        );

        assertThrowsConfigurationException(
                () -> serializerAs.serialize(List.of(new A())),
                msg.formatted(A.class.getName(), "type")
        );
    }

    @Test
    void deserializeMissingTypeFails() {
        @Polymorphic
        interface A {}
        record R() implements A {}
        record Config(A a) {}

        var invalidClassName = R.class.getName() + "_INVALID";
        var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a"));
        assertThrowsConfigurationException(
                () -> serializer.deserialize(Map.of(DEFAULT_PROPERTY, invalidClassName)),
                ("Polymorphic deserialization for type '%s' failed. " +
                 "The class '%s' does not exist.")
                        .formatted(A.class.getName(), invalidClassName)
        );
    }

    static final class PolymorphicTypesTest {
        @Polymorphic
        @PolymorphicTypes({
                @PolymorphicTypes.Type(type = Impl1.class, alias = "IMPL_1"),
                @PolymorphicTypes.Type(type = Impl2.class, alias = "IMPL_2"),
                @PolymorphicTypes.Type(type = Impl3.class, alias = " "), // blank alias
                /* @PolymorphicTypes.Type(type = Impl4.class) */         // missing
        })
        interface A {}

        record Impl1(int i) implements A {}

        record Impl2(double d) implements A {}

        record Impl3(String s) implements A {}

        record Impl4(long l) implements A {}

        record Config(List<A> as) {}

        @Test
        void serializeUsesTypeAliasIfPresent() {
            @SuppressWarnings("unchecked")
            var serializer = (ListSerializer<A, ?>)
                    SELECTOR.select(fieldAsElement(Config.class, "as"));
            List<?> serialized = serializer.serialize(List.of(
                    new Impl1(1),
                    new Impl2(2d),
                    new Impl3("3"),
                    new Impl4(4)
            ));
            assertThat(serialized, is(List.of(
                    asMap("type", "IMPL_1", "i", 1L),
                    asMap("type", "IMPL_2", "d", 2d),
                    asMap("type", Impl3.class.getName(), "s", "3"),
                    asMap("type", Impl4.class.getName(), "l", 4L)
            )));
        }

        @Test
        void deserializeUsesTypeAliasIfPresent() {
            @SuppressWarnings("unchecked")
            var serializer = (ListSerializer<?, Map<?, ?>>)
                    SELECTOR.select(fieldAsElement(Config.class, "as"));
            List<?> serialized = serializer.deserialize(List.of(
                    asMap("type", "IMPL_1", "i", 1L),
                    asMap("type", "IMPL_2", "d", 2d),
                    asMap("type", Impl3.class.getName(), "s", "3"),
                    asMap("type", Impl4.class.getName(), "l", 4L)
            ));
            assertThat(serialized.size(), is(4));
            assertThat(serialized.get(0), is(new Impl1(1)));
            assertThat(serialized.get(1), is(new Impl2(2)));
            assertThat(serialized.get(2), is(new Impl3("3")));
            assertThat(serialized.get(3), is(new Impl4(4)));
        }

        @Test
        void typesMustNotAppearMoreThanOnce() {
            @Polymorphic
            @PolymorphicTypes({
                    @PolymorphicTypes.Type(type = Impl1.class, alias = "1a"),
                    @PolymorphicTypes.Type(type = Impl1.class, alias = "1b")
            })
            interface E {}
            record Config(E e) {}

            RuntimeException exception = Assertions.assertThrows(
                    RuntimeException.class,
                    () -> SELECTOR.select(fieldAsElement(Config.class, "e"))
            );
            ConfigurationException configurationException = (ConfigurationException)
                    exception.getCause().getCause();
            Assertions.assertEquals(
                    "The @PolymorphicTypes annotation must not contain multiple definitions for " +
                    "the same subtype. Type '%s' appears more than once."
                            .formatted(Impl1.class.getName()),
                    configurationException.getMessage()
            );
        }

        @Test
        void aliasesMustNotAppearMoreThanOnce() {
            @Polymorphic
            @PolymorphicTypes({
                    @PolymorphicTypes.Type(type = Impl1.class, alias = "2"),
                    @PolymorphicTypes.Type(type = Impl2.class, alias = "2")
            })
            interface E {}
            record Config(E e) {}

            RuntimeException exception = Assertions.assertThrows(
                    RuntimeException.class,
                    () -> SELECTOR.select(fieldAsElement(Config.class, "e"))
            );
            ConfigurationException configurationException = (ConfigurationException)
                    exception.getCause().getCause();
            Assertions.assertEquals(
                    "The @PolymorphicTypes annotation must not use the same alias for multiple " +
                    "types. Alias '2' appears more than once.",
                    configurationException.getMessage()
            );
        }
    }

    static final class PolymorphicSerializerPropertyTest {
        private static final String CUSTOM_PROPERTY = DEFAULT_PROPERTY + DEFAULT_PROPERTY;

        @Polymorphic(property = CUSTOM_PROPERTY)
        interface A {}

        @Polymorphic(property = "")
        interface B {}

        record R(int i) implements A {}

        record Config(A a, B b, List<A> as, List<B> bs) {}

        @Test
        void requirePropertyNameNonBlank() {
            RuntimeException exception = Assertions.assertThrows(
                    RuntimeException.class,
                    () -> SELECTOR.select(fieldAsElement(Config.class, "b"))
            );
            ConfigurationException configurationException = (ConfigurationException)
                    exception.getCause().getCause();
            Assertions.assertEquals(
                    "The @Polymorphic annotation does not allow a blank property name but " +
                    "type '%s' uses one.".formatted(B.class.getName()),
                    configurationException.getMessage()
            );
        }

        @Test
        void requirePropertyNameNonBlankNested() {
            RuntimeException exception = Assertions.assertThrows(
                    RuntimeException.class,
                    () -> SELECTOR.select(fieldAsElement(Config.class, "bs"))
            );
            ConfigurationException configurationException = (ConfigurationException)
                    exception.getCause().getCause();
            Assertions.assertEquals(
                    "The @Polymorphic annotation does not allow a blank property name but " +
                    "type '%s' uses one.".formatted(B.class.getName()),
                    configurationException.getMessage()
            );
        }

        @Test
        void serializeUsesPropertyName() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a"));
            Map<?, ?> serialize = serializer.serialize(new R(10));
            assertThat(serialize, is(Map.of(CUSTOM_PROPERTY, R.class.getName(), "i", 10L)));
        }

        @Test
        void deserializeUsesPropertyName() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a"));
            R deserialize = (R) serializer.deserialize(Map.of(
                    CUSTOM_PROPERTY, R.class.getName(),
                    "i", 20L
            ));
            assertThat(deserialize, is(new R(20)));
        }

        @Test
        void deserializeThrowsExceptionIfPropertyMissing() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a"));
            var serialized = Map.of("i", 20L);
            assertThrowsConfigurationException(
                    () -> serializer.deserialize(serialized),
                    ("Polymorphic deserialization for type '%s' failed. The property '%s' which " +
                     "holds the type is missing. Value to be deserialized:\n%s")
                            .formatted(A.class.getName(), CUSTOM_PROPERTY, serialized)
            );
        }

        @Test
        void deserializeThrowsExceptionIfPropertyMissingNested() {
            @SuppressWarnings("unchecked")
            var serializer = (ListSerializer<?, Map<?, ?>>)
                    SELECTOR.select(fieldAsElement(Config.class, "as"));
            var serialized = Map.of("i", 20L);
            assertThrowsConfigurationException(
                    () -> serializer.deserialize(List.of(serialized)),
                    ("Polymorphic deserialization for type '%s' failed. The property '%s' which " +
                     "holds the type is missing. Value to be deserialized:\n%s")
                            .formatted(A.class.getName(), CUSTOM_PROPERTY, serialized)
            );
        }

        @Test
        void deserializeThrowsExceptionIfPropertyHasWrongType() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a"));
            var serialized = Map.of(CUSTOM_PROPERTY, 1, "i", 20L);
            assertThrowsConfigurationException(
                    () -> serializer.deserialize(serialized),
                    ("Polymorphic deserialization for type '%s' failed. The type identifier '1' " +
                     "which should hold the type is not a string but of type 'java.lang.Integer'.")
                            .formatted(A.class.getName())
            );
        }

        @Test
        void deserializeThrowsExceptionIfPropertyHasWrongTypeNested() {
            @SuppressWarnings("unchecked")
            var serializer = (ListSerializer<?, Map<?, ?>>)
                    SELECTOR.select(fieldAsElement(Config.class, "as"));
            var serialized = Map.of(CUSTOM_PROPERTY, 1, "i", 20L);
            assertThrowsConfigurationException(
                    () -> serializer.deserialize(List.of(serialized)),
                    ("Polymorphic deserialization for type '%s' failed. The type identifier '1' " +
                     "which should hold the type is not a string but of type 'java.lang.Integer'.")
                            .formatted(A.class.getName())
            );
        }
    }


    static final class PolymorphicInterfaceSerializerTest {
        @Polymorphic
        interface A {}

        @Configuration
        static final class Impl1 implements A {
            int i = 10;
        }

        record Impl2(double d) implements A {}

        static final class Config {
            A a1 = new Impl1();
            A a2 = new Impl2(20d);
            List<A> as = List.of(a1, a2);
        }

        static final Config CONFIG = new Config();

        @Test
        void getPolymorphicType() {
            var serializer1 = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a1"));
            var serializer2 = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a2"));
            assertThat(serializer1.getPolymorphicType(), equalTo(A.class));
            assertThat(serializer2.getPolymorphicType(), equalTo(A.class));
        }

        @Test
        void serializeA1() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a1"));
            Map<?, ?> serialize = serializer.serialize(CONFIG.a1);
            assertThat(serialize, is(asMap(
                    DEFAULT_PROPERTY, Impl1.class.getName(),
                    "i", 10L
            )));
        }

        @Test
        void deserializeA1() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a1"));
            Impl1 deserialize = (Impl1) serializer.deserialize(asMap(
                    DEFAULT_PROPERTY, Impl1.class.getName(),
                    "i", 20L
            ));
            assertThat(deserialize.i, is(20));
        }

        @Test
        void serializeA2() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a2"));
            Map<?, ?> serialize = serializer.serialize(CONFIG.a2);
            assertThat(serialize, is(asMap(
                    DEFAULT_PROPERTY, Impl2.class.getName(),
                    "d", 20d
            )));
        }

        @Test
        void deserializeA2() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a2"));
            Impl2 deserialize = (Impl2) serializer.deserialize(asMap(
                    DEFAULT_PROPERTY, Impl2.class.getName(),
                    "d", 30d
            ));
            assertThat(deserialize.d, is(30d));
        }

        @Test
        void serializeAs() {
            @SuppressWarnings("unchecked")
            var serializer = (ListSerializer<A, Object>)
                    SELECTOR.select(fieldAsElement(Config.class, "as"));
            List<?> serialize = serializer.serialize(CONFIG.as);
            assertThat(serialize, is(List.of(
                    asMap(DEFAULT_PROPERTY, Impl1.class.getName(), "i", 10L),
                    asMap(DEFAULT_PROPERTY, Impl2.class.getName(), "d", 20d)
            )));
        }

        @Test
        void deserializeAs() {
            @SuppressWarnings("unchecked")
            ListSerializer<A, Map<?, ?>> serializer = (ListSerializer<A, Map<?, ?>>)
                    SELECTOR.select(fieldAsElement(Config.class, "as"));
            List<A> deserialize = serializer.deserialize(List.of(
                    asMap(DEFAULT_PROPERTY, Impl1.class.getName(), "i", 20L),
                    asMap(DEFAULT_PROPERTY, Impl2.class.getName(), "d", 30d)
            ));
            assertThat(deserialize.size(), is(2));

            Impl1 actual1 = (Impl1) deserialize.get(0);
            Impl2 actual2 = (Impl2) deserialize.get(1);
            assertThat(actual1.i, is(20));
            assertThat(actual2, is(new Impl2(30)));
        }
    }

    static final class PolymorphicAbstractClassSerializerTest {
        @Polymorphic
        @Configuration
        static abstract class A {
            String s1 = "s1";
        }

        static class B extends A {
            String s2 = "s2";
        }

        static final class Impl1 extends A {
            int i = 10;
        }

        static final class Impl2 extends B {
            double d = 20d;
        }

        static final class Config {
            A a1 = new Impl1();
            A a2 = new Impl2();
            List<A> as = List.of(a1, a2);
        }

        static final Config CONFIG = new Config();

        @Test
        void getPolymorphicType() {
            var serializer1 = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a1"));
            var serializer2 = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a2"));
            assertThat(serializer1.getPolymorphicType(), equalTo(A.class));
            assertThat(serializer2.getPolymorphicType(), equalTo(A.class));
        }

        @Test
        void serializeA1() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a1"));
            Map<?, ?> serialize = serializer.serialize(CONFIG.a1);
            assertThat(serialize, is(asMap(
                    DEFAULT_PROPERTY, Impl1.class.getName(),
                    "s1", "s1",
                    "i", 10L
            )));
        }

        @Test
        void deserializeA1() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a1"));
            Impl1 deserialize = (Impl1) serializer.deserialize(asMap(
                    DEFAULT_PROPERTY, Impl1.class.getName(),
                    "s1", "sa",
                    "i", 20L
            ));
            assertThat(deserialize.i, is(20));
            assertThat(deserialize.s1, is("sa"));
        }

        @Test
        void serializeA2() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a2"));
            Map<?, ?> serialize = serializer.serialize(CONFIG.a2);
            assertThat(serialize, is(asMap(
                    DEFAULT_PROPERTY, Impl2.class.getName(),
                    "s1", "s1",
                    "s2", "s2",
                    "d", 20d
            )));
        }

        @Test
        void deserializeA2() {
            var serializer = (PolymorphicSerializer) SELECTOR.select(fieldAsElement(Config.class, "a2"));
            Impl2 deserialize = (Impl2) serializer.deserialize(asMap(
                    DEFAULT_PROPERTY, Impl2.class.getName(),
                    "s1", "sa",
                    "s2", "sb",
                    "d", 30d
            ));
            assertThat(deserialize.d, is(30d));
            assertThat(deserialize.s1, is("sa"));
            assertThat(deserialize.s2, is("sb"));
        }

        @Test
        void serializeAs() {
            @SuppressWarnings("unchecked")
            ListSerializer<A, ?> serializer = (ListSerializer<A, Object>)
                    SELECTOR.select(fieldAsElement(Config.class, "as"));
            List<?> serialize = serializer.serialize(CONFIG.as);
            assertThat(serialize, is(List.of(
                    asMap(DEFAULT_PROPERTY, Impl1.class.getName(), "s1", "s1", "i", 10L),
                    asMap(DEFAULT_PROPERTY, Impl2.class.getName(), "s1", "s1", "s2", "s2", "d", 20d)
            )));
        }

        @Test
        void deserializeAs() {
            @SuppressWarnings("unchecked")
            ListSerializer<A, Map<?, ?>> serializer = (ListSerializer<A, Map<?, ?>>)
                    SELECTOR.select(fieldAsElement(Config.class, "as"));
            List<A> deserialize = serializer.deserialize(List.of(
                    asMap(DEFAULT_PROPERTY, Impl1.class.getName(), "s1", "sa", "i", 20L),
                    asMap(DEFAULT_PROPERTY, Impl2.class.getName(), "s1", "sa", "s2", "sb", "d", 30d)
            ));
            assertThat(deserialize.size(), is(2));

            Impl1 actual1 = (Impl1) deserialize.get(0);
            assertThat(actual1.i, is(20));
            assertThat(actual1.s1, is("sa"));

            Impl2 actual2 = (Impl2) deserialize.get(1);
            assertThat(actual2.d, is(30d));
            assertThat(actual2.s1, is("sa"));
            assertThat(actual2.s2, is("sb"));
        }
    }
}