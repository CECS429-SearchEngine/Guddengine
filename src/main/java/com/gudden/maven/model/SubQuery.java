package com.gudden.maven.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubQuery {
	
	private List<String> literals;

	// ------------------------------------------------------------------------------------------------------

	public SubQuery(String subQuery) {
		this.literals = createLiterals(subQuery);
	}

	// ------------------------------------------------------------------------------------------------------

	public List<String> getLiterals() {
		return this.literals;
	}

	// ------------------------------------------------------------------------------------------------------

	public String toString() {
		return String.join(" ", literals);
	}

	// ------------------------------------------------------------------------------------------------------

	private List<String> createLiterals(String subQuery) {
		List<String> tempLiterals = new ArrayList<String>();
		// Anything encased in () refers to capture everything inside ()
		// [^\"] capture a character that is not a quotation mark.
		// \\S* capture zero or more non white-space character.
		// \".+?\" match one or more characters that are in quotation marks.
		// \\s match zero or more white spaced
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(subQuery);

		// gets words that are either separated by space or escape in quotes.
		while (m.find()) {
			String literal = m.group(1);
			if (literal.matches("near/\\d")) {
				if (m.find()) {
					String near = createNearLiteral(tempLiterals.remove(tempLiterals.size() - 1), 
												   literal, m.group(1));
					tempLiterals.add(near);
				} else {
					throw new IllegalStateException("Illegal Near Query against empty string.");
				}
			} else if (literal.matches("^\".*\"$")) {
				tempLiterals.add(String.join(" ", createLiterals(literal.substring(1, literal.length() - 1))));
			} else if (literal.contains("*")) {
				tempLiterals.add(createWildCardLiteral(literal));
			} else {
				tempLiterals.add(literal);
			}
		}
		return tempLiterals;
	}

	// ------------------------------------------------------------------------------------------------------

	private String createNearLiteral(String word, String near, String other) {
		StringBuilder sb = new StringBuilder();
		sb.append(word);
		sb.append(" ");
		sb.append(near);
		sb.append(" ");
		if (other.matches("^\".*\"$")) {
			sb.append(String.join(" ", createLiterals(other.substring(1, other.length() - 1))));
		} else if (other.contains("*")) {
			sb.append(createWildCardLiteral(other));
		 } else {
			sb.append(other);
		}
		return sb.toString();
	}

	// ------------------------------------------------------------------------------------------------------

	private String createWildCardLiteral(String literal) {
		StringBuilder sb = new StringBuilder();
		sb.append("$");
		sb.append(literal);
		sb.append("$");
		return sb.toString();
	}
	
}
