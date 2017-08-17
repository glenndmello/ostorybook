/*
 * Created on Oct 30, 2007
 */
package shef.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.bushe.swing.action.ActionUIFactory;

/**
 * A collection of static UI helper methods.
 *
 * @author Bob Tantlinger
 *
 */
public class ShefUtils {
	public static final String X16 = "shef/resources/images/x16/";
	public static final String X24 = "shef/resources/images/x24/";
	public static final String X32 = "shef/resources/images/x32/";

	public static ImageIcon getIconX16(String iconName) {
		return getIcon(X16,iconName);
	}

	public static ImageIcon getIconX24(String iconName) {
		return getIcon(X24,iconName);
	}

	public static ImageIcon getIconX32(String iconName) {
		return getIcon(X32,iconName);
	}

	public static ImageIcon getIcon(String _package, String iconName) {
		if (!_package.endsWith("/")) {
			_package += "/";
		}
		return getIcon(_package + iconName+".png");
	}

	public static ImageIcon getIcon(String path) {
		return createImageIcon(path);
	}

	public static ImageIcon createImageIcon(String path) {

		URL u = Thread.currentThread().getContextClassLoader().getResource(path);
		if (u == null) {
			return null;
		}
		return new ImageIcon(u);
	}

	public static void showError(String msg) {
		showError(null, msg);
	}

	public static void showError(Component c, Throwable ex) {
		Window w = SwingUtilities.getWindowAncestor(c);
		if (w instanceof Frame) {
			showError((Frame) w, ex);
		} else if (w instanceof Dialog) {
			showError((Dialog) w, ex);
		} else {
			showError(c, ex.getLocalizedMessage());
		}
	}

	public static void showError(Component owner, String msg) {
		showError(owner, "Error", msg);
	}

	public static void showError(Component owner, String title, String msg) {
		JOptionPane.showMessageDialog(owner, msg, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void showError(Frame owner, String title, Throwable th) {
		JDialog d = new ExceptionDialog(owner, th);
		if (title != null) {
			d.setTitle(title);
		}
		d.setLocationRelativeTo(owner);
		d.setVisible(true);
		th.printStackTrace();
	}

	public static void showError(Frame owner, Throwable th) {
		showError(owner, null, th);
	}

	public static void showError(Dialog owner, String title, Throwable th) {
		JDialog d = new ExceptionDialog(owner, th);
		if (title != null) {
			d.setTitle(title);
		}
		d.setLocationRelativeTo(owner);
		d.setVisible(true);
		th.printStackTrace();
	}

	public static void showError(Dialog owner, Throwable th) {
		showError(owner, null, th);
	}

	public static void showWarning(Component owner, String title, String msg) {
		JOptionPane.showMessageDialog(
				owner, msg, title, JOptionPane.WARNING_MESSAGE);
	}

	public static void showWarning(Component owner, String msg) {
		showWarning(owner, "Warning", msg);
	}

	public static void showWarning(String msg) {
		showWarning(null, msg);
	}

	public static void showInfo(Component owner, String title, String msg) {
		JOptionPane.showMessageDialog(
				owner, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showInfo(Component owner, String msg) {
		showInfo(owner, "Information", msg);
	}

	public static void showInfo(String msg) {
		showInfo(null, msg);
	}

	public static AbstractButton addToolBarButton(JToolBar tb, Action a) {
		return addToolBarButton(tb, ActionUIFactory.getInstance().createButton(a));
	}

	public static AbstractButton addToolBarButton(JToolBar tb, Action a, boolean focusable, boolean showIconOnly) {
		return addToolBarButton(tb, ActionUIFactory.getInstance().createButton(a), false, true);
	}

	public static AbstractButton addToolBarButton(JToolBar tb, AbstractButton button) {
		return addToolBarButton(tb, button, false, true);
	}

	public static AbstractButton addToolBarButton(JToolBar tb, AbstractButton button, boolean focusable, boolean showIconOnly) {
		if (button.getAction() != null) {
			button.setToolTipText((String) button.getAction().getValue(Action.NAME));
			//prefer large icons for toolbar buttons
			if (button.getAction().getValue("LARGE_ICON") != null) {
				try {
					button.setIcon((Icon) button.getAction().getValue("LARGE_ICON"));
				} catch (ClassCastException cce) {
				}
			}
		}

		Icon ico = button.getIcon();
		if (ico != null && showIconOnly) {
			button.setText(null);
			button.setMnemonic(0);
			button.putClientProperty("hideActionText", Boolean.TRUE);
			int square = Math.max(ico.getIconWidth(), ico.getIconHeight()) + 6;
			Dimension size = new Dimension(square, square);
			button.setPreferredSize(size);
    		//button.setMinimumSize(size);
			//button.setMaximumSize(size);
		}

		if (!focusable) {
			button.setFocusable(false);
			button.setFocusPainted(false);
		}

		button.setMargin(new Insets(1, 1, 1, 1));
		tb.add(button);
		return button;
	}

	public static JMenuItem addMenuItem(JMenu menu, Action action) {
		JMenuItem item = menu.add(action);
		configureMenuItem(item, action);
		return item;
	}

	public static JMenuItem addMenuItem(JPopupMenu menu, Action action) {
		JMenuItem item = menu.add(action);
		configureMenuItem(item, action);
		return item;
	}

	private static void configureMenuItem(JMenuItem item, Action action) {
		KeyStroke keystroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
		if (keystroke != null) {
			item.setAccelerator(keystroke);
		}

		item.setIcon(null);
		item.setToolTipText(null);
	}
}
