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
package storybook.ui.panel.typist;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.miginfocom.swing.MigLayout;
import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.htmleditor.HtmlEditor;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;

/**
 *
 * @author favdb
 */
public class TypistEditNotes extends AbstractDialog {
	private HtmlEditor panel;

	public TypistEditNotes(MainFrame caller, HtmlEditor panel, String t) {
		super(caller);
		this.panel=panel;
		init(panel, t);
	}

	@Override
	public void init() {
	}

	public void init(HtmlEditor panel, String t) {
		this.setModal(true);
		this.setTitle(I18N.getMsg(t));
		this.setIconImage(I18N.getIconImage("icon.sb"));
		this.setLayout(new MigLayout("wrap,fill,ins 2"));
		this.add(panel, "wrap");
		this.addOkCancel();
		this.pack();
		this.setLocationRelativeTo(this.getParent().getParent());
		this.setVisible(true);
	}

	@Override
	public void initUi() {
		setModal(true);
		setTitle(I18N.getMsg("edit"));
		setIconImage(I18N.getIconImage("icon.sb"));
		setLayout(new MigLayout("wrap,fill,ins 2"));
		add(panel, "wrap");
		addOkCancel();
		pack();
		setLocationRelativeTo(this.getParent().getParent());
		setVisible(true);
		//SwingUtil.getDlgSize(this, new Idea());
	}

	@Override
	public void addOkCancel() {
		add(getOkButton(), "right,split 2");
		add(getCancelButton());
	}

	@Override
	protected JButton getOkButton() {
		AbstractAction act = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canceled = false;
				dispose();
			}
		};
		JButton bt = new JButton(act);
		bt.setText(I18N.getMsg("ok"));
		bt.setIcon(I18N.getIcon("icon.small.ok"));
		SwingUtil.addEnterAction(bt, act);
		return bt;
	}

	@Override
	protected JButton getCancelButton() {
		AbstractAction act = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canceled = true;
				dispose();
			}
		};
		JButton bt = new JButton(act);
		bt.setText(I18N.getMsg("cancel"));
		bt.setIcon(I18N.getIcon("icon.small.cancel"));
		SwingUtil.addEnterAction(bt, act);
		return bt;
	}

	@Override
	public boolean isCanceled() {
		return (canceled);
	}

}
