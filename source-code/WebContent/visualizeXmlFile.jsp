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
<%@page import="lab.eric.datafetcher.web.controllers.VisualizeXmlServlet" %>

<jsp:useBean id="model" 
			 scope="request" 
			 class="lab.eric.datafetcher.web.models.XmlFileModel"
			 type="lab.eric.datafetcher.web.models.XmlFileModel" /> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	
	<jsp:include page="layout/header.jsp" flush="true" />

	<script language="JavaScript" type="text/javascript">
		function toggleApplet()
		{
		  document.appletpopout.popout.value = "checked" ;
		  document.appletpopout.submit() ;
		}
	</script> 
	
	<body>	
	
		<jsp:include page="layout/menu.jsp" flush="true" />
		
		<div id="page">
			<div id="content">
				<div class="post">
					<!-- BEGINNING OF PAGE CONTENT -->
					
						<h2>Visualize XML FILE</h2>
						<br/>
						<h3>Warning:</h3>
						<p> 
							If you are having errors when loading the applet, try increasing the Java memory limits, 
							as shown <a href="http://www.webmo.net/support/java_memory.html">here</a>.
							Use the following options: "-Xms1024m -Xmx1024m -Xss20m"
						</p>
						
							<% if (model.getErrors().size() > 0) { %>
								<ul class="error">
									<% for (String error : model.getErrors()) { %>
											<li><%= error %></li>
									<% } %>
								</ul>
							<% } %>
							
							<form name="appletpopout" method="POST" action="visualize" name="form">
								<input type="hidden" name="<%=VisualizeXmlServlet.DISC_POPOUT_KEY%>" />
								<table>
									<tr>
										<td> <a href="downloadXmlFiles.jsp">Back to list</a> </td>
										<% if (model.isPopedOut() ) { %>
										<td> <a href="javascript:toggleApplet();;" >Embedded Applet</a> </td>
										<% }  else {%>
										<td> <a href="javascript:toggleApplet();;" >PopOut Applet</a> </td>
										<% } %>
									</tr>
								</table>
							</form>
							
							<% if (model.isPopedOut() ) { %>
								<APPLET 
									ARCHIVE="Visualizer.jar"
									CODE=lab.eric.visualizer.view.AppletPopUp.class WIDTH=1 HEIGHT=1>
									<PARAM name="java_arguments" value="-Xms1024m -Xmx1024m -Xss20m">
									<PARAM name="separate_jvm" value="true">
									<ul class="error">
										<li>Your browser is not configured to show Java Applets.
											Please check your browser and Java instalation.</li>
									</ul>
								</APPLET>
							<%
								} else {
							%>
								<APPLET
									ARCHIVE="Visualizer.jar"
									CODE=lab.eric.visualizer.view.MainAppletWindow.class WIDTH=900
									HEIGHT=700>
									<PARAM name="java_arguments" value="-Xms1024m -Xmx1024m -Xss20m">
									<PARAM name="separate_jvm" value="true">
									<ul class="error">
										<li>Your browser is not configured to show Java Applets.
											Please check your browser and Java instalation.</li>
									</ul>
								</APPLET>
							<%
								}
							%>
						
					<!-- ENDING OF PAGE CONTENT -->					
				</div>
			</div>
			<div style="clear: both;">&nbsp;</div>
		</div>
		
		<jsp:include page="layout/footer.jsp" flush="true" />
	
	</body>

</html>
