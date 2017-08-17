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

package storybook.ui.table;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.hibernate.Session;
import storybook.SbApp;
import storybook.controller.BookController;
import storybook.exim.exporter.TableExporter;
import storybook.model.BookModel;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Part;
import storybook.ui.MainFrame;
import storybook.ui.SbView;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class PartTable extends AbstractTable {

	public PartTable(MainFrame mainFrame) {
		super(mainFrame);
		allowMultiDelete = false;
	}

	@Override
	public void init() {
		columns = SbColumnFactory.getInstance().getPartColumns();
	}

	@Override
	protected void modelPropertyChangeLocal(PropertyChangeEvent evt) {
		try {
			String propName = evt.getPropertyName();
			if (BookController.PartProps.INIT.check(propName)) {
				initTableModel(evt);
			} else if (BookController.PartProps.UPDATE.check(propName)) {
				updateEntity(evt);
				mainFrame.getSbActionManager().reloadMenuToolbar();
			} else if (BookController.PartProps.NEW.check(propName)) {
				newEntity(evt);
				mainFrame.getSbActionManager().reloadMenuToolbar();
			} else if (BookController.PartProps.DELETE.check(propName)) {
				deleteEntity(evt);
				mainFrame.getSbActionManager().reloadMenuToolbar();
			} else if (BookController.CommonProps.EXPORT.check(propName) 
				&& ((SbView)evt.getNewValue()).getName().equals("Parts")) {
				TableExporter.exportTable(mainFrame,(SbView)evt.getNewValue());
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void sendSetEntityToEdit(int row) {
		if (row == -1) {
			return;
		}
		Part part = (Part) getEntityFromRow(row);
//		ctrl.setPartToEdit(part);
//		mainFrame.showView(ViewName.EDITOR);
		mainFrame.showEditorAsDialog(part);
		mainFrame.getSbActionManager().reloadMenuToolbar();
	}

	@Override
	protected void sendSetNewEntityToEdit(AbstractEntity entity) {
//		ctrl.setPartToEdit((Part) entity);
//		mainFrame.showView(ViewName.EDITOR);
		mainFrame.showEditorAsDialog(entity);
		mainFrame.getSbActionManager().reloadMenuToolbar();
	}

	@Override
	protected synchronized void sendDeleteEntity(int row) {
		Part part = (Part) getEntityFromRow(row);
		ctrl.deletePart(part);
		mainFrame.getSbActionManager().reloadMenuToolbar();
	}

	@Override
	protected synchronized void sendDeleteEntities(int[] rows) {
		ArrayList<Long> ids = new ArrayList<>();
		for (int row : rows) {
			Part part = (Part) getEntityFromRow(row);
			ids.add(part.getId());
		}
		ctrl.deleteMultiParts(ids);
	}

	@Override
	protected AbstractEntity getEntity(Long id) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		Part part = dao.find(id);
		model.commit();
		return part;
	}

	@Override
	protected AbstractEntity getNewEntity() {
		return new Part();
	}

	@Override
	public String getTableName() {
		return("Part");
	}
}
