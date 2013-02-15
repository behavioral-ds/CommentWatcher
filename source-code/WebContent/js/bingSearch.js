/*-------------------------------------------------------------------------------
# Copyright (c) 2013 Marian-Andrei RIZOIU.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the GNU Public License v3.0
# which accompanies this distribution, and is available at
# http://www.gnu.org/licenses/gpl.html
# 
# Contributors:
#     Marian-Andrei RIZOIU - initial API and implementation
#------------------------------------------------------------------------------- */

/*
 * 
 * Chercher - jQuery plugin that works with searching using bing API.
 * 
 */
(function( $ ){
	$.fn.chercher = function(options) {
		var defaults = {
			appid : "B657BC89DA6AC53B070868F7FCC518FA6A0A79F9",
			webcount : 20
		};
		var opts = $.extend(defaults, options);
		
		var AppId = "AppId=";
		var Query = "Query=";
		var Sources = "Sources=Web";
		var Version = "Version=2.0";
		var Market = "Market=fr-FR";  
		var Options = "Options=DisableLocationDetection";
		var WebOffset = 0;
		
		$('#resultsOnPage').change(function() {
			WebOffset = 0;
			opts.webcount = $('#resultsOnPage option:selected').html();			
		});
		
		$("#prev").click(function() {			
		    if (WebOffset > 0) WebOffset--;
		    search();
		});
		
		$("#next").click(function() {			
		    WebOffset++;
		    search();
		});
		
		return this.click(function() {
			WebOffset = 0;
		    search();
		});
							
		function search() {
			if ($('input.targetSite:checked').length == 0) {
				alert('Please select at least one target site.');
				return;
			}
			
			var searchTerms = composeQuery();
			var arr = [AppId + opts.appid, Query + searchTerms, Sources, Version, Market, Options, "Web.Count=" + opts.webcount, "Web.Offset=" + WebOffset, "JsonType=callback", "JsonCallback=?"];  
			var requestStr = "http://api.search.live.net/json.aspx?" + arr.join("&");
			$('.resreshing').show();
								  
			$.ajax({
				type: "GET",
				url: requestStr,  
				dataType: "jsonp",  
				success: function(msg) {  
					SearchCompleted(msg);  
				},
				error: function(msg) {
					alert("Something hasn't worked\n" + msg.d);  
				}
			});
		}
		
		// Compose query, cosidering selected sites.
		function composeQuery() {
			var terms = $('#txtQuery').val().replace(" ", "+");
			
			checkBoxes = $('input.targetSite:checked');
			if (checkBoxes.length > 0) {
				sites = ' (';
				checkBoxes.each(function(index) {
					sites = sites + 'site:' + $(this).val();
					if (index < checkBoxes.length - 1) {
						sites = sites + ' OR ';
					}					
				});
				sites = sites + ')';				
			}
			return terms + sites;
		}
		
		function SearchCompleted(response) {  
			  
		    var errors = response.SearchResponse.Errors;  
		    if (errors != null) {  
		        // There are errors in the response. Display error details.  
		        DisplayErrors(errors);  
		    }  
		    else {  
		        // There were no errors in the response. Display the Web results.  
		        DisplayResults(response);  
		    }  
		} 
		
		function DisplayResults(response) {  
			$("#result-list").html("");                                 // Clear our existing results  
			$("#result-navigation a").filter(".nav-page").remove();    // Remove our navigation  
			$("#result-aggregates").children().remove();                // Remove our hit information  
				  
			var results = response.SearchResponse.Web.Results;          // Define our web results in a more succinct way  
				  
			// Let the user know what the search yielded			  
			$('#result-aggregates').prepend("<p id=\"result-count\">Displaying " + StartOffset(results)
					+ " to " + EndOffset(results)
					+ " of " + parseInt(response.SearchResponse.Web.Total) + "</p>");
				  
			// Create the list of results  
			var link = [];                                  // We'll create each link in this array 
			// Uncomment this if you enable highlighting option
			// var regexBegin = new RegExp("\uE000", "g");     // Look for the starting bold character in the search response  
			// var regexEnd = new RegExp("\uE001", "g");       // Look for the ending bold character in the search response  
			for (var i = 0; i < results.length; ++i) {				
				// Step through our list of results and build our list items
				link[i] = "<tr><td><input class='searchResult' type='checkbox' checked='checked' value='" + i + "'></td>" +
						  "<td><a id='res" + i + "' href='" + results[i].Url +"'>"+ results[i].Title +"</a>&nbsp;" +
						  "<img id='loading" + i + "' class='loading' alt='loading' src='images/ajax-loader.gif'>" +
						  "<img id='success" + i + "' class='success' alt='success' src='images/success.png'>" +
						  "<img id='fail" + i + "' class='fail' alt='fail' src='images/fail.png'>&nbsp;" +
						  "<span id='message" + i + "' class='errMesg' />" + 
						  "<input id='disc" + i + "' type='hidden' value='-1' /></td></tr>" +
						  "<tr><td/><td>" + results[i].Description + "</td></tr><tr class='table-skip'/>";
				
				// Uncomment this if you enable highlighting option
				// Replace our unprintable bold characters with HTML  
				// link[i] = link[i].replace(regexBegin, "<strong>").replace(regexEnd, "</strong>");  
			}
			$("#result-list").html(link.join(''));          // Concatenate our list and add it to our page  
				  
			// Set up our result page navigation  
			CreateNavigation(response.SearchResponse.Web.Total, results.length);
			$('.resreshing').hide();
		}
		
		
		function StartOffset(results) {
		    if (WebOffset == 0) {
		        return 1;
		    }
		    else {
		        return (WebOffset * results.length) + 1;
		    }
		}
		
		function EndOffset(results) {
		    if (WebOffset == 0) {
		        return results.length;
		    }
		    else {
		        return (WebOffset + 1) * results.length;
		    }
		}
		
		function CreateNavigation(totalHits, pageSize) {  
		    var totalPages = (totalHits / pageSize > 10) ? 10 : parseInt(totalHits / pageSize);
		    var nav = [];
		    for (var i = 0; i < totalPages; i++) {  
		        nav[i] = "<a class='nav-page' href='#'>" + (i + 1) + "</a>";
		    }  
		    $("#result-navigation a:first").after(nav.join(''));
		  
		    // Create a listener for the page navigation click event  
		    $("#result-navigation a.nav-page").click(function() {  
		        WebOffset = parseInt($(this).html()) - 1;
		        search();  
		    });  
		  
		    // Show the navigation!  
		    $("#result-navigation").show();  
		}		
		
		function DisplayErrors(errors) {
		    var errorHtml = [];
		
		    for (var i = 0; i < errors.length; ++i) {
		        errorHtml[i] = "<li>" + errors[i] + "</li>";
		    }
		    $('#error-list').append(errorHtml.join(''));
		}
  	};
})( jQuery );
