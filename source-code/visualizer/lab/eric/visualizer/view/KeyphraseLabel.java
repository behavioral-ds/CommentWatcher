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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * @author Samadjon Uroqov
 * 
 *         La classe qui permet de faire des labels à partir des phrases clées.
 *         Elle va enregistrer des ecouteurs individuels pour chaque phrase
 * 
 */
public class KeyphraseLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new KeyphraseLabel - a JLabel, to be more customized. Adds a
	 * MouseListener that changes the cursor to HAND_CURSOR when the mouse is
	 * entered. More functionality is to be added in the future.
	 * 
	 * @param labelName
	 */
	public KeyphraseLabel(String labelName) {

		super(labelName);
		final Color opaque = getBackground();
		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				// new SourceVisualizer(topic).buildGUI();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				setOpaque(true);
				setBorder(BorderFactory.createLineBorder(Color.black));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				setOpaque(false);
				setBorder(BorderFactory.createLineBorder(Color.white));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				setBackground(Color.darkGray);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				setBackground(opaque);
			}

		});
	}
}
