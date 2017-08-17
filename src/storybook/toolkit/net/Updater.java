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
package storybook.toolkit.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.SwingUtilities;
import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbPref;
import storybook.toolkit.DateUtil;
import storybook.i18n.I18N;
import storybook.ui.MainFrame;

public class Updater {

	public static boolean checkForUpdate(boolean force) {
		SbApp.trace("Updater.checkForUpdate(".concat(force?"force":"no force")+")");
		String doUpdater = SbApp.getInstance().preferences.getString(SbPref.Key.DO_UPDATER, "0");
		boolean toDo=false;
		switch(doUpdater) {
			case "0":
				SbApp.getInstance().preferences.setString(SbPref.Key.DO_UPDATER, "2");
				setDateUpdater();
				break;
			case "1":
				break;
			case "2":
				toDo=checkDateUpdater();
				break;
			case "3":
				toDo=true;
				break;
			default:
				break;
		}
		if (toDo || force) {
			try {
				// get version
				URL url = new URL(SbConstants.URL.VERSION.toString());
				String versionStr;
				try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
					String inputLine;
					versionStr = "";
					int c = 0,nc=-1;
					while ((inputLine = in.readLine()) != null) {
						versionStr = inputLine;
						if (inputLine.contains("Versions")) {
							nc = c + 1;
						}
						if (c == nc) {
							break;
						}
						c++;
					}
				}

				// compare version
				int remoteVersion = calculateVersion(versionStr);
				int localVersion = calculateVersion(SbConstants.Storybook.PRODUCT_VERSION.toString());
				if (localVersion < remoteVersion) {
					SwingUtilities.invokeLater(() -> {
						String updateUrl = SbConstants.URL.VERSION.toString();
						BrowserDlg.show((MainFrame)(java.awt.Frame)null, updateUrl, I18N.getMsg("update.title"));
					});
					setDateUpdater();
					return false;
				}
			} catch (SocketException | UnknownHostException e) {
				return true;
			} catch (Exception e) {
				SbApp.error("Updater.checkForUpdate() Exception:", e);
			}
		}
		return true;
	}

	private static int calculateVersion(String str) {
		SbApp.trace("Updater.calculateVersion("+str+")");
		String[] s = str.split("\\.");
		if (str.contains(":")) {
			String[] x = str.split(":");
			s=x[0].split("\\.");
		}
		if (s.length != 3) {
			SbApp.trace("Warning version text not recognized: \n"
				+"s.length="+ s.length+",s="+s.toString()+"\n");
			return -1;
		}
		int ret = 0;
		ret += Integer.parseInt(s[0]) * 1000000;
		ret += Integer.parseInt(s[1]) * 1000;
		ret += Integer.parseInt(s[2]);
		SbApp.trace("Updater.calculateVersion("+str+") return "+ret);
		return ret;
	}

	private static boolean checkDateUpdater() {
		boolean rc=false;
		String dateLast=SbApp.getInstance().preferences.getString(SbPref.Key.LAST_UPDATER, "");
		SbApp.trace("Updater.checkDateUpdater() last="+dateLast);
		if (!"".equals(dateLast)) {
			Date ddLast=DateUtil.stringToDate(dateLast);
			SbApp.trace("Updater.checkDateUpdater() last="+ddLast.toString());
			Date ddj=new Date();
			//si la différence entre ddLast et ddj est plus grand que 90 jours alors vérif
			long diff=ddj.getTime() - ddLast.getTime();
			long diffDays=(diff / (1000 * 60 * 60 * 24));
			SbApp.trace("Updater.checkDateUpdater() last="+ddLast.toString()+",ddj="+ddj.toString()+",diff="+diffDays);
			if (diffDays>90) {
				SbApp.getInstance().preferences.setString(SbPref.Key.LAST_UPDATER, DateUtil.dateToString(ddj));
				rc=true;
			}
		} else {
			SbApp.getInstance().preferences.setString(SbPref.Key.LAST_UPDATER, DateUtil.dateToString(new Date()));
		}
		SbApp.trace("Updater.checkDateUpdater() return="+(rc?"true":"false"));
		return(rc);
	}

	private static void setDateUpdater() {
		Date ddj=new Date();
		SbApp.getInstance().preferences.setString(SbPref.Key.LAST_UPDATER, DateUtil.dateToString(ddj));
	}
}
