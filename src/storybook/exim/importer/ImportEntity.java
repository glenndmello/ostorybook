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
package storybook.exim.importer;

import org.w3c.dom.Node;
import storybook.model.hbn.entity.AbstractEntity;

/**
 *
 * @author favdb
 */
public class ImportEntity {

	public final AbstractEntity entity;
	public final Node node;
	
	public ImportEntity(AbstractEntity e, Node n) {
		entity=e;
		node=n;
	}
	
}
