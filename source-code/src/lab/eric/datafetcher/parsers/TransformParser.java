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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import lab.eric.datafetcher.entities.Article;
import lab.eric.datafetcher.entities.Citation;
import lab.eric.datafetcher.entities.Comment;
import lab.eric.datafetcher.entities.Discussion;
import lab.eric.datafetcher.entities.Url;

import org.apache.log4j.Logger;

/**
 * This class performs parsing of a discussion web pages.
 * The underlying mechanism is to use XSL transformation
 * in order to transform HTML into a predefined XML format
 * and then deserialize this XML into entity classes such as
 * {@link Discussion}, {@link Article} and {@link Comment}.
 * 
 * @author Nikolay Anokhin, updated Marian-Andrei RIZOIU
 */
public class TransformParser extends Observable {

	public static int SUCCESS_RESULT_CODE = -1;
	public static int ERROR_RESULT_CODE = -1000;

	private static Logger logger = Logger.getLogger(TransformParser.class);

	private HtmlCleaner htmlCleaner;

	private XslTransformer xslTransformer;

	private Discussion discussion = null;

	private DiscussionDeserializer discussionDeserializer;

	private int currentPageNumber = 0;

	/**
	 * Creates an instance of parser.
	 * 
	 * @param discussion {@link Discussion} that contains parsing parameters.
	 *	 
	 */
	public TransformParser(Discussion discussion) {
		this.discussion = discussion;

		this.htmlCleaner = new HtmlCleaner();
		this.xslTransformer = new XslTransformer(extractTransformName(this.discussion.getUrl()));
		this.discussionDeserializer = new DiscussionDeserializer();		
	}

	/**
	 * Does the same as parse, but does not throw {@link DiscussionParsingException}.
	 * And sets current page number to error state.
	 * 
	 * @param commentsUrl An URL of a first page of the discussion that contains
	 * 			comments. Set it {@code null} if you are sure that url can be retrieved from
	 * 			the discussion pare using the transformation.
	 */
	public void parseSilent(String commentsUrl) {
		try {
			parse(commentsUrl);
			setCurrentPageNumber(SUCCESS_RESULT_CODE);
		} catch (DiscussionParsingException ex) {
			logger.error("An error occured parsing the discussion.", ex);
			setCurrentPageNumber(ERROR_RESULT_CODE);
		}		
	}

	/**
	 * Parses a discussion from the page starting with specified URL.
	 * The new {@link Discussion} instance is created in this method.
	 * 
	 * @param commentsUrl An URL of a first page of the discussion that contains
	 * 			comments. Set it {@code null} if you are sure that url can be retrieved from
	 * 			the discussion page using the transformation.
	 * 
	 * @throws DiscussionParsingException
	 */
	private void parse(String commentsUrl) throws DiscussionParsingException {
		String url = this.discussion.getUrl();
		logger.debug("Starting to parse the discussion. Webpage: " + url);
		Discussion first = processWebpage(url);

		if (first == null) {
			throw new DiscussionParsingException("Deserialization of a discussion failed.");
		}

		Url currentUrl = new Url(url);

		this.discussion.setArticle(first.getArticle());
		if (this.discussion.getArticle() != null) {
			this.discussion.getArticle().setDiscussionRecursive(this.discussion);
			this.discussion.getArticle().setUrl(currentUrl);
			this.discussion.getArticle().formatUpdateDateRecursive();
		}

		this.discussion.setFirstLevelComments(first.getFirstLevelComments());

		for (Comment comment: discussion.getFirstLevelComments()) {
			comment.setUrlRecursive(currentUrl);
			comment.setDiscussionRecursive(this.discussion);
			comment.formatUpdateDateRecursive();
			
		}
		
		// Comments url can be optionally provided if the comments page url
		// can not be evaluated in the discussion.
		if(commentsUrl != null) {
			mergeDiscussionTail(commentsUrl);
		} else if(first.getNext() != null && !first.getNext().isEmpty()) {
			mergeDiscussionTail(first.getNext());
		}
		this.discussion.setFetched(new Date());
		logger.debug("Discussion parsing completed.");
	}

