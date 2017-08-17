package storybook.ui.panel.memoria;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.infonode.docking.View;
import org.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import org.jdesktop.swingx.icon.EmptyIcon;

import storybook.SbConstants;
import storybook.SbApp;
import storybook.controller.BookController;
import storybook.i18n.I18N;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.dao.RelationshipDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.AbstractTag;
import storybook.model.hbn.entity.Attribute;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Relationship;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.model.hbn.entity.Tag;
import storybook.toolkit.BookUtil;
import storybook.toolkit.EnvUtil;
import storybook.toolkit.IOUtil;
import storybook.toolkit.Period;
import storybook.toolkit.FileFilter;
import storybook.toolkit.swing.IconButton;
import storybook.toolkit.swing.ScreenImage;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.combobox.EntityTypeListCellRenderer;
import storybook.ui.interfaces.IRefreshable;
import storybook.ui.options.OptionsDlg;
import storybook.ui.panel.AbstractPanel;

public class MemoriaPanel extends AbstractPanel implements ActionListener, IRefreshable {

	DelegateForest<AbstractEntity, Long> graph;
	private VisualizationViewer<AbstractEntity, Long> vv;
	private TreeLayout<AbstractEntity, Long> treeLayout;
	private BalloonLayout<AbstractEntity, Long> balloonLayout;
	private GraphZoomScrollPane graphPanel;
	Map<AbstractEntity, String> labelMap;
	Map<AbstractEntity, Icon> iconMap;
	private String entitySourceName;
	public long entityId;
	private AbstractEntity shownEntity;
	public long graphIndex;
	private ScalingControl scaler;
	//scenes
	Scene sceneVertex;
	String sceneVertexTitle;
	List<Long> sceneIds;
	//persons
	Person personVertex;
	//relationships
	Relationship relationshipVertex;
	private String relationshipVertexTitle;
	//locations
	Location locationVertex;
	String locationVertexTitle;
	//tag
	boolean showTagVertex = true;
	Tag tagVertex;
	Tag involvedTagVertex;
	Set<Tag> involvedTags;
	//items
	Item itemVertex;
	Item involvedItemVertex;
	Set<Item> involvedItems;
	//strands
	Strand strandVertex;
	private String strandVertexTitle;
	//panels and combos
	boolean showBalloonLayout = true;
	private JPanel controlPanel;
	private JPanel datePanel;
	private JComboBox entityTypeCombo;
	private JComboBox entityCombo;
	public Date chosenDate;
	private JComboBox dateCombo;
	private JCheckBox cbAutoRefresh;
	//icons
	public final Icon emptyIcon = new EmptyIcon();
	//processAction
	private boolean processActionListener = true;
	RelationshipDAOImpl relationshipDAO;

