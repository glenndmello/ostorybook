/*
 * Copyright (C) 2015 favdb
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
package storybook.ui.panel.memo;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.infonode.docking.View;
import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;
import storybook.SbApp;
import storybook.SbConstants;
import storybook.action.DeleteEntityAction;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.dao.MemoDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Memo;
import storybook.toolkit.BookUtil;
import storybook.i18n.I18N;
import storybook.toolkit.net.NetUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.options.OptionsDlg;
import storybook.ui.panel.AbstractPanel;

/**
 *
 * @author favdb
 */
public class MemoPanel extends AbstractPanel implements ActionListener, ListSelectionListener, HyperlinkListener {
	private JComboBox memoCombo=null;//liste deroulante des memos
	private JList memoList=null;//liste des memos
	private JButton btNew;// bouton nouveau
	private JButton btDelete;// bouton supprimer
	private JButton btEdit;// bouton modifier
	private boolean processActionListener;// listener à ignorer
	private JPanel controlPanel;// le panneau de controle contient la liste deroulante et les boutons
	private JTextPane infoPanel;// le panneau d'information
	private Memo currentMemo;// le memo actuellement affiché
	private boolean disposition=false; // false(default)=left, true=top

	public MemoPanel(MainFrame mainFrame) {
		super(mainFrame);
	}
	@Override
	public void init() {
		SbApp.trace("MemoPanel.init()");
		/* disposition
		- liste deroulante memoCombo btEdit btDelete btNew
		- affichage du memo selectionne
		*/
		disposition=BookUtil.getBoolean(mainFrame, SbConstants.BookKey.MEMOS_VIEW);
		currentMemo=null;
	}

