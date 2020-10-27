package org.hallock.npdef;

import java.util.LinkedList;
import java.util.Random;

public class Possibles {
	private int[][] possibles;
	
	public int random(Random random, int lane) {
		if (possibles[lane].length == 0) {
			throw new RuntimeException("Lane " + lane + " cannot handle any enemies.");
		}
		return possibles[lane][random.nextInt(possibles[lane].length)];
	}

	public static Possibles create(Towers towers) {
		LinkedList<Integer>[] possiblesLists = new LinkedList[Constants.NUM_LANES];
		for (int i = 0; i < possiblesLists.length; i++) {
			possiblesLists[i] = new LinkedList<Integer>();
		}
		for (int l = 0; l < possiblesLists.length; l++) {
			for (int t = 0; t < Constants.NUM_TOWERS; t++) {
				if (towers.satisfiedBy(new int[] {t}, l)) {
					possiblesLists[l].add(t);
				}
			}
		}
		int[][] array = new int[possiblesLists.length][];
		for (int i = 0; i < possiblesLists.length; i++) {
			array[i] = new int[possiblesLists[i].size()];
			int index = 0;
			for (int j : possiblesLists[i]) {
				array[i][index++] = j;
			}
		}
		Possibles ret = new Possibles();
		ret.possibles = array;
		return ret;
	}
}
