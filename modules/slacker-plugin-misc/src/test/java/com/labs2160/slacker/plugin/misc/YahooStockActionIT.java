package com.labs2160.slacker.plugin.misc;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.plugin.misc.YahooStockAction;

public class YahooStockActionIT {
	
	private static final Logger logger = LoggerFactory.getLogger(YahooStockActionIT.class);

	private YahooStockAction action;
	
	@Before
	public void before() {
		action = new YahooStockAction();
	}
	
	@Test
	public void testExecute() {
		try {
			logger.info(getStock("AAPL"));
		} catch (SlackerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getStock(String symbol) throws SlackerException {
		WorkflowContext ctx = new WorkflowContext(new String [] {"stock"}, new String [] {symbol});
		Assert.assertTrue(action.execute(ctx));
		return ctx.getResponseMessage();
	}
}
