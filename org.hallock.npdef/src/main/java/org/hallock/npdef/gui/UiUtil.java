package org.hallock.npdef.gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;


public class UiUtil {
	
	public static String percentage(double d) {
		return String.format("%.03f%%", 100 * d);
	}
	
	public static interface BoundsResettable { void resetBounds(); }
	public static void resetBoundsOnShown(JFrame frame, final BoundsResettable view) {
		frame.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
				view.resetBounds();
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				view.resetBounds();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {}
			
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
	}
}
