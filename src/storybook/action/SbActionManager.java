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
package storybook.action;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;

import org.hibernate.Session;
import storybook.SbConstants;
import storybook.SbApp;
import storybook.controller.BookController;
import storybook.model.DbFile;
import storybook.model.BookModel;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.entity.Part;
import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;

import com.sun.jaf.ui.ActionManager;
import java.io.File;
import javax.swing.JCheckBoxMenuItem;
import storybook.toolkit.BookUtil;
import storybook.toolkit.EnvUtil;
import storybook.ui.SbMenu;

/**
 * @author martin
 *
 */
public class SbActionManager implements PropertyChangeListener {

	private ActionHandler actionHandler;
	private ActionManager actionManager;
	private final MainFrame mainFrame;
	private SbMenu mainMenu;

	public SbActionManager(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public SbActionManager(MainFrame mainFrame,boolean withInit) {
		this.mainFrame = mainFrame;
		init();
	}

	public void init() {
		SbApp.trace("SbActionManager.init()");
		initActions();
		initUiFactory();
		if (mainFrame.isBlank()) {
			mainMenu.setMenuForBlank();
		}
	}

	private void initActions() {
		SbApp.trace("SbActionManager.initActions()");
		actionManager = new ActionManager();
		ActionManager.setInstance(actionManager);
		registerActions();
	}

	private void registerActions() {
		SbApp.trace("SbActionManager.registerActions()");
		actionHandler = new ActionHandler(mainFrame);
		actionManager.registerCallback("save-command", actionHandler, "handleSave");
	}

	private void initUiFactory() {
		SbApp.trace("SbActionManager.initUiFactory()");
		mainMenu=new SbMenu(mainFrame);
		JMenuBar menubar = mainMenu.menuBar;
		if (menubar != null) {
			reloadRecentMenu(menubar);
			reloadPartMenu(menubar);
			reloadWindowMenu(menubar);
			mainFrame.setJMenuBar(menubar);
		} else {
			System.err.println("General error : unable to load main menu");
		}
		//JToolBar toolBar = factory.createToolBar("main-toolbar");
		JToolBar toolBar=mainMenu.toolBar;
		if (toolBar != null) {
			toolBar.setName(SbConstants.ComponentName.TB_MAIN.toString());
			mainFrame.setMainToolBar(toolBar);
		}

		mainFrame.invalidate();
		mainFrame.validate();
		mainFrame.pack();
		mainFrame.repaint();
	}

	private void reloadWindowMenu(JMenuBar menubar) {
		SbApp.trace("SbActionManager.reloadWindowMenu(" + menubar.getName() + ")");
		JMenu miLoad=mainMenu.windowLoadLayout;
		miLoad.removeAll();
		File dir=new File(EnvUtil.getPrefDir().getAbsolutePath());
		File[] files=dir.listFiles();
		if (files==null || files.length<1) {
			SbApp.trace("Pref dir is empty");
			return;
		}
		for (File file : files) {
			if (file.isFile()) {
				String name=file.getName();
				if (name.endsWith(".layout")) {
					String str=name.substring(0,name.lastIndexOf(".layout"));
					if (str.equals("_internal_last_used_layout_")
						||str.equals("LastUsedLayout")) continue;
					LoadDockingLayoutAction act = new LoadDockingLayoutAction(mainFrame, str);
					JMenuItem item = new JMenuItem(act);
					miLoad.add(item);
				}
			}
		}
	}

	private void reloadRecentMenu(JMenuBar menubar) {
		SbApp.trace("SbActionManager.reloadRecentMenu(" + menubar.getName() + ")");
		JMenu miRecent=mainMenu.fileOpenRecent;
		miRecent.removeAll();
		List<DbFile> list = SbApp.getInstance().preferences.getDbFileList();
		for (DbFile dbFile : list) {
			OpenFileAction act = new OpenFileAction(dbFile.getName(), dbFile);
			JMenuItem item = new JMenuItem(act);
			miRecent.add(item);
		}
		miRecent.addSeparator();
		JMenuItem item = new JMenuItem(new ClearRecentFilesAction(actionHandler));
		miRecent.add(item);
	}

	private void reloadPartMenu(JMenuBar menubar) {
		SbApp.trace("SbActionManager.reloadPartMenu(" + menubar.getName() + ")");
		BookModel model = mainFrame.getBookModel();
		if (model == null) {
			SbApp.trace("SbActionManager.reloadPartMenu==>model is null");
			return;
		}
		JMenu menu=mainMenu.menuParts;
		if (!mainFrame.showAllParts) {
			menu.setVisible(true);
			JMenuItem miPreviousPart=mainMenu.partPrevious;
			JMenuItem miNextPart=mainMenu.partNext;
			JCheckBoxMenuItem allParts=mainMenu.allParts;
			Part currentPart = mainFrame.getCurrentPart();
			Session session = model.beginTransaction();
			PartDAOImpl dao = new PartDAOImpl(session);
			List<Part> parts = dao.findAll();
			model.commit();
			menu.removeAll();
			int pos = 0;
			ButtonGroup group = new ButtonGroup();
			for (Part part : parts) {
				Action action = new ChangePartAction(I18N.getMsg("part") + " " + part.getNumberName(), actionHandler, part);
				JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem(action);
				SwingUtil.setAccelerator(rbmi, KeyEvent.VK_0 + part.getNumber(), Event.ALT_MASK);
				if (currentPart.getId().equals(part.getId()))
					rbmi.setSelected(true);
				group.add(rbmi);
				menu.insert(rbmi, pos);
				++pos;
			}
			menu.insertSeparator(pos++);
			menu.insert(miPreviousPart, pos++);
			menu.insert(miNextPart, pos++);
			menu.insertSeparator(pos++);
			if (parts.size()>1) {
				menu.insert(allParts, pos++);
				mainMenu.btPreviousPart.setEnabled(true);
				mainMenu.btNextPart.setEnabled(true);
			} else {
				mainMenu.btPreviousPart.setEnabled(false);
				mainMenu.btNextPart.setEnabled(false);
			}
		}
	}

	private void selectPartMenu(JMenuBar menubar) {
		SbApp.trace("SbActionManager.selectPartMenu(" + menubar.getName() + ")");
		JMenu menu=mainMenu.menuParts;
		Component[] comps = menu.getMenuComponents();
		for (Component comp : comps) {
			if (comp instanceof JRadioButtonMenuItem) {
				JRadioButtonMenuItem rbmi = (JRadioButtonMenuItem) comp;
				ChangePartAction action = (ChangePartAction) rbmi.getAction();
				if (action.getPart().getId().equals(mainFrame.getCurrentPart().getId())) {
					rbmi.setSelected(true);
					return;
				}
			}
		}
	}

	public void reloadMenuToolbar() {
		init();
	}

	public ActionHandler getActionController() {
		SbApp.trace("SbActionManager.getActionController()");
		return actionHandler;
	}

	public ActionManager getActionManager() {
		SbApp.trace("SbActionManager.getActionManager()");
		return actionManager;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		SbApp.trace("SbActionManager.propertyChange(" + evt.getPropertyName() + "::" + evt.getNewValue() + ")");
		String propName = evt.getPropertyName();
		if (BookController.PartProps.NEW.check(propName)
				|| BookController.PartProps.UPDATE.check(propName)
				|| BookController.PartProps.DELETE.check(propName)) {
			reloadMenuToolbar();
			return;
		}
		if (BookController.PartProps.CHANGE.check(propName))
			selectPartMenu(mainFrame.getJMenuBar()); //return;
	}

	public ActionHandler getActionHandler() {
		SbApp.trace("SbActionManager.getActionHandler()");
		return actionHandler;
	}
	
	public SbMenu getMainMenu() {
		return(mainMenu);
	}

}
