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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import lab.eric.visualizer.model.Comment;
import lab.eric.visualizer.model.ConfigurationManager;


/**
 * @author Samadjon Uroqov
 * 
 * 
 *         Defines a custom tooltip in a new JFrame, inspired from Eclipse
 *         javadoc.
 */
public class VisualizerCustomToolTip extends JFrame {

	private static final long serialVersionUID = 1L;
	private Point location;
	private String toolTipText;
	private Thread frameThread;
	private JEditorPane commentEditor;
	private final JPanel toolTipPanel = new JPanel();
	private JScrollPane commentScroller;

	/**
	 * Creates a custom tooltip inspired from Eclipse javadoc
	 * 
	 * @param toolTipText1
	 * @param location1
	 */
	public VisualizerCustomToolTip(String toolTipText1, Point location1) {
		location = location1;
		toolTipText = toolTipText1;

		String[] topics = toolTipText.split("\r\n");
		JLabel[] labels = new JLabel[topics.length];

		setUndecorated(true);
		setAlwaysOnTop(true);

		toolTipPanel.setLayout(new BoxLayout(toolTipPanel, BoxLayout.Y_AXIS));

		toolTipPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		toolTipPanel.setBackground(Color.getHSBColor(15, 3, 99));
		toolTipPanel.add(new JLabel(
				"<html>Appuyez sur 'v' pour visualiser un commentaire aléatoire,"
						+ "<br/>pris de cet intervalle de temps</html>"));
		toolTipPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		commentEditor = new JEditorPane();

		String text = "";
		commentEditor.setText(text);

		commentEditor.setEditable(false);
		// commentEditor.setLineWrap(true);
		commentEditor.setContentType("text/html");

		commentScroller = new JScrollPane(commentEditor);

		for (int i = 0; i < topics.length; i++) {
			labels[i] = new JLabel(topics[i]);
			labels[i].setForeground(Color.DARK_GRAY);
			labels[i].setFont(new Font(null, 1, 10));

			toolTipPanel.add(labels[i]);
		}

		this.getContentPane().add(toolTipPanel);
		pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int locationX = location.x + 10;
		int locationY = location.y + 10;

		if (locationX + getWidth() + 10 >= dim.width)
			locationX -= getWidth() - 20;

		if (locationY + getHeight() + 10 >= dim.height)
			locationY -= getHeight() - 20;

		setLocation(locationX, locationY);
		frameThread = new Thread(new Runnable() {

			@Override
			public void run() {
				boolean cont = true;

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					cont = false;
				}

				if (cont)
					setVisible(true);
			}
		});

