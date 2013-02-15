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
import java.util.Hashtable;

/**
 * @author Samadjon Uroqov
 * 
 *         la classe pour tenir les objets de type Source
 */
public class Source {

	private String sourceName; // "rue89"
	private Hashtable<String, Article> articleHashtable; // key=article title,
															// value=article
															// object

	private Hashtable<Topic, ArrayList<Comment>> topicHashtable;

	/**
	 * Creates a new instance of Source and adds this first comment to it's
	 * articleHashtable and topicHashtable
	 * 
	 * @param comment
	 */
	public Source(Comment comment) {

		sourceName = comment.getCommentSource();
		articleHashtable = new Hashtable<String, Article>();
		topicHashtable = new Hashtable<Topic, ArrayList<Comment>>();
		addComment(comment);
	}

	/**
	 * Adds the next comment to it's articleHashtable and topicHashtable
	 * 
	 * @param comment
	 */
	public void addComment(Comment comment) {
		String article = comment.getCommentSrcArticle();
		Topic topic = comment.getTopic();

		if (topicHashtable.containsKey(topic)) {
			topicHashtable.get(topic).add(comment);
		} else {
			ArrayList<Comment> newComments = new ArrayList<Comment>();
			newComments.add(comment);
			topicHashtable.put(topic, newComments);
		}

		if (articleHashtable.containsKey(article)) {
			articleHashtable.get(article).addComment(comment);
		} else {
			Article newArticle = new Article(comment);
			articleHashtable.put(article, newArticle);
		}
	}

	public void debugIt() throws Exception {
		articleHashtable.get(articleHashtable.keys().nextElement()).debugIt();
	}

	public String toString() {
		return this.sourceName;
	}

	/**
	 * @return Element count in the articleHashtable
	 */
	public int getArticleCount() {
		return this.articleHashtable.size();
	}

	/**
	 * @return articleHashtable
	 */
	public Hashtable<String, Article> getArticleHashtable() {
		return this.articleHashtable;
	}

	/**
	 * @param sourceName
	 *            the sourceName to set
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	/**
	 * @return the sourceName
	 */
	public String getSourceName() {
		return sourceName;
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

}
