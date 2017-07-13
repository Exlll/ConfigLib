package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class VersionTest {
    private static final String CONFIG_AS_STRING =
            "# class\n\n# field\nk: 0\n\n# c1\n";
    private static final String VERSION_CONFIG_AS_STRING =
            "# class\n\n# field\nk: 0\n\n# c1\nv: 1.2.3-alpha\n";
    private static final String OLD_VERSION_CONFIG_AS_STRING =
            "# class\n\n# field\nk: 0\n\n# c1\nv: 1.2.2-alpha\n";
    @Rule
    public ExpectedException exception = ExpectedException.none();
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
    public void versionSaved() throws Exception {
        new VersionConfiguration(configPath).save();
        assertThat(ConfigReader.read(configPath), is(VERSION_CONFIG_AS_STRING));
    }

    @Test
    public void saveThrowsExceptionIfVersionNameClash() throws Exception {
        exception.expect(ConfigException.class);
        exception.expectMessage("Solution: Rename the field or use a " +
                "different version field name.");
        new VersionConfigurationWithVersionField(configPath).save();
    }

    @Test
    public void currentFileVersionReturnsEmptyOptionalIfFileDoesntExist() throws Exception {
        Configuration cfg = new VersionConfiguration(configPath);
        assertThat(cfg.currentFileVersion(), nullValue());
    }

    @Test
    public void currentFileVersionReturnsOptionalWithVersion() throws Exception {
        Configuration cfg = new VersionConfiguration(configPath);
        cfg.save();
        assertThat(cfg.currentFileVersion(), is("1.2.3-alpha"));
    }

    @Test
    public void updateRenameApplied() throws Exception {
        Files.createDirectories(configPath.getParent());
        ConfigWriter.write(configPath, CONFIG_AS_STRING);

        final Path oldPath = fileSystem.getPath(configPath.toString() + "-old");

        new VersionConfiguration(configPath).save();
        assertThat(ConfigReader.read(configPath), is(VERSION_CONFIG_AS_STRING));
        assertThat(ConfigReader.read(oldPath), is(CONFIG_AS_STRING));
    }

    @Test
    public void updateRenameAppendsOldVersion() throws Exception {
        final Path oldPath = fileSystem.getPath(configPath.toString() + "-v1.2.2-alpha");

        Files.createDirectories(configPath.getParent());
        ConfigWriter.write(configPath, OLD_VERSION_CONFIG_AS_STRING);
        ConfigWriter.write(oldPath, "123");

        new VersionConfiguration(configPath).save();

        assertThat(ConfigReader.read(configPath), is(VERSION_CONFIG_AS_STRING));
        assertThat(ConfigReader.read(oldPath), is(OLD_VERSION_CONFIG_AS_STRING));
    }

    @Comment("class")
    @Version(version = "1.2.3-alpha",
            fieldName = "v",
            fieldComments = {"", "c1"},
            updateStrategy = UpdateStrategy.DEFAULT_RENAME)
    private static final class VersionConfiguration extends Configuration {
        @Comment("field")
        private int k;

        protected VersionConfiguration(Path configPath) {
            super(configPath);
        }
    }

    @Version
    private static final class VersionConfigurationWithVersionField
            extends Configuration {
        private int version;

        protected VersionConfigurationWithVersionField(Path configPath) {
            super(configPath);
        }
    }
}
