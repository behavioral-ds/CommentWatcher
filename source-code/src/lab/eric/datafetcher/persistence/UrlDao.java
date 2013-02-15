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

import lab.eric.datafetcher.entities.Url;

/**
 * This class works with database operations on {@link Url} objects.
 * 
 * @author Nikolay Anokhin
 */
public class UrlDao extends HibernateDao<Url> {

	public UrlDao(Session session) {
		super(session);
	}
	
	/**
	 * @param id The id has to be {@code String}.
	 */
	protected Url getById(Object id) {
		if(!(id instanceof String)) {
			throw new IllegalArgumentException("The id of the Url has to be of String type.");			
		}
		
		return getByUrl((String) id);
	}
	
	/**
	 * Gets url by url using current session. 
	 * 
	 * @param url Url of the url to get.
	 * 
	 * @return An instance of {@link Url} with the specified url.
	 * 		   If such instance does not exist, returns {@code null}.
	 */
	private Url getByUrl(String url) {
		Object ourl = session.get(Url.class, url);
		
		return ourl != null ? (Url) ourl : null;
	}
}
