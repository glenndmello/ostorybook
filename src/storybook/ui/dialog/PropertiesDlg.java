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
package storybook.ui.dialog;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Calendar;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;
import storybook.SbConstants;
import storybook.i18n.I18N;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.entity.Internal;
import storybook.toolkit.BookUtil;
import storybook.toolkit.DateUtil;
import storybook.toolkit.html.HtmlUtil;
import storybook.toolkit.odt.ODTUtils;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class PropertiesDlg extends AbstractDialog {

	private JTextField tfTitle;
	private JTextField tfSubtitle;
	private JTextField tfAuthor;
	private JTextArea taNotes;
	private JTextArea taBlurb;
	private JTextField tfCopyright;
	private JCheckBox cbUseHtmlScenes;
	private JCheckBox cbUseHtmlDescr;
	private JCheckBox cbExportChapterNumbers;
	private JCheckBox cbExportRomanNumerals;
	private JCheckBox cbExportChapterTitles;
	private JCheckBox cbExportChapterDatesLocations;
	private JCheckBox cbExportSceneTitles;
	private JCheckBox cbExportPartTitles;
	private JCheckBox cbDidascalie;
	private JCheckBox cbUseNonModalEditors;
	private JCheckBox cbUseX;
	private JTextField tfExtension;
	private JTextField tfTemplate;
	private JTextField tfName;
	private JTextPane taFileInfo;
	private JButton btTemplate;
	private JLabel lbName;
	private JLabel lbExtension;
	private JLabel lbTemplate;

	public PropertiesDlg(MainFrame m) {
		super(m);
		initAll();
	}
	@Override
	public void init() {
	}
	
	@Override
	public void initUi() {
		super.initUi();
		JTabbedPane tbPane=new JTabbedPane();
		tbPane.addTab(I18N.getMsg("properties"), initProperties());
		tbPane.addTab(I18N.getMsg("preferences.global"), initGeneral());
		tbPane.addTab(I18N.getMsg("xeditor"), initExternEditor());
        tbPane.addTab(I18N.getMsg("file.info"), initFileInfo());
		Dimension dim = new Dimension(640, 480);
		tbPane.setPreferredSize(dim);
		tbPane.setMinimumSize(dim);
		// layout
		setLayout(new MigLayout("wrap,fill"/*, "[]", "[grow][grow]"*/));
		setTitle(I18N.getMsg("properties.title"));
		setIconImage(I18N.getIconImage("icon.sb"));
		add(tbPane,"wrap");
		add(getOkButton(), "span, split 2,sg,right");
		add(getCancelButton(), "sg");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);
	}

	private JPanel initProperties() {
        JLabel lbTitle=new JLabel(I18N.getMsg("title"));
        tfTitle=new JTextField();
		setTextfield(tfTitle, SbConstants.BookKey.TITLE);

        JLabel lbSubtitle=new JLabel(I18N.getMsg("subtitle"));
		tfSubtitle=new JTextField();
		setTextfield(tfSubtitle, SbConstants.BookKey.SUBTITLE);

        JLabel lbAuthor=new JLabel(I18N.getMsg("author_s"));
        tfAuthor=new JTextField();
		setTextfield(tfAuthor, SbConstants.BookKey.AUTHOR);

        JLabel lbCopyright=new JLabel(I18N.getMsg("copyright"));
        tfCopyright=new JTextField();
		setTextfield(tfCopyright, SbConstants.BookKey.COPYRIGHT);

        JLabel lbNotes=new JLabel(I18N.getMsg("notes"));
		taNotes=new JTextArea();
		taNotes = new JTextArea();
		taNotes.setLineWrap(true);
		taNotes.setWrapStyleWord(true);
		setTextArea(taNotes, SbConstants.BookKey.NOTES);
		taNotes.setCaretPosition(0);

        JLabel lbBlurb=new JLabel(I18N.getMsg("blurb")); // NOI18N
		taBlurb = new JTextArea();
		taBlurb.setLineWrap(true);
		taBlurb.setWrapStyleWord(true);
		setTextArea(taBlurb, SbConstants.BookKey.BLURB);
		taBlurb.setCaretPosition(0);
				
		//layout
		JPanel panel=new JPanel();
		panel.setLayout(new MigLayout("wrap 2", "[][grow]", "[][][grow][grow]"));
		panel.add(lbTitle);panel.add(tfTitle, "growx");
		panel.add(lbSubtitle);panel.add(tfSubtitle, "growx");
		panel.add(lbAuthor);panel.add(tfAuthor, "growx");
		panel.add(lbCopyright);panel.add(tfCopyright, "growx");
		JScrollPane scroller = new JScrollPane(taBlurb);
		SwingUtil.setMaxPreferredSize(scroller);
		panel.add(lbBlurb, "top");panel.add(scroller, "grow");
		scroller = new JScrollPane(taNotes);
		SwingUtil.setMaxPreferredSize(scroller);
		panel.add(lbNotes, "top");panel.add(scroller, "grow");

		return(panel);
	}

	private void addTitle(JPanel panel, String i18nKey) {
		JLabel lb = new JLabel(I18N.getMsg(i18nKey));
		lb.setFont(SwingUtil.getFontBold(12));
		panel.add(lb, "span,gaptop 10");
	}
	
	private JCheckBox addCB(String title, boolean value) {
		JCheckBox cb = new JCheckBox(I18N.getMsg(title));
		cb.setSelected(value);
		return(cb);
	}

	private JPanel initGeneral() {
        //JLabel lbDivers=new JLabel(I18N.getMsg("diverse"));
        //lbDivers.setFont(new Font("Noto Sans", 1, 12));
		cbUseHtmlScenes=addCB("properties.use.html.scenes",BookUtil.isUseHtmlScenes(mainFrame));
        cbUseHtmlDescr=addCB("properties.use.html.descr",BookUtil.isUseHtmlDescr(mainFrame));

        cbExportChapterNumbers=addCB("export.chapter.numbers",BookUtil.isExportChapterNumbers(mainFrame));
        cbExportRomanNumerals=addCB("export.roman.numerals",BookUtil.isExportRomanNumerals(mainFrame));
        cbExportChapterTitles=addCB("export.chapter.titles",BookUtil.isExportChapterTitles(mainFrame));
        cbExportChapterDatesLocations=addCB("export.chapter.dates.locations",BookUtil.isExportChapterDatesLocations(mainFrame));
        cbExportSceneTitles=addCB("export.scene.titles",BookUtil.isExportSceneTitle(mainFrame));
        cbExportPartTitles=addCB("export.part.titles",BookUtil.isExportPartTitles(mainFrame));
        cbDidascalie=addCB("export.scene.didascalie",BookUtil.isExportSceneDidascalie(mainFrame));
		//JLabel lbOther=new JLabel(I18N.getMsg("other"));
		//lbOther.setFont(new Font("Noto Sans", 1, 12));
        cbUseNonModalEditors=addCB("editors.nonmodal",BookUtil.isEditorModless(mainFrame));
		
		//layout
		JPanel p=new JPanel();
		p.setLayout(new MigLayout("wrap","[][]", ""));
		addTitle(p,"properties.formatted.title");
		p.add(new JLabel("    "));p.add(cbUseHtmlScenes,"wrap");
		p.add(new JLabel("    "));p.add(cbUseHtmlDescr,"wrap");
		addTitle(p, "export.settings");
		p.add(new JLabel("    "));p.add(cbExportChapterNumbers,"wrap");
		p.add(new JLabel("    "));p.add(cbExportRomanNumerals,"wrap");
		p.add(new JLabel("    "));p.add(cbExportChapterTitles,"wrap");
		p.add(new JLabel("    "));p.add(cbExportChapterDatesLocations,"wrap");
		p.add(new JLabel("    "));p.add(cbExportSceneTitles,"wrap");
		p.add(new JLabel("    "));p.add(cbExportPartTitles,"wrap");
		p.add(new JLabel("    "));p.add(cbDidascalie,"wrap");
		addTitle(p, "other");
		p.add(new JLabel("    "));p.add(cbUseNonModalEditors);
		return(p);
	}

	private JPanel initExternEditor() {
        cbUseX=new JCheckBox(I18N.getMsg("xeditor.askuse")); // NOI18N
        cbUseX.addActionListener(new java.awt.event.ActionListener() {
			@Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeXeditor();
            }
        });

		lbName = new JLabel(I18N.getMsg("xeditor.name"));
        tfName = new JTextField();
		
		lbExtension = new JLabel(I18N.getMsg("xeditor.extension"));
		tfExtension = new JTextField();
		tfExtension.setColumns(4);

		lbTemplate = new JLabel(I18N.getMsg("xeditor.template"));
        tfTemplate = new JTextField();
		btTemplate=new JButton();
		btTemplate.setIcon(new ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/file-open.png")));
		btTemplate.setMargin(new Insets(0,0,0,0));
        btTemplate.addActionListener((java.awt.event.ActionEvent evt) -> {
			JFileChooser chooser = new JFileChooser(tfTemplate.getText());
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int i = chooser.showOpenDialog(null);
			if (i != 0) return;
			File file = chooser.getSelectedFile();
			tfTemplate.setText(file.getAbsolutePath());
			tfTemplate.setCaretPosition(0);
		});
		
		if (BookUtil.isUseXeditor(mainFrame)) {
			cbUseX.setSelected(BookUtil.isUseXeditor(mainFrame));
			setTextfield(tfName,SbConstants.BookKey.XEDITOR_NAME);
			setTextfield(tfExtension,SbConstants.BookKey.XEDITOR_EXTENSION);
			setTextfield(tfTemplate,SbConstants.BookKey.XEDITOR_TEMPLATE);
		} else {
			lbName.setEnabled(false);
			lbExtension.setEnabled(false);
			lbTemplate.setEnabled(false);
			tfName.setEnabled(false);
			tfExtension.setEnabled(false);
			tfTemplate.setEnabled(false);
			btTemplate.setEnabled(false);
		}

		//layout
		JPanel p=new JPanel();
		//p.setLayout(new MigLayout("wrap,fill", "", "[grow][]"));
		p.setLayout(new MigLayout("wrap 3","[][grow][]", "[][][][]"));
				
		p.add(cbUseX,"span");
		p.add(lbName);p.add(tfName,"growx, wrap");
		p.add(lbExtension);p.add(tfExtension,"wrap");
		p.add(lbTemplate);p.add(tfTemplate,"growx");p.add(btTemplate);
		
		return(p);
	}

	private void changeXeditor() {
		boolean b=false;
		if (cbUseX.isSelected()) {
			b=true;
		}
		lbName.setEnabled(b);
		lbExtension.setEnabled(b);
		lbTemplate.setEnabled(b);
		tfName.setEnabled(b);
		tfExtension.setEnabled(b);
		tfTemplate.setEnabled(b);
		btTemplate.setEnabled(b);
	}

	private JPanel initFileInfo() {
		int textLength = ODTUtils.getBookSize(mainFrame);
		int words = ODTUtils.getBookWords(mainFrame);

		String creationDate = DateUtil.simpleDateToString(BookUtil.getBookCreationDate(mainFrame));
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl sceneDao = new SceneDAOImpl(session);

		File file = mainFrame.getDbFile().getFile();

		PartDAOImpl partDao = new PartDAOImpl(session);
		ChapterDAOImpl chapterDao = new ChapterDAOImpl(session);
		PersonDAOImpl personDao = new PersonDAOImpl(session);
		LocationDAOImpl locationDao = new LocationDAOImpl(session);
		TagDAOImpl tagDao = new TagDAOImpl(session);
		ItemDAOImpl itemDao = new ItemDAOImpl(session);

		StringBuilder buf = new StringBuilder();
		buf.append("<html>");
		buf.append(HtmlUtil.getHeadWithCSS(mainFrame.getFont()));
		buf.append("<body><table>");
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("file.info.filename")+": ", file.toString()));
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("file.info.creation")+": ", creationDate));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(file.lastModified());
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("file.info.last.mod")+": ", DateUtil.calendarToString(cal)));
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("file.info.text.length")+": ", Integer.toString(textLength)));
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("file.info.words")+": ", Integer.toString(words)));
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("parts")+": ", Integer.toString(partDao.count(null))));
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("chapters")+": ", Integer.toString(chapterDao.count(null))));
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("scenes")+": ", Integer.toString(sceneDao.count(null))));
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("persons")+": ", Integer.toString(personDao.count(null))));
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("locations")+": ", Integer.toString(locationDao.count(null))));
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("tags")+": ", Integer.toString(tagDao.count(null))));
		buf.append(HtmlUtil.getRow2Cols(I18N.getMsg("items")+": ", Integer.toString(itemDao.count(null))));
		buf.append("</table></body></html>");
		model.commit();
        taFileInfo = new JTextPane();
        taFileInfo.setEditable(false);
        taFileInfo.setContentType("text/html");
		taFileInfo.setText(buf.toString());
		taFileInfo.setCaretPosition(0);
		JScrollPane scroller = new JScrollPane();
        scroller.setViewportView(taFileInfo);
		
		//layout
		JPanel p=new JPanel();
		p.setLayout(new MigLayout("wrap,fill", "", "[grow]"));
		p.add(scroller,"grow");
		return(p);
	}
	
	private void setTextfield(JTextField tx, SbConstants.BookKey bookKey) {
		Internal internal = BookUtil.get(mainFrame, bookKey, "");
		tx.setText(internal.getStringValue());
	}

	private void setTextArea(JTextArea tx, SbConstants.BookKey bookKey) {
		Internal internal = BookUtil.get(mainFrame, bookKey, "");
		tx.setText(internal.getStringValue());
		tx.setCaretPosition(0);
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applySettings();
			}
		};
	}
	
	private void applySettings() {
		if (cbUseX.isSelected()) {
			if (tfName.getText().isEmpty()) {
				errorMessage("xeditor.err.missing.name");
				return;
			}
			if (tfExtension.getText().isEmpty()) {
				errorMessage("xeditor.err.missing.extension");
				return;
			}
			if (tfTemplate.getText().isEmpty()) {
				errorMessage("xeditor.err.missing.template");
				return;
			}
			BookUtil.store(mainFrame, SbConstants.BookKey.XEDITOR_USE, cbUseX.isSelected());
			BookUtil.store(mainFrame, SbConstants.BookKey.XEDITOR_NAME, tfName.getText());
			BookUtil.store(mainFrame, SbConstants.BookKey.XEDITOR_TEMPLATE, tfTemplate.getText());
			BookUtil.store(mainFrame, SbConstants.BookKey.XEDITOR_EXTENSION, tfExtension.getText());
		} else {
			BookUtil.store(mainFrame, SbConstants.BookKey.XEDITOR_USE, false);
			BookUtil.store(mainFrame, SbConstants.BookKey.XEDITOR_NAME, "");
			BookUtil.store(mainFrame, SbConstants.BookKey.XEDITOR_TEMPLATE, "");
			BookUtil.store(mainFrame, SbConstants.BookKey.XEDITOR_EXTENSION, "");
		}
		BookUtil.store(mainFrame, SbConstants.BookKey.EXPORT_PART_TITLES, cbExportPartTitles.isSelected());
		BookUtil.store(mainFrame, SbConstants.BookKey.USE_HTML_SCENES, cbUseHtmlScenes.isSelected());
		BookUtil.store(mainFrame, SbConstants.BookKey.USE_HTML_DESCR, cbUseHtmlDescr.isSelected());
		BookUtil.store(mainFrame, SbConstants.BookKey.EXPORT_CHAPTER_NUMBERS, cbExportChapterNumbers.isSelected());
		BookUtil.store(mainFrame, SbConstants.BookKey.EXPORT_ROMAN_NUMERALS, cbExportRomanNumerals.isSelected());
		BookUtil.store(mainFrame, SbConstants.BookKey.EXPORT_CHAPTER_TITLES, cbExportChapterTitles.isSelected());
		BookUtil.store(mainFrame, SbConstants.BookKey.EXPORT_CHAPTER_DATES_LOCATIONS, cbExportChapterDatesLocations.isSelected());
		BookUtil.store(mainFrame, SbConstants.BookKey.EXPORT_SCENE_TITLES, cbExportSceneTitles.isSelected());
		BookUtil.store(mainFrame, SbConstants.BookKey.EXPORT_SCENE_DIDASCALIE, cbDidascalie.isSelected());
		BookUtil.store(mainFrame, SbConstants.BookKey.EDITOR_MODLESS, cbUseNonModalEditors.isSelected());
		// properties
		BookUtil.store(mainFrame, SbConstants.BookKey.TITLE, tfTitle.getText());
		BookUtil.store(mainFrame, SbConstants.BookKey.SUBTITLE, tfSubtitle.getText());
		BookUtil.store(mainFrame, SbConstants.BookKey.AUTHOR, tfAuthor.getText());
		BookUtil.store(mainFrame, SbConstants.BookKey.COPYRIGHT, tfCopyright.getText());
		BookUtil.store(mainFrame, SbConstants.BookKey.BLURB, taBlurb.getText());
		BookUtil.store(mainFrame, SbConstants.BookKey.NOTES, taNotes.getText());
		dispose();
		mainFrame.setTitle();
		//mainFrame.getBookController().fireAgain();
		mainFrame.refresh();
	}
	
	private void errorMessage(String s) {
		JOptionPane.showMessageDialog(this,
			I18N.getMsg(s),
			I18N.getMsg("error"), JOptionPane.ERROR_MESSAGE);
	}

}
