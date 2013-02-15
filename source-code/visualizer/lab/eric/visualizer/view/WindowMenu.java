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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import lab.eric.visualizer.lang.Messages;

/**
 * Menu component that handles the functionality expected of a standard
 * "Windows" menu for MDI applications.
 */

public class WindowMenu extends JMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MDIDesktopPane desktop;

	private JMenuItem cascadeWindows = new JMenuItem(Messages.getString("WindowMenu.0")); //$NON-NLS-1$
	private JMenuItem tileWindows = new JMenuItem(Messages.getString("WindowMenu.1")); //$NON-NLS-1$
	private JMenuItem closeWindows = new JMenuItem(Messages.getString("WindowMenu.2")); //$NON-NLS-1$

	public WindowMenu(MDIDesktopPane desktop1) {
		this.desktop = desktop1;
		setText(Messages.getString("WindowMenu.3")); //$NON-NLS-1$

		cascadeWindows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				desktop.cascadeFrames();
			}
		});
		tileWindows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				desktop.tileFrames();
			}
		});

		closeWindows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				desktop.disposeFrames();
			}
		});

		addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
			}

			public void menuDeselected(MenuEvent e) {
				removeAll();
			}

			public void menuSelected(MenuEvent e) {
				buildChildMenus();
			}
		});
	}

	/**
	 * Sets up the children menus depending on the current desktop state
	 */
	private void buildChildMenus() {
		int i;
		ChildMenuItem menu;
		JInternalFrame[] array = desktop.getAllFrames();

		add(cascadeWindows);
		add(tileWindows);
		add(closeWindows);
		if (array.length > 0)
			addSeparator();
		cascadeWindows.setEnabled(array.length > 0);
		tileWindows.setEnabled(array.length > 0);
		closeWindows.setEnabled(array.length > 0);

		for (i = 0; i < array.length; i++) {
			menu = new ChildMenuItem(array[i]);
			menu.setState(i == 0);
			menu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JInternalFrame frame = ((ChildMenuItem) ae.getSource())
							.getFrame();
					frame.moveToFront();
					try {
						frame.setSelected(true);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			});
			menu.setIcon(array[i].getFrameIcon());
			add(menu);
		}
	}

	/**
	 * This JCheckBoxMenuItem descendant is used to track the child frame that
	 * corresponds to a give menu.
	 * 
	 */
	class ChildMenuItem extends JCheckBoxMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JInternalFrame frame;

		public ChildMenuItem(JInternalFrame frame) {
			super(frame.getTitle());
			this.frame = frame;
		}

		public JInternalFrame getFrame() {
			return frame;
		}
	}
}
