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
 * You should have received a copyCategory of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.ui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;
import storybook.SbApp;
import storybook.SbConstants;
import storybook.controller.BookController;
import storybook.i18n.I18N;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.handler.AbstractEntityHandler;
import storybook.model.handler.AttributeEntityHandler;
import storybook.model.handler.CategoryEntityHandler;
import storybook.model.hbn.dao.CategoryDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.SbGenericDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Attribute;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Strand;
import storybook.toolkit.BookUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.edit.panel.CbPanelDecorator;
import storybook.ui.dialog.edit.panel.CheckBoxPanel;
import storybook.ui.dialog.edit.panel.IdeaCbPanelDecorator;
import storybook.ui.dialog.edit.panel.ItemCbPanelDecorator;
import storybook.ui.dialog.edit.panel.LocationCbPanelDecorator;
import storybook.ui.dialog.edit.panel.PersonCbPanelDecorator;
import storybook.ui.dialog.edit.panel.StrandCbPanelDecorator;

/**
 *
 * @author favdb
 */
public class EntityCopyDlg extends AbstractDialog implements ActionListener, CaretListener {

	private AbstractEntity entity;
	private String entityType;
	private JComboBox<MainFrame> destCombo;
	private JComboBox<EntityName> cbEntity;
	private CheckBoxPanel cbPanel;
	private List<AbstractEntity> entities;
	private AbstractEntityHandler entityHandler;
	private final String[] entityAllowed = {"strand", "person", "location", "item", "idea"};

	public EntityCopyDlg(MainFrame m, AbstractEntity e) {
		super(m);
		entity = e;
		entityType = e.getClass().getSimpleName().toLowerCase();
		entityHandler = EntityUtil.getEntityHandler(mainFrame, entity);
		initAll();
	}

