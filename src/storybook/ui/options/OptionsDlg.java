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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;
import org.miginfocom.swing.MigLayout;
import storybook.SbConstants;
import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.SbView;
import storybook.ui.dialog.AbstractDialog;

/**
 *
 * @author favdb
 */
public class OptionsDlg extends AbstractDialog {

	private String sbView;
	OptBook book;
	OptChrono chrono;
	OptManage manage;
	OptMemo memo;
	OptMemoria memoria;
	OptReading reading;
	
	public OptionsDlg(MainFrame m) {
		super(m);
		sbView=null;
		init();
		initUi();
	}
	
	public OptionsDlg(MainFrame m, String v) {
		super(m);
		sbView=v;
		init();
		initUi();
	}
	
	public static void show(MainFrame m, String v) {
		OptionsDlg dlg = new OptionsDlg(m,v);
		dlg.setVisible(true);
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		//setPreferredSize(new Dimension(500, 300));
		book=new OptBook(mainFrame);
		chrono=new OptChrono(mainFrame);
		manage=new OptManage(mainFrame);
		memo=new OptMemo(mainFrame);
		memoria=new OptMemoria(mainFrame);
		reading=new OptReading(mainFrame);
		
		//layout
		setLayout(new MigLayout("wrap,fill"));
		setTitle(I18N.getMsg("options"));
		setIconImage(I18N.getIconImage("icon.sb"));
		if (sbView==null) {
			JTabbedPane tabbed=new JTabbedPane();
			tabbed.add(I18N.getMsg("view.book"),book);
			tabbed.add(I18N.getMsg("view.chrono"),chrono);
			tabbed.add(I18N.getMsg("view.manage"),manage);
			tabbed.add(I18N.getMsg("view.memo"),memo);
			tabbed.add(I18N.getMsg("view.memoria"),memoria);
			tabbed.add(I18N.getMsg("view.reading"),reading);
			add(tabbed,"wrap");
		} else {
			if (sbView.equals(SbConstants.ViewName.BOOK.toString())) {
				add(book,"wrap");
			}
			else if (sbView.equals(SbConstants.ViewName.CHRONO.toString())) {
				add(chrono,"wrap");
			}
			else if (sbView.equals(SbConstants.ViewName.MANAGE.toString())) {
				add(manage,"wrap");
			}
			else if (sbView.equals(SbConstants.ViewName.MEMOS.toString())) {
				add(memo,"wrap");
			}
			else if (sbView.equals(SbConstants.ViewName.MEMORIA.toString())) {
				add(memoria,"wrap");
			}
			else if (sbView.equals(SbConstants.ViewName.READING.toString())) {
				add(reading,"wrap");
			}
		}
		//add(getOkButton(), "span, split 2,sg,right");
		add(getCloseButton(), "span, sg, right");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);
	}

}
