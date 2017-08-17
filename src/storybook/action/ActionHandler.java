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

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.Session;


import net.infonode.docking.View;
import storybook.SbApp;
import storybook.SbConstants.ViewName;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.entity.Part;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.RenameDlg;

/**
 * @author martin
 *
 */
public class ActionHandler {

	private final MainFrame mainFrame;

	public ActionHandler(MainFrame mainframe) {
		mainFrame = mainframe;
	}

	public void handleText2Html() {
		int n = SwingUtil.showBetaDialog(mainFrame);
		if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
			return;
		}
		mainFrame.setWaitingCursor();
		EntityUtil.convertPlainTextToHtml(mainFrame);
		mainFrame.refresh();
		mainFrame.setDefaultCursor();
	}

	public void handleHtml2Text() {
		int n = SwingUtil.showBetaDialog(mainFrame);
		if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
			return;
		}
		mainFrame.setWaitingCursor();
		EntityUtil.convertHtmlToPlainText(mainFrame);
		mainFrame.refresh();
		mainFrame.setDefaultCursor();
	}

	public void handleRenameCity() {
		RenameDlg dlg=new RenameDlg(mainFrame,"city");
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handleRenameCountry() {
		RenameDlg dlg=new RenameDlg(mainFrame,"country");
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handleRenameTagCategory() {
		RenameDlg dlg=new RenameDlg(mainFrame,"tag");
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handleRenameItemCategory() {
		RenameDlg dlg=new RenameDlg(mainFrame,"item");
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handlePreviousPart() {
		Part currentPart = mainFrame.getCurrentPart();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Part> parts = dao.findAll();
		int index = parts.indexOf(currentPart);
		if (index == 0) {
			// already first part
			return;
		}
		--index;
		handleChangePart(parts.get(index));
	}

	public void handleNextPart() {
		Part currentPart = mainFrame.getCurrentPart();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Part> parts = dao.findAll();
		int index = parts.indexOf(currentPart);
		if (index == parts.size() - 1) {
			// already last part
			return;
		}
		++index;
		handleChangePart(parts.get(index));
	}

	public void handleChangePart(Part part) {
		mainFrame.setWaitingCursor();
		Part currentPart = mainFrame.getCurrentPart();
		if (currentPart.getId().equals(part.getId())) {
			// same part
			return;
		}
		mainFrame.setCurrentPart(part);
		mainFrame.setTitle();
		mainFrame.getBookController().changePart(part);
		mainFrame.setDefaultCursor();
	}

	public void handleShowChronoView() {
		showAndFocus(ViewName.CHRONO);
	}

	public void handleShowStoryboardView() {
		showAndFocus(ViewName.STORYBOARD);
	}

	public void handleShowAttributesView() {
		showAndFocus(ViewName.ATTRIBUTES);
	}

	public void handleShowAttributesListView() {
		showAndFocus(ViewName.ATTRIBUTESLIST);
	}

	public void handleShowBookView() {
		showAndFocus(ViewName.BOOK);
	}

	public void handleShowManageView() {
		showAndFocus(ViewName.MANAGE);
	}

	public void handleShowReadingView() {
		showAndFocus(ViewName.READING);
	}

	public void handleShowMemoria() {
		showAndFocus(ViewName.MEMORIA);
	}

	public void handleShowStoryboard() {
		showAndFocus(ViewName.STORYBOARD);
	}

	public void handleShowTree() {
		showAndFocus(ViewName.TREE);
	}

	public void handleShowInfo() {
		showAndFocus(ViewName.INFO);
	}

	public void handleShowMemo() {
		showAndFocus(ViewName.MEMOS);
	}

	public void handleShowNavigation() {
		showAndFocus(ViewName.NAVIGATION);
	}

	public void handleDumpAttachedViews() {
		mainFrame.getBookController().printAttachedViews();
	}

	public void handleDummy() {
		SbApp.trace("ActionHandler.handleDummy(): ");
	}

	public void handleShowInternals() {
		showAndFocus(ViewName.INTERNALS);
	}

	public void handleShowScenes() {
		showAndFocus(ViewName.SCENES);
	}

	public void handleShowTags() {
		showAndFocus(ViewName.TAGS);
	}

	public void handleShowTagLinks() {
		showAndFocus(ViewName.TAGLINKS);
	}

	public void handleShowItems() {
		showAndFocus(ViewName.ITEMS);
	}

	public void handleShowItemLinks() {
		showAndFocus(ViewName.ITEMLINKS);
	}

	public void handleShowIdeas() {
		showAndFocus(ViewName.IDEAS);
	}

	public void handleShowStrands() {
		showAndFocus(ViewName.STRANDS);
	}

	public void handleShowCategories() {
		showAndFocus(ViewName.CATEGORIES);
	}

	public void handleShowGenders() {
		showAndFocus(ViewName.GENDERS);
	}

	public void handleShowPersons() {
		showAndFocus(ViewName.PERSONS);
	}

	public void handleShowLocations() {
		showAndFocus(ViewName.LOCATIONS);
	}

	public void handleShowChapters() {
		showAndFocus(ViewName.CHAPTERS);
	}

	public void handleShowParts() {
		showAndFocus(ViewName.PARTS);
	}

	private void showAndFocus(ViewName viewName) {
		View view = mainFrame.getView(viewName);
		view.restore();
		view.restoreFocus();
	}

	public void handleRecentClear() {
		SbApp.getInstance().clearRecentFiles();
	}

	public void handleClose() {
		mainFrame.close(true);
	}

	public void handleViewStatus(boolean selected) {
	}

}
