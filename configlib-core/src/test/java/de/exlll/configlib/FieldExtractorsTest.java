package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static de.exlll.configlib.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FieldExtractorsTest {
    @Test
    void extractConfigurationRequiresNonNull() {
        assertThrowsNullPointerException(
                () -> FieldExtractors.CONFIGURATION.extract(null),
                "configuration class"
        );
    }

    @Test
    void extractConfigurationRequiresConfiguration() {
        assertThrowsConfigurationException(
                () -> FieldExtractors.CONFIGURATION.extract(getClass()),
                "Class 'FieldExtractorsTest' must be a configuration."
        );
    }

    @Test
    void extractConfigurationDirectly() {
        @Configuration
        class A {
            int i;
        }

        FieldExtractor extractor = FieldExtractors.CONFIGURATION;

        List<Field> actual = extractor.extract(A.class).toList();
        List<Field> expected = List.of(
                getField(A.class, "i")
        );
        assertThat(actual, is(expected));
    }

    @Test
    void extractConfigurationIndirectly() {
        @Configuration
        class A {
            int i;
        }
        class B extends A {
            int j;
        }
        class C extends B {
            int k;
        }

        FieldExtractor extractor = FieldExtractors.CONFIGURATION;

        List<Field> actual = extractor.extract(C.class).toList();
        List<Field> expected = List.of(
                getField(A.class, "i"),
                getField(B.class, "j"),
                getField(C.class, "k")
        );
        assertThat(actual, is(expected));
    }

    @Test
    void extractConfigurationRequiresNoShadowing() {
        @Configuration
        class A {
            int i;
        }
        class B extends A {
            int i;
        }
        assertThrowsConfigurationException(
                () -> FieldExtractors.CONFIGURATION.extract(B.class),
                "Shadowing of fields is not supported. Field 'i' of class B " +
                "shadows field 'i' of class A."
        );
    }

    @Test
    void extractConfigurationAppliesDefaultFieldFilter() {
        class A {
            @Configuration
            class B {
                private final int i = 1;
                @Ignore
                private int j;
                private static int k;
                private transient int l;
                private int m;
            }
        }
        List<Field> actual = FieldExtractors.CONFIGURATION.extract(A.B.class).toList();
        List<Field> expected = List.of(getField(A.B.class, "m"));
        assertThat(actual, is(expected));
    }
}