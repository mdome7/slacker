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
 * Google returns html:
 * // [
{
"id": "22144"
,"t" : "AAPL"
,"e" : "NASDAQ"
,"l" : "126.60"
,"l_fix" : "126.60"
,"l_cur" : "126.60"
,"s": "2"
,"ltt":"4:09PM EST"
,"lt" : "Mar 6, 4:09PM EST"
,"lt_dts" : "2015-03-06T16:09:55Z"
,"c" : "+0.19"
,"c_fix" : "0.19"
,"cp" : "0.15"
,"cp_fix" : "0.15"
,"ccol" : "chg"
,"pcls_fix" : "126.41"
,"el": "127.05"
,"el_fix": "127.05"
,"el_cur": "127.05"
,"elt" : "Mar 6, 7:59PM EST"
,"ec" : "+0.45"
,"ec_fix" : "0.45"
,"ecp" : "0.36"
,"ecp_fix" : "0.36"
,"eccol" : "chg"
,"div" : "0.47"
,"yld" : "1.48"
}
]
 *
 */
public class GoogleStockAction implements Action {
	
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
	
	public GoogleStockAction() {
		Client client = ClientBuilder.newClient(new ClientConfig());
		target = client.target("http://finance.google.com/finance/info");
	}

	@Override
	public boolean execute(WorkflowContext ctx) throws WorkflowException {
		if (ctx.getWorkflowArgs() == null || ctx.getWorkflowArgs().length == 0) {
			throw new WorkflowException("Stock symbol argument is required");
		}
//		StockInfoResponse stock = target.queryParam("q", ctx.getWorkflowArgs()[0])
//				.request().accept(MediaType.APPLICATION_JSON_TYPE)
//				.get(StockInfoResponse.class);
//		ctx.setResponseMessage(stock.getResults().price);
		String stock = target.queryParam("q", ctx.getWorkflowArgs()[0])
				.request().accept(MediaType.APPLICATION_JSON_TYPE)
				.get(String.class);
		ctx.setResponseMessage(stock);
		//ctx.setOutputMessage("87.99");
		return true;
	}
}
