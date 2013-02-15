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
<%@page import="lab.eric.datafetcher.web.controllers.CreateDiscussionServlet" %>

<jsp:useBean id="model" 
			 scope="request" 
			 class="lab.eric.datafetcher.web.models.DiscussionModel"
			 type="lab.eric.datafetcher.web.models.DiscussionModel" /> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	
	<jsp:include page="layout/header.jsp" flush="true" />
	
	<script type="text/javascript">
		function extractSource() {
			if ($("#source").val() != ""){
				return;
			}
			var url = $("#url").val();			
			var myRegexp = /(\w+)\.(\w+)\.[(fr)(com)]/i;
			var match = myRegexp.exec(url);
			$("#source").val(match[2]);
		}
	</script>
	
	<body>
	
		<jsp:include page="layout/menu.jsp" flush="true" />
		
		<div id="page">
			<div id="content">
				<div class="post">
					<!-- BEGINNING OF PAGE CONTENT -->
					
					<h2>Create new discussion</h2>
					
					<form action="createDiscussion" method="post">
						<fieldset class="filter">
							
							<% if (model.getErrors().size() > 0) { %>
								<ul class="error">
									<% for (String error : model.getErrors()) { %>
											<li><%= error %></li>
									<% } %>
								</ul>
							<% } %>
						
							<table class="details">
								<tbody>
									<tr>
									<td>Name</td>
										<td>
											<input type="text" 
												   name="<%= CreateDiscussionServlet.DISC_NAME_KEY %>" 
												   value="<%= model.getName() %>" />
										</td>
									</tr>									
									<tr>
										<td>URL</td>
										<td>
											<input type="text" 
												   id = "url" onchange="extractSource()" onblur="extractSource()"
												   name="<%= CreateDiscussionServlet.DISC_URL_KEY %>" 
												   value="<%= model.getUrl() %>" />
										</td>
									</tr>
									<tr>
									<td>Source</td>
										<td>
											<input type="text"
												   id = "source"
												   name="<%= CreateDiscussionServlet.DISC_SOURCE_KEY %>" 
												   value="<%= model.getSource() %>" />
										</td>
									</tr>
									<tr>
										<td>Theme</td>
										<td>
											<input type="text" 
												   name="<%= CreateDiscussionServlet.DISC_THEME_KEY %>" 
												   value="<%= model.getTheme() %>" />
										</td>
									</tr>									
									<tr>
										<td>Language</td>
										<td>
											<select name="<%= CreateDiscussionServlet.DISC_LANG_KEY%>">
												<option value="1" selected="selected">French</option>
												<option value="2">English</option>
											</select>											
										</td>
									</tr>
									<tr>
										<td>Type</td>
										<td>
											<select name="<%= CreateDiscussionServlet.DISC_TYPE_KEY%>">
												<option value="feed">RSS Feed</option>
												<option value="discussion" selected="selected">Discussion</option>
											</select>
										</td>
									</tr>
								</tbody>
							</table>
							<input type="submit" value="Create">
							<a href="discussionList">Back to list</a>
						</fieldset>
					</form>
										
					<!-- ENDING OF PAGE CONTENT -->					
				</div>
			</div>
			<div style="clear: both;">&nbsp;</div>
		</div>
		
		<jsp:include page="layout/footer.jsp" flush="true" />
	
	</body>

</html>
