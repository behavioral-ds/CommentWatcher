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
package lab.eric.visualizer.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyVetoException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JApplet;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.commons.collections15.Transformer;

import lab.eric.visualizer.graph.GraphElements.MyEdge;
import lab.eric.visualizer.graph.GraphElements.MyVertex;
import lab.eric.visualizer.model.Comment;
import lab.eric.visualizer.model.CommentManager;
import lab.eric.visualizer.model.ConfigurationManager;
import lab.eric.visualizer.model.Source;
import lab.eric.visualizer.model.Topic;
import lab.eric.visualizer.model.TopicsContainer;
import lab.eric.visualizer.model.XmlFileParser;
import lab.eric.visualizer.view.MDIDesktopPane;
import lab.eric.visualizer.view.PreferencesDialog;
import lab.eric.visualizer.view.WindowMenu;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.layout.FRLayout;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import java.util.Hashtable;

public class CitationGraph extends JApplet {

	private static final long serialVersionUID = 1916865979078083936L;
	private int minNumberOfcomments = 0;
	private String userSearch = "";
	private boolean topic = true;
	JPanel topicCheckBoxPanel;
	JPanel commentCheckBoxPanel;
	private JCheckBox[] topicCheckBoxes;
	private JCheckBox[] commentCheckBoxes;
	private Hashtable<Color, Boolean> topicHashTable = null;
	private Hashtable<Color, Boolean> commentColorHashTable = null;
	
	Map<MyVertex, Integer> vertexCommentCountHashtable = new HashMap<MyVertex, Integer>();
	Map<String, Set<Topic>> vertexNoOfTopicsHashtable = new HashMap<String, Set<Topic>>();
	Map<String, MyVertex> vertexNoOfPostsHashtable = new HashMap<String, MyVertex>();
	Map<String, MyVertex> vertexNoOfInitHashtable = new HashMap<String, MyVertex>();
	Map<String, MyVertex> vertexNoOfAlonePostsHashtable = new HashMap<String, MyVertex>();
	Set<Integer> firstLevelCommentIdsSet = new HashSet<Integer>();

	private XmlFileParser xmlFileParser;
	private Hashtable<String, Source> sourceHashtable;
	private TopicsContainer topicsContainer;

	private Hashtable<Integer, SortedSet<Comment>> commentTopicsSortedHashtable = new Hashtable<Integer, SortedSet<Comment>>();
	private Map<Integer, Set<Topic>> commentTopicsHashMap = new HashMap<Integer, Set<Topic>>();

	Set<Integer> topicHashSet = new HashSet<Integer>();
	
	
	// below are the variables which define the boundaries of the number of posts for each user
	int X = 1;
	int Y1 = 2;
	int Y2 = 10;
	int Z = 10;
	/*
	 * this method is used to return a distinguished color per no of posts
	 */
	public Paint getCommentCountColor(int noOfPosts) {
		Paint color = null;
		if (X == 1 && noOfPosts == 0)
			color = Color.ORANGE;
		else if (noOfPosts == X)
			color = Color.ORANGE;
		else if (noOfPosts <= Y2 && noOfPosts >= Y1)
			color = Color.GREEN;
		else if (noOfPosts > Z)
			color = Color.RED;
		else
			color = Color.GRAY;

		return color;
	}

	private void usersPropsInit() {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			ConfigurationManager.dbConnect();
			conn = ConfigurationManager.dbConnection;

			// to find those users who initiated a thread we need to have the
			// first level comments,
			// following query is used to achieve that ,
			String query = " select distinct NUM_MESG from datasource.messgearticle M1 where "
				+ " exists (select NUM_MESG from datasource.messgearticle M2 where M2.replies=0 "
				+ " and M1.replies = M2.NUM_MESG)";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			// firstLevelCommentIdsSet is a set data structure which is used to
			// store the result of previous query
			// which will be used later
			while (rs.next()) {
				Integer mesgId = rs.getInt("NUM_MESG");
				firstLevelCommentIdsSet.add(mesgId);
			}
			rs.close();
			stmt.close();
			// the following query is used to find the number of comments the
			// user had where no one has put any comment on
			// , the result is stored in a hashtable to be populated in
			// corresponding vertex
			query = " select nom_auteur author, count(nom_auteur) alone_count from datasource.messgearticle M1 where "
				+ " exists (select NUM_MESG from datasource.messgearticle M2 where M2.replies=0 "
				+ " and M1.replies = M2.NUM_MESG) and not exists (SELECT id "
				+ " from datasource.citation C where M1.NUM_MESG = C.mesg_target) group by nom_auteur";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int aloneCount = rs.getInt("alone_count");
				String author = rs.getString("author");
				MyVertex vertex = new MyVertex(author);
				vertex.setNoOfAlonePosts(aloneCount);
				vertexNoOfAlonePostsHashtable.put(author, vertex);
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {

			try {
				rs.close();
				stmt.close();
				conn.close();
				Runtime r = Runtime.getRuntime();
				r.gc();
			} catch (SQLException e) {

				e.printStackTrace();
			}

		}
	}

