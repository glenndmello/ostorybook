package storybook.ui.panel.tree;

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.hibernate.Session;

import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.ui.MainFrame;

public class DefaultTreeTransferHandler extends AbstractTreeTransferHandler {

	public DefaultTreeTransferHandler(TreePanel treePanel, int action) {
		super(treePanel, action, true);
	}

	public boolean canPerformAction(Tree target, TreeNode draggedNode, int action, Point location) {
		TreePath pathTarget = target.getPathForLocation(location.x, location.y);
		if (pathTarget == null) {
			target.setSelectionPath(null);
			return (false);
		}
		target.setSelectionPath(pathTarget);
		if (action == DnDConstants.ACTION_MOVE) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathTarget.getLastPathComponent();
			Object draggedObject = ((DefaultMutableTreeNode) draggedNode).getUserObject();
			if (node != null) {
				Object targetObject = node.getUserObject();
				if (targetObject != null) {
					if (draggedObject instanceof Scene) {
						return (targetObject instanceof Chapter) || (targetObject instanceof Scene);
					} else if (draggedObject instanceof Chapter) {
						return (targetObject instanceof Part) || (targetObject instanceof Chapter);
					}
				}
			}
		}
		return (false);
	}

	public boolean executeDrop(Tree targetTree, TreeNode draggedNode, TreeNode targetNode, int action) {
		if (action == DnDConstants.ACTION_MOVE) {
			Object draggedObject = ((DefaultMutableTreeNode) draggedNode).getUserObject();
			Object targetObject = ((DefaultMutableTreeNode) targetNode).getUserObject();

			if ((draggedObject instanceof Scene) && (targetObject instanceof Chapter)) {
				dropSceneInChapter((Scene) draggedObject, (Chapter) targetObject);
			} else if ((draggedObject instanceof Scene) && (targetObject instanceof Scene)) {
				dropSceneBeforeScene((Scene) draggedObject, (Scene) targetObject);
			} else if ((draggedObject instanceof Chapter) && (targetObject instanceof Part)) {
				dropChapterInPart((Chapter) draggedObject, (Part) targetObject);
			} else if ((draggedObject instanceof Chapter) && (targetObject instanceof Chapter)) {
				dropChapterBeforeChapter((Chapter) draggedObject, (Chapter) targetObject);
			}
			return (true);
		}
		return (false);
	}

	private void dropChapterBeforeChapter(Chapter dragged, Chapter target) {
		MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(getTree());
		BookModel model = frame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Chapter> toUpdate = new ArrayList<>();
		List<Chapter> chapters = dao.findChapters(target.getPart());
		int refChapterNumber = target.getChapterno();
		for (Chapter chapter : chapters)
		{
			int chapterNum = chapter.getChapterno();
			if (!chapter.equals(dragged))
			{
				if (chapterNum >= refChapterNumber) {
					chapter.setChapterno(chapterNum + 1);
					toUpdate.add(chapter);
				}
			}
		}
		dragged.setPart(target.getPart());
		dragged.setChapterno(refChapterNumber);
		model.commit();

		BookController ctrl = frame.getBookController();
		ctrl.updateChapter(dragged);
		for (Chapter chapter : toUpdate)
		{
			ctrl.updateChapter(chapter);
		}
		ctrl.updatePart(target.getPart());

		Session session1 = model.beginTransaction();
		PartDAOImpl dao1 = new PartDAOImpl(session1);
		int newNum = 1;
		List<Chapter> chapters1 = dao1.findChapters(target.getPart());
		for (Chapter chapter : chapters1)
		{
			chapter.setChapterno(newNum++);
		}
		model.commit();
		
		for (Chapter chapter : chapters1)
		{
			ctrl.updateChapter(chapter);
		}
		ctrl.updatePart(target.getPart());
		getTreePanel().refreshTree();
	}

	private void dropChapterInPart(Chapter dragged, Part target) {
		MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(getTree());
		BookModel model = frame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Chapter> chapters = dao.findChapters(target);
		int lastChapterNumber = 0;
		for (Chapter chapter : chapters)
		{
			int chapterNum = chapter.getChapterno();
			if (chapterNum > lastChapterNumber) {
				lastChapterNumber = chapterNum;
			}
		}
		dragged.setPart(target);
		dragged.setChapterno(lastChapterNumber + 1);
		model.commit();

		BookController ctrl = frame.getBookController();
		ctrl.updateChapter(dragged);
		ctrl.updatePart(target);
		getTreePanel().refreshTree();
	}

	private void dropSceneBeforeScene(Scene dragged, Scene target) {
		MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(getTree());
		BookModel model = frame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Scene> toUpdate = new ArrayList<>();
		List<Scene> scenes = dao.findScenes(target.getChapter());
		int refSceneNumber = target.getSceneno();
		for (Scene scene : scenes)
		{
			int sceneNum = scene.getSceneno();
			if (!scene.equals(dragged))
			{
				if (sceneNum >= refSceneNumber) {
					scene.setSceneno(sceneNum + 1);
					toUpdate.add(scene);
				}
			}
		}
		dragged.setChapter(target.getChapter());
		dragged.setSceneno(refSceneNumber);
		model.commit();

		BookController ctrl = frame.getBookController();
		ctrl.updateScene(dragged);
		for (Scene scene : toUpdate)
		{
			ctrl.updateScene(scene);
		}
		ctrl.updateChapter(target.getChapter());

		Session session1 = model.beginTransaction();
		ChapterDAOImpl dao1 = new ChapterDAOImpl(session1);
		int newNum = 1;
		List<Scene> scenes1 = dao1.findScenes(target.getChapter());
		for (Scene scene : scenes1)
		{
			scene.setSceneno(newNum++);
		}
		model.commit();
		
		for (Scene scene : scenes1)
		{
			ctrl.updateScene(scene);
		}
		ctrl.updateChapter(target.getChapter());
		getTreePanel().refreshTree();
	}

	private void dropSceneInChapter(Scene dragged, Chapter target) {
		MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(getTree());
		BookModel model = frame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Scene> scenes = dao.findScenes(target);
		int lastSceneNumber = 0;
		for (Scene scene : scenes) {
			int sceneNum = scene.getSceneno();
			if (sceneNum > lastSceneNumber) {
				lastSceneNumber = sceneNum;
			}
		}
		if (target.getChapterno() == -1){
		   dragged.setChapter(null);
		} else {
		  dragged.setChapter(target);
	    }
		dragged.setSceneno(lastSceneNumber + 1);
		model.commit();

		BookController ctrl = frame.getBookController();
		ctrl.updateScene(dragged);
		if (target.getChapterno() != -1){
			ctrl.updateChapter(target);
	    }
		getTreePanel().refreshTree();
	}
}