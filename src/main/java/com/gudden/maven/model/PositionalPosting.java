package com.gudden.maven.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PositionalPosting {
	
	private int id;
	private List<Integer> positions;

	// ------------------------------------------------------------------------------------------------------

	public PositionalPosting(int id) {
		this.id = id;
		this.positions = new ArrayList<Integer>();
	}
	
	// ------------------------------------------------------------------------------------------------------

	public PositionalPosting(int id, List<Integer> positions) {
		this.id = id;
		this.positions = positions;
	}

	// ------------------------------------------------------------------------------------------------------

	public void addPosition(int position) {
		this.positions.add(position);
	}

	// ------------------------------------------------------------------------------------------------------

	public int getId() {
		return id;
	}

	// ------------------------------------------------------------------------------------------------------

	public List<Integer> getPositions() {
		return this.positions;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	public PositionalPosting merge(PositionalPosting other) {
		List<Integer> mergedPositions = new ArrayList<Integer>();
		mergedPositions.addAll(this.positions);
		mergedPositions.addAll(other.getPositions());
		Collections.sort(mergedPositions, Collections.reverseOrder());
		return new PositionalPosting(this.id, mergedPositions);
	}

	// ------------------------------------------------------------------------------------------------------

	public void setPositions(List<Integer> positions) {
		this.positions = positions;
	}
	
}
