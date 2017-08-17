package storybook.ui.panel.planning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.hibernate.Session;
//import org.jfree.data.gantt.TaskSeries;

import org.miginfocom.swing.MigLayout;
import storybook.SbApp;
import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.model.stringcategory.AbstractStringCategory;
import storybook.toolkit.BookUtil;
import storybook.i18n.I18N;
import storybook.toolkit.StringCategoryUtil;
import storybook.toolkit.odt.ODTUtils;
import storybook.toolkit.swing.CircleProgressBar;
import storybook.toolkit.swing.ColorUtil;
import storybook.ui.MainFrame;
import storybook.ui.SbView;
import storybook.ui.chart.timeline.Dataset;
import storybook.ui.chart.timeline.DatasetItem;
import storybook.ui.chart.timeline.Timeline;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.panel.tree.Tree;

/**
 * Panel for planfication vision.
 *
 * @author Jean
 *
 */
@SuppressWarnings("serial")
public class Planning extends AbstractPanel implements MouseListener {

	/**
	 * Pane to contain all.
	 */
	private JTabbedPane tabbedPane;
	private JTree tree;
	CircleProgressBar[] progress = new CircleProgressBar[5];
	private Dataset dataset;
	private Timeline timeline;
	private DefaultMutableTreeNode topNode;
	private PlanningElement topSp;

	/**
	 * Constructor.
	 *
	 * @param mainframe to include panel in.
	 */
	public Planning(MainFrame mainframe) {
		super(mainframe);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see storybook.ui.panel.AbstractPanel#init()
	 */
	@Override
	public void init() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see storybook.ui.panel.AbstractPanel#initUi()
	 */
	@Override
	public void initUi() {
		SbApp.trace("Planning.initUi()");
		// Create tabbed pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		MigLayout migLayout0 = new MigLayout("fill", "[grow]", "[grow]");
		setLayout(migLayout0);

		// add all subpanels to tabbed pane
		addGlobalPanel();
		addTimePanel();
		addProgressPanel();

		// add tabbed pane to global panel
		add(tabbedPane, "pos 0% 0% 100% 100%");
	}

	/**
	 * Generate panel for showing global information
	 */
	private void addGlobalPanel() {
		SbApp.trace("Planning.addGlobalPanel()");
		// create panel
		JPanel globalPanel = new JPanel();
		MigLayout migLayout = new MigLayout("wrap 5,fill", "[center, sizegroup]",
			"[][grow]");
		globalPanel.setLayout(migLayout);
		tabbedPane.addTab(I18N.getMsg("plan.title.global"), globalPanel);

		// Get labels
		String[] labels = new String[6];
		labels[0] = I18N.getMsg("status.outline");
		labels[1] = I18N.getMsg("status.draft");
		labels[2] = I18N.getMsg("status.1st.edit");
		labels[3] = I18N.getMsg("status.2nd.edit");
		labels[4] = I18N.getMsg("status.done");
		labels[5] = I18N.getMsg("status.outline");

		// add progress bars
		for (int i = 0; i < 5; i++) {
			progress[i] = new CircleProgressBar(0, 100);
			progress[i].setPreferredSize(new Dimension(100, 100));
			progress[i].setBorderPainted(false);
			globalPanel.add(progress[i], "split 2, flowy");
			globalPanel.add(new JLabel(labels[i], SwingConstants.CENTER));
		}
		refreshProgressBarsValues();
	}

	private void refreshProgressBarsValues() {
		SbApp.trace("Planning.setProgressBarsValues()");
		// get neded elements
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		int allScenes = dao.findAll().size();
		int[] nbScenesByState = new int[6];
		for (int i = 0; i < 6; i++) {
			nbScenesByState[i] = dao.findBySceneState(i).size();
		}
		session.close();
		for (int i = 0; i < 5; i++) {
			progress[i].setValue((nbScenesByState[i + 1] * 100) / ((allScenes == 0) ? 1 : allScenes));
		}

	}

	/**
	 * Generate panel for showing size progress information
	 */
	private void addProgressPanel() {
		topSp = new PlanningElement();
		topSp.setElement(mainFrame.getDbFile().getName());
		// create panel
		topNode = new DefaultMutableTreeNode(topSp);

		tree = new Tree(topNode);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new PlanningTreeCellRenderer());
		JScrollPane scroller = new JScrollPane(tree);
		
		refreshProgressValues();

		tree.expandRow(0);
		tabbedPane.addTab(I18N.getMsg("plan.title.progress"), scroller);
		tree.addMouseListener(this);
	}
	
