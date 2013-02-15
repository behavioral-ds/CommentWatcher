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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lab.eric.visualizer.model.Comment;
import lab.eric.visualizer.model.ConfigurationManager;
import lab.eric.visualizer.model.Topic;


/**
 * @author Samadjon Uroqov
 * 
 * voir le constructeur
 */
public class TopicChartPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Hashtable<Topic, ArrayList<Comment>> topicHashtable;
	private Date firstDate;
	private Date lastDate;
	private String title;

	// ArrayList (level1) = Topics, ArrayList (level2) = Periods, ArrayList
	// (level3) = Comments
	private ArrayList<ArrayList<ArrayList<Comment>>> commentsPerTopicAndPeriod;
	// private TreeSet<Comment> commentTreeSet;
	private long period;
	private Topic[] topics;
	private int periodCount;

	int[][] commentCountPerTopic;
	private VisualizerChartEngine visualizerChartEngine;

	/**
	 * Les objets de ce type sont créés par le modèle de données et sont placés
	 * dans la table. Chacun représente une cellule sur la JTable. Ils prennent
	 * comme linput une table de hachage Topic=Liste des commentaires. Leur
	 * opération consiste en deux parties exécutées simultanément. Ils
	 * organisent les données par topic et périodes et fabriquent des points
	 * pour la construction de la graphique. Pour cela un ArrayList à trois
	 * dimensions a été choisi. Le premier niveau pour le topic, deuxième niveau
	 * pour les périodes de topic et le troisième niveau pour une liste de
	 * commentaires. En même temps ils préparent un vecteur de deux dimensions
	 * pour représenter les points. La première dimension est pour représenter
	 * les topics et la deuxième dimension  les périodes. Chaque point
	 * représente le nombre de commentaire correspondant à un topic et un
	 * intervalle donnés. Ensuite chaque objet de ce type appelle un
	 * VisualizerChartEngine pour la construction effective de la graphique
	 * 
	 * @param topicHashtable1
	 * @param title1
	 * @param firstDate1
	 * @param lastDate1
	 */
	public TopicChartPanel(
			Hashtable<Topic, ArrayList<Comment>> topicHashtable1,
			String title1, Date firstDate1, Date lastDate1) {

		// System.out.println("Start date=" + firstDate1 + "\nEnd date=" +
		// lastDate1);

		topicHashtable = topicHashtable1;
		title = title1;
		firstDate = firstDate1;
		lastDate = lastDate1;
		makePanel();

	}

	/**
	 * Removes, if exists, the current graphics engine. Used to update the view
	 * forcibly
	 */
	public void removeChart() {
		if (visualizerChartEngine != null) {
			remove(visualizerChartEngine);
			visualizerChartEngine = null;
		}
	}

	/**
	 * Constructs a three-dimensional ArrayList out of topicHashtable and a
	 * two-dimensional vector of geomethric points, in order to pass it to the
	 * graphics engine
	 */
	public void makePanel() {
		// System.out.println("makePanel called");
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setToolTipText("Bougez la souris autour des gros points pour afficher les descriptions");

		periodCount = ConfigurationManager.periodCount;

		// int topicCount = topicHashtable.size() -
		// ConfigurationManager.topicsToFilter.size();
		int topicCount = topicHashtable.size();

		period = (lastDate.getTime() - firstDate.getTime()) / periodCount;

		period = (period != 0) ? period : 1;

		topics = new Topic[topicCount];
		commentCountPerTopic = new int[topicCount][periodCount + 1];

		commentsPerTopicAndPeriod = new ArrayList<ArrayList<ArrayList<Comment>>>(
				topicCount);

		for (int i = 0; i < topicCount; i++) {
			commentsPerTopicAndPeriod.add(new ArrayList<ArrayList<Comment>>(
					periodCount + 1));
			for (int j = 0; j < periodCount + 1; j++)
				commentsPerTopicAndPeriod.get(i).add(new ArrayList<Comment>());
		}

		Enumeration<Topic> enumTopic = topicHashtable.keys();

		int topicIndex = 0;
		// int[] debug = {100,100};

		while (enumTopic.hasMoreElements()) {
			Topic currentTopic = enumTopic.nextElement();

			if (ConfigurationManager.topicsToFilter.contains(currentTopic))
				continue;
			topics[topicIndex] = currentTopic;
			ArrayList<Comment> commentList = topicHashtable
					.get(topics[topicIndex]);
			int commentCount = commentList.size();

			int indexPeriod = 0;
			for (int i = 0; i < commentCount; i++) {
				Comment comment = commentList.get(i);

				Date date = comment.getCommentDate();
				indexPeriod = (int) ((date.getTime() - firstDate.getTime()) / period);

				// // if(comment.getCommentID() == 925)
				// // System.out.println(topicIndex);
				// if(comment.getCommentID() == 875 &&
				// comment.getTopic().getTopicID() == 2 &&
				// comment.getCommentSrcArticle().equalsIgnoreCase("presidant-iranien")
				// )
				// {
				// debug[0] = topicIndex;
				// debug[1] = indexPeriod;
				// System.out.println("found " + indexPeriod);
				// }

				commentsPerTopicAndPeriod.get(topicIndex).get(indexPeriod)
						.add(comment);
			}

			for (int i = 0; i < periodCount + 1; i++) {
				int size = commentsPerTopicAndPeriod.get(topicIndex).get(i)
						.size();

				commentCountPerTopic[topicIndex][i] = size;

				// if(debug[0] == topicIndex)
				// if(debug[0] == topicIndex && debug[1] == i)
				// if(i == periodCount - 1 && topicIndex == topicCount - 1)
				// System.out.println(size + " " + i);
			}

			topicIndex++;
		}

		if (visualizerChartEngine != null) {
			remove(visualizerChartEngine);
			visualizerChartEngine = null;
		}

		visualizerChartEngine = new VisualizerChartEngine(commentCountPerTopic,
				firstDate, lastDate, topics, title, this);
		add(visualizerChartEngine);
	}

	// public void paintComponent(Graphics g)
	// {
	//
	// makePanel();
	// //super.paintComponent(g);
	// }

	/**
	 * @return new TopicChartPanel Used to create a separate frame to visualize
	 *         selected charts
	 */
	public TopicChartPanel makeNewChart() {
		return new TopicChartPanel(topicHashtable, title, firstDate, lastDate);
	}

	// public void repaint()
	// {
	// if(article != null)
	// makePanel();
	// // if(visualizerChartEngine != null && periodCount !=
	// ConfigurationManager.periodCount)
	// // makePanel();
	// // super.repaint();
	// }

	/**
	 * @return the commentsPerTopicAndPeriod
	 */
	public ArrayList<ArrayList<ArrayList<Comment>>> getCommentsPerTopicAndPeriod() {
		return commentsPerTopicAndPeriod;
	}

	public ArrayList<Comment> getCommentsPerTopicAndPeriod(int whichTopic,
			int whichPeriod) {

		return commentsPerTopicAndPeriod.get(whichTopic).get(whichPeriod);
	}

	/**
	 * @param commentsPerTopicAndPeriod
	 *            the commentsPerTopicAndPeriod to set
	 */
	public void setCommentsPerTopicAndPeriod(
			ArrayList<ArrayList<ArrayList<Comment>>> commentsPerTopicAndPeriod) {
		this.commentsPerTopicAndPeriod = commentsPerTopicAndPeriod;
	}

}
