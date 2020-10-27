package org.hallock.npdef.search;

import java.util.HashSet;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

import org.hallock.npdef.Constants;
import org.hallock.npdef.Towers;
import org.hallock.npdef.Waves;


class SharedState {
	Random random;
	
	Towers towers;
	DiscreteDistribution distribution;
	Waves[] waves;
	TreeSet<WaveIndex> indices;
	HashSet<String> existingSolutions;
	
	double variance = 1.0;
	
	CountDownLatch initialized;
	CountDownLatch completed;
	final Object sync = new Object();
	
	SearchMonitor monitor;
	int initializedCount;
	int terminatedCount;
	long attemptsMade;
	
	boolean error;
	
	
	public static SharedState create(Random random, Towers towers, SearchMonitor monitor) {
		SharedState sharedState = new SharedState();
		sharedState.random = random;
		sharedState.towers = towers;
		sharedState.distribution = new DiscreteDistribution(random, Constants.POPULATION_SIZE);
		sharedState.waves = new Waves[Constants.POPULATION_SIZE];
		sharedState.indices = new TreeSet<>();
		sharedState.initialized = new CountDownLatch(Constants.NUM_THREADS);
		sharedState.completed = new CountDownLatch(Constants.NUM_THREADS);
		sharedState.monitor = monitor;
		sharedState.existingSolutions = new HashSet<>();
		return sharedState;
	}

	public boolean initialize(int index, Waves waves2, int possible) {
		String hash = waves2.id();
		synchronized (sync) {
			if (existingSolutions.contains(hash))
				return false;
			existingSolutions.add(hash);
			
			
			WaveIndex oldMinimum = null;
			if (!indices.isEmpty()) {
				oldMinimum = indices.first();
			}
			
			distribution.setProb(index, probability(possible));
			indices.add(new WaveIndex(index, possible));
			waves[index] = waves2;
			monitor.updateInitializationProgress(++initializedCount / (double) waves.length);
			
			
			monitor.updateAttemptsMade(++attemptsMade);
			if (oldMinimum == null || possible < oldMinimum.numSolutions) {
				monitor.updateMinimumSolutionsFound(waves2, possible);
			}
			return true;
		}
	}

	public void update(Waves waves2, int possible) {
		String hash = waves2.id();
		synchronized (sync) {
			if (existingSolutions.contains(hash))
				return;
			
			WaveIndex last = indices.last();
			WaveIndex first = indices.first();
			monitor.updateAttemptsMade(++attemptsMade);
			if (last.numSolutions < possible) {
				return;
			}
			if (first.numSolutions > possible) {
				monitor.updateMinimumSolutionsFound(waves2, possible);
			}
			existingSolutions.remove(waves[last.index].id());
			existingSolutions.add(hash);
			indices.remove(last);
			last.numSolutions = possible;
			indices.add(last);
			waves[last.index] = waves2;
			distribution.setProb(last.index, probability(possible));
			monitor.updateDistributions(distribution);
		}
	}
	
	public TwoWaves getTwoWaves() {
		TwoWaves ret = new TwoWaves();
		synchronized (sync) {
			int index1 = distribution.sample();
			int index2;
			do {
				index2 = distribution.sample();
			} while (index2 == index1);
			ret.waves1 = waves[index1];
			ret.waves2 = waves[index2];
		}
		return ret;
	}
	
	void threadTerminating() {
		monitor.updateTerminatingProgress(++terminatedCount / (double) Constants.NUM_THREADS);
	}

	
	private static double probability(int numSolutions) {
		return 1.0 / (numSolutions * numSolutions);
	}
}
