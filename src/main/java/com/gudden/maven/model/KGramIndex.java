package com.gudden.maven.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class KGramIndex extends Index<SortedSet<String>> {
	
	private Map<String, SortedSet<String>> index;
	private Set<String> termSet;
	
	// ------------------------------------------------------------------------------------------------------

	public KGramIndex() {
		this.index = new HashMap<String, SortedSet<String>>();
		this.termSet = new HashSet<String>();
	}

	// ------------------------------------------------------------------------------------------------------

	public void add(String term) {
		if (!termSet.contains(term)) {
			String specialTerm = encapsulate(term);
			for (int k = 1; k <= 3; k++) {
				List<String> grams = generateGrams(k, specialTerm);
				addPosting(grams, term);
			}
			this.termSet.add(term);
		}
		this.index.remove("$");
	}

	// ------------------------------------------------------------------------------------------------------

	public List<String> generateGrams(int k, String term) {
		List<String> grams = new ArrayList<String>();
		for (int i = 0; i <= term.length() - k; i++) {
			String gram = term.substring(i, i + k);
			grams.add(gram);
		}
		return grams;
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	public String[] getDictionary() {
		SortedSet<String> grams = new TreeSet<String>(this.index.keySet());
		return grams.toArray(new String[grams.size()]);
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	public SortedSet<String> getPostings(String term) {
		return this.index.get(term);
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	public void resetIndex() {
		this.index = new HashMap<String, SortedSet<String>>();

	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	public int size() {
		return this.index.size();
	}

	// ------------------------------------------------------------------------------------------------------

	private void addPosting(List<String> grams, String term) {
		for (String gram : grams) {
			if (!contains(gram))
				createPosting(gram);
			getPostings(gram).add(term);
		}
	}
	
	// ------------------------------------------------------------------------------------------------------

	private String encapsulate(String term) {
		StringBuilder sb = new StringBuilder();
		sb.append('$');
		sb.append(term);
		sb.append('$');
		return sb.toString();
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	protected boolean contains(String gram) {
		return this.index.containsKey(gram);
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	protected void createPosting(String gram) {
		this.index.put(gram, new TreeSet<String>());
	}
	
}
