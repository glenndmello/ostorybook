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
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;
import storybook.SbApp;
import storybook.i18n.I18N;
import storybook.model.BookModel;
import storybook.model.handler.AbstractEntityHandler;
import storybook.model.handler.ItemEntityHandler;
import storybook.model.handler.LocationEntityHandler;
import storybook.model.handler.PersonEntityHandler;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;
import storybook.ui.dialog.edit.panel.CheckBoxPanel;

/**
 *
 * @author favdb
 */
public class TypistListEdit extends AbstractDialog {

	private final Scene scene;
	private final String typeEntity;
	private CheckBoxPanel cbPanel;
	private List<Person> persons;
	private List<Location> locations;
	private List<Item> items;

	public TypistListEdit(MainFrame i, Scene s, String t) {
		super(i);
		scene = s;
		typeEntity = t;
		initAll();
	}

	@Override
	public void init() {
		this.setTitle(I18N.getMsg(typeEntity));
		this.setModal(true);
	}

	@Override
	public void initUi() {
		super.initUi();
		// layout
		setLayout(new MigLayout("wrap,fill", "[][][]"));
		setTitle(I18N.getMsg(typeEntity + "s"));
		setIconImage(I18N.getIconImage("icon.sb"));
		setPreferredSize(new Dimension(500, 600));

		JPanel p = createEntitiesPanel();

		add(p, "span");

		JButton btEdit = new JButton();
		btEdit.setText(I18N.getMsg("edit"));
		btEdit.setIcon(new ImageIcon(SbApp.class.getResource("/storybook/resources/icons/16x16/edit.png")));
		btEdit.setName("BtEdit");
		btEdit.addActionListener((ActionEvent evt) -> {
			AbstractEntity pointedEntity = cbPanel.getPointedEntity();
			if (pointedEntity != null) {
				mainFrame.showEditorAsDialog(pointedEntity);
			}
		});
		add(btEdit, "span 2,left");

		add(getOkButton(), "split 2, right");
		add(getCancelButton());
		pack();
		setLocationRelativeTo(parent);
		this.setModal(true);
	}

	List<Person> getPersons() {
		List<Person> sel = new ArrayList<>();
		List<AbstractEntity> entities = cbPanel.getSelectedEntities();
		for (AbstractEntity entity : entities) {
			sel.add((Person) entity);
		}
		return (sel);
	}

	List<Location> getLocations() {
		List<Location> sel = new ArrayList<>();
		List<AbstractEntity> entities = cbPanel.getSelectedEntities();
		for (AbstractEntity entity : entities) {
			sel.add((Location) entity);
		}
		return (sel);
	}

	List<Item> getItems() {
		List<Item> sel = new ArrayList<>();
		List<AbstractEntity> entities = cbPanel.getSelectedEntities();
		for (AbstractEntity entity : entities) {
			sel.add((Item) entity);
		}
		return (sel);
	}

	private List<Person> loadPersons() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		persons = dao.findAll();
		return (persons);
	}

	private List<Location> loadLocations() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		locations = dao.findAll();
		return (locations);
	}

	private List<Item> loadItems() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		items = dao.findAll();
		return (items);
	}

	private JPanel createEntitiesPanel() {
		MigLayout layout = new MigLayout();
		JPanel panel = new JPanel(layout);

		cbPanel = new CheckBoxPanel(mainFrame);
		JScrollPane scroller = new JScrollPane(cbPanel);
		SwingUtil.setUnitIncrement(scroller);
		SwingUtil.setMaxPreferredSize(scroller);
		panel.add(scroller, "grow");

		cbPanel.setAutoSelect(false);
		cbPanel.setEntityHandler(getEntityHandler());

		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		List<AbstractEntity> buf = new ArrayList<>();
		cbPanel.setEntityList((List<AbstractEntity>) buf);
		switch (typeEntity) {
			case "person":
				for (AbstractEntity entity : loadPersons()) {
					cbPanel.addEntity(session, entity);
				}
				break;
			case "location":
				for (AbstractEntity entity : loadLocations()) {
					cbPanel.addEntity(session, entity);
				}
				break;
			case "item":
				for (AbstractEntity entity : loadItems()) {
					cbPanel.addEntity(session, entity);
				}
				break;
		}
		cbPanel.initAll();
		switch (typeEntity) {
			case "person":
				for (AbstractEntity entity : scene.getPersons()) {
					cbPanel.selectEntity(session, entity);
				}
				break;
			case "location":
				for (AbstractEntity entity : scene.getLocations()) {
					cbPanel.selectEntity(session, entity);
				}
				break;
			case "item":
				for (AbstractEntity entity : scene.getItems()) {
					cbPanel.selectEntity(session, entity);
				}
				break;
		}

		return panel;
	}

	private AbstractEntityHandler getEntityHandler() {
		switch (typeEntity) {
			case "person":
				return new PersonEntityHandler(mainFrame);
			case "location":
				return new LocationEntityHandler(mainFrame);
			case "item":
				return new ItemEntityHandler(mainFrame);
		}
		return (null);
	}

}
