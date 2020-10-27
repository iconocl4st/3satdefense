package org.hallock.npdef;

import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;

import org.hallock.npdef.ColoredShape.Shape;

public class Constants {
	public static final int TOWER_RANGE = 1;
	public static final int NUM_LANES = 6;
	public static final int NUM_TOWERS = NUM_LANES;
	public static final int NUM_WAVES = 3 * NUM_LANES;
	public static final int NUM_ENEMIES_PER_WAVE = 3;
	public static final int POPULATION_SIZE = 50;
	public static final int NUM_THREADS = 8;
	public static final int SCREEN_WIDTH = 1500;
	public static final int SCREEN_HEIGHT = 2 * SCREEN_WIDTH / 3;

	public static final long ANIMATION_DELAY = 20;
	public static final double ENEMY_SPEED = 0.1;
	
	private static final Color[] COLORS = new Color[] {
		new Color(255, 0, 0),
		new Color(0, 255, 0),
		new Color(0, 0, 255),
		new Color(0, 255, 255),
		new Color(255, 0, 255),
		new Color(255, 255, 0),
	};
	
	private static final ColoredShape[] COLORED_SHAPES = createColoredShapes();
	private static ColoredShape[] createColoredShapes() {
		LinkedList<ColoredShape> list = new LinkedList<>();
		for (Color color : COLORS) {
			for (Shape shape : ColoredShape.Shape.values())
				list.add(new ColoredShape(color, shape));
		}
		Collections.shuffle(list);
		return list.toArray(new ColoredShape[0]);
	}

	public static final ColoredShape DEFAULT_COLORED_SHAPE = new ColoredShape(Color.white, ColoredShape.Shape.OVAL);
	
	public static ColoredShape getColoredShape(int i) {
		if (i >= COLORED_SHAPES.length) {
			throw new RuntimeException("Need more colored shapes!");
		}
		return COLORED_SHAPES[i];
	}
}
