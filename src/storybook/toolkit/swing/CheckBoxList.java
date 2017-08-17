/*
 * Copyright (C) 2016 favdb
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package storybook.toolkit.swing;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author favdb
 */
public class CheckBoxList extends JList<javax.swing.JCheckBox> {

	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public CheckBoxList() {
		setCellRenderer(new CellRenderer());
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				if (index != -1) {
					javax.swing.JCheckBox checkbox = (javax.swing.JCheckBox) getModel().getElementAt(index);
					checkbox.setSelected(!checkbox.isSelected());
					repaint();
				}
			}
		});
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public CheckBoxList(ListModel<javax.swing.JCheckBox> model) {
		this();
		setModel(model);
	}
	
	public int getCheckBoxSize() {
		@SuppressWarnings("unchecked")
		DefaultListModel<javax.swing.JCheckBox> ckmodel = (DefaultListModel)this.getModel();
		return(ckmodel.getSize());
	}
	
	public boolean getCheckBox(String str) {
		@SuppressWarnings("unchecked")
		DefaultListModel<javax.swing.JCheckBox> ckmodel = (DefaultListModel)this.getModel();
		for (int i=0; i<ckmodel.getSize(); i++) {
			javax.swing.JCheckBox ck=ckmodel.getElementAt(i);
			return (ck.isSelected());
		}
		return(false);
	}

	public void setCheckBox(String str, boolean enable) {
		@SuppressWarnings("unchecked")
		DefaultListModel<javax.swing.JCheckBox> ckmodel = (DefaultListModel)this.getModel();
		for (int i=0; i<ckmodel.getSize(); i++) {
			javax.swing.JCheckBox ck=ckmodel.getElementAt(i);
			if (ck.getText().equals(str)) {
				ck.setSelected(enable);
				ckmodel.setElementAt(ck, i);
			}
		}
		this.revalidate();
	}

	protected class CellRenderer implements ListCellRenderer<javax.swing.JCheckBox> {

		@Override
		public Component getListCellRendererComponent(
			JList<? extends javax.swing.JCheckBox> list, javax.swing.JCheckBox value, int index,
			boolean isSelected, boolean cellHasFocus) {
			javax.swing.JCheckBox checkbox = value;
			checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return checkbox;
		}
	}
}
