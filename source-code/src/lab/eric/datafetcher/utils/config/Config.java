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
package lab.eric.datafetcher.utils.config;

import java.util.StringTokenizer;
import java.util.Vector;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * This class provides set of static properties that are stores in config file.
 * This class contains singleton instance.
 * 
 * @author Nikolay Anokhin. updated Marian-Andrei RIZOIU.
 * 
 */
public class Config {

	private static Logger logger = Logger.getLogger(Config.class);

	// These constants are essential to read the config.
	private static final String DEFAULT_ANALYSE_DIR_WINDOWS = "C:\\CaseStudy\\analyse";
	private static final String DEFAULT_ANALYSE_DIR_LINUX = "/opt/analyse";
	private static final String DEFAULT_CONFIG_DIR = "/config";
	private static final String DEFAULT_CONFIG_FILE_NAME = "config-general.xml";
	private static final String DEFAULT_LOGS_DIR = "/logs";
	private static final String DEFAULT_LOG_FILE_NAME = "root.log";

	// These are the configuration default values.
	private static final String DEFAULT_TEMP_DIR = "/temp";
	private static final String DEFAULT_XML_OUTPUT_DIR = "/xmlFiles";
	private static final String DEFAULT_TRANSFORMS_DIR = "/transforms";
	private static final String DEFAULT_STOPWORDS_DIR = "/stopwords";

	// other constants
	public static final int LANGUAGE_FRENCH = 1;
	public static final int LANGUAGE_ENGLISH = 2;
	
	public static final int CLASSIFIER_CKP = 0;
	public static final int CLASSIFIER_TOPICALNGRAMS = 1;
	
	// Classification related values
	private static final String DEFAULT_CLASSIFICATION_ALGORITHM = String.valueOf(CLASSIFIER_CKP);	// we default to CKP
	private static final String DEFAULT_CLASSIFICATION_ENABLED = "false";
	private static final String DEFAULT_INPUT_CLASSIFICATION_DIR = "/input-ckp";
	private static final String DEFAULT_EXTRA_INFO_FILE = "/extra-info";
	private static final String DEFAULT_EVAL_MATRIX_FILE = "/eval-matrix.csv";
	private static final int DEFAULT_MAX_KEYPHRASES = 100;
	private static final int DEFAULT_EXTRA_INFO = 5;
	private static final String DEFAULT_CLASSIFICATION_UPDATE_TIME = "-1";	// by default we execute it only once
	private static final String DEFAULT_CLASSIFICATION_LANGUAGE = "1";
	private static final String DEFAULT_CLASSIFICATION_MEASURE = "1";
	private static final String DEFAULT_CLASSIFICATION_NUM_CLUSTERS = "2";
	private static final String DEFAULT_CLASSIFICATION_MIN_WORDS = "5";
	private static final String DEFAULt_FRENCH_STOPWORDS_FILE = "/French";
	private static final String DEFAULT_ENGLISH_STOPWORDS_FILE = "/English";
	private static final String DEFAULT_CLASSIFICATION_TEXT_FILTER = "";
	private static final String DEFAULT_CLASSIFICATION_ONLY_WORDS = "";
	private static final String DEFAULT_CLASSIFICATION_SELECTED_DISCUSSIONS = "";
	private static final int DEFAULT_CLASSIFICATION_DISCUSSION_LIST_LANGUAGE = LANGUAGE_FRENCH;

	// Rss feed related values
	private static final String DEFAULT_RSS_FEED_UPDATE_TIME = "30";

	// Database connection related variables - old connexion type
	public static String databaseLink = "jdbc:mysql://localhost:3306/datasource";
	public static String databaseUser = "root";
	public static String databasePassword = "";

