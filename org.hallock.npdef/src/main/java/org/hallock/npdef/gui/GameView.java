package org.hallock.npdef.gui;

import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.hallock.npdef.Towers;
import org.hallock.npdef.Waves;
import org.hallock.npdef.gui.TowersView.TowerListener;
import org.hallock.npdef.gui.UiUtil.BoundsResettable;
import org.hallock.npdef.gui.WaveAnimator.AnimationListener;
import org.hallock.npdef.gui.WavesView.WavesListener;

public class GameView extends JPanel implements BoundsResettable, WavesListener, AnimationListener {
	Waves waves;
	WavesView wavesView;
	TowersView towersView;
	JSplitPane splitPane;
	WaveAnimator animator;

	@Override
	public void resetBounds() {
		splitPane.setDividerLocation(0.5);
	}

	@Override
	public void waveSelected(int[][] wave) {
		if (animator.isAnimating())
			return;
		animator.beginAnimating(wave);
	}

	@Override
	public void animationBegan() {
		towersView.setEditable(false);
	}

	@Override
	public void animationEnded() {
		towersView.setEditable(true);
	}

	public static GameView createView(JFrame frame, Waves waves, Towers initialTowers) {
		GameView view = new GameView();
		view.waves = waves;

		view.wavesView = WavesView.createView();
		view.wavesView.setWaves(waves);
		view.wavesView.setTowers(initialTowers);
		
		view.towersView = TowersView.createView();
		view.towersView.setTowers(initialTowers);
		view.towersView.addListener(view.wavesView);
		view.towersView.setEditable(true);
		
		view.splitPane = new JSplitPane();
		view.splitPane.setLeftComponent(view.towersView);
		view.splitPane.setRightComponent(view.wavesView);
		
		view.setLayout(new GridLayout(1, 1));
		view.add(view.splitPane);

		view.animator = new WaveAnimator(view.towersView);
		view.animator.addListener(view);
		view.wavesView.addListener(view);
		view.towersView.addListener(view.new WinListener(frame));
		return view;
	}
	
	private final class WinListener implements TowerListener {
		JFrame frame;
		
		public WinListener(JFrame frame) {
			this.frame = frame;
		}
		
		@Override
		public void setTowers(Towers towers) {
			if (!waves.satisfies(towers))
				return;
			JDialog d = new JDialog(frame, "You win");
			d.setSize(100, 100);
			d.setVisible(true);
			d.add(new JLabel("Well done!"));
			d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
	}
}
