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
package storybook.ui.panel.typist;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;
import storybook.SbConstants;
import storybook.controller.BookController;
import storybook.i18n.I18N;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.BookUtil;
import storybook.toolkit.swing.FontUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.htmleditor.HtmlEditor;
import storybook.ui.MainFrame;
import storybook.ui.dialog.ChapterSelectDlg;
import storybook.ui.dialog.edit.EntityEditor;
import storybook.ui.panel.AbstractPanel;

/**
 *
 * @author favdb
 */
public class TypistPanel  extends AbstractPanel {

	private Scene scene=null;
	private boolean toRefresh;
	private Dimension screenSize;
	private int defaultHeight;
	private HtmlEditor panelEdit;
	private TypistInfo panelInfo;
	private JToolBar toolbar;
	private JComboBox entityCombo;
	private boolean entityComboInit;
	private JButton btNewScene;
	private JButton btFirst;
	private JButton btPrior;
	private JButton btNext;
	private JButton btLast;
	private JButton btStandard;
	private JButton btChapter;
	private JButton btTitle;
	private JButton btExit;
	private JPanel statusbar;
	private boolean modified;

	public TypistPanel(MainFrame mainFrame, Scene s) {
		super(mainFrame);
		scene = s;
		if (scene == null) {
			scene = getFirstScene();
		}
		initAll();
	}
	
	@Override
	public void init() {
		toRefresh = false;
		BookUtil.store(mainFrame, SbConstants.BookKey.TYPIST_USE, true);
	}

