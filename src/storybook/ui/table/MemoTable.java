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
import storybook.model.hbn.dao.MemoDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Memo;
import storybook.ui.MainFrame;
import storybook.ui.SbView;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class MemoTable extends AbstractTable {

	public MemoTable(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void init() {
		columns = SbColumnFactory.getInstance().getTagColumns();
		allowMultiDelete = true;
	}

	@Override
	protected void modelPropertyChangeLocal(PropertyChangeEvent evt) {
		try {
			String propName = evt.getPropertyName();
			if (BookController.TagProps.INIT.check(propName)) {
				initTableModel(evt);
			} else if (BookController.TagProps.UPDATE.check(propName)) {
				updateEntity(evt);
			} else if (BookController.TagProps.NEW.check(propName)) {
				newEntity(evt);
			} else if (BookController.TagProps.DELETE.check(propName)) {
				deleteEntity(evt);
			} else if (BookController.CommonProps.EXPORT.check(propName) 
				&& ((SbView)evt.getNewValue()).getName().equals("Memos")) {
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
		Memo memo = (Memo) getEntityFromRow(row);
//		ctrl.setTagToEdit(tag);
//		mainFrame.showView(ViewName.EDITOR);
		mainFrame.showEditorAsDialog(memo);
	}

	@Override
	protected void sendSetNewEntityToEdit(AbstractEntity entity) {
//		ctrl.setTagToEdit((Tag) entity);
//		mainFrame.showView(ViewName.EDITOR);
		mainFrame.showEditorAsDialog(entity);
	}

	@Override
	protected synchronized void sendDeleteEntity(int row) {
		Memo memo = (Memo) getEntityFromRow(row);
		ctrl.deleteMemo(memo);
	}

	@Override
	protected synchronized void sendDeleteEntities(int[] rows) {
		ArrayList<Long> ids = new ArrayList<>();
		for (int row : rows) {
			Memo memo = (Memo) getEntityFromRow(row);
			ids.add(memo.getId());
		}
		ctrl.deleteMultiMemos(ids);
	}

	@Override
	protected AbstractEntity getEntity(Long id) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		MemoDAOImpl dao = new MemoDAOImpl(session);
		Memo memo = dao.find(id);
		model.commit();
		return memo;
	}

	@Override
	protected AbstractEntity getNewEntity() {
		return new Memo();
	}

	@Override
	public String getTableName() {
		return("Memo");
	}
}