	// These are the database-connection properties
	private static final String DEFAULT_DB_CONNECTION_DIALECT = "org.hibernate.dialect.MySQLDialect";
	private static final String DEFAULT_DB_CONNECTION_URL = "jdbc:mysql://localhost:3306/datasource";
	private static final String DEFAULT_DB_CONNECTION_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DEFAULT_DB_CONNECTION_USER = "root";
	private static final String DEFAULT_DB_CONNECTION_PWD = "";
	private static final String DEFAULT_DB_CONNECTION_LOG_LEVEL = "DefaultLevel=WARN, Tool=INFO";
	private static final String DEFAULT_DB_PROVIDER_CLASS = "org.hibernate.connection.C3P0ConnectionProvider";
	private static final String DEFAULT_DB_C3P0_MIN_SIZE = "2";
	private static final String DEFAULT_DB_C3P0_MAX_SIZE = "20";
	private static final String DEFAULT_DB_C3P0_TIMEOUT = "300";
	private static final String DEFAULT_DB_C3P0_MAX_STATEMENTS = "50";
	private static final String DEFAULT_DB_C3P0_IDLE_TEST_PERIOD = "3000";
	
	
	private static final String DEFAULT_BING_API_APP_ID = "B657BC89DA6AC53B070868F7FCC518FA6A0A79F9";

	private XMLConfiguration xmlConfig = null;

	private static Config config = null;

	private Config() {
		try {
			this.xmlConfig = new XMLConfiguration(getDefaultConfigFullPath());
			this.xmlConfig.setAutoSave(true);
		} catch (ConfigurationException e) {
			logger.error("Can not read configuration file.", e);
		}
	}

	/**
	 * Gets string value of an attribute with the specified key. If the
	 * value/attribute does not exist, the default values (provided) is returned
	 * and then stored in the config file
	 * 
	 * @param key
	 *            Period separated config property path.
	 * @param attrName
	 *            the name of the attribute.
	 * @param defaultValue
	 *            The default value of a config property.
	 * @return String value of the attribute.
	 */
	private String getAttr(String key, String attrName, String defaultValue) {
		// create the name in XML format
		String name = key + "[@" + attrName + "]";

		return this.getString(name, defaultValue);
	}

	private void setAttr(String key, String attrName, String value) {
		// create the name in XML format
		String name = key + "[@" + attrName + "]";

		this.setString(name, value);
	}
	
	/**
	 * Wrapper for clearTree in ( @ref HierarchicalConfiguration ). Deletes all
	 * keys equal or starting with key
	 * 
	 * @param key
	 *            the key to be deleted
	 */
	private void clearTree (String key) {
		xmlConfig.clearTree(key);
	}

	/**
	 * Gets string value of a config property with the specified key. If the
	 * property does not exist, the default values (provided) is returned and
	 * then stored in the config file
	 * 
	 * @param key
	 *            Period separated config property path.
	 * 
	 * @param defaultValue
	 *            The default value of a config property.
	 * 
	 * @return String value of a config property.
	 */
	private String getString(String key, String defaultValue) {

		if (!xmlConfig.containsKey(key)) {
			xmlConfig.setProperty(key, defaultValue);
			logger.debug("Non-existing property: \"" + key + "\". Adding it with default value: \"" + defaultValue + "\"" );
		}

		return xmlConfig.getString(key, defaultValue);
	}

	/**
	 * Sets string value of a config property with the specified key.
	 * 
	 * @param key
	 *            Period separated config property path.
	 * @param value
	 *            The value of the config property.
	 */
	private void setString(String key, String value) {
		xmlConfig.setProperty(key, value);
		logger.debug("Property: \"" + key + "\". Value changed to : \"" + value + "\"" );
	}

	/**
	 * Creates a singleton instance to make config properties available. Call
	 * this on application startup.
	 */
	public static void createConfig() {
		if (config == null) {
			config = new Config();
		}
		// logger.debug("Config successfully initialized.");
	}

	private static String getDefaultAnalyseDir() {
		// True OS = Windows.
		boolean isTrueOs = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;

		if ( isTrueOs ) {
			return DEFAULT_ANALYSE_DIR_WINDOWS;
		}

		return DEFAULT_ANALYSE_DIR_LINUX;		
	}

	public static String getDefaultConfigDir() {
		return getDefaultAnalyseDir() + DEFAULT_CONFIG_DIR;
	}

	private static String getDefaultConfigFullPath() {
		return getDefaultConfigDir() + "/" + DEFAULT_CONFIG_FILE_NAME;
	}

	public static String getDefaultLogFilePath() {
		return getDefaultAnalyseDir() + DEFAULT_LOGS_DIR + "/" + DEFAULT_LOG_FILE_NAME;
	}

	// Below are the static getters of config properties that use config file.

