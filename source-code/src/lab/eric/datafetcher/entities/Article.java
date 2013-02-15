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
 * This class represents an article that is under the discussion.
 * 
 * @author Nikolay Anokhin
 */
public class Article extends Entry {
	
	public Article() {		
	}	
	
	public Article(Title title, String body) {
		setTitle(title);
		setBody(body);
	}
	
	private String subtitle;	

	/**
	 * @return Subtitle of an article as string.
	 */
	public String getSubtitle() {
		return subtitle;
	}
	
	/**
	 * @param subtitle Subtitle of an article as string.
	 */
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}	
}
