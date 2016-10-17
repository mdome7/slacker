package com.labs2160.slacker.server;

import java.nio.file.Paths;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.core.plugin.PluginManager;

public class PluginManagerIT {

    private static final Logger logger = LoggerFactory.getLogger(PluginManagerIT.class);

    @Test
    public void testLoadAndGetAction() throws ClassNotFoundException {
        String baseDir = System.getProperty("baseDir");
        logger.info("Base Directory: " + baseDir);
        PluginManager manager = new PluginManager(Paths.get(baseDir));
        Action a = manager.getActionInstance(null, "com.labs2160.slacker.plugin.extra.MathAction");
    }
}
