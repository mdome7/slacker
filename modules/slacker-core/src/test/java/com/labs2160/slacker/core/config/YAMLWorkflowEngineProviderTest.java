package com.labs2160.slacker.core.config;

import java.io.IOException;
import java.nio.file.Paths;

import com.labs2160.slacker.api.InitializationException;
import org.junit.Test;

import com.labs2160.slacker.core.engine.WorkflowEngine;

public class YAMLWorkflowEngineProviderTest {

    @Test
    public void test() throws IOException, InitializationException {
        SlackerConfig config = new SlackerConfig();
        config.setConfigFile(Paths.get("./src/test/resources/valid_config.yaml"));
        YAMLWorkflowEngineProvider provider = new YAMLWorkflowEngineProvider(config);

        WorkflowEngine engine = provider.provide();

    }

}
