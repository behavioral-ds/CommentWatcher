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
package lab.eric.datafetcher.web.models;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import lab.eric.datafetcher.utils.Miscellaneous;
import lab.eric.datafetcher.utils.config.Config;
import lab.eric.datafetcher.web.controllers.VisualizeXmlServlet;

import org.apache.log4j.Logger;

/**
 * Model class used in {@link VisualizeXmlServlet}.
 * Handles the XML files.
 * 
 * @author Hamza Mallek, updated Marian-Andrei RIZOIU
 *
 */
public class XmlFileModel implements Comparable<XmlFileModel>{
	
	private static Logger logger = Logger.getLogger(XmlFileModel.class);
	
	private String fileName = "";
	
	private String completeURL = "";
	
	private List<String> errors = new ArrayList<String>();
	
	private boolean popedOut;

	public XmlFileModel() {
		this.setPopedOut(false);
	}
	
	public XmlFileModel(String na, String u) {
		this.fileName = na;
		this.completeURL = u;
		this.setPopedOut(false);
	}
	
	// XML file related methods
	public String getCompleteURL() {
		return completeURL;
	}

	public void setCompleteURL(String urlCompl) {
		this.completeURL = urlCompl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String name) {
		this.fileName = name;
	}
	
	/**
	 * Returns a file name proposition extracted from the URL
	 * 
	 * @return
	 */
	public String getFileNameFromURL() {
		String URL = this.getCompleteURL();
		File myFile = new File(URL);
		return myFile.getName();
	}

	@Override
	public int compareTo(XmlFileModel o) {

		String name1 = this.getFileName();
		String name2 = o.getFileName();

		name1 = name1.substring(0, name1.indexOf(".xml") );
		name2 = name2.substring(0, name2.indexOf(".xml") );

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH.mm.ss");
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = dateFormat.parse(name1);
			date2 = dateFormat.parse(name2);
		} catch (ParseException e) {
			logger.error("This should not happen! Files names are malformed: " + e);
		}

		return date2.compareTo(date1);
	}

	// Model related methods
	public void addError(String error) {
		this.errors.add(error);		
	}

	public List<String> getErrors() {
		return errors;
	}
	
	// XMOL filelist related methods
	/**
	 * Lists the file names in the XML result file folder in inverse order (the
	 * most recent is the first).
	 * 
	 * @return names in files in the folder.
	 */
	public static Vector<XmlFileModel> listXMLFilesinFolder() {
		Miscellaneous.verifyCreateFolder(Config.getXmlOutputDir());
		File folder = new File(Config.getXmlOutputDir());
		Vector<XmlFileModel> list = new Vector<XmlFileModel>();
		String[] fileList = folder.list();
	
		for (int i = 0; i < fileList.length; i++) {
			XmlFileModel obj = new XmlFileModel(fileList[i], Config.getXmlOutputDir() + "/" + fileList[i]);
			list.add(obj);
		}
		
		Collections.sort(list);
		return list;
	}

	/**
	 * Removes an XML file from the XML folder.
	 * 
	 * @param file
	 *            fileName of file to delete
	 * @return true if the file was successfully deleted
	 */
	public static boolean deleteXmlFile(String file) {
		File myFile = new File(file);
		logger.debug("XML file \"" + file + "\" was deleted!");
		return myFile.delete();
	}

	public boolean isPopedOut() {
		return popedOut;
	}

	public void setPopedOut(boolean popedOut) {
		this.popedOut = popedOut;
	}
	
	public void togglePopedOut() {
		if ( this.popedOut )
			this.popedOut = false;
		else
			this.popedOut = true;
	}

}
