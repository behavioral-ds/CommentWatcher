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
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import lab.eric.visualizer.controller.MainController;
import lab.eric.visualizer.lang.Messages;
import lab.eric.visualizer.model.ConfigurationManager;

/**
 * 
 * C'est la fenêtre principale de l'application. Elle contient un gestionnaire
 * de l'affichage multi-fenêtres (MDI). Les deux fenêtres de visualisation sont
 * y placées
 * 
 * @author Samadjon Uroqov, updated Marian-Andrei RIZOIU
 * 
 */
public class MainAppletWindow extends JApplet {

	private static final long serialVersionUID = 1L;
	/**
	 * The desktop pane on which all other internal frames are put.
	 */
	private MDIDesktopPane desktop = new MDIDesktopPane();
	private JMenuBar menuBar = new JMenuBar();

	private JMenu fileMenu = new JMenu(Messages.getString("MainAppletWindow.0")); //$NON-NLS-1$
	private JMenuItem openMenu = new JMenuItem(Messages.getString("MainAppletWindow.9")); //$NON-NLS-1$
	private JMenuItem refreshMenu = new JMenuItem(Messages.getString("MainAppletWindow.10")); //$NON-NLS-1$
	private JMenuItem configMenu = new JMenuItem(Messages.getString("MainAppletWindow.1")); //$NON-NLS-1$
	private JMenuItem exitMenu = new JMenuItem(Messages.getString("MainAppletWindow.2")); //$NON-NLS-1$

	private JMenu visualizerMenu = new JMenu(Messages.getString("MainAppletWindow.3")); //$NON-NLS-1$
	private JMenuItem visualKeyphraseMenu = new JMenuItem(Messages.getString("MainAppletWindow.4")); //$NON-NLS-1$
	private JMenuItem visualGraphMenu = new JMenuItem(Messages.getString("MainAppletWindow.5")); //$NON-NLS-1$

	private JMenu helpMenu = new JMenu(Messages.getString("MainAppletWindow.6")); //$NON-NLS-1$
	private JMenuItem userGuideMenu = new JMenuItem(Messages.getString("MainAppletWindow.7")); //$NON-NLS-1$
	private JMenuItem aboutMenu = new JMenuItem(Messages.getString("MainAppletWindow.8")); //$NON-NLS-1$

	/**
	 * All internal frames are placed under a JScrollPane to make a convenient
	 * view, when not enough place for all frames
	 */
	private JScrollPane scrollPane = new JScrollPane();

	private MainController mainController;
	private boolean xmlReady = false;

	private StatusBar statusBar = new StatusBar();

	public MainAppletWindow() {
		mainController = new MainController();
		mainController.initializeApplication(this);
	}

	/* 
	 * Creates a new (and unique) instance of application's main MDI Frame
	 * 
	 * (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		//		super(ConfigurationManager.applicationName);

		menuBar.add(fileMenu);
		if ( !ConfigurationManager.isApplet )
			fileMenu.add(openMenu); 
		else
			fileMenu.add(refreshMenu);
		fileMenu.add(configMenu);
		if ( !ConfigurationManager.isApplet )
			fileMenu.add(exitMenu);

		menuBar.add(visualizerMenu);
		visualizerMenu.setEnabled(xmlReady);
		visualizerMenu.add(visualKeyphraseMenu);
		visualizerMenu.add(visualGraphMenu);

		menuBar.add(new WindowMenu(desktop));

		menuBar.add(helpMenu);
		helpMenu.add(userGuideMenu);
		helpMenu.add(aboutMenu);

		openMenu.addActionListener(new ActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			public void actionPerformed(ActionEvent ae) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				/**
				 * The file chooser is managed to 1) show only *.xml files to
				 * select for opening
				 */

				JFileChooser fileChooser = new JFileChooser(new File(".")); //$NON-NLS-1$
				fileChooser.addChoosableFileFilter(new FileFilter() {

					@Override
					public boolean accept(File arg0) {

						return (arg0.isDirectory() || arg0.getName()
								.toLowerCase().endsWith(".xml")); //$NON-NLS-1$
					}

					@Override
					public String getDescription() {

						return "*.xml"; //$NON-NLS-1$
					}

				});

				if (fileChooser.showOpenDialog(MainAppletWindow.this) != JFileChooser.APPROVE_OPTION) {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}

				File file = fileChooser.getSelectedFile();
				mainController.startXmlParser(file);
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		
		refreshMenu.addActionListener(new ActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			public void actionPerformed(ActionEvent ae) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				mainController.startXmlParser(null);
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		
		configMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				desktop.add(new PreferencesDialog());
			}
		});

		exitMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		});

		visualKeyphraseMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				desktop.add(mainController.createClusterVisualizer());
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});

		visualGraphMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				desktop.add(mainController.createSourceVisualizer());
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});

		userGuideMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				new UserGuide();
			}
		});

		aboutMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				AboutDialog about = new AboutDialog(MainAppletWindow.this);
				about.setLocationRelativeTo(desktop);
				about.setVisible(true);

			}
		});

		setJMenuBar(menuBar);

		scrollPane.getViewport().add(desktop);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

		Container contentPane = getContentPane();

		contentPane.setLayout(new BorderLayout());
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(statusBar, BorderLayout.SOUTH);

		setSize(ConfigurationManager.screenWidth, ConfigurationManager.screenHeight);
		setVisible(true);
		
		// final touches in case we are an applet
		if (ConfigurationManager.isApplet) {
			// set the database location (same as the Applet source)
			if (ConfigurationManager.webHost == null)
				ConfigurationManager.webHost = this.getCodeBase().toString();
			mainController.startXmlParser(null);
		}
	}

	/**
	 * @return the desktop
	 */
	public MDIDesktopPane getDesktop() {
		return desktop;
	}

	/**
	 * @param desktop
	 *            the desktop to set
	 */
	public void setDesktop(MDIDesktopPane desktop) {
		this.desktop = desktop;
	}

	/**
	 * @return the statusBar
	 */
	public StatusBar getStatusBar() {
		return statusBar;
	}

	/**
	 * @param statusBar
	 *            the statusBar to set
	 */
	public void setStatusBar(StatusBar statusBar) {
		this.statusBar = statusBar;
	}

	/**
	 * @return the visualizerMenu
	 */
	public JMenu getVisualizerMenu() {
		return visualizerMenu;
	}

	/**
	 * @param visualizerMenu
	 *            the visualizerMenu to set
	 */
	public void setVisualizerMenu(JMenu visualizerMenu) {
		this.visualizerMenu = visualizerMenu;
	}

}
