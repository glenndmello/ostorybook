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

import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.miginfocom.swing.MigLayout;
import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbPref;
import storybook.i18n.I18N;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class FirstStartDlg extends AbstractDialog {

	private JComboBox<String> languageCombo;
	private JCheckBox ckTypist;
	private JLabel lbLanguage;

	public FirstStartDlg() {
		super((MainFrame) null);
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		lbLanguage = new JLabel(I18N.getMsg("language"));
		lbLanguage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

		JLabel lbLogo = new JLabel();
		lbLogo.setBackground(java.awt.Color.white);
		lbLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		ImageIcon img=new ImageIcon(getClass().getResource("/storybook/resources/icons/Banner256.png"));
		lbLogo.setIcon(img);
		lbLogo.setBorder(null);

		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		for (SbConstants.Language lang : SbConstants.Language.values()) {
			model.addElement(lang.getI18N());
		}
		languageCombo = new JComboBox<>();
		languageCombo.setModel(model);
		languageCombo.addActionListener((java.awt.event.ActionEvent evt) -> {
			changeLanguage();
		});
		
		ckTypist=new JCheckBox(I18N.getMsg("typist.preference"));

		//layout
		setLayout(new MigLayout("", "[][]", ""));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setModal(true);
		setTitle(I18N.getMsg("first_start.title"));
		add(lbLogo, "span,center,wrap");
		add(lbLanguage);
		add(languageCombo, "growx, wrap");
		add(ckTypist,"span, wrap");
		add(getOkButton(),"span,center");
		pack();
		setLocationRelativeTo(mainFrame);
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = languageCombo.getSelectedIndex();
				SbConstants.Language lang = SbConstants.Language.values()[i];
				Locale locale = lang.getLocale();
				SbApp.getInstance().preferences.setString(SbPref.Key.LANG, I18N.getCountryLanguage(locale));
				I18N.initMsgInternal(locale);
				SbApp.getInstance().preferences.setBoolean(SbPref.Key.TYPIST_USE, ckTypist.isSelected());
				dispose();
			}
		};
	}

	private void changeLanguage() {
		int i = languageCombo.getSelectedIndex();
		SbConstants.Language lang = SbConstants.Language.values()[i];
		Locale locale = lang.getLocale();
		I18N.initMsgInternal(locale);
		setTitle(I18N.getMsg("first_start.title"));
		lbLanguage.setText(I18N.getMsg("language"));
		ckTypist.setText(I18N.getMsg("typist.preference"));
		pack();
	}

}
