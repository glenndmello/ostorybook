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
public class CSVpanel extends JPanel {
	
    public JRadioButton csvComma;
    public JRadioButton csvDoubleQuotes;
    public JRadioButton csvNoQuotes;
    public JRadioButton csvSemicolon;
    public JRadioButton csvSingleQuotes;

	public CSVpanel(ParamExport param) {
		ButtonGroup group1 = new ButtonGroup();
		ButtonGroup group2 = new ButtonGroup();
        csvSingleQuotes = new JRadioButton(I18N.getMsg("export.options.csv.quoted.single"));
        csvDoubleQuotes = new JRadioButton(I18N.getMsg("export.options.csv.quoted.double"));
        csvNoQuotes = new JRadioButton(I18N.getMsg("none"));
		if (null!=param.csvQuote) switch (param.csvQuote) {
			case "'":
				csvSingleQuotes.setSelected(true);
				break;
			case "\"":
				csvDoubleQuotes.setSelected(true);
				break;
			default:
				csvNoQuotes.setSelected(true);
				break;
		}
		group1.add(csvSingleQuotes);group1.add(csvDoubleQuotes);group1.add(csvNoQuotes);

		csvComma = new JRadioButton(I18N.getMsg("export.options.csv.separate.comma"));
        csvSemicolon = new JRadioButton(I18N.getMsg("export.options.csv.separate.semicolon"));
		if (";".equals(param.csvComma)) csvSemicolon.setSelected(true);
		else csvComma.setSelected(true);
		group2.add(csvComma);group2.add(csvSemicolon);

		//layout
		setLayout(new MigLayout("", "", ""));
		add(new JLabel("CSV "+ I18N.getMsg("export.options.csv.quoted")),"split 4");
		add(csvSingleQuotes);
		add(csvDoubleQuotes);
		add(csvNoQuotes,"wrap");
		add(new JLabel(I18N.getMsg("export.options.csv.separate")),"split 3");
		add(csvComma);
		add(csvSemicolon,"wrap");
	}
	
}
