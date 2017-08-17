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

package storybook.ui.panel.reading;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.infonode.docking.View;
import org.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.exim.exporter.BookExporter;
import static storybook.exim.exporter.BookExporter.*;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Part;
import storybook.toolkit.BookUtil;
import storybook.toolkit.ViewUtil;
import storybook.toolkit.html.HtmlUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;
import storybook.ui.options.OptionsDlg;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ReadingPanel extends AbstractPanel implements HyperlinkListener {

	private JTextPane tpText;
	private JScrollPane scroller;
	private StrandPanel strandPanel;

	private int scrollerWidth;
	private int fontSize;

	public ReadingPanel(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		Object newValue = evt.getNewValue();

		if (BookController.SceneProps.INIT.check(propName)) {
			super.refresh();
			return;
		}

		if (BookController.CommonProps.REFRESH.check(propName)) {
			View newView = (View) evt.getNewValue();
			View view = (View) getParent().getParent();
			if (view == newView) {
				// super.refresh();
				refresh();
			}
			return;
		}

		if (BookController.ChapterProps.INIT.check(propName)
				|| BookController.ChapterProps.UPDATE.check(propName)
				|| BookController.SceneProps.UPDATE.check(propName)) {
			refresh();
			return;
		}

		if (BookController.CommonProps.SHOW_OPTIONS.check(propName)) {
			View view = (View) evt.getNewValue();
			if (!view.getName().equals(ViewName.READING.toString())) {
				return;
			}
			OptionsDlg.show(mainFrame, view.getName());
			return;
		}

		if (BookController.ReadingViewProps.ZOOM.check(propName)) {
			setZoomedSize((Integer) newValue);
			scroller.setMaximumSize(new Dimension(scrollerWidth, 10000));
			scroller.getParent().invalidate();
			scroller.getParent().validate();
			scroller.getParent().repaint();
			return;
		}

		if (BookController.ReadingViewProps.FONT_SIZE.check(propName)) {
			setFontSize((Integer) newValue);
			refresh();
			return;
		}

		if (BookController.PartProps.CHANGE.check(propName)) {
			ViewUtil.scrollToTop(scroller);
			// super.refresh();
			refresh();
			return;
		}


		dispatchToStrandPanels(this, evt);

		if (BookController.StrandProps.UPDATE.check(propName)) {
			refresh();
//			return;
		}
	}

	private void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	private void setZoomedSize(int zoomValue) {
		scrollerWidth = zoomValue * 10;
	}

	@Override
	public void init() {
		strandPanel = new StrandPanel(mainFrame, this);
		strandPanel.init();

		try {
			Internal internal = BookUtil.get(mainFrame,
					BookKey.READING_ZOOM, SbConstants.DEFAULT_READINGZOOM);
			setZoomedSize(internal.getIntegerValue());
			internal = BookUtil.get(mainFrame,
					BookKey.READING_FONT_SIZE,
					SbConstants.DEFAULT_READINGFONTSIZE);
			setFontSize(internal.getIntegerValue());
		} catch (Exception e) {
			setZoomedSize(SbConstants.DEFAULT_READINGZOOM);
			setFontSize(SbConstants.DEFAULT_READINGFONTSIZE);
		}
	}

	@Override
	public void initUi() {
		MigLayout layout = new MigLayout(
				"flowx",
				"[][fill,grow]", // columns
				"" // rows
				);
		setLayout(layout);

		strandPanel.initUi();

		tpText = new JTextPane();
		tpText.setEditable(false);
		tpText.setContentType("text/html");
		tpText.addHyperlinkListener(this);

		scroller = new JScrollPane(tpText);
		scroller.setMaximumSize(new Dimension(scrollerWidth, Short.MAX_VALUE));
		SwingUtil.setMaxPreferredSize(scroller);

		// layout
		add(strandPanel, "aligny top");
		add(scroller, "growy");
	}

	@Override
	public void refresh() {
		Part part = mainFrame.getCurrentPart();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		model.commit();

		StringBuilder buf = new StringBuilder();
		buf.append(HtmlUtil.getHeadWithCSS(mainFrame.getFont()));

		// table of contents
		buf.append("<body>\n<p style='font-weight:bold'>");
		// content
		buf.append(BookExporter.toPanel(mainFrame,strandPanel.getStrandIds(),ONLY_CURRENT_PART,TOC_LINKS));
		buf.append("<p>&nbsp;</p></body></html>\n");

		final int pos = scroller.getVerticalScrollBar().getValue();
		tpText.setText(buf.toString());
		final Action restoreAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scroller.getVerticalScrollBar().setValue(pos);
			}
		};
		SwingUtilities.invokeLater(() -> {
			restoreAction.actionPerformed(null);
		});
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				if (!evt.getDescription().isEmpty()) {
					// anchor
					tpText.scrollToReference(evt.getDescription().substring(1));
				} else {
					// external links
					tpText.setPage(evt.getURL());
				}
			} catch (IOException e) {
			}
		}
	}

	private static void dispatchToStrandPanels(Container cont,
			PropertyChangeEvent evt) {
		List<Component> ret = new ArrayList<>();
		SwingUtil.findComponentsByClass(cont, StrandPanel.class, ret);
		for (Component comp : ret) {
			StrandPanel panel = (StrandPanel) comp;
			panel.modelPropertyChange(evt);
		}
	}
}
