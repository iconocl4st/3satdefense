package org.hallock.npdef.gui;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import org.hallock.npdef.Constants;

public class WaveAnimator {

	private TowersView view;
	
	private final LinkedList<AnimationListener> listeners;
	
	private final Timer animationTimer;
	private final Object sync = new Object();
	private TimerTask animationTask;

	public WaveAnimator(TowersView view) {
		this.view = view;
		listeners = new LinkedList<>();
		animationTimer = new Timer();
	}
	
	void addListener(AnimationListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	void removeListener(AnimationListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	void beginAnimating(int[][] wave) {
		synchronized (sync) {
			if (animationTask != null)
				return;
			animationTask = new AnimationTask(wave);
			animationTimer.scheduleAtFixedRate(
				animationTask, Constants.ANIMATION_DELAY, Constants.ANIMATION_DELAY
			);
		}
		
		synchronized (listeners) {
			for (final AnimationListener listener : listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						listener.animationBegan();
					}
				});
			}
		}
	}
	
	void cancelAnimation() {
		synchronized (sync) {
			if (animationTask == null)
				return;
			animationTask.cancel();
			animationTask = null;
		}
		
		synchronized (listeners) {
			for (final AnimationListener listener : listeners) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						listener.animationEnded();
					}
				});
			}
		}
	}
	
	boolean isAnimating() {
		return animationTask != null;
	}

	private final class AnimationTask extends TimerTask {
		int[][] wave;
		long startTime;
		
		AnimationTask(int[][] wave) {
			this.wave = wave;
			this.startTime = -1;
		}
		
		@Override
		public void run() {
			long currentTime = System.currentTimeMillis();
			
			double position;
			if (startTime < 0) {
				startTime = currentTime;
				view.setWave(wave);
				position = 0.0;
			} else {
				position = Constants.ENEMY_SPEED * (currentTime - startTime) / 1000.0;
			}

			if (position > 1.0) {
				cancelAnimation();
				view.setWave(null);
			} else {
				view.setWavePosition(position);
			}
			
			view.repaint();
		}
	}
	
	public static interface AnimationListener {
		void animationBegan();
		void animationEnded();
	}
}