	/**
	 * Returns the parent config path (writable part of the project), depending
	 * on the operating system (development purposes).
	 * 
	 * @return the configPath: "D:\\Projects\\Eclipse\\Java\\CaseStudy\\analyse"
	 *         for Windows and "/opt/analyse" for Linux
	 */
	public static String getAnalyseDir() {
		return config.getString("GeneralConfiguration.configPath", getDefaultAnalyseDir());
	}

	public static String getConfigFullPath() {
		return config.getString("GeneralConfiguration.configFileGeneral", getDefaultConfigFullPath());
	}

	public static String getConfigDir() {
		return config.getString("GeneralConfiguration.configDir", getAnalyseDir() + DEFAULT_CONFIG_DIR);
	}

	public static String getTempDir() {
		return config.getString("GeneralConfiguration.tempFolder", getAnalyseDir() + DEFAULT_TEMP_DIR);
	}
	
	public static String getStopwordsDir() {
		return config.getString("GeneralConfiguration.stopwordsFolder", getAnalyseDir() + DEFAULT_STOPWORDS_DIR);
	}

	public static String getLogsDir() {
		return config.getString("GeneralConfiguration.logsFolder", getAnalyseDir() + DEFAULT_LOGS_DIR);
	}

	public static String getXmlOutputDir() {
		return config.getString("GeneralConfiguration.xmlOutputFolder", 
				getDefaultAnalyseDir() + DEFAULT_XML_OUTPUT_DIR);
	}

	public static String getTransformsDir() {
		return config.getString("GeneralConfiguration.transformDir", getAnalyseDir() + DEFAULT_TRANSFORMS_DIR);
	}

	public static String getLogFileName() {
		return config.getString("GeneralConfiguration.logFileName", DEFAULT_LOG_FILE_NAME);
	}

	public static String getDbConnectionUrl() {
		return config.getString("DatabaseConnection.url", DEFAULT_DB_CONNECTION_URL);
	}

	public static String getDbConnectionDriver() {
		return config.getString("DatabaseConnection.driver", DEFAULT_DB_CONNECTION_DRIVER);
	}

	public static String getDbConnectionUser() {
		return config.getString("DatabaseConnection.user", DEFAULT_DB_CONNECTION_USER);
	}

	public static String getDbConnectionPwd() {
		return config.getString("DatabaseConnection.pwd", DEFAULT_DB_CONNECTION_PWD);
	}

	public static String getDbConnectionLogLevel() {
		return config.getString("DatabaseConnection.logLevel", DEFAULT_DB_CONNECTION_LOG_LEVEL);
	}

	public static String getDbConnectionSqlDialect() {
		return config.getString("DatabaseConnection.dialect", DEFAULT_DB_CONNECTION_DIALECT);
	}
	
	public static String getDbProviderClass() {
		return config.getString("DatabaseConnection.providerClass", DEFAULT_DB_PROVIDER_CLASS);
	}
	
	public static String getC3P0MinSize() {
		return config.getString("DatabaseConnection.c3p0.minSize", DEFAULT_DB_C3P0_MIN_SIZE);
	}

	public static String getC3P0MaxSize() {
		return config.getString("DatabaseConnection.c3p0.maxSize", DEFAULT_DB_C3P0_MAX_SIZE);
	}

	public static String getC3P0Timeout() {
		return config.getString("DatabaseConnection.c3p0.timeout", DEFAULT_DB_C3P0_TIMEOUT);
	}

	public static String getC3P0MaxStatements() {
		return config.getString("DatabaseConnection.c3p0.maxStatements", DEFAULT_DB_C3P0_MAX_STATEMENTS);
	}

	public static String getC3P0IdleTestPeriod() {
		return config.getString("DatabaseConnection.c3p0.idleTestPeriod", DEFAULT_DB_C3P0_IDLE_TEST_PERIOD);
	}

	//---------------------- CLASSIFICATION RELATED GETTERS ----------------
	//-----------STATUS RELATED GETTERS ----------------
	public static int getClassificationAlgorithm() {
		return Integer.parseInt(config.getString("ClassificationConfiguration.algorithm", DEFAULT_CLASSIFICATION_ALGORITHM));
	}

	public static void setClassificationAlgorithm(int value) {
		config.setString("ClassificationConfiguration.algorithm", String.valueOf(value));
	}
	
	public static boolean getClassificationEnabled() {
		return Boolean.parseBoolean(config.getAttr("ClassificationConfiguration", "enabled", DEFAULT_CLASSIFICATION_ENABLED));
	}

