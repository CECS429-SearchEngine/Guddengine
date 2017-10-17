package com.gudden.maven.model;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gudden.maven.model.Document;

public class DocumentTest {

	private Document testDocument;

	// ------------------------------------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
		this.testDocument = new Document();
	}

	// ------------------------------------------------------------------------------------------------------

	@After
	public void tearDown() throws Exception {
		this.testDocument = null;
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testDocument() {
		assertTrue(this.testDocument.getUrl() == null);
		assertTrue(this.testDocument.getBody() == null);
		assertTrue(this.testDocument.getTitle() == null);
		this.testDocument.setUrl("www.neal-terrell.com");
		this.testDocument.setBody("I love avocados. Image of an Avocado.");
		this.testDocument.setTitle("The Avocadoman");
		assertTrue(this.testDocument.getUrl() != null);
		assertTrue(this.testDocument.getBody() != null);
		assertTrue(this.testDocument.getTitle() != null);
	}

}
