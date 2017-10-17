package com.gudden.maven.model;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QueryTest {
	Query queryI, queryII;

	// ------------------------------------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
		this.queryI = new Query("shakes \"Jamba Juice\"");
		this.queryII = new Query("shakes + 	smoothies mango + \"Jamba Juice\"");
	}

	// ------------------------------------------------------------------------------------------------------

	@After
	public void tearDown() throws Exception {
		this.queryI = null;
		this.queryII = null;
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testQuery() {
		List<SubQuery> sqI = queryI.getSubQueries();
		List<SubQuery> sqII = queryII.getSubQueries();
		assertTrue(sqI.size() == 1);
		assertTrue(sqI.get(0).getLiterals().size() == 2);
		assertTrue(sqII.size() == 3);
		assertTrue(sqII.get(0).getLiterals().size() == 1);
		assertTrue(sqII.get(1).getLiterals().size() == 2);
		assertTrue(sqII.get(2).getLiterals().size() == 1);
	}

}
