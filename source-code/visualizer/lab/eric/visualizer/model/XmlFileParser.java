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
/**
 * 
 */
package lab.eric.visualizer.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Le parseur de fichier XML, implémente l'interface Runnable pour essayer de
 * gérer java.lang.OutOfMemoryError. Voir en bas.
 * MAR: adapted for applet use. Demands and gets the XML document from the servlet.
 * 
 * @author Samadjon Uroqov, updated Marian-Andrei RIZOIU
 * 
 */
public class XmlFileParser implements Runnable {

	/*
	 * (non-javadoc)
	 */
	private Document xmlDocument;

	/*
	 * (non-javadoc)
	 */
	private File xmlFile;

	/*
	 * (non-javadoc)
	 */
	private Date xmlFileDate;

	/*
	 * (non-javadoc)
	 */
	private String xmlFilePath;

	/*
	 * (non-javadoc)
	 */
	private TopicsContainer topicsContainer;
	private CommentManager commentManager;
	
	private URL servletURL;
	private String servletName;

	/**
	 * The DOM parser crashes if XML file is too big and
	 * java.lang.OutOfMemoryError occurs, blocking the thread. If ok, fatalError
	 * is set to false. This information is used by the MainController thread to
	 * find out what happened.
	 */
	public static boolean fatalError = true;

	/**
	 * @param xmlDocument
	 */
	public XmlFileParser(Document xmlDocument) {
		this.xmlDocument = xmlDocument;

	}

	/**
	 * @param xmlFile
	 */
	public XmlFileParser(File xmlFile) {
		this.xmlFile = xmlFile;

	}

	/**
	 * @param xmlFilePath
	 */
	public XmlFileParser(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
		this.xmlFile = new File(xmlFilePath);

	}

	/**
	 * Once the XML file is loaded to xmlDocument object in the memory, this
	 * method populates storage objects of the application model.
	 * topicsContainer is populated separately and commentManager is used to
	 * redistribute comments according to sources, articles, etc.
	 */
	private void populateTopics() {

		NodeList thematicList = xmlDocument.getElementsByTagName(ConfigurationManager.thematicTagName);
		int nodeCount = thematicList.getLength();
		if (nodeCount == 0) {
			fatalError = false;
			commentManager = null;
			return;
		}
		topicsContainer = new TopicsContainer(nodeCount);
		commentManager = new CommentManager();
		
		// create the meta-topic that contains everything
		Topic topicAll = new Topic(-1, null);
		topicsContainer.addTopic(topicAll);

		for (int i = 0; i < nodeCount; i++) {
			try {

				Element currentThematic = (Element) thematicList.item(i);

				NodeList keywordList = currentThematic.getElementsByTagName(ConfigurationManager.keyphraseTagName);
				int keywordCount = keywordList.getLength();

				if (keywordCount == 0) {
					fatalError = false;
					commentManager = null;
					return;
				}

				int topicId = Integer.parseInt(currentThematic.getAttribute(ConfigurationManager.thematicIdName));

				Keyphrase[] keyphrases = new Keyphrase[keywordCount];

				for (int j = 0; j < keywordCount; j++) {

					Element currentKeyword = (Element) keywordList.item(j);
					keyphrases[j] = new Keyphrase(
							currentKeyword.getFirstChild().getNodeValue(),
							Float.parseFloat(currentKeyword.getAttribute(ConfigurationManager.keyphraseValueName)) 
							);
				}

				Topic topic = new Topic(topicId, keyphrases);
				topicAll.addKeyphrase(keyphrases);
				topicsContainer.addTopic(topic);

				NodeList commentList = currentThematic.getElementsByTagName(ConfigurationManager.commentTagName);
				int articleCount = commentList.getLength();

				if (articleCount == 0) {
					fatalError = false;
					commentManager = null;
					return;
				}

				for (int j = 0; j < articleCount; j++) {
					Element currentArticle = (Element) commentList.item(j);

					String title = ((Element) currentArticle
							.getElementsByTagName(ConfigurationManager.commentSrcArticleName)
							.item(0)).getTextContent();

					String source = ((Element) currentArticle
							.getElementsByTagName(ConfigurationManager.commentSrcName)
							.item(0)).getTextContent();

					String dateString = ((Element) currentArticle
							.getElementsByTagName(ConfigurationManager.commentDateName)
							.item(0)).getTextContent();
					
					String weightString = ((Element) currentArticle
							.getElementsByTagName(ConfigurationManager.commentWeight)
							.item(0)).getTextContent();
					
					DateFormat dayFormat = new SimpleDateFormat(ConfigurationManager.xmlDateFormat);
					Date date = null;

					try {
						date = dayFormat.parse(dateString);
					} catch (ParseException e) {
						ConfigurationManager.showErrorMessage(e, false);
						fatalError = false;
						commentManager = null;
						return;
					}
					// comments[j] = new
					// Comment(Integer.parseInt(currentArticle.getAttribute(ConfigurationManager.commentIdName)),
					// date, source, title,topic.getTopicID());

					// System.out.println("handling comment N° " + (debug++));

					int tmpCommentId = Integer.parseInt(currentArticle.getAttribute(ConfigurationManager.commentIdName));
					Comment tmpComment = new Comment(tmpCommentId, date,source, title,weightString, topic);
					commentManager.addComment(tmpComment);
					
					// create another comment that has as a dad the topicAll topic
					tmpComment = new Comment(tmpCommentId, date,source, title,weightString, topicAll);
					commentManager.addComment(tmpComment);
				}
				// topic.setComment(comments);
				// topicsContainer.addTopic(topic);
			} catch (Exception e) {
				ConfigurationManager.showErrorMessage(e, false);
				fatalError = false;
				commentManager = null;
				return;
			}
		}

		// commentManager.debugIt();

	}

