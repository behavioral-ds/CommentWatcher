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
package lab.eric.datafetcher.parsers;

/**
 * Throw this when discussion parsing fails.
 * 
 * @author Nikolay Anokhin
 */
public class DiscussionParsingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3981339339618706888L;	
	
	public DiscussionParsingException(String message) {
		super(message);
	}
	
	public DiscussionParsingException(String message, Exception cause) {
		super(message, cause);
	}
}
