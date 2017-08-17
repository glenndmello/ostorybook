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
package storybook.exim.exporter;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import org.miginfocom.swing.MigLayout;
import storybook.SbApp;

import storybook.exim.exporter.options.BOOKpanel;
import storybook.exim.exporter.options.CSVpanel;
import storybook.exim.exporter.options.HTMLpanel;
import storybook.exim.exporter.options.TXTpanel;
import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;

/**
 *
 * @author favdb
 */
public class ExportOptionsDlg extends AbstractDialog {

	private final ParamExport param;
	private JTabbedPane tabbed;
	private BOOKpanel BOOK;
	private CSVpanel CSV;
	private TXTpanel TXT;
	private HTMLpanel HTML;
	
	public ExportOptionsDlg(MainFrame parent) {
		super(parent);
		this.param=new ParamExport(mainFrame);
		initAll();
	}


	@Override
	public void init() {
	}
	
	@Override
	public void initUi() {
        tabbed = new JTabbedPane();
		BOOK=new BOOKpanel(param);
		tabbed.add(BOOK,I18N.getMsg("export.book.text"));
		CSV=new CSVpanel(param);
		tabbed.add(CSV,"CSV");
		TXT=new TXTpanel(param);
		tabbed.add(TXT,"TXT");
		HTML=new HTMLpanel(param);
		tabbed.add(HTML,"HTML");

        //layout
		setLayout(new MigLayout(""));
        setTitle(I18N.getMsg("export.options"));
		add(tabbed,"wrap");
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
				if (null!=param.csvQuote) switch (param.csvQuote) {
					case "'": CSV.csvSingleQuotes.isSelected(); break;
					case "\"": CSV.csvDoubleQuotes.isSelected(); break;
					default: CSV.csvNoQuotes.isSelected(); break;
				}
				if (";".equals(param.csvComma)) CSV.csvComma.isSelected();
				param.txtTab=TXT.rbTab.isSelected();
				if (!param.txtTab) param.txtSeparator=TXT.txSeparator.getText();
				param.htmlCssUse=HTML.cbUseCss.isSelected();
				if (param.htmlCssUse) param.htmlCssFile=HTML.txCssFile.getText();
				param.htmlNavImage=HTML.cbNavImage.isSelected();
				param.isExportChapterNumbers=BOOK.ckExportChapterNumbers.isSelected();
				param.isExportChapterNumbersRoman=BOOK.ckExportChapterNumbersRoman.isSelected();
				param.isExportChapterTitles=BOOK.ckExportChapterTitles.isSelected();
				param.isExportSceneTitles=BOOK.ckExportSceneTitles.isSelected();
				param.isExportSceneSeparator=BOOK.ckExportSceneSeparator.isSelected();
				param.htmlBookMulti=BOOK.htmlBookMultiFile.isSelected();
				param.isExportChapterBreakPage=HTML.ckExportChapterBreakPage.isSelected();
				param.save();
				dispose();
				SbApp.getInstance().refresh();
			}
		};
	}

	public static void show(MainFrame m) {
		SwingUtil.showDialog(new ExportOptionsDlg(m), m, true);
	}

}
