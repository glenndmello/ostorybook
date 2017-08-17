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
package storybook.exim.exporter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.Session;

import storybook.SbPref;
import storybook.model.BookModel;
import storybook.model.hbn.dao.*;
import storybook.model.hbn.entity.*;
import storybook.i18n.I18N;
import storybook.ui.MainFrame;
import storybook.ui.SbView;

/**
 *
 * @author favdb
 */
public class TableExporter extends AbstractExporter {

	private String viewName = "";

	/**
	 *
	 * @param mainFrame
	 * @param view
	 * @param format
	 */
	public TableExporter(MainFrame mainFrame, String view, String format) {
		super(mainFrame, view, format);
	}

	public static void exportDB(MainFrame m, String f) {
		TableExporter export = new TableExporter(m,"DataBase", f);
		if (export.askFileExists("DataBase")) {
			if (JOptionPane.showConfirmDialog(m,
				I18N.getMsg("export.replace", export.param.fileName),
				I18N.getMsg("export"),
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		export.writeDataBase();
	}
	
	public static void exportDB(MainFrame m) {
		exportDB(m,"xml");
	}

	public static void exportTable(MainFrame m, SbView v) {
		exportTable(m,v,m.getPref().getString(SbPref.Key.EXPORT_PREF,"xml"));
	}
	
	public static void exportTable(MainFrame m, SbView v,String f) {
		exportTable(m,v.getName(),f);
	}
	
	public static void exportTable(MainFrame m, String v, String f) {
		TableExporter export=new TableExporter(m,v,f);
		if (export.askFileExists(v)) {
			if (JOptionPane.showConfirmDialog(m,
				I18N.getMsg("export.replace", export.param.fileName),
				I18N.getMsg("export"),
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		if (!export.openFile(v)) {
			return;
		}
		export.writeTable(v);
		export.closeFile(true);
	}

	public void setDir(String d) {
		param.dir = d;
	}

	public void setFormat(String f) {
		param.format = f;
	}

	public void writeTable(String name) {
		if (isOpened == false) {
			return;
		}
		switch (name.toLowerCase()) {
			case "attributes":
				writeAttribute();
				break;
			case "categories":
				writeCategory();
				break;
			case "chapters":
				writeChapter();
				break;
			case "genders":
				writeGender();
				break;
			case "ideas":
				writeIdea();
				break;
			case "internals":
				writeInternal();
				break;
			case "items":
				writeItem();
				break;
			case "itemlinks":
				writeItemLink();
				break;
			case "locations":
				writeLocation();
				break;
			case "memos":
				writeMemo();
				break;
			case "parts":
				writePart();
				break;
			case "persons":
				writePerson();
				break;
			case "relationships":
				writeRelationship();
				break;
			case "scenes":
				writeScene();
				break;
			case "strands":
				writeStrand();
				break;
			case "tags":
				writeTag();
				break;
			case "taglinks":
				writeTagLink();
				break;
			case "timeevents":
				writeTimeEvent();
				break;
			default:
				break;
		}
	}

	private void writeAttribute() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("attribute.key"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("attribute.value"), 80));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		AttributeDAOImpl dao = new AttributeDAOImpl(session);
		List<Attribute> entities = dao.findAll();
		for (Attribute e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeCategory() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("category.name"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("category.sort"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("category.value"), 80));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		CategoryDAOImpl dao = new CategoryDAOImpl(session);
		List<Category> entities = dao.findAll();
		for (Category e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeChapter() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("chapter.number"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("title"), 80));
		headers.add(new ColumnHeader(I18N.getMsg("part"), 20));
		headers.add(new ColumnHeader(I18N.getMsg("manage.date.creation"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("manage.date.objective"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("manage.date.done"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("manage.size.objective"), 6));
		headers.add(new ColumnHeader(I18N.getMsg("description"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Chapter> entities = dao.findAll();
		for (Chapter e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeGender() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("name"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("chart.gantt.childhood"), 3));
		headers.add(new ColumnHeader(I18N.getMsg("chart.gantt.adolescence"), 3));
		headers.add(new ColumnHeader(I18N.getMsg("chart.gantt.adulthood"), 3));
		headers.add(new ColumnHeader(I18N.getMsg("chart.gantt.retirement"), 3));
		headers.add(new ColumnHeader(I18N.getMsg("icone"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		GenderDAOImpl dao = new GenderDAOImpl(session);
		List<Gender> entities = dao.findAll();
		for (Gender e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeIdea() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("category"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("status"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		IdeaDAOImpl dao = new IdeaDAOImpl(session);
		List<Idea> entities = dao.findAll();
		for (Idea e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeInternal() {
		if ("xml".equals(param.format)) writeText("    <info>\n");
		else {
			List<ColumnHeader> headers = new ArrayList<>();
			headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
			headers.add(new ColumnHeader(I18N.getMsg("key"), 16));
			headers.add(new ColumnHeader(I18N.getMsg("value"), 32));
			writeHeaderColumn(headers);
		}
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		InternalDAOImpl dao = new InternalDAOImpl(session);
		List<Internal> entities = dao.findAll();
		for (Internal e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
		if ("xml".equals(param.format)) writeText("    </info>\n");
	}

	private void writeItem() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("name"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("category"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("status"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("description"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		List<Item> entities = dao.findAll();
		for (Item e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeItemLink() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("scene.start"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("scene.end"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("location"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("person"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ItemLinkDAOImpl dao = new ItemLinkDAOImpl(session);
		List<ItemLink> entities = dao.findAll();
		for (ItemLink e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeLocation() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("name"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("address"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("city"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("country"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("altitude"), 8));
		headers.add(new ColumnHeader(I18N.getMsg("location.site"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("description"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<Location> entities = dao.findAll();
		for (Location e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeMemo() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("name"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		MemoDAOImpl dao = new MemoDAOImpl(session);
		List<Memo> entities = dao.findAll();
		for (Memo e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writePart() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("number"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("name"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("part"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("manage.date.creation"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("manage.date.objective"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("manage.date.done"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("manage.size.objective"), 6));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Part> entities = dao.findAll();
		for (Part e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writePerson() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("gender"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("person.firstname"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("person.lastname"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("person.abbr"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("person.birthday"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("person.death"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("person.occupation"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("person.color"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("category"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("attribute_s"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("description"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		List<Person> entities = dao.findAll();
		for (Person e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeRelationship() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("person.first"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("person.second"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("scene.start"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("scene.end"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("persons"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("items"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("locations"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("description"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		RelationshipDAOImpl dao = new RelationshipDAOImpl(session);
		List<Relationship> entities = dao.findAll();
		for (Relationship e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeScene() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("title"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("chapter"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("strand"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("number"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("date"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("status"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("informative"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("scene.relativedate.occurs"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("scene.relativedate.after"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("scene.narrator"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("scene.relativedate.after"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("xeditor"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("persons"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("items"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("locations"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("strands"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("scene.summary"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> entities = dao.findAll();
		for (Scene e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeStrand() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("strand.abbr"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("name"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("color"), 8));
		headers.add(new ColumnHeader(I18N.getMsg("sort"), 10));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		StrandDAOImpl dao = new StrandDAOImpl(session);
		List<Strand> entities = dao.findAll();
		for (Strand e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeTag() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("name"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("category"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("status"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("description"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TagDAOImpl dao = new TagDAOImpl(session);
		List<Tag> entities = dao.findAll();
		for (Tag e : entities) {
			switch (param.format) {
				case "xml": writeText(e.toXml());  break;
				case "hml": writeText(e.toHtml()); break;
				case "csv": writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma)); break;
				case "txt": writeText(e.toText()); break;
			}
		}
		model.commit();
	}

	private void writeTagLink() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("scene.start"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("scene.end"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("location"), 32));
		headers.add(new ColumnHeader(I18N.getMsg("person"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TagLinkDAOImpl dao = new TagLinkDAOImpl(session);
		List<TagLink> entities = dao.findAll();
		for (TagLink e : entities) {
			switch (param.format) {
				case "xml":
					writeText(e.toXml());
					break;
				case "hml":
					writeText(e.toHtml());
					break;
				case "csv":
					writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma));
					break;
				case "txt":
					writeText(e.toText());
					break;
			}
		}
		model.commit();
	}

	private void writeTimeEvent() {
		List<ColumnHeader> headers = new ArrayList<>();
		headers.add(new ColumnHeader(I18N.getMsg("id"), 5));
		headers.add(new ColumnHeader(I18N.getMsg("title"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("timeevent.date"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("timeevent.combo.label"), 8));
		headers.add(new ColumnHeader(I18N.getMsg("category"), 16));
		headers.add(new ColumnHeader(I18N.getMsg("notes"), 32));
		writeHeaderColumn(headers);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TimeEventDAOImpl dao = new TimeEventDAOImpl(session);
		List<TimeEvent> entities = dao.findAll();
		for (TimeEvent e : entities) {
			switch (param.format) {
				case "xml":
					writeText(e.toXml());
					break;
				case "hml":
					writeText(e.toHtml());
					break;
				case "csv":
					writeText(e.toCsv(param.csvQuote, param.csvQuote, param.csvComma));
					break;
				case "txt":
					writeText(e.toText());
					break;
			}
		}
		model.commit();
	}

	public void writeDataBase() {
		viewName = "DataBase";
		if (!openFile(viewName)) {
			return;
		}
		writeInternal();
		writeAttribute();
		writeCategory();
		writeGender();
		writeItem();
		writeItemLink();
		writeLocation();
		writePerson();
		writeTag();
		writeTagLink();
		writeIdea();
		writeMemo();
		writeRelationship();
		writeTimeEvent();
		writeStrand();
		writePart();
		writeChapter();
		writeScene();
		closeFile(VERBOSE);
	}

	private void writeHeaderColumn(List<ColumnHeader> headers) {
		if (headers == null || headers.isEmpty()) {
			return;
		}
		switch(param.format) {
			case "html":
				writeText("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n<tr>\n");
				for (ColumnHeader header : headers) {
					writeColumn(header.getName(), header.getSize());
				}
				writeText("</tr>\n");
				break;
			case "xml": //no headers
				break;
			case "csv":
				for (ColumnHeader header : headers) {
					writeText(param.csvQuote+header.getName()+param.csvQuote+param.csvComma);
				}
				writeText("\n");
			case "txt":
				for (ColumnHeader header : headers) {
					writeText(header.getName()+param.txtSeparator);
				}
				writeText("\n");
		}
	}

	private void writeColumn(String name, int size) {
		switch (param.format) {
			case "html":
				if (size > 0) {
					writeText("    <td width=\"" + size + "%\">" + ("".equals(name) ? "&nbsp" : name) + "</td>\n");
				} else {
					writeText("    <td>" + name + "</td>\n");
				}
				break;
			case "csv":
				writeText("\"" + name + "\";");
				break;
			case "text":
				writeText("\t" + name + "");
				break;
		}
	}

	public class ColumnHeader {

		private final String name;
		private final int size;

		ColumnHeader(String n, int s) {
			name = n;
			size = s;
		}

		public String getName() {
			return (name);
		}

		public int getSize() {
			return (size);
		}

	}
}
