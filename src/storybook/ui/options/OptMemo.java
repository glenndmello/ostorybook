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
import storybook.model.hbn.entity.Internal;
import storybook.toolkit.BookUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
class OptMemo extends OptionsAbstract implements ItemListener {
	private boolean left;
	private JRadioButton rbLeft;
	
	public OptMemo(MainFrame m) {
		super(m);
		initAll();
	}
	
	@Override
	public void init() {
		try {
			Internal internal = BookUtil.get(mainFrame, SbConstants.BookKey.MEMOS_VIEW,false);
			left = internal.getBooleanValue();
		} catch (Exception e) {
			System.err.println(Arrays.toString(e.getStackTrace()));
			left = false;
		}
	}

	@Override
	public void initUi() {
		// memos list layout
		JLabel label = new JLabel(I18N.getColonMsg("memo.layoutdirection"));
		ButtonGroup bg = new ButtonGroup();
		rbLeft = new JRadioButton(I18N.getMsg("memo.layoutdirection.left"));
		rbLeft.setName("left");
		if (!left) {
			rbLeft.setSelected(true);
		}
		bg.add(rbLeft);
		JRadioButton rbTop = new JRadioButton(I18N.getMsg("memo.layoutdirection.top"));
		rbTop.setName("top");
		bg.add(rbTop);
		if (left) {
			rbTop.setSelected(true);
		}

		// layout
		setLayout(new MigLayout("wrap,fill"));
		add(label);
		add(rbLeft, "split 2");
		add(rbTop);

		rbLeft.addItemListener(this);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		boolean val = rbLeft.isSelected();
		JRadioButton rb=(JRadioButton)e.getItem();
		if (rb.getName().equals("left") && val) val=false;
		else val=true;
		BookUtil.store(mainFrame, SbConstants.BookKey.MEMOS_VIEW, val);
	}
}
