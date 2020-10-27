package org.hallock.npdef.search;

import org.hallock.npdef.Waves;

public interface SearchMonitor {
	void updateInitializationProgress(double percentage);
	void updateDistributions(DiscreteDistribution distribution);
	void updateMinimumSolutionsFound(Waves waves, int numPossible);
	void updateTerminatingProgress(double percentage);
	boolean shouldStop();
	void updateAttemptsMade(long l);
	void setError(Exception e);
}