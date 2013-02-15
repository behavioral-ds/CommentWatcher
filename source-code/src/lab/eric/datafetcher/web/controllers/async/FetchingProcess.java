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

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import lab.eric.datafetcher.entities.Discussion;
import lab.eric.datafetcher.parsers.TransformParser;
import lab.eric.datafetcher.persistence.DiscussionDao;
import lab.eric.datafetcher.persistence.Persistence;

/**
 * Encapsulates the whole parsing process including
 * downloading page, parsing and storing into the DB.
 * This is to separate business-logic and presentation
 * and to make richer presentation.
 * This is an observable for FetchingProcessObserver.
 * At the same time it observes TransformParser.
 * 
 * @author Nikolay Anokhin
 */
public class FetchingProcess extends Observable implements Observer, Runnable {
	
	private static Logger logger = Logger.getLogger(FetchingProcess.class);
	
	private FetchingProcessStatus status = FetchingProcessStatus.IDLE;
	
	private TransformParser parser;
	
	private String commentsUrl;
	
	public void initialize(int discussionId, String commentsUrl) {
		this.commentsUrl = commentsUrl;		
		
		Session session = Persistence.getSessionFactory().openSession();
		
		DiscussionDao dao = new DiscussionDao(session);
		Discussion discussion = dao.getById(discussionId);
		
		Transaction transaction = session.beginTransaction();
		try {
			dao.cleanDiscussion(discussion);
			transaction.commit();
		} catch(Exception ex) {
			transaction.rollback();
			logger.error("Unable to clean the discussion with id " + discussionId, ex);
		}
		
		session.close();

		parser = new TransformParser(discussion);
		parser.addObserver(this);					
	}
	
	public void run() {
		setStatus(FetchingProcessStatus.PROGRESS, "Fetching process started.");	
		
		parser.parseSilent(commentsUrl);
		
		if (status == FetchingProcessStatus.ERROR) {
			return;
		}
		
		Discussion discussion = parser.getDiscussion();
		if (discussion.getArticle() == null || discussion.getArticle().getTitle() == null) {			
			setStatus(FetchingProcessStatus.ERROR, "No article fetched.");
			return;
		}
			
		Session session = Persistence.getSessionFactory().openSession();			
		Transaction transaction = session.beginTransaction();
		try {
			DiscussionDao dao = new DiscussionDao(session);			
			
			dao.persistWithContent(discussion);				
								
			transaction.commit();
			
			setStatus(FetchingProcessStatus.SUCCESS, "Discussion successfully fetched.");
			logger.debug("Discussion with id " + discussion.getId() + " is successfully saved.");
		} catch (Exception ex) {
			transaction.rollback();			
			setStatus(FetchingProcessStatus.ERROR, "An error occured during saving. See error log.");
			logger.error("An exception occured when saving the discussion.", ex);
		}
			
		session.close();		
	}
	
	private void setStatus(FetchingProcessStatus status, String message) {
		this.status = status;
		this.status.setMessage(message);		
		this.setChanged();
		this.notifyObservers(status);
	}

	@Override
	public void update(Observable o, Object arg) {
		int pageNumber = (Integer) arg;
				
		if (pageNumber == TransformParser.SUCCESS_RESULT_CODE) {
			setStatus(FetchingProcessStatus.PROGRESS, "Saving discussion...");
		} else if (pageNumber == TransformParser.ERROR_RESULT_CODE) {
			setStatus(FetchingProcessStatus.ERROR, "An error occured during fetching. See error log.");			
		} else {		
			setStatus(FetchingProcessStatus.PROGRESS, "Fetching page " + pageNumber + "...");
		}
	}
}
