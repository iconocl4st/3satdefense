package org.hallock.npdef.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.hallock.npdef.ColoredShape;
import org.hallock.npdef.Constants;
import org.hallock.npdef.Towers;
import org.hallock.npdef.Towers.Satisfication;
import org.hallock.npdef.Waves;
import org.hallock.npdef.gui.TowersView.TowerListener;
import org.hallock.npdef.gui.UiUtil.BoundsResettable;

public class WavesView extends JPanel implements BoundsResettable, TowerListener {
	private static final long serialVersionUID = 1L;
	
	Waves waves;
	Towers towers;
	
	private final HashMap<Rectangle, int[][]> wavePositions = new HashMap<>();
	private final LinkedList<WavesListener> listeners = new LinkedList<>();
	
	public void addListener(WavesListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeListener(WavesListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	public void setWaves(Waves waves2) {
		this.waves = waves2;
	}

	public void setTowers(Towers towers) {
		this.towers = towers;
		repaint();
	}
	
	@Override
	public void resetBounds() {}
	
	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		int width = getWidth();
		int height = getHeight();
		
		synchronized (wavePositions) {
			wavePositions.clear();
		}
		
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		for (int w = 0; w < Constants.NUM_WAVES; w++) {
			drawWave(
				g, w,
				0, w * height / Constants.NUM_WAVES,
				width, height / Constants.NUM_WAVES
			);
		}
	}

	private void drawWave(Graphics2D g, int w, int x, int y, int width, int height) {
		synchronized (wavePositions) {
			if (waves != null)
				wavePositions.put(new Rectangle(x, y, width, height), waves.get(w));
		}
		g.setColor(Color.white);
		g.fillRect(x - 1, y, width + 1, 1);
		g.fillRect(x - 1, y + height, width + 1, 1);
		
		for (int l = 0; l < Constants.NUM_LANES; l++) {
			drawLane(
				g, w, l,
				x + l * width / Constants.NUM_LANES, y,
				width / Constants.NUM_LANES, height
			);
		}
	}

	private void drawLane(Graphics2D g, int w, int l, int x, int y, int width, int height) {
		if (towers != null && waves != null && !towers.satisfiedBy(waves.get(w, l), l)) {
			g.setColor(Color.red);
			g.fillRect(x, y, width, height);
		}
		g.setColor(Color.white);
		g.fillRect(x, y - 1, 1, height + 2);
		g.fillRect(x + width, y - 1, 1, height + 2);
		
		for (int e = 0; e < Constants.NUM_ENEMIES_PER_WAVE; e++) {
			drawEnemy(
				g, w, l, e,
				x + e * width / Constants.NUM_ENEMIES_PER_WAVE, y,
				width / Constants.NUM_ENEMIES_PER_WAVE, height
			);
		}
	}

	
	private void drawEnemy(
		Graphics2D g, int w, int l, int e,
		int x, int y, int width, int height
	) {
		ColoredShape cs;
		if (waves == null) {
			cs = Constants.DEFAULT_COLORED_SHAPE;
		} else {
			cs = Constants.getColoredShape(waves.get(w, l, e)); 
		}
		int diam = 3 * Math.min(width, height) / 4;
		cs.withColor(Color.black).draw(g, x + width / 2 - diam / 2, y + height / 2 - diam / 2, diam, diam);
		cs.fill(g, x + width / 2 - diam / 2, y + height / 2 - diam / 2, diam, diam);
		
		if (towers == null || waves == null)
			return;
		Satisfication satisfiedBy = towers.getSatisfiedBy(new int[] {waves.get(w, l, e)}, l);
		if (satisfiedBy == null)
			return;
		g.setColor(ColoredShape.invert(cs.color));
		g.drawString(
			satisfiedBy.lane + "," + satisfiedBy.tower,
			x + width / 2 - 10,
			y + height / 2 + 5
		);
	}

	public static WavesView createView() {
		WavesView view = new WavesView();
		view.addMouseListener(view.new WaveMouseListener());
		return view;
	}
	
	private class WaveMouseListener extends MouseAdapter {
		private int[][] getWaveAt(Point p) {
			synchronized (wavePositions) {
				for (Entry<Rectangle, int[][]> entry : wavePositions.entrySet()) {
					if (entry.getKey().contains(p))
						return entry.getValue();
				}
			}
			return null;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			final int[][] wave = getWaveAt(e.getPoint());
			if (wave == null)
				return;
			synchronized (listeners) {
				for (final WavesListener listener : listeners) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							listener.waveSelected(wave);
						}
					});
				}
			}
		}
	}
	
	public static interface WavesListener {
		public void waveSelected(int[][] wave);
	}
}
