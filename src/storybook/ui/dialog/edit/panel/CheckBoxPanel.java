package storybook.ui.dialog.edit.panel;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import storybook.model.BookModel;
import storybook.model.handler.AbstractEntityHandler;
import storybook.model.hbn.dao.SbGenericDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.ui.MainFrame;
import storybook.ui.interfaces.IRefreshable;
import storybook.ui.panel.AbstractPanel;

import com.googlecode.genericdao.search.Search;
import java.awt.event.ActionEvent;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Person;

@SuppressWarnings("serial")
public class CheckBoxPanel extends AbstractPanel implements IRefreshable {

//	private MainFrame mainFrame;
	public final Map<AbstractEntity, JCheckBox> cbMap;
	private CbPanelDecorator decorator;
	private AbstractEntity entity;
	public AbstractEntityHandler entityHandler;
	public List<AbstractEntity> entities;
	private Search search;
	public boolean autoSelect = true;
	private AbstractEntity pointedEntity;
	
	public CheckBoxPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		cbMap = new TreeMap<>();
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		cbMap.clear();
		List<AbstractEntity> allEntities;
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SbGenericDAOImpl<?, ?> dao = entityHandler.createDAO();
		dao.setSession(session);
		if (search != null) {
			if (search.toString().equals(Person.class.toString())) allEntities=loadPersons();
			else if (search.toString().equals(Item.class.toString())) allEntities=loadItems();
			else allEntities = dao.search(search);
		} else {
			allEntities = (List<AbstractEntity>) dao.findAll();
		}
		for (AbstractEntity entity2 : allEntities) {
			addEntity(session, entity2);
		}
		refresh();

		// refresh entities, must be before selectEntity()
		if (entities != null) {
			for (AbstractEntity ent : entities) {
				session.refresh(ent);
			}
			if (autoSelect) {
			    for (AbstractEntity ent : entities) {
				    selectEntity(session, ent);
			    }
			}
		}
		model.commit();
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("wrap"));
		setBackground(Color.white);
		refresh();
	}

	@Override
	public void refresh() {
		removeAll();

		if (decorator!=null) decorator.decorateBeforeFirstEntity();
		Iterator<Entry<AbstractEntity, JCheckBox>> it = cbMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<AbstractEntity, JCheckBox> pairs = (Map.Entry<AbstractEntity, JCheckBox>) it.next();
			AbstractEntity ent = pairs.getKey();
			if (decorator != null) {
				decorator.decorateBeforeEntity(ent);
				decorator.decorateEntity(pairs.getValue(), ent);
			} else {
				add(pairs.getValue(), "split 2");
				add(new JLabel(ent.getImageIcon()));
			}
			if (decorator != null) {
				decorator.decorateAfterEntity(ent);
			}
		}

		revalidate();
		repaint();
	}

	public void selectEntity(Session session, AbstractEntity ent) {
		JCheckBox cb = cbMap.get(ent);
		if (cb != null) {
			cb.setSelected(true);
		}
	}

	public List<AbstractEntity> getSelectedEntities() {
		ArrayList<AbstractEntity> ret = new ArrayList<>();
		Iterator<Entry<AbstractEntity, JCheckBox>> it = cbMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<AbstractEntity, JCheckBox> pairs = (Map.Entry<AbstractEntity, JCheckBox>) it.next();
			JCheckBox cb = pairs.getValue();
			if (cb.isSelected()) {
				ret.add(pairs.getKey());
			}
		}
		return ret;
	}

	public void addEntity(Session session, AbstractEntity entity) {
		session.refresh(entity);
		JCheckBox cb = new JCheckBox();
		cb.setOpaque(false);
		cb.addActionListener((ActionEvent evt) -> {
			pointedEntity=entity;
		});
		cbMap.put(entity, cb);
		cb.setText(entity.toString());
		add(cb);
		add(new JLabel(entity.getImageIcon()));
	}
	
	public AbstractEntity getPointedEntity() {
		return(pointedEntity);
	}

	public CbPanelDecorator getDecorator() {
		return decorator;
	}

	public void setDecorator(CbPanelDecorator decorator) {
		this.decorator = decorator;
	}

	@Override
	public MainFrame getMainFrame() {
		return mainFrame;
	}

	public AbstractEntity getEntity() {
		return entity;
	}

	public void setEntity(AbstractEntity entity) {
		this.entity = entity;
	}

	public AbstractEntityHandler getEntityHandler() {
		return entityHandler;
	}

	public void setEntityHandler(AbstractEntityHandler entityHandler) {
		this.entityHandler = entityHandler;
	}

	public void setEntityList(List<AbstractEntity> entities) {
		this.entities = entities;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public boolean getAutoSelect() {
		return autoSelect;
	}

	public void setAutoSelect(boolean flag) {
		this.autoSelect = flag;
	}

	private List<AbstractEntity> loadPersons() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		List<AbstractEntity> persons=dao.findAllByCategory();
		return((List<AbstractEntity>)persons);
	}

	private List<AbstractEntity> loadItems() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		List<AbstractEntity> items=dao.findAllByCategory();
		return(items);
	}

}
