package com.gudden.maven.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PositionalInvertedIndexWriterTest {
	
	private PositionalInvertedIndex pii;
	private final String[][] documents = { { "hello", "world", "how", "are", "you", "doing" },
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
		
		File dir = new File("testPIIW");
		if(dir.exists() == false)
			dir.mkdirs();
		
		PositionalInvertedIndexWriter piiw = new PositionalInvertedIndexWriter(dir.getAbsolutePath());
		
		
	}

	// ------------------------------------------------------------------------------------------------------

	@After
	public void tearDown() throws Exception {
		this.pii.resetIndex();
		this.pii = null;
		this.piiw = null;
	}

	// ------------------------------------------------------------------------------------------------------
	@Test
	public void TestThis() {
		
	}
	// ------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------------------


}
