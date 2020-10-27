package org.hallock.npdef;

import org.hallock.npdef.Towers;

public final class TowersIterator {
	private int numLanes;
	private int[] positions;
	private boolean isFirst;
	
	public TowersIterator(int numTowers, int numLanes) {
		positions = new int[numTowers];
		this.numLanes = numLanes;
		isFirst = true;
	}

	void reset() {
		isFirst = true;
		for (int i = 0; i < positions.length; i++)
			positions[i] = 0;
	}
	
	public String toString() {
		return current().toString();
	}
	
	public boolean hasNext() {
		for (int i = 0; i < positions.length; i++) {
			if (positions[i] < numLanes - 1)
				return true;
		}
		return false;
	}
	
	public Towers current() {
		return new Towers(positions);
	}
	
	public void next() {
		if (isFirst) {
			isFirst = false;
			return;
		}
		int first;
		for (first = positions.length - 1; first >= 0 && positions[first] == numLanes - 1; first--)
			;
		if (first < 0) {
			throw new RuntimeException("Called next while there were no more!");
		}
		positions[first]++;
		for (first++; first < positions.length; first++)
			positions[first] = 0;
		return;
	}
}
