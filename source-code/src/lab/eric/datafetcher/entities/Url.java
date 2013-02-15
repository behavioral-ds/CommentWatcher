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

/**
 * This class represents an URL on which the entry can be found.
 * This was introduced for the sake of compliance with the database structure.
 * 
 * @author Nikolay Anokhin
 */
public class Url {
	
	public Url() {		
	}
	
	public Url(String url) {
		this.url = url;
	}
	
	private String url;

	/**
	 * @param url Url {@code String}.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return Url {@code String}.
	 */
	public String getUrl() {
		return url;
	}
}
