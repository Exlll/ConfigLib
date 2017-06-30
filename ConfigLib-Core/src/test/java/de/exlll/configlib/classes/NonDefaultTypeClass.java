package de.exlll.configlib.classes;

import de.exlll.configlib.Configuration;

import java.nio.file.Path;

public class NonDefaultTypeClass extends Configuration {
    public DefaultTypeClass defaultTypeClass;

    public NonDefaultTypeClass(Path configPath) {
        super(configPath);
        this.defaultTypeClass = new DefaultTypeClass(configPath);
    }
}
