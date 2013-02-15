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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import lab.eric.datafetcher.entities.Discussion;
import lab.eric.datafetcher.persistence.DiscussionDao;
import lab.eric.datafetcher.persistence.Persistence;
import lab.eric.datafetcher.web.models.DiscussionModel;

/**
 * Controller class that works with discussionDetails.jsp.
 * 
 * @author Nikolay Anokhin
 */
public class DiscussionDetailsServlet extends ControllerServlet {
	
	private static Logger logger = Logger.getLogger(DiscussionDetailsServlet.class);

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 7364780508231464027L;
	
	private static final String GET_VIEW_NAME = "discussionDetails.jsp";	
	private static final String PAGE_TITLE = "Discussion | Data Fetcher";

	public static final String DISC_STATUS_KEY = "status";
	public static final String DISC_NAME_KEY = "name";
	public static final String DISC_SOURCE_KEY = "source";
	public static final String DISC_THEME_KEY = "theme";
	public static final String DISC_DELETE_KEY = "delete";
	public static final String DISC_UPDATE_KEY = "update";
	public static final String DISC_FETCH_KEY = "fetch";
	
	protected void processGet(HttpServletRequest request, HttpServletResponse response) {
		DiscussionModel model = new DiscussionModel();
		request.setAttribute(MODEL_REQUEST_KEY, model);		
		
		Integer id = extractIdParameter(request);
		if (id == null) {
			model.addError("Id is not specified or it is not an integer");
		} else {
			model.setId(id);
		}
		
		//Retrieve operation status if request came from redirect.
		String statusParam = request.getParameter(DISC_STATUS_KEY);
		if (statusParam != null && !statusParam.isEmpty()) {
			try {
				model.setStatus(DiscussionModel.Status.valueOf(statusParam));
			} catch(IllegalArgumentException ex) {
				logger.debug("Status not found: " + statusParam);
			}
		}
		
		Session session = Persistence.getSessionFactory().openSession();
		
		Discussion discussion = new DiscussionDao(session).getById(id);
		if (discussion == null) {
			model.addError("Can not find the discussion with specified id: " + id);
		} else {
			model.setName(discussion.getName());
			model.setSource(discussion.getSource());
			model.setTheme(discussion.getTheme());
			model.setUrl(discussion.getUrl());
			model.setLang(discussion.getLang());
			model.setType(discussion.getType());
			model.setFetchDate(discussion.getFetched());
			
			if (discussion.getArticle() != null && discussion.getArticle().getTitle() != null) {
				model.setArticleTitle(discussion.getArticle().getTitle().getTitle());
			} else {
				model.setArticleTitle("no article");
			}
			
			model.setCommentsFetched(discussion.getAllComments().size());
		}
		
		session.close();
	}
	
	protected void processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DiscussionModel model = new DiscussionModel();
		request.setAttribute(MODEL_REQUEST_KEY, model);	
		
		Integer id = extractIdParameter(request);
		if (id == null) {
			model.addError("Id is not specified or it is not an integer");
		} else {
			model.setId(id);
		}
		
		String source = request.getParameter(DISC_SOURCE_KEY);
		if (source != null && !source.trim().isEmpty()) {
			model.setSource(source.trim());
		} else {
			model.setSource("");
			model.addError("Source can not be empty.");
		}
		
		String name = request.getParameter(DISC_NAME_KEY);
		if (name != null && !name.trim().isEmpty()) {
			model.setName(name.trim());
		} else {
			model.setName("");
			model.addError("Name can not be empty.");
		}
		
		String theme = request.getParameter(DISC_THEME_KEY);
		if (theme != null) {
			model.setTheme(theme.trim());
		}
		
		String url = "/";
		if (model.getErrors().isEmpty()) {			
			if (request.getParameter(DISC_DELETE_KEY) != null) {
				processDelete(model);
				url = String.format("discussionList?%1$s=%2$s", DISC_STATUS_KEY, DiscussionModel.Status.DELETE_SUCCESS);
			}
			if (request.getParameter(DISC_UPDATE_KEY) != null) {
				processUpdate(model);
				url = String.format("discussion?%1$s=%2$d&%3$s=%4$s", DISC_ID_KEY, id, 
									DISC_STATUS_KEY, DiscussionModel.Status.UPDATE_SUCCESS);
			}			
		}
		
		if (!model.getErrors().isEmpty()) {		
			RequestDispatcher view = request.getRequestDispatcher(getGetViewName());
			request.setAttribute(PAGE_TITLE_REQUEST_KEY, getPageTitle());
			view.forward(request, response);
			return;
		} else {		
			String redirectUrl = response.encodeRedirectURL(url);
			response.sendRedirect(redirectUrl);
		}
	}
	
	private void processDelete(DiscussionModel model) {
		Session session = Persistence.getSessionFactory().openSession();
		
		DiscussionDao dao = new DiscussionDao(session);
		Discussion discussion = dao.getById(model.getId());
		if (discussion == null) {
			model.addError("Can not find the discussion with specified id: " + model.getId());
			session.close();
			return;
		}
		
		Transaction transaction = session.beginTransaction();
		try {
			logger.debug("Delete discussion with id " + discussion.getId());
			dao.remove(discussion);			
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();			
			logger.error("Can not delete discussion with id " + discussion.getId(), ex);
			model.addError("Can not delete discussion with id " + discussion.getId());
		} finally {		
			session.close();
		}
	}
	
	private void processUpdate(DiscussionModel model) {
		Session session = Persistence.getSessionFactory().openSession();
		
		Discussion discussion = new DiscussionDao(session).getById(model.getId());
		if (discussion == null) {
			model.addError("Can not find the discussion with specified id: " + model.getId());
			session.close();
			return;
		}
		
		Transaction transaction = session.beginTransaction();
		try {
			discussion.setName(model.getName());
			discussion.setSource(model.getSource());
			discussion.setTheme(model.getTheme());						
			logger.info("Updated discussion with id " + discussion.getId());			
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();			
			logger.error("Can not delete discussion with id " + discussion.getId(), ex);
			model.addError("Can not delete discussion with id " + discussion.getId());
		} finally {		
			session.close();
		}
	}		
	
	protected String getGetViewName() {
		return GET_VIEW_NAME;
	}
	
	protected String getPageTitle() {
		return PAGE_TITLE;
	}
}
