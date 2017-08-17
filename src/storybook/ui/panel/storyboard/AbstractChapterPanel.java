/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2016 FaVdB

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

package storybook.ui.panel.storyboard;

import java.beans.PropertyChangeEvent;
import org.hibernate.Session;

import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Scene;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public abstract class AbstractChapterPanel extends AbstractPanel {

	protected Chapter chapter;

	public AbstractChapterPanel(MainFrame mainFrame, Chapter chapter) {
		super(mainFrame);
		this.chapter = chapter;
		init();
		initUi();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (BookController.SceneProps.NEW.check(propName) || BookController.SceneProps.DELETE.check(propName)) {
			refresh();
			if (getParent() != null) {
				getParent().validate();
			} else {
				validate();
			}
		}
	}

	public Chapter getChapter() {
		return chapter;
	}
	
	public Scene getScene(Long id) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl sceneDao = new SceneDAOImpl(session);
		Scene scene = sceneDao.find(id);
		model.commit();
		return(scene);
	}
}
