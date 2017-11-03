package com.gudden.maven.model;

public abstract class Index<T> {

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