	/**
	 * Merges comments tail to a discussion processed by the parser.
	 * 
	 * @param url An URL of the page of discussion that contains the comments tail.
	 * 
	 * @throws DiscussionParsingException
	 */
	private void mergeDiscussionTail(String url) throws DiscussionParsingException {
		logger.debug("Merging tail to the discussion. Webpage: " + url);
		Discussion tail = processWebpage(url);

		if (tail == null) {
			throw new DiscussionParsingException("Deserialization of a discussion failed.");
		}

		Url currentUrl = new Url(url);

		// in rue89, sometimes the article is on the second page
		if ( this.discussion.getArticle() == null && tail.getArticle() != null) {
			this.discussion.setArticle(tail.getArticle());
			this.discussion.getArticle().setDiscussionRecursive(this.discussion);
			this.discussion.getArticle().setUrl(currentUrl);
			this.discussion.getArticle().formatUpdateDateRecursive();
		}

		for (Comment comment : tail.getFirstLevelComments()) {
			comment.setUrlRecursive(currentUrl);
			comment.setDiscussionRecursive(this.discussion);
			comment.formatUpdateDateRecursive();

			if (comment.getLevel() == 1) {
				discussion.addFirstLevelComment(comment);
				
				
				continue;
			}

			// Add comment of a non-first level.			
			Comment lastComment = discussion.getLastComment(comment.getLevel() - 1);
			lastComment.addReply(comment);
			
			
			
		}

		if (tail.getNext() != null && !tail.getNext().isEmpty()) {
			mergeDiscussionTail(tail.getNext());
		}
	}

	/**
	 * Processes a webpage from the specified URL. <br />
	 * Processing includes: <br /> 
	 * - cleaning of HTML, <br />
	 * - transformation into defined XML, <br />
	 * - deserializing into a {@link Discussion} instance.
	 * 
	 * @param url URL of a webpage to process.
	 * 
	 * @return A {@link Discussion} instance.
	 * 
	 * @throws DiscussionParsingException
	 */
	private Discussion processWebpage (String url) throws DiscussionParsingException {		

		Discussion discussion = null;
		currentPageNumber++;
		this.setChanged();
		this.notifyObservers(currentPageNumber);

		try {
			byte[] cleanXml = this.htmlCleaner.cleanHtml(url);
			byte[] discussionXml = this.xslTransformer.transform(cleanXml);
			discussion = this.discussionDeserializer.deserialize(discussionXml);
		} catch (MalformedURLException e) {
			throw new DiscussionParsingException("Url " + url + " is not properly formed.", e);
		} catch (IllegalStateException e) {
			throw new DiscussionParsingException("xslTransformer is not in the proper state.", e);
		} catch (IOException e) {
			throw new DiscussionParsingException("Can not read or clean HTML from the specified URL.", e);
		} catch (TransformerException e) {
			throw new DiscussionParsingException("An exception occured during the transformation.", e);
		}

		return discussion;
	}

	/**
	 * Extracts transformation name from the provided URL of a discussion.
	 * Simply extracts a web site domain name.
	 * REMARK1: your transformations have to be named accordingly
	 * to the rule "<website-domain-name>.xsl".
	 * REMARK2: Only supports .fr and .com sites, see regexp inside. 
	 * 
	 * @param url An URL of the discussion.
	 * 
	 * @return Transformation name in the format "<website-domain-name>" or empty string if not found.
	 */
	public static String extractTransformName(String url) {
		// allow addresses like "eco.rue89.fr"
		Pattern pattern = Pattern.compile("(\\w+)\\.(\\w+)\\.[(fr)(com)]");
		Matcher matcher = pattern.matcher(url);

		if (matcher.find()) {
			return matcher.group(2);
		} else {
			logger.error("Transformation name can not be extracted from the url: " + url);
			return "";
		}		
	}

	/**
	 * Discussion that is the result of a parsing process.
	 * This should be {@code null} if the parsing has not been called yet.
	 * 
	 * @return {@link Discussion} instance.
	 */
	public Discussion getDiscussion() {
		return discussion;
	}	

	/**
	 * @return Number of a page that is currently parsed.
	 */
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	/**
	 * Sets the value of currentPageNumber and notifies observers.
	 */
	public void setCurrentPageNumber(int pageNumber) {		
		this.currentPageNumber = pageNumber;
		this.setChanged();
		this.notifyObservers(currentPageNumber);
	}
	
}
