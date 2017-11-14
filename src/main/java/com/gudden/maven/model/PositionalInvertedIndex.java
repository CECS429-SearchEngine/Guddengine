package com.gudden.maven.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class PositionalInvertedIndex extends Index<List<PositionalPosting>> {

	private Map<String, List<PositionalPosting>> index;
	
	// ------------------------------------------------------------------------------------------------------
	
	public PositionalInvertedIndex() {
		this.index = new HashMap<String, List<PositionalPosting>>();
	}

	// ------------------------------------------------------------------------------------------------------

	public void add(String term, int id, int position) {
		if (!contains(term))
			createPosting(term);
		if (!contains(term, id))
			addPosting(term, id);
		PositionalPosting posting = getLatestPosting(getPostings(term));
		posting.addPosition(position);
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	public String[] getDictionary() {
		SortedSet<String> tokens = new TreeSet<String>(this.index.keySet());
		return tokens.toArray(new String[tokens.size()]);
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	public List<PositionalPosting> getPostings(String term) {
		return this.index.get(term);
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	public void resetIndex() {
		this.index = new HashMap<String, List<PositionalPosting>>();
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	public int size() {
		return index.size();
	}

	// ------------------------------------------------------------------------------------------------------

	private void addPosting(String term, int id) {
		getPostings(term).add(new PositionalPosting(id));
	}

	// ------------------------------------------------------------------------------------------------------

	private boolean contains(String term, int id) {
		List<PositionalPosting> postings = getPostings(term);
		return !postings.isEmpty() && getLatestPosting(postings).getId() >= id;
	}

	// ------------------------------------------------------------------------------------------------------

	private PositionalPosting getLatestPosting(List<PositionalPosting> postings) {
		return postings.get(postings.size() - 1);
	}
	
	// ------------------------------------------------------------------------------------------------------

	@Override
	protected boolean contains(String term) {
		return this.index.containsKey(term);
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	protected void createPosting(String term) {
		this.index.put(term, new ArrayList<PositionalPosting>());
	}

}
