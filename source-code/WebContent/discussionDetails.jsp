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
<%@page import="lab.eric.datafetcher.web.controllers.DiscussionDetailsServlet" %>
<%@page import="lab.eric.datafetcher.web.controllers.ControllerServlet" %>
<%@page import="lab.eric.datafetcher.web.controllers.async.FetchingProcessServlet" %>
<%@page import="lab.eric.datafetcher.web.models.FetchingProcessModel" %>
<%@page import="lab.eric.datafetcher.web.controllers.async.FetchingProcessStatus" %>

<jsp:useBean id="model" 
			 scope="request" 
			 class="lab.eric.datafetcher.web.models.DiscussionModel"
			 type="lab.eric.datafetcher.web.models.DiscussionModel" /> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	
	<jsp:include page="layout/header.jsp" flush="true" />
	
	<script type="text/javascript">
		function startFetch() {
			if (!confirm("Do you really want to fetch this discussion?\nPreviously fetched data may be lost...")) {
				return false;
			}
			$.ajax({
				url: "fetchDiscussion",
				type: "POST",
				dataType: "json",
				data: ({<%= FetchingProcessServlet.ACTION_KEY %> : "<%= FetchingProcessServlet.START_ACTION %>",
						<%= ControllerServlet.DISC_ID_KEY %> : "<%= model.getId() %>"}),
				success: function (data, textStatus) {
					if (data.status == "<%= FetchingProcessStatus.PROGRESS.getCode() %>") {
						$("#hideFetchButton").hide();
						$("#fetchArea").show();
						monitorFetch();
					} else if (data.status == "<%= FetchingProcessStatus.ERROR.getCode() %>") {
						$("#fetchProgress").text(data.message);
					}
			    }
			});
		}
		
		function monitorFetch() {
			$.ajax({
				url: "fetchDiscussion",
				type: "POST",
				dataType: "json",
				data: ({<%= FetchingProcessServlet.ACTION_KEY %> : "<%= FetchingProcessServlet.MONITOR_ACTION %>",
						<%= ControllerServlet.DISC_ID_KEY %> : "<%= model.getId() %>"}),
				success: function (data, textStatus) {
					if (data.status == "<%= FetchingProcessStatus.PROGRESS.getCode() %>") {
					    setTimeout(monitorFetch, 250);
					} else {						
						$(".rf-img").show();
						$("#hideFetchButton").show();
						setTimeout(summarizeFetch, 2000);
					}
					$("#fetchProgress").text(data.message);
			    }
			});
		}
		
		function summarizeFetch() {
			$.ajax({
				url: "fetchDiscussion",
				type: "POST",
				dataType: "json",
				data: ({<%= FetchingProcessServlet.ACTION_KEY %> : "<%= FetchingProcessServlet.RESULT_ACTION %>",
						<%= ControllerServlet.DISC_ID_KEY %> : "<%= model.getId() %>"}),
				success: function (data, textStatus) {					
					$("#fecthDate .updatable").text(data.date);
					$("#atitle .updatable").text(data.atitle);
					$("#comments .updatable").text(data.comments);
					$(".rf-img").hide();
			    }
			});
		}
		
		function hideFetchArea() {			
			$("#fetchArea").hide('slow');
		}
		
		function easter (evt) {
			$("div#logo h1 a").fadeOut('slow', function() {
				$("div#logo h1").append("<span id='easter'>FECI AUOD POTUI, FACIANT MELIORA POTENTES</span>");	
			});			
		}
	</script>	
	
	<body>	
	
		<jsp:include page="layout/menu.jsp" flush="true" />
		
		<div id="page">
			<div id="content">
				<div class="post">
					<!-- BEGINNING OF PAGE CONTENT -->
					
					<h2>Discussion</h2>
					
					<form action="discussion" method="post">
						<fieldset class="filter">
							<legend>Discussion details</legend>							
							
							<input type="hidden" 
								   name="<%= ControllerServlet.DISC_ID_KEY %>"
								   value="<%= model.getId() %>" />
								   
							<% if (model.getErrors().size() > 0) { %>
								<ul class="error">
									<% for (String error : model.getErrors()) { %>
											<li><%= error %></li>
									<% } %>
								</ul>
							<% } %>
							
							<% if (model.getId() != -1) {%>
								<% if (model.getStatus() != null) { %>
									<ul class="status"><li><%= model.getStatus().getMessage() %></li></ul>
								<% } %>							
								<table class="details">
									<tbody>
										<tr>
											<td>Name</td>
											<td>
												<input type="text" 
													   name="<%= DiscussionDetailsServlet.DISC_NAME_KEY %>" 
													   value="<%= model.getName() %>" />
											</td>
											<td rowspan="2">Previous fetch date</td>
											<td id="fecthDate" rowspan="2">
												<span class="updatable"><%= model.getFetchDate() %></span>
												<span class="rf-img"><img alt="loading" src="images/ajax-loader.gif" /></span>
											</td>																						
										</tr>
										<tr>
											<td>Source</td>
											<td>
												<input type="text" 
													   name="<%= DiscussionDetailsServlet.DISC_SOURCE_KEY %>" 
													   value="<%= model.getSource() %>" />
											</td>											
										</tr>
										<tr>
											<td>Theme</td>
											<td>
												<input type="text" 
													   name="<%= DiscussionDetailsServlet.DISC_THEME_KEY %>" 
													   value="<%= model.getTheme() %>" />
											</td>
											<td rowspan="2">Article title</td>
											<td id="atitle" rowspan="2">
												<span class="updatable"><%= model.getArticleTitle() %></span>
												<span class="rf-img"><img alt="loading" src="images/ajax-loader.gif" /></span>
											</td>
										</tr>
										<tr>
											<td>URL</td>
											<td><a target="_blank" href="<%= model.getUrl() %>">link</a></td>											
										</tr>
										<tr>
											<td>Language</td>
											<td><%= model.getLang() == 1 ? "French" : "English" %></td>
											<td rowspan="2">Comments fetched</td>
											<td id="comments" id="fecthDate" rowspan="2">
												<span class="updatable"><%= model.getCommentsFetched() %></span>
												<span class="rf-img"><img alt="loading" src="images/ajax-loader.gif" /></span>
											</td>
										</tr>
										<tr>
											<td>Type</td>
											<td><%= model.getType() == "feed" ? "Rss Feed" : "Discussion" %></td>
										</tr>
									</tbody>
								</table>
								<input name="fetch" type="button" value="Fetch" onclick="startFetch()"/>
								
								<input name="delete" type="submit" value="Delete" 
									   onclick="return confirm('Are you sure that you want to delete this discussion?');"/>
								<input name="update" type="submit" value="Update"/>
								<a href="discussionList">Back to list</a>
							<% } %>							
						</fieldset>
					</form>
					
					<fieldset id="fetchArea">
						<legend>Fetching progress</legend>
						<span id="fetchProgress"></span>
						<a id="hideFetchButton" onclick="hideFetchArea()" ondblclick="easter(event)" href="#">hide</a>				
					</fieldset>
										
					<!-- ENDING OF PAGE CONTENT -->					
				</div>
			</div>
			<div style="clear: both;">&nbsp;</div>
		</div>
		
		<jsp:include page="layout/footer.jsp" flush="true" />
	
	</body>

</html>
