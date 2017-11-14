package com.gudden.maven.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class PositionalDiskInvertedIndex extends DiskInvertedIndex {
	
	private RandomAccessFile weights;
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Opens a disk positional inverted index that was constructed in a give path. */
	public PositionalDiskInvertedIndex (String path) {
		this.path = path;
		try {
			this.list = new RandomAccessFile(new File(path, "bin/vocab.bin"), "r");
			this.postings = new RandomAccessFile(new File(path, "bin/postings.bin"), "r");
			this.weights = new RandomAccessFile(new File(path, "bin/docWeights.bin"), "r");
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		}
		this.vocabTable = readVocabTable(path);
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Returns the vocabularies that are in the corpus. */
	public String[] getDictionary() {
		String [] vocabularies = new String [this.vocabTable.length];
		try {
			for (int i = 0; i < this.vocabTable.length - 1; i++) {
				int length = (int) (this.vocabTable[i + 1] - this.vocabTable[i]);
				this.list.seek(this.vocabTable[i]);
				
				// Read vocabList into buffer and create the string from gap (termLength)
				byte[] buffer = new byte[length];
				this.list.read(buffer, 0, length);
				vocabularies[i] = new String(buffer, "ASCII");
			}
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return vocabularies;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Read the Ld weight for a given document id*/
	public double getDocumentWeights(int docID) {
		try {
			// skips to the appropriate location to read 8-byte double
			this.weights.seek(docID*8);
			return this.weights.readDouble();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return -1;
		
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Returns the list of PositionalPosting with/without position for a given term. */
	public List<PositionalPosting> getPostings(String term, boolean position) {
		long postingsPosition = binarySearchVocabulary(term);
		if (postingsPosition >= 0) {
			return readPostingsFromFile(postingsPosition, position);
		}
		return null;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Reads the file vocabTable.bin into memory. */
	private long[] readVocabTable(String indexName) {
		// holds vocabPostion in odd and length of vocabulary in even.
		long[] vocabTable = null;
		
		try {
			RandomAccessFile tableFile = new RandomAccessFile(new File(indexName, "bin/vocabTable.bin"), "r");
			long tableSize = decodeFile(tableFile);
			vocabTable = new long[(int) tableSize * 2];
			
			for (int i = 0; i < vocabTable.length; i += 2) {
				vocabTable[i] = decodeFile(tableFile);	// decodes the position located in postings.bin
				vocabTable[i + 1] = decodeFile(tableFile);	// decodes the the length of the vocabulary.
			}
			tableFile.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		
		return vocabTable;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Returns the list of positional posting created from reading the postings file. */
	private List<PositionalPosting> readPostingsFromFile(long postingsPosition, boolean position) {
		try {
			this.postings.seek(postingsPosition);	// move cursor to start where the term starts.
			int documentId = 0;
			int documentFrequency = (int)decodeFile(this.postings);
			
			List<PositionalPosting> postings = new ArrayList<PositionalPosting>(documentFrequency);
			for (int i = 0; i < documentFrequency; i++) {
				documentId += decodeFile(this.postings);	// accumulate the document id gaps.
				double score = this.postings.readDouble();	// read score for DSP.
				long termFrequency = decodeFile(this.postings);
				
				// if position is true, then we are doing a boolean query.
				if (position) {
					List<Integer> positions = readTermPositions(termFrequency);
					postings.add(new PositionalPosting(documentId, positions, score));
				} else {
					// position is false, we are doing ranked query
					for (int counter = 0; counter < termFrequency;) {
						if (this.postings.read() > 127) counter++;
					}
					postings.add(new PositionalPosting(documentId, null, score));
				}
				
			}
			return postings;
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	// ------------------------------------------------------------------------------------------------------

	/** Returns the list of positions that the term occurs in a document. */
	private List<Integer> readTermPositions(long termFrequency) throws IOException {
		int termPosition = 0;
		List<Integer> positions = new ArrayList<Integer>();
		for (int j = 0; j < termFrequency; j++) {
			termPosition += (int)decodeFile(this.postings);
			positions.add(termPosition);
		}
		return positions;
	}
	
}