	public MemoriaPanel(MainFrame paramMainFrame) {
		super(paramMainFrame);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		try {
			String str = evt.getPropertyName();
			if (str == null) {
				return;
			}
			if (BookController.CommonProps.REFRESH.check(str)) {
				View newView = (View) evt.getNewValue();
				View oldView = (View) getParent().getParent();
				if (oldView == newView) {
					refresh();
				}
				return;
			}
			if (BookController.CommonProps.SHOW_IN_MEMORIA.check(str)) {
				AbstractEntity entity = (AbstractEntity) evt.getNewValue();
				refresh(entity);
				return;
			}
			if (BookController.CommonProps.SHOW_OPTIONS.check(str)) {
				View newView = (View) evt.getNewValue();
				View oldView = (View) getParent().getParent();
				if (newView.getName().equals(SbConstants.ViewName.MEMORIA.toString())) {
					OptionsDlg.show(mainFrame, newView.getName());
				}
				return;
			}
			if (BookController.MemoriaViewProps.BALLOON.check(str)) {
				showBalloonLayout = (Boolean) evt.getNewValue();
				makeLayoutTransition();
				return;
			}
			if ((str.startsWith("Update")) || (str.startsWith("Delete")) || (str.startsWith("New"))) {
				refresh();
				return;
			}
			if (BookController.CommonProps.EXPORT.check(str)) {
				View newView = (View) evt.getNewValue();
				View oldView = (View) getParent().getParent();
				if (newView == oldView) {
					export();
				}
			}
		} catch (Exception exc) {
			SbApp.error("MemoriaPanel.modelPropertyChange(" + evt.toString() + ")", exc);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init() {
		SbApp.trace("MemoriaPanel.init()");
		try {
			chosenDate = new Date(0L);
			entitySourceName = "";
			sceneIds = new ArrayList();
			involvedTags = new HashSet();
			involvedItems = new HashSet();
			scaler = new CrossoverScalingControl();
			try {
				Internal internal = BookUtil.get(mainFrame, SbConstants.BookKey.MEMORIA_BALLOON, true);
				showBalloonLayout = internal.getBooleanValue();
			} catch (Exception exc) {
				showBalloonLayout = true;
			}
		} catch (Exception exc2) {
			SbApp.error("MemoriaPanel.init()", exc2);
		}
	}

	@Override
	public void initUi() {
		SbApp.trace("MemoriaPanel.initUi()");
		try {
			MigLayout migLayout1 = new MigLayout("wrap,fill", "[]", "[][grow]");
			setLayout(migLayout1);
			setBackground(SwingUtil.getBackgroundColor());
			controlPanel = new JPanel();
			MigLayout migLayout2 = new MigLayout("flowx", "", "");
			controlPanel.setLayout(migLayout2);
			controlPanel.setOpaque(false);
			refreshControlPanel();
			initGraph();
			add(controlPanel, "alignx center");
			add(graphPanel, "grow");
		} catch (Exception exc) {
			SbApp.error("MemoriaPanel.modelPropertyChange()", exc);
		}
	}

	@SuppressWarnings("unchecked")
	private void refreshEntityCombo(EntityTypeCbItem.Type type) {
		SbApp.trace("MemoriaPanel.refreshEntityCombo("+type.name()+")");
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		List list;
		switch (type) {
			case SCENE:
				{
					SceneDAOImpl dao = new SceneDAOImpl(session);
					list = dao.findAll();
					refreshCombo(new Scene(), list, false);
					datePanel.setVisible(false);
					break;
				}
			case PERSON:
				{
					PersonDAOImpl dao = new PersonDAOImpl(session);
					list = dao.findAllByName();
					Person person = new Person();
					ArrayList array = new ArrayList();
					array.add(new Attribute("fd", "fds"));
					person.setAttributes(array);
					refreshCombo(person, list, false);
					datePanel.setVisible(true);
					break;
				}
			case LOCATION:
				{
					LocationDAOImpl dao = new LocationDAOImpl(session);
					list = dao.findAllByName();
					refreshCombo(new Location(), list, false);
					datePanel.setVisible(true);
					break;
				}
			case TAG:
				{
					TagDAOImpl dao = new TagDAOImpl(session);
					list = dao.findAllByName();
					refreshCombo(new Tag(), list, false);
					datePanel.setVisible(true);
					break;
				}
			case ITEM:
				{
					ItemDAOImpl dao = new ItemDAOImpl(session);
					list = dao.findAllByName();
					refreshCombo(new Item(), list, false);
					datePanel.setVisible(true);
					break;
				}
			case STRAND:
				{
					StrandDAOImpl dao = new StrandDAOImpl(session);
					list = dao.findAllByName();
					refreshCombo(new Strand(), list, false);
					datePanel.setVisible(false);
					break;
				}
			default:
				break;
		}
		model.commit();
	}

	void addIconButton(String icon, String btString) {
		SbApp.trace("MemoriaPanel.addIconButton("+icon+","+btString+")");
		IconButton ib = new IconButton("icon.small." + icon);
		ib.setSize32x20();
		ib.setName(btString);
		ib.addActionListener(this);
		datePanel.add((Component) ib);
	}

	@SuppressWarnings({"unchecked", "null"})
	private void refreshControlPanel() {
		SbApp.trace("MemoriaPanel.refreshControlPanel()");
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findAll();
		List<Date> dates = dao.findDistinctDates();
		dates.removeAll(Collections.singletonList(null));
		model.commit();
		Object entityTypeSelected = null;
		if (entityTypeCombo != null) {
			entityTypeSelected = entityTypeCombo.getSelectedItem();
		}
		Object entityComboSelected = null;
		if (entityCombo != null) {
			entityComboSelected = entityCombo.getSelectedItem();
		}
		Object dateComboSelected = null;
		if (dateCombo != null) {
			dateComboSelected = dateCombo.getSelectedItem();
		}
		dateCombo = new JComboBox();
		dateCombo.setPreferredSize(new Dimension(100, 20));
		dateCombo.addItem(null);
		for (Date onedate : dates) {
			dateCombo.addItem(onedate);
		}
		dateCombo.setName(SbConstants.ComponentName.COMBO_DATES.toString());
		dateCombo.setMaximumRowCount(15);
		if (dateComboSelected != null) {
			dateCombo.setSelectedItem(dateComboSelected);
		}
		datePanel = new JPanel(new MigLayout("flowx,ins 0"));
		datePanel.setOpaque(false);
		datePanel.setVisible(false);
		datePanel.add(dateCombo);
		addIconButton("first", SbConstants.ComponentName.BT_FIRST.toString());
		addIconButton("next", SbConstants.ComponentName.BT_NEXT.toString());
		addIconButton("previous", SbConstants.ComponentName.BT_PREVIOUS.toString());
		addIconButton("last", SbConstants.ComponentName.BT_LAST.toString());
		entityTypeCombo = new JComboBox();
		entityTypeCombo.setPreferredSize(new Dimension(120, 20));
		entityTypeCombo.setName(SbConstants.ComponentName.COMBO_ENTITY_TYPES.toString());
		entityTypeCombo.setRenderer(new EntityTypeListCellRenderer());
		entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.SCENE));
		entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.PERSON));
		entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.LOCATION));
		entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.TAG));
		entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.ITEM));
		entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.STRAND));
		if (entityTypeSelected != null) {
			entityTypeCombo.setSelectedItem(entityTypeSelected);
		}
		entityCombo = new JComboBox();
		entityCombo.setName(SbConstants.ComponentName.COMBO_ENTITIES.toString());
		entityCombo.setMaximumRowCount(15);
		if (entityComboSelected != null) {
			EntityTypeCbItem cbbItem = (EntityTypeCbItem) entityTypeSelected;
			refreshEntityCombo(cbbItem.getType());
			entityCombo.setSelectedItem(entityComboSelected);
		} else {
			refreshCombo(new Scene(), scenes, false);
		}
		controlPanel.removeAll();
		controlPanel.add(entityTypeCombo);
		controlPanel.add(entityCombo, "gapafter 32");
		controlPanel.add(datePanel);
		controlPanel.revalidate();
		controlPanel.repaint();
		entityTypeCombo.addActionListener(this);
		entityCombo.addActionListener(this);
		dateCombo.addActionListener(this);
	}

	@SuppressWarnings("unchecked")
	private void makeLayoutTransition() {
		if (vv == null) {
			return;
		}
		LayoutTransition layout;
		if (showBalloonLayout) {
			layout = new LayoutTransition(vv, treeLayout, balloonLayout);
		} else {
			layout = new LayoutTransition(vv, balloonLayout, treeLayout);
		}
		Animator animator = new Animator(layout);
		animator.start();
		vv.repaint();
	}

	@SuppressWarnings("unchecked")
	private void clearGraph() {
		try {
			if (graph == null) {
				graph = new DelegateForest();
				return;
			}
			Collection collections = graph.getRoots();
			Iterator iCollection = collections.iterator();
			while (iCollection.hasNext()) {
				AbstractEntity entity = (AbstractEntity) iCollection.next();
				if (entity != null) {
					graph.removeVertex(entity);
				}
			}
		} catch (Exception exc) {
			graph = new DelegateForest();
		}
	}

	public void zoomIn() {
		scaler.scale(vv, 1.1F, vv.getCenter());
	}

	public void zoomOut() {
		scaler.scale(vv, 0.9090909F, vv.getCenter());
	}

	public void export() {
		try {
			if (shownEntity == null) {
				return;
			}
			Internal internal = BookUtil.get(mainFrame, SbConstants.BookKey.EXPORT_DIRECTORY, EnvUtil.getDefaultExportDir(mainFrame));
			File file1 = new File(internal.getStringValue());
			JFileChooser chooser = new JFileChooser(file1);
			FileFilter filter=new FileFilter(FileFilter.PNG,I18N.getMsg("file.type.png"));
			chooser.setFileFilter(filter);
			chooser.setApproveButtonText(I18N.getMsg("export"));
			String str = IOUtil.getEntityFileNameForExport(mainFrame, "Memoria", shownEntity);
			chooser.setSelectedFile(new File(str));
			int i = chooser.showDialog(this, I18N.getMsg("export"));
			if (i == 1) {
				return;
			}
			File file2 = chooser.getSelectedFile();
			if (!file2.getName().endsWith(".png")) {
				file2 = new File(file2.getPath() + ".png");
			}
			ScreenImage.createImage(graphPanel, file2.toString());
			JOptionPane.showMessageDialog(this, I18N.getMsg("export.success") + "\n" + file2.getAbsolutePath(), I18N.getMsg("export"), 1);
		} catch (IOException exc) {
			SbApp.error("MemoriaPanel.export()", exc);
		}
	}

	@SuppressWarnings("unchecked")
	private void refreshCombo(AbstractEntity pEntity, List<? extends AbstractEntity> pList, boolean b) {
		SbApp.trace("MemoriaPanel.refreshCombo('???',...)");
		try {
			processActionListener = false;
			DefaultComboBoxModel combo = (DefaultComboBoxModel) entityCombo.getModel();
			combo.removeAllElements();
			combo.addElement(pEntity);
			for (AbstractEntity entity : pList) {
				combo.addElement(entity);
			}
			processActionListener = true;
		} catch (Exception exc) {
			SbApp.error("MemoriaPanel.refreshCombo(" + pEntity.toString() + ", list<" + ">, " + b + ")", exc);
		}
	}

	@SuppressWarnings("unchecked")
	private void initGraph() {
		SbApp.trace("MemoriaPanel.initGraph()");
		try {
			labelMap = new HashMap();
			iconMap = new HashMap();
			graph = new DelegateForest();
			treeLayout = new TreeLayout(graph);
			balloonLayout = new BalloonLayout(graph);
			vv = new VisualizationViewer(balloonLayout);
			vv.setSize(new Dimension(800, 800));
			refreshGraph();
			vv.setBackground(Color.white);
			vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
			vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
			vv.setVertexToolTipTransformer(new EntityTransformer());
			graphPanel = new GraphZoomScrollPane(vv);
			DefaultModalGraphMouse mouse = new DefaultModalGraphMouse();
			vv.setGraphMouse(mouse);
			mouse.add(new MemoriaGraphMouse(this));
			// T O D O  MemoriaPanel compile error suppress 2 lines
			VertexStringerImpl localVertexStringerImpl = new VertexStringerImpl(labelMap);
			vv.getRenderContext().setVertexLabelTransformer(localVertexStringerImpl);
			VertexIconShapeTransformer transformer = new VertexIconShapeTransformer(new EllipseVertexShapeTransformer());
			DefaultVertexIconTransformer iconTransformer = new DefaultVertexIconTransformer();
			transformer.setIconMap(iconMap);
			iconTransformer.setIconMap(iconMap);
			vv.getRenderContext().setVertexShapeTransformer(transformer);
			vv.getRenderContext().setVertexIconTransformer(iconTransformer);
		} catch (Exception exc) {
			System.err.println(exc.getLocalizedMessage());
			System.err.println(Arrays.toString(exc.getStackTrace()));
		}
	}

	private void refreshGraph() {
		refreshGraph(null);
	}

	@SuppressWarnings({"unchecked", "unchecked"})
	private void refreshGraph(AbstractEntity entity) {
		if (entity==null) {
			SbApp.trace("MemoriaPanel.refreshGraph(null)");
		}
		else SbApp.trace("MemoriaPanel.refreshGraph("+entity.toText()+")");
		try {
			clearGraph();
			if (entity == null) {
				entity = (AbstractEntity) entityCombo.getItemAt(0);
			}
			//if ((!(entity instanceof Scene)) && (chosenDate == null)) {
			//	return;
			//}
			if ((entity instanceof Scene)) {
				GraphScene.create(this);
			} else if ((entity instanceof Person)) {
				GraphPerson.create(this);
			} else if ((entity instanceof Location)) {
				GraphLocation.create(this);
			} else if ((entity instanceof Tag)) {
				GraphTag.create(this);
			} else if ((entity instanceof Item)) {
				GraphItem.create(this);
			} else if ((entity instanceof Strand)) {
				GraphStrand.createStrandGraph(this);
			}
			shownEntity = entity;
			treeLayout = new TreeLayout(graph);
			balloonLayout = new BalloonLayout(graph);
			Dimension dimension = mainFrame.getSize();
			balloonLayout.setSize(new Dimension(dimension.width / 2, dimension.height / 2));
			balloonLayout.setGraph(graph);
			if (showBalloonLayout) {
				vv.setGraphLayout(balloonLayout);
			} else {
				vv.setGraphLayout(treeLayout);
			}
			vv.repaint();
		} catch (Exception exc) {
			SbApp.error("MemoriaPanel.refreshGraph()", exc);
			System.err.println(Arrays.toString(exc.getStackTrace()));
		}
	}

	private boolean isNothingSelected() {
		return entityId <= -1L;
	}

	private void showMessage(String paramString) {
		Graphics2D graphics2D = (Graphics2D) vv.getGraphics();
		if (graphics2D == null) {
			return;
		}
		Rectangle rectangle = vv.getBounds();
		int i = (int) rectangle.getCenterX();
		int j = (int) rectangle.getCenterY();
		graphics2D.setColor(Color.lightGray);
		graphics2D.fillRect(i - 200, j - 20, 400, 40);
		graphics2D.setColor(Color.black);
		graphics2D.drawString(paramString, i - 180, j + 5);
	}

	private void scaleToLayout(ScalingControl scalingControl) {
		Dimension dimension1 = vv.getPreferredSize();
		if (vv.isShowing()) {
			dimension1 = vv.getSize();
		}
		Dimension dimension2 = vv.getGraphLayout().getSize();
		if (!dimension1.equals(dimension2)) {
			scalingControl.scale(vv, (float) (dimension1.getWidth() / dimension2.getWidth()), new Point2D.Double());
		}
	}

	@SuppressWarnings("unchecked")
	void removeDoublesFromInvolvedTags(Set<Tag> set1) {
		List<Tag> tagList = new ArrayList();
		for (Tag atag : involvedTags) {
			for (Tag tag : set1) {
				if (tag.getId().equals(atag.getId())) {
					tagList.add(atag);
				}
			}
		}
		for (Tag atag : tagList) {
			involvedTags.remove(atag);
		}
	}

	@SuppressWarnings("unchecked")
	void removeDoublesFromInvolvedItems(Set<Item> set2) {
		List<AbstractTag> tagList = new ArrayList();
		for (AbstractTag atag : involvedItems) {
			for (Item item : set2) {
				if (item.getId().equals(atag.getId())) {
					tagList.add(atag);
				}
			}
		}
		for (AbstractTag atag : tagList) {
			involvedItems.remove((Item) atag);
		}
	}

	@SuppressWarnings("unchecked")
	void initVertices(AbstractEntity entity) {
		GraphScene.init(this,entity);
		GraphPerson.init(this,entity);
		GraphRelationship.init(this,entity);
		GraphLocation.init(this,entity);
		if (showTagVertex) {
			if (!(entity instanceof Tag)) {
				GraphTag.init(this,entity);
			}
			if (!(entity instanceof Item)) {
				GraphItem.init(this,entity);
			}
		}
		GraphTag.initInvolded(this,entity);
		GraphItem.initInvolded(this,entity);
		sceneIds = new ArrayList();
		involvedTags = new HashSet();
		involvedItems = new HashSet();
		sceneVertexTitle = null;
		locationVertexTitle = null;
		showTagVertex = true;
	}

	public void refresh(AbstractEntity entity) {
		SbApp.trace("MemoriaPanel.refresh("+(entity!=null?entity.toString():"null")+")");
		if (entity == null) {
			return;
		}
		try {
			entityId = entity.getId();
			refreshGraph(entity);
			updateControlPanel(entity);
		} catch (Exception exc) {
			SbApp.error("MemoriaPanel.refresh(" + entity.toString() + ")", exc);
		}
	}

	private void updateControlPanel(AbstractEntity pEntity) {
		SbApp.trace("MemoriaPanel.upDatePanel("+pEntity.toString()+")");
		int i = 0;
		EntityTypeCbItem tobj;
		for (int j = 0; j < entityTypeCombo.getItemCount(); j++) {
			tobj = (EntityTypeCbItem) entityTypeCombo.getItemAt(j);
			if ((tobj.getType() == EntityTypeCbItem.Type.PERSON) && ((pEntity instanceof Person))) {
				i = j;
				break;
			}
			if ((tobj.getType() == EntityTypeCbItem.Type.LOCATION) && ((pEntity instanceof Location))) {
				i = j;
				break;
			}
			if ((tobj.getType() == EntityTypeCbItem.Type.SCENE) && ((pEntity instanceof Scene))) {
				i = j;
				break;
			}
			if ((tobj.getType() == EntityTypeCbItem.Type.TAG) && ((pEntity instanceof Tag))) {
				i = j;
				break;
			}
			if ((tobj.getType() == EntityTypeCbItem.Type.ITEM) && ((pEntity instanceof Item))) {
				i = j;
				break;
			}
			if ((tobj.getType() == EntityTypeCbItem.Type.STRAND) && ((pEntity instanceof Strand))) {
				i = j;
				break;
			}
		}
		entityTypeCombo.setSelectedIndex(i);
		int j;
		if ((pEntity instanceof Scene)) {
			for (j = 0; j < entityCombo.getItemCount(); j++) {
				Scene s = (Scene) entityCombo.getItemAt(j);
				if (s.getId().equals(pEntity.getId())) {
					entityCombo.setSelectedIndex(j);
					break;
				}
			}
		} else {
			entityCombo.setSelectedItem(pEntity);
		}
	}

	public boolean hasAutoRefresh() {
		return cbAutoRefresh.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if ((evt.getSource() == null) || (!processActionListener)) {
			return;
		}
		if (evt.getSource() instanceof JButton) {
			String buttonString = ((JButton) evt.getSource()).getName();
			int i = dateCombo.getSelectedIndex();
			if (dateCombo.getSize().height == 0) {
				return;
			}
			if (SbConstants.ComponentName.BT_PREVIOUS.check(buttonString)) {
				i--;
				if (i < 0) {
					return;
				}
			} else if (SbConstants.ComponentName.BT_NEXT.check(buttonString)) {
				i++;
				if (i > dateCombo.getItemCount() - 1) {
					return;
				}
			} else if (SbConstants.ComponentName.BT_FIRST.check(buttonString)) {
				i = 0;
			} else if (SbConstants.ComponentName.BT_LAST.check(buttonString)) {
				i = dateCombo.getItemCount() - 1;
			}
			dateCombo.setSelectedIndex(i);
			return;
		}
		entitySourceName = ((JComponent) evt.getSource()).getName();
		if (entitySourceName.equals(SbConstants.ComponentName.COMBO_ENTITY_TYPES.toString())) {
			EntityTypeCbItem eCombo = (EntityTypeCbItem) entityTypeCombo.getSelectedItem();
			refreshEntityCombo(eCombo.getType());
			return;
		}
		chosenDate = ((Date) dateCombo.getSelectedItem());
		refresh((AbstractEntity) entityCombo.getSelectedItem());
	}

	@Override
	public void refresh() {
		refreshControlPanel();
		refreshGraph();
	}

	@Override
	public MainFrame getMainFrame() {
		return mainFrame;
	}

	private boolean isInPeriod(Period period, Date date) {
		return (period == null) || (date == null) || (period.isInside(date));
	}
	
	public Icon getIconPerson(Person person, boolean medium) {
		Icon icon;
		if (medium) {
			if (person==null) icon=I18N.getIcon("icon.medium.person");
			else if (person.getGender().getIcone()!=null && !person.getGender().getIcone().isEmpty()) {
				icon=I18N.resizeIcon((ImageIcon) person.getImageIcon(), new Dimension(32,32));
			}
			else if (person.getGender().isMale()) icon=I18N.getIcon("icon.medium.man");
			else if (person.getGender().isFemale()) icon=I18N.getIcon("icon.medium.woman");
			else icon=I18N.getIcon("icon.medium.person");
		} else {
			if (person==null) icon=I18N.getIcon("icon.large.person");
			else if (person.getGender().getIcone()!=null && !person.getGender().getIcone().isEmpty()) 
				icon=I18N.resizeIcon((ImageIcon) person.getImageIcon(), new Dimension(64,64));
			else if (person.getGender().isMale()) icon=I18N.getIcon("icon.large.man");
			else if (person.getGender().isFemale()) icon=I18N.getIcon("icon.large.woman");
			else icon=I18N.getIcon("icon.large.person");
		}
		return(icon);
	}

	public Icon getIconItem(Item item, boolean medium) {
		Icon icon;
		if (medium) {
			if (!item.getIcone().isEmpty()) icon=I18N.resizeIcon((ImageIcon) item.getImageIcon(), new Dimension(32,32));
			else icon=I18N.getIcon("icon.medium.item");
		} else {
			if (!item.getIcone().isEmpty()) icon=I18N.resizeIcon((ImageIcon) item.getImageIcon(), new Dimension(64,64));
			else icon=I18N.getIcon("icon.large.item");
		}
		return(icon);
	}

	public Icon getIconTag(Tag tag, boolean medium) {
		Icon icon;
		if (medium) {
			if (tag.getIcone().isEmpty()) icon=I18N.resizeIcon((ImageIcon) tag.getImageIcon(), new Dimension(32,32));
			else icon=I18N.getIcon("icon.medium.tag");
		} else {
			if (tag.getIcone().isEmpty()) icon=I18N.resizeIcon((ImageIcon) tag.getImageIcon(), new Dimension(64,64));
			else icon=I18N.getIcon("icon.large.tag");
		}
		return(icon);
	}

}
