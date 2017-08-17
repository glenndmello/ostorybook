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
package storybook.ui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.*;

import storybook.SbApp;
import storybook.SbConstants;
import static storybook.SbConstants.ViewName.*;
import storybook.SbPref;
import storybook.exim.exporter.BookExporter;
import storybook.i18n.I18N;
import storybook.i18n.I18NDlg;
import storybook.model.EntityUtil;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Memo;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Relationship;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;
import storybook.toolkit.BookUtil;
import storybook.toolkit.DockingWindowUtil;
import storybook.toolkit.TextTransfer;
import storybook.toolkit.net.NetUtil;
import storybook.toolkit.net.Updater;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.dialog.AboutDlg;
import storybook.ui.dialog.CreateChaptersDlg;
import storybook.ui.dialog.FoiDlg;
import storybook.ui.dialog.PreferencesDlg;
import storybook.ui.dialog.RenameDlg;
import storybook.ui.dialog.ReplaceDlg;
import storybook.ui.dialog.SearchDlg;
import storybook.ui.dialog.SpellDlg;
import storybook.ui.dialog.ToolBarDlg;
import storybook.ui.dialog.EntityCopyDlg;
import storybook.ui.options.OptionsDlg;

/**
 *
 * @author favdb
 */
public class SbMenu {
	private final MainFrame mainFrame;
	public boolean testFunction=false;

    public JToolBar toolBar;
    private JButton btFileNew;
    private JButton btFileOpen;
    private JButton btFileSave;
    private JButton btIdea;
    private JButton btMemo;
    private JButton btNewChapter;
    private JButton btNewGender;
    private JButton btNewItem;
    private JButton btNewItemlink;
    private JButton btNewLocation;
    private JButton btNewPart;
    private JButton btNewPerson;
    private JButton btNewRelationship;
    private JButton btNewScene;
    private JButton btNewStrand;
    private JButton btNewTag;
    private JButton btNewTaglink;
    public JButton btNextPart;
    public JButton btPreviousPart;
    private JButton btTabChapter;
    private JButton btTabGender;
    private JButton btTabIdea;
    private JButton btTabItem;
    private JButton btTabItemLink;
    private JButton btTabLocation;
    private JButton btTabMemo;
    private JButton btTabPart;
    private JButton btTabPerson;
    private JButton btTabRelationship;
    private JButton btTabScene;
    private JButton btTabStrand;
    private JButton btTabTag;
    private JButton btTabTagLink;
    private JButton btViewBook;
    private JButton btViewChrono;
    private JButton btViewManage;
    private JButton btViewMemoria;
    private JButton btViewReading;
    private JButton btViewStoryboard;
    private JButton btViewTypist;
	
    public JMenuBar menuBar;
    private JMenuItem chartOccurrenceOfItems;
    private JMenuItem chartOccurrenceOfLocations;
    private JMenuItem chartOccurrenceOfPersons;
    private JMenuItem chartPersonsByDate;
    private JMenuItem chartPersonsByScene;
    private JMenuItem chartStrandsByDate;
    private JMenuItem chartWIWW;
    private JMenuItem chartsAttributes;
    private JMenuItem devTest;
    private JMenuItem editCopyBlurb;
    private JMenuItem editCopyBook;
    private JMenuItem editPreferences;
    private JMenuItem editSpellChecker;
    private JMenuItem fileClose;
    private JMenuItem fileExit;
    private JMenu fileExport;
    private JMenuItem fileExportHTML;
    private JMenuItem fileExportOptions;
    private JMenuItem fileExportOther;
    private JMenuItem fileExportXml;
    private JMenuItem fileImport;
    private JMenuItem fileNew;
    private JMenuItem fileOpen;
    public JMenu fileOpenRecent;
    private JMenuItem fileProperties;
    private JMenuItem fileRename;
    public JMenuItem fileSave;
    private JMenuItem fileSaveAs;
    private JMenuItem helpAbout;
    private JMenuItem helpCheckUpdates;
    private JMenuItem helpDoc;
    private JMenuItem helpFaq;
    private JMenuItem helpHome;
    private JMenuItem helpReportBug;
    private JCheckBoxMenuItem helpTrace;
    private JMenuItem helpTranslate;
    private JMenuItem menuToolsChaptersOrder;
    private JMenu menuToolsRename;
	private JMenu menuCharts;
    private JMenu menuEdit;
    private JMenu menuFile;
    private JMenu menuHelp;
    private JMenu menuNewEntity;
    public JMenu menuParts;
    private JMenu menuPrimaryObjects;
    private JMenu menuSecondaryObjects;
    private JMenu menuTables;
    private JMenu menuTools;
    private JMenuItem menuToolsSearch;
    private JMenu menuView;
    private JMenu menuWindow;
    private JMenuItem newCategory;
    private JMenuItem newChapter;
    private JMenuItem newChapters;
    private JMenuItem newFOI;
    private JMenuItem newGender;
    private JMenuItem newIdea;
    private JMenuItem newItem;
    private JMenuItem newItemLink;
    private JMenuItem newLocation;
    private JMenuItem newMemo;
    private JMenuItem newPart;
    private JMenuItem newPerson;
    private JMenuItem newRelationships;
    private JMenuItem newScene;
    private JMenuItem newStrand;
    private JMenuItem newTag;
    private JMenuItem newTagLink;
    public JMenuItem partNext;
    public JMenuItem partPrevious;
    private JMenuItem renameCity;
    private JMenuItem renameCountry;
    private JMenuItem renameItemCategory;
    private JMenuItem renameTagCategory;
    private JMenuItem tabAttribute;
    private JMenuItem tabCategory;
    private JMenuItem tabChapter;
    private JMenuItem tabGender;
    private JMenuItem tabIdea;
    private JMenuItem tabItem;
    private JMenuItem tabItemLink;
    private JMenuItem tabLocation;
    private JMenuItem tabMemo;
    private JMenuItem tabPart;
    private JMenuItem tabPerson;
    private JMenuItem tabRelationship;
    private JMenuItem tabScene;
    private JMenuItem tabStrand;
    private JMenuItem tabTag;
    private JMenuItem tabTagLink;
    private JMenuItem toolsPlan;
    private JMenuItem toolsTaskList;
    private JMenuItem vueBook;
    private JMenuItem vueChrono;
    private JMenuItem vueInfo;
    private JMenuItem vueMemos;
    private JMenuItem vueManageScene;
    private JMenuItem vueMemoria;
    private JMenuItem vueNavigation;
    private JMenuItem vueReading;
    private JMenuItem vueStoryboard;
    private JMenuItem vueTree;
    private JMenuItem vueTypist;
    private JMenuItem windowBook;
    private JMenuItem windowChrono;
    private JMenuItem windowDefaultLayout;
    public JMenu windowLoadLayout;
    private JMenuItem windowManage;
    private JMenuItem windowPersonsAndLocations;
    private JMenuItem windowReading;
    private JMenuItem windowRefresh;
    private JMenuItem windowResetLayout;
    private JMenuItem windowSaveLayout;
    private JMenuItem windowTagsAndItems;
	private JMenuItem menuToolsReplace;
	public JCheckBoxMenuItem allParts;
	private JMenuItem editCopyEntity;
	private JMenuItem vueOptions;
	private JMenuItem menuToolsScenesOrder;
	private JPopupMenu.Separator separator1;
	private JPopupMenu.Separator separator2;
	private JPopupMenu.Separator separator3;
	private JPopupMenu.Separator separator4;
	
