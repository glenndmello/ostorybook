/*
 * Created on Nov 2, 2007
 */
package shef.ui.text.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;

import shef.ui.ShefUtils;

import org.bushe.swing.action.ActionManager;
import storybook.i18n.I18N;

/**
 * @author Bob Tantlinger
 *
 */
public class CutAction extends BasicEditAction {

	private static final long serialVersionUID = 1L;

	public CutAction() {
		super("");
		putValue(Action.NAME, I18N.getMsg("shef.cut"));
		putValue(Action.SMALL_ICON, ShefUtils.getIconX16("cut"));
		putValue(ActionManager.LARGE_ICON, ShefUtils.getIconX24("cut"));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		putValue(MNEMONIC_KEY, (int) I18N.getMnemonic("shef.cut"));
		addShouldBeEnabledDelegate((Action a) -> {
			JEditorPane ed = getCurrentEditor();
			return ed != null && ed.getSelectionStart() != ed.getSelectionEnd();
		});
		putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
	}

	@Override
	protected void doEdit(ActionEvent e, JEditorPane editor) {
		editor.cut();
	}

	@Override
	protected void contextChanged() {
		super.contextChanged();
		this.updateEnabledState();
	}
}
