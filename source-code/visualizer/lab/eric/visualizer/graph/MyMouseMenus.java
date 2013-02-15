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
 * MyMouseMenus.java
 *
 * Created on March 21, 2007, 3:34 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package lab.eric.visualizer.graph;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import lab.eric.visualizer.graph.GraphElements.MyVertex;

/**
 * A collection of classes used to assemble popup mouse menus for the custom
 * edges and vertices developed in this example.
 * 
 * @author Dr. Greg M. Bernstein
 */
public class MyMouseMenus {

	public static class EdgeMenu extends JPopupMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// private JFrame frame;
		public EdgeMenu(final JApplet applet) {
			super("Edge Menu");
			// this.frame = frame;
			// this.add(new DeleteEdgeMenuItem<GraphElements.MyEdge>());
			// this.addSeparator();
			this.add(new WeightDisplay());
			this.add(new CapacityDisplay());
			this.addSeparator();
			this.add(new EdgePropItem(applet));
		}

	}

	public static class EdgePropItem extends JMenuItem implements
			EdgeMenuListener<lab.eric.visualizer.graph.GraphElements.MyEdge>,
			MenuPointListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		GraphElements.MyEdge edge;
		VisualizationViewer visComp;
		Point2D point;

		public void setEdgeAndView(GraphElements.MyEdge edge,
				VisualizationViewer visComp) {
			this.edge = edge;
			this.visComp = visComp;
		}

		public void setPoint(Point2D point) {
			this.point = point;
		}

		public EdgePropItem(final JApplet applet) {
			super("Edit Edge Properties...");
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EdgePropertyDialog dialog = new EdgePropertyDialog(applet,
							edge);
					dialog.setLocation((int) point.getX() + applet.getX(),
							(int) point.getY() + applet.getY());
					dialog.setVisible(true);
				}

			});
		}

	}

	public static class WeightDisplay extends JMenuItem implements
			EdgeMenuListener<lab.eric.visualizer.graph.GraphElements.MyEdge> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void setEdgeAndView(GraphElements.MyEdge e,
				VisualizationViewer visComp) {
			this.setText("Weight " + e + " = " + e.getWeight());
		}
	}

	public static class CapacityDisplay extends JMenuItem implements
			EdgeMenuListener<lab.eric.visualizer.graph.GraphElements.MyEdge> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void setEdgeAndView(GraphElements.MyEdge e,
				VisualizationViewer visComp) {
			this.setText("Capacity " + e + " = " + e.getCapacity());
		}
	}

	public static class VertexMenu extends JPopupMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public VertexMenu() {
			super("Vertex Menu");
			this.add(new DeleteVertexMenuItem<GraphElements.MyVertex>());
			this.addSeparator();
			this.add(new InDegreeDisplay());
			this.add(new OutDegreeDisplay());
			this.add(new WeightInDegreeDisplay());
			this.add(new WeightOutDegreeDisplay());
			this.add(new BtwnessDisplay());
			this.add(new ClosenessDisplay());
			this.add(new NoOfTopicsDisplay());
			this.add(new NoOfPostsDisplay());
			this.add(new NoOfInitsDisplay());
			this.add(new NoOfAlonePostsDisplay());

		}
	}

	public static class NoOfAlonePostsDisplay extends JMenuItem implements
			VertexMenuListener<GraphElements.MyVertex> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setVertexAndView(MyVertex v, VisualizationViewer visView) {
			// TODO Auto-generated method stub
			this.setText("Number of Alone Posts " + v.getNoOfAlonePosts());
		}

	}

	public static class NoOfInitsDisplay extends JMenuItem implements
			VertexMenuListener<GraphElements.MyVertex> {

		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setVertexAndView(MyVertex v, VisualizationViewer visView) {
			// TODO Auto-generated method stub
			this.setText("Number of Inits " + v.getNoOfInits());
		}

	}

	public static class NoOfPostsDisplay extends JMenuItem implements
			VertexMenuListener<GraphElements.MyVertex> {

		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setVertexAndView(MyVertex v, VisualizationViewer visView) {
			// TODO Auto-generated method stub
			this.setText("Number of Posts " + v.getNoOfPosts());
		}

	}

	public static class NoOfTopicsDisplay extends JMenuItem implements
			VertexMenuListener<GraphElements.MyVertex> {

		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setVertexAndView(MyVertex v, VisualizationViewer visView) {
			// TODO Auto-generated method stub
			this.setText("Number of Topics " + v.getNoOftopics());
		}

	}

	public static class InDegreeDisplay extends JMenuItem implements
			VertexMenuListener<GraphElements.MyVertex> {

		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setVertexAndView(MyVertex v, VisualizationViewer visView) {
			// TODO Auto-generated method stub
			this.setText("In-Degree " + v.getInDegree());
		}

	}
	
	
	public static class WeightInDegreeDisplay extends JMenuItem implements
			VertexMenuListener<GraphElements.MyVertex> {

		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setVertexAndView(MyVertex v, VisualizationViewer visView) {
			// TODO Auto-generated method stub
			this.setText("Weight In-Degree " + v.getWeightInDegree());
		}

	}
	
	

	public static class OutDegreeDisplay extends JMenuItem implements
			VertexMenuListener<GraphElements.MyVertex> {

		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setVertexAndView(MyVertex v, VisualizationViewer visView) {
			this.setText("Out-Degree " + v.getOutDegree());

		}

	}
	
	public static class WeightOutDegreeDisplay extends JMenuItem implements
			VertexMenuListener<GraphElements.MyVertex> {

		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setVertexAndView(MyVertex v, VisualizationViewer visView) {
			this.setText("Wright Out-Degree " + v.getWeightOutDegree());

		}

	}


	public static class BtwnessDisplay extends JMenuItem implements
			VertexMenuListener<GraphElements.MyVertex> {

		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setVertexAndView(MyVertex v, VisualizationViewer visView) {
			// TODO Auto-generated method stub
			this.setText("BTWness " + v.getBetweenness());
		}

	}

	public static class ClosenessDisplay extends JMenuItem implements
			VertexMenuListener<GraphElements.MyVertex> {

		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;

		@Override
		public void setVertexAndView(MyVertex v, VisualizationViewer visView) {
			// TODO Auto-generated method stub
			this.setText("Closeness " + v.getCloseness());
		}

	}

}
