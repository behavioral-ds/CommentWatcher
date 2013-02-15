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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="lab.eric.datafetcher.web.controllers.ClassificationServlet"%>
<%@ page import="lab.eric.datafetcher.persistence.Persistence"%>
<%@ page import="lab.eric.datafetcher.persistence.DiscussionDao"%>
<%@ page import="lab.eric.datafetcher.entities.Discussion"%>
<%@ page import="org.hibernate.Session"%>
<%@ page import="lab.eric.datafetcher.web.models.ClassificationModel"%>
<%@ page import="lab.eric.datafetcher.web.models.DiscussionModel"%>
<%@ page import="lab.eric.datafetcher.utils.config.Config"%>
<%@ page session="true"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<jsp:useBean id="model" 
			 scope="request" 
			 class="lab.eric.datafetcher.web.models.ClassificationModel"
			 type="lab.eric.datafetcher.web.models.ClassificationModel" /> 

<html>

<SCRIPT LANGUAGE="JavaScript"> 
function exchangeLists(radio)
{
	for ( var i = 0; i < radio.length; i++) {
		if (radio[i].checked) {
			document.body.innerHTML += '<form id="dynForm" action="classification" method="post"><input type="hidden" name="languageradio" value="a"><input type="hidden" name="selectedradio" value="' + radio[i].value + '"></form>';
			document.getElementById("dynForm").submit();
		}
	}
}
</SCRIPT>

<jsp:include page="layout/header.jsp" flush="true" />

