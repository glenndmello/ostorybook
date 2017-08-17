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
package storybook.ui.dialog;

import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.miginfocom.swing.MigLayout;
import storybook.i18n.I18N;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class SearchResultsDlg extends AbstractDialog {

	private JScrollPane scroller;
	
	public SearchResultsDlg(MainFrame m, JPanel res) {
		super(m);
		initAll();
		scroller.setViewportView(res);
	}

	@Override
	public void init() {
	}
	
	@Override
	public void initUi() {
		scroller=new JScrollPane();
		scroller.setMinimumSize(new Dimension(576,419));
		
		//layout
		setLayout(new MigLayout());
		setTitle(I18N.getMsg("search.results"));
		add(scroller,"wrap");
		add(getCloseButton(),"sg,span,right");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);		
	}
	
}
