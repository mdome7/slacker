package com.labs2160.slacker.api.misc;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.WorkflowException;
import com.labs2160.slacker.plugin.misc.StockAction;

public class StockActionTest {
	
	private static final Logger logger = LoggerFactory.getLogger(StockActionTest.class);

	private StockAction action;
	
	@Before
	public void before() {
		action = new StockAction();
	}
	
	@Test
	public void testExecute() {
		try {
			logger.info(getStock("AAPL"));
		} catch (WorkflowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getStock(String symbol) throws WorkflowException {
		WorkflowContext ctx = new WorkflowContext("stock", symbol);
		Assert.assertTrue(action.execute(ctx));
		return ctx.getResponseMessage();
	}
}
