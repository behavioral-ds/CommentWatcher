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
package lab.eric.datafetcher.web.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import lab.eric.datafetcher.entities.Discussion;
import lab.eric.datafetcher.persistence.DiscussionDao;
import lab.eric.datafetcher.persistence.Persistence;
import lab.eric.datafetcher.utils.Classification;
import lab.eric.datafetcher.utils.config.Config;
import lab.eric.datafetcher.web.controllers.ClassificationServlet;

import org.apache.log4j.Logger;
import org.hibernate.Session;

/**
 * Model class used in {@link ClassificationServlet}
 * 
 * @author Marian-Andrei RIZOIU
 * 
 */
public class ClassificationModel {
	
	private static Logger logger = Logger.getLogger(ClassificationModel.class);

	public ClassificationModel() {
		this.algorithm = Config.getClassificationAlgorithm();
		this.language = Config.getClassificationLanguage();
		this.measure = Config.getClassificationMeasure();
		this.numClusters = Config.getClassificationNumClusters();
		this.updateTime = Config.getClassificationUpdateTime();
		this.minWords = Config.getClassificationMinWords();
		this.worker = new Classification();
		this.frenchDiscussions = new ArrayList<DiscussionModel>();
		this.englishDiscussions = new ArrayList<DiscussionModel>();
		this.shownDiscussionLanguage = Config.getClassificationDiscussionListLanguage();
		
		// populate the two Discussion lists (French and English)
		Session mySession = Persistence.getSessionFactory().openSession();
		DiscussionDao dao = new DiscussionDao(mySession);
		this.setFrenchDiscussions(dao.getAllFiltered(null ,null, null, Config.LANGUAGE_FRENCH));
		this.setEnglishDiscussions(dao.getAllFiltered(null, null, null, Config.LANGUAGE_ENGLISH));
		mySession.close();
		
		logger.info("ClassificationServlet initialized...");

		// the work is enabled, we should start the classifier
		// with the settings saved in the config file
		if ( Config.getClassificationEnabled() ) {
			logger.info("Enabled state loaded! Re-starting classification...");
			this.startClassification();
		}

	}

	private Vector<String> errors = new Vector<String>();

	// classification
	private String[] algorithms = { "CKP", "Topical NGrams" };
	private int algorithm;
	private int updateTime;
	private int language;
	private int measure;
	private int numClusters;
	private int minWords;
	private Classification worker;

	// text selection
	private int shownDiscussionLanguage;
	private List<DiscussionModel> frenchDiscussions;
	private List<DiscussionModel> englishDiscussions;

	public String[] getAlgorithms() {
		return algorithms;
	}

	public void setAlgorithm(int algorithm) {
		this.algorithm = algorithm;
	}

