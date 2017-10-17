package com.gudden.maven.model;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PositionalPostingTest {
	
	private PositionalPosting dummyPostings;

	// ------------------------------------------------------------------------------------------------------

	@Before
	public void setUp() {
		this.dummyPostings = new PositionalPosting(0);
		this.dummyPostings.addPosition(0);
		this.dummyPostings.addPosition(2);
		this.dummyPostings.addPosition(8);
	}

	// ------------------------------------------------------------------------------------------------------

	@After
	public void tearDown() {
		this.dummyPostings.setPositions(null);
		this.dummyPostings = null;
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testGetId() {
		assertEquals("Posting Id must be 0", 0, this.dummyPostings.getId());
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void testPositions() {
		List<Integer> testPositions = new ArrayList<Integer>();
		testPositions.add(0);
		testPositions.add(2);
		testPositions.add(8);
		assertEquals("Positions must be <0, 2, 8>", this.dummyPostings.getPositions(), testPositions);
		assertEquals("There are 3 positions", this.dummyPostings.getPositions().size(), 3);
	}

}
