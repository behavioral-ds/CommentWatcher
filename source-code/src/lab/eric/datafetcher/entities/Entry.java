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

import java.util.ArrayList;
import java.util.List;

import lab.eric.datafetcher.utils.DateFormatter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * An abstraction above article and comment.
 * Contains common fields, namely:
 * - title <br/>
 * - body <br/>
 * - author <br/>
 * - updated date <br/>
 * - url <br/>
 * - parent id <br/>
 * - discussion
 * 
 * @author Nikolay Anokhin
 */
public abstract class Entry {
	
	@XStreamOmitField
	private Integer id;
	
	@XStreamAlias("topic")
	private Title title;

	private String body;
	
	private Author author;
	
	protected List<Comment> replies;
	
	
	@XStreamAsAttribute
	private String updated;
	
	// Fields that are not deserialized from XML.
		
	@XStreamOmitField
	private Url url;
	
	@XStreamOmitField
	private int parentId = -1;
	
	@XStreamOmitField
	private Discussion discussion;
	
	@SuppressWarnings("unused")
	@XStreamOmitField
	private String systemDate;
	
	/**
	 * Adds a new comment to the replies list.
	 * 
	 * @param reply {@link Comment} to add.
	 */
	public void addReply(Comment reply) {
		if (this.replies == null) {
			this.replies = new ArrayList<Comment>();
		}
		
		this.replies.add(reply);
	}	
	
	
	/**
	 * Set {@link Url} to the comment and all its replies.
	 */
	public void setUrlRecursive(Url url) {
		this.url = url;
		
		if(replies != null) {
			for (Comment reply : replies) {
				reply.setUrlRecursive(url);
			}
		}
	}
	
	/**
	 * Formats updated field to contain date in a unified format.
	 */
	public void formatUpdateDateRecursive() {
		this.updated  = DateFormatter.formatDate(this.updated);
		
		if(replies != null) {
			for (Comment reply : replies) {
				reply.formatUpdateDateRecursive();
			}
		}
	}
	
	/**
	 * Set {@link Discussion} to the comment and all its replies.
	 */
	public void setDiscussionRecursive(Discussion discussion) {
		this.discussion = discussion;
		
		if(replies != null) {
			for (Comment reply : replies) {
				reply.setDiscussionRecursive(discussion);
			}
		}
	}

	/**
	 * @return {@link Title} instance associated with this entry.
	 */
	public Title getTitle() {
		return title;
	}
	
	/**
	 * @param title {@link Title} instance associated with this entry.
	 */
	public void setTitle(Title title) {
		this.title = title;
	}
	
	/**
	 * @return Body of an entry as {@code String}.
	 */
	public String getBody() {
		return body;
	}
	
	/**
	 * @param body Body of an entry as {@code String}.
	 */
	public void setBody(String body) {
		this.body = body;
	}	
	
	/**
	 * @return {@link Author} instance associated with this entry.
	 */
	public Author getAuthor() {
		return author;
	}
	
	/**
	 * @param author {@link Author} instance associated with this entry.
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}	
	
	/**
	 * @return {@code String} update date representation.
	 */
	public String getUpdated() {
		return updated;
	}
	
	/**
	 * @param updated {@code String} update date representation.
	 */
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	
	/**
	 * @return {@link Url} instance associated with this entry.
	 */
	public Url getUrl() {
		return url;
	}
	
	/**
	 * @param url {@link Url} instance associated with this entry.
	 */
	public void setUrl(Url url) {
		this.url = url;
	}

	/**
	 * @return An identifier of the current Entry. This property is auto-generated.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param parentId An identifier of the parent. <br/> -1 if none.
	 */
	protected void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return An identifier of the parent. <br/> -1 if none.
	 */
	public int getParentId() {
		return parentId;
	}	

	/**
	 * @return {@link Discussion} instance that includes this entry.
	 */
	public Discussion getDiscussion() {
		return discussion;
	}
	
	/**
	 * @return A {@code List} of {@link Comment} instances as replies.
	 */
	public List<Comment> getReplies() {
		return replies;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return title.getTitle() + body;
	}

	/**
	 * @param replies @return A {@code List} of {@link Comment} instances as replies.
	 */
	public void setReplies(List<Comment> replies) {
		this.replies = replies;
	}
}
