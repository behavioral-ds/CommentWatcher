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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import lab.eric.datafetcher.persistence.DiscussionDao;
import lab.eric.datafetcher.persistence.Persistence;
import lab.eric.datafetcher.utils.config.Config;
import lab.eric.datafetcher.web.controllers.ClassificationServlet;
import lab.eric.malletwrapper.core.MalletWrapper;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import TopicExtractor.ExtractTopics;

/**
 * Class that wraps the calls to CKP textual clustering. The call to CKP is done
 * in a thread so that it works in the background. The instance of this class
 * calls CKP periodically, while "workEnabled" is set
 * 
 * @author Marian-Andrei RIZOIU
 * 
 */
public class Classification implements Serializable {

	private class Keyphrase implements Comparable<Keyphrase>{
		public String key;
		public Double value;

		public Keyphrase (String key, Double value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public int compareTo(Keyphrase o) {
			if ( (this.value - o.value) > 0 )
				return 1;

			if ( (this.value - o.value) < 0 )
				return -1;

			return 0;
		}
	}

	private class MyDoc {
		public int documentNo;
		public double score;

		public MyDoc(String name, double score) {
			StringTokenizer st = new StringTokenizer(name, "_");
			if ( st.countTokens() != 2) {
				logger.error("[MyDoc].[Constructor]: Internal error! Filename: " + name + " does not respect format (message_<number>");
				return;
			}

			// the second token will be the number of the document
			int tokenNo = -1;
			while (st.hasMoreTokens()) {
				tokenNo++;
				if ( tokenNo == 1)
					this.documentNo = Integer.parseInt(st.nextToken());
				else 
					st.nextToken();
			}

			this.score = score;
		}
	}

	private static final long serialVersionUID = 1L;
	private boolean workEnabled = false;
	private boolean workNow = false;

	// the CSV transformation stuff
	private static Logger logger = Logger.getLogger(Classification.class);
	private Element root = null;
	private Document document = null;
	private String sep = "\t";
	private Vector<String> selectedDiscussions;
	private	String textFilter;
	private String onlyWords;

	public Classification() {
		this.workNow = false;
		this.workEnabled = false;
	}

	/**
	 * With the saved parameters, this method extract the discussion in the file
	 * format required by the classifiers.
	 * 
	 * @return true if the operation was successful, false otherwise.
	 */
	private boolean extractTexts() {
		try {

			int status = 0;
			
			// populate the two Discussion lists (French and English)
			Session mySession = Persistence.getSessionFactory().openSession();
			DiscussionDao dao = new DiscussionDao(mySession);
			status = dao.createClassificationInput(selectedDiscussions, textFilter, onlyWords);
			mySession.close();

			if (status == 0) {
				ClassificationServlet.addError("There are no texts for your selection!");
				this.setWorkEnabled(false);

				return false;
			} else {
				// set the status config
				Config.setClassificationSelectedDiscussions(this.selectedDiscussions);
				Config.setClassificationTextFilter(this.textFilter);
				Config.setClassificationOnlyWords(this.onlyWords);

				return true;
			}
		} catch (Exception ex) {
			ClassificationServlet.addError("At least one discussion should be selected!");
			logger.error("[Classification].[extractTexts]: Error occured: ", ex);
			this.setWorkEnabled(false);
			return false;
		}
	}

