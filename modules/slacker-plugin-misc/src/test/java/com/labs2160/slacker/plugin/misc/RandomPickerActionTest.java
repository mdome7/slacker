package com.labs2160.slacker.plugin.misc;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.WorkflowContext;

public class RandomPickerActionTest {

	private static final Logger logger = LoggerFactory.getLogger(RandomPickerActionTest.class);
	
	private static final String [] FAKE_PATH = {"pick"};
	
	private static RandomPickerAction action;
	
	@BeforeClass
	public static void before() {
		action = new RandomPickerAction();
	}
	
	@Test
	public void testSinglePick() throws SlackerException {
		final int num = 1;
		WorkflowContext ctx = new WorkflowContext(FAKE_PATH, new String[]{"" + num, "a", "b", "c"});
		action.execute(ctx);
		String [] pickedStr = ctx.getResponseMessage().split(" ");
		Assert.assertEquals(1, pickedStr.length);
		logger.debug("Pick {}: {}", num, pickedStr);
	}

	@Test
	public void testMultiPick() throws SlackerException {
		final int num = 2;
		WorkflowContext ctx = new WorkflowContext(FAKE_PATH, new String[]{"" + num, "a", "b", "c"});
		action.execute(ctx);
		String [] pickedStr = ctx.getResponseMessage().split(" ");
		Assert.assertEquals(2, pickedStr.length);
		logger.debug("Pick {}: {}", num, pickedStr);
	}

	@Test
	public void testPickAll() throws SlackerException {
		final int num = 3;
		WorkflowContext ctx = new WorkflowContext(FAKE_PATH, new String[]{"" + num, "a", "b", "c"});
		action.execute(ctx);
		String [] pickedStr = ctx.getResponseMessage().split(" ");
		Assert.assertEquals(num, pickedStr.length);
		logger.debug("Pick {}: {}", num, pickedStr);
	}
	
	@Test
	public void testPickOverflow() throws SlackerException {
		final int num = 4;
		WorkflowContext ctx = new WorkflowContext(FAKE_PATH, new String[]{"" + num, "a", "b", "c"});
		action.execute(ctx);
		String [] pickedStr = ctx.getResponseMessage().split(" ");
		Assert.assertEquals(3, pickedStr.length);
		logger.debug("Pick {}: {}", num, pickedStr);
	}

	@Test(expected=InvalidRequestException.class)
	public void testPickZero() throws SlackerException {
		WorkflowContext ctx = new WorkflowContext(FAKE_PATH, new String[]{"0", "a", "b", "c"});
		action.execute(ctx);
	}
	
	@Test(expected=InvalidRequestException.class)
	public void testPickNegative() throws SlackerException {
		WorkflowContext ctx = new WorkflowContext(FAKE_PATH, new String[]{"-1", "a", "b", "c"});
		action.execute(ctx);
	}

	@Test(expected=InvalidRequestException.class)
	public void testPickInvalidNumber() throws SlackerException {
		WorkflowContext ctx = new WorkflowContext(FAKE_PATH, new String[]{"a", "b", "c"});
		action.execute(ctx);
	}
}
