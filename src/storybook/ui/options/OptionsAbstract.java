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

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public abstract class OptionsAbstract extends JPanel implements ChangeListener {

	protected MainFrame mainFrame;
	private boolean zoom;
	protected int zoomValue;
	protected int zoomMinValue;
	protected int zoomMaxValue;
	
	public OptionsAbstract(MainFrame m) {
		mainFrame=m;
	}
	
	public void initAll() {
		init();
		initUi();
	}
	
	public void init() {
		
	}
	
	public void initUi() {
		
	}
	
	public int getZoomMinValue() {
		return this.zoomMinValue;
	}

	public void setZoomMinValue(int zoomMinValue) {
		this.zoomMinValue = zoomMinValue;
	}

	public int getZoomMaxValue() {
		return this.zoomMaxValue;
	}

	public void setZoomMaxValue(int zoomMaxValue) {
		this.zoomMaxValue = zoomMaxValue;
	}

	public boolean isZoom() {
		return this.zoom;
	}

	public void setZoom(boolean zoom) {
		this.zoom = zoom;
	}

	protected void zoom(int val) {
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();
		if (!slider.getValueIsAdjusting()) {
			int val = slider.getValue();
			zoom(val);
		}
	}

}
