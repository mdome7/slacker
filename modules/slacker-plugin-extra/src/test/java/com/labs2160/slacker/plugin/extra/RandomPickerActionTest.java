package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.response.SlackerOutput;
import com.labs2160.slacker.api.response.TextOutput;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.SlackerContext;

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
		boolean pickedA = false;
		boolean pickedB = false;
		boolean pickedC = false;
		int maxIter = 300;
		int iter = 0;
		while((!pickedA || !pickedB || !pickedC) && iter++ < maxIter) {
			String output = pick(1, "a b c");
			Assert.assertEquals(1, output.split(" ").length);
			if ("a".equals(output)) pickedA = true;
			if ("b".equals(output)) pickedB = true;
			if ("c".equals(output)) pickedC = true;
		}
		logger.info("Number of iterations to pick each at least once: {}", iter);
	}

	@Test
	public void testMultiPick() throws SlackerException {
		String output = pick(2, "a b c");
		Assert.assertEquals(2, output.split(" ").length);
	}

	@Test
	public void testPickAll() throws SlackerException {
		String output = pick(3, "a b c");
		Assert.assertEquals(3, output.split(" ").length);
	}

	@Test
	public void testPickOverflow() throws SlackerException {
		String output = pick(4, "a b c");
		Assert.assertEquals(3, output.split(" ").length);
	}

	@Test(expected=InvalidRequestException.class)
	public void testPickZero() throws SlackerException {
		SlackerContext ctx = new SlackerContext(FAKE_PATH, new String[]{"0", "a", "b", "c"});
		action.execute(ctx);
	}

	@Test(expected=InvalidRequestException.class)
	public void testPickNegative() throws SlackerException {
		SlackerContext ctx = new SlackerContext(FAKE_PATH, new String[]{"-1", "a", "b", "c"});
		action.execute(ctx);
	}

	@Test(expected=InvalidRequestException.class)
	public void testPickInvalidNumber() throws SlackerException {
		SlackerContext ctx = new SlackerContext(FAKE_PATH, new String[]{"a", "b", "c"});
		action.execute(ctx);
	}

	private String pick(int pickCount, String choices) throws SlackerException {
		SlackerContext ctx = new SlackerContext(FAKE_PATH, (pickCount + " " + choices).split(" "));
		logger.debug("Picking {} from: {}", pickCount, choices);
		SlackerOutput output = action.execute(ctx);
		org.junit.Assert.assertTrue(output instanceof TextOutput);
		String message = ((TextOutput) output).getMessage();
		logger.debug("\t=> {}", message);
		return message;
	}
}
