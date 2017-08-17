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

import java.awt.Dimension;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;
import storybook.i18n.I18N;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.edit.EntityEditor;

/**
 *
 * @author favdb
 */
public class ChapterSelectDlg extends JDialog {

	private JList lstChapters;
	private final Chapter chapter;
	private final MainFrame mainFrame;
	private boolean canceled=false;
	
	public ChapterSelectDlg(JFrame p,MainFrame m, Chapter c) {
		super(p);
		mainFrame=m;
		chapter=c;
		init();
	}

	public ChapterSelectDlg(MainFrame m, Chapter c) {
		super(m);
		mainFrame=m;
		chapter=c;
		init();
	}

	@SuppressWarnings("unchecked")
	public void init() {
		this.setModal(true);
		JScrollPane scroller = new JScrollPane();
		scroller.setPreferredSize(new Dimension(310,320));
        lstChapters = new JList();
		lstChapters.setModel(new DefaultListModel());
		loadList(-1);
		lstChapters.setSelectedValue(chapter, true);
        scroller.setViewportView(lstChapters);
		JButton btNew = SwingUtil.createButton("", "16x16/chapter_new","chapter.new");
        btNew.addActionListener((java.awt.event.ActionEvent evt) -> {
			newChapter();
		});

		JButton btOK = SwingUtil.createButton("ok", "16x16/ok","ok");
        btOK.addActionListener((java.awt.event.ActionEvent evt) -> {
			dispose();
		});
		
		JButton btCancel = SwingUtil.createButton("cancel", "16x16/cancel","cancel");
        btCancel.addActionListener((java.awt.event.ActionEvent evt) -> {
			canceled=true;
			dispose();
		});
		
		//layout
		setLayout(new MigLayout());
		setTitle(I18N.getMsg("chapter"));
		add(scroller,"split 2");
		add(btNew,"top,wrap");
		add(btCancel,"split 2,right");
		add(btOK);
		pack();
		setLocationRelativeTo(getParent());
	}

	@SuppressWarnings("unchecked")
	private void loadList(int first) {
		DefaultListModel listModel=(DefaultListModel)lstChapters.getModel();
		listModel.removeAllElements();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl ChapterDAO = new ChapterDAOImpl(session);
		List<Chapter> chapters = ChapterDAO.findAll();
		for (Chapter c : chapters) {
			listModel.addElement(c);
		}
		if (first!=-1) {
			lstChapters.setSelectedIndex(first);
		}
		model.commit();
	}

	private void newChapter() {
		JDialog dlg = new JDialog(this, true);
		EntityEditor editor = new EntityEditor(mainFrame, new Chapter(), dlg);
		dlg.setTitle(I18N.getMsg("editor"));
		Dimension pos = SwingUtil.getDlgPosition(dlg, new Chapter());
		if (pos != null) {
			dlg.setLocation(pos.height, pos.width);
		} else {
			dlg.setSize(this.getWidth() / 2, 680);
		}
		dlg.add(editor);
		Dimension size = SwingUtil.getDlgSize(dlg, new Chapter());
		if (size != null) {
			dlg.setSize(size.height, size.width);
		} else {
			dlg.setLocationRelativeTo(this);
		}
		dlg.setVisible(true);
		SwingUtil.saveDlgPosition(dlg, new Chapter());
		loadList(-1);
		lstChapters.setSelectedValue(chapter, true);
	}

	public Chapter getSelectedChapter() {
		System.out.println("initialChapter="+chapter.toString());
		System.out.println("selectedChapterIndex="+lstChapters.getSelectedIndex());
		System.out.println("selectedChapter="+((Chapter)lstChapters.getSelectedValue()).toString());
		return((Chapter)lstChapters.getSelectedValue());
	}

	public boolean isCanceled() {
		return(canceled);
	}
	
}
