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
package lab.eric.datafetcher.utils.log;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import lab.eric.datafetcher.utils.config.Config;

import org.apache.log4j.PropertyConfigurator;

/**
 * This class initializes log4j logger.
 * Note: Config has to be already initialized.
 * 
 * @author Nikolay Anokhin.
 */
public class Logger {
	
	private static String LOG_FILE_PATH_PROP_NAME = "log4j.appender.textfile.File";	

	/**
	 * Configures logger.
	 * 
	 * @param logConfigName Name of the file that contains logger configuration.
	 */
	public static void configure(String logConfigName) {
	    String configPropertiesPath = Config.getDefaultConfigDir() + "/" + logConfigName;		

	    try {
		    FileReader fileReader = new FileReader(configPropertiesPath);
		    
		    Properties properties = new Properties();
		    properties.load(fileReader);
		    
			// Set full name to a log file.
		    properties.setProperty(LOG_FILE_PATH_PROP_NAME, Config.getDefaultLogFilePath());		    
		    
		    PropertyConfigurator.configure(properties);
		    
		    fileReader.close();	
		    
		    System.out.println("Logger successfully initialized.");
	    } catch (FileNotFoundException e) {
			System.err.println("ERROR: Can not find file with logger configuration: " + configPropertiesPath);
		} catch (IOException e) {
			System.err.println("ERROR: Can not read from file with logger configuration: " + configPropertiesPath);
		}
	}
}
