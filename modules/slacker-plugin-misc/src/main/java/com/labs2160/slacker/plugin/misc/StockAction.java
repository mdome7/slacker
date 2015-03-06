package com.labs2160.slacker.plugin.misc;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.WorkflowException;

/**
 * select * from pm.finance where symbol="YHOO"
 * 
 * https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20pm.finance%20where%20symbol%3D%22YHOO%22&format=json&diagnostics=true&callback=
 *
 */
public class StockAction implements Action {
	
	private WebTarget target;
	
	@JsonIgnoreProperties(ignoreUnknown=true)
	@JsonRootName("query")
	private class StockInfoResponse {
		private class StockQuote {
			private final String symbol;
			
			private final String price;
			
			public StockQuote(@JsonProperty("symbol") String symbol, @JsonProperty("LastTradePriceOnly") String price) {
				this.symbol = symbol;
				this.price = price;
			}
		}
		
		private StockQuote results;

		public StockQuote getResults() {
			return results;
		}

		public void setResults(StockQuote results) {
			this.results = results;
		}
	}
	
	public StockAction() {
		Client client = ClientBuilder.newClient(new ClientConfig());
		target = client.target("http://finance.google.com/finance/info");
	}

	@Override
	public boolean execute(WorkflowContext ctx) throws WorkflowException {
		if (ctx.getWorkflowArgs() == null || ctx.getWorkflowArgs().length == 0) {
			throw new WorkflowException("Stock symbol argument is required");
		}
		StockInfoResponse stock = target.queryParam("q", ctx.getWorkflowArgs()[0])
				.request().accept(MediaType.APPLICATION_JSON_TYPE)
				.get(StockInfoResponse.class);
		ctx.setResponseMessage(stock.getResults().price);
		//ctx.setOutputMessage("87.99");
		return true;
	}
}
