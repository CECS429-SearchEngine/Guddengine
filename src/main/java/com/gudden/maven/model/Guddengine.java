package com.gudden.maven.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Guddengine {
	
	private PositionalInvertedIndex PII;
	private KGramIndex KGI;
	private Normalizer NORM;

	// ------------------------------------------------------------------------------------------------------
	
	/** Creates a new instance of Guddengine. */
	public Guddengine() {
		this.PII = new PositionalInvertedIndex();
		this.KGI = new KGramIndex();
		this.NORM = new Normalizer();
	}

	// ------------------------------------------------------------------------------------------------------
	
	/** Returns the file names that has been added from indexing the file that were in the given path. */
	public List<String> indexDirectory(String path) throws IOException {
		final Path currentWorkingPath = Paths.get(path).toAbsolutePath();

		// the list of file names that were processed.
		List<String> fileNames = new ArrayList<String>();

		// This is our standard "walk through all .json files" code.
		Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
			int documentID = 0;

			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				// make sure we only process the current working directory.
				if (currentWorkingPath.equals(dir)) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				// only process .json files
				if (file.toString().endsWith(".json")) {
					// we have found a .json file; add its name to the fileName list,
					// then index the file and increase the document ID counter.
					fileNames.add(file.getFileName().toString());
					addTokens(file.toFile(), documentID++);
				}
				return FileVisitResult.CONTINUE;
			}

			// don't throw exceptions if files are locked/other errors occur.
			public FileVisitResult visitFileFailed(Path file, IOException e) {
				return FileVisitResult.CONTINUE;
			}
		});
		return fileNames;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Remove all the terms and types that have been indexed. */
	public void resetIndex() {
		this.PII.resetIndex();
		this.KGI.resetIndex();
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Returns the merged result of the query that has been provided.. */
	public List<PositionalPosting> search(Query query) {
		List<SubQuery> subQueries = query.getSubQueries();
		
		// process each sub-query and union their results.
		List<PositionalPosting> result = processSubQuery(subQueries.get(0));
		for (int i = 1; i < subQueries.size(); i++) {
			result = union(result, processSubQuery(subQueries.get(i)));
		}
		return result;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Returns the String that has been stemmed. */
	public String stemToken(String token) {
		return this.NORM.stem(token);
	}

	// ------------------------------------------------------------------------------------------------------
	
	/** Returns an array of String containing terms that have been added into the positional inverted index */
	public String[] vocabulary() {
		return PII.getDictionary();
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private int findNearLiteral(String[] literals) {
		for (int i = 1; i < literals.length - 1; i++) {
			if (literals[i].matches("near/\\d")) {
				return i;
			}
		}
		return -1;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private SortedSet<String> getTypes(String[] grams) {
		SortedSet<String> result = new TreeSet<String>();
		for (String each : grams) {
			if (each.equals("$")) {
				continue;
			} else if (each.length() > 3) {
				// generate the KGrams from if the length of the gram is greater than 4 since we have a
				// maximum of 3 grams in the KGramIndex.
				List<String> subGrams = KGI.generateGrams(3,  each);
				// recursively call processGrams to retrive the types for the subGrams. Union results.
				SortedSet<String> resultSet = getTypes(subGrams.toArray(new String[subGrams.size()]));
				if (!result.isEmpty())
					result.retainAll(resultSet);
				else
					result.addAll(resultSet);
			} else if (result.isEmpty()) {
				result.addAll(KGI.getPostings(each));
			} else {
				result.retainAll(KGI.getPostings(each));
			}
		}
		return result;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private void addTokens(File file, int docId) {
		DocumentTokenStream dp = new DocumentTokenStream(file);
		int position = 0;	// keep track of the positions for each token.
		while (dp.hasNextToken()) {
			String type = dp.nextToken();
			if (type == null) continue;	// skip the proceeding instructions if the term is an empty string.
			type = this.NORM.normalize(type);
			if (type.contains("-")) {
				// We separate the hyphened type to create a set types that will be added into the index.
				Set<String> types = this.NORM.splitHypenWords(type);
				for (String each : types) {
					this.KGI.add(each);
					this.PII.add(this.NORM.stem(each), docId, position);
				}
			} else {
				// Add the type to the KGramIndex and the term (stemmed type) into the PositionalInvertedIndex
				this.KGI.add(type);
				this.PII.add(this.NORM.stem(type), docId, position);
			}
			position++;	// Increment for each token.
		}
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private List<PositionalPosting> intersect(List<List<PositionalPosting>> results) {
		if (results.isEmpty()) return null;
		
		// Sort the results by decreasing order so we minimize the amount of intersect we do.
		Collections.sort(results, new Comparator<List<PositionalPosting>>() {
			@Override
			public int compare(List<PositionalPosting> o1, List<PositionalPosting> o2) {
				return Integer.valueOf(o1.size()).compareTo(o2.size());
			}		
		});
		
		List<PositionalPosting> result = results.get(0);
		for (int i = 1; i < results.size(); i++) 
			result = intersect(result, results.get(i));
		return result;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private List<PositionalPosting> intersect(List<PositionalPosting> current, List<PositionalPosting> other) {
		// Intersection of a set with an empty set is empty.
		if (current == null || other == null) return null;
		List<PositionalPosting> result = new ArrayList<PositionalPosting>();
		int i = 0, j = 0;
		while(i < current.size() && j < other.size()) {
			int currentId = current.get(i).getId();
			int otherId = other.get(j).getId();
			if (currentId == otherId) {
				result.add(new PositionalPosting(currentId));
				
				// increment both indexes since the document that had the same ID were checked.
				i++;
				j++;
			} else if (currentId < otherId) {
				// increment the index for current since current document ID was less than other document ID
				i++;
			} else {
				// similarly we increment the index for other since other document ID was less than current
				// document id.
				j++;
			}
		}
		return result;
	}
	
	// ------------------------------------------------------------------------------------------------------	

	private List<PositionalPosting> positionalIntersect(List<PositionalPosting> current, 
												       List<PositionalPosting> other, int k) {
		if (current == null || other == null) return null;	// intersection of empty set is empty.
		List<PositionalPosting> result = new ArrayList<PositionalPosting>();
		int i = 0, j = 0;	// keep track of the indexes for current and other.
		
		while (i < current.size() && j < other.size()) {
			int currentId = current.get(i).getId();
			int otherId = other.get(j).getId();
			
			if (currentId == otherId) {
				// keep track of the positions that are k distance away from each other.
				List<Integer> positions = new ArrayList<Integer>();
				List<Integer> currentDocumentPositions = current.get(i).getPositions();
				List<Integer> otherDocumentPositions = other.get(j).getPositions();
				
				for (Integer c : currentDocumentPositions) {
					for (Integer o : otherDocumentPositions) {
						// the distance between the two positions in the document.
						int distance = o - c;
						
						if (0 < distance && distance <= k) {
							// if the distance is within k then add the position from other element.
							positions.add(o);
						} else if (o > c) {
							// if the position in other document is greater than the current document, then
							// we know that there cannot exist in the current position where the distance is
							// within k. Thus breaking out of the inner forloop.
							break;
						}
					}
					
					// Remove unnecessary positions added from previous loop.
					while (!positions.isEmpty() && Math.abs(positions.get(0) - c) > k)
						positions.remove(0);
					
					// Add all the positions that were within k distance of the current position.
					if (!positions.isEmpty()) {
						PositionalPosting posting = new PositionalPosting(currentId);
						posting.addPosition(c);
						for (Integer position : positions)
							posting.addPosition(position);
						result.add(posting);
					}
				}
				// increment both indexes since the document that had the same ID were checked.
				i++;
				j++;
			} else if (currentId < otherId) {
				// increment the index for current since current document ID was less than other document ID
				i++;
			} else {
				// similarly we increment the index for other since other document ID was less than current
				// document id.
				j++;
			}
		}
		return result;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private List<PositionalPosting> processSubQuery(SubQuery subQuery) {
		List<String> literals = subQuery.getLiterals();
		List<List<PositionalPosting>> result = new ArrayList<List<PositionalPosting>>();
		for (String each : literals)
			result.add(processLiteral(each));
		return intersect(result);
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private List<PositionalPosting> processLiteral(String literal) {
		if (literal.matches("(.*near/\\d.*)")) {
			// If the literal matches the near regex, then we will return the results of the processed near
			// query.
			return processNearQuery(literal.split("\\s+"));
		} else if (literal.matches("^\\$.+\\$$")) {
			// If the literal matches the gram regex, then we will return the results of the processes gram
			// query.
			return processGramQuery(literal);
		} else if (literal.contains(" ")) {
			// If the literal contains a space, then we know that this is a phrase query. Return the results
			// of the processed phrase query.
			return processPhraseQuery(literal.split("\\s+"));
		}
		String token = NORM.stem(NORM.normalize(literal));
		return PII.getPostings(token);
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private List<PositionalPosting> processNearQuery(String[] literals) {
		// find the index of the first occurrence of the "near/\\d."
		int nearLiteral = findNearLiteral(literals);
		int k = Integer.parseInt(literals[nearLiteral].split("/")[1]);
		
		// reconstruct the literals that occurred before and after "near/\\d".
		String thisLiteral = reconstructLiteral(0, nearLiteral, literals);
		String otherLiteral = reconstructLiteral(nearLiteral + 1, literals.length, literals);
		
		// Recursively process the reconstructed literals and do the positional Intersect.
		List<PositionalPosting> thisPosting = processLiteral(thisLiteral);
		List<PositionalPosting> otherPosting = processLiteral(otherLiteral);
		return positionalIntersect(thisPosting, otherPosting, k);
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private List<PositionalPosting> processGramQuery(String literal) {
		List<PositionalPosting> result = null;
		
		// Split the grams to search for the gram that start before and/or after *.
		String[] grams = literal.split("\\*");
		String originalRegex = String.join(".*", grams).replaceAll("\\$", "");
		SortedSet<String> types = getTypes(grams);
		if (!types.isEmpty()) {
			for (String each : types) {
				if (result != null && each.matches(originalRegex)) {
					// Since result is not empty, we need to set result to be the union of the current result
					// and the result of the type.
					result = union(result, processLiteral(each));
				} else if (each.matches(originalRegex)) {
					// Since result is empty, we set result the type that we will process for the literal.
					result = processLiteral(each);
				}
			}
		}
		return result;
	}
	
	// ------------------------------------------------------------------------------------------------------	

	private List<PositionalPosting> processPhraseQuery(String[] literals) {
		// First Positional Postings for the first query.
		List<PositionalPosting> result = processLiteral(literals[0]);
		
		// Since we do a positional intersect with a distance of 1, since a phrase query consists of phrases
		// that are right next to each other. We keep intersecting the results to ensure that the phrase of
		// next word is near the result.
		for (int i = 1; i < literals.length; i++) {
			result = positionalIntersect(result, processLiteral(literals[i]), 1);
		}
		return result;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private String reconstructLiteral(int start, int end, String[] literals) {
		StringBuilder sb = new StringBuilder(literals[start]);
		for (int i = start + 1; i < end; i++) {
			sb.append(" ");
			sb.append(literals[i]);
		}
		return sb.toString();
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private List<PositionalPosting> union(List<PositionalPosting> current, List<PositionalPosting> other) {
		if (current == null || current.isEmpty()) return other; // union of empty and non-empty is non-empty
		if (other == null || other.isEmpty()) return current;
		
		List<PositionalPosting> result = new ArrayList<PositionalPosting>();
		int i = 0, j = 0; // keep track of the indexes for current and other.
		
		while (i < current.size() && j < other.size()) {
			int currentId = current.get(i).getId();
			int otherId = other.get(j).getId();
			
			if (currentId == otherId) {
				// Add only one PositionalPosting since they are the same. Then increment both the current
				// and other indexes to compare the postings from the next elements.
				result.add(current.get(i++).merge(other.get(j++)));
			} else if (j < other.size() && currentId > otherId) {
				// Add the PositionalPosting from other since we know that the current document ID is
				// greater than the other document ID. And we increment the index j to get the next item for
				// other.
				result.add(other.get(j++));
			} else if (i < current.size() && currentId < otherId) {
				// Similarly we do this for Postings when current document ID is less than the other
				// document ID. 
				result.add(current.get(i++));
			}
		}
		
		// Add all PositionalPostings from current if we did not loop through all the Postings in current.
		while (i < current.size()) {
			PositionalPosting currentPosting = current.get(i++);
			if (currentPosting.getId() > result.get(result.size() - 1).getId())
				result.add(currentPosting);
		}
		// Similarly we do this for Postings in other.
		while (j < other.size()) {
			PositionalPosting otherPosting = other.get(j++);
			if (otherPosting.getId() > result.get(result.size() - 1).getId())
				result.add(otherPosting);
		}
		
		return result;
	}

}
