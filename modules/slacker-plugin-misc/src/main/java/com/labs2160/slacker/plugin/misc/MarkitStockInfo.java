package com.labs2160.slacker.plugin.misc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MarkitStockInfo {
	@JsonProperty("Name")
	private String name;

	@JsonProperty("Symbol")
	private String symbol;
	
	@JsonProperty("LastPrice")
	private Double lastPrice;

	@JsonProperty("ChangePercent")
	private Double changePct;

	@JsonProperty("Message")	
	private String message;

	public MarkitStockInfo() {}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Double getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(Double lastPrice) {
		this.lastPrice = lastPrice;
	}

	public Double getChangePct() {
		return changePct;
	}

	public void setChangePct(Double changePct) {
		this.changePct = changePct;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString() {
		return String.format("%s (%s) %.2f (%.2f%% change)",
				this.name, this.symbol, this.lastPrice, this.changePct);
	}
}