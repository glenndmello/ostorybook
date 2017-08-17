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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import org.miginfocom.swing.MigLayout;
import storybook.i18n.I18N;
import storybook.model.EntityUtil;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class ConfirmDeleteDlg extends AbstractDialog {
	private JTextPane tpEntity;
	private final List<AbstractEntity> entities;

	public ConfirmDeleteDlg(MainFrame parent, AbstractEntity entity) {
		super(parent);
		canceled = true;
		mainFrame = parent;
		this.entities=new ArrayList<>();
		entities.add(entity);
		initAll();
	}

	public ConfirmDeleteDlg(MainFrame parent, List<AbstractEntity> entities) {
		super(parent);
		canceled = true;
		mainFrame = parent;
		this.entities=entities;
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		JLabel lb = new JLabel(I18N.getMsg("ask.multi.delete"));
		JScrollPane scroller = new JScrollPane();
		//scroller.setMinimumSize(new Dimension(334, 348));
        tpEntity = new JTextPane();
        tpEntity.setEditable(false);
        tpEntity.setContentType("text/html");
		StringBuilder buf = new StringBuilder();
		//buf.append(HtmlUtil.getHeadWithCSS());
		for (AbstractEntity e : entities) {
			buf.append("<p style='margin-bottom:10px'>\n")
				.append(EntityUtil.getEntityFullTitle(e))
				.append("</p>\n")
				.append("<p style=''>\n")
				.append(EntityUtil.getDeleteInfo(mainFrame, e))
				.append("</p>\n")
				.append("<hr style='margin:10px'>\n");
		}
		tpEntity.setText(buf.toString());
		tpEntity.setCaretPosition(0);
        scroller.setViewportView(tpEntity);
		
		//layout
		setLayout(new MigLayout("", "", ""));
        setTitle(I18N.getMsg("warning"));
		add(lb,"wrap");add(scroller,"wrap");
		add(getCancelButton(),"split 2, right");add(getOkButton(), "right");
		pack();
		setLocationRelativeTo(mainFrame);
	}

}