	public SbMenu(MainFrame m) {
		mainFrame=m;
		init();
	}
	
	private void init() {
		//create ToolBar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setName("MainToolbar");
        toolBar.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toolBarMouseClicked(evt);
            }
        });

		initTbFile();
        toolBar.add(new JToolBar.Separator());
		initTbNew();
        toolBar.add(new JToolBar.Separator());
		initTbTab();
        toolBar.add(new JToolBar.Separator());
		initTbView();
        toolBar.add(new JToolBar.Separator());
		initTbPart();
        toolBar.add(new JToolBar.Separator());
		initTbMemo();

		//create MenuBar
		menuBar = new JMenuBar();
        menuBar.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        menuBar.setName("menuBar");
		initMenuFile();
		initMenuEdit();
		initMenuNew();
		initMenuTable();
		initMenuTools();
		initMenuView();
		initMenuCharts();
		initMenuParts();
		initMenuWindow();
		initMenuHelp();
		
		setToolbar();
	}
	
	private void toolBarMouseClicked(MouseEvent evt) {
		if (SwingUtilities.isRightMouseButton(evt)) {
			ToolBarDlg dlg = new ToolBarDlg(mainFrame);
			dlg.setVisible(true);
			if (!dlg.isCanceled()) {
				setToolbar();
			}
		}
	}
	
	private void setToolbar() {
		JButton[] btn = {
			btFileNew, btFileOpen, btFileSave, //3
			btNewStrand, btNewPart, btNewChapter, btNewScene,//4
			btNewPerson, btNewGender, btNewRelationship,//3
			btNewLocation,//1
			btNewItem, btNewItemlink,//2
			btNewTag, btNewTaglink, //2
			btMemo, btIdea, //2
			btTabStrand, btTabPart, btTabChapter, btTabScene,//4
			btTabPerson, btTabGender, btTabRelationship, //3
			btTabLocation, //1
			btTabItem, btTabItemLink,//2
			btTabTag, btTabTagLink, //2
			btTabMemo, btTabIdea,//2
			btViewChrono, btViewBook, btViewReading, btViewManage, btViewMemoria, btViewStoryboard, btViewTypist //6
		}; // total 37
		//récupération du paramétrage sous forme d'un
		String param = mainFrame.getPref().get(SbPref.Key.TOOLBAR.toString(), SbPref.Default.TOOLBAR.toString());
		while (param.length() < btn.length) {
			param += "1";// default allParts button are visible
		}
		if (param.length() > btn.length) {
			param = param.substring(0, btn.length);
		}
		for (int i = 0; i < param.length(); i++) {
			btn[i].setVisible((param.charAt(i) == '1'));
		}
		toolBar.setFloatable(true);
	}

	private JButton initButton(String icon, String tips) {
		return(SwingUtil.createButton("", icon, tips));
	}
	
	private void initTbFile() {
		btFileNew=initButton("16x16/file-new","file.new");
        btFileNew.addActionListener((ActionEvent evt) -> {
			mainFrame.fileNewAction();
		});
        toolBar.add(btFileNew);

        btFileOpen = initButton("16x16/file-open","file.open");
        btFileOpen.addActionListener((ActionEvent evt) -> {
			mainFrame.fileOpenAction();
		});
        toolBar.add(btFileOpen);

        btFileSave = initButton("16x16/file-save","file.save");
        btFileSave.addActionListener((ActionEvent evt) -> {
			mainFrame.fileSaveAction();
		});
        toolBar.add(btFileSave);

	}
	
	private void initTbNew() {
        btNewStrand = initButton("16x16/strand","strand.new");
        btNewStrand.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Strand());
		});
        toolBar.add(btNewStrand);

        btNewPart = initButton("16x16/part","part.new");
        btNewPart.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Part());
		});
        toolBar.add(btNewPart);

        btNewChapter = initButton("16x16/chapter","chapter.new");
        btNewChapter.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Chapter());
		});
        toolBar.add(btNewChapter);

        btNewScene = initButton("16x16/scene","scene.new");
        btNewScene.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Scene());
		});
        toolBar.add(btNewScene);

        btNewPerson = initButton("16x16/person","person.new");
        btNewPerson.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Person());
		});
        toolBar.add(btNewPerson);

        btNewGender = initButton("16x16/gender","gender.new");
        btNewGender.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Gender());
		});
        toolBar.add(btNewGender);

        btNewRelationship = initButton("16x16/group","relationship.new");
        btNewRelationship.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Relationship());
		});
        toolBar.add(btNewRelationship);

        btNewLocation = initButton("16x16/location","location.new");
        btNewLocation.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Location());
		});
        toolBar.add(btNewLocation);

        btNewItem = initButton("16x16/item","item.new");
        btNewItem.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Item());
		});
        toolBar.add(btNewItem);

        btNewItemlink = initButton("16x16/itemlink","itemlink.new");
        btNewItemlink.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new ItemLink());
		});
        toolBar.add(btNewItemlink);

        btNewTag = initButton("16x16/tag","tag.new");
        btNewTag.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Tag());
		});
        toolBar.add(btNewTag);

        btNewTaglink = initButton("16x16/taglink","taglink.new");
        btNewTaglink.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new TagLink());
		});
        toolBar.add(btNewTaglink);

	}
	
	private void initTbTab() {
        btTabStrand = initButton("16x32/manage_strands","strands");
        btTabStrand.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(STRANDS);
		});
        toolBar.add(btTabStrand);

        btTabPart = initButton("16x32/manage_parts","parts");
        btTabPart.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(PARTS);
		});
        toolBar.add(btTabPart);

        btTabChapter = initButton("16x32/manage_chapters","chapters");
        btTabChapter.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHAPTERS);
		});
        toolBar.add(btTabChapter);

        btTabScene = initButton("16x32/manage_scenes","scenes");
        btTabScene.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(SCENES);
		});
        toolBar.add(btTabScene);

        btTabPerson = initButton("16x32/manage_persons","persons");
        btTabPerson.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(PERSONS);
		});
        toolBar.add(btTabPerson);

        btTabGender = initButton("16x32/manage_genders","genders");
        btTabGender.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(GENDERS);
		});
        toolBar.add(btTabGender);

        btTabRelationship = initButton("16x32/manage_relationships","relationship");
        btTabRelationship.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(RELATIONSHIPS);
		});
        toolBar.add(btTabRelationship);

        btTabLocation = initButton("16x32/manage_locations","locations");
        btTabLocation.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(LOCATIONS);
		});
        toolBar.add(btTabLocation);

        btTabItem = initButton("16x32/manage_items","items");
        btTabItem.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(ITEMS);
		});
        toolBar.add(btTabItem);

        btTabItemLink = initButton("16x32/manage_item_links","item.links");
        btTabItemLink.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(ITEMLINKS);
		});
        toolBar.add(btTabItemLink);

        btTabTag = initButton("16x32/manage_tags","tags");
        btTabTag.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(TAGS);
		});
        toolBar.add(btTabTag);

        btTabTagLink = initButton("16x32/manage_tag_links","tags.links");
        btTabTagLink.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(TAGLINKS);
		});
        toolBar.add(btTabTagLink);

        btTabMemo = initButton("16x32/manage_memos","memos");
        btTabMemo.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(MEMOS);
		});
        toolBar.add(btTabMemo);

        btTabIdea = initButton("16x32/manage_ideas","ideas");
        btTabTagLink.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(IDEAS);
		});
        toolBar.add(btTabIdea);

	}

	private void initTbView() {
        btViewChrono = initButton("16x16/chrono_view","view.chrono");
        btViewChrono.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHRONO);
		});
        toolBar.add(btViewChrono);

        btViewBook = initButton("16x16/book_view","view.book");
        btViewBook.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(BOOK);
		});
        toolBar.add(btViewBook);

        btViewManage = initButton("16x16/manage_view","view.manage");
        btViewManage.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(MANAGE);
		});
        toolBar.add(btViewManage);

        btViewReading = initButton("16x16/reading","view.reading");
        btViewReading.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(READING);
		});
        toolBar.add(btViewReading);

        btViewMemoria = initButton("16x16/memoria","view.pov");
        btViewMemoria.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(MEMORIA);
		});
        toolBar.add(btViewMemoria);

        btViewStoryboard = initButton("16x16/stamp","view.storyboard");
        btViewStoryboard.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(STORYBOARD);
		});
        toolBar.add(btViewStoryboard);
		
		toolBar.addSeparator();

        btViewTypist = initButton("16x16/typist","typist");
        btViewTypist.addActionListener((ActionEvent evt) -> {
			//TypistFrame.show(mainFrame, null);
			mainFrame.activateTypist();
		});
        toolBar.add(btViewTypist);

	}

	private void initTbMemo() {
        btMemo = initButton("16x16/memo","memos");
        btMemo.addActionListener((ActionEvent evt) -> {
			//mainFrame.showAndFocus(MEMOS);
			mainFrame.showEditorAsDialog(new Memo());
		});
        toolBar.add(btMemo);

        btIdea = initButton("16x16/idea","ideas");
        btIdea.addActionListener((ActionEvent evt) -> {
			//mainFrame.showAndFocus(IDEAS);
			mainFrame.showEditorAsDialog(new Idea());
		});
        toolBar.add(btIdea);

	}

	private void initTbPart() {
        btPreviousPart = initButton("16x16/arrowleft","part.previous");
        btPreviousPart.addActionListener((ActionEvent evt) -> {
			mainFrame.getSbActionManager().getActionHandler().handlePreviousPart();
		});
        toolBar.add(btPreviousPart);

        btNextPart = initButton("16x16/arrowright","part.next");
        btNextPart.addActionListener((ActionEvent evt) -> {
			mainFrame.getSbActionManager().getActionHandler().handleNextPart();
		});
        toolBar.add(btNextPart);

	}
	
	private JMenuItem initMenuItem(String title) {
		return(initMenuItem("","",' ',title,""));
	}
	
	private JMenuItem initMenuItem(String icon, String key,char mnemonic,String text,String name) {
		JMenuItem m = new JMenuItem();
        if (!key.isEmpty()) {
			KeyStroke k = KeyStroke.getKeyStroke(key);
			if (k!=null) m.setAccelerator(k);
		}
        if (!icon.isEmpty()) m.setIcon(new ImageIcon(getClass().getResource("/storybook/resources/icons/"+icon+".png")));
        if (mnemonic!=' ') m.setMnemonic(mnemonic);
        m.setText(I18N.getMsg(text));
        m.setActionCommand(name);
        m.setName(name);
		return(m);
	}

	private void initMenuFile() {
        menuFile = new JMenu();
        menuFile.setMnemonic(I18N.getMsg("file.mnemonic").charAt(0));
        menuFile.setText(I18N.getMsg("file"));

        fileNew = initMenuItem("16x16/file-new","ctrl N",'N',"file.new","new-command");
        fileNew.addActionListener((ActionEvent evt) -> {
			mainFrame.fileNewAction();
		});
        menuFile.add(fileNew);
		
        fileOpen = initMenuItem("16x16/file-open","ctrl O",'O',"file.open","open-command");
        fileOpen.addActionListener((ActionEvent evt) -> {
			mainFrame.fileOpenAction();
		});
        menuFile.add(fileOpen);
		
        fileOpenRecent = new JMenu();
        fileOpenRecent.setText(I18N.getMsg("file.open.recent"));
        fileOpenRecent.setActionCommand("recent-menu-command");
        fileOpenRecent.setName("recent-menu-command");
        menuFile.add(fileOpenRecent);

		fileSave = initMenuItem("16x16/file-save","ctrl S",'S',"file.save","save-command");
        fileSave.setEnabled(false);
        fileSave.addActionListener((ActionEvent evt) -> {
			mainFrame.fileSaveAction();
		});
        menuFile.add(fileSave);

		fileSaveAs = initMenuItem("16x16/file-save-as","ctrl A",'A',"file.save.as","export-book-command");
        fileSaveAs.addActionListener((ActionEvent evt) -> {
			mainFrame.fileSaveAsAction();
		});
        menuFile.add(fileSaveAs);

        fileRename = initMenuItem("16x16/rename","ctrl R",'R',"rename","rename-command");
        fileRename.addActionListener((ActionEvent evt) -> {
			mainFrame.fileRenameAction();
		});
        menuFile.add(fileRename);

        fileClose = initMenuItem("16x16/close","ctrl F4",' ',"file.close","close-command");
        fileClose.addActionListener((ActionEvent evt) -> {
			mainFrame.close(false);
		});
        menuFile.add(fileClose);
		
        separator1=new JPopupMenu.Separator();
		menuFile.add(separator1);
		
        fileProperties = initMenuItem("file.properties");
        fileProperties.addActionListener((ActionEvent evt) -> {
			mainFrame.filePropertiesAction();
		});
        menuFile.add(fileProperties);
		
		separator2=new JPopupMenu.Separator();
        menuFile.add(separator2);

		fileImport = initMenuItem("file.import");
        fileImport.addActionListener((ActionEvent evt) -> {
			mainFrame.fileImportAction();
		});
        menuFile.add(fileImport);

        fileExport = new JMenu(I18N.getMsg("file.export"));

        fileExportHTML = initMenuItem("16x16/book_view","",' ',"export.file.html","exportHtml");
        fileExportHTML.addActionListener((ActionEvent evt) -> {
			mainFrame.fileExportBookAction();
		});
        fileExport.add(fileExportHTML);

        fileExportXml = initMenuItem("16x16/columns","",' ',"file.export.base","exportXml");
        fileExportXml.addActionListener((ActionEvent evt) -> {
			mainFrame.fileExportXmlAction();
		});
        fileExport.add(fileExportXml);

        fileExportOther = initMenuItem("16x16/expand","",' ',"file.export.other","exportOther");
        fileExportOther.addActionListener((ActionEvent evt) -> {
			mainFrame.fileExportOtherAction();
		});
        fileExport.add(fileExportOther);

        separator3=new JPopupMenu.Separator();
		fileExport.add(separator3);

		fileExportOptions = initMenuItem("16x16/hammer","",' ',"options","");
        fileExportOptions.addActionListener((ActionEvent evt) -> {
			mainFrame.fileExportOptionsAction();
		});
        fileExport.add(fileExportOptions);

        menuFile.add(fileExport);
		
		separator4=new JPopupMenu.Separator();
        menuFile.add(separator4);

		fileExit = initMenuItem("16x16/exit","alt F4",' ',"exit","exit-command");
        fileExit.addActionListener((ActionEvent evt) -> {
			mainFrame.close(true);
			SbApp.getInstance().exit();
		});
        menuFile.add(fileExit);
		
        menuBar.add(menuFile);
	}

	private void initMenuEdit() {
        menuEdit = new JMenu(I18N.getMsg("edit"));
        menuEdit.setMnemonic(I18N.getMsg("edit.mnemonic").charAt(0));
        menuEdit.setName("edit-menu-command");

        editCopyBook = initMenuItem("16x16/edit-copy","",'C',"book.copy.text","bookCopy");
        editCopyBook.addActionListener((ActionEvent evt) -> {
			BookExporter.toClipboard(mainFrame,true);
		});
        menuEdit.add(editCopyBook);

        editCopyBlurb = initMenuItem("16x16/edit-copy","",'P',"book.copy.blurb","blurbCopy");
        editCopyBlurb.addActionListener((ActionEvent evt) -> {
			Internal internal = BookUtil.get(mainFrame, SbConstants.BookKey.BLURB, "");
			TextTransfer tf = new TextTransfer();
			tf.setClipboardContents(internal.getStringValue() + "\n");
		});
        menuEdit.add(editCopyBlurb);

		editCopyEntity = initMenuItem("copy.menu");
        editCopyEntity.addActionListener((ActionEvent evt) -> {
			EntityCopyDlg.show(mainFrame);
		});
        menuEdit.add(editCopyEntity);

        menuEdit.add(new JPopupMenu.Separator());

		editPreferences = initMenuItem("preferences.title");
        editPreferences.addActionListener((ActionEvent evt) -> {
			PreferencesDlg dlg = new PreferencesDlg(mainFrame);
			SwingUtil.showModalDialog(dlg, mainFrame);
		});
        menuEdit.add(editPreferences);

        editSpellChecker = initMenuItem("preferences.spelling");
        editSpellChecker.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jortho/icon.png")));
        editSpellChecker.addActionListener((ActionEvent evt) -> {
			SpellDlg dlg = new SpellDlg(mainFrame);
			dlg.setVisible(true);
		});
        menuEdit.add(editSpellChecker);

        menuBar.add(menuEdit);
	}

	private void initMenuNew() {
		menuNewEntity = new JMenu(I18N.getMsg("new"));

        newStrand = initMenuItem("16x16/strand","",'S',"strand","newStrand");
        newStrand.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Strand());
		});
        menuNewEntity.add(newStrand);

        newPart = initMenuItem("16x16/part","",'P',"part","newPart");
        newPart.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Part());
		});
        menuNewEntity.add(newPart);

        newChapter = initMenuItem("16x16/chapter","shift C",'C',"chapter","newChapter");
        newChapter.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Chapter());
		});
        menuNewEntity.add(newChapter);

        newChapters = initMenuItem("","",'M',"chapters.generate","newChapters");
        newChapters.addActionListener((ActionEvent evt) -> {
			CreateChaptersDlg dlg = new CreateChaptersDlg(mainFrame);
			SwingUtil.showModalDialog(dlg, mainFrame);
		});
        menuNewEntity.add(newChapters);

        newScene = initMenuItem("16x16/scene","shift S",'S',"scene","newScene");
        newScene.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Scene());
		});
        menuNewEntity.add(newScene);
        menuNewEntity.add(new JPopupMenu.Separator());

		newPerson = initMenuItem("16x16/person","shift P",'P',"person","newPerson");
        newPerson.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Person());
		});
        menuNewEntity.add(newPerson);

        newGender = initMenuItem("16x16/gender","",' ',"genders","newGender");
        newGender.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Gender());
		});
        menuNewEntity.add(newGender);

        newCategory = initMenuItem("16x16/category","",' ',"persons.category","newCategory");
        newCategory.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Category());
		});
        menuNewEntity.add(newCategory);

        newRelationships = initMenuItem("16x16/relationship","",' ',"relationship","newRelationship");
        newRelationships.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Relationship());
		});
        menuNewEntity.add(newRelationships);
        menuNewEntity.add(new JPopupMenu.Separator());

		newLocation = initMenuItem("16x16/location","shift L",'L',"location","newLocation");
        newLocation.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Location());
		});
        menuNewEntity.add(newLocation);
        menuNewEntity.add(new JPopupMenu.Separator());

		newItem = initMenuItem("16x16/item","shift I",'I',"item","newItem");
        newItem.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Item());
		});
        menuNewEntity.add(newItem);

        newItemLink = initMenuItem("16x16/link","",' ',"item.link","newItemLink");
        newItemLink.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new ItemLink());
		});
        menuNewEntity.add(newItemLink);
        menuNewEntity.add(new JPopupMenu.Separator());

		newTag = initMenuItem("16x16/tag","shift T",'T',"tag","newTag");
        newTag.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Tag());
		});
        menuNewEntity.add(newTag);

        newTagLink = initMenuItem("16x16/link","",' ',"tag.link","newTagLink");
        newTagLink.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new TagLink());
		});
        menuNewEntity.add(newTagLink);
        menuNewEntity.add(new JPopupMenu.Separator());

		newFOI = initMenuItem("16x16/bulb","ctrl F",' ',"foi.title","newFOI");
        newFOI.addActionListener((ActionEvent evt) -> {
			FoiDlg.show(mainFrame);
		});
        menuNewEntity.add(newFOI);

        newIdea = initMenuItem("16x16/bulb","ctrl shift F",' ',"idea.table.idea","newIdea");
        newIdea.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Idea());
		});
        menuNewEntity.add(newIdea);

        newMemo = initMenuItem("16x16/memo","ctrl shift M",' ',"memo","newMemo");
        newMemo.addActionListener((ActionEvent evt) -> {
			mainFrame.newEntity(new Memo());
		});
        menuNewEntity.add(newMemo);

        menuBar.add(menuNewEntity);
	}

	private void initMenuTable() {
		menuTables = new JMenu(I18N.getMsg("tables"));
		
        menuPrimaryObjects = new JMenu(I18N.getMsg("primary.objects"));

		tabStrand = initMenuItem("16x32/manage_strands","ctrl alt D",'D',"strand","tabStrand");
        tabStrand.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(STRANDS);
		});
        menuPrimaryObjects.add(tabStrand);

        tabPart = initMenuItem("16x32/manage_parts","ctrl alt T",'T',"part","tabPart");
        tabPart.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(PARTS);
		});
        menuPrimaryObjects.add(tabPart);

        tabChapter = initMenuItem("16x32/manage_chapters","ctrl alt C",'C',"chapter","tabChapter");
        tabChapter.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHAPTERS);
		});
        menuPrimaryObjects.add(tabChapter);

        tabScene = initMenuItem("16x32/manage_scenes","ctrl alt S",'S',"scene","tabScene");
        tabScene.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(SCENES);
		});
        menuPrimaryObjects.add(tabScene);

        tabPerson = initMenuItem("16x32/manage_persons","ctrl alt P",'P',"person","tabPerson");
        tabPerson.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(PERSONS);
		});
        menuPrimaryObjects.add(tabPerson);

        tabLocation = initMenuItem("16x32/manage_locations","ctrl alt L",'L',"location","tabLocation");
        tabLocation.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(LOCATIONS);
		});
        menuPrimaryObjects.add(tabLocation);

        tabItem = initMenuItem("16x32/manage_items","ctrl alt I",'I',"item","tabItem");
        tabItem.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(ITEMS);
		});
        menuPrimaryObjects.add(tabItem);

        menuTables.add(menuPrimaryObjects);

		menuSecondaryObjects = new JMenu(I18N.getMsg("secondary.objects"));

        tabGender = initMenuItem("16x32/manage_genders","ctrl alt G",'G',"genders","tabGender");
        tabGender.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(GENDERS);
		});
        menuSecondaryObjects.add(tabGender);

        tabCategory = initMenuItem("16x32/manage_categories","",' ',"persons.category","tabCategory");
        tabCategory.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CATEGORIES);
		});
        menuSecondaryObjects.add(tabCategory);

        tabRelationship = initMenuItem("16x32/manage_relationships","",' ',"relationship","tabRelationship");
        tabRelationship.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(RELATIONSHIPS);
		});
        menuSecondaryObjects.add(tabRelationship);

        tabAttribute = initMenuItem("","",' ',"attribute_s","tabAttribute");
        tabAttribute.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(ATTRIBUTES);
		});
        menuSecondaryObjects.add(tabAttribute);
        menuSecondaryObjects.add(new JPopupMenu.Separator());

		tabItemLink = initMenuItem("16x32/manage_item_links","",' ',"item.link","tabItemLink");
        tabItemLink.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(ITEMLINKS);
		});
        menuSecondaryObjects.add(tabItemLink);
        menuSecondaryObjects.add(new JPopupMenu.Separator());

		tabTag = initMenuItem("16x32/manage_tags","ctrl alt T",'T',"tag","tabTag");
        tabTag.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(TAGS);
		});
        menuSecondaryObjects.add(tabTag);

        tabTagLink = initMenuItem("16x32/manage_tag_links","",' ',"tag.link","tabTagLink");
        tabTagLink.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(TAGLINKS);
		});
        menuSecondaryObjects.add(tabTagLink);
        menuSecondaryObjects.add(new JPopupMenu.Separator());

		tabMemo = initMenuItem("16x32/manage_memos","",' ',"memos","tabMemo");
        tabMemo.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(MEMOS);
		});
        menuSecondaryObjects.add(tabMemo);

        tabIdea = initMenuItem("16x32/manage_ideas","",' ',"idea.table.idea","tabIdea");
        tabIdea.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(IDEAS);
		});
        menuSecondaryObjects.add(tabIdea);

        menuTables.add(menuSecondaryObjects);

        menuBar.add(menuTables);
	}

	private void initMenuTools() {
		menuTools = new JMenu(I18N.getMsg("tools"));

        menuToolsSearch = initMenuItem("search");
        menuToolsSearch.addActionListener((ActionEvent evt) -> {
			SearchDlg.show(mainFrame);
		});
        menuTools.add(menuToolsSearch);

        menuToolsReplace = initMenuItem("replace");
        menuToolsReplace.addActionListener((ActionEvent evt) -> {
			ReplaceDlg.show(mainFrame);
		});
        menuTools.add(menuToolsReplace);

        menuToolsChaptersOrder = initMenuItem("chapters.order");
        menuToolsChaptersOrder.addActionListener((ActionEvent evt) -> {
			mainFrame.chaptersOrderAction();
		});
        menuTools.add(menuToolsChaptersOrder);

        menuToolsScenesOrder = initMenuItem("scenes.renumber");
        menuToolsScenesOrder.addActionListener((ActionEvent evt) -> {
			EntityUtil.renumberScenes(mainFrame);
		});
        menuTools.add(menuToolsScenesOrder);

        menuToolsRename = new JMenu(I18N.getMsg("tools.rename"));
		menuToolsRename.setIcon(new ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/rename.png")));

        renameCity = initMenuItem("location.rename.city");
        renameCity.addActionListener((ActionEvent evt) -> {
			RenameDlg dlg = new RenameDlg(mainFrame, "city");
			SwingUtil.showModalDialog(dlg, mainFrame);
		});
        menuToolsRename.add(renameCity);

        renameCountry = initMenuItem("location.rename.country");
        renameCountry.addActionListener((ActionEvent evt) -> {
			RenameDlg dlg = new RenameDlg(mainFrame, "country");
			SwingUtil.showModalDialog(dlg, mainFrame);
		});
        menuToolsRename.add(renameCountry);

        renameTagCategory = initMenuItem("tag.rename.category");
        renameTagCategory.addActionListener((ActionEvent evt) -> {
			RenameDlg dlg = new RenameDlg(mainFrame, "tag");
			SwingUtil.showModalDialog(dlg, mainFrame);
		});
        menuToolsRename.add(renameTagCategory);

        renameItemCategory = initMenuItem("item.rename.category");
        renameItemCategory.addActionListener((ActionEvent evt) -> {
			RenameDlg dlg = new RenameDlg(mainFrame, "item");
			SwingUtil.showModalDialog(dlg, mainFrame);
		});
        menuToolsRename.add(renameItemCategory);

        menuTools.add(menuToolsRename);

        menuBar.add(menuTools);
        
	}

	private void initMenuView() {
		menuView = new JMenu(I18N.getMsg("view"));

        vueTypist = initMenuItem("16x16/typist","",' ',"typist","");
        vueTypist.addActionListener((ActionEvent evt) -> {
			//TypistFrame.show(mainFrame, null);
			mainFrame.activateTypist();
		});
        menuView.add(vueTypist);
		menuView.addSeparator();

        vueChrono = initMenuItem("16x16/chrono_view","",' ',"view.chrono","");
        vueChrono.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHRONO);
		});
        menuView.add(vueChrono);

        vueBook = initMenuItem("16x16/book_view","",' ',"view.book","");
        vueBook.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(BOOK);
		});
        menuView.add(vueBook);

        vueReading = initMenuItem("16x16/reading","",' ',"view.reading","");
        vueReading.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(READING);
		});
        menuView.add(vueReading);

        vueManageScene = initMenuItem("16x16/manage_view","",' ',"view.manage","");
        vueManageScene.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(MANAGE);
		});
        menuView.add(vueManageScene);

        vueMemoria = initMenuItem("16x16/memoria","",' ',"view.pov","");
        vueMemoria.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(MEMORIA);
		});
        menuView.add(vueMemoria);

        vueStoryboard = initMenuItem("16x16/stamp","",' ',"view.storyboard","");
        vueStoryboard.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(STORYBOARD);
		});
        menuView.add(vueStoryboard);
        menuView.add(new JPopupMenu.Separator());

		toolsTaskList = initMenuItem("16x16/tasklist","",' ',"tasklist.title","");
        toolsTaskList.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(SCENES);
			mainFrame.getBookController().showTaskList();
		});
        menuView.add(toolsTaskList);

        toolsPlan = initMenuItem("16x16/plan","",' ',"view.plan","");
        toolsPlan.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(PLAN);
		});
        menuView.add(toolsPlan);

        vueTree = initMenuItem("16x16/tree","",' ',"tree","");
        vueTree.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(TREE);
		});
        menuView.add(vueTree);

        vueInfo = initMenuItem("16x16/info","",' ',"info.title","");
        vueInfo.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(INFO);
		});
        menuView.add(vueInfo);

        vueNavigation = initMenuItem("16x16/compass","",' ',"navigation","");
        vueNavigation.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(NAVIGATION);
		});
        menuView.add(vueNavigation);

        vueMemos = initMenuItem("16x16/memo","",' ',"memo","");
        vueMemos.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(MEMOS);
		});
        menuView.add(vueMemos);
		
		menuView.addSeparator();
        vueOptions = initMenuItem("16x16/options","",' ',"options","");
        vueOptions.addActionListener((ActionEvent evt) -> {
			OptionsDlg.show(mainFrame,null);
		});
        menuView.add(vueOptions);
		
        menuBar.add(menuView);
	}

	private void initMenuCharts() {
		menuCharts = new JMenu(I18N.getMsg("charts"));

		chartsAttributes = initMenuItem("16x16/columns","",' ',"attribute.list","");
        chartsAttributes.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(ATTRIBUTESLIST);
		});
        menuCharts.add(chartsAttributes);

        chartPersonsByDate = initMenuItem("16x16/chart","",' ',"tools.charts.overall.character.date","");
        chartPersonsByDate.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHART_PERSONS_BY_DATE);
		});
        menuCharts.add(chartPersonsByDate);

        chartPersonsByScene = initMenuItem("16x16/chart","",' ',"tools.charts.part.character.scene","");
        chartPersonsByScene.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHART_PERSONS_BY_SCENE);
		});
        menuCharts.add(chartPersonsByScene);

        chartWIWW = initMenuItem("16x16/chart","",' ',"tools.charts.overall.whoIsWhereWhen","");
        chartWIWW.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHART_WiWW);
		});
        menuCharts.add(chartWIWW);

        chartStrandsByDate = initMenuItem("16x16/chart","",' ',"tools.charts.overall.strand.date","");
        chartStrandsByDate.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHART_STRANDS_BY_DATE);
		});
        menuCharts.add(chartStrandsByDate);
        menuCharts.add(new JPopupMenu.Separator());

		chartOccurrenceOfPersons = initMenuItem("16x16/chart","",' ',"tools.charts.overall.character.occurrence","");
        chartOccurrenceOfPersons.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHART_OCCURRENCE_OF_PERSONS);
		});
        menuCharts.add(chartOccurrenceOfPersons);

        chartOccurrenceOfLocations = initMenuItem("16x16/chart","",' ',"tools.charts.overall.location.occurrence","");
        chartOccurrenceOfLocations.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHART_OCCURRENCE_OF_LOCATIONS);
		});
        menuCharts.add(chartOccurrenceOfLocations);

        chartOccurrenceOfItems = initMenuItem("16x16/chart","",' ',"tools.charts.overall.item.occurrence","");
        chartOccurrenceOfItems.addActionListener((ActionEvent evt) -> {
			mainFrame.showAndFocus(CHART_OCCURRENCE_OF_ITEMS);
		});
        menuCharts.add(chartOccurrenceOfItems);

        menuBar.add(menuCharts);
	}

	private void initMenuParts() {
        menuParts = new JMenu(I18N.getMsg("parts"));
		
		partPrevious = initMenuItem("16x16/arrowleft","",' ',"part.previous","");
		partPrevious.addActionListener((ActionEvent evt) -> {
			mainFrame.getSbActionManager().getActionHandler().handlePreviousPart();
		});
		menuParts.add(partPrevious);

		partNext = initMenuItem("16x16/arrowright","",' ',"part.next","");
		partNext.addActionListener((ActionEvent evt) -> {
			mainFrame.getSbActionManager().getActionHandler().handleNextPart();
		});
		menuParts.add(partNext);

		allParts = new JCheckBoxMenuItem(I18N.getMsg("parts.all"));
        allParts.addActionListener((ActionEvent evt) -> {
			mainFrame.showAllParts=!mainFrame.showAllParts;
			mainFrame.getBookController().changePart(mainFrame.getCurrentPart());
		});
        menuParts.add(allParts);

        menuBar.add(menuParts);
	}

	private void initMenuWindow() {
		menuWindow = new JMenu(I18N.getMsg("window"));

        windowLoadLayout = new JMenu(I18N.getMsg("docking.load.layout"));
        menuWindow.add(windowLoadLayout);

        windowSaveLayout = initMenuItem("docking.save.layout");
        windowSaveLayout.addActionListener((ActionEvent evt) -> {
			mainFrame.windowSaveLayoutAction();
		});
        menuWindow.add(windowSaveLayout);

        windowDefaultLayout = initMenuItem("layout.default");
        windowDefaultLayout.addActionListener((ActionEvent evt) -> {
			DockingWindowUtil.setLayout(mainFrame, DockingWindowUtil.DEFAULT_LAYOUT);
		});
        menuWindow.add(windowDefaultLayout);
        menuWindow.add(new JPopupMenu.Separator());

		windowPersonsAndLocations = initMenuItem("layout.persons.locations");
        windowPersonsAndLocations.addActionListener((ActionEvent evt) -> {
			DockingWindowUtil.setLayout(mainFrame, DockingWindowUtil.PERSONS_LOCATIONS_LAYOUT);
		});
        menuWindow.add(windowPersonsAndLocations);

        windowTagsAndItems = initMenuItem("layout.tags.items");
        windowTagsAndItems.addActionListener((ActionEvent evt) -> {
			DockingWindowUtil.setLayout(mainFrame, DockingWindowUtil.TAGS_ITEMS_LAYOUT);
		});
        menuWindow.add(windowTagsAndItems);

        windowChrono = initMenuItem("layout.chrono.only");
        windowChrono.addActionListener((ActionEvent evt) -> {
			DockingWindowUtil.setLayout(mainFrame, DockingWindowUtil.CHRONO_ONLY_LAYOUT);
		});
        menuWindow.add(windowChrono);

        windowBook = initMenuItem("layout.book.only");
        windowBook.addActionListener((ActionEvent evt) -> {
			DockingWindowUtil.setLayout(mainFrame, DockingWindowUtil.BOOK_ONLY_LAYOUT);
		});
        menuWindow.add(windowBook);

        windowManage = initMenuItem("layout.manage.only");
        windowManage.addActionListener((ActionEvent evt) -> {
			DockingWindowUtil.setLayout(mainFrame, DockingWindowUtil.MANAGE_ONLY_LAYOUT);
		});
        menuWindow.add(windowManage);

        windowReading = initMenuItem("layout.reading.only");
        windowReading.addActionListener((ActionEvent evt) -> {
			DockingWindowUtil.setLayout(mainFrame, DockingWindowUtil.READING_ONLY_LAYOUT);
		});
        menuWindow.add(windowReading);
        menuWindow.add(new JPopupMenu.Separator());

		windowResetLayout = initMenuItem("docking.reset.layout");
        windowResetLayout.addActionListener((ActionEvent evt) -> {
			SwingUtil.setWaitingCursor(mainFrame);
			mainFrame.setDefaultLayout();
			SwingUtil.setDefaultCursor(mainFrame);
		});
        menuWindow.add(windowResetLayout);

        windowRefresh = initMenuItem("","F5",' ',"refresh","");
        windowRefresh.addActionListener((ActionEvent evt) -> {
			mainFrame.refresh();
		});
        menuWindow.add(windowRefresh);

        menuBar.add(menuWindow);
	}

	private void initMenuHelp() {
		menuHelp = new JMenu(I18N.getMsg("help"));
		
        helpDoc = initMenuItem("","F1",' ',"help.doc","helpDoc");
        helpDoc.addActionListener((ActionEvent evt) -> {
			NetUtil.openBrowser(SbConstants.URL.DOC.toString());
		});
        menuHelp.add(helpDoc);

        helpFaq = initMenuItem("help.faq");
        helpFaq.addActionListener((ActionEvent evt) -> {
			NetUtil.openBrowser(SbConstants.URL.FAQ.toString());
		});
        menuHelp.add(helpFaq);

        helpHome = initMenuItem("help.homepage");
        helpHome.addActionListener((ActionEvent evt) -> {
			NetUtil.openBrowser(SbConstants.URL.HOMEPAGE.toString());
		});
        menuHelp.add(helpHome);

        helpReportBug = initMenuItem("help.bug");
        helpReportBug.addActionListener((ActionEvent evt) -> {
			NetUtil.openBrowser(SbConstants.URL.REPORTBUG.toString());
		});
        menuHelp.add(helpReportBug);

        helpAbout = initMenuItem("help.about");
        helpAbout.addActionListener((ActionEvent evt) -> {
			AboutDlg.show(mainFrame);
		});
        menuHelp.add(helpAbout);
        menuHelp.add(new JPopupMenu.Separator());

		helpCheckUpdates = initMenuItem("help.update");
        helpCheckUpdates.addActionListener((ActionEvent evt) -> {
			if (Updater.checkForUpdate(true)) {
				JOptionPane.showMessageDialog(mainFrame,
					I18N.getMsg("update.no.text"),
					I18N.getMsg("update.no.title"),
					JOptionPane.INFORMATION_MESSAGE);
			}
		});
        menuHelp.add(helpCheckUpdates);

        helpTrace = new JCheckBoxMenuItem();
        helpTrace.setMnemonic('T');
        helpTrace.setText(I18N.getMsg("help.trace"));
        helpTrace.addActionListener((ActionEvent evt) -> {
			if (SbApp.getTrace()) {
				SbApp.setTrace(false);
			} else {
				SbApp.setTrace(true);
			}
			helpTrace.setSelected(SbApp.getTrace());
		});
        menuHelp.add(helpTrace);

        devTest = initMenuItem("Dev-test");
		devTest.setText("Dev-test");
        devTest.addActionListener((ActionEvent evt) -> {
			testFunction=(devTest.getText().contains("Dev"));
			devTest.setText((testFunction?"Test in progress":"Dev-Test"));
		});
        if (SbApp.isDevTest()) menuHelp.add(devTest);

        helpTranslate = initMenuItem("Translate UI");
		helpTranslate.setText("Translate UI");
        helpTranslate.addActionListener((ActionEvent evt) -> {
			I18NDlg.show(mainFrame);
		});
        menuHelp.add(helpTranslate);

        menuBar.add(menuHelp);
	}

	public void setMenuForBlank() {
		// hide menus from MenuBar
		javax.swing.JMenu menus[] = {
			menuNewEntity, menuParts, menuTables, menuPrimaryObjects, menuSecondaryObjects,
			menuCharts, menuCharts, menuView, menuWindow, menuTools
		};
		for (javax.swing.JMenu m : menus) {
			m.setVisible(false);
		}
		javax.swing.JMenuItem[] submenus = {
			editCopyBlurb, editCopyBook, editCopyEntity,
			fileClose, fileExport,
			fileProperties, fileRename, fileSave, fileSaveAs, fileExport, fileImport
		};
		for (javax.swing.JMenuItem m : submenus) {
			m.setVisible(false);
		}
		javax.swing.JSeparator[] separators= {
			separator1,separator2,separator3
		};
		for (javax.swing.JSeparator sep:separators) {
			sep.setVisible(false);
		}
		javax.swing.JButton button[] = {
			btFileSave,
			btViewManage,
			btNewChapter, btNewItem, btNewLocation, btNewPerson, btNewScene, btNewTag,
			btNextPart, btPreviousPart,
			btTabChapter, btTabItem, btTabItemLink, btTabLocation, btTabPerson, btTabRelationship,
			btTabScene, btTabTag, btTabTagLink,
			btViewTypist, btViewBook, btViewChrono, btViewMemoria, btViewReading, btIdea, btMemo
		};
		for (javax.swing.JButton bt : button) {
			bt.setVisible(false);
		}
		if (SbApp.isDevTest() == false) {
			devTest.setVisible(false);
		}
	}

	public void refreshMenuPart() {
		menuParts.removeAll();
		partPrevious = initMenuItem("16x16/arrowleft","",' ',"part.previous","");
		partPrevious.addActionListener((ActionEvent evt) -> {
			mainFrame.getSbActionManager().getActionHandler().handlePreviousPart();
		});
		menuParts.add(partPrevious);

		partNext = initMenuItem("16x16/arrowright","",' ',"part.next","");
		partNext.addActionListener((ActionEvent evt) -> {
			mainFrame.getSbActionManager().getActionHandler().handleNextPart();
		});
		menuParts.add(partNext);

		JCheckBoxMenuItem cbParts = new JCheckBoxMenuItem(I18N.getMsg("parts.all"));
        cbParts.addActionListener((ActionEvent evt) -> {
			mainFrame.showAllParts=!mainFrame.showAllParts;
			mainFrame.getBookController().changePart(mainFrame.getCurrentPart());
		});
        menuParts.add(cbParts);
	}
}
