package org.hallock.npdef.search;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.hallock.npdef.Constants;
import org.hallock.npdef.Towers;

public class WavesSearch {
	public static WaveSearchResult search(Random random, Towers towers, SearchMonitor monitor) {
		SharedState sharedState = SharedState.create(random, towers, monitor);
		
		Thread[] threads = new Thread[Constants.NUM_THREADS];
		int populationPerThread = Constants.POPULATION_SIZE / Constants.NUM_THREADS;
		for (int i = 0; i < Constants.NUM_THREADS; i++) {
			threads[i] = new Thread(new SearchTask(
				sharedState,
				i * populationPerThread,
				Math.min((i + 1) * populationPerThread, Constants.POPULATION_SIZE)
			));
			threads[i].start();
		}

		try {
			while (!sharedState.completed.await(10, TimeUnit.SECONDS)) {
				if (sharedState.error)
					return null;
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < Constants.NUM_THREADS; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		WaveIndex first = sharedState.indices.first();
		return new WaveSearchResult(sharedState.waves[first.index], first.numSolutions);
	}
}
