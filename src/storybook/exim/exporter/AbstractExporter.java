/*
 * Copyright (C) 2017 favdb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.exim.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import storybook.SbApp;
import storybook.i18n.I18N;
import storybook.toolkit.BookUtil;
import storybook.toolkit.TextUtil;
import storybook.toolkit.html.HtmlUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.ExceptionDlg;

/**
 *
 * @author favdb
 */
public abstract class AbstractExporter {

	public ParamExport param;
	public boolean isOpened;
	public BufferedWriter outStream;
	public MainFrame mainFrame;
	public boolean isXml;
	public String name;
	public static boolean VERBOSE=true, SILENT=false;
	
	public AbstractExporter(MainFrame m, String view, String format) {
		mainFrame=m;
		param=new ParamExport(m);
		param.dir = mainFrame.getDbFile().getPath();
		param.format=format;
		isXml=(param.format.equals("xml"));
		isOpened=false;
	}
	
	public boolean askFileExists(String n) {
		this.name=n;
		param.fileName = param.dir + File.separator + name + "." + param.format;
		File file = new File(param.fileName);
		if (file.exists()) {
			return (true);
		}
		return (false);
	}

	public boolean openFile(String name) {
		isOpened = false;
		param.fileName = param.dir + File.separator + name + "." + param.format;
		this.name=name;
		File file = new File(param.dir);
		if (!(file.exists() && file.isDirectory())) {
			JOptionPane.showMessageDialog(mainFrame,
				I18N.getMsg("export.dir.error"),
				I18N.getMsg("export"), 1);
			return (false);
		}
		try {
			outStream = new BufferedWriter(new FileWriter(param.fileName));
		} catch (IOException ex) {
			ExceptionDlg.show("Export", ex);
			return (false);
		}
		// create header of file for Html or Xml
		switch (param.format) {
			case "xml":
				writeHeaderXml();
				break;
			case "html":
				writeHeaderHtml();
				break;
		}
		isOpened = true;
		return (true);
	}

	private boolean writeHeaderXml() {
		StringBuilder str = new StringBuilder();
		str.append("<?xml version='1.0'?>\n");
		if (isXml) {
			str.append("<book xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\">\n");
		} else {
			str.append("<book>\n");
		}
		try {
			outStream.write(str.toString(), 0, str.length());
			outStream.flush();
		} catch (IOException ex) {
			ExceptionDlg.show("Export", ex);
			return (false);
		}
		return (true);
	}

	/**
	 * writeHeaderHtml
	 *
	 * @return false if not OK else true
	 */
	private boolean writeHeaderHtml() {
		StringBuilder str = new StringBuilder();
		str.append("<!DOCTYPE html>\n<html>\n<head>\n");
		str.append("<title>");
		String title = BookUtil.getTitle(mainFrame);
		if (!isXml) {
			title += " - " + name;
		}
		str.append(title);
		str.append("</title>");
		str.append(HtmlUtil.getCSS(param.htmlCssFile,mainFrame.getFont()));
		str.append("<body>");
		str.append("<h1>").append(title).append("</h1>");
		try {
			outStream.write(str.toString(), 0, str.length());
			outStream.flush();
		} catch (IOException ex) {
			ExceptionDlg.show("Export", ex);
			return (false);
		}
		return (true);
	}

	public void writeText(String str) {
		if (!isOpened) return;
		if ("".equals(str)) {
			return;
		}
		SbApp.trace("ExportFile.writeText(" + TextUtil.truncateString(str, 32) + ")");
		try {
			outStream.write(str, 0, str.length());
			outStream.flush();
		} catch (IOException ex) {
			SbApp.error("ExportXml.writeText(" + str + ")", ex);
		}
	}
	
	public void closeFile(boolean verbose) {
		if (isOpened == false) {
			return;
		}
		try {
			switch(param.format) {
				case "xml":
					writeText("</book>\n");
					break;
				case "html":
					writeText("</body>\n</html>\n");
					break;
			}
			outStream.close();
			isOpened = false;
			if (verbose) JOptionPane.showMessageDialog(mainFrame,
				I18N.getMsg("export.success") + "\n" + param.fileName,
				I18N.getMsg("export"), JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException ex) {
			SbApp.error("ExportXml.close()", ex);
		}
	}

}
