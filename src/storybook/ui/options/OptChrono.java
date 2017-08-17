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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import javax.swing.JCheckBox;

import org.miginfocom.swing.MigLayout;
import storybook.SbConstants;
import storybook.i18n.I18N;
import storybook.toolkit.BookUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
class OptChrono extends OptionsAbstract implements ItemListener{
	private final String CN_LAYOUT_DIRECTION = "CbLayoutDirection";
	private final String CN_DATE_DIFFERENCE = "CbDateDifference";
	private boolean layoutDirection;
	private boolean showDateDifference;
	
	public OptChrono(MainFrame m) {
		super(m);
		init();
		initUi();
	}
	
	@Override
	public void init() {
		setZoomMinValue(SbConstants.MIN_CHRONO_ZOOM);
		setZoomMaxValue(SbConstants.MAX_CHRONO_ZOOM);
		try {
			zoomValue=BookUtil.getInteger(mainFrame,
					SbConstants.BookKey.CHRONO_ZOOM, SbConstants.DEFAULT_CHRONO_ZOOM);
			layoutDirection = BookUtil.getBoolean(mainFrame,
					SbConstants.BookKey.CHRONO_LAYOUT_DIRECTION,
					SbConstants.DEFAULT_CHRONO_LAYOUT_DIRECTION);
			showDateDifference = BookUtil.getBoolean(mainFrame,
					SbConstants.BookKey.CHRONO_SHOW_DATE_DIFFERENCE,
					SbConstants.DEFAULT_CHRONO_SHOW_DATE_DIFFERENCE);
		} catch (Exception e) {
			System.err.println(Arrays.toString(e.getStackTrace()));
			zoomValue = SbConstants.DEFAULT_CHRONO_ZOOM;
			layoutDirection = SbConstants.DEFAULT_CHRONO_LAYOUT_DIRECTION;
			showDateDifference = SbConstants.DEFAULT_CHRONO_SHOW_DATE_DIFFERENCE;
		}
	}

	@Override
	public void initUi() {
		// layout direction
		JCheckBox cbDir = new JCheckBox();
		cbDir.setName(CN_LAYOUT_DIRECTION);
		cbDir.addItemListener(this);
		cbDir.setText(I18N.getMsg("horizontal.vertical"));
		cbDir.setOpaque(false);
		cbDir.setSelected(layoutDirection);
		cbDir.setToolTipText(I18N.getColonMsg("statusbar.change.layout.direction"));

		// show date difference
		JCheckBox cbDiff = new JCheckBox();
		cbDiff.setName(CN_DATE_DIFFERENCE);
		cbDiff.addItemListener(this);
		cbDiff.setText(I18N.getMsg("preferences.datediff.show"));
		cbDiff.setOpaque(false);
		cbDiff.setSelected(showDateDifference);
		cbDiff.setToolTipText(I18N.getColonMsg("preferences.datediff"));

		// layout
		setLayout(new MigLayout("wrap,fill"));
		add(cbDir,"wrap");
		add(cbDiff,"wrap");
	}

	@Override
	protected void zoom(int val) {
		BookUtil.store(mainFrame, SbConstants.BookKey.CHRONO_ZOOM, val);
		mainFrame.getBookController().chronoSetZoom(val);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox cb = (JCheckBox) e.getSource();
		boolean val = cb.isSelected();

		if (cb.getName().equals(CN_LAYOUT_DIRECTION)) {
			mainFrame.getBookController().chronoSetLayoutDirection(val);
			BookUtil.store(mainFrame, SbConstants.BookKey.CHRONO_LAYOUT_DIRECTION,
					val);
			return;
		}

		if (cb.getName().equals(CN_DATE_DIFFERENCE)) {
			mainFrame.getBookController().chronoSetShowDateDifference(
					cb.isSelected());
			BookUtil.store(mainFrame,
					SbConstants.BookKey.CHRONO_SHOW_DATE_DIFFERENCE, val);
			return;
		}
	}
}
