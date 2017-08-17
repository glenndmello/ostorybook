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
package storybook.toolkit.swing.htmleditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;

import jortho.SpellChecker;

import shef.ui.DefaultAction;
import shef.ui.ShefUtils;
import shef.ui.text.CompoundUndoManager;
import shef.ui.text.Entities;
import shef.ui.text.HTMLUtils;
import shef.ui.text.IndentationFilter;
import shef.ui.text.SourceCodeEditor;
import shef.ui.text.WysiwygHTMLEditorKit;
import shef.ui.text.actions.ClearStylesAction;
import shef.ui.text.actions.FindReplaceAction;
import shef.ui.text.actions.HTMLEditorActionFactory;
import shef.ui.text.actions.HTMLElementPropertiesAction;
import shef.ui.text.actions.HTMLFontAction;
import shef.ui.text.actions.HTMLFontColorAction;
import shef.ui.text.actions.HTMLHorizontalRuleAction;
import shef.ui.text.actions.HTMLImageAction;
import shef.ui.text.actions.HTMLInlineAction;
import shef.ui.text.actions.HTMLLineBreakAction;
import shef.ui.text.actions.HTMLLinkAction;
import shef.ui.text.actions.HTMLTableAction;
import shef.ui.text.actions.HTMLTextEditAction;
import shef.ui.text.actions.SpecialCharAction;

import org.miginfocom.swing.MigLayout;

import org.bushe.swing.action.ActionList;
import org.bushe.swing.action.ActionManager;
import org.bushe.swing.action.ActionUIFactory;

import novaworx.syntax.SyntaxFactory;
import novaworx.textpane.SyntaxDocument;
import novaworx.textpane.SyntaxGutter;
import novaworx.textpane.SyntaxGutterBase;

import shef.ui.text.actions.HTMLAlignAction;
import shef.ui.text.actions.HTMLBlockAction;

import storybook.i18n.I18N;
import storybook.SbConstants.Spelling;
import storybook.SbPref;
import storybook.toolkit.html.HtmlUtil;
import storybook.toolkit.TextUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.ExceptionDlg;

/**
 * Based on HTMLEditorPane by SHEF / Bob Tantlinger.<br>
 * http://shef.sourceforge.net
 *
 * @author martin, revu par favdb en 2017
 * @author Bob Tantlinger
 */
@SuppressWarnings("serial")
public class HtmlEditor extends JPanel {

	public static boolean HTMLshow = true;
	public static boolean TWOline = true;

	private static final String INVALID_TAGS[] = {"html", "head", "body", "title", "style"};

	private int maxLength = 32768;
	private boolean showFullToolbar = true;

	private JEditorPane wysEditor;
	private SourceCodeEditor srcEditor;
	private JEditorPane focusedEditor;
	private JComboBox cbFontFamily;
	private JComboBox cbParagraph;
	private JTabbedPane tabs;
	private JToolBar formatToolBar;
	private JLabel lbMessage;

	private JMenu editMenu;
	private JMenu formatMenu;
	private JMenu insertMenu;

	private JPopupMenu wysPopupMenu, srcPopupMenu;

	private ActionList actionList;

	private final FocusListener focusHandler = new FocusHandler();
	private final DocumentListener textChangedHandler = new TextChangedHandler();
	private final ActionListener cbFontFamilyHandler = new FontChangeHandler();
	private final ActionListener cbParagraphHandler = new ParagraphComboHandler();
	private final CaretListener caretHandler = new CaretHandler();
	private final MouseListener popupHandler = new PopupHandler();

	private boolean isWysTextChanged;
	private final MainFrame mainFrame;
	private JButton unicodeButton;
	private final boolean showHtml;
	private final boolean twoLineBar;
	private JPanel panel;

	public HtmlEditor(MainFrame m) {
		this.showFullToolbar = false;
		this.showHtml = false;
		this.twoLineBar = false;
		mainFrame = m;
		initUI();
	}