	private void refreshProgressValues() {
		SbApp.trace("Planning.setProgressValues()");
		topNode.removeAllChildren();
		Map<Object, Integer> sizes = ODTUtils.getElementsSize(mainFrame);

		// get elements
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl partdao = new PartDAOImpl(session);
		List<Part> parts = partdao.findAllRoots();
		session.close();

		int topsize = 0;
		int topmaxsize = 0;
		for (Part part : parts) {
			createSubStructure(topNode, part, sizes);
			topmaxsize += part.getObjectiveChars();
			topsize += sizes.get(part);
		}
		if (topmaxsize == 0) {
			topSp.setSize(100);
			topSp.setMaxSize(100);
		} else {
			topSp.setSize(topsize);
			topSp.setMaxSize(topmaxsize);
		}
	}

	private void createSubStructure(DefaultMutableTreeNode father, Part part, Map<Object, Integer> sizes) {
		SbApp.trace("Planning.createSubStructure("+father.toString()+", "+part.getName()+", sizes)");
		PlanningElement sp = new PlanningElement();
		sp.setElement(part);
		sp.setSize(sizes.get(part));
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(sp);
		father.add(node);

		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl partdao = new PartDAOImpl(session);
		List<Part> subparts = partdao.getParts(part);
		List<Chapter> chapters = partdao.findChapters(part);
		session.close();

		for (Part subpart : subparts) {
			createSubStructure(node, subpart, sizes);
		}
		for (Chapter chapter : chapters) {
			createSubStructure(node, chapter, sizes);
		}

		// align objective chars value with the one of contained elements
		int subObjective = 0;
		for (Part subpart : subparts) {
			subObjective += subpart.getObjectiveChars();
		}
		for (Chapter chapter : chapters) {
			subObjective += chapter.getObjectiveChars();
		}
		if (subObjective > part.getObjectiveChars()) {
			part.setObjectiveChars(subObjective);
		}
	}

	private void createSubStructure(DefaultMutableTreeNode father, Chapter chapter, Map<Object, Integer> sizes) {
		SbApp.trace("Planning.createSubStructure("+father.toString()+", "+chapter.getTitle()+", sizes)");
		PlanningElement sp = new PlanningElement();
		sp.setElement(chapter);
		sp.setSize(sizes.get(chapter));
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(sp);
		father.add(node);

		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl chapterdao = new ChapterDAOImpl(session);
		List<Scene> scenes = chapterdao.findScenes(chapter);
		session.close();

		for (Scene scene : scenes) {
			PlanningElement ssp = new PlanningElement();
			ssp.setElement(scene);
			ssp.setSize(sizes.get(scene));
			DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(ssp);
			node.add(subnode);
		}

	}

	/**
	 * Generate panel for showing timeline information
	 */
	private void addTimePanel() {
		SbApp.trace("Planning.addTimePanel()");
		// create panel and scroller to contain it
		JPanel timePanel = new JPanel();
		JScrollPane scroller = new JScrollPane(timePanel);
		add(scroller, "grow");
		MigLayout layout = new MigLayout("wrap 5,fill", "[center]", "[][grow]");
		timePanel.setLayout(layout);
		tabbedPane.add(I18N.getMsg("plan.title.timeline"), scroller);

		// add chart
		timeline=createTimeline();
		timePanel.add(timeline, "wrap, grow");
		
	}
	
	private Timeline createTimeline() {
		SbApp.trace("Planning.createTimeline()");
		dataset = new Dataset(mainFrame);
		dataset.items=new ArrayList<>();
		BookModel model = this.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl partDao = new PartDAOImpl(session);
		List<Part> parts = partDao.findAllRoots();
		session.close();
		Color[] color=ColorUtil.getNiceColors();
		int nc=0;
		for (Part part : parts) {
			dataset.items.add(createPartItem(part,color[nc++]));
			if (nc>=color.length) nc=0;
		}
		dataset.listId=new ArrayList<>();
		
		Timeline localTimeline = new Timeline("date", dataset);
		return(localTimeline);
	}
	
	private void refreshTimelineValues() {
		for (int i=0;i<dataset.items.size(); i++) {
			dataset.items.remove(0);
		}
		BookModel model = this.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl partDao = new PartDAOImpl(session);
		List<Part> parts = partDao.findAllRoots();
		session.close();
		Color[] color=ColorUtil.getNiceColors();
		int nc=0;
		for (Part part : parts) {
			dataset.items.add(createPartItem(part,color[nc++]));
			if (nc>=color.length) nc=0;
		}
		dataset.listId=new ArrayList<>();
		timeline.redraw();
	}
	
