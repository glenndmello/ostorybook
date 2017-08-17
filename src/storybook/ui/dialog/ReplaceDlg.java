/*
 * Copyright (C) 2017 favdb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;
import storybook.i18n.I18N;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.IdeaDAOImpl;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.MemoDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Memo;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.model.hbn.entity.Tag;
import storybook.toolkit.TextUtil;
import storybook.toolkit.html.HtmlUtil;
import storybook.toolkit.swing.FontUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import static storybook.ui.dialog.edit.EntityEditor.MINIMUM_SIZE;

/**
 *
 * @author favdb
 */
public class ReplaceDlg extends AbstractDialog {

	private String words;
	private final JPanel resultat = new JPanel();
	private JTextField txWords;
	List<JCheckBox> cbList;
	String[] objects = {"strand", "part", "chapter", "scene", "person", "location", "item", "tag", "idea", "memo"};
	private JButton btAll;
	private JTextField txByWords;
	private String bywords;
	private JButton btReplace;

	public ReplaceDlg(MainFrame m) {
		super(m);
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		JLabel jLabel1 = new JLabel(I18N.getMsg("search.words"));
		txWords = new JTextField(32);
		txWords.addCaretListener((CaretEvent evt) -> {
			checkIfOk();
		});
		JLabel jLabel2 = new JLabel(I18N.getMsg("replace.bywords"));
		txByWords = new JTextField(32);
		txByWords.addCaretListener((CaretEvent evt) -> {
			checkIfOk();
		});
		btAll = new JButton(I18N.getMsg("all"));
		btAll.addActionListener((ActionEvent evt) -> {
			selectAll();
		});
		JPanel jPanel1 = new JPanel(new MigLayout("wrap 4"));
		jPanel1.setBorder(BorderFactory.createTitledBorder(I18N.getMsg("search.for")));
		cbList = new ArrayList<>();
		for (String str : objects) {
			JCheckBox cb = new JCheckBox(I18N.getMsg(str));
			cb.setName(str);
			cb.addActionListener((ActionEvent evt) -> {
				checkIfOk();
			});
			jPanel1.add(cb);
			cbList.add(cb);
		}
		jPanel1.add(new JLabel(""), "wrap");
		jPanel1.add(btAll, "span,right");

		btReplace = new JButton(I18N.getMsg("find"));
		btReplace.setIcon(new ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/search.png")));
		btReplace.setEnabled(false);
		btReplace.addActionListener((ActionEvent evt) -> {
			searchEntities();
		});

		//layout
		setLayout(new MigLayout());
		setTitle(I18N.getMsg("search"));
		add(jLabel1, "wrap");
		add(txWords, "center,wrap");
		add(jLabel2, "wrap");
		add(txByWords, "center,wrap");
		add(jPanel1, "span,wrap");
		add(getCancelButton(), "sg,span,split 2,right");
		add(btReplace, "sg");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);
	}

	private void checkIfOk() {
		boolean b = !(txWords.getText().isEmpty() && txByWords.getText().isEmpty());
		boolean bb = false;
		for (JCheckBox cb : cbList) {
			if (cb.isSelected()) {
				bb = true;
				break;
			}
		}
		if (bb == false) {
			b = false;
		}
		btReplace.setEnabled(b);
	}

	private void selectAll() {
		boolean x;
		if (btAll.getText().equals(I18N.getMsg("all"))) {
			btAll.setText(I18N.getMsg("none"));
			x = true;
		} else {
			btAll.setText(I18N.getMsg("all"));
			x = false;
		}
		for (JCheckBox cb : cbList) {
			cb.setSelected(x);
		}
		checkIfOk();
	}

	private void searchEntities() {
		String s = HtmlUtil.htmlToText(txWords.getText());
		if (s.isEmpty()) {
			return;
		}
		words = s;
		initResultat();
		int x = 0;
		for (JCheckBox cb : cbList) {
			if (cb.isSelected()) {
				int y = 0;
				switch (cb.getName()) {
					case "strand":
						y += findStrands();
						break;
					case "part":
						y += findParts();
						break;
					case "chapter":
						y += findChapters();
						break;
					case "scene":
						y += findScenes();
						break;
					case "person":
						y += findPersons();
						break;
					case "location":
						y += findLocations();
						break;
					case "item":
						y += findItems();
						break;
					case "tag":
						y += findTags();
						break;
					case "idea":
						y += findIdeas();
						break;
					case "memo":
						y += findMemos();
						break;
					default:
						y = -1;
						break;
				}
				if (y == -1) {
					x = y;
					break;
				}
				if (y > 0) {
					x += y;
				}
			}
		}
		if (x < 0) {
			SwingUtil.showError("search.error.object");
			return;
		}
		if (x > 0) {
			JButton bt = new JButton(I18N.getMsg("replace.all"));
			bt.addActionListener((ActionEvent evt) -> {
				replaceAll("all");
			});
			resultat.add(bt, "span");
		}
		showResults(resultat);
	}

