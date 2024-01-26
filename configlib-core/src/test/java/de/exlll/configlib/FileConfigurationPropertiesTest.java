package de.exlll.configlib;

import org.junit.jupiter.api.Test;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
        assertEquals(Charset.defaultCharset(), properties.getCharset());
    }

    @Test
    void builderCopiesValues() {
        FileConfigurationProperties properties = FileConfigurationProperties.newBuilder()
                .header("THE HEADER")
                .footer("THE FOOTER")
                .createParentDirectories(false)
                .charset(StandardCharsets.ISO_8859_1)
                .build();
        assertEquals("THE HEADER", properties.getHeader());
        assertEquals("THE FOOTER", properties.getFooter());
        assertFalse(properties.createParentDirectories());
        assertEquals(StandardCharsets.ISO_8859_1, properties.getCharset());
    }

    @Test
    void builderCtorCopiesValues() {
        FileConfigurationProperties properties = FileConfigurationProperties.newBuilder()
                .outputNulls(true)
                .header("A")
                .footer("B")
                .createParentDirectories(false)
                .charset(StandardCharsets.ISO_8859_1)
                .build()
                .toBuilder()
                .build();

        assertThat(properties.outputNulls(), is(true));
        assertThat(properties.getHeader(), is("A"));
        assertThat(properties.getFooter(), is("B"));
        assertThat(properties.createParentDirectories(), is(false));
        assertThat(properties.getCharset(), is(StandardCharsets.ISO_8859_1));
    }
}