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

import java.applet.Applet;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;

import lab.eric.visualizer.model.ConfigurationManager;


/**
 * Main class of the visualizer. If run with main it runs as a desktop application.
 * It can be also called as an applet (as the popup mode - putting up a window).
 * 
 * For the browser embeded applet, select the MainAppletWindow class
 * 
 * @author Samadjon Uroqov, updated Marian-Andrei RIZOIU
 */
public class AppletPopUp extends Applet {

	private static final long serialVersionUID = -2470856365803202685L;

	/**
	 * Main method that allows it to run as an application.
	 * Le point d'entrée de l'application.
	 * 
	 * @param args
	 */
	public static void main (String args[]) {
		ConfigurationManager.isApplet = false;
		new AppletPopUp().init();
	}
	
	public void init() {
		final MainAppletWindow myApplet = new MainAppletWindow();
		ConfigurationManager.webHost = this.getCodeBase().toString();
		myApplet.init();
		myApplet.start();

		Frame applicationFrame = new Frame(ConfigurationManager.applicationName);
		applicationFrame.addWindowListener (
				new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						myApplet.stop();
						myApplet.destroy();
						System.exit(0);
					}
				}
		);

		applicationFrame.add("Center", myApplet);

		ImageIcon icon = ConfigurationManager.imageHashtable.get(ConfigurationManager.applicationIcon);
		if (icon != null)
			applicationFrame.setIconImage(icon.getImage());

//		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
		applicationFrame.setSize(ConfigurationManager.screenWidth,	ConfigurationManager.screenHeight);
		applicationFrame.setVisible(true);
		applicationFrame.setLocationRelativeTo(null);
	}
}
