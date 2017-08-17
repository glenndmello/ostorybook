package storybook.ui.panel.planning;

import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.html.HtmlUtil;

public class PlanningElement {
	Object element;
	int size;
	int maxSize;
	
	public Object getElement() {
		return element;
	}
	/**
	 * @param element
	 */
	public void setElement(Object element) {
		this.element = element;
		if (element instanceof Part) {
			Part part = (Part)element;
			maxSize = part.getObjectiveChars();
		} else if (element instanceof Chapter) {
			Chapter chapter = (Chapter)element;
			maxSize = chapter.getObjectiveChars();
		}
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(int size) {
		this.maxSize = size;
	}
	
	@Override
	public String toString() {
		String t="";
		String notes="";
		if (element instanceof Part) {
			Part part=(Part)element;
			if (part.getNotes()!=null && !HtmlUtil.htmlToText(part.getNotes()).equals("")) notes="*";
			t=part.getName() + notes + "    (" + size + "/" + maxSize +")";
			maxSize = part.getObjectiveChars();
		} else if (element instanceof Chapter) {
			Chapter chapter=(Chapter)element;
			if (chapter.getNotes()!=null && !HtmlUtil.htmlToText(chapter.getNotes()).equals("")) notes="*";
			maxSize = chapter.getObjectiveChars();
			t=chapter.getTitle() + notes + "    (" + size + "/" + maxSize +")";
		} else if (element instanceof Scene) {
			Scene scene=((Scene)element);
			if (scene.getNotes()!=null && !HtmlUtil.htmlToText(scene.getNotes()).equals("")) notes="*";
			t= scene.getTitle() + notes + "    (" + size + ")";
		} else if (element instanceof String) {
			t= (String)element + "    (" + size + "/" + maxSize +")";
		}
		return(t);
	}
}
