package com.labs2160.slacker.api;

/**
 * A Collector can be a passive one (i.e. "listener") or
 * an active one (i.e. "poller").
 * @author mike
 *
 */
public interface RequestCollector {

	void start();
	
	void shutdown();
	
	boolean isActive();
}
