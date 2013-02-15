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
 * This class represents an author of an article.
 * @author Gustav
 *
 */
public class Author {
	
	public Author() {		
	}
	
	public Author (String name) {
		this.name = name;
	}
	
	@XStreamAsAttribute
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
