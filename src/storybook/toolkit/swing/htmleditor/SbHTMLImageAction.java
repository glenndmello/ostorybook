package storybook.toolkit.swing.htmleditor;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML;

import shef.ui.ShefUtils;
import shef.ui.text.HTMLUtils;
import shef.ui.text.actions.HTMLTextEditAction;
import shef.ui.text.dialogs.ImageDialog;

import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class SbHTMLImageAction extends HTMLTextEditAction {

	private final MainFrame mainFrame;

	public SbHTMLImageAction(MainFrame m) {
		super(I18N.getMsg("shef.image_"));
		mainFrame=m;
		putValue(SMALL_ICON, ShefUtils.getIconX16("image"));
		putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
	}

	@Override
	protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
		ImageDialog d = createDialog(editor);
		d.setLocationRelativeTo(d.getParent());
		d.setVisible(true);
		if (d.hasUserCancelled()) return;
		editor.requestFocusInWindow();
		editor.replaceSelection(d.getHTML());
	}

	@Override
	protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
		SbImageDialog dlg = new SbImageDialog(mainFrame,editor);
		SwingUtil.showModalDialog(dlg, editor.getParent());
		if (dlg.isCanceled()) {
			return;
		}
		String tagText = dlg.getHTML();
		if (editor.getCaretPosition() == editor.getDocument().getLength()) tagText += "&nbsp;";

		editor.replaceSelection("");
		HTML.Tag tag = HTML.Tag.IMG;
		if (tagText.startsWith("<a")) tag = HTML.Tag.A;

		HTMLUtils.insertHTML(tagText, tag, editor);
	}

	protected ImageDialog createDialog(JTextComponent ed) {
		Window w = SwingUtilities.getWindowAncestor(ed);
		ImageDialog d = null;
		if (w != null && w instanceof Frame)
			d = new ImageDialog((Frame) w);
		else if (w != null && w instanceof Dialog)
			d = new ImageDialog((Dialog) w);

		return d;
	}
}
