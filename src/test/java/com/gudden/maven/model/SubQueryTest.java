package com.gudden.maven.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SubQueryTest {

	@Test
	public void testSubQuery() {
		String queryLiteral = "shakes Jamba Juice";
		SubQuery dummySubQuery = new SubQuery("shakes            \"Jamba Juice\"       ");
		System.out.println(dummySubQuery);
		assertEquals("Excpected 'shakes \"Jamba Juice\"'", dummySubQuery.toString(), queryLiteral);
		assertTrue(dummySubQuery.getLiterals().size() == 2);
	}
	
}
