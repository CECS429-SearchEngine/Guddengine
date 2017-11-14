package com.gudden.maven.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class KGramDiskIndex extends DiskInvertedIndex {
	
	public KGramDiskIndex(String path) {
		this.path = path;
		try {
			this.list = new RandomAccessFile(new File(path, "bin/gramVocab.bin"), "r");
			this.postings = new RandomAccessFile(new File(path, "bin/gramPostings.bin"), "r");
			this.vocabTable = readVocabTable(path, "bin/gramTable.bin");
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	public List<String> getTypes(String gram) {
		long gramPosition = binarySearchVocabulary(gram);
		if (gramPosition >= 0) {
			return readTypesFromFile(gramPosition);
		}
		return null;
	}

	// ------------------------------------------------------------------------------------------------------
	
	private String buildType(long length) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append((char)decodeFile(this.postings));
		}
		return sb.toString();
	}
	
	// ------------------------------------------------------------------------------------------------------
	
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
	
	// ------------------------------------------------------------------------------------------------------
	
	private List<String> readTypesFromFile(long gramPosition) {
		try {
			this.postings.seek(gramPosition);
			int typeFrequency = (int)decodeFile(this.postings);
			List<String> types = new ArrayList<String>(typeFrequency);
			for (int i = 0; i < typeFrequency; i++) {
				types.add(buildType((int)decodeFile(this.postings)));
			}
			return types;
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return null;
	}

}