		frameThread.start();

	}

	/**
	 * The tooltip is shown with a delay of 500 milliseconds. This delay is
	 * managed by a frame thread. The method first interrupts this thread and
	 * then disposes the tooltip frame completely. This is made to simulate
	 * traditional tooltips with delay. Interrupts
	 */
	public void destroy() {
		frameThread.interrupt();
		this.dispose();
	}

	/**
	 * Overloaded method to retrieve contents of a specified list of comments
	 * from MySQL
	 * 
	 * @param comments
	 */
	public void showEditor(ArrayList<Comment> comments) {

		if (ConfigurationManager.dbConnection == null) {
			destroy();
			JOptionPane.showMessageDialog(null, "Pas de connexion MySQL");
			return;
		}

		String commentsContent = "<html>";
		int commentCount = comments.size();
		commentCount = Math.min(commentCount,
				ConfigurationManager.maxCommentCount);
		for (int i = 0; i < commentCount; i++)
			commentsContent += retreiveCommentContent(comments.get(i))
					+ "<br /><br />__________________________________<br />";

		commentsContent += "</html>";

		showEditor(commentsContent);
	}

	/**
	 * Overloaded method to retrieve content of one specified comment from MySQL
	 * 
	 * @param comment
	 */
	public void showEditor(Comment comment) {

		if (ConfigurationManager.dbConnection == null) {
			destroy();
			JOptionPane.showMessageDialog(null, "Pas de connexion MySQL");
			return;
		}

		String commentContent = retreiveCommentContent(comment);
		if (commentContent == "") {
			JOptionPane.showMessageDialog(null,
					"La requête a retourné un ensemble vide", "Réponse SQL",
					JOptionPane.OK_OPTION);
			return;
		}
		showEditor("<html>" + commentContent + "</html>");
	}

	/**
	 * Overloaded method to shiw the comment content in an HTML formatted
	 * JEditorPane
	 * 
	 * @param commentContent
	 */
	private void showEditor(String commentContent) {
		if (commentContent == "")
			return;

		final JLabel maxCommentLabel = new JLabel("Nombre max de commentaires:");
		maxCommentLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
		final JTextField maxCommentField = new JTextField(""
				+ ConfigurationManager.maxCommentCount);

		maxCommentLabel.setVisible(!ConfigurationManager.showRandomComment);
		maxCommentField.setVisible(!ConfigurationManager.showRandomComment);

		ActionListener randomOrAllCommentsListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String command = arg0.getActionCommand();
				ConfigurationManager.showRandomComment = command
						.equalsIgnoreCase("Un commentaire aléatoire");
				maxCommentLabel
						.setVisible(!ConfigurationManager.showRandomComment);
				maxCommentField
						.setVisible(!ConfigurationManager.showRandomComment);
			}

		};

		ButtonGroup randomOrAllComments = new ButtonGroup();

		JRadioButton randomComment = new JRadioButton(
				"Un commentaire aléatoire");
		randomComment.addActionListener(randomOrAllCommentsListener);
		randomComment.setBackground(Color.getHSBColor(15, 3, 99));
		randomOrAllComments.add(randomComment);

		JRadioButton allComments = new JRadioButton("Tous les commentaires");
		allComments.addActionListener(randomOrAllCommentsListener);
		allComments.setBackground(Color.getHSBColor(15, 3, 99));
		randomOrAllComments.add(allComments);

		if (ConfigurationManager.showRandomComment)
			randomOrAllComments.setSelected(randomComment.getModel(), true);
		else
			randomOrAllComments.setSelected(allComments.getModel(), true);

		maxCommentField.getDocument().addDocumentListener(
				new DocumentListener() {

					@Override
					public void changedUpdate(DocumentEvent arg0) {
					}

					@Override
					public void insertUpdate(DocumentEvent event) {

						Document doc = (Document) event.getDocument();
						String text = "";

						try {
							text = doc.getText(0, doc.getLength());
						} catch (BadLocationException e) {
							ConfigurationManager.showErrorMessage(e, false);
						}
						int count = 1;
						try {
							count = Integer.parseInt(text);
						} catch (Exception e) {
							return;
						}
						ConfigurationManager.maxCommentCount = count;
					}

					@Override
					public void removeUpdate(DocumentEvent arg0) {
						insertUpdate(arg0);
					}

				});

		JPanel configPanel = new JPanel(new GridLayout(2, 2));// new
																// GridLayout(2,2)
		configPanel.add(randomComment);
		configPanel.add(allComments);
		configPanel.add(maxCommentLabel);
		configPanel.add(maxCommentField);
		configPanel.setBackground(Color.getHSBColor(15, 3, 99));

		add(configPanel, BorderLayout.NORTH);

		commentEditor.setText(commentContent);
		// System.out.println(commentEditor.getText().replaceAll("<br />",
		// "\n"));

		toolTipPanel.removeAll();
		toolTipPanel.setLayout(new BorderLayout());

		JLabel label = new JLabel();
		if (ConfigurationManager.showRandomComment)
			label.setText("<html>Le commentaire est pris aléatoirement et appartient à cette période de temps</html>");
		else
			label.setText("<html>Les commentaires appartiennent à cette période de temps</html>");

		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		toolTipPanel.add(label, BorderLayout.NORTH);
		toolTipPanel.add(new JSeparator(SwingConstants.HORIZONTAL),
				BorderLayout.CENTER);
		toolTipPanel.add(commentScroller, BorderLayout.CENTER);

		setSize(400, 300);

		int locationX = location.x + 10;
		int locationY = location.y + 10;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		if (locationX + getWidth() + 10 >= dim.width)
			locationX -= getWidth() - 30;

		if (locationY + getHeight() + 10 >= dim.height)
			locationY -= getHeight() - 20;

		setLocation(locationX, locationY);
		getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		commentEditor.setCaretPosition(0);
		// System.out.println(commentEditor.getText().replaceAll("<br />",
		// "\n"));
	}

	/**
	 * Helper method to retreive the specified comment content from MySQL
	 * 
	 * @param comment
	 * @return
	 */
	private String retreiveCommentContent(Comment comment) {

		String sqlCmd = "SELECT " + ConfigurationManager.dbFieldAuthor + ", "
				+ ConfigurationManager.dbFieldMsg + " FROM "
				+ ConfigurationManager.dbTable + " WHERE "
				+ ConfigurationManager.dbCommentIdName + " = "
				+ comment.getCommentID();

		String sqlResponse = "";

		try {

			Connection connection = ConfigurationManager.dbConnection;

			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sqlCmd);

			while (rs.next()) {
				sqlResponse += "<b>Autheur: " + rs.getString(1) + "</b><br />";
				sqlResponse += "<b>Message:</b><br />" + rs.getString(2)
						+ "<br />";
			}

			if (sqlResponse == "") {
				destroy();
				return "";
			}

		} catch (Exception exc) {
			destroy();
			JOptionPane
					.showMessageDialog(
							null,
							"Impossible d'accèder à la base de données. Assurez que la configuration MySQL soit correcte.",
							"Erreur SQL", JOptionPane.ERROR_MESSAGE);
			ConfigurationManager.showErrorMessage(exc, false);
			return "";
		}

		return

		"Commentaire ID: "
				+ comment.getCommentID()
				+ "<br />"
				+ "Commentaire Date: "
				+ new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.FRANCE)
						.format(comment.getCommentDate()) + "<br />"
				+ "Commentaire Topic: " + comment.getTopic() + "<br />"
				+ "Commentaire Source: " + comment.getCommentSource()
				+ "<br />" + "Commentaire Article: "
				+ comment.getCommentSrcArticle() + "<br />"
				+ "<br /><br />Contenu du Commentaire<br />"
				+ "------------------------<br /> " + sqlResponse;

		// "------------------------<br /><b>Example du contenu d'un commentaire.</b><br />"
		// +
		// "L'affichage d'un commentaire nécessite l'accès à la base de données.<br />"
		// +
		// "Pour le configurer, c'est très simple, il faut écrire 2 lignes de code pour faire les requêtes à la base de données "
		// +
		// "dans la méthode <i>private String retreiveCommentContent(Comment comment)</i>.<br />"
		// +
		// "Pour cela toutes les données sont déjà fournies: le ID de commentaire, l'article où il apparaît, la date, etc.";
	}

}
