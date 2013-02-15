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
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author Samadjon Uroqov
 * 
 *         La classe des objets Article
 */
public class Article {

	private String articleTitle;

	// key=Topic ID, value = comment list
	private Hashtable<Topic, ArrayList<Comment>> topicHashtable;

	private ArrayList<Comment> totalCommentList;

	/**
	 * Creates a new instance of Article, then adds the first comment to it's
	 * topicHashtable(organized by topics) and to it's comment list(all
	 * comments)
	 * 
	 * @param Comment
	 *            comment
	 */
	public Article(Comment comment) {
		articleTitle = comment.getCommentSrcArticle();
		topicHashtable = new Hashtable<Topic, ArrayList<Comment>>();

		totalCommentList = new ArrayList<Comment>();

		addComment(comment);
	}

	/**
	 * Adds a new comment to it's topicHashtable(organized by topics) and to
	 * it's comment list(all comments)
	 * 
	 * @param comment
	 */
	public void addComment(Comment comment) {

		Topic topic = comment.getTopic();

		totalCommentList.add(comment);

		if (topicHashtable.containsKey(topic)) {
			topicHashtable.get(topic).add(comment);
		} else {
			ArrayList<Comment> newComments = new ArrayList<Comment>();
			newComments.add(comment);
			topicHashtable.put(topic, newComments);
		}
	}

	/**
	 * A simple iteration method used in debugging to make sure that
	 * topicHashtable is consistent
	 * 
	 * @throws Exception
	 *             "Invalid comment topic"
	 */
	public void debugIt() throws Exception {

		System.out.println(topicHashtable.size());

		Enumeration<Topic> enumTopic = topicHashtable.keys();

		while (enumTopic.hasMoreElements()) {
			Topic currentTopic = enumTopic.nextElement();
			ArrayList<Comment> commentlist = topicHashtable.get(currentTopic);
			int commentCount = commentlist.size();

			for (int i = 0; i < commentCount; i++) {
				if (commentlist.get(i).getTopic() != currentTopic) {
					System.out.println("current topic hash code : ");
					throw new Exception("Invalid comment Exception");
				}
			}
		}

	}

	// public static int debug = 0;

	// public Article(String articleSource, String articleTitle, Comment
	// comment,
	// Topic firstTopic) {
	//
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return (articleTitle + ": " + topicHashtable); // Debug
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.articleTitle = title;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return articleTitle;
	}

	/**
	 * @param topicHashtable
	 *            the topicHashtable to set
	 */
	public void setTopicHashtable(
			Hashtable<Topic, ArrayList<Comment>> topicHashtable) {
		this.topicHashtable = topicHashtable;
	}

	/**
	 * @return the topicHashtable
	 */
	public Hashtable<Topic, ArrayList<Comment>> getTopicHashtable() {
		return topicHashtable;
	}

	/**
	 * @param totalCommentList
	 *            the totalCommentList to set
	 */
	public void setTotalCommentList(ArrayList<Comment> totalCommentList) {
		this.totalCommentList = totalCommentList;
	}

	/**
	 * @return the totalCommentList
	 */
	public ArrayList<Comment> getTotalCommentList() {
		return totalCommentList;
	}

}
