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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * This class represents a single comment for an entry.
 */
@XStreamAlias("comment")
public class Comment extends Entry {
	
	public Comment() {		
	}
	
	public Comment(Author author, String body) {
		setAuthor(author);
		setBody(body);
	}
	
	/**
	 * @return Gets the last reply of the specified level, recursively.
	 * 
	 * @param Level of requested reply.
	 */
	public Comment getLastReply(int level) {
		if (this.level > level) {
			throw new IllegalArgumentException("Desired level is lower than the one of current comment.");
		}
		
		if (this.level == level) {
			return this;
		}
		
		if (replies == null || replies.isEmpty()) {
			throw new IllegalArgumentException("There is no last comment of the requested level.");
		}
		
		Comment lastComment = replies.get(replies.size() - 1);		
		if (lastComment.getLevel() == level) {
			return lastComment;
		}
		
		return lastComment.getLastReply(level);
	}
	
	@XStreamAsAttribute
	private int level;	
	
	/**
	 * @param level Level of a comment (root is 1).
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return Level of a comment (root is 1).
	 */
	public int getLevel() {
		return level;
	}	
	
	public void setParentId(int parentId) {
		super.setParentId(parentId);
	}
}
