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
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import lab.eric.datafetcher.persistence.DiscussionDao;
import lab.eric.datafetcher.persistence.Persistence;
import lab.eric.datafetcher.utils.config.Config;
import lab.eric.datafetcher.web.models.ClassificationModel;
import lab.eric.datafetcher.web.models.DiscussionModel;


/**
 * Controller class that works with classification.jsp.
 * 
 * @author Marian-Andrei RIZOIU
 */
public class ClassificationServlet extends ControllerServlet {

	private static final long serialVersionUID = -8326415132534505218L;

	private static final String GET_VIEW_NAME = "classification.jsp";	
	private static final String PAGE_TITLE = "Classification | Data Fetcher";

	public static final String CLASS_PARAMETERS_MODIFY_KEY = "modify";
	public static final String CLASS_PARAMETERS_STOP_KEY = "stop";
	public static final String CLASS_PARAMETERS_START_KEY = "start";
	public static final String CLASS_SWITH_LANGUAGE_KEY = "languageradio";

	public static final String CLASS_ALGO_KEY = "algo";
	public static final String CLASS_NUM_CLASSES_KEY = "numclass";
	public static final String CLASS_UPDATE_TIME_KEY = "updatetime";
	public static final String CLASS_LANGUAGE_KEY = "language";
	public static final String CLASS_MEASURE_KEY = "measure";
	public static final String CLASS_MIN_WORDS_KEY = "minwords";

	public static final String CLASS_SELECTED_LANGUAGE_RADIO_KEY = "selectedradio";
	public static final String CLASS_SELECTED_DISC_KEY = "discussions";
	public static final String CLASS_FILTER_TEXT_KEY = "filtertext";
	public static final String CLASS_FILTER_TYPE_KEY = "wordsonly";

	private static ClassificationModel model = null;

	protected void processGet(HttpServletRequest request, HttpServletResponse response) {
		// add model to be available in the JSP page 
		request.setAttribute(MODEL_REQUEST_KEY, model);
		
		// populate the two Discussion lists (French and English)
		Session mySession = Persistence.getSessionFactory().openSession();
		DiscussionDao dao = new DiscussionDao(mySession);
		model.setFrenchDiscussions(dao.getAllFiltered(null, null, null, Config.LANGUAGE_FRENCH));
		model.setEnglishDiscussions(dao.getAllFiltered(null, null, null, Config.LANGUAGE_ENGLISH));
		mySession.close();
	}

	protected void processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// add model to be available in the JSP page 
		request.setAttribute(MODEL_REQUEST_KEY, model);

		if (request.getParameter(CLASS_PARAMETERS_MODIFY_KEY) != null) {
			try {
				if (model.getAlgorithm() == 0 ) { // only for CKP
					Config.setClassificationMeasure(Integer.parseInt(request.getParameter(CLASS_MEASURE_KEY)));
					model.setMeasure(Integer.parseInt(request.getParameter(CLASS_MEASURE_KEY)));
					Config.setClassificationMinWords(Integer.parseInt(request.getParameter(CLASS_MIN_WORDS_KEY)));
					model.setMinWords(Integer.parseInt(request.getParameter(CLASS_MIN_WORDS_KEY)));
				}

				Config.setClassificationAlgorithm(Integer.parseInt(request.getParameter(CLASS_ALGO_KEY)));
				model.setAlgorithm(Integer.parseInt(request.getParameter(CLASS_ALGO_KEY)));
				Config.setClassificationNumClusters(Integer.parseInt(request.getParameter(CLASS_NUM_CLASSES_KEY)));
				model.setNumClusters(Integer.parseInt(request.getParameter(CLASS_NUM_CLASSES_KEY)));
				Config.setClassificationUpdateTime(Integer.parseInt(request.getParameter(CLASS_UPDATE_TIME_KEY)));
				model.setUpdateTime(Integer.parseInt(request.getParameter(CLASS_UPDATE_TIME_KEY)));
				Config.setClassificationLanguage(Integer.parseInt(request.getParameter(CLASS_LANGUAGE_KEY)));
				model.setLanguage(Integer.parseInt(request.getParameter(CLASS_LANGUAGE_KEY)));

			} catch (NumberFormatException e) {
				model.addError("Some fields are invalid!");
			}
		}

		if (request.getParameter(CLASS_PARAMETERS_STOP_KEY) != null) {
			model.setEnabled(false);
		}

		if (request.getParameter(CLASS_PARAMETERS_START_KEY) != null) {

			Vector<Integer> selectedDiscussionsId = new Vector<Integer>();
			Vector<String> selectedDiscussions = new Vector<String>();
			String[] checkboxes = request.getParameterValues(CLASS_SELECTED_DISC_KEY);
			String onlyWords = request.getParameter(CLASS_FILTER_TYPE_KEY);
			String textFilter = request.getParameter(CLASS_FILTER_TEXT_KEY);

			try {
				// put the selected discussions into a vector
				for (int i = 0; i < checkboxes.length; ++i)
					selectedDiscussionsId.addElement( Integer.parseInt(checkboxes[i]) );
				
				//search for discussion names
				for (DiscussionModel discussion : model.getDiscussionList()) {
					if ( selectedDiscussionsId.contains( discussion.getId() )) 
						selectedDiscussions.addElement(discussion.getName());
				}

				model.startClassification(selectedDiscussions, textFilter, onlyWords);
			} catch (Exception ex) {
				ClassificationServlet.addError("At least one discussion should be selected!");
				model.setEnabled(false);
			}
		}

		if (request.getParameter(CLASS_SWITH_LANGUAGE_KEY) != null) {
			int val = Integer.parseInt(request.getParameter(CLASS_SELECTED_LANGUAGE_RADIO_KEY));
			model.setShownDiscussionLanguage(val);

			// just make sure they don't classify French texts with English language
			// let's set it for them
			Config.setClassificationLanguage(val);
			model.setLanguage(val);
		}

		RequestDispatcher view = request.getRequestDispatcher(getGetViewName());
		request.setAttribute(PAGE_TITLE_REQUEST_KEY, getPageTitle());
		view.forward(request, response);
	}

	protected String getGetViewName() {
		return GET_VIEW_NAME;
	}	

	protected String getPageTitle() {
		return PAGE_TITLE;
	}

	/**
	 * Creates the singleton instance of the Model.
	 */
	public static void createClassificationModel() {
		if (model == null) {
			model = new ClassificationModel();
		}
	}
	
	public static void addError(String message) {
		model.addError(message);
	}
}
