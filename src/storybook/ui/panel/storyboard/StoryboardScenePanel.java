/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2016 FaVdB

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

package storybook.ui.panel.storyboard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.text.DateFormat;
import javax.swing.AbstractAction;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import storybook.SbConstants;
import storybook.controller.BookController;
import storybook.model.EntityUtil;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.DateUtil;
import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.label.SceneStateLabel;
import storybook.ui.panel.linkspanel.LocationLinksPanel;
import storybook.ui.panel.linkspanel.PersonLinksPanel;
import storybook.ui.panel.linkspanel.StrandLinksPanel;

import org.miginfocom.swing.MigLayout;
import storybook.action.DeleteEntityAction;
import storybook.action.EditEntityAction;
import storybook.model.hbn.entity.Strand;
import storybook.toolkit.swing.IconButton;
import storybook.ui.panel.AbstractGradientPanel;
import storybook.ui.panel.linkspanel.ItemLinksPanel;

@SuppressWarnings("serial")
public class StoryboardScenePanel extends AbstractGradientPanel implements FocusListener {

	private final String CN_UPPER_PANEL = "upperPanel";

	private JPanel upperPanel;
	private JLabel lbStatus;
	private JLabel lbInformational;
	private JLabel lbSceneNo;
	private JLabel lbTime;

	private Integer size;
	protected Scene scene;
	protected AbstractAction newAction;

	protected IconButton btNew;
	protected IconButton btEdit;
	protected IconButton btDelete;

