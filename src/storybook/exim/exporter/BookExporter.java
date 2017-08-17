/*
 * oStorybook: Open Source software for novelists and authors.
 * Original idea 2008 - 2012 Martin Mustun
 * Copyrigth (C) Favdb
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
 */
package storybook.exim.exporter;

import java.awt.HeadlessException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JOptionPane;

import org.hibernate.Session;

import storybook.SbApp;
import storybook.i18n.I18N;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.toolkit.BookUtil;
import storybook.toolkit.DateUtil;
import storybook.toolkit.IOUtil;
import storybook.toolkit.html.HtmlUtil;
import storybook.toolkit.LangUtil;
import storybook.toolkit.ListUtils;
import storybook.toolkit.TextTransfer;
import storybook.ui.MainFrame;

/*
 * BookExporter : main class for export the text of the book
 * Only export :
 * - title of Part : if more than one part
 * - title of chapter : optional, with optional number
 * - title of scene : optional
 * - didascalie : optional
 * - text of scene
 * @author favdb
 */
public class BookExporter extends AbstractExporter {

	boolean isHtml = true;

	public boolean exportOnlyCurrentPart = false;
	public static boolean ONLY_CURRENT_PART=true;
	public boolean exportTableOfContentsLink = false;
	public static boolean TOC_LINKS=true;
	private HashSet<Long> strandIdsToExport = null;
	private String bH1 = "<h1>", eH1 = "</h1>",
		bH2 = "<h2>", eH2 = "</h2>",
		bH3 = "<h3>", eH3 = "</h3>",
		bH4 = "<h4>", eH4 = "</h4>",
		bTx = "<p>", eTx = "</p";
	private ChapterDAOImpl chapterDAO;
	private Chapter firstChapter;
	private Chapter lastChapter;

	public BookExporter(MainFrame m, String name, String format) {
		super(m, name, format);
		isHtml = (param.format.equals("html"));
		if (!isHtml) {
			bH1 = bH2 = bH3 = bH4 = "";
			eH1 = eH2 = eH3 = eH4 = "";
			bTx = eTx = "";
		} else if (param.isExportChapterBreakPage && !param.htmlBookMulti) {
			bH1="<h1 style=\"page-break-before: always;\">";
			bH2="<h2 style=\"page-break-before: always;\">";
		}
		SbApp.trace("BookExporter(" + m.getName() + "," + name + "," + format + ")");
	}

	public static void toClipboard(MainFrame m, boolean onlyPart) {
		SbApp.trace("BookExporter.toClipboarb(mainFrame," + (onlyPart ? "current part" : "all part") + ")");
		BookExporter export = new BookExporter(m, "Book", "txt");
		export.setExportOnlyCurrentPart(onlyPart);
		try {
			TextTransfer tf = new TextTransfer();
			tf.setClipboardContents(export.getBody().toString());
			JOptionPane.showMessageDialog(m,
				I18N.getMsg("book.copy.text.confirmation"),
				I18N.getMsg("copied.title"), 1);
		} catch (HeadlessException exc) {
		}
	}

