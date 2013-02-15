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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import lab.eric.datafetcher.utils.config.Config;

/**
 * Performs an XSL transformation of a cleaned HTML
 * into the predefined XML format.
 * The XSLT transformation file is detected by website name.
 * 
 * @author Nikolay Anokhin.
 *
 */
public class XslTransformer { 
	
	/**
	 * Constructs a new instance of {@link XslTransformer}.
	 * This class can be reused for several transformations
	 * of web pages from the same web site.
	 * 
	 * @param websiteName Name of a web site from which input XML comes.
	 */
	public XslTransformer(String websiteName) {		
		if (websiteName == null || websiteName.isEmpty()) {
			throw new IllegalArgumentException("The websiteName argument should not be empty.");
		}
		
		this.transformationName = websiteName;
	}
	
	/**
	 * Name of a web site from which input XML comes.
	 * This is immutable.
	 */
	private String transformationName;
	
	/**
	 * Transforms an input XML-string into a new XML-string 
	 * using an appropriate transformation.
	 * 
	 * @param inputXml A byte array containing input XML file.
	 * 
	 * @return Transformation result as a {@code byte} array.
	 *  
	 * @throws TransformerException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public byte[] transform(byte[] inputXml) throws TransformerException, IllegalStateException, IOException {	
		Reader xsltReader = getTransformationReader();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(inputXml);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		StreamResult transformResult = new StreamResult(outputStream);
	    StreamSource xsltSource = new StreamSource(xsltReader);        
	    StreamSource xmlSource = new StreamSource(inputStream);
	        
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer(xsltSource);
	    // Apply transformation.
	    transformer.transform(xmlSource, transformResult);
	    byte[] resultBytes = outputStream.toByteArray();
	    
	    outputStream.close();
	    inputStream.close();
	    xsltReader.close();
		
		return resultBytes;
	}
	
	/**
	 * Get a new reader for XSLT-transformation.
	 * The XSLT transformation source is determined by the website name.
	 * 
	 * @return {@code Reader} to read the transformation. Close it after using!
	 * 
	 * @throws FileNotFoundException
	 */
	private Reader getTransformationReader() throws FileNotFoundException {
		if (transformationName == null || transformationName.isEmpty()) {
			throw new IllegalStateException("The website name should be specified.");
		}		
		
		FileReader fileReader = new FileReader(Config.getTransformsDir() + "/" + transformationName + ".xsl");
		return new BufferedReader(fileReader);
	}

	public String getTransformationName() {
		return transformationName;
	}
}
