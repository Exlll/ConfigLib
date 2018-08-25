package de.exlll.configlib;

import de.exlll.configlib.configs.mem.InSharedMemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

class ConfigurationTest {
    private static class TestHook extends InSharedMemoryConfiguration {
        protected TestHook() { super(Properties.builder().build()); }
    }

    @Test
    void configExecutesPreSaveHook() {
        class A extends TestHook {
            int i = 0;

            @Override
            protected void preSave() { i++; }
        }
        A save = new A();
        save.save();
        assertThat(save.i, is(1));

        A load = new A();
        load.load();
        assertThat(load.i, is(1));
    }

    @Test
    void configExecutesPostLoadHook() {
        class A extends TestHook {
            int i = 0;

            @Override
            protected void postLoad() { i++; }
        }
        A save = new A();
        save.save();
        assertThat(save.i, is(0));

        A load = new A();
        load.load();
        assertThat(load.i, is(1));
    }
}