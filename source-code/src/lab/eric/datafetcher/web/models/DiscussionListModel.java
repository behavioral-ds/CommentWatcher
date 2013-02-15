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

import java.util.Date;
import java.util.List;

/**
 * Model class used in discussionList.jsp.
 * 
 * @author Nikolay Anokhin
 */
public class DiscussionListModel {	

	private String nameFilter = "";
	
	private String sourceFilter = "";
	
	private String themeFilter = "";
	
	private Date startDate = null;
	
	private Date endDate = null;
	
	private boolean onlyEmpty = false;
	
	private List<DiscussionModel> discussions;
	
	private DiscussionModel.Status status;

	public void setSourceFilter(String sourceFilter) {
		this.sourceFilter = sourceFilter;
	}

	public String getSourceFilter() {
		return sourceFilter;
	}

	public void setThemeFilter(String themeFilter) {
		this.themeFilter = themeFilter;
	}

	public String getThemeFilter() {
		return themeFilter;
	}
	
	public void setDiscussions(List<DiscussionModel> discussions) {
		this.discussions = discussions;
	}

	public List<DiscussionModel> getDiscussions() {
		return discussions;
	}

	public void setStatus(DiscussionModel.Status status) {
		this.status = status;
	}

	public DiscussionModel.Status getStatus() {
		return status;
	}

	public void setNameFilter(String nameFilter) {
		this.nameFilter = nameFilter;
	}

	public String getNameFilter() {
		return nameFilter;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setOnlyEmpty(boolean onlyEmpty) {
		this.onlyEmpty = onlyEmpty;
	}

	public boolean isOnlyEmpty() {
		return onlyEmpty;
	}
}
