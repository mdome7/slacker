package com.labs2160.slacker.api;


/**
 * Scheduled job that is run by the engine.
 * TODO: allow more options for scheduling other than simple periodic scheme
 */
public abstract class ScheduledJob implements Runnable {

	private final int period;
	
	public ScheduledJob(int period) {
		this.period = period;
	}
	
	public int getPeriod() {
		return period;
	}
}
