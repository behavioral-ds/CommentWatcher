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

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * This the title of a current entry.
 * This class was introduced for the sake of compliance with database structure.
 * 
 * MAR: this is the title of an entry (actually it should belong only to articles)
 * 
 * @author Nikolay Anokhin, updated Marian-Andrei RIZOIU
 */
public class Title {
	
	@XStreamAsAttribute	
	private String title;

	/**
	 * @param title Title of the topic.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return title Title of the topic.
	 */
	public String getTitle() {
		return title;
	}
	
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		
		if (!(other instanceof Title)) {
			return false;
		}
		
		Title otherTitle = (Title) other;
		return this.title.equals(otherTitle.getTitle());
	}
	
	public int hashCode() {
		return this.title == null ? 0 : this.title.hashCode();
	}	
}
