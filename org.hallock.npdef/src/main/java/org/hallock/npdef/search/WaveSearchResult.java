package org.hallock.npdef.search;

import org.hallock.npdef.Waves;

public class WaveSearchResult {
	public Waves waves;
	public int possibleSolutions;
	
	public WaveSearchResult(Waves waves, int possibleSolutions) {
		this.waves = waves;
		this.possibleSolutions = possibleSolutions;
	}
}
