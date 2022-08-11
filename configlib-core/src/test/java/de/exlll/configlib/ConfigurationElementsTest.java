package de.exlll.configlib;

import de.exlll.configlib.ConfigurationElements.FieldElement;
import de.exlll.configlib.ConfigurationElements.RecordComponentElement;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ConfigurationElementsTest {

    static final class FieldElementTest {
        private static final Field FIELD = C.class.getDeclaredFields()[0];
        private static final FieldElement ELEMENT = new FieldElement(FIELD);

        static final class C {
            @Comment("")
            List<String> field = List.of("20");
        }

        @Test
        void elementName() {
            assertThat(ELEMENT.name(), is("field"));
        }

        @Test
        void elementType() {
            assertThat(ELEMENT.type(), equalTo(List.class));
        }

        @Test
        void elementAnnotatedType() {
            assertThat(ELEMENT.annotatedType(), is(FIELD.getAnnotatedType()));
        }

        @Test
        void elementValue() {
            assertThat(ELEMENT.value(new C()), is(List.of("20")));
        }

        @Test
        void declaringType() {
            assertThat(ELEMENT.declaringType(), equalTo(C.class));
        }

        @Test
        void annotation() {
            assertThat(ELEMENT.annotation(Comment.class), notNullValue());
        }

    }

    static final class RecordComponentElementTest {
        private static final RecordComponent RECORD_COMPONENT = R.class.getRecordComponents()[0];
        private static final RecordComponentElement ELEMENT =
                new RecordComponentElement(RECORD_COMPONENT);

        record R(@Comment("") Set<Integer> comp) {}

        @Test
        void elementName() {
            assertThat(ELEMENT.name(), is("comp"));
        }

        @Test
        void elementType() {
            assertThat(ELEMENT.type(), equalTo(Set.class));
        }

        @Test
        void elementAnnotatedType() {
            assertThat(ELEMENT.annotatedType(), is(RECORD_COMPONENT.getAnnotatedType()));
        }

        @Test
        void elementValue() {
            assertThat(ELEMENT.value(new R(Set.of(1))), is(Set.of(1)));
        }

        @Test
        void declaringType() {
            assertThat(ELEMENT.declaringType(), equalTo(R.class));
        }

        @Test
        void annotation() {
            assertThat(ELEMENT.annotation(Comment.class), notNullValue());
        }
    }
}