package com.gudden.maven.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IndexBankTest {
	private IndexBank iB;
	private final String SPATH = "external" + File.separatorChar + "json";
	File file;
	
	@Before
	public void setUp() throws Exception {
		file = new File(SPATH);
		iB = new IndexBank();
	}

	// ------------------------------------------------------------------------------------------------------

	@After
	public void tearDown() throws Exception {
		file = null;
		iB = null;
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testIndexDirectory() {
		try {
			List<String> testFileList = iB.indexDirectory(file.getAbsolutePath());
			for(int x = 1; x <= 4; x++) {
				assertEquals("article"+x+".json", testFileList.get(x-1));
			}
			// Test if indexes have been populated
			assertEquals("$c", iB.getKGramIndex().getDictionary()[0]);
			assertEquals("cat", iB.getPositionalInvertedIndex().getDictionary()[0]);
			
			// Test if Score has been Assigned in PII
			assertTrue(iB.getPositionalInvertedIndex().getPostings("cat").get(0).getScore() == 1.0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("Cannot open directory or files in IndexDirectory");
		}
	}
}
