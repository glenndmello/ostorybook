/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.toolkit.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import storybook.ui.dialog.ExceptionDlg;

public class FlashThread extends Thread implements ActionListener {

	private JComponent comp;
	private JLabel lb;
	Color fg, bg;
	int cpt=0;

	private boolean remove = false;

	private static final String CN_FLASH_LABEL = "FLASH";

	public FlashThread(JComponent comp) {
		this.comp = comp;
		remove=false;
	}

	public FlashThread(JComponent comp, boolean remove) {
		this.comp = comp;
		this.remove = remove;
	}

	public FlashThread(JComponent comp, Color color) {
		this.comp = comp;
		this.remove = true;
		bg=color;
	}

	@Override
	public void run() {
		try {
			if (remove) {
				
			} else {
				//lb = new JLabel(I18N.getIcon("icon.medium.target"));
				//lb.setName(CN_FLASH_LABEL);
				//Dimension dim = comp.getSize();
				//comp.add(lb, "pos " + dim.width / 2 + " " + dim.height / 2);
				//comp.setComponentZOrder(lb, 0);
				bg=comp.getBackground();
				comp.setBackground(Color.red);
				comp.validate();
			}
		} catch (Exception e) {
			ExceptionDlg.show("",e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			/*Component lb = SwingUtil.findComponentByName(comp, CN_FLASH_LABEL);
			if (lb == null) {
				return;
			}*/
			cpt++;
			if (cpt>15) {
				comp.setBackground(bg);
				comp.repaint();
				SwingUtil.flashEnded();
				return;
			}
			if (comp.getBackground()==Color.red) comp.setBackground(bg);
			else comp.setBackground(Color.red);
			//comp.remove(lb);
			comp.repaint();
			//SwingUtil.flashEnded();
		} catch (Exception e) {
			ExceptionDlg.show("",e);
		}
	}

}
