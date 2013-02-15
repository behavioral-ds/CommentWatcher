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
package lab.eric.datafetcher.web.controllers.async;

public enum FetchingProcessStatus {
	
	IDLE(0),
	PROGRESS(2),	
	SUCCESS(1),
	ERROR(3);
	
	private FetchingProcessStatus(int code){
		this.code = code;
	}
	
	private int code;
	
	private String message = null;
	
	public int getCode() {
		return code;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
