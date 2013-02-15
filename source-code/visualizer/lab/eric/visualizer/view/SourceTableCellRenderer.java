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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author Samadjon Uroqov
 * 
 *         La classe pour afficher les objets de JPanel placés dans la table de
 *         sources
 */

public class SourceTableCellRenderer extends JPanel implements
		TableCellRenderer {

	/**
	 * La classe pour afficher les objets de JPanel placés dans la table de
	 * sources
	 */
	public SourceTableCellRenderer() {

	}

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, final int row, int column) {

		if (!(value instanceof JPanel)) {
			return new JLabel("");
		}
		// System.out.println("SourceTableCellEditor: getTableCellRendererComponent called");

		JPanel panel = (JPanel) value;

		return panel;

	}

}
