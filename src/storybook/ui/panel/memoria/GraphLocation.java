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

import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
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
import storybook.model.hbn.entity.TagLink;
import storybook.i18n.I18N;
import storybook.toolkit.Period;

/**
 *
 * @author favdb
 */
public class GraphLocation {
	
	static void init(MemoriaPanel m,AbstractEntity entity) {
		m.locationVertex = new Location();
		if (m.locationVertexTitle != null) {
			m.locationVertex.setName(m.locationVertexTitle);
		} else {
			m.locationVertex.setName(I18N.getMsg("locations"));
		}
		m.locationVertex.setName(" ");
		m.graph.addVertex(m.locationVertex);
		m.labelMap.put(m.locationVertex, m.locationVertex.toString());
		m.iconMap.put(m.locationVertex, m.emptyIcon);
		m.graph.addEdge(m.graphIndex++, entity, m.locationVertex);
	}
	
	static void create(MemoriaPanel m) {
		BookModel model = m.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl locationDao = new LocationDAOImpl(session);
		m.relationshipDAO = new RelationshipDAOImpl(session);
		Location location = (Location) locationDao.find(m.entityId);
		if (location == null) {
			model.commit();
			return;
		}
		SceneDAOImpl sceneDao = new SceneDAOImpl(session);
		List<Scene> scenes = sceneDao.findAll();
		if ((scenes == null) || (scenes.isEmpty())) {
			return;
		}
		TagLinkDAOImpl tagLinkDao = new TagLinkDAOImpl(session);
		ItemLinkDAOImpl itemLinkDao = new ItemLinkDAOImpl(session);
		m.graphIndex = 0L;
		m.graph.addVertex(location);
		m.labelMap.put(location, location.toString());
		m.iconMap.put(location, I18N.getIcon("icon.large.location"));
		m.locationVertexTitle = I18N.getMsg("graph.involved.locations");
		m.initVertices(location);
		List<TagLink> tagLinks = tagLinkDao.findByLocation(location);
		if (!tagLinks.isEmpty()) {
			for (TagLink tagLink : tagLinks) {
				Period period = tagLink.getPeriod();
				if ((period == null) || (m.chosenDate == null) || (period.isInside(m.chosenDate))) {
					GraphTag.add(m,tagLink.getTag());
				}
			}
		}
		List<ItemLink> itemLinks = itemLinkDao.findByLocation(location);
		if (!itemLinks.isEmpty()) {
			for (ItemLink itemLink : itemLinks) {
				Period period = itemLink.getPeriod();
				if ((period == null) || (m.chosenDate == null) || (period.isInside(m.chosenDate))) {
					GraphItem.add(m,itemLink.getItem());
				}
			}
		}
		for (Scene scene : scenes) {
			boolean c = false;
			if (m.chosenDate == null) {
				c = true;
			} else if ((!scene.hasNoSceneTs()) && (m.chosenDate.compareTo(scene.getDate()) == 0)) {
				c = true;
			}
			if (c && scene.getLocations().contains(location)) {
				List<Location> sceneLocations = scene.getLocations();
				for (Location sceneLocation : sceneLocations) {
					if (sceneLocation.equals(location)) {
						GraphScene.add(m,scene);
						m.sceneIds.add(scene.getId());
						GraphTag.searchInvolved(m,tagLinkDao.findByScene(scene));
						GraphItem.searchInvolved(m,itemLinkDao.findByScene(scene));
						List<Person> persons = scene.getPersons();
						if (!persons.isEmpty()) {
							for (Person person : persons) {
								GraphPerson.add(m,person, null);
								GraphTag.searchInvolved(m,tagLinkDao.findByPerson(person));
								GraphItem.searchInvolved(m,itemLinkDao.findByPerson((Person) person));
							}
						}
						List<Item> items = scene.getItems();
						if (!items.isEmpty()) {
							for (Item item : items) {
								GraphItem.addInvolved(m,item);
							}
						}
					}
				}
			}
		}
		model.commit();
	}

	static void add(MemoriaPanel m,Location location) {
		m.graph.addVertex(location);
		m.labelMap.put(location, location.toString());
		m.iconMap.put(location, I18N.getIcon("icon.medium.location"));
		m.graph.addEdge(m.graphIndex++, m.locationVertex, location);
		List<Relationship> relationships = m.relationshipDAO.findByLocationLink(location);
		GraphRelationship.addToVertexRelationships(m,relationships, null);
	}

	static void addSet(MemoriaPanel m, Set<Location> paramSet) {
		if (!paramSet.isEmpty()) {
			for (Location location : paramSet) {
				add(m,location);
			}
		}
	}

}
