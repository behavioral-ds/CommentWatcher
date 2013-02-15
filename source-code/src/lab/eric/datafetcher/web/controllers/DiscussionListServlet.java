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
package lab.eric.datafetcher.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import lab.eric.datafetcher.entities.Discussion;
import lab.eric.datafetcher.persistence.DiscussionDao;
import lab.eric.datafetcher.persistence.Persistence;
import lab.eric.datafetcher.utils.DateFormatter;
import lab.eric.datafetcher.web.models.DiscussionListModel;
import lab.eric.datafetcher.web.models.DiscussionModel;

/**
 * Serves as a controller for discussionList.jsp.
 * The main model is {@link DiscussionListModel}.
 * 
 * @author Nikolay Anokhin.
 */
public class DiscussionListServlet extends ControllerServlet {
	
	private static Logger logger = Logger.getLogger(DiscussionListServlet.class);

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 78782294358325934L;
	
	private static final String VIEW_NAME = "discussionList.jsp";
	private static final String PAGE_TITLE = "List of Discussions | Data Fetcher";
	
	public static final String NAME_FILTER_KEY = "name_filter";
	public static final String SOURCE_FILTER_KEY = "source_filter";
	public static final String THEME_FILTER_KEY = "theme_filter";
	public static final String EMPTY_FILTER_KEY = "empty_filter";
	public static final String START_FILTER_KEY = "start_filter";
	public static final String END_FILTER_KEY = "end_filter";
	
	protected void processGet(HttpServletRequest request, HttpServletResponse response) {	
		DiscussionListModel model = new DiscussionListModel();
		
		String nameFilter = request.getParameter(NAME_FILTER_KEY);
		if (nameFilter != null) {
			model.setNameFilter(nameFilter.trim());
		}
		
		String sourceFilter = request.getParameter(SOURCE_FILTER_KEY);
		if (sourceFilter != null) {
			model.setSourceFilter(sourceFilter.trim());
		}
		
		String themeFilter = request.getParameter(THEME_FILTER_KEY);
		if (themeFilter != null) {
			model.setThemeFilter(themeFilter.trim());
		}

		Date startDate = DateFormatter.parseDateString(request.getParameter(START_FILTER_KEY));		
		model.setStartDate(startDate);
		
		Date endDate = DateFormatter.parseDateString(request.getParameter(END_FILTER_KEY));		
		model.setEndDate(endDate);
				
		boolean onlyEmpty = request.getParameter(EMPTY_FILTER_KEY) != null && 
							request.getParameter(EMPTY_FILTER_KEY).equals("on");
		model.setOnlyEmpty(onlyEmpty);
		
		String statusParam = request.getParameter(DiscussionDetailsServlet.DISC_STATUS_KEY);
		if (statusParam != null) {
			try {
				model.setStatus(DiscussionModel.Status.valueOf(statusParam));
			} catch (IllegalArgumentException ex) {
				logger.debug("Status not found: " + statusParam);
			}
		}
		
		// Load discussions corresponding to the filter.
		Session session = Persistence.getSessionFactory().openSession();
		
		DiscussionDao dao = new DiscussionDao(session);
		List<Discussion> discussions = dao.getAllFiltered(nameFilter, sourceFilter, themeFilter, 0, onlyEmpty);		
		discussions = dao.applyDateFilter(discussions, startDate, endDate);		
		
		List<DiscussionModel> models = new ArrayList<DiscussionModel>();
		DiscussionModel discussionModel = null;
		for (Discussion discussion : discussions) {
			discussionModel = new DiscussionModel(discussion.getId(), 
												  discussion.getName(),
												  discussion.getSource(), 
												  discussion.getTheme(), 
												  discussion.getUrl());			
			discussionModel.setCommentsFetched(discussion.getAllComments().size());			
			discussionModel.setArticlePostedDate(discussion.getArticle() != null
					? DateFormatter.parseDateTimeString(discussion.getArticle().getUpdated())
					: null);
			
			models.add(discussionModel);
		}
		model.setDiscussions(models);
		
		session.close();
		
		// Set model to the transmitted request.
		request.setAttribute(MODEL_REQUEST_KEY, model);
	}
	
	protected void processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String toDeleteParam = request.getParameter("to_delete");
		
		if (toDeleteParam != null && !toDeleteParam.trim().isEmpty()) {			
			Session session = Persistence.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			
			DiscussionDao dao = new DiscussionDao(session);
					
			try {
				String[] ids = toDeleteParam.split(";");			
				for (String id : ids) {
					Discussion discussion = dao.getById(Integer.parseInt(id));
					if (discussion == null) {
						logger.error("Discussion with id" + id + " not found.");
						continue;
					}					
					
					logger.debug("Delete discussion with id " + discussion.getId());
					dao.remove(discussion);					
				}
				transaction.commit();
			} catch (Exception ex) {
				transaction.rollback();			
				logger.error("Can not delete one of discussions. Ids: " + toDeleteParam, ex);				
			} finally {		
				session.close();
			}
		}		
		
		String url = String.format("discussionList?%1$s=%2$s&%3$s=%4$s&%5$s=%6$s&%7$s=%8$s&%9$s=%10$s&%11$s=%12$s", 
				NAME_FILTER_KEY, request.getParameter(NAME_FILTER_KEY),
				SOURCE_FILTER_KEY, request.getParameter(SOURCE_FILTER_KEY),
				THEME_FILTER_KEY, request.getParameter(THEME_FILTER_KEY),
				START_FILTER_KEY, request.getParameter(START_FILTER_KEY),
				END_FILTER_KEY, request.getParameter(END_FILTER_KEY),
				EMPTY_FILTER_KEY, request.getParameter(EMPTY_FILTER_KEY));
		String redirectUrl = response.encodeRedirectURL(url);
		response.sendRedirect(redirectUrl);
	}
	
	protected String getGetViewName() {
		return VIEW_NAME;
	}
	
	protected String getPageTitle() {
		return PAGE_TITLE;
	}
}
