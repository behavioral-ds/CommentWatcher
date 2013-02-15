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
package lab.eric.datafetcher.utils.lang;

/**
 * Utils that deal with languages and encodings.
 * 
 * @author Nikolay Anokhin
 */
public class LangUtils {
	
	/**
	 * Replaces all letters with accents by normal letters.
	 * Use carefully.
	 * 
	 * @param s String with accents.
	 * 
	 * @return String without accents in lower case.
	 */
	public static String replaceAccents(String s) {
		s = s.toLowerCase()
					   .replace('\u00FF', 'y')
					   .replace('\u00F9', 'u')
					   .replace('\u00FB', 'u')
					   .replace('\u00FC', 'u')
					   .replace('\u00F4', 'o')
					   .replace('\u0153', 'o')
					   .replace('\u00EF', 'i')
					   .replace('\u00EE', 'i')
					   .replace('\u00E9', 'e')
					   .replace('\u00E8', 'e')
					   .replace('\u00EA', 'e')
					   .replace('\u00EB', 'e')						   
					   .replace('\u00E7', 'c')
					   .replace('\u00E0', 'a')
					   .replace('\u00E2', 'a')
					   .replace('\u00E6', 'a');					   	 
	    return s;
	}
}
