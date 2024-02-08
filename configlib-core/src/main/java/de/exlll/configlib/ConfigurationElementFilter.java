package de.exlll.configlib;

import java.util.function.Predicate;

import static de.exlll.configlib.Validator.requireNonNull;


/**
 * Implementations of this interface test configuration elements for specific
 * conditions.
 */
public interface ConfigurationElementFilter
        extends Predicate<ConfigurationElement<?>> {
    @Override
    default ConfigurationElementFilter and(
            Predicate<? super ConfigurationElement<?>> other
    ) {
        return element -> test(element) && other.test(element);
    }

    /**
     * Creates a new {@code ConfigurationElementFilter} whose {@code test}
     * method returns {@code true} if the tested configuration element is
     * of the given type.
     *
     * @param type the type the filter is looking for
     * @return new {@code ConfigurationElementFilter} that tests configuration
     * elements for their type
     * @throws NullPointerException if {@code type} is null
     */
    static ConfigurationElementFilter byType(Class<?> type) {
        requireNonNull(type, "type");
        return element -> element.type().equals(type);
    }

    /**
     * Creates a new {@code ConfigurationElementFilter} whose {@code test}
     * method returns {@code true} if the tested configuration element is
     * annotated with a {@code PostProcess} annotation whose key equals
     * {@code key}.
     *
     * @param key the key of the {@code PostProcess} annotation the filter is
     *            looking for
     * @return new {@code ConfigurationElementFilter} that tests configuration
     * elements for {@code PostProcess} annotations with the given key
     * @throws NullPointerException if {@code key} is null
     */
    static ConfigurationElementFilter byPostProcessKey(String key) {
        requireNonNull(key, "post-process key");
        return element -> {
            final PostProcess postProcess = element.annotation(PostProcess.class);
            if (postProcess == null) return false;
            final String actualKey = postProcess.key();
            return actualKey.equals(key);
        };
    }
}
