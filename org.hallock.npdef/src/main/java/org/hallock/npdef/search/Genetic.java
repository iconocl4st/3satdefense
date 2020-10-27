package org.hallock.npdef.search;

import java.util.Random;

import org.hallock.npdef.Constants;
import org.hallock.npdef.Towers;
import org.hallock.npdef.TowersIterator;
import org.hallock.npdef.Waves;

class Genetic {
	static void mutate(double variance, Random random, Waves waves) {
		int numMutations = random.nextInt(10);
		for (int i = 0; i < numMutations * variance; i++) {
			int wave = random.nextInt(Constants.NUM_WAVES);
			int lane = random.nextInt(Constants.NUM_LANES);
			int enem = random.nextInt(Constants.NUM_ENEMIES_PER_WAVE);
			int repl;
			do {
				repl = random.nextInt(Constants.NUM_TOWERS);
			} while (waves.contains(wave, lane, repl));
			waves.set(wave, lane, enem, repl);
		}
	}

	static void crossMultiply(Random random, TwoWaves wvs, Waves waves) {
		for (int w = 0; w < Constants.NUM_WAVES; w++) {
			waves.assign(w, random.nextBoolean() ? wvs.waves1 : wvs.waves2);
		}
	}
	
	static int countPossible(Waves waves) {
		int count = 0;
		TowersIterator tIterator = new TowersIterator(
			Constants.NUM_TOWERS,
			Constants.NUM_LANES
		);
		while (tIterator.hasNext()) {
			tIterator.next();
			Towers current = tIterator.current();
			if (waves.satisfies(current)) {
				count++;
			}
		}
		return count;
	}
	
	static int getStatistics(Waves waves) {
		int count = 0;
		TowersIterator tIterator = new TowersIterator(
			Constants.NUM_TOWERS,
			Constants.NUM_LANES
		);
		while (tIterator.hasNext()) {
			tIterator.next();
			Towers current = tIterator.current();
			if (waves.satisfies(current)) {
				count++;
			}
		}
		return count;
	}
}
