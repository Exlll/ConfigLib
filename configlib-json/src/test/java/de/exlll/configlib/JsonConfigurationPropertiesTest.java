package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JsonConfigurationPropertiesTest {
    @Test
    void builderCtorCopiesValues() {
        JsonConfigurationProperties properties = JsonConfigurationProperties.newBuilder()
                .outputNulls(true)
                .build()
                .toBuilder()
                .build();

        assertThat(properties.outputNulls(), is(true));
    }
}