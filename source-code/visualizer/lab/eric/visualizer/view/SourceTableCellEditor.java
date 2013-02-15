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
import java.util.EventObject;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * @author Samadjon Uroqov
 * 
 *         La classe pour editer les objets JPanel placés dans la table de
 *         sources
 */

public class SourceTableCellEditor extends JPanel implements TableCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void addCellEditorListener(CellEditorListener arg0) {
		// TODO Auto-generated method stub
		// System.out.println("SourceTableCellEditor: addCellEditorListener called, event: "
		// + arg0);

	}

	@Override
	public void cancelCellEditing() {
		// TODO Auto-generated method stub
		// System.out.println("SourceTableCellEditor: cancelCellEditing called");

	}

	@Override
	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		// System.out.println("SourceTableCellEditor: getCellEditorValue called");
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject arg0) {
		// TODO Auto-generated method stub
		// System.out.println("SourceTableCellEditor: isCellEditable called, event: "
		// + arg0);
		return true;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener arg0) {
		// TODO Auto-generated method stub
		// System.out.println("SourceTableCellEditor: removeCellEditorListener called, event: "
		// + arg0);

	}

	@Override
	public boolean shouldSelectCell(EventObject arg0) {
		// TODO Auto-generated method stub
		// System.out.println("SourceTableCellEditor: shouldSelectCell called, event: "
		// + arg0);
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		// TODO Auto-generated method stub
		// System.out.println("SourceTableCellEditor: stopCellEditing called");
		return true;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table,
			Object value, boolean isSelected, final int row, final int col) {

		// System.out.println("getTableCellEditorComponent called");

		if (!(value instanceof JPanel)) {
			return null;
		}

		// System.out.println("SourceTableCellEditor: getTableCellEditorComponent called");

		final JPanel panel = (JPanel) value;

		return panel;
	}

}
