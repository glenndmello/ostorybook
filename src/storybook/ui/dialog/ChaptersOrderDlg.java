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
import java.awt.Insets;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;
import storybook.i18n.I18N;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class ChaptersOrderDlg extends AbstractDialog {

	private JList lstChapters;
	
	public ChaptersOrderDlg(MainFrame m) {
		super(m);
		init();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init() {
		JScrollPane scroller = new JScrollPane();
		scroller.setPreferredSize(new Dimension(310,320));
        lstChapters = new JList();
		lstChapters.setModel(new DefaultListModel());
		loadList(-1);
        scroller.setViewportView(lstChapters);
		JButton btUp = new JButton();
		btUp.setMargin(new Insets(0,0,0,0));
		btUp.setIcon(new ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/arrowup.png")));
        btUp.addActionListener((java.awt.event.ActionEvent evt) -> {
			moveList(-1);
		});

		JButton btDown = new JButton();
		btDown.setMargin(new Insets(0,0,0,0));
        btDown.setIcon(new ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/arrowdown.png")));
        btDown.addActionListener((java.awt.event.ActionEvent evt) -> {
			moveList(1);
		});

		JButton btExit = new JButton();
        btExit.setText(I18N.getMsg("exit"));
		btExit.setMargin(new Insets(0,0,0,0));
        btExit.addActionListener((java.awt.event.ActionEvent evt) -> {
			dispose();
		});
		
		//layout
		setLayout(new MigLayout("","",""));
		setTitle(I18N.getMsg("chapters.order"));
		setIconImage(I18N.getIconImage("icon.small.error"));
		add(scroller,"dock west");
		add(btUp,"wrap"); add(btDown,"bottom, wrap");
		add(btExit,"dock south,right");
		pack();
		setLocationRelativeTo(mainFrame);
	}

	@SuppressWarnings("unchecked")
	private void loadList(int first) {
		DefaultListModel listModel=(DefaultListModel)lstChapters.getModel();
		listModel.removeAllElements();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl ChapterDAO = new ChapterDAOImpl(session);
		List<Chapter> chapters = ChapterDAO.findAll();
		for (Chapter chapter : chapters) {
			listModel.addElement(chapter);
		}
		if (first!=-1) {
			lstChapters.setSelectedIndex(first);
		}
		model.commit();
	}

	private void moveList(int sens) {
		int index=lstChapters.getSelectedIndex();
		DefaultListModel listModel=(DefaultListModel)lstChapters.getModel();
		if ((sens==-1)&&(index==0)) {
			return;
		} 
		if ((sens==1) && (index==listModel.getSize()-1)) {
			System.out.println("fin");
		}
		Chapter oChapter=(Chapter)listModel.getElementAt(index+sens);
		Integer oChapterNo=oChapter.getChapterno();
		oChapter.setChapterno(99999);
		mainFrame.getBookController().updateChapter(oChapter);
		Chapter nChapter=(Chapter)listModel.getElementAt(index);
		Integer nChapterNo=nChapter.getChapterno();
		nChapter.setChapterno(oChapterNo);
		mainFrame.getBookController().updateChapter(nChapter);
		oChapter.setChapterno(nChapterNo);
		mainFrame.getBookController().updateChapter(oChapter);
		loadList(index+sens);
	}
	
}
