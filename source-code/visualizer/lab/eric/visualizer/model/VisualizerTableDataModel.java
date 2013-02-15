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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import lab.eric.visualizer.view.TopicChartPanel;


/**
 * @author Samadjon Uroqov
 * 
 *         La classe qui définit le modele de données pour la table de sources
 */

public class VisualizerTableDataModel extends AbstractTableModel implements
		TableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Hashtable<String, Source> sourceHashtable;
	private Object[][] tableData;

	private Object[] rowHeaders;

	/**
	 * A Comparator to sort the comment list in a normal chronological order
	 */
	private Comparator<Comment> commentDateSorter = new Comparator<Comment>() {

		@Override
		public int compare(Comment arg0, Comment arg1) {
			return arg0.getCommentDate().compareTo(arg1.getCommentDate());
		}
	};

	/**
	 * Model used to create separately the row headers
	 */
	private ListModel rowHeaderModel = new AbstractListModel() {
		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		public int getSize() {
			return rowHeaders.length;
		}

		public Object getElementAt(int index) {
			return rowHeaders[index];
		}
	};

	private JList rowHeader = new JList(rowHeaderModel);

	/**
	 * Creates a new instance of VisualizerTableDataModel and begins to populate
	 * table cells
	 * 
	 * @param sourceHashtable1
	 */
	public VisualizerTableDataModel(Hashtable<String, Source> sourceHashtable1) {

		// System.out.println("VisualizerTableDataModel constructor");

		this.sourceHashtable = sourceHashtable1;

		if (ConfigurationManager.chartPerArticle)
			populatePerArticle();
		else
			populatePerSource();
	}

	/**
	 * Populates table per article.<br />
	 * Creates a new tableData [][] vector according to sourceCount and
	 * articleCount and populates it by iterating over Source and Article
	 * objects
	 */
	public void populatePerArticle() {
		Enumeration<String> enumSource = sourceHashtable.keys();
		int sourceCount = sourceHashtable.size();
		int articleCount = maxArticleCount();

		tableData = new Object[sourceCount][articleCount];
		rowHeaders = new Object[sourceCount];

		int rowIndex = 0;
		int colIndex = 0;

		while (enumSource.hasMoreElements()) {

			String currentSource = enumSource.nextElement();

			ImageIcon icon = ConfigurationManager.imageHashtable
					.get(currentSource.toLowerCase());

			rowHeaders[rowIndex] = (icon == null) ? currentSource : icon;

			Article currentArticleObject = null;
			Hashtable<String, Article> articleHashtable = sourceHashtable.get(
					currentSource).getArticleHashtable();
			Enumeration<String> enumArticle = articleHashtable.keys();

			while (enumArticle.hasMoreElements()) {

				String currentArticle = enumArticle.nextElement();
				currentArticleObject = articleHashtable.get(currentArticle);

				Hashtable<Topic, ArrayList<Comment>> topicHashtable = filterTopics(currentArticleObject
						.getTopicHashtable());

				Date[] firstLastDates = getFirstLastDates(topicHashtable);

				TopicChartPanel panel = new TopicChartPanel(topicHashtable,
						"L'article: " + currentArticle, firstLastDates[0],
						firstLastDates[1]);

				tableData[rowIndex][colIndex++] = panel;
			}
			colIndex = 0;
			rowIndex++;
		}
	}

	/**
	 * @param topicHashtable
	 * @return Date[] getFirstLastDates vector with 2 elements: the first and
	 *         last dates of the comments of all topics
	 */
	private Date[] getFirstLastDates(
			Hashtable<Topic, ArrayList<Comment>> topicHashtable) {
		if (topicHashtable.size() == 0)
			return new Date[] { new Date(), new Date() };

		Enumeration<Topic> enumTopic = topicHashtable.keys();
		ArrayList<Comment> totalCommentList = new ArrayList<Comment>();

		while (enumTopic.hasMoreElements()) {
			Topic topic = enumTopic.nextElement();
			totalCommentList.addAll(topicHashtable.get(topic));
		}

		Collections.sort(totalCommentList, commentDateSorter);

		Date[] firstLastDates = {
				totalCommentList.get(0).getCommentDate(),
				totalCommentList.get(totalCommentList.size() - 1).getCommentDate() 
				};

		return firstLastDates;
	}

	/**
	 * If there are topics to filter, creates a new topicHashtable and place in
	 * it all topics that aren't filtered
	 * 
	 * @param oldTopicHashtable
	 * @return newTopicHashtable
	 */
	public Hashtable<Topic, ArrayList<Comment>> filterTopics(
			Hashtable<Topic, ArrayList<Comment>> oldTopicHashtable) {
		if (ConfigurationManager.topicsToFilter.size() == 0)
			return oldTopicHashtable;
		Hashtable<Topic, ArrayList<Comment>> newTopicHashtable = new Hashtable<Topic, ArrayList<Comment>>(); 
		
		// oldTopicHashtable.size()
		// ConfigurationManager.topicsToFilter.size()

		Enumeration<Topic> enumTopic = oldTopicHashtable.keys();
		while (enumTopic.hasMoreElements()) {
			Topic topic = enumTopic.nextElement();
			if (!ConfigurationManager.topicsToFilter.contains(topic))
				newTopicHashtable.put(topic, oldTopicHashtable.get(topic));
		}

		return newTopicHashtable;
	}

	/**
	 * @param sourceHashtable the sources from which to extract the begin and end dates
	 * @return Date[] getFirstLastDates vector with 2 elements: the first and
	 *         last dates of the comments of all topics of all sources
	 */
	public Date[] getFirstLastDatesForAll(Hashtable<String, Source> sourceHashtable) {
		Vector<Date> allFirstLast = new Vector<Date>();
		
		// get all the first and last dates of sources
		Enumeration<String> enumSource = sourceHashtable.keys();
		while (enumSource.hasMoreElements()) {
			String currentSource = enumSource.nextElement();
			Source currentSourceObject = sourceHashtable.get(currentSource);
			Hashtable<Topic, ArrayList<Comment>> topicHashtable = filterTopics(currentSourceObject.getTopicHashtable());
			Date[] firstLastDates = getFirstLastDates(topicHashtable);
			
			allFirstLast.addElement(firstLastDates[0]);
			allFirstLast.addElement(firstLastDates[1]);
		}
		
		Collections.sort(allFirstLast);
		Date[] firstLastDates = {
				allFirstLast.elementAt(0),
				allFirstLast.elementAt(allFirstLast.size() - 1) 
				};
		
		return firstLastDates;
	}
	
	/**
	 * Populates table per source.<br />
	 * Creates a new tableData [][1] vector according to sourceCount and
	 * populates it by iterating over Source objects
	 */
	public void populatePerSource() {
		// System.out.println("VisualizerTableDataModel:populatePerSource chaqirildi"
		// + new Date().getTime());
		
		Date[] firstLastDates = getFirstLastDatesForAll(sourceHashtable);
		
		Enumeration<String> enumSource = sourceHashtable.keys();
		int sourceCount = sourceHashtable.size();

		tableData = new Object[sourceCount][1];
		rowHeaders = new Object[sourceCount];

		int rowIndex = 0;

		while (enumSource.hasMoreElements()) {

			String currentSource = enumSource.nextElement();

			ImageIcon icon = ConfigurationManager.imageHashtable.get(currentSource.toLowerCase());

			rowHeaders[rowIndex] = (icon == null) ? currentSource : icon;

			Source currentSourceObject = sourceHashtable.get(currentSource);
			Hashtable<Topic, ArrayList<Comment>> topicHashtable = filterTopics(currentSourceObject
					.getTopicHashtable());

//			Date[] firstLastDates = getFirstLastDates(topicHashtable);

			TopicChartPanel panel = new TopicChartPanel(topicHashtable,
					"La source: " + currentSource, firstLastDates[0],
					firstLastDates[1]);

			tableData[rowIndex++][0] = panel;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int arg0, int arg1) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	public Class<?> getColumnClass(int arg0) {

		// Object obj = tableData[0][arg0];

		return JPanel.class;
	}

	/**
	 * @return rowHeaders to be used as row headers of table with fixed
	 *         horizontal positions
	 */
	public JList getRowHeaders() {
		return rowHeader;
	}

	/**
	 * Iterates over sourceHashtable to determine maxArticleCount, necessary to
	 * create a fixed size, basic data vector
	 * tableData[sourceCount][maxArticleCount]
	 * 
	 * @return maxArticleCount
	 */
	public int maxArticleCount() {
		int maxCount = 0;
		Enumeration<String> enumSource = sourceHashtable.keys();
		while (enumSource.hasMoreElements()) {
			String currentSource = enumSource.nextElement();
			int tmpArticleCount = sourceHashtable.get(currentSource)
					.getArticleCount();
			maxCount = (tmpArticleCount > maxCount) ? tmpArticleCount
					: maxCount;
		}
		return maxCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return tableData[0].length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return tableData.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int col) {

		// System.out.println("VisualizerTableDataModel:getValueAt row=" + row +
		// "col=" + col);
		return tableData[row][col];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int arg0) {

		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		tableData[arg1][arg2] = arg0;
	}

}