	/*
	 * this method is used to set the db url string from the applet to call the
	 * servlet which is responsible for sending the xml file to applet running
	 * on the client side (user's browser)
	 */
	private void xmlFileParse() {
		// set the database location (same as the Applet source)
		if (ConfigurationManager.webHost == null) {
			ConfigurationManager.webHost = this.getCodeBase().toString();

		}
		File fileToParse = null;
		xmlFileParser = new XmlFileParser(fileToParse);
		URL home = null;
		try {
			home = new URL(ConfigurationManager.webHost);
		} catch (MalformedURLException e) {
			ConfigurationManager.showErrorMessage(e, true);
			e.printStackTrace();
		}
		xmlFileParser.setConnexionDetails(home, "citation");
		Thread t = new Thread(xmlFileParser);
		t.start();

		try {
			t.join();
		} catch (InterruptedException e) {
			ConfigurationManager.showErrorMessage(e, false);
			e.printStackTrace();
		}
	}

	/*
	 * this method is used to create a number of hash objects which all have the
	 * comment id as the key, the first one is commentTopicsSortedHashtable
	 * which its values are comments sorted based on their weights the second
	 * one is commentTopicsHashMap which its values are set topics, the last
	 * this this method is taking care is finding all the topics in the xml file
	 * and storing them in topicHashSet
	 */
	private void createCommenHashObjects() {
		CommentManager commentManager = xmlFileParser.getCommentManager();

		sourceHashtable = commentManager.getSourceHashtable();
		Collection<Source> sources = sourceHashtable.values();

		for (Source source : sources) {
			Collection<ArrayList<Comment>> topicComments = source
			.getTopicHashtable().values();
			for (ArrayList<Comment> comments : topicComments) {
				for (Comment comment : comments) {

					if (comment.getTopic().getTopicID() > -1) {

						if (commentTopicsSortedHashtable.containsKey(comment
								.getCommentID())) {

							SortedSet<Comment> sortedSet = commentTopicsSortedHashtable
							.get(comment.getCommentID());
							sortedSet.add(comment);
							commentTopicsSortedHashtable.put(
									comment.getCommentID(), sortedSet);
							Set<Topic> commentTopicSet = commentTopicsHashMap
							.get(comment.getCommentID());
							commentTopicSet.add(comment.getTopic());
							commentTopicsHashMap.put(comment.getCommentID(),
									commentTopicSet);
							topicHashSet.add(comment.getTopic().getTopicID());

						} else {
							Comparator<Comment> comparator = new Comparator<Comment>() {
								@Override
								public int compare(Comment o1, Comment o2) {
									if (o1.getCommentWeight() < o2
											.getCommentWeight())
										return -1;
									else
										return 1;
								}
							};
							SortedSet<Comment> sortedSet = new TreeSet<Comment>(
									comparator);
							sortedSet.add(comment);
							commentTopicsSortedHashtable.put(
									comment.getCommentID(), sortedSet);
							Set<Topic> topicSet = new HashSet<Topic>();
							topicSet.add(comment.getTopic());
							commentTopicsHashMap.put(comment.getCommentID(),
									topicSet);
							topicHashSet.add(comment.getTopic().getTopicID());
						}
					}

				}
			}
		}

		topicsContainer = xmlFileParser.getTopicsContainer();
	}

	public void init() {

		usersPropsInit();
		xmlFileParse();
		createCommenHashObjects();
		createColorsHashtable();
		draw(topicCalc());
	}

	/**
	 * Creates unique colors for topics and saves them in a static hashtable of
	 * ConfigurationManager
	 * 
	 */
	private void createColorsHashtable() {

		ArrayList<Topic> topics = topicsContainer.getTopicList();
		int topicCount = topics.size();
		ConfigurationManager.makeTopicColors(topicCount, false);
		for (int i = 0; i < topicCount; i++)
			ConfigurationManager.topicColorsHashtable.put(topics.get(i),
					ConfigurationManager.topicColors[i]);
	}

