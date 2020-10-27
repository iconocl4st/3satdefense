package org.hallock.npdef;

import org.hallock.npdef.search.DiscreteDistribution;
import org.hallock.npdef.search.SearchMonitor;

public class StdOutSearchMonitor implements SearchMonitor {
	long startTime = System.currentTimeMillis();
	
	@Override
	public void updateInitializationProgress(double percentage) {
		System.out.println("Initialized " + percentage);
	}

	@Override
	public void updateDistributions(DiscreteDistribution distribution) {
		
	}

	@Override
	public void updateMinimumSolutionsFound(Waves waves, int numPossible) {
		System.out.println("Found new minimum solution: " + waves);
		System.out.println("with " + numPossible + " possible solutions");
	}

	@Override
	public void updateTerminatingProgress(double percentage) {
		System.out.println("Terminated progress: " + percentage);
	}

	@Override
	public boolean shouldStop() {
		return System.currentTimeMillis() > startTime + 10 * 1000;
	}

	@Override
	public void updateAttemptsMade(long l) {
		
	}

	@Override
	public void setError(Exception e) {
		e.printStackTrace();
	}
}
