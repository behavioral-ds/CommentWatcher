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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import lab.eric.visualizer.model.ConfigurationManager;
import lab.eric.visualizer.model.Topic;


/**
 * @author Samadjon Uroqov
 * 
 *         La classe qui permet de d'afficher le nuage de mots / phrases clés
 * 
 */
public class KeyphraseVisualizer extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JCheckBox[] topicCheckBoxes;
	private JPanel mainPanel = new JPanel();
	private JPanel keyphrasePanel;
	private JScrollPane scroller;
	private Hashtable<Topic, JCheckBox> hashtableTopic;

	/**
	 * Creates a new KeyphraseVisualizer. Calls the method buildGUI() to
	 * construct a keyphrase cloud panel.
	 * 
	 */
	public KeyphraseVisualizer() {
		// super(ConfigurationManager.applicationName);
		super(ConfigurationManager.applicationName
				+ ": Présentation des phrases-clées");

		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setResizable(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		ImageIcon icon = ConfigurationManager.imageHashtable
				.get(ConfigurationManager.applicationIcon);
		if (icon != null)
			setFrameIcon(icon);

		buildGUI();
	}

	/**
	 * Constructs a panel of keyphrases (a keyphrase cloud). Colors of
	 * keyphrases represent their belongings to different topics. Different font
	 * sizes represent the score (importance) of each keyphrase in defining a
	 * given topic.<br/>
	 * Constructs a set of JCheckBox that represent existing topics in memory.
	 * On each JCheckBox an ItemListener is registered, that calls the method
	 * filterTopics(Topic topic, boolean isFilter) with arguments that
	 * correspond to each represented topic.
	 */
	public void buildGUI() {
		//
		// JLabel label = new JLabel(
		// "<html>Nuage de mots selon les topics (representés par couleurs)."
		// + " Pour la présentation des sources / dates de discussions"
		// + " correspondantes, ouvrez le visualiseur des sources</html>");

		// Font font = new Font("arial", Font.ITALIC, 14);
		// label.setFont(font);

		int topicCount = ConfigurationManager.topicColorsHashtable.size();
		topicCheckBoxes = new JCheckBox[topicCount];
		hashtableTopic = new Hashtable<Topic, JCheckBox>(topicCount);

		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new GridLayout(2, topicCount / 2));
		checkBoxPanel.setBorder(BorderFactory
				.createLineBorder(Color.LIGHT_GRAY));

		Enumeration<Topic> enumTopic = ConfigurationManager.topicColorsHashtable
				.keys();

		int checkBoxIndex = 0;
		while (enumTopic.hasMoreElements()) {
			final Topic currentTopic = enumTopic.nextElement();
			Color topicColor = ConfigurationManager.topicColorsHashtable
					.get(currentTopic);
			topicCheckBoxes[checkBoxIndex] = new JCheckBox(
					currentTopic.toString());
			topicCheckBoxes[checkBoxIndex].setForeground(topicColor);
			topicCheckBoxes[checkBoxIndex]
					.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {

							JCheckBox checkBox = (JCheckBox) arg0.getSource();
							filterTopics(currentTopic, !checkBox.isSelected());
						}

					});

			topicCheckBoxes[checkBoxIndex]
					.setSelected(!ConfigurationManager.topicsToFilter
							.contains(currentTopic));
			checkBoxPanel.add(topicCheckBoxes[checkBoxIndex]);
			hashtableTopic.put(currentTopic, topicCheckBoxes[checkBoxIndex++]);
		}

		JLabel maxKeyphraseLabel = new JLabel("Nombre max de phrases clées:  ");

		Integer[] maxKeyphrases = new Integer[100];
		for (int i = 0; i < 100; i++)
			maxKeyphrases[i] = (i + 1);

		final JComboBox maxKeyphraseCombo = new JComboBox(maxKeyphrases);
		maxKeyphraseCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {

				int keyCount = ConfigurationManager.maxKeyphraseCount;

				try {
					keyCount = (Integer) maxKeyphraseCombo.getSelectedItem();
				} catch (Exception e) {
					keyCount = ConfigurationManager.maxKeyphraseCount;
				}

				ConfigurationManager.maxKeyphraseCount = keyCount;
				preparePanel();

			}

		});

		maxKeyphraseCombo.setEditable(true);

		JPanel comboPanel = new JPanel();
		// comboPanel.setLayout(new BoxLayout(comboPanel,BoxLayout.Y_AXIS));
		comboPanel.add(maxKeyphraseLabel);
		comboPanel.add(maxKeyphraseCombo);
		comboPanel.setBorder(new EmptyBorder(0, 5, 0, 0));

		// mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		// mainPanel.setLayout(new GridLayout(3,1));

		Border raisedbevel, loweredbevel, compound;
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		compound = BorderFactory
				.createCompoundBorder(raisedbevel, loweredbevel);
		compound = BorderFactory.createCompoundBorder(compound,
				new EmptyBorder(10, 10, 10, 10));

		// mainPanel.setBorder(BorderFactory.createCompoundBorder(new
		// EmptyBorder(
		// 10, 10, 10, 10), compound));
		mainPanel.setBorder(compound);

		// label.setAlignmentX(Component.CENTER_ALIGNMENT);
		// label.setBorder(new EmptyBorder(10, 10, 10, 10));
		// label.setBackground(Color.white);

		// mainPanel.add(label);

		JPanel configPanel = new JPanel();
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.X_AXIS));

		configPanel.add(checkBoxPanel);
		configPanel.add(comboPanel);
		configPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		mainPanel.setLayout(new BorderLayout());

		mainPanel.add(configPanel, BorderLayout.PAGE_START);

		scroller = new JScrollPane();
		scroller.getVerticalScrollBar().setUnitIncrement(16);
		scroller.getHorizontalScrollBar().setUnitIncrement(16);

		preparePanel();

		maxKeyphraseCombo
				.setSelectedItem(ConfigurationManager.maxKeyphraseCount);
		mainPanel.add(scroller, BorderLayout.CENTER);

		getContentPane().add(mainPanel);
		setSize(ConfigurationManager.screenWidth,
				ConfigurationManager.screenHeight);
	}

	/**
	 * If the checkbox is NOT selected, the method receives isFilter=true and
	 * adds the topic to the static ArrayList topicsToFilter in the
	 * ConfigurationManager. <br/>
	 * If the checkbox IS selected, the method receives isFilter=false and
	 * removes the topic from the static ArrayList topicsToFilter in the
	 * ConfigurationManager.<br/>
	 * Then updates the view.
	 * 
	 * @param topic
	 * @param filter
	 */
	private void filterTopics(Topic topic, boolean isFilter) {
		if (isFilter)
			ConfigurationManager.topicsToFilter.add(topic);
		else
			ConfigurationManager.topicsToFilter.remove(topic);
		// System.out.println("filterTopics called");
		preparePanel();
		MDIDesktopPane mainPane = (MDIDesktopPane) getParent();
		int compCount = mainPane.getComponentCount();

		for (int i = 0; i < compCount; i++)
			mainPane.getComponent(i).repaint();

	}

	/**
	 * Used to update the view. Replaces the existing keyphrasePanel with a new
	 * one.
	 */
	private void preparePanel() {

		if (keyphrasePanel != null)
			scroller.remove(keyphrasePanel);
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border compound = BorderFactory.createCompoundBorder(raisedbevel,
				loweredbevel);
		compound = BorderFactory.createCompoundBorder(compound,
				new EmptyBorder(10, 10, 10, 10));
		keyphrasePanel = new LabelPanelMaker();
		keyphrasePanel.setBorder(compound);
		keyphrasePanel.setBackground(Color.white);
		keyphrasePanel.setLayout(new LabelFlowLayout());
		keyphrasePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		scroller.setViewportView(keyphrasePanel);
		// invalidate();
		// repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#repaint()
	 */
	public void repaint() {
		if (hashtableTopic != null) {
			Enumeration<Topic> enumTopic = hashtableTopic.keys();
			while (enumTopic.hasMoreElements()) {
				Topic currTopic = enumTopic.nextElement();
				boolean isFiltered = ConfigurationManager.topicsToFilter
						.contains(currTopic);
				JCheckBox chBox = hashtableTopic.get(currTopic);
				if (isFiltered == chBox.isSelected()) {
					chBox.setSelected(!isFiltered);
					preparePanel();
				}
			}
		}
		super.repaint();
	}

}
