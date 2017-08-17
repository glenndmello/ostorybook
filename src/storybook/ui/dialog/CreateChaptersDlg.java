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
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import jdatechooser.calendar.JDateChooser;
import org.miginfocom.swing.MigLayout;
import storybook.SbPref;
import storybook.controller.BookController;
import storybook.i18n.I18N;
import storybook.model.EntityUtil;
import storybook.model.handler.ChapterEntityHandler;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class CreateChaptersDlg extends AbstractDialog {

	private JDateChooser dateChooser;
	private JTextField tfQuantity;
	private JComboBox<Object> partCombo;
	private JTextField tfSize;

	public CreateChaptersDlg(MainFrame parent) {
		super(parent);
		mainFrame = parent;
		initUi();
		this.setLocationRelativeTo(mainFrame);
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		JLabel lb1 = new JLabel(I18N.getMsg("chapters.generate.text"));
		tfQuantity = new JTextField();
		tfQuantity.setColumns(2);

		JLabel lb2 = new JLabel(I18N.getMsg("part"));
		partCombo = new JComboBox<>();
		Part part = new Part();
		EntityUtil.fillPartCombo(mainFrame, partCombo, part, false, false);

		JLabel lb3 = new JLabel(I18N.getMsg("manage.size.objective"));
		tfSize = new JTextField();
		tfSize.setColumns(8);

		JLabel lb4 = new JLabel(I18N.getMsg("manage.date.objective"));
		dateChooser = new JDateChooser();
		dateChooser.setDateFormatString(mainFrame.getPref().getString(SbPref.Key.DATEFORMAT, "MM-dd-yyyy"));

		//layout
		setLayout(new MigLayout("", "", ""));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(I18N.getMsg("chapters.generate"));
		add(lb1, "split 2");
		add(tfQuantity, "wrap");
		add(lb2, "split 2");
		add(partCombo, "wrap");
		add(lb3, "split 2");
		add(tfSize, "wrap");
		add(lb4, "split 2");
		add(dateChooser, "wrap");

		add(getCancelButton(), "split 2, right");
		add(getOkButton(), "right");
		pack();
		setLocationRelativeTo(mainFrame);
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int quant = 0;
				try {
					quant = Integer.parseInt(tfQuantity.getText());
				} catch (NumberFormatException evt) {
					// ignore
				}
				if (quant < 1 || quant > 20) {
					return;
				}
				int size = 0;
				try {
					size = Integer.parseInt(tfSize.getText());
				} catch (NumberFormatException evt) {
					// ignore
				}
				Timestamp xdate = getTimestamp();
				ChapterEntityHandler handler = new ChapterEntityHandler(mainFrame);
				Part part = (Part) partCombo.getSelectedItem();
				for (int i = 0; i < quant; ++i) {
					Chapter ch = (Chapter) handler.createNewEntity();
					ch.setPart(part);
					if (size != 0) {
						ch.setObjectiveChars(size);
					}
					if (xdate != null) {
						ch.setObjectiveTime(xdate);
					}
					BookController ctrl = mainFrame.getBookController();
					ctrl.newChapter(ch);
				}
				canceled = false;
				dispose();
			}
		};
	}

	public Timestamp getTimestamp() {
		if (dateChooser.getDate() == null) {
			return null;
		}
		Date date = dateChooser.getDate();
		return(new Timestamp(date.getTime()));
	}
}
