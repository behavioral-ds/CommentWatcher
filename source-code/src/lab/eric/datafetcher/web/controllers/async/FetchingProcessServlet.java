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
package lab.eric.datafetcher.web.controllers.async;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lab.eric.datafetcher.entities.Discussion;
import lab.eric.datafetcher.persistence.DiscussionDao;
import lab.eric.datafetcher.persistence.Persistence;
import lab.eric.datafetcher.web.controllers.ControllerServlet;
import lab.eric.datafetcher.web.models.FetchingProcessModel;

import org.apache.log4j.Logger;
import org.hibernate.Session;

public class FetchingProcessServlet extends ControllerServlet {
	
	private static Logger logger = Logger.getLogger(FetchingProcessServlet.class);
	
	public static String ACTION_KEY = "action";
	
	public static String START_ACTION = "start";
	public static String MONITOR_ACTION = "monitor";
	public static String RESULT_ACTION = "result";
	
	public static String OBSERVER_SESSION_KEY = "observer";

	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -2252403802733612218L;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String actionParam = request.getParameter(ACTION_KEY);
		response.setContentType("application/json;charset=UTF-8");
		FetchingProcessModel model = new FetchingProcessModel();
		if (actionParam == null || actionParam.trim().isEmpty()) {
			logger.error("FetchingProcessServlet is called with wrong action parameter.");
			model.setStatus(FetchingProcessStatus.ERROR.getCode());
			model.setMessage("FetchingProcessServlet is called with wrong action parameter.");
			response.getWriter().write(model.toJson());
			return;
		}
		
		String result = null;
		String action = actionParam.trim();
		
		Integer id = extractIdParameter(request);
		if (id == null) {
			logger.error("Unable to find a valid id of the discussion in the request, id: " + id);
			model.setStatus(FetchingProcessStatus.ERROR.getCode());
			model.setMessage("Unable to find a valid id of the discussion in the request, id: " + id);			
			response.getWriter().write(model.toJson());
			return;
		}
		
		if (action.equalsIgnoreCase(START_ACTION)) {
			result = startFetch(request, id);
		} else if (action.equalsIgnoreCase(MONITOR_ACTION)) {
			result = monitorFetch(request, id);
		} else if (action.equalsIgnoreCase(RESULT_ACTION)) {
			result = getResultOfFetch(request, id);
		} else {
			logger.error("The action of FetchingProcessServlet not found: " + action);
			model.setStatus(FetchingProcessStatus.ERROR.getCode());
			model.setMessage("The action of FetchingProcessServlet not found: " + action);
			result = model.toJson();
		}
		
		response.getWriter().write(result);
	}
	
	private String startFetch(HttpServletRequest request, Integer id) {
		FetchingProcessModel model = new FetchingProcessModel();
		model.setDiscId(id);
				
		FetchingProcess fetchingProcess = new FetchingProcess();
		fetchingProcess.initialize(id, null);
		
		FetchingProcessObserver observer = new FetchingProcessObserver();
		fetchingProcess.addObserver(observer);
		
		Thread thread = new Thread(fetchingProcess);
		thread.start();
		
		request.getSession().setAttribute(OBSERVER_SESSION_KEY + id, observer);	
		
		model.setStatus(observer.getStatus().getCode());
		model.setMessage(observer.getStatus().getMessage());
		return model.toJson();
	}
	
	private String monitorFetch(HttpServletRequest request, Integer id) {
		FetchingProcessModel model = new FetchingProcessModel();
		model.setDiscId(id);
		
		FetchingProcessObserver observer = 
			(FetchingProcessObserver) request.getSession().getAttribute(OBSERVER_SESSION_KEY + id);
		
		if (observer == null) {
			logger.error("FetchingProcessObserver does not exist in memory.");
			model.setStatus(FetchingProcessStatus.ERROR.getCode());
			model.setMessage("FetchingProcessObserver does not exist in memory.");
			return model.toJson();
		}		
		
		FetchingProcessStatus status = observer.getStatus();		
		model.setStatus(status.getCode());
		model.setMessage(status.getMessage());
		return model.toJson();
	}
	
	private String getResultOfFetch(HttpServletRequest request, Integer id) {
		FetchingProcessModel model = new FetchingProcessModel();
		model.setDiscId(id);
		
		Session session = Persistence.getSessionFactory().openSession();
		
		DiscussionDao dao = new DiscussionDao(session);
		Discussion discussion = dao.getById(id);
		
		model.setMessage("Refreshing parameters...");
		model.setFetchDate(discussion.getFetched());		
		if (discussion.getArticle() != null && discussion.getArticle().getTitle() != null) {
			model.setArticleTitle(discussion.getArticle().getTitle().getTitle());
		} else {
			model.setArticleTitle("no article");
		}		
		model.setCommentsFetched(discussion.getAllComments().size());
		
		session.close();
		
		model.setStatus(FetchingProcessStatus.SUCCESS.getCode());
		request.getSession().removeAttribute(OBSERVER_SESSION_KEY + id);
		return model.toJson();
	}
}
