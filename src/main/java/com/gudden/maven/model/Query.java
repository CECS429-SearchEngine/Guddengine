package com.gudden.maven.model;

import java.util.ArrayList;
import java.util.List;

public class Query {
	
	private List<SubQuery> SubQueries;

	// ------------------------------------------------------------------------------------------------------

	public Query(String query) {
		this.SubQueries = createSubQueries(splitQuery(query.toLowerCase()));
	}

	// ------------------------------------------------------------------------------------------------------
	
	public List<SubQuery> getSubQueries() {
		return this.SubQueries;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private List<SubQuery> createSubQueries(String[] subQueries) {
		List<SubQuery> tempQueries = new ArrayList<SubQuery>();
		for (String each : subQueries) tempQueries.add(new SubQuery(each));
		return tempQueries;
	}

	// ------------------------------------------------------------------------------------------------------

	private String[] splitQuery(String query) {
		return query.trim().split("(\\s+\\+\\s+)");
	}
	
}
