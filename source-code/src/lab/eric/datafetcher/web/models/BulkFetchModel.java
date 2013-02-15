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

import lab.eric.datafetcher.utils.config.Config;

public class BulkFetchModel {
	
	private String bingAppId = Config.getBingApiAppId();

	public void setBingAppId(String bingAppId) {
		this.bingAppId = bingAppId;
	}

	public String getBingAppId() {
		return bingAppId;
	}		
}
