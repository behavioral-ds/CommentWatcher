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
package lab.eric.datafetcher.web.controllers.async;

import java.util.Observable;
import java.util.Observer;

public class FetchingProcessObserver implements Observer {
	
	private FetchingProcessStatus status = FetchingProcessStatus.PROGRESS;	

	@Override
	public void update(Observable o, Object arg) {
		if (!(o instanceof FetchingProcess)) {
			throw new IllegalArgumentException("o should be of type FetchingProcess");
		}
		
		status = (FetchingProcessStatus) arg;
	}

	public FetchingProcessStatus getStatus() {
		return status;
	}
}