	public static void setClassificationEnabled(boolean value) {
		
		if ( !value ) {
			// if we need to unset it then delete all temporary values
			config.clearTree("ClassificationConfiguration.ClassificationStatus");
		}
		
		config.setAttr("ClassificationConfiguration", "enabled", String.valueOf(value));
	}
	
	public static int getClassificationUpdateTime() {
		return Integer.parseInt(config.getString("ClassificationConfiguration.updateTime", DEFAULT_CLASSIFICATION_UPDATE_TIME ));
	}

	public static void setClassificationUpdateTime(int value) {
		config.setString("ClassificationConfiguration.updateTime", String.valueOf(value));
	}

	public static String getClassificationTextFilter() {
		return config.getString("ClassificationConfiguration.ClassificationStatus.textFilter", DEFAULT_CLASSIFICATION_TEXT_FILTER);
	}
	
	public static void setClassificationTextFilter(String value) {
		config.setString("ClassificationConfiguration.ClassificationStatus.textFilter", value);
	}
	
	public static String getClassificationOnlyWords() {
		return config.getString("ClassificationConfiguration.ClassificationStatus.onlyWords", DEFAULT_CLASSIFICATION_ONLY_WORDS);
	}
	
	public static void setClassificationOnlyWords(String value) {
		if (value == null )
			value = "";
		config.setString("ClassificationConfiguration.ClassificationStatus.onlyWords", value);
	}
	
	public static Vector<String> getClassificationSelectedDiscussions() {
		 String value = config.getString("ClassificationConfiguration.ClassificationStatus.selectedDiscussions", DEFAULT_CLASSIFICATION_SELECTED_DISCUSSIONS);
		 StringTokenizer st = new StringTokenizer(value, "|");
		 
		 Vector<String> result = new Vector<String>();
		 while (st.hasMoreTokens()) {
			 result.addElement(st.nextToken());
		 }
		 
		 return result;
	}
	
	public static void setClassificationSelectedDiscussions(Vector<String> values) {
		String value = "";
		for (String word : values) {
			value += word + "|";
		}
		
		config.setString("ClassificationConfiguration.ClassificationStatus.selectedDiscussions", value);
	}
	
	//-----------OTHER CHOICES RELATED GETTERS ----------------
	public static String getInputClassificationDir() {
		return config.getString("ClassificationConfiguration.path", 
				getTempDir() + DEFAULT_INPUT_CLASSIFICATION_DIR);
	}
	
	public static String getEvalMatrixFile() {
		return config.getString("ClassificationConfiguration.dump-eval-matrix",
				getTempDir() + DEFAULT_EVAL_MATRIX_FILE);
	}

	public static int getMaxKeyphrases() {
		return Integer.parseInt(config.getString("ClassificationConfiguration.maxKeyphrases", String.valueOf(DEFAULT_MAX_KEYPHRASES)));
	}

	public static int getExtraInfo() {
		return Integer.parseInt(config.getString("ClassificationConfiguration.extra-info", String.valueOf(DEFAULT_EXTRA_INFO)));
	}

	public static String getExtraInfoFile() {
		return config.getString("ClassificationConfiguration.extraInfoFile", 
				getTempDir() + DEFAULT_EXTRA_INFO_FILE);
	}

	public static int getClassificationLanguage() {
		return Integer.parseInt(config.getString("ClassificationConfiguration.language", DEFAULT_CLASSIFICATION_LANGUAGE ));
	}

	public static void setClassificationLanguage(int value) {
		config.setString("ClassificationConfiguration.language", String.valueOf(value) );
	}

	public static int getClassificationMeasure() {
		return Integer.parseInt(config.getString("ClassificationConfiguration.CKP.measure", DEFAULT_CLASSIFICATION_MEASURE ));
	}

	public static void setClassificationMeasure(int value) {
		config.setString("ClassificationConfiguration.CKP.measure", String.valueOf(value) );
	}

	public static int getClassificationNumClusters() {
		return Integer.parseInt(config.getString("ClassificationConfiguration.k", DEFAULT_CLASSIFICATION_NUM_CLUSTERS ));
	}

	public static void setClassificationNumClusters(int value) {
		config.setString("ClassificationConfiguration.k", String.valueOf(value) );
	}

