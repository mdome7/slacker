package com.labs2160.slacker.core.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SlackerConfigProvider {
    private static final Logger logger = LoggerFactory.getLogger(SlackerConfigProvider.class);

    @Produces @ApplicationScoped @Named("config")
    public SlackerConfig provide() throws IOException {
        Properties config = new Properties();
        final String configPath = System.getProperty("config","slacker.properties");
        logger.info("Loading config file: {}", configPath);
        config.load(new FileInputStream(configPath));

        return new SlackerConfig(config);
    }
}
