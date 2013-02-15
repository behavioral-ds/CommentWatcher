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
<%@page import="lab.eric.datafetcher.web.controllers.VisualizeCitationServlet" %>



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
					
						<h2>Visualize Citation</h2>
						<br/>
						<h3>Warning:</h3>
						<p> 
							If you are having errors when loading the applet, try increasing the Java memory limits, 
							as shown <a href="http://www.webmo.net/support/java_memory.html">here</a>.
							Use the following options: "-Xms1024m -Xmx1024m -Xss20m"
						</p>
						
						<form name="appletpopout" method="POST" action="visualize" name="form">
							<input type="hidden" name="<%=VisualizeCitationServlet.DISC_POPOUT_KEY%>" />
							<table>
								<tr>
									<td> <a href="downloadXmlFiles.jsp">Back to list</a> </td>					
								</tr>
							</table>
						</form>

				<APPLET
					archive="Visualizer.jar, jung-3d-2.0.1.jar, jung-graph-impl-2.0.1.jar, mysql-connector-java-5.1.7-bin.jar"
					CODE="lab.eric.visualizer.graph.CitationGraph.class" WIDTH=900
					HEIGHT=700>
					<PARAM name="java_arguments" value="-Xms1024m -Xmx1024m -Xss20m">
					<PARAM name="separate_jvm" value="true">
					<ul class="error">
						<li>Your browser is not configured to show Java Applets.
							Please check your browser and Java instalation.</li>
					</ul>
				</APPLET>




				<!-- ENDING OF PAGE CONTENT -->					
				</div>
			</div>
			<div style="clear: both;">&nbsp;</div>
		</div>
		
		<jsp:include page="layout/footer.jsp" flush="true" />
	
	</body>

</html>
