package org.hallock.npdef;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class ColoredShape {
	public final Color color;
	public final Shape shape;
	
	public ColoredShape(Color color, Shape shape) {
		this.color = color;
		this.shape = shape;
	}

	public enum Shape {
		SQUARE,
		OVAL
	}
	
	public void fill(Graphics2D g, int x, int y, int w, int h) {
		g.setColor(color);
		switch (shape) {
		case SQUARE:
			g.fillRect(x, y, w, h);
			break;
		case OVAL:
			g.fillOval(x, y, w, h);
			break;
		}
	}
	public void draw(Graphics2D g, int x, int y, int w, int h) {
		g.setColor(color);
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(3));
		switch (shape) {
		case SQUARE:
			g.drawRect(x, y, w, h);
			break;
		case OVAL:
			g.drawOval(x, y, w, h);
			break;
		}
		g.setStroke(s);
	}
	
	public ColoredShape getShaded() {
		return new ColoredShape(shade(color), shape);
	}
	
	public ColoredShape withColor(Color color) {
		return new ColoredShape(color, shape);
	}

	private static Color shade(Color color) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		return new Color(red, green, blue, 255/4);
	}
	
	public static Color invert(Color color) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		return new Color(255 - red, 255 - green, 255 - blue);
	}
}
