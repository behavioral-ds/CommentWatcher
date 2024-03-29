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
/*
 * MenuPointListener.java
 *
 * Created on March 22, 2007, 4:08 PM
 *
 * Copyright 2007 Grotto Networking
 */

package lab.eric.visualizer.graph;

import java.awt.geom.Point2D;

/**
 * Used to set the point at which the mouse was clicked for those menu items
 * interested in this information.  Useful, for example, if you want to bring up
 * a dialog box right at the point the mouse was clicked.
 * The PopupVertexEdgeMenuMousePlugin checks to see if a menu component implements
 * this interface and if so calls it to set the point.
 * @author Dr. Greg M. Bernstein
 */
public interface MenuPointListener {
    /**
     * Sets the point of the mouse click.
     * @param point 
     */
 void   setPoint(Point2D point);
    
}
