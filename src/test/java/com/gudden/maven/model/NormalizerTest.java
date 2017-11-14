package com.gudden.maven.model;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NormalizerTest {
	Normalizer norm;

	// ------------------------------------------------------------------------------------------------------
	
	@Before
	public void setUp() throws Exception {
		norm = new Normalizer();
	}

	// ------------------------------------------------------------------------------------------------------

	@After
	public void tearDown() throws Exception {
		norm = null;
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testSplitHyphen() {
		String[] outTokens = new String[3];
		norm.splitHypenWords("first-grader").toArray(outTokens);
		assertEquals("firstgrader", outTokens[0]);
		assertEquals("grader", outTokens[1]);
		assertEquals("first", outTokens[2]);
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testStem() {
		assertEquals("capi", norm.stem("capy"));
	}
}
//article11545.json
//article25905.json
//article25911.json
//article26157.json
//article29663.json
//article31915.json
//article9774.json