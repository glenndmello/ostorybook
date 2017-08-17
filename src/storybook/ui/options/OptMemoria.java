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
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import org.miginfocom.swing.MigLayout;

import storybook.SbConstants;
import storybook.i18n.I18N;
import storybook.toolkit.BookUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
class OptMemoria extends OptionsAbstract implements ItemListener {
	private boolean balloon;
	private JRadioButton rbBalloon;
	
	public OptMemoria(MainFrame m) {
		super(m);
		initAll();
	}
	
	@Override
	public void init() {
		try {
			balloon = BookUtil.getBoolean(mainFrame, SbConstants.BookKey.MEMORIA_BALLOON,SbConstants.DEFAULT_MEMORIA_BALLOON);
		} catch (Exception e) {
			System.err.println(Arrays.toString(e.getStackTrace()));
			balloon = SbConstants.DEFAULT_MEMORIA_BALLOON;
		}
	}

	@Override
	public void initUi() {
		// balloon or tree layout
		JLabel lbPres = new JLabel(I18N.getColonMsg("graph.presentation"));
		ButtonGroup bgPresentation = new ButtonGroup();
		rbBalloon = new JRadioButton(I18N.getMsg("graph.pres.balloon"));
		if (balloon) {
			rbBalloon.setSelected(true);
		}
		bgPresentation.add(rbBalloon);
		JRadioButton rbTree = new JRadioButton(I18N.getMsg("graph.pres.tree"));
		bgPresentation.add(rbTree);
		if (!balloon) {
			rbTree.setSelected(true);
		}

		// layout
		setLayout(new MigLayout("wrap,fill"));
		add(lbPres);
		add(rbBalloon, "split 2");
		add(rbTree);

		rbTree.addItemListener(this);
		rbBalloon.addItemListener(this);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		boolean val = rbBalloon.isSelected();
		mainFrame.getBookController().memoriaSetBalloonLayout(val);
		BookUtil.store(mainFrame, SbConstants.BookKey.MEMORIA_BALLOON, val);
	}
}