	public static int getClassificationMinWords() {
		return Integer.parseInt(config.getString("ClassificationConfiguration.CKP.min-words", DEFAULT_CLASSIFICATION_MIN_WORDS ));
	}

	public static void setClassificationMinWords(int value) {
		config.setString("ClassificationConfiguration.CKP.min-words", String.valueOf(value) );
	}
	
	public static String getFrenchStopwordsFile() {
		return config.getString("ClassificationConfiguration.frenchStopwords", getStopwordsDir() + DEFAULt_FRENCH_STOPWORDS_FILE);
	}
	
	public static String getEnglishStopwordsFile() {
		return config.getString("ClassificationConfiguration.englishStopwords", getStopwordsDir() + DEFAULT_ENGLISH_STOPWORDS_FILE);
	}
	
	public static int getClassificationDiscussionListLanguage() {
		return Integer.parseInt(config.getString("ClassificationConfiguration.discussionListLanguage", String.valueOf(DEFAULT_CLASSIFICATION_DISCUSSION_LIST_LANGUAGE)));
	}
	
	public static void setClassificationDiscussionListLanguage(int language) {
		config.setString("ClassificationConfiguration.discussionListLanguage", String.valueOf(language));
	}
	
	/**
	 * Methods that returns the stopwords file based on the current language.
	 * 
	 * @return the file with the stopwords for the current language.
	 */
	public static String getStopwordsFile() {
		
		if ( getClassificationLanguage() == LANGUAGE_FRENCH )
			return getFrenchStopwordsFile();
		
		return getEnglishStopwordsFile();
	}

	/**
	 * Loads the configuration parameters needed to pass to the CKP
	 * classification from the config XML file
	 * 
	 * @return a vector of Strings containing the command line parameters needed
	 *         for the classification.
	 */
	public static Vector<String> createCKPClassificationParameterList() {

		String[] DEFAULT_CKP_PARAMETER_NODE_VALUES = { "", 
			DEFAULT_CLASSIFICATION_NUM_CLUSTERS, 
			DEFAULT_CLASSIFICATION_LANGUAGE,
			getInputClassificationDir(), 
			DEFAULT_CLASSIFICATION_MEASURE, 
			DEFAULT_CLASSIFICATION_MIN_WORDS,
			getEvalMatrixFile(), 
			"1" 
			};

		String[] DEFAULT_CKP_PARAMETER_ATTR_VALUES = { "--debug", 
			"-k=",
			"--language=", 
			"--path=", 
			"--measure=", 
			"--min-words=",
			"--dump-eval-matrix=", 
			"--output-charset=" 
			};

		String[] DEFAULT_CKP_PARAMETER_NODES = { "ClassificationConfiguration.CKP.debug", 
			"ClassificationConfiguration.k", 
			"ClassificationConfiguration.language",
			"ClassificationConfiguration.path", 
			"ClassificationConfiguration.CKP.measure", 
			"ClassificationConfiguration.CKP.min-words", 
			"ClassificationConfiguration.dump-eval-matrix",
			"ClassificationConfiguration.CKP.output-charset" 
			};
		
		// the parameters that will be loaded
		Vector<String> parameters = new Vector<String>();
		String work;

		// get all the values that we need and put them into the vector
		for (int i = 0; i < DEFAULT_CKP_PARAMETER_NODES.length; i++) {
			String value = config.getAttr(DEFAULT_CKP_PARAMETER_NODES[i], "val", DEFAULT_CKP_PARAMETER_ATTR_VALUES[i]);
			work = value + config.getString(DEFAULT_CKP_PARAMETER_NODES[i], DEFAULT_CKP_PARAMETER_NODE_VALUES[i]);
			parameters.add(work);
		}

		// return the parameter vector
		return parameters;
	}

	//---------------------- RSS FEED RELATED GETTERS ----------------

	public static String getRssFeedUpdateTime() {
		return config.getAttr("RssFeedConfiguration", "time", DEFAULT_RSS_FEED_UPDATE_TIME);
	}

	public static void setRssFeedUpdateTime(String value) {
		config.setAttr("RssFeedConfiguration", "time", value);
	}
	
	public static String getBingApiAppId() {
		return config.getString("GeneralConfiguration.bingAppId", DEFAULT_BING_API_APP_ID);
	}

}
