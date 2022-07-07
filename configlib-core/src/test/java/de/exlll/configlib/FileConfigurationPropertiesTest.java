package de.exlll.configlib;

import org.junit.jupiter.api.Test;

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
}