<body>

	<jsp:include page="layout/menu.jsp" flush="true" />

	<div id="page">
		<div id="content1">

					<form method="POST" action="classification" name="form">
							
						<% if (model.getErrors().size() > 0) { %>
							<div style="background-color:white;">

								<ul class="error">
									<% for (String error : model.getErrors()) { %>
											<li><%= error %></li>
									<% } %>
								</ul>
							</div>
						<% } 
						model.emptyErrors();
						%>
						
						<div class="post1">

							<h2 class="title">
								<a>Discussions list</a>
							</h2>
							
							Source::name (theme)
							
							<br/>
							<br/>
							
								<% if ( model.getShownDiscussionLanguage() == Config.LANGUAGE_FRENCH )  { %>
								<table>
									<tr>
										<td>French <input type="radio" value="<%= Config.LANGUAGE_FRENCH %>" name="groupe" onclick=exchangeLists(this.form.groupe);; checked />
										<td>English <input type="radio" value="<%= Config.LANGUAGE_ENGLISH %>" name="groupe" onclick=exchangeLists(this.form.groupe);; />
									</tr>
								</table>
								<% } else {%>
								<table>
									<tr>
										<td>French <input type="radio" value="<%= Config.LANGUAGE_FRENCH %>" name="groupe" onclick=exchangeLists(this.form.groupe);; />
										<td>English <input type="radio" value="<%= Config.LANGUAGE_ENGLISH %>" name="groupe" onclick=exchangeLists(this.form.groupe);; checked />
									</tr>
								</table>
								<% } %>

								<div id="discussionsList" style="display: block" >
									<table>
									
									<% for (DiscussionModel discussion : model.getDiscussionList()) { %>
										<tr>
											<td>
												<% if (model.isSelectedDiscussion(discussion.getName())) { %>
													<input type="checkbox" name="<%=ClassificationServlet.CLASS_SELECTED_DISC_KEY%>" value="<%=discussion.getId()%>" checked>
												<% } else { %>
													<input type="checkbox" name="<%=ClassificationServlet.CLASS_SELECTED_DISC_KEY%>" value="<%=discussion.getId()%>">
												<% }%>
											</td>
											<td>  
												<%=discussion.getSource()%>::<%=discussion.getName()%> ( <%=discussion.getTheme()%> )
											</td>
										</tr>
									<% } %>
									
									</table>
								</div>
									
						</div>
						<!-- End of box div -->
						
						<div class="post2">
							<h2 class="title">
								<a>Configuration of classification</a>
							</h2>

							<fieldset>
								<LEGEND>Filter the selected discussions by an expression or list of words</LEGEND>
								
								<% if (model.isEnabled() || model.isWorking()) {	%>
									<SCRIPT LANGUAGE="JavaScript"> setTimeout("location='classification'", 5000);; 
									</SCRIPT>
									<% if (model.isWorking()) {	%>
										<img align="right" src="./images/working.png" />
									<% }
								} %>
								
								<table>
									<tr>
										<td>Filter by expression:</td>
										<td>
											<input id="search-text" name="<%=ClassificationServlet.CLASS_FILTER_TEXT_KEY%>" value="<%=model.getTextFilter()%>" />
										</td>
									</tr>
									<tr>
										<td colspan="2">
											<% if (model.isIndividualWordFilter()) { %>
												<input value="oui" type="checkbox" name="<%=ClassificationServlet.CLASS_FILTER_TYPE_KEY%>" checked>
											<% } else { %>
												<input value="oui" type="checkbox" name="<%=ClassificationServlet.CLASS_FILTER_TYPE_KEY%>" >
											<% }%>
											Search for texts that contain at least one of the words, rather than the whole expression.
										</td>
									</tr>
								</table>
							</fieldset>

							<br /> 
							<br />

							<fieldset>
							<LEGEND>Parameters of the classification algorithm</LEGEND>

							<table>
							   <tr>
								<td>Classification algorithm :</td>
								<td><select name="<%=ClassificationServlet.CLASS_ALGO_KEY%>" id="search-text2">
										<% int i = 0; 
										for (String algo:model.getAlgorithms()) { 
											if (model.getAlgorithm() == i) {%>
												<option value="<%=i%>"  selected ><%= algo %></option>
											<% } else { %>
												<option value="<%=i%>" ><%= algo %></option>
											<% } 
											i++;
										} %>
								</select></td>
							</tr>
							<tr>
								<td>Number of groups :</td>
								<td><input type="text" name="<%=ClassificationServlet.CLASS_NUM_CLASSES_KEY%>" id="search-text" size="4" value="<%=model.getNumClusters()%>" />
								</td>
							</tr>
							<tr>
								<td>Update time (min):</td>
								<td><input type="text" name="<%=ClassificationServlet.CLASS_UPDATE_TIME_KEY%>" id="search-text" size="4" value="<%=model.getUpdateTime()%>" />
								</td>
							</tr>
							<tr>
								<td>Language</td>
								<td><select name="<%=ClassificationServlet.CLASS_LANGUAGE_KEY%>" id="search-text2">
										<% if (model.getLanguage() == 1) { %>
												<option value="1"  selected >French</option>
												<option value="2"  >English</option>
										<% } else { %>
												<option value="1"  >French</option>
												<option value="2"  selected >English</option>
										<% } %>
								</select>
								</td>
							</tr>
							<tr>
								<td>Term Weighting Scheme :</td>
								<% if (model.getAlgorithm() == 0) {%>
								<td><select name="<%=ClassificationServlet.CLASS_MEASURE_KEY%>" id="search-text2">
										<% if (model.getMeasure() == 0) { %>
												<option value="0"  selected >P/A</option>
												<option value="1"   >TF</option>
												<option value="3"   >TF.IDF</option>
										<% } else if (model.getMeasure() == 1) { %>
												<option value="0"  >P/A</option>
												<option value="1"  selected >TF</option>
												<option value="3"   >TF.IDF</option>
										<%	} else if (model.getMeasure() == 3) { %>
												<option value="0"  >P/A</option>
												<option value="1"  >TF</option>
												<option value="3"  selected >TF.IDF</option>
										<% } %>
								</select></td>
								<% } else { %>
								<td><select name="<%=ClassificationServlet.CLASS_MEASURE_KEY%>" id="search-text2" disabled>
								</select></td>
								
								<td>
										<ul class="error"> 
											<li> The option is not compatible with the algorithm!</li>
										</ul>
								</td>
								<% } %>
							</tr>
							<tr>
								<td>Drop text having less words than :</td>
								<td>
								<% if (model.getAlgorithm() == 0) {%>
									<input type="text" name="<%=ClassificationServlet.CLASS_MIN_WORDS_KEY%>" id="search-text" size="4" value=<%=model.getMinWords()%> />
								<% } else { %>
									<input type="text" name="<%=ClassificationServlet.CLASS_MIN_WORDS_KEY%>" id="search-text" size="4" value=<%=model.getMinWords()%> disabled />
									</td>
									
									<td>
										<ul class="error"> 
											<li> The option is not compatible with the algorithm!</li>
										</ul>
								<% } %>
								</td>
							</tr>
						</table>
						</fieldset>

						<br /> 

						<!-- Now put the buttons, according to the state of the classifier. -->
						<table>
							<tr>
								<td> <input type="submit" id="search-submit1" value="Modify"  name="<%=ClassificationServlet.CLASS_PARAMETERS_MODIFY_KEY%>"/> </td>
								<% if (!model.isEnabled()) { %>
										<td> 
											<input type="submit" name="<%=ClassificationServlet.CLASS_PARAMETERS_START_KEY%>" value="Start classification" id="search-submit1" />
										</td>
										<td>
											<input type="submit" id="search-submit1" value="   Stop   "  name="<%=ClassificationServlet.CLASS_PARAMETERS_STOP_KEY%>" disabled/> 
										</td>
								<% } else { %>
										<td> 
											<input type="submit" name="<%=ClassificationServlet.CLASS_PARAMETERS_START_KEY%>" value="Start classification" id="search-submit1" disabled/>
										</td>
										<td>
											<input type="submit" id="search-submit1" value="   Stop   "  name="<%=ClassificationServlet.CLASS_PARAMETERS_STOP_KEY%>" /> 
										</td>
								<% } %>
							</tr>
						</table>
						</div>
					</form>
		</div>
		<!-- end #content1 -->
		
		<div style="clear: both;">&nbsp;</div>
	</div>
	<!-- end #page -->

	<jsp:include page="layout/footer.jsp" flush="true" />
</html>
