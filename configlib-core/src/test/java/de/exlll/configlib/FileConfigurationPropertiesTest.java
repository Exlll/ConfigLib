package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class FileConfigurationPropertiesTest {
    @Test
    void builderDefaultValues() {
        FileConfigurationProperties properties = FileConfigurationProperties.newBuilder().build();
        assertNull(properties.getHeader());
        assertNull(properties.getFooter());
        assertTrue(properties.createParentDirectories());
    }

    @Test
    void builderCopiesValues() {
        FileConfigurationProperties properties = FileConfigurationProperties.newBuilder()
                .header("THE HEADER")
                .footer("THE FOOTER")
                .createParentDirectories(false)
                .build();
        assertEquals("THE HEADER", properties.getHeader());
        assertEquals("THE FOOTER", properties.getFooter());
        assertFalse(properties.createParentDirectories());
    }

    @Test
    void builderCtorCopiesValues() {
        FileConfigurationProperties properties = FileConfigurationProperties.newBuilder()
                .outputNulls(true)
                .header("A")
                .footer("B")
                .createParentDirectories(false)
                .build()
                .toBuilder()
                .build();

        assertThat(properties.outputNulls(), is(true));
        assertThat(properties.getHeader(), is("A"));
        assertThat(properties.getFooter(), is("B"));
        assertThat(properties.createParentDirectories(), is(false));
    }
}