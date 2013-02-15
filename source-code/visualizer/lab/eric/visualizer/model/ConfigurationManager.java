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
package lab.eric.visualizer.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lab.eric.visualizer.lang.Messages;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Samadjon Uroqov
 * 
 *         La classe pour gèrer la configuration globale de l'application. Elle
 *         va lire, si existe le fichier de configuration config.xml et va
 *         changer les valeurs par defaut Elle gere aussi les exceptions
 *         globales de l'application. Pour assurer une configuration commune la
 *         mjorité des méthodes et des champs sont static
 */
public class ConfigurationManager {

	/*
	 * (non-javadoc)
	 */

	// private
	private Document xmlDocument;

	// general
	public static boolean isApplet = true;
	public static String applicationLang = "EN";	// ENglish and FRench available //$NON-NLS-1$
	public static String applicationName = "Visualizer"; //$NON-NLS-1$
	public static File configXmlFile;
	public static String configFilePath = "config.xml"; //$NON-NLS-1$
	public static String applicationIcon = "applicationicon"; //$NON-NLS-1$
	public static int screenWidth = 1000;
	public static int screenHeight = 700;
	private String applicationIconPath = "images/frame_icon.png"; //$NON-NLS-1$

	// Database
	public static String dbHost = "jdbc:mysql://localhost:3306/";
	public static String webHost = null;
	public static String dbName = "datasource"; //$NON-NLS-1$
	public static String dbTable = "messgearticle"; //$NON-NLS-1$
	public static String dbUser = "analyse"; //$NON-NLS-1$
	public static String dbPass = "analyse"; //$NON-NLS-1$
	public static String dbFieldMsg = "MESSAGE"; //$NON-NLS-1$
	public static String dbFieldAuthor = "NOM_AUTEUR"; //$NON-NLS-1$
	public static String dbCommentIdName = "NUM_MESG"; //$NON-NLS-1$
	// public static String dbTopicIdName = "TOPIC_ID";

	// XmlFile
	public static String thematicTagName = "Thematic"; //$NON-NLS-1$
	public static String thematicIdName = "ID"; //$NON-NLS-1$
	public static String keyphraseTagName = "Keyphrase"; //$NON-NLS-1$
	public static String keyphraseValueName = "value_key"; //$NON-NLS-1$
	public static String commentTagName = "Comment"; //$NON-NLS-1$
	public static String commentSrcArticleName = "Article"; //$NON-NLS-1$
	public static String commentSrcName = "Source"; //$NON-NLS-1$
	public static String commentDateName = "Date"; //$NON-NLS-1$
	public static String xmlDateFormat = "dd/MM/yyyy HH:mm"; //$NON-NLS-1$
	public static String commentIdName = "id"; //$NON-NLS-1$
	public static String commentWeight = "Weight"; //$NON-NLS-1$

	

	// KeyphrasePanel
	public static int[] fontSizes = { 30, 18, 15, 10, 8, 4 };
	public static int maxKeyphraseCount = 40;
	public static Color colorToMix = Color.gray;

	// SourceTable
	public static int rowHeight = 300;
	public static int colWidth = 600;
	public static int maxCommentCount = 10;
	public static String tableDateFormat = "dd/MMM/yyyy"; //$NON-NLS-1$

	public static ArrayList<Topic> topicsToFilter = new ArrayList<Topic>();
	public static Dimension rowHeaderSize = new Dimension(100, 70);
	public static Hashtable<Topic, Color> topicColorsHashtable = new Hashtable<Topic, Color>();
	public static int periodCount = 16;
	public static boolean chartPerArticle = true;
	public static boolean showRandomComment = true;

	public static int divisionCount = 4;
	public static int smallSquaresSize = 6;
	public static int thickness = 2;
	public static boolean verticalGridHide = true;
	public static boolean horizontalGridHide = false;

	// SourceImages key=source name, value=ImageIcon
	public static Hashtable<String, ImageIcon> imageHashtable;

