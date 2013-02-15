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

import java.util.Vector;

/**
 * @author Samadjon Uroqov
 * 
 *         La classe pour tenir les objets Topic
 */
public class Topic {

	/*
	 * this.commentDate = commentDate; this.commentID = commentID;
	 * this.commentSource = commentSource; this.commentSrcArticle =
	 * commentSrcArticle;
	 */

	/*
	 * (non-javadoc)
	 */
	private int topicID;

	/*
	 * (non-javadoc)
	 */
	private Vector<Word> word;

	/*
	 * (non-javadoc)
	 */
	private Vector<Keyphrase> keyphrase; 

	/*
	 * (non-javadoc)
	 */
	private Vector<Comment> comment;
	
	/**
	 * @param topicID
	 * @param keyphrase
	 */
	public Topic(int topicID, Keyphrase[] keyphrase) {
		this.keyphrase = new Vector<Keyphrase>();
		this.word = new Vector<Word>();
		this.comment = new Vector<Comment>();
		
		this.topicID = topicID;
		this.setKeyphrase(keyphrase);
	}

	public Comment getCommentAt(int commentIndex) {
		return this.comment.elementAt(commentIndex);
	}

	public Word getWordAt(int wordIndex) {
		return this.word.elementAt(wordIndex);
	}

	public Keyphrase getKeyphraseAt(int keyphraseIndex) {
		return this.keyphrase.elementAt(keyphraseIndex);

	}

	public String toString() {
		if ( this.topicID > -1 )
			return "Topic" + this.topicID;
		return "All Topics";
	}

	/**
	 * Getter of the property <tt>article</tt>
	 * 
	 * @return Returns the article.
	 * 
	 */

	public Comment[] getComment() {
		Comment[] returnVal = new Comment[this.comment.size()];
		returnVal = this.comment.toArray(returnVal);
		return returnVal;
	}

	/**
	 * Setter of the property <tt>article</tt>
	 * 
	 * @param article
	 *            The article to set.
	 * 
	 */
	public void setComment(Comment[] comment) {
		this.comment.clear();
		this.addComment(comment);
	}
	
	public void addComment(Comment[] comment) {
		if ( comment == null )
			return;
		for ( int i=0; i < comment.length; i++)
			this.comment.addElement(comment[i]);
	}

	/**
	 * Getter of the property <tt>keyphrase</tt>
	 * 
	 * @return Returns the keyphrase.
	 * 
	 */

	public Keyphrase[] getKeyphrase() {
		Keyphrase[] returnVal = new Keyphrase[this.keyphrase.size()];
		returnVal = this.keyphrase.toArray(returnVal);
		return returnVal;
	}

	/**
	 * Setter of the property <tt>keyphrase</tt>
	 * 
	 * @param keyphrase
	 *            The keyphrase to set.
	 * 
	 */
	public void setKeyphrase(Keyphrase[] keyphrase) {
		this.keyphrase.clear();
		this.addKeyphrase(keyphrase);
	}
	
	public void addKeyphrase(Keyphrase[] keyphrase) {
		if ( keyphrase == null )
			return;
		
		for ( int i=0; i < keyphrase.length; i++)
			this.keyphrase.addElement(keyphrase[i]);
	}

	/**
	 * Getter of the property <tt>word</tt>
	 * 
	 * @return Returns the word.
	 * 
	 */

	public Word[] getWord() {
		Word[] returnVal = new Word[this.word.size()];
		returnVal = this.word.toArray(returnVal);
		return returnVal;
	}

	/**
	 * Setter of the property <tt>word</tt>
	 * 
	 * @param word
	 *            The word to set.
	 * 
	 */
	public void setWord(Word[] word) {
		this.word.clear();
		this.addWord(word);
	}
	
	/**
	 * Adds the words given as parameter to the inner collection
	 * 
	 * @param word
	 */
	public void addWord(Word[] word) {
		if ( word == null )
			return;
		for ( int i=0; i < word.length; i++)
			this.word.addElement(word[i]);
	}

	/**
	 * Getter of the property <tt>topicID</tt>
	 * 
	 * @return Returns the topicID.
	 * 
	 */

	public int getTopicID() {
		return topicID;
	}

	/**
	 * Setter of the property <tt>topicID</tt>
	 * 
	 * @param topicID
	 *            The topicID to set.
	 * 
	 */
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + topicID;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {		
		if(!(obj instanceof Topic))
			return false;
		Topic other = (Topic) obj;		
		return (topicID == other.topicID);
	}
}
