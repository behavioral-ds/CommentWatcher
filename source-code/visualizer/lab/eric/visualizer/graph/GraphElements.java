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
 * GraphElements.java
 *
 * Created on March 21, 2007, 9:57 AM
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package lab.eric.visualizer.graph;

import java.awt.Color;
import java.awt.Paint;
import java.text.ParseException;

import org.apache.commons.collections15.Factory;


/**
 *
 * @author Dr. Greg M. Bernstein
 */
public class GraphElements {
    
    /** Creates a new instance of GraphElements */
    public GraphElements() {
    }
    
    public static class MyVertex  {
        private String name;
        private String title; 
        private Paint color; 
        private double betweenness;
        private Double closeness;
        private int NoOfTopics;
        private int NoOfPosts;
        private int NoOfInits;
        private int NoOfAlonePosts;
		private Integer inDegree;
        private Integer outDegree;
        private Integer weightInDegree;
        private Integer weightOutDegree;
        
        
        public Integer getInDegree() {
			return inDegree;
		}

		public void setInDegree(Integer inDegree) {
			this.inDegree = inDegree;
		}

		public Integer getOutDegree() {
			return outDegree;
		}

		public void setOutDegree(Integer outDegree) {
			this.outDegree = outDegree;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MyVertex other = (MyVertex) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		private boolean packetSwitchCapable;
        private boolean tdmSwitchCapable;
        
        public MyVertex(String name,Paint color) {
          
        	this.name = name;
            this.color = color;
        }
        
        public MyVertex(String name){
        	this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isPacketSwitchCapable() {
            return packetSwitchCapable;
        }

        public void setPacketSwitchCapable(boolean packetSwitchCapable) {
            this.packetSwitchCapable = packetSwitchCapable;
        }

        public boolean isTdmSwitchCapable() {
            return tdmSwitchCapable;
        }

        public void setTdmSwitchCapable(boolean tdmSwitchCapable) {
            this.tdmSwitchCapable = tdmSwitchCapable;
        }
        
        public String toString() {
            return name;
        }

		public void setColor(Paint color) {
			this.color = color;
		}

		public Paint getColor() {
			return color;
		}

		public void setBetweenness(double betweenness) {
			this.betweenness = betweenness;
		}

		public double getBetweenness() {
			//return betweenness;
			java.text.DecimalFormat df = new
			java.text.DecimalFormat("###.###");
			double val = betweenness;
			try {
				val = df.parse(df.format(val)).doubleValue();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return val;
		}

		public void setCloseness(Double closeness) {
			this.closeness = closeness;
		}

		public Double getCloseness() {
			java.text.DecimalFormat df = new
			java.text.DecimalFormat("#.###");
			double val = closeness;
			try {
				val = df.parse(df.format(val)).doubleValue();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return val;
		}

		public void setNoOftopics(int noOftopics) {
			NoOfTopics = noOftopics;
		}

		public int getNoOftopics() {
			return NoOfTopics;
		}

		public void setNoOfPosts(int noOfPosts) {
			NoOfPosts = noOfPosts;
		}

		public int getNoOfPosts() {
			return NoOfPosts;
		}

		public void setNoOfInits(int noOfInits) {
			NoOfInits = noOfInits;
		}

		public int getNoOfInits() {
			return NoOfInits;
		}

		public void setNoOfAlonePosts(int noOfAlonePosts) {
			NoOfAlonePosts = noOfAlonePosts;
		}

		public int getNoOfAlonePosts() {
			return NoOfAlonePosts;
		}

		public void setWeightInDegree(Integer weightInDegree) {
			this.weightInDegree = weightInDegree;
		}

		public Integer getWeightInDegree() {
			return weightInDegree;
		}

		public void setWeightOutDegree(Integer weightOutDegree) {
			this.weightOutDegree = weightOutDegree;
		}

		public Integer getWeightOutDegree() {
			return weightOutDegree;
		}
    }
    
    public static class MyEdge {
        private double capacity;
        private double weight;
        private String name;
        private Color color;

        @Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MyEdge other = (MyEdge) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		public MyEdge(String name, Color color) {
            this.name = name;
            this.color = color;
        }
        public double getCapacity() {
            return capacity;
        }

        public void setCapacity(double capacity) {
            this.capacity = capacity;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }             
        
        public String toString() {
            return name;
        }
		public void setColor(Color color) {
			this.color = color;
		}
		public Color getColor() {
			return color;
		}
    }
    
    // Single factory for creating Vertices...
    public static class MyVertexFactory implements Factory<MyVertex> {
        private static int nodeCount = 0;
        private static boolean defaultPSC = false;
        private static boolean defaultTDM = true;
        private static MyVertexFactory instance = new MyVertexFactory();
        
        private MyVertexFactory() {            
        }
        
        public static MyVertexFactory getInstance() {
            return instance;
        }
        
        public GraphElements.MyVertex create() {
            String name = "Node" + nodeCount++;
            MyVertex v = new MyVertex(name,Color.BLACK);
            v.setPacketSwitchCapable(defaultPSC);
            v.setTdmSwitchCapable(defaultTDM);
            return v;
        }        

        public static boolean isDefaultPSC() {
            return defaultPSC;
        }

        public static void setDefaultPSC(boolean aDefaultPSC) {
            defaultPSC = aDefaultPSC;
        }

        public static boolean isDefaultTDM() {
            return defaultTDM;
        }

        public static void setDefaultTDM(boolean aDefaultTDM) {
            defaultTDM = aDefaultTDM;
        }
    }
    
    // Singleton factory for creating Edges...
    public static class MyEdgeFactory implements Factory<MyEdge> {
        private static int linkCount = 0;
        private static double defaultWeight;
        private static double defaultCapacity;

        private static MyEdgeFactory instance = new MyEdgeFactory();
        
        private MyEdgeFactory() {            
        }
        
        public static MyEdgeFactory getInstance() {
            return instance;
        }
        
        public GraphElements.MyEdge create() {
            String name = "Link" + linkCount++;
            MyEdge link = new MyEdge(name , Color.RED);
            link.setWeight(defaultWeight);
            link.setCapacity(defaultCapacity);
            return link;
        }    

        public static double getDefaultWeight() {
            return defaultWeight;
        }

        public static void setDefaultWeight(double aDefaultWeight) {
            defaultWeight = aDefaultWeight;
        }

        public static double getDefaultCapacity() {
            return defaultCapacity;
        }

        public static void setDefaultCapacity(double aDefaultCapacity) {
            defaultCapacity = aDefaultCapacity;
        }
        
    }

}
