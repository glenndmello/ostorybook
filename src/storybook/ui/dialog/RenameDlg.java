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

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;
import storybook.controller.BookController;
import storybook.i18n.I18N;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Tag;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class RenameDlg extends AbstractDialog {

	private final String which;
	JComboBox<String> combo;
	JTextField tfNewName;

	public RenameDlg(MainFrame parent, String which) {
		super(parent);
		this.which=which;
		initAll();
	}

	@Override
	public void init() {
	}
	
	@Override
	public void initUi() {
		JLabel lbRename=new JLabel(I18N.getMsg("rename.rename"));
        combo = new JComboBox<>();
		JLabel lbTo = new JLabel(I18N.getMsg("rename.to"));
        tfNewName = new JTextField(20);
		switch(which) {
			case "country":
				this.setTitle(I18N.getMsg("location.rename.country"));
				createListCombo(countryGetList());
				break;
			case "city":
				this.setTitle(I18N.getMsg("location.rename.city"));
				createListCombo(cityGetList());
				break;
			case "item":
				this.setTitle(I18N.getMsg("item.rename.category"));
				createListCombo(ItemCategoryGetList());
				break;
			case "tag":
				this.setTitle(I18N.getMsg("tag.rename.category"));
				createListCombo(TagCategoryGetList());
				break;
		}
		
		//layout
		setLayout(new MigLayout("wrap 4", "[]", "[]20[]"));
		setTitle(I18N.getMsg("rename"));
		add(lbRename);add(combo);add(lbTo);add(tfNewName);
		add(getOkButton(), "sg,span,split 2,right");
		add(getCancelButton(), "sg");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);		
	}
	
	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String oldValue = (String) combo.getSelectedItem();
				String newValue = tfNewName.getText();
				if (!newValue.isEmpty()) {
					rename(oldValue, newValue);
					dispose();
				}
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	private void createListCombo(List<String> list) {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (String category : list) {
			model.addElement(category);
		}
		combo.setModel(model);
	}

	private void rename(String oldValue, String newValue) {
		switch(which) {
			case "country":
				countryRename(oldValue, newValue);
				break;
			case "city":
				cityRename(oldValue, newValue);
				break;
			case "item":
				ItemCategoryRename(oldValue, newValue);
				break;
			case "tag":
				TagCategoryRename(oldValue, newValue);
				break;
		}
	}
	
	protected List<String> countryGetList() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<String> ret = dao.findCountries();
		model.commit();
		return ret;
	}

	protected void countryRename(String oldValue, String newValue) {
		BookModel model = mainFrame.getBookModel();
		BookController ctrl = mainFrame.getBookController();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<Location> locations = dao.findByCountry(oldValue);
		model.commit();
		for (Location location : locations) {
			location.setCountry(newValue);
			ctrl.updateLocation(location);
		}
	}
	
	protected List<String> cityGetList() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<String> ret = dao.findCities();
		model.commit();
		return ret;
	}

	protected void cityRename(String oldValue, String newValue) {
		BookModel model = mainFrame.getBookModel();
		BookController ctrl = mainFrame.getBookController();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<Location> locations = dao.findByCity(oldValue);
		model.commit();
		for (Location location : locations) {
			location.setCity(newValue);
			ctrl.updateLocation(location);
		}
	}

	protected List<String> ItemCategoryGetList() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		List<String> ret = dao.findCategories();
		model.commit();
		return ret;
	}

	protected void ItemCategoryRename(String oldValue, String newValue) {
		BookModel model = mainFrame.getBookModel();
		BookController ctrl = mainFrame.getBookController();
		Session session = model.beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		List<Item> items = dao.findByCategory(oldValue);
		model.commit();
		for (Item item : items) {
			item.setCategory(newValue);
			ctrl.updateItem(item);
		}
	}

	protected List<String> TagCategoryGetList() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TagDAOImpl dao = new TagDAOImpl(session);
		List<String> ret = dao.findCategories();
		model.commit();
		return ret;
	}

	protected void TagCategoryRename(String oldValue, String newValue) {
		BookModel model = mainFrame.getBookModel();
		BookController ctrl = mainFrame.getBookController();
		Session session = model.beginTransaction();
		TagDAOImpl dao = new TagDAOImpl(session);
		List<Tag> tags = dao.findByCategory(oldValue);
		model.commit();
		for (Tag tag : tags) {
			tag.setCategory(newValue);
			ctrl.updateTag(tag);
		}
	}

}
