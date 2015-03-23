package com.labs2160.slacker.core.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ThreadExecutorProvider {
    private static final Logger logger = LoggerFactory.getLogger(ThreadExecutorProvider.class);

    @Inject @Named("config")
    private SlackerConfig config;

    @Produces @ApplicationScoped @Named("mainExecutor")
    public ExecutorService provide() {
        logger.info("Max threads: {}", config.getMaxThreads());
        return Executors.newFixedThreadPool(config.getMaxThreads());
    }
}
