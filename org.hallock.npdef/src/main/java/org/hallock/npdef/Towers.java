package org.hallock.npdef;

import java.util.Random;

public class Towers {

	int[] positions;

	public Towers(int[] positions) {
		this.positions = positions;
	}

	public Towers() {
		positions = new int[Constants.NUM_TOWERS];
	}

	public String toString() {
		StringBuilder builder = new StringBuilder(3 * Constants.NUM_LANES);
		for (int i = 0; i < Constants.NUM_LANES; i++) {
			builder.append(String.format("%3d", getPosition(i)));
		}
		return builder.toString();
	}

	public Towers copy() {
		Towers ret = new Towers();
		System.arraycopy(positions, 0, ret.positions, 0, positions.length);
		return ret;
	}

	public int getPosition(int index) {
		return positions[index];
	}

	public void set(int tower, int position) {
		positions[tower] = position;
	}

	public void generate(Random random) {
		for (int i = 0; i < positions.length; i++) {
			positions[i] = random.nextInt(Constants.NUM_LANES);
		}
	}

	public boolean satisfiedBy(int[][] wave) {
		for (int lane = 0; lane < Constants.NUM_LANES; lane++) {
			if (!satisfiedBy(wave[lane], lane))
				return false;
		}
		return true;
	}

	protected int countPass(int[][] wave) {
		int count = 0;
		for (int lane = 0; lane < Constants.NUM_LANES; lane++) {
			if (!satisfiedBy(wave[lane], lane))
				++count;
		}
		return count;
	}

	public boolean satisfiedBy(int[] wave, int lane) {
		for (int e : wave) {
			int position = positions[e];
			int distance = Math.min(
					Math.min(Math.abs(lane - position), Math.abs(lane - position + Constants.NUM_LANES)),
					Math.abs(lane - position - Constants.NUM_LANES));
			if (distance <= Constants.TOWER_RANGE) {
				return true;
			}
		}
		return false;
	}

	public Satisfication getSatisfiedBy(int[] wave, int lane) {
		Satisfication satisfication = null;
		for (int i = 0; i < wave.length; i++) {
			int position = positions[wave[i]];
			int distance = Math.min(
					Math.min(Math.abs(lane - position), Math.abs(lane - position + Constants.NUM_LANES)),
					Math.abs(lane - position - Constants.NUM_LANES));
			if (distance > Constants.TOWER_RANGE)
				continue;
			Satisfication s = new Satisfication(i, wave[i], position);
			if (satisfication == null || s.compareTo(satisfication) < 0)
				satisfication = s;
		}
		return satisfication;
	}

	public static final class Satisfication implements Comparable<Satisfication> {
		public final int index;
		public final int tower;
		public final int lane;

		public Satisfication(int index, int tower, int lane) {
			this.index = index;
			this.tower = tower;
			this.lane = lane;
		}

		@Override
		public int compareTo(Satisfication o) {
			int c;
			c = Integer.compare(tower, o.tower);
			if (c != 0) return c;
			c = Integer.compare(lane, o.lane);
			if (c != 0) return c;
			c = Integer.compare(index, o.index);
			return c;
		}
	}
}
