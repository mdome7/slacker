package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.response.SlackerOutput;
import com.labs2160.slacker.api.response.TextOutput;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.SlackerContext;

public class MarkitStockActionIT {

	private static final Logger logger = LoggerFactory.getLogger(MarkitStockActionIT.class);

	private MarkitStockAction action;

	@Before
	public void before() {
		action = new MarkitStockAction();
	}

	@Test
	public void testExecute() {
		try {
			logger.info(getStock("aapl"));
		} catch (SlackerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testExecuteInvalid() {
		try {
			logger.info(getStock("michael"));
		} catch (SlackerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getStock(String symbol) throws SlackerException {
		SlackerContext ctx = new SlackerContext(new String [] {"stock"}, new String [] {symbol});
		SlackerOutput output = action.execute(ctx);
		Assert.assertTrue(output instanceof TextOutput);
		return ((TextOutput) output).getMessage();
	}
}
