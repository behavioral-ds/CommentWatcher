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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * This is a base controller for all webpages, 
 * contains the common information such as page title. 
 * 
 * @author Nikolay Anokhin
 */
public class ControllerServlet extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(ControllerServlet.class);

	/**
	 * Generated Serial version Id.
	 */
	private static final long serialVersionUID = 7568605845421666430L;
	
	private static final String NOT_FOUND_VIEW_NAME = "notFound.jsp";
	public static final String DEFAULT_PAGE_TITLE = "Data Fetcher";
	
	public static final String MODEL_REQUEST_KEY = "model";
	public static final String PAGE_TITLE_REQUEST_KEY = "page_title";
	
	public static final String DISC_ID_KEY = "id";
	public static final String DISC_FILE_KEY = "file";
	
	/**
	 * This method processes get request from the client. <br/>
	 * In particular, put model instance into the request here. <br/>
	 * Override this in children classes.
	 * 
	 * @param request Received request.
	 * 
	 * @param response Response to be sent.
	 */
	protected void processGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
	}
	
	/**
	 * This method processes POST request from the client. <br/>
	 * In particular, put model instance into the request here. <br/>
	 * Override this in children classes, after POST use response.sendRedirect(..).
	 * 
	 * @param request Received request.
	 * 
	 * @param response Response to be sent.
	 */
	protected void processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
	}
	
	/**
	 * Do not override this method, or override wisely.
	 * Use processGet() instead.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		processGet(request, response);
		
		RequestDispatcher view = request.getRequestDispatcher(getGetViewName());
		request.setAttribute(PAGE_TITLE_REQUEST_KEY, getPageTitle());
		view.forward(request, response);
	}
	
	/**
	 * Do not override this method, or override wisely.
	 * Use processPost() instead.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processPost(request, response);		
	}
	
	/**
	 * Extracts id of a model from the request.
	 * 
	 * @param request {@code HttpServletRequest} instance.
	 * 
	 * @return Integer id if the parameter is found and parsed, {@code null} otherwise.
	 */
	public Integer extractIdParameter(HttpServletRequest request) {
		String idParam = request.getParameter(DISC_ID_KEY);
		if (idParam == null) {			
			return null;
		}
		Integer id;
		try {
			id = Integer.parseInt(idParam.trim());			
		} catch (NumberFormatException ex) {
			logger.debug("An attempt to access the discussion with id " + idParam + ":" + ex.getMessage());			
			return null;
		}
		return id;
	}

	/**
	 * Override this method in order to return right .jsp page that serves for GET request.
	 * 
	 * @return Name of the GET view .jsp.
	 */
	protected String getGetViewName() {
		return NOT_FOUND_VIEW_NAME;
	}	
	
	/**
	 * Override this to provide the right page title.
	 * 
	 * @return Title of web-page.
	 */
	protected String getPageTitle() {
		return DEFAULT_PAGE_TITLE;
	}
}
