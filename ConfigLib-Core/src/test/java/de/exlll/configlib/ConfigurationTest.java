package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final FileSystem fs = Jimfs.newFileSystem();
    private Path filePath;

    @Before
    public void setUp() throws Exception {
        filePath = fs.getPath("/dir1/dir2/file1");
        Files.deleteIfExists(filePath);
        Files.deleteIfExists(filePath.getParent());
    }

    @Test
    public void constructorRequiresNonNullPath() throws Exception {
        exception.expect(NullPointerException.class);
        new Configuration(null) {
        };
    }

    @Test
    public void saveCreatesParentDirectories() throws Exception {
        Path dirPath = filePath.getParent();

        assertThat(Files.notExists(dirPath), is(true));

        new Configuration(filePath) {
        }.save();

        assertThat(Files.exists(dirPath), is(true));
    }

    @Test
    public void saveDoesntThrowExceptionIfDirectoryAlreadyCreated() throws Exception {
        Configuration configuration = new Configuration(filePath) {
        };
        configuration.save();
        configuration.save();
    }

    @Test
    public void save() throws Exception {
        new TestConfiguration(filePath).save();

        String config = new ConfigurationReader(filePath).read();

        assertThat(config, is(TestConfiguration.CONFIG_AS_STRING));
    }

    @Test
    public void load() throws Exception {
        new OriginalTestClass(filePath).save();
        ChangedTestClass cls = new ChangedTestClass(filePath);
        cls.loadAndSave();

        assertThat(cls.a, is(0));
        assertThat(cls.b, is(1));
        assertThat(cls.c, is(4));
    }

    @Test
    public void loadAndSaveSavesFile() throws Exception {
        new OriginalTestClass(filePath).loadAndSave();

        assertThat(Files.exists(filePath), is(true));
    }

    @Test
    public void postLoadHookExecutedAfterLoad() throws Exception {
        HookTestClass cls = new HookTestClass(filePath);
        cls.save();
        assertThat(cls.hookCalled, is(false));
        cls.load();
        assertThat(cls.hookCalled, is(true));
    }

    @Test
    public void postLoadHookExecutedAfterLoadAndSaveIfPathNotExists() throws Exception {
        HookTestClass cls = new HookTestClass(filePath);
        assertThat(cls.hookCalled, is(false));
        cls.loadAndSave();
        assertThat(cls.hookCalled, is(true));
    }

    @Test
    public void postLoadHookExecutedAfterLoadAndSaveIfPathExists() throws Exception {
        HookTestClass cls = new HookTestClass(filePath);
        cls.save();
        assertThat(cls.hookCalled, is(false));
        cls.loadAndSave();
        assertThat(cls.hookCalled, is(true));
    }

    private static final class HookTestClass extends Configuration {
        private transient boolean hookCalled;

        public HookTestClass(Path configPath) {
            super(configPath);
        }

        @Override
        protected void postLoadHook() {
            hookCalled = true;
        }
    }

    private static final class OriginalTestClass extends Configuration {
        private int a = 0;
        private int b = 1;

        public OriginalTestClass(Path configPath) {super(configPath);}
    }

    private static final class ChangedTestClass extends Configuration {
        private int a = 2;
        private int b = 3;
        private int c = 4;

        public ChangedTestClass(Path configPath) {super(configPath);}
    }
}