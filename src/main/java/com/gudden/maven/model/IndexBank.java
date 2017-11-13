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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndexBank {
	private PositionalInvertedIndex PII;
	private KGramIndex KGI;
	private KGramDiskIndex KGDI;
	private PositionalDiskInvertedIndex PDII;
	private List<Double> docLengths;
	
	// ------------------------------------------------------------------------------------------------------
	
	public IndexBank() {
		this.PII = new PositionalInvertedIndex();
		this.KGI = new KGramIndex();
		this.docLengths = new ArrayList<Double>();
	}

	// ------------------------------------------------------------------------------------------------------
	
	public PositionalInvertedIndex getPositionalInvertedIndex() {
		return this.PII;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	public KGramIndex getKGramIndex() {
		return this.KGI;
	}
	
	public KGramDiskIndex getKGramDiskIndex() {
		return this.KGDI;
	}
	
	public PositionalDiskInvertedIndex getPositionalDiskInvertedIndex() {
		return this.PDII;
	}
	
	public List<Double> getDocLengths() {
		return this.docLengths;
	}

	// ------------------------------------------------------------------------------------------------------
	
	public void setDiskIndexes(String path) {
		KGDI = new KGramDiskIndex(path);
		PDII = new PositionalDiskInvertedIndex(path);
	}
	
	// ------------------------------------------------------------------------------------------------------	
	
	public void reset() {
		this.PII.resetIndex();
		this.KGI.resetIndex();
		this.KGDI = null;
		this.PDII = null;
		this.docLengths = new ArrayList<Double>();
	}

	// ------------------------------------------------------------------------------------------------------
	
	public List<String> indexDirectory(String path) throws IOException {
		final Path currentWorkingPath = Paths.get(path).toAbsolutePath();
		
		PositionalInvertedIndexWriter piiw = new PositionalInvertedIndexWriter(path);
		KGramIndexWriter kgiw = new KGramIndexWriter(path);
		
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
		piiw.buildIndex(this.PII);
		piiw.buildWeights(this.docLengths);
		kgiw.buildIndex(this.KGI);
		return fileNames;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private void addTokens(File file, int docId) {
		int position = 0;	// keep track of the positions for each token.
		String term;
		
		// Map to keep track of term Frequency inside each document.
		Map<String, Integer> termFrequency = new HashMap<String, Integer>();
		DocumentTokenStream dp = new DocumentTokenStream(file);
		
		while (dp.hasNextToken()) {
			String type = dp.nextToken();
			
			if (type == null) continue;	// skip the proceeding instructions if the term is an empty string.
			type = Normalizer.normalize(type);
			
			// We separate the hyphened type to create a set types that will be added into the index.
			if (type.contains("-")) {
				Set<String> types = Normalizer.splitHypenWords(type);
				
				for (String each : types) {
					term = Normalizer.stem(each);
					this.KGI.add(each);
					this.PII.add(term, docId, position);
					addTermFrequency(termFrequency, term);
				}
			} else {
				// Add the type to the KGramIndex and the term (stemmed type) into the PositionalInvertedIndex
				term = Normalizer.stem(type);
				this.KGI.add(type);
				this.PII.add(term, docId, position);
				addTermFrequency(termFrequency, term);
			}
			position++;	// Increment for each token.
		}
		docLengths.add(calculateMagnitue(termFrequency));
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private void addTermFrequency(Map<String, Integer> termFrequency, String term) {
		if (!termFrequency.containsKey(term)) {
			termFrequency.put(term, 0);
		}
		termFrequency.replace(term, termFrequency.get(term) + 1);
	}
	
	// ------------------------------------------------------------------------------------------------------

	private double calculateMagnitue(Map<String, Integer> termFrequency) {
		double sum = 0;
		for (String term : termFrequency.keySet()) {
			double score = calculateScore(termFrequency.get(term));
			addScore(term, score);
			sum += Math.pow(score, 2); 
		}
		return Math.sqrt(sum);
	}
	
	private double calculateScore(int frequency) {
		return 1 + Math.log(frequency);
	}
	
	private void addScore(String term, double score) {
		List<PositionalPosting> postings = this.PII.getPostings(term);
		postings.get(postings.size() - 1).setScore(score);
	}
	
	
}
