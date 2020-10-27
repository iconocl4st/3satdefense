package org.hallock.npdef.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.hallock.npdef.ColoredShape;
import org.hallock.npdef.Constants;
import org.hallock.npdef.Towers;
import org.hallock.npdef.Towers.Satisfication;
import org.hallock.npdef.gui.UiUtil.BoundsResettable;

public class TowersView extends JPanel implements BoundsResettable {
	
	Towers towers;
	boolean editable;
	private final HashMap<Rectangle, PositionedTower> positions = new HashMap<>();

	private final LinkedList<TowerListener> listeners = new LinkedList<>();
	
	private LinkedList<Explosion> explosions = new LinkedList<>();
	private boolean[] waveExploded;
	private int[][] currentWave;
	private double currentWavePosition;
	private final Object waveSync = new Object();
	
	@Override
	public void resetBounds() {}

	public void setTowers(Towers towers) {
		this.towers = towers.copy();
	}
	
	public Towers getCurrentTowers() {
		return towers.copy();
	}

	public void setEditable(boolean b) {
		editable = b;
	}

	public void addListener(TowerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeListener(TowerListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	public void setWave(int[][] wave) {
		synchronized (waveSync) {
			currentWavePosition = 0.0;
			currentWave = wave;
			if (wave != null)
				waveExploded = new boolean[wave.length];
			else
				waveExploded = null;
			explosions.clear();
		}
	}
	
	public void setWavePosition(double position) {
		synchronized (waveSync) {
			currentWavePosition = position;
		}
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		int width = getWidth();
		int height = getHeight();
		
		g.setColor(Color.green);
		g.fillRect(0, 0, width, height);
		
		synchronized (positions) {
			positions.clear();
			for (int t = 0; t < Constants.NUM_TOWERS; t++) {
				drawTowers(
					g, t,
					0, t * height / Constants.NUM_TOWERS,
					width, height / Constants.NUM_TOWERS
				);
			}
		}
		
		synchronized (waveSync) {
			paintWave(g, width, height);
		}
		
		paintExplosions(g);
	}
	
	private void paintExplosions(Graphics2D g) {
		Stroke stroke = g.getStroke();
		g.setStroke(new BasicStroke(2));
		LinkedList<Explosion> toRemove = new LinkedList<>();
		for (Explosion explosion : explosions) {
			g.setColor(Color.yellow);
			int maxRadius = explosion.count++ * 5;
			for (int ring = 0; ring < 5; ring++) {
				int r = (int)(maxRadius - ring * Math.sqrt(ring) * 3);
				if (r < 0) break;
				g.drawOval(explosion.x - r, explosion.y - r, 2 * r, 2 * r);
			}
			g.setColor(explosion.color);
			g.drawLine(explosion.srcX, explosion.srcY, explosion.x, explosion.y);
			if (maxRadius > 100)
				toRemove.add(explosion);
		}
		explosions.removeAll(toRemove);
		g.setStroke(stroke);
	}

	private void paintWave(Graphics2D g, int width, int height) {
		if (currentWave == null || currentWavePosition < 0) {
			return;
		}
		int r = width / Constants.NUM_LANES / 8;
		for (int l = 0; l < Constants.NUM_LANES; l++) {
			if (waveExploded[l]) {
				continue;
			}
			int centerX = (int)((l + 0.5) * width / Constants.NUM_LANES);
			int centerY = (int)(currentWavePosition * height);
			
			Satisfication satisfication = towers.getSatisfiedBy(currentWave[l], l);
			if (satisfication != null && currentWavePosition > satisfication.tower / (double) Constants.NUM_TOWERS) {
				waveExploded[l] = true;
				explosions.add(new Explosion(
					centerX, centerY,
					(int)((satisfication.lane + 0.5) * width / Constants.NUM_LANES),
					(int)((satisfication.tower + 0.5) * height / Constants.NUM_TOWERS),
					Constants.getColoredShape(currentWave[l][satisfication.index]).color
				));
				continue;
			}
			
			for (int e = 0; e < currentWave[l].length; e++) {
				ColoredShape cs = Constants.getColoredShape(currentWave[l][e]);
				cs.fill(g, 
					centerX - r, centerY - 3 * r * e,
					2 * r, 2 * r
				);
			}
		}
	}

	private void drawTowers(Graphics2D g, int t, int x, int y, int width, int height) {
		ColoredShape cs = Constants.getColoredShape(t);
		ColoredShape shaded = cs.getShaded();
		for (int l = 0; l < Constants.NUM_LANES; l++) {
			boolean isShaded = towers == null || l != towers.getPosition(t);
			drawTower(
				g, t, l, isShaded ? shaded : cs,
				x + l * width / Constants.NUM_LANES, y,
				width / Constants.NUM_LANES, height
			);
		}
	}
	
	private void drawTower(Graphics2D g, int t, int l, ColoredShape cs, int x, int y, int width, int height) {
		positions.put(new Rectangle(x, y, width, height), new PositionedTower(t, l));
		g.setColor(Color.black);
		g.fillRect(x + width / 4, y - 1, width / 2, height + 2);
		
		int diam = Math.min(width, height) / 3;
		
		cs.fill(g, x + width / 2 - diam / 2, y + height / 2 - diam / 2, diam, diam);
	}

	public static TowersView createView() {
		TowersView view = new TowersView();
		view.addMouseListener(view.new TowerMouseListener());
		return view;
	}
	
	
	private static final class PositionedTower {
		int tower;
		int position;
		
		public PositionedTower(int tower, int position) {
			this.tower = tower;
			this.position = position;
		}
	}

	private class TowerMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (towers == null || !editable) return;
			Point point = e.getPoint();
			for (Entry<Rectangle, PositionedTower> entry : positions.entrySet()) {
				if (!entry.getKey().contains(point)) {
					continue;
				}
				towers.set(entry.getValue().tower, entry.getValue().position);
				repaint();
				
				synchronized (listeners) {
					for (final TowerListener listener : listeners) {
						final Towers currentTowers = getCurrentTowers();
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								listener.setTowers(currentTowers);
							}
						});
					}
				}
				return;
			}
		}
	}
	
	private static class Explosion {
		int x, y;
		int srcX, srcY;
		int count;
		Color color;
		
		public Explosion(int x, int y, int sX, int sY, Color color) {
			this.x = x;
			this.y = y;
			this.srcX = sX;
			this.srcY = sY;
			this.color = color;
			this.count = 0;
		}
	}
	
	public static interface TowerListener {
		public void setTowers(Towers towers);
	}
}
