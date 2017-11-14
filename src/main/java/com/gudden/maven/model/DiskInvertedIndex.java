package com.gudden.maven.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public abstract class DiskInvertedIndex {
	
	protected String path;
	protected long[] vocabTable;
	protected RandomAccessFile list;
	protected RandomAccessFile postings;
	
	// ------------------------------------------------------------------------------------------------------

	/** Locates the byte position of the postings for the given term. */
	protected long binarySearchVocabulary(String term) {
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
					termLength = (int) (this.list.length() - this.vocabTable[mid * 2]);
				else termLength = (int) (this.vocabTable[(mid + 1) * 2] - vListPosition);
				
				// Moves pointer to offset position in vocabList
				this.list.seek(vListPosition);
				
				// Read vocabList into buffer and create the string from gap (termLength)
				byte[] buffer = new byte[termLength];
				list.read(buffer, 0, termLength);
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
	
	/** This is genius. Decodes the first instance of the encoded variable byte.*/
	protected long decodeFile(RandomAccessFile file) throws IOException {
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
}
