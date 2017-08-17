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
package storybook.exim.exporter.options;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.miginfocom.swing.MigLayout;
import storybook.exim.exporter.ParamExport;
import storybook.i18n.I18N;

/**
 *
 * @author favdb
 */
public class BOOKpanel extends JPanel {
	private ButtonGroup buttonGroup1;
    public JCheckBox ckExportChapterDatesLocations;
    public JCheckBox ckExportChapterNumbers;
    public JCheckBox ckExportChapterNumbersRoman;
    public JCheckBox ckExportChapterTitles;
    public JCheckBox ckExportSceneSeparator;
    public JCheckBox ckExportSceneTitles;
    public JRadioButton htmlBookMultiFile;
    public JRadioButton htmlBookOneFile;

	public BOOKpanel(ParamExport param) {
		buttonGroup1 = new ButtonGroup();
		JLabel label = new JLabel(I18N.getMsg("export.book.htmloption"));
        htmlBookMultiFile = new JRadioButton();
        htmlBookOneFile = new JRadioButton();
        ckExportChapterNumbers = new JCheckBox(I18N.getMsg("export.chapter.numbers"));
        ckExportChapterNumbersRoman = new JCheckBox(I18N.getMsg("export.roman.numerals"));
        ckExportChapterTitles = new JCheckBox(I18N.getMsg("export.chapter.titles"));
        ckExportChapterDatesLocations = new JCheckBox(I18N.getMsg("export.chapter.dates.locations"));
        ckExportSceneTitles = new JCheckBox(I18N.getMsg("export.scene.titles"));
        ckExportSceneSeparator = new JCheckBox(I18N.getMsg("export.scene.separator"));
        htmlBookMultiFile.setText(I18N.getMsg("export.book.htmloption.multifile"));
        htmlBookOneFile.setText(I18N.getMsg("export.book.htmloption.onefile"));
		buttonGroup1.add(htmlBookMultiFile);
        buttonGroup1.add(htmlBookOneFile);

		if (param.htmlBookMulti) {
			htmlBookOneFile.setSelected(false);
			htmlBookMultiFile.setSelected(true);
		} else {
			htmlBookOneFile.setSelected(true);
			htmlBookMultiFile.setSelected(false);
		}
		ckExportChapterNumbers.setSelected(param.isExportChapterNumbers);
		ckExportChapterNumbersRoman.setSelected(param.isExportChapterNumbersRoman);
		ckExportChapterTitles.setSelected(param.isExportChapterTitles);
		ckExportChapterDatesLocations.setSelected(param.isExportChapterDatesLocs);
		ckExportSceneTitles.setSelected(param.isExportSceneTitles);
		ckExportSceneSeparator.setSelected(param.isExportSceneSeparator);

		//layout
		setLayout(new MigLayout("", "", ""));
		add(label,"split 3"); add(htmlBookMultiFile); add(htmlBookOneFile,"wrap");
		add(ckExportChapterNumbers,"wrap");
		add(ckExportChapterNumbersRoman,"wrap");
		add(ckExportChapterTitles,"wrap");
		add(ckExportChapterDatesLocations,"wrap");
		add(ckExportSceneTitles,"wrap");
		add(ckExportSceneSeparator,"wrap");
	}

}
