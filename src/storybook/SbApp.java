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

package storybook;

import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.swing.JEditorPane;
import javax.swing.JMenu;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.io.FileUtils;

import storybook.SbConstants.BookKey;
import storybook.model.DbFile;
import storybook.model.oldModel.ModelMigration;
import storybook.toolkit.BookUtil;
import storybook.i18n.I18N;
import storybook.toolkit.net.Updater;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.splash.HourglassSplash;
import storybook.ui.MainFrame;
import storybook.ui.dialog.ChooseFileDlg;
import storybook.ui.dialog.ExceptionDlg;
import storybook.ui.dialog.FirstStartDlg;

public class SbApp extends Component {

	public SbPref preferences;
	private final List<MainFrame> mainFrames;
	private Font defaultFont;
	private Font editorFont;

	private static boolean bTrace=false;
	private static boolean bTraceHibernate=false;

	private static SbApp instance;
	private static String i18nFile="";
	private static boolean bDevTest=false;

	public static boolean isDevTest() {
		return(bDevTest);
	}

	public static void traceError(Exception e) {
		System.err.println(e.getLocalizedMessage());
		StackTraceElement[] se=e.getStackTrace();
		int i=0;
		for (StackTraceElement el:se) {
			System.err.println(el.toString());
			i++;
			if (i>10) break;
		}
	}

	private SbApp() {
		mainFrames = new ArrayList<>();
	}

	public SbPref getPref() {
		return(getInstance().preferences);
	}
	private void init(String i18n) {
		init();
	}
	private void init() {
		trace("SbApp.init()");
		try {
			MainFrame mainFrame = new MainFrame();
			//new Preferences
			preferences=new SbPref();
			initI18N();
			trace("-->SwingUtil.setLookAndFeel()");
			SwingUtil.setLookAndFeel();
			restoreDefaultFont();
			//restoreEditorFont();
			// first start dialog
			if (preferences.getBoolean(SbPref.Key.FIRST_START_4,true)) {
				trace("call dlgFirst()");
				//DlgFirst dlg = new DlgFirst();
				FirstStartDlg dlg=new FirstStartDlg();
				dlg.setVisible(true);
				preferences.setBoolean(SbPref.Key.FIRST_START_4, false);
			}

			boolean fileHasBeenOpened = false;
			if (preferences.getBoolean(SbPref.Key.OPEN_LAST_FILE,false)) {
				DbFile dbFile = new DbFile(preferences.getString(SbPref.Key.LAST_OPEN_FILE,""));
				trace("SbApp.init(): loading... " + dbFile);
				fileHasBeenOpened = openFile(dbFile);
			}
			if (fileHasBeenOpened) {
				// check for updates
				Updater.checkForUpdate(false);
				return;
			}
			mainFrame.init();
			mainFrame.initBlankUi();
			addMainFrame(mainFrame);

			// check for updates
			Updater.checkForUpdate(false);
			
		} catch (Exception e) {
			error("SbApp.init()",e);
			ExceptionDlg.show("",e);
		}
	}

	public void initI18N() {
		trace("SbApp.initI18N()");
		String localeStr = preferences.getString(SbPref.Key.LANG, "en_US");
		SbConstants.Language lang;
		try {
			lang= SbConstants.Language.valueOf(localeStr);
		} catch(Exception ex) {
			lang= SbConstants.Language.valueOf(preferences.getString(SbPref.Key.LANG, "en_US"));
		}
		Locale locale = lang.getLocale();
		setLocale(locale);
		I18N.initMessages(getLocale());
	}

	public List<MainFrame> getMainFrames() {
		return mainFrames;
	}

	public void addMainFrame(MainFrame mainFrame) {
		trace("SbApp.addMainFrame("+mainFrame.getName()+")");
		mainFrames.add(mainFrame);
	}

	public void removeMainFrame(MainFrame mainFrame) {
		trace("SbApp.removeMainFrame("+mainFrame.getName()+")");
		mainFrames.remove(mainFrame);
	}

	public void closeBlank() {
		trace("SbApp.closeBlank()");
		for (MainFrame mainFrame : mainFrames) {
			if (mainFrame.isBlank()) {
				mainFrames.remove(mainFrame);
				mainFrame.dispose();
			}
		}
	}
/* suppression de l'appel du garbage collector
	public void runGC(){
		System.gc();
	}
*/
	public static SbApp getInstance() {
		if (instance == null) {
			instance = new SbApp();
		}
		return instance;
	}

