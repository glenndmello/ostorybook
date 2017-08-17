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
/* vérification OK */

package storybook.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.jopendocument.dom.OOUtils;

import storybook.SbApp;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Scene;
import storybook.i18n.I18N;
import storybook.toolkit.odt.ODTUtils;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class EditSceneXeditorAction extends AbstractEntityAction {

	public EditSceneXeditorAction(MainFrame mainFrame, AbstractEntity entity) {
		super(mainFrame, entity, I18N.getMsg("xeditor"), I18N
				.getIcon("icon.small.libreoffice"));
		SbApp.trace("EditSceneXeditorAction(" + mainFrame.getName() + ","
				+ entity.toString() + ")");
	}

	@Override
	@SuppressWarnings("UnusedAssignment")
	public void actionPerformed(ActionEvent e) {
		SbApp.trace("EditSceneXeditorAction.actionPerformed(...) entity="
				+ entity.toString());
		String name = ODTUtils.getFilePath(mainFrame, (Scene) entity);
		File file = new File(name);
		if (!file.exists()) {
			file=ODTUtils.createNewODT(mainFrame,name);
			if (file==null) return;
			((Scene) entity).setOdf(name);
		}
		try {
			OOUtils.open(file);
		} catch (IOException ex) {
			SbApp.error("execLibreOffice(mainFrame,...)", ex);
		}
	}
}
