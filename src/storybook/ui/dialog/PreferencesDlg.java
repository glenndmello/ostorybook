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

import storybook.ui.dialog.chooser.FontChooserDlg;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.miginfocom.swing.MigLayout;

import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbPref;
import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class PreferencesDlg extends AbstractDialog {

	private JComboBox<String> cbLanguage;
	Font font;
	Font fontEditor;
	private int inLang;
	private JComboBox<String> cbDateFormat;
	private JCheckBox ckOnStart;
	private JCheckBox ckOnExit;
	private JRadioButton rbUpdaterNever;
	private JRadioButton rbUpdaterYear;
	private JRadioButton rbUpdaterAuto;
	private JRadioButton rbTxt;
	private JRadioButton rbCsv;
	private JRadioButton rbXml;
	private JRadioButton rbHtml;
	private JCheckBox ckTypist;
	private JCheckBox cbMemory;
	private JLabel lbDateFormat;
	private JLabel lbOnStart;
	private JLabel lbOnExit;
	private JLabel lbUpdater;
	private JLabel lbAppearence;
	private JLabel lbFontGeneral;
	private JLabel lbShowFontGeneral;
	private JLabel lbFontEditor;
	private JLabel lbShowFontEditor;
	private JLabel lbExport;
	private JLabel lbExportParam;
	private JLabel lbCommon;
	private JLabel lbLanguage;
	private Locale currentlocale;
	
	public PreferencesDlg(MainFrame m) {
		super(m);
		initAll();
	}

	@Override
	public void init() {
		currentlocale=Locale.getDefault();
	}

	@Override
	public void initUi() {
		super.initUi();
		
		JButton btDumpPreferences = new JButton("Dump Preferences");
        btDumpPreferences.addActionListener((java.awt.event.ActionEvent evt) -> {
			SbApp.getInstance().preferences.dump();
		});
		
		cbMemory=new JCheckBox(I18N.getMsg("preferences.memory"));
		cbMemory.setSelected(mainFrame.getPref().getBoolean(SbPref.Key.MEMORY, false));

		// layout
		setLayout(new MigLayout("wrap,fill"/*, "", "[grow][]"*/));
		setTitle(I18N.getMsg("preferences.title"));
		setIconImage(I18N.getIconImage("icon.sb"));
		add(initCommon(),"wrap");
		add(initUpdater(),"wrap");
		add(initAppearence(),"wrap");
		add(initExport(),"wrap");
		add(cbMemory,"wrap");
		add(btDumpPreferences,"split 4, left");
		JLabel lbEmpty=new JLabel(" ");
		add(lbEmpty,"growx");
		add(getCancelButton(), "right");
		add(getOkButton(), "right");
		pack();
		this.setLocationRelativeTo(mainFrame);
	}

	private JPanel initCommon() {
		lbCommon = new JLabel(I18N.getMsg("general"));
		lbCommon.setFont(new java.awt.Font("Noto Sans", 1, 12));

		lbLanguage = new JLabel(I18N.getMsg("language"));
		String currentLangStr = I18N.getCountryLanguage(Locale.getDefault());
		SbConstants.Language lang_idx = SbConstants.Language.valueOf(currentLangStr);
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		for (SbConstants.Language lang : SbConstants.Language.values()) {
			model.addElement(lang.getI18N());
		}
        cbLanguage = new javax.swing.JComboBox<>();
		cbLanguage.setModel(model);
		inLang=lang_idx.ordinal();
		cbLanguage.setSelectedIndex(lang_idx.ordinal());
		cbLanguage.addActionListener((java.awt.event.ActionEvent evt) -> {
			int i = cbLanguage.getSelectedIndex();
			SbConstants.Language lang = SbConstants.Language.values()[i];
			Locale locale = lang.getLocale();
			I18N.initMsgInternal(locale);
			refreshUi();
		});
		
		lbDateFormat = new JLabel(I18N.getMsg("dateformat.label"));
        cbDateFormat = new JComboBox<>();
		model = new DefaultComboBoxModel<>();
		model.addElement("dd-MM-yyyy");
		model.addElement("MM-dd-yyyy");
		model.addElement("dd/MM/yyyy");
		cbDateFormat.setModel(model);
		String prefDateFormat = mainFrame.getPref().getString(SbPref.Key.DATEFORMAT, "MM-dd-yyyy");
		cbDateFormat.setSelectedItem(prefDateFormat);
		
		lbOnStart = new JLabel(I18N.getMsg("preferences.start"));
        ckOnStart = new JCheckBox(I18N.getMsg("preferences.start.openproject"));
		ckOnStart.setSelected(mainFrame.getPref().getBoolean(SbPref.Key.OPEN_LAST_FILE, false));

		lbOnExit = new JLabel(I18N.getMsg("preferences.exit"));
        ckOnExit = new JCheckBox(I18N.getMsg("preferences.exit.chb"));
		ckOnExit.setSelected(mainFrame.getPref().getBoolean(SbPref.Key.CONFIRM_EXIT, false));
		
		//layout
		JPanel p=new JPanel();
		p.setLayout(new MigLayout());
		p.add(lbCommon,"wrap");
		p.add(lbLanguage,"split 2");p.add(cbLanguage,"wrap");
		p.add(lbDateFormat,"split 2");p.add(cbDateFormat,"wrap");
		p.add(lbOnStart,"split 2");p.add(ckOnStart,"wrap");
		p.add(lbOnExit,"split 2");p.add(ckOnExit,"wrap");
		
		return(p);
	}
	
	private JPanel initUpdater() {
		lbUpdater = new JLabel(I18N.getMsg("help.update"));
		lbUpdater.setFont(new java.awt.Font("Noto Sans", 1, 12));
        rbUpdaterNever = new JRadioButton(I18N.getMsg("preferences.updater.never"));
        rbUpdaterYear = new JRadioButton(I18N.getMsg("preferences.updater.year"));
        rbUpdaterAuto = new JRadioButton(I18N.getMsg("preferences.updater.auto"));
		switch(mainFrame.getPref().getString(SbPref.Key.DO_UPDATER, "")) {
			case "1": rbUpdaterNever.setSelected(true); break;
			case "2": rbUpdaterYear.setSelected(true); break;
			case "3": rbUpdaterAuto.setSelected(true); break;
			default: rbUpdaterNever.setSelected(true);
		}
		ButtonGroup bgUpdater = new ButtonGroup();
		bgUpdater.add(rbUpdaterNever);
		bgUpdater.add(rbUpdaterYear);
		bgUpdater.add(rbUpdaterAuto);
		
		JPanel p=new JPanel();
		p.setLayout(new MigLayout());
		p.add(lbUpdater,"span, wrap");
		p.add(new JLabel("     ")/*,"wrap"*/);
		p.add(rbUpdaterNever/*,"wrap"*/);
		p.add(rbUpdaterYear,"wrap");
		p.add(new JLabel("     ")/*,"wrap"*/);
		p.add(rbUpdaterAuto,"wrap");
		return(p);
	}
	
	private JPanel initAppearence() {
		lbAppearence = new JLabel(I18N.getMsg("preferences.appearance"));
        lbAppearence.setFont(new Font("Noto Sans", 1, 12));
		
		lbFontGeneral = new JLabel(I18N.getMsg("preferences.font.standard"));
		lbShowFontGeneral = new JLabel(SwingUtil.getNiceFontName(font));
        lbShowFontGeneral.setBorder(BorderFactory.createEtchedBorder());
		font = SbApp.getInstance().getDefaultFont();
		lbShowFontGeneral.setText(SwingUtil.getNiceFontName(font));
		JButton btFontGeneral = new JButton(I18N.getIcon("icon.small.preferences"));
		btFontGeneral.setMargin(new Insets(0,0,0,0));
		btFontGeneral.addActionListener((java.awt.event.ActionEvent evt) -> {
			FontChooserDlg dlg = new FontChooserDlg(this,font);
			dlg.setVisible(true);
			if (dlg.isCanceled) return;
			if (dlg.getSelectedFont() == null) {
				return;
			}
			lbShowFontGeneral.setFont(dlg.getSelectedFont());
			lbShowFontGeneral.setText(SwingUtil.getNiceFontName(dlg.getSelectedFont()));
			font = dlg.getSelectedFont();
		});

		lbFontEditor = new JLabel(I18N.getMsg("preferences.editor.font"));
		lbShowFontEditor = new JLabel(SwingUtil.getNiceFontName(fontEditor));
        lbShowFontEditor.setBorder(BorderFactory.createEtchedBorder());
		fontEditor = SbApp.getInstance().getEditorFont();
		lbShowFontEditor.setFont(fontEditor);
		lbShowFontEditor.setText(SwingUtil.getNiceFontName(fontEditor));
		JButton btFontEditor = new JButton(I18N.getIcon("icon.small.preferences"));
		btFontEditor.setMargin(new Insets(0,0,0,0));
		btFontEditor.addActionListener((java.awt.event.ActionEvent evt) -> {
			FontChooserDlg dlg = new FontChooserDlg(this,fontEditor);
			dlg.setVisible(true);
			if (dlg.isCanceled) return;
			if (dlg.getSelectedFont() == null) {
				return;
			}
			fontEditor = dlg.getSelectedFont();
			lbShowFontEditor.setFont(fontEditor);
			lbShowFontEditor.setText(SwingUtil.getNiceFontName(fontEditor));
		});

		ckTypist=new JCheckBox(I18N.getMsg("typist.preference"));
		ckTypist.setSelected(mainFrame.getPref().getBoolean(SbPref.Key.TYPIST_USE, false));
		
		//layout
		JPanel p=new JPanel();
		p.setLayout(new MigLayout());
		p.add(lbAppearence,"span,wrap");
		p.add(lbFontGeneral);p.add(lbShowFontGeneral);p.add(btFontGeneral,"wrap");
		p.add(lbFontEditor);p.add(lbShowFontEditor);p.add(btFontEditor,"wrap");
		p.add(ckTypist,"span");
		return(p);

	}
	
	private JPanel initExport() {
		lbExport = new JLabel(I18N.getMsg("preferences.export"));
        lbExport.setFont(new Font("Noto Sans", Font.BOLD, 12));
		lbExportParam = new JLabel(I18N.getMsg("preferences.export.parameters"));
        lbExportParam.setFont(new Font("Noto Sans", Font.ITALIC, 12));
        rbTxt = new JRadioButton(I18N.getMsg("preferences.export.txt"));
        rbCsv = new JRadioButton(I18N.getMsg("preferences.export.csv"));
        rbXml = new JRadioButton(I18N.getMsg("preferences.export.xml"));
        rbHtml = new JRadioButton(I18N.getMsg("preferences.export.html"));
		switch(mainFrame.getPref().getString(SbPref.Key.EXPORT_PREF, "xml")) {
			case "txt": rbTxt.setSelected(true); break;
			case "csv": rbCsv.setSelected(true); break;
			case "html": rbCsv.setSelected(true); break;
			default: rbXml.setSelected(true); break;
		}
		ButtonGroup bgExport = new ButtonGroup();
		bgExport.add(rbTxt);
		bgExport.add(rbCsv);
		bgExport.add(rbXml);
		bgExport.add(rbHtml);
		
		//layout
		JPanel p=new JPanel();
		p.setLayout(new MigLayout());
		p.add(lbExport/*,"span,wrap"*/);
		p.add(rbTxt);p.add(rbCsv);p.add(rbXml);p.add(rbHtml,"wrap");
		p.add(lbExportParam,"span");
		return(p);
	}
	
	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applySettings();
				dispose();
			}
		};
	}
	
	@Override
	protected AbstractAction getCancelAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canceled = true;
				I18N.initMsgInternal(currentlocale);
				dispose();
			}
		};
	}

	private void applySettings() {
		SbApp app = SbApp.getInstance();
		SwingUtil.setWaitingCursor(this);
		String updater="1";
		if (rbUpdaterYear.isSelected()) updater="2";
		else if (rbUpdaterAuto.isSelected()) updater="3";
		mainFrame.getPref().setString(SbPref.Key.DO_UPDATER, updater);

		mainFrame.getPref().setBoolean(SbPref.Key.OPEN_LAST_FILE, ckOnStart.isSelected());
		mainFrame.getPref().setBoolean(SbPref.Key.CONFIRM_EXIT, ckOnExit.isSelected());

		String export="xml";
		if (rbTxt.isSelected()) export="txt";
		else if (rbCsv.isSelected()) export="csv";
		else if (rbHtml.isSelected()) export="html";
		mainFrame.getPref().setString(SbPref.Key.EXPORT_PREF, export);
		// language
		int i = cbLanguage.getSelectedIndex();
		SbConstants.Language lang = SbConstants.Language.values()[i];
		Locale locale = lang.getLocale();
		mainFrame.getPref().setString(SbPref.Key.LANG, I18N.getCountryLanguage(locale));
		mainFrame.getPref().setString(SbPref.Key.DATEFORMAT, cbDateFormat.getSelectedItem().toString());
		I18N.initMsgInternal(locale);
		app.setDefaultFont(font);
		app.setEditorFont(fontEditor);
		//default use Typist
		mainFrame.getPref().setBoolean(SbPref.Key.TYPIST_USE, ckTypist.isSelected());
		mainFrame.getPref().setBoolean(SbPref.Key.MEMORY, cbMemory.isSelected());
		app.refresh();
		if (inLang!=lang.ordinal()) app.refreshViews();
		SwingUtil.setDefaultCursor(this);
	}
	
	private void refreshUi() {
		cbMemory.setText(I18N.getMsg("preferences.memory"));
		lbCommon.setText(I18N.getMsg("general"));
		lbLanguage.setText(I18N.getMsg("language"));
		lbDateFormat.setText(I18N.getMsg("dateformat.label"));
		lbOnStart.setText(I18N.getMsg("preferences.start"));
        ckOnStart.setText(I18N.getMsg("preferences.start.openproject"));
		lbOnExit.setText(I18N.getMsg("preferences.exit"));
        ckOnExit.setText(I18N.getMsg("preferences.exit.chb"));
		lbUpdater.setText(I18N.getMsg("help.update"));
        rbUpdaterNever.setText(I18N.getMsg("preferences.updater.never"));
        rbUpdaterYear.setText(I18N.getMsg("preferences.updater.year"));
        rbUpdaterAuto.setText(I18N.getMsg("preferences.updater.auto"));
		lbAppearence.setText(I18N.getMsg("preferences.appearance"));
		lbFontGeneral.setText(I18N.getMsg("preferences.font.standard"));
		lbShowFontGeneral.setText(SwingUtil.getNiceFontName(font));
		lbFontEditor.setText(I18N.getMsg("preferences.editor.font"));
		lbShowFontEditor.setText(SwingUtil.getNiceFontName(fontEditor));
		ckTypist.setText(I18N.getMsg("typist.preference"));
		lbExport.setText(I18N.getMsg("preferences.export"));
		lbExportParam.setText(I18N.getMsg("preferences.export.parameters"));
        rbTxt.setText(I18N.getMsg("preferences.export.txt"));
        rbCsv.setText(I18N.getMsg("preferences.export.csv"));
        rbXml.setText(I18N.getMsg("preferences.export.xml"));
        rbHtml.setText(I18N.getMsg("preferences.export.html"));
	}

}
