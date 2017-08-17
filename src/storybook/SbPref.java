/*
 * Copyright (C) 2016 favdb
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package storybook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.text.util.StringUtils;

import storybook.model.DbFile;
import storybook.toolkit.EnvUtil;

/**
 *
 * @author favdb
 */
public class SbPref {

	List<PrefValue> preferences = new ArrayList<>();
	private final String configFile = EnvUtil.getPrefFile().getAbsolutePath();

	public void dump() {
		for (PrefValue preference : preferences) {
			System.out.println(preference.toString());
		}
	}
	
	public enum Key {
		VERSION("Version"),
		LAST_OPEN_DIR("LastOpenDir"),
		LAST_OPEN_BOOK("LastOpenBook"),
		LAST_OPEN_FILE("LastOpenFile"),
		OPEN_LAST_FILE("OpenLastFile"),
		RECENT_FILES("RecentFiles"),
		CONFIRM_EXIT("ConfirmExit"),
		SIZE_WIDTH("SizeWidth"),
		SIZE_HEIGHT("SizeHeight"),
		POS_X("PosX"),
		POS_Y("PosY"),
		MAXIMIZED("Maximized"),
		LANG("language"),
		SPELLING("Spelling"),
		DATEFORMAT("dateFormat"),
		LAF("LookAndFeel"),
		FIRST_START_4("FirstStart4"),
		DOCKING_LAYOUT("DockingLayout"),
		DEFAULT_FONT_NAME("DefaultFontName"),
		DEFAULT_FONT_SIZE("DefaultFontSize"),
		DEFAULT_FONT_STYLE("DefaultFontStyle"),
		EDITOR_FONT_NAME("EditorFontName"),
		EDITOR_FONT_SIZE("EditorFontSize"),
		EDITOR_FONT_STYLE("EditorFontStyle"),
		DO_UPDATER("UpdaterDo"),
		LAST_UPDATER("UpdaterLast"),
		EXPORT_PREF("EportPref"),
		EXPORT_CSV_QUOTE("ExportCsvQUOTE"),
		EXPORT_CSV_COMMA("ExportCsvComma"),
		EXPORT_TXT_TAB("ExportTxtTab"),
		EXPORT_TXT_SEPARATOR("ExportTxtSeparator"),
		LAST_USED_LAYOUT("LastDockingLayout"),
		TOOLBAR("ToolBar"),
		TOOLBAR_ORIENTATION("ToolBarOrientation"),
		TYPIST_USE("TypistUse"),
		MEMORY("SeeMemory");
		final private String text;

