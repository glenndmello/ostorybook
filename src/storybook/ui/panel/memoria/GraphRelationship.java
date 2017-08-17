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
import storybook.SbApp;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Relationship;
import storybook.i18n.I18N;
import storybook.toolkit.Period;

/**
 *
 * @author favdb
 */
public class GraphRelationship {
	
	static void init(MemoriaPanel m,AbstractEntity entity) {
		SbApp.trace("GraphRelationship.init(" + entity.toString() + ")");
		m.relationshipVertex = new Relationship();
		m.relationshipVertex.setDescription(I18N.getMsg("relationships"));
		m.graph.addVertex(m.relationshipVertex);
		m.labelMap.put(m.relationshipVertex, m.relationshipVertex.toString());
		m.iconMap.put(m.relationshipVertex, m.emptyIcon);
		m.graph.addEdge(m.graphIndex++, entity, m.relationshipVertex);
	}

	static void addToVertexRelationship(MemoriaPanel m,Relationship relationship) {
		boolean b=false;
		if (relationship.hasPeriod() && (m.chosenDate != null)) {
			Period period = relationship.getPeriod();
			if ((period != null) && (!period.isInside(m.chosenDate))) {
				b=true;
			}
		} else {
			b=true;
		}
		if (b) {
			m.graph.addVertex(relationship);
			m.labelMap.put(relationship, relationship.getDescription());
			m.iconMap.put(relationship, I18N.getIcon("icon.medium.relationship"));
			m.graph.addEdge(m.graphIndex++, m.relationshipVertex, relationship);
		}
	}

	static void addToVertexRelationships(MemoriaPanel m,List<Relationship> relationships, Person person) {
		if (!relationships.isEmpty()) {
			for (Relationship relationship : relationships) {
				if (relationship.hasPerson1()) {
					if (relationship.getPerson1().equals(person))
						addToVertexRelationship(m,relationship);
				}
				if (relationship.hasPerson2()) {
					if (relationship.getPerson2().equals(person))
						addToVertexRelationship(m,relationship);
				}
			}
		}
	}

}
