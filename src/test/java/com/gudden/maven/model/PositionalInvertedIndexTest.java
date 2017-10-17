package com.gudden.maven.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PositionalInvertedIndexTest {
	private PositionalInvertedIndex pii;
	private String[][] documents = { { "hello", "world", "how", "are", "you", "doing" },
			{ "who", "are", "you", "thats", "talking", "in" } };

	// ------------------------------------------------------------------------------------------------------
	
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
	
	@Test
	public void testAdd() {
		assertTrue(this.pii.size() == 10);
		this.pii.add("my", 1, 6);
		this.pii.add("are", 1, 7);
		assertTrue(this.pii.size() == 11);
	}

	// ------------------------------------------------------------------------------------------------------
	
	@Test
	public void testSize() {
		assertEquals("Size must equal 10", this.pii.size(), 10);
	}

	// ------------------------------------------------------------------------------------------------------
	
	@Test
	public void testGetPostings() {
		List<PositionalPosting> ps;
		ps = this.pii.getPostings("are");
		assertTrue(ps.size() == 2);
		ps = this.pii.getPostings("no");
		assertEquals("Postings must be null", ps, null);
	}

	// ------------------------------------------------------------------------------------------------------
	
	@Test
	public void testResetIndex() {
		this.pii.resetIndex();
		assertTrue(this.pii.size() == 0);
	}

	// ------------------------------------------------------------------------------------------------------
	
	@Test
	public void testGetDictionary() {
		String[] dictionary = {"hello", "world", "how", "are", "you", 
							  "doing", "who", "thats", "talking", "in"};
		Arrays.sort(dictionary);
		assertTrue(Arrays.equals(this.pii.getDictionary(), dictionary));
		assertEquals("Dictionary must equal in length", this.pii.getDictionary().length, dictionary.length);
	}

}
