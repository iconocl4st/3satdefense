package org.hallock.npdef;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Waves {
	int[][][] waves;
	
	public Waves() {
		waves = new int[
		    Constants.NUM_WAVES][
			Constants.NUM_LANES][
			Constants.NUM_ENEMIES_PER_WAVE
		];
	}
	
	public void assign(int wave, Waves source) {
		for (int l = 0; l < waves[wave].length; l++) {
			for (int e = 0; e < waves[wave][l].length; e++) {
				waves[wave][l][e] = source.waves[wave][l][e];
			}
		}
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder(
			waves.length * (3 + waves[0].length * (1 + 3 * waves[0][0].length))
		);
		for (int w = 0; w < waves.length; w++) {
			builder.append('|');
			for (int l = 0; l < waves.length; l++) {
				for (int e = 0; e < waves[w][l].length; e++)
					builder.append(String.format("%3d", waves[w][l][e]));
				if (l != waves[w].length - 1)
					builder.append('\t');
			}
			builder.append("|\n");
		}
		return builder.toString();
	}
	
	
	private void assignDistinct(Random random, int[] array, int max, HashSet<Integer> set) {
		set.clear();
		while (set.size() < array.length) {
			set.add(random.nextInt(max));
		}
		int index = 0;
		for (Integer i : set) {
			array[index++] = i;
		}
	}
	
	public void randomlySatisfy(Random random, Towers towers) {
		Possibles possibles = Possibles.create(towers);
		int numWaves = waves.length;
		int numLanes = waves[0].length;
		int numPerWave = waves[0][0].length;
		int numTowers = Constants.NUM_TOWERS;
		HashSet<Integer> set = new HashSet<>();
		for (int w = 0; w < numWaves; w++) {
			for (int l = 0; l < numLanes; l++) {
				assignDistinct(random, waves[w][l], numTowers, set);
				if (!towers.satisfiedBy(waves[w][l], l)) {
					waves[w][l][random.nextInt(numPerWave)] = possibles.random(random, l);
				}
				Arrays.sort(waves[w][l]);
			}
		}
	}
	
	public boolean satisfies(Towers towers) {
		for (int w = 0; w < waves.length; w++) {
			if (!towers.satisfiedBy(waves[w]))
				return false;
		}
		return true;
	}
	
	public int countPass(Towers towers) {
		int count = 0;
		for (int w = 0; w < waves.length; w++)
			count += towers.countPass(waves[w]);
		return count;
	}

	public boolean contains(int wave, int lane, int enem) {
		for (int e : waves[wave][lane])
			if (e == enem) return true;
		return false;
	}

	public void set(int wave, int lane, int enem, int num) {
		waves[wave][lane][enem] = num;
		Arrays.sort(waves[wave][lane]);
	}

	public int get(int w, int l, int e) {
		return waves[w][l][e];
	}

	public int[] get(int w, int l) {
		return waves[w][l];
	}

	public int[][] get(int w) {
		return waves[w];
	}

	public String id() {
		StringBuilder builder = new StringBuilder(2 * waves.length * waves[0].length * waves[0][0].length);
		for (int[][] wss : waves)
			for (int[] ws : wss)
				for (int w : ws)
					builder.append(w).append(',');
		return builder.toString();
	}
}
