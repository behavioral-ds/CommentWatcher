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
package lab.eric.datafetcher.persistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import lab.eric.datafetcher.entities.Article;
import lab.eric.datafetcher.entities.Citation;
import lab.eric.datafetcher.entities.Comment;
import lab.eric.datafetcher.entities.Discussion;
import lab.eric.datafetcher.utils.DateFormatter;
import lab.eric.datafetcher.utils.Miscellaneous;
import lab.eric.datafetcher.utils.config.Config;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import TopicExtractor.util.Pretreatment;

/**
 * This class works with database operations on {@link Discussion} objects.
 * 
 * @author Nikolay Anokhin, update Marian-Andrei RIZOIU.
 */
public class DiscussionDao extends HibernateDao<Discussion> {
	
	private static Logger logger = Logger.getLogger(DiscussionDao.class);
	private BufferedWriter extraInfoFileWriter = null;
	
	public DiscussionDao(Session session) {
		super(session);
	}
	
	public Discussion getById(Object id) {
		if (!(id instanceof Integer)) {
			return null;
		}
		
		Object discussion = session.get(Discussion.class, (Integer) id); 
		if (discussion != null) {
			return (Discussion) discussion;
		}
		
		return null;
	}
	
	/**
	 * Returns single discussion with the specified URL.
	 *  
	 * @param url URL of a discussion.
	 * 
	 * @return {@link Discussion} instance.
	 * 
	 * @throws HibernateException if there are more than one discussions with this URL.
	 * 		   Theoretically this should never happen.
	 */
	public Discussion getByUrl(String url) {
		Query query = session.createQuery("from Discussion disc where disc.url = :url");
		query.setString("url", url);
		return(Discussion) query.uniqueResult();		
	}
	
	/**
	 * Persists discussion, its article and all its comments recursively.
	 * 
	 * @param discussion A {@link Discussion} to be persisted.
	 */
	@SuppressWarnings("unchecked")
	public void persistWithContent(Discussion discussion) {			
		int articleId = -1;
		if (discussion.getArticle() != null) {				
			articleId = new ArticleDao(session).save(discussion.getArticle());
		}
			
		new CommentDao(session).persistWithReplies(discussion.getFirstLevelComments()
				, articleId,discussion.getArticle().getAuthor() );
		
		session.saveOrUpdate(discussion);
	}
	
	/**
	 * Gets {@code List} of all {@link Discussion}s filtered by source name and theme.
	 * 
	 * @param nameFilter Filter for discussion name. The syntax is SQL 'like'.
	 * 					   {@code null} or empty is considered 'all'.
	 * @param sourceFilter Filter for source name. The syntax is SQL 'like'.
	 * 					   {@code null} or empty is considered 'all'.
	 * @param themeFilter Filter for theme name. The syntax is SQL 'like'.
	 * 					   {@code null} or empty is considered 'all'.
	 * @param languageFilter Filter for language. The syntax is SQL '='. 1 for French, 2 for English.
	 * 					   Zero or non-valid values are considered 'all'.
	 * @param onlyEmpty Filter all the not-empty discussions (1 or more comments fetched).
	 * 
	 * @return A list of discussions that match filters. Empty list if none found.
	 */
	public List<Discussion> getAllFiltered(String nameFilter, String sourceFilter, String themeFilter, int languageFilter, boolean onlyEmpty) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select disc from Discussion disc where disc is not null");
		
		if (nameFilter != null && !nameFilter.trim().isEmpty()) {
			queryBuilder.append(" and lower(disc.name) like '%" + nameFilter + "%'");
		}
		if (sourceFilter != null && !sourceFilter.trim().isEmpty()) {
			queryBuilder.append(" and lower(disc.source) like '%" + sourceFilter + "%'");
		}
		if (themeFilter != null && !themeFilter.trim().isEmpty()) {
			queryBuilder.append(" and lower(disc.theme) like '%" + themeFilter + "%'");
		}
		if (languageFilter == Config.LANGUAGE_FRENCH || languageFilter == Config.LANGUAGE_ENGLISH) {
			queryBuilder.append(" and disc.lang = " + languageFilter );
		}
		if (onlyEmpty) {
			queryBuilder.append(" and disc.allComments.size = 0");
		}
		
