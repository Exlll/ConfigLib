package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationWriterTest {
    private static final FileSystem fs = Jimfs.newFileSystem();
    private Path configPath;
    private ConfigurationWriter writer;
    private String dump;

    @Before
    public void setUp() throws Exception {
        configPath = fs.getPath("/config.yml");
        FilteredFieldStreamSupplier streamSupplier = new FilteredFieldStreamSupplier(
                TestConfiguration.class, ConfigurationFieldFilter.INSTANCE);
        Comments comments = Comments.from(streamSupplier);
        FieldMapper mapper = new FieldMapper(streamSupplier);
        Map<String, Object> valuesByFieldNames = mapper.mapFieldNamesToValues(
                new TestConfiguration(configPath));
        dump = new YamlSerializer().serialize(valuesByFieldNames);
        writer = new ConfigurationWriter(configPath, comments);
    }

    @Test
    public void write() throws Exception {
        writer.write(dump);
        String read = new ConfigurationReader(configPath).read();

        assertThat(read, is(TestConfiguration.CONFIG_AS_STRING));
    }
}