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
package lab.eric.datafetcher.persistence;

import java.util.HashMap;
import java.util.List;

import lab.eric.datafetcher.entities.Author;
import lab.eric.datafetcher.entities.Citation;
import lab.eric.datafetcher.entities.Comment;
import lab.eric.datafetcher.entities.Title;
import lab.eric.datafetcher.entities.Url;

import org.hibernate.Session;

/**
 * This class works with database operations on {@link Comment} objects.
 * 
 * @author Nikolay Anokhin
 */
public class CommentDao extends EntryDao<Comment> {
	
	private HashMap<String, Title> topicCache = null;
	private HashMap<String, Author> authorCache = null;
	private HashMap<String, Url> urlCache = null;	
	
	public CommentDao(Session session) {
		super(session);
	}
	
	/**
	 * Saves list of comments into the database and sets provided parent id.
	 * 
	 * @param comments {@code List} of Comments.
	 * 
	 * @param parentId An id of a parent.
	 */
	public void persistWithReplies(List<Comment> comments, int parentId, Author targetAuthor) {
		topicCache = new HashMap<String, Title>();
		authorCache = new HashMap<String, Author>();
		urlCache = new HashMap<String, Url>();
		
		for (Comment comment : comments) {
			comment.setParentId(parentId);
			persistWithReplies(comment,targetAuthor,true);
		}
		
		topicCache = null;
		authorCache = null;
		urlCache = null;
	}
	
	/**
	 * Saves comment and all its replies recursively into the database. 
	 * Use this if you want to use an external session.
	 * Transaction has to be created and committed outside.
	 * Important: uses static caches.
	 * 
	 * @param comment {@link Comment} instance to be persisted.
	 */
	private void persistWithReplies(Comment comment, Author targetAuthor
			,boolean firstLevel) {
		// Set an existing author instance with the same name if it has been already loaded.
		comment.setAuthor(new AuthorDao(session).
				getCachedAndSaved(comment.getAuthor().getName(), comment.getAuthor(), authorCache));
		// Set an existing topic instance with the same name if it has been already loaded.
		comment.setTitle(new TitleDao(session).
				getCachedAndSaved(comment.getTitle().getTitle(), comment.getTitle(), topicCache));
		// Set an existing url instance with the same name if it has been already loaded.
		comment.setUrl(new UrlDao(session).
				getCachedAndSaved(comment.getUrl().getUrl(), comment.getUrl(), urlCache));		
		
		Integer commentId = save(comment);	
		
		if (!firstLevel) {
			
		Citation citation = new Citation();
		
		citation.setAuthorSource(comment.getAuthor());
		
		citation.setAuthorTarget(targetAuthor);
			
		new AuthorDao(session).
				getCachedAndSaved(citation.getAuthorSource().getName(), citation.getAuthorSource(), authorCache);
	  
		new AuthorDao(session).
				getCachedAndSaved(citation.getAuthorTarget().getName(), citation.getAuthorTarget(), authorCache);
		
	  
		citation.setMesgTargetId(comment.getParentId());
		
		citation.setMesgSourceId(commentId);
		
		citation.setCitationType("0");
			
		session.save(citation);
		}
		
		if (comment.getReplies() != null) {
			for (Comment reply : comment.getReplies()) {
				reply.setParentId(commentId);
				persistWithReplies(reply,comment.getAuthor(),false);
			}
		}
	}
}
