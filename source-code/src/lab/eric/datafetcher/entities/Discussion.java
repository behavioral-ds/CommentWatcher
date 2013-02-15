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
package lab.eric.datafetcher.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * The container class that include an article and set of firstLevelComments.
 * Also contains link to the next page of discussion (if exists).
 * 
 * @author Nikolay Anokhin
 */
@XStreamAlias("discussion")
public class Discussion implements Serializable{
	
	/**
	 * Generated serial version Id.
	 */
	private static final long serialVersionUID = -1931698893534420777L;

	public Discussion() {		
	}	
	
	@XStreamOmitField
	private Integer id;
	
	@XStreamOmitField
	private String name;
	
	@XStreamOmitField
	private String source;
	
	@XStreamOmitField
	private String url;

	@XStreamOmitField
	private int lang;	
	
	@XStreamOmitField
	private String type;
	
	@XStreamOmitField
	private String theme;
	
	@XStreamOmitField
	private int nb;
	
	@XStreamOmitField
	private Date created;
	
	@XStreamOmitField
	private Date fetched;
	
	private Article article;
	
	private String next;
	
	@XStreamAlias("comments")	
	private List<Comment> firstLevelComments;
	
	@XStreamOmitField
	private List<Comment> allComments;
	
	/**
	 * Adds a comment to the firstLevelComments list.
	 * 
	 * @param comment {@link Comment} instance to add. 
	 */
	public void addFirstLevelComment(Comment comment) {
		if (this.firstLevelComments == null) {
			firstLevelComments = new ArrayList<Comment>();
		}
		
		this.firstLevelComments.add(comment);
	}	
	
	/**
	 * Gets last comment of a specified level.
	 * 
	 * @param level A level of comment.
	 * 
	 * @return {@link Comment} instance.
	 */
	public Comment getLastComment(int level) {
		if (this.firstLevelComments == null || this.firstLevelComments.isEmpty()) {
			throw new IllegalArgumentException("There is no comment with this level as firstLevelComments list is empty");
		}
		
		return this.firstLevelComments.get(this.firstLevelComments.size()-1).getLastReply(level);
	}

	/**
	 * @return {@link Article} instance that is discussed.
	 */
	public Article getArticle() {
		return article;
	}
	
	public void setArticle(Article article) {
		this.article = article;
	}

	/**
	 * REMARK: This field is not database-mapped. To get something 
	 * 		   you would have to fill it in by hand first 
	 * 		   (unless it's a result of parsing).
	 * 
	 * @return {@code List} of first-level firstLevelComments.
	 */
	public List<Comment> getFirstLevelComments() {
		return firstLevelComments;
	}
	
	public void setFirstLevelComments(List<Comment> comments) {
		this.firstLevelComments = comments;
	}

	/**
	 * @return An URL of a next page of the discussion. {@code Null} if it does not exist.
	 */
	public String getNext() {
		return next;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setLang(int lang) {
		this.lang = lang;
	}

	public int getLang() {
		return lang;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getTheme() {
		return theme;
	}

	public void setNb(int nb) {
		this.nb = nb;
	}

	public int getNb() {
		return nb;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
	
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		
		if (!(other instanceof Discussion)) {
			return false;
		}
		
		Discussion o = (Discussion) other;
		return this.getId() == o.getId();
	}
	
	public int hashCode() {
		return getId().hashCode();
	}

	public Integer getId() {
		return id;
	}

	public void setAllComments(List<Comment> allComments) {
		this.allComments = allComments;
	}

	public List<Comment> getAllComments() {
		return allComments;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public void setFetched(Date fetched) {
		this.fetched = fetched;
	}

	public Date getFetched() {
		return fetched;
	}
}
