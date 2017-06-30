package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import de.exlll.configlib.classes.DefaultTypeClass;
import de.exlll.configlib.classes.NonDefaultTypeClass;
import de.exlll.configlib.classes.SimpleTypesClass;
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

    @Test
    public void simpleTypesConfigSavesAndLoads() throws Exception {
        Configuration cfg = new SimpleTypesClass(configPath);
        cfg.save();
        cfg.load();
    }

    @Test
    public void defaultTypesConfigSavesAndLoads() throws Exception {
        Configuration cfg = new DefaultTypeClass(configPath);
        cfg.save();
        cfg.load();
    }

    @Test
    public void nonDefaultConfigSavesAndLoads() throws Exception {
        Configuration cfg = new NonDefaultTypeClass(configPath);
        cfg.save();
        cfg.load();
    }
}