	/**
	 * Method that calls the classification algorithm using the parameters found
	 * in the config file. Iterates the call with the same parameters while
	 * classification is enabled.
	 */
	private void callClassification() {

		while (this.isWorkEnabled()) {

			this.setWorkingNow(true);
			
			// let's extract the discussions
			if (!extractTexts()) {
				// there was a problem extracting the texts
				this.setWorkEnabled(false);
				this.setWorkingNow(false);

				return;
			}

			switch (Config.getClassificationAlgorithm()) {
			case Config.CLASSIFIER_CKP:
				// we classify with CKP
				// load the command arguments to be passed to the classification
				Vector<String> commandArguments = Config.createCKPClassificationParameterList();
				String args[] = new String[commandArguments.size()];
				for (int i = 0; i <= commandArguments.size() - 1; i++) {
					args[i] = commandArguments.elementAt(i);
				}

				// run the classification in itself, using the parameters created earlier
				@SuppressWarnings("unused")
				ExtractTopics MyRun = new ExtractTopics(args);

				break;
			case Config.CLASSIFIER_TOPICALNGRAMS:
				try {
					// we classify with Mallet Topical NGrams
					MalletWrapper.runWrapper(Config.getInputClassificationDir(), 
							Config.getTempDir(), 
							Config.getStopwordsFile(), 
							Config.getClassificationNumClusters(), 
							Config.getEvalMatrixFile());
					
				} catch (Exception ex) {
					ClassificationServlet.addError("There was an error while running Topical NGrams! Try CKP instead!");
					ClassificationServlet.addError("Error message: " + ex);
					logger.error("[Classification.callClassification]: Error while running classification Topical NGrams! " + ex);
					ex.printStackTrace();
					
					this.setWorkEnabled(false);
					this.setWorkingNow(false);
					
					return;
				}

				break;
			default:
				// some kind of unknown algorithm or an error
				this.setWorkEnabled(false);
				this.setWorkingNow(false);

				return;
			}

			// transform the result into a XML file
			transformCsvToXml();
			this.setWorkingNow(false);

			// let's see how much we sleep
			int inteval = 60000 * Config.getClassificationUpdateTime();

			// if this was a one time shot, disable the classification
			if ( inteval < 0 )
				this.setWorkEnabled(false);
			else {
				// sleep the time out
				try {
					Thread.sleep(inteval);
				} catch (Exception ie) {
				}
			}
		}

	}

	/**
	 * Inner class used to start the classification in a parallel thread so that
	 * we can continue working while classification is going on.
	 * 
	 * @author Hamza Mallek, updated Marian-Andrei RIZOIU
	 * 
	 */
	private class MyRunClassification implements Runnable {
		Classification classifParent;

		public MyRunClassification(Classification parent) {
			this.classifParent = parent;
		}

		public void run() {
			classifParent.callClassification();
		}
	}

	/**
	 * Start the classification thread in the background using CKP.
	 * 
	 * @param inteval
	 *            the time between two classifications
	 */
	public void startClassification(Vector<String> selectedDiscussions,	String textFilter, String onlyWords) {
		this.selectedDiscussions = selectedDiscussions;
		this.textFilter = textFilter;
		this.onlyWords = onlyWords;

		this.setWorkEnabled(true);
		Runnable temp = new MyRunClassification(this);
		Thread myThread = new Thread(temp);
		myThread.start();
	}

	public boolean isWorkEnabled() {
		return this.workEnabled;
	}

	public void setWorkEnabled(boolean enabled) {
		Config.setClassificationEnabled(enabled);
		this.workEnabled = enabled;
	}

	public boolean isWorkingNow() {
		return this.workNow;
	}
	
	public void setWorkingNow(boolean enabled) {
		this.workNow = enabled;
	}

