package de.exlll.configlib;

import de.exlll.configlib.Serializers.StringSerializer;
import de.exlll.configlib.TestUtils.PointSerializer;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Predicate;

import static de.exlll.configlib.TestUtils.assertThrowsNullPointerException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationPropertiesTest {
    private static final NameFormatter FORMATTER = String::toLowerCase;
    private static final FieldFilter FILTER = field -> field.getName().startsWith("f");
    private static final PointSerializer SERIALIZER = new PointSerializer();
    private static final Predicate<? super Type> PREDICATE = type -> true;
    private static final ConfigurationProperties.Builder<?> BUILDER = ConfigurationProperties.newBuilder()
            .addSerializer(Point.class, SERIALIZER)
            .addSerializerFactory(Point.class, ignored -> SERIALIZER)
            .addSerializerByCondition(PREDICATE, SERIALIZER)
            .setNameFormatter(FORMATTER)
            .setFieldFilter(FILTER)
            .outputNulls(true)
            .inputNulls(true)
            .serializeSetsAsLists(false);

    @Test
    void builderDefaultValues() {
        ConfigurationProperties properties = ConfigurationProperties.newBuilder().build();

        assertThat(properties.serializeSetsAsLists(), is(true));
        assertThat(properties.outputNulls(), is(false));
        assertThat(properties.inputNulls(), is(false));
        assertThat(properties.getSerializers().entrySet(), empty());
        assertThat(properties.getSerializerFactories().entrySet(), empty());
        assertThat(properties.getSerializersByCondition().entrySet(), empty());
        assertThat(properties.getNameFormatter(), is(NameFormatters.IDENTITY));
        assertThat(properties.getFieldFilter(), is(FieldFilters.DEFAULT));
    }

    @Test
    void builderCopiesValues() {
        ConfigurationProperties properties = BUILDER.build();
        assertConfigurationProperties(properties);
    }

    @Test
    void builderCtorCopiesValues() {
        ConfigurationProperties properties = BUILDER.build().toBuilder().build();
        assertConfigurationProperties(properties);
    }

    private static void assertConfigurationProperties(ConfigurationProperties properties) {
        assertThat(properties.getSerializers(), is(Map.of(Point.class, SERIALIZER)));
        assertThat(properties.getSerializersByCondition(), is(Map.of(PREDICATE, SERIALIZER)));
        assertThat(properties.outputNulls(), is(true));
        assertThat(properties.inputNulls(), is(true));
        assertThat(properties.serializeSetsAsLists(), is(false));
        assertThat(properties.getNameFormatter(), sameInstance(FORMATTER));
        assertThat(properties.getFieldFilter(), sameInstance(FILTER));

        var factories = properties.getSerializerFactories();
        assertThat(factories.size(), is(1));
        assertThat(factories.get(Point.class).apply(null), is(SERIALIZER));
    }

    @Test
    void builderSerializersUnmodifiable() {
        ConfigurationProperties properties = ConfigurationProperties.newBuilder().build();
        var serializersByType = properties.getSerializers();
        var serializersFactoriesByType = properties.getSerializerFactories();
        var serializersByCondition = properties.getSerializersByCondition();

        assertThrows(
                UnsupportedOperationException.class,
                () -> serializersByType.put(Point.class, new PointSerializer())
        );
        assertThrows(
                UnsupportedOperationException.class,
                () -> serializersFactoriesByType.put(Point.class, ignored -> new PointSerializer())
        );
        assertThrows(
                UnsupportedOperationException.class,
                () -> serializersByCondition.put(t -> true, new PointSerializer())
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
        void setNameFormatterRequiresNonNull() {
            assertThrowsNullPointerException(
                    () -> builder.setNameFormatter(null),
                    "name formatter"
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
        void addSerializerFactoryByTypeRequiresNonNull() {
            assertThrowsNullPointerException(
                    () -> builder.addSerializerFactory(null, ignored -> new StringSerializer()),
                    "serialized type"
            );

            assertThrowsNullPointerException(
                    () -> builder.addSerializerFactory(String.class, null),
                    "serializer factory"
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