	@Override
	public void initUi() {
		SbApp.trace("MemoPanel.initUi()");
		MigLayout migLayout1 = new MigLayout("wrap,fill", "[20%][80%]", "[][grow]");
		setLayout(migLayout1);
		setBackground(SwingUtil.getBackgroundColor());
		controlPanel = new JPanel();
		MigLayout migLayout2 = new MigLayout("flowx", "", "");
		controlPanel.setLayout(migLayout2);
		controlPanel.setOpaque(false);
		memoList=new JList<>();
		memoCombo=new JComboBox();
		refreshListMemo();
		refreshControlPanel();
			
		add(controlPanel, "alignx center, span");
		if (!disposition) {
			JScrollPane listScroller = new JScrollPane(memoList);
			SwingUtil.setMaxPreferredSize(listScroller);
			add(listScroller);
		}
		
		infoPanel = new JTextPane();
		infoPanel.setEditable(false);
		infoPanel.setOpaque(true);
		infoPanel.setContentType("text/html");
		infoPanel.addHyperlinkListener(this);
		JScrollPane scroller = new JScrollPane(infoPanel);
		scroller.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		add(scroller,"span");
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		SbApp.trace("MemoPanel.modelPropertyChange(..)");
		Object newValue = evt.getNewValue();
		String propName = evt.getPropertyName();

		if (BookController.CommonProps.REFRESH.check(propName)) {
			View newView = (View) newValue;
			View view = (View) getParent().getParent();
			if (view == newView) {
				refresh();
			}
			return;
		}

		if (BookController.CommonProps.SHOW_MEMO.check(propName)) {
			if (newValue instanceof AbstractEntity) {
				currentMemo = (Memo) newValue;
				if (currentMemo.isTransient()) {
					return;
				}
				BookModel model = mainFrame.getBookModel();
				Session session = model.beginTransaction();
				session.refresh(currentMemo);
				model.commit();
				refreshMemo();
				return;
			}
		}

		if (BookController.CommonProps.SHOW_OPTIONS.check(propName)) {
			View view = (View) evt.getNewValue();
			if (!view.getName().equals(SbConstants.ViewName.MEMOS.toString())) {
				return;
			}
			int idx=(!disposition?memoList.getSelectedIndex():memoCombo.getSelectedIndex());
			OptionsDlg.show(mainFrame, view.getName());
			refresh();
			if (!disposition) memoList.setSelectedIndex(idx);
			else memoCombo.setSelectedIndex(idx);
			return;
		}

		if (BookController.MemoProps.LAYOUT_DIRECTION.check(propName)) {
			int idx=(memoList!=null?memoList.getSelectedIndex():memoCombo.getSelectedIndex());
			refresh();
			if (!disposition) memoList.setSelectedIndex(idx);
			else memoCombo.setSelectedIndex(idx);
			return;
		}

		if (currentMemo != null && newValue instanceof AbstractEntity) {
			AbstractEntity updatedEntity = (AbstractEntity) newValue;
			if (updatedEntity.getId().equals(currentMemo.getId())) {
				refreshMemo();
			}
		}
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		SbApp.trace("MemoPanel.hyperlinkIpdate(...)");
		try {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				NetUtil.openBrowser(e.getURL().toString());
			}
		} catch (Exception exc) {
			System.err.println("InfoPanel.hyperlinkUpdate("+e.toString()+") Exception : "+exc.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if ((evt.getSource() == null) || (!processActionListener)) {
			return;
		}
		SbApp.trace("MemoPanel.actionPerformed("+evt.toString()+")");
		if (evt.getSource() instanceof JButton) {
			String buttonString = ((JButton) evt.getSource()).getName();
			if (SbConstants.ComponentName.BT_EDIT.check(buttonString)) {
				mainFrame.showEditorAsDialog(currentMemo);
				refresh();
				return;
			} else if (SbConstants.ComponentName.BT_NEW.check(buttonString)) {
				mainFrame.showEditorAsDialog(new Memo());
				refresh();
				int i=memoList.getModel().getSize();
				if (!disposition) memoList.setSelectedIndex(i-1);
				else memoCombo.setSelectedIndex(i-1);
				return;
			} else if (SbConstants.ComponentName.BT_DELETE.check(buttonString)) {
				if (!disposition) currentMemo=(Memo)memoList.getSelectedValue();
				else currentMemo=(Memo)memoCombo.getSelectedItem();
				DeleteEntityAction act = new DeleteEntityAction(mainFrame, currentMemo);
				act.actionPerformed(null);
				if (act.confirm) {
					refresh();
					if (!disposition) memoList.setSelectedIndex(-1);
					else memoCombo.setSelectedIndex(-1);
				}
				return;
			}
		}
		if (!disposition) currentMemo=(Memo)memoList.getSelectedValue();
		else currentMemo=(Memo)memoCombo.getSelectedItem();
		refreshMemo();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		SbApp.trace("MemoPanel.valueChanged(...)");
		if (!disposition) currentMemo=(Memo)memoList.getSelectedValue();
		else currentMemo=(Memo)memoCombo.getSelectedItem();
		refreshMemo();
	}

	private void refreshMemo() {
		SbApp.trace("MemoPanel.refreshMemo("+(currentMemo!=null?currentMemo.toString():"null")+")");
		if (currentMemo==null) {
			infoPanel.setText("");
			btEdit.setEnabled(false);
			btDelete.setEnabled(false);
		} else {
			infoPanel.setText(currentMemo.getNotes());
			btEdit.setEnabled(true);
			btDelete.setEnabled(true);
		}
		infoPanel.setCaretPosition(0);
	}

	@SuppressWarnings({"unchecked", "unchecked", "unchecked", "unchecked"})
	private void refreshListMemo() {
		SbApp.trace("MemoPanel.refreshMemoCombo()");
		AbstractEntity entitySelected = null;
		if (!disposition) entitySelected = (AbstractEntity)memoList.getSelectedValue();
		else entitySelected = (AbstractEntity)memoCombo.getSelectedItem();
		long currentId=-1;
		if (entitySelected!=null) currentId=entitySelected.getId();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		MemoDAOImpl dao=new MemoDAOImpl(session);
		List<Memo> memos=dao.findAll();
		model.commit();
		processActionListener = false;
		if (!disposition) {
			DefaultListModel list = new DefaultListModel();
			list.removeAllElements();
			for (Memo memo : memos) {
				list.addElement(memo);
				if (memo.getId()==currentId) entitySelected=memo;
			}
			memoList.setModel(list);
			memoList.setSelectedValue(entitySelected, true);
			memoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			memoList.addListSelectionListener(this);
		} else {
			DefaultComboBoxModel combo = (DefaultComboBoxModel) memoCombo.getModel();
			combo.removeAllElements();
			for (Memo memo : memos) {
				combo.addElement(memo);
				if (memo.getId()==currentId) entitySelected=memo;
			}
			memoCombo.setModel(combo);
			memoCombo.setSelectedItem(entitySelected);
			memoCombo.addActionListener(this);
		}
		processActionListener = true;
	}
	
	private void refreshControlPanel() {
		SbApp.trace("MemoPanel.refreshControlPanel()");
		add(controlPanel, "alignx center");
		controlPanel.removeAll();
		if (disposition) {
			controlPanel.add(memoCombo, "gapafter 32");
		}

		btNew = new JButton(/*I18N.getMsg("new")*/);
		btNew.setIcon(I18N.getIcon("icon.small.new"));
		btNew.setName(SbConstants.ComponentName.BT_NEW.toString());
		btNew.setMargin(new Insets(0, 0, 0, 0));
		btNew.addActionListener(this);
		controlPanel.add(btNew);
		
		btEdit = new JButton(/*I18N.getMsg("edit")*/);
		btEdit.setIcon(I18N.getIcon("icon.small.edit"));
		btEdit.setName(SbConstants.ComponentName.BT_EDIT.toString());
		btEdit.setMargin(new Insets(0, 0, 0, 0));
		btEdit.setEnabled(false);
		btEdit.addActionListener(this);
		controlPanel.add(btEdit);

		btDelete = new JButton(/*I18N.getMsg("delete")*/);
		btDelete.setIcon(I18N.getIcon("icon.small.delete"));
		btDelete.setName(SbConstants.ComponentName.BT_DELETE.toString());
		btDelete.setMargin(new Insets(0, 0, 0, 0));
		btDelete.addActionListener(this);
		btDelete.setEnabled(false);
		controlPanel.add(btDelete);

		controlPanel.revalidate();
		controlPanel.repaint();
	}
	
}
