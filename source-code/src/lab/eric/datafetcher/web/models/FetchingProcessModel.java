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

import java.util.Date;

/**
 * Serves as a model for the (intermediate)
 * results of discussion fetching process.
 * Supports conversion to JSON for async transmission
 * 
 * @author Nikolay Anokhin
 */
public class FetchingProcessModel {	
	
	private int status;
	
	private Integer discId = -1;
	
	private String message;
	
	private String articleTitle = null;
	
	private int commentsFetched = -1;
	
	private Date fetchDate = null;
	
	/**
	 * Serializes current state into JSON format
	 * that can be used for async transmission.
	 */
	public String toJson() {
		StringBuilder builder = new StringBuilder();
				
		builder.append("{");
		
		builder.append("\"discId\" :");
		builder.append(discId);
		builder.append(", ");
		
		builder.append("\"status\" :");
		builder.append(status);
		builder.append(", ");
		
		builder.append("\"atitle\" :\"");
		if (articleTitle != null) {
			builder.append(articleTitle);
		}
		builder.append("\", ");
		
		builder.append("\"comments\" :");
		builder.append(commentsFetched);
		builder.append(", ");
		
		builder.append("\"date\" :\"");
		if (fetchDate != null) {
			builder.append(fetchDate);
		} else {
			builder.append("not fetched");
		}
		builder.append("\", ");
		
		builder.append("\"message\" :\"");
		builder.append(message);
		builder.append("\"");
		
		builder.append("}");		
		
		return builder.toString();
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setCommentsFetched(int commentsFetched) {
		this.commentsFetched = commentsFetched;
	}

	public int getCommentsFetched() {
		return commentsFetched;
	}

	public void setFetchDate(Date fetchedDate) {
		this.fetchDate = fetchedDate;
	}

	public Date getFetchDate() {
		return fetchDate;
	}

	public void setDiscId(Integer discId) {
		this.discId = discId;
	}

	public Integer getDiscId() {
		return discId;
	}
}
