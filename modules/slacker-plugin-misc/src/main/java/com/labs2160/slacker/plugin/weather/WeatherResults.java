package com.labs2160.slacker.plugin.weather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WeatherResults {

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class Item {
		public List<Forecast> forecast;
	}
	
	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class Forecast {
		public String date;
		public long low;
		public long high;
		public String text;
		
		public String toString() {
			return date + ": temp " + low + " to " + high + " - " + text;
		}
	}
	
	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class Channel {
		public String title;
		public Item item;
	}
	
	public Channel channel;

	@JsonIgnore
	public String getTitle() {
		if (channel != null) {
			return channel.title;
		}
		return null;
	}
	
	@JsonIgnore
	public List<Forecast> getForecast() {
		if (channel != null && channel.item != null) {
			return channel.item.forecast;
		}
		return null;
	}

	@JsonIgnore
	public Forecast getImmediateForecast() {
		if (channel != null && channel.item != null && channel.item.forecast != null && !channel.item.forecast.isEmpty()) {
			return channel.item.forecast.get(0);
		}
		return null;
	}
	
	public String toString() {
		Forecast fc = getImmediateForecast();
		return getTitle() + " - " + (fc == null ? "no forecast available" : fc);
	}
}