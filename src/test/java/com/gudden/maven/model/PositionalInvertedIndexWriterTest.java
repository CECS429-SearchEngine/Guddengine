package com.gudden.maven.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PositionalInvertedIndexWriterTest {
	
	private PositionalInvertedIndex pii;
	private String[][] documents = { { "hello", "world", "how", "are", "you", "doing" },
			{ "who", "are", "you", "thats", "talking", "in" } };
	private PositionalInvertedIndexWriter piiw;
	
	
	@Before
	public void setUp() throws Exception {
		this.pii = new PositionalInvertedIndex();
		for (int i = 0; i < documents.length; i++) {
			for (int j = 0; j < documents[i].length; j++) {
				this.pii.add(this.documents[i][j], i, j);
			}
		}
		
	}

	// ------------------------------------------------------------------------------------------------------

	@After
	public void tearDown() throws Exception {
		this.pii.resetIndex();
		this.pii = null;
	}

	// ------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------------------


}