	public static String[] sourceNames = { "rue89", "facebook", "lemonde", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"lefigaro", "twitter", "applicationicon" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	public static String[] imagePaths = { "images/rue89-actu.png", //$NON-NLS-1$
		"images/facebook-logo.jpg", "images/lemonde-logo.jpg", //$NON-NLS-1$ //$NON-NLS-2$
		"images/figaro-logo.jpg", "images/twitter-logo.gif", //$NON-NLS-1$ //$NON-NLS-2$
	"images/frame_icon.png" }; //$NON-NLS-1$

	/**
	 * La classe pour gèrer la configuration globale de l'application. Elle va
	 * lire, si existe le fichier de configuration config.xml et va changer les
	 * valeurs par defaut Elle gere aussi les exceptions globales de
	 * l'application
	 */

	public ConfigurationManager() {

		configXmlFile = new File(configFilePath);
		imageHashtable = new Hashtable<String, ImageIcon>();

		ImageIcon icon1 = makeIcon(applicationIconPath, applicationIcon);
		if (icon1 != null)
			imageHashtable.put(applicationIcon, icon1);

		boolean existTest = false;
		// too bad that sometimes this throws permissions exceptions when run as an applet
		try {
			existTest = configXmlFile.exists();
		} catch (Exception e) {
			existTest = false;
		}
		
		if ( existTest ) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;

			try {
				dBuilder = dbFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				ConfigurationManager.showErrorMessage(e, false);
				return;
			}
			try {
				xmlDocument = dBuilder.parse(configXmlFile);
			} catch (SAXException e) {
				ConfigurationManager.showErrorMessage(e, false);
				return;
			} catch (IOException e) {
				ConfigurationManager.showErrorMessage(e, false);
				return;
			}
			xmlDocument.getDocumentElement().normalize();
			parseConfiguration();
		} else {
			ImageIcon icon = null;
			for (int i = 0; i < sourceNames.length; i++) {
				icon = makeIcon(imagePaths[i], imagePaths[i]);
				if (icon != null)
					imageHashtable.put(sourceNames[i].toLowerCase(), icon);
			}
		}
	}

	private static JTextArea msgArea;

	public static Color[] topicColors;

	public static Connection dbConnection = null;

	/**
	 * Creates unique colors for topics and saves them in the static field:
	 * topicColors
	 * 
	 * @param colorCount
	 * @param makeRandom
	 */
	public static void makeTopicColors(int colorCount, boolean makeRandom) {

		if (makeRandom) {

			topicColors = new Color[colorCount];
			for (int i = 0; i < colorCount; i++)
				topicColors[i] = makeRandomColor(colorToMix);
		}

		else {

			if (colorCount > 20)
				topicColors = new Color[colorCount];
			else
				topicColors = new Color[20];

			topicColors[0] = new Color(255, 0, 0);
			topicColors[1] = new Color(99, 156, 255);
			topicColors[2] = new Color(230, 160, 70);
			topicColors[3] = new Color(140, 140, 140);
			topicColors[4] = new Color(70, 150, 70);
			topicColors[5] = new Color(52, 184, 222);
			topicColors[6] = new Color(79, 237, 224);
			topicColors[7] = new Color(202, 134, 177);
			topicColors[8] = new Color(198, 99, 165);
			topicColors[9] = new Color(99, 90, 255);

			topicColors[10] = new Color(199, 174, 145);
			topicColors[11] = new Color(219, 208, 165);
			topicColors[12] = new Color(173, 198, 148);
			topicColors[13] = new Color(137, 238, 151);
			topicColors[14] = new Color(247, 189, 132);
			topicColors[15] = new Color(206, 173, 156);
			topicColors[16] = new Color(219, 208, 165);
			topicColors[17] = new Color(99, 165, 156);
			topicColors[18] = new Color(99, 204, 213);
			topicColors[19] = new Color(244, 129, 114);

			if (colorCount > 20)
				for (int i = 20; i < colorCount; i++)
					topicColors[i] = makeRandomColor(colorToMix);
		}

		// Collections.shuffle(Arrays.asList(topicColors));
	}