	public void createNewFile() {
		trace("SbApp.createNewFile()");
		try {
			//DlgFile dlg=new DlgFile(false);
			ChooseFileDlg dlg=new ChooseFileDlg((MainFrame)null,false);
			dlg.setVisible(true);
			if (dlg.isCanceled()) {
				return;
			}
			DbFile dbFile = new DbFile(dlg.getFile());
			String dbName = dbFile.getDbName();
			if (dbName == null) {
				return;
			}
			final MainFrame newMainFrame = new MainFrame();
			newMainFrame.init(dbFile);
			newMainFrame.getBookModel().initEntites();
			BookUtil.store(newMainFrame, SbConstants.BookKey.TITLE, I18N.getMsg("title"));
			BookUtil.store(newMainFrame, BookKey.USE_HTML_SCENES, true);
			BookUtil.store(newMainFrame, BookKey.USE_HTML_DESCR, true);
			BookUtil.store(newMainFrame, BookKey.BOOK_CREATION_DATE,
					new SimpleDateFormat("dd/MM/yy").format(new Date()));
			BookUtil.store(newMainFrame, BookKey.TYPIST_USE, preferences.getBoolean(SbPref.Key.TYPIST_USE, false));
			newMainFrame.initUi();
			newMainFrame.getBookController().fireAgain();
			addMainFrame(newMainFrame);
			closeBlank();
			updateFilePref(dbFile);
			setDefaultCursor();
		} catch (Exception e) {
			error("SbApp.createNewFile()",e);
		}
	}

	public void renameFile(final MainFrame mainFrame, File outFile) {
		trace("SbApp.renameFile("+mainFrame.getName()+","+outFile.getAbsolutePath()+")");
		try {
			File inFile=mainFrame.getDbFile().getFile();
			mainFrame.close(false);
			FileUtils.copyFile(inFile, outFile);
			inFile.delete();
			DbFile dbFile = new DbFile(outFile);
			openFile(dbFile);				
		} catch (IOException e) {
			error("SbApp.renameFile("+mainFrame.getName()+","+outFile.getName()+")", e);
		}
	}

	public boolean openFile() {
		trace("SbApp.openFile()");
		final DbFile dbFile = BookUtil.openDocumentDialog();
		if (dbFile == null) {
			return false;
		}
		return openFile(dbFile);
	}

	public boolean openFile(final DbFile dbFile, String oldPath, String newPath) {
		boolean rc=openFile(dbFile);
		SwingUtilities.invokeLater(() -> {
			SbApp.changingDbFile(dbFile, oldPath, newPath);			
		});
		return rc;
	}
	
	public static void changingDbFile(DbFile dbFile, String oldPath, String newPath) {
		MainFrame mf=null;
		for (MainFrame m : SbApp.getInstance().mainFrames) {
			System.out.println("dbFile="+dbFile.getDbName()+", m.dbFile="+m.getDbFile().getDbName());
			if (m.getDbFile().equals(dbFile)) mf=m;
		}
		if (mf==null) {
			System.out.println("mf=null");
			return;
		}
		mf.changePath(oldPath,newPath);
		mf.changeTitle(I18N.getMsg("copyof")+" "+BookUtil.getTitle(mf));
	}
	
	public boolean openFile(DbFile dbFile) {
		trace("SbApp.openFile("+dbFile.getDbName()+")");
		if (!dbFile.isOK()) return(false);
		if (dbFile.isAlreadyOpened()) {
			return(true);
		}
		try {
			// model update from Storybook 3.x to 4.0
			final ModelMigration oldPersMngr = ModelMigration.getInstance();
			oldPersMngr.open(dbFile);
			try {
				if (!oldPersMngr.checkAndAlterModel()) {
					oldPersMngr.closeConnection();
					return false;
				}
			} catch (Exception e) {
				oldPersMngr.closeConnection();
				SbApp.error("SbApp.openFile("+dbFile.getDbName()+")",e);
				ExceptionDlg.show("", e);
				return false;
			}
			oldPersMngr.closeConnection();
			setWaitCursor();
			final HourglassSplash dlg = new HourglassSplash(I18N.getMsg("loading", dbFile.getName()));
			SwingUtilities.invokeLater(() -> {
				try {
					setWaitCursor();
					dlg.setProgress("init mainframe");
					MainFrame newMainFrame = new MainFrame();
					newMainFrame.init(dbFile);
					newMainFrame.initUi();
					addMainFrame(newMainFrame);
					closeBlank();
					dlg.setProgress("update file preferences");
					updateFilePref(dbFile);
					dlg.setProgress("reload menu bar");
					reloadMenuBars();
					setDefaultCursor();
					dlg.stop();
					dlg.dispose();
				} catch (Exception e) {
					ExceptionDlg.show("",e);
					dlg.dispose();
				}
			});
		} catch (HeadlessException e) {
			SbApp.error("SbApp.openFile", e);
		}
		return true;
	}

