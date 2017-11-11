package com.gudden.maven.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.List;

public abstract class IndexWriter<T> {
	
	private String folderPath;

	// ------------------------------------------------------------------------------------------------------
	
	protected IndexWriter(String folderPath) {
		this.folderPath = folderPath;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	public String getFolderPath() {
		return this.folderPath;
	}

	// ------------------------------------------------------------------------------------------------------
	
	public abstract void buildIndex(T index);

	// ------------------------------------------------------------------------------------------------------
	
	protected abstract void buildIndexForDirectory(T index, String folder);

	// ------------------------------------------------------------------------------------------------------
	
	protected void buildVocabFile(String folder, String[] dict, long[] vocabPositions, String fileName) throws IOException {
		int vocabIdx = 0;
		int vocabPos = 0;
		File vocabFile = new File(folder, fileName);
		OutputStreamWriter vocabList = new OutputStreamWriter(new FileOutputStream(vocabFile), "ASCII");
		
		for (String vocabulary : dict) {
			vocabPositions[vocabIdx++] = vocabPos;
			vocabList.write(vocabulary);
			vocabPos += vocabulary.length();
		}
		vocabList.close();
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	protected byte[] convertToByte(int allocate, long amount) { 
		byte[] ba = new byte[allocate];
		ba[0] = (byte) amount;
		return ba;
	}	
	
	// ------------------------------------------------------------------------------------------------------
	
	protected byte[] convertToByte(double amount) { 
		return ByteBuffer.allocate(8).putDouble(amount).array();
	}	
	
	// ------------------------------------------------------------------------------------------------------
	
	protected void writeToFile(FileOutputStream file, List<Long> encoded) throws IOException {
		for (long each : encoded) {
			file.write(convertToByte(1, each), 0, 1);
		}
	}
}
