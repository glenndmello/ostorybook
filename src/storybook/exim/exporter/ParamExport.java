/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package storybook.exim.exporter;

import storybook.SbConstants.BookKey;
import storybook.SbPref;
import storybook.SbPref.Key;
import storybook.toolkit.BookUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class ParamExport {

	public MainFrame mainFrame;
	public String format="xml";
	public String dir;
	public String fileName;
	public String csvQuote="\"";
	public String csvComma;
	public boolean txtTab;
	public String txtSeparator;
	public boolean htmlCssUse;
	public String htmlCssFile;
	public boolean isExportChapterBreakPage;
	public boolean isExportChapterNumbers;
	public boolean isExportChapterNumbersRoman;
	public boolean isExportChapterTitles;
	public boolean isExportChapterDatesLocs;
	public boolean isExportSceneTitles;
	public boolean isExportSceneSeparator;
	public boolean isExportSceneDidascalie;
	public boolean isExportPartTitles=false;
	public boolean htmlBookMulti;
	public String pdfPageSize;
	public boolean pdfLandscape;
	public boolean htmlNavImage=false;

	public ParamExport(MainFrame m) {
		mainFrame = m;
		init();
	}

	public void init() {
		format=mainFrame.getPref().getString(Key.EXPORT_PREF, "010");
		csvQuote = mainFrame.getPref().getString(Key.EXPORT_CSV_QUOTE, "'");
		csvComma = mainFrame.getPref().getString(Key.EXPORT_CSV_COMMA, ";");
		txtTab = mainFrame.getPref().getBoolean(Key.EXPORT_TXT_TAB, true);
		if (!txtTab)
			txtSeparator = mainFrame.getPref().getString(Key.EXPORT_TXT_SEPARATOR, "");
		htmlCssUse = BookUtil.getBoolean(mainFrame, BookKey.HTML_USE_CSS);
		htmlNavImage = BookUtil.getBoolean(mainFrame, BookKey.HTML_NAV_IMAGE);
		if (htmlCssUse)
			htmlCssFile = BookUtil.getString(mainFrame, BookKey.HTML_CSS_FILE);
		else
			htmlCssFile = "";
		htmlBookMulti = BookUtil.getBoolean(mainFrame, BookKey.HTML_BOOK_MULTI);
		isExportChapterBreakPage = BookUtil.getBoolean(mainFrame, BookKey.EXPORT_CHAPTER_BREAKPAGE);
		isExportChapterNumbers = BookUtil.getBoolean(mainFrame, BookKey.EXPORT_CHAPTER_NUMBERS);
		isExportChapterNumbersRoman = BookUtil.getBoolean(mainFrame, BookKey.EXPORT_ROMAN_NUMERALS);
		isExportChapterTitles = BookUtil.getBoolean(mainFrame, BookKey.EXPORT_CHAPTER_TITLES);
		isExportChapterDatesLocs = BookUtil.getBoolean(mainFrame, BookKey.EXPORT_CHAPTER_DATES_LOCATIONS);
		isExportSceneTitles = BookUtil.getBoolean(mainFrame, BookKey.EXPORT_SCENE_TITLES);
		isExportSceneSeparator = BookUtil.getBoolean(mainFrame, BookKey.EXPORT_SCENE_SEPARATOR);
		isExportSceneDidascalie = BookUtil.isExportSceneDidascalie(mainFrame);
		isExportPartTitles = BookUtil.isExportPartTitles(mainFrame);
		pdfPageSize = BookUtil.getString(mainFrame, BookKey.PDF_PAGE_SIZE);
		pdfLandscape = BookUtil.getBoolean(mainFrame, BookKey.PDF_LANDSCAPE);
	}

	public void save() {
		mainFrame.getPref().setString(SbPref.Key.EXPORT_CSV_QUOTE, csvQuote);
		mainFrame.getPref().setString(SbPref.Key.EXPORT_CSV_COMMA, csvComma);
		mainFrame.getPref().setBoolean(SbPref.Key.EXPORT_TXT_TAB, txtTab);
		mainFrame.getPref().setString(SbPref.Key.EXPORT_TXT_SEPARATOR, txtSeparator);
		BookUtil.store(mainFrame, BookKey.HTML_USE_CSS.toString(), htmlCssUse);
		BookUtil.store(mainFrame, BookKey.HTML_CSS_FILE.toString(), htmlCssFile);
		BookUtil.store(mainFrame, BookKey.HTML_NAV_IMAGE.toString(), htmlNavImage);
		BookUtil.store(mainFrame, BookKey.HTML_BOOK_MULTI.toString(), htmlBookMulti);
		BookUtil.store(mainFrame, BookKey.EXPORT_CHAPTER_BREAKPAGE.toString(), isExportChapterBreakPage);
		BookUtil.store(mainFrame, BookKey.EXPORT_CHAPTER_NUMBERS.toString(), isExportChapterNumbers);
		BookUtil.store(mainFrame, BookKey.EXPORT_ROMAN_NUMERALS.toString(), isExportChapterNumbersRoman);
		BookUtil.store(mainFrame, BookKey.EXPORT_CHAPTER_TITLES.toString(), isExportChapterTitles);
		BookUtil.store(mainFrame, BookKey.EXPORT_CHAPTER_DATES_LOCATIONS.toString(), isExportChapterDatesLocs);
		BookUtil.store(mainFrame, BookKey.EXPORT_SCENE_TITLES.toString(), isExportSceneTitles);
		BookUtil.store(mainFrame, BookKey.EXPORT_SCENE_SEPARATOR.toString(), isExportSceneSeparator);
		BookUtil.store(mainFrame, BookKey.PDF_PAGE_SIZE.toString(), pdfPageSize);
		BookUtil.store(mainFrame, BookKey.PDF_LANDSCAPE.toString(), pdfLandscape);
	}
	
}
