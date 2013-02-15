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
package lab.eric.visualizer.lang;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lab.eric.visualizer.model.ConfigurationManager;

public class Messages {
	private static final String BUNDLE_NAME_FR = "lab.eric.visualizer.lang.messagesFR"; //$NON-NLS-1$
	private static final String BUNDLE_NAME_EN = "lab.eric.visualizer.lang.messagesEN"; //$NON-NLS-1$

	private static ResourceBundle RESOURCE_BUNDLE = null;

	private Messages() {
	}

	public static String getString(String key) {
		
		if ( RESOURCE_BUNDLE == null ) {
			if ( ConfigurationManager.applicationLang.compareTo("FR") == 0)
				RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME_FR);
			else
				RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME_EN);
		}
		
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
