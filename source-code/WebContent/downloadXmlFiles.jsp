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
<%@ page import="lab.eric.datafetcher.utils.*"%>
<%@ page import="lab.eric.datafetcher.web.models.XmlFileModel"%>
<%@ page import="java.util.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	if (request.getParameter("sup") != null) {
		XmlFileModel.deleteXmlFile(request.getParameter("sup"));
	}

	Vector<XmlFileModel> maListe = XmlFileModel.listXMLFilesinFolder();
%>

<html>

<jsp:include page="layout/header.jsp" flush="true" />

<body>

	<jsp:include page="layout/menu.jsp" flush="true" />

	<div id="page">
		<div id="content">
			<div class="post">
				<h2 class="title">
					<a>XML file list</a>
				</h2>
				<div class="entry">
					<form action="" method="get" enctype="application/x-www-form-urlencoded">
						<table id="results">

							<% for (XmlFileModel obj : maListe) { %>
							<tr>
								
								
								<td>
									<ul class="puces">
										<li><%= obj.getFileName() %></li>
									</ul>
								</td>
								<td width="20"></td>
								<td>
									<a href="downloadXmlFiles.jsp?sup=<%=obj.getCompleteURL()%>" class="button"> 
										<span class="delete"> Delete </span> 
									</a>
								</td>
								<td>
									<a href="downloadFile.jsp?file=<%=obj.getCompleteURL()%>" class="button"> 
										<span class="download"> Download </span> 
									</a>
								</td>
								<td>
									<a href="visualize?file=<%=obj.getCompleteURL()%>" class="button"> 
										<span class="lens"> Visualize </span> 
									</a>
								</td>
								
								<td>
									<a href="citation?file=<%=obj.getCompleteURL()%>" class="button2" >
									    <span class="lens"> Citation </span> 
									</a>
								</td>
								
							</tr>
							<%}%>
						</table>

						<div id="pageNavPosition"></div>
					</form>

					<script type="text/javascript">
					<!--
						var pager = new Pager('results', 5);
						pager.init();
						pager.showPageNav('pager', 'pageNavPosition');
						pager.showPage(1);
					//-->
					</script>
				</div>
			</div>
		</div>
		<div style="clear: both;">&nbsp;</div>
	</div>
	<!-- end #page -->

	<jsp:include page="layout/footer.jsp" flush="true" />

</body>
</html>
