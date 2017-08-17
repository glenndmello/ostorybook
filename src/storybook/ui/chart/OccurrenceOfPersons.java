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
package storybook.ui.chart;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import storybook.SbApp;
import storybook.model.BookModel;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Person;
import storybook.ui.MainFrame;
import storybook.ui.chart.timeline.Dataset;
import storybook.ui.chart.timeline.DatasetItem;
import storybook.ui.chart.timeline.TimelinePanel;

/**
 *
 * @author favdb
 */
public class OccurrenceOfPersons extends AbstractPersonsChart {

	private TimelinePanel timelinePanel;
	private Dataset dataset;

	public OccurrenceOfPersons(MainFrame paramMainFrame) {
		super(paramMainFrame, "report.person.occurrence.title");
	}

	@Override
	protected void initChartUi() {
		createOccurenceOfPersons();
		timelinePanel=new TimelinePanel(mainFrame, chartTitle, "value"," ", dataset);
		this.panel.add(this.timelinePanel, "grow");
	}
	
	public List<Category> getSelectedCategories() {
		return(selectedCategories);
	}
	
	private void createOccurenceOfPersons() {
		dataset=new Dataset(mainFrame);
		dataset.items = new ArrayList<>();
		BookModel model = this.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		List<Person> categories = dao.findByCategories(this.selectedCategories);
		SceneDAOImpl scenes = new SceneDAOImpl(session);
		double d = 0.0D;
		dataset.maxValue = 0L;
		dataset.listId = new ArrayList<>();
		for (Person person : categories) {
			long l = scenes.countByPerson(person);
			dataset.items.add(new DatasetItem(person.getAbbr(), l, person.getJColor()));
			SbApp.trace("--> id=" + person.getAbbr() + ", value=" + l + ", color=" + (person.getJColor()!=null?person.getJColor().getRGB():"null"));
			if (dataset.maxValue < l) {
				dataset.maxValue = l;
			}
			dataset.listId.add(person.getAbbr());
			d += l;
		}
		model.commit();
		dataset.average = (d / dataset.items.size());
	}

}
