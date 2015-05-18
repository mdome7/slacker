package com.labs2160.slacker.plugin.misc.yahoo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class YahooResponse<T> {

	@JsonIgnoreProperties(ignoreUnknown=true)
	public class Query<V> {
		private V results;

		public V getResults() {
			return results;
		}

		public void setResults(V results) {
			this.results = results;
		}
	}

	private Query<T> query;

	public Query<T> getQuery() {
		return query;
	}

	public void setQuery(Query<T> query) {
		this.query = query;
	}
	
	@JsonIgnore
	public T getResults() {
		if (query != null) {
			return query.getResults();
		}
		return null;
	}
}