	public Graph<MyVertex, MyEdge> commentCalc() {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Graph<GraphElements.MyVertex, GraphElements.MyEdge> g = null;
		Graph<GraphElements.MyVertex, GraphElements.MyEdge> weightGraph = null;
		try {

			ConfigurationManager.dbConnect();
			conn = ConfigurationManager.dbConnection;

			String  query = "select  author_source, mesg_source, author_target , VSource.source_count source_count, VTarget.target_count target_count from " 
				+ "datasource.citation T, "
				+ "(select author_source authorsource, count(author_source) source_count from datasource.citation group by author_source) VSource, "
				+ "(select author_target authortarget, count(author_target) target_count from datasource.citation group by author_target) VTarget "
				+ "where VSource.authorsource = T.author_source AND VTarget.authortarget=T.author_target" 
				+ " AND source_count > "+ minNumberOfcomments
				+ " AND"
				+ " (author_source like '%"
				+ userSearch
				+ "%' OR author_target like '%"
				+ userSearch
				+ "%') "
				+"order by source_count desc";

			g = new SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge>();
			weightGraph = new SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge>();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			GraphElements.MyEdgeFactory.setDefaultCapacity(192.0);
			GraphElements.MyEdgeFactory.setDefaultWeight(5.0);
			int sourceCount = 0;

			while (rs.next()) {
				String authorSource = rs.getString("author_source");
				String authorTarget = rs.getString("author_target");
				Integer mesg_source = rs.getInt("mesg_source");
				if (!commentTopicsSortedHashtable.containsKey(mesg_source))
					continue;
				sourceCount = rs.getInt("source_count");
				
				GraphElements.MyEdge myEdge = new MyEdge(authorSource+authorTarget,
							Color.BLACK);
				GraphElements.MyEdge myWeightEdge = new MyEdge(authorSource+authorTarget+mesg_source,
						Color.BLACK);
				
				Paint sourceColor = getCommentCountColor(sourceCount);

				Paint targetColor = getCommentCountColor(0);
				if (commentColorHashTable != null) {

					Boolean sourceColorBoolean = commentColorHashTable
					.get((Color) sourceColor);

					Boolean targetColorBoolean = commentColorHashTable
					.get((Color) targetColor);

					if (sourceColorBoolean.booleanValue()) {

						GraphElements.MyVertex mySourceVertex = new MyVertex(
								authorSource, sourceColor);

						GraphElements.MyVertex myTargetVertex = new MyVertex(
								authorTarget, targetColor);

						Integer hashColorNo = vertexCommentCountHashtable
						.get(myTargetVertex);

						targetColorBoolean = commentColorHashTable
						.get((Color) getCommentCountColor(hashColorNo
								.intValue()));

						if (targetColorBoolean) {
							g.addEdge(myEdge, mySourceVertex, myTargetVertex,
									EdgeType.DIRECTED);
							
							weightGraph.addEdge(myWeightEdge, mySourceVertex, myTargetVertex,
									EdgeType.DIRECTED);
						
							
						} else {
							g.addVertex(mySourceVertex);
							weightGraph.addVertex(mySourceVertex);
						}

					}
				} else {

					GraphElements.MyVertex mySourceVertex = new MyVertex(
							authorSource, sourceColor);

					// here we use a hash table to hold the number of posts
					// if the vertex is already present in hash table its value
					// is compared to the current vertex
					// and the greater value is stored in hash table
					if (vertexCommentCountHashtable.containsKey(mySourceVertex)) {
						Integer hashColorNo = vertexCommentCountHashtable
						.get(mySourceVertex);
						if (hashColorNo.intValue() > sourceCount) {
							mySourceVertex
							.setColor(getCommentCountColor(hashColorNo
									.intValue()));
						} else {
							vertexCommentCountHashtable.put(mySourceVertex,
									sourceCount);
						}

					} else {

						vertexCommentCountHashtable.put(mySourceVertex,
								sourceCount);
					}

					GraphElements.MyVertex myTargetVertex = new MyVertex(
							authorTarget, targetColor);
					// for the target vertex since by default the number of
					// posts is 0 we need to
					// make sure if there is not any vertex with the same name
					// in the vertex ,
					// other wise the greater value is color determinant for the
					// target vertex
					if (vertexCommentCountHashtable.containsKey(myTargetVertex)) {
						Integer hashColorNo = vertexCommentCountHashtable
						.get(myTargetVertex);
						if (hashColorNo.intValue() > 0) {
							myTargetVertex
							.setColor(getCommentCountColor(hashColorNo
									.intValue()));
						} else {
							vertexCommentCountHashtable.put(myTargetVertex, 0);
						}

					} else {

						vertexCommentCountHashtable.put(myTargetVertex, 0);

					}

					g.addEdge(myEdge, mySourceVertex, myTargetVertex,
							EdgeType.DIRECTED);
					
					weightGraph.addEdge(myEdge, mySourceVertex, myTargetVertex,
							EdgeType.DIRECTED);
					

				}

			}

			if (commentColorHashTable == null) {
				commentColorHashTable = new Hashtable<Color, Boolean>(3);
				if (commentCheckBoxes == null)
					commentCheckBoxes = new JCheckBox[4];

				int[] array = { X, Y1, Z + 1, -1 };
				String[] label = { "posts equal to", "between(inclusive)",
						"greater than(exclusive)", "other" };

				for (int i = 0; i < array.length; i++) {

					final int current = array[i];

					commentColorHashTable.put(
							(Color) getCommentCountColor(array[i]),
							new Boolean(true));
					// if ( i!=(array.length-1) ) {
					if (commentCheckBoxes[i] == null)
						commentCheckBoxes[i] = new JCheckBox(label[i]);
					commentCheckBoxes[i]
					                  .setForeground((Color) getCommentCountColor(current));
					commentCheckBoxes[i].setSelected(true);
					commentCheckBoxes[i]
					                  .addActionListener(new ActionListener() {

					                	  @Override
					                	  public void actionPerformed(ActionEvent arg0) {

					                		  JCheckBox checkBox = (JCheckBox) arg0
					                		  .getSource();

					                		  commentColorHashTable
					                		  .put((Color) getCommentCountColor(current),
					                				  new Boolean(checkBox
					                						  .isSelected()));

					                		  Graph<MyVertex, MyEdge> graph = commentCalc();
					                		  layout.setGraph(graph);
					                		  vv.repaint();
					                	  }

					                  });
				}
				// }
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				rs.close();
				stmt.close();
				conn.close();
				Runtime r = Runtime.getRuntime();
				r.gc();
			} catch (SQLException e) {

				e.printStackTrace();
			}

		}

		ClosenessCentrality<MyVertex, MyEdge> closeness = new ClosenessCentrality<GraphElements.MyVertex, GraphElements.MyEdge>(
				g);

		BetweennessCentrality<MyVertex, MyEdge> ranker = new BetweennessCentrality<MyVertex, MyEdge>(
				g);
		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();

		for (Iterator<MyVertex> iterator = g.getVertices().iterator(); iterator
		.hasNext();) {

			MyVertex myVertex = (MyVertex) iterator.next();

			if (vertexNoOfTopicsHashtable.containsKey(myVertex.getName()))
				myVertex.setNoOftopics(vertexNoOfTopicsHashtable.get(
						myVertex.getName()).size());
			else
				myVertex.setNoOftopics(1);

			if (vertexNoOfPostsHashtable.containsKey(myVertex.getName()))
				myVertex.setNoOfPosts(vertexNoOfPostsHashtable.get(
						myVertex.getName()).getNoOfPosts());
			else
				myVertex.setNoOfPosts(0);

			if (vertexNoOfInitHashtable.containsKey(myVertex.getName()))
				myVertex.setNoOfInits(vertexNoOfInitHashtable.get(
						myVertex.getName()).getNoOfInits());
			else
				myVertex.setNoOfInits(0);

			if (vertexNoOfAlonePostsHashtable.containsKey(myVertex.getName()))
				myVertex.setNoOfAlonePosts(vertexNoOfAlonePostsHashtable.get(
						myVertex.getName()).getNoOfAlonePosts());
			else
				myVertex.setNoOfAlonePosts(0);

			if (vertexCommentCountHashtable.containsKey(myVertex)) {
				Integer hashColorNo = vertexCommentCountHashtable.get(myVertex);
				myVertex.setColor(getCommentCountColor(hashColorNo));
			} else {
				myVertex.setColor(getCommentCountColor(0));
			}
			myVertex.setInDegree(g.inDegree(myVertex));
			myVertex.setOutDegree(g.outDegree(myVertex));
			
			myVertex.setWeightInDegree(weightGraph.inDegree(myVertex));
			myVertex.setWeightOutDegree(weightGraph.outDegree(myVertex));
			
			myVertex.setBetweenness(ranker.getVertexRankScore(myVertex));
			myVertex.setCloseness(closeness.getVertexScore(myVertex));

		}

		return g;

	}

	
	public Graph<MyVertex, MyEdge> topicCalc() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Graph<GraphElements.MyVertex, GraphElements.MyEdge> weightGraph = null;
		Graph<GraphElements.MyVertex, GraphElements.MyEdge> graph = null;
		try {
			ConfigurationManager.dbConnect();
			conn = ConfigurationManager.dbConnection;
			// the following query is used to find all the comments a user has
			// put
			String query = "select id, author_source, mesg_source , mesg_target, author_target ,"
				+ " VSource.source_count source_count from datasource.citation T "
				+ " ,(select author_source authorsource, count(author_source) source_count "
				+ " from datasource.citation group by author_source) VSource "
				+ " where VSource.authorsource = T.author_source  and author_source in "
				+ " (SELECT author_source  FROM datasource.citation group by author_source "
				+ " having count(author_source)> "
				+ minNumberOfcomments
				+ ")"
				+ " AND"
				+ " (author_source like '%"
				+ userSearch
				+ "%' OR author_target like '%"
				+ userSearch
				+ "%') "
				+ " order by author_source, author_target";
			weightGraph = new SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge>();
			graph = new SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge>();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			GraphElements.MyEdgeFactory.setDefaultCapacity(192.0);
			GraphElements.MyEdgeFactory.setDefaultWeight(5.0);
			int numberOfPosts = 0;
			while (rs.next()) {
				String authorSource = rs.getString("author_source");
				String authorTarget = rs.getString("author_target");
				Integer mesgSource = rs.getInt("mesg_source");
				Integer mesgTarget = rs.getInt("mesg_target");
				numberOfPosts = rs.getInt("source_count");
				int id = rs.getInt("id");
				Color edgeColor = null;
				// the following code is used to find the topic which is common between source and target comments which has the 
				// least weight, this topic will determine the edge color
				if (commentTopicsSortedHashtable.containsKey(mesgSource)) {

					// first the sorted set of both source and target comments
					SortedSet<Comment> sourceSortedSet = commentTopicsSortedHashtable
					.get(mesgSource);
					SortedSet<Comment> targetSortedSet = commentTopicsSortedHashtable
					.get(mesgTarget);
					
					// since the target's set dose not require to be sorted and only is required to be tested to make sure it 
					//includes the common topic so all the topics are stored in targetColorSet 
					Set<Integer> targetColorSet = new HashSet<Integer>();
					for (Comment targetComment : targetSortedSet)
						targetColorSet.add(targetComment.getTopic()
								.getTopicID());
					
					// for each comment in the sorted set of comments of source comment id if there is a common topic
					//,the color is defined and if this color is not removed by the user it will determine the edge color
					for (Comment sourceComment : sourceSortedSet) {
						if (targetColorSet.contains(sourceComment.getTopic()
								.getTopicID())) {

							Color tempColor = ConfigurationManager.topicColorsHashtable
							.get(sourceComment.getTopic());

							if (topicHashTable != null) {

								if (topicHashTable.get(tempColor)) {
									edgeColor = tempColor;
									break;
								}

							} else {
								edgeColor = tempColor;
								break;
							}

						}

					}

				} else {
					continue;
				}

				GraphElements.MyEdge myWeightEdge = new MyEdge(String.valueOf(id),
						edgeColor);
				
				GraphElements.MyEdge myEdge = new MyEdge(authorSource+authorTarget,
						edgeColor);

				

				if (topicHashTable != null && edgeColor != null) {

					GraphElements.MyVertex mySourceVertex = new MyVertex(
							authorSource, Color.WHITE);

					GraphElements.MyVertex myTargetVertex = new MyVertex(
							authorTarget, Color.WHITE);

					weightGraph.addEdge(myWeightEdge, mySourceVertex, myTargetVertex,
							EdgeType.DIRECTED);
					
					graph.addEdge(myEdge, mySourceVertex, myTargetVertex,
							EdgeType.DIRECTED);
					

				} else {
					GraphElements.MyVertex mySourceVertex = new MyVertex(
							authorSource, Color.WHITE);
					if (firstLevelCommentIdsSet.contains(mesgTarget))
						if (vertexNoOfInitHashtable.containsKey(authorSource)) {
							GraphElements.MyVertex tempVertex = vertexNoOfInitHashtable
							.get(authorSource);
							tempVertex
							.setNoOfInits(tempVertex.getNoOfInits() + 1);
							vertexNoOfInitHashtable.put(authorSource,
									tempVertex);
						} else {
							mySourceVertex.setNoOfInits(1);
							vertexNoOfInitHashtable.put(authorSource,
									mySourceVertex);
						}

					// the following code is to store the no of posts of a user
					// in a hash table
					if (!vertexNoOfPostsHashtable.containsKey(authorSource)) {
						mySourceVertex.setNoOfPosts(numberOfPosts);
						vertexNoOfPostsHashtable.put(authorSource,
								mySourceVertex);
					}

					// commentTopicsHashMap which was populated during initialization now is used to
					// find all the topics the user has been involved by combining the set of all topics 
					// which was stored previously in vertexNoOfTopicsHashtable  with the set of topics of current comment id
					if (!vertexNoOfTopicsHashtable.containsKey(mySourceVertex
							.getName())) {

						vertexNoOfTopicsHashtable.put(mySourceVertex.getName(),
								commentTopicsHashMap.get(mesgSource));
					} else {
						Set<Topic> set1 = vertexNoOfTopicsHashtable
						.get(mySourceVertex.getName());
						Set<Topic> set2 = commentTopicsHashMap.get(mesgSource);
						set1.addAll(set2);
						vertexNoOfTopicsHashtable.put(mySourceVertex.getName(),
								set1);
					}

					GraphElements.MyVertex myTargetVertex = new MyVertex(
							authorTarget, Color.WHITE);
					if (!vertexNoOfTopicsHashtable.containsKey(myTargetVertex
							.getName())) {

						vertexNoOfTopicsHashtable.put(myTargetVertex.getName(),
								commentTopicsHashMap.get(mesgTarget));
					} else {
						Set<Topic> set1 = vertexNoOfTopicsHashtable
						.get(myTargetVertex.getName());
						Set<Topic> set2 = commentTopicsHashMap.get(mesgTarget);
						set1.addAll(set2);
						vertexNoOfTopicsHashtable.put(myTargetVertex.getName(),
								set1);
					}
					if (edgeColor != null) {
						
						weightGraph.addEdge(myWeightEdge, mySourceVertex, myTargetVertex,
								EdgeType.DIRECTED);
						
						graph.addEdge(myEdge, mySourceVertex, myTargetVertex,
								EdgeType.DIRECTED);
						
					}
				}
			}

			if (topicHashTable == null) {

				
				int size = topicHashSet.size();
				topicHashTable = new Hashtable<Color, Boolean>(size);
				topicCheckBoxes = new JCheckBox[size];
				ArrayList<Topic> topicList = topicsContainer.getTopicList();
				int i = 0;
				for (final Topic topic : topicList) {
					if (topicHashSet.contains(topic.getTopicID())) {
						Color color = ConfigurationManager.topicColorsHashtable
						.get(topic);
						topicHashTable.put(color, new Boolean(true));
						topicCheckBoxes[i] = new JCheckBox(topic.toString());
						topicCheckBoxes[i].setForeground(color);
						topicCheckBoxes[i].setSelected(true);
						topicCheckBoxes[i]
						                .addActionListener(new ActionListener() {

						                	@Override
						                	public void actionPerformed(ActionEvent arg0) {

						                		JCheckBox checkBox = (JCheckBox) arg0
						                		.getSource();

						                		topicHashTable
						                		.put(ConfigurationManager.topicColorsHashtable
						                				.get(topic),
						                				new Boolean(checkBox
						                						.isSelected()));

						                		Graph<MyVertex, MyEdge> graph = topicCalc();
						                		layout.setGraph(graph);
						                		vv.repaint();
						                	}

						                });
						i++;
					}
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				rs.close();
				stmt.close();
				conn.close();
				Runtime r = Runtime.getRuntime();
				r.gc();
			} catch (SQLException e) {

				e.printStackTrace();
			}

		}

		ClosenessCentrality<MyVertex, MyEdge> closeness = new ClosenessCentrality<GraphElements.MyVertex, GraphElements.MyEdge>(
				weightGraph);

		BetweennessCentrality<MyVertex, MyEdge> ranker = new BetweennessCentrality<MyVertex, MyEdge>(
				weightGraph);
		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();

		for (Iterator<MyVertex> iterator = weightGraph.getVertices().iterator(); iterator
		.hasNext();) {

			MyVertex myVertex = (MyVertex) iterator.next();

			// the following code is used to populated the vertexes' attributes
			// from the relevant hash tables which were built
			// previously including hash tables for no of topics, no of posts,
			// no of comments a user initiated and no of alone
			// posts for each user if any

			if (vertexNoOfTopicsHashtable.containsKey(myVertex.getName()))
				myVertex.setNoOftopics(vertexNoOfTopicsHashtable.get(
						myVertex.getName()).size());
			else
				myVertex.setNoOftopics(1);

			if (vertexNoOfPostsHashtable.containsKey(myVertex.getName()))
				myVertex.setNoOfPosts(vertexNoOfPostsHashtable.get(
						myVertex.getName()).getNoOfPosts());
			else
				myVertex.setNoOfPosts(0);

			if (vertexNoOfInitHashtable.containsKey(myVertex.getName()))
				myVertex.setNoOfInits(vertexNoOfInitHashtable.get(
						myVertex.getName()).getNoOfInits());
			else
				myVertex.setNoOfInits(0);

			if (vertexNoOfAlonePostsHashtable.containsKey(myVertex.getName()))
				myVertex.setNoOfAlonePosts(vertexNoOfAlonePostsHashtable.get(
						myVertex.getName()).getNoOfAlonePosts());
			else
				myVertex.setNoOfAlonePosts(0);

			myVertex.setInDegree(graph.inDegree(myVertex));
			myVertex.setOutDegree(graph.outDegree(myVertex));
			
			myVertex.setWeightInDegree(weightGraph.inDegree(myVertex));
			myVertex.setWeightOutDegree(weightGraph.outDegree(myVertex));
			
			myVertex.setBetweenness(ranker.getVertexRankScore(myVertex));
			myVertex.setCloseness(closeness.getVertexScore(myVertex));

		}

		return weightGraph;

	}