	private DatasetItem createPartItem(Part part, Color color) {
		SbApp.trace("Planning.createPartItem("+part.getName()+", "+color.getRGB()+")");
		Date ddj=java.util.Calendar.getInstance().getTime();
		Date debut;
		if (part.hasCreationTime()) debut=new Date(part.getCreationTime().getTime());
		else debut=BookUtil.getBookCreationDate(this.mainFrame);
		Date fin;
		int value=0;
		if (part.hasDoneTime()) {
			value=2;
			fin=part.getDoneTime();
		}
		else {
			fin=ddj;
			if (part.hasObjectiveTime()) {
				if (part.getObjectiveTime().after(ddj)) value=1;
			} else {
				value=2;
			}
		}
		Color[] colors={ColorUtil.getNiceRed(), ColorUtil.getNiceBlue(), ColorUtil.getDarkGreen()};
		DatasetItem item=new DatasetItem(part.getName(),debut,fin,colors[value]);
		List<DatasetItem> subItems=createChapterItems(part);
		item.setSubItem(subItems);
		return(item);
	}
	
	private List<DatasetItem> createChapterItems(Part part) {
		SbApp.trace("Planning.createChapterItems("+part.getName()+")");
		List<DatasetItem> items= new ArrayList<>();
		BookModel model = this.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl chapterDAO = new ChapterDAOImpl(session);
		List<Chapter> chapters = chapterDAO.findAll(part);
		session.close();
		Color[] colors={ColorUtil.getNiceRed(), ColorUtil.getNiceBlue(), ColorUtil.getNiceGreen()};
		Date ddj=java.util.Calendar.getInstance().getTime();
		for (Chapter chapter : chapters) {
			Date debut;
			if (chapter.hasCreationTime())  debut=chapter.getCreationTime();
			else debut=BookUtil.getBookCreationDate(this.mainFrame);
			Date fin;
			int value=0;
			if (chapter.hasDoneTime()) {
				fin=chapter.getDoneTime();
				value=2;
			}
			else {
				fin=ddj;
				if (chapter.hasObjectiveTime()) {
					if (chapter.getObjectiveTime().after(ddj)) value=1;
				} else {
					value=2;
				}
			}
			DatasetItem item=new DatasetItem(chapter.getTitle(), debut, fin, colors[value]);
			items.add(item);
		}
		
		return(items);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see storybook.ui.panel.AbstractPanel#modelPropertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		SbApp.trace("Planning.modelPropertyChange("+evt.toString()+")");
		// Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		String propName = evt.getPropertyName();

		if (BookController.SceneProps.INIT.check(propName)) {
			refresh();
		} else if (BookController.CommonProps.REFRESH.check(propName)
			|| propName.equals("UpdateChapter")
			|| propName.equals("UpdateScene")
			|| propName.equals("UpdatePart")
			|| propName.equals("ShowInfo")) {
			//if (newValue instanceof SbView) {
				//if (ViewName.PLAN.compare(((SbView) newValue))) {
					refreshValues();
				//}
			//}
		}
	}

	private void refreshValues() {
		SbApp.trace("Planning.refreshValues()");
		refreshProgressBarsValues();
		refreshProgressValues();
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		model.reload();
		refreshTimelineValues();
	}

	private void showPopupMenu(JTree tree, MouseEvent evt) {
		SbApp.trace("Planning.showPopupMenu(tree, evt)");
		TreePath selectedPath = tree.getPathForLocation(evt.getX(), evt.getY());
		DefaultMutableTreeNode selectedNode = null;
		try {
			selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
		} catch (Exception e) {
			// ignore
		}
		if (selectedNode == null) {
			return;
		}
		Object eltObj = selectedNode.getUserObject();
		if (!(eltObj instanceof PlanningElement)) {
			return;
		}
		JPopupMenu menu = null;
		Object userObj = ((PlanningElement) eltObj).getElement();
		if (userObj instanceof AbstractStringCategory) {
			AbstractStringCategory cat = (AbstractStringCategory) userObj;
			menu = StringCategoryUtil.createPopupMenu(mainFrame, cat);
		}
		if (userObj instanceof AbstractEntity) {
			AbstractEntity entity = (AbstractEntity) userObj;
			menu = EntityUtil.createPopupMenu(mainFrame, entity);
		}
		if (menu == null) {
			return;
		}
		tree.setSelectionPath(selectedPath);
		JComponent comp = (JComponent) tree.getComponentAt(evt.getPoint());
		Point p = SwingUtilities.convertPoint(comp, evt.getPoint(), this);
		menu.show(this, p.x, p.y);
		evt.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		SbApp.trace("Planning.mouseReleased(e)");
		if (e.isPopupTrigger()) {
			showPopupMenu(tree, e);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}
}