	/**
	 * Writes the current configuration to the file config.xml
	 */
	public static void writeConfiguration() {

		Writer writer = new java.io.StringWriter();
		XmlWriter xmlwriter = new XmlWriter(writer, "ISO-8859-1"); //$NON-NLS-1$

		try {
			xmlwriter.writeDeclaration();
			xmlwriter
			.writeComment(Messages.getString("ConfigurationManager.37") //$NON-NLS-1$
					+ Messages.getString("ConfigurationManager.38")); //$NON-NLS-1$
			xmlwriter.writeElement("Settings"); //$NON-NLS-1$

			// General
			xmlwriter.writeElement("General"); //$NON-NLS-1$

			xmlwriter.writeElement("applicationName"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", applicationName); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.writeElement("screenWidth"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + screenWidth); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.writeElement("screenHeight"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + screenHeight); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.endElement(); // End General

			// Database
			xmlwriter.writeElement("Database"); //$NON-NLS-1$

			
			xmlwriter.writeElement("dbHost"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", dbHost); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.writeElement("webHost"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", webHost); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.writeElement("dbName"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", dbName); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.writeElement("dbTable"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", dbTable); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.writeElement("dbUser"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", dbUser); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.writeElement("dbPass"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", dbPass); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.writeElement("dbFieldMsg"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", dbFieldMsg); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.writeElement("dbFieldAuthor"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", dbFieldAuthor); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.writeElement("dbCommentIdName"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", dbCommentIdName); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.endElement(); // End Database

			// KeyphrasePanel
			xmlwriter.writeElement("KeyphrasePanel"); //$NON-NLS-1$

			xmlwriter
			.writeComment(Messages.getString("ConfigurationManager.0")); //$NON-NLS-1$
			xmlwriter.writeElement("fontSizes"); //$NON-NLS-1$
			for (int i = 0; i < fontSizes.length; i++)
				xmlwriter.writeAttribute("value" + i, "" + fontSizes[i]); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.writeElement("maxKeyphraseCount"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + maxKeyphraseCount); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			// xmlwriter.writeElement("colorToMix");
			// xmlwriter.writeAttribute("value", "" + colorToMix);
			// xmlwriter.endElement();

			xmlwriter.endElement(); // End KeyphrasePanel

			// SourceTable
			xmlwriter.writeElement("SourceTable"); //$NON-NLS-1$

			xmlwriter.writeElement("rowHeight"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + rowHeight); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.writeElement("colWidth"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + colWidth); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.writeComment(Messages.getString("ConfigurationManager.1")); //$NON-NLS-1$
			xmlwriter.writeElement("tableDateFormat"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", tableDateFormat); //$NON-NLS-1$
			xmlwriter.endElement();

			xmlwriter.writeElement("maxCommentCount"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + maxCommentCount); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.writeElement("divisionCount"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + divisionCount); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.writeElement("smallSquaresSize"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + smallSquaresSize); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.writeElement("thickness"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + thickness); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.writeElement("verticalGridHide"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + verticalGridHide); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.writeElement("horizontalGridHide"); //$NON-NLS-1$
			xmlwriter.writeAttribute("value", "" + horizontalGridHide); //$NON-NLS-1$ //$NON-NLS-2$
			xmlwriter.endElement();

			xmlwriter.endElement(); // End SourceTable

			// SourceImages

			xmlwriter.writeElement("SourceImages"); //$NON-NLS-1$
			xmlwriter
			.writeComment(Messages.getString("ConfigurationManager.2") //$NON-NLS-1$
					+ Messages.getString("ConfigurationManager.3")); //$NON-NLS-1$

			Enumeration<String> enumSource = imageHashtable.keys();

			while (enumSource.hasMoreElements()) {
				String currentSource = enumSource.nextElement();

				xmlwriter.writeElement("Source"); //$NON-NLS-1$

				xmlwriter.writeElement("Name"); //$NON-NLS-1$
				xmlwriter.writeAttribute("value", currentSource); //$NON-NLS-1$
				xmlwriter.endElement();

				xmlwriter.writeElement("ImagePath"); //$NON-NLS-1$
				xmlwriter.writeAttribute("value", //$NON-NLS-1$
						imageHashtable.get(currentSource).getDescription());
				xmlwriter.endElement();

				xmlwriter.endElement();
			}

			xmlwriter.endElement(); // End SourceImages

			xmlwriter.endElement(); // End Settings

			// XmlFile

			// xmlwriter.writeElement("XmlFile");

			xmlwriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		FileWriter fWriter = null;

		try {
			fWriter = new FileWriter(new File("config.xml")); //$NON-NLS-1$
			fWriter.write(writer.toString());
			fWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// System.out.println(writer.toString());
	}

	/**
	 * Creates a random, yet pleasant color using another color to mix
	 * 
	 * @param colorToMix
	 * @return new Color randomColor
	 */
	private static Color makeRandomColor(Color colorToMix) {

		Random random = new Random();
		int red = random.nextInt(256);
		int green = random.nextInt(256);
		int blue = random.nextInt(256);

		if (colorToMix != null) {
			red = (int) ((red + colorToMix.getRed()) / 2);
			green = (int) ((green + colorToMix.getGreen()) / 2);
			blue = (int) ((blue + colorToMix.getBlue()) / 2);
		}
		return new Color(red, green, blue);
	}

	/**
	 * Shows a static error message for the whole application in a new JFrame,
	 * for debugging purposes. If it's a fatal error (ex.: OutOfMemory), advises
	 * the user to restart
	 * 
	 * @param exception
	 * @param fatal
	 */
	public static void showErrorMessage(Object exception, boolean fatal) {
		final JFrame frame = new JFrame("DEBUG"); //$NON-NLS-1$

		Font font = new Font("arial", Font.PLAIN, 14); //$NON-NLS-1$

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JLabel msgLabel = new JLabel(
				Messages.getString("ConfigurationManager.4") //$NON-NLS-1$
				+ Messages.getString("ConfigurationManager.5")); //$NON-NLS-1$
		msgLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
		msgLabel.setFont(font);
		msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton quitButton = new JButton(Messages.getString("ConfigurationManager.6")); //$NON-NLS-1$
		quitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(1);
			}
		});
		quitButton.setFont(font);
		JButton continueButton = new JButton(Messages.getString("ConfigurationManager.7")); //$NON-NLS-1$
		continueButton.setEnabled(!fatal);
		continueButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		continueButton.setFont(font);

		JButton copyButton = new JButton(Messages.getString("ConfigurationManager.8")); //$NON-NLS-1$
		copyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Clipboard clipboard = toolkit.getSystemClipboard();
				StringSelection selection = new StringSelection(msgArea
						.getText());
				clipboard.setContents(selection, null);
			}
		});
		copyButton.setFont(font);

