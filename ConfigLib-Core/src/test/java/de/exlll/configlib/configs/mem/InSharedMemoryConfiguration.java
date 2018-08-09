package de.exlll.configlib.configs.mem;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.ConfigurationSource;

import java.util.Map;
import java.util.Objects;

public class InSharedMemoryConfiguration
        extends Configuration<InSharedMemoryConfiguration> {
    private final InSharedMemorySource source = new InSharedMemorySource();

    protected InSharedMemoryConfiguration(Properties properties) {
        super(properties);
    }

    @Override
    protected ConfigurationSource<InSharedMemoryConfiguration> getSource() {
        return source;
    }

    @Override
    protected InSharedMemoryConfiguration getThis() {
        return this;
    }

    private static final class InSharedMemorySource implements
            ConfigurationSource<InSharedMemoryConfiguration> {
        private static Map<String, Object> map;

        @Override
        public Map<String, Object> loadConfiguration(
                InSharedMemoryConfiguration config
        ) {
            return Objects.requireNonNull(map);
        }

        @Override
        public void saveConfiguration(
                InSharedMemoryConfiguration config, Map<String, Object> map
        ) {
            InSharedMemorySource.map = map;
        }
    }
}