	private boolean checkIfAlreadyOpened(String dbName) {
		trace("SbApp.checkIfAlreadyOpened("+dbName+")");
		for (MainFrame mainFrame : mainFrames) {
			if (mainFrame.isBlank()) {
				continue;
			}
			if (mainFrame.getDbFile().getDbName().equals(dbName)) {
				mainFrame.setVisible(true);
				return true;
			}
		}
		return false;
	}

	private void updateFilePref(DbFile dbFile) {
		trace("SbApp.updateFilePref("+dbFile.getDbName()+")");
		// save last open directory and file
		File file = dbFile.getFile();
		preferences.setString(SbPref.Key.LAST_OPEN_DIR, file.getParent());
		preferences.setString(SbPref.Key.LAST_OPEN_FILE, file.getPath());
		// save recent files
		List<DbFile> list=preferences.getDbFileList();
		if (!list.contains(dbFile)) {
			list.add(dbFile);
		}
		// check recent files and remove non-existing entries
		Iterator<DbFile> it = list.iterator();
		while (it.hasNext()) {
			DbFile dbFile2 = it.next();
			if (!dbFile2.getFile().exists()) {
				it.remove();
			}
		}
		preferences.setDbFileList(list);
		reloadMenuBars();
	}

	public void clearRecentFiles() {
		trace("SbApp.clearRecentFiles()");
		preferences.setDbFileList(new ArrayList<>());
		reloadMenuBars();
	}

	public void exit() {
		trace("SbApp.exit()");
		if (mainFrames.size() > 0) {
			if (preferences.getBoolean(SbPref.Key.CONFIRM_EXIT,true)) {
				int n = JOptionPane.showConfirmDialog(null,
						I18N.getMsg("want.exit"),
						I18N.getMsg("exit"),
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
					return;
				}
			}
			saveAll();
		}
		System.exit(0);
	}

	public void resetUiFont() {
		if (defaultFont == null) {
			return;
		}
		SwingUtil.setUIFont(new FontUIResource(defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize()));
	}

	public void setDefaultFont(Font font) {
		trace("SbApp.setDefaultFont()");
		if (font == null) {
			JMenu e=new JMenu();
			defaultFont=e.getFont();
		}
		defaultFont = font;
		resetUiFont();
		preferences.setString(SbPref.Key.DEFAULT_FONT_NAME, defaultFont.getName());
		preferences.setInteger(SbPref.Key.DEFAULT_FONT_SIZE, defaultFont.getSize());
		preferences.setInteger(SbPref.Key.DEFAULT_FONT_STYLE, defaultFont.getStyle());
	}

	public Font getDefaultFont() {
		return this.defaultFont;
	}

	public void restoreDefaultFont() {
		trace("SbApp.restoreDefaultFont()");
		String name=preferences.getString(SbPref.Key.DEFAULT_FONT_NAME, SbConstants.DEFAULT_FONT_NAME);
		int style=preferences.getInteger(SbPref.Key.DEFAULT_FONT_STYLE,SbConstants.DEFAULT_FONT_STYLE);
		int size=preferences.getInteger(SbPref.Key.DEFAULT_FONT_SIZE,SbConstants.DEFAULT_FONT_SIZE);
		setDefaultFont(new Font(name, style, size));
	}

	public void setEditorFont(Font font) {
		trace("SbApp.setEditorFont()");
		if (font == null) {
			JEditorPane e= new JEditorPane();
			editorFont=e.getFont();
		} else editorFont = font;
		resetUiFont();
		preferences.setString(SbPref.Key.EDITOR_FONT_NAME, editorFont.getName());
		preferences.setInteger(SbPref.Key.EDITOR_FONT_SIZE, editorFont.getSize());
		preferences.setInteger(SbPref.Key.EDITOR_FONT_STYLE, editorFont.getStyle());
	}

	public Font getEditorFont() {
		if (editorFont==null) restoreEditorFont();
		return this.editorFont;
	}

	public void restoreEditorFont() {
		trace("SbApp.restoreEditorFont()");
		String name=preferences.getString(SbPref.Key.EDITOR_FONT_NAME, SbConstants.EDITOR_FONT_NAME);
		int style=preferences.getInteger(SbPref.Key.EDITOR_FONT_STYLE,SbConstants.EDITOR_FONT_STYLE);
		int size=preferences.getInteger(SbPref.Key.EDITOR_FONT_SIZE,SbConstants.EDITOR_FONT_SIZE);
		setEditorFont(new Font(name, style, size));
	}

	public void refresh() {
		trace("SbApp.refresh()");
		for (MainFrame mainFrame : mainFrames) {
			int width = mainFrame.getWidth();
			int height = mainFrame.getHeight();
			boolean maximized = mainFrame.isMaximized();
			mainFrame.getSbActionManager().reloadMenuToolbar();
			mainFrame.setSize(width, height);
			if (maximized) {
				mainFrame.setMaximized();
			}
			mainFrame.refresh();
		}
	}

