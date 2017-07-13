package de.exlll.configlib;

import java.io.IOException;
import java.nio.file.*;

/**
 * The constants of this enumerated type describe strategies used to update
 * different versions of configuration files. The strategies are only applied
 * if a version change is detected.
 */
public enum UpdateStrategy {
    /**
     * Updates the configuration file using the default strategy described at
     * {@link Configuration#save()}.
     */
    DEFAULT {
        @Override
        void update(Configuration config, Version version) {}
    },
    /**
     * Updates the configuration file using the {@link #DEFAULT} strategy.
     * Before the configuration is updated a copy of its current version is
     * saved. If the configuration uses versioning for the first time, the
     * copy is named "&lt;filename&gt;-old". Otherwise, the old version is
     * appended to the file name: "&lt;filename&gt;-v&lt;old version&gt;".
     *
     * @see UpdateStrategy#DEFAULT
     */
    DEFAULT_RENAME {
        @Override
        void update(Configuration config, Version version) throws IOException {
            final Path path = config.getPath();

            if (!Files.exists(path)) {
                return;
            }

            final String fileVersion = config.currentFileVersion();
            if (!version.version().equals(fileVersion)) {
                final FileSystem fs = path.getFileSystem();
                final String v = (fileVersion == null) ? "-old" : "-v" + fileVersion;
                final String fn = path.toString() + v;
                Files.move(path, fs.getPath(fn), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    };

    abstract void update(Configuration config, Version version)
            throws IOException;
}
