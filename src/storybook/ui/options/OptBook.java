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
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import org.miginfocom.swing.MigLayout;

import storybook.SbConstants;
import storybook.i18n.I18N;
import storybook.model.hbn.entity.Internal;
import storybook.toolkit.BookUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
class OptBook extends OptionsAbstract {
	private final String CN_HEIGHT_FACTOR = "HeightFactorSlider";
	private int heightFactor;

	public OptBook(MainFrame m) {
		super(m);
		init();
		initUi();
	}
	
	@Override
	public void init() {
		setZoomMinValue(SbConstants.DEFAULT_BOOKMINZOOM);
		setZoomMaxValue(SbConstants.DEFAULT_BOOKMAXZOOM);
		try {
			Internal internal = BookUtil.get(mainFrame,	SbConstants.BookKey.BOOK_ZOOM, SbConstants.DEFAULT_BOOKZOOM);
			zoomValue = internal.getIntegerValue();
			internal = BookUtil.get(mainFrame, SbConstants.BookKey.BOOK_HEIGHT_FACTOR, SbConstants.DEFAULT_BOOK_HEIGHT_FACTOR);
			heightFactor = internal.getIntegerValue();
		} catch (Exception e) {
			System.err.println(Arrays.toString(e.getStackTrace()));
			zoomValue = SbConstants.DEFAULT_BOOKZOOM;
			heightFactor = SbConstants.DEFAULT_BOOK_HEIGHT_FACTOR;
		}
	}

	@Override
	public void initUi() {
		// height factor
		JLabel lbHeightFactor = new JLabel(I18N.getColonMsg("height.factor"));
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 10, 20, heightFactor);
		slider.setName(CN_HEIGHT_FACTOR);
		slider.setMajorTickSpacing(5);
		slider.setMinorTickSpacing(1);
		slider.setOpaque(false);
		slider.setPaintTicks(true);
		slider.addChangeListener(this);

		// layout
		setLayout(new MigLayout());
		//setPreferredSize(new Dimension(500, 300));
		add(lbHeightFactor);
		add(slider, "growx");
	}

	@Override
	protected void zoom(int val) {
		BookUtil.store(mainFrame, SbConstants.BookKey.BOOK_ZOOM, val);
		mainFrame.getBookController().bookSetZoom(val);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Component comp = (Component) e.getSource();
		if (CN_HEIGHT_FACTOR.equals(comp.getName())) {
			JSlider slider = (JSlider) e.getSource();
			if (!slider.getValueIsAdjusting()) {
				int val = slider.getValue();
				mainFrame.getBookController().bookSetHeightFactor(val);
				BookUtil.store(mainFrame, SbConstants.BookKey.BOOK_HEIGHT_FACTOR, val);
				return;
			}
		}
		super.stateChanged(e);
	}
}
