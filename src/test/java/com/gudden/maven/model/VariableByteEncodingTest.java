package com.gudden.maven.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VariableByteEncodingTest {
	private VariableByteEncoding vbe;
	private ArrayList<Integer> testGaps; 
	private List<Long> encodeGapValues;
	private List<Long> decodeGapValues;
	
	@Before
	public void setUp() throws Exception {
		 this.vbe = new VariableByteEncoding();
		 this.testGaps = new ArrayList<Integer>();
		 this.testGaps.add(1);
		 this.testGaps.add(2);
		 this.testGaps.add(4);
		 this.testGaps.add(8);
		 this.testGaps.add(11);
		 this.testGaps.add(85);
		 this.encodeGapValues = this.vbe.VBEncode(testGaps);
		 this.decodeGapValues = this.vbe.VBDecode(encodeGapValues);
	}

	// ------------------------------------------------------------------------------------------------------

	@After
	public void tearDown() throws Exception {
		this.vbe = null;
		this.testGaps = null;
	}

	// ------------------------------------------------------------------------------------------------------

	@Test
	public void TestVBENumbers() {
		//  5 -VBE-> 1000 0101 = 133
		assertEquals("133", this.vbe.VBEncodenumber(5).get(0).toString());
		// 33 -VBE-> 1010 0001 = 161
		assertEquals("161", this.vbe.VBEncodenumber(33).get(0).toString());
		// 14 -VBE-> 1000 1110 = 142
		assertEquals("142", this.vbe.VBEncodenumber(14).get(0).toString());
	}
	
	// ------------------------------------------------------------------------------------------------------

	@Test
	public void TestVBEGaps() {
		//1 -VBE-> 1000 0001 = 129
		assertEquals("129", this.encodeGapValues.get(0).toString());
		//2 - 1 = 1 -VBE-> 1000 0001 = 129
		assertEquals("129", this.encodeGapValues.get(1).toString());
		//4 - 2 = 2 -VBE-> 1000 0010 = 130
		assertEquals("130", this.encodeGapValues.get(2).toString());
		//8 - 4 = 4 -VBE-> 1000 0100 = 132
		assertEquals("132", this.encodeGapValues.get(3).toString());
		//11 - 8 = 3 -VBE-> 1000 0011 = 131
		assertEquals("131", this.encodeGapValues.get(4).toString());
		//85 - 11 = 74 -VBE-> 1100 1010 = 202
		assertEquals("202", this.encodeGapValues.get(5).toString());
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	@Test
	public void TestVBEDecode() {
		//1 -VBE-> 1000 0001 = 129
		assertEquals("1", this.decodeGapValues.get(0).toString());
		//2 - 1 = 1 -VBE-> 1000 0001 = 129
		assertEquals("2", this.decodeGapValues.get(1).toString());
		//4 - 2 = 2 -1-> 1000 0010 = 130
		assertEquals("4", this.decodeGapValues.get(2).toString());
		//8 - 4 = 4 -4-> 1000 0100 = 132
		assertEquals("8", this.decodeGapValues.get(3).toString());
		//11 - 8 = 3 -3-> 1000 0011 = 131
		assertEquals("11", this.decodeGapValues.get(4).toString());
		//85 - 11 = 74 -74-> 1100 1010 = 202
		assertEquals("85", this.decodeGapValues.get(5).toString());
	}

}
