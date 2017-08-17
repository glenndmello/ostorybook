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

package storybook.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;

import storybook.SbApp;
import storybook.SbConstants;
import storybook.i18n.I18N;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
public class DbFile {

	private File file;		// the real file
	private File dbFile;	// the h2 file
	private String dbName;	// the complete dbname with the h2.db or mv.db extension
	private String name;	// the real name of the file with the osb5 extension
	private boolean opened=false;

	public DbFile(String dbName) {
		this(new File(dbName));
		SbApp.trace("DbFile("+dbName+")");
	}

	public DbFile(File file) {
		if (file==null) return;
		SbApp.trace("DbFile("+file.getAbsolutePath()+")");
		this.file = file;
		String absPath = file.getAbsolutePath();
		String ext = SbConstants.Storybook.DB_FILE_EXT.toString();
		int idx = absPath.lastIndexOf(ext);
		// Using new "mv.db" name
		if (idx < 0) {
			ext = SbConstants.Storybook.DB_FILE_EXT2.toString();
			idx = absPath.lastIndexOf(ext);
			if (idx < 0) { // this is for new osb5 ext
				ext = SbConstants.Storybook.OSB_FILE_EXT.toString();
				idx = absPath.lastIndexOf(ext);
			}
		}
		dbName = absPath.substring(0, idx);
		dbFile=new File(dbName+ext);
		String fileName = file.getName();
		idx = fileName.lastIndexOf(ext);
		name = fileName.substring(0, idx);
	}

	public File getFile() {
		return file;
	}

	public File getDbFile() {
		return dbFile;
	}

	public String getPath() {
		String f=file.getAbsolutePath();
		f=f.substring(0, f.lastIndexOf(File.separator));
		return(f);
	}

	public String getDbName() {
		return dbName;
	}

	public String getName() {
		return name;
	}
	
	public static boolean exists(File f) {
		if (f.exists()) return(true);
		else return(false);
	}

	@Override
	public boolean equals(Object obj) {
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		DbFile test = (DbFile) obj;
		boolean ret = true;
		ret = ret && file.equals(test.file);
		return ret;

	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + (file != null ? file.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return file.getPath();
	}

	public String getExt() {
		int beginIndex=file.getName().indexOf(".");
		return(file.getName().substring(beginIndex));
	}

	public boolean isOK() {
		SbApp.trace("DbFile.isOK()");
		boolean rc=true;
		// file doesn't exist
		if (!file.exists()) {
			String txt = I18N.getMsg("project.not.exist.text", file.getPath());
			JOptionPane.showMessageDialog(null,
				txt,
				I18N.getMsg("project.not.exist.title"),
				JOptionPane.ERROR_MESSAGE);
			rc=false;
		}
		// file is read-only
		if (!file.canWrite()) {
			String txt = I18N.getMsg("err.db.read.only", file.getPath());
			JOptionPane.showMessageDialog(null,
				txt,
				I18N.getMsg("warning"),
				JOptionPane.ERROR_MESSAGE);
			rc=false;
		}
		return(rc);
	}
	
	public boolean isAlreadyOpened() {
		SbApp.trace("DbFile.isAlreadyOpened("+dbName+")");
		boolean rc=false;
		List<MainFrame> mainFrames=SbApp.getInstance().getMainFrames();
		if (!mainFrames.isEmpty()) for (MainFrame mainFrame : mainFrames) {
			if (mainFrame.isBlank()) {
				continue;
			}
			if (mainFrame.getDbFile().getDbName().equals(dbName)) {
				rc=true;
				mainFrame.setVisible(true);
				break;
			}
		} else {
			SbApp.trace("==>frames are empty");
		}
		SbApp.trace("==>File "+dbName+" "+(rc?"already opened":"not already opened"));
		return(rc);
	}

	public void open() {
		// to confirm open of the dbFile by copying an osb5 file to a h2.db or mv.db file
		if (opened) return;
		opened=true;
	}

	public void save() {
		if (!opened) return;
	}
	
	public void close() {
		opened=false;
	}

	public void doBackup() {
		// backup file befor import
		File backup=new File(file.getAbsolutePath()+".backup");
		System.out.println("backup of\n"+file.getAbsolutePath()+"\nto\n"+backup.getAbsolutePath());
		try {
			FileUtils.copyFile(file,backup);
		} catch (IOException ex) {
			SbApp.error(DbFile.class.getName(), ex);
		}
	}

}
