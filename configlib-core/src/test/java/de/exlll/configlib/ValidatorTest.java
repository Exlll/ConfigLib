package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import static de.exlll.configlib.TestUtils.*;
import static de.exlll.configlib.Validator.requireTargetTypeRet;

class ValidatorTest {

    @Test
    void isOfTargetTypeSimple() {
        enum E {E;}
        record R(Long l) {}

        requireTargetTypeRet(null);
        requireTargetTypeRet(1L);
        requireTargetTypeRet(1.0);
        requireTargetTypeRet("");

        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(1),
                "Value '1' must be null or of a valid target type but its type is java.lang.Integer."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(1.0f),
                "Value '1.0' must be null or of a valid target type but its type is java.lang.Float."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(E.E),
                "Value 'E' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$1E."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(new R(1L)),
                "Value 'R[l=1]' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$1R."
        );
        assertThrowsConfigurationException(() -> requireTargetTypeRet(new Object()));
    }

    @Test
    void isOfTargetTypeList() {
        enum E {E;}
        record R(Long l) {}

        requireTargetTypeRet(asList());
        requireTargetTypeRet(asList((Object) null));
        requireTargetTypeRet(asList(1L));
        requireTargetTypeRet(asList(1.0));
        requireTargetTypeRet(asList(""));

        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asList(1)),
                "Value '1' must be null or of a valid target type but its type is java.lang.Integer."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asList(1.0f)),
                "Value '1.0' must be null or of a valid target type but its type is java.lang.Float."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asList(E.E)),
                "Value 'E' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$2E."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asList(new R(1L))),
                "Value 'R[l=1]' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$2R."
        );
        assertThrowsConfigurationException(() -> requireTargetTypeRet(asList(new Object())));

        requireTargetTypeRet(asList(asList()));
        requireTargetTypeRet(asList(asList((Object) null)));
        requireTargetTypeRet(asList(asList(1L)));
        requireTargetTypeRet(asList(asList(1.0)));
        requireTargetTypeRet(asList(asList("")));

        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asList(asList(1))),
                "Value '1' must be null or of a valid target type but its type is java.lang.Integer."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asList(asList(1.0f))),
                "Value '1.0' must be null or of a valid target type but its type is java.lang.Float."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asList(asList(E.E))),
                "Value 'E' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$2E."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asList(asList(new R(1L)))),
                "Value 'R[l=1]' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$2R."
        );
        assertThrowsConfigurationException(() -> requireTargetTypeRet(asList(asList(new Object()))));
    }

    @Test
    void isOfTargetTypeMap() {
        enum E {E;}
        record R(Long l) {}

        requireTargetTypeRet(asMap());

        requireTargetTypeRet(asMap("key", null));
        requireTargetTypeRet(asMap("key", 1L));
        requireTargetTypeRet(asMap("key", 1.0));
        requireTargetTypeRet(asMap("key", ""));

        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap("key", 1)),
                "Value '1' must be null or of a valid target type but its type is java.lang.Integer."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap("key", 1.0f)),
                "Value '1.0' must be null or of a valid target type but its type is java.lang.Float."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap("key", E.E)),
                "Value 'E' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$3E."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap("key", new R(1L))),
                "Value 'R[l=1]' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$3R."
        );
        assertThrowsConfigurationException(() -> requireTargetTypeRet(asMap("key", new Object())));

        requireTargetTypeRet(asMap(null, "val"));
        requireTargetTypeRet(asMap(1L, "val"));
        requireTargetTypeRet(asMap(1.0, "val"));
        requireTargetTypeRet(asMap("", "val"));

        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap(1, "val")),
                "Value '1' must be null or of a valid target type but its type is java.lang.Integer."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap(1.0f, "val")),
                "Value '1.0' must be null or of a valid target type but its type is java.lang.Float."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap(E.E, "val")),
                "Value 'E' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$3E."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap(new R(1L), "val")),
                "Value 'R[l=1]' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$3R."
        );
        assertThrowsConfigurationException(() -> requireTargetTypeRet(asMap(new Object(), "val")));


        requireTargetTypeRet(asMap("key", asMap("key", null)));
        requireTargetTypeRet(asMap("key", asMap("key", 1L)));
        requireTargetTypeRet(asMap("key", asMap("key", 1.0)));
        requireTargetTypeRet(asMap("key", asMap("key", "")));

        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap("key", asMap("key", 1))),
                "Value '1' must be null or of a valid target type but its type is java.lang.Integer."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap("key", asMap("key", 1.0f))),
                "Value '1.0' must be null or of a valid target type but its type is java.lang.Float."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap("key", asMap("key", E.E))),
                "Value 'E' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$3E."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap("key", asMap("key", new R(1L)))),
                "Value 'R[l=1]' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$3R."
        );
        assertThrowsConfigurationException(() -> requireTargetTypeRet(asMap("key", asMap("key", new Object()))));

        requireTargetTypeRet(asMap(asMap(null, "val"), "val"));
        requireTargetTypeRet(asMap(asMap(1L, "val"), "val"));
        requireTargetTypeRet(asMap(asMap(1.0, "val"), "val"));
        requireTargetTypeRet(asMap(asMap("", "val"), "val"));

        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap(asMap(1, "val"), "val")),
                "Value '1' must be null or of a valid target type but its type is java.lang.Integer."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap(asMap(1.0f, "val"), "val")),
                "Value '1.0' must be null or of a valid target type but its type is java.lang.Float."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap(asMap(E.E, "val"), "val")),
                "Value 'E' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$3E."
        );
        assertThrowsConfigurationException(
                () -> requireTargetTypeRet(asMap(asMap(new R(1L), "val"), "val")),
                "Value 'R[l=1]' must be null or of a valid target type but its type is de.exlll.configlib.ValidatorTest$3R."
        );
        assertThrowsConfigurationException(() -> requireTargetTypeRet(asMap(asMap(new Object(), "val"), "val")));
    }
}