		JPanel buttonPanel = new JPanel();

		buttonPanel.add(continueButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(copyButton);
		buttonPanel.setBorder(new EmptyBorder(5, 10, 20, 10));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		msgArea = new JTextArea();
		// msgArea.setLineWrap(true);
		msgArea.setEditable(false);

		String errorMsg = null;
		if (exception instanceof Exception) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			((Exception) exception).printStackTrace(pw);
			errorMsg = sw.toString();
		} else if (exception instanceof String) {
			errorMsg = (String) exception;
		}

		msgArea.setText(errorMsg);
		msgArea.setCaretPosition(0);

		JScrollPane msgScroller = new JScrollPane(msgArea);

		mainPanel.add(msgLabel);
		mainPanel.add(msgScroller);
		mainPanel.add(buttonPanel);

		frame.add(mainPanel);

		frame.setSize(700, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		if ( !ConfigurationManager.isApplet ) {
			// if we are an application, there are other stuff to do
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			buttonPanel.add(quitButton);
		}
	}

	/**
	 * Parses a string into an object Color
	 * 
	 * @param colorString
	 * @return colorObject
	 */
	@SuppressWarnings("unused")
	private Color getColorFromString(String colorString) {
		if (colorString == null) {
			return Color.black;
		}
		try {

			return Color.decode(colorString);
		} catch (NumberFormatException exc) {

			try {

				final Field field = Color.class.getField(colorString);

				return (Color) field.get(null);
			} catch (Exception exc2) {
				return Color.black;
			}
		}
	}

	/**
	 * Makes an ImageIcon that has the same width as source table row headers
	 * 
	 * @param imagePath
	 * @param description
	 * @return
	 */
	private ImageIcon makeIcon(String imagePath, final String description) {

		File file = new File(imagePath);

		// too bad that sometimes this throws permissions exceptions when run as an applet
		try {
			if (!file.exists())
				return null;
		} catch (Exception e) {
			return null;
		}

		ImageIcon icon = new ImageIcon(imagePath);
		Image im = icon.getImage();

		if (description.equalsIgnoreCase(applicationIcon))
			icon = new ImageIcon(im.getScaledInstance(rowHeaderSize.width / 3,
					rowHeaderSize.height / 3, java.awt.Image.SCALE_SMOOTH),
					description);
		else
			icon = new ImageIcon(im.getScaledInstance(rowHeaderSize.width,
					rowHeaderSize.height, java.awt.Image.SCALE_SMOOTH),
					description);
		return icon;
	}

