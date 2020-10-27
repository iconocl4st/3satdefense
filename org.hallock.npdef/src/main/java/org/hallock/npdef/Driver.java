package org.hallock.npdef;

import java.awt.Container;
import java.util.Random;

import javax.swing.JFrame;

import org.hallock.npdef.gui.GameView;
import org.hallock.npdef.gui.SearchMonitorView;
import org.hallock.npdef.gui.TowersView;
import org.hallock.npdef.gui.UiUtil;
import org.hallock.npdef.gui.UiUtil.BoundsResettable;
import org.hallock.npdef.gui.WavesView;
import org.hallock.npdef.search.SearchMonitor;
import org.hallock.npdef.search.WaveSearchResult;
import org.hallock.npdef.search.WavesSearch;

public class Driver {

	private static void showView(String name, BoundsResettable resettable, Container container) {
		showView(new JFrame(), name, resettable, container);
	}

	private static void showView(JFrame frame, String name, BoundsResettable resettable, Container container) {
		frame.setTitle(name);
		frame.setBounds(50, 50, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(container);
		UiUtil.resetBoundsOnShown(frame, resettable);
		
		frame.setVisible(true);
	}

	public static void showTowers(Towers towers) {
		TowersView towersView = TowersView.createView();
		towersView.setTowers(towers);
		showView("Towers", towersView, towersView);
		towersView.setEditable(true);
	}
	
	public static void showWaves(Random random, Towers towers) {
		Waves waves = new Waves();
		waves.randomlySatisfy(random, towers);
		
		WavesView wavesView = WavesView.createView();
		wavesView.setWaves(waves);
		showView("Waves", wavesView, wavesView);
	}
	
	
	private static void showSearch(Random random, Towers towers) {
		SearchMonitorView searchView = SearchMonitorView.createView(random, towers);
		showView("Search", searchView, searchView);
	}

	public static void showGame(Random random, Waves waves) {
		Towers towers = new Towers();
		towers.generate(random);

		JFrame frame = new JFrame();
		GameView gameView = GameView.createView(frame, waves, towers);
		showView(frame, "Game", gameView, gameView);
	}
	
	public static void searchCmdLine(Random random, Towers towers, Waves waves) {
		System.out.println("Towers:");
		System.out.println(towers.toString());
		
		SearchMonitor monitor = new StdOutSearchMonitor();
		WaveSearchResult result = WavesSearch.search(random, towers, monitor);

		System.out.println("Fewest solution waves:");
		System.out.println(result.waves);
		System.out.println("Fewest solutions");
		System.out.println(result.possibleSolutions);
		System.out.println("Towers");
		System.out.println(towers);
	}
	
	
	public static void main(String[] args) {
		Random random = new Random(); // new Random(1776);

		Towers towers = new Towers();
		towers.generate(random);

//		showTowers(towers);
//		showWaves(random, towers);
		showSearch(random, towers);
	}

}