	@Override
	public void initUi() {
		this.setLayout(new MigLayout("wrap,fill,ins 2"));
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		defaultHeight = 32;
		add(initToolbar(),"growx");
		panelEdit=new HtmlEditor(mainFrame, !HtmlEditor.HTMLshow,!HtmlEditor.TWOline);
		if (scene!=null) {
			panelEdit.setText(scene.getSummary());
			panelEdit.setCaretPosition(0);
		}
		panelInfo = new TypistInfo(this, scene);
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,panelEdit,panelInfo);
		split.setResizeWeight(0.8);
		add(split,"growy");
		add(initStatusbar());
		refresh();
	}

	private JToolBar initToolbar() {
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setLayout(new MigLayout("ins 2"));
		toolbar.setName("TypistToolbar");
		Dimension d = new Dimension(screenSize.width, defaultHeight);
		toolbar.setSize(d);
		toolbar.setMinimumSize(d);
		toolbar.setMaximumSize(d);

		entityCombo = new JComboBox();
		entityCombo.setName("sceneList");
		entityCombo.setMaximumRowCount(15);
		refreshScenes(scene);
		entityCombo.setSelectedItem(scene);
		entityCombo.addActionListener((ActionEvent evt) -> {
			if (entityComboInit) {
				return;
			}
			if (askModified() == JOptionPane.CANCEL_OPTION) {
				return;
			}
			if (((JComponent) evt.getSource()).getName().equals("sceneList")) {
				if (entityCombo.getSelectedItem()!=null) {
					scene = (Scene) entityCombo.getSelectedItem();
					refresh();
				}
			}
		});
		toolbar.add(entityCombo);
		btNewScene = SwingUtil.createButton("", "16x16/add", "scene.new");
		btNewScene.addActionListener((ActionEvent evt) -> {
			if (newScene()) {
				refresh();
			}
		});
		toolbar.add(btNewScene);
		toolbar.add(new JToolBar.Separator());

		//navigation dans les scènes
		btFirst = SwingUtil.createButton("", "16x16/first", "export.nav.first");
		btFirst.addActionListener((ActionEvent evt) -> {
			if (askModified() == JOptionPane.CANCEL_OPTION) {
				return;
			}
			getFirstScene();
			refresh();
		});
		btFirst.setEnabled(!isSceneFirst());
		toolbar.add(btFirst);
		btPrior = SwingUtil.createButton("", "16x16/previous", "export.nav.prior");
		btPrior.addActionListener((ActionEvent evt) -> {
			if (askModified() == JOptionPane.CANCEL_OPTION) {
				return;
			}
			getPriorScene();
			refresh();
		});
		toolbar.add(btPrior);
		btNext = SwingUtil.createButton("", "16x16/next", "export.nav.next");
		btNext.addActionListener((ActionEvent evt) -> {
			if (askModified() == JOptionPane.CANCEL_OPTION) {
				return;
			}
			getNextScene();
			refresh();
		});
		toolbar.add(btNext);
		btLast = SwingUtil.createButton("", "16x16/last", "export.nav.last");
		btLast.addActionListener((ActionEvent evt) -> {
			if (askModified() == JOptionPane.CANCEL_OPTION) {
				return;
			}
			getLastScene();
			refresh();
		});
		btLast.setEnabled(!isSceneLast());
		toolbar.add(btLast);
		toolbar.add(new JToolBar.Separator());

		//ignorer les modifications		
		JButton btChange = SwingUtil.createButton("discard.changes", "", "");
		btChange.addActionListener((ActionEvent evt) -> {
			refresh();
		});
		toolbar.add(btChange);
		toolbar.add(new JToolBar.Separator());

		//sortie du mode typist
		btStandard = SwingUtil.createButton("", "16x16/switch", "standardview");
		btStandard.setEnabled(true);
		btStandard.addActionListener((ActionEvent evt) -> {
			if (askModified() != JOptionPane.CANCEL_OPTION) {
				close();
			}
			if (toRefresh) {
				SwingUtil.setWaitingCursor(this);
				mainFrame.refresh();
				SwingUtil.setDefaultCursor(this);
			}
			BookUtil.store(mainFrame, SbConstants.BookKey.TYPIST_USE, false);
		});
		toolbar.add(btStandard);
		toolbar.add(new JToolBar.Separator());

		//titre du chapitre
		JLabel lb = new JLabel(I18N.getMsg("chapter"));
		lb.setFont(FontUtil.getBoldFont());
		toolbar.add(lb);
		btChapter = SwingUtil.createButton("", "16x16/chapter", "change");
		btChapter.addActionListener((ActionEvent evt) -> {
			changeChapter();
		});
		toolbar.add(btChapter, "growx");

		//titre de la scène
		lb = new JLabel(I18N.getMsg("scene"));
		lb.setFont(FontUtil.getBoldFont());
		toolbar.add(lb);
		btTitle = SwingUtil.createButton("", "16x16/scene", "title");
		btTitle.addActionListener((ActionEvent evt) -> {
			changeSceneTitle();
		});
		toolbar.add(btTitle);

		return (toolbar);
	}

	private JPanel initStatusbar() {
		statusbar = new JPanel();
		statusbar.setName("TypistStatusbar");
		statusbar.setLayout(new MigLayout("ins 2"));
		statusbar.setSize(screenSize.width, defaultHeight);
		return (statusbar);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
	
	@Override
	public void refresh() {
		if (scene.getSummary() != null) {
			panelEdit.setText(scene.getSummary());
			panelEdit.setCaretPosition(0);
		}
		if (scene.hasChapter()) {
			btChapter.setText(scene.getChapter().getTitle());
		} else {
			btChapter.setText(I18N.getMsg("scene.hasnochapter"));
		}
		if (scene.getTitle().isEmpty()) {
			btTitle.setText(I18N.getMsg("scene.hasnotitle"));
		} else {
			btTitle.setText(scene.getTitle());
		}
		panelInfo.refreshInfo(scene);
		modified = false;
		btFirst.setEnabled(!isSceneFirst());
		btPrior.setEnabled(!isSceneFirst());
		btNext.setEnabled(!isSceneLast());
		btLast.setEnabled(!isSceneLast());
		entityCombo.setSelectedItem(scene);
		mainFrame.setLastUsedScene(scene);
		repaint();
	}

	private Scene getFirstScene() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findAll();
		if (scenes.isEmpty()) {
			return (null);
		}
		scene = scenes.get(0);
		return (scene);
	}

	private Scene getPriorScene() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findAll();
		for (int i = 1; i < scenes.size(); i++) {
			if (Objects.equals(scene.getId(), scenes.get(i).getId())) {
				scene = (scenes.get(i - 1));
				break;
			}
		}
		return (scene);
	}

	private Scene getNextScene() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findAll();
		for (int i = 0; i < scenes.size() - 1; i++) {
			if (Objects.equals(scene.getId(), scenes.get(i).getId())) {
				scene = (scenes.get(i + 1));
				break;
			}
		}
		return (scene);
	}

	private Scene getLastScene() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findAll();
		scene = scenes.get(scenes.size() - 1);
		return (scene);
	}

	public int askModified() {
		if (scene != null) {
			if (panelEdit.isTextChanged()) {
				if (panelEdit.getText().length() > 32767) {
					String str = I18N.getMsg("editor.text_too_large");
					JOptionPane.showMessageDialog(
						this,
						str,
						I18N.getMsg("editor"),
						JOptionPane.ERROR_MESSAGE);
					return (JOptionPane.CANCEL_OPTION);
				}
				modified = true;
				scene.setSummary(panelEdit.getText());
			}
		}
		if (modified) {
			final Object[] options = {I18N.getMsg("save.changes"),
				I18N.getMsg("discard.changes"),
				I18N.getMsg("cancel")};
			int n = JOptionPane.showOptionDialog(
				this,
				I18N.getMsg("save.or.discard.changes") + "\n\n"
				+ EntityUtil.getEntityTitle(scene) + ": "
				+ scene.toString() + "\n\n",
				I18N.getMsg("save.changes.title"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[2]);
			switch (n) {
				case JOptionPane.CANCEL_OPTION:
					return (JOptionPane.CANCEL_OPTION);
				case JOptionPane.YES_OPTION:
					save();
					break;
				case JOptionPane.NO_OPTION:
					break;
				default:
					break;
			}
		}
		return (JOptionPane.YES_OPTION);
	}

	private boolean isSceneFirst() {
		if (scene == null) {
			return (true);
		}
		return (false);
	}

	private boolean isSceneLast() {
		if (scene == null) {
			return (true);
		}
		return (false);
	}

	private boolean newScene() {
		boolean rc = false;
		Scene s = new Scene();
		s.setChapter(scene.getChapter());
		JDialog dlg = new JDialog(mainFrame, true);
		dlg.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		EntityEditor editor = new EntityEditor(mainFrame, s, dlg);
		dlg.setTitle(I18N.getMsg("editor"));
		Dimension pos = SwingUtil.getDlgPosition(dlg, s);
		if (pos != null) {
			dlg.setLocation(pos.height, pos.width);
		} else {
			dlg.setSize(this.getWidth() / 2, 680);
		}
		dlg.add(editor);
		Dimension size = SwingUtil.getDlgSize(dlg, s);
		if (size != null) {
			dlg.setSize(size.height, size.width);
		} else {
			dlg.setLocationRelativeTo(this);
		}
		dlg.setVisible(true);
		if (editor.entityLast != null) {
			scene = (Scene) editor.entityLast;
			refreshScenes(scene);
			entityCombo.setSelectedItem(scene);
		}
		SwingUtil.saveDlgPosition(dlg, s);
		return (rc);
	}

	@SuppressWarnings("unchecked")
	private void refreshScenes(Scene s) {
		entityComboInit = true;
		DefaultComboBoxModel combo = (DefaultComboBoxModel) entityCombo.getModel();
		combo.removeAllElements();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findAll();
		for (Scene entity : scenes) {
			combo.addElement(entity);
		}
		entityComboInit = false;
	}

	private void changeChapter() {
		ChapterSelectDlg dlg = new ChapterSelectDlg(mainFrame, scene.getChapter());
		dlg.setVisible(true);
		if (!dlg.isCanceled()) {
			Chapter chapter = dlg.getSelectedChapter();
			scene.setChapter(chapter);
			btChapter.setText(chapter.getTitle());
			setModified();
		}
	}

	private void changeSceneTitle() {
		String s = (String) JOptionPane.showInputDialog(
			this,"",I18N.getMsg("title"),JOptionPane.PLAIN_MESSAGE,null,null,scene.getTitle());
		if ((s != null) && (s.length() > 0)) {
			scene.setTitle(s);
			btTitle.setText(s);
			setModified();
		}
	}

	private void save() {
		BookController ctrl = mainFrame.getBookController();
		ctrl.updateScene(scene);
		toRefresh = true;
	}

	void setModified() {
		modified = true;
	}

	private void close() {
		mainFrame.activateTypist();
	}

}
