/*
 * Created on Feb 25, 2005
 *
 */
package shef.ui.text.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import org.bushe.swing.action.ActionManager;
import shef.ui.ShefUtils;
import shef.ui.text.CompoundUndoManager;
import shef.ui.text.HTMLUtils;
import storybook.i18n.I18N;

/**
 * Action which aligns HTML elements
 *
 * @author Bob Tantlinger
 *
 */
public class HTMLAlignAction extends HTMLTextEditAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	public static final int JUSTIFY = 3;

	public static final String ALIGNMENT_NAMES[]
			= {
				I18N.getMsg("shef.al_left"),
				I18N.getMsg("shef.al_center"),
				I18N.getMsg("shef.al_right"),
				I18N.getMsg("shef.al_justify")
			};

	private static final int[] MNEMS
			= {
				I18N.getMnemonic("shef.al_left"),
				I18N.getMnemonic("shef.al_center"),
				I18N.getMnemonic("shef.al_right"),
				I18N.getMnemonic("shef.al_justify")
			};

	public static final String ALIGNMENTS[]
			= {
				"left", "center", "right", "justify"
			};

	private static final String IMGS[]
			= {
				"al_left", "al_center", "al_right", "al_justify"
			};

	private int align;

	/**
	 * Creates a new HTMLAlignAction
	 *
	 * @param al LEFT, RIGHT, CENTER, or JUSTIFY
	 * @throws IllegalArgumentException
	 */
	public HTMLAlignAction(int al) throws IllegalArgumentException {
		super("");
		if (al < 0 || al >= ALIGNMENTS.length) {
			throw new IllegalArgumentException("Illegal Argument");
		}

		//String pkg = getClass().getPackage().getName();
		putValue(NAME, (ALIGNMENT_NAMES[al]));
		putValue(MNEMONIC_KEY, MNEMS[al]);

		putValue(SMALL_ICON, ShefUtils.getIconX16(IMGS[al]));
		putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_RADIO);

		align = al;
	}

	@Override
	protected void updateWysiwygContextState(JEditorPane ed) {
		setSelected(shouldBeSelected(ed));
	}

	private boolean shouldBeSelected(JEditorPane ed) {
		HTMLDocument document = (HTMLDocument) ed.getDocument();
		Element elem = document.getParagraphElement(ed.getCaretPosition());
		if (HTMLUtils.isImplied(elem)) {
			elem = elem.getParentElement();
		}

		AttributeSet at = elem.getAttributes();
		return at.containsAttribute(HTML.Attribute.ALIGN, ALIGNMENTS[align]);
	}

	@Override
	protected void updateSourceContextState(JEditorPane ed) {
		setSelected(false);
	}

	@Override
	protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
		HTMLDocument doc = (HTMLDocument) editor.getDocument();
		Element curE = doc.getParagraphElement(editor.getSelectionStart());
		Element endE = doc.getParagraphElement(editor.getSelectionEnd());
        //System.err.println("ALIGN " + curE.getName());

		CompoundUndoManager.beginCompoundEdit(doc);
		while (true) {
			alignElement(curE);
			if (curE.getEndOffset() >= endE.getEndOffset()
					|| curE.getEndOffset() >= doc.getLength()) {
				break;
			}
			curE = doc.getParagraphElement(curE.getEndOffset() + 1);
		}
		CompoundUndoManager.endCompoundEdit(doc);
	}

	private void alignElement(Element elem) {
		HTMLDocument doc = (HTMLDocument) elem.getDocument();

		if (HTMLUtils.isImplied(elem)) {
			HTML.Tag tag = HTML.getTag(elem.getParentElement().getName());
            //System.out.println(tag);
			//pre tag doesn't support an align attribute
			//http://www.w3.org/TR/REC-html32#pre
			if (tag != null && (!tag.equals(HTML.Tag.BODY))
					&& (!tag.isPreformatted() && !tag.equals(HTML.Tag.DD))) {
				SimpleAttributeSet as = new SimpleAttributeSet(elem.getAttributes());
				as.removeAttribute("align");
				as.addAttribute("align", ALIGNMENTS[align]);

				Element parent = elem.getParentElement();
				String html = HTMLUtils.getElementHTML(elem, false);
				html = HTMLUtils.createTag(tag, as, html);
				String snipet = "";
				for (int i = 0; i < parent.getElementCount(); i++) {
					Element el = parent.getElement(i);
					if (el == elem) {
						snipet += html;
					} else {
						snipet += HTMLUtils.getElementHTML(el, true);
					}
				}

				try {
					doc.setOuterHTML(parent, snipet);
				} catch (BadLocationException | IOException ex) {
					ex.printStackTrace();
				}
			}
		} else {
			//Set the HTML attribute on the paragraph...
			MutableAttributeSet set = new SimpleAttributeSet(elem.getAttributes());
			set.removeAttribute(HTML.Attribute.ALIGN);
			set.addAttribute(HTML.Attribute.ALIGN, ALIGNMENTS[align]);
			//Set the paragraph attributes...
			int start = elem.getStartOffset();
			int length = elem.getEndOffset() - elem.getStartOffset();
			doc.setParagraphAttributes(start, length - 1, set, true);
		}
	}

	@Override
	protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
		String prefix = "<p align=\"" + ALIGNMENTS[align] + "\">";
		String postfix = "</p>";
		String sel = editor.getSelectedText();
		if (sel == null) {
			editor.replaceSelection(prefix + postfix);

			int pos = editor.getCaretPosition() - postfix.length();
			if (pos >= 0) {
				editor.setCaretPosition(pos);
			}
		} else {
			sel = prefix + sel + postfix;
			editor.replaceSelection(sel);
		}
	}
}
