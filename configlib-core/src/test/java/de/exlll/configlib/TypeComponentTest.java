package de.exlll.configlib;

import de.exlll.configlib.TypeComponent.ConfigurationField;
import de.exlll.configlib.TypeComponent.ConfigurationRecordComponent;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TypeComponentTest {

    static final class ConfigurationFieldTest {
        private static final Field FIELD = TestUtils.getField(C.class, "field");
        private static final ConfigurationField COMPONENT = new ConfigurationField(FIELD);

        static final class C {
            @Comment("")
            int field = 20;
        }

        @Test
        void componentName() {
            assertThat(COMPONENT.componentName(), is("field"));
        }

        @Test
        void componentType() {
            assertThat(COMPONENT.componentType(), equalTo(int.class));
        }

        @Test
        void componentValue() {
            assertThat(COMPONENT.componentValue(new C()), is(20));
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

        record R(@Comment("") float comp) {}

        @Test
        void componentName() {
            assertThat(COMPONENT.componentName(), is("comp"));
        }

        @Test
        void componentType() {
            assertThat(COMPONENT.componentType(), equalTo(float.class));
        }

        @Test
        void componentValue() {
            assertThat(COMPONENT.componentValue(new R(10f)), is(10f));
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
