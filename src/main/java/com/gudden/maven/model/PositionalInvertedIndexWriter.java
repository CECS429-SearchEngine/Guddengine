package com.gudden.maven.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.List;

public class PositionalInvertedIndexWriter extends IndexWriter<PositionalInvertedIndex> {
	
	public static void main(String[] args) {
		Guddengine engine = new Guddengine();
		PositionalInvertedIndexWriter piiw = new PositionalInvertedIndexWriter("/Users/kuminin/Desktop/GuddenTheEngine/test/bin");
		IndexBank bank = engine.getBank();
		engine.indexDirectory("/Users/kuminin/Desktop/GuddenTheEngine/test");
		piiw.buildIndex(bank.getPositionalInvertedIndex());
		piiw.buildWeights(bank.getDocLengths());
	}
	public PositionalInvertedIndexWriter(String folderPath) {
		super(folderPath);
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	@Override
	public void buildIndex(PositionalInvertedIndex index) {
		// TODO Auto-generated method stub
		buildIndexForDirectory(index, super.getFolderPath());		
	}

	public void buildWeights(List<Double> docLengths) {
		try {
			String folder = super.getFolderPath();
			FileOutputStream postingsFile = new FileOutputStream(new File (folder, "docWeights.bin"));
			for (double each : docLengths) {
				postingsFile.write(convertToByte(each), 0, 8);
			}
			postingsFile.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	// ------------------------------------------------------------------------------------------------------
	
	@Override
	protected void buildIndexForDirectory(PositionalInvertedIndex index, String folder) {
		// TODO Auto-generated method stub
		
		// An array of terms 
		String[] dictionary = index.getDictionary();
		// The positions in the vocabulary file 
		long[] vocabPositions = new long[dictionary.length];
		try {
			buildVocabFile(folder, dictionary, vocabPositions, "vocab.bin");
			buildPostingsFile(folder, index, dictionary, vocabPositions);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	private void buildPostingsFile(String folder, PositionalInvertedIndex index, String[] dict, long[] vocabPositions) throws IOException {
		
		int idx = 0;		// index for vocabPositions.
		FileOutputStream postingsFile = new FileOutputStream(new File(folder, "postings.bin"));
		FileOutputStream vocabTable = new FileOutputStream(new File(folder, "vocabTable.bin")); 
		
		// writes to vocabTable.bin indicating the number of vocabularies that exists.
		writeToFile(vocabTable, VariableByteEncoding.VBEncodenumber(dict.length));
		
		for (String term : dict) {
			int previousId = 0;		// used for encoding the gaps between documents
			
			List<PositionalPosting> postings = index.getPostings(term);
			
			// Write the byte location in the vocabTable.bin and the number of bytes the term starts at in the
			// postings.bin file.
			writeToFile(vocabTable, VariableByteEncoding.VBEncodenumber(vocabPositions[idx++]));
			writeToFile(vocabTable, VariableByteEncoding.VBEncodenumber(postingsFile.getChannel().position()));
			
			// Write the document frequency of the given term in the postings file.
			writeToFile(postingsFile, VariableByteEncoding.VBEncodenumber(postings.size()));
			
			for (PositionalPosting each : postings) {
				int gap = each.getId() - previousId;
				previousId = each.getId();
				
				// Write the gap between the previous id and current id in the postings file.
				writeToFile(postingsFile, VariableByteEncoding.VBEncodenumber(gap));
				
				// Write the document term weight into the postings file.
				postingsFile.write(convertToByte(each.getScore()), 0, 8);
				
				// Write the term frequency of the given document and posting
				writeToFile(postingsFile, VariableByteEncoding.VBEncodenumber(each.getPositions().size()));
				
				// Write the position for each occurrence in document by their gap.
				writeToFile(postingsFile, VariableByteEncoding.VBEncode(each.getPositions()));
			}
		}
		vocabTable.close();
		postingsFile.close();
	}

}