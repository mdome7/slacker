package com.labs2160.slacker.core.config;

import java.util.Properties;

public class SlackerConfig {

	/** max number of threads for the entire application */
	private int maxThreads;


	private Properties properties;

	public SlackerConfig() {}

	public SlackerConfig(Properties properties) {
	    maxThreads = Integer.parseInt(properties.getProperty("app.maxThreads"));
	    this.properties = properties;
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public void setProperty(String key, String value) {
	    this.properties.setProperty(key, value);
	}

	public String getProperty(String key) {
	    return this.properties.getProperty(key);
	}
}
