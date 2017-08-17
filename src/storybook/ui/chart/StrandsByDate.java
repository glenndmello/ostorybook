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
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.ui.MainFrame;
import storybook.ui.chart.timeline.Dataset;
import storybook.ui.chart.timeline.DureeScene;
import storybook.ui.chart.timeline.DatasetItem;
import storybook.ui.chart.timeline.TimelinePanel;

/**
 *
 * @author favdb
 */
public class StrandsByDate extends AbstractPersonsChart {

	private TimelinePanel timelinePanel;
	private Dataset dataset;

	public StrandsByDate(MainFrame mainFrame) {
		super(mainFrame, "tools.charts.overall.strand.date");
		this.partRelated = true;
	}

	@Override
	protected void initChartUi() {
		dataset=new Dataset(mainFrame);
		createStrandsDate();
		timelinePanel=new TimelinePanel(mainFrame, chartTitle, "date", "strands", dataset);
		this.panel.add(this.timelinePanel, "grow");
	}
	
	public List<Category> getSelectedCategories() {
		return selectedCategories;
	}

	private void createStrandsDate() {
		dataset=new Dataset(mainFrame);
		dataset.items= new ArrayList<>();
		Part part = this.mainFrame.getCurrentPart();
		BookModel model = this.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		StrandDAOImpl strandDAO = new StrandDAOImpl(session);
		List<Strand> strands = strandDAO.findAll();
		ChapterDAOImpl chapterDAO = new ChapterDAOImpl(session);
		List<Chapter> chapters;
		if (mainFrame.showAllParts) chapters = chapterDAO.findAll();
		else chapters = chapterDAO.findAll(part);
		for (Strand strand : strands) {
			List<DureeScene> dureeScene = new ArrayList<>();
			for (Chapter chapter : chapters) {
				List<Scene> scenes = chapterDAO.findScenes(chapter);
				for (Scene scene : scenes) {
					if (scene.hasSceneTs()) {
						dureeScene.add(new DureeScene(scene.getId(), scene.getSceneTs()));
					}
				}
			}
			if (!dureeScene.isEmpty()) {
				dureeScene = DureeScene.calculDureeScene(session, dureeScene);
				for (DureeScene d : dureeScene) {
					SbApp.trace(strand.getAbbr() + "(" + d.trace() + ")");
					dataset.items.add(new DatasetItem(strand.getName(), d.debut, d.fin, strand.getJColor()));
				}
			}
		}
	}

}
