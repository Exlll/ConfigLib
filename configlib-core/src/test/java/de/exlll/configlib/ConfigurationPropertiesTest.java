package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Locale;
import java.util.Map;

import static de.exlll.configlib.TestUtils.assertThrowsNullPointerException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationPropertiesTest {

    @Test
    void builderDefaultValues() {
        ConfigurationProperties properties = ConfigurationProperties.newBuilder().build();

        assertThat(properties.serializeSetsAsLists(), is(true));
        assertThat(properties.outputNulls(), is(false));
        assertThat(properties.inputNulls(), is(false));
        assertThat(properties.getSerializers().entrySet(), empty());
        assertThat(properties.getFieldFormatter(), is(FieldFormatters.IDENTITY));
        assertThat(properties.getFieldFilter(), is(FieldFilters.DEFAULT));
    }

    @Test
    void builderCopiesValues() {
        FieldFormatter formatter = field -> field.getName().toLowerCase(Locale.ROOT);
        FieldFilter filter = field -> field.getName().startsWith("f");
        TestUtils.PointSerializer serializer = new TestUtils.PointSerializer();

        ConfigurationProperties properties = ConfigurationProperties.newBuilder()
                .addSerializer(Point.class, serializer)
                .setFieldFormatter(formatter)
                .setFieldFilter(filter)
                .outputNulls(true)
                .inputNulls(true)
                .serializeSetsAsLists(false)
                .build();

        assertThat(properties.getSerializers(), is(Map.of(Point.class, serializer)));
        assertThat(properties.outputNulls(), is(true));
        assertThat(properties.inputNulls(), is(true));
        assertThat(properties.serializeSetsAsLists(), is(false));
        assertThat(properties.getFieldFormatter(), sameInstance(formatter));
        assertThat(properties.getFieldFilter(), sameInstance(filter));
    }

    @Test
    void builderCtorCopiesValues() {
        FieldFormatter formatter = field -> field.getName().toLowerCase(Locale.ROOT);
        FieldFilter filter = field -> field.getName().startsWith("f");
        TestUtils.PointSerializer serializer = new TestUtils.PointSerializer();

        ConfigurationProperties properties = ConfigurationProperties.newBuilder()
                .addSerializer(Point.class, serializer)
                .setFieldFormatter(formatter)
                .setFieldFilter(filter)
                .outputNulls(true)
                .inputNulls(true)
                .serializeSetsAsLists(false)
                .build()
                .toBuilder()
                .build();

        assertThat(properties.getSerializers(), is(Map.of(Point.class, serializer)));
        assertThat(properties.outputNulls(), is(true));
        assertThat(properties.inputNulls(), is(true));
        assertThat(properties.serializeSetsAsLists(), is(false));
        assertThat(properties.getFieldFormatter(), sameInstance(formatter));
        assertThat(properties.getFieldFilter(), sameInstance(filter));
    }

    @Test
    void builderSerializersUnmodifiable() {
        ConfigurationProperties properties = ConfigurationProperties.newBuilder().build();
        Map<Class<?>, Serializer<?, ?>> map = properties.getSerializers();

        assertThrows(
                UnsupportedOperationException.class,
                () -> map.put(Point.class, new TestUtils.PointSerializer())
        );
    }

    public static final class BuilderTest {
        private static final ConfigurationProperties.Builder<?> builder = ConfigurationProperties.newBuilder();

        @Test
        void setFieldFilterRequiresNonNull() {
            assertThrowsNullPointerException(
                    () -> builder.setFieldFilter(null),
                    "field filter"
            );
        }

        @Test
        void setFieldFormatterRequiresNonNull() {
            assertThrowsNullPointerException(
                    () -> builder.setFieldFormatter(null),
                    "field formatter"
            );
        }

        @Test
        void addSerializerRequiresNonNull() {
            assertThrowsNullPointerException(
                    () -> builder.addSerializer(null, new Serializers.StringSerializer()),
                    "serialized type"
            );

            assertThrowsNullPointerException(
                    () -> builder.addSerializer(String.class, null),
                    "serializer"
            );
        }
    }
}