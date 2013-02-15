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
/**
 * 
 */
package lab.eric.visualizer.view;

import static java.awt.geom.AffineTransform.getRotateInstance;
import static java.awt.geom.AffineTransform.getTranslateInstance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lab.eric.visualizer.model.Topic;


/**
 * Une autre alternative au moteur graphique. N'est pas encore utilisé dans
 * cette version de l'application.
 * 
 * @author Samadjon Uroqov
 * 
 */
public class TopicPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Topic topic;
	private Point[] topicPoints;
	private int commentCount;

	/**
	 * @param topic_
	 * @param topicPoints_
	 * @param commentCount1
	 *            Une autre alternative au moteur graphique. N'est pas encore
	 *            utilisé dans cette version de l'application.
	 */
	public TopicPanel(Topic topic_, Point[] topicPoints_, int commentCount1) {
		topic = topic_;
		topicPoints = topicPoints_;
		commentCount = commentCount1;
		// setBorder(BorderFactory.createLineBorder(Color.red));
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				JPanel panel = (JPanel) arg0.getSource();

				if (panel.getParent() instanceof TopicChartPanel) {
					JFrame frame = new JFrame();
					frame.getContentPane().add(
							new TopicPanel(topic, topicPoints, commentCount));
					frame.setSize(getWidth(), 200);
					frame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent arg0) {
							((JFrame) arg0.getSource()).dispose();
						}
					});
					frame.setVisible(true);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		final int thickness = 3;
		int maxWidth = getWidth() * 4 / 5; //
		int maxHeight = getHeight() * 4 / 5;
		g.setFont(new Font("Serif", Font.PLAIN, Math.min(maxHeight / 2, 20)));
		((Graphics2D) g).setStroke(new BasicStroke(thickness));

		g.setColor(makeRandomColor(Color.gray)); // Topic Color
		int pointCount = topicPoints.length;

		final int maxCount = getMaxPoint();
		ArrayList<Integer> pts = new ArrayList<Integer>();

		int maxIndex = 0;
		for (int i = 0; i < pointCount; i++) {
			if (!(topicPoints[i].y < 1)) {
				if (topicPoints[i].y == maxCount)
					maxIndex = pts.size() + 1;

				pts.add((int) (topicPoints[i].y * maxHeight
						/ Math.max(maxCount, 2) / 2 * .95 + maxHeight
						/ Math.max(maxCount, 2) / 2));
			}
		}

		int x2 = 0;
		int y2 = 0;
		int x1 = 0;
		int y1 = 0;

		pointCount = pts.size();
		double hstep = (double) maxWidth / (double) pointCount;

		Point maxPoint = new Point();
		for (int i = 1; i < pointCount; i++) {

			x1 = (int) ((i - 1) * hstep + maxWidth * 0.02); // padding
			x2 = (int) (i * hstep + maxWidth * 0.02); // padding
			y1 = (int) (maxHeight - (pts.get(i - 1)));
			y2 = (int) (maxHeight - (pts.get(i)));
			g.drawLine(x1, y1, x2, y2);
			g.drawRect(x1, y1, 2, 2);

			if (i == maxIndex) {
				maxPoint.x = x1;
				maxPoint.y = y1;
			}
		}

		if (maxPoint.x == 0)
			g.drawString("" + maxCount, x2, y2);
		else
			g.drawString("" + maxCount, maxPoint.x, maxPoint.y);
		// if(maxPoint != null)
		// àg.drawString("" + maxCount, maxPoint.x, 0);
		g.drawString(topic.toString() + " (" + commentCount + ")",
				x2 * 15 / 14, y2); // Topic name

		// int y = (int) (maxHeight * 0.75);
		// g.setColor(Color.black);
		// g.drawLine(0, y, x2, y);
		// makeArrow(g,0, y, (int) (x2 * 1.01), y);
	}

	/**
	 * @return maxPoint in the data, to determine the height of the chart
	 */
	private int getMaxPoint() {
		int maxPoint = 0;
		for (int i = 0; i < topicPoints.length; i++) {
			maxPoint = Math.max(maxPoint, topicPoints[i].y);
		}
		return maxPoint;
	}

	/**
	 * @param g1
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * 
	 *            Makes an arrow line at the specified location. Used to
	 *            represent directed vector-lines (time, abcisses, etc.)
	 */
	@SuppressWarnings("unused")
	private void makeArrowLine(Graphics g1, int x1, int y1, int x2, int y2) {
		Graphics2D g = (Graphics2D) g1.create();
		final int arrowSize = 10;

		double dx = x2 - x1, dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		int len = (int) Math.sqrt(dx * dx + dy * dy);
		AffineTransform at = getTranslateInstance(x1, y1);
		at.concatenate(getRotateInstance(angle));
		g.setTransform(at);
		g.fillPolygon(new int[] { len, len - arrowSize, len - arrowSize, len },
				new int[] { 0, -arrowSize, arrowSize, 0 }, 4);
	}

	/**
	 * Makes a random, yet plaisant looking color
	 * 
	 * @param colorToMix
	 * @return randomColor
	 */
	private Color makeRandomColor(Color colorToMix) {

		Random random = new Random();
		int red = random.nextInt(256);
		int green = random.nextInt(256);
		int blue = random.nextInt(256);

		if (colorToMix != null) {
			red = (int) ((red + colorToMix.getRed()) / 2);
			green = (int) ((green + colorToMix.getGreen()) / 2);
			blue = (int) ((blue + colorToMix.getBlue()) / 2);
		}
		return new Color(red, green, blue);
	}
}
