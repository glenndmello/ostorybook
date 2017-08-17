/*
 * Copyright (C) 2016 favdb
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
package storybook.ui.panel.memoria;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.RelationshipDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Relationship;
import storybook.model.hbn.entity.Scene;
import storybook.i18n.I18N;
import storybook.toolkit.Period;

/**
 *
 * @author favdb
 */
public class GraphItem {
	
	public GraphItem(MemoriaPanel m) {
		create(m);
	}
	
	static void init(MemoriaPanel m,AbstractEntity entity) {
		m.itemVertex = new Item();
		m.itemVertex.setName(I18N.getMsg("items"));
		m.itemVertex.setName("");
		m.graph.addVertex(m.itemVertex);
		m.labelMap.put(m.itemVertex, m.itemVertex.getName());
		m.iconMap.put(m.itemVertex, m.emptyIcon);
		m.graph.addEdge(m.graphIndex++, entity, m.itemVertex);
	}

	public static void create(MemoriaPanel m) {
		BookModel model = m.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ItemDAOImpl itemDAOImpl = new ItemDAOImpl(session);
		Item localItem = (Item) itemDAOImpl.find(m.entityId);
		if (localItem == null) {
			model.commit();
			return;
		}
		m.relationshipDAO = new RelationshipDAOImpl(session);
		SceneDAOImpl sceneDAO = new SceneDAOImpl(session);
		TagLinkDAOImpl tagLinkDAO = new TagLinkDAOImpl(session);
		ItemLinkDAOImpl itemLinkDAO = new ItemLinkDAOImpl(session);
		m.graphIndex = 0L;
		m.graph.addVertex(localItem);
		m.labelMap.put(localItem, localItem.toString());
		m.iconMap.put(localItem, I18N.getIcon("icon.large.item"));
		m.showTagVertex = false;
		m.initVertices(localItem);
		List<ItemLink> itemLinks = itemLinkDAO.findByItem(localItem);
		for (ItemLink itemLink : itemLinks) {
			Period period = itemLink.getPeriod();
			if ((m.chosenDate == null) || (period == null) || (period.isInside(m.chosenDate))) {
				if (itemLink.hasPerson()) {
					GraphPerson.add(m,itemLink.getPerson(), null);
				}
				if (itemLink.hasLocation()) {
					GraphLocation.add(m,itemLink.getLocation());
				}
				List<Scene> listScenes = sceneDAO.findAll();
				for (Scene scene : listScenes) {
					if ((period != null) && ((!scene.hasNoSceneTs()) && (period.isInside(scene.getDate())))) {
						GraphScene.add(m,scene);
						List<Location> locations = scene.getLocations();
						if (!locations.isEmpty()) {
							for (Location loc : locations) {
								GraphLocation.add(m,loc);
							}
						}
						List<Person> persons = scene.getPersons();
						if (!persons.isEmpty()) {
							for (Person person : persons) {
								GraphPerson.add(m,person, null);
							}
						}
						List<Item> items = scene.getItems();
						if (!items.isEmpty()) {
							for (Item item : items) {
								addInvolved(m,item);
							}
						}
						GraphTag.searchInvolved(m,tagLinkDAO.findByScene(scene));
						searchInvolved(m,itemLinkDAO.findByScene(scene));
					}
				}
			}
		}
		List<Scene> scenes = sceneDAO.findAll();
		for (Scene scene : scenes) {
			if (scene.getItems().contains(localItem)) {
				GraphScene.add(m,scene);
				List<Location> locations = scene.getLocations();
				if (!locations.isEmpty()) {
					for (Location loc : locations) {
						GraphLocation.add(m,loc);
					}
				}
				List<Person> persons = scene.getPersons();
				if (!persons.isEmpty()) {
					for (Person person : persons) {
						GraphPerson.add(m,person,null);
					}
				}
				List<Item> items = scene.getItems();
				if (!items.isEmpty()) {
					for (Item item : items) {
						addInvolved(m,item);
					}
				}
				GraphTag.searchInvolved(m,tagLinkDAO.findByScene(scene));
				searchInvolved(m,itemLinkDAO.findByScene(scene));
			}
		}
		model.commit();
	}
	
	static void add(MemoriaPanel m, Item item) {
		if (item != null) {
			m.graph.addVertex(item);
			m.labelMap.put(item, item.toString());
			m.iconMap.put(item, I18N.getIcon("icon.medium.item"));
			m.graph.addEdge(m.graphIndex++, m.itemVertex, item);
			List<Relationship> relationships = m.relationshipDAO.findByItemLink(item);
			GraphRelationship.addToVertexRelationships(m,relationships, null);
		}
	}

	static void addSet(MemoriaPanel m,Set<Item> paramSet) {
		if (!paramSet.isEmpty()) {
			for (Item item : paramSet) {
				add(m,item);
			}
		}
	}

	static void initInvolded(MemoriaPanel m,AbstractEntity entity) {
		m.involvedItemVertex = new Item();
		m.involvedItemVertex.setName(I18N.getMsg("graph.involved.items"));
		m.involvedItemVertex.setName(" ");
		m.graph.addVertex(m.involvedItemVertex);
		m.labelMap.put(m.involvedItemVertex, m.involvedItemVertex.getName());
		m.iconMap.put(m.involvedItemVertex, m.emptyIcon);
		m.graph.addEdge(m.graphIndex++, entity, m.involvedItemVertex);
	}

	static void addInvolved(MemoriaPanel m,Item item) {
		if (item != null) {
			if (!isInGraph(m,item)) {
				m.graph.addVertex(item);
				m.labelMap.put(item, item.toString());
				m.iconMap.put(item, I18N.getIcon("icon.medium.item"));
				m.graph.addEdge(m.graphIndex++, m.involvedItemVertex, item);
			}
		}
	}

	static void addSet(MemoriaPanel m) {
		if (!m.involvedItems.isEmpty()) {
			for (Item item : m.involvedItems) {
				addInvolved(m,item);
			}
		}
	}

	static void searchInvolved(MemoriaPanel m,List<ItemLink> itemLinks) {
		if (!itemLinks.isEmpty()) {
			for (ItemLink itemLink : itemLinks) {
				if (itemLink.hasOnlyScene()) {
					m.involvedItems.add(itemLink.getItem());
				}
			}
		}
	}

	static boolean isInGraph(MemoriaPanel m,Item item) {
		if (item == null) {
			return false;
		}
		Collection collection = m.graph.getVertices();
		Iterator iterator = collection.iterator();
		while (iterator.hasNext()) {
			AbstractEntity entity = (AbstractEntity) iterator.next();
			if (((entity instanceof Item)) && (entity.getId().equals(item.getId()))) {
				return true;
			}
		}
		return false;
	}

}
