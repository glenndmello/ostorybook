/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package storybook.model.hbn.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Item;

public class ItemDAOImpl extends SbGenericDAOImpl<Item, Long> implements
		ItemDAO {

	public ItemDAOImpl() {
		super();
	}

	public ItemDAOImpl(Session session) {
		super(session);
	}

	public Item findTitle(String str) {
		String nstr=str.trim();
		List<Item> list = findAll();
		for (Item p:list) {
			if (p.getName().trim().equals(nstr)) return(p);
		}
		return(null);
	}

	@SuppressWarnings("unchecked")
	public List<AbstractEntity> findAllByName() {
		Criteria crit = session.createCriteria(Item.class);
		crit.addOrder(Order.asc("name"));
		List<AbstractEntity> entities = (List<AbstractEntity>) crit.list();
		return entities;
	}

	@SuppressWarnings("unchecked")
	public List<String> findAllInList() {
		Query query = session.createQuery("from Item order by name");
		List<String> list=new ArrayList<>();
		for (Item s : (List<Item>) query.list()) {
			list.add(s.getName());
		}
		return(list);
	}

	@SuppressWarnings("unchecked")
	public List<String> findCategories() {
		Query query = session
				.createQuery("select distinct(i.category) from Item as i order by i.category");
		return (List<String>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Item> findByCategory(String category) {
		Criteria crit = session.createCriteria(Item.class);
		crit.add(Restrictions.eq("category", category));
		crit.addOrder(Order.asc("name"));
		List<Item> items = (List<Item>) crit.list();
		return items;
	}
	
	@SuppressWarnings("unchecked")
	public List<AbstractEntity> findAllByCategory() {
		Criteria crit = session.createCriteria(Item.class);
		crit.addOrder(Order.asc("category"));
		crit.addOrder(Order.asc("name"));
		List<AbstractEntity> items = (List<AbstractEntity>) crit.list();
		return items;
	}

}
