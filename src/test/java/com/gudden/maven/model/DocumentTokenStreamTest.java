package com.gudden.maven.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DocumentTokenStreamTest {
	private DocumentTokenStream stringStream;
	private DocumentTokenStream fileStream;

	// ------------------------------------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
		String path = "/Users/kuminin/Desktop/GuddenTheEngine/guddengine/external/test/test1.json";
		stringStream = new DocumentTokenStream("!@#$%^&*(|||||hel''''lo#@!^#%^$*#&'''''''' world goo''dbye");
		fileStream = new DocumentTokenStream(new File(path));
	}

	// ------------------------------------------------------------------------------------------------------

	@After
	public void tearDown() throws Exception {
		stringStream = null;
		fileStream = null;
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testToken() {
		assertTrue(stringStream.hasNextToken() == fileStream.hasNextToken());
		while (stringStream.hasNextToken() && fileStream.hasNextToken()) {
			assertTrue(stringStream.nextToken().equals(fileStream.nextToken()));
		}
		assertFalse(stringStream.hasNextToken());
		assertFalse(fileStream.hasNextToken());
	}

}
