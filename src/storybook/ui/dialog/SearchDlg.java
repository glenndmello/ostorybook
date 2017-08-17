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
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
public class SearchDlg extends AbstractDialog {

	private String[] words;
	private JPanel resultat = new JPanel();
	private JTextField txWords;
	List<JCheckBox> cbList;
	String[] objects = {"strand", "part", "chapter", "scene", "person", "location", "item", "tag", "idea", "memo"};
	private JButton btAll;
	private JButton btFind;

	public SearchDlg(MainFrame m) {
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

		btFind = new JButton(I18N.getMsg("find"));
		btFind.setIcon(new ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/search.png")));
		btFind.setEnabled(false);
		btFind.addActionListener((ActionEvent evt) -> {
			searchEntities();
		});

		//layout
		setLayout(new MigLayout());
		setTitle(I18N.getMsg("search"));
		add(jLabel1, "wrap");
		add(txWords, "center,wrap");
		add(jPanel1, "span,wrap");
		add(getCancelButton(), "sg,span,split 2,right");
		add(btFind, "sg");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);
	}

	private void checkIfOk() {
		boolean b = (!txWords.getText().isEmpty());
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
		btFind.setEnabled(b);
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
		words = s.split(" ");
		for (String word : words) {
			if (word.length() < 4) {
				SwingUtil.showError("search.error.word");
				return;
			}
		}
		initResultat();
		boolean x = false;
		for (JCheckBox cb : cbList) {
			if (cb.isSelected()) {
				switch (cb.getName()) {
					case "strand":
						findStrands();
						break;
					case "part":
						findParts();
						break;
					case "chapter":
						findChapters();
						break;
					case "scene":
						findScenes();
						break;
					case "person":
						findPersons();
						break;
					case "location":
						findLocations();
						break;
					case "item":
						findItems();
						break;
					case "tag":
						findTags();
						break;
					case "idea":
						findIdeas();
						break;
					case "memo":
						findMemos();
						break;
				}
				x = true;
			}
		}
		if (!x) {
			SwingUtil.showError("search.error.object");
			return;
		}
		showResults(resultat);
	}

	private void findStrands() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		StrandDAOImpl dao = new StrandDAOImpl(session);
		List<Strand> entities = dao.findAll();
		boolean finds = false;
		doTitle(I18N.getMsg("strand"));
		for (Strand entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (!finds) {
					doNext();
				}
				doEntity(entity.getName(), entity);
				finds = true;
			}
		}
		if (!finds) {
			doEmpty();
		}
	}

	private void findParts() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Part> entities = dao.findAll();
		boolean finds = false;
		doTitle(I18N.getMsg("part"));
		for (Part entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (!finds) {
					doNext();
				}
				doEntity(entity.getName(), entity);
				finds = true;
			}
		}
		if (!finds) {
			doEmpty();
		}
	}

	private void findChapters() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Chapter> entities = dao.findAll();
		boolean finds = false;
		doTitle(I18N.getMsg("chapter"));
		for (Chapter entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (!finds) {
					doNext();
				}
				doEntity(entity.getChapternoStr() + " " + entity.getTitle(), entity);
				finds = true;
			}
		}
		if (!finds) {
			doEmpty();
		}
	}

	private void findScenes() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> entities = dao.findAll();
		boolean finds = false;
		doTitle(I18N.getMsg("scene"));
		for (Scene entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (!finds) {
					doNext();
				}
				doEntity(entity.getFullTitle(), entity);
				finds = true;
			}
		}
		if (!finds) {
			doEmpty();
		}
	}

	private void findPersons() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		List<Person> entities = dao.findAll();
		boolean finds = false;
		doTitle(I18N.getMsg("person"));
		for (Person entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (!finds) {
					doNext();
				}
				doEntity(entity.getFullNameAbbr(), entity);
				finds = true;
			}
		}
		if (!finds) {
			doEmpty();
		}
	}

	private void findLocations() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<Location> entities = dao.findAll();
		boolean finds = false;
		doTitle(I18N.getMsg("location"));
		for (Location entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (!finds) {
					doNext();
				}
				doEntity(entity.getName(), entity);
				finds = true;
			}
		}
		if (!finds) {
			doEmpty();
		}
	}

	private void findItems() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		List<Item> entities = dao.findAll();
		boolean finds = false;
		doTitle(I18N.getMsg("item"));
		for (Item entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (!finds) {
					doNext();
				}
				doEntity(entity.getName(), entity);
				finds = true;
			}
		}
		if (!finds) {
			doEmpty();
		}
	}

	private void findTags() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TagDAOImpl dao = new TagDAOImpl(session);
		List<Tag> entities = dao.findAll();
		boolean finds = false;
		doTitle(I18N.getMsg("tag"));
		for (Tag entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (!finds) {
					doNext();
				}
				doEntity(entity.getName(), entity);
				finds = true;
			}
		}
		if (!finds) {
			doEmpty();
		}
	}

	private void findIdeas() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		IdeaDAOImpl dao = new IdeaDAOImpl(session);
		List<Idea> entities = dao.findAll();
		boolean finds = false;
		doTitle(I18N.getMsg("idea"));
		for (Idea entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (!finds) {
					doNext();
				}
				doEntity(entity.getId() + " " + TextUtil.truncateString(entity.getNotes(), 30), entity);
				finds = true;
			}
		}
		if (!finds) {
			doEmpty();
		}
	}

	private void findMemos() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		MemoDAOImpl dao = new MemoDAOImpl(session);
		List<Memo> entities = dao.findAll();
		boolean finds = false;
		doTitle(I18N.getMsg("memo"));
		for (Memo entity : entities) {
			if (searchWords(entity.toCsv(" ", " ", "\t"))) {
				if (!finds) {
					doNext();
				}
				doEntity(entity.getName(), entity);
				finds = true;
			}
		}
		if (!finds) {
			doEmpty();
		}
	}

	private boolean searchWords(String str) {
		String r = HtmlUtil.htmlToText(str);
		for (String word : words) {
			if (r.toLowerCase(Locale.getDefault()).contains(word.toLowerCase(Locale.getDefault()))) {
				return (true);
			}
		}
		return (false);
	}

	private void showResults(JPanel res) {
		SearchResultsDlg dlg = new SearchResultsDlg(mainFrame, res);
		dlg.setVisible(true);
	}

	private void initResultat() {
		resultat.setLayout(new MigLayout("top,wrap", "[][]"));
		resultat.setBackground(Color.white);
		resultat.setMinimumSize(MINIMUM_SIZE);
		resultat.removeAll();
	}

	private void doTitle(String msg) {
		JLabel lb = new JLabel(msg);
		lb.setFont(FontUtil.getBoldFont());
		resultat.add(lb);
	}

	private void doEmpty() {
		JLabel r = new JLabel(I18N.getMsg("search.empty"));
		resultat.add(r, "wrap");
	}

	private void doEntity(String str, AbstractEntity entity) {
		resultat.add(new JLabel(I18N.getIcon("icon.small.next")), "right");
		JLabel r = new JLabel(str);
		r.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainFrame.showEditorAsDialog(entity);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				resultat.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				resultat.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		resultat.add(r, "wrap");
	}

	private void doNext() {
		JLabel r = new JLabel(" ");
		resultat.add(r, "wrap");
	}

	public static void show(MainFrame m) {
		SwingUtil.showModalDialog(new SearchDlg(m), m, true);
	}
}
