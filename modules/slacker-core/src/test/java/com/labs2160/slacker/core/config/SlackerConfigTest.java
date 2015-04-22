package com.labs2160.slacker.core.config;

import junit.framework.Assert;

import org.junit.Test;

public class SlackerConfigTest {

    @Test
    public void testDefaultMaxThreads() {
        SlackerConfig config = new SlackerConfig();
        Assert.assertEquals(SlackerConfig.DEFAULT_MAX_THREADS, config.getMaxThreads());
    }
}