	private int findStrands() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		StrandDAOImpl dao = new StrandDAOImpl(session);
		List<Strand> entities = dao.findAll();
		int finds = 0;
		for (Strand entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				finds++;
			}
		}
		doTitle("strand",finds);
		for (Strand entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (finds == 0) {
					doNext();
				}
				doEntity("strand",entity.getName(), entity);
			}
		}
		return (finds);
	}

	private int findParts() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Part> entities = dao.findAll();
		int finds = 0;
		for (Part entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				finds++;
			}
		}
		doTitle("part",finds);
		for (Part entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (finds == 0) {
					doNext();
				}
				doEntity("prat",entity.getName(), entity);
			}
		}
		return (finds);
	}

	private int findChapters() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Chapter> entities = dao.findAll();
		int finds = 0;
		for (Chapter entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				finds++;
			}
		}
		doTitle("chapter",finds);
		for (Chapter entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (finds == 0) {
					doNext();
				}
				doEntity("chapter",entity.getChapternoStr() + " " + entity.getTitle(), entity);
			}
		}
		return (finds);
	}

	private int findScenes() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> entities = dao.findAll();
		int finds = 0;
		for (Scene entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				finds++;
			}
		}
		doTitle("scene",finds);
		for (Scene entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (finds == 0) {
					doNext();
				}
				doEntity("scene",entity.getFullTitle(), entity);
			}
		}
		return (finds);
	}

	private int findPersons() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		List<Person> entities = dao.findAll();
		int finds = 0;
		for (Person entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				finds++;
			}
		}
		doTitle("person",finds);
		for (Person entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (finds == 0) {
					doNext();
				}
				doEntity("person",entity.getFullNameAbbr(), entity);
			}
		}
		return (finds);
	}

	private int findLocations() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<Location> entities = dao.findAll();
		int finds = 0;
		for (Location entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				finds++;
			}
		}
		doTitle("location",finds);
		for (Location entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (finds == 0) {
					doNext();
				}
				doEntity("location",entity.getName(), entity);
			}
		}
		return (finds);
	}

	private int findItems() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		List<Item> entities = dao.findAll();
		int finds = 0;
		for (Item entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				finds++;
			}
		}
		doTitle("item",finds);
		for (Item entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (finds == 0) {
					doNext();
				}
				doEntity("item",entity.getName(), entity);
			}
		}
		return (finds);
	}

	private int findTags() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TagDAOImpl dao = new TagDAOImpl(session);
		List<Tag> entities = dao.findAll();
		int finds = 0;
		for (Tag entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				finds++;
			}
		}
		doTitle("tag",finds);
		for (Tag entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (finds == 0) {
					doNext();
				}
				doEntity("tag",entity.getName(), entity);
			}
		}
		return (finds);
	}

	private int findIdeas() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		IdeaDAOImpl dao = new IdeaDAOImpl(session);
		List<Idea> entities = dao.findAll();
		int finds = 0;
		for (Idea entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				finds++;
			}
		}
		doTitle("idea",finds);
		for (Idea entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (finds == 0) {
					doNext();
				}
				doEntity("idea",entity.getId() + " " + TextUtil.truncateString(entity.getNotes(), 30), entity);
			}
		}
		return (finds);
	}

	private int findMemos() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		MemoDAOImpl dao = new MemoDAOImpl(session);
		List<Memo> entities = dao.findAll();
		int finds = 0;
		for (Memo entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				finds++;
			}
		}
		doTitle("memo",finds);
		for (Memo entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (finds == 0) {
					doNext();
				}
				doEntity("memo",entity.getName(), entity);
			}
		}
		return (finds);
	}

	private boolean searchWords(String str) {
		String r = HtmlUtil.htmlToText(str);
		if (r.toLowerCase(Locale.getDefault()).contains(words.toLowerCase(Locale.getDefault()))) {
			return (true);
		}
		return (false);
	}

	private void showResults(JPanel res) {
		ReplaceResultsDlg dlg = new ReplaceResultsDlg(mainFrame, res, words, bywords);
		dlg.setVisible(true);
	}

	private void initResultat() {
		resultat.setLayout(new MigLayout("top,wrap", "[][]"));
		resultat.setBackground(Color.white);
		resultat.setMinimumSize(MINIMUM_SIZE);
		resultat.removeAll();
	}

	private void doTitle(String msg, int finds) {
		JLabel lb = new JLabel(I18N.getMsg(msg));
		lb.setFont(FontUtil.getBoldFont());
		resultat.add(lb);
		if (finds==0) doEmpty();
		else {
			JButton bt = new JButton(I18N.getMsg("replace.all")+" "+I18N.getMsg(msg));
			bt.setName("btReplaceAll_"+msg);
			bt.addActionListener((ActionEvent evt) -> {
				replaceAll(msg);
			});
			resultat.add(bt, "span");
		}
	}

	private void doEmpty() {
		JLabel r = new JLabel(I18N.getMsg("search.empty"));
		resultat.add(r, "wrap");
	}

	private void doEntity(String nature,String str, AbstractEntity entity) {
		resultat.add(new JLabel(" "/*I18N.getIcon("icon.small.search")*/), "right");
		JLabel r = new JLabel(str);
		JLabel doOk = new JLabel(I18N.getIcon("icon.small.ok"));
		doOk.setVisible(false);
		JButton bt = new JButton(I18N.getIcon("icon.small.rename"));
		bt.setName("btReplace_"+nature);
		bt.setToolTipText(I18N.getMsg("replace"));
		bt.addActionListener((ActionEvent evt) -> {
			replace(entity, bt, doOk);
		});
		bt.setMargin(new Insets(0, 0, 0, 0));
		resultat.add(r, "split 3, growx");
		resultat.add(doOk);
		resultat.add(bt,"wrap");
	}

	private void doNext() {
		JLabel r = new JLabel(" ");
		resultat.add(r, "wrap");
	}

	public static void show(MainFrame m) {
		SwingUtil.showModalDialog(new ReplaceDlg(m), m, true);
	}

	private void replace(AbstractEntity entity, JButton bt, JLabel doOk) {
		String nature=entity.getClass().getSimpleName().toLowerCase();
		switch(nature) {
			/* TODO add description field
			case "category": replaceCategory((Category)entity); break;
			*/
			case "strand": replaceStrand((Strand)entity); break;
			case "part": replacePart((Part)entity); break;
			case "chapter": replaceChapter((Chapter)entity); break;
			case "scene": replaceScene((Scene)entity); break;
			case "person": replacePerson((Person)entity); break;
			case "location": replaceLocation((Location)entity); break;
			case "item": replaceItem((Item)entity); break;
			case "tag": replaceTag((Tag)entity); break;
			case "idea": replaceIdea((Idea)entity); break;
			case "memo": replaceMemo((Memo)entity); break;
		}
		bt.setVisible(false);
		doOk.setVisible(true);
	}

	private void replaceAll(String name) {
		JButton bt = null;
		boolean all=(name.equals("all"));
		for (Component c:resultat.getComponents()) {
			if (c instanceof JButton) {
				if (all && (c.getName() !=null) && c.getName().contains("btReplaceAll_")) {
					bt=(JButton)c;
				}
				else if ((c.getName() !=null) && c.getName().contains("btReplaceAll_"+name)) {
					bt=(JButton)c;
				}
				boolean d=false;
				if (all && (c.getName() !=null) && c.getName().contains("btReplace_")) d=true;
				if ((c.getName() !=null) && c.getName().contains("btReplace_"+name)) d=true;
				if (d) {
					((JButton)c).doClick();
					if (bt!=null) bt.setVisible(false);
				}
			}
		}
	}

	/* TODO add description field
	private void replaceCategory(Category entity) {
		boolean b=false;
		if (entity.getDescription().contains(words)) {
			b=true;
			entity.setDescritpion(entity.getDescription().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updateEntity(entity);
	}

	*/
	private void replaceStrand(Strand entity) {
		boolean b=false;
		if (entity.getName().contains(words)) {
			b=true;
			entity.setName(entity.getName().replace(words, bywords));
		}
		if (entity.getNotes().contains(words)) {
			b=true;
			entity.setNotes(entity.getNotes().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updateStrand(entity);
	}

	private void replacePart(Part entity) {
		boolean b=false;
		if (entity.getName().contains(words)) {
			b=true;
			entity.setName(entity.getName().replace(words, bywords));
		}
		if (entity.getNotes().contains(words)) {
			b=true;
			entity.setNotes(entity.getNotes().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updatePart(entity);
	}

	private void replaceChapter(Chapter entity) {
		boolean b=false;
		if (entity.getTitle().contains(words)) {
			b=true;
			entity.setTitle(entity.getTitle().replace(words, bywords));
		}
		if (entity.getDescription().contains(words)) {
			b=true;
			entity.setDescription(entity.getDescription().replace(words, bywords));
		}
		if (entity.getNotes().contains(words)) {
			b=true;
			entity.setNotes(entity.getNotes().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updateChapter(entity);
	}

	private void replaceScene(Scene entity) {
		boolean b=false;
		if (entity.getTitle().contains(words)) {
			b=true;
			entity.setTitle(entity.getTitle().replace(words, bywords));
		}
		if (entity.getSummary().contains(words)) {
			b=true;
			entity.setSummary(entity.getSummary().replace(words, bywords));
		}
		if (entity.getNotes().contains(words)) {
			b=true;
			entity.setNotes(entity.getNotes().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updateScene(entity);
	}

	private void replacePerson(Person entity) {
		boolean b=false;
		if (entity.getFirstname().contains(words)) {
			b=true;
			entity.setFirstname(entity.getFirstname().replace(words, bywords));
		}
		if (entity.getLastname().contains(words)) {
			b=true;
			entity.setLastname(entity.getLastname().replace(words, bywords));
		}
		if (entity.getOccupation().contains(words)) {
			b=true;
			entity.setOccupation(entity.getOccupation().replace(words, bywords));
		}
		if (entity.getDescription().contains(words)) {
			b=true;
			entity.setDescription(entity.getDescription().replace(words, bywords));
		}
		if (entity.getNotes().contains(words)) {
			b=true;
			entity.setNotes(entity.getNotes().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updatePerson(entity);
	}

	private void replaceLocation(Location entity) {
		boolean b=false;
		if (entity.getName().contains(words)) {
			b=true;
			entity.setName(entity.getName().replace(words, bywords));
		}
		if (entity.getAddress().contains(words)) {
			b=true;
			entity.setAddress(entity.getAddress().replace(words, bywords));
		}
		if (entity.getCity().contains(words)) {
			b=true;
			entity.setCity(entity.getCity().replace(words, bywords));
		}
		if (entity.getCountry().contains(words)) {
			b=true;
			entity.setCountry(entity.getCountry().replace(words, bywords));
		}
		if (entity.getNotes().contains(words)) {
			b=true;
			entity.setNotes(entity.getNotes().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updateLocation(entity);
	}

	private void replaceItem(Item entity) {
		boolean b=false;
		if (entity.getName().contains(words)) {
			b=true;
			entity.setName(entity.getName().replace(words, bywords));
		}
		if (entity.getCategory().contains(words)) {
			b=true;
			entity.setCategory(entity.getCategory().replace(words, bywords));
		}
		if (entity.getDescription().contains(words)) {
			b=true;
			entity.setDescription(entity.getDescription().replace(words, bywords));
		}
		if (entity.getNotes().contains(words)) {
			b=true;
			entity.setNotes(entity.getNotes().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updateItem(entity);
	}

	private void replaceTag(Tag entity) {
		boolean b=false;
		if (entity.getName().contains(words)) {
			b=true;
			entity.setName(entity.getName().replace(words, bywords));
		}
		if (entity.getCategory().contains(words)) {
			b=true;
			entity.setCategory(entity.getCategory().replace(words, bywords));
		}
		if (entity.getDescription().contains(words)) {
			b=true;
			entity.setDescription(entity.getDescription().replace(words, bywords));
		}
		if (entity.getNotes().contains(words)) {
			b=true;
			entity.setNotes(entity.getNotes().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updateTag(entity);
	}

	private void replaceIdea(Idea entity) {
		boolean b=false;
		if (entity.getCategory().contains(words)) {
			b=true;
			entity.setCategory(entity.getCategory().replace(words, bywords));
		}
		if (entity.getNotes().contains(words)) {
			b=true;
			entity.setNotes(entity.getNotes().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updateIdea(entity);
	}

	private void replaceMemo(Memo entity) {
		boolean b=false;
		if (entity.getName().contains(words)) {
			b=true;
			entity.setName(entity.getName().replace(words, bywords));
		}
		if (entity.getCategory().contains(words)) {
			b=true;
			entity.setCategory(entity.getCategory().replace(words, bywords));
		}
		if (entity.getDescription().contains(words)) {
			b=true;
			entity.setDescription(entity.getDescription().replace(words, bywords));
		}
		if (entity.getNotes().contains(words)) {
			b=true;
			entity.setNotes(entity.getNotes().replace(words, bywords));
		}
		if (b) mainFrame.getBookController().updateMemo(entity);
	}
}
