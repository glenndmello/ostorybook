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
package storybook.toolkit;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.hibernate.Criteria;
import org.hibernate.Query;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import storybook.SbApp;

import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.SbPref;
import storybook.i18n.I18N;
import storybook.model.DbFile;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.InternalDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Scene;
import storybook.ui.MainFrame;
import storybook.ui.dialog.ExceptionDlg;

/**
 * @author martin
 *
 */
public class BookUtil {

	public static boolean isUseSimpleTemplate(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.USE_SIMPLETEMPLATE,
			SbConstants.DEFAULT_USE_SIMPLE_TEMPLATE);
		return internal.getBooleanValue();
	}

	public static boolean isUseLibreOffice(MainFrame mainFrame) {
		if (!isUseXeditor(mainFrame)) {
			return (false);
		}
		Internal internal = get(mainFrame,
			BookKey.XEDITOR_EXTENSION,
			"");
		return internal.getStringValue().equals("odt");
	}

	public static boolean isUseXeditor(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.XEDITOR_USE,
			SbConstants.DEFAULT_XEDITOR_USE);
		return internal.getBooleanValue();
	}

	public static String getXeditorTemplate(MainFrame mainFrame) {
		Internal internal = get(mainFrame, BookKey.XEDITOR_TEMPLATE, "");
		return internal.getStringValue();
	}

	public static String getXeditorExtension(MainFrame mainFrame) {
		Internal internal = get(mainFrame, BookKey.XEDITOR_EXTENSION, "");
		return internal.getStringValue();
	}

	public static boolean isUseNoTemplate(MainFrame mainFrame) {
		Internal internal = get(mainFrame, BookKey.USE_NO_TEMPLATE, false);
		return internal.getBooleanValue();
	}

	public static boolean isUsePersonnalTemplate(MainFrame mainFrame) {
		Internal internal = get(mainFrame, BookKey.USE_PERSONALTEMPLATE, "");
		return !"".equals(internal.getStringValue());
	}

	public static boolean isUseHtmlScenes(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.USE_HTML_SCENES,
			SbConstants.DEFAULT_USE_HTML_SCENES);
		return internal.getBooleanValue();
	}

	public static boolean isUseHtmlDescr(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.USE_HTML_DESCR, SbConstants.DEFAULT_USE_HTML_DESCR);
		return internal.getBooleanValue();
	}

	public static boolean isExportBookHtmlMulti(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.HTML_BOOK_MULTI, false);
		return internal.getBooleanValue();
	}

	public static boolean isExportChapterNumbers(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.EXPORT_CHAPTER_NUMBERS,
			SbConstants.DEFAULT_EXPORT_CHAPTER_NUMBERS);
		return internal.getBooleanValue();
	}

	public static boolean isExportRomanNumerals(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.EXPORT_ROMAN_NUMERALS,
			SbConstants.DEFAULT_EXPORT_ROMAN_NUMERALS);
		return internal.getBooleanValue();
	}

	public static boolean isExportChapterTitles(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.EXPORT_CHAPTER_TITLES,
			SbConstants.DEFAULT_EXPORT_CHAPTERTITLES);
		return internal.getBooleanValue();
	}

	public static boolean isExportChapterDatesLocations(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.EXPORT_CHAPTER_DATES_LOCATIONS,
			SbConstants.DEFAULT_EXPORT_CHAPTERDATESLOCATIONS);
		return internal.getBooleanValue();
	}

	public static boolean isExportSceneTitle(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.EXPORT_SCENE_TITLES,
			SbConstants.DEFAULT_EXPORT_SCENETITLES);
		return internal.getBooleanValue();
	}

	public static boolean isExportSceneDidascalie(MainFrame mainFrame) {
		Internal internal = get(mainFrame, BookKey.EXPORT_SCENE_DIDASCALIE, false);
		return internal.getBooleanValue();
	}

	public static boolean isExportSceneSeparator(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.EXPORT_SCENE_SEPARATOR,
			SbConstants.DEFAULT_EXPORT_SCENESEPARATOR);
		return internal.getBooleanValue();
	}

	public static boolean isExportPartTitles(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.EXPORT_PART_TITLES,
			SbConstants.DEFAULT_EXPORT_PARTTITLES);
		return internal.getBooleanValue();
	}

	public static boolean isEditorFullToolbar(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.EDITOR_FULL_TOOLBAR,
			SbConstants.DEFAULT_EDITOR_FULL_TOOLBAR);
		return internal.getBooleanValue();
	}

	public static boolean isEditorModless(MainFrame mainFrame) {
		Internal internal = get(mainFrame,
			BookKey.EDITOR_MODLESS,
			SbConstants.DEFAULT_EDITOR_MODLESS);
		return internal.getBooleanValue();
	}

	public static Date getBookCreationDate(MainFrame mainFrame) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
		Internal internal = get(mainFrame,
			BookKey.BOOK_CREATION_DATE,
			format.format(new Date()));
		String dateStr = internal.getStringValue();
		Date date = new Date();
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			ExceptionDlg.show("", e);
		}
		return date;
	}

	public static void store(MainFrame mainFrame, BookKey key, Object val) {
		store(mainFrame, key.toString(), val);
	}

	public static void store(MainFrame mainFrame, String strKey, Object val) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		InternalDAOImpl dao = new InternalDAOImpl(session);
		dao.saveOrUpdate(strKey, val);
		model.commit();
	}

	public static void removeAllTableKey(MainFrame mainFrame, String prefix) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		String sql = "DELETE FROM Internal WHERE key LIKE '" + prefix + "%'";
		Query stmt = session.createQuery(sql);
		stmt.executeUpdate();
		model.commit();
	}

	public static boolean isKeyExist(MainFrame mainFrame, String strKey) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		InternalDAOImpl dao = new InternalDAOImpl(session);
		Internal internal = dao.findByKey(strKey);
		boolean b;
		if (internal == null) {
			b = false;
		} else {
			b = true;
		}
		model.commit();
		return b;
	}

	public static String getString(MainFrame mainFrame, BookKey key) {
		Internal internal = get(mainFrame, key, "");
		return (internal.getStringValue());
	}

	public static String getTitle(MainFrame mainFrame) {
		return (getString(mainFrame, BookKey.TITLE));
	}

	public static String getSubtitle(MainFrame mainFrame) {
		return (getString(mainFrame, BookKey.SUBTITLE));
	}

	public static String getAuthor(MainFrame mainFrame) {
		return (getString(mainFrame, BookKey.AUTHOR));
	}

	public static String getCopyright(MainFrame mainFrame) {
		return (getString(mainFrame, BookKey.COPYRIGHT));
	}

	public static String getNotes(MainFrame mainFrame) {
		return (getString(mainFrame, BookKey.NOTES));
	}

	public static String getBlurb(MainFrame mainFrame) {
		return (getString(mainFrame, BookKey.BLURB));
	}

	public static boolean getBoolean(MainFrame mainFrame, BookKey key) {
		Internal internal = get(mainFrame, key, false);
		return (internal.getBooleanValue());
	}

	public static boolean getBoolean(MainFrame mainFrame, BookKey key, boolean def) {
		Internal internal = get(mainFrame, key, def);
		return (internal.getBooleanValue());
	}

	public static int getInteger(MainFrame mainFrame, BookKey key, int def) {
		Internal internal = get(mainFrame, key, def);
		return (internal.getIntegerValue());
	}

	public static Internal get(MainFrame mainFrame, BookKey key) {
		return (get(mainFrame, key.toString(), ""));
	}

	public static Internal get(MainFrame mainFrame, BookKey key, Object val) {
		return (get(mainFrame, key.toString(), val));
	}

	public static Internal get(MainFrame mainFrame, String strKey, Object val) {
		BookModel model = mainFrame.getBookModel();
		if (model == null) {
			System.err.println("BoukUtil.get(mainframe," + strKey + ", " + (val == null ? "null" : val.toString()) + ")");
		}
		Session session = model.beginTransaction();
		InternalDAOImpl dao = new InternalDAOImpl(session);
		Internal internal = dao.findByKey(strKey);
		if (internal == null) {
			internal = new Internal(strKey, val);
			session.save(internal);
		}
		model.commit();
		return internal;
	}

	public static DbFile openDocumentDialog() {
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(SbApp.getInstance().preferences.getString(SbPref.Key.LAST_OPEN_DIR, getHomeDir())));
		FileFilter filter = new FileFilter(FileFilter.H2, I18N.getMsg("file.type.h2"));
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		int ret = fc.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null,
					I18N.getMsg("project.not.exist.text", file),
					I18N.getMsg("project.not.exist.title"),
					JOptionPane.ERROR_MESSAGE);
				return null;
			}
			DbFile dbFile = new DbFile(file);
			return dbFile;
		}
		return null;
	}

	public static String getHomeDir() {
		return System.getProperty("user.home");
	}

	public static int getNbParts(MainFrame m) {
		BookModel model = m.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		int n = dao.findAll().size();
		return (n);
	}

	public static Scene getScene(MainFrame m, String sceneTitle) {
		BookModel model = m.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findAll();
		model.commit();
		for (Scene scene : scenes) {
			if (scene.getChapterSceneNo(false).equals(sceneTitle)) {
				return (scene);
			}
		}
		return (null);
	}

	public static int getNbScenesInChapter(MainFrame mainFrame, Chapter chapter) {
		if (chapter == null) {
			return (0);
		}
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Scene> scenes = dao.findScenes(chapter);
		//model.commit();
		return (scenes.size());
	}

}
