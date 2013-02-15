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

import lab.eric.datafetcher.utils.lang.LangUtils;

import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;

/**
 * An implementation of {@link Dao} interface 
 * that uses Hibernate to perform database operations.
 * 
 * @author Nikolay Anokhin
 *
 * @param <E> Class of an entity.
 */
public abstract class HibernateDao<E> implements Dao<E> {	
	
	protected Session session = null;
		
	/**
	 * Creates an new instance.
	 * 
	 * @param session Session to use with database operations.
	 */
	public HibernateDao(Session session) {
		if (!session.isOpen()) {
			throw new IllegalArgumentException("Session has to be open.");
		}
		this.session = session;
	}	

	@Override
	public void persist(E entity) {		
		session.saveOrUpdate(entity);
	}

	@Override
	public void remove(E entity) {
		session.delete(entity);
	}

	/**
	 * Gets an entity by the identifier using provided session.
	 * 
	 * @param id An identifier of the object.
	 * 
	 * @return An object with specified Id or {@code null} if it is not in the database.
	 */
	protected E getById(Object id) {
		throw new NotImplementedException("This method is not implemented for the current Dao.");
	}
	
	/**
	 * Checks id in cache, if entity is cached, returns cached one.
	 * Otherwise, caches and saves the provided one and returns.
	 * 
	 * @param id An id to be checked in cache.
	 * 
	 * @param entity A default entity that is saved if it is not cached before.
	 * 
	 * @param cache {@link HashMap} cache.
	 * 
	 * @return An entity that is cached and saved.
	 */	
	protected E getCachedAndSaved(String id, E entity, HashMap<String, E> cache) {		
		E cachedEntity = getUsingCache(id, cache);
		if (cachedEntity != null) {					
			return cachedEntity;
		}
		
		saveAndCache(id, entity, cache);
		return entity;
	}
	
	/**
	 * Gets an entity with the specified string id.
	 * First looks for the entity in the provided {@code HashMap} cache, and returns it if found.
	 * Then looks up in the database using provided Dao. If finds, puts it into the cache and returns.
	 * Otherwise returns {@code null}.
	 * 
	 * @param <T> The type of an entity to retrieve.
	 * 
	 * @param id An id that is used to retrieve the entity.
	 * 
	 * @param cache A {@code HashMap} containing cached entitied.
	 * 
	 * @return An instance of type T or {@code null} if not found.
	 */
	private E getUsingCache(String id, HashMap<String, E> cache) {
		// Key has to be in lower case!
		String key = LangUtils.replaceAccents(id);
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		
		E entity = this.getById(id);
		if (entity != null) {
			cache.put(key, entity);			
		}
		return entity;
	}
	
	/**
	 * Puts an entity into the cache and saves it into the database.
	 * 
	 * @param id An id of the entity to put.
	 * 
	 * @param value An entity to put.
	 * 
	 * @param cache {@link HashMap} cache.
	 */
	private void saveAndCache(String id, E value, HashMap<String, E> cache) {
		// Key has to be in lower case and without accents!
		String key = LangUtils.replaceAccents(id);
		cache.put(key, value);
		session.saveOrUpdate(value);
	}		
}
