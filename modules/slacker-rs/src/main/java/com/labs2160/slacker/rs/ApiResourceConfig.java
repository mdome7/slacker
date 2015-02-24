package com.labs2160.slacker.rs;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NOTE: Not needed for now.  Use this if there is a need for a custom Application/Config.
 *
 * Created by mdometita on 11/7/14.
 */
public class ApiResourceConfig extends ResourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApiResourceConfig.class);

    public ApiResourceConfig() {
        // Packages to scan for resources (add more if needed)
        final String [] scanPackages = { this.getClass().getPackage().getName() };
        packages(scanPackages);
    }
}
