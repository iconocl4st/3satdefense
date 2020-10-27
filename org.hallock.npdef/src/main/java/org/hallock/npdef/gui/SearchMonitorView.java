package org.hallock.npdef.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.util.Random;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.hallock.npdef.Constants;
import org.hallock.npdef.Driver;
import org.hallock.npdef.Towers;
import org.hallock.npdef.Waves;
import org.hallock.npdef.gui.UiUtil.BoundsResettable;
import org.hallock.npdef.search.DiscreteDistribution;
import org.hallock.npdef.search.SearchMonitor;
import org.hallock.npdef.search.WaveSearchResult;
import org.hallock.npdef.search.WavesSearch;

public class SearchMonitorView extends JPanel implements SearchMonitor, BoundsResettable {
	Random random;
	
	TowersView towersView;
	WavesView wavesView;
	JLabel numberOfSolutionsView;
	JLabel numberOfAttempts;
	JLabel statusView;
	DistributionView distributionView;
	JSplitPane mainSplit;
	JSplitPane leftSplit;
	JSplitPane rightSplit;
	boolean stop;

	@Override
	public void updateInitializationProgress(double percentage) {
		statusView.setText("Initializing: " + UiUtil.percentage(percentage));
	}

	@Override
	public void updateDistributions(DiscreteDistribution distribution) {
		statusView.setText("Searching...");
		distributionView.setDistribution(distribution);
	}

	@Override
	public void updateMinimumSolutionsFound(Waves waves, int numPossible) {
		this.wavesView.setWaves(waves);
		this.numberOfSolutionsView.setText(
				"Current number of solutions: " + numPossible 
				+ " out of " + 
						BigInteger.valueOf(Constants.NUM_LANES).pow(Constants.NUM_TOWERS)
		);
		repaint();
	}

	@Override
	public void updateTerminatingProgress(double percentage) {
		statusView.setText("Terminating: " + UiUtil.percentage(percentage));
	}

	@Override
	public void updateAttemptsMade(long l) {
		numberOfAttempts.setText("Created " + l + " waves.");
	}

	@Override
	public void setError(Exception e) {
		statusView.setText(e.toString());
		e.printStackTrace();
	}

	@Override
	public boolean shouldStop() {
		return stop;
	}

	@Override
	public void resetBounds() {
		mainSplit.setDividerLocation(0.25);
		leftSplit.setDividerLocation(0.75);
		rightSplit.setDividerLocation(0.1);
		towersView.resetBounds();
		wavesView.resetBounds();
		distributionView.resetBounds();
	}
	
	public static SearchMonitorView createView(final Random random, Towers towers) {
		final SearchMonitorView view = new SearchMonitorView();
		view.random = random;

		view.wavesView = WavesView.createView();
		view.distributionView = DistributionView.createView();

		view.towersView = TowersView.createView();	
		view.towersView.setEditable(true);
		view.towersView.setTowers(towers);

		view.numberOfSolutionsView = new JLabel("No solutions found");
		view.numberOfAttempts = new JLabel("No waves searched");
		view.statusView = new JLabel("Idle");
		final JButton startButton = new JButton("Start");
		final JButton stopButton = new JButton("Stop");
		final JButton playButton = new JButton("Play");
		stopButton.setEnabled(false);
		playButton.setEnabled(false);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				playButton.setEnabled(false);
				view.towersView.setEditable(false);
				view.stop = false;
				final Towers towers = view.towersView.getCurrentTowers();
				view.wavesView.setTowers(towers);

				new Thread(new Runnable() {
					public void run() {
						WaveSearchResult result = WavesSearch.search(
							view.random,
							towers, 
							view
						);					
					}
				}).start();
			}
		});
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.stop = true;
				stopButton.setEnabled(false);
				startButton.setEnabled(true);
				playButton.setEnabled(true);
				view.towersView.setEditable(true);
			}
		});
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Driver.showGame(random, view.wavesView.waves);
			}
		});
		
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(0, 1));
		statusPanel.add(view.statusView);
		statusPanel.add(view.numberOfSolutionsView);
		statusPanel.add(view.numberOfAttempts);
		statusPanel.add(startButton);
		statusPanel.add(stopButton);
		statusPanel.add(playButton);
		
		view.leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		view.leftSplit.setLeftComponent(view.towersView);
		view.leftSplit.setRightComponent(statusPanel);
		
		view.rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		view.rightSplit.setLeftComponent(view.distributionView);
		view.rightSplit.setRightComponent(view.wavesView);

		view.mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		view.mainSplit.setLeftComponent(view.leftSplit);
		view.mainSplit.setRightComponent(view.rightSplit);
		
		view.setLayout(new GridLayout(1, 1));
		view.add(view.mainSplit);
		
		return view;
	}
}
