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

/**
 * @author Samadjon Uroqov
 * 
 *         La classe qui gère la liste de tous les objets Topic
 */
public class TopicsContainer {

	/*
	 * (non-javadoc)
	 */
	private ArrayList<Topic> topicList;

	/*
	 * (non-javadoc)
	 */
	private int minCapacity;

	/**
	 * @param minCapacity
	 */
	public TopicsContainer(int minCapacity) {
		topicList = new ArrayList<Topic>();
		this.minCapacity = minCapacity;
	}

	/**
	 * @return size of topicList
	 */
	public int getSize() {

		return topicList.size();

	}

	/**
	 * @param topicIndex
	 * @return topic at the index=topicIndex 
	 */
	public Topic getTopicAt(int topicIndex) {

		return this.topicList.get(topicIndex);

	}

	public void addTopic(Topic topic) {
		topicList.add(topic);

	}

	/**
	 * Getter of the property <tt>topicList</tt>
	 * 
	 * @return Returns the topicList.
	 * 
	 */

	public ArrayList<Topic> getTopicList() {
		return topicList;
	}

	/**
	 * Setter of the property <tt>topicList</tt>
	 * 
	 * @param topicList
	 *            The topicList to set.
	 * 
	 */
	public void setTopicList(ArrayList<Topic> topicList) {
		this.topicList = topicList;
	}

	/**
	 * Getter of the property <tt>minCapacity</tt>
	 * 
	 * @return Returns the minCapacity.
	 * 
	 */

	public int getMinCapacity() {
		return minCapacity;
	}

	/**
	 * Setter of the property <tt>minCapacity</tt>
	 * 
	 * @param minCapacity
	 *            The minCapacity to set.
	 * 
	 */
	public void setMinCapacity(int minCapacity) {
		this.minCapacity = minCapacity;
	}

}
