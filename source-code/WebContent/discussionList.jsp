<!-- #-------------------------------------------------------------------------------
# Copyright (c) 2013 Marian-Andrei RIZOIU.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the GNU Public License v3.0
# which accompanies this distribution, and is available at
# http://www.gnu.org/licenses/gpl.html
# 
# Contributors:
#     Marian-Andrei RIZOIU - initial API and implementation
#------------------------------------------------------------------------------- -->
 <%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@page session="true" %>
<%@page import="lab.eric.datafetcher.web.controllers.DiscussionListServlet" %>
<%@page import="lab.eric.datafetcher.web.models.DiscussionModel" %>

<jsp:useBean id="model" 
			 scope="request" 
			 class="lab.eric.datafetcher.web.models.DiscussionListModel"
			 type="lab.eric.datafetcher.web.models.DiscussionListModel" /> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	
	<jsp:include page="layout/header.jsp" flush="true" />
	
	<script type="text/javascript" src="js/jquery.tablesorter.js"></script>
	<script type="text/javascript" src="js/jquery.tablesorter.pager.js"></script>		
	<script type="text/javascript">
		$(document).ready(function() {
			try {
		    	$("table.stylish").tablesorter({
		    			headers: { 0: {sorter: false}, 6: {sorter: false}, 7: {sorter: false}},
		    			widgets: ['zebra']
		    		}).tablesorterPager({container: $("#pager"), positionFixed: false, size: 20});
			} finally {
		    	$("#startDate").datepicker({ dateFormat: 'yy/mm/dd' });
		    	$("#endDate").datepicker({ dateFormat: 'yy/mm/dd' });		    	
			}
		});
		
		function toggleAllCb() {
			var grouper = $('#allDiscs');
			var grouperChecked = grouper.attr('checked');
			var discCbs = $('table.tablesorter tbody input:checkbox');
			discCbs.each(function(index) {
			    $(this).attr('checked', grouperChecked);
			});			
		}
		
		 function toggleGrouper() {
			 var checkedDiscCbs = $('table.tablesorter tbody input:checkbox:checked');
			 var discCBs = $('table.tablesorter tbody input:checkbox');
			 if (checkedDiscCbs.size() < discCBs.size()) {
				 $('#allDiscs').attr('checked', '');
				 return;
			 }
			 
			 if(checkedDiscCbs.size() == discCBs.size()) {
				 $('#allDiscs').attr('checked', 'checked');
			 }		 
		 }
		 
		 function preDeletion() {
			 if (!confirm('Are you sure that you want to delete all these discussions?')) {
				 return false;
			 }
		 	var checkedDiscCbs = $('table.tablesorter tbody input:checkbox:checked');
			var result = ""; 
			checkedDiscCbs.each(function(index) {
				result = result + $(this).val();
				if (index != (checkedDiscCbs.size() - 1)) {
					result = result + ";";	
				}
			});
			$('#to_delete').val(result);
			return true;
		 }
	</script>
	
	<body>
	
		<jsp:include page="layout/menu.jsp" flush="true" />
		
		<div id="page">
			<div id="content">
				<div class="post">
					<!-- BEGINNING OF PAGE CONTENT -->
					
					<h2>List of Discussions</h2>					
					
					<form action="discussionList" method="get">
						<div class="create_disc">							
							<a href="createDiscussion">Create discussion</a>							
						</div>
					
						<fieldset class="filter">
							<legend>Discussion filters</legend>
							
							Name:
							<input name="<%= DiscussionListServlet.NAME_FILTER_KEY %>" 
								   type="text"
								   size="15"
								   value="<%= model.getNameFilter() %>" />&nbsp;
							
							Source:
							<input name="<%= DiscussionListServlet.SOURCE_FILTER_KEY %>" 
								   type="text"
								   size="15"
								   value="<%= model.getSourceFilter() %>" />&nbsp;
							
							Theme:
							<input name="<%= DiscussionListServlet.THEME_FILTER_KEY %>" 
								   type="text"
								   size="15"
								   value="<%= model.getThemeFilter() %>" />&nbsp;
							
							Start:
							<input id="startDate"
								   name="<%= DiscussionListServlet.START_FILTER_KEY %>" 
								   type="text"
								   size="6"
								   value="<%= model.getStartDate() != null 
								   			? new java.text.SimpleDateFormat("yyyy/MM/dd").format(model.getStartDate())
								   			: "" %>" />&nbsp;
								   
							End:
							<input id="endDate"
								   name="<%= DiscussionListServlet.END_FILTER_KEY %>" 
								   type="text"								   
								   size="6"
								   value="<%= model.getEndDate() != null 
								   			? new java.text.SimpleDateFormat("yyyy/MM/dd").format(model.getEndDate())
								   			: "" %>" />&nbsp;
							
							Only empty:
							<input name="<%= DiscussionListServlet.EMPTY_FILTER_KEY %>"
								   type="checkbox"
								   <%= model.isOnlyEmpty() ? "checked=\"checked\"" : "" %>" />
								   
							<input type="submit" value="Find" name="Find">&nbsp;&nbsp;
							
							<% if(model.getStatus() != null) { %>
								<span class="status"><%= model.getStatus().getMessage() %></span>
							<% } %>
						</fieldset>						
					
						<table class="tablesorter stylish">
							<thead>
								<tr>
									<th width="20px"><input type="checkbox" id="allDiscs" onclick="toggleAllCb()"/></th>
									<th width="200px">Name</th>
									<th width="150px">Source</th>
									<th width="">Theme</th>
									<th width="80px">Posted</th>
									<th width="80px">Comments</th>
									<th width="80px">URL</th>
									<th width="80px">Details</th>
								</tr>
							</thead>
							<tbody>
								<% for (DiscussionModel discModel : model.getDiscussions()) { %>
								<tr>
									<td>
										<input type="checkbox" name="discussion" value="<%= discModel.getId() %>" onclick="toggleGrouper()"/>
									</td>
									<td><%= discModel.getName() %></td>
									<td><%= discModel.getSource() %></td>
									<td><%= discModel.getTheme() %></td>
									<td><%= discModel.getArticlePostedDate() != null 
										?(new java.text.SimpleDateFormat("yyyy/MM/dd")).format(discModel.getArticlePostedDate()) 
										: "unknown" %>
									</td>
									<td><%= discModel.getCommentsFetched() %></td>
									<td class="link"><a target="_blank" href="<%= discModel.getUrl() %>">link</a></td>
									<td class="link">									
										<a href="discussion?id=<%= discModel.getId() %>">details</a>&nbsp;									
									</td>								
								</tr>
								<% } %>
							</tbody>							
						</table>						
					</form>
					
					<div id="pager">
						<form action="discussionList" method="post" onsubmit="return preDeletion()">
							<img src="images/first.png" class="first">
							<img src="images/prev.png" class="prev">
							<input type="text" class="pagedisplay">
							<img src="images/next.png" class="next">
							<img src="images/last.png" class="last">
							
							<select class="pagesize">
								<option selected="selected" value="20">20</option>
								<option value="50">50</option>
								<option value="100">100</option>
							</select>
							
							<input type="hidden" name="<%= DiscussionListServlet.NAME_FILTER_KEY %>"  
								   value="<%= model.getNameFilter() %>" />
							<input type="hidden" name="<%= DiscussionListServlet.SOURCE_FILTER_KEY %>"
								   value="<%= model.getSourceFilter() %>" />
							<input type="hidden" name="<%= DiscussionListServlet.THEME_FILTER_KEY %>" 
								   value="<%= model.getThemeFilter() %>" />
							<input type="hidden" name="<%= DiscussionListServlet.START_FILTER_KEY %>" 
								   value="<%= model.getStartDate() != null 
								   		? new java.text.SimpleDateFormat("yyyy/MM/dd").format(model.getStartDate())
								   		: "" %>" />
							<input type="hidden" name="<%= DiscussionListServlet.END_FILTER_KEY %>" 
								   value="<%= model.getEndDate() != null 
								   		? new java.text.SimpleDateFormat("yyyy/MM/dd").format(model.getEndDate())
								   		: "" %>" />
							<input type="hidden" name="<%= DiscussionListServlet.EMPTY_FILTER_KEY %>" 
								   value="<%= model.isOnlyEmpty() ? "on" : "" %>" />
							<input type="hidden" id="to_delete" name="to_delete" /> 
							
							<input type="submit" value="delete selection">					
						</form>
					</div>					
										
					<!-- ENDING OF PAGE CONTENT -->					
				</div>
			</div>
			<div style="clear: both;">&nbsp;</div>
		</div>
		
		<jsp:include page="layout/footer.jsp" flush="true" />
	
	</body>

</html>
