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

import java.util.Date;

/**
 * @author Samadjon Uroqov
 * 
 *         La classe pour tenir les objets de type Comment
 */
public class Comment {

	/*
	 * (non-javadoc)
	 */
	private Date commentDate;

	/*
	 * (non-javadoc)
	 */
	private int commentID;

	/*
	 * (non-javadoc)
	 */
	private String commentSource;

	/*
	 * (non-javadoc)
	 */
	private String commentSrcArticle;

	private Topic topic;

	private double commentWeight;
	public double getCommentWeight() {
		return commentWeight;
	}

	public void setCommentWeight(double commentWeight) {
		this.commentWeight = commentWeight;
	}

	/**
	 * Creates a new instance of Comment
	 * 
	 * @param commentID
	 * @param commentDate
	 * @param commentSource
	 * @param commentSrcArticle
	 * @param topic
	 */
	public Comment(int commentID, Date commentDate, String commentSource,
			String commentSrcArticle,String commentWeight ,Topic topic) {

		this.commentDate = commentDate;
		this.commentID = commentID;
		this.commentSource = commentSource;
		this.commentSrcArticle = commentSrcArticle;
		this.commentWeight = Double.valueOf(commentWeight);
		this.topic = topic;
	}

	/**
	 * Getter of the property <tt>commentDate</tt>
	 * 
	 * @return Returns the commentDate.
	 * 
	 */

	public Date getCommentDate() {
		return commentDate;
	}

	/**
	 * Setter of the property <tt>commentDate</tt>
	 * 
	 * @param commentDate
	 *            The commentDate to set.
	 * 
	 */
	public void setcommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}

	/**
	 * Getter of the property <tt>commentID</tt>
	 * 
	 * @return Returns the commentID.
	 * 
	 */

	public int getCommentID() {
		return commentID;
	}

	/**
	 * Setter of the property <tt>commentID</tt>
	 * 
	 * @param commentID
	 *            The commentID to set.
	 * 
	 */
	public void setCommentID(int commentID) {
		this.commentID = commentID;
	}

	/**
	 * Getter of the property <tt>commentSource</tt>
	 * 
	 * @return Returns the commentSource.
	 * 
	 */

	public String getCommentSource() {
		return commentSource;
	}

	/**
	 * Setter of the property <tt>commentSource</tt>
	 * 
	 * @param commentSource
	 *            The commentSource to set.
	 * 
	 */
	public void setCommentSource(String commentSource) {
		this.commentSource = commentSource;
	}

	/**
	 * Getter of the property <tt>commentSrcArticle</tt>
	 * 
	 * @return Returns the commentSrcArticle.
	 * 
	 */

	public String getCommentSrcArticle() {
		return commentSrcArticle;
	}

	/**
	 * Setter of the property <tt>commentSrcArticle</tt>
	 * 
	 * @param commentSrcArticle
	 *            The commentSrcArticle to set.
	 * 
	 */
	public void setCommentSrcArticle(String commentSrcArticle) {
		this.commentSrcArticle = commentSrcArticle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// SimpleDateFormat dateFormat = new
		// SimpleDateFormat(ConfigurationManager.tableDateFormat,
		// Locale.FRANCE);
		return "" + this.commentID + " " + this.commentWeight +" "+ this.topic;
	}

	/**
	 * @param topic
	 *            the topic to set
	 */
	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	/**
	 * @return the topic
	 */
	public Topic getTopic() {
		return topic;
	}

}
