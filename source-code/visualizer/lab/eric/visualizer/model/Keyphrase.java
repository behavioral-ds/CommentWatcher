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
/**
 * 
 */
package lab.eric.visualizer.model;

/**
 * @author Samadjon Uroqov
 * 
 *         La classe qui étend Word pour tenir les phrases-clées
 */
public class Keyphrase extends Word {

	/**
	 * Creates a new instance of Keyphrase, a child of Word class
	 * @param keyphraseText
	 * @param keyphraseScore
	 */
	public Keyphrase(String keyphraseText, float keyphraseScore) {
		super(keyphraseText, keyphraseScore);
	}

}
