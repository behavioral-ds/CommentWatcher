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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import lab.eric.datafetcher.entities.Article;
import lab.eric.datafetcher.entities.Author;
import lab.eric.datafetcher.entities.Comment;
import lab.eric.datafetcher.entities.Discussion;
import lab.eric.datafetcher.entities.Entry;
import lab.eric.datafetcher.entities.Title;
import lab.eric.datafetcher.entities.Url;

import com.thoughtworks.xstream.XStream;

/**
 * Deserializes the discussion from the predefined XML format.
 * 
 * @author Nikolay Anokhin
 *
 */
public class DiscussionDeserializer {
	
	private XStream xstream;
	
	public DiscussionDeserializer() {
		xstream = new XStream();
		xstream.processAnnotations(Entry.class);
		xstream.processAnnotations(Comment.class);		
		xstream.processAnnotations(Article.class);
		xstream.processAnnotations(Discussion.class);
		xstream.processAnnotations(Title.class);
		xstream.processAnnotations(Author.class);
		xstream.processAnnotations(Url.class);
	}
	
	/**
	 * Deserializes the discussion from a {@code byte} array
	 * that contains predefined XML discussion format.
	 * 
	 * @param discussionXml A {@code byte} array that contains discussion XML.
	 * 
	 * @return {@link Discussion} instance.
	 * @throws IOException 
	 */
	public Discussion deserialize(byte[] discussionXml) throws IOException {
		Discussion discussion = null;
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(discussionXml);
		
		discussion = (Discussion) xstream.fromXML(inputStream);
		
		inputStream.close();
		
		return discussion;
	}	
}
