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

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import lab.eric.visualizer.model.ConfigurationManager;


/**
 * Simple dialog frame to change visually some interesting parameters in the
 * config.xml file
 * 
 * @author Samadjon Uroqov
 */
public class PreferencesDialog extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Makes a dialog frame to change visually some interesting parameters in
	 * the config.xml file. There are 3 sections to be configured: <br/>
	 * dbPanel = Database access configuration panel.<br/>
	 * visualPanel = Source table configuration panel.<br/>
	 * iconPanel = Source icons configuration panel, allows to add a new
	 * Source/Icon couple.
	 */
	public PreferencesDialog() {
		super(ConfigurationManager.applicationName + ": Préférences");
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

		Font font = new Font("arial", Font.PLAIN, 14);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setFont(font);

		JPanel dbPanel = new JPanel(new GridLayout(9, 2));
		dbPanel.setBorder(BorderFactory.createTitledBorder("Base de données"));

		JLabel webHostLabel = new JLabel("Web Host: ");
		final JTextField webHostField = new JTextField(
				ConfigurationManager.webHost);

		JLabel dbHostLabel = new JLabel("DB Host: ");
		final JTextField dbHostField = new JTextField(
				ConfigurationManager.dbHost);

		
		JLabel dbNameLabel = new JLabel("Base de données: ");
		final JTextField dbNameField = new JTextField(
				ConfigurationManager.dbName);

		dbPanel.add(dbHostLabel);
		dbPanel.add(dbHostField);
		dbPanel.add(webHostLabel);
		dbPanel.add(webHostField);
		dbPanel.add(dbNameLabel);
		dbPanel.add(dbNameField);

		JLabel dbTableLabel = new JLabel("Table: ");
		final JTextField dbTableField = new JTextField(
				ConfigurationManager.dbTable);

		JLabel dbUserLabel = new JLabel("Login: ");
		final JTextField dbUserField = new JTextField(
				ConfigurationManager.dbUser);

		dbPanel.add(dbTableLabel);
		dbPanel.add(dbTableField);
		dbPanel.add(dbUserLabel);
		dbPanel.add(dbUserField);

		JLabel dbPassLabel = new JLabel("Mot de passe: ");
		final JPasswordField dbPassField = new JPasswordField(
				ConfigurationManager.dbPass);

		JLabel dbFieldMsgLabel = new JLabel("Champ du message: ");
		final JTextField dbFieldMsgField = new JTextField(
				ConfigurationManager.dbFieldMsg);

		dbPanel.add(dbPassLabel);
		dbPanel.add(dbPassField);
		dbPanel.add(dbFieldMsgLabel);
		dbPanel.add(dbFieldMsgField);

		JLabel dbFieldAuthorLabel = new JLabel("Champ de l'auteur: ");
		final JTextField dbFieldAuthorField = new JTextField(
				ConfigurationManager.dbFieldAuthor);

		JLabel dbCommentIdNameLabel = new JLabel("ID de commentaire: ");
		final JTextField dbCommentIdNameField = new JTextField(
				ConfigurationManager.dbCommentIdName);

		dbPanel.add(dbFieldAuthorLabel);
		dbPanel.add(dbFieldAuthorField);
		dbPanel.add(dbCommentIdNameLabel);
		dbPanel.add(dbCommentIdNameField);

		JPanel visualPanel = new JPanel(new GridLayout(8, 2));
		visualPanel
				.setBorder(BorderFactory
						.createTitledBorder("Visualisation: phrases clés et graphiques"));

		JLabel fontSizesLabel = new JLabel("Tailles de police: ");
		String strFontSizes = "";

		for (int i = 0; i < ConfigurationManager.fontSizes.length; i++)
			strFontSizes += ConfigurationManager.fontSizes[i]
					+ ((i != ConfigurationManager.fontSizes.length - 1) ? ";"
							: "");

		final JTextField fontSizesField = new JTextField(strFontSizes);

		JLabel tableDateFormatLabel = new JLabel("Format de date: ");
		final JTextField tableDateFormatField = new JTextField(
				ConfigurationManager.tableDateFormat);

		JLabel divisionCountLabel = new JLabel(
				"Nombre de lignes horizontales: ");
		final JTextField divisionCountField = new JTextField(
				ConfigurationManager.divisionCount + "");

		JLabel smallSquaresSizeLabel = new JLabel("Taille des gros points: ");
		final JTextField smallSquaresSizeField = new JTextField(
				ConfigurationManager.smallSquaresSize + "");

		JLabel thicknessLabel = new JLabel("Epaisseur des lignes: ");
		final JTextField thicknessField = new JTextField(
				ConfigurationManager.thickness + "");

		final JCheckBox verticalGrid = new JCheckBox("Lignes verticales");
		verticalGrid.setSelected(!ConfigurationManager.verticalGridHide);
		final JCheckBox horizontalGrid = new JCheckBox("Lignes horizontales");
		horizontalGrid.setSelected(!ConfigurationManager.horizontalGridHide);

		JLabel rowHeightLabel = new JLabel("Hauteur des cellules: ");
		final JTextField rowHeightField = new JTextField(
				ConfigurationManager.rowHeight + "");

		JLabel colWidthLabel = new JLabel("Largeur des cellules: ");
		final JTextField colWidthField = new JTextField(
				ConfigurationManager.colWidth + "");

		visualPanel.add(fontSizesLabel);
		visualPanel.add(fontSizesField);
		visualPanel.add(tableDateFormatLabel);
		visualPanel.add(tableDateFormatField);
		visualPanel.add(divisionCountLabel);
		visualPanel.add(divisionCountField);
		visualPanel.add(smallSquaresSizeLabel);
		visualPanel.add(smallSquaresSizeField);
		visualPanel.add(thicknessLabel);
		visualPanel.add(thicknessField);
		visualPanel.add(verticalGrid);
		visualPanel.add(horizontalGrid);
		visualPanel.add(rowHeightLabel);
		visualPanel.add(rowHeightField);
		visualPanel.add(colWidthLabel);
		visualPanel.add(colWidthField);

		JPanel iconPanel = new JPanel(new GridLayout(1, 3));
		iconPanel.setBorder(BorderFactory
				.createTitledBorder("Icônes des sources"));

		final JLabel sourceIconLabel = new JLabel();
		JComboBox sourceNameCombo = new JComboBox();
		sourceNameCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				Object obj = arg0.getItem();

				if (obj instanceof String) {
					ImageIcon icon = ConfigurationManager.imageHashtable
							.get(arg0.getItem().toString());
					if (icon != null)
						sourceIconLabel.setIcon(icon);
				}
			}

		});

		Enumeration<String> enumSource = ConfigurationManager.imageHashtable
				.keys();
		while (enumSource.hasMoreElements())
			sourceNameCombo.addItem(enumSource.nextElement());

		JButton addIconButton = new JButton("Nouvelle source");
		addIconButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigurationManager.addNewSourceIcon();
			}

		});

		iconPanel.add(sourceNameCombo);
		iconPanel.add(sourceIconLabel);
		iconPanel.add(addIconButton);

		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton("Sauvegarder");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				ConfigurationManager.dbHost = dbHostField.getText();
				ConfigurationManager.webHost = webHostField.getText();
				ConfigurationManager.dbCommentIdName = dbCommentIdNameField
						.getText();
				ConfigurationManager.dbFieldAuthor = dbFieldAuthorField
						.getText();
				ConfigurationManager.dbFieldMsg = dbFieldMsgField.getText();
				ConfigurationManager.dbName = dbNameField.getText();
				ConfigurationManager.dbPass = new String(dbPassField.getPassword());
				ConfigurationManager.dbTable = dbTableField.getText();
				ConfigurationManager.dbUser = dbUserField.getText();

				String[] strFontSizes = fontSizesField.getText().split(";");
				int[] fontSizes = new int[strFontSizes.length];

				for (int i = 0; i < strFontSizes.length; i++) {
					try {
						fontSizes[i] = Integer.parseInt(strFontSizes[i]);
					} catch (Exception e) {
						fontSizes = null;
						break;
					}
				}

				if (fontSizes != null)
					ConfigurationManager.fontSizes = fontSizes;

				ConfigurationManager.tableDateFormat = tableDateFormatField
						.getText();

				int divCount = 0;
				try {
					divCount = Integer.parseInt(divisionCountField.getText());
				} catch (Exception e) {
					divCount = 0;
				}

				if (divCount != 0)
					ConfigurationManager.divisionCount = divCount;

				int smallSqSize = 0;
				try {
					smallSqSize = Integer.parseInt(smallSquaresSizeField
							.getText());
				} catch (Exception e) {
					smallSqSize = 0;
				}

				if (smallSqSize != 0)
					ConfigurationManager.smallSquaresSize = smallSqSize;

				int thickness = 0;
				try {
					thickness = Integer.parseInt(thicknessField.getText());
				} catch (Exception e) {
					thickness = 0;
				}

				if (thickness != 0)
					ConfigurationManager.thickness = thickness;

				ConfigurationManager.horizontalGridHide = !horizontalGrid
						.isSelected();
				ConfigurationManager.verticalGridHide = !verticalGrid
						.isSelected();

				int rowH = 0;
				try {
					rowH = Integer.parseInt(rowHeightField.getText());
				} catch (Exception e) {
					rowH = 0;
				}

				if (rowH != 0)
					ConfigurationManager.rowHeight = rowH;

				int colW = 0;
				try {
					colW = Integer.parseInt(colWidthField.getText());
				} catch (Exception e) {
					colW = 0;
				}

				if (colW != 0)
					ConfigurationManager.colWidth = colW;

				ConfigurationManager.writeConfiguration();
				dispose();
			}

		});
		JButton cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		mainPanel.add(new JLabel(
				"Certains paramètres prennent effet après redémarrage"));
		mainPanel.add(dbPanel);
		mainPanel.add(visualPanel);
		mainPanel.add(iconPanel);
		mainPanel.add(buttonPanel);

		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JScrollPane scroller = new JScrollPane(mainPanel);
		scroller.getVerticalScrollBar().setUnitIncrement(16);
		scroller.getHorizontalScrollBar().setUnitIncrement(16);

		getContentPane().add(scroller);
		setVisible(true);
	}

}