	/**
	 * Transforms the output files from the classification into the XML format
	 * necessary for visualization.
	 */
	private void transformCsvToXml() {
		String evalFile = Config.getEvalMatrixFile();
		String extraInfoFile = Config.getExtraInfoFile();

		root = new Element("root");
		document = new Document(root);

		try {
			BufferedReader inEval = new BufferedReader(new FileReader(evalFile));
			BufferedReader extraInfo = new BufferedReader(new FileReader(extraInfoFile));

			// in here we will store the thematic that emerge from the result file
			Vector<Element> thematics = new Vector<Element>();

			// first put the date under the root
			Element current = new Element("DATE");
			current.setText(getDateTime());
			root.addContent(current);

			//now get our vocabulary (second line in inEval)
			Vector<String> evalLine = null;
			do {
				evalLine = returnLine( inEval.readLine(), sep );
			}while ( evalLine.size() == 0 );
			String vocabulary = "";
			for (int i = 0; i < evalLine.size() - 1; i++)
				vocabulary += evalLine.elementAt(i) + "\t";
			vocabulary += evalLine.elementAt(evalLine.size() - 1);

			current = new Element("VOCABULARY");
			current.setText(vocabulary);
			root.addContent(current);

			// first parse the word - topic part
			// while in here count topics
			Integer clusterNo = -1;
			while ( true ) {
				// we pass to a new cluster
				clusterNo++;

				// first lets read the values for the words in the eval file
				// just need to advance a line
				evalLine = returnLine( inEval.readLine(), sep );
				if ( (evalLine.size() == 0) || (Integer.parseInt(evalLine.elementAt(0)) != clusterNo) ){
					// we are are done in here, it means we are done with this part, just get out
					clusterNo--;
					break;
				}		

				// create the thematic
				Element thematic = new Element("Thematic");
				Attribute attr = new Attribute("ID", clusterNo.toString());
				thematic.setAttribute(attr);
				// add it to the thematic vector
				thematics.addElement(thematic);

				// lets create the wordValues element
				current = new Element("Word-values");
				vocabulary = "";
				// starting from the second element
				for (int i = 1; i < evalLine.size() - 1; i++)
					vocabulary += evalLine.elementAt(i) + "\t";
				vocabulary += evalLine.elementAt(evalLine.size() - 1);
				current.setText(vocabulary);
				thematic.addContent(current);
			}

			// so far so good
			// now passing to the keyphases

			evalLine = returnLine( inEval.readLine(), sep );
			// read the keyphrases
			Vector<String> keyphrases = new Vector<String>();
			for (int i = 0; i < evalLine.size(); i++)
				keyphrases.addElement(evalLine.elementAt(i) );

			clusterNo = -1;
			while ( true ) {
				// we pass to a new cluster
				clusterNo++;
				//	read values for keyphrases
				evalLine = returnLine( inEval.readLine(), sep );
				if ( (evalLine.size() == 0) || (Integer.parseInt(evalLine.elementAt(0)) != clusterNo) ){
					// we are are done in here, it means we are done with this part, just get out
					clusterNo--;
					break;
				}		

				Vector<Keyphrase> values = new Vector<Keyphrase>();
				// need to start from 1, the first field is the thematic number
				for (int i = 1; i < evalLine.size() - 1; i++) {
					// a new value (the value is actually 1 - distance to centroid)
					Double val = 1 - Double.parseDouble(evalLine.elementAt(i));
					// a value corresponds to the keyphrase at i-1 (first field is the thematic number)
					Keyphrase temp = new Keyphrase( keyphrases.elementAt(i - 1), val);
					values.addElement(temp);
				}

				// Sort out the keyphrases
				Collections.sort(values);

				// and now put the keyphrases in the thematic 
				Element thematic = thematics.elementAt(clusterNo);

				int min = Config.getMaxKeyphrases();
				if ( min > values.size() )
					min = values.size();
				for ( int i = 0; i < min; i++ ) {
					Keyphrase temp= values.elementAt(i);

					// no need to print keyphrases that have a score more than 1
					if ( temp.value > 1.0 )
						break;

					current = new Element("Keyphrase");
					current.setText( temp.key );
					Attribute attr = new Attribute("value_key", String.valueOf(temp.value));
					current.setAttribute(attr);
					thematic.addContent(current);
				}

			}
			// now pass to documents.
			int noClusters = clusterNo + 1;
			Vector<Vector<MyDoc>> clusters = new Vector<Vector<MyDoc>>();
			// initialize my cluster collections
			for (int i = 0 ; i < noClusters; i++){
				Vector<MyDoc> collection = new Vector<MyDoc>();
				clusters.addElement(collection);
			}

			// the next line is the header with the cluster numbers
			evalLine = returnLine( inEval.readLine(), sep );

			// and from now on each line contains the cluster membership for each document
			while ( true ) {
				//	read values for keyphrases
				evalLine = returnLine( inEval.readLine(), sep );
				if ( (evalLine == null) || (evalLine.size() == 0) ){
					// we are are done in here, it means we are done with this part, just get out
					break;
				}

				// first field is the document name
				String documentName = evalLine.elementAt(0);

				for (int i = 1; i < evalLine.size(); i++) {
					if ( i > noClusters)
						logger.error("Format error in document membership in eval file! (too many clusters)");
					Double weight = Double.parseDouble(evalLine.elementAt(i));

					if ( weight > 0 ) {
						MyDoc doc = new MyDoc(documentName, weight);
						clusters.elementAt(i-1).addElement(doc);
					}
				}
			}

			// now we need to read the extra info from the ExtraInfo file
			Vector<String> extraInfoLine = null;
			String line = null;
			while ( (line = extraInfo.readLine()) != null) {
				extraInfoLine = returnLine(line, sep);
				
				if ( extraInfoLine.size() != Config.getExtraInfo() ) {
					logger.error("Format error in extra info file! (line \"" + line + "\" contains " + extraInfoLine.size() + " extra infos instead of " + Config.getExtraInfo() + ")");
					return;
				}
				
				// the document no is the first info
				Integer documentNo = Integer.parseInt(extraInfoLine.elementAt(0));
				String article = extraInfoLine.elementAt(1);
				String date = extraInfoLine.elementAt(2);
				String source = extraInfoLine.elementAt(3);
				Integer type = Integer.parseInt(extraInfoLine.elementAt(4));

				// lets see in what clusters we have it
				for (int i = 0; i < clusters.size(); i++)
					for (int j = 0; j < clusters.elementAt(i).size(); j++)
						if ( documentNo == clusters.elementAt(i).elementAt(j).documentNo) {
							// found at association to cluster i

							// the Message
							current = null;
							if ( type == 0 )
								current = new Element("Comment");
							else
								current = new Element("Article");
							Attribute attr = new Attribute("id", String.valueOf(documentNo));
							current.setAttribute(attr);
							Element message = current;

							// the Title
							current = new Element("Article");
							current.setText(article);
							message.addContent(current);

							// the URL
							current = new Element("Source");
							current.setText(source);
							message.addContent(current);

							// the Date
							current = new Element("Date");
							current.setText(date);
							message.addContent(current);

							// the Weight
							current = new Element("Weight");
							current.setText(String.valueOf(clusters.elementAt(i).elementAt(j).score));
							message.addContent(current);

							// and add our article element to its thematic
							Element thematic = thematics.elementAt(i);
							thematic.addContent(message);
						}
			}


			// and add all to the document root
			for ( int i = 0 ; i < thematics.size(); i++)
				root.addContent(thematics.elementAt(i));

			this.saveXMLFile();

			inEval.close();
			extraInfo.close();
			logger.debug("Transformation done!");

		}catch (Exception e) {
			logger.error("Error while transforming classification result into XML! " + e);
			ClassificationServlet.addError("There was an error while running classification! If you are running Topical NGrams, try CKP! If you are running CKP, then pray!");
			ClassificationServlet.addError("Error message: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Saves the XML document denoted by "document" in a file.
	 * 
	 */
	private void saveXMLFile() {
		try {
			DateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
			Date date = new Date();
			String fileName =formater.format(date);
			 
			String xmlFileName = Config.getXmlOutputDir() + "/"+ fileName + ".xml";
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(this.document, new FileOutputStream(xmlFileName));
			logger.info("XML file created: \"" + xmlFileName + "\"");
		} catch (java.io.IOException e) {
		}
	}

	/**
	 * Method which return the current date
	 * 
	 * @return the date as a String
	 */
	private String getDateTime() {
		DateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date = new Date();

		return formater.format(date);
	}


	/**
	 * Gets a string denoting a line with fields separated by a separator and
	 * returns a vector of fields.
	 * 
	 * @param line
	 *            the line
	 * @param separator
	 *            the separator
	 * @return the vector of fields
	 */
	private Vector<String> returnLine(String line, String separator) {
		Vector<String> ch = new Vector<String>();
		if ( line == null)
			return null;

		StringTokenizer st = new StringTokenizer(line, separator);
		while (st.hasMoreTokens()) {
			ch.add(st.nextToken());
		}

		return ch;
	}


}
