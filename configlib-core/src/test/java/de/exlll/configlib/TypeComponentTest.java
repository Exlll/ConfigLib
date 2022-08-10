package de.exlll.configlib;

import de.exlll.configlib.TypeComponent.ConfigurationField;
import de.exlll.configlib.TypeComponent.ConfigurationRecordComponent;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TypeComponentTest {

    static final class ConfigurationFieldTest {
        private static final Field FIELD = TestUtils.getField(C.class, "field");
        private static final ConfigurationField COMPONENT = new ConfigurationField(FIELD);

        static final class C {
            @Comment("")
            List<String> field = List.of("20");
        }

        @Test
        void componentName() {
            assertThat(COMPONENT.name(), is("field"));
        }

        @Test
        void componentType() {
            assertThat(COMPONENT.type(), equalTo(List.class));
        }

        @Test
        void componentAnnotatedType() {
            assertThat(COMPONENT.annotatedType(), equalTo(FIELD.getAnnotatedType()));
        }

        @Test
        void componentValue() {
            assertThat(COMPONENT.value(new C()), is(List.of("20")));
        }

        @Test
        void declaringType() {
            assertThat(COMPONENT.declaringType(), equalTo(C.class));
        }

        @Test
        void annotation() {
            assertThat(COMPONENT.annotation(Comment.class), notNullValue());
        }
    }

    static final class ConfigurationRecordComponentTest {
        private static final RecordComponent RECORD_COMPONENT = R.class.getRecordComponents()[0];
        private static final ConfigurationRecordComponent COMPONENT =
                new ConfigurationRecordComponent(RECORD_COMPONENT);

        record R(@Comment("") Set<Integer> comp) {}

        @Test
        void componentName() {
            assertThat(COMPONENT.name(), is("comp"));
        }

        @Test
        void componentType() {
            assertThat(COMPONENT.type(), equalTo(Set.class));
        }

        @Test
        void componentAnnotatedType() {
            assertThat(COMPONENT.annotatedType(), equalTo(RECORD_COMPONENT.getAnnotatedType()));
        }

        @Test
        void componentValue() {
            assertThat(COMPONENT.value(new R(Set.of(1))), is(Set.of(1)));
        }

        @Test
        void declaringType() {
            assertThat(COMPONENT.declaringType(), equalTo(R.class));
        }

        @Test
        void annotation() {
            assertThat(COMPONENT.annotation(Comment.class), notNullValue());
        }
    }
}
