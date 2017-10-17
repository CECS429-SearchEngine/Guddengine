package com.gudden.maven.model;

import java.util.HashSet;
import java.util.Set;

public class Normalizer {

	public String normalize(String token) {
		return token.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$|\'", "");
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/**
	 * Splits words with hyphens in them and stores both the separated words, and
	 * the original hyphenated word without the hyphens in between the words.
	 * 
	 * @param token
	 *            The string
	 * @return A set of all strings from the hyphenated word
	 */
	public Set<String> splitHypenWords(String token) {
		Set<String> tokenSet = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		String[] tokens = token.split("-");
		for (String each : tokens) {
			tokenSet.add(each);
			sb.append(each);
		}
		tokenSet.add(sb.toString());
		return tokenSet;
	}
	
	// ------------------------------------------------------------------------------------------------------

	/**
	 * Stems a string using a stemming algorithm.
	 * 
	 * @param token
	 *            The string
	 * @return The stemmed string
	 */
	public String stem(String token) {
		Stemmer stemmer = new Stemmer();
		stemmer.add(token.toCharArray(), token.length());
		stemmer.stem();
		return stemmer.toString();
	}
}
