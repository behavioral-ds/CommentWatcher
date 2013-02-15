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

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * @author Samadjon Uroqov
 * 
 *         Simple AboutDialog to show authors and application version
 * 
 */
public class AboutDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AboutDialog to show authors and application version,
	 * captures mouse click on email address and opens default email client of
	 * the user to send me his/her suggestions :)
	 * 
	 * @param mainAppletWindow
	 */
	public AboutDialog(MainAppletWindow mainAppletWindow) {

//		super(mainDesktopPane, "A propos de", true);

		Box box = Box.createVerticalBox();
		box.add(Box.createGlue());
		box.add(new JLabel("Visualiseur v.3"));
		JLabel separator = new JLabel();
		separator.setBorder(new EmptyBorder(10, 10, 10, 10));
		box.add(separator);
		box.add(new JLabel("Sous la direction du prof. Julien Velcin"));
		box.add(new JLabel("Auteur: Samadjon Uroqov, pour le projet Info-Stat"));
		box.add(new JLabel("Master Informatique Décisionnelle et Statistique"));
		box.add(new JLabel("2011 Université Lumière Lyon 2"));
		JLabel uriLabel = new JLabel(
				"<html>Pour info: <a href=\"mailto:samadjon@yahoo.fr\">samadjon@yahoo.fr</a><html>");
		uriLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();

					try {
						desktop.browse(new URI("mailto:samadjon@yahoo.fr"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				JLabel label = (JLabel) e.getSource();
				label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JLabel label = (JLabel) e.getSource();
				label.setCursor(Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		uriLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		box.add(uriLabel);
		box.add(Box.createGlue());
		getContentPane().add(box, "Center");

		JPanel p2 = new JPanel();
		JButton ok = new JButton("Ok");
		p2.add(ok);
		getContentPane().add(p2, "South");

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			}
		});

		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border compound = BorderFactory.createCompoundBorder(raisedbevel,
				loweredbevel);
		compound = BorderFactory.createCompoundBorder(compound,
				new EmptyBorder(10, 10, 10, 10));

		box.setBorder(compound);

		setSize(300, 150);
		pack();
	}
}