	public HtmlEditor(MainFrame m, boolean showHtml, boolean twoLineBar) {
		this.showFullToolbar = true;
		this.showHtml = showHtml;
		this.twoLineBar = twoLineBar;
		mainFrame = m;
		initUI();
	}

	public HtmlEditor(MainFrame m, boolean showFullToolbar, boolean showHtml, boolean twoLineBar) {
		this.showFullToolbar = showFullToolbar;
		this.showHtml = showHtml;
		this.twoLineBar = twoLineBar;
		mainFrame = m;
		initUI();
	}

	public boolean getShowSimpleToolbar() {
		return showFullToolbar;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setCaretPosition(int pos) {
		if (!showHtml || tabs.getSelectedIndex() == 0) {
			wysEditor.setCaretPosition(pos);
			wysEditor.requestFocusInWindow();
		} else if (!showHtml || tabs.getSelectedIndex() == 1) {
			srcEditor.setCaretPosition(pos);
			srcEditor.requestFocusInWindow();
		}
	}

	public void setSelectedTab(int i) {
		if (showHtml) {
			tabs.setSelectedIndex(i);
		}
	}

	private void initUI() {
		createEditorTabs();
		createEditorActions();
		setLayout(new MigLayout("fill,wrap,ins 0", "", "[][grow][]"));
		add(formatToolBar);
		lbMessage = new JLabel("", JLabel.RIGHT);
		add(lbMessage, "shrink, pos null null 100% 100%");

		if (showHtml) {
			add(tabs, "grow, id tabs");
		} else {
			add(panel, "grow, id tabs");
		}
		SwingUtilities.invokeLater(() -> {
			isWysTextChanged = false;
			wysEditor.requestFocusInWindow();
		});
	}

	private JButton initButton(Character accel, String name, ActionListener al) {
		String tips = I18N.getMsg("shef." + name);
		JButton bt = new JButton(ShefUtils.getIconX16(name));
		bt.setName(name);
		bt.setText(null);
		bt.setMnemonic(0);
		bt.setToolTipText(tips);
		bt.setMargin(new Insets(1, 1, 1, 1));
		bt.setMaximumSize(new Dimension(22, 22));
		bt.setMinimumSize(new Dimension(22, 22));
		bt.setPreferredSize(new Dimension(22, 22));
		bt.setFocusable(false);
		bt.setFocusPainted(false);
		if (accel != ' ') {
			bt.registerKeyboardAction(al,
				KeyStroke.getKeyStroke(accel, Event.CTRL_MASK, false),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		}
		bt.addActionListener(al);
		return (bt);
	}

	public JMenu getEditMenu() {
		return editMenu;
	}

	public JMenu getFormatMenu() {
		return formatMenu;
	}

	public JMenu getInsertMenu() {
		return insertMenu;
	}

	private void createEditorActions() {
		actionList = new ActionList("editor-actions");

		ActionList paraActions = new ActionList("paraActions");
		ActionList fontSizeActions = new ActionList("fontSizeActions");
		ActionList editActions = HTMLEditorActionFactory.createEditActionList();
		Action objectPropertiesAction = new HTMLElementPropertiesAction();

		// create popup menu
		wysPopupMenu = ActionUIFactory.getInstance().createPopupMenu(editActions);
		wysPopupMenu.addSeparator();
		wysPopupMenu.add(objectPropertiesAction);
		wysPopupMenu.addSeparator();

		// spell checker
		String spelling = mainFrame.getPref().getString(SbPref.Key.SPELLING, Spelling.none.name());
		if (!Spelling.none.name().equals(spelling)) {
			wysPopupMenu.add(SpellChecker.createCheckerMenu());
			wysPopupMenu.add(SpellChecker.createLanguagesMenu());
			wysPopupMenu.addSeparator();
		}

		// open URL
		wysPopupMenu.add(new OpenUrlAction(wysEditor));
		srcPopupMenu = ActionUIFactory.getInstance().createPopupMenu(editActions);

		// create file menu
		//JMenu fileMenu = new JMenu(I18N.getMsg("shef.file"));
		// create edit menu
		ActionList lst = new ActionList("edits");
		Action act = new ChangeTabAction(0);
		lst.add(act);
		act = new ChangeTabAction(1);
		lst.add(act);
		lst.add(null);// separator
		lst.addAll(editActions);
		lst.add(null);
		lst.add(new FindReplaceAction(false));
		actionList.addAll(lst);
		editMenu = ActionUIFactory.getInstance().createMenu(lst);
		editMenu.setText(I18N.getMsg("shef.edit"));

		// create format menu
		formatMenu = new JMenu(I18N.getMsg("shef.format"));
		lst = HTMLEditorActionFactory.createFontSizeActionList();
		actionList.addAll(lst);
		formatMenu.add(createMenu(lst, I18N.getMsg("shef.size")));
		fontSizeActions.addAll(lst);

		lst = HTMLEditorActionFactory.createInlineActionList();
		actionList.addAll(lst);
		formatMenu.add(createMenu(lst, I18N.getMsg("shef.style")));

		act = new HTMLFontColorAction();
		actionList.add(act);
		formatMenu.add(act);

		act = new HTMLFontAction();
		actionList.add(act);
		formatMenu.add(act);

		act = new ClearStylesAction();
		actionList.add(act);
		formatMenu.add(act);
		formatMenu.addSeparator();

		lst = HTMLEditorActionFactory.createBlockElementActionList();
		actionList.addAll(lst);
		formatMenu.add(createMenu(lst, I18N.getMsg("shef.paragraph")));
		paraActions.addAll(lst);

		lst = HTMLEditorActionFactory.createListElementActionList();
		actionList.addAll(lst);
		formatMenu.add(createMenu(lst, I18N.getMsg("shef.list")));
		formatMenu.addSeparator();
		paraActions.addAll(lst);

		lst = HTMLEditorActionFactory.createAlignActionList();
		actionList.addAll(lst);
		formatMenu.add(createMenu(lst, I18N.getMsg("shef.align")));

		JMenu tableMenu = new JMenu(I18N.getMsg("shef.table"));
		lst = HTMLEditorActionFactory.createInsertTableElementActionList();
		actionList.addAll(lst);
		tableMenu.add(createMenu(lst, I18N.getMsg("shef.insert")));

		lst = HTMLEditorActionFactory.createDeleteTableElementActionList();
		actionList.addAll(lst);
		tableMenu.add(createMenu(lst, I18N.getMsg("shef.delete")));
		formatMenu.add(tableMenu);
		formatMenu.addSeparator();

		actionList.add(objectPropertiesAction);
		formatMenu.add(objectPropertiesAction);

		// create insert menu
		insertMenu = new JMenu(I18N.getMsg("shef.insert"));
		act = new HTMLLinkAction();
		actionList.add(act);
		insertMenu.add(act);

		act = new HTMLImageAction();
		actionList.add(act);
		insertMenu.add(act);

		act = new HTMLTableAction();
		actionList.add(act);
		insertMenu.add(act);
		insertMenu.addSeparator();

		act = new HTMLLineBreakAction();
		actionList.add(act);
		insertMenu.add(act);

		act = new HTMLHorizontalRuleAction();
		actionList.add(act);
		insertMenu.add(act);

		act = new SpecialCharAction();
		actionList.add(act);
		insertMenu.add(act);

		createFormatToolBar(paraActions, fontSizeActions);
	}

	/**
	 * Creation de la barre d'outil. La barre est composée de deux éléments: 1) une barre de base comprenant : le type
	 * de paragraphe, la famille de la police de caractères la taille de la police, le choix de la couleur. 2) si la
	 * barre complète est à afficher : gras, italique, souligné, liste à puces, liste ordonnée, cadre à gauche, cadrage
	 * au centre, cadrage à droite, justifié, insertion d'un lien, insertion d'une image insertion d'un tableau.
	 *
	 * @param blockActs
	 * @param fontSizeActs
	 */
	@SuppressWarnings({"unchecked"})
	private void createFormatToolBar(ActionList blockActs, ActionList fontSizeActs) {
		formatToolBar = new JToolBar();
		formatToolBar.setFloatable(false);
		formatToolBar.setFocusable(false);
		formatToolBar.setLayout(new MigLayout("ins 0,flowx"));
		formatToolBar.setOpaque(false);
		
		// paragraphs
		formatToolBar.add(initParagraphCombo(blockActs), "split 6");
		formatToolBar.addSeparator();
		
		// fonts family
		formatToolBar.add(initFontFamilyCombo());
		// font size
		final JButton fontSizeButton = new JButton(ShefUtils.getIconX16("fontsize"));
		fontSizeButton.setToolTipText(I18N.getMsg("shef.size"));
		final JPopupMenu sizePopup = ActionUIFactory.getInstance().createPopupMenu(fontSizeActs);
		ActionListener al = (ActionEvent e) -> {
			sizePopup.show(fontSizeButton, 0, fontSizeButton.getHeight());
		};
		fontSizeButton.addActionListener(al);
		configToolbarButton(fontSizeButton);
		formatToolBar.add(fontSizeButton);
		// font color
		Action actColor = new HTMLFontColorAction();
		actionList.add(actColor);
		formatToolBar.add(initButton(' ', "color", actColor));

		String newline = "newline,";
		if (!twoLineBar) {
			newline = "";
		}
		// bold
		Action actBold = new HTMLInlineAction(HTMLInlineAction.BOLD);
		actBold.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		actionList.add(actBold);
		formatToolBar.add(initButton('B', "bold", actBold), newline + "split 20");
		// italic
		Action actItalic = new HTMLInlineAction(HTMLInlineAction.ITALIC);
		actItalic.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		actionList.add(actItalic);
		formatToolBar.add(initButton('I', "italic", actItalic));
		// underline
		Action actUnderline = new HTMLInlineAction(HTMLInlineAction.UNDERLINE);
		actUnderline.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		actionList.add(actUnderline);
		formatToolBar.add(initButton('U', "underline", actUnderline));
		//TODO add strike, subscript, supercscript

		formatToolBar.addSeparator();

		// ul and ol
		Action actUL=new HTMLBlockAction(HTMLBlockAction.UL);
		actUL.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		actionList.add(actUL);
		formatToolBar.add(initButton(' ', "listunordered", actUL));
		actUL=new HTMLBlockAction(HTMLBlockAction.OL);
		actUL.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		actionList.add(actUL);
		formatToolBar.add(initButton(' ', "listordered", actUL));

		formatToolBar.addSeparator();

		// alignement (left, center, right, justify)
		Action actAlign=new HTMLAlignAction(HTMLAlignAction.LEFT);
		actAlign.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		actionList.add(actAlign);
		formatToolBar.add(initButton(' ', "al_left", actAlign));
		actAlign=new HTMLAlignAction(HTMLAlignAction.CENTER);
		actAlign.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		actionList.add(actAlign);
		formatToolBar.add(initButton(' ', "al_center", actAlign));
		actAlign=new HTMLAlignAction(HTMLAlignAction.RIGHT);
		actAlign.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		actionList.add(actAlign);
		formatToolBar.add(initButton(' ', "al_right", actAlign));
		actAlign=new HTMLAlignAction(HTMLAlignAction.JUSTIFY);
		actAlign.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		actionList.add(actAlign);
		formatToolBar.add(initButton(' ', "al_justify", actAlign));

		formatToolBar.addSeparator();

		// link, image, table
		Action actLink = new HTMLLinkAction();
		actionList.add(actLink);
		formatToolBar.add(initButton(' ', "link", actLink));

		Action actImage = new SbHTMLImageAction(mainFrame);
		actionList.add(actImage);
		formatToolBar.add(initButton(' ', "image", actImage));

		Action actTable = new HTMLTableAction();
		actionList.add(actTable);
		formatToolBar.add(initButton(' ', "table", actTable));

		//line break
		Action act = new HTMLLineBreakAction();
		actionList.add(act);
		formatToolBar.add(initButton(' ', "line_break", act));

		formatToolBar.addSeparator();

		//TODO replace by new unicode + add math symbols
		unicodeButton = SwingUtil.createButton("", "16x16/specchar", "editor.specchar.tooltip");
		unicodeButton.addActionListener((ActionEvent arg0) -> {
			mainFrame.showUnicodeDialog();
		});
		formatToolBar.add(unicodeButton, "align right");
		
		//TODO add view HTML code if wiew Wysiwyg only
	}

	@SuppressWarnings("unchecked")
	private JComboBox initParagraphCombo(ActionList blockActs) {
		PropertyChangeListener propLst = (PropertyChangeEvent evt) -> {
			if (evt.getPropertyName().equals("selected")) {
				if (evt.getNewValue().equals(Boolean.TRUE)) {
					cbParagraph.removeActionListener(cbParagraphHandler);
					cbParagraph.setSelectedItem(evt.getSource());
					cbParagraph.addActionListener(cbParagraphHandler);
				}
			}
		};
		for (Object o: blockActs) {
			if (o instanceof DefaultAction) {
				((DefaultAction) o).addPropertyChangeListener(propLst);
			}
		}
		cbParagraph = new JComboBox(toArray(blockActs));
		cbParagraph.setFont(new Font("Dialog", Font.PLAIN, 11));
		cbParagraph.addActionListener(cbParagraphHandler);
		cbParagraph.setRenderer(new ParagraphComboRenderer());
		cbParagraph.setToolTipText(I18N.getMsg("shef.style"));
		return(cbParagraph);
	}

	@SuppressWarnings("unchecked")
	private JComboBox initFontFamilyCombo() {
		List<String> fonts = new ArrayList<>();
		fonts.add("Default");
		fonts.add("serif");
		fonts.add("sans-serif");
		fonts.add("monospaced");
		GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		fonts.addAll(Arrays.asList(gEnv.getAvailableFontFamilyNames()));

		cbFontFamily = new JComboBox(fonts.toArray());
		cbFontFamily.setFont(new Font("Dialog", Font.PLAIN, 11));
		cbFontFamily.addActionListener(cbFontFamilyHandler);
		cbFontFamily.setToolTipText(I18N.getMsg("shef.font"));
		return(cbFontFamily);
	}

	private void addToToolBar(JToolBar toolbar, Action act) {
		addToToolBar(toolbar, act, "");
	}

	private void addToToolBar(JToolBar toolbar, Action act, String options) {
		AbstractButton button = ActionUIFactory.getInstance().createButton(act);
		configToolbarButton(button);
		toolbar.add(button, options);
	}

	/**
	 * Converts an action list to an array. Any of the null "separators" or sub ActionLists are omitted from the array.
	 *
	 * @param lst
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Action[] toArray(ActionList lst) {
		List acts = new ArrayList();
		for (Iterator it = lst.iterator(); it.hasNext();) {
			Object v = it.next();
			if (v != null && v instanceof Action) {
				acts.add(v);
			}
		}

		return (Action[]) acts.toArray(new Action[acts.size()]);
	}

	private void configToolbarButton(AbstractButton button) {
		button.setText(null);
		button.setMnemonic(0);
		button.setMargin(new Insets(1, 1, 1, 1));
		button.setMaximumSize(new Dimension(22, 22));
		button.setMinimumSize(new Dimension(22, 22));
		button.setPreferredSize(new Dimension(22, 22));
		button.setFocusable(false);
		button.setFocusPainted(false);
		// button.setBorder(plainBorder);
		Action a = button.getAction();
		if (a != null) {
			button.setToolTipText(a.getValue(Action.NAME).toString());
		}
	}

	private JMenu createMenu(ActionList lst, String menuName) {
		JMenu m = ActionUIFactory.getInstance().createMenu(lst);
		m.setText(menuName);
		return m;
	}

	private void createEditorTabs() {
		wysEditor = createWysiwygEditor();
		srcEditor = createSourceEditor();

		if (showHtml) {
			tabs = new JTabbedPane(SwingConstants.BOTTOM);
			SwingUtil.setMaxPreferredSize(tabs);

			tabs.addTab("Edit", new JScrollPane(wysEditor));

			srcEditor.setBackground((new JEditorPane().getBackground()));
			srcEditor.setForeground(Color.black);
			JScrollPane scrollPane = new JScrollPane(srcEditor);
			SyntaxGutter gutter = new SyntaxGutter(srcEditor);
			SyntaxGutterBase gutterBase = new SyntaxGutterBase(gutter);
			scrollPane.setRowHeaderView(gutter);
			scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, gutterBase);

			tabs.addTab("HTML", scrollPane);
			tabs.addChangeListener((ChangeEvent e) -> {
				updateEditView();
			});
		} else {
			panel = new JPanel();
			panel.setLayout(new MigLayout());
			panel.add(new JScrollPane(wysEditor), "grow");
		}
	}

	private SourceCodeEditor createSourceEditor() {
		SourceCodeEditor ed = new SourceCodeEditor();
		SyntaxDocument doc = new SyntaxDocument();
		doc.setSyntax(SyntaxFactory.getSyntax("html"));
		CompoundUndoManager cuh = new CompoundUndoManager(doc, new UndoManager());

		doc.addUndoableEditListener(cuh);
		doc.setDocumentFilter(new IndentationFilter());
		doc.addDocumentListener(textChangedHandler);
		ed.setDocument(doc);
		ed.addFocusListener(focusHandler);
		ed.addCaretListener(caretHandler);
		ed.addMouseListener(popupHandler);

		return ed;
	}

	private JEditorPane createWysiwygEditor() {
		JEditorPane ed = new JEditorPane();

		ed.setEditorKitForContentType("text/html", new WysiwygHTMLEditorKit());
		ed.setContentType("text/html");
		SwingUtil.setForcedSize(ed, SwingUtil.getScreenSize());
		insertHTML(ed, "<p></p>", 0);
		
		ed.addCaretListener(caretHandler);
		ed.addFocusListener(focusHandler);
		// spell checker, must be before the popup handler
		String spelling = mainFrame.getPref().getString(SbPref.Key.SPELLING, Spelling.none.name());
		if (!Spelling.none.name().equals(spelling)) {
			SpellChecker.register(ed);
		}

		ed.addMouseListener(popupHandler);

		HTMLDocument document = (HTMLDocument) ed.getDocument();
		CompoundUndoManager cuh = new CompoundUndoManager(document, new UndoManager());
		document.addUndoableEditListener(cuh);

		isWysTextChanged = false;
		
		return ed;
	}

	// inserts html into the wysiwyg editor
	private void insertHTML(JEditorPane editor, String html, int location) {
		JEditorPane ed=new JEditorPane();
		Font font=ed.getFont();
		String topText = "<html><body "
			+ "style=\"f"
			+ "font-family: " +  mainFrame.getPref().getString(SbPref.Key.EDITOR_FONT_NAME, "Serif") + "; "
			+ "font-size: " + mainFrame.getPref().getInteger(SbPref.Key.EDITOR_FONT_SIZE, font.getSize()) + "pt;"
			+ "\">"
			+ removeInvalidTags(html)+"</body></html>";
		try {
			HTMLEditorKit kit = (HTMLEditorKit) editor.getEditorKit();
			Document doc = editor.getDocument();
			StringReader reader = new StringReader(HTMLUtils.jEditorPaneizeHTML(topText));
			kit.read(reader, doc, location);
		} catch (IOException | BadLocationException e) {
			ExceptionDlg.show("", e);
		}
	}

	// called when changing tabs
	private void updateEditView() {
		if (!showHtml || tabs.getSelectedIndex() == 0) {
			String topText = removeInvalidTags(srcEditor.getText());
			wysEditor.setText("");
			insertHTML(wysEditor, topText, 0);
			CompoundUndoManager.discardAllEdits(wysEditor.getDocument());
		} else {
			String topText = removeInvalidTags(wysEditor.getText());
			if (isWysTextChanged || srcEditor.getText().equals("")) {
				String t = deIndent(removeInvalidTags(topText));
				t = Entities.HTML40.unescapeUnknownEntities(t);
				srcEditor.setText(t);
			}
			CompoundUndoManager.discardAllEdits(srcEditor.getDocument());
		}
		isWysTextChanged = false;
		cbParagraph.setEnabled(tabs.getSelectedIndex() == 0);
		cbFontFamily.setEnabled(tabs.getSelectedIndex() == 0);
		updateState();
	}

	public void setText(String text) {
		/*JEditorPane ed=new JEditorPane();
		Font font=ed.getFont();*/
		String topText = removeInvalidTags(text);
		if (!showHtml || tabs.getSelectedIndex() == 0) {
			wysEditor.setText("");
			insertHTML(wysEditor, topText, 0);
			CompoundUndoManager.discardAllEdits(wysEditor.getDocument());
		} else {
			{
				String t = deIndent(removeInvalidTags(topText));
				t = Entities.HTML40.unescapeUnknownEntities(t);
				srcEditor.setText(t);
			}
			CompoundUndoManager.discardAllEdits(srcEditor.getDocument());
		}
		isWysTextChanged = false;
	}

	public String getText() {
		String topText = "";
		// return only body content
		try {
			if (!showHtml || tabs.getSelectedIndex() == 0) {
				HTMLDocument doc = (HTMLDocument) wysEditor.getDocument();
				topText = HtmlUtil.getContent(doc);
				topText = removeInvalidTags(topText);
			} else {
				topText = removeInvalidTags(srcEditor.getText());
				topText = deIndent(removeInvalidTags(topText));
				topText = Entities.HTML40.unescapeUnknownEntities(topText);
			}
		} catch (Exception e) {
			ExceptionDlg.show("", e);
		}
		return topText;
	}

	/* *******************************************************************
     * Methods for dealing with HTML between wysiwyg and source editors
     * *****************************************************************
	 */
	private String deIndent(String html) {
		String ws = "\n    ";
		StringBuilder sb = new StringBuilder(html);
		while (sb.indexOf(ws) != -1) {
			int s = sb.indexOf(ws);
			int e = s + ws.length();
			sb.delete(s, e);
			sb.insert(s, "\n");
		}
		return sb.toString();
	}

	private String removeInvalidTags(String html) {
		for (String invalid_tag : INVALID_TAGS) {
			html = deleteOccurance(html, '<' + invalid_tag + '>');
			html = deleteOccurance(html, "</" + invalid_tag + '>');
		}
		return html.trim();
	}

	private String deleteOccurance(String text, String word) {
		StringBuilder sb = new StringBuilder(text);
		int p;
		while ((p = sb.toString().toLowerCase().indexOf(word.toLowerCase())) != -1) {
			sb.delete(p, p + word.length());
		}
		return sb.toString();
	}

	private void updateState() {
		if (focusedEditor == wysEditor) {
			cbFontFamily.removeActionListener(cbFontFamilyHandler);
			String fontName = HTMLUtils.getFontFamily(wysEditor);
			if (fontName == null) {
				cbFontFamily.setSelectedIndex(0);
			} else {
				cbFontFamily.setSelectedItem(fontName);
			}
			cbFontFamily.addActionListener(cbFontFamilyHandler);
		}
		actionList.putContextValueForAll(HTMLTextEditAction.EDITOR, focusedEditor);
		actionList.updateEnabledForAll();
	}

	public boolean isTextChanged() {
		return (isWysTextChanged);
	}

	private class CaretHandler implements CaretListener {

		@Override
		public void caretUpdate(CaretEvent e) {
			if (maxLength > 0) {
				int len = maxLength - getText().length() - 1;
				if (len < 0) {
					lbMessage.setForeground(Color.red);
				} if (len < 100) {
					lbMessage.setForeground(Color.orange);
				} else {
					lbMessage.setForeground(Color.black);
				}
				lbMessage.setText(I18N.getMsg("editor.letters.left", (len + "/" + maxLength))
					+ " (" + TextUtil.countWords(getText()) + ")");
			}
			updateState();
		}
	}

	private class PopupHandler extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			checkForPopupTrigger(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			checkForPopupTrigger(e);
		}

		private void checkForPopupTrigger(MouseEvent e) {
			if (e.isPopupTrigger()) {
				JPopupMenu p;
				if (e.getSource() == wysEditor) {
					p = wysPopupMenu;
				} else if (e.getSource() == srcEditor) {
					p = srcPopupMenu;
				} else {
					return;
				}
				p.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private class FocusHandler implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			if (e.getComponent() instanceof JEditorPane) {
				JEditorPane ed = (JEditorPane) e.getComponent();
				CompoundUndoManager.updateUndo(ed.getDocument());
				focusedEditor = ed;

				updateState();
				// updateEnabledStates();
			}
		}

		@Override
		public void focusLost(FocusEvent e) {

			if (e.getComponent() instanceof JEditorPane) {
				// focusedEditor = null;
				// wysiwygUpdated();
			}
		}
	}

	private class TextChangedHandler implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			textChanged();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			textChanged();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			textChanged();
		}

