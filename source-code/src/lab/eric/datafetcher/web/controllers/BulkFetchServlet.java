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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lab.eric.datafetcher.entities.Discussion;
import lab.eric.datafetcher.parsers.TransformParser;
import lab.eric.datafetcher.persistence.Persistence;
import lab.eric.datafetcher.web.controllers.async.FetchingProcessServlet;
import lab.eric.datafetcher.web.controllers.async.FetchingProcessStatus;
import lab.eric.datafetcher.web.models.FetchingProcessModel;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BulkFetchServlet extends ControllerServlet {
	
	private static Logger logger = Logger.getLogger(BulkFetchServlet.class);
	
	public static final String CLEANUP_ACTION = "cleanup";
	public static final String CREATE_ACTION = "create";

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -8125741679968825592L;
	
	private static final String GET_VIEW_NAME = "bulkFetch.jsp";	
	private static final String PAGE_TITLE = "Bulk Fetching | Data Fetcher";
	
	protected void processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
		FetchingProcessModel model = new FetchingProcessModel();
		
		String actionParam = request.getParameter(FetchingProcessServlet.ACTION_KEY);		
		
		if (actionParam == null || actionParam.trim().isEmpty()) {
			logger.error("BulkFetchServlet is called with wrong action parameter.");
			model.setStatus(FetchingProcessStatus.ERROR.getCode());
			model.setMessage("BulkFetchServlet is called with wrong action parameter.");
			response.getWriter().write(model.toJson());
			return;
		}
		
		String action = actionParam.trim();
		String result = "";
		
		if (action.equalsIgnoreCase(CREATE_ACTION)) {
			result = createDiscussion(request);
		} else if (action.equalsIgnoreCase(CLEANUP_ACTION)) {
			request.getSession().removeAttribute(FetchingProcessServlet.OBSERVER_SESSION_KEY + request.getParameter(DISC_ID_KEY));
			model.setMessage("Cleanup done");
			result = model.toJson();
		}
		response.getWriter().write(result);
	}
	
	private String createDiscussion(HttpServletRequest request) {
		FetchingProcessModel model = new FetchingProcessModel();
		
		String theme = request.getParameter("theme");
		String title = request.getParameter("title");
		String url = request.getParameter("href");
		logger.debug("Creating discussion with parameters: title: " + title + " href: " + url);
		
		Session session = Persistence.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		
		try {				
			Discussion discussion = new Discussion();
			
			discussion.setName(title);
			discussion.setSource(TransformParser.extractTransformName(url));				
			discussion.setTheme(theme);
			discussion.setUrl(url);
			discussion.setLang(1);
			discussion.setType("discussion");
			discussion.setNb(0);
			discussion.setCreated(new Date());
			Integer id = (Integer) session.save(discussion);
			model.setDiscId(id);			
			
			transaction.commit();
		} catch(Exception ex) {
			transaction.rollback();
			logger.error("Can not create discussion from href: " + url);
			model.setStatus(FetchingProcessStatus.ERROR.getCode());
			model.setMessage("Can not create discussion. Possibly, it duplicates an existing one");			
			return model.toJson();
		}
		
		session.close();
		model.setStatus(FetchingProcessStatus.SUCCESS.getCode());
		model.setMessage("Discussion successfully created");
		return model.toJson();
	}
	
	protected String getGetViewName() {
		return GET_VIEW_NAME;
	}	
	
	protected String getPageTitle() {
		return PAGE_TITLE;
	}
}
