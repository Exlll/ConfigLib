package de.exlll.configlib;

import de.exlll.configlib.Serializers.*;
import de.exlll.configlib.configurations.ExampleConfigurationA2;
import de.exlll.configlib.configurations.ExampleConfigurationB1;
import de.exlll.configlib.configurations.ExampleConfigurationB2;
import de.exlll.configlib.configurations.ExampleEnum;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static de.exlll.configlib.TestUtils.assertThrowsConfigurationException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TypeSerializerTest {
    private static <T> TypeSerializer<T, ?> newTypeSerializer(
            Class<T> type,
            Consumer<ConfigurationProperties.Builder<?>> propertiesConfigurer
    ) {
        var builder = ConfigurationProperties.newBuilder();
        builder.addSerializer(Point.class, TestUtils.POINT_SERIALIZER);
        propertiesConfigurer.accept(builder);
        ConfigurationProperties properties = builder.build();
        return TypeSerializer.newSerializerFor(type, properties);
    }

    private static <T> TypeSerializer<T, ?> newTypeSerializer(Class<T> type) {
        return newTypeSerializer(type, builder -> {});
    }

    @Test
    void buildSerializerMapUsesComponentName() {
        Map<String, Serializer<?, ?>> serializers = newTypeSerializer(
                ExampleConfigurationA2.class,
                builder -> builder.setNameFormatter(NameFormatters.UPPER_UNDERSCORE)
        ).buildSerializerMap();
        assertThat(serializers.get("A2_PRIM_BOOL"), nullValue());
        assertThat(serializers.get("a2_primBool"), instanceOf(BooleanSerializer.class));
    }

    @Test
    void buildSerializerMapForConfiguration() {
        Map<String, Serializer<?, ?>> serializers = newTypeSerializer(ExampleConfigurationA2.class)
                .buildSerializerMap();
        assertThat(serializers.get("a2_primBool"), instanceOf(BooleanSerializer.class));
        assertThat(serializers.get("a2_refChar"), instanceOf(CharacterSerializer.class));
        assertThat(serializers.get("a2_string"), instanceOf(StringSerializer.class));
        assertThat(serializers.get("a2_Enm"), instanceOf(EnumSerializer.class));

        ConfigurationSerializer<?> serializerB1 =
                (ConfigurationSerializer<?>) serializers.get("a2_b1");
        ConfigurationSerializer<?> serializerB2 =
                (ConfigurationSerializer<?>) serializers.get("a2_b2");

        assertThat(serializerB1.getConfigurationType(), equalTo(ExampleConfigurationB1.class));
        assertThat(serializerB2.getConfigurationType(), equalTo(ExampleConfigurationB2.class));

        Serializers.ListSerializer<?, ?> serializerList =
                (Serializers.ListSerializer<?, ?>) serializers.get("a2_listByte");
        Serializers.ArraySerializer<?, ?> serializerArray =
                (Serializers.ArraySerializer<?, ?>) serializers.get("a2_arrayString");
        Serializers.SetAsListSerializer<?, ?> serializerSet =
                (Serializers.SetAsListSerializer<?, ?>) serializers.get("a2_setBigInteger");
        Serializers.MapSerializer<?, ?, ?, ?> serializerMap =
                (Serializers.MapSerializer<?, ?, ?, ?>) serializers.get("a2_mapLocalTimeLocalTime");

        assertThat(
                serializers.get("a2_arrayPrimDouble"),
                instanceOf(PrimitiveDoubleArraySerializer.class)
        );

        assertThat(serializerList.getElementSerializer(), instanceOf(NumberSerializer.class));
        assertThat(serializerArray.getElementSerializer(), instanceOf(StringSerializer.class));
        assertThat(serializerSet.getElementSerializer(), instanceOf(BigIntegerSerializer.class));
        assertThat(serializerMap.getKeySerializer(), instanceOf(LocalTimeSerializer.class));
        assertThat(serializerMap.getValueSerializer(), instanceOf(LocalTimeSerializer.class));

        assertThat(serializers.get("a2_point"), sameInstance(TestUtils.POINT_SERIALIZER));
    }

    private record R1(
            boolean primBool,
            Character refChar,
            String string,
            ExampleEnum enm,
            ExampleConfigurationB1 b1,
            ExampleConfigurationB2 b2,
            List<Byte> listByte,
            String[] arrayString,
            Set<BigInteger> setBigInteger,
            Map<UUID, UUID> mapUuidUuid,
            double[] arrayDouble,
            Point point
    ) {}

    @Test
    void buildSerializerMapForRecord() {
        Map<String, Serializer<?, ?>> serializers = newTypeSerializer(R1.class)
                .buildSerializerMap();
        assertThat(serializers.get("primBool"), instanceOf(BooleanSerializer.class));
        assertThat(serializers.get("refChar"), instanceOf(CharacterSerializer.class));
        assertThat(serializers.get("string"), instanceOf(StringSerializer.class));
        assertThat(serializers.get("enm"), instanceOf(EnumSerializer.class));

        ConfigurationSerializer<?> serializerB1 =
                (ConfigurationSerializer<?>) serializers.get("b1");
        ConfigurationSerializer<?> serializerB2 =
                (ConfigurationSerializer<?>) serializers.get("b2");

        assertThat(serializerB1.getConfigurationType(), equalTo(ExampleConfigurationB1.class));
        assertThat(serializerB2.getConfigurationType(), equalTo(ExampleConfigurationB2.class));

        Serializers.ListSerializer<?, ?> serializerList =
                (Serializers.ListSerializer<?, ?>) serializers.get("listByte");
        Serializers.ArraySerializer<?, ?> serializerArray =
                (Serializers.ArraySerializer<?, ?>) serializers.get("arrayString");
        Serializers.SetAsListSerializer<?, ?> serializerSet =
                (Serializers.SetAsListSerializer<?, ?>) serializers.get("setBigInteger");
        Serializers.MapSerializer<?, ?, ?, ?> serializerMap =
                (Serializers.MapSerializer<?, ?, ?, ?>) serializers.get("mapUuidUuid");

        assertThat(
                serializers.get("arrayDouble"),
                instanceOf(PrimitiveDoubleArraySerializer.class)
        );

        assertThat(serializerList.getElementSerializer(), instanceOf(NumberSerializer.class));
        assertThat(serializerArray.getElementSerializer(), instanceOf(StringSerializer.class));
        assertThat(serializerSet.getElementSerializer(), instanceOf(BigIntegerSerializer.class));
        assertThat(serializerMap.getKeySerializer(), instanceOf(UuidSerializer.class));
        assertThat(serializerMap.getValueSerializer(), instanceOf(UuidSerializer.class));

        assertThat(serializers.get("point"), sameInstance(TestUtils.POINT_SERIALIZER));
    }

    @Configuration
    static final class Recursive1 {
        Recursive2 recursive2;
    }

    @Configuration
    static final class Recursive2 {
        Recursive1 recursive1;
    }

    @Test
    void buildSerializerMapForConfigurationPreventsRecursiveDefinitions() {
        assertThrowsConfigurationException(
                () -> newTypeSerializer(Recursive1.class),
                "Recursive type definitions are not supported."
        );
    }

    record RecursiveRecord1(RecursiveRecord2 recursiveRecord2) {}

    record RecursiveRecord2(RecursiveRecord1 recursiveRecord1) {}

    record RecursiveRecord3(RecursiveRecord3 recursiveRecord3) {}

    @Test
    void buildSerializerMapForRecordPreventsRecursiveDefinitions() {
        assertThrowsConfigurationException(
                () -> newTypeSerializer(RecursiveRecord1.class),
                "Recursive type definitions are not supported."
        );

        assertThrowsConfigurationException(
                () -> newTypeSerializer(RecursiveRecord3.class),
                "Recursive type definitions are not supported."
        );
    }
}