		Query query = session.createQuery(queryBuilder.toString());
		@SuppressWarnings("unchecked")
		List<Discussion> result = query.list();
		
		if (result == null) {
			return new ArrayList<Discussion>();
		}
		
		return result;
	}
	
	/**
	 * Gets {@code List} of all {@link Discussion}s filtered by source name and theme.
	 * 
	 * @param nameFilter Filter for discussion name. The syntax is SQL 'like'.
	 * 					   {@code null} or empty is considered 'all'.
	 * @param sourceFilter Filter for source name. The syntax is SQL 'like'.
	 * 					   {@code null} or empty is considered 'all'.
	 * @param themeFilter Filter for theme name. The syntax is SQL 'like'.
	 * 					   {@code null} or empty is considered 'all'.
	 * @param languageFilter Filter for language. The syntax is SQL '='. 1 for French, 2 for English.
	 * 					   Zero or non-valid values are considered 'all'.
	 * 
	 * @return A list of discussions that match filters. Empty list if none found.
	 */
	public List<Discussion> getAllFiltered(String nameFilter, String sourceFilter, String themeFilter, int languageFilter) {
		return getAllFiltered(nameFilter, sourceFilter, themeFilter, languageFilter, false);
	}
	
	/**
	 * Selects from the provided list only the discussions that
	 * have an article and it was posted between start and end dates.
	 *  
	 * @param discussions A list of discussions.
	 * 
	 * @param start Start date.
	 * 
	 * @param end End date.
	 * 
	 * @return A list of discussions filtered by dates.
	 */
	public List<Discussion> applyDateFilter(List<Discussion> discussions, Date start, Date end) {
		if (start == null && end == null) {
			return discussions;
		}
		
		List<Discussion> filtered = new ArrayList<Discussion>();		
		for (Discussion discussion : discussions) {
			if (discussion.getArticle() == null) {
				continue;
			}
			
			Date discussionDate = DateFormatter.parseDateTimeString(discussion.getArticle().getUpdated());
			if (discussionDate == null) {
				continue;
			}
			
			if (start != null && discussionDate.before(start)) {
				continue;
			}
			
			if (end != null && discussionDate.after(end)) {
				continue;
			}
			
			filtered.add(discussion);
		}
		return filtered;
	}
	
	/**
	 * Deletes all comments and article from the provided discussion.
	 * 
	 * @param discussion {@link Discussion} instance to clean.
	 */
	public void cleanDiscussion(Discussion discussion) {
		if (discussion.getArticle() != null) {
			session.delete(discussion.getArticle());	
		}
		
		if (discussion.getFirstLevelComments() != null) {
			for (Comment comment : discussion.getAllComments()) {
				session.delete(comment);
			}
		}
	}
	
	/**
	 * 
	 * Given a {@code List} of discussion names and a filter, it creates the
	 * input of the classification.
	 * 
	 * @param discussions
	 *            names of discussions from which to select the entries
	 * @param searchText
	 *            textual filter
	 * @param wholeExpression
	 *            is a filter, in fact, a space-separated list of words?
	 * @return the number of entries selected
	 */
	public int createClassificationInput(Vector<String> discussions, String searchText, String wholeExpression) {
		
		// clean problems in discussion names
		for (int i=0; i<discussions.size(); i++) {
			String disc = discussions.elementAt(i);
			
			disc = disc.replace("\"", "\\\"");
			disc = disc.replace("'", "''");
			
			discussions.setElementAt(disc, i);
		}
		
		// first get the discussions that are selected by their names
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select disc from Discussion disc where disc is not null and (");
		
		for ( int i = 0; i < discussions.size() - 1; i++)
			queryBuilder.append(" lower(disc.name) like '" + discussions.elementAt(i).toLowerCase() + "' or ");
		queryBuilder.append(" lower(disc.name) like '" + discussions.elementAt(discussions.size() - 1).toLowerCase() + "' ) ");
		
		logger.debug("HQL query: " + queryBuilder.toString());
		Query query = session.createQuery(queryBuilder.toString());
		@SuppressWarnings("unchecked")
		List<Discussion> result = query.list();
		
		if (result == null) {
			return 0;
		}
		
		int selected = 0;
		
		try {
			this.initInputCollection();
			
			// transform the filtering conditions into a list of strings to filter
			boolean filter = false;
			LinkedList<String> conditions = null;
			if (!searchText.equals("")) {
				filter = true;
				conditions = new LinkedList<String>();
				
				if (wholeExpression != null && wholeExpression.length() != 0 && wholeExpression.compareTo("null") != 0 ) {
					StringTokenizer st = new StringTokenizer(searchText, " ");
					while (st.hasMoreTokens()) {
						conditions.add(st.nextToken().toLowerCase());
					}
				} else {
					conditions.add(searchText);
				}
			}
			
			// now lets filter the comments and article (if any)
			for ( Discussion discussion : result ) {
				// first the article
				Article art = discussion.getArticle();
				if ( art != null && isValid(art.getBody(), filter, conditions) ) {
					this.appendToInputCollection(art.getId(), art.getBody(), discussion.getName(), art.getUpdated(), discussion.getSource(), 1);
					selected++;
				}
				
				// then each comment
				for (Comment comm : discussion.getAllComments() ) {
					if ( comm != null && isValid(comm.getBody(), filter, conditions) ) {
						this.appendToInputCollection(comm.getId(), comm.getBody(), discussion.getName(), comm.getUpdated(), discussion.getSource(), 0);
						selected++;
					}
				}
			}
			
			this.closeInputCollection();
		} catch (IOException e) {
			logger.error("Problems while creating classification input: ", e);
			this.extraInfoFileWriter = null;
		}
		
		logger.debug("Prepared classification input file. Dumped " + selected + " messages!");
		return selected;
	}
	
	/**
	 * Given the text filters, verifies if a text passes the filtering.
	 * 
	 * @param text
	 * @param doFilter
	 *            if false, no filtering is to be done (simply return true).
	 * @param conditions
	 * @return true if it contains at least of the conditions
	 */
	private boolean isValid(String text, boolean doFilter, List<String> conditions) {
		
		if (text == null)
			return false;
		
		if (doFilter) {
			for ( String cond : conditions )
				if ( text.toLowerCase().indexOf(cond) > -1 )
					return true;
			
			// the text does not contain any of the filters
			return false;
		}
		
		return true;
	}
	
	/**
	 * Initializes the classification input files.
	 * 
	 * @throws IOException
	 */
	private void initInputCollection() throws IOException {
		// delete then create the input folder
		Miscellaneous.deleteFileOrFolderRecursively(Config.getInputClassificationDir());
		Miscellaneous.verifyCreateFolder(Config.getInputClassificationDir());
		
		this.extraInfoFileWriter = new BufferedWriter(new FileWriter(Config.getExtraInfoFile(), false));
	}
	
	/**
	 * Closes the classification input files.
	 * 
	 * @throws IOException
	 */
	private void closeInputCollection() throws IOException {
		this.extraInfoFileWriter.close();
		this.extraInfoFileWriter = null;
	}
	
	/**
	 * 
	 * Adds to the classification input one entry (article or comment), specified by the necessary parameters.
	 * 
	 * @param id
	 * @param body
	 * @param discussionName
	 * @param date
	 * @param sourceName
	 * @param type
	 * @throws IOException
	 */
	private void appendToInputCollection (int id, String body, String discussionName, String date, String sourceName, int type) throws IOException {
		// the second field is the ID of the comment or article
		// so use it in the filename
		String name = String.valueOf(id);
		FileWriter w = new FileWriter(Config.getInputClassificationDir() + "/message_" + name, false);
		BufferedWriter out = new BufferedWriter(w);

		// print the text in the input file (just the first field)
		String text = body;
		// for topical NGrams, we are going to remove French accents
		if (Config.getClassificationAlgorithm() == Config.CLASSIFIER_TOPICALNGRAMS)
			text = Pretreatment.removeFrenchAccents(text);
		out.write(text + "\n");
		
		// print the extra info in the extra info file (last value is zero because we are working with a comment)
		this.extraInfoFileWriter.write(id + "\t" + discussionName + "\t" + DateFormatter.formatDate(date) + "\t" + sourceName + "\t" + type + "\n");
		
		out.close();
	}
}