	public static void toFile(MainFrame m, String ext) {
		SbApp.trace("BookExporter.toFile(mainFrame," + ext + ")");
		BookExporter export = new BookExporter(m, "Book", ext);
		String fileName = BookUtil.getTitle(m);
		if (export.isHtml && export.param.htmlBookMulti) {
			fileName += " index";
		}
		if (export.askFileExists(fileName)) {
			if (JOptionPane.showConfirmDialog(m,
				I18N.getMsg("export.replace", export.param.fileName),
				I18N.getMsg("export"),
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		if (!export.openFile(fileName)) {
			return;
		}
		export.getTOC();
		export.getBody();
		export.closeFile(SILENT);
		JOptionPane.showMessageDialog(m,
			I18N.getMsg("export.success"),
			I18N.getMsg("export"), JOptionPane.INFORMATION_MESSAGE);
	}

	public static String toPanel(MainFrame m, HashSet<Long> strandIds,boolean onlyCurrentPart, boolean tocLinks) {
		BookExporter exp = new BookExporter(m,"Book","html");
		exp.setExportOnlyCurrentPart(true);
		exp.setExportTableOfContentsLink(true);
		exp.setStrandIdsToExport(strandIds);
		StringBuilder buf=new StringBuilder();
		buf.append(exp.getTOC());
		buf.append(exp.getBody());
		return(buf.toString());
	}

	public String getTOC() {
		if (!isHtml) {
			return ("");
		}
		SbApp.trace("BookExporter.getToc()");
		StringBuilder buf = new StringBuilder();
		if (isOpened) {
			buf.append("<h2>").append(I18N.getMsg("export.toc")).append("</h2>\n");
		} else {
			buf.append("<a name=\"toc\" />\n");
		}
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl partDAO = new PartDAOImpl(session);
		chapterDAO = new ChapterDAOImpl(session);
		List<Part> parts = partDAO.findAll();
		List<Chapter> chapters = chapterDAO.findAll();
		if (parts == null) {
			return ("");
		}
		for (Part part : parts) {
			if (parts.size() > 1) {
				buf.append("<h2>").append(part.getNumberName()).append("</h2>");
			}
			for (Chapter chapter : chapters) {
				if (chapter == null) {
					continue;
				}
				if (!chapter.getPart().equals(part)) {
					continue;
				}
				if (firstChapter == null) {
					firstChapter = chapter;
				}
				lastChapter = chapter;
				StringBuilder str = new StringBuilder("<a href=\"");
				if (isOpened && param.htmlBookMulti) {
					str.append(BookUtil.getTitle(mainFrame)).append(" ")
						.append(getChapterId(chapter)).append(".html\">");
				} else {
					str.append("#").append(chapter.getChapterno()).append("\">");
				}
				if (param.isExportChapterNumbers) {
					if (param.isExportChapterNumbersRoman) {
						str.append((String) LangUtil.intToRoman(chapter.getChapterno()));
					} else {
						str.append(chapter.getChapternoStr());
					}
					str.append(" ");
				}
				str.append(chapter.getTitle()).append("</a><br>\n");
				buf.append(str.toString());
			}
		}
		writeText(buf.toString());
		return (buf.toString());
	}

	private String getNavBar(Chapter precedent, Chapter current, Chapter suivant) {
		if (!isHtml) {
			return ("");
		}
		String toc = " ";
		String imgToc = I18N.getMsg("export.nav.summary");
		String imgFirst = I18N.getMsg("export.nav.first");
		String imgNext = I18N.getMsg("export.nav.next");
		String imgPrior = I18N.getMsg("export.nav.prior");
		String imgLast = I18N.getMsg("export.nav.last");
		String title = BookUtil.getTitle(mainFrame);
		if (param.htmlNavImage) {
			imgToc = getNavImage("summary", imgToc);
			imgFirst = getNavImage("first", imgFirst);
			imgNext = getNavImage("next", imgNext);
			imgPrior = getNavImage("previous", imgPrior);
			imgLast = getNavImage("last", imgLast);
		}
		if (current != null) {
			toc = "<a href=\""
				+ title
				+ " index.html" + "\">"
				+ imgToc + "</a> | ";
		}
		//premier chapitre
		String first = " ";
		if (precedent != null) {
			first = "<a href=\""
				+ title + " " + getChapterId(firstChapter) + ".html\">"
				+ imgFirst + "</a> | ";
		}
		//chapitre precedent
		String prior = "";
		if (precedent != null) {
			prior = "<a href=\""
				+ title + " " + getChapterId(precedent) + ".html\">"
				+ imgPrior + "</a> | ";
		}
		//chapitre suivant
		String next = "";
		if (suivant != null) {
			next = "<a href=\""
				+ title + " " + getChapterId(suivant) + ".html\">"
				+ imgNext + "</a>";
		}
		//dernier chapitre
		String last = "";
		if (suivant != null) {
			last = " | <a href=\""
				+ title + " " + getChapterId(lastChapter) + ".html\">"
				+ imgLast + "</a></p>\n";
		}
		String str = first + prior + toc + next + last;
		return ("<p>" + str + "</p>");
	}

	private String getNavImage(String img, String name) {
		copyImage(img, param.dir + File.separator + "img");
		return ("<img src=\"img/" + img + ".png\" title=\"" + name + "\" alt=\"" + name + "\">");
	}

	private void copyImage(String img, String dir) {
		SbApp.trace("BookExporter.copyImage(" + img + "," + dir + ")");
		File fdir = new File(dir);
		fdir.mkdirs();
		File out = new File(dir + File.separator + img + ".png");
		if (out.exists()) {
			return;
		}
		InputStream resource = mainFrame.getClass().getResourceAsStream("/storybook/resources/icons/16x16/" + img + ".png");
		if (resource == null) {
			return;
		}
		try {
			InputStream in = resource;
			try (OutputStream writer = new BufferedOutputStream(new FileOutputStream(out))) {
				byte[] buffer = new byte[1024];
				int length;
				while ((length = in.read(buffer)) >= 0) {
					writer.write(buffer, 0, length);
				}
			}
		} catch (IOException ex) {
			System.err.println("BookExporter.copyImage " + ex.getLocalizedMessage());
		}
	}

	public StringBuffer getBody() {
		SbApp.trace("BookExporter.getBody()");
		Part currentPart = mainFrame.getCurrentPart();
		StringBuffer buf = new StringBuffer();
		try {
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			PartDAOImpl PartDAO = new PartDAOImpl(session);
			chapterDAO = new ChapterDAOImpl(session);
			SceneDAOImpl SceneDAO = new SceneDAOImpl(session);
			List<Part> listParts;
			if (exportOnlyCurrentPart) {
				listParts = new ArrayList<>();
				listParts.add(currentPart);
			} else {
				listParts = PartDAO.findAll();
			}
			List<Chapter> chapters = chapterDAO.findAllOrderByChapterNoAndSceneNo();
			List<Scene> scenes = SceneDAO.findAll();
			firstChapter = chapters.get(0);
			lastChapter = chapters.get(chapters.size() - 1);
			for (Part part : listParts) {
				if (listParts.size() > 1) {
					buf.append(getPart(part));
				}
				for (int i = 0; i < chapters.size(); i++) {
					Chapter chapter = chapters.get(i);
					if (chapter.getPart().equals(part)) {
						if (isOpened && isHtml && param.htmlBookMulti) {
							writeText(buf.toString());
							buf = new StringBuffer();
							closeFile(SILENT);
							openFile(BookUtil.getTitle(mainFrame) + " " + getChapterId(chapter));
						}
						buf.append(getChapter(chapter));
						for (Scene scene : scenes) {
							if (scene.getChapter() == null) {
								continue;
							}
							if (scene.getChapter().equals(chapter)) {
								if (isOpened) {
									buf.append(getScene(scene, param.dir));
								}
								else buf.append(getScene(scene));
							}
						}
						if (isOpened && isHtml && param.htmlBookMulti) {
							Chapter precedent = null;
							if (i > 0) {
								precedent = chapters.get(i - 1);
							}
							Chapter suivant = null;
							if (i < chapters.size() - 1) {
								suivant = chapters.get(i + 1);
							}
							buf.append(getNavBar(precedent, chapter, suivant));
						}
					}
				}
			}
			//model.commit();
		} catch (Exception exc) {
			SbApp.error("BookExport.getBody()", exc);
		}
		if (isOpened) {
			writeText(buf.toString());
		}
		return buf;
	}

	public boolean isExportOnlyCurrentPart() {
		return exportOnlyCurrentPart;
	}

	public void setExportOnlyCurrentPart(boolean b) {
		exportOnlyCurrentPart = b;
	}

	public boolean isExportTableOfContentsLink() {
		return exportTableOfContentsLink;
	}

	public void setExportTableOfContentsLink(boolean b) {
		exportTableOfContentsLink = b;
	}

	public HashSet<Long> getStrandIdsToExport() {
		return strandIdsToExport;
	}

	public void setStrandIdsToExport(HashSet<Long> p) {
		strandIdsToExport = p;
	}

	public void setExportToTxt() {
		isHtml = false;
	}

	public String getPart(Part part) {
		SbApp.trace("BookExporter.getPart(...)");
		String buf = "";
		if (param.isExportPartTitles) {
			buf = bH1 + I18N.getMsg("part") + ": " + part.getNumber() + eH1;
		}
		return (buf);
	}

	private String getChapterId(Chapter chapter) {
		SbApp.trace("BookExporter.getChapterId(...)");
		String spart = Integer.toString(chapter.getPart().getNumber());
		String schapter = Integer.toString(chapter.getChapterno());
		if (spart.length() < 2) {
			spart = "0" + spart;
		}
		if (schapter.length() < 2) {
			schapter = "0" + schapter;
		}
		return (spart + "-" + schapter);
	}

	@SuppressWarnings("unchecked")
	public String getChapter(Chapter chapter) {
		SbApp.trace("BookExporter.getChapterl(...)");
		String buf = "";
		buf += bH2;
		if (isHtml) {
			buf = bH2 + "<a name='" + chapter.getChapternoStr() + "'>";
		}
		if (param.isExportChapterNumbers) {
			if (param.isExportChapterNumbersRoman) {
				buf += (String) LangUtil.intToRoman(chapter.getChapterno());
			} else {
				buf += Integer.toString(chapter.getChapterno());
			}
			if (param.isExportChapterTitles) {
				buf += ": " + chapter.getTitle();
			}
		} else if (param.isExportChapterTitles) {
			buf += chapter.getTitle();
		}
		if (isHtml) {
			buf += "</a>"+eH2+"\n";
		} else {
			buf += "\n";
		}
		if (param.isExportChapterDatesLocs) {
			if (isHtml) {
				buf += bH3;
			}
			buf += DateUtil.getNiceDates((List) chapterDAO.findDates(chapter));
			if (!((List) chapterDAO.findLocations(chapter)).isEmpty()) {
				buf += ": " + ListUtils.join(chapterDAO.findLocations(chapter), ", ");
			}
			if (isHtml) {
				buf += eH3;
			}
		}
		return (buf);
	}

	// get a Scene and replace all absolute path with a relative one
	public String getScene(Scene scene, String path) {
		SbApp.trace("BookExporter.getScene(...) withe path");
		String str = getScene(scene);
		return (IOUtil.convertToRrelativePath(str, path));
	}

	public String getScene(Scene scene) {
		SbApp.trace("BookExporter.getScene(...) without path");
		String buf = "";
		if (strandIdsToExport != null) {
			long l = scene.getStrand().getId();
			if (!strandIdsToExport.contains(l)) {
				return ("");
			}
		}
		if (!scene.getInformative()) {
			if (param.isExportSceneTitles) {
				String t = scene.getTitle();
				if (!t.isEmpty()) {
					t = HtmlUtil.getHtag(t);
					buf += t;
				}
			}
			if (param.isExportSceneDidascalie) {
				buf += getDidascalie(scene);
			}
			if (isHtml) {
				buf += scene.getText() + "\n";
			} else {
				buf += HtmlUtil.htmlToText(scene.getText(), true) + "\n";
			}
			if (isHtml && param.isExportSceneSeparator) {
				buf += "<p style=\"text-align:center\">.oOo.</p>\n";
			}
		}
		if (exportTableOfContentsLink) {
			buf += "<p style='font-size:8px;text-align:left;'><a href='#toc'>"
				+ I18N.getMsg("toc")
				+ "</a></p>\n";
		}
		return (buf);
	}

	public String getDidascalie(Scene scene) {
		SbApp.trace("BookExporter.getDidascalie(...)");
		String rc = "";
		if (param.isExportSceneDidascalie) {
			// personnages
			if (!scene.getPersons().isEmpty()) {
				if (isHtml) {
					rc += "<i><b>" + I18N.getMsg("persons") + "</b> : ";
				} else {
					rc += I18N.getMsg("persons") + ": ";
				}
				for (Person p : scene.getPersons()) {
					rc += p.getFullName() + ", ";
				}
				rc = rc.substring(0, rc.length() - 2);
				if (isHtml) {
					rc += "</i><br>";
				}
				rc += "\n";
			}
			// lieux
			if (!scene.getLocations().isEmpty()) {
				if (isHtml) {
					rc += "<i><b>" + I18N.getMsg("locations") + "</b> : ";
				} else {
					rc += I18N.getMsg("locations") + ": ";
				}
				for (Location p : scene.getLocations()) {
					rc += p.getFullName() + ", ";
				}
				rc = rc.substring(0, rc.length() - 2);
				if (isHtml) {
					rc += "</i><br>";
				}
				rc += "\n";
			}
			// items
			if (!scene.getItems().isEmpty()) {
				if (isHtml) {
					rc += "<i><b>" + I18N.getMsg("items") + "</b> : ";
				} else {
					rc += I18N.getMsg("items") + ": ";
				}
				for (Item p : scene.getItems()) {
					rc += p.getName() + ", ";
				}
				rc = rc.substring(0, rc.length() - 2);
				if (isHtml) {
					rc += "</i><br>";
				}
				rc += "\n";
			}
			if (isHtml) {
				if (!rc.isEmpty()) {
					rc = "<p style=\"text-align:right\">" + rc + "</p>";
				}
			}
		}
		return (rc);
	}
}
