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

import java.util.List;

import javax.swing.JPanel;

import org.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class StoryboardRowPanel extends AbstractChapterPanel {

	public StoryboardRowPanel(MainFrame mainFrame, Chapter chapter) {
		super(mainFrame, chapter);
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		try {
			MigLayout layout = new MigLayout("insets 0 0 0 0", "[]", "[]");
			setLayout(layout);
			setOpaque(false);

			// date
			StrandChapterLabel lbChapter = new StrandChapterLabel(chapter);
			add(lbChapter, "wrap");

			// scenes by chapter
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			SceneDAOImpl sceneDao = new SceneDAOImpl(session);
			List<Scene> sceneList = sceneDao.findByChapter(chapter);
			model.commit();
			if (sceneList.isEmpty()) {
				StoryboardSpacePanel spacePanel = new StoryboardSpacePanel(mainFrame, chapter);
				add(spacePanel, "grow");
			} else {
				MigLayout layout2 = new MigLayout("insets 0 0 0 0", "[]", "[]");
				JPanel colPanel = new JPanel(layout2);
				colPanel.setOpaque(false);
				for (Scene scene : sceneList) {
					StoryboardScenePanel csp = new StoryboardScenePanel(mainFrame, scene);
					colPanel.add(csp);
				}
				add(colPanel);
			}
		} catch (Exception e) {
		}
	}
}
