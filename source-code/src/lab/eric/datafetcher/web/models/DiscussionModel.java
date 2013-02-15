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
package lab.eric.datafetcher.web.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lab.eric.datafetcher.web.controllers.DiscussionListServlet;

/**
 * Model class used in {@link DiscussionListServlet}, {@link ClassificationModel}
 * 
 */
public class DiscussionModel implements Comparable<DiscussionModel>{
	
	public DiscussionModel() {		
	}
	
	public DiscussionModel(int id, String name, String source, String theme, String url) {
		this.id = id;
		this.name = name;
		this.source = source;
		this.theme = theme;
		this.url = url;
	}
	
	private int id = -1;
	
	private String name = "";
	
	private String source = "";
	
	private String theme = "";
	
	private String url = "";
	
	private String type = "";	
	
	private int lang;
	
	private Date fetchDate;
	
	private Date articlePostedDate;
	
	private int commentsFetched;
	
	private String articleTitle;
	
	private List<String> errors = new ArrayList<String>();
	
	/**
	 * Status of an operation with performed with model.
	 * Example: "discussion updated successfully". 
	 */
	private Status status;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getTheme() {
		return theme;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void addError(String error) {
		this.errors.add(error);		
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setLang(int lang) {
		this.lang = lang;
	}

	public int getLang() {
		return lang;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Status of an update/create operation.
	 */
	public enum Status {
		DELETE_SUCCESS ("Discussion was successfully deleted"),
		UPDATE_SUCCESS ("Discussion was successfully updated"),
		CREATE_SUCCESS ("Discussion was successfully created");
		
		private String message;
		Status (String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
	}

	@Override
	public int compareTo(DiscussionModel other) {
		return this.getSource().compareTo(other.getSource());
	}

	public void setFetchDate(Date fetchDate) {
		this.fetchDate = fetchDate;
	}

	public Date getFetchDate() {
		return fetchDate;
	}

	public void setCommentsFetched(int commentsFetched) {
		this.commentsFetched = commentsFetched;
	}

	public int getCommentsFetched() {
		return commentsFetched;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticlePostedDate(Date articlePostedDate) {
		this.articlePostedDate = articlePostedDate;
	}

	public Date getArticlePostedDate() {
		return articlePostedDate;
	}	
}
