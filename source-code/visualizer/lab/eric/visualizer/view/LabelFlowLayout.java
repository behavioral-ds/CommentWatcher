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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * @author Samadjon Uroqov
 * 
 *         La classe qui étend FlowLayout pour permettre d'afficher le Panel de
 *         nuage de mots en plusieurs lignes. J'ai etendu parce que FlowLayout
 *         affichait toutes les phrases dans une seule ligne horizontalement
 */
public class LabelFlowLayout extends FlowLayout {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public LabelFlowLayout() {
		super();
	}

	// /**
	// * @param align
	// */
	// public LabelFlowLayout(int align) {
	// super(align);
	// }
	//
	// /**
	// * @param align
	// * @param hgap
	// * @param vgap
	// */
	// public LabelFlowLayout(int align, int hgap, int vgap) {
	// super(align, hgap, vgap);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.FlowLayout#minimumLayoutSize(java.awt.Container)
	 */
	public Dimension minimumLayoutSize(Container target) {
		return computeSize(target, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.FlowLayout#preferredLayoutSize(java.awt.Container)
	 */
	public Dimension preferredLayoutSize(Container target) {
		return computeSize(target, true);
	}

	/**
	 * Calculates a new dimension in order to make the default FlowLayout a
	 * multi-line one. The cloud of words consists of a set of KeyphraseLabels,
	 * first we calculate the dimensions of these objects, then given the size
	 * of the container panel, it is manipulated to place the KeyphraseLabel
	 * until reaching its width, and finally we start to put on a new line the
	 * next KeyphraseLabels
	 * 
	 * @param target
	 * @param minimum
	 * @return new Desired dimension
	 */
	private Dimension computeSize(Container target, boolean minimum) {
		int hGap = getHgap();
		int vGap = getVgap();
		int tagetWidth = target.getWidth();

		Insets insets = target.getInsets();
		if (insets == null)
			insets = new Insets(0, 0, 0, 0);
		int reqestedWidth = 0;

		int maxWidth = tagetWidth - (insets.left + insets.right + hGap * 2);
		int componentCount = target.getComponentCount();
		int x = 0;
		int y = insets.top;
		int rowsHeight = 0;

		for (int i = 0; i < componentCount; i++) {
			Component currentComponent = target.getComponent(i);
			if (currentComponent.isVisible()) {
				Dimension componentDimension = minimum ? currentComponent
						.getMinimumSize() : currentComponent.getPreferredSize();

				if ((x == 0) || ((x + componentDimension.width) <= maxWidth)) {
					if (x > 0) {
						x += hGap;
					}
					x += componentDimension.width;
					rowsHeight = Math
							.max(rowsHeight, componentDimension.height);
				} else {
					x = componentDimension.width;
					y += vGap + rowsHeight;
					rowsHeight = componentDimension.height;
				}
				reqestedWidth = Math.max(reqestedWidth, x);
			}
		}
		y += rowsHeight;
		return new Dimension(reqestedWidth + insets.left + insets.right, y);
	}
}
