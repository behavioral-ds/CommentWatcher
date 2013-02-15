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

import lab.eric.datafetcher.entities.Comment;
import lab.eric.datafetcher.entities.Title;

/**
 * This class works with database operations on {@link Comment} objects.
 * 
 * @author Nikolay Anokhin, updated Marian-Andrei RIZOIU
 */
public class TitleDao extends HibernateDao<Title> {
	
	public TitleDao(Session session) {
		super(session);
	}
	
	/**
	 * @param id The id has to be {@code String}.
	 */
	protected Title getById(Object id) {
		if(!(id instanceof String)) {
			throw new IllegalArgumentException("The id of the Title has to be of String type.");			
		}
		
		return getByTitle((String) id);
	}
	
	/**
	 * Gets topic by title using current session. 
	 * 
	 * @param title Title of the topic to get.
	 * 
	 * @return An instance of {@link Title} with the specified title.
	 * 		   If such instance does not exist, returns {@code null}.
	 */
	private Title getByTitle(String title) {
		Object topic = session.get(Title.class, title);
		
		return topic != null ? (Title) topic : null;
	}
}
