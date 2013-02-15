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
package lab.eric.visualizer.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JOptionPane;

import lab.eric.visualizer.lang.Messages;
import lab.eric.visualizer.model.CommentManager;
import lab.eric.visualizer.model.ConfigurationManager;
import lab.eric.visualizer.model.Source;
import lab.eric.visualizer.model.SqlManager;
import lab.eric.visualizer.model.Topic;
import lab.eric.visualizer.model.TopicsContainer;
import lab.eric.visualizer.model.XmlFileParser;
import lab.eric.visualizer.view.KeyphraseVisualizer;
import lab.eric.visualizer.view.MainAppletWindow;
import lab.eric.visualizer.view.SourceVisualizer;


/**
 * @author Samadjon Uroqov
 * 
 *         La classe qui contrôle les évenements, la transformation de données
 *         pour les fenetres
 * 
 */
public class MainController {

	private Hashtable<String, Source> sourceHashtable;

	/**
	 * Starts the XML parser thread (class XmlFileParser). It's done in a
	 * different thread in order to be able to handle
	 * java.lang.OutOfMemoryError, it occurs when the XML file is too big. When
	 * this occurs, the thread does terminate but doesn't return (accomplish the
	 * job) and we know that there was an OutOfMemoryError. This prevents the
	 * whole application thread from hanging
	 * 
	 * @param fileToParse
	 */
	public void startXmlParser(File fileToParse) {

		mainAppletWindow.getStatusBar().getStatusLabel().setText(Messages.getString("MainController.0")); //$NON-NLS-1$

		xmlFileParser = new XmlFileParser(fileToParse);
		if (ConfigurationManager.isApplet) {
			URL home = null;
			try {
				home = new URL(ConfigurationManager.webHost);
			} catch (MalformedURLException e) {
				ConfigurationManager.showErrorMessage(e, true);
				e.printStackTrace();
			}
			xmlFileParser.setConnexionDetails(home, "visualize");
		}
		Thread t = new Thread(xmlFileParser);
		t.start();

		try {
			t.join();
		} catch (InterruptedException e) {
			ConfigurationManager.showErrorMessage(e, false);
			e.printStackTrace();
		}

		CommentManager commentManager = xmlFileParser.getCommentManager();

		if (commentManager != null) {

			sourceHashtable = commentManager.getSourceHashtable();
			topicsContainer = xmlFileParser.getTopicsContainer();
			createColorsHashtable();

			mainAppletWindow.getStatusBar().getStatusLabel()
					.setText(Messages.getString("MainController.1")); //$NON-NLS-1$
			mainAppletWindow.getVisualizerMenu().setEnabled(true);

		} else {

			if (sourceHashtable != null)
				mainAppletWindow
						.getStatusBar()
						.getStatusLabel()
						.setText(
								Messages.getString("MainController.2")); //$NON-NLS-1$
			else {
				mainAppletWindow.getStatusBar().setDefaultStatus();
				mainAppletWindow.getVisualizerMenu().setEnabled(false);
			}

			if (XmlFileParser.fatalError) {
				ConfigurationManager
						.showErrorMessage(
								Messages.getString("MainController.3") //$NON-NLS-1$
										+ Messages.getString("MainController.4") //$NON-NLS-1$
										+ Messages.getString("MainController.5") //$NON-NLS-1$
										+ Messages.getString("MainController.6"), //$NON-NLS-1$
								true);
			} else {
				JOptionPane.showMessageDialog(mainAppletWindow,
						Messages.getString("MainController.7"), Messages.getString("MainController.8"), //$NON-NLS-1$ //$NON-NLS-2$
						JOptionPane.ERROR_MESSAGE);

			}
		}
	}

	/**
	 * Creates unique colors for topics and saves them in a static hashtable of
	 * ConfigurationManager
	 * 
	 */
	private void createColorsHashtable() {

		ArrayList<Topic> topics = topicsContainer.getTopicList();
		int topicCount = topics.size();
		ConfigurationManager.makeTopicColors(topicCount, false);
		for (int i = 0; i < topicCount; i++)
			ConfigurationManager.topicColorsHashtable.put(topics.get(i),
					ConfigurationManager.topicColors[i]);
	}

	/**
	 * @author Samadjon Uroqov
	 * 
	 *         La sous-classe pour gerer l'ouverture du fichier xml
	 */

	/*
	 * (non-javadoc)
	 */

	/**
	 * At first tries to connect to MySQL and then creates a new instance of
	 * SourceVisualizer = a Source Table
	 * 
	 * @return new SourceVisualizer
	 * 
	 */
	public SourceVisualizer createSourceVisualizer() {
		ConfigurationManager.dbConnect();
		if (ConfigurationManager.dbConnection != null)
			mainAppletWindow.getStatusBar().getDbLabel()
					.setText(Messages.getString("MainController.9")); //$NON-NLS-1$
		else
			mainAppletWindow.getStatusBar().setDefaultDbStatus();
		return new SourceVisualizer(sourceHashtable, this);
	}

