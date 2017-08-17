/**
 * 
 */
package storybook.ui.panel.tree;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * @author jean
 *
 */
@SuppressWarnings("serial")
public class Tree extends JTree {

	/**
	 * 
	 */
	public Tree() {
	}

	/**
	 * @param arg0
	 */
	public Tree(Object[] arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public Tree(Vector<?> arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public Tree(Hashtable<?, ?> arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public Tree(TreeNode arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public Tree(TreeModel arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public Tree(TreeNode arg0, boolean arg1) {
		super(arg0, arg1);
	}

	Insets autoscrollInsets = new Insets(20, 20, 20, 20); // insets

	public void autoscroll(Point cursorLocation) {
		Insets insets = getAutoscrollInsets();
		Rectangle outer = getVisibleRect();
		Rectangle inner = new Rectangle(outer.x + insets.left, outer.y + insets.top,
				outer.width - (insets.left + insets.right), outer.height - (insets.top + insets.bottom));
		if (!inner.contains(cursorLocation)) {
			Rectangle scrollRect = new Rectangle(cursorLocation.x - insets.left, cursorLocation.y - insets.top,
					insets.left + insets.right, insets.top + insets.bottom);
			scrollRectToVisible(scrollRect);
		}
	}

	public Insets getAutoscrollInsets() {
		return (autoscrollInsets);
	}
}