		private void textChanged() {
			if (!showHtml) {
				isWysTextChanged = true;
			} else if (tabs != null && tabs.getSelectedIndex() == 0) {
				isWysTextChanged = true;
			}
		}
	}

	private class ChangeTabAction extends DefaultAction {

		private static final long serialVersionUID = 1L;
		int tab;

		public ChangeTabAction(int tab) {
			super((tab == 0) ? I18N.getMsg("shef.rich_text") : I18N.getMsg("shef.source"));
			this.tab = tab;
			putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_RADIO);
		}

		@Override
		protected void execute(ActionEvent e) {
			tabs.setSelectedIndex(tab);
			setSelected(true);
		}

		@Override
		protected void contextChanged() {
			if (showHtml) {
				setSelected(tabs.getSelectedIndex() == tab);
			}
		}
	}

	private class ParagraphComboHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cbParagraph) {
				Action a = (Action) (cbParagraph.getSelectedItem());
				a.actionPerformed(e);
			}
		}
	}

	private class ParagraphComboRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
			if (value instanceof Action) {
				value = ((Action) value).getValue(Action.NAME);
			}

			return super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
		}
	}

	private class FontChangeHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cbFontFamily && focusedEditor == wysEditor) {
				// MutableAttributeSet tagAttrs = new SimpleAttributeSet();
				HTMLDocument document = (HTMLDocument) focusedEditor.getDocument();
				CompoundUndoManager.beginCompoundEdit(document);

				if (cbFontFamily.getSelectedIndex() != 0) {
					HTMLUtils.setFontFamily(wysEditor, cbFontFamily.getSelectedItem().toString());
				} else {
					HTMLUtils.setFontFamily(wysEditor, null);
				}
				CompoundUndoManager.endCompoundEdit(document);
			}
		}

		public void itemStateChanged(ItemEvent e) {
		}
	}
}
