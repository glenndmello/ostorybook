package shef.ui.text.actions;

/*
 * Created on Dec 10, 2005
 *
 */
import java.awt.event.ActionEvent;
import java.io.StringWriter;
import java.util.Enumeration;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import shef.ui.text.CompoundUndoManager;
import shef.ui.text.ElementWriter;
import shef.ui.text.HTMLUtils;

/**
 * Action which properly inserts breaks for an HTMLDocument
 *
 * @author Bob Tantlinger
 *
 */
public class EnterKeyAction extends DecoratedTextAction {

	private static final long serialVersionUID = 1L;

	public EnterKeyAction(Action defaultEnterAction) {
		super("EnterAction", defaultEnterAction);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JEditorPane editor;
		HTMLDocument document;

		try {
			editor = (JEditorPane) getTextComponent(e);
			document = (HTMLDocument) editor.getDocument();
		} catch (ClassCastException ex) {
			delegate.actionPerformed(e);
			return;
		}

		Element elem = document.getParagraphElement(editor.getCaretPosition());
		Element parentElem = elem.getParentElement();
		HTML.Tag tag = HTML.getTag(elem.getName());
		HTML.Tag parentTag = HTML.getTag(parentElem.getName());
		int caret = editor.getCaretPosition();

		CompoundUndoManager.beginCompoundEdit(document);
		try {
			if (HTMLUtils.isImplied(elem)) {
				if (parentTag.equals(HTML.Tag.LI)) {
					if (parentElem.getEndOffset() - parentElem.getStartOffset() > 1) {
						String txt = "";
						if (caret == parentElem.getStartOffset()) {
							document.insertBeforeStart(parentElem, toListItem(txt));
						} else if (caret < parentElem.getEndOffset() - 1 && caret > parentElem.getStartOffset()) {
							int len = parentElem.getEndOffset() - caret;
							txt = document.getText(caret, len);
							caret--;
							document.insertAfterEnd(parentElem, toListItem(txt));
							document.remove(caret, len);
						} else {
							document.insertAfterEnd(parentElem, toListItem(txt));
						}

						editor.setCaretPosition(caret + 1);
					} else {
						Element listParentElem = HTMLUtils.getListParent(parentElem).getParentElement();
						if (isListItem(HTML.getTag(listParentElem.getName()))) {
							HTML.Tag listParentTag = HTML.getTag(HTMLUtils.getListParent(listParentElem).toString());
							int start = parentElem.getStartOffset();

							Element nextElem = HTMLUtils.getNextElement(document, parentElem);

							int len = nextElem.getEndOffset() - start;

							String ml = HTMLUtils.getElementHTML(listParentElem, true);

							ml = ml.replaceFirst("\\<li\\>\\s*\\<\\/li\\>\\s*\\<\\/ul\\>", "</ul>");
							ml = ml.replaceFirst("\\<ul\\>\\s*\\<\\/ul\\>", "");

							document.setOuterHTML(listParentElem, ml);

						} else if (listParentElem.getName().equals("td")) {
							encloseInDIV(listParentElem, document);
							editor.setCaretPosition(caret + 1);
						} else {
							if (isInList(listParentElem)) {
								HTML.Tag listParentTag = HTML.getTag(HTMLUtils.getListParent(listParentElem).toString());
								HTMLEditorKit.InsertHTMLTextAction a
									= new HTMLEditorKit.InsertHTMLTextAction("insert",
										"<li></li>", listParentTag, HTML.Tag.LI);
								a.actionPerformed(e);
							} else {
								HTML.Tag root = HTML.Tag.BODY;
								if (HTMLUtils.getParent(elem, HTML.Tag.TD) != null) {
									root = HTML.Tag.TD;
								}

								HTMLEditorKit.InsertHTMLTextAction a
									= new HTMLEditorKit.InsertHTMLTextAction("insert",
										"<p></p>", root, HTML.Tag.P);
								a.actionPerformed(e);
							}

							HTMLUtils.removeElement(parentElem);
						}
					}
				} else if (parentTag.isPreformatted()) {
					insertImpliedBR(e);
				} else if (parentTag.equals(HTML.Tag.TD)) {
					encloseInDIV(parentElem, document);
					editor.setCaretPosition(caret + 1);
				} else if (parentTag.equals(HTML.Tag.BODY) || isInList(elem)) {
					insertParagraphAfter(elem, editor);
				} else {
					insertParagraphAfter(parentElem, editor);
				}
			} else if (isListItem(tag)) {
				if ((elem.getEndOffset() - editor.getCaretPosition()) == 1) {
					editor.replaceSelection("\n ");
					editor.setCaretPosition(editor.getCaretPosition() - 1);
				} else {
					delegate.actionPerformed(e);
				}
			} else {
				insertParagraphAfter(elem, editor);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		CompoundUndoManager.endCompoundEdit(document);
	}

	private boolean isListItem(HTML.Tag t) {
		return (t.equals(HTML.Tag.LI) || t.equals(HTML.Tag.DT) || t.equals(HTML.Tag.DD));
	}

	private String toListItem(String txt) {
		return "<li>" + txt + "</li>";
	}

	private boolean isInList(Element el) {
		return HTMLUtils.getListParent(el) != null;
	}

	private void insertImpliedBR(ActionEvent e) {
		HTMLEditorKit.InsertHTMLTextAction hta
			= new HTMLEditorKit.InsertHTMLTextAction("insertBR", "<br>", HTML.Tag.IMPLIED, HTML.Tag.BR);
		hta.actionPerformed(e);
	}

	private void encloseInDIV(Element elem, HTMLDocument document)
		throws Exception {
		HTML.Tag tag = HTML.getTag(elem.getName());
		String html = HTMLUtils.getElementHTML(elem, false);
		html = HTMLUtils.createTag(tag, elem.getAttributes(), "<div>" + html + "</div><div></div>");

		document.setOuterHTML(elem, html);
	}

	private void insertParagraphAfter(Element elem, JEditorPane editor)
		throws BadLocationException, java.io.IOException {
		int cr = editor.getCaretPosition();
		HTMLDocument document = (HTMLDocument) elem.getDocument();
		HTML.Tag t = HTML.getTag(elem.getName());
		int endOffs = elem.getEndOffset();
		int startOffs = elem.getStartOffset();

		if (t == null || elem.getName().equals("p-implied")) {
			t = HTML.Tag.DIV;
		}

		String html;
		if (cr == startOffs) {
			html = createBlock(t, elem, "");
		} else {
			StringWriter out = new StringWriter();
			ElementWriter w = new ElementWriter(out, elem, startOffs, cr);
			w.write();
			html = createBlock(t, elem, out.toString());
		}

		if (cr == endOffs - 1) {
			html += createBlock(t, elem, "");
		} else {
			StringWriter out = new StringWriter();
			ElementWriter w = new ElementWriter(out, elem, cr, endOffs);
			w.write();
			html += createBlock(t, elem, out.toString());
		}

		AttributeSet chAttribs;
		if (endOffs > startOffs && cr == endOffs - 1) {
			chAttribs = new SimpleAttributeSet(document.getCharacterElement(cr - 1).getAttributes());
		} else {
			chAttribs = new SimpleAttributeSet(document.getCharacterElement(cr).getAttributes());
		}

		document.setOuterHTML(elem, html);

		cr++;
		Element p = document.getParagraphElement(cr);
		if (cr == endOffs) {
			setCharAttribs(p, chAttribs);
		}

		editor.setCaretPosition(p.getStartOffset());
	}

	private String createBlock(HTML.Tag t, Element elem, String html) {
		AttributeSet attribs = elem.getAttributes();
		return HTMLUtils.createTag(t, attribs, HTMLUtils.removeEnclosingTags(elem, html));
	}

	private void setCharAttribs(Element p, AttributeSet chAttribs) {
		HTMLDocument document = (HTMLDocument) p.getDocument();
		int start = p.getStartOffset();
		int end = p.getEndOffset();

		SimpleAttributeSet sas = new SimpleAttributeSet(chAttribs);
		sas.removeAttribute(HTML.Attribute.SRC);
		boolean skipAttribs = false;
		for (Enumeration ee = sas.getAttributeNames(); ee.hasMoreElements();) {
			Object n = ee.nextElement();
			String val = chAttribs.getAttribute(n).toString();
			skipAttribs = val.equals("br") || val.equals("hr") || val.equals("img");
		}

		if (!skipAttribs) {
			document.setCharacterAttributes(start, end - start, sas, true);
		}
	}

}
