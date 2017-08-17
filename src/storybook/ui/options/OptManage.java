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
package storybook.ui.options;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import org.miginfocom.swing.MigLayout;

import storybook.SbConstants;
import storybook.i18n.I18N;
import storybook.toolkit.BookUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
class OptManage extends OptionsAbstract {
	private final String CN_COLUMNS = "ColumnSlider";
	private final String HIDE_UNASSIGNED="HideUnassigned";
	private int columns;
	private boolean hide_unassigned;
	private JCheckBox ckHide;
	
	public OptManage(MainFrame m) {
		super(m);
		init();
		initUi();
	}
	
	@Override
	public void init() {
		setZoomMinValue(SbConstants.DEFAULT_MANAGEMINZOOM);
		setZoomMaxValue(SbConstants.DEFAULT_MANAGEMAXZOOM);
		try {
			zoomValue = BookUtil.getInteger(mainFrame,
					SbConstants.BookKey.MANAGE_ZOOM, SbConstants.DEFAULT_MANAGEZOOM);
			columns = BookUtil.getInteger(mainFrame,
					SbConstants.BookKey.MANAGE_COLUMNS,
					SbConstants.DEFAULT_MANAGECOLUMNS);
			hide_unassigned=BookUtil.getBoolean(mainFrame,SbConstants.BookKey.MANAGE_HIDE_UNASSIGNED);
		} catch (Exception e) {
			System.err.println(Arrays.toString(e.getStackTrace()));
			zoomValue = SbConstants.DEFAULT_MANAGEZOOM;
			columns = SbConstants.DEFAULT_MANAGECOLUMNS;
		}
	}

	@Override
	public void initUi() {
		// columns
		JLabel lbColumns = new JLabel(I18N.getColonMsg("columns"));
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 20, columns);
		slider.setName(CN_COLUMNS);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(5);
		slider.setOpaque(false);
		slider.setPaintTicks(true);
		slider.addChangeListener(this);
		
		ckHide=new JCheckBox(I18N.getMsg("preferences.manage.hide_unassigned"));
		ckHide.setName(HIDE_UNASSIGNED);
		ckHide.setSelected(hide_unassigned);
		ckHide.addActionListener((ActionEvent evt) -> {
			BookUtil.store(mainFrame, SbConstants.BookKey.MANAGE_HIDE_UNASSIGNED,ckHide.isSelected());
			mainFrame.getBookController().manageSetHideUnassigned((Boolean)ckHide.isSelected());
		});

		// layout
		setLayout(new MigLayout("wrap,fill"));
		add(lbColumns);
		add(slider, "growx, wrap");
		add(ckHide);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Component comp = (Component) e.getSource();
		if (CN_COLUMNS.equals(comp.getName())) {
			JSlider slider = (JSlider) e.getSource();
			if (!slider.getValueIsAdjusting()) {
				int val = slider.getValue();
				BookUtil.store(mainFrame, SbConstants.BookKey.MANAGE_COLUMNS,val);
				mainFrame.getBookController().manageSetColumns(val);
				return;
			}
		}
		super.stateChanged(e);
	}

	@Override
	protected void zoom(int val) {
		BookUtil.store(mainFrame, SbConstants.BookKey.MANAGE_ZOOM, val);
		mainFrame.getBookController().manageSetZoom(val);
	}
}
