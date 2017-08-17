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
public class CopyAction extends BasicEditAction {

	private static final long serialVersionUID = 1L;

	public CopyAction() {
		super("");
		putValue(Action.NAME, I18N.getMsg("shef.copy"));
		putValue(Action.SMALL_ICON, ShefUtils.getIconX16("copy"));
		putValue(ActionManager.LARGE_ICON, ShefUtils.getIconX24("copy"));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		putValue(MNEMONIC_KEY, (int) I18N.getMnemonic("shef.copy"));
		addShouldBeEnabledDelegate((Action a) -> {
			JEditorPane ed = getCurrentEditor();
			return ed != null && ed.getSelectionStart() != ed.getSelectionEnd();
		});
		putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
	}

	@Override
	protected void doEdit(ActionEvent e, JEditorPane editor) {
		editor.copy();
	}

	@Override
	protected void contextChanged() {
		super.contextChanged();
		this.updateEnabledState();
	}

}
