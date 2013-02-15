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
package lab.eric.datafetcher.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lab.eric.datafetcher.persistence.Persistence;
import lab.eric.datafetcher.utils.config.Config;
import lab.eric.datafetcher.utils.log.Logger;
import lab.eric.datafetcher.web.controllers.ClassificationServlet;

/**
 * A servlet that initializes logger and config on the web-application startup.
 * Also initializes Hibernate mappings.
 * See web.xml for the servlet declaration.
 * 
 * @author Nikolay Anokhin.
 */
public class StartupServlet extends HttpServlet {
	
	/**
	 * Generated serial ID.
	 */
	private static final long serialVersionUID = 5515774819027783888L;

	/**
	 * Initializes config and logger on application startup.
	 */
	public void init() {		
		// Configure logger.
		String logConfigName = getInitParameter("log4j-init-file");
		Logger.configure(logConfigName);
		
		// Create config.
		Config.createConfig();
		
		// Init ClassificationModel
		ClassificationServlet.createClassificationModel();
		
		// Initialize persistence.
		Persistence.configureHibernate();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) {
	}
}
