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
import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.dao.RelationshipDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.i18n.I18N;

/**
 *
 * @author favdb
 */
public class GraphStrand {
	
	private void initVertexStrand(MemoriaPanel m,AbstractEntity entity) {
		m.strandVertex = new Strand();
		m.strandVertex.setName(I18N.getMsg("msg.strands"));
		m.strandVertex.setName("");
		m.graph.addVertex(m.strandVertex);
		m.labelMap.put(m.strandVertex, m.strandVertex.getName());
		m.iconMap.put(m.strandVertex, m.emptyIcon);
		m.graph.addEdge(m.graphIndex++, entity, m.strandVertex);
	}

	
	@SuppressWarnings("unchecked")
	static void createStrandGraph(MemoriaPanel m) {
		BookModel model = m.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		StrandDAOImpl strandDAOImpl = new StrandDAOImpl(session);
		Strand localStrand = (Strand) strandDAOImpl.find(m.entityId);
		if (localStrand == null) {
			model.commit();
			return;
		}
		m.strandVertex=localStrand;
		m.relationshipDAO = new RelationshipDAOImpl(session);
		m.graphIndex = 0L;
		m.graph.addVertex(localStrand);
		m.labelMap.put(localStrand, localStrand.toString());
		m.iconMap.put(localStrand, I18N.getIcon("icon.large.strand"));
		//find all Persons appearing in strand
		//first find all Scenes for the strand
		SceneDAOImpl sceneDAO = new SceneDAOImpl(session);
		List<Scene> listScenes = sceneDAO.findByStrand(localStrand);
		//second for each scene find all Persons
		for (Scene scene:listScenes) {
			if (!scene.getPersons().isEmpty()) {
				for (Person person:scene.getPersons()) {
					GraphPerson.addForStrand(m,person);
				}
			}
		}
		model.commit();
	}

}