	public static void show(MainFrame m) {
		EntityCopyDlg dlg = new EntityCopyDlg(m, new Strand());
		dlg.setVisible(true);
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		super.initUi();
		setLayout(new MigLayout("wrap,fill", "", "[grow][]"));
		setTitle(I18N.getMsg("copy.title"));
		setIconImage(I18N.getIconImage("icon.sb"));
		setPreferredSize(new Dimension(500, 600));

		JPanel panel = new JPanel(new MigLayout("flowy,fill"));
		panel.add(createDestinationPanel(), "growx");
		panel.add(createEntitiesPanel(), "growx");

		add(panel);

		add(getOkButton(), "split 2,sg,right");
		add(getCancelButton(), "sg");
		pack();
		setLocationRelativeTo(mainFrame);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String compName = ((Component) e.getSource()).getName();
		if (compName.equals("cbEntity")) {
			String newType = ((EntityName) cbEntity.getSelectedItem()).getType();
			if (!newType.equals(entityType)) {
				entityType = newType;
				refreshType();
			}
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame destination = (MainFrame) destCombo.getSelectedItem();
				if (destination != null) {
					for (AbstractEntity srce : entities) {
						copyEntity(destination, srce);
					}
					canceled = false;
					dispose();
				}
			}

		};
	}

	private JPanel createDestinationPanel() {
		JPanel panel = new JPanel(new MigLayout("wrap 2", "", "[]10"));
		panel.setBorder(BorderFactory.createTitledBorder(I18N.getMsg("copy.destination")));
		JLabel lbopened = new JLabel(I18N.getColonMsg("copy.opened.projects"));
		DefaultComboBoxModel<MainFrame> model = new DefaultComboBoxModel<>();
		for (MainFrame frame : SbApp.getInstance().getMainFrames()) {
			if (frame != mainFrame) {
				model.addElement(frame);
			}
		}
		destCombo = new JComboBox<>(model);
		destCombo.setRenderer(new ProjectComboRenderer());
		JLabel lbOpenProject = new JLabel(" ");
		JButton openproject = new JButton();
		openproject.setAction(openProjectAction());
		openproject.setText(I18N.getMsg("copy.open.project") + "...");
		// layout
		panel.add(lbopened);
		panel.add(destCombo);
		panel.add(lbOpenProject);
		panel.add(openproject);
		return panel;
	}

	@SuppressWarnings("unchecked")
	private JPanel createEntitiesPanel() {
		JPanel panel = new JPanel(new MigLayout());
		panel.setBorder(BorderFactory.createTitledBorder(I18N.getMsg("copy.elements")));
		cbEntity = new JComboBox();
		cbEntity.setName("cbEntity");
		for (String e : entityAllowed) {
			cbEntity.addItem(new EntityName(e));
		}
		cbEntity.addActionListener(this);
		cbEntity.setSelectedItem(entityType);
		panel.add(new JLabel(I18N.getMsg("copy.elements")), "split 2");
		panel.add(cbEntity, "wrap");
		cbPanel = new CheckBoxPanel(mainFrame);
		JScrollPane scroller = new JScrollPane(cbPanel);
		SwingUtil.setUnitIncrement(scroller);
		SwingUtil.setMaxPreferredSize(scroller);
		panel.add(scroller, "grow");
		cbPanel.setAutoSelect(false);
		cbPanel.setEntityHandler(entityHandler);
		CbPanelDecorator decorator = getDecorator();
		if (decorator != null) {
			decorator.setPanel(cbPanel);
			cbPanel.setDecorator(decorator);
		}
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		cbPanel.setEntityList(getAllElements(session));
		for (AbstractEntity ent : getAllElements(session)) {
			cbPanel.addEntity(session, ent);
		}
		model.commit();
		cbPanel.initAll();
		return panel;
	}

	public AbstractAction openProjectAction() {
		AbstractAction projectAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				mainFrame.setWaitingCursor();
				SbApp.getInstance().openFile();
				SwingUtilities.invokeLater(() -> {
					DefaultComboBoxModel<MainFrame> model = (DefaultComboBoxModel<MainFrame>) destCombo.getModel();
					model.removeAllElements();
					for (MainFrame frame : SbApp.getInstance().getMainFrames()) {
						if (frame != mainFrame) {
							model.addElement(frame);
						}
					}
					mainFrame.setDefaultCursor();
				});
			}
		};
		return (projectAction);
	}

	private CbPanelDecorator getDecorator() {
		switch (entityType) {
			case "strand":
				return new StrandCbPanelDecorator();
			case "person":
				return new PersonCbPanelDecorator();
			case "location":
				return new LocationCbPanelDecorator();
			case "item":
				return new ItemCbPanelDecorator();
			case "idea":
				return new IdeaCbPanelDecorator();
		}
		return (null);
	}

	@SuppressWarnings("unchecked")
	private List<AbstractEntity> getAllElements(Session session) {
		SbGenericDAOImpl<?, ?> dao = entityHandler.createDAO();
		dao.setSession(session);
		List<AbstractEntity> ret = (List<AbstractEntity>) dao.findAll();
		return (ret);
	}

	private void copyEntity(MainFrame destination, AbstractEntity srce) {
		AbstractEntity dest = entityHandler.createNewEntity();
		BookController destCtrl = destination.getBookController();
		EntityUtil.copyEntityProperties(destination, srce, dest);
		prepareTransfer(destination, srce, dest);
		destCtrl.newEntity(dest);
		copySpecialInformation(mainFrame, destination, srce, dest);
	}

	private void prepareTransfer(MainFrame destination, AbstractEntity srce, AbstractEntity dest) {
		if (srce instanceof Location) {
			setLocation(destination, (Location) srce, (Location) dest);
		} else if (srce instanceof Person) {
			setCategory(destination, (Person) srce, (Person) dest);
			((Person) dest).setAttributes(new ArrayList<>());
		}
	}

	private void setLocation(MainFrame destination, Location srce, Location dest) {
		Location site = srce.getSite();
		if (site != null) {
			BookModel destinationModel = destination.getBookModel();
			Session destinationSession = destinationModel.beginTransaction();
			List<Location> sites = new LocationDAOImpl(destinationSession).findAll();
			destinationSession.close();

			boolean found = false;
			for (Location loc : sites) {
				if (loc.getName().equals(site.getName())) {
					found = true;
					dest.setSite(loc);
					break;
				}
			}

			if (!found) {
				Location destCat = copyLocation(destination, site);
				dest.setSite(destCat);
			}
		}
	}

	protected Location copyLocation(MainFrame destination, Location ent) {
		AbstractEntityHandler handler = new CategoryEntityHandler(destination);
		AbstractEntity newEnt = handler.createNewEntity();
		BookController destCtrl = destination.getBookController();
		EntityUtil.copyEntityProperties(destination, ent, newEnt);
		copyLocationPrepare(destination, ent, (Location) newEnt);
		destCtrl.newEntity(newEnt);
		return ((Location) newEnt);
	}

	protected void copyLocationPrepare(MainFrame destination, Location originElt, Location destElt) {
		setLocation(destination, originElt, destElt);
	}

	private void setCategory(MainFrame destination, Person srcePerson, Person destPerson) {
		BookModel destinationModel = destination.getBookModel();
		Session destinationSession = destinationModel.beginTransaction();
		Category categ = srcePerson.getCategory();
		List<Category> cats = new CategoryDAOImpl(destinationSession).findAll();
		destinationSession.close();

		boolean found = false;
		for (Category cat : cats) {
			if (cat.getName().equals(categ.getName())) {
				found = true;
				destPerson.setCategory(cat);
				break;
			}
		}

		if (!found) {
			Category destCat = new CopyCategory().copyCategory(destination, categ);
			destPerson.setCategory(destCat);
		}
	}

	protected void copyCategoryPrepare(MainFrame destination, Category srceCat, Category destCat) {
		Category sup = srceCat.getSup();
		if (sup != null) {
			BookModel destinationModel = destination.getBookModel();
			Session destinationSession = destinationModel.beginTransaction();
			List<Category> cats = new CategoryDAOImpl(destinationSession).findAll();
			boolean found = false;
			for (Category cat : cats) {
				if (cat.getName().equals(sup.getName())) {
					found = true;
					destCat.setSup(cat);
					break;
				}
			}
			if (!found) {
				Category destSup = copyCategory(destination, sup);
				destCat.setSup(destSup);
			}
		}
	}

	protected Category copyCategory(MainFrame destination, Category cat) {
		AbstractEntityHandler handler = new CategoryEntityHandler(destination);
		AbstractEntity newCat = handler.createNewEntity();
		BookController destCtrl = destination.getBookController();
		EntityUtil.copyEntityProperties(destination, cat, newCat);
		copyCategoryPrepare(destination, cat, (Category) newCat);
		destCtrl.newEntity(newCat);
		return ((Category) newCat);
	}

	private void copySpecialInformation(MainFrame origin, MainFrame destination, AbstractEntity srce, AbstractEntity dest) {
		if (srce instanceof Person) {
			AttributeEntityHandler handler = new AttributeEntityHandler(destination);
			List<Attribute> attributes = EntityUtil.getEntityAttributes(origin, srce);
			BookModel model = origin.getBookModel();
			List<Attribute> newAttributes = new ArrayList<>();
			for (Attribute attribute : attributes) {
				Session oriSession = model.beginTransaction();
				oriSession.refresh(attribute);
				String key = attribute.getKey();
				String value = attribute.getValue();

				Attribute attr = (Attribute) handler.createNewEntity();
				attr.setKey(key);
				attr.setValue(value);
				newAttributes.add(attr);
				model.commit();
			}
			EntityUtil.setEntityAttributes(destination, dest, newAttributes);
		}
	}

	private void refreshType() {
		cbPanel.removeAll();
		switch (entityType) {
			case "strand":
				entity = new Strand();
				break;
			case "person":
				entity = new Person();
				break;
			case "location":
				entity = new Location();
				break;
			case "item":
				entity = new Item();
				break;
			case "idea":
				entity = new Idea();
				break;
		}
		entityHandler = EntityUtil.getEntityHandler(mainFrame, entity);
		CbPanelDecorator decorator = getDecorator();
		if (decorator != null) {
			decorator.setPanel(cbPanel);
			cbPanel.setDecorator(decorator);
		}
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		cbPanel.setEntity(entity);
		cbPanel.setEntityHandler(entityHandler);
		cbPanel.setEntityList(getAllElements(session));
		model.commit();
		cbPanel.initAll();
	}

	private static class EntityName {

		String entityType;

		public EntityName(String t) {
			entityType = t;
		}

		public String getType() {
			return (entityType);
		}

		@Override
		public String toString() {
			return (I18N.getMsg(entityType));
		}
	}

	class ProjectComboRenderer extends JLabel implements ListCellRenderer<MainFrame> {

		public ProjectComboRenderer() {
			super("");
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends MainFrame> list,
			MainFrame value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
			if (value != null) {
				String title = BookUtil.get(value, SbConstants.BookKey.TITLE, "").getStringValue();
				if (title.isEmpty()) {
					title = value.getTitle();
				}
				setText(title);
			}
			return this;
		}
	}

	class CopyCategory {

		public CopyCategory() {
		}

		protected void copyCategoryPrepare(MainFrame destination, Category originElt, Category destElt) {
			Category sup = originElt.getSup();
			if (sup != null) {
				BookModel destinationModel = destination.getBookModel();
				Session destinationSession = destinationModel.beginTransaction();
				List<Category> cats = new CategoryDAOImpl(destinationSession).findAll();
				boolean found = false;
				for (Category cat : cats) {
					if (cat.getName().equals(sup.getName())) {
						found = true;
						destElt.setSup(cat);
						break;
					}
				}
				if (!found) {
					Category destSup = copyCategory(destination, sup);
					destElt.setSup(destSup);
				}
			}
		}

		protected Category copyCategory(MainFrame destination, Category elt) {
			AbstractEntityHandler entityHandler = new CategoryEntityHandler(destination);
			AbstractEntity newElt = entityHandler.createNewEntity();
			BookController destCtrl = destination.getBookController();
			EntityUtil.copyEntityProperties(destination, elt, newElt);
			copyCategoryPrepare(destination, elt, (Category) newElt);
			destCtrl.newEntity(newElt);
			return ((Category) newElt);
		}
	}
}
