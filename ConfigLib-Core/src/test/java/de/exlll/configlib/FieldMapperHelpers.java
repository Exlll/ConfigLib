package de.exlll.configlib;

import de.exlll.configlib.FieldMapper.MappingInfo;
import de.exlll.configlib.annotation.ConfigurationElement;

import java.util.Map;

import static de.exlll.configlib.configs.yaml.YamlConfiguration.YamlProperties.DEFAULT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FieldMapperHelpers {
    @ConfigurationElement
    interface LocalTestInterface {}

    @ConfigurationElement
    static class LocalTestInterfaceImpl implements LocalTestInterface {}

    @ConfigurationElement
    static abstract class LocalTestAbstractClass {}

    @ConfigurationElement
    static class LocalTestAbstractClassImpl extends LocalTestAbstractClass {}

    @ConfigurationElement
    enum LocalTestEnum {
        S, T
    }

    static class Sub1 {}

    @ConfigurationElement
    static class Sub2 {}

    @ConfigurationElement
    static class Sub3 {
        public Sub3(int x) {}
    }

    public static void assertItmCfgExceptionMessage(Object o, String msg) {
        ConfigurationException ex = assertItmThrowsCfgException(o);
        assertThat(ex.getMessage(), is(msg));
    }

    public static void assertIfmCfgExceptionMessage(
            Object o, Map<String, Object> map, String msg
    ) {
        ConfigurationException ex = assertIfmThrowsCfgException(o, map);
        assertThat(ex.getMessage(), is(msg));
    }

    public static ConfigurationException assertItmThrowsCfgException(Object o) {
        return assertThrows(
                ConfigurationException.class,
                () -> instanceToMap(o)
        );
    }

    public static ConfigurationException assertIfmThrowsCfgException(
            Object o, Map<String, Object> map
    ) {
        return assertThrows(
                ConfigurationException.class,
                () -> instanceFromMap(o, map)
        );
    }

    public static Map<String, Object> instanceToMap(Object o) {
        return instanceToMap(o, DEFAULT);
    }

    public static Map<String, Object> instanceToMap(
            Object o, Configuration.Properties props
    ) {
        MappingInfo mappingInfo = new MappingInfo(null, props);
        return FieldMapper.instanceToMap(o, mappingInfo);
    }

    public static <T> T instanceFromMap(T o, Map<String, Object> map) {
        return instanceFromMap(o, map, DEFAULT);
    }

    public static <T> T instanceFromMap(
            T o, Map<String, Object> map, Configuration.Properties props
    ) {
        MappingInfo mappingInfo = new MappingInfo(null, props);
        FieldMapper.instanceFromMap(o, map, mappingInfo);
        return o;
    }
}
