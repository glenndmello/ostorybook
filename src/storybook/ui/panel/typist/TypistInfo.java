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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import org.miginfocom.swing.MigLayout;
import storybook.i18n.I18N;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.html.HtmlUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.htmleditor.HtmlEditor;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class TypistInfo extends JPanel {

	public Scene scene;
	public boolean modified;
	private JPanel pPersons;
	private JPanel pLocations;
	private JPanel pItems;
	private JEditorPane lbNotes;
	private final MainFrame mainFrame;
	private final TypistPanel parentPanel;

	public TypistInfo(TypistPanel m, Scene s) {
		parentPanel=m;
		mainFrame = m.getMainFrame();
		scene = s;
		init();
		if (scene != null) {
			refreshInfo(false);
		}
		modified = false;
	}

	public void init() {
		setLayout(new MigLayout("ins 2"));
		JButton bt = new JButton(I18N.getIcon("icon.small.idea"));
		bt.setMargin(new Insets(0, 0, 0, 0));
		bt.addActionListener((ActionEvent evt) -> {
			mainFrame.showEditorAsDialog(new Idea());
		});
		add(bt, "span,flowx,wrap");
		add(new JLabel(" "), "wrap");

		JButton btEdit = initButton("person");
		btEdit.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MIN_VALUE));
		add(btEdit, "growx,wrap");
		pPersons = new JPanel();
		pPersons.setLayout(new MigLayout("ins 0"));
		add(pPersons, " growx, wrap");

		btEdit = initButton("location");
		add(btEdit, "growx,wrap");
		pLocations = new JPanel();
		pLocations.setLayout(new MigLayout("ins 0,wrap"));
		add(pLocations, " growx, wrap");

		btEdit = initButton("item");
		add(btEdit, "growx,wrap");
		pItems = new JPanel();
		pItems.setLayout(new MigLayout("ins 0,wrap"));
		add(pItems, " growx, wrap");

		btEdit = initButton("notes");
		add(btEdit, "growx,wrap");
		lbNotes=new JEditorPane("text/html","");
		lbNotes.setEditable(false);
		lbNotes.setBackground(null);
		add(lbNotes, "growx, wrap");
	}

	private JButton initButton(String entity) {
		JButton bt = new JButton(I18N.getIcon("icon.small.edit"));
		bt.setText(I18N.getMsg(entity));
		bt.setToolTipText(I18N.getMsg(entity + ".edit"));
		bt.setMargin(new Insets(0, 0, 0, 0));
		bt.setHorizontalAlignment(SwingConstants.LEADING);
		bt.setHorizontalTextPosition(SwingConstants.LEADING);
		switch (entity) {
			case "person":
				bt.addActionListener((ActionEvent evt) -> {
					btPersonAction();
				});
				break;
			case "location":
				bt.addActionListener((ActionEvent evt) -> {
					btLocationAction();
				});
				break;
			case "item":
				bt.addActionListener((ActionEvent evt) -> {
					btItemAction();
				});
				break;
			case "notes":
				bt.addActionListener((ActionEvent evt) -> {
					btNotesAction();
				});
				break;
		}
		return (bt);
	}

	private void btPersonAction() {
		TypistListEdit dlg = new TypistListEdit(mainFrame, scene, "person");
		dlg.setModal(true);
		dlg.setVisible(true);
		if (!dlg.isCanceled()) {
			scene.setPersons(dlg.getPersons());
			refreshInfo(true);
			modified = true;
		}
	}

	private void btLocationAction() {
		TypistListEdit dlg = new TypistListEdit(mainFrame, scene, "location");
		dlg.setModal(true);
		dlg.setVisible(true);
		if (!dlg.isCanceled()) {
			scene.setLocations(dlg.getLocations());
			refreshInfo(true);
			modified = true;
		}
	}

	private void btItemAction() {
		TypistListEdit dlg = new TypistListEdit(mainFrame, scene, "item");
		dlg.setModal(true);
		dlg.setVisible(true);
		if (!dlg.isCanceled()) {
			scene.setItems(dlg.getItems());
			refreshInfo(true);
			modified = true;
		}
	}

	private void btNotesAction() {
		HtmlEditor panel = new HtmlEditor(mainFrame, true,false,false);
		panel.setText(scene.getNotes());
		TypistEditNotes dlg = new TypistEditNotes(mainFrame, panel, "edit");
		if (!dlg.isCanceled()) {
			scene.setNotes(panel.getText());
			refreshInfo(true);
		}
	}

	public void refreshInfo(boolean toUpdate) {
		pPersons.removeAll();
		if (scene.getPersons().size() > 0) {
			pPersons.setBorder(javax.swing.BorderFactory.createEtchedBorder());
			for (Person p : scene.getPersons()) {
				pPersons.add(new JLabel(p.getFullName()), "wrap");
			}
		} else {
			pPersons.setBorder(null);
		}

		pLocations.removeAll();
		if (scene.getLocations().size() > 0) {
			pLocations.setBorder(javax.swing.BorderFactory.createEtchedBorder());
			for (Location p : scene.getLocations()) {
				pLocations.add(new JLabel(p.getName()), "wrap");
			}
		} else pLocations.setBorder(null);

		pItems.removeAll();
		if (scene.getItems().size() > 0) {
			pItems.setBorder(javax.swing.BorderFactory.createEtchedBorder());
			for (Item p : scene.getItems()) {
				pItems.add(new JLabel(p.getName()), "wrap");
			}
		} else pItems.setBorder(null);

		String text=HtmlUtil.htmlToText(scene.getNotes());
		lbNotes.setText(scene.getNotes());
		if (!text.isEmpty()) {
			lbNotes.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		} else lbNotes.setBorder(null);
		this.revalidate();
		if (toUpdate) {
			parentPanel.setModified();
		}
	}

	void refreshInfo(Scene s) {
		scene = s;
		modified = false;
		refreshInfo(false);
	}

	public MainFrame getMainFrame() {
		return (((TypistPanel)getParent()).getMainFrame());
	}

}
