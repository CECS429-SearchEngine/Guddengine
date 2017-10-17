package com.gudden.maven.model;

import java.util.Map;

public abstract class Index <T> {

	protected Map<String, T> index;

	// ------------------------------------------------------------------------------------------------------
	
	public abstract String[] getDictionary();

	// ------------------------------------------------------------------------------------------------------
	
	public abstract T getPostings(String token);

	// ------------------------------------------------------------------------------------------------------
	
	public abstract void resetIndex();
	
	// ------------------------------------------------------------------------------------------------------
	
	public abstract int size();

	// ------------------------------------------------------------------------------------------------------
	
	protected abstract boolean contains(String token);

	// ------------------------------------------------------------------------------------------------------
	
	protected abstract void createPosting(String token);

}
