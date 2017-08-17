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
package storybook.i18n;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import org.miginfocom.swing.MigLayout;
import storybook.toolkit.SpellCheckerUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;

/**
 *
 * @author favdb
 */
public class I18NnewDlg extends AbstractDialog {

	public String language;
	private final JDialog parentDlg;
	private final List<String> languages;
	private JList<String> lsLanguage;
	private final boolean bFile=false;
	
	public I18NnewDlg(JDialog p, MainFrame m,List<String> l) {
		super(m);
		languages=l;
		parentDlg=p;
		initAll();
	}

	@Override
	public void init() {
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void initUi() {
		JLabel jLabel1 = new JLabel(I18N.getMsg("language.select"));
		JScrollPane jScrollPane1 = new JScrollPane();
        lsLanguage = new javax.swing.JList<>();
		DefaultListModel model = new DefaultListModel();
		for (SpellCheckerUtil.Language lang : SpellCheckerUtil.getLanguages()) {
			if (!languages.contains(lang.getCode()))
				model.addElement(lang.getCode()+"="+lang.getName());
		}
		lsLanguage.setModel(model);
		jScrollPane1.setViewportView(lsLanguage);
		
		//layout
		setLayout(new MigLayout(""));
		setTitle(I18N.getMsg("language.select"));
		add(jLabel1,"wrap");
		add(jScrollPane1,"wrap");
		add(getCancelButton(), "split 2,sg,right");
		add(getOkButton(), "sg, right");
		pack();
		setLocationRelativeTo(mainFrame);
		setModal(true);
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lsLanguage.getSelectedIndex()==-1) return;
				language=(String) lsLanguage.getSelectedValue();
				dispose();
			}
		};
	}

	@Override
	protected AbstractAction getCancelAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				language="";
				dispose();
			}
		};
	}

	public String getLanguage() {
		return(language);
	}
	
	public boolean isFile() {
		return(bFile);
	}
}
