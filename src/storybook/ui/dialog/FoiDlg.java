/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun

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

package storybook.ui.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JLabel;

import org.miginfocom.swing.MigLayout;
import storybook.model.hbn.entity.Idea;
import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.htmleditor.HtmlEditor;
import storybook.ui.MainFrame;

/**
 * @author martin
 * 
 */
@SuppressWarnings("serial")
public class FoiDlg extends AbstractDialog {

	private HtmlEditor ideaEditor;

	public FoiDlg(MainFrame mainFrame) {
		super(mainFrame);
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		super.initUi();
		setLayout(new MigLayout("wrap,fill", "", ""));
		setTitle(I18N.getMsg("foi.title"));
		setIconImage(I18N.getIconImage("icon.small.bulb"));

		JLabel lbTitle = new JLabel(I18N.getColonMsg("foi.new"));

		ideaEditor = new HtmlEditor(mainFrame,HtmlEditor.HTMLshow,HtmlEditor.TWOline);

		// layout
		add(lbTitle);
		add(ideaEditor, "grow");
		add(getOkButton(), "split 2,sg,right");
		add(getCancelButton(), "sg");
		pack();
		setLocationRelativeTo(mainFrame);
	}

	public String getText() {
		return ideaEditor.getText();
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canceled = false;
				dispose();
				Idea idea = new Idea();
				idea.setStatus(0);
				idea.setCategory(I18N.getMsg("foi.title"));
				idea.setNotes(ideaEditor.getText());
				mainFrame.getBookController().newIdea(idea);
			}
		};
	}
	
	public static void show(MainFrame m) {
		SwingUtil.showModalDialog(new FoiDlg(m), m,true);
	}
}