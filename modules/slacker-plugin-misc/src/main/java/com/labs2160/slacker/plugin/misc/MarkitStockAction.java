package com.labs2160.slacker.plugin.misc;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.NoArgumentsFoundException;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.WorkflowContext;

/**
 * {
    "Name":"Apple Inc",
    "Symbol":"AAPL",
    "LastPrice":524.49,
    "Change":15.6,
    "ChangePercent":3.06549549018453,
    "Timestamp":"Wed Oct 23 13:39:19 UTC-06:00 2013",
    "MSDate": 41570.568969907,
    "MarketCap":476497591530,
    "Volume":397562,
    "ChangeYTD":532.1729,
    "ChangePercentYTD":-1.44368493773359,
    "High":52499,
    "Low":519.175,
    "Open":519.175
}
 */
public class MarkitStockAction implements Action {
	
	private final static Logger logger = LoggerFactory.getLogger(MarkitStockAction.class);
	
	private WebTarget target;
	
	public MarkitStockAction() {
		Client client = ClientBuilder.newClient(new ClientConfig());
		target = client.target("http://dev.markitondemand.com/Api/v2/Quote/json");
	}

	@Override
	public boolean execute(WorkflowContext ctx) throws SlackerException {
		if (ctx.getWorkflowArgs() == null || ctx.getWorkflowArgs().length == 0) {
			throw new NoArgumentsFoundException("Stock symbol argument is required");
		}
		final String stockSymbol = ctx.getWorkflowArgs()[0].replaceAll("\"","");
		MarkitStockInfo stock = target.queryParam("symbol", stockSymbol)
				.request().accept(MediaType.APPLICATION_JSON_TYPE)
				.get(MarkitStockInfo.class);
		if (stock.getMessage() != null && stock.getMessage().startsWith("No symbol matches")) {
			ctx.setResponseMessage("No match found for symbol: " + stockSymbol);
		} else {
			ctx.setResponseMessage(stock.toString());
		}
		//ctx.setOutputMessage("87.99");
		return true;
	}
}
