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
<%@page import="lab.eric.datafetcher.web.controllers.BulkFetchServlet" %>
<%@page import="lab.eric.datafetcher.web.controllers.async.FetchingProcessServlet" %>
<%@page import="lab.eric.datafetcher.web.models.FetchingProcessModel" %>
<%@page import="lab.eric.datafetcher.web.controllers.ControllerServlet" %>
<%@page import="lab.eric.datafetcher.web.controllers.async.FetchingProcessStatus" %>

<jsp:useBean id="model" 
			 scope="request" 
			 class="lab.eric.datafetcher.web.models.BulkFetchModel"
			 type="lab.eric.datafetcher.web.models.BulkFetchModel" /> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	
	<jsp:include page="layout/header.jsp" flush="true" />
	<script type="text/javascript" src="js/bingSearch.js">
		<!-- This is an ajax search script -->		
	</script>
	<script type="text/javascript">		
		$(document).ready(function () {			
			$("#bing").chercher({
				appid : "<%= model.getBingAppId() %>"
			});
		});		
		
		function createFetch() {
			var theme = ($('#txtQuery').val());
			$('input.searchResult:checked').each(function() {
				var resId = $(this).val();
				var anchor = $('a#res' + resId);	
				var title = anchor.text();
				var href = anchor.attr('href');
				$('img#loading' + resId).show();
				$('img#success' + resId).hide();
				$('img#fail' + resId).hide();
				$('span#message' + resId).hide();				
				$.ajax({
					type: "POST",
					url: "bulk",
					dataType: "json",
					data: ({theme : theme,
							title: title,
							href: href,
							<%= FetchingProcessServlet.ACTION_KEY %> : "<%= BulkFetchServlet.CREATE_ACTION %>"}),
					success: function(data) {						
						if (data.status == "<%= FetchingProcessStatus.SUCCESS.getCode() %>") {
							$('#disc' + resId).val(data.discId);
							startFetch(resId);
						} else if (data.status == "<%= FetchingProcessStatus.ERROR.getCode() %>") {
							$('img#loading' + resId).hide();
							$('img#fail' + resId).show();
							$('span#message' + resId).text(data.message).show();
						}
					},
					error: function(msg) {
						$('img#loading' + resId).hide();
						$('img#fail' + resId).show();						
						$('span#message' + resId).text('somethig broke...').show();
					}
				});
			});
		}
		
		function startFetch(resId) {
			var discId = $('#disc' + resId).val();			
			$.ajax({
				url: "fetchDiscussion",
				type: "POST",
				dataType: "json",
				data: ({<%= FetchingProcessServlet.ACTION_KEY %> : "<%= FetchingProcessServlet.START_ACTION %>",
						<%= ControllerServlet.DISC_ID_KEY %> : discId}),
				success: function (data, textStatus) {
					if (data.status == "<%= FetchingProcessStatus.PROGRESS.getCode() %>") {
						setTimeout('monitorFetch(' + resId + ')', 250);
					} else if (data.status == "<%= FetchingProcessStatus.ERROR.getCode() %>") {
						$('img#loading' + resId).hide();
						$('img#fail' + resId).show();
						$('span#message' + resId).text(data.message).show();
					}
			    }
			});
		}
		
		function monitorFetch(resId) {
			var discId = $('#disc' + resId).val();
			$.ajax({
				url: "fetchDiscussion",
				type: "POST",
				dataType: "json",
				data: ({<%= FetchingProcessServlet.ACTION_KEY %> : "<%= FetchingProcessServlet.MONITOR_ACTION %>",
						<%= ControllerServlet.DISC_ID_KEY %> : discId}),
				success: function (data, textStatus) {
					if (data.status == "<%= FetchingProcessStatus.SUCCESS.getCode() %>") {					
						$('img#loading' + resId).hide();
						$('img#success' + resId).show();
						setTimeout('cleanup(' + resId + ')', 1000);
					} else if (data.status == "<%= FetchingProcessStatus.PROGRESS.getCode() %>") {
					    setTimeout('monitorFetch(' + resId + ')', 250);
					} else {
						$('img#loading' + resId).hide();
						$('img#fail' + resId).show();
						$('span#message' + resId).text(data.message).show();
					}				
			    }
			});
		}
		
		function cleanup(resId) {
			var discId = $('#disc' + resId).val();
			$.ajax({
				url: "bulk",
				type: "POST",
				dataType: "json",
				data: ({<%= FetchingProcessServlet.ACTION_KEY %> : "<%= BulkFetchServlet.CLEANUP_ACTION %>",
						<%= ControllerServlet.DISC_ID_KEY %> : discId}),
				success: function (data, textStatus) {									
			    }
			});
		}
	</script>
	
	<body>
	
		<jsp:include page="layout/menu.jsp" flush="true" />
		
		<div id="page">
			<div id="content">
				<div class="post">
					<!-- BEGINNING OF PAGE CONTENT -->				
					
						<fieldset>
					
							<div class="grouping">		
								Results on page:&nbsp;
								<select id='resultsOnPage'>
									<option>10</option>
									<option selected="selected">20</option>
									<option>50</option>
								</select><br/><br/>
								Target website(s):&nbsp;
								<input type="checkbox" class="targetSite" value="liberation.fr">Liberation &nbsp;
								<input type="checkbox" class="targetSite" value="rue89.com">Rue89 &nbsp;
								<input type="checkbox" class="targetSite" value="lefigaro.fr">Lefigaro &nbsp;
								<input type="checkbox" class="targetSite" value="lemonde.fr" disabled="disabled">Lemonde					
							</div>
							
							<input id="txtQuery" type="text" />
							<input id="bing" type="button" value="search">
							<img class="resreshing" alt="loading" src="images/ajax-loader.gif">
						</fieldset>
					
						<div id="result-aggregates"></div>
	                    <table id="result-list">                    	
	                    </table>
	                    
	                    <div id="result-navigation">
	                        <a id="prev" href="#">&laquo;</a>
	                        <a id="next" href="#">&raquo;</a>
	                    </div>
	                    
	                    <input type="button" id="addForFetching" value="fetch selection" onclick="createFetch()" />
                    
                    	<p id="error-list"></p>                    	
										
					<!-- ENDING OF PAGE CONTENT -->					
				</div>
			</div>
			<div style="clear: both;">&nbsp;</div>
		</div>
		
		<jsp:include page="layout/footer.jsp" flush="true" />
	
	</body>

</html>
