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
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class Citation {

	@XStreamOmitField
	private Integer id;
	
	@XStreamAsAttribute
	private Author authorSource;
	
	@XStreamAsAttribute
	private Author authorTarget;
	
	@XStreamAsAttribute
	private int mesgSourceId;
	
	@XStreamAsAttribute
	private int mesgTargetId;
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	private String citationType;
	

	public Author getAuthorSource() {
		return authorSource;
	}

	public void setAuthorSource(Author authorSource) {
		this.authorSource = authorSource;
	}

	public Author getAuthorTarget() {
		return authorTarget;
	}

	public void setAuthorTarget(Author authorTarget) {
		this.authorTarget = authorTarget;
	}

	public int getMesgSourceId() {
		return mesgSourceId;
	}

	public void setMesgSourceId(int mesg) {
		this.mesgSourceId = mesg;
	}

	public String getCitationType() {
		return citationType;
	}

	public void setCitationType(String citationType) {
		this.citationType = citationType;
	}

	public void setMesgTargetId(int mesgTarget) {
		this.mesgTargetId = mesgTarget;
	}

	public int getMesgTargetId() {
		return mesgTargetId;
	}

}
