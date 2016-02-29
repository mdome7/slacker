package com.labs2160.slacker.api;


/**
 * A Collector can be a passive one (i.e. "listener") or
 * an active one (i.e. "poller").
 * @author mike
 *
 */
public interface RequestCollector extends Configurable {

	/**
	 * Start the collector.
	 * @param handler used for processing any requests this collector may generate
	 */
	void start(RequestHandler handler);

	void shutdown();

	boolean isActive();

	/**
	 * Get SchedulerTasks that should be managed and run by the engine.
	 * @return
	 */
	SchedulerTask [] getSchedulerTasks();
}
