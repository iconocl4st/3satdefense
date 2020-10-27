package org.hallock.npdef.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.hallock.npdef.gui.UiUtil.BoundsResettable;
import org.hallock.npdef.search.DiscreteDistribution;

public class DistributionView extends JPanel implements BoundsResettable {
	private static final long serialVersionUID = 1L;
	
	private double maxValue = Double.MAX_VALUE;
	private double minValue = Double.MIN_VALUE;
	private double[] values;
	private final Object sync = new Object();

	JSplitPane split;
	DistributionGraph graph;
	JLabel minValueLabel;
	JLabel maxValueLabel;

	public void setDistribution(DiscreteDistribution distribution) {
		synchronized (sync) {
			double[] probs = distribution.getProbs();
			int nBins = probs.length;
			if (values == null || values.length != nBins) {
				values = new double[nBins];
			}

			System.arraycopy(distribution.getProbs(), 0, values, 0, nBins);
			Arrays.sort(values);
			
			maxValue = values[values.length - 1];
			minValue = values[0];
			
			minValueLabel.setText("Min. Val: " + String.format("%.4f", minValue));
			maxValueLabel.setText("Max. Val: " + String.format("%.4f", maxValue));
			
			repaint();
		}
	}
	
	private final class DistributionGraph extends JPanel {
		private static final long serialVersionUID = 1L;

		public void paint(Graphics graphics) {
			synchronized (sync) {
				Graphics2D g = (Graphics2D) graphics;

				int w = getWidth();
				int h = getHeight();

				g.setColor(Color.black);
				g.fillRect(0, 0, w, h);

				if (values == null)
					return;

				g.setColor(Color.white);
				for (int i = 0; i < values.length; i++) {
					int bx = i * w / values.length;
					int ex = (i + 1) * w / values.length;
					int by = (int) (values[i] * h / maxValue);
					int ey = h;
					g.fillRect(bx, by, ex - bx, ey - by);
				}
			}
		}
	}

	@Override
	public void resetBounds() {
		split.setDividerLocation(0.25);
	}
	
	
	public static DistributionView createView() {
		DistributionView view = new DistributionView();

		JPanel statsView = new JPanel();
		statsView.setLayout(new GridLayout(1, 0));
		view.minValueLabel = new JLabel("No min");
		statsView.add(view.minValueLabel);
		view.maxValueLabel = new JLabel("No max");
		statsView.add(view.maxValueLabel);
		
		view.graph = view.new DistributionGraph();
		
		view.split = new JSplitPane();
		view.split.setLeftComponent(statsView);
		view.split.setRightComponent(view.graph);
		
		view.setLayout(new GridLayout(1, 1));
		view.add(view.split);
		
		return view;
	}
}
