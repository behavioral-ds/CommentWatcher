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
package lab.eric.datafetcher.parsers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

/**
 * Performs cleaning operation of a webpage, i.e.
 * transformation of a raw HTML into a well-formed XML.
 * 
 * @author Nikolay Anokhin.
 */
public class HtmlCleaner {
	
	public HtmlCleaner() {		
	}
	
	private static final String TARGET_ENCODING = "UTF-8";
	
	private boolean advancedXmlEscape = true;
	
	private boolean omitComments = true;
	
	private boolean recognizeUnicodeChars = true;
	
	private boolean useCdataForScriptAndStyle = true;
	
	private boolean namespacesAware = false;	
	
	/**
	 * Transforms badly-formed HTML-webpage from the specified Url into a well-formed XML 
	 * and returns a {@code String} for further processing.	 
	 * 
	 * @param url An Url of a page to be cleaned. 
	 * 
	 * @return {@code byte} array containing result XML.
	 * 
	 * @throws MalformedURLException
	 * 
	 * @throws IOException
	 */
	public byte[] cleanHtml(String url) throws MalformedURLException, IOException {
		if (url.isEmpty()) {
			throw new IllegalArgumentException("The 'url' argument is null.");
		}

		org.htmlcleaner.HtmlCleaner cleaner = new org.htmlcleaner.HtmlCleaner();		 
		// Take default cleaner properties
		CleanerProperties props = cleaner.getProperties();
		
		props.setAdvancedXmlEscape(advancedXmlEscape);
		props.setOmitComments(omitComments);
		props.setRecognizeUnicodeChars(recognizeUnicodeChars);
		props.setUseCdataForScriptAndStyle(useCdataForScriptAndStyle);
		// Important: if true makes mess in namespaces.
		props.setNamespacesAware(namespacesAware);	
		
		//Throws IOException
		TagNode node = cleaner.clean(new URL(url));		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		PrettyXmlSerializer serializer = new PrettyXmlSerializer(props);
		serializer.writeToStream(node, outputStream, TARGET_ENCODING);
		
		byte[] result = outputStream.toByteArray();		
		outputStream.close();
		
		return result;
	}

	public void setAdvancedXmlEscape(boolean advancedXmlEscape) {
		this.advancedXmlEscape = advancedXmlEscape;
	}

	public boolean isAdvancedXmlEscape() {
		return advancedXmlEscape;
	}

	public void setOmitComments(boolean omitComments) {
		this.omitComments = omitComments;
	}

	public boolean isOmitComments() {
		return omitComments;
	}

	public void setRecognizeUnicodeChars(boolean recognizeUnicodeChars) {
		this.recognizeUnicodeChars = recognizeUnicodeChars;
	}

	public boolean isRecognizeUnicodeChars() {
		return recognizeUnicodeChars;
	}

	public void setUseCdataForScriptAndStyle(boolean useCdataForScriptAndStyle) {
		this.useCdataForScriptAndStyle = useCdataForScriptAndStyle;
	}

	public boolean isUseCdataForScriptAndStyle() {
		return useCdataForScriptAndStyle;
	}

	public void setNamespacesAware(boolean namespacesAware) {
		this.namespacesAware = namespacesAware;
	}

	public boolean isNamespacesAware() {
		return namespacesAware;
	}	
}
