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
import java.util.concurrent.atomic.AtomicInteger;
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

    @Test
    void atMost1PostProcessMethodAllowed() {
        @Configuration
        class A {
            int i;

            @PostProcess
            void postProcessA() {}

            @PostProcess
            void postProcessB() {}
        }

        record R1(int i) {
            @PostProcess
            void postProcessA() {}

            @PostProcess
            void postProcessB() {}
        }

        assertThrowsConfigurationException(
                () -> newTypeSerializer(A.class),
                """
                Configuration types must not define more than one method for post-processing \
                but type 'class de.exlll.configlib.TypeSerializerTest$1A' defines 2:
                  void de.exlll.configlib.TypeSerializerTest$1A.postProcessA()
                  void de.exlll.configlib.TypeSerializerTest$1A.postProcessB()\
                """
        );
        assertThrowsConfigurationException(
                () -> newTypeSerializer(R1.class),
                """
                Configuration types must not define more than one method for post-processing \
                but type 'class de.exlll.configlib.TypeSerializerTest$1R1' defines 2:
                  void de.exlll.configlib.TypeSerializerTest$1R1.postProcessA()
                  void de.exlll.configlib.TypeSerializerTest$1R1.postProcessB()\
                """
        );
    }

    @Test
    void postProcessMustNotBeStaticOrAbstract() {
        @Configuration
        class B {
            int i;

            @PostProcess
            static void postProcess() {}
        }
        @Configuration
        abstract class C {
            int i;

            @PostProcess
            abstract void postProcess();
        }

        record R2(int i) {
            @PostProcess
            static void postProcess() {}
        }
        assertThrowsConfigurationException(
                () -> newTypeSerializer(B.class),
                """
                Post-processing methods must be neither abstract nor static, but post-processing \
                method 'static void de.exlll.configlib.TypeSerializerTest$1B.postProcess()' of \
                type 'class de.exlll.configlib.TypeSerializerTest$1B' is.\
                """
        );
        assertThrowsConfigurationException(
                () -> newTypeSerializer(C.class),
                """
                Post-processing methods must be neither abstract nor static, but post-processing \
                method 'abstract void de.exlll.configlib.TypeSerializerTest$1C.postProcess()' of \
                type 'class de.exlll.configlib.TypeSerializerTest$1C' is.\
                """
        );
        assertThrowsConfigurationException(
                () -> newTypeSerializer(R2.class),
                """
                Post-processing methods must be neither abstract nor static, but post-processing \
                method 'static void de.exlll.configlib.TypeSerializerTest$1R2.postProcess()' of \
                type 'class de.exlll.configlib.TypeSerializerTest$1R2' is.\
                """
        );
    }

    @Test
    void postProcessMustNotHaveArguments() {
        @Configuration
        class D {
            int i;

            @PostProcess
            void postProcess(int j, int k) {}
        }

        record R4(int i) {
            @PostProcess
            void postProcess(int l) {}
        }

        assertThrowsConfigurationException(
                () -> newTypeSerializer(D.class),
                """
                Post-processing methods must not define any parameters but post-processing method \
                'void de.exlll.configlib.TypeSerializerTest$1D.postProcess(int,int)' of type \
                'class de.exlll.configlib.TypeSerializerTest$1D' defines 2.\
                """
        );
        assertThrowsConfigurationException(
                () -> newTypeSerializer(R4.class),
                """
                Post-processing methods must not define any parameters but post-processing method \
                'void de.exlll.configlib.TypeSerializerTest$1R4.postProcess(int)' of type \
                'class de.exlll.configlib.TypeSerializerTest$1R4' defines 1.\
                """
        );
    }

    @Test
    void postProcessMustReturnVoidOrSameType() {
        @Configuration
        class E {
            int i;

            @PostProcess
            E postProcess() {return null;}
        }
        class F extends E {
            @Override
            @PostProcess
            E postProcess() {return null;}
        }
        class G extends E {
            @Override
            @PostProcess
            G postProcess() {
                return null;
            }
        }

        // both of these are okay:
        newTypeSerializer(E.class);
        newTypeSerializer(G.class);

        assertThrowsConfigurationException(
                () -> newTypeSerializer(F.class),
                """
                The return type of post-processing methods must either be 'void' or the same \
                type as the configuration type in which the post-processing method is defined. \
                The return type of the post-processing method of \
                type 'class de.exlll.configlib.TypeSerializerTest$1F' is neither 'void' nor 'F'.\
                """
        );
    }

    @Test
    void postProcessorInvokesAnnotatedMethodWithVoidReturnType1() {
        final AtomicInteger integer = new AtomicInteger(0);

        @Configuration
        class H1 {
            int i;

            @PostProcess
            void postProcess() {integer.set(20);}
        }

        final var serializer = newTypeSerializer(H1.class);
        final var postProcessor = serializer.createPostProcessorFromAnnotatedMethod();

        final H1 h1_1 = new H1();
        final H1 h1_2 = postProcessor.apply(h1_1);

        assertThat(h1_2, sameInstance(h1_2));
        assertThat(integer.get(), is(20));
    }

    static int postProcessorInvokesAnnotatedMethodWithVoidReturnType2_int = 0;

    @Test
    void postProcessorInvokesAnnotatedMethodWithVoidReturnType2() {
        record H2(int j) {
            @PostProcess
            void postProcess() {
                postProcessorInvokesAnnotatedMethodWithVoidReturnType2_int += 10;
            }
        }

        final var serializer = newTypeSerializer(H2.class);
        final var postProcessor = serializer.createPostProcessorFromAnnotatedMethod();

        final H2 h2_1 = new H2(10);
        final H2 h2_2 = postProcessor.apply(h2_1);

        assertThat(h2_2, sameInstance(h2_1));
        assertThat(postProcessorInvokesAnnotatedMethodWithVoidReturnType2_int, is(10));
    }

    @Test
    void postProcessorInvokesAnnotatedMethodWithSameReturnType1() {
        @Configuration
        class H3 {
            int i;

            @PostProcess
            H3 postProcess() {
                H3 h3 = new H3();
                h3.i = i + 20;
                return h3;
            }
        }

        final var serializer = newTypeSerializer(H3.class);
        final var postProcessor = serializer.createPostProcessorFromAnnotatedMethod();

        final H3 h3_1 = new H3();
        h3_1.i = 10;
        final H3 h3_2 = postProcessor.apply(h3_1);

        assertThat(h3_2, not(sameInstance(h3_1)));
        assertThat(h3_1.i, is(10));
        assertThat(h3_2.i, is(30));
    }

    @Test
    void postProcessorInvokesAnnotatedMethodWithSameReturnType2() {
        record H4(int i) {
            @PostProcess
            H4 postProcess() {
                return new H4(i + 20);
            }
        }

        final var serializer = newTypeSerializer(H4.class);
        final var postProcessor = serializer.createPostProcessorFromAnnotatedMethod();

        final H4 h4_1 = new H4(10);
        final H4 h4_2 = postProcessor.apply(h4_1);

        assertThat(h4_2, not(sameInstance(h4_1)));
        assertThat(h4_1.i, is(10));
        assertThat(h4_2.i, is(30));
    }

    @Test
    void postProcessorIsIdentityFunctionIfNoPostProcessAnnotationPresent() {
        @Configuration
        class J {
            int i;
        }
        final var serializer = newTypeSerializer(J.class);
        final var postProcessor = serializer.createPostProcessorFromAnnotatedMethod();

        final J j_1 = new J();
        final J j_2 = postProcessor.apply(j_1);
        assertThat(j_2, sameInstance(j_1));
    }

    @Test
    void postProcessOfParentClassNotCalled() {
        @Configuration
        class A {
            int i = 10;

            @PostProcess
            void postProcess() {
                this.i = this.i + 10;
            }
        }

        class B extends A {}

        final var serializer = newTypeSerializer(B.class);
        final var postProcessor = serializer.createPostProcessorFromAnnotatedMethod();


        B b = new B();
        postProcessor.apply(b);
        assertThat(b.i, is(10));
    }
}
