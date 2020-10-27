package org.hallock.npdef.search;

import java.util.Random;

public class DiscreteDistribution {
	double[] probs;
	double sumProb;
	Random random;

	public DiscreteDistribution(Random random, int numEvents) {
		this.random = random;
		this.probs = new double[numEvents];
	}
	
	public void setProb(int index, double val) {
		if (val == 0) {
			System.out.println("here...");
		}
		sumProb -= probs[index];
		probs[index] = val;
		sumProb += val;
	}

	public int sample() {
		double prob = random.nextDouble() * sumProb;
		int i;
		for (i = 0; prob > 0 && i <= probs.length; i++) {
			prob -= probs[i];
		}
		return i - 1;
	}

	public int numBins() {
		return probs.length;
	}

	public double[] getProbs() {
		return probs;
	}
}
