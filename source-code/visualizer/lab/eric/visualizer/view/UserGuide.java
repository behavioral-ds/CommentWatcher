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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import lab.eric.visualizer.model.ConfigurationManager;


/**
 * @author Samadjon Uroqov
 * 
 *         Simple separate frame to show the HTML formatted user guide.
 */
public class UserGuide extends JFrame {

	private static final long serialVersionUID = 1L;
	private JEditorPane guideEditor;
	private JScrollPane guideScroller;

	/**
	 * 
	 */
	public UserGuide() {
		super(ConfigurationManager.applicationName);

		// guideEditor = new JEditorPane();
		guideEditor = createEditorPane();
		if (guideEditor == null)
			return;

		guideEditor.setEditable(false);

		// guideEditor.setContentType("text/html");
		guideScroller = new JScrollPane(guideEditor);
		add(guideScroller);

		// guideEditor.setText(result);
		guideEditor.setCaretPosition(0);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		int x = (dim.width - 600) / 2;
		int y = (dim.height - 600) / 2;

		ImageIcon icon = ConfigurationManager.imageHashtable
				.get(ConfigurationManager.applicationIcon);
		if (icon != null)
			setIconImage(icon.getImage());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// setSize(ConfigurationManager.screenWidth,
		// ConfigurationManager.screenHeight);

		setLocation(x, y);
		setSize(600, 400);
		setVisible(true);

	}

	/**
	 * Creates a new HTML formatted JEditorPane from internal resources
	 * (lab.eric.visualizer.view.guide) to show a simple user guide
	 * 
	 * @return new JEditorPane
	 */
	private JEditorPane createEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		java.net.URL helpURL = UserGuide.class.getResource("guide/guide.html");
		if (helpURL != null) {
			try {
				editorPane.setPage(helpURL);
			} catch (IOException e) {
				return null;
			}
		} else {
			return null;
		}

		return editorPane;
	}

}
