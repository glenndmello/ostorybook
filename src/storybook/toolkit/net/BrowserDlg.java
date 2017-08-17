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
package storybook.toolkit.net;

import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import org.miginfocom.swing.MigLayout;
import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;

/**
 *
 * @author favdb
 */
public class BrowserDlg extends AbstractDialog {

	private final String url;
	private final String titleDlg;
	
	public BrowserDlg(java.awt.Frame parent, String u, String t) {
		url=u;
		titleDlg=t;
		initAll();
	}

	@Override
	public void init() {
	}
	
	@Override
	public void initUi() {
		JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);
		JScrollPane scroller = new JScrollPane();
        scroller.setPreferredSize(new java.awt.Dimension(640, 480));
        scroller.setViewportView(jEditorPane);
		try {
			jEditorPane.setPage(url);
		} catch (IOException ex) {
			jEditorPane.setText(I18N.getMsg("error.internet.connection.failed", url) + "\n");
		}
		
		//layout
		setLayout(new MigLayout("wrap 9"));
		setTitle(titleDlg);
		add(scroller,"wrap");
		add(getCloseButton(), "right");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);
	}
	
	public static void show(MainFrame m, String u, String t) {
		SwingUtil.showDialog(new BrowserDlg(m,u,t),m,true);
	}
}