	public void setConnexionDetails(URL servURL, String servName) {
		this.servletURL = servURL;
		this.servletName = servName;
	}
	
	/**
	 * Get a connection to the servlet.
	 */
	private URLConnection getServletConnection()
		throws MalformedURLException, IOException {

		// open the connection to the servlet
		URL urlServlet = new URL(this.servletURL, this.servletName);
//		URL urlServlet = new URL("http://localhost:8081/DataFetcher/visualize");
		System.out.println(urlServlet);
		URLConnection con = urlServlet.openConnection();

		// configuration
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setRequestProperty(
			"Content-Type",
			"application/x-java-serialized-object");

		return con;
	}

	/**
	 * In the case of Applet execution, the XML file will be demanded to the servlet.
	 */
	private void retrieveXmlFile() {
		try {
			// create a communication object
			CommunicationObject request = new CommunicationObject(CommunicationObject.REQUEST_XML_FILE);

			// send data to the servlet
			URLConnection connection = getServletConnection();
			OutputStream outstream = connection.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(request);
			oos.flush();
			oos.close();

			// receive result from servlet
			InputStream instr = connection.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			CommunicationObject result = (CommunicationObject) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();

			// if we got a malformed result
			if ( !result.isValid() ) {
				Exception e = new Exception("Error in the communication with the server! Malformed response!");
				throw e;
			}
			
			// if we got an error
			if ( result.getCommunicationType() == CommunicationObject.REPLY_FAILED_XML_FILE ) {
				Exception e = null;
				if ( result.getAttachment() != null){
					e = (Exception) result.getAttachment();
				} else {
					e = new Exception("Exception occured on the server side! No detailed info available!");
				}
				
				throw e;
			}
			
			// if everything is alright
			if ( result.getCommunicationType() == CommunicationObject.REPLY_OK_XML_FILE ) {
				xmlDocument = (Document) result.getAttachment();
			}

		} catch (Exception ex) {
			try {
				CommunicationObject request = new CommunicationObject(CommunicationObject.REPLY_FAILED_XML_FILE, ex);
				URLConnection connection = getServletConnection();
				OutputStream outstream = connection.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(outstream);
				oos.writeObject(request);
				oos.flush();
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ConfigurationManager.showErrorMessage(ex, false);
			commentManager = null;
			fatalError = false;
			ex.printStackTrace();
			
			return;
		}
		
		populateTopics();
		
	}
	
	/**
	 * Loads the XML file into xmlDocument object in the memory
	 */
	private void loadXmlFile() {

		if (xmlFile == null) {
			fatalError = false;
			commentManager = null;
			return;
		}

		fatalError = true;

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;

			try {
				dBuilder = dbFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				ConfigurationManager.showErrorMessage(e, false);
				commentManager = null;
				return;
			}
			try {
				xmlDocument = dBuilder.parse(xmlFile);
			} catch (SAXException e) {
				ConfigurationManager.showErrorMessage(e, false);

				fatalError = false;
				commentManager = null;
				return;
			} catch (IOException e) {
				ConfigurationManager.showErrorMessage(e, false);
				commentManager = null;
				fatalError = false;
				return;
			}
			xmlDocument.getDocumentElement().normalize();
		} catch (Exception e) {
			ConfigurationManager.showErrorMessage(e, false);
			commentManager = null;
			fatalError = false;
			return;
		}

		populateTopics();

	}

