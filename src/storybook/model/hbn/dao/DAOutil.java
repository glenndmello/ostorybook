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
package storybook.model.hbn.dao;

import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class DAOutil {
	
	public static AttributeDAOImpl getAttributeDAO(MainFrame m) {
		return(new AttributeDAOImpl(m.getSession()));
	}
	
	public static CategoryDAOImpl getCategoryDAO(MainFrame m) {
		return(new CategoryDAOImpl(m.getSession()));
	}
	
	public static GenderDAOImpl getGenderDAO(MainFrame m) {
		return(new GenderDAOImpl(m.getSession()));
	}
	
	public static StrandDAOImpl getStrandDAO(MainFrame m) {
		return(new StrandDAOImpl(m.getSession()));
	}
	
	public static PartDAOImpl getPartDAO(MainFrame m) {
		return(new PartDAOImpl(m.getSession()));
	}
	
	public static ChapterDAOImpl getChapterDAO(MainFrame m) {
		return(new ChapterDAOImpl(m.getSession()));
	}
	
	public static SceneDAOImpl getSceneDAO(MainFrame m) {
		return(new SceneDAOImpl(m.getSession()));
	}
	
	public static PersonDAOImpl getPersonDAO(MainFrame m) {
		return(new PersonDAOImpl(m.getSession()));
	}
	
	public static LocationDAOImpl getLocationDAO(MainFrame m) {
		return(new LocationDAOImpl(m.getSession()));
	}
	
	public static ItemDAOImpl getItemDAO(MainFrame m) {
		return(new ItemDAOImpl(m.getSession()));
	}
	
	public static ItemLinkDAOImpl getItemLinkDAO(MainFrame m) {
		return(new ItemLinkDAOImpl(m.getSession()));
	}
	
	public static TagDAOImpl getTagDAO(MainFrame m) {
		return(new TagDAOImpl(m.getSession()));
	}
	
	public static TagLinkDAOImpl getTagLinkDAO(MainFrame m) {
		return(new TagLinkDAOImpl(m.getSession()));
	}
	
	public static RelationshipDAOImpl getRelationshipDAO(MainFrame m) {
		return(new RelationshipDAOImpl(m.getSession()));
	}
	
	public static TimeEventDAOImpl getTimeEventDAO(MainFrame m) {
		return(new TimeEventDAOImpl(m.getSession()));
	}
	
	public static IdeaDAOImpl getIdeaDAO(MainFrame m) {
		return(new IdeaDAOImpl(m.getSession()));
	}
	
	public static MemoDAOImpl getMemoDAO(MainFrame m) {
		return(new MemoDAOImpl(m.getSession()));
	}
	
}
