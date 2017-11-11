package com.gudden.maven.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class PositionalDiskInvertedIndex {
	
	public static void main(String[] args) {
		List<PositionalPosting> x = new PositionalDiskInvertedIndex("/Users/kuminin/Desktop/GuddenTheEngine/test").getPostings("the");
		for (PositionalPosting s : x) {
			System.out.println("Document Id: " + s.getId() + "\nScore: " + s.getScore());
			System.out.print("Positions:");
			for (int k : s.getPositions()) {
				System.out.print(" " + k);
			}
			System.out.println("\n");
		}
	}
	
	private long[] vocabTable;
	private String path;
	private RandomAccessFile vocabList;
	private RandomAccessFile vocabPostings;
	private RandomAccessFile weights;
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Opens a disk positional inverted index that was constructed in a give path. */
	public PositionalDiskInvertedIndex (String path) {
		this.path = path;
		try {
			this.vocabList = new RandomAccessFile(new File(path, "bin/vocab.bin"), "r");
			this.vocabPostings = new RandomAccessFile(new File(path, "bin/postings.bin"), "r");
			this.weights = new RandomAccessFile(new File(path, "bin/docWeights.bin"), "r");
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		}
		this.vocabTable = readVocabTable(path);
	}
	
	public List<PositionalPosting> getPostings(String term) {
		long postingsPosition = binarySearchVocabulary(term);
		if (postingsPosition >= 0) {
			return readPostingsFromFile(postingsPosition);
		}
		return null;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	public double getWeights(int id) {
		try {
			
			// skips to the appropriate location to read 8-byte double
			this.weights.seek(id * 8);
			return this.weights.readDouble();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return -1;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Locates the byte position of the postings for the given term. */
	private long binarySearchVocabulary(String term) {
		int low = 0, high = this.vocabTable.length / 2 - 1;
		while (low <= high) {
			int termLength;
			String fileTerm = null;
			
			// Overflow caused by (low + high) / 2.
			int mid = low + ((high - low) / 2);
			long vListPosition = this.vocabTable[mid * 2];
			try {
				// middle is the term that we searched for.
				if(mid == this.vocabTable.length / 2 - 1) 
					termLength = (int) (this.vocabList.length() - this.vocabTable[mid * 2]);
				else termLength = (int) (this.vocabTable[(mid + 1) * 2] - vListPosition);
				
				// Moves pointer to offset position in vocabList
				this.vocabList.seek(vListPosition);
				
				// Read vocabList into buffer and create the string from gap (termLength)
				byte[] buffer = new byte[termLength];
				vocabList.read(buffer, 0, termLength);
				fileTerm = new String(buffer, "ASCII");
			} catch (IOException e) {
				e.printStackTrace();
			}
			int compareValue = term.compareTo(fileTerm);
			if (compareValue == 0) return this.vocabTable[mid * 2 + 1];
			else if (compareValue < 0) high = mid - 1;
			else low = mid + 1;
		}
		return -1;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	/** Reads the file vocabTable.bin into memory. */
	private long[] readVocabTable(String indexName) {
		long[] vocabTable = null;
		try {
			RandomAccessFile tableFile = new RandomAccessFile(new File(indexName, "bin/vocabTable.bin"), "r");
			long tableSize = decodeFile(tableFile);
			vocabTable = new long[(int) tableSize * 2];
			for (int i = 0; i < vocabTable.length; i += 2) {
				vocabTable[i] = decodeFile(tableFile);
				vocabTable[i + 1] = decodeFile(tableFile);
			}
			tableFile.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return vocabTable;
	}
	
	// ------------------------------------------------------------------------------------------------------	
	
	/** This is genius */
	private long decodeFile(RandomAccessFile file) throws IOException {
		long encode = 0;
		List<Long> encoded = new ArrayList<Long>();
		
		// If encode is less than 128, then it is the continuation bit. Hence, continue adding the encode to
		// the list.
		do {
			encode = file.read();
			encoded.add(encode);
		} while (encode < 128);
		
		// Genius way of decoding.
		return VariableByteEncoding.VBDecode(encoded).get(0);
	}
	
	// ------------------------------------------------------------------------------------------------------	
	
	private List<PositionalPosting> readPostingsFromFile(long postingsPosition) {
		try {
			this.vocabPostings.seek(postingsPosition);
			int documentId = 0;
			int documentFrequency = (int)decodeFile(this.vocabPostings);	
			
			List<PositionalPosting> postings = new ArrayList<PositionalPosting>(documentFrequency);
			System.out.println(documentFrequency);
			for (int i = 0; i < documentFrequency; i++) {
				documentId += decodeFile(this.vocabPostings);
				double score = this.vocabPostings.readDouble();
				List<Integer> positions = readTermPositions(decodeFile(this.vocabPostings));
				postings.add(new PositionalPosting(documentId, positions, score));
				System.out.println(postings.get(postings.size() - 1));
			}
			return postings;
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	private List<Integer> readTermPositions(long termFrequency) throws IOException {
		int termPosition = 0;
		List<Integer> positions = new ArrayList<Integer>();
		for (int j = 0; j < termFrequency; j++) {
			termPosition += (int)decodeFile(this.vocabPostings);
			positions.add(termPosition);
		}
		return positions;
	}
	
	public String[] getDictionary() {
		String [] vocabularies = new String [this.vocabTable.length];
		try {
			for(int i = 0; i < this.vocabTable.length - 1; i++) {
				int length = (int) (vocabTable[i + 1] - vocabTable[i]);
				this.vocabList.seek(vocabTable[i]);
				
				// Read vocabList into buffer and create the string from gap (termLength)
				byte[] buffer = new byte[length];
				vocabList.read(buffer, 0, length);
				vocabularies[i] = new String(buffer, "ASCII");
			}
		} catch(IOException e) {
			System.out.println(e.toString());
		}
		return vocabularies;
	}
}
