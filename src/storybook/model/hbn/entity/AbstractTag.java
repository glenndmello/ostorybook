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

import java.awt.Dimension;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import storybook.i18n.I18N;
import storybook.toolkit.html.HtmlUtil;


/**
 * @hibernate.class
 *   table="TAG"
 *   discriminator-value="-1"
 * @hibernate.discriminator
 *   type="integer"
 *   column="type"
 */
public abstract class AbstractTag extends AbstractEntity {

	public static final int TYPE_TAG = 0;
	public static final int TYPE_ITEM = 1;
	public static final int TYPE_LINK = 10;
	public static final int TYPE_MEMO = 20;

	protected Integer type;
	private String category;
	private String name;
	private String description;
	private String notes;
	private String icone;

	public AbstractTag() {
	}

	public AbstractTag(Integer type, String category, String name, String description, String notes) {
		this.type = type;
		this.category = category;
		this.name = name;
		this.description = description;
		this.notes = notes;
	}

	public Tag getTag() {
		if (type != TYPE_TAG) {
			return null;
		}
		return (Tag) this;
	}

	public Item getItem() {
		if (type != TYPE_ITEM) {
			return null;
		}
		return (Item) this;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = (category == null ? "" : category);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean hasNotes() {
		return(!HtmlUtil.htmlToText(this.notes).equals(""));
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public void setIcone(String str) {
		icone=str;
	}
	
	public String getIcone() {
		return icone;
	}
	
	@Override
	public Icon getImageIcon() {
		if (icone!= null) {
			return new ImageIcon(getIcone());
		}
		if (type==TYPE_ITEM) return(I18N.getIcon("icon.small.item"));
		else if (type==TYPE_MEMO) return(I18N.getIcon("icon.small.memo"));
		return(I18N.getIcon("icon.small.tag"));
	}

	public Icon getImageIcon(int h, int l) {
		ImageIcon ic=(ImageIcon) (I18N.getIcon("icon.small.item"));
		if (icone!= null) {
			ic=new ImageIcon(getIcone());
		}
		if (null!=type) switch (type) {
			case TYPE_ITEM:
				ic=(ImageIcon) (I18N.getIcon("icon.small.item"));
				break;
			case TYPE_MEMO:
				ic=(ImageIcon) (I18N.getIcon("icon.small.memo"));
				break;
			default:
				ic=(ImageIcon) I18N.getIcon("icon.small.tag");
				break;
		}
		return(I18N.resizeIcon(ic,new Dimension(h,l)));
	}

	@Override
	public String toCsv(String quoteStart,String quoteEnd, String separator) {
		StringBuilder b=new StringBuilder();
		b.append(quoteStart).append(getId().toString()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getName()).append(quoteEnd).append(separator);
		if (getType()==20) {
			b.append(quoteStart).append(getClean(getNotes())).append(quoteEnd).append("\n");
		} else {
			b.append(quoteStart).append(getCategory()).append(quoteEnd).append(separator);
			b.append(quoteStart).append(getIcone()).append(quoteEnd).append(separator);
			b.append(quoteStart).append(getDescription()).append(quoteEnd).append(separator);
			b.append(quoteStart).append(getClean(getNotes())).append(quoteEnd).append("\n");
		}
		return(b.toString());
	}
	
	@Override
	public String toHtml() {
		return(toCsv("<td>","</td","\n"));
	}
	
	@Override
	public String toText() {
		return(toCsv("","","\t"));
	}
	
	@Override
	public String toString() {
		return getName()+(this.hasNotes()?"*":"");
	}
	
	@Override
	public String toXml() {
		StringBuilder b=new StringBuilder();
		String typestr="tag";
		switch(getType()) {
			case 0: typestr="tag";break;
			case 1: typestr="item";break;
			case 10: typestr="link";break;
			case 20: typestr="memo";break;
		}
		b.append(xmlTab(1)).append("<").append(typestr).append(" \n");
		b.append(xmlCommon());
		b.append(xmlAttribute("name", getName()));
		b.append(xmlAttribute("category", getCategory()));
		b.append(xmlAttribute("icone", getIcone()));
		b.append(xmlTab(2)).append(">\n");
		b.append(xmlMeta(2,"description",getDescription()));
		b.append(xmlMeta(2,"notes",getNotes()));
		b.append(xmlTab(1)).append("</").append(typestr).append(">\n");
		return(b.toString());
	}
	
	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		AbstractTag test = (AbstractTag) obj;
		if (!Objects.equals(type, test.type)) {
			return false;
		}
		boolean ret = true;
		ret = ret && equalsStringNullValue(name, test.getName());
		ret = ret && equalsStringNullValue(category, test.getCategory());
		ret = ret && equalsStringNullValue(description, test.getDescription());
		ret = ret && equalsStringNullValue(notes, test.getNotes());
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + (type != null ? type.hashCode() : 0);
		hash = hash * 31 + (name != null ? name.hashCode() : 0);
		hash = hash * 31 + (category != null ? category.hashCode() : 0);
		hash = hash * 31 + (description != null ? description.hashCode() : 0);
		hash = hash * 31 + (notes != null ? notes.hashCode() : 0);
		return hash;
	}
}
