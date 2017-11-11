package com.gudden.maven.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class KGramIndexWriter extends IndexWriter<KGramIndex> {

	public KGramIndexWriter(String folderPath) {
		super(folderPath);
	}
	
	@Override
	public void buildIndex(KGramIndex index) {
		buildIndexForDirectory(index, super.getFolderPath());
	}

	@Override
	protected void buildIndexForDirectory(KGramIndex index, String folder) {
		// TODO Auto-generated method stub
		
		// An array of types
		String[] dictionary = index.getDictionary();
		
		long[] gramPositions = new long[dictionary.length];
		
		try {
			buildVocabFile(folder, dictionary, gramPositions, "gramVocab.bin");
			buildGramPostingsFile(folder, index, dictionary, gramPositions);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	private void buildGramPostingsFile(String folder, KGramIndex index, String[] dict, long[] positions) throws IOException {
		int idx = 0;
		FileOutputStream postingsFile = new FileOutputStream(new File(folder, "gramPostings.bin"));
		FileOutputStream vocabTable = new FileOutputStream(new File(folder, "gramVocabTable.bin"));
		
		// writes to gramVocabTable.bin indicating the number of vocabularies that exists.
		
	}

}