	public StoryboardScenePanel(MainFrame mainFrame, Scene scene) {
		super(mainFrame, true, Color.white, scene.getStrand().getJColor());
		this.mainFrame=mainFrame;
		this.scene = scene;
		init();
		initUi();
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	protected AbstractAction getNewAction() {
		if (newAction == null) {
			newAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//BookController ctrl = mainFrame.getBookController();
					Scene newScene = new Scene();
					newScene.setStrand(scene.getStrand());
					newScene.setSceneTs(scene.getSceneTs());
					if (scene.hasChapter()) {
						newScene.setChapter(scene.getChapter());
					}
					//ctrl.setSceneToEdit(newScene);
					mainFrame.showEditorAsDialog(newScene);
				}
			};
		}
		return newAction;
	}

	protected IconButton getEditButton() {
		if (btEdit != null) {
			return btEdit;
		}
		btEdit = new IconButton("icon.small.edit", new EditEntityAction(mainFrame, scene,false));
		btEdit.setText("");
		btEdit.setSize32x20();
		btEdit.setToolTipText(I18N.getMsg("edit"));
		return btEdit;
	}

	protected IconButton getDeleteButton() {
		if (btDelete != null) {
			return btDelete;
		}
		btDelete = new IconButton("icon.small.delete", new DeleteEntityAction(mainFrame, scene));
		btDelete.setText("");
		btDelete.setSize32x20();
		btDelete.setToolTipText(I18N.getMsg("delete"));
		return btDelete;
	}

	protected IconButton getNewButton() {
		if (btNew != null) {
			return btNew;
		}
		btNew = new IconButton("icon.small.new", getNewAction());
		btNew.setSize32x20();
		btNew.setToolTipText(I18N.getMsg("new"));
		return btNew;
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		String propName = evt.getPropertyName();

		if (BookController.StrandProps.UPDATE.check(propName)) {
			EntityUtil.refresh(mainFrame, scene.getStrand());
			repaint();
			return;
		}

		if (BookController.SceneProps.UPDATE.check(propName)) {
			Scene newScene = (Scene) newValue;
			if (!newScene.getId().equals(scene.getId())) {
				// not this scene
				return;
			}
			scene = newScene;
			lbSceneNo.setText(scene.getChapterSceneNo(false));
			lbSceneNo.setToolTipText(scene.getChapterSceneToolTip());
			lbStatus.setIcon(scene.getStatusIcon());
			if (scene.hasSceneTs()) {
				if (!DateUtil.isZeroTimeDate(scene.getSceneTs())) {
					DateFormat formatter = I18N.getDateTimeFormatter();
					lbTime.setText(formatter.format(scene.getSceneTs()));
				} else {
					lbTime.setText("");
				}
			}
			return;
		}

		if (BookController.StoryboardViewProps.ZOOM.check(propName)) {
			setZoomedSize((Integer) newValue);
			refresh();
		}
	}

	private void setZoomedSize(int zoomValue) {
		size = zoomValue * 7;
	}

	@Override
	public void init() {
		setZoomedSize(SbConstants.DEFAULT_STORYBOARD_ZOOM);
	}

	@Override
	public void initUi() {
		refresh();
	}

	@Override
	public void refresh() {
		MigLayout layout = new MigLayout("fill,flowy,insets 0", "[]", "[][grow]");
		setLayout(layout);
		setPreferredSize(new Dimension(size, size));
		setComponentPopupMenu(EntityUtil.createPopupMenu(mainFrame, scene));

		removeAll();

		// strand links
		StrandLinksPanel strandLinksPanel = new StrandLinksPanel(mainFrame, scene, true);
		Strand strand=scene.getStrand();
		JLabel lbStrand=new JLabel("  ");
		lbStrand.setToolTipText(strand.getName());
		lbStrand.setBackground(strand.getJColor());
		lbStrand.setOpaque(true);

		// person links
		PersonLinksPanel personLinksPanel = new PersonLinksPanel(mainFrame, scene);

		// location links
		LocationLinksPanel locationLinksPanel = new LocationLinksPanel(mainFrame, scene);
		JLabel lbLocation= new JLabel(" ");
		if (!scene.getLocations().isEmpty()) lbLocation.setText(scene.getLocations().get(0).getName());

		// location links
		ItemLinksPanel itemLinksPanel = new ItemLinksPanel(mainFrame, scene);

		// button new
		btNew = getNewButton();
		btNew.setSize20x20();
		// btNew.setName(COMP_NAME_BT_NEW);

		// button remove
		btDelete = getDeleteButton();
		btDelete.setSize20x20();
		// btDelete.setName(COMP_NAME_BT_REMOVE);

		// button edit
		btEdit = getEditButton();
		btEdit.setSize20x20();
		// btEdit.setName(COMP_NAME_BT_EDIT);

		// chapter and scene number
		lbSceneNo = new JLabel("", SwingConstants.LEFT);
		lbSceneNo.setText("n°"+scene.getChapterSceneNo(false));
		lbSceneNo.setToolTipText(scene.getChapterSceneToolTip());
		//lbSceneNo.setOpaque(true);
		//lbSceneNo.setBackground(Color.white);

		// status
		lbStatus = new SceneStateLabel(scene.getSceneState(), true);

		// informational
		lbInformational = new JLabel("");
		if (scene.getInformative()) {
			lbInformational.setIcon(I18N.getIcon("icon.small.info"));
			lbInformational.setToolTipText(I18N .getMsg("informative"));
		}

		// scene time
		lbTime = new JLabel();
		if (scene.hasSceneTs()) {
			if (!DateUtil.isZeroTimeDate(scene.getSceneTs())) {
				DateFormat formatter = I18N.getDateTimeFormatter();
				lbTime.setText(formatter.format(scene.getSceneTs()));
			}
		} else lbTime.setText(" ");

		// title
		JLabel lbTitle=new JLabel(scene.getTitle());
		lbTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		lbTitle.setToolTipText(getToolTitle());

		// layout

		// button panel
		JPanel buttonPanel = new JPanel(new MigLayout("flowy,insets 0"));
		buttonPanel.setName("buttonpanel");
		buttonPanel.setOpaque(false);
		buttonPanel.add(btEdit);
		buttonPanel.add(btDelete);
		buttonPanel.add(btNew);
		
		JLabel lbTop=new JLabel(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/stamp.png")));
		JLabel lbBottom=new JLabel(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/stamp.png")));

		upperPanel = new JPanel(new MigLayout("insets 6 0 0 0", "[][grow][]", "[top][top][top]"));
		upperPanel.setName(CN_UPPER_PANEL);
		upperPanel.setOpaque(false);
		upperPanel.add(lbTop,"spanx, wrap");
		upperPanel.add(lbTitle, "grow,spanx 2");
		upperPanel.add(lbStatus);
		upperPanel.add(lbInformational);
		upperPanel.add(lbStrand, "grow");
		upperPanel.add(buttonPanel, "spany 4,wrap");
		JScrollPane scroller = new JScrollPane(personLinksPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setMinimumSize(new Dimension(20, 16));
		scroller.setOpaque(false);
		scroller.getViewport().setOpaque(false);
		scroller.setBorder(null);
		upperPanel.add(scroller, "spanx,growx,wrap");
		upperPanel.add(lbLocation, "spanx 3,grow,wrap");
		upperPanel.add(itemLinksPanel, "spanx 3,grow,wrap");
		upperPanel.add(lbTime,"spanx,wrap");

		upperPanel.add(lbBottom,"spanx");
		// main panel
		add(upperPanel);

		revalidate();
		repaint();
	}

	protected StoryboardScenePanel getThis() {
		return this;
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof JTextComponent) {
			JTextComponent tc = (JTextComponent) e.getSource();
			switch (tc.getName()) {
				//case CN_TITLE:
				//	scene.setTitle(tc.getText());
				//	break;
			}
			mainFrame.getBookController().updateScene(scene);
		}
	}

	private String getToolTitle() {
		StringBuilder b=new StringBuilder();
		b.append("<html><p>");
		b.append("n°").append(" <b>").append(scene.getSceneno()).append("</b><br>");
		if (!scene.getNotes().isEmpty())
			b.append("notes: ").append(scene.getNotes()).append("<br>");
		b.append("</p></html>");
		return(b.toString());
	}
}