	Layout<GraphElements.MyVertex, GraphElements.MyEdge> layout;
	VisualizationViewer<GraphElements.MyVertex, GraphElements.MyEdge> vv;

	public void draw(Graph<MyVertex, MyEdge> g) {

		layout = new FRLayout<MyVertex, MyEdge>(g);
		layout.setSize(new Dimension(1000, 700));

		vv = new VisualizationViewer<GraphElements.MyVertex, GraphElements.MyEdge>(
				layout);

		vv.setPreferredSize(new Dimension(1050, 750));

		// Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer(
				new ToStringLabeller<MyVertex>());

		vv.getRenderContext().setEdgeLabelTransformer(
				new ToStringLabeller<MyEdge>() {
					@Override
					public String transform(MyEdge v) {
						// TODO Auto-generated method stub
						return ""; // super.transform(v);
					}
				});

		vv.getRenderContext().setVertexFillPaintTransformer(
				new Transformer<MyVertex, Paint>() {
					@Override
					public Paint transform(MyVertex arg0) {
						return arg0.getColor();
					}

				});

		vv.getRenderContext().setEdgeDrawPaintTransformer(
				new Transformer<MyEdge, Paint>() {
					@Override
					public Paint transform(MyEdge arg0) {
						return arg0.getColor();
					}

				});

		vv.getRenderContext().setEdgeStrokeTransformer(
				new Transformer<MyEdge, Stroke>() {
					// float dash[] = { 10.0f };
					public Stroke transform(MyEdge s) {
						return new BasicStroke(3.1f, BasicStroke.CAP_BUTT,
								BasicStroke.JOIN_MITER, 3.0f, null, 3.0f);
					}
				});

		// Create a graph mouse and add it to the visualization viewer
		EditingModalGraphMouse<MyVertex, MyEdge> gm = new EditingModalGraphMouse<MyVertex, MyEdge>(
				vv.getRenderContext(),
				GraphElements.MyVertexFactory.getInstance(),
				GraphElements.MyEdgeFactory.getInstance());

		// Set some defaults for the Edges...
		GraphElements.MyEdgeFactory.setDefaultCapacity(192.0);
		GraphElements.MyEdgeFactory.setDefaultWeight(5.0);
		// Trying out our new popup menu mouse plugin...
		PopupVertexEdgeMenuMousePlugin<Object, Object> myPlugin = new PopupVertexEdgeMenuMousePlugin<Object, Object>();
		// Add some popup menus for the edges and vertices to our mouse plugin.
		JPopupMenu edgeMenu = new MyMouseMenus.EdgeMenu(this);
		JPopupMenu vertexMenu = new MyMouseMenus.VertexMenu();
		myPlugin.setEdgePopup(edgeMenu);
		myPlugin.setVertexPopup(vertexMenu);

