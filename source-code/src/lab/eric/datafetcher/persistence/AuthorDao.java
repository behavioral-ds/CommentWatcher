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

import lab.eric.datafetcher.entities.Author;

/**
 * This class works with database operations on {@link Author} objects.
 * 
 * @author Nikolay Anokhin
 */
public class AuthorDao extends HibernateDao<Author> {
	
	public AuthorDao(Session session) {
		super(session);
	}
	
	/**
	 * @param id The id has to be {@code String}.
	 */
	protected Author getById(Object id) {
		if(!(id instanceof String)) {
			throw new IllegalArgumentException("The id of the Author has to be of String type.");			
		}
		
		return getByName((String) id);
	}
	
	/**
	 * Gets author by name using current session. 
	 * 
	 * @param name Name of the author to get.
	 * 
	 * @return An instance of {@link Author} with the specified name.
	 * 		   If such instance does not exist, returns {@code null}.
	 */
	private Author getByName(String name) {
		Object author = session.get(Author.class, name);
		
		return author != null ? (Author) author : null;
	}
}
