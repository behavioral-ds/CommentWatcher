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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lab.eric.datafetcher.web.models.XmlFileModel;
import lab.eric.visualizer.model.CommunicationObject;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * Controller class that works with visualizeCitation.jsp.
 * 
 * @author Marian-Andrei RIZOIU
 */
public class VisualizeCitationServlet extends ControllerServlet {
	
	private static Logger logger = Logger.getLogger(VisualizeXmlServlet.class);
	
	private static XmlFileModel myModel= null;

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 7364780508231464027L;
	
	private static final String GET_VIEW_NAME = "visualizeCitation.jsp";	
	private static final String PAGE_TITLE = "Citation | Data Fetcher";
	
	public static final String DISC_POPOUT_KEY = "popout";

	protected void processGet(HttpServletRequest request, HttpServletResponse response) {
		XmlFileModel model = new XmlFileModel();
		request.setAttribute(MODEL_REQUEST_KEY, model);		
		
		String fileURL = extractFileParameter(request);
		if (fileURL == null) {
			model.addError("Could not understand the file you want to visualize!");
			logger.error("Visualize called with no XML file parameter! Not good!");
		} else {
			model.setCompleteURL(fileURL);
		}
		
		model.setFileName(model.getFileNameFromURL());
		myModel = model;
	}
	
	private String extractFileParameter(HttpServletRequest request) {
		String fileParam = request.getParameter(DISC_FILE_KEY);
		if (fileParam == null) {			
			return null;
		}
		
		return fileParam.trim();
	}

	protected void processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		XmlFileModel model = myModel;
		request.setAttribute(MODEL_REQUEST_KEY, model);	
		
		String source = request.getParameter(DISC_POPOUT_KEY);
		if (source != null && !source.trim().isEmpty()) {
			model.togglePopedOut();
			RequestDispatcher view = request.getRequestDispatcher(getGetViewName());
			request.setAttribute(PAGE_TITLE_REQUEST_KEY, getPageTitle());
			view.forward(request, response);
			return;
		}
		
		logger.debug("Request from the applet!");
		// if we are here, means there is a request from the applet
		try {
			response.setContentType("application/x-java-serialized-object");

			// read a CommunicationObject from applet
			InputStream in = request.getInputStream();
			ObjectInputStream inputFromApplet = new ObjectInputStream(in);
			CommunicationObject requestObj = (CommunicationObject) inputFromApplet.readObject();
			
			logger.debug("Got the communication object!");
			
			// if we got a malformed result
			if ( !requestObj.isValid() ) {
				Exception e = new Exception("Error in the communication with the applet! Malformed request!");
				throw e;
			}
			
			// if there is a problem with the communication (not a request)
			if ( requestObj.getCommunicationType() == CommunicationObject.REPLY_OK_XML_FILE ) {
				Exception e = new Exception("Error in the communication with the applet! Bad request!");
				throw e;
			}
			
			// if the applet had a problem that feels to share with us
			if ( requestObj.getCommunicationType() == CommunicationObject.REPLY_FAILED_XML_FILE ) {
				Exception e = (Exception) requestObj.getAttachment();
				logger.error("The applet died proudly! Here is why: " + e);
				return;
			}
			
			// parse the file. All exceptions will be caught and sent to the applet
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;

			dBuilder = dbFactory.newDocumentBuilder();
			Document xmlDocument = dBuilder.parse(model.getCompleteURL());
			xmlDocument.getDocumentElement().normalize();
			
			// construct the response
			CommunicationObject responseObj = new CommunicationObject(CommunicationObject.REPLY_OK_XML_FILE, xmlDocument);

			// send it to the applet
			OutputStream outstr = response.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstr);
			oos.writeObject(responseObj);
			oos.flush();
			oos.close();
			
			logger.debug("Reply sent to applet!");

		} catch (Exception e) {
			// everything needs to be sent to the applet
			logger.error("Got an exception while treating a demand from the applet: " + e);
			CommunicationObject responseObj = new CommunicationObject(CommunicationObject.REPLY_FAILED_XML_FILE, e);
			
			// send it to the applet
			OutputStream outstr = response.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstr);
			oos.writeObject(responseObj);
			oos.flush();
			oos.close();
		}
		
	}		
	
	protected String getGetViewName() {
		return GET_VIEW_NAME;
	}
	
	protected String getPageTitle() {
		return PAGE_TITLE;
	}
}
