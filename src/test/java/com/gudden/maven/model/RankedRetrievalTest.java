package com.gudden.maven.model;

import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RankedRetrievalTest {

	private Guddengine engine;
	private Query [] queries;
	
	// ------------------------------------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
		engine = new Guddengine();
		
		String path = "/Users/crystalchun/Developer/Java/SearchEngine/corpus/Test";
		engine.getFileNames(path);
		engine.setDiskIndexes(path);
		
		queries = new Query[5];
		queries[0] = new Query("Washington");
		queries[1] = new Query("Dogg");
		queries[2] = new Query("how many dogs are in seattle");
		queries[3] = new Query("monumental washington");
		queries[4] = new Query("quiz me about states");
	}

	// ------------------------------------------------------------------------------------------------------

	@After
	public void tearDown() throws Exception {
		engine = null;
		queries = null;
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testResultsQuery0() {
		List<PositionalPosting> results = engine.rankedSearch(queries[0]);
		assertTrue(results.size() == 3);
		
		assertTrue(results.get(0).getId() == 3);
		assertTrue(results.get(0).getScore() == 0.482083506634572);
		
		assertTrue(results.get(1).getId() == 1);
		assertTrue(results.get(1).getScore() == 0.3269430843372421);
		
		assertTrue(results.get(2).getId() == 4);
		assertTrue(results.get(2).getScore() == 0.3122528462124668);
	}
	
	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testResultsQuery1() {
		List<PositionalPosting> results = engine.rankedSearch(queries[1]);
		assertTrue(results.isEmpty());
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testResultsQuery2() {
		List<PositionalPosting> results = engine.rankedSearch(queries[2]);
		assertTrue(results.size() == 3);
		
		assertTrue(results.get(0).getId() == 1);
		assertTrue(results.get(0).getScore() == 2.6269347815583006);
		
		assertTrue(results.get(1).getId() == 2);
		assertTrue(results.get(1).getScore() == 0.7562510959411338);
		
		assertTrue(results.get(2).getId() == 3);
		assertTrue(results.get(2).getScore() == 0.3636663006094893);
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testResultsQuery3() {
		List<PositionalPosting> results = engine.rankedSearch(queries[3]);
		assertTrue(results.size() == 4);
		
		assertTrue(results.get(0).getId() == 3);
		assertTrue(results.get(0).getScore() == 0.8457498072440613);
		
		assertTrue(results.get(1).getId() == 0);
		assertTrue(results.get(1).getScore() == 0.39882456741691497);
		
		assertTrue(results.get(2).getId() == 1);
		assertTrue(results.get(2).getScore() == 0.3269430843372421);
		
		assertTrue(results.get(3).getId() == 4);
		assertTrue(results.get(3).getScore() == 0.3122528462124668);
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testResultsQuery4() {
		List<PositionalPosting> results = engine.rankedSearch(queries[4]);
		assertTrue(results.isEmpty());
	}
}