	/**
	 * Adds a new source icon to the imageHashtable and to the config.xml file.
	 * The new image is copied to the local directory "images". If the directory
	 * doesn't exist it is created.
	 * 
	 */
	public static void addNewSourceIcon() {

		final JFrame frame = new JFrame(Messages.getString("ConfigurationManager.9")); //$NON-NLS-1$
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final ImageIcon[] iconHolder = new ImageIcon[1];

		JPanel mainPanel = new JPanel(new GridLayout(2, 2));
		final JTextField sourceField = new JTextField();

		JLabel label = new JLabel(Messages.getString("ConfigurationManager.10")); //$NON-NLS-1$
		label.setBorder(new EmptyBorder(5, 5, 5, 5));

		mainPanel.add(label);
		mainPanel.add(sourceField);

		final JButton applyButton = new JButton(Messages.getString("ConfigurationManager.11")); //$NON-NLS-1$
		applyButton.setEnabled(false);
		applyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (sourceField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null,
					Messages.getString("ConfigurationManager.12")); //$NON-NLS-1$
					return;
				}

				if (iconHolder[0] != null) {
					imageHashtable.put(sourceField.getText(), iconHolder[0]);
					frame.dispose();
				} else
					JOptionPane.showMessageDialog(null,
					Messages.getString("ConfigurationManager.14")); //$NON-NLS-1$
			}

		});
		JButton chooseButton = new JButton(Messages.getString("ConfigurationManager.15")); //$NON-NLS-1$
		chooseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser(new File(".")); //$NON-NLS-1$

				if (fileChooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
					return;

				ImageIcon icon = addIcon(fileChooser.getSelectedFile());
				if (icon != null) {
					iconHolder[0] = icon;
					applyButton.setEnabled(true);
				}
			}

		});
		mainPanel.add(applyButton);
		mainPanel.add(chooseButton);

		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * Helper function for addNewSourceIcon()
	 * 
	 * @param file
	 * @return
	 */
	private static ImageIcon addIcon(File file) {
		if (!file.exists())
			return null;

		File imgDir = new File("images"); //$NON-NLS-1$
		if (!imgDir.exists())
			imgDir.mkdir();

		if (!fileCopy(file.getAbsolutePath(), "images/" + file.getName())) //$NON-NLS-1$
			return null;

		try {
			ImageIcon icon = new ImageIcon("images/" + file.getName()); //$NON-NLS-1$
			Image im = icon.getImage();

			icon = new ImageIcon(im.getScaledInstance(rowHeaderSize.width,
					rowHeaderSize.height, java.awt.Image.SCALE_SMOOTH),
					"images/" + file.getName()); //$NON-NLS-1$
			return icon;

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Utility function to copy an image file to the local directory "images".
	 * If the directory doesn't exist, it is created.
	 * 
	 * @param strSourceFile
	 * @param strDestinationFile
	 * @return
	 */
	private static boolean fileCopy(String strSourceFile,
			String strDestinationFile) {

		if (new File(strDestinationFile).exists())
			return true;

		if (strDestinationFile.toLowerCase().endsWith(".jpg") //$NON-NLS-1$
				|| strDestinationFile.toLowerCase().endsWith(".png") //$NON-NLS-1$
				|| strDestinationFile.toLowerCase().endsWith(".jif") //$NON-NLS-1$
				|| strDestinationFile.toLowerCase().endsWith(".jpeg")) { //$NON-NLS-1$

			try {

				FileInputStream fin = new FileInputStream(strSourceFile);
				FileOutputStream fout = new FileOutputStream(strDestinationFile);

				byte[] b = new byte[1024];
				int noOfBytes = 0;

				while ((noOfBytes = fin.read(b)) != -1) {
					fout.write(b, 0, noOfBytes);
				}

				fin.close();
				fout.close();

			} catch (IOException ioe) {
				return false;
			}

			return true;
		}
		return false;
	}

	/**
	 * Connects to MySQL and saves the connection in a static field, available
	 * to the whole application.
	 */
	public static void dbConnect() {
		try {

			if (dbConnection != null)
				dbConnection.close();
			Class.forName("com.mysql.jdbc.Driver"); //$NON-NLS-1$

			//String url = "jdbc:mysql://" + ConfigurationManager.dbHost + "/" //$NON-NLS-1$ //$NON-NLS-2$
			String url =  ConfigurationManager.dbHost //$NON-NLS-1$ //$NON-NLS-2$
			+ ConfigurationManager.dbName;
			String user = ConfigurationManager.dbUser;
			String password = ConfigurationManager.dbPass;
			dbConnection = DriverManager.getConnection(url, user, password);

		} catch (Exception e) {
			dbConnection = null;
		}
	}

	/**
	 * Parses, if exists, the config.xml file and changes application default
	 * settings
	 */
	private void parseConfiguration() {

		try {

			Element settings = (Element) (xmlDocument
					.getElementsByTagName("Settings").item(0)); //$NON-NLS-1$

			if (settings == null)
				return;

			// General
			Element general = (Element) (settings
					.getElementsByTagName("General").item(0)); //$NON-NLS-1$
			applicationName = ((Element) (general
					.getElementsByTagName("applicationName").item(0))) //$NON-NLS-1$
					.getAttribute("value"); //$NON-NLS-1$
			// String iconPath = ((Element) (general
			// .getElementsByTagName("applicationIcon").item(0)))
			// .getAttribute("value");
			//
			// ImageIcon appIcon = makeIcon(iconPath, iconPath);
			//
			// if (appIcon != null)
			// imageHashtable.put(applicationIcon, appIcon);

			screenWidth = Integer.parseInt(((Element) (general
					.getElementsByTagName("screenWidth").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$
			screenHeight = Integer.parseInt(((Element) (general
					.getElementsByTagName("screenHeight").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$

			// Database
			Element database = (Element) (settings
					.getElementsByTagName("Database").item(0)); //$NON-NLS-1$
			webHost = ((Element) (database.getElementsByTagName("dbHost") //$NON-NLS-1$
					.item(0))).getAttribute("value"); //$NON-NLS-1$
			dbName = ((Element) (database.getElementsByTagName("dbName") //$NON-NLS-1$
					.item(0))).getAttribute("value"); //$NON-NLS-1$
			dbTable = ((Element) (database.getElementsByTagName("dbTable") //$NON-NLS-1$
					.item(0))).getAttribute("value"); //$NON-NLS-1$
			dbUser = ((Element) (database.getElementsByTagName("dbUser") //$NON-NLS-1$
					.item(0))).getAttribute("value"); //$NON-NLS-1$
			dbPass = ((Element) (database.getElementsByTagName("dbPass") //$NON-NLS-1$
					.item(0))).getAttribute("value"); //$NON-NLS-1$
			dbFieldMsg = ((Element) (database
					.getElementsByTagName("dbFieldMsg").item(0))) //$NON-NLS-1$
					.getAttribute("value"); //$NON-NLS-1$
			dbFieldAuthor = ((Element) (database
					.getElementsByTagName("dbFieldAuthor").item(0))) //$NON-NLS-1$
					.getAttribute("value"); //$NON-NLS-1$
			// dbTopicIdName = ((Element) (database
			// .getElementsByTagName("dbTopicIdName").item(0)))
			// .getAttribute("value");
			dbCommentIdName = ((Element) (database
					.getElementsByTagName("dbCommentIdName").item(0))) //$NON-NLS-1$
					.getAttribute("value"); //$NON-NLS-1$

			// // XmlFileSettings
			// Element xmlFileSettings = (Element) (settings
			// .getElementsByTagName("XmlFile").item(0));
			// thematicTagName = ((Element) (xmlFileSettings
			// .getElementsByTagName("thematicTagName").item(0)))
			// .getAttribute("value");
			// thematicIdName = ((Element) (xmlFileSettings
			// .getElementsByTagName("thematicIdName").item(0)))
			// .getAttribute("value");
			// keyphraseTagName = ((Element) (xmlFileSettings
			// .getElementsByTagName("keyphraseTagName").item(0)))
			// .getAttribute("value");
			// keyphraseValueName = ((Element) (xmlFileSettings
			// .getElementsByTagName("keyphraseValueName").item(0)))
			// .getAttribute("value");
			// commentTagName = ((Element) (xmlFileSettings
			// .getElementsByTagName("articleTagName").item(0)))
			// .getAttribute("value");
			// commentSrcArticleName = ((Element) (xmlFileSettings
			// .getElementsByTagName("articleTitleName").item(0)))
			// .getAttribute("value");
			// commentSrcName = ((Element) (xmlFileSettings
			// .getElementsByTagName("articleUrlName").item(0)))
			// .getAttribute("value");
			// commentDateName = ((Element) (xmlFileSettings
			// .getElementsByTagName("articleDateName").item(0)))
			// .getAttribute("value");
			// xmlDateFormat = ((Element) (xmlFileSettings
			// .getElementsByTagName("xmlDateFormat").item(0)))
			// .getAttribute("value");
			// commentIdName = ((Element) (xmlFileSettings
			// .getElementsByTagName("commentIdName").item(0)))
			// .getAttribute("value");

			// KeyphrasePanel Settings
			Element clusteringSettings = (Element) (settings
					.getElementsByTagName("KeyphrasePanel").item(0)); //$NON-NLS-1$

			Node test = clusteringSettings.getElementsByTagName("fontSizes") //$NON-NLS-1$
			.item(0);
			test.getAttributes();

			for (int i = 0; i < 5; i++) {
				fontSizes[i] = Integer.parseInt(((Element) (clusteringSettings
						.getElementsByTagName("fontSizes").item(0))) //$NON-NLS-1$
						.getAttribute("value" + i)); //$NON-NLS-1$
			}
			maxKeyphraseCount = Integer.parseInt(((Element) (clusteringSettings
					.getElementsByTagName("maxKeyphraseCount").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$
			// colorToMix = getColorFromString(((Element) (clusteringSettings
			// .getElementsByTagName("colorToMix").item(0)))
			// .getAttribute("value"));

			// Source Table settings
			Element sourceTable = (Element) (settings
					.getElementsByTagName("SourceTable").item(0)); //$NON-NLS-1$
			rowHeight = Integer.parseInt(((Element) (sourceTable
					.getElementsByTagName("rowHeight").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$
			colWidth = Integer.parseInt(((Element) (sourceTable
					.getElementsByTagName("colWidth").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$
			tableDateFormat = ((Element) (sourceTable
					.getElementsByTagName("tableDateFormat").item(0))) //$NON-NLS-1$
					.getAttribute("value"); //$NON-NLS-1$

			maxCommentCount = Integer.parseInt(((Element) (sourceTable
					.getElementsByTagName("maxCommentCount").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$

			divisionCount = Integer.parseInt(((Element) (sourceTable
					.getElementsByTagName("divisionCount").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$

			smallSquaresSize = Integer.parseInt(((Element) (sourceTable
					.getElementsByTagName("smallSquaresSize").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$

			thickness = Integer.parseInt(((Element) (sourceTable
					.getElementsByTagName("thickness").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$

			verticalGridHide = Boolean.parseBoolean(((Element) (sourceTable
					.getElementsByTagName("verticalGridHide").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$

			horizontalGridHide = Boolean.parseBoolean(((Element) (sourceTable
					.getElementsByTagName("horizontalGridHide").item(0))) //$NON-NLS-1$
					.getAttribute("value")); //$NON-NLS-1$

			// SourceImages

			Element sourceImage = (Element) (settings
					.getElementsByTagName("SourceImages").item(0)); //$NON-NLS-1$
			NodeList source = sourceImage.getElementsByTagName("Source"); //$NON-NLS-1$

			int sourceCount = source.getLength();
			String srcName, srcImagePath;
			for (int i = 0; i < sourceCount; i++) {
				Element currentSource = (Element) source.item(i);
				srcName = ((Element) (currentSource
						.getElementsByTagName("Name").item(0))) //$NON-NLS-1$
						.getAttribute("value"); //$NON-NLS-1$
				srcImagePath = ((Element) (currentSource
						.getElementsByTagName("ImagePath").item(0))) //$NON-NLS-1$
						.getAttribute("value"); //$NON-NLS-1$

				ImageIcon icon = makeIcon(srcImagePath, srcImagePath);
				if (icon != null)
					imageHashtable.put(srcName.toLowerCase(), icon);
			}
		} catch (Exception e) {
			ConfigurationManager.showErrorMessage(e, false);
		}
	}
}
