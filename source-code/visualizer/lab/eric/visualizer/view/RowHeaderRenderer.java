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

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

import lab.eric.visualizer.model.ConfigurationManager;


/**
 * @author Samadjon Uroqov
 * 
 *         Renders the table row headers with or without icons. If source icon
 *         is present makes a JLabel with icon. If icon not present, makes a
 *         JLabel with source string
 */
public class RowHeaderRenderer extends JLabel implements ListCellRenderer {

	/**
				 * 
				 */
	private static final long serialVersionUID = 1L;

	/**
	 * Renders the table row headers with or without icons. If source icon is
	 * present makes a JLabel with icon. If icon not present, makes a JLabel
	 * with source string
	 * 
	 * @param table
	 */
	public RowHeaderRenderer(JTable table) {
		JTableHeader header = table.getTableHeader();
		setOpaque(true);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setHorizontalAlignment(CENTER);
		setForeground(header.getForeground());
		setBackground(header.getBackground());
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		if (value == null) {
			setText("");
			return this;
		}
		if (value instanceof Icon) {
			setText(null);
			setIcon((Icon) value);
		} else {
			setText(value.toString());
			setIcon(null);
		}

		// list.setFixedCellHeight(ConfigurationManager.rowHeaderSize.height);
		list.setFixedCellWidth(ConfigurationManager.rowHeaderSize.width);
		setOpaque(true);
		return this;
	}
}
