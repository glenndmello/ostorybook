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

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import storybook.model.hbn.entity.Chapter;
import storybook.i18n.I18N;

@SuppressWarnings("serial")
public class ChapterLabel extends JLabel {

	private Chapter chapter;

	public ChapterLabel(Chapter chapter) {
		super();
		this.chapter = chapter;
		setText(chapter.getTitle());
		setToolTipText(chapter.getDescription());
		setIcon(I18N.getIcon("icon.small.chapter"));
		setBackground(new Color(240, 240, 240));
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.LEFT);
	}

	public final String getChapterText() {
		if (chapter == null) {
			return "";
		}
		return chapter.getTitle();
	}

	public Chapter getChapter() {
		return chapter;
	}

	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
	}
}
