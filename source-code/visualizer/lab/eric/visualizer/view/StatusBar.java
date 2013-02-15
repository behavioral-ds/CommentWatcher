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
/**
 * 
 */
package lab.eric.visualizer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lab.eric.visualizer.lang.Messages;

/**
 * @author Samadjon Uroqov
 * 
 *         Simple JPanel that simulates the appearance of status bar. Indicates
 *         if the XML is loaded and if we have a MySQL connection
 */
public class StatusBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel statusLabel = new JLabel(Messages.getString("StatusBar.0")); //$NON-NLS-1$
	private JLabel dbLabel = new JLabel(Messages.getString("StatusBar.1")); //$NON-NLS-1$

	public StatusBar() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(10, 23));

		JPanel statusPanel = new JPanel();

		statusPanel.add(statusLabel);
		statusPanel.add(new JLabel(" | ")); //$NON-NLS-1$
		statusPanel.add(dbLabel);
		statusPanel.setOpaque(false);
		add(statusPanel, BorderLayout.WEST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int y = 0;
		g.setColor(new Color(156, 154, 140));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(196, 194, 183));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(218, 215, 201));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(233, 231, 217));
		g.drawLine(0, y, getWidth(), y);

		y = getHeight() - 3;
		g.setColor(new Color(233, 232, 218));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(233, 231, 216));
		g.drawLine(0, y, getWidth(), y);
		y = getHeight() - 1;
		g.setColor(new Color(221, 221, 220));
		g.drawLine(0, y, getWidth(), y);

	}

	/**
	 * @return the statusLabel
	 */
	public JLabel getStatusLabel() {
		return statusLabel;
	}

	/**
	 * @return dbLabel - database connection indicator
	 */
	public JLabel getDbLabel() {
		return dbLabel;
	}

	/**
	 * @param statusLabel
	 *            the statusLabel to set
	 */
	public void setStatusLabel(JLabel statusLabel) {
		this.statusLabel = statusLabel;
	}

	/**
	 * Indicates that the XML file is not loaded into memory
	 */
	public void setDefaultStatus() {
		statusLabel.setText(Messages.getString("StatusBar.3")); //$NON-NLS-1$
	}

	/**
	 * Indicate that we have not a MySQL connection
	 */
	public void setDefaultDbStatus() {
		dbLabel.setText(Messages.getString("StatusBar.4")); //$NON-NLS-1$
	}

}
