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
import java.util.List;

import org.hibernate.Session;

import storybook.SbApp;
import storybook.SbConstants.ClientPropertyName;
import storybook.controller.BookController;
import storybook.exim.exporter.TableExporter;
import storybook.model.BookModel;
import storybook.model.hbn.dao.TimeEventDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.TimeEvent;
import storybook.ui.MainFrame;
import storybook.ui.SbView;

/**
 * @author martin
 *
 */

@SuppressWarnings("serial")
public class TimeEventTable extends AbstractTable {

	public TimeEventTable(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void init() {
		columns = SbColumnFactory.getInstance().getTimeEventColumns();
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public List<AbstractEntity> getAllEntities() {
		SbApp.trace("getAllEntities()");
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TimeEventDAOImpl dao = new TimeEventDAOImpl(session);
		List<?> ret;
		ret = dao.findAll();
		model.commit();
		return (List<AbstractEntity>) ret;
	}
	
	@Override
	protected void initTableModel(PropertyChangeEvent evt) {
		SbApp.trace("AbstractTable.initTableModel(evt)");
		table.putClientProperty(ClientPropertyName.MAIN_FRAME.toString(), mainFrame);
		for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		try {
			List<AbstractEntity> entities = getAllEntities();

			for (AbstractEntity entity : entities) {
				List<Object> cols = getRow(entity);
				tableModel.addRow(cols.toArray());
			}
		} catch (ClassCastException e) {
		}
		table.packAll();
	}

	@Override
	protected void modelPropertyChangeLocal(PropertyChangeEvent evt) {
		try {
			String propName = evt.getPropertyName();
			if (BookController.TimeEventProps.INIT.check(propName)) {
				initTableModel(evt);
			} else if (BookController.TimeEventProps.UPDATE.check(propName)) {
				updateEntity(evt);
			} else if (BookController.TimeEventProps.NEW.check(propName)) {
				newEntity(evt);
			} else if (BookController.TimeEventProps.DELETE.check(propName)) {
				deleteEntity(evt);
			} else if (BookController.CommonProps.EXPORT.check(propName) 
				&& ((SbView)evt.getNewValue()).getName().equals("TimeEvents")) {
				TableExporter.exportTable(mainFrame,(SbView)evt.getNewValue());
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void sendSetEntityToEdit(int row) {
		SbApp.trace("TimeEventTable.sendSetEntityToEdit("+row+")");
		if (row == -1) {
			return;
		}
		TimeEvent event = (TimeEvent) getEntityFromRow(row);
		mainFrame.showEditorAsDialog(event);
	}

	@Override
	protected void sendSetNewEntityToEdit(AbstractEntity entity) {
		mainFrame.showEditorAsDialog(entity);
	}

	@Override
	protected synchronized void sendDeleteEntity(int row) {
		TimeEvent scene = (TimeEvent) getEntityFromRow(row);
		ctrl.deleteTimeEvent(scene);
	}

	@Override
	protected synchronized void sendDeleteEntities(int[] rows) {
		for (int row : rows) {
			TimeEvent scene = (TimeEvent) getEntityFromRow(row);
			ctrl.deleteTimeEvent(scene);
		}
	}

	@Override
	protected AbstractEntity getEntity(Long id) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TimeEventDAOImpl dao = new TimeEventDAOImpl(session);
		TimeEvent scene = dao.find(id);
		model.commit();
		return scene;
	}

	@Override
	protected AbstractEntity getNewEntity() {
		return new TimeEvent();
	}

	@Override
	public String getTableName() {
		return("TimeEvent");
	}
}
