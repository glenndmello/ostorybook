/*
Storybook: Open Source software for novelists and authors.
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

package storybook.model.hbn.entity;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import static storybook.model.hbn.entity.AbstractEntity.getXmlText;

/**
 * @hibernate.subclass
 *   discriminator-value="0"
 */
public class Tag extends AbstractTag {

	public static Tag fromXml(Node node) {
		Tag p=new Tag();
		p.setId(getXmlLong(node,"id"));
		p.setName(getXmlString(node,"name"));
		p.setCategory(getXmlString(node,"category"));
		p.setIcone(getXmlString(node,"icone"));
		p.setDescription(getXmlText(node,"description"));
		p.setNotes(getXmlText(node,"notes"));
		return(p);
	}

}