	/**
	 * Getter of the property <tt>xmlDocument</tt>
	 * 
	 * @return Returns the xmlDocument.
	 * 
	 */

	public Document getXmlDocument() {
		return xmlDocument;
	}

	/**
	 * Setter of the property <tt>xmlDocument</tt>
	 * 
	 * @param xmlDocument
	 *            The xmlDocument to set.
	 * 
	 */
	public void setXmlDocument(Document xmlDocument) {
		this.xmlDocument = xmlDocument;
	}

	/**
	 * Getter of the property <tt>xmlFile</tt>
	 * 
	 * @return Returns the xmlFile.
	 * 
	 */

	public File getXmlFile() {
		return xmlFile;
	}

	/**
	 * Setter of the property <tt>xmlFile</tt>
	 * 
	 * @param xmlFile
	 *            The xmlFile to set.
	 * 
	 */
	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	/**
	 * Getter of the property <tt>xmlFileDate</tt>
	 * 
	 * @return Returns the xmlFileDate.
	 * 
	 */

	public Date getXmlFileDate() {
		return xmlFileDate;
	}

	/**
	 * Setter of the property <tt>xmlFileDate</tt>
	 * 
	 * @param xmlFileDate
	 *            The xmlFileDate to set.
	 * 
	 */
	public void setXmlFileDate(Date xmlFileDate) {
		this.xmlFileDate = xmlFileDate;
	}

	/**
	 * Getter of the property <tt>xmlFilePath</tt>
	 * 
	 * @return Returns the xmlFilePath.
	 * 
	 */

	public String getXmlFilePath() {
		return xmlFilePath;
	}

	/**
	 * Setter of the property <tt>xmlFilePath</tt>
	 * 
	 * @param xmlFilePath
	 *            The xmlFilePath to set.
	 * 
	 */
	public void setXmlFilePath(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
	}

	/**
	 * Getter of the property <tt>topicsContainer</tt>
	 * 
	 * @return Returns the topicsContainer.
	 * 
	 */

	public TopicsContainer getTopicsContainer() {
		return topicsContainer;
	}

	/**
	 * Setter of the property <tt>topicsContainer</tt>
	 * 
	 * @param topicsContainer
	 *            The topicsContainer to set.
	 * 
	 */
	public void setTopicsContainer(TopicsContainer topicsContainer) {
		this.topicsContainer = topicsContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (ConfigurationManager.isApplet)
			this.retrieveXmlFile();
		else
			this.loadXmlFile();
	}

	/**
	 * @param commentManager
	 *            the commentManager to set
	 */
	public void setCommentManager(CommentManager commentManager) {
		this.commentManager = commentManager;
	}

	/**
	 * @return the commentManager
	 */
	public CommentManager getCommentManager() {
		return commentManager;
	}

}
