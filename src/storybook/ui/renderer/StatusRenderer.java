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
package storybook.ui.renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import storybook.i18n.I18N;

/**
 *
 * @author favdb
 */
public class StatusRenderer extends JLabel implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(
								JList list,
								Object value,
								int index,
								boolean isSelected,
								boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (value!=null) {
			setText(I18N.getMsg(value.toString()));
			String[] lbSt = {
				"status.outline",
				"status.draft",
				"status.1st.edit",
				"status.2nd.edit",
				"status.done"
			};
			String[] icSt = {
				"icon.small.status.outline",
				"icon.small.status.draft",
				"icon.small.status.edit1",
				"icon.small.status.edit2",
				"icon.small.status.done"
			};
			for(int i=0;i<lbSt.length;i++) {
				if (lbSt[i].equals(value.toString())) {
					setIcon(I18N.getIcon(icSt[i]));
				}
			}
		}

		return this;
	}
}
