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

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.RelationshipDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.TagLink;
import storybook.i18n.I18N;

/**
 *
 * @author favdb
 */
public class GraphScene {
	
	static void init(MemoriaPanel m,AbstractEntity entity) {
		m.sceneVertex = new Scene();
		if (m.sceneVertexTitle != null) {
			m.sceneVertex.setTitle(m.sceneVertexTitle);
		} else {
			m.sceneVertex.setTitle(I18N.getMsg("scenes"));
		}
		m.graph.addVertex(m.sceneVertex);
		m.labelMap.put(m.sceneVertex, m.sceneVertex.toString());
		m.iconMap.put(m.sceneVertex, m.emptyIcon);
		m.graph.addEdge(m.graphIndex++, entity, m.sceneVertex);
	}

	@SuppressWarnings("unchecked")
	static void create(MemoriaPanel m) {
		m.graphIndex = 0L;
		BookModel model = m.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl daoScene = new SceneDAOImpl(session);
		m.relationshipDAO = new RelationshipDAOImpl(session);
		Scene localScene = (Scene) daoScene.find(m.entityId);
		if (localScene == null) {
			model.commit();
			return;
		}
		m.graph.addVertex(localScene);
		m.labelMap.put(localScene, localScene.toString());
		m.iconMap.put(localScene, I18N.getIcon("icon.large.scene"));
		m.sceneVertexTitle = I18N.getMsg("graph.scenes.same.date");
		m.initVertices(localScene);
		TagLinkDAOImpl daoTagLink = new TagLinkDAOImpl(session);
		ItemLinkDAOImpl daoItemLink = new ItemLinkDAOImpl(session);
		if (!localScene.hasNoSceneTs()) {
			//scènes liés à la scène via la même date
			Date date = new Date(localScene.getSceneTs().getTime());
			long sceneId = localScene.getId();
			List<Scene> listScenes = daoScene.findByDate(date);
			if (!listScenes.isEmpty()) {
				for (Scene lScene : listScenes) {
					if (!lScene.getId().equals(sceneId)) {
						add(m,lScene);
						// tags et items impliqués via le lien de date entre scène
						GraphTag.searchInvolved(m,daoTagLink.findByScene(lScene));
						GraphItem.searchInvolved(m,daoItemLink.findByScene(lScene));
					}
				}
			}
		} else {
			// tags et items impliqués
			GraphTag.searchInvolved(m,daoTagLink.findByScene(localScene));
			GraphItem.searchInvolved(m,daoItemLink.findByScene(localScene));
		}
		// liste des personnages liés à la scène
		List<Person> persons = localScene.getPersons();
		for (Person person : persons) {
			GraphPerson.add(m,person, null);
		}
		// liste des lieux liés à la scène
		List<Location> locations = localScene.getLocations();
		for (Location location : locations) {
			GraphLocation.add(m,location);
		}
		// liste des items liés directement à la scène
		List<Item> items = localScene.getItems();
		for (Item item : items) {
			GraphItem.add(m,item);
		}
		// liste des tags si lien tagLink avec seulement la scene dans startScene
		List<TagLink> tagLinks = daoTagLink.findByScene(localScene);
		for (TagLink tagLink : tagLinks) {
			if (tagLink.hasOnlyScene()) {
				GraphTag.add(m,tagLink.getTag());
			}
		}
		// liste des items si lien itemLink avec seulement la scene dans startScene
		List<ItemLink> itemLinks = daoItemLink.findByScene(localScene);
		for (ItemLink itemLink : itemLinks) {
			if (itemLink.hasOnlyScene()) {
				GraphItem.add(m,itemLink.getItem());
			}
		}
		model.commit();
	}

	static void add(MemoriaPanel m,Scene scene) {
		m.graph.addVertex(scene);
		m.labelMap.put(scene, scene.toString());
		m.iconMap.put(scene, I18N.getIcon("icon.medium.scene"));
		m.graph.addEdge(m.graphIndex++, m.sceneVertex, scene);
	}

	void addSet(MemoriaPanel m,Set<Scene> paramSet) {
		if (!paramSet.isEmpty()) {
			for (Scene scene : paramSet) {
				m.add(m,scene);
			}
		}
	}

}