	/**
	 * Creates a new instance of KeyphraseVisualizer = a Keyphrase Panel
	 * 
	 * @return new KeyphraseVisualizer
	 */
	public KeyphraseVisualizer createClusterVisualizer() {
		// return new KeyphraseVisualizer(topicsContainer);
		return new KeyphraseVisualizer();
	}

	/*
	 * (non-javadoc)
	 */
	private KeyphraseVisualizer keyphraseVisualizer;

	/*
	 * (non-javadoc)
	 */
	private SourceVisualizer sourceVisualizer;

	/*
	 * (non-javadoc)
	 */
	private XmlFileParser xmlFileParser;

	/*
	 * (non-javadoc)
	 */
	private SqlManager sqlManager;

	/*
	 * (non-javadoc)
	 */
	private TopicsContainer topicsContainer;

	/*
	 * (non-javadoc)
	 */
	private ConfigurationManager configurationManager;

	private MainAppletWindow mainAppletWindow;

	/**
	 * Initializes the whole application: creates a new instances of
	 * ConfigurationManager and sets the MainAppletWindow instance. Called from
	 * the constructor of the Applet.
	 */
	public void initializeApplication(MainAppletWindow applet) {
		configurationManager = new ConfigurationManager();
		mainAppletWindow = applet;
	}

	/**
	 * Getter of the property <tt>keyphraseVisualizer</tt>
	 * 
	 * @return Returns the keyphraseVisualizer.
	 * 
	 */

	public KeyphraseVisualizer getClusterVisualizer() {
		return keyphraseVisualizer;
	}

	/**
	 * Setter of the property <tt>keyphraseVisualizer</tt>
	 * 
	 * @param keyphraseVisualizer
	 *            The keyphraseVisualizer to set.
	 * 
	 */
	public void setClusterVisualizer(KeyphraseVisualizer keyphraseVisualizer) {
		this.keyphraseVisualizer = keyphraseVisualizer;
	}

	/**
	 * Getter of the property <tt>sourceVisualizer</tt>
	 * 
	 * @return Returns the sourceVisualizer.
	 * 
	 */

	public SourceVisualizer getSourceVisualizer() {
		return sourceVisualizer;
	}

	/**
	 * Setter of the property <tt>sourceVisualizer</tt>
	 * 
	 * @param sourceVisualizer
	 *            The sourceVisualizer to set.
	 * 
	 */
	public void setSourceVisualizer(SourceVisualizer sourceVisualizer) {
		this.sourceVisualizer = sourceVisualizer;
	}

	/**
	 * Getter of the property <tt>xmlFileParser</tt>
	 * 
	 * @return Returns the xmlFileParser.
	 * 
	 */

	public XmlFileParser getXmlFileParser() {
		return xmlFileParser;
	}

	/**
	 * Setter of the property <tt>xmlFileParser</tt>
	 * 
	 * @param xmlFileParser
	 *            The xmlFileParser to set.
	 * 
	 */
	public void setXmlFileParser(XmlFileParser xmlFileParser) {
		this.xmlFileParser = xmlFileParser;
	}

	/**
	 * Getter of the property <tt>sqlManager</tt>
	 * 
	 * @return Returns the sqlManager.
	 * 
	 */

	public SqlManager getSqlManager() {
		return sqlManager;
	}

	/**
	 * Setter of the property <tt>sqlManager</tt>
	 * 
	 * @param sqlManager
	 *            The sqlManager to set.
	 * 
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	/**
	 * Getter of the property <tt>topicsContainer</tt>
	 * 
	 * @return Returns the topicsContainer.
	 * 
	 */

	public TopicsContainer getTopicsContainer() {
		return topicsContainer;
	}

	/**
	 * Setter of the property <tt>topicsContainer</tt>
	 * 
	 * @param topicsContainer
	 *            The topicsContainer to set.
	 * 
	 */
	public void setTopicsContainer(TopicsContainer topicsContainer) {
		this.topicsContainer = topicsContainer;
	}

	/**
	 * Getter of the property <tt>configurationManager</tt>
	 * 
	 * @return Returns the configurationManager.
	 * 
	 */

	public ConfigurationManager getConfigurationManager() {
		return configurationManager;
	}

	/**
	 * Setter of the property <tt>configurationManager</tt>
	 * 
	 * @param configurationManager
	 *            The configurationManager to set.
	 * 
	 */
	public void setConfigurationManager(
			ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}

	/**
	 * @param mainAppletWindow
	 *            the mainAppletWindow to set
	 */
	public void setMainDesktopPane(MainAppletWindow mainAppletWindow) {
		this.mainAppletWindow = mainAppletWindow;
	}

	/**
	 * @return the mainAppletWindow
	 */
	public MainAppletWindow getMainDesktopPane() {
		return mainAppletWindow;
	}

	/**
	 * @return the sourceHashtable
	 */
	public Hashtable<String, Source> getSourceHashtable() {
		return sourceHashtable;
	}

	/**
	 * @param sourceHashtable
	 *            the sourceHashtable to set
	 */
	public void setSourceHashtable(Hashtable<String, Source> sourceHashtable) {
		this.sourceHashtable = sourceHashtable;
	}

}