		private Key(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public enum Default {
		VERSION(SbConstants.Storybook.PRODUCT_VERSION.toString()),
		LAST_OPEN_DIR(System.getProperty("user.home")),
		LAST_OPEN_BOOK(""),
		LAST_OPEN_FILE(""),
		OPEN_LAST_FILE("false"),
		RECENT_FILES(""),
		CONFIRM_EXIT("true"),
		SIZE_WIDTH("1000"),
		SIZE_HEIGHT("700"),
		POS_X("100"),
		POS_Y("100"),
		MAXIMIZED("false"),
		LANG(SbConstants.DEFAULT_LANG),
		SPELLING("en"),
		DATEFORMAT("MM-dd-yyyy"),
		LAF(SbConstants.LookAndFeel.cross.name()),
		FIRST_START_4("true"),
		DOCKING_LAYOUT(""),
		DEFAULT_FONT_NAME("Dialog"),
		DEFAULT_FONT_SIZE("12"),
		DEFAULT_FONT_STYLE("0"),
		EDITOR_FONT_NAME("Times"),
		EDITOR_FONT_SIZE("12"),
		EDITOR_FONT_STYLE("0"),
		DO_UPDATER("0"),
		LAST_UPDATER(""),
		EXPORT_PREF("010"),
		EXPORT_CSV_QUOTE("'"),
		EXPORT_CSV_COMMA(";"),
		EXPORT_TXT_TAB("true"),
		EXPORT_TXT_SEPARATOR(""),
		LAST_USED_LAYOUT(""),
		TOOLBAR("1110011100110101100111001101000111110"),
		TOOLBAR_ORIENTATION("North");

		final private String text;

		private Default(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public SbPref() {
		load();
		String av = get(Key.VERSION.toString(), Default.VERSION.toString());
		String cv = Default.VERSION.toString();
		if (!av.equals(cv)) {
			setString(Key.VERSION.toString(), cv);
			System.out.println("Set Preferences version to " + cv);
		}
	}

	public String get(String key, String defvalue) {
		return (getString(key, defvalue));
	}

	public String getString(Key key, String def) {
		return (getString(key.toString(), def));
	}

	public String getString(String key, String defvalue) {
		for (PrefValue v : preferences) {
			if (v.key.equals(key)) {
				return (v.value);
			}
		}
		if (!"".equals(defvalue)) {
			preferences.add(new PrefValue(key, defvalue));
			save();
		}
		return (defvalue);
	}

	public Integer getInteger(Key key, Integer val) {
		String r = getString(key, val.toString());
		return (Integer.parseInt(r));
	}

	public Integer getInteger(String key, Integer val) {
		String r = getString(key, val.toString());
		return (Integer.parseInt(r));
	}

	public boolean getBoolean(Key key, boolean b) {
		return (getBoolean(key.toString(), b));
	}

	public boolean getBoolean(String key, boolean val) {
		String r = getString(key, (val ? "true" : "false"));
		return (("true".equals(r)));
	}

	public List<DbFile> getDbFileList() {
		ArrayList<DbFile> list = new ArrayList<>();
		for (PrefValue v : preferences) {
			if (v.key.equals(Key.RECENT_FILES.toString())) {
				if (v.value.isEmpty()) return(list);
				String values[] = v.value.split(";");
				for (String val : values) {
					File f=new File(val);
					if (f.exists()) list.add(new DbFile(f));
				}
				return list;
			}
		}
		return (list);
	}

	public void set(String key, String value) {
		setString(key, value);
	}

	public void setString(Key key, String value) {
		setString(key.toString(), value);
	}

	public void setString(String key, String value) {
		boolean notok = true;
		for (PrefValue v : preferences) {
			if (v.key.equals(key)) {
				v.set(value);
				notok = false;
			}
		}
		if (notok) {
			preferences.add(new PrefValue(key, value));
		}
		save();
	}

	public void setDbFileList(List<DbFile> list) {
		@SuppressWarnings("unchecked")
		List<DbFile> uniqueList = (List<DbFile>) SetUniqueList.decorate(list);
		try {
			if (uniqueList.size() > 10) {
				uniqueList = uniqueList.subList(uniqueList.size() - 10,
					uniqueList.size());
			}
		} catch (IndexOutOfBoundsException e) {
		}
		String val = StringUtils.join(uniqueList, ";");
		setString(Key.RECENT_FILES, val);
	}

	public void setInteger(Key key, Integer value) {
		setString(key, value.toString());
	}

	public void setInteger(String key, Integer value) {
		setString(key, value.toString());
	}

	public void setBoolean(Key key, boolean value) {
		setString(key, (value ? "true" : "false"));
	}

	public void setBoolean(String key, boolean value) {
		setString(key, (value ? "true" : "false"));
	}
	
	public List<String> getList(Key key) {
		List<String> list = new ArrayList<>();
		for (PrefValue v : preferences) {
			if (v.key.equals(Key.DOCKING_LAYOUT.toString())) {
				String values[] = v.value.split("#");
				list.addAll(Arrays.asList(values));
			}
		}
		return(list);
	}

	public void setList(Key key, List<String> values) {
		@SuppressWarnings("unchecked")
		List<String> uniqueList = (List<String>) SetUniqueList.decorate(values);
		try {
			if (uniqueList.size() > 10) {
				uniqueList = uniqueList.subList(uniqueList.size() - 10,
					uniqueList.size());
			}
		} catch (IndexOutOfBoundsException e) {
		}
		String val = StringUtils.join(uniqueList, "#");
		setString(key, val);
	}

	public void load() {
		File file = new File(configFile);
		if (!file.exists()) {
			create(file);
			return;
		}
		System.out.println("Load Preferences from " + file.getAbsolutePath());
		try {
			InputStream ips = new FileInputStream(configFile);
			InputStreamReader ipsr = new InputStreamReader(ips);
			try (BufferedReader br = new BufferedReader(ipsr)) {
				String ligne;
				while ((ligne = br.readLine()) != null) {
					String[] s = ligne.split("=");
					if (s.length<2) preferences.add(new PrefValue(s[0], ""));
					else preferences.add(new PrefValue(s[0], s[1]));
				}
				br.close();
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	private void create(File file) {
		System.out.println("Create new Preferences in " + file.getAbsolutePath());
		try {
			EnvUtil.getPrefDir().mkdir();
			file.createNewFile();
		} catch (IOException e) {
			SbApp.trace("Unable to create new file " + file.getAbsolutePath());
		}
		preferences.add(new PrefValue(Key.VERSION.toString(), Default.VERSION.toString()));
		save();
	}

	private void save() {
		String newline = System.getProperty("line.separator");
		try {
			OutputStream f = new FileOutputStream(configFile);
			for (PrefValue p : preferences) {
				f.write((p.toString() + newline).getBytes());
			}
		} catch (FileNotFoundException ex) {
			SbApp.error("Unable to save Preferences (file not found)", ex);
		} catch (IOException ex) {
			SbApp.error("Unable to save Preferences", ex);
		}
	}

	private static class PrefValue {

		String key;
		String value;

		public PrefValue(String k, String v) {
			key = k;
			value = v;
		}

		public void set(String v) {
			value = v;
		}

		public String get() {
			return (value);
		}

		@Override
		public String toString() {
			return (key + "=" + value);
		}
	}

}
