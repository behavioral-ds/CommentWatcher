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

/**
 * This is an interface for Data Access Objects
 * For any persistent entity.
 * 
 * @author Nikolay Anokhin
 */
public interface Dao<E> {
	
	/**
	 * Insert an entity into database.
	 * 
	 * @param entity An entity to be stored.
	 */
    void persist(E entity);
    
    /**
     * Removes an entity from database.
     * 
     * @param entity An entity to be removed.
     */
    void remove(E entity);    
}
