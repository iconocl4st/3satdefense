package org.hallock.npdef;

public class SubsetGenerator {

	int numElements;
	int[] current;
	boolean first;

	public SubsetGenerator(int subsetSize, int numElements) {
		if (numElements < subsetSize) {
			throw new RuntimeException("Impossible");
		}
		current = new int[subsetSize];
		this.numElements = numElements;
		reset();
	}
	
	void reset() {
		first = true;
		for (int i = 0; i < current.length; i++) {
			current[i] = i;
		}
	}
	
	boolean hasNext() {
		for (int i = 0; i < current.length; i++)
			if (current[i] < numElements - current.length + i)
				return true;
		return false;
	}
	
	void next() {
		if (first) {
			first = false;
			return;
		}
		for (int i = current.length - 1; i >= 0; i--) {
			if (current[i] == numElements - current.length + i)
				continue;
            current[i]++;
			while (++i < current.length)
				current[i] = current[i - 1] + 1;
            break;
	    }
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder(2 * current.length);
		for (int i = 0; i < current.length; i++) {
			builder.append(current[i]);
			if (i != current.length - 1)
				builder.append(',');
		}
		return builder.toString();
	}
	
	
	private static final class ChainedIterator {
		SubsetGenerator[] generators;
		
		public ChainedIterator(int subsetSize, int numElements, int numSubsets) {
			generators = new SubsetGenerator[numSubsets];
			for (int i = 0; i < numSubsets; i++)
				generators[i] = new SubsetGenerator(subsetSize, numElements);
		}

		boolean hasNext() {
			for (SubsetGenerator gen : generators)
				if (gen.hasNext())
					return true;
			return false;
		}
		
		public void next() {
			for (int i = generators.length - 1; i >= 0; i--) {
				if (!generators[i].hasNext())
					continue;
				generators[i].next();
				while (++i < generators.length)
					generators[i].reset();
				break;
			}
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < generators.length; i++) {
				builder.append(generators[i]);
				if (i != generators.length - 1)
					builder.append(':');
			}
			return builder.toString();
		}
	}
	
	public static void main2(String[] args) {
		ChainedIterator gen = new ChainedIterator(3, 6, 3);
		while (gen.hasNext()) {
			gen.next();
			System.out.println(gen);
		}
		System.out.println("Done.");
	}
	
}
