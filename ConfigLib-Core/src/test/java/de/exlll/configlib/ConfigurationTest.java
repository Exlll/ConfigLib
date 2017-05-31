package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {

    private FileSystem fileSystem;
    private Path configPath;

    @Before
    public void setUp() throws Exception {
        fileSystem = Jimfs.newFileSystem();
        configPath = fileSystem.getPath("/a/b/config.yml");
    }

    @After
    public void tearDown() throws Exception {
        fileSystem.close();
    }

    @Test
    public void saveCreatesParentDirectories() throws Exception {
        TestConfiguration cfg = new TestConfiguration(configPath);
        assertThat(Files.exists(configPath.getParent()), is(false));
        
        cfg.save();
        assertThat(Files.exists(configPath.getParent()), is(true));
    }

    @Test
    public void saveWritesConfig() throws Exception {
        TestConfiguration cfg = new TestConfiguration(configPath);
        assertThat(Files.exists(configPath), is(false));

        cfg.save();
        assertThat(Files.exists(configPath), is(true));
        assertThat(ConfigReader.read(configPath), is(TestConfiguration.CONFIG_AS_TEXT));
    }
}