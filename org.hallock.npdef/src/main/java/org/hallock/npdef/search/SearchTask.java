package org.hallock.npdef.search;

import java.util.concurrent.TimeUnit;

import org.hallock.npdef.Constants;
import org.hallock.npdef.Waves;

class SearchTask implements Runnable {
	private int startIndex;
	private int endIndex;
	private SharedState sstate;

	SearchTask(SharedState sstate, int startIndex, int endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.sstate = sstate;
	}

	private void createInitial() {
		for (int it = startIndex; it < endIndex; it++) {
			Waves waves = new Waves();
			waves.randomlySatisfy(sstate.random, sstate.towers);
			int possible = Genetic.countPossible(waves);
			sstate.initialize(it, waves, possible);
		}
	}

	private void improvePopulation() {
		TwoWaves wvs = sstate.getTwoWaves();
		Waves waves = new Waves();
		Genetic.crossMultiply(sstate.random, wvs, waves);
		Genetic.mutate(sstate.variance, sstate.random, waves);
		waves.randomlySatisfy(sstate.random, sstate.towers);
		int possible = Genetic.countPossible(waves);
		sstate.update(waves, possible);
	}

	public void run() {
		try {
			createInitial();
		} catch (Exception e) {
			sstate.monitor.setError(e);
			sstate.error = true;
			return;
		}
		sstate.initialized.countDown();
		try {
			while (!sstate.initialized.await(1, TimeUnit.SECONDS))
				if (sstate.error)
					return;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (!sstate.monitor.shouldStop()) {
			if (sstate.error)
				return;
			try {
				improvePopulation();
			} catch (Exception e) {
				sstate.monitor.setError(e);
				sstate.error = true;
				return;
			}
		}
		sstate.threadTerminating();
		sstate.completed.countDown();
	}
}