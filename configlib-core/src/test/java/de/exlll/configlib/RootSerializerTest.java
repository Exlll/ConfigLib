package de.exlll.configlib;

import de.exlll.configlib.ConfigurationProperties.EnvVarResolutionConfiguration;
import de.exlll.configlib.TestUtils.MapEnvironment;
import de.exlll.configlib.TestUtils.PointSerializer;
import de.exlll.configlib.configurations.ExampleConfigurationA2;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import static de.exlll.configlib.TestUtils.*;
import static de.exlll.configlib.configurations.ExampleConfigurationsSerialized.initExampleConfigurationA2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RootSerializerTest {
    @Test
    void ctorRequiresNonNullArgs() {
        record Config(int i) {}
        final var props = ConfigurationProperties.newBuilder().build();
        final var env = new MapEnvironment(Map.of());
        assertThrowsNullPointerException(
                () -> new RootSerializer<>(null, props, env),
                "type"
        );
        assertThrowsNullPointerException(
                () -> new RootSerializer<>(Config.class, null, env),
                "configuration properties"
        );
        assertThrowsNullPointerException(
                () -> new RootSerializer<>(Config.class, props, null),
                "environment"
        );
    }


    public static final class EnvironmentVariablePreprocessorTest {
        record Config1(Config2 config2) {}

        record Config2(
                int i,
                List<Float> listFloat,
                Map<Integer, List<Double>> mapIntegerListDouble,
                List<Map<String, Integer>> listMapStringInteger,
                List<List<String>> listListString
        ) {}


        @Test
        void preprocessEnvVarsFailsIfEnvVarTriesToReplaceCollection() {
            final String format =
                    """
                    Environment variables cannot be used to replace collections, but \
                    environment variable '%s' with value '%s' tries to replace a %s.\
                    """;
            assertThrowsConfigurationException(
                    () -> {
                        final var serializer = newSerializer(
                                Config1.class, "", false,
                                entriesAsMap(entry("CONFIG2_LISTFLOAT", "1"))
                        );
                        deserializeExampleConfig1(serializer);
                    },
                    format.formatted("CONFIG2_LISTFLOAT", "1", "list")
            );
            assertThrowsConfigurationException(
                    () -> {
                        final var serializer = newSerializer(
                                Config1.class, "", false,
                                entriesAsMap(entry("CONFIG2_MAPINTEGERLISTDOUBLE", "2"))
                        );
                        deserializeExampleConfig1(serializer);
                    },
                    format.formatted("CONFIG2_MAPINTEGERLISTDOUBLE", "2", "map")
            );

            assertThrowsConfigurationException(
                    () -> {
                        final var serializer = newSerializer(
                                Config1.class, "", false,
                                entriesAsMap(entry("CONFIG2_LISTMAPSTRINGINTEGER_0", "3"))
                        );
                        deserializeExampleConfig1(serializer);
                    },
                    format.formatted("CONFIG2_LISTMAPSTRINGINTEGER_0", "3", "map")
            );
            assertThrowsConfigurationException(
                    () -> {
                        final var serializer = newSerializer(
                                Config1.class, "", false,
                                entriesAsMap(entry("CONFIG2_LISTLISTSTRING_2", "4"))
                        );
                        deserializeExampleConfig1(serializer);
                    },
                    format.formatted("CONFIG2_LISTLISTSTRING_2", "4", "list")
            );
        }

        @Test
        void preprocessNestedEnvVarsWithPrefixCaseInsensitive() {
            RootSerializer<Config1> serializer = newSerializer(
                    Config1.class, "MY_PREFIX", false,
                    entriesAsMap(
                            entry("MY_PREFIX_CONFIG2_I", "10"),
                            entry("MY_PREFIX_CONFIG2_LISTFLOAT_1", "1.5"),
                            entry("MY_PREFIX_CONFIG2_MAPINTEGERLISTDOUBLE_1_1", "2.5"),
                            entry("MY_PREFIX_CONFIG2_LISTMAPSTRINGINTEGER_0_YES", "3"),
                            entry("MY_PREFIX_CONFIG2_LISTLISTSTRING_2_1", "TEST2")
                    )
            );
            assertNestedEnvVarsDeserialized(serializer);
        }

        @Test
        void preprocessNestedEnvVarsWithoutPrefixCaseInsensitive() {
            RootSerializer<Config1> serializer = newSerializer(
                    Config1.class, "", false,
                    entriesAsMap(
                            entry("CONFIG2_I", "10"),
                            entry("CONFIG2_LISTFLOAT_1", "1.5"),
                            entry("CONFIG2_MAPINTEGERLISTDOUBLE_1_1", "2.5"),
                            entry("CONFIG2_LISTMAPSTRINGINTEGER_0_YES", "3"),
                            entry("CONFIG2_LISTLISTSTRING_2_1", "TEST2")
                    )
            );
            assertNestedEnvVarsDeserialized(serializer);
        }

        @Test
        void preprocessNestedEnvVarsWithPrefixCaseSensitive() {
            RootSerializer<Config1> serializer = newSerializer(
                    Config1.class, "MY_PREFIX", true,
                    entriesAsMap(
                            entry("MY_PREFIX_config2_i", "10"),
                            entry("MY_PREFIX_config2_listFloat_1", "1.5"),
                            entry("MY_PREFIX_config2_mapIntegerListDouble_1_1", "2.5"),
                            entry("MY_PREFIX_config2_listMapStringInteger_0_YES", "3"),
                            entry("MY_PREFIX_config2_listListString_2_1", "TEST2")
                    )
            );
            assertNestedEnvVarsDeserialized(serializer);
        }

        @Test
        void preprocessNestedEnvVarsWithoutPrefixCaseSensitive() {
            RootSerializer<Config1> serializer = newSerializer(
                    Config1.class, "", true,
                    entriesAsMap(
                            entry("config2_i", "10"),
                            entry("config2_listFloat_1", "1.5"),
                            entry("config2_mapIntegerListDouble_1_1", "2.5"),
                            entry("config2_listMapStringInteger_0_YES", "3"),
                            entry("config2_listListString_2_1", "TEST2")
                    )
            );
            assertNestedEnvVarsDeserialized(serializer);
        }

        private static void assertNestedEnvVarsDeserialized(RootSerializer<Config1> serializer) {
            Config1 config1 = deserializeExampleConfig1(serializer);
            assertThat(config1.config2.i, is(10));
            assertThat(config1.config2.listFloat.get(1), is(1.5f));
            assertThat(config1.config2.mapIntegerListDouble.get(1).get(1), is(2.5d));
            assertThat(config1.config2.listMapStringInteger.get(0).get("YES"), is(3));
            assertThat(config1.config2.listListString.get(2).get(1), is("TEST2"));
        }

        private static Config1 deserializeExampleConfig1(RootSerializer<Config1> serializer) {
            return serializer.deserialize(asMap("config2", entriesAsMap(
                    entry("i", 9L),
                    entry("listFloat", asList(1d, 2d, 3d)),
                    entry("mapIntegerListDouble", asMap(1L, asList(1d, 2d))),
                    entry("listMapStringInteger", asList(asMap("NO", 1L, "YES", 2L))),
                    entry("listListString", asList(asList(), asList(), asList("", "TEST1")))
            )));
        }

        @Test
        void preprocessEnvVarsForTopLevelVariablesWithoutPrefixCaseInsensitive() {
            RootSerializer<ExampleConfigurationA2> serializer = newSerializer(
                    ExampleConfigurationA2.class,
                    "",
                    false,
                    entriesAsMap(
                            entry("A2_PRIMBOOL", "false"),
                            entry("A2_PRIMCHAR", "e"),
                            entry("A2_PRIMBYTE", "3"),
                            entry("A2_PRIMSHORT", "5"),
                            entry("A2_PRIMINT", "7"),
                            entry("A2_PRIMLONG", "9"),
                            entry("A2_PRIMFLOAT", "11"),
                            entry("A2_PRIMDOUBLE", "13"),
                            entry("A2_REFBOOL", "true"),
                            entry("A2_REFCHAR", "f"),
                            entry("A2_REFBYTE", "15"),
                            entry("A2_REFSHORT", "17"),
                            entry("A2_REFINT", "19"),
                            entry("A2_REFLONG", "21"),
                            entry("A2_REFFLOAT", "23"),
                            entry("A2_REFDOUBLE", "25"),
                            entry("A2_STRING", "g")
                    ));

            assertDeserializeResolvesTopLevelEnvVars(serializer);
        }

        @Test
        void preprocessEnvVarsForTopLevelVariablesWithoutPrefixCaseSensitive() {
            RootSerializer<ExampleConfigurationA2> serializer = newSerializer(
                    ExampleConfigurationA2.class,
                    "",
                    true,
                    entriesAsMap(
                            entry("a2_primBool", "false"),
                            entry("a2_primChar", "e"),
                            entry("a2_primByte", "3"),
                            entry("a2_primShort", "5"),
                            entry("a2_primInt", "7"),
                            entry("a2_primLong", "9"),
                            entry("a2_primFloat", "11"),
                            entry("a2_primDouble", "13"),
                            entry("a2_refBool", "true"),
                            entry("a2_refChar", "f"),
                            entry("a2_refByte", "15"),
                            entry("a2_refShort", "17"),
                            entry("a2_refInt", "19"),
                            entry("a2_refLong", "21"),
                            entry("a2_refFloat", "23"),
                            entry("a2_refDouble", "25"),
                            entry("a2_string", "g")
                    ));

            assertDeserializeResolvesTopLevelEnvVars(serializer);
        }

        @Test
        void preprocessEnvVarsForTopLevelVariablesWithPrefixCaseInsensitive() {
            RootSerializer<ExampleConfigurationA2> serializer = newSerializer(
                    ExampleConfigurationA2.class,
                    "MY_PREFIX",
                    false,
                    entriesAsMap(
                            entry("MY_PREFIX_A2_PRIMBOOL", "false"),
                            entry("MY_PREFIX_A2_PRIMCHAR", "e"),
                            entry("MY_PREFIX_A2_PRIMBYTE", "3"),
                            entry("MY_PREFIX_A2_PRIMSHORT", "5"),
                            entry("MY_PREFIX_A2_PRIMINT", "7"),
                            entry("MY_PREFIX_A2_PRIMLONG", "9"),
                            entry("MY_PREFIX_A2_PRIMFLOAT", "11"),
                            entry("MY_PREFIX_A2_PRIMDOUBLE", "13"),
                            entry("MY_PREFIX_A2_REFBOOL", "true"),
                            entry("MY_PREFIX_A2_REFCHAR", "f"),
                            entry("MY_PREFIX_A2_REFBYTE", "15"),
                            entry("MY_PREFIX_A2_REFSHORT", "17"),
                            entry("MY_PREFIX_A2_REFINT", "19"),
                            entry("MY_PREFIX_A2_REFLONG", "21"),
                            entry("MY_PREFIX_A2_REFFLOAT", "23"),
                            entry("MY_PREFIX_A2_REFDOUBLE", "25"),
                            entry("MY_PREFIX_A2_STRING", "g")
                    ));
            assertDeserializeResolvesTopLevelEnvVars(serializer);
        }

        @Test
        void preprocessEnvVarsForTopLevelVariablesWithPrefixCaseSensitive() {
            RootSerializer<ExampleConfigurationA2> serializer = newSerializer(
                    ExampleConfigurationA2.class,
                    "MY_PREFIX",
                    true,
                    entriesAsMap(
                            entry("MY_PREFIX_a2_primBool", "false"),
                            entry("MY_PREFIX_a2_primChar", "e"),
                            entry("MY_PREFIX_a2_primByte", "3"),
                            entry("MY_PREFIX_a2_primShort", "5"),
                            entry("MY_PREFIX_a2_primInt", "7"),
                            entry("MY_PREFIX_a2_primLong", "9"),
                            entry("MY_PREFIX_a2_primFloat", "11"),
                            entry("MY_PREFIX_a2_primDouble", "13"),
                            entry("MY_PREFIX_a2_refBool", "true"),
                            entry("MY_PREFIX_a2_refChar", "f"),
                            entry("MY_PREFIX_a2_refByte", "15"),
                            entry("MY_PREFIX_a2_refShort", "17"),
                            entry("MY_PREFIX_a2_refInt", "19"),
                            entry("MY_PREFIX_a2_refLong", "21"),
                            entry("MY_PREFIX_a2_refFloat", "23"),
                            entry("MY_PREFIX_a2_refDouble", "25"),
                            entry("MY_PREFIX_a2_string", "g")
                    ));
            assertDeserializeResolvesTopLevelEnvVars(serializer);
        }

        record Config3(boolean b, long l, double d) {}

        @Test
        void preprocessEnvVarWithWrongType() {
            final var serializer1 = newSerializer(Config3.class, "A", false, asMap("A_B", "1"));
            final var serializer2 = newSerializer(Config3.class, "A", false, asMap("A_L", "false"));
            final var serializer3 = newSerializer(Config3.class, "A", false, asMap("A_D", "false"));

            String msg = "Value '%s' of environment variable '%s' cannot be converted to type '%s'. " +
                         "The value the environment variable is supposed to replace is '%s'.";

            assertThrowsConfigurationException(
                    () -> serializer1.deserialize(asMap("b", true)),
                    msg.formatted("1", "A_B", "Boolean", "true")
            );
            assertThrowsConfigurationException(
                    () -> serializer2.deserialize(asMap("l", 10L)),
                    msg.formatted("false", "A_L", "Long", "10")
            );
            assertThrowsConfigurationException(
                    () -> serializer3.deserialize(asMap("d", 20d)),
                    msg.formatted("false", "A_D", "Double", "20.0")
            );
        }

        @Test
        void preprocessEnvVarWithInvalidValue() {
            final var serializer = newSerializer(
                    Config3.class, "", false,
                    asMap("B", "invalid", "L", "invalid", "D", "invalid")
            );

            ConfigurationException exception1 = assertThrowsConfigurationException(
                    () -> serializer.deserialize(asMap("b", true))
            );
            assertThat(
                    exception1.getCause().getMessage(),
                    is("The string 'invalid' is not a valid boolean value. " +
                       "Only the values 'true' and 'false' (ignoring their case) are supported.")
            );

            ConfigurationException exception2 = assertThrowsConfigurationException(
                    () -> serializer.deserialize(asMap("l", 1L))
            );
            assertThat(
                    exception2.getCause().getMessage(),
                    is("For input string: \"invalid\"")
            );

            ConfigurationException exception3 = assertThrowsConfigurationException(
                    () -> serializer.deserialize(asMap("d", 1.0d))
            );
            assertThat(
                    exception3.getCause().getMessage(),
                    is("For input string: \"invalid\"")
            );
        }

        @Test
        void preprocessEnvVarForNullValue() {
            final var serializer = newSerializer(Config3.class, "A", false, asMap("A_B", "1"));
            assertThrowsConfigurationException(
                    () -> serializer.deserialize(asMap("b", null)),
                    "Value '1' of environment variable 'A_B' cannot be converted because the " +
                    "value that is supposed to be replaced is null."
            );
        }

        @Test
        void environmentVariablesNotResolvedIfConfigurationDisabled() {
            record Config(int val1, int val2) {}
            final var envConfig = EnvVarResolutionConfiguration.disabled();
            final var props = ConfigurationProperties.newBuilder()
                    .setEnvVarResolutionConfiguration(envConfig)
                    .build();
            final var serializer = new RootSerializer<>(
                    Config.class,
                    props,
                    new MapEnvironment(asMap("val1", "3", "val2", "4", "VAL1", "5", "VAL2", "6"))
            );
            Config deserialized = serializer.deserialize(asMap("val1", 1L, "val2", 2L));
            assertThat(deserialized.val1, is(1));
            assertThat(deserialized.val2, is(2));
        }

        private static void assertDeserializeResolvesTopLevelEnvVars(
                RootSerializer<ExampleConfigurationA2> serializer
        ) {
            ExampleConfigurationA2 config = serializer.deserialize(initExampleConfigurationA2());

            assertThat(config.isA2_primBool(), is(false));
            assertThat(config.getA2_primChar(), is('e'));
            assertThat(config.getA2_primByte(), is((byte) 3));
            assertThat(config.getA2_primShort(), is((short) 5));
            assertThat(config.getA2_primInt(), is(7));
            assertThat(config.getA2_primLong(), is(9L));
            assertThat(config.getA2_primFloat(), is(11f));
            assertThat(config.getA2_primDouble(), is(13d));
            assertThat(config.getA2_refBool(), is(true));
            assertThat(config.getA2_refChar(), is('f'));
            assertThat(config.getA2_refByte(), is((byte) 15));
            assertThat(config.getA2_refShort(), is((short) 17));
            assertThat(config.getA2_refInt(), is(19));
            assertThat(config.getA2_refLong(), is(21L));
            assertThat(config.getA2_refFloat(), is(23f));
            assertThat(config.getA2_refDouble(), is(25d));
            assertThat(config.getA2_string(), is("g"));
        }

        private static <T> RootSerializer<T> newSerializer(
                Class<T> configType,
                String prefix,
                boolean caseSensitive,
                Map<String, String> environment
        ) {
            final var envConfig = EnvVarResolutionConfiguration
                    .resolveEnvVarsWithPrefix(prefix, caseSensitive);
            final var props = ConfigurationProperties.newBuilder()
                    .addSerializer(Point.class, new PointSerializer())
                    .setEnvVarResolutionConfiguration(envConfig)
                    .build();
            return new RootSerializer<>(
                    configType,
                    props,
                    new MapEnvironment(environment)
            );
        }
    }
}