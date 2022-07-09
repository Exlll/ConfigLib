package de.exlll.configlib;

import de.exlll.configlib.Serializers.StringSerializer;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

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
        assertThat(properties.getSerializersByCondition().entrySet(), empty());
        assertThat(properties.getFieldFormatter(), is(FieldFormatters.IDENTITY));
        assertThat(properties.getFieldFilter(), is(FieldFilters.DEFAULT));
    }

    @Test
    void builderCopiesValues() {
        FieldFormatter formatter = field -> field.getName().toLowerCase(Locale.ROOT);
        FieldFilter filter = field -> field.getName().startsWith("f");
        TestUtils.PointSerializer serializer = new TestUtils.PointSerializer();
        Predicate<? super Type> predicate = type -> true;

        ConfigurationProperties properties = ConfigurationProperties.newBuilder()
                .addSerializer(Point.class, serializer)
                .addSerializerByCondition(predicate, serializer)
                .setFieldFormatter(formatter)
                .setFieldFilter(filter)
                .outputNulls(true)
                .inputNulls(true)
                .serializeSetsAsLists(false)
                .build();

        assertThat(properties.getSerializers(), is(Map.of(Point.class, serializer)));
        assertThat(properties.getSerializersByCondition(), is(Map.of(predicate, serializer)));
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
        Predicate<? super Type> predicate = type -> true;

        ConfigurationProperties properties = ConfigurationProperties.newBuilder()
                .addSerializer(Point.class, serializer)
                .addSerializerByCondition(predicate, serializer)
                .setFieldFormatter(formatter)
                .setFieldFilter(filter)
                .outputNulls(true)
                .inputNulls(true)
                .serializeSetsAsLists(false)
                .build()
                .toBuilder()
                .build();

        assertThat(properties.getSerializers(), is(Map.of(Point.class, serializer)));
        assertThat(properties.getSerializersByCondition(), is(Map.of(predicate, serializer)));
        assertThat(properties.outputNulls(), is(true));
        assertThat(properties.inputNulls(), is(true));
        assertThat(properties.serializeSetsAsLists(), is(false));
        assertThat(properties.getFieldFormatter(), sameInstance(formatter));
        assertThat(properties.getFieldFilter(), sameInstance(filter));
    }

    @Test
    void builderSerializersUnmodifiable() {
        ConfigurationProperties properties = ConfigurationProperties.newBuilder().build();
        var serializersByType = properties.getSerializers();
        var serializersByCondition = properties.getSerializersByCondition();

        assertThrows(
                UnsupportedOperationException.class,
                () -> serializersByType.put(Point.class, new TestUtils.PointSerializer())
        );
        assertThrows(
                UnsupportedOperationException.class,
                () -> serializersByCondition.put(t -> true, new TestUtils.PointSerializer())
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
        void addSerializerByTypeRequiresNonNull() {
            assertThrowsNullPointerException(
                    () -> builder.addSerializer(null, new StringSerializer()),
                    "serialized type"
            );

            assertThrowsNullPointerException(
                    () -> builder.addSerializer(String.class, null),
                    "serializer"
            );
        }

        @Test
        void addSerializerByConditionRequiresNonNull() {
            assertThrowsNullPointerException(
                    () -> builder.addSerializerByCondition(null, new StringSerializer()),
                    "condition"
            );

            assertThrowsNullPointerException(
                    () -> builder.addSerializerByCondition(type -> true, null),
                    "serializer"
            );
        }
    }
}