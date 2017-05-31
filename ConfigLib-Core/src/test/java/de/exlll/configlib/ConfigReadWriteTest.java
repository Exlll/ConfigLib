package de.exlll.configlib;

import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConfigReadWriteTest {

    @Test
    public void readWrite() throws Exception {
        FileSystem fs = Jimfs.newFileSystem();
        Path p = fs.getPath("/path/p");
        Files.createDirectories(p.getParent());

        String text = "Hello\nWorld\n";
        ConfigWriter.write(p, text);

        String read = ConfigReader.read(p);
        assertThat(read, is(text));

        fs.close();
    }
}