		gm.remove(gm.getPopupEditingPlugin()); // Removes the existing popup
		// editing plugin

		gm.add(myPlugin); // Add our new plugin to the mouse

		vv.setGraphMouse(gm);

		JPanel checkBoxPanel = new JPanel(new BorderLayout());

		topicCheckBoxPanel = new JPanel();

		commentCheckBoxPanel = new JPanel();

		for (JCheckBox checkBox : topicCheckBoxes) {
			topicCheckBoxPanel.add(checkBox);
		}

		JPanel mainPanel = new JPanel(new BorderLayout());

		vv.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		mainPanel.add(vv, BorderLayout.CENTER);

		JPanel userSearchPanel = new JPanel(new GridLayout(3, 1));

		JLabel userSearchLabel = new JLabel("Search Based On User Name:");
		final JTextField userSearchTextField = new JTextField();
		userSearchLabel.setLabelFor(userSearchTextField);

		userSearchPanel.add(userSearchLabel);
		userSearchPanel.add(userSearchTextField);

		JPanel commentFilterPanel = new JPanel(new GridLayout(3, 1));
		JLabel commmentCountLabel = new JLabel("Users With Min Comments");
		final JTextField commentCount = new JTextField();

		commmentCountLabel.setLabelFor(commentCount);
		final JButton filterButton = new JButton("filter");
		filterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//topic = true;
				///titleRadioButton.setSelected(true);
				
