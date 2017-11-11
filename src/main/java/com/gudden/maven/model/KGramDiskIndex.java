package com.gudden.maven.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class KGramDiskIndex {
	
	public static void main(String[] args) {
		List<String> x = new KGramDiskIndex("/Users/kuminin/Desktop/GuddenTheEngine/test").getTypes("th");
		for (String s : x) {
			System.out.println(s);
		}
	}
	
	private String path;
	private long[] vocabTable;
	private RandomAccessFile gramList;
	private RandomAccessFile gramPostings;
	
	public KGramDiskIndex(String path) {
		this.path = path;
		try {
			this.gramList = new RandomAccessFile(new File(path, "bin/gramVocab.bin"), "r");
			this.gramPostings = new RandomAccessFile(new File(path, "bin/gramPostings.bin"), "r");
			this.vocabTable = readVocabTable(path, "bin/gramTable.bin");
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	public List<String> getTypes(String gram) {
		long gramPosition = binarySearchVocabulary(gram);
		if (gramPosition >= 0) {
			return readTypesFromFile(gramPosition);
		}
		return null;
	}
	
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
					termLength = (int) (this.gramList.length() - this.vocabTable[mid * 2]);
				else termLength = (int) (this.vocabTable[(mid + 1) * 2] - vListPosition);
				
				// Moves pointer to offset position in vocabList
				this.gramList.seek(vListPosition);
				
				// Read vocabList into buffer and create the string from gap (termLength)
				byte[] buffer = new byte[termLength];
				gramList.read(buffer, 0, termLength);
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
	
	private long[] readVocabTable(String indexName, String fileName) throws IOException {
		long[] gramTable = null;
		RandomAccessFile tableFile = new RandomAccessFile(new File(indexName, fileName), "r");
		long tableSize = decodeFile(tableFile);
		gramTable = new long[(int) tableSize * 2];
		for (int i = 0; i < gramTable.length; i += 2) {
			gramTable[i] = decodeFile(tableFile);
			gramTable[i + 1] = decodeFile(tableFile);
		}
		tableFile.close();
		return gramTable;
	}
	
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
	
	private List<String> readTypesFromFile(long gramPosition) {
		try {
			this.gramPostings.seek(gramPosition);
			int typeFrequency = (int)decodeFile(this.gramPostings);
			List<String> types = new ArrayList<String>(typeFrequency);
			for (int i = 0; i < typeFrequency; i++) {
				types.add(buildType((int)decodeFile(this.gramPostings)));
			}
			return types;
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	private String buildType(long length) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append((char)decodeFile(this.gramPostings));
		}
		return sb.toString();
	}
}