	public int getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(int updateTime) {
		this.updateTime = updateTime;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public int getMeasure() {
		return measure;
	}

	public void setMeasure(int measure) {
		this.measure = measure;
	}

	public int getNumClusters() {
		return numClusters;
	}

	public void setNumClusters(int numClusters) {
		this.numClusters = numClusters;
	}

	public int getMinWords() {
		return minWords;
	}

	public void setMinWords(int minWords) {
		this.minWords = minWords;
	}

	public Classification getWorker() {
		return worker;
	}

	public void setWorker(Classification worker) {
		this.worker = worker;
	}

	public boolean isEnabled() {
		return this.worker.isWorkEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.worker.setWorkEnabled(enabled);
	}

	public boolean isWorking() {
		return this.worker.isWorkingNow();
	}

	public List<DiscussionModel> getFrenchDiscussions() {
		return frenchDiscussions;
	}

	public void setFrenchDiscussions(List<Discussion> french) {
		this.frenchDiscussions.clear();
		for (Discussion disc : french )
			this.frenchDiscussions.add(new DiscussionModel(disc.getId(), disc.getName(), disc.getSource(), disc.getTheme(), disc.getUrl()));
		
		Collections.sort(this.frenchDiscussions);
	}

	public List<DiscussionModel> getEnglishDiscussions() {
		return englishDiscussions;
	}

	public void setEnglishDiscussions(List<Discussion> english) {
		this.englishDiscussions.clear(); 
		for (Discussion disc : english )
			this.englishDiscussions.add(new DiscussionModel(disc.getId(), disc.getName(), disc.getSource(), disc.getTheme(), disc.getUrl()));
		
		Collections.sort(this.englishDiscussions);
	}

	public String getTextFilter() {
		return Config.getClassificationTextFilter();
	}

	public void setTextFilter(String textFilter) {
		Config.setClassificationTextFilter(textFilter);
	}

	public boolean isIndividualWordFilter() {
		return (Config.getClassificationOnlyWords().length() != 0);
	}

	public void setIndividualWordFilter(boolean individualWordFilter) {
		Config.setClassificationOnlyWords("oui");	// dummy value
	}

	public int getAlgorithm() {
		return algorithm;
	}

	public void addError(String error) {
		this.errors.add(error);		
	}

	public Vector<String> getErrors() {
		return this.errors;
	}

	public void emptyErrors() {
		this.errors.clear();
	}

	public int getShownDiscussionLanguage() {
		return shownDiscussionLanguage;
	}

	/**
	 * Verifies the validity of the language (French or English) and sets the view language.
	 * 
	 * @param shownDiscussionLanguage
	 */
	public void setShownDiscussionLanguage(int shownDiscussionLanguage) {
		if ( shownDiscussionLanguage == Config.LANGUAGE_FRENCH || shownDiscussionLanguage == Config.LANGUAGE_ENGLISH ) {
			this.shownDiscussionLanguage = shownDiscussionLanguage;
			Config.setClassificationDiscussionListLanguage(shownDiscussionLanguage);
		}
	}
	
	/**
	 * Return the list of discussions to be shown in the classification panel,
	 * accordingly to the language selected for being shown.
	 * 
	 * @return A list of discussions.
	 */
	public List<DiscussionModel> getDiscussionList () {
		if ( shownDiscussionLanguage == Config.LANGUAGE_ENGLISH )
			return getEnglishDiscussions();
		
		return getFrenchDiscussions();
	}
	
	/**
	 * Searches if a discussion gave by the name is selected in the discussion
	 * selection window.
	 * 
	 * @param disc
	 *            the discussion to check
	 * @return true if it was selected
	 */
	public boolean isSelectedDiscussion(String disc) {
		
		for (String discussion : Config.getClassificationSelectedDiscussions())
			if (disc.compareTo(discussion) == 0)
				return true;
		
		return false;
	}
	
	/**
	 * Starts the classification using the parameters saved in the config.
	 * Wrapper for the method that takes the selected discussions, the text
	 * filter and only words as parameters.
	 */
	public void startClassification() {
		this.startClassification(Config.getClassificationSelectedDiscussions(),
				Config.getClassificationTextFilter(),
				Config.getClassificationOnlyWords());
	}
	
	/**
	 * Starts the classification using the user-supplied parameters. It verifies
	 * that the selected list matches with the language and real discussion in
	 * the database.
	 * 
	 * @param selectedDiscussions
	 *            vector of discussion names that need to be classified
	 * @param textFilter
	 *            the textual filter of the discussions
	 * @param onlyWords
	 *            if the filter is supposed to be interpreted as a collection of
	 *            words or an expression.
	 */
	public void startClassification(Vector<String> selectedDiscussions,	String textFilter, String onlyWords) {
		
		List<DiscussionModel> myList;
		// first lets make sure that the selected discussions exist and
		// correspond to the language chosen for classification
		if ( Config.getClassificationLanguage() == Config.LANGUAGE_FRENCH) 
			myList = getFrenchDiscussions();
		else
			myList = getEnglishDiscussions();
		
		for ( String discussion : selectedDiscussions) {
			boolean found = false;
			for ( DiscussionModel disc : myList ) 
				if ( disc.getName().compareTo(discussion) == 0)
					found = true;
			
			if (!found) {
				this.addError("Discussion " + discussion + " does not exist or is not compatible with the selected classification language!");
				this.setEnabled(false);
				
				return;
			}
		}

		// start the classification thread using the specified timeout.
		this.getWorker().startClassification(selectedDiscussions, textFilter, onlyWords);
	}
}
