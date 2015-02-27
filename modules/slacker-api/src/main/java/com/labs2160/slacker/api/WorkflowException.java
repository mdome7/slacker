package com.labs2160.slacker.api;

/**
 * Checked exception that can be thrown during workflow execution.
 * @author mike
 *
 */
public class WorkflowException extends Exception {

	private static final long serialVersionUID = 7313537574143370074L;

	public WorkflowException(String msg) {
		super(msg);
	}
	
	public WorkflowException(String msg, Throwable t) {
		super(msg, t);
	}
}
