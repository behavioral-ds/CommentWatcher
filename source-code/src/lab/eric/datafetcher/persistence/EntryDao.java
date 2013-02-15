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

import lab.eric.datafetcher.entities.Entry;

/**
 * This class works with database operations on {@link Entry} objects.
 * 
 * @author Nikolay Anokhin
 */
public class EntryDao<T extends Entry> extends HibernateDao<T> {
	
	public EntryDao(Session session) {
		super(session);
	}
	
	/**
	 * Saves an {@link Entry} instance to the database and updates dependencies if necessary. 
	 * 
	 * @param entity An instance of {@link Entry} to be saved.
	 * 
	 * @return An identifier of a saved entry.
	 */
	public Integer save(T entity) {
		return (Integer) session.save(entity);
	}
}
