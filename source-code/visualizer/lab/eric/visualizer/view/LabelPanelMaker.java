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

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lab.eric.visualizer.model.ConfigurationManager;
import lab.eric.visualizer.model.Keyphrase;
import lab.eric.visualizer.model.Topic;


/**
 * @author Samadjon Uroqov
 * 
 *         La classe pour faire le Panel des phrases-clées
 */
public class LabelPanelMaker extends JPanel {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	/*
	 * @Override public void paintComponent(Graphics g) { //TODO: FK! }
	 */

	/**
	 * Calls the method makePanel() to construct the tag cloud panel.
	 */
	public LabelPanelMaker() {
		makePanel();
	}

	/**
	 * Filters, if necessary the topics and chooses keyphrases that belong to
	 * non-filtered topics and simply places them on itself
	 */
	public void makePanel() {
		// System.out.println("makePanel called");
		double fontSizesLength = ConfigurationManager.fontSizes.length;

		Enumeration<Topic> enumTopic = ConfigurationManager.topicColorsHashtable
				.keys();

		while (enumTopic.hasMoreElements()) {
			Topic currentTopic = enumTopic.nextElement();
			if (ConfigurationManager.topicsToFilter.contains(currentTopic))
				continue;

			Color topicColor = ConfigurationManager.topicColorsHashtable
					.get(currentTopic);
			Keyphrase[] keyphrases = currentTopic.getKeyphrase();

			int keyphrasesTotalCount = keyphrases.length;
			int nbrKeyphrases = (keyphrasesTotalCount > ConfigurationManager.maxKeyphraseCount) ? ConfigurationManager.maxKeyphraseCount
					: keyphrasesTotalCount;
			int nbrFonts = (int) (nbrKeyphrases / (fontSizesLength - 1));

			if (((double) nbrFonts) < ((double) nbrKeyphrases / ((double) (fontSizesLength - 1))))
				nbrFonts++;

			int fontSizeIndex = 0;

			ArrayList<JLabel> keyphraseLabels = new ArrayList<JLabel>();
			keyphraseLabels.ensureCapacity(nbrKeyphrases);

			for (int j = 0; j < nbrKeyphrases; j++) {
				JLabel tmpLabel = new KeyphraseLabel(currentTopic
						.getKeyphraseAt(keyphrasesTotalCount - j - 1)
						.toString());
				int fontSize = (j % nbrFonts == 0) ? ConfigurationManager.fontSizes[fontSizeIndex++]
						: ConfigurationManager.fontSizes[fontSizeIndex];

				tmpLabel.setFont(new Font("arial", Font.BOLD, fontSize));
				tmpLabel.setToolTipText("<html>DEBUG <br />Keyphrase: <b>"
						+ keyphrases[keyphrasesTotalCount - j - 1]
								.getWordText()
						+ "</b><br />Score: <b>"
						+ keyphrases[keyphrasesTotalCount - j - 1]
								.getWordScore() + "</b><br />Topic ID: <b>"
						+ currentTopic.getTopicID() + "</b></html>");

				tmpLabel.setForeground(topicColor);
				keyphraseLabels.add(tmpLabel);
			}

			JLabel topicLabel = new JLabel("[Topic ID: "
					+ currentTopic.getTopicID() + "]");
			topicLabel.setFont(new Font("arial", Font.ITALIC | Font.BOLD,
					ConfigurationManager.fontSizes[3]));
			topicLabel.setForeground(Color.red);
			this.add(topicLabel);

			Collections.shuffle(keyphraseLabels);

			for (int j = 0; j < nbrKeyphrases; j++) {
				add(keyphraseLabels.get(j));
			}
		}
	}
}
