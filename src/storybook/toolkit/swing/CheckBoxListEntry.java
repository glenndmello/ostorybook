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

import javax.swing.JCheckBox;

/**
 *
 * @author favdb
 */
class CheckBoxListEntry extends JCheckBox {

	private Object value = null;

	private boolean red = false;

	public CheckBoxListEntry(Object itemValue, boolean selected) {
		super(itemValue == null ? "" : "" + itemValue, selected);
		setValue(itemValue);
	}

	public boolean isSelected() {
		return super.isSelected();
	}

	public void setSelected(boolean selected) {
		super.setSelected(selected);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isRed() {
		return red;
	}

	public void setRed(boolean red) {
		this.red = red;
	}

}
