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
package lab.eric.datafetcher.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lab.eric.datafetcher.persistence.DiscussionDao;

import org.apache.log4j.Logger;

public class DateFormatter {
	
	private static Logger logger = Logger.getLogger(DiscussionDao.class);
	
	public static String formatDate(String strDate) {
		SimpleDateFormat sdfDestination = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		if (strDate == null || strDate.isEmpty()) {
			return sdfDestination.format(new Date());			
		}
		
		Date cur_date = new Date();
		SimpleDateFormat myyear=new SimpleDateFormat("yyyy");
		strDate = strDate.toLowerCase().trim();
		strDate = strDate.replaceAll("mise à jour", "");
//		strDate = strDate.replaceAll("[\174\050\051à-]", "");
		strDate = strDate.replaceAll("[\\|\\(\\)à-]", "");
		strDate = strDate.replaceAll(" : ", "");
		strDate = strDate.replaceAll("lundi |mardi |mercredi |jeudi |vendredi |samedi |dimanche ", "");
		strDate = strDate.replaceAll(" janvier", "/01/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" février", "/02/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" mars", "/03/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" avril", "/04/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" mai", "/05/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" juin", "/06/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" juillet", "/07/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" août", "/08/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" septembre", "/09/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" octobre", "/10/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" novembre", "/11/" + myyear.format(cur_date));
		strDate = strDate.replaceAll(" décembre", "/12/" + myyear.format(cur_date));
		strDate = strDate.replaceAll("\\.", "/");
		strDate = strDate.replaceAll("h", ":");
		
		String hour = null;
		String mydate = null;
		
		// extract the hour
		Pattern pattern = Pattern.compile("(\\d{1,2}+)\\:(\\d{1,2}+)");
		Matcher matcher = pattern.matcher(strDate);
		
		if (matcher.find()) {
			hour = matcher.group(1) + ":" + matcher.group(2);
		} else hour = "00:00";
		
		// extract the date
		pattern = Pattern.compile("(\\d{1,2}+)\\/(\\d{1,2}+)/(\\d{2,4}+)");
		matcher = pattern.matcher(strDate);
		
		if (matcher.find()) {
			mydate = matcher.group(1) + "/" + matcher.group(2) + "/" + matcher.group(3);
		} else { 
			sdfDestination = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			mydate = sdfDestination.format(cur_date);
		}
		
		strDate = mydate + " " + hour;
		
		try
	    {
	    	//create SimpleDateFormat object with source string date format
	    	SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yy HH:mm");
	    	//parse the string into Date object
	    	Date date = sdfSource.parse(strDate);
	        //create SimpleDateFormat object with desired date format
	        sdfDestination = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	        //parse the date into another format
	        strDate = sdfDestination.format(date);
	    }
	    catch(ParseException pe)
	    {
	    	logger.error("Parse Exception : " + pe);
	    }

	    if ( strDate.trim().length() != 0)
	    	return strDate;
	    
	    // if all failed, return current date	    
	    return sdfDestination.format(cur_date);
    }
	
	public static Date parseDateTimeString(String dateString) {
		if (dateString == null || dateString.isEmpty()) {
			return null;
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date result = null;
		try {
			result = formatter.parse(dateString);
		} catch (ParseException e) {
			logger.error("Unable to format date: " + dateString + ". Returning null.");
		}
		return result;
	}
	
	public static Date parseDateString(String dateString) {
		if (dateString == null || dateString.isEmpty()) {
			return null;
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date result = null;
		try {
			result = formatter.parse(dateString);
		} catch (ParseException e) {
			logger.error("Unable to format date: " + dateString + ". Returning null.");
		}
		return result;
	}
}
