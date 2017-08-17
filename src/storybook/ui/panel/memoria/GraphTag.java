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
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.RelationshipDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;
import storybook.i18n.I18N;
import storybook.toolkit.Period;

/**
 *
 * @author favdb
 */
public class GraphTag {
	
	static void init(MemoriaPanel m,AbstractEntity entity) {
		m.tagVertex = new Tag();
		m.tagVertex.setName(I18N.getMsg("tags"));
		m.tagVertex.setName("");
		m.graph.addVertex(m.tagVertex);
		m.labelMap.put(m.tagVertex, m.tagVertex.getName());
		m.iconMap.put(m.tagVertex, m.emptyIcon);
		m.graph.addEdge(m.graphIndex++, entity, m.tagVertex);
	}

	@SuppressWarnings("unchecked")
	static void create(MemoriaPanel m) {
		BookModel model = m.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TagDAOImpl localTagDAOImpl = new TagDAOImpl(session);
		m.relationshipDAO = new RelationshipDAOImpl(session);
		Tag tag = (Tag) localTagDAOImpl.find(m.entityId);
		if (tag == null) {
			model.commit();
			return;
		}
		SceneDAOImpl localSceneDAOImpl = new SceneDAOImpl(session);
		TagLinkDAOImpl localTagLinkDAOImpl = new TagLinkDAOImpl(session);
		ItemLinkDAOImpl localItemLinkDAOImpl = new ItemLinkDAOImpl(session);
		m.graphIndex = 0L;
		m.graph.addVertex(tag);
		m.labelMap.put(tag, tag.toString());
		m.iconMap.put(tag, I18N.getIcon("icon.large.tag"));
		m.showTagVertex = false;
		m.initVertices(tag);
		List<TagLink> tagLinks = localTagLinkDAOImpl.findByTag(tag);
		for (TagLink tagLink : tagLinks) {
			Period period = tagLink.getPeriod();
			if ((m.chosenDate == null) || (period == null) || (period.isInside(m.chosenDate))) {
				if (tagLink.hasLocationOrPerson()) {
					if (tagLink.hasPerson()) {
						GraphPerson.add(m,tagLink.getPerson(), null);
						List<TagLink> TLbys = localTagLinkDAOImpl.findByPerson(tagLink.getPerson());
						if (!TLbys.isEmpty()) {
							for (TagLink TLby : TLbys) {
								if (!TLby.getTag().getId().equals(tag.getId())) {
									addInvolved(m,TLby.getTag());
								}
							}
						}
						List<ItemLink> ILbys = localItemLinkDAOImpl.findByPerson(tagLink.getPerson());
						if (!ILbys.isEmpty()) {
							for (ItemLink ILby : ILbys) {
								GraphItem.addInvolved(m,ILby.getItem());
							}
						}
					}
					if (tagLink.hasLocation()) {
						GraphLocation.add(m,tagLink.getLocation());
						List<TagLink> TLbys = localTagLinkDAOImpl.findByLocation(tagLink.getLocation());
						if (!TLbys.isEmpty()) {
							for (TagLink TLby : TLbys) {
								if (!TLby.getTag().getId().equals(tag.getId())) {
									addInvolved(m,TLby.getTag());
								}
							}
						}
						List<ItemLink> ILbys = localItemLinkDAOImpl.findByLocation(tagLink.getLocation());
						if (!ILbys.isEmpty()) {
							for (ItemLink ILby : ILbys) {
								GraphItem.addInvolved(m,ILby.getItem());
							}
						}
					}
				} else {
					Scene startScene = tagLink.getStartScene();
					if (startScene != null) {
						List<Scene> scenes = localSceneDAOImpl.findByDate(m.chosenDate);
						if (!scenes.isEmpty()) {
							for (Scene sc : scenes) {
								if (((tagLink.hasEndScene()) || (!sc.getId().equals(startScene.getId())))
										&& (sc.getStrand().getId().equals(startScene.getStrand().getId()))) {
									GraphScene.add(m,sc);
									List<TagLink> TLbys = localTagLinkDAOImpl.findByScene(sc);
									if (!TLbys.isEmpty()) {
										for (TagLink tl : TLbys) {
											if (!tag.getId().equals(tl.getId())) {
												addInvolved(m,tl.getTag());
											}
										}
									}
									List<ItemLink> ILbys = localItemLinkDAOImpl.findByScene(sc);
									if (!ILbys.isEmpty()) {
										for (ItemLink ILby : ILbys) {
											GraphItem.addInvolved(m,ILby.getItem());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		model.commit();
	}

	static void add(MemoriaPanel m,Tag tag) {
		if (tag != null) {
			m.graph.addVertex(tag);
			m.labelMap.put(tag, tag.toString());
			m.iconMap.put(tag, I18N.getIcon("icon.medium.tag"));
			m.graph.addEdge(m.graphIndex++, m.tagVertex, tag);
		}
	}

	static void addSet(MemoriaPanel m,Set<Tag> paramSet) {
		for (Tag tag : paramSet) {
			add(m,tag);
		}
	}

	static void initInvolded(MemoriaPanel m,AbstractEntity entity) {
		m.involvedTagVertex = new Tag();
		m.involvedTagVertex.setName(I18N.getMsg("graph.involved.tags"));
		m.involvedTagVertex.setName("");
		m.graph.addVertex(m.involvedTagVertex);
		m.labelMap.put(m.involvedTagVertex, m.involvedTagVertex.getName());
		m.iconMap.put(m.involvedTagVertex, m.emptyIcon);
		m.graph.addEdge(m.graphIndex++, entity, m.involvedTagVertex);
	}

	static void addInvolved(MemoriaPanel m,Tag tag) {
		if (tag != null) {
			if (!isInGraph(m,tag)) {
				m.graph.addVertex(tag);
				m.labelMap.put(tag, tag.toString());
				m.iconMap.put(tag, I18N.getIcon("icon.medium.tag"));
				m.graph.addEdge(m.graphIndex++, m.involvedTagVertex, tag);
			}
		}
	}

	static void addInvolved(MemoriaPanel m) {
		if (!m.involvedTags.isEmpty()) {
			for (Tag tag : m.involvedTags) {
				addInvolved(m,tag);
			}
		}
	}

	static void searchInvolved(MemoriaPanel m,List<TagLink> tagLinks) {
		if (!tagLinks.isEmpty()) {
			for (TagLink tagLink : tagLinks) {
				if (tagLink.hasOnlyScene()) {
					m.involvedTags.add(tagLink.getTag());
				}
			}
		}
	}

	static boolean isInGraph(MemoriaPanel m, Tag tag) {
		if (tag == null) {
			return false;
		}
		Collection collection = m.graph.getVertices();
		Iterator iterator = collection.iterator();
		while (iterator.hasNext()) {
			AbstractEntity entity = (AbstractEntity) iterator.next();
			if (((entity instanceof Tag)) && (entity.getId().equals(tag.getId()))) {
				return true;
			}
		}
		return false;
	}

}
