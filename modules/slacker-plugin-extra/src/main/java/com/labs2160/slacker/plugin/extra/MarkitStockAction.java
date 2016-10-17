package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.api.annotation.ActionDescription;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Properties;

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
@ActionDescription(
        name = "Stock Price",
        description = "Retrieves current price for the specified stock symbol",
        argsSpec = "<stock symbol>",
        argsExample = "AAPL"
)
public class MarkitStockAction extends SimpleAbstractAction {

    private final static Logger logger = LoggerFactory.getLogger(MarkitStockAction.class);

    private WebTarget target;

    public MarkitStockAction() {
        Client client = ClientBuilder.newClient(new ClientConfig());
        target = client.target("http://dev.markitondemand.com/Api/v2/Quote/json");
    }

    @Override
    public boolean execute(SlackerContext ctx) throws SlackerException {
        if (ctx.getRequestArgs() == null || ctx.getRequestArgs().length == 0) {
            throw new NoArgumentsFoundException("Stock symbol argument is required");
        }
        final String stockSymbol = ctx.getRequestArgs()[0].replaceAll("\"","");
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
