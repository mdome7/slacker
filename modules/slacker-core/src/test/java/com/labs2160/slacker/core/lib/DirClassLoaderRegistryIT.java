package com.labs2160.slacker.core.lib;

import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirClassLoaderRegistryIT {

    private static final Logger logger = LoggerFactory.getLogger(DirClassLoaderRegistryIT.class);

    @Test
    public void testLoadBaseDir() {
        String baseDir = System.getProperty("baseDir");
        logger.info("Base Directory: " + baseDir);
        DirClassLoaderRegistry registry = new DirClassLoaderRegistry(Paths.get(baseDir));
        for (String dirName : registry.getClassLoaderDirNames()) {
            logger.debug("Loaded directory: {}", dirName);
        }
        Assert.assertFalse(registry.getClassLoaderDirNames().isEmpty());
    }
}
