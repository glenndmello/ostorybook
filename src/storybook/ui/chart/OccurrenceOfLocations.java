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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;
import storybook.SbApp;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Location;
import storybook.i18n.I18N;
import storybook.toolkit.swing.ColorUtil;
import storybook.ui.MainFrame;
import storybook.ui.chart.timeline.Dataset;
import storybook.ui.chart.timeline.DatasetItem;
import storybook.ui.chart.timeline.TimelinePanel;

/**
 *
 * @author favdb
 */
public class OccurrenceOfLocations extends AbstractChartPanel {

	private TimelinePanel timelinePanel;
	private List<JCheckBox> countryCbList;
	protected List<String> selectedCountries;
	private Dataset dataset;

	public OccurrenceOfLocations(MainFrame mainFrame) {
		super(mainFrame, "report.location.occurrence.title");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void initChart() {
		this.countryCbList = EntityUtil.createCountryCheckBoxes(this.mainFrame, this);
		this.selectedCountries = new ArrayList();
		updateSelectedCountries();
	}

	@Override
	protected void initChartUi() {
		dataset=new Dataset(mainFrame);
		createOccurenceOfLocations();
		timelinePanel=new TimelinePanel(mainFrame, chartTitle, "value", " ", dataset);
		this.panel.add(this.timelinePanel, "grow");
	}

	@Override
	protected void initOptionsUi() {
		JPanel localJPanel = new JPanel(new MigLayout("flowx"));
		localJPanel.setOpaque(false);
		JLabel localJLabel = new JLabel(I18N.getColonMsg("location.country"));
		localJPanel.add(localJLabel);
		for (JCheckBox localJCheckBox : this.countryCbList) {
			localJPanel.add(localJCheckBox);
		}
		this.optionsPanel.add(localJPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		updateSelectedCountries();
		refreshChart();
	}

	private void updateSelectedCountries() {
		this.selectedCountries.clear();
		for (JCheckBox localJCheckBox : this.countryCbList) {
			if (localJCheckBox.isSelected()) {
				this.selectedCountries.add(localJCheckBox.getText());
			}
		}
	}

	public List<String> getSelectedCountries() {
		return(selectedCountries);
	}

	private void createOccurenceOfLocations() {
		dataset=new Dataset(mainFrame);
		dataset.items = new ArrayList<>();
		BookModel localDocumentModel = this.mainFrame.getBookModel();
		Session localSession = localDocumentModel.beginTransaction();
		LocationDAOImpl localLocationDAOImpl = new LocationDAOImpl(localSession);
		SceneDAOImpl localSceneDAOImpl = new SceneDAOImpl(localSession);
		List<Location> localList = localLocationDAOImpl.findByCountries(selectedCountries);
		double d = 0.0D;
		Color[] color = ColorUtil.getNiceColors();
		dataset.maxValue = 0L;
		dataset.listId = new ArrayList<>();
		int ncolor = 0;
		for (Location location : localList) {
			long l = localSceneDAOImpl.countByLocation(location);
			dataset.items.add(new DatasetItem(location.getAbbr(), l, color[ncolor]));
			SbApp.trace("--> id=" + location.getAbbr() + ", value=" + l + ", color=" + color[ncolor].getRGB());
			if (dataset.maxValue < l) {
				dataset.maxValue = l;
			}
			dataset.listId.add(location.getAbbr());
			ncolor++;
			if (ncolor >= color.length) {
				ncolor = 0;
			}
			d += l;
		}
		localDocumentModel.commit();
		dataset.average = (d / localList.size());
	}

}
