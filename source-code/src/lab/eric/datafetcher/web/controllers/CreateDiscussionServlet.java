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
import java.util.Date;

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

public class CreateDiscussionServlet extends ControllerServlet {
	
	private static Logger logger = Logger.getLogger(CreateDiscussionServlet.class);

	/**
	 * Generated serial version Id.
	 */
	private static final long serialVersionUID = -4858044950070917037L;

	private static final String GET_VIEW_NAME = "createDiscussion.jsp";	
	private static final String PAGE_TITLE = "Create Discussion | Data Fetcher";
	
	public static final String DISC_STATUS_KEY = "status";
	
	public static final String DISC_ID_KEY = "id";
	public static final String DISC_NAME_KEY = "name";
	public static final String DISC_SOURCE_KEY = "source";
	public static final String DISC_THEME_KEY = "theme";
	public static final String DISC_URL_KEY = "url";
	public static final String DISC_LANG_KEY = "lang";
	public static final String DISC_TYPE_KEY = "type";
	
	public static final String DISC_DELETE_KEY = "create";	
	
	protected void processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DiscussionModel model = new DiscussionModel();
		request.setAttribute(MODEL_REQUEST_KEY, model);
		
		Session session = Persistence.getSessionFactory().openSession();
		
		Discussion discussion = null;
		DiscussionDao dao = new DiscussionDao(session);
		
		String urlParam = request.getParameter(DISC_URL_KEY);
		if (urlParam != null && !urlParam.trim().isEmpty()) {
			discussion = dao.getByUrl(urlParam.trim());
			if (discussion != null) {
				model.addError("A discussion with this URL has been already added. " +
						"<a href='discussion?id=" + discussion.getId() + "'>Go and check</a>");
			}
			model.setUrl(urlParam.trim());
		} else {
			model.addError("URL field can not be empty.");
		}
		
		String nameParam = request.getParameter(DISC_NAME_KEY);
		if (nameParam == null || nameParam.trim().isEmpty()) {
			model.addError("Name field can not be empty");
		} else {
			model.setName(nameParam.trim());
		}
		
		String sourceParam = request.getParameter(DISC_SOURCE_KEY);
		if (sourceParam == null || sourceParam.trim().isEmpty()) {
			model.addError("Source field can not be empty");
		} else {
			model.setSource(sourceParam.trim());
		}		
		
		model.setTheme(request.getParameter(DISC_THEME_KEY));
		model.setLang(Integer.parseInt(request.getParameter(DISC_LANG_KEY)));
		model.setType(request.getParameter(DISC_TYPE_KEY));
		
		if (model.getErrors().isEmpty()) {
			Transaction transaction = session.beginTransaction();
			try {
				discussion = new Discussion();
				discussion.setName(model.getName());
				discussion.setSource(model.getSource());				
				discussion.setTheme(model.getTheme());
				discussion.setUrl(model.getUrl());
				discussion.setLang(model.getLang());
				discussion.setType(model.getType());
				discussion.setNb(0);
				discussion.setCreated(new Date());
				session.save(discussion);
				
				transaction.commit();
			} catch(Exception ex) {
				transaction.rollback();
				logger.error("Failed to create discussion: ", ex);
				model.addError("Failed to create discussion. See log for details.");
			}
		}
		
		session.close();
		
		if (!model.getErrors().isEmpty()) {		
			RequestDispatcher view = request.getRequestDispatcher(getGetViewName());
			request.setAttribute(PAGE_TITLE_REQUEST_KEY, getPageTitle());
			view.forward(request, response);
			return;
		} else {
			String url = String.format("discussion?%1$s=%2$s&%3$s=%4$s", 
					DISC_ID_KEY, discussion.getId(),
					DISC_STATUS_KEY, DiscussionModel.Status.CREATE_SUCCESS);
			String redirectUrl = response.encodeRedirectURL(url);
			response.sendRedirect(redirectUrl);
		}
	}
	
	protected String getGetViewName() {
		return GET_VIEW_NAME;
	}	
	
	protected String getPageTitle() {
		return PAGE_TITLE;
	}
}
