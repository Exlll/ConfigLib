package de.exlll.configlib.configs;

import de.exlll.configlib.Configuration;

import java.nio.file.Path;

public class CustomConfiguration extends Configuration {
    private String config = "config1";
    private CustomSection cs1 = new CustomSection();
    private CustomSection cs2 = new CustomSection();

    public CustomConfiguration(Path configPath) {
        super(configPath);
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config1) {
        this.config = config1;
    }

    public CustomSection getCs1() {
        return cs1;
    }

    public void setCs1(CustomSection cs1) {
        this.cs1 = cs1;
    }

    public CustomSection getCs2() {
        return cs2;
    }

    public void setCs2(CustomSection cs2) {
        this.cs2 = cs2;
    }
}
