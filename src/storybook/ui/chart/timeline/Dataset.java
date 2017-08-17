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
package storybook.ui.chart.timeline;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import storybook.model.hbn.entity.Category;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class Dataset {
	public String type;
	public List<DatasetItem> items;
	private final MainFrame mainFrame;
	public List<Category> selectedCategories;
	//private final Timeline parent;
	public int marginTop;
	public int marginBottom;
	public int marginLeft;
	public int marginRight;
	public long intervalDate;
	public int intervalX;
	public Date lastDate;
	public Date firstDate;
	public int areaHeight;
	public ArrayList<String> listId;
	public int intervalY;
	public int areaWidth;
	public long intervalValue;
	public long maxValue;
	public double average;
	public List<String> selectedCountries;

	public Dataset(MainFrame mainFrame) {
		this.mainFrame=mainFrame;
	}

	DatasetItem findItem(String get) {
		if (items!=null) for (DatasetItem item:items) {
			if (item.id.equals(get)) return(item);
			if (item.subItems!=null) for (DatasetItem subItem: item.subItems) {
				if (subItem.id.equals(get)) return(subItem);
			}
		}
		return(null);
	}
	
	public int getItemInList(String id) {
		int i=0;
		for (String x : listId) {
			if (x.equals(id)) return(i);
			i++;
		}
		return(-1);
	}

}
