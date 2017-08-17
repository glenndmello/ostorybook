/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.ui.panel.tree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import storybook.model.EntityUtil;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.model.hbn.entity.Tag;
import storybook.toolkit.html.HtmlUtil;

@SuppressWarnings("serial")
class EntityTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
		boolean sel, boolean expanded, boolean leaf, int row,
		boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object object = node.getUserObject();
		if (leaf) {
			if (object instanceof Person) {
				setLeafIcon(((Person) object).getGender().getImageIcon());
			} else if (object instanceof Scene) {
				setLeafIcon(((Scene) object).getSceneState().getIcon());
			} else if (object instanceof AbstractEntity) {
				Icon icon = EntityUtil.getEntityIcon((AbstractEntity) object);
				setLeafIcon(icon);
			} else {
				setLeafIcon(null);
			}
		}
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (!leaf && object instanceof AbstractEntity) {
			Icon icon = EntityUtil.getEntityIcon((AbstractEntity) object);
			setIcon(icon);
		}
		setNotes(object);
		return this;
	}

	;

	private void setNotes(Object object) {
		String texte = null;
		if (object instanceof Strand) {
			texte = (((Strand) object).getNotes());
		} else if (object instanceof Person) {
			texte = (((Person) object).getNotes());
		} else if (object instanceof Part) {
			texte = (((Part) object).getNotes());
		}  else if (object instanceof Chapter) {
			texte = (((Chapter) object).getNotes());
		} else if (object instanceof Scene) {
			texte = (((Scene) object).getNotes());
		} else if (object instanceof Item) {
			texte = (((Item) object).getNotes());
		}  else if (object instanceof Location) {
			texte = (((Location) object).getNotes());
		}  else if (object instanceof Tag) {
			texte = (((Tag) object).getNotes());
		} 
		if (!HtmlUtil.htmlToText(texte).equals("")) {
			setToolTipText("<html>" + texte + "</html>");
		} else {
			setToolTipText(null);
		}

	}
}
