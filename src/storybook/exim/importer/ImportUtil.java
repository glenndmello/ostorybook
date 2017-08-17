/*
 * Copyright (C) 2016 favdb
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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import storybook.SbApp;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.AttributeDAOImpl;
import storybook.model.hbn.dao.CategoryDAOImpl;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.DAOutil;
import storybook.model.hbn.dao.GenderDAOImpl;
import storybook.model.hbn.dao.IdeaDAOImpl;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.MemoDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.RelationshipDAOImpl;
import storybook.model.hbn.dao.SbGenericDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.dao.TimeEventDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import static storybook.model.hbn.entity.AbstractEntity.getXmlText;
import storybook.model.hbn.entity.Attribute;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Memo;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Relationship;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;
import storybook.model.hbn.entity.TimeEvent;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
class ImportUtil {

	static List<ImportEntity> list(Importer imp, String tobj) {
		SbApp.trace("ImportEntities.list("+imp.fileName+","+tobj+")");
		if (!imp.isOpened()) {
			System.err.println("error Importer is not opened");
			return(new ArrayList<>());
		}
		if (imp.isXml()) {
			return(listXml(imp, tobj));
		}
		return(listH2(imp, tobj));
	}

	static List<ImportEntity> listXml(Importer imp, String tobj) {
		SbApp.trace("ImportEntities.Xml("+imp.fileName+","+tobj+")");
		List<ImportEntity> entities=new ArrayList<>();
		NodeList nodes=imp.rootNode.getElementsByTagName(tobj);
		for(int i=0; i<nodes.getLength();i++) {
			Node n=nodes.item(i);
			if (!n.getParentNode().equals(imp.rootNode)) continue;
			switch(tobj) {
				case "strand":
					Strand strand=Strand.fromXml(n);
					if (strand.getId()>1) entities.add(new ImportEntity(strand,n));
					break;
				case "part":
					Part part=Part.fromXml(n);
					if (part.getId()>1) entities.add(new ImportEntity(part,n));
					break;
				case "chapter":
					entities.add(new ImportEntity(Chapter.fromXml(n),n));
					break;
				case "scene":
					entities.add(new ImportEntity(Scene.fromXml(n),n));
					break;
				case "person":
					entities.add(new ImportEntity(Person.fromXml(n),n));
					break;
				case "gender":
					Gender gender=Gender.fromXml(n);
					//import only other than male female
					if (gender.getId()>2) entities.add(new ImportEntity(gender,n));
					break;
				case "category":
					Category category=Category.fromXml(n);
					//import only other than central and secondary characters
					if (category.getId()>2) entities.add(new ImportEntity(category,n));
					break;
				case "location":
					entities.add(new ImportEntity(Location.fromXml(n),n));
					break;
				case "item":
					entities.add(new ImportEntity(Item.fromXml(n),n));
					break;
				case "itemlink":
					entities.add(new ImportEntity(ItemLink.fromXml(n),n));
					break;
				case "tag":
					entities.add(new ImportEntity(Tag.fromXml(n),n));
					break;
				case "taglink":
					entities.add(new ImportEntity(TagLink.fromXml(n),n));
					break;
				case "timeevent":
					entities.add(new ImportEntity(TimeEvent.fromXml(n),n));
					break;
			}
		}
		return(entities);
	}
	@SuppressWarnings("unchecked")
	static List<ImportEntity> listH2(Importer imp, String tobj) {
		Session session = imp.bookModel.beginTransaction();
		SbGenericDAOImpl<?, ?> dao=null;
		List<AbstractEntity> entities = new ArrayList<>();
		switch(tobj) {
			case "strand":
				dao = new StrandDAOImpl(session); break;
			case "part":
				dao = new PartDAOImpl(session); break;
			case "chapter":
				dao = new ChapterDAOImpl(session); break;
			case "scene":
				dao = new SceneDAOImpl(session); break;
			case "person":
				dao = new PersonDAOImpl(session); break;
			case "category":
				dao = new CategoryDAOImpl(session); break;
			case "gender":
				dao = new GenderDAOImpl(session); break;
			case "location":
				dao = new LocationDAOImpl(session); break;
			case "item":
				dao = new ItemDAOImpl(session); break;
			case "itemlink":
				dao = new ItemLinkDAOImpl(session); break;
			case "tag":
				dao = new TagDAOImpl(session); break;
			case "taglink":
				dao = new TagLinkDAOImpl(session); break;
			case "idea":
				dao = new IdeaDAOImpl(session); break;
			case "memo":
				dao = new MemoDAOImpl(session); break;
			case "relationship":
				dao = new RelationshipDAOImpl(session); break;
			case "timeevent":
				dao = new TimeEventDAOImpl(session); break;
		}
		List<ImportEntity> ientities=new ArrayList<>();
		if (dao!=null) {
			entities = (List<AbstractEntity>)dao.findAll();
			for (AbstractEntity entity:entities) {
				ientities.add(new ImportEntity(entity,null));
			}
			imp.bookModel.commit();
		}
		return(ientities);
	}

	@SuppressWarnings("null")
	static AbstractEntity updateEntity(MainFrame mainFrame, ImportEntity newEntity, AbstractEntity oldEntity) {
		SbApp.trace("ImportUtil.updateEntity("+newEntity.getClass().getSimpleName()+","+oldEntity.getClass().getSimpleName()+")");
		if (oldEntity!=null) newEntity.entity.setId(oldEntity.getId());
		else newEntity.entity.setId(-1L);
		switch(newEntity.entity.getClass().getSimpleName().toLowerCase()) {
			case "strand":
				return(updateStrand(mainFrame,newEntity,(Strand)oldEntity));
			case "part":
				return(updatePart(mainFrame,newEntity,(Part)oldEntity));
			case "chapter":
				return(updateChapter(mainFrame,newEntity,(Chapter)oldEntity));
			case "scene":
				return(updateScene(mainFrame,newEntity,(Scene)oldEntity));
			case "category":
				return(updateCategory(mainFrame,newEntity,(Category)oldEntity));
			case "gender":
				return(updateGender(mainFrame,newEntity,(Gender)oldEntity));
			case "person":
				return(updatePerson(mainFrame,newEntity,(Person)oldEntity));
			case "location":
				return(updateLocation(mainFrame,newEntity,(Location)oldEntity));
			case "item":
				return(updateItem(mainFrame,newEntity,(Item)oldEntity));
			case "tag":
				return(updateTag(mainFrame,newEntity,(Tag)oldEntity));
			case "itemlink":
				return(updateItemLink(mainFrame,newEntity,(ItemLink)oldEntity));
			case "taglink":
				return(updateTagLink(mainFrame,newEntity,(TagLink)oldEntity));
			case "idea":
				return(updateIdea(mainFrame,newEntity,(Idea)oldEntity));
			case "memo":
				return(updateMemo(mainFrame,newEntity,(Memo)oldEntity));
			case "relationship":
				return(updateRelationship(mainFrame,newEntity,(Relationship)oldEntity));
			case "timeevent":
				return(updateTimeEvent(mainFrame,newEntity,(TimeEvent)oldEntity));
			default:
				System.err.println("Unknown object type \""+newEntity.entity.getClass().getSimpleName()+"\" do nothing");
		}
		return(newEntity.entity);
	}

	private static AbstractEntity updateStrand(MainFrame mainFrame,ImportEntity newEntity, Strand oldEntity) {
		Strand entity=(Strand)newEntity.entity;
		// as is, no link to update
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updatePart(MainFrame mainFrame,ImportEntity newEntity, Part oldEntity) {
		Part entity=(Part)newEntity.entity;
		// creationTime, doneTime, objectiveTime, objectiveChars not modified
		if (oldEntity!=null) {
			entity.setCreationTime(oldEntity.getCreationTime());
			entity.setDoneTime(oldEntity.getDoneTime());
			entity.setObjectiveChars(oldEntity.getObjectiveChars());
			entity.setObjectiveTime(oldEntity.getObjectiveTime());
			if (oldEntity.hasSuperpart()) entity.setSuperpart(oldEntity.getSuperpart());
		}
		if (newEntity.node!=null) {
			String str=AbstractEntity.getXmlText(newEntity.node,"superpart");
			if (!str.isEmpty()) entity.setSuperpart(DAOutil.getPartDAO(mainFrame).findTitle(str));
		}
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateChapter(MainFrame mainFrame,ImportEntity newEntity, Chapter oldEntity) {
		Chapter entity=(Chapter)newEntity.entity;
		// creationTime, doneTime, objectiveTime, objectiveChars not modified
		if (oldEntity!=null) {
			entity.setCreationTime(oldEntity.getCreationTime());
			entity.setDoneTime(oldEntity.getDoneTime());
			entity.setObjectiveChars(oldEntity.getObjectiveChars());
			entity.setObjectiveTime(oldEntity.getObjectiveTime());
		}
		if (newEntity.node!=null) {
			String str=AbstractEntity.getXmlText(newEntity.node,"part");
			if (!str.isEmpty()) entity.setPart(DAOutil.getPartDAO(mainFrame).findTitle(str));
		}
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateScene(MainFrame mainFrame,ImportEntity newEntity, Scene oldEntity) {
		Scene entity=(Scene)newEntity.entity;
		if (oldEntity!=null) {
			entity.setStrand(oldEntity.getStrand());
			entity.setChapter(oldEntity.getChapter());
			entity.setPersons(oldEntity.getPersons());
			entity.setItems(oldEntity.getItems());
		}
		if (newEntity.node!=null) {
			String str=AbstractEntity.getXmlText(newEntity.node,"strand");
			if (!str.isEmpty()) entity.setStrand(DAOutil.getStrandDAO(mainFrame).findTitle(str));
			str=AbstractEntity.getXmlText(newEntity.node,"chapter");
			if (!str.isEmpty()) entity.setChapter(DAOutil.getChapterDAO(mainFrame).findTitle(str));
			List<String> list;
			list=AbstractEntity.getXmlList(newEntity.node,"strands");
			if (!list.isEmpty()) {
				List<Strand> lstEntity = new ArrayList<>();
				for (String xstr:list) {
					lstEntity.add(DAOutil.getStrandDAO(mainFrame).findTitle(xstr));
				}
				entity.setStrands(lstEntity);
			}
			list=AbstractEntity.getXmlList(newEntity.node,"persons");
			if (!list.isEmpty()) {
				List<Person> lstEntity = new ArrayList<>();
				for (String xstr:list) {
					lstEntity.add(DAOutil.getPersonDAO(mainFrame).findTitle(xstr));
				}
				entity.setPersons(lstEntity);
			}
			list=AbstractEntity.getXmlList(newEntity.node,"locations");
			if (!list.isEmpty()) {
				List<Location> lstEntity = new ArrayList<>();
				for (String xstr:list) {
					lstEntity.add(DAOutil.getLocationDAO(mainFrame).findTitle(xstr));
				}
				entity.setLocations(lstEntity);
			}
			list=AbstractEntity.getXmlList(newEntity.node,"items");
			if (!list.isEmpty()) {
				List<Item> lstEntity = new ArrayList<>();
				for (String xstr:list) {
					lstEntity.add(DAOutil.getItemDAO(mainFrame).findTitle(xstr));
				}
				entity.setItems(lstEntity);
			}
		}
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateCategory(MainFrame mainFrame,ImportEntity newEntity, Category oldEntity) {
		Category entity=(Category)newEntity.entity;
		if (oldEntity!=null) entity.setSup(oldEntity.getSup());
		if (newEntity.node!=null) {
			String str=AbstractEntity.getXmlText(newEntity.node,"sup");
			if (!str.isEmpty()) entity.setSup(DAOutil.getCategoryDAO(mainFrame).findTitle(str));
		}
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateGender(MainFrame mainFrame,ImportEntity newEntity, Gender oldEntity) {
		Gender entity=(Gender)newEntity.entity;
		// as is, no link to update
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updatePerson(MainFrame mainFrame,ImportEntity newEntity, Person oldEntity) {
		Person entity=(Person)newEntity.entity;
		// gender, category, attributes not modified
		if (oldEntity!=null) {
			entity.setGender(oldEntity.getGender());
			entity.setCategory(oldEntity.getCategory());
			entity.setAttributes(oldEntity.getAttributes());
		}
		if (newEntity.node!=null) {
			String str;
			str=AbstractEntity.getXmlText(newEntity.node,"gender");
			if (!str.isEmpty()) entity.setGender(DAOutil.getGenderDAO(mainFrame).findTitle(str));
			str=AbstractEntity.getXmlText(newEntity.node,"category");
			if (!str.isEmpty()) entity.setCategory(DAOutil.getCategoryDAO(mainFrame).findTitle(str));
			List<String> list;
			list=AbstractEntity.getXmlList(newEntity.node,"attribute");
			if (!list.isEmpty()) {
				List<Attribute> lstEntity = new ArrayList<>();
				for (String xstr:list) {
					String key, val;
					key=xstr.substring(1,xstr.indexOf("]"));
					val=xstr.substring(xstr.indexOf("]")+1);
					System.out.println("Attribute to update/create : key="+key+",val="+val);
					Attribute attribute= new Attribute(key,val);
					Attribute findEntity=EntityUtil.findAttribute(mainFrame,key,val);
					if (findEntity==null) {
						BookModel model = mainFrame.getBookModel();
						Session session = model.beginTransaction();
						session.save(attribute);
						model.commit();
						System.out.println("attribute created : key="+findEntity.getKey()+", val="+findEntity.getValue()+" (id="+findEntity.getId()+")");
						findEntity=EntityUtil.findAttribute(mainFrame,key,val);
						if (findEntity!=null) lstEntity.add(findEntity);
					}
					else {
						System.out.println("attribute ok : key="+findEntity.getKey()+", val="+findEntity.getValue()+" (id="+findEntity.getId()+")");
						lstEntity.add(findEntity);
					}
				}
				entity.setAttributes(lstEntity);
			}
		}
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateLocation(MainFrame mainFrame,ImportEntity newEntity, Location oldEntity) {
		Location entity=(Location)newEntity.entity;
		if (oldEntity!=null) {
			entity.setSite(oldEntity.getSite());
		}
		if (newEntity.node!=null) {
			String str;
			str=AbstractEntity.getXmlText(newEntity.node,"site");
			if (!str.isEmpty()) entity.setSite(DAOutil.getLocationDAO(mainFrame).findTitle(str));
		}
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateItem(MainFrame mainFrame,ImportEntity newEntity, Item oldEntity) {
		Item entity=(Item)newEntity.entity;
		if (oldEntity!=null) {
			entity.setType(oldEntity.getType());
		}
		if (newEntity.node!=null) {
			//update nothing
		}
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateTag(MainFrame mainFrame,ImportEntity newEntity, Tag oldEntity) {
		Tag entity=(Tag)newEntity.entity;
		if (oldEntity!=null) {
			entity.setType(oldEntity.getType());
		}
		if (newEntity.node!=null) {
			//update nothing
		}
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateIdea(MainFrame mainFrame,ImportEntity newEntity, Idea oldEntity) {
		Idea entity=(Idea)newEntity.entity;
		// nothing to change
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateMemo(MainFrame mainFrame,ImportEntity newEntity, Memo oldEntity) {
		Memo entity=(Memo)newEntity.entity;
		// nothing to change
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateTagLink(MainFrame mainFrame,ImportEntity newEntity, TagLink oldEntity) {
		TagLink entity=(TagLink)newEntity.entity;
		if (oldEntity!=null) {
		}
		if (newEntity.node!=null) {
			//update nothing
		}
		return((AbstractEntity)entity);
	}
	
	private static AbstractEntity updateItemLink(MainFrame mainFrame,ImportEntity newEntity, ItemLink oldEntity) {
		ItemLink entity=(ItemLink)newEntity.entity;
		if (oldEntity!=null) {
		}
		if (newEntity.node!=null) {
			//update nothing
		}
		return((AbstractEntity)entity);
	}

	private static AbstractEntity updateTimeEvent(MainFrame mainFrame,ImportEntity newEntity, TimeEvent oldEntity) {
		TimeEvent entity=(TimeEvent)newEntity.entity;
		// nothing to change
		return((AbstractEntity)entity);
	}
		
	private static AbstractEntity updateRelationship(MainFrame mainFrame,ImportEntity newEntity, Relationship oldEntity) {
		Relationship entity=(Relationship)newEntity.entity;
		if (oldEntity!=null) {
		}
		if (newEntity.node!=null) {
			String str;
			str=AbstractEntity.getXmlText(newEntity.node,"person1");
			if (!str.isEmpty()) entity.setPerson1(DAOutil.getPersonDAO(mainFrame).findTitle(str));
			str=AbstractEntity.getXmlText(newEntity.node,"person2");
			if (!str.isEmpty()) entity.setPerson1(DAOutil.getPersonDAO(mainFrame).findTitle(str));
			List<String> list;
			list=AbstractEntity.getXmlList(newEntity.node,"persons");
			if (!list.isEmpty()) {
				List<Person> lstEntity = new ArrayList<>();
				for (String xstr:list) {
					lstEntity.add(DAOutil.getPersonDAO(mainFrame).findTitle(xstr));
				}
				entity.setPersons(lstEntity);
			}
			list=AbstractEntity.getXmlList(newEntity.node,"locations");
			if (!list.isEmpty()) {
				List<Location> lstEntity = new ArrayList<>();
				for (String xstr:list) {
					lstEntity.add(DAOutil.getLocationDAO(mainFrame).findTitle(xstr));
				}
				entity.setLocations(lstEntity);
			}
			list=AbstractEntity.getXmlList(newEntity.node,"items");
			if (!list.isEmpty()) {
				List<Item> lstEntity = new ArrayList<>();
				for (String xstr:list) {
					lstEntity.add(DAOutil.getItemDAO(mainFrame).findTitle(xstr));
				}
				entity.setItems(lstEntity);
			}
		}
		return((AbstractEntity)entity);
	}
		
}