				//topicCheckBoxPanel.setVisible(true);
				//commentCheckBoxPanel.setVisible(false);

				userSearch = userSearchTextField.getText();
				
				if (commentCount.getText() == null
						|| commentCount.getText().equals(""))
					minNumberOfcomments = 0;
				else
					minNumberOfcomments = new Integer(commentCount.getText())
				.intValue();
				
				Graph<MyVertex, MyEdge> graph;
				if (topic) {
				graph = topicCalc();
				}else{
				graph = commentCalc();	
				}
				layout.setGraph(graph);
				vv.repaint();

			}
		});

		commentFilterPanel.add(commmentCountLabel);
		commentFilterPanel.add(commentCount);

		commentFilterPanel.add(filterButton);

		checkBoxPanel.add(topicCheckBoxPanel, BorderLayout.SOUTH);
		topicCheckBoxPanel.setLayout(new GridLayout(2, 10 / 2));
		topicCheckBoxPanel.setBorder(BorderFactory
				.createLineBorder(Color.LIGHT_GRAY));

		checkBoxPanel.add(commentCheckBoxPanel, BorderLayout.NORTH);

		mainPanel.add(checkBoxPanel, BorderLayout.SOUTH);

		commentCheckBoxPanel.setLayout(new GridLayout(2, 10));
		commentCheckBoxPanel.setBorder(BorderFactory
				.createLineBorder(Color.LIGHT_GRAY));

		commentCheckBoxPanel.setVisible(false);

		JPanel togglePanel = new JPanel(new GridLayout(3, 1));

		JRadioButton titleRadioButton = new JRadioButton("Topic");
		titleRadioButton.setSelected(true);
		JRadioButton commentRadioButton = new JRadioButton("Number of Comments");
		ButtonGroup bg = new ButtonGroup();
		bg.add(titleRadioButton);
		bg.add(commentRadioButton);

		togglePanel.add(titleRadioButton);
		togglePanel.add(commentRadioButton);

		final JTextField XTextField = new JTextField(3);
		final JTextField Y1TextField = new JTextField(3);
		final JTextField Y2TextField = new JTextField(3);
		final JTextField ZTextField = new JTextField(3);

		XTextField.setSize(3, 3);

		XTextField.setText(X + "");
		Y1TextField.setText(Y1 + "");
		Y2TextField.setText(Y2 + "");
		ZTextField.setText(Z + "");
		final JButton filterCommentButton = new JButton("redraw");

		commentRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				topic = false;
				userSearch ="";
				minNumberOfcomments=0;
				userSearchTextField.setText("");
				commentCount.setText("");
				
				Graph<MyVertex, MyEdge> graph = commentCalc();
				layout.setGraph(graph);
				vv.repaint();

				int i = 0;

				// this block of code adds the no of posts text fields to a panel
				if (commentCheckBoxes != null)
					for (JCheckBox checkBox : commentCheckBoxes) {
						commentCheckBoxPanel.add(checkBox);
						if (i == 0)
							commentCheckBoxPanel.add(XTextField);
						if (i == 1) {
							commentCheckBoxPanel.add(Y1TextField);
							commentCheckBoxPanel.add(Y2TextField);
						}
						if (i == 2)
							commentCheckBoxPanel.add(ZTextField);

						i++;
					}

				commentCheckBoxPanel.add(filterCommentButton);
				filterCommentButton.addActionListener(new ActionListener() {

					@Override
					/*
					 * here we get all of the boundaries values from text fields and populate the corresponding variables
					 * then we recalculate the graph based on the new values using commentCalc method
					 */
					public void actionPerformed(ActionEvent e) {

						X = Integer.parseInt(XTextField.getText());
						Y1 = Integer.parseInt(Y1TextField.getText());
						Y2 = Integer.parseInt(Y2TextField.getText());
						Z = Integer.parseInt(ZTextField.getText());

						if (Y1 <= X) {
							// JDialog dialog = new JDialog();
							JOptionPane
							.showMessageDialog(vv,
							"the number for 'posts equal to' text box must be less than other numbers");
						} else if (Y1 >= Y2) {
							JOptionPane
							.showMessageDialog(
									vv,
									"the number for first text box of 'between' text boxes must be less than the other");

						} else if (Y2 > Z)
							JOptionPane
							.showMessageDialog(
									vv,
									"the number for 'greater than' text box should be equal or greater than of the value of the upper range of 'between' text boxes");

						else {

							commentColorHashTable = null;

							Graph<MyVertex, MyEdge> graph = commentCalc();
							layout.setGraph(graph);
							vv.repaint();
						}

					}
				});

				topicCheckBoxPanel.setVisible(false);
				commentCheckBoxPanel.setVisible(true);

			}
		});

		titleRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				topic = true;
				userSearch ="";
				minNumberOfcomments=0;
				userSearchTextField.setText("");
				commentCount.setText("");
				
				Graph<MyVertex, MyEdge> graph = topicCalc();
				layout.setGraph(graph);
				vv.repaint();
				topicCheckBoxPanel.setVisible(true);
				commentCheckBoxPanel.setVisible(false);

			}
		});

		JPanel headerPanel = new JPanel(new GridLayout(1, 3));

		headerPanel.add(togglePanel);
		headerPanel.add(commentFilterPanel);
		headerPanel.add(userSearchPanel);

		mainPanel.add(headerPanel, BorderLayout.NORTH);

		getContentPane().add(mainPanel);

		// Let's add a menu for changing mouse modes
		JMenuBar menuBar = new JMenuBar();
		JMenu modeMenu = gm.getModeMenu();
		modeMenu.setText("Mouse Mode");
		modeMenu.setIcon(null); // I'm using this in a main menu
		modeMenu.setPreferredSize(new Dimension(130, 20)); // Change the size so
		
		menuBar.add(modeMenu);

		setJMenuBar(menuBar);
		gm.setMode(ModalGraphMouse.Mode.EDITING); // Start off in editing mode
		setVisible(true);
	}

	@Override
	public void destroy() {

		super.destroy();
		vertexCommentCountHashtable = null;
		vertexNoOfTopicsHashtable = null;
		vertexNoOfPostsHashtable = null;
		vertexNoOfInitHashtable = null;
		vertexNoOfAlonePostsHashtable = null;
		Runtime r = Runtime.getRuntime();
		r.gc();

	}

}
