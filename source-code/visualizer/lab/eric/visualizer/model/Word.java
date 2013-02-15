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
 *         La classe pour tenir les objets Word
 */
public class Word {

	/*
	 * (non-javadoc)
	 */
	private String wordText;

	/*
	 * (non-javadoc)
	 */
	private float wordScore;

	public Word(String wordText, float wordScore) {
		this.wordText = wordText;
		this.wordScore = wordScore;
	}

	public String toString() {

		return this.wordText;

	}

	/**
	 * Getter of the property <tt>wordText</tt>
	 * 
	 * @return Returns the wordText.
	 * 
	 */

	public String getWordText() {
		return wordText;
	}

	/**
	 * Setter of the property <tt>wordText</tt>
	 * 
	 * @param wordText
	 *            The wordText to set.
	 * 
	 */
	public void setWordText(String wordText) {
		this.wordText = wordText;
	}

	/**
	 * Getter of the property <tt>wordScore</tt>
	 * 
	 * @return Returns the wordScore.
	 * 
	 */

	public float getWordScore() {
		return wordScore;
	}

	/**
	 * Setter of the property <tt>wordScore</tt>
	 * 
	 * @param wordScore
	 *            The wordScore to set.
	 * 
	 */
	public void setWordScore(float wordScore) {
		this.wordScore = wordScore;
	}

}