	public void reloadMenuBars() {
		for (MainFrame mainFrame : mainFrames) {
			mainFrame.getSbActionManager().reloadMenuToolbar();
		}
	}

	public void reloadStatusBars() {
		for (MainFrame mainFrame : mainFrames) {
			mainFrame.refreshStatusBar();
		}
	}

	public void setWaitCursor() {
		for (MainFrame mainFrame : mainFrames) {
			SwingUtil.setWaitingCursor(mainFrame);
		}
	}

	public void setDefaultCursor() {
		for (MainFrame mainFrame : mainFrames) {
			SwingUtil.setDefaultCursor(mainFrame);
		}
	}

	public void saveAll() {
		trace("SbApp.saveAll()");
		for (MainFrame mainFrame : mainFrames) {
		}
	}

	public void modelPropertyChange(PropertyChangeEvent evt) {
		// works, but currently not used
		// may be used for entity copying between files
		// String propName = evt.getPropertyName();
		// Object newValue = evt.getNewValue();
		// Object oldValue = evt.getOldValue();
	}

	public static void main(String[] args) {
		String tempDir = System.getProperty("java.io.tmpdir");
		String fn = tempDir + File.separator + "storybook.lck";
		if (args.length>0) {
			for (int i=0;i<args.length;i++) {
				if (args[i].equalsIgnoreCase("--trace")) {
					SbApp.bTrace=true;
					System.out.println("Storybook execution in trace mode");
				}
				if (args[i].equalsIgnoreCase("--hibernate")) {
					SbApp.bTraceHibernate=true;
					System.out.println("Hibernate in trace mode");
				}
				if (args[i].equalsIgnoreCase("--dev")) {
					SbApp.bDevTest=true;
					System.out.println("Development test");
				}
				if (args[i].equalsIgnoreCase("--msg")) {
					String fileI18N = args[i+1];
					File f=new File(args[i+1]);
					if (!f.exists()) {
						fileI18N=args[i+1]+".properties";
						f=new File(fileI18N);
						if (!f.exists()) {
							System.out.println("Msg test file not exists : "+args[i+1]);
							fileI18N="";
						}
					}
					if (!fileI18N.isEmpty()) {
						SbApp.i18nFile=fileI18N;
						System.out.println("Msg test file is : "+fileI18N);
						I18N.setFileMessages(i18nFile);
					}
				}
			}
		}
		if (!lockInstance(fn)) {
			Object[] options = { I18N.getMsg("running.remove"), I18N.getMsg("cancel") };
			int n = JOptionPane.showOptionDialog(null,
					I18N.getMsg("running.msg"),
					I18N.getMsg("running.title"),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			if (n == 0) {
				File file = new File(fn);
				if (file.exists() && file.canWrite()) {
					if (!file.delete()) {
						JOptionPane.showMessageDialog(null, "Delete failed",
								"File\n" + file.getAbsolutePath() + "\ncould not be deleted.",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			return;
		}
		
		SwingUtilities.invokeLater(() -> {
			SbApp app=SbApp.getInstance();
			app.init();
		});
	}

	private static boolean lockInstance(final String lockFile) {
		try {
			final File file = new File(lockFile);
			final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
			final FileLock fileLock = randomAccessFile.getChannel().tryLock();
			if (fileLock != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						try {
							fileLock.release();
							randomAccessFile.close();
							file.delete();
						} catch (IOException e) {
							System.err.println("Unable to remove lock file: "+ lockFile+"->"+e.getMessage());
						}
					}
				});
				return true;
			}
		} catch (IOException e) {
			System.err.println("Unable to create and/or lock file: " + lockFile+"->"+e.getMessage());
		}
		return false;
	}

	public static void error(String txt, Exception e) {
		System.err.println(txt+" Exception:"+e.getMessage());
		System.err.println(Arrays.toString(e.getStackTrace()));
	}

	public static void trace(String msg) {
		if (bTrace) {
			System.out.println(msg);
		}
	}

	public static boolean getTrace() {
		return(bTrace);
	}

	public static boolean getTraceHibernate() {
		return(bTraceHibernate);
	}

	public static String getI18nFile() {
		return(i18nFile);
	}
	
	public void setI18nFile(String file) {
		i18nFile=file;
	}

	public static void setTrace(boolean b) {
		bTrace=b;
		System.out.println((b?"Enter":"Exit")+" trace mode");
	}

	// refresh titles of views
	public void refreshViews() {
		trace("SbApp.refreshViews()");
		for (MainFrame mainFrame : mainFrames) {
			mainFrame.refreshViews();
		}
	}

}
