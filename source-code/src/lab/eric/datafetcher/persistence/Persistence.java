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
package lab.eric.datafetcher.persistence;

import java.util.Locale;
import java.util.Properties;

import lab.eric.datafetcher.utils.config.Config;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Persistence {
	
	private static Logger logger = Logger.getLogger(Persistence.class);
	
	private static final String HIBERNATE_MAPPING_PATH = "lab/eric/datafetcher/entities/entities.hbm.xml";
	
	private static final String HIBERNATE_DIALECT_PROPERTY_NAME = "hibernate.dialect";
	private static final String HIBERNATE_DRIVER_PROPERTY_NAME = "hibernate.connection.driver_class";
	private static final String HIBERNATE_URL_PROPERTY_NAME = "hibernate.connection.url";
	private static final String HIBERNATE_USERNAME_PROPERTY_NAME = "hibernate.connection.username";
	private static final String HIBERNATE_PWD_PROPERTY_NAME = "hibernate.connection.password";
	private static final String HIBERNATE_SHOW_SQL_PROPERTY_NAME = "hibernate.show_sql";
	private static final String HIBERNATE_PROVIDER_CLASS = "hibernate.connection.provider_class";
	
	private static final String HIBERNATE_C3P0_MIN_SIZE_PROPERTY_NAME = "hibernate.c3p0.min_size";
	private static final String HIBERNATE_C3P0_MAX_SIZE_PROPERTY_NAME = "hibernate.c3p0.max_size";
	private static final String HIBERNATE_C3P0_TIMEOUT_PROPERTY_NAME = "hibernate.c3p0.timeout";
	private static final String HIBERNATE_C3P0_MAX_STATEMENTS_PROPERTY_NAME = "hibernate.c3p0.max_statements";
	private static final String HIBERNATE_C3P0_IDLE_TEST_PERIOD_PROPERTY_NAME = "hibernate.c3p0.idle_test_period";
	
	private static SessionFactory sessionFactory = null;
	
	public static void configureHibernate() {		
		Locale.setDefault(Locale.ENGLISH);
		Configuration cfg = new Configuration();
		cfg.addResource(HIBERNATE_MAPPING_PATH);
		
		Properties hibernateProps = new Properties();
		hibernateProps.put(HIBERNATE_DIALECT_PROPERTY_NAME, Config.getDbConnectionSqlDialect());
		hibernateProps.put(HIBERNATE_DRIVER_PROPERTY_NAME, Config.getDbConnectionDriver());
		hibernateProps.put(HIBERNATE_URL_PROPERTY_NAME, Config.getDbConnectionUrl());
		hibernateProps.put(HIBERNATE_USERNAME_PROPERTY_NAME, Config.getDbConnectionUser());
		hibernateProps.put(HIBERNATE_PWD_PROPERTY_NAME, Config.getDbConnectionPwd());
		hibernateProps.put(HIBERNATE_SHOW_SQL_PROPERTY_NAME, true);
		hibernateProps.put(HIBERNATE_PROVIDER_CLASS, Config.getDbProviderClass());

//		  <property name="hibernate.c3p0.min_size" value="2"/>
//		  <property name="hibernate.c3p0.max_size" value="20"/>
//		  <property name="hibernate.c3p0.timeout" value="300"/>
//		  <property name="hibernate.c3p0.max_statements" value="50"/>
//		  <property name="hibernate.c3p0.idle_test_period" value="3000"/>
//		  <property name="hibernate.connection.provider_class" value="org.hibernate.connection.C3P0ConnectionProvider"/>
		
		hibernateProps.put(HIBERNATE_C3P0_MIN_SIZE_PROPERTY_NAME, Config.getC3P0MinSize());
		hibernateProps.put(HIBERNATE_C3P0_MAX_SIZE_PROPERTY_NAME, Config.getC3P0MaxSize());
		hibernateProps.put(HIBERNATE_C3P0_TIMEOUT_PROPERTY_NAME, Config.getC3P0Timeout());
		hibernateProps.put(HIBERNATE_C3P0_MAX_STATEMENTS_PROPERTY_NAME, Config.getC3P0MaxStatements());
		hibernateProps.put(HIBERNATE_C3P0_IDLE_TEST_PERIOD_PROPERTY_NAME, Config.getC3P0IdleTestPeriod());
		
		cfg.setProperties(hibernateProps);
		
		sessionFactory = cfg.buildSessionFactory();
		
		logger.info("Persistence initialized successfully.");
	}

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			configureHibernate();
		}
		
		return sessionFactory;
	}
}
