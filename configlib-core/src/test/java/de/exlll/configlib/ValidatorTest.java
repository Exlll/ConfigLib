package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import static de.exlll.configlib.TestUtils.*;
import static de.exlll.configlib.Validator.requireTargetType;

class ValidatorTest {

    @Test
    void isOfTargetTypeSimple() {
        enum E {E;}
        record R(Long l) {}

        requireTargetType(null);
        requireTargetType(1L);
        requireTargetType(1.0);
        requireTargetType("");

        assertThrowsConfigurationException(
                () -> requireTargetType(1),
                "Object '1' does not have a valid target type. Its type is: class java.lang.Integer"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(1.0f),
                "Object '1.0' does not have a valid target type. Its type is: class java.lang.Float"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(E.E),
                "Object 'E' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$1E"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(new R(1L)),
                "Object 'R[l=1]' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$1R"
        );
        assertThrowsConfigurationException(() -> requireTargetType(new Object()));
    }

    @Test
    void isOfTargetTypeList() {
        enum E {E;}
        record R(Long l) {}

        requireTargetType(asList());
        requireTargetType(asList((Object) null));
        requireTargetType(asList(1L));
        requireTargetType(asList(1.0));
        requireTargetType(asList(""));

        assertThrowsConfigurationException(
                () -> requireTargetType(asList(1)),
                "Object '1' does not have a valid target type. Its type is: class java.lang.Integer"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asList(1.0f)),
                "Object '1.0' does not have a valid target type. Its type is: class java.lang.Float"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asList(E.E)),
                "Object 'E' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$2E"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asList(new R(1L))),
                "Object 'R[l=1]' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$2R"
        );
        assertThrowsConfigurationException(() -> requireTargetType(asList(new Object())));

        requireTargetType(asList(asList()));
        requireTargetType(asList(asList((Object) null)));
        requireTargetType(asList(asList(1L)));
        requireTargetType(asList(asList(1.0)));
        requireTargetType(asList(asList("")));

        assertThrowsConfigurationException(
                () -> requireTargetType(asList(asList(1))),
                "Object '1' does not have a valid target type. Its type is: class java.lang.Integer"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asList(asList(1.0f))),
                "Object '1.0' does not have a valid target type. Its type is: class java.lang.Float"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asList(asList(E.E))),
                "Object 'E' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$2E"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asList(asList(new R(1L)))),
                "Object 'R[l=1]' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$2R"
        );
        assertThrowsConfigurationException(() -> requireTargetType(asList(asList(new Object()))));
    }

    @Test
    void isOfTargetTypeMap() {
        enum E {E;}
        record R(Long l) {}

        requireTargetType(asMap());

        requireTargetType(asMap("key", null));
        requireTargetType(asMap("key", 1L));
        requireTargetType(asMap("key", 1.0));
        requireTargetType(asMap("key", ""));

        assertThrowsConfigurationException(
                () -> requireTargetType(asMap("key", 1)),
                "Object '1' does not have a valid target type. Its type is: class java.lang.Integer"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap("key", 1.0f)),
                "Object '1.0' does not have a valid target type. Its type is: class java.lang.Float"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap("key", E.E)),
                "Object 'E' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$3E"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap("key", new R(1L))),
                "Object 'R[l=1]' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$3R"
        );
        assertThrowsConfigurationException(() -> requireTargetType(asMap("key", new Object())));

        requireTargetType(asMap(null, "val"));
        requireTargetType(asMap(1L, "val"));
        requireTargetType(asMap(1.0, "val"));
        requireTargetType(asMap("", "val"));

        assertThrowsConfigurationException(
                () -> requireTargetType(asMap(1, "val")),
                "Object '1' does not have a valid target type. Its type is: class java.lang.Integer"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap(1.0f, "val")),
                "Object '1.0' does not have a valid target type. Its type is: class java.lang.Float"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap(E.E, "val")),
                "Object 'E' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$3E"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap(new R(1L), "val")),
                "Object 'R[l=1]' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$3R"
        );
        assertThrowsConfigurationException(() -> requireTargetType(asMap(new Object(), "val")));


        requireTargetType(asMap("key", asMap("key", null)));
        requireTargetType(asMap("key", asMap("key", 1L)));
        requireTargetType(asMap("key", asMap("key", 1.0)));
        requireTargetType(asMap("key", asMap("key", "")));

        assertThrowsConfigurationException(
                () -> requireTargetType(asMap("key", asMap("key", 1))),
                "Object '1' does not have a valid target type. Its type is: class java.lang.Integer"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap("key", asMap("key", 1.0f))),
                "Object '1.0' does not have a valid target type. Its type is: class java.lang.Float"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap("key", asMap("key", E.E))),
                "Object 'E' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$3E"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap("key", asMap("key", new R(1L)))),
                "Object 'R[l=1]' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$3R"
        );
        assertThrowsConfigurationException(() -> requireTargetType(asMap("key", asMap("key", new Object()))));

        requireTargetType(asMap(asMap(null, "val"), "val"));
        requireTargetType(asMap(asMap(1L, "val"), "val"));
        requireTargetType(asMap(asMap(1.0, "val"), "val"));
        requireTargetType(asMap(asMap("", "val"), "val"));

        assertThrowsConfigurationException(
                () -> requireTargetType(asMap(asMap(1, "val"), "val")),
                "Object '1' does not have a valid target type. Its type is: class java.lang.Integer"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap(asMap(1.0f, "val"), "val")),
                "Object '1.0' does not have a valid target type. Its type is: class java.lang.Float"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap(asMap(E.E, "val"), "val")),
                "Object 'E' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$3E"
        );
        assertThrowsConfigurationException(
                () -> requireTargetType(asMap(asMap(new R(1L), "val"), "val")),
                "Object 'R[l=1]' does not have a valid target type. Its type is: class de.exlll.configlib.ValidatorTest$3R"
        );
        assertThrowsConfigurationException(() -> requireTargetType(asMap(asMap(new Object(), "val"), "val")));
    }
}