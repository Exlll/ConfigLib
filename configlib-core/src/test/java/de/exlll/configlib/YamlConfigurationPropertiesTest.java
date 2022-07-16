package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class YamlConfigurationPropertiesTest {
    @Test
    void builderCtorCopiesValues() {
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .outputNulls(true)
                .header("A")
                .build()
                .toBuilder()
                .build();

        assertThat(properties.outputNulls(), is(true));
        assertThat(properties.getHeader(), is("A"));
    }
}