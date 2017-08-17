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
import storybook.toolkit.BookUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
class OptReading extends OptionsAbstract {
	private final String CN_FONT_SIZE = "FontSizeSlider";
	private int fontSize;
	
	public OptReading(MainFrame m) {
		super(m);
		initAll();
	}
	
	@Override
	public void init() {
		setZoomMinValue(10);
		setZoomMaxValue(200);
		try {
			zoomValue = BookUtil.getInteger(mainFrame, SbConstants.BookKey.READING_ZOOM, SbConstants.DEFAULT_READINGZOOM);
			fontSize = BookUtil.getInteger(mainFrame, SbConstants.BookKey.READING_FONT_SIZE, SbConstants.DEFAULT_READINGFONTSIZE);
		} catch (Exception e) {
			System.err.println(Arrays.toString(e.getStackTrace()));
			zoomValue = SbConstants.DEFAULT_READINGZOOM;
			fontSize = SbConstants.DEFAULT_READINGFONTSIZE;
		}
	}

	@Override
	public void initUi() {
		// font size
		JLabel lbFontSize = new JLabel(I18N.getColonMsg("font.size"));
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 6, 40, fontSize);
		slider.setName(CN_FONT_SIZE);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(5);
		slider.setOpaque(false);
		slider.setPaintTicks(true);
		slider.addChangeListener(this);

		// layout
		setLayout(new MigLayout("wrap,fill"));
		add(lbFontSize);
		add(slider, "growx");
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Component comp = (Component) e.getSource();
		if (CN_FONT_SIZE.equals(comp.getName())) {
			JSlider slider = (JSlider) e.getSource();
			if (!slider.getValueIsAdjusting()) {
				int val = slider.getValue();
				mainFrame.getBookController().readingSetFontSize(val);
				BookUtil.store(mainFrame, SbConstants.BookKey.READING_FONT_SIZE, val);
				return;
			}
		}
		super.stateChanged(e);
	}

	@Override
	protected void zoom(int val) {
		BookUtil.store(mainFrame, SbConstants.BookKey.READING_ZOOM, val);
		mainFrame.getBookController().readingSetZoom(val);
	}
}
