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

import java.sql.Timestamp;
import org.w3c.dom.Node;
import static storybook.model.hbn.entity.AbstractEntity.getXmlText;

import storybook.i18n.I18N;
import storybook.toolkit.TextUtil;
import storybook.toolkit.html.HtmlUtil;

@SuppressWarnings("serial")
public class Chapter extends AbstractEntity implements Comparable<Chapter> {

	private Part part;
	private Integer chapterno;
	private String title;
	private String description;
	private String notes;
    private Timestamp creationTime;
    private Timestamp objectiveTime;
    private Timestamp doneTime;
    private Integer objectiveChars;

	public Chapter() {
	}

	public Chapter(Part part, Integer chapterno, String title,
			String description, String notes,
			Timestamp creationTime, Timestamp objectiveTime,
			Timestamp doneTime, Integer objectiveChars) {
		this.part = part;
		this.chapterno = chapterno;
		this.title = title;
		this.description = description;
		this.notes = notes;
		this.creationTime = creationTime;
		this.objectiveTime = objectiveTime;
		this.doneTime = doneTime;
		this.objectiveChars = objectiveChars;
	}

	public Part getPart() {
		return part;
	}

	public boolean hasPart() {
		return part != null;
	}

	public void setPart(Part part) {
		this.part = part;
	}

	public Integer getChapterno() {
		return this.chapterno == null ? -1 : this.chapterno;
	}

	public void setChapterno(Integer chapterno) {
		this.chapterno = chapterno;
	}

	public String getChapternoStr() {
		return getChapterno().toString();
	}

	public String getTitle() {
		return this.title == null ? "" : this.title;
	}

	public String getTitle(boolean truncate) {
		return title == null ? "" : TextUtil.truncateString(title, 30);
	}

	public String getTitle(int truncate) {
		return title == null ? "" : TextUtil.truncateString(title, truncate);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean hasNotes() {
		return(!HtmlUtil.htmlToText(this.notes).equals(""));
	}
	
	public String getNbScenes() {
		return("");
	}
	
	public void setNdScenes(Integer n) {
	}

	public String getNotes() {
		if (notes == null) {
			return "";
		}
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

    public boolean hasCreationTime() {
        return creationTime != null;
    }

    public void setCreationTime(Timestamp ts) {
    	creationTime = ts;
    }

    public Timestamp getCreationTime() {
    	return creationTime;
    }

    public boolean hasObjectiveTime() {
        return objectiveTime != null;
    }

    public void setObjectiveTime(Timestamp ts) {
    	objectiveTime = ts;
    }

    public Timestamp getObjectiveTime() {
    	return objectiveTime;
    }

    public boolean isDone() {
        return hasDoneTime();
    }
    public boolean hasDoneTime() {
        return doneTime != null;
    }

    public void setDoneTime(Timestamp ts) {
    	doneTime = ts;
    }

    public Timestamp getDoneTime() {
    	return doneTime;
    }

	public Integer getObjectiveChars() {
		return (this.objectiveChars == null) ? 0 : this.objectiveChars;
	}

	public void setObjectiveChars(Integer objectiveChars) {
		this.objectiveChars = objectiveChars;
	}
	
	@Override
	public String toString() {
		if (chapterno == null) {
			return I18N.getMsg("scenes.unassigned");
		}
		return(getChapterno() + ": " + getTitle()+(this.hasNotes()?"*":""));
	}
	
	@Override
	public String toCsv(String quoteStart,String quoteEnd, String separator) {
		StringBuilder b=new StringBuilder();
		b.append(quoteStart).append(getId().toString()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getPart().getId().toString()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getChapterno().toString()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getTitle()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getCreationTime())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getObjectiveTime())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getDoneTime())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getObjectiveChars())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getDescription())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getNotes())).append(quoteEnd).append("\n");
		return(b.toString());
	}
	
	@Override
	public String toHtml() {
		StringBuilder b=new StringBuilder();
		b.append("<h2>");
		b.append(getChapterno().toString());
		b.append(" ");
		b.append(getTitle());
		b.append("</h2>\n");
		return(b.toString());
	}
	
	public String getFullName() {
		StringBuilder b=new StringBuilder();
		b.append(getChapterno().toString());
		b.append(" ");
		b.append(getTitle());
		b.append("\n");
		return(b.toString());
	}
	
	@Override
	public String toText() {
		return(toCsv("","","\t"));
	}
	
	@Override
	public String toXml() {
		StringBuilder b=new StringBuilder();
		b.append(xmlTab(1)).append("<chapter \n");
		b.append(xmlCommon());
		b.append(xmlAttribute("part", getPart().getName()));
		b.append(xmlAttribute("number", getChapterno().toString()));
		b.append(xmlTab(2)).append(">\n");
		b.append(xmlMeta(2,"title",getClean(getTitle())));
		b.append(xmlTab(2)).append("<objective \n");
		b.append(xmlAttribute("creation", getClean(getCreationTime())));
		b.append(xmlAttribute("objective", getClean(getObjectiveTime())));
		b.append(xmlAttribute("done", getClean(getDoneTime())));
		b.append(xmlAttribute("chars", getClean(getObjectiveChars())));
		b.append(xmlTab(2)).append("/>\n");
		b.append(xmlMeta(2,"description",getDescription()));
		b.append(xmlMeta(2,"notes",getNotes()));
		b.append(xmlTab(1)).append("</chapter>\n");
		return(b.toString());
	}
	
	public static Chapter fromXml(Node node) {
		Chapter p=new Chapter();
		p.setId(getXmlLong(node,"id"));
		p.setChapterno(getXmlInteger(node,"number"));
		p.setTitle(getXmlText(node,"title").replace("''", "\""));
		p.setDescription(getXmlText(node,"description"));
		p.setNotes(getXmlText(node,"notes"));
		return(p);
	}

	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		Chapter test = (Chapter) obj;
		boolean ret = true;
		ret = ret && equalsObjectNullValue(part, test.getPart());
		ret = ret && equalsIntegerNullValue(chapterno, test.getChapterno());
		ret = ret && equalsStringNullValue(title, test.getTitle());
		ret = ret && equalsStringNullValue(description, test.getDescription());
		ret = ret && equalsStringNullValue(notes, test.getNotes());
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + part.hashCode();
		hash = hash * 31 + chapterno.hashCode();
		hash = hash * 31 + title.hashCode();
		hash = hash * 31 + description.hashCode();
		hash = hash * 31 + notes.hashCode();
		return hash;
	}

	@Override
	public int compareTo(Chapter ch) {
		return chapterno.compareTo(ch.chapterno);
	}

}
