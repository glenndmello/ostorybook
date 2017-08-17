/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun, 2015 FaVdB

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
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Person;

public class PersonDAOImpl extends SbGenericDAOImpl<Person, Long> implements PersonDAO {

	public PersonDAOImpl() {
		super();
	}

	public PersonDAOImpl(Session session) {
		super(session);
	}

	public Person findTitle(String str) {
		String nstr=str.trim();
		List<Person> list = findAll();
		for (Person p:list) {
			if (p.getFullName().trim().equals(nstr)) return(p);
		}
		return(null);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findAllInList() {
		Query query = session.createQuery("from Person order by Firstname, Lastname, Abbreviation");
		List<String> list=new ArrayList<>();
		for (Person s : (List<Person>)query.list()) {
			list.add(s.getFullNameAbbr());
		}
		return(list);
	}

	@SuppressWarnings("unchecked")
	public List<AbstractEntity> findAllByName() {
		Criteria crit = session.createCriteria(Person.class);
		crit.addOrder(Order.asc("lastname"));
		crit.addOrder(Order.asc("firstname"));
		List<AbstractEntity> persons = (List<AbstractEntity>) crit.list();
		return persons;
	}

	@SuppressWarnings("unchecked")
	public List<Person> findByCategory(Category category) {
		Criteria crit = session.createCriteria(Person.class);
		crit.add(Restrictions.eq("category", category));
		crit.addOrder(Order.asc("firstname"));
		crit.addOrder(Order.asc("lastname"));
		List<Person> persons = (List<Person>) crit.list();
		return persons;
	}

	@SuppressWarnings("unchecked")
	public List<AbstractEntity> findAllByCategory() {
		Criteria crit = session.createCriteria(Person.class);
		crit.addOrder(Order.asc("category"));
		crit.addOrder(Order.asc("firstname"));
		crit.addOrder(Order.asc("lastname"));
		List<AbstractEntity> persons = (List<AbstractEntity>) crit.list();
		return persons;
	}

	@SuppressWarnings("unchecked")
	public List<Person> findByCategories(List<Category> categories) {
		Criteria crit = session.createCriteria(Person.class);
		crit.add(Restrictions.in("category",categories));
		crit.addOrder(Order.asc("firstname"));
		crit.addOrder(Order.asc("lastname"));
		List<Person> persons = (List<Person>) crit.list();
		return persons;
	}
}
