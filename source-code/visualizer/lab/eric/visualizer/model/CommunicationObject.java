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
package lab.eric.visualizer.model;
import java.io.Serializable;

/**
 * The instances of this class will serve for the communication between the servlet and the applet
 * 
 * @author Marian-Andrei RIZOIU
 *
 */
public class CommunicationObject implements Serializable{
	
	private static final long serialVersionUID = 7566477016357211922L;
	public static final int REQUEST_XML_FILE = 0;
	public static final int REPLY_OK_XML_FILE = 1;
	public static final int REPLY_FAILED_XML_FILE = 2;
	/**
	 * The type of communication
	 */
	private int communicationType;
	private Object attachment;
	
	public CommunicationObject(int type, Object attach) {
		this.communicationType = type;
		this.attachment = attach;
	}
	
	public CommunicationObject(int type) {
		this.communicationType = type;
		this.attachment = null;
	}
	
	/**
	 * Verifies if the current CommunicationObject instance is a valid one.
	 * 
	 * @return
	 */
	public boolean isValid() {
		// a request
		if (this.communicationType == REQUEST_XML_FILE && this.attachment == null )
			return true;
		
		// an OK response. Must contain the attachment 
		if (this.communicationType == REPLY_OK_XML_FILE && this.attachment != null )
			return true;
		
		// an failed response. Can contain the exception. 
		if (this.communicationType == REPLY_FAILED_XML_FILE )
			return true;
		
		return false;
	}

	public int getCommunicationType() {
		return communicationType;
	}

	public void setCommunicationType(int communicationType) {
		this.communicationType = communicationType;
	}

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

}
