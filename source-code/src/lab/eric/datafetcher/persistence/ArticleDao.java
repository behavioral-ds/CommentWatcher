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

import org.hibernate.Session;

import lab.eric.datafetcher.entities.Article;
import lab.eric.datafetcher.entities.Title;

/**
 * This class works with database operations on {@link Article} objects.
 * 
 * @author Nikolay Anokhin
 */
public class ArticleDao extends EntryDao<Article> {

	public ArticleDao(Session session) {
		super(session);
	}
	
	/**
	 * Saves an {@link Article} instance to the database and updates Author, Url and Title. 
	 * 
	 * @param entity An instance of {@link Article} to be saved.
	 * 
	 * @return An identifier of a saved entry.
	 */
	public Integer save(Article entity) {
		session.saveOrUpdate(entity.getAuthor());
		session.saveOrUpdate(entity.getTitle());
		session.saveOrUpdate(entity.getUrl());
		return super.save(entity);
	}
	
	/**
	 * @param id The id has to be {@code String}.
	 */
	protected Article getById(Object id) {
		if(!(id instanceof String)) {
			throw new IllegalArgumentException("The id of the Title has to be of String type.");			
		}
		
		return getByArticle((String) id);
	}
	
	/**
	 * Gets topic by title using current session. 
	 * 
	 * @param title Title of the topic to get.
	 * 
	 * @return An instance of {@link Title} with the specified title.
	 * 		   If such instance does not exist, returns {@code null}.
	 */
	private Article getByArticle(String title) {
		Object article = session.get(Article.class, title);
		
		return article != null ? (Article) article : null;
	}
	
}
