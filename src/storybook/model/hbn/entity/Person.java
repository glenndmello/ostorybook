/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2015 Martin Mustun, Pete Keller

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

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.w3c.dom.Node;
import storybook.toolkit.DateUtil;
import static storybook.toolkit.DateUtil.clearTime;
import storybook.toolkit.html.HtmlUtil;

import storybook.toolkit.swing.ColorUtil;

/**
 * @hibernate.class
 *   table="PERSON"
 */
public class Person extends AbstractEntity implements Comparable<Person> {

	private Gender gender;
	private String firstname = "";
	private String lastname = "";
	private String abbreviation = "";
	private Date birthday;
	private Date dayofdeath;
	private String occupation = "";
	private String description = "";
	private Integer color;
	private String notes = "";
	private Category category;
	private List<Attribute> attributes;

	public Person() {
		super();
	}

	public Person(Gender gender, String firstname, String lastname,
			String abbreviation, Date birthday, Date dayofdeath,
			String occupation, String description, Integer color, String notes,
			Category category, List<Attribute> attributes) {
		this.gender = gender;
		this.firstname = firstname;
		this.lastname = lastname;
		this.abbreviation = abbreviation;
		this.birthday = birthday;
		this.dayofdeath = dayofdeath;
		this.occupation = occupation;
		this.description = description;
		this.color = color;
		this.notes = notes;
		this.category = category;
		this.attributes = attributes;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getFirstname() {
		return this.firstname == null ? "" : this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return this.lastname == null ? "" : this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFullName() {
		return getFirstname() + " " + getLastname();
	}

	public String getFullNameAbbr() {
		return getFirstname() + " " + getLastname() + " [" + getAbbreviation()
				+ "]";
	}

	public String getAbbreviation() {
		return this.abbreviation == null ? "" : this.abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Date getDayofdeath() {
		return this.dayofdeath;
	}

	public void setDayofdeath(Date dayofdeath) {
		this.dayofdeath = dayofdeath;
	}

	public String getOccupation() {
		return this.occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getColor() {
		return this.color;
	}

	public Color getJColor() {
		if (color == null) {
			return null;
		}
		return new Color(color);
	}

	public String getHTMLColor() {
		return ColorUtil.getHTMLName(getJColor());
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	public void setJColor(Color color) {
		if (color == null) {
			this.color = null;
			return;
		}
		this.color = color.getRGB();
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

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getAbbr(){
		return abbreviation;
	}

	@Override
	public Icon getImageIcon() {
		if (gender != null) {
			return gender.getImageIcon();
		}
		return new ImageIcon();
	}
        
	public Boolean isDead(Date now) {
		if (getDayofdeath() == null || now == null) {
                    return false;
                }
		return (now.after(getDayofdeath()));
	}
        

	public int calculateAge(Date now) {
		if (birthday == null) {
                    return -1;
                }

		Calendar dateOfBirth = new GregorianCalendar();
		dateOfBirth.setTime(birthday);
                dateOfBirth = clearTime(dateOfBirth);


		if (isDead(now)) {
			Calendar death = new GregorianCalendar();
			death.setTime(getDayofdeath());
                        death = clearTime(death);
                        
			int age = death.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
                        
			Calendar dateOfBirth2 = new GregorianCalendar();
                        dateOfBirth2.setTime(birthday);
                        dateOfBirth2 = clearTime(dateOfBirth2);
                        dateOfBirth2.add(Calendar.YEAR, age);
                        
			if (death.before(dateOfBirth2)) {
				age--;
                        }
			return age;
		}

		Calendar today = new GregorianCalendar();
		today.setTime(now);

		int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

		dateOfBirth.add(Calendar.YEAR, age);

		if (today.before(dateOfBirth))
			age--;
		return age;
	}

	@Override
	public String toString() {
		if (isTransient()) {
			return "";
		}
		return getFullNameAbbr()+(this.hasNotes()?"*":"");
	}

	@Override
	public String toCsv(String quoteStart,String quoteEnd, String separator) {
		StringBuilder b=new StringBuilder();
		b.append(quoteStart).append(getId().toString()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getGender())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getFirstname()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getLastname()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getAbbreviation()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getBirthday())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getDayofdeath())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getOccupation()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getColor())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getCategory())).append(quoteEnd).append(separator);
		b.append(quoteStart);
		for (Attribute attr:getAttributes()) {
			b.append(attr.getId().toString()).append("/");
		}
		b.append(quoteEnd).append(separator);
		b.append(quoteStart).append(getDescription()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getNotes()).append(quoteEnd).append("\n");
		return(b.toString());
	}
	
	@Override
	public String toHtml() {
		return(toCsv("<td>","</td>","\n"));
	}
	
	@Override
	public String toText() {
		return(toCsv("","","\t"));
	}
	
	@Override
	public String toXml() {
		StringBuilder b=new StringBuilder();
		b.append(xmlTab(1)).append("<person \n");
		b.append(xmlCommon());
		b.append(xmlAttribute("gender", getClean(getGender().getName())));
		b.append(xmlAttribute("firstname",getFirstname()));
		b.append(xmlAttribute("lastname",getLastname()));
		b.append(xmlAttribute("abbreviation",getAbbreviation()));
		b.append(xmlAttribute("birthday",getClean(getBirthday())));
		b.append(xmlAttribute("death", getClean(getDayofdeath())));
		b.append(xmlAttribute("occupation", getOccupation()));
		b.append(xmlAttribute("color", getClean(getColor())));
		b.append(xmlAttribute("category", getClean(getCategory().getName())));
		b.append(xmlTab(2)).append(">\n");
		for (Attribute attr:getAttributes()) {
			b.append(xmlMeta(2,"attribute","["+attr.getKey()+"]"+attr.getValue()));
		}
		b.append(xmlMeta(2,"description",getDescription()));
		b.append(xmlMeta(2,"notes",getNotes()));
		b.append(xmlTab(1)).append("</person>\n");
		return(b.toString());
	}
	
	@SuppressWarnings("deprecation")
	public static Person fromXml(Node node) {
		Person p=new Person();
		p.setId(getXmlLong(node,"id"));
		p.setFirstname(getXmlString(node,"firstname"));
		p.setLastname(getXmlString(node,"lastname"));
		p.setAbbreviation(getXmlString(node,"abbreviation"));
		p.setBirthday(DateUtil.stdStringToDate(getXmlString(node,"birthday")));
		p.setDayofdeath(DateUtil.stdStringToDate(getXmlString(node,"death")));
		p.setOccupation(getXmlString(node,"occupation"));
		if (!getXmlString(node,"color").isEmpty()) p.setColor(getXmlInteger(node,"color"));
		//checkCategory();
		//checkAttributes();
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
		Person test = (Person) obj;
		boolean ret = true;
		ret = ret && equalsStringNullValue(abbreviation, test.getAbbreviation());
		ret = ret && equalsStringNullValue(firstname, test.getFirstname());
		ret = ret && equalsStringNullValue(lastname, test.getLastname());
		ret = ret && equalsObjectNullValue(gender, test.getGender());
		ret = ret && equalsDateNullValue(birthday, test.getBirthday());
		ret = ret && equalsDateNullValue(dayofdeath, test.getDayofdeath());
		ret = ret && equalsIntegerNullValue(color, test.getColor());
		ret = ret && equalsObjectNullValue(category, test.getCategory());
		ret = ret && equalsStringNullValue(description, test.getDescription());
		ret = ret && equalsStringNullValue(notes, test.getNotes());
		ret = ret && equalsListNullValue(attributes, test.getAttributes());
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + (abbreviation != null ? abbreviation.hashCode() : 0);
		hash = hash * 31 + (firstname != null ? firstname.hashCode() : 0);
		hash = hash * 31 + (lastname != null ? lastname.hashCode() : 0);
		hash = hash * 31 + (gender != null ? gender.hashCode() : 0);
		hash = hash * 31 + (birthday != null ? birthday.hashCode() : 0);
		hash = hash * 31 + (dayofdeath != null ? dayofdeath.hashCode() : 0);
		hash = hash * 31 + (color != null ? color.hashCode() : 0);
		hash = hash * 31 + (category != null ? category.hashCode() : 0);
		hash = hash * 31 + (description != null ? description.hashCode() : 0);
		hash = hash * 31 + (notes != null ? notes.hashCode() : 0);
		hash = hash * 31 + (attributes != null ? getListHashCode(attributes) : 0);
		return hash;
	}

	@Override
	public int compareTo(Person o) {
		if (category == null && o == null) {
			return 0;
		}
		if (category != null && o.getCategory() == null) {
			return -1;
		}
		if (o.getCategory() != null && category == null) {
			return -1;
		}
		int cmp = category.getSort().compareTo(o.getCategory().getSort());
		if (cmp == 0) {
			return getFullName().compareTo(o.getFullName());
		}
		return cmp;
	}

}
