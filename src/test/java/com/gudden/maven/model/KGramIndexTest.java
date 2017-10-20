package com.gudden.maven.model;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KGramIndexTest {
	private KGramIndex kgi;
	
	// ------------------------------------------------------------------------------------------------------
	
	@Before
	public void setUp() throws Exception {
		this.kgi = new KGramIndex();
		this.kgi.add("hello");
	}

	// ------------------------------------------------------------------------------------------------------
	
	@After
	public void tearDown() throws Exception {
		this.kgi.resetIndex();
		this.kgi = null;
	}

	// ------------------------------------------------------------------------------------------------------
	
	@Test
	public void testSize() {
		assertTrue(this.kgi.size() == 15);
	}

	// ------------------------------------------------------------------------------------------------------
	
	@Test
	public void testResetIndex() {
		this.kgi.resetIndex();
		assertTrue(this.kgi.size() == 0);
	}

	// ------------------------------------------------------------------------------------------------------
	
	@Test
	public void testGetDictionary() {
		String[] grams = {"h", "e", "l", "ll", "o", "$h", "he", "el",
				         "lo", "o$", "$he", "hel", "ell", "llo", "lo$"};
		String[] result = this.kgi.getDictionary();
		Arrays.sort(grams);
		Arrays.sort(result);
		assertTrue(Arrays.equals(result, grams));
		assertEquals("Dictionary must equal in length", result.length, grams.length);
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testAdd() {
		this.kgi.add("bye");
		assertTrue(this.kgi.size() == 24);
		assertTrue(this.kgi.getPostings("e").size() == 2);
	}

	// ------------------------------------------------------------------------------------------------------
	
	@Test
	public void testGetPostings() {
		assertTrue(this.kgi.size() == 15);
		assertTrue(this.kgi.getPostings("e").size() == 1);
	}

}
