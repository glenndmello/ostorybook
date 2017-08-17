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

package storybook.ui;

import storybook.ui.panel.AbstractPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;

import org.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import storybook.SbApp;
import storybook.SbPref;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.i18n.I18N;
import storybook.toolkit.odt.ODTUtils;
import storybook.toolkit.swing.panel.MemoryMonitorPanel;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class StatusBarPanel extends AbstractPanel implements ActionListener {

	private JLabel lbParts;
	private JLabel lbStat;
	private int nbWords;
	private int nbCharacters;
	private int nbPersons;
	private int nbChapters;
	private int nbScenes;

	//JComboBox layoutCombo = new JComboBox();

	public StatusBarPanel(MainFrame mainFrame) {
		SbApp.trace("StatusBarPanel(mainFrame)");
		this.mainFrame = mainFrame;
		initAll();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		SbApp.trace("StatusBarPanel.modelPropertyChange("+evt.toString()+")");
		String propName = evt.getPropertyName();

		if (BookController.PartProps.CHANGE.check(propName) || BookController.PartProps.UPDATE.check(propName)) {
			refresh();
			return;
		}

		if (BookController.CommonProps.REFRESH.check(propName)) {
			refresh();
			return;
		}
		
		if (BookController.ChapterProps.INIT.check(propName)
				|| BookController.ChapterProps.DELETE.check(propName)
				|| BookController.ChapterProps.DELETE_MULTI.check(propName)
				|| BookController.ChapterProps.NEW.check(propName)
				|| BookController.ChapterProps.UPDATE.check(propName)
				|| BookController.SceneProps.INIT.check(propName)
				|| BookController.SceneProps.DELETE.check(propName)
				|| BookController.SceneProps.DELETE_MULTI.check(propName)
				|| BookController.SceneProps.NEW.check(propName)
				|| BookController.SceneProps.UPDATE.check(propName)) {
			refreshStat();
//			return;
		}

	}

	@Override
	public void init() {
		SbApp.trace("StatusBarPanel.init()");
		computeStatistics();
	}
	
	private String geneLibStat() {
		String strStat=I18N.getMsg("statistics")+":";
		strStat+=" "+I18N.getMsg("chapters")+"="+nbChapters+", ";
		strStat+=" "+I18N.getMsg("scenes")+"="+nbScenes+", ";
		strStat+=" "+I18N.getMsg("persons")+"="+nbPersons+", ";
		strStat+=" "+I18N.getMsg("characters")+"="+nbCharacters+", ";
		strStat+=" "+I18N.getMsg("words")+"="+nbWords;
		return(strStat);
	}

	@Override
	public void initUi() {
		SbApp.trace("StatusBarPanel.initUi()");
		setLayout(new MigLayout("flowx,fill,ins 1", "[][grow][][]"));

		lbParts = new JLabel(" " + I18N.getColonMsg("part.current")
				+ " " + mainFrame.getCurrentPart().toString());
		add(lbParts);

		lbStat=new JLabel(geneLibStat());
		add(lbStat, "al center");
		MemoryMonitorPanel memPanel = new MemoryMonitorPanel();
		if (mainFrame.getPref().getBoolean(SbPref.Key.MEMORY, false)) add(memPanel, "al right");

		revalidate();
		repaint();
	}

	@SuppressWarnings("unchecked")
	private void refreshLayoutCombo() {
		/*
		layoutCombo.removeAllItems();
		layoutCombo.addItem("");
		PreferenceModel model = SbApp.getInstance().getPreferenceModel();
		Session session = model.beginTransaction();
		PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
		List<Preference> pref = dao.findAll();
		for (Preference preference : pref) {
			if (preference.getKey().startsWith(PreferenceKey.DOCKING_LAYOUT.toString())) {
				String name = preference.getStringValue();
				if (SbConstants.BookKey.LAST_USED_LAYOUT.toString().equals(name)) {
					continue;
				}
				LoadDockingLayoutAction act = new LoadDockingLayoutAction(mainFrame, name);
				layoutCombo.addItem(act);
			}
		}
		model.commit();
				*/
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SbApp.trace("StatusBarPanel.actionPerformed("+e.paramString()+")");
		lbStat.setText(geneLibStat());
	}
	
	private void computeStatistics() {
		SbApp.trace("StatusBarPanel.computeStatistics()");
		nbCharacters=0;
		nbWords=0;
		nbPersons=0;
		nbChapters=0;
		nbScenes=0;
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl chapterDao = new ChapterDAOImpl(session);
		nbChapters=chapterDao.count(null);
		PersonDAOImpl personDao = new PersonDAOImpl(session);
		nbPersons=personDao.count(null);
		SceneDAOImpl sceneDao = new SceneDAOImpl(session);
		nbScenes=sceneDao.count(null);
		session.close();
		nbWords=ODTUtils.getBookWords(mainFrame);
		nbCharacters=ODTUtils.getBookSize(mainFrame);
	}

	private void refreshStat() {
		SbApp.trace("StatusBarPanel.refreshStat()");
		computeStatistics();
		lbStat.setText(geneLibStat());
		revalidate();
		repaint();
		SbApp.trace(geneLibStat());
	}
}
