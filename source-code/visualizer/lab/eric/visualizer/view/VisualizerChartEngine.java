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
package lab.eric.visualizer.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import lab.eric.visualizer.model.Comment;
import lab.eric.visualizer.model.ConfigurationManager;
import lab.eric.visualizer.model.Topic;


/**
 * @author Samadjon Uroqov
 * 
 *         Cest un petit moteur de construction des graphiques. Elle prend
 *         comme input un vecteur de points à deux dimensions et laffiche
 *         graphiquement. Un vecteur basique de données (int[][] dataPoints) a
 *         été choisi pour la performance au lieu dun objet des classes
 *         Collections. Car la classe est appelée fréquemment et doit être
 *         executé rapidement. En effet ce choix a fait assez de différence dans
 *         la rapidité daffichage (pour gros volume de données). Lobjet gère
 *         en même temps linterface et les événements. Pour laffichage on a
 *         remplacé la méthode paintComponent() du JPanel. Cette méthode
 *         commence par créer un nouveau Rectangle de graphique selon les
 *         paramètres de nouvel affichage (dimension). Elle crée ensuite une
 *         image pour y placer le graphique.
 */
public class VisualizerChartEngine extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Stores topics to actually filter, captured from popup menu
	 */
	private ArrayList<Integer> filterTopics;
	/**
	 * Stores topics to consider to be filtered, captured from popup menu
	 */
	private ArrayList<Integer> topicPointsToFilter;

	private String chartTitle = "Un article";
	private Color backgroundColor = Color.white;

	private Color borderColor = Color.gray;
	private double chart_h = -1;
	private double chart_w = -1;

	private double chart_x = -1;
	private double chart_x_init = 30;
	private double chart_y = -1;
	/**
	 * Defines the edges of the topic lines chart.
	 */
	private Rectangle chartRectangle;

	/**
	 * Main graphics data points. First dimension=topics, second
	 * dimension=periods. Ex: dataPoints[1][2] = n, where n represents the
	 * number of comments posted under the topic (topic index=1, NOT topic ID!)
	 * and in the time interval (interval index=2)
	 */

	private int dataPoints[][];
	private int dataPointsCount;

	/**
	 * Defines how frequent a date descriptor should appear in the x axis
	 * description. Ex.: dateDivisor = 1 --> all dates should appear<br/>
	 * dateDivisor = 3 --> one in 3 dates should appear
	 */
	private int dateDivisor = 2;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			ConfigurationManager.tableDateFormat);
	/**
	 * Defines the number of horizontal divisions (lines) to be created
	 */
	private double divisionCount = ConfigurationManager.divisionCount;

	private Color gridBackgroundColor = Color.white;
	private Color gridColor = Color.gray;

	private double horizontalStep = 0;

	/**
	 * Stores the main chart image. The final chart is rendered as an Image
	 * object and is drawn by the graphics context of the parent: JPanel
	 */

	private Image mainChartImage;

	/**
	 * Represents the graphics context of the main chart image. This is where
	 * the actual drawing occurs.
	 */
	private Graphics2D chartGraphics;

	/**
	 * Main graphics context to which we draw the chart image (mainChartImage).
	 * The graphics context is passed as an argument to the method
	 * <code>void paintComponent(Graphics g)</code> by the JVM
	 */

	private Graphics2D mainGraphics;
	private double maxValue = 0;

	private double screenHeight;
	private double screenWidth;

	private boolean showBorder = true;
	/**
	 * Squares that represent period points, set always to true to enable chart
	 * interaction: it's at this point that the mouse motion is captured easily
	 */
	private boolean showSmallSquares = true;
	private int smallSquaresSize = ConfigurationManager.smallSquaresSize;
	private boolean showTitle = true;

	/**
	 * X abcisse descriptors: dates in String format
	 */
	private String stringDates[];
	private Font stringDatesFont = new Font("SansSerif", Font.PLAIN, 12);
	/**
	 * thickness of lines
	 */
	private final int thickness = ConfigurationManager.thickness;
	private Color titleColor = Color.black;
	private Font titleFont = new Font("SansSerif", Font.BOLD, 18);
	private int titleXpos = -1;
	private int titleYpos = 30;

	private Color topicColors[];
	private int topicCount = 0;
	/**
	 * Transformation angle to apply for x abcisse descriptors (to save space)
	 */
	private double transformAngle = 45;
	private boolean transformXdescription = true;

	private boolean verticalGridHide = ConfigurationManager.verticalGridHide;
	private boolean horizontalGridHide = ConfigurationManager.horizontalGridHide;
	private double verticalStep = 0;

	private double verticalStringLength = 0;
	private String xAxisDescription = null;

	private String yAxisDescription = null;
	private TopicChartPanel topicChartPanel;

	/**
	 * Defines max number of x axis descriptors, eg date strings Ex.:
	 * maxXDescriptorCount = 3 --> x descriptors appears as [dateString1]
	 * [dateString2] [dateString3]<br/>
	 * Used to prevent overpopulating x axis descriptions (space problem)
	 */
	private final int maxXDescriptorCount = 7;

	private Topic[] topics;
	private Date startDate;
	/**
	 * Number of milliseconds that exists in a time interval. Used as a unite of
	 * time interval here
	 */
	private long period;
	private VisualizerCustomToolTip customToolTip;

	// copy constructor
	public VisualizerChartEngine(VisualizerChartEngine visualizerChartEngine) {
		this.dataPoints = visualizerChartEngine.dataPoints;
		this.startDate = visualizerChartEngine.startDate;

		this.topics = visualizerChartEngine.topics;
		this.chartTitle = visualizerChartEngine.chartTitle;
		this.topicChartPanel = visualizerChartEngine.topicChartPanel;
	}

	/**
	 * @return chartTitle
	 */
	public String getArticleTitle() {
		return chartTitle;
	}

	private Date endDate;

	/**
	 * Creates a new chart engine. Overrides the method paintComponent, which is
	 * called automatically (and very frequently) by the JVM
	 * 
	 * @param dataPoints1
	 * @param startDate1
	 * @param endDate1
	 * @param topics1
	 * @param title1
	 * @param topicChartPanel1
	 *            *
	 * 
	 */
	public VisualizerChartEngine(int[][] dataPoints1, Date startDate1,
			Date endDate1, final Topic[] topics1, String title1,
			TopicChartPanel topicChartPanel1) {

		// System.out.println("Start date=" + startDate1 + "\nEnd date=" +
		// endDate1);

		dataPoints = dataPoints1;
		topicChartPanel = topicChartPanel1;
		topics = topics1;
		chartTitle = title1;
		startDate = startDate1;
		endDate = endDate1;
		topicPointsToFilter = new ArrayList<Integer>();

		filterTopics = new ArrayList<Integer>();
		topicColors = new Color[topics.length];
		period = (endDate.getTime() - startDate.getTime())
				/ ConfigurationManager.periodCount;

		// Un peu bizarre, cette instruction lance parfois des exceptions
		// NullPointer, apparemment tout est ok
		for (int i = 0; i < topics.length; i++)
			topicColors[i] = ConfigurationManager.topicColorsHashtable
					.get(topics[i]);

		/**
		 * Popup menu to filter a topic line in an individual chart only
		 */

		final JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem menuItem = new JMenuItem("supprimer ce topic");
		popupMenu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				if (topicPointsToFilter.size() != 0)
					filterTopics.add(topicPointsToFilter.get(0));
				topicPointsToFilter = new ArrayList<Integer>();
				repaint();
			}
		});

		topicCount = dataPoints.length;
		if (topicCount == 0)
			dataPointsCount = 0;
		else
			dataPointsCount = dataPoints[0].length;

		stringDates = new String[dataPointsCount];

		/**
		 * Populating x axis description = dates in specified format
		 */

		for (int j = 0; j < dataPointsCount; j++) {

			stringDates[j] = dateFormat.format(new Date(startDate.getTime() + j
					* period));
		}

		/**
		 * 
		 * If there are too much periods in the data set, increase the
		 * dateDivisor so that descriptor count in the chart doesn't exceed
		 * specified maxXDescriptorCount. Used to prevent overpopulating x axis
		 * descriptions (space problem).
		 * 
		 */

		if (dataPointsCount / dateDivisor > maxXDescriptorCount)
			dateDivisor = dataPointsCount / maxXDescriptorCount;

		/**
		 * This MouseMotionListener allows to capture the mouse when it's on the
		 * period point areas(big squares). When it is captured, there are 4
		 * actions defined: 1) the cursor is set to HAND_CIRSOR <br/>
		 * 2) a new ArrayList<Point> selectedDataPoints is created. It contains
		 * a set of Point objects. The x field of each Point represents a topic
		 * index and y field - a period index. The geometric point to which the
		 * cursor is directed may contain many topics, thus the ArrayList is
		 * then filled with all of these topics.<br/>
		 * 3) A custom tooltip is created at the location point after 500
		 * milliseconds. <br/>
		 * 4) A KeyListener is registered to this tooltip allowing to show a
		 * particular comment (or comments) that belongs to the selected topic
		 * and selected time interval. <br/>
		 * Immediately after the cursor movs away from the captured point, the
		 * MouseMotionListener sets the cursor to DEFAULT_CURSOR
		 */
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {

			}

			boolean editMode = false;

			ArrayList<Point> selectedDataPoints;

			@Override
			public void mouseMoved(MouseEvent arg0) {
				selectedDataPoints = new ArrayList<Point>();

				// System.out.println("mouseMoved called");
				Point point = arg0.getPoint();
				JPanel panel = (JPanel) arg0.getSource();
				boolean found = false;
				String tooltip = "";

				if (customToolTip != null && !customToolTip.isActive()) {
					editMode = false;
					customToolTip.destroy();
				}

				if (!editMode && customToolTip != null)
					customToolTip.destroy();

				for (int i = 0; i < topicCount; i++) {
					for (int j = 0; j < dataPointsCount; j++) {
						if (dataRectangles[i][j] != null
								&& dataRectangles[i][j].contains(point)) {

							Point newDataPoint = new Point(i, j);
							if (!selectedDataPoints.contains(newDataPoint))
								selectedDataPoints.add(newDataPoint);

							panel.setCursor(Cursor
									.getPredefinedCursor(Cursor.HAND_CURSOR));

							Date end = new Date(startDate.getTime() + (j + 1)
									* period);
							Date start = new Date(end.getTime() - period);

							tooltip += topics[i] + " Interval={"
									+ dateFormat.format(start) + "-->"
									+ dateFormat.format(end)
									+ "} commentCount=" + dataPoints[i][j]
									+ "\r\n";

							found = true;
						} else if (!found) {
							panel.setCursor(Cursor
									.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					}
				}
				makeToolTip(panel, tooltip, arg0.getLocationOnScreen());

			}

			/**
			 * Creates a custom tooltip (VisualizerCustomToolTip) at the
			 * specified location
			 * 
			 * @param panel
			 * @param tooltip
			 * @param location
			 */
			private void makeToolTip(JPanel panel, String tooltip,
					Point location) {
				panel.repaint();
				if (tooltip != "")
					makeToolTip(tooltip, location);

			}

			private void makeToolTip(String toolTipText, final Point location) {

				if (customToolTip != null && !customToolTip.isActive()) {
					editMode = false;
					customToolTip.destroy();
				}

				if (editMode && customToolTip != null)
					return;
				if (customToolTip != null)
					customToolTip.destroy();
				customToolTip = new VisualizerCustomToolTip(toolTipText,
						location);
				customToolTip.addKeyListener(new KeyAdapter() {

					@Override
					public void keyTyped(KeyEvent e) {

						VisualizerCustomToolTip myToolTip = (VisualizerCustomToolTip) e
								.getSource();
						if ((e.getKeyChar() == 'v' || e.getKeyChar() == 'V')
								&& !editMode) {
							setCursor(Cursor
									.getPredefinedCursor(Cursor.WAIT_CURSOR));
							editMode = true;
							if (ConfigurationManager.showRandomComment)
								myToolTip
										.showEditor(retreiveRandomComment(selectedDataPoints));
							else
								myToolTip
										.showEditor(retreiveAllComments(selectedDataPoints));
							setCursor(Cursor
									.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						} else
							myToolTip.destroy();
					}

				});

			}

		});

		/**
		 * 
		 * This MouseListener has 2 operations: 1) when the mouse is clicked
		 * twice a new independent frame is created containing the same chart as
		 * the one created here. It allows to view the chart more freely. <br/>
		 * 2) when the mouse is clicked once, if it's a context menu request, a
		 * popup menu appears allowing to delete a line(=topic) directly from
		 * THIS chart only
		 */

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					if (topicChartPanel.getParent().getComponentCount() != 1) {
						JFrame frame = new JFrame(
								VisualizerChartEngine.this.chartTitle);

						ImageIcon icon = ConfigurationManager.imageHashtable
								.get(ConfigurationManager.applicationIcon);
						if (icon != null)
							frame.setIconImage(icon.getImage());

						frame.setSize(700, 500);
						frame.getContentPane().add(
								topicChartPanel.makeNewChart());
						frame.addWindowListener(new WindowAdapter() {

							@Override
							public void windowClosing(WindowEvent arg0) {
								((JFrame) arg0.getSource()).dispose();
							}

						});
						frame.setVisible(true);
					}
				}

			}

			public void mouseReleased(MouseEvent event) {
				Point point = event.getPoint();
				boolean found = false;
				if (event.isPopupTrigger()) {

					for (int i = 0; i < topicCount; i++) {
						for (int j = 0; j < dataPointsCount; j++) {
							if (dataRectangles[i][j] != null
									&& dataRectangles[i][j].contains(point)) {
								VisualizerChartEngine.this.topicPointsToFilter
										.add(i);
								found = true;

								// filterTopic(new Point(i, j));
								// JOptionPane.showMessageDialog(null, "point="+
								// i + ":" + j);
							}
						}
					}
					if (found) {
						if (customToolTip != null && !customToolTip.isActive())
							customToolTip.destroy();
						popupMenu.show(VisualizerChartEngine.this, point.x,
								point.y);
					}
				}
			}
		});

	}

	/**
	 * Calculates the chartRectangle dimensions according to the number of data
	 * points, dimension of this panel, the maximum value of data points, number
	 * of time intervals, number of topics, geomethric length of x axis
	 * descriptors. <br/>
	 * Also calculates the step to take to draw the chart.
	 */
	private void calculateDimensions() {

		chartGraphics.setFont(stringDatesFont);

		maxValue = 0;

		for (int i = 0; i < topicCount; i++) {
			if (filterTopics.contains(i)
					|| ConfigurationManager.topicsToFilter.contains(topics[i])) {
				// topicCount--;
				continue;
			}
			for (int j = 0; j < dataPointsCount; j++) {

				if ((int) (dataPoints[i][j]) > maxValue)
					maxValue = (int) (dataPoints[i][j]);
			}
		}

		int nr_cifre = 1;
		double max1 = maxValue;
		while (max1 / 10 >= 1) {
			max1 = max1 / 10;
			nr_cifre++;
		}
		int font_metrics = chartGraphics.getFontMetrics().stringWidth("a");
		verticalStringLength = font_metrics * nr_cifre + 5;
		if (chart_y == -1)
			chart_y = 50;
		if (chart_x == -1) {
			chart_x = chart_x_init;

			if (chart_x < verticalStringLength + 10)
				chart_x = verticalStringLength + 10;
			if (yAxisDescription != null)
				chart_x += 20;
		}
		double o;

		if (topicCount > 0) {

			chart_h = screenHeight - chart_y - 65 - 20
					* (transformXdescription ? 1 : 0);
			if (xAxisDescription != null)
				chart_h -= 20;

			chart_w = screenWidth - chart_x - chart_x_init - 20;

			chartRectangle.height = (int) chart_h;
			chartRectangle.width = (int) chart_w;
			chartRectangle.x = (int) chart_x;
			chartRectangle.y = (int) chart_y;
			if (chartRectangle.height % divisionCount != 0)
				chartRectangle.height -= chartRectangle.height % divisionCount;

			if (chartRectangle.width % (dataPointsCount - 1) != 0)
				chartRectangle.width -= chartRectangle.width
						% (dataPointsCount - 1);
			horizontalStep = (double) chartRectangle.width
					/ (double) (dataPointsCount - 1);
			verticalStep = (double) chartRectangle.height
					/ (double) divisionCount;

		} else {
			maxValue = 0;
			o = chartRectangle.height / 5;
			chartRectangle.width = (int) (screenWidth - screenWidth / 2 - o);
			chartRectangle.x = (int) (screenWidth / 12);
			chartRectangle.y = (int) ((screenHeight - chartRectangle.height) / 2);
			verticalStep = chartRectangle.height;
		}
	}

	/**
	 * This method consists of three methods: drawTitle() to draw the chart
	 * title, drawGrids(graphics) to draw the grids, drawLines() to draw lines
	 * (topics evolution)
	 * 
	 * @param graphics
	 *            Where to put the chart
	 */
	private void drawChart(Graphics graphics) {
		chartGraphics.setColor(borderColor);

		if (showBorder)
			chartGraphics.drawRect(0, 0, (int) screenWidth - 1,
					(int) screenHeight - 1);

		if (showTitle)
			drawTitle();
		drawGrids(graphics);
		drawLines();

	}

	/**
	 * Draws the edges of the main chart rectangle.<br/>
	 * Calculates the maximum geometric length of x axis descriptors, if it is
	 * more than <code>horizontalStep - 4</code> then 45degrees transform is
	 * applied to all x axis descriptors to save space.<br/>
	 * If required, draws as many vertical lines as the number of time
	 * intervals.<br/>
	 * If required, draws as many horizontal lines as specified by the
	 * divisionCount.<br/>
	 * If required, draws X axis descriptors.<br/>
	 * If required, draws Y axis descriptors.<br/>
	 * 
	 * @param graphics
	 */
	private void drawGrids(Graphics graphics) {
		chartGraphics.setColor(gridBackgroundColor);
		chartGraphics
				.fillRect((int) (chartRectangle.x), chartRectangle.y,
						(int) (chartRectangle.width + 1),
						(int) (chartRectangle.height));

		int i = (int) divisionCount;
		chartGraphics.setFont(stringDatesFont);
		chartGraphics.setColor(gridColor);

		AffineTransform oldAffineTransform;
		AffineTransform newTransformer;
		chartGraphics.drawRect(chartRectangle.x, chartRectangle.y,
				chartRectangle.width, chartRectangle.height);

		int m = 0;
		String max_str = "";
		for (int c = 0; i < dataPointsCount; i++) {
			if (stringDates[c].length() > m) {
				m = stringDates[c].length();
				max_str = stringDates[c];
			}
			;
		}
		chartGraphics.setFont(stringDatesFont);
		int max_metrics = chartGraphics.getFontMetrics().stringWidth(max_str);
		if (max_metrics > horizontalStep - 4)
			transformXdescription = true;

		for (int k = 0; k < dataPointsCount; k++) {
			if (!verticalGridHide)
				chartGraphics.drawLine(
						(int) (k * horizontalStep + chartRectangle.x),
						(int) (chartRectangle.y), (int) (chartRectangle.x + k
								* horizontalStep),
						(int) (chartRectangle.height + chartRectangle.y));

			if (k % dateDivisor == 0)
				if (transformXdescription) {
					oldAffineTransform = chartGraphics.getTransform();
					newTransformer = AffineTransform.getTranslateInstance(
							(chartRectangle.x + k * horizontalStep),
							(chartRectangle.height + chartRectangle.y + 20));
					newTransformer.rotate(Math.toRadians(transformAngle));
					chartGraphics.setTransform(newTransformer);
					chartGraphics.drawString(stringDates[k], 0, 0);
					chartGraphics.setTransform(oldAffineTransform);
				} else {
					double d_width = chartGraphics.getFontMetrics()
							.stringWidth(stringDates[k]);
					double start_desc = (horizontalStep - d_width) / 2;
					chartGraphics
							.drawString(stringDates[k], (int) (chartRectangle.x
									+ k * horizontalStep + start_desc),
									(int) (chartRectangle.height
											+ chartRectangle.y + 20));
				}
			i++;
		}

		for (int k = 0; k <= divisionCount; k++) {
			if (!horizontalGridHide)
				chartGraphics.drawLine((int) (chartRectangle.x), (int) (k
						* verticalStep + chartRectangle.y),
						(int) (chartRectangle.x + chartRectangle.width),
						(int) (k * verticalStep + chartRectangle.y));
			int gr = (int) (maxValue - k * (maxValue / divisionCount));
			chartGraphics.drawString("" + gr,
					(int) (chartRectangle.x - verticalStringLength), (int) (k
							* verticalStep + chartRectangle.y));
		}

		if (xAxisDescription != null) {
			double l = chartGraphics.getFontMetrics().stringWidth(
					xAxisDescription);
			double startPos = chartRectangle.x + chartRectangle.width / 2 - l
					/ 2;
			double yStartPos;
			yStartPos = screenHeight - 15;
			chartGraphics.drawString(xAxisDescription, (int) startPos,
					(int) yStartPos);
		}
		if (yAxisDescription != null) {
			double l = chartGraphics.getFontMetrics().stringWidth(
					yAxisDescription);
			double startPos = chartRectangle.y + chartRectangle.height / 2 + l
					/ 2;

			oldAffineTransform = chartGraphics.getTransform();
			newTransformer = AffineTransform.getTranslateInstance(20, startPos);
			newTransformer.rotate(Math.toRadians(-90));
			chartGraphics.setTransform(newTransformer);
			chartGraphics.drawString(yAxisDescription, 0, 0);
			chartGraphics.setTransform(oldAffineTransform);
		}
	}

	/**
	 * Stores the rectangles built at the small squares. Used to capture mouse
	 * motions above them.
	 */
	private Rectangle[][] dataRectangles;

	/**
	 * Draws the actual lines that represent individual topics.<br/>
	 * The method setStroke() is used to define the thickness of lines.<br/>
	 * On each iteration, graphics context is set to one color (topic unique
	 * color) once and one line with all points is completely drawn.<br/>
	 * Also fills Rectangles, each representing mouse-interactive time interval
	 * points.
	 */
	private void drawLines() {

		dataRectangles = new Rectangle[topicCount][dataPointsCount];

		double stepx = (double) (chartRectangle.width)
				/ (double) (dataPointsCount - 1);
		double stepy = (double) (chartRectangle.height) / (double) maxValue;

		chartGraphics.setStroke(new BasicStroke(thickness));
		for (int c = 0; c < topicCount; c++) {
			if (filterTopics.contains(c)
					|| ConfigurationManager.topicsToFilter.contains(topics[c]))
				continue;
			for (int k = 0; k < dataPointsCount; k++) {

				chartGraphics.setColor(topicColors[c]);
				int x1, y1, x2, y2;
				x1 = chartRectangle.x + (int) (stepx * (double) k);
				y1 = chartRectangle.y + chartRectangle.height
						- (int) (stepy * (double) dataPoints[c][k]);
				if (showSmallSquares && dataPoints[c][k] != 0) {
					dataRectangles[c][k] = new Rectangle(x1
							- (int) (smallSquaresSize / 2), y1
							- (int) (smallSquaresSize / 2), smallSquaresSize,
							smallSquaresSize);
					chartGraphics.fillRect(dataRectangles[c][k].x,
							dataRectangles[c][k].y, dataRectangles[c][k].width,
							dataRectangles[c][k].height);

				}

				// if(c == topicCount - 1 && k == dataPointsCount -1 &&
				// dataPoints[c][k] != 0)
				// {
				// System.out.println(topicCount + "Data problem point? " +
				// dataPointsCount);
				// }
				//
				if (k < dataPointsCount - 1) {
					int k2 = k + 1;
					x2 = chartRectangle.x + (int) (stepx * (double) (k + 1));
					y2 = chartRectangle.y + chartRectangle.height
							- (int) (stepy * (double) dataPoints[c][k2]);

					chartGraphics.drawLine(x1, y1, x2, y2);
				}
			}
		}
		chartGraphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1.f));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {

		screenHeight = getHeight();
		screenWidth = getWidth();

		chartRectangle = new Rectangle((int) (screenWidth / 12),
				(int) (screenHeight / 12),
				(int) (screenWidth - screenWidth / 6),
				(int) (screenHeight - screenHeight / 2));

		mainGraphics = (Graphics2D) g;

		mainChartImage = createImage((int) screenWidth, (int) screenHeight);

		chartGraphics = (Graphics2D) mainChartImage.getGraphics();

		chartGraphics.setPaint(backgroundColor);
		chartGraphics.fillRect(0, 0, (int) screenWidth, (int) screenHeight);
		calculateDimensions();
		drawChart(chartGraphics);

		mainGraphics.drawImage(mainChartImage, 0, 0, (int) screenWidth,
				(int) screenHeight, null);
	}

	/**
	 * Draws the chart title at the top
	 */
	private void drawTitle() {
		double startPos;
		chartGraphics.setFont(titleFont);
		chartGraphics.setColor(titleColor);
		if (titleXpos == -1) {
			double l = chartGraphics.getFontMetrics().stringWidth(chartTitle);
			startPos = chartRectangle.getCenterX() - l / 2;
		} else
			startPos = titleXpos;
		chartGraphics.drawString(chartTitle, (int) startPos, titleYpos);
	}

	/**
	 * Retreives a random comment. <br/>
	 * If there are many topics, one topic is randomly choosen.<br/>
	 * The list of comments belonging to this topic is taken and a random
	 * comment is selected from them.
	 * 
	 * @param selectedDataPoints
	 * @return randomComment
	 */
	private Comment retreiveRandomComment(ArrayList<Point> selectedDataPoints) {
		Random random = new Random();

		int whichPoint = Math.abs(random.nextInt(selectedDataPoints.size()));
		int whichTopic = selectedDataPoints.get(whichPoint).x;
		int whichPeriod = selectedDataPoints.get(whichPoint).y;

		ArrayList<Comment> comments = this.topicChartPanel
				.getCommentsPerTopicAndPeriod(whichTopic, whichPeriod);

		random = new Random();
		int whichComment = Math.abs(random.nextInt(comments.size()));

		return (Comment) comments.toArray()[whichComment];
	}

	/**
	 * Retrieves ALL comments that belong to this point and to this source
	 * 
	 * @param selectedDataPoints
	 * @return allCommentList
	 */
	private ArrayList<Comment> retreiveAllComments(
			ArrayList<Point> selectedDataPoints) {

		int topicCount = selectedDataPoints.size();
		ArrayList<Comment> commentList = new ArrayList<Comment>();
		for (int i = 0; i < topicCount; i++) {
			Point point = selectedDataPoints.get(i);
			commentList.addAll(topicChartPanel.getCommentsPerTopicAndPeriod(
					point.x, point.y));
		}

		return commentList;
	}
}
