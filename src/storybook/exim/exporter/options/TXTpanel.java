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

import java.awt.event.ItemEvent;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.miginfocom.swing.MigLayout;
import storybook.exim.exporter.ParamExport;
import storybook.i18n.I18N;

/**
 *
 * @author favdb
 */
public class TXTpanel extends JPanel {
    public JRadioButton rbOther;
    public JTextField txSeparator;
    public JRadioButton rbTab;
	
	public TXTpanel(ParamExport param) {
		ButtonGroup group1 = new ButtonGroup();
        rbTab = new JRadioButton("tab");
        rbOther = new JRadioButton(I18N.getMsg("other"));
        rbOther.addItemListener((java.awt.event.ItemEvent evt) -> {
			if (evt.getStateChange()==ItemEvent.SELECTED) {
				txSeparator.setVisible(true);
			} else {
				txSeparator.setVisible(false);
			}
		});
		rbTab.setSelected(param.txtTab);
		group1.add(rbTab); group1.add(rbOther);
        txSeparator = new JTextField();
		if (!param.txtTab) {
			txSeparator.setText(param.txtSeparator);
			txSeparator.setVisible(true);
		} else {
			txSeparator.setVisible(false);
		}

		//layout
		setLayout(new MigLayout("", "", ""));
		add(new JLabel("TXT "+I18N.getMsg("export.options.csv.separate")),"split 4");
		add(rbTab);
		add(rbOther);
		add(txSeparator);
	}
	
}
