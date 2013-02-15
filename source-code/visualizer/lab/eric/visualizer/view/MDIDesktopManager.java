/*******************************************************************************
 * Copyright (c) 2013 Marian-Andrei RIZOIU.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Marian-Andrei RIZOIU - initial API and implementation
 ******************************************************************************/
package lab.eric.visualizer.view;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * The class used to replace the standard DesktopManager for JDesktopPane. Used
 * to provide scrollbar functionality.
 */
public class MDIDesktopManager extends DefaultDesktopManager {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MDIDesktopPane desktop;

	/**
	 * @param desktop
	 */
	public MDIDesktopManager(MDIDesktopPane desktop) {
		this.desktop = desktop;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.DefaultDesktopManager#endResizingFrame(javax.swing.JComponent
	 * )
	 */
	public void endResizingFrame(JComponent f) {
		super.endResizingFrame(f);
		resizeDesktop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.DefaultDesktopManager#endDraggingFrame(javax.swing.JComponent
	 * )
	 */
	public void endDraggingFrame(JComponent f) {
		super.endDraggingFrame(f);
		resizeDesktop();
	}

	/**
	 * Public helper method called from MDIDesktopPane, to set initial dimension
	 * before making cascade frames or tile frames
	 */
	public void setNormalSize() {
		JScrollPane scrollPane = getScrollPane();
		int x = 0;
		int y = 0;
		Insets scrollInsets = getScrollPaneInsets();

		if (scrollPane != null) {
			Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
				d.setSize(
						d.getWidth() - scrollInsets.left - scrollInsets.right,
						d.getHeight() - scrollInsets.top - scrollInsets.bottom);
			}

			d.setSize(d.getWidth() - 20, d.getHeight() - 20);
			desktop.setAllSize(x, y);
			scrollPane.invalidate();
			scrollPane.validate();
			// scrollPane.repaint();
			// repaint();
			// System.out.println("x=" + x + " y=" + y);
		}
	}

	/**
	 * @return the main ScrollPane insets
	 */
	private Insets getScrollPaneInsets() {
		JScrollPane scrollPane = getScrollPane();
		if (scrollPane == null)
			return new Insets(0, 0, 0, 0);
		else
			return getScrollPane().getBorder().getBorderInsets(scrollPane);
	}

	/**
	 * @return the main ScrollPane
	 */
	private JScrollPane getScrollPane() {
		if (desktop.getParent() instanceof JViewport) {
			JViewport viewPort = (JViewport) desktop.getParent();
			if (viewPort.getParent() instanceof JScrollPane)
				return (JScrollPane) viewPort.getParent();
		}
		return null;
	}

	/**
	 * Each time the Frame is resized or dragged, this method is called to
	 * ensure that all frames are placed in the visible viewport of the main
	 * window
	 */
	protected void resizeDesktop() {
		int x = 0;
		int y = 0;
		JScrollPane scrollPane = getScrollPane();
		Insets scrollInsets = getScrollPaneInsets();

		if (scrollPane != null) {
			JInternalFrame allFrames[] = desktop.getAllFrames();
			for (int i = 0; i < allFrames.length; i++) {
				if (allFrames[i].getX() + allFrames[i].getWidth() > x) {
					x = allFrames[i].getX() + allFrames[i].getWidth();
				}
				if (allFrames[i].getY() + allFrames[i].getHeight() > y) {
					y = allFrames[i].getY() + allFrames[i].getHeight();
				}
			}
			Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
				d.setSize(
						d.getWidth() - scrollInsets.left - scrollInsets.right,
						d.getHeight() - scrollInsets.top - scrollInsets.bottom);
			}

			if (x <= d.getWidth())
				x = ((int) d.getWidth()) - 20;
			if (y <= d.getHeight())
				y = ((int) d.getHeight()) - 20;
			desktop.setAllSize(x, y);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}
}
