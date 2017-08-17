/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jortho.FileUserDictionary;
import jortho.SpellChecker;

import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbPref;
import storybook.toolkit.EnvUtil;

public class SpellCheckerUtil {

	private static File userDictDir = null;

	public static File getDictionaryDir() throws IOException {
		try {
			File dir = EnvUtil.getPrefDir();
			File file = new File(
				dir.getCanonicalPath()
				+ File.separator
				+ SbConstants.SpellCheker.DICTIONARIES);
			if (!file.exists()) {
				file.mkdir();
			}
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URL getDictionaryDirAsURL() throws MalformedURLException,
		IOException {
		URI uri = getDictionaryDir().toURI();
		return uri.toURL();
	}

	public static void registerDictionaries() {
		try {
			URL url = getDictionaryDirAsURL();
			String spelling = SbApp.getInstance().getPref().getString(SbPref.Key.SPELLING, "none");
			if (!spelling.equals("none")) {
				String lang = spelling.substring(0, 2);
				String allDicts=getAllDicts();
				if (allDicts.isEmpty()) return;
				SpellChecker.registerDictionaries(url, allDicts, lang);
				// user dictionary directory
				File uDictDir = initUserDictDir();
				FileUserDictionary fud = new FileUserDictionary(uDictDir.toString());
				SpellChecker.setUserDictionaryProvider(fud);
			}
		} catch (Exception e) {
		}
	}

	public static File initUserDictDir() {
		if (userDictDir == null) {
			File dir = EnvUtil.getPrefDir();
			userDictDir = new File(
				dir
				+ File.separator
				+ SbConstants.SpellCheker.USER_DICTS);
			userDictDir.mkdir();
		}
		return userDictDir;
	}

	public static boolean isSpellCheckActive() {
//		String spelling = PrefManager.getInstance().getStringValue(
//				Constants.Preference.SPELLING);
//		if (spelling.equals(Constants.Spelling.none.name())) {
//			return false;
//		}
		return true;
	}

	public static String getAllDicts() {
		String rc="";
		try {
			File path=getDictionaryDir();
			File[] files=path.listFiles();
			for (File f: files) {
				if (f.isFile()) {
					String name=f.getName();
					if ((name.contains(".ortho")) && (name.contains("dictionary_"))) {
						name=name.replace("dictionary_", "");
						name=name.replace(".ortho", "");
						rc+=name+",";
					}
				}
			}
		} catch (IOException ex) {
			// no dicts directory in user home
			return("");
		}
		if (!rc.isEmpty()) rc=rc.substring(0, rc.length()-1);
		return(rc);
	}
	
	public static List<Language> getLanguages() {
		List<Language> languages = new ArrayList<>();
		InputStream stream = SbApp.getInstance().getClass().getResourceAsStream("resources/languages.txt");
		BufferedReader txt = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		try {
			line = txt.readLine();
		} catch (IOException ex) {
			Logger.getLogger(SbApp.class.getName()).log(Level.SEVERE, null, ex);
		}
		while (line != null) {
			String[] s = line.split(",");
			if (s.length==2) languages.add(new Language(s[1], s[0]));
			try {
				line = txt.readLine();
			} catch (IOException ex) {
				Logger.getLogger(SbApp.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		try {
			txt.close();
		} catch (IOException ex) {
			Logger.getLogger(SbApp.class.getName()).log(Level.SEVERE, null, ex);
		}
		return(languages);
	}

	public static class Language {

		String name, code;

		private Language(String n, String c) {
			name=n;
			code=c;
		}
		
		public String getCode() {
			return(code);
		}
		
		public String getName() {
			return(name);
		}
		
		@Override
		public String toString() {
			return(name);
		}
	}

}
