package com.gudden.maven.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

public class KGramIndexWriter extends IndexWriter<KGramIndex> {
	
	public KGramIndexWriter(String folderPath) {
		this.folderPath = folderPath;
	}

	// ------------------------------------------------------------------------------------------------------
	
	@Override
	public void buildIndex(KGramIndex index) {
		buildIndexForDirectory(index, this.folderPath);
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private void buildGramPostingsFile(String folder, KGramIndex index, String[] dict, long[] positions) throws IOException {
		int idx = 0;
		FileOutputStream gramsFile = new FileOutputStream(new File(folder, "gramPostings.bin"));
		FileOutputStream gramTable = new FileOutputStream(new File(folder, "gramTable.bin"));
		
		// writes to gramTable.bin indicating the number of vocabularies that exists.
		writeToFile(gramTable, VariableByteEncoding.VBEncodenumber(dict.length));
		
		for (String type : dict) {
			Set<String> types = index.getPostings(type);
			
			// Write the byte location in the gramVocabTable.bin and the number of bytes the term starts in
			// the gramPostings.bin file.
			writeToFile(gramTable, VariableByteEncoding.VBEncodenumber(positions[idx++]));
			writeToFile(gramTable, VariableByteEncoding.VBEncodenumber(gramsFile.getChannel().position()));
			
			// Write the type frequency of the given term in the postings file.
			writeToFile(gramsFile, VariableByteEncoding.VBEncodenumber(types.size()));
			
			for (String each: types) {
				writeToFile(gramsFile, VariableByteEncoding.VBEncodenumber(each.length()));
				for (int i = 0; i < each.length(); i++) 
					writeToFile(gramsFile, VariableByteEncoding.VBEncodenumber(each.charAt(i)));
			}
		}
		gramTable.close();
		gramsFile.close();
	}

	// ------------------------------------------------------------------------------------------------------
	
	@Override
	protected void buildIndexForDirectory(KGramIndex index, String folder) {
		// TODO Auto-generated method stub
		
		// An array of types
		String[] dictionary = index.getDictionary();
		
		long[] gramPositions = new long[dictionary.length];
		
		try {
			buildVocabFile(folder + "/bin", dictionary, gramPositions, "gramVocab.bin");
			buildGramPostingsFile(folder + "/bin", index, dictionary, gramPositions);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
}
