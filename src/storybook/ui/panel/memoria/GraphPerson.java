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
import storybook.model.hbn.dao.PersonDAOImpl;
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
public class GraphPerson {
	
	static void init(MemoriaPanel m,AbstractEntity entity) {
		m.personVertex = new Person();
		m.personVertex.setFirstname(I18N.getMsg("persons"));
		m.personVertex.setFirstname(I18N.getMsg(" "));
		m.graph.addVertex(m.personVertex);
		m.labelMap.put(m.personVertex, m.personVertex.getAbbreviation());
		m.iconMap.put(m.personVertex, m.emptyIcon);
		m.graph.addEdge(m.graphIndex++, entity, m.personVertex);
	}

	static void create(MemoriaPanel m) {
		BookModel model = m.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl personDAO = new PersonDAOImpl(session);
		m.relationshipDAO = new RelationshipDAOImpl(session);
		Person localPerson = (Person) personDAO.find(m.entityId);
		if (localPerson == null) {
			model.commit();
			return;
		}
		SceneDAOImpl sceneDAO = new SceneDAOImpl(session);
		TagLinkDAOImpl tagLinkDAO = new TagLinkDAOImpl(session);
		ItemLinkDAOImpl itemLinkDAO = new ItemLinkDAOImpl(session);
		m.graphIndex = 0L;
		m.graph.addVertex(localPerson);
		m.labelMap.put(localPerson, localPerson.toString());
		m.iconMap.put(localPerson, m.getIconPerson(localPerson, false));
		m.initVertices(localPerson);
		List<TagLink> tagLinks = tagLinkDAO.findByPerson(localPerson);
		if (!tagLinks.isEmpty()) {
			for (TagLink tagLink : tagLinks) {
				if (tagLink.hasPeriod() && (m.chosenDate != null)) {
					Period period = tagLink.getPeriod();
					if ((period != null) && (!period.isInside(m.chosenDate))) {
						GraphTag.add(m,tagLink.getTag());
					}
				} else {
					GraphTag.add(m,tagLink.getTag());
				}
			}
		}
		List<ItemLink> itemLinks = itemLinkDAO.findByPerson(localPerson);
		if (!itemLinks.isEmpty()) {
			for (ItemLink itemLink : itemLinks) {
				if (itemLink.hasPeriod() && (m.chosenDate != null)) {
					Period period = itemLink.getPeriod();
					if ((period != null) && (!period.isInside(m.chosenDate))) {
						GraphItem.add(m,itemLink.getItem());
					}
				} else {
					GraphItem.add(m,itemLink.getItem());
				}
			}
		}
		List<Scene> scenes = sceneDAO.findAll();
		if (!scenes.isEmpty()) {
			for (Scene scene : scenes) {
				boolean c = false;
				if (m.chosenDate == null) {
					c = true;
				} else if ((!scene.hasNoSceneTs()) && (m.chosenDate.compareTo(scene.getDate()) == 0)) {
					c = true;
				}
				if (c && (scene.getPersons().contains(localPerson))) {
					GraphScene.add(m,scene);
					m.sceneIds.add(scene.getId());
					List<TagLink> TLbys = tagLinkDAO.findByScene(scene);
					if (!TLbys.isEmpty()) {
						for (TagLink TLby : TLbys) {
							GraphTag.addInvolved(m,TLby.getTag());
						}
					}
					List<ItemLink> ILbys = itemLinkDAO.findByScene(scene);
					if (!ILbys.isEmpty()) {
						for (ItemLink ILby : ILbys) {
							GraphItem.addInvolved(m,ILby.getItem());
						}
					}
					List<Location> locations = scene.getLocations();
					if (!locations.isEmpty()) {
						for (Location location : locations) {
							GraphLocation.add(m,location);
							TLbys = tagLinkDAO.findByLocation((Location) location);
							if (!TLbys.isEmpty()) {
								for (TagLink TLby : TLbys) {
									GraphTag.addInvolved(m,TLby.getTag());
								}
							}
							ILbys = itemLinkDAO.findByLocation((Location) location);
							if (!ILbys.isEmpty()) {
								for (ItemLink ILby : ILbys) {
									GraphItem.addInvolved(m,ILby.getItem());
								}
							}
						}
					}
					List<Person> persons = scene.getPersons();
					if (!persons.isEmpty()) {
						for (Person person : persons) {
							if (!person.equals(localPerson)) {
								add(m,person,localPerson);
								TLbys = tagLinkDAO.findByPerson(person);
								if (!TLbys.isEmpty()) {
									for (TagLink TLby : TLbys) {
										GraphTag.addInvolved(m,TLby.getTag());
									}
								}
								ILbys = itemLinkDAO.findByPerson(person);
								if (!ILbys.isEmpty()) {
									for (ItemLink ILby : ILbys) {
										GraphItem.addInvolved(m,ILby.getItem());
									}
								}
							}
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
		model.commit();
	}

	static void add(MemoriaPanel m,Person person, Person personLink) {
		m.graph.addVertex(person);
		m.labelMap.put(person, person.getAbbr());
		m.iconMap.put(person, m.getIconPerson(person, true));
		m.graph.addEdge(m.graphIndex++, m.personVertex, person);
		List<Relationship> relationships;
		relationships = m.relationshipDAO.findByPersonLink(person);
		GraphRelationship.addToVertexRelationships(m,relationships, personLink);
		relationships = m.relationshipDAO.findByPerson(person);
		GraphRelationship.addToVertexRelationships(m,relationships, personLink);
	}

	static void addForStrand(MemoriaPanel m,Person person) {
		m.graph.addVertex(person);
		m.labelMap.put(person, person.getAbbreviation());
		m.iconMap.put(person, m.getIconPerson(person, true));
		m.graph.addEdge(m.graphIndex++, m.strandVertex, person);
	}

	void addSet(MemoriaPanel m,Set<Person> paramSet) {
		if (!paramSet.isEmpty()) {
			for (Person person : paramSet) {
				add(m,person, null);
			}
		}
	}

}
