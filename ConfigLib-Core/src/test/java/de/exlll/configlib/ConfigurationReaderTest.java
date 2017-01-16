package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.Before;
import org.junit.Test;

import java.io.Writer;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationReaderTest {
    private static final FileSystem fs = Jimfs.newFileSystem();
    private ConfigurationReader reader;

    @Before
    public void setUp() throws Exception {
        Path configPath = fs.getPath("/config.yml");
        Writer writer = Files.newBufferedWriter(configPath);
        writer.write(TestConfiguration.CONFIG_AS_STRING);
        writer.close();
        reader = new ConfigurationReader(configPath);
    }

    @Test
    public void readFileReadsFile() throws Exception {
        assertThat(reader.read(), is(TestConfiguration.CONFIG_AS_STRING));
    }
}