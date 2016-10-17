package com.labs2160.slacker.core.engine;

import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowRegistryTest {
	private static final Logger logger = LoggerFactory.getLogger(WorkflowRegistryTest.class);

	private WorkflowRegistry registry;

	@Before
	public void before() {
		registry = new WorkflowRegistry();
	}

	@Test
	public void testBasic() {
		Workflow stockPrice = new Workflow("Stock Price", "gets stock price");
		registry.addWorkflow(stockPrice, "stock");
		registry.addWorkflow(stockPrice, "stock", "price");
		registry.addWorkflow(new Workflow("Stock Chart", "gets stock chart"), "stock", "chart");
		registry.addWorkflow(new Workflow("Weather Info", "gets weather"), "weather");
		logger.debug(printRegistry());

		Assert.assertNull(registry.findWorkflowMatch("does not exist"));
        Assert.assertSame(stockPrice, registry.findWorkflowMatch(" stock ", " test ").getWorkflow());
		Assert.assertSame(stockPrice, registry.findWorkflowMatch("stock", "test").getWorkflow());
		Assert.assertSame(stockPrice, registry.findWorkflowMatch("stock", "price").getWorkflow());
		Assert.assertSame(stockPrice, registry.findWorkflowMatch("stock", "price", "aapl").getWorkflow());
	}

	@Test
	public void testFindWorkflowMatch() {
		Workflow a = new Workflow("a", "Workflow a");
		Workflow b = new Workflow("b", "Workflow b");
		registry.addWorkflow(a, "1", "a");
		registry.addWorkflow(b, "1", "a", "2", "b");

		Assert.assertNull(registry.findWorkflowMatch("a"));
		Assert.assertSame(a, registry.findWorkflowMatch("1", "a").getWorkflow());
		Assert.assertSame(a, registry.findWorkflowMatch("1", "a", "2").getWorkflow());
		Assert.assertSame(b, registry.findWorkflowMatch("1", "a", "2", "b").getWorkflow());
	}

	public String printRegistry() {
		StringBuilder sb = new StringBuilder();
		List<WorkflowMetadata> metadata = registry.getWorkflowMetadata();
		for (WorkflowMetadata md : metadata) {
			sb.append(StringUtils.join(md.getPath(), " "))
			.append(" - ")
			.append(md.getName())
			.append(" : ")
			.append(md.getDescription())
			.append("\n");
		}
		return sb.toString();
	}
}
