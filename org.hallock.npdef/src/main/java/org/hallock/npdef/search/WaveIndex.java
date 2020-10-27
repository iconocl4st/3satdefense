package org.hallock.npdef.search;

class WaveIndex implements Comparable<WaveIndex> {
	public int index;
	public int numSolutions;
	
	public WaveIndex(int it, int possible) {
		this.index = it;
		this.numSolutions = possible;
	}
	@Override
	public int compareTo(WaveIndex o) {
		return Integer.compare(numSolutions, o.numSolutions);
	}
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof WaveIndex)) return false;
		WaveIndex o = (WaveIndex) other;
		return index == o.index;
	}
}