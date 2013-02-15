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

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author Samadjon Uroqov
 * 
 *         Un seul objet de ce type est créé par fichier XML. Le parseur de XML
 *         sadresse à lui pour la répartition des commentaires. En outre il
 *         permet de contenir la liste de tous les objets Comment du fichier XML
 *         et ainsi évite de les calculer après
 */

public class CommentManager {

	private ArrayList<Comment> allComments;
	private Hashtable<String, Source> sourceHashtable;

	/**
	 * Un seul objet de ce type est créé par fichier XML. Le parseur de XML
	 * sadresse à lui pour la répartition des commentaires. En outre il permet
	 * de contenir la liste de tous les objets Comment du fichier XML et ainsi
	 * évite de les calculer après
	 */
	public CommentManager() {
		allComments = new ArrayList<Comment>();
		sourceHashtable = new Hashtable<String, Source>();
	}

	/**
	 * Adds a new comment to it's sourceHashtable(organized by sources) and to
	 * it's comment list(all comments)
	 * 
	 * @param Comment
	 *            comment
	 */
	public void addComment(Comment comment) {
		allComments.add(comment);

		String source = comment.getCommentSource();

		if (sourceHashtable.containsKey(source)) {
			sourceHashtable.get(source).addComment(comment);
		} else {
			Source newSource = new Source(comment);
			sourceHashtable.put(source, newSource);
		}
	}

	/**
	 * A simple method used in debugging to make sure that all hashtables are
	 * consistent
	 */

	public void debugIt() {
		System.out.println(allComments.size());
		try {
			sourceHashtable.get(sourceHashtable.keys().nextElement()).debugIt();
		} catch (Exception e) {
			ConfigurationManager.showErrorMessage(e, true);
		}
	}

	/**
	 * @return the sourceHashtable
	 */
	public Hashtable<String, Source> getSourceHashtable() {
		return sourceHashtable;
	}

	/**
	 * @param sourceHashtable
	 *            the sourceHashtable to set
	 */
	public void setSourceHashtable(Hashtable<String, Source> sourceHashtable) {
		this.sourceHashtable = sourceHashtable;
	}

}
