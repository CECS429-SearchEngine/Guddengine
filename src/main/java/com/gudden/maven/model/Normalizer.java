package com.gudden.maven.model;

import java.util.HashSet;
import java.util.Set;

public class Normalizer {

	public String normalize(String token) {
		return token.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$|\'", "");
	}

	// ------------------------------------------------------------------------------------------------------

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

	public String stem(String token) {
		Stemmer stemmer = new Stemmer();
		stemmer.add(token.toCharArray(), token.length());
		stemmer.stem();
		return stemmer.toString();
	}
}
