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
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;

import lab.eric.visualizer.controller.MainController;
import lab.eric.visualizer.lang.Messages;
import lab.eric.visualizer.model.ConfigurationManager;
import lab.eric.visualizer.model.Source;
import lab.eric.visualizer.model.Topic;
import lab.eric.visualizer.model.VisualizerTableDataModel;

/**
 * @author Samadjon Uroqov
 * 
 *         La classe qui affiche la table de sources.
 * 
 */
public class SourceVisualizer extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-javadoc)
	 */
	private MainController mainController;

	/*
	 * (non-javadoc)
	 */
	private JFrame sourceVisualizerFrame;
	private Font font = new Font("arial", Font.PLAIN, 15); //$NON-NLS-1$

	private Hashtable<String, Source> sourceHashtable;

	/**
	 * Creates a new SourceVisualizer - Source table and calls the method
	 * buildGUI() to build the interface
	 * 
	 * @param sourceHashtable1
	 * @param mainController1
	 */
	public SourceVisualizer(Hashtable<String, Source> sourceHashtable1,
			MainController mainController1) {

		// super(ConfigurationManager.applicationName);
		super(ConfigurationManager.applicationName + Messages.getString("SourceVisualizer.1")); //$NON-NLS-1$
		this.mainController = mainController1;
		sourceHashtable = sourceHashtable1;
		model = new VisualizerTableDataModel(sourceHashtable);

		setSize(ConfigurationManager.screenWidth,
				ConfigurationManager.screenHeight);
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

	private JTable table;
	private JScrollPane scroller;

	private VisualizerTableDataModel model;
	private JCheckBox[] topicCheckBoxes;
	private JComboBox periodCountCombo;
	private Hashtable<Topic, JCheckBox> hashtableTopic;

	/**
	 * La méthode structure laffichage en trois grandes parties. Le panel est
	 * affiché à laide de BorderLayout. Au centre est placée la table
	 * principale de source. Les entêtes des lignes (sources) de la table sont
	 * construites séparément pour geler leur position pendant la navigation :
	 * elles sont ajouté au scroller à laide de sa méthode setRowHeaderView().
	 * Elles sont de type JList et sont peuplé également par le modèle de
	 * données pour assurer la synchronisation des données. Le panel de la
	 * configuration est affiché à laide de BoxLayout sur laxe x et comprend
	 * un panel de JCheckBox qui représente le filtrage de topics et un panel
	 * qui permet de sélectionner le nombre dintervalles de temps et le type
	 * daffichage (par source ou par article).
	 */
	public void buildGUI() {

		table = new JTable(model);
		correctTableDimensions();

		table.getTableHeader().setFont(font);
		table.setShowVerticalLines(false);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(ConfigurationManager.rowHeight);
		table.setDefaultRenderer(JPanel.class, new SourceTableCellRenderer());
		table.setDefaultEditor(JPanel.class, new SourceTableCellEditor());

		JList rowHeader = model.getRowHeaders();

		rowHeader.setFixedCellWidth(ConfigurationManager.rowHeaderSize.width);

		rowHeader.setFont(font);
		rowHeader.setFixedCellHeight(table.getRowHeight()
				+ table.getRowMargin());
		rowHeader.setCellRenderer(new RowHeaderRenderer(table));

		scroller = new JScrollPane(table);

		scroller.getVerticalScrollBar().setUnitIncrement(16);
		scroller.getHorizontalScrollBar().setUnitIncrement(16);
		scroller.setRowHeaderView(rowHeader);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		JLabel label = new JLabel(
				Messages.getString("SourceVisualizer.2") //$NON-NLS-1$
						+ Messages.getString("SourceVisualizer.3")); //$NON-NLS-1$
		label.setFont(font);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel textPanel = new JPanel();

		GridBagConstraints c = new GridBagConstraints();

		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.add(label);

		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridy = 2;
		c.gridx = 1;

		JPanel upPanel = new JPanel();
		upPanel.setLayout(new GridBagLayout());

		c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 0;
		c.gridwidth = 1;
		c.weightx = 4;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 10, 0, 10);

		upPanel.add(textPanel, c);

		mainPanel.add(upPanel, BorderLayout.PAGE_START);
		mainPanel.add(scroller, BorderLayout.CENTER);

		// Interactive
		// //////////////////////////Chekboxes

		int topicCount = ConfigurationManager.topicColorsHashtable.size();
		topicCheckBoxes = new JCheckBox[topicCount];
		hashtableTopic = new Hashtable<Topic, JCheckBox>(topicCount);

		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new GridLayout(2, topicCount / 2));
		checkBoxPanel.setBorder(BorderFactory
				.createLineBorder(Color.LIGHT_GRAY));
		// checkBoxPanel.setAlignmentY(LEFT_ALIGNMENT);

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

		// ////////////////////////////periodCount combo

		JPanel comboRadioPanel = new JPanel(new GridLayout(2, 2));
		comboRadioPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		comboRadioPanel.add(new JLabel(Messages.getString("SourceVisualizer.4"))); //$NON-NLS-1$

		Integer[] periodCounts = new Integer[50];
		for (int i = 0; i < 50; i++)
			periodCounts[i] = (i + 1);

		periodCountCombo = new JComboBox(periodCounts);
		periodCountCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				setPeriodCount();
			}

		});

		periodCountCombo.setEditable(true);
		periodCountCombo.setSelectedItem(ConfigurationManager.periodCount);
		comboRadioPanel.add(periodCountCombo);

		// /////////////////////////Per Article-Source? Radiobutton

		ActionListener perArticleOrSourceListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String command = arg0.getActionCommand();

				if (command.equalsIgnoreCase(Messages.getString("SourceVisualizer.5"))) //$NON-NLS-1$
					makePerArticle();
				else
					makePerSource();
			}

		};

		ButtonGroup perArticleOrSource = new ButtonGroup();

		JRadioButton perArticle = new JRadioButton(Messages.getString("SourceVisualizer.6")); //$NON-NLS-1$
		perArticle.addActionListener(perArticleOrSourceListener);
		perArticleOrSource.add(perArticle);
		comboRadioPanel.add(perArticle);

		JRadioButton perSource = new JRadioButton(Messages.getString("SourceVisualizer.7")); //$NON-NLS-1$
		perSource.addActionListener(perArticleOrSourceListener);
		perArticleOrSource.add(perSource);
		comboRadioPanel.add(perSource);

		if (ConfigurationManager.chartPerArticle)
			perArticleOrSource.setSelected(perArticle.getModel(), true);
		else
			perArticleOrSource.setSelected(perSource.getModel(), true);

		JPanel configPanel = new JPanel();
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.X_AXIS));

		configPanel.add(checkBoxPanel);
		configPanel.add(comboRadioPanel);

		mainPanel.add(configPanel, BorderLayout.PAGE_END);
		getContentPane().add(mainPanel);
		// setLocationRelativeTo(null);
		// setVisible(true);
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

		updateTableDataModel();
		// forceUpdateTable();
		// System.out.println("filterTopics called");
		// table.repaint();

		MDIDesktopPane mainPane = (MDIDesktopPane) getParent();
		int compCount = mainPane.getComponentCount();

		for (int i = 0; i < compCount; i++)
			mainPane.getComponent(i).repaint();
	}

	/**
	 * Just sets ConfigurationManager.chartPerArticle to true and changes the
	 * table data model
	 */
	private void makePerArticle() {
		ConfigurationManager.chartPerArticle = true;
		// forceRemovePanels();
		updateTableDataModel();
		// correctTableDimensions();
	}

	/**
	 * Just sets ConfigurationManager.chartPerArticle to false and changes the
	 * table data model
	 */
	private void makePerSource() {
		ConfigurationManager.chartPerArticle = false;
		// forceRemovePanels();
		updateTableDataModel();
		// correctTableDimensions();
	}

	/**
	 * Just sets ConfigurationManager.periodCount to desired integer and changes
	 * the table data model
	 */
	private void setPeriodCount() {
		int perCount = ConfigurationManager.periodCount;

		try {
			perCount = (Integer) periodCountCombo.getSelectedItem();
		} catch (Exception e) {
			perCount = ConfigurationManager.periodCount;
		}

		if (perCount != 0) {
			ConfigurationManager.periodCount = perCount;
			updateTableDataModel();
		}
	}

	/**
	 * Changes the table data model and updates its dimensions
	 */
	public void updateTableDataModel() {
		model = new VisualizerTableDataModel(sourceHashtable);
		table.setModel(model);
		correctTableDimensions();

	}

	/**
	 * Changes the table dimensions according to new view requirements
	 */
	private void correctTableDimensions() {
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			int width = ConfigurationManager.chartPerArticle ? ConfigurationManager.colWidth
					: (2 * ConfigurationManager.colWidth);
			col.setPreferredWidth(width);
		}
	}

	//
	/**
	 * Parfois je n'arrive pas à eliminer les panels anciens, quand il y a un
	 * changement. J'ai utilisé cette méthode pour une mise à jour de vues
	 * "avec force"
	 */
	public void forceRemovePanels() {
		int rowCount = table.getRowCount();
		int colCount = table.getColumnCount();

		for (int i = 0; i < rowCount; i++)
			for (int j = 0; j < colCount; j++) {
				Object obj = table.getValueAt(i, j);
				if (obj instanceof TopicChartPanel)
					((TopicChartPanel) obj).removeChart();
			}
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
					updateTableDataModel();
				}
			}
		}

		super.repaint();
	}

	//
	/**
	 * Getter of the property <tt>mainController</tt>
	 * 
	 * @return Returns the mainController.
	 * 
	 */

	public MainController getMainController() {
		return mainController;
	}

	/**
	 * Setter of the property <tt>mainController</tt>
	 * 
	 * @param mainController
	 *            The mainController to set.
	 * 
	 */
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	/**
	 * Getter of the property <tt>sourceVisualizerFrame</tt>
	 * 
	 * @return Returns the sourceVisualizerFrame.
	 * 
	 */

	public JFrame getSourceVisualizerFrame() {
		return sourceVisualizerFrame;
	}

	/**
	 * Setter of the property <tt>sourceVisualizerFrame</tt>
	 * 
	 * @param sourceVisualizerFrame
	 *            The sourceVisualizerFrame to set.
	 * 
	 */
	public void setSourceVisualizerFrame(JFrame sourceVisualizerFrame) {
		this.sourceVisualizerFrame = sourceVisualizerFrame;
	}

}
