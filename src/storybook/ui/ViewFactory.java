
/*
 Storybook: Open Source software for novelists and authors.
 Copyright (C) 2008 - 2012 Martin Mustun, 2015 FaVdB

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.infonode.docking.View;
import net.infonode.docking.util.StringViewMap;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import storybook.SbApp;
import storybook.SbConstants.ViewName;
import storybook.toolkit.BookUtil;
import storybook.i18n.I18N;
import storybook.ui.chart.OccurrenceOfItems;
import storybook.ui.chart.OccurrenceOfLocations;
import storybook.ui.chart.OccurrenceOfPersons;
import storybook.ui.chart.PersonsByDate;
import storybook.ui.chart.PersonsByScene;
import storybook.ui.chart.StrandsByDate;
import storybook.ui.chart.WiWW;
import storybook.ui.panel.memo.MemoPanel;
import storybook.ui.panel.memoria.MemoriaPanel;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.panel.BlankPanel;
import storybook.ui.panel.attributes.AttributesViewPanel;
import storybook.ui.panel.book.BookPanel;
import storybook.ui.panel.chrono.ChronoPanel;
import storybook.ui.panel.info.InfoPanel;
import storybook.ui.panel.manage.ManagePanel;
import storybook.ui.panel.navigation.NavigationPanel;
import storybook.ui.panel.reading.ReadingPanel;
import storybook.ui.panel.tree.TreePanel;
import storybook.ui.panel.planning.Planning;
import storybook.ui.panel.storyboard.StoryboardPanel;
import storybook.ui.table.AbstractTable;
import storybook.ui.table.AttributeTable;
import storybook.ui.table.CategoryTable;
import storybook.ui.table.ChapterTable;
import storybook.ui.table.GenderTable;
import storybook.ui.table.IdeaTable;
import storybook.ui.table.InternalTable;
import storybook.ui.table.ItemLinkTable;
import storybook.ui.table.ItemTable;
import storybook.ui.table.LocationTable;
import storybook.ui.table.PartTable;
import storybook.ui.table.PersonTable;
import storybook.ui.table.RelationshipTable;
import storybook.ui.table.SceneTable;
import storybook.ui.table.StrandTable;
import storybook.ui.table.TagLinkTable;
import storybook.ui.table.TagTable;
import storybook.ui.table.TimeEventTable;

/**
 * @author martin
 *
 */
public class ViewFactory {

	private int NONE=0,EXPORT=1, OPTIONS=10;
	private final MainFrame mainFrame;
	private final StringViewMap viewMap;
	private boolean initialisation;

	public ViewFactory(MainFrame mainFrame) {
		SbApp.trace("ViewFactory(mainFrame)");
		this.mainFrame = mainFrame;
		viewMap = new StringViewMap();
	}

	public void setInitialisation() {
		initialisation = true;
	}

	public void resetInitialisation() {
		initialisation = false;
	}
	
	@SuppressWarnings("null")
	public void setViewTitle(View view) {
		SbApp.trace("ViewFactory.setViewTitle(" + view.getName() + ")");
		if (view == null) {
			return;
		}
		String title="";
		if (ViewName.CHRONO.compare(view)) {
			title="view.chrono";
		} else if (ViewName.BOOK.compare(view)) {
			title="view.book";
		} else if (ViewName.MANAGE.compare(view)) {
			title="view.manage";
		} else if (ViewName.READING.compare(view)) {
			title="view.reading";
		} else if (ViewName.MEMORIA.compare(view)) {
			title="view.pov";
		} else if (ViewName.STORYBOARD.compare(view)) {
			title="view.storyboard";
		} else if (ViewName.SCENES.compare(view)) {
			title="scenes";
		} else if (ViewName.CHAPTERS.compare(view)) {
			title="chapters";
		} else if (ViewName.PARTS.compare(view)) {
			title="parts";
		} else if (ViewName.LOCATIONS.compare(view)) {
			title="locations";
		} else if (ViewName.PERSONS.compare(view)) {
			title="persons";
		} else if (ViewName.RELATIONSHIPS.compare(view)) {
			title="relationship";
		} else if (ViewName.GENDERS.compare(view)) {
			title="genders";
		} else if (ViewName.CATEGORIES.compare(view)) {
			title="persons.categories";
		} else if (ViewName.STRANDS.compare(view)) {
			title="strands";
		} else if (ViewName.IDEAS.compare(view)) {
			title="ideas.title";
		} else if (ViewName.TAGS.compare(view)) {
			title="tags";
		} else if (ViewName.ITEMS.compare(view)) {
			title="items";
		} else if (ViewName.TAGLINKS.compare(view)) {
			title="tags.links";
		} else if (ViewName.ITEMLINKS.compare(view)) {
			title="items.links";
		} else if (ViewName.CHART_PERSONS_BY_DATE.compare(view)) {
			title="tools.charts.overall.strand.date";
		} else if (ViewName.CHART_PERSONS_BY_SCENE.compare(view)) {
			title="tools.charts.part.character.scene";
		} else if (ViewName.CHART_WiWW.compare(view)) {
			title="tools.charts.overall.whoIsWhereWhen";
		} else if (ViewName.CHART_STRANDS_BY_DATE.compare(view)) {
			title="tools.charts.overall.strand.date";
		} else if (ViewName.CHART_OCCURRENCE_OF_PERSONS.compare(view)) {
			title="tools.charts.overall.character.occurrence";
		} else if (ViewName.CHART_OCCURRENCE_OF_LOCATIONS.compare(view)) {
			title="tools.charts.overall.location.occurrence";
		} else if (ViewName.CHART_OCCURRENCE_OF_ITEMS.compare(view)) {
			title="tools.charts.overall.item.occurrence";
		} else if (ViewName.ATTRIBUTES.compare(view)) {
			title="attribute_s";
		} else if (ViewName.ATTRIBUTESLIST.compare(view)) {
			title="attribute.list";
		} else if (ViewName.TREE.compare(view)) {
			title="tree";
		} else if (ViewName.INFO.compare(view)) {
			title="info.title";
		} else if (ViewName.MEMOS.compare(view)) {
			title="memo.title";
		} else if (ViewName.NAVIGATION.compare(view)) {
			title="navigation";
		} else if (ViewName.PLAN.compare(view)) {
			title="view.plan";
		} else if (ViewName.TIMEEVENT.compare(view)) {
			title="view.timeline";
		}
		title=I18N.getMsg(title);
		view.getViewProperties().setTitle(title);
	}

	public SbView getView(ViewName viewName) {
		SbApp.trace("ViewFactory.getView(" + viewName.name() + ")");
		SbView view = (SbView) viewMap.getView(viewName.toString());
		if (view != null) {
			return view;
		}
		switch (viewName) {
			case SCENES:
				return getScenesView();
			case CHAPTERS:
				return getChaptersView();
			case PARTS:
				return getPartsView();
			case LOCATIONS:
				return getLocationsView();
			case PERSONS:
				return getPersonsView();
			case RELATIONSHIPS:
				return getRelationshipsView();
			case GENDERS:
				return getGendersView();
			case CATEGORIES:
				return getCategoriesView();
			case STRANDS:
				return getStrandsView();
			case IDEAS:
				return getIdeasView();
			case TAGS:
				return getTagsView();
			case ITEMS:
				return getItemView();
			case TAGLINKS:
				return getTagLinksView();
			case ITEMLINKS:
				return getItemLinksView();
			case INTERNALS:
				return getInternalsView();
			case CHRONO:
				return getChronoView();
			case BOOK:
				return getBookView();
			case MANAGE:
				return getManageView();
			case READING:
				return getReadingView();
			case MEMORIA:
				return getMemoriaView();
			case STORYBOARD:
				return getStoryboardView();
			case TREE:
				return getTreeView();
			case INFO:
				return getQuickInfoView();
			case MEMOS:
				return getMemoView();
			case NAVIGATION:
				return getNavigationView();
			case CHART_PERSONS_BY_DATE:
				return getChartPersonsByDate();
			case CHART_PERSONS_BY_SCENE:
				return getChartPersonsByScene();
			case CHART_WiWW:
				return getChartWiWW();
			case CHART_STRANDS_BY_DATE:
				return getChartStrandsByDate();
			case CHART_OCCURRENCE_OF_PERSONS:
				return getChartOccurrenceOfPersons();
			case CHART_OCCURRENCE_OF_LOCATIONS:
				return getChartOccurrenceOfLocations();
			case CHART_OCCURRENCE_OF_ITEMS:
				return getChartOccurrenceOfItems();
			case CHART_GANTT:
				return getChartGantt();
			case ATTRIBUTES:
				return getAttributesView();
			case ATTRIBUTESLIST:
				return getAttributesListView();
			case PLAN:
				return getPlanView();
			case TIMEEVENT:
				return getTimeEventView();
			default:
				break;
		}
		return null;
	}

	public SbView getView(String viewName) {
		SbApp.trace("ViewFactory.getView(string=" + viewName + ")");
		return (SbView) viewMap.getView(viewName);
	}

	public void loadView(SbView view) {
		if (view == null) {
			return;
		}
		SbApp.trace("ViewFactory.loadView(view=" + view.getName() + ")");
		AbstractPanel comp = new BlankPanel(mainFrame);
		boolean isTable = false;
		if (ViewName.CHRONO.compare(view)) {
			comp = new ChronoPanel(mainFrame);
		} else if (ViewName.BOOK.compare(view)) {
			comp = new BookPanel(mainFrame);
		} else if (ViewName.MANAGE.compare(view)) {
			comp = new ManagePanel(mainFrame);
		} else if (ViewName.READING.compare(view)) {
			comp = new ReadingPanel(mainFrame);
		} else if (ViewName.MEMORIA.compare(view)) {
			comp = new MemoriaPanel(mainFrame);
		} else if (ViewName.STORYBOARD.compare(view)) {
			comp = new StoryboardPanel(mainFrame);
		} else if (ViewName.SCENES.compare(view)) {
			comp = new SceneTable(mainFrame);
			isTable = true;
		} else if (ViewName.CHAPTERS.compare(view)) {
			comp = new ChapterTable(mainFrame);
			isTable = true;
		} else if (ViewName.PARTS.compare(view)) {
			comp = new PartTable(mainFrame);
			isTable = true;
		} else if (ViewName.LOCATIONS.compare(view)) {
			comp = new LocationTable(mainFrame);
			isTable = true;
		} else if (ViewName.PERSONS.compare(view)) {
			comp = new PersonTable(mainFrame);
			isTable = true;
		} else if (ViewName.RELATIONSHIPS.compare(view)) {
			comp = new RelationshipTable(mainFrame);
			isTable = true;
		} else if (ViewName.GENDERS.compare(view)) {
			comp = new GenderTable(mainFrame);
			isTable = true;
		} else if (ViewName.CATEGORIES.compare(view)) {
			comp = new CategoryTable(mainFrame);
			isTable = true;
		} else if (ViewName.STRANDS.compare(view)) {
			comp = new StrandTable(mainFrame);
			isTable = true;
		} else if (ViewName.IDEAS.compare(view)) {
			comp = new IdeaTable(mainFrame);
			isTable = true;
		} else if (ViewName.TAGS.compare(view)) {
			comp = new TagTable(mainFrame);
			isTable = true;
		} else if (ViewName.ITEMS.compare(view)) {
			comp = new ItemTable(mainFrame);
			isTable = true;
		} else if (ViewName.TAGLINKS.compare(view)) {
			comp = new TagLinkTable(mainFrame);
			isTable = true;
		} else if (ViewName.ITEMLINKS.compare(view)) {
			comp = new ItemLinkTable(mainFrame);
			isTable = true;
		} else if (ViewName.CHART_PERSONS_BY_DATE.compare(view)) {
			comp = new PersonsByDate(mainFrame);
		} else if (ViewName.CHART_PERSONS_BY_SCENE.compare(view)) {
			comp = new PersonsByScene(mainFrame);
		} else if (ViewName.CHART_WiWW.compare(view)) {
			comp = new WiWW(mainFrame);
		} else if (ViewName.CHART_STRANDS_BY_DATE.compare(view)) {
			comp = new StrandsByDate(mainFrame);
		} else if (ViewName.CHART_OCCURRENCE_OF_PERSONS.compare(view)) {
			comp = new OccurrenceOfPersons(mainFrame);
		} else if (ViewName.CHART_OCCURRENCE_OF_LOCATIONS.compare(view)) {
			comp = new OccurrenceOfLocations(mainFrame);
		} else if (ViewName.CHART_OCCURRENCE_OF_ITEMS.compare(view)) {
			comp = new OccurrenceOfItems(mainFrame);
		} else if (ViewName.ATTRIBUTES.compare(view)) {
			comp = new AttributeTable(mainFrame);
		} else if (ViewName.ATTRIBUTESLIST.compare(view)) {
			comp = new AttributesViewPanel(mainFrame);
		} else if (ViewName.TREE.compare(view)) {
			comp = new TreePanel(mainFrame);
		} else if (ViewName.INFO.compare(view)) {
			comp = new InfoPanel(mainFrame);
		} else if (ViewName.MEMOS.compare(view)) {
			comp = new MemoPanel(mainFrame);
		} else if (ViewName.NAVIGATION.compare(view)) {
			comp = new NavigationPanel(mainFrame);
		} else if (ViewName.INTERNALS.compare(view)) {
			comp = new InternalTable(mainFrame);
		} else if (ViewName.PLAN.compare(view)) {
			comp = new Planning(mainFrame);
		} else if (ViewName.TIMEEVENT.compare(view)) {
			comp = new TimeEventTable(mainFrame);
			isTable = true;
		}
		comp.initAll();
		view.load(comp);
		if (isTable && !initialisation) {
			loadTableDesign(view);
		}
	}

	public void unloadView(SbView view) {
		SbApp.trace("ViewFactory.unloadView(" + view.getName() + ")");
		if (ViewName.SCENES.compare(view) || 
				ViewName.CHAPTERS.compare(view) ||
				ViewName.PARTS.compare(view) ||
				ViewName.LOCATIONS.compare(view) ||
				ViewName.PERSONS.compare(view) ||
				ViewName.RELATIONSHIPS.compare(view) ||
				ViewName.GENDERS.compare(view) ||
				ViewName.CATEGORIES.compare(view) ||
				ViewName.STRANDS.compare(view) ||
				ViewName.IDEAS.compare(view) ||
				ViewName.MEMOS.compare(view) ||
				ViewName.TAGS.compare(view) ||
				ViewName.ITEMS.compare(view) ||
				ViewName.TAGLINKS.compare(view) ||
				ViewName.ITEMLINKS.compare(view) ||
				ViewName.TIMEEVENT.compare(view)) {
			saveTableDesign(view);
		}
		view.unload();
	}

	private String getChartName(String i18nKey) {
		SbApp.trace("ViewFactory.getChartName(" + i18nKey + ")");
		return I18N.getMsg("chart") + ": " + I18N.getMsg(i18nKey);
	}

	public SbView getChartPersonsByDate() {
		SbApp.trace("ViewFactory.getChartPersonsByDate()");
		if (isViewInitialized(ViewName.CHART_PERSONS_BY_DATE)) {
			SbView view = new SbView(getChartName("tools.charts.overall.character.date"));
			view.setName(ViewName.CHART_PERSONS_BY_DATE.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CHART_PERSONS_BY_DATE.toString());
	}

	public SbView getChartPersonsByScene() {
		SbApp.trace("ViewFactory.getChartPersonsByScene()");
		if (isViewInitialized(ViewName.CHART_PERSONS_BY_SCENE)) {
			SbView view = new SbView(getChartName("tools.charts.part.character.scene"));
			view.setName(ViewName.CHART_PERSONS_BY_SCENE.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CHART_PERSONS_BY_SCENE.toString());
	}

	public SbView getChartWiWW() {
		SbApp.trace("ViewFactory.getChartWiWW()");
		if (isViewInitialized(ViewName.CHART_WiWW)) {
			SbView view = new SbView(getChartName("tools.charts.overall.whoIsWhereWhen"));
			view.setName(ViewName.CHART_WiWW.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CHART_WiWW.toString());
	}

	public SbView getChartStrandsByDate() {
		SbApp.trace("ViewFactory.getChartStrandsByDate()");
		if (isViewInitialized(ViewName.CHART_STRANDS_BY_DATE)) {
			SbView view = new SbView(getChartName("tools.charts.overall.strand.date"));
			view.setName(ViewName.CHART_STRANDS_BY_DATE.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CHART_STRANDS_BY_DATE.toString());
	}

	public SbView getChartOccurrenceOfPersons() {
		SbApp.trace("ViewFactory.getChartOccurrenceOfPersons()");
		if (isViewInitialized(ViewName.CHART_OCCURRENCE_OF_PERSONS)) {
			SbView view = new SbView(getChartName("tools.charts.overall.character.occurrence"));
			view.setName(ViewName.CHART_OCCURRENCE_OF_PERSONS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CHART_OCCURRENCE_OF_PERSONS.toString());
	}

	public SbView getChartOccurrenceOfLocations() {
		SbApp.trace("ViewFactory.getChartOccurrenceOfLocations()");
		if (isViewInitialized(ViewName.CHART_OCCURRENCE_OF_LOCATIONS)) {
			SbView view = new SbView(getChartName("tools.charts.overall.location.occurrence"));
			view.setName(ViewName.CHART_OCCURRENCE_OF_LOCATIONS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CHART_OCCURRENCE_OF_LOCATIONS.toString());
	}

	public SbView getChartOccurrenceOfItems() {
		SbApp.trace("ViewFactory.getChartOccurrenceOfItems()");
		if (isViewInitialized(ViewName.CHART_OCCURRENCE_OF_ITEMS)) {
			SbView view = new SbView(getChartName("tools.charts.overall.item.occurrence"));
			view.setName(ViewName.CHART_OCCURRENCE_OF_ITEMS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CHART_OCCURRENCE_OF_ITEMS.toString());
	}

	public SbView getChartGantt() {
		SbApp.trace("ViewFactory.getChartGantt()");
		if (isViewInitialized(ViewName.CHART_GANTT)) {
			SbView view = new SbView(getChartName("chart.gantt.characters.title"));
			view.setName(ViewName.CHART_GANTT.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CHART_GANTT.toString());
	}
	
	public SbView getScenesView() {
		SbApp.trace("ViewFactory.getScenesView()");
		if (isViewInitialized(ViewName.SCENES)) {
			SbView view = new SbView(I18N.getMsg("scenes"));
			view.setName(ViewName.SCENES.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.SCENES.toString());
	}

	public SbView getChaptersView() {
		SbApp.trace("ViewFactory.getChaptersView()");
		if (isViewInitialized(ViewName.CHAPTERS)) {
			SbView view = new SbView(I18N.getMsg("chapters"));
			view.setName(ViewName.CHAPTERS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CHAPTERS.toString());
	}

	public SbView getPartsView() {
		SbApp.trace("ViewFactory.getPartsView()");
		if (isViewInitialized(ViewName.PARTS)) {
			SbView view = new SbView(I18N.getMsg("parts"));
			view.setName(ViewName.PARTS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.PARTS.toString());
	}

	public SbView getLocationsView() {
		SbApp.trace("ViewFactory.getLocationsView()");
		if (isViewInitialized(ViewName.LOCATIONS)) {
			SbView view = new SbView(I18N.getMsg("locations"));
			view.setName(ViewName.LOCATIONS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.LOCATIONS.toString());
	}

	public SbView getPersonsView() {
		SbApp.trace("ViewFactory.getPersonsView()");
		if (isViewInitialized(ViewName.PERSONS)) {
			SbView view = new SbView(I18N.getMsg("persons"));
			view.setName(ViewName.PERSONS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.PERSONS.toString());
	}

	public SbView getRelationshipsView() {
		SbApp.trace("ViewFactory.getRelationshipsView()");
		if (isViewInitialized(ViewName.RELATIONSHIPS)) {
			SbView view = new SbView(I18N.getMsg("relationship"));
			view.setName(ViewName.RELATIONSHIPS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.RELATIONSHIPS.toString());
	}

	public SbView getGendersView() {
		SbApp.trace("ViewFactory.getGendersView()");
		if (isViewInitialized(ViewName.GENDERS)) {
			SbView view = new SbView(I18N.getMsg("genders"));
			view.setName(ViewName.GENDERS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.GENDERS.toString());
	}

	public SbView getCategoriesView() {
		SbApp.trace("ViewFactory.getCategoriesView()");
		if (isViewInitialized(ViewName.CATEGORIES)) {
			SbView view = new SbView(I18N.getMsg("persons.categories"));
			view.setName(ViewName.CATEGORIES.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CATEGORIES.toString());
	}

	public SbView getStrandsView() {
		SbApp.trace("ViewFactory.getStrandsView()");
		if (isViewInitialized(ViewName.STRANDS)) {
			SbView view = new SbView(I18N.getMsg("strands"));
			view.setName(ViewName.STRANDS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.STRANDS.toString());
	}

	public SbView getIdeasView() {
		SbApp.trace("ViewFactory.getIdeasView()");
		if (isViewInitialized(ViewName.IDEAS)) {
			SbView view = new SbView(I18N.getMsg("ideas.title"));
			view.setName(ViewName.IDEAS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.IDEAS.toString());
	}

	public SbView getTagsView() {
		SbApp.trace("ViewFactory.getTagsView()");
		if (isViewInitialized(ViewName.TAGS)) {
			SbView view = new SbView(I18N.getMsg("tags"));
			view.setName(ViewName.TAGS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.TAGS.toString());
	}

	public SbView getItemView() {
		SbApp.trace("ViewFactory.getItemView()");
		if (isViewInitialized(ViewName.ITEMS)) {
			SbView view = new SbView(I18N.getMsg("items"));
			view.setName(ViewName.ITEMS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.ITEMS.toString());
	}

	public SbView getTagLinksView() {
		SbApp.trace("ViewFactory.getTagLinksView()");
		if (isViewInitialized(ViewName.TAGLINKS)) {
			SbView view = new SbView(I18N.getMsg("tags.links"));
			view.setName(ViewName.TAGLINKS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.TAGLINKS.toString());
	}

	public SbView getItemLinksView() {
		SbApp.trace("ViewFactory.getItemLinksView()");
		if (isViewInitialized(ViewName.ITEMLINKS)) {
			SbView view = new SbView(I18N.getMsg("items.links"));
			view.setName(ViewName.ITEMLINKS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.ITEMLINKS.toString());
	}

//	public SbView getEditorView() {
//		SbApp.trace("ViewFactory.getEditorView()");
//		if (isViewInitialized(ViewName.EDITOR)) {
//			EntityEditor editor = new EntityEditor(mainFrame);
//			/* supression editor.initAll();*/
//			SbView view = new SbView(I18N.getMsg("editor"), editor);
			// view.getWindowProperties().setCloseEnabled(false);
//			view.setName(ViewName.EDITOR.toString());
			// view.addListener(new DockingWindowAdapter() {
			// public void windowHidden(DockingWindow window) {
			// System.out.println("hidden");
			// }
			// });
//			viewMap.addView(view.getName(), view);
//		}
//		return (SbView) viewMap.getView(ViewName.EDITOR.toString());
//	}

	public SbView getChronoView() {
		SbApp.trace("ViewFactory.getChronoView()");
		if (isViewInitialized(ViewName.CHRONO)) {
			SbView view = new SbView(I18N.getMsg("view.chrono"));
			view.setName(ViewName.CHRONO.toString());
			addButtons(view,OPTIONS+EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.CHRONO.toString());
	}

	public SbView getBookView() {
		SbApp.trace("ViewFactory.getBookView()");
		if (isViewInitialized(ViewName.BOOK)) {
			SbView view = new SbView(I18N.getMsg("view.book"));
			view.setName(ViewName.BOOK.toString());
			addButtons(view,OPTIONS);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.BOOK.toString());
	}

	public SbView getManageView() {
		SbApp.trace("ViewFactory.getManageView()");
		if (isViewInitialized(ViewName.MANAGE)) {
			SbView view = new SbView(I18N.getMsg("view.manage"));
			view.setName(ViewName.MANAGE.toString());
			addButtons(view,OPTIONS);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.MANAGE.toString());
	}

	public SbView getReadingView() {
		SbApp.trace("ViewFactory.getReadingView()");
		if (isViewInitialized(ViewName.READING)) {
			SbView view = new SbView(I18N.getMsg("view.reading"));
			view.setName(ViewName.READING.toString());
			addButtons(view,OPTIONS);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.READING.toString());
	}

	public SbView getMemoriaView() {
		SbApp.trace("ViewFactory.getMemoriaView()");
		if (isViewInitialized(ViewName.MEMORIA)) {
			SbView view = new SbView(I18N.getMsg("view.pov"));
			view.setName(ViewName.MEMORIA.toString());
			addButtons(view,OPTIONS);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.MEMORIA.toString());
	}

	public SbView getStoryboardView() {
		SbApp.trace("ViewFactory.getStoryboardView()");
		if (isViewInitialized(ViewName.STORYBOARD)) {
			SbView view = new SbView(I18N.getMsg("view.storyboard"));
			view.setName(ViewName.STORYBOARD.toString());
			addButtons(view,NONE);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.STORYBOARD.toString());
	}

	public SbView getPlanView() {
		SbApp.trace("ViewFactory.getPlanView()");
		if (isViewInitialized(ViewName.PLAN)) {
			SbView view = new SbView(I18N.getMsg("view.plan"));
			view.setName(ViewName.PLAN.toString());
			addButtons(view,NONE);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.PLAN.toString());
	}

	public SbView getTimeEventView() {
		SbApp.trace("ViewFactory.getTimeEventView()");
		if (isViewInitialized(ViewName.TIMEEVENT)) {
			SbView view = new SbView(I18N.getMsg("view.timeline"));
			view.setName(ViewName.TIMEEVENT.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.TIMEEVENT.toString());
	}

	public SbView getTreeView() {
		SbApp.trace("ViewFactory.getTreeView()");
		if (isViewInitialized(ViewName.TREE)) {
			SbView view = new SbView(I18N.getMsg("tree"));
			view.setName(ViewName.TREE.toString());
			addButtons(view,NONE);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.TREE.toString());
	}

	public SbView getQuickInfoView() {
		SbApp.trace("ViewFactory.getQuickInfoView()");
		if (isViewInitialized(ViewName.INFO)) {
			SbView view = new SbView(I18N.getMsg("info.title"));
			view.setName(ViewName.INFO.toString());
			addButtons(view,NONE);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.INFO.toString());
	}

	public SbView getMemoView() {
		SbApp.trace("ViewFactory.getMemoView()");
		if (isViewInitialized(ViewName.INFO)) {
			SbView view = new SbView(I18N.getMsg("memo.title"));
			view.setName(ViewName.MEMOS.toString());
			addButtons(view,EXPORT+OPTIONS);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.MEMOS.toString());
	}

	public SbView getAttributesView() {
		SbApp.trace("ViewFactory.getAttributesView()");
		if (isViewInitialized(ViewName.ATTRIBUTES)) {
			SbView view = new SbView(I18N.getMsg("attribute_s"));
			view.setName(ViewName.ATTRIBUTES.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.ATTRIBUTES.toString());
	}

	public SbView getAttributesListView() {
		SbApp.trace("ViewFactory.getAttributesListView()");
		if (isViewInitialized(ViewName.ATTRIBUTESLIST)) {
			SbView view = new SbView(I18N.getMsg("attribute.list"));
			view.setName(ViewName.ATTRIBUTESLIST.toString());
			addRefreshButton(view);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.ATTRIBUTESLIST.toString());
	}

	public SbView getNavigationView() {
		SbApp.trace("ViewFactory.getNavigationView()");
		if (isViewInitialized(ViewName.NAVIGATION)) {
			SbView view = new SbView(I18N.getMsg("navigation"));
			view.setName(ViewName.NAVIGATION.toString());
			addButtons(view,NONE);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.NAVIGATION.toString());
	}

	public SbView getTypistView() {
		SbApp.trace("ViewFactory.getTypistView()");
		if (isViewInitialized(ViewName.TYPIST)) {
			SbView view = new SbView(I18N.getMsg("typist"));
			view.setName(ViewName.TYPIST.toString());
			addButtons(view,NONE);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.TYPIST.toString());
	}

	public SbView getInternalsView() {
		SbApp.trace("ViewFactory.getInternalsView()");
		if (isViewInitialized(ViewName.INTERNALS)) {
			SbView view = new SbView("Internals");
			view.setName(ViewName.INTERNALS.toString());
			addButtons(view,EXPORT);
			viewMap.addView(view.getName(), view);
		}
		return (SbView) viewMap.getView(ViewName.INTERNALS.toString());
	}

	private void addButtons(SbView view, int bt) {
		addRefreshButton(view);
		if (bt==10 || bt==11) addOptionsButton(view);
		//addPrintButton(view);
		if (bt==1 || bt==11) addExportButton(view);
		addSeparator(view);
	}

	@SuppressWarnings("unchecked")
	private void addRefreshButton(SbView view) {
		//SbApp.trace("ViewFactory.addRefreshButton("+view.getName()+")");
		JButton bt = createMiniButton("icon.mini.refresh", "refresh");
		bt.addActionListener((ActionEvent e) -> {
			mainFrame.setWaitingCursor();
			mainFrame.getBookController().refresh(view);
			mainFrame.setDefaultCursor();
		});
		view.getCustomTabComponents().add(bt);
	}

	@SuppressWarnings("unchecked")
	private void addOptionsButton(final SbView view) {
		//SbApp.trace("ViewFactory.addOptionsButton("+view.getName()+")");
		JButton bt = createMiniButton("icon.mini.options", "options");
		bt.addActionListener((ActionEvent e) -> {
			mainFrame.getBookController().showOptions(view);
		});
		view.getCustomTabComponents().add(bt);
	}

	@SuppressWarnings("unchecked")
	private void addExportButton(final SbView view) {
		//SbApp.trace("ViewFactory.addExportButton("+view.getName()+")");
		JButton bt = createMiniButton("icon.mini.export", "export");
		bt.addActionListener((ActionEvent e) -> {
			mainFrame.getBookController().export(view);
		});
		view.getCustomTabComponents().add(bt);
	}

	@SuppressWarnings({"unchecked", "unused"})
	private void addPrintButton(final SbView view) {
		//SbApp.trace("ViewFactory.addPrintButton("+view.getName()+")");
		JButton bt = createMiniButton("icon.mini.print", "print");
		bt.addActionListener((ActionEvent e) -> {
			mainFrame.getBookController().print(view);
		});
		view.getCustomTabComponents().add(bt);
	}

	private JButton createMiniButton(String iconKey, String toolTipKey) {
		//SbApp.trace("ViewFactory.createMiniButton("+iconKey+","+toolTipKey+")");
		final JButton bt = new JButton(I18N.getImageIcon(iconKey,new Dimension(12,12)));
		bt.setOpaque(false);
		bt.setBorder(null);
		bt.setBorderPainted(false);
		bt.setContentAreaFilled(false);
		bt.setToolTipText(I18N.getMsg(toolTipKey));
		return bt;
	}

	@SuppressWarnings("unchecked")
	private void addSeparator(View view) {
		SbApp.trace("ViewFactory.addSeparator("+(view!=null?view.getName():"null")+")");
		if (view!=null) view.getCustomTabComponents().add(new JLabel("  "));
	}

	public StringViewMap getViewMap() {
		SbApp.trace("ViewFactory.getViewMap()");
		return viewMap;
	}

	private boolean isViewInitialized(ViewName viewName) {
		return viewMap.getView(viewName.toString()) == null;
	}

	public void saveAllTableDesign() {
		if (viewMap.getViewCount() == 0) {
			return;
		}
		for (int i = 0; i < viewMap.getViewCount(); i++) {
			saveTableDesign((SbView) viewMap.getViewAtIndex(i));
		}
	}

	private void saveTableDesign(SbView view) {
		if (mainFrame.isBlank()) return;
		if (!ViewName.SCENES.compare(view) && 
				!ViewName.CHAPTERS.compare(view) &&
				!ViewName.PARTS.compare(view) &&
				!ViewName.LOCATIONS.compare(view) &&
				!ViewName.PERSONS.compare(view) &&
				!ViewName.RELATIONSHIPS.compare(view) &&
				!ViewName.GENDERS.compare(view) &&
				!ViewName.CATEGORIES.compare(view) &&
				!ViewName.STRANDS.compare(view) &&
				!ViewName.IDEAS.compare(view) &&
				//!ViewName.MEMOS.compare(view) &&
				!ViewName.TAGS.compare(view) &&
				!ViewName.ITEMS.compare(view) &&
				!ViewName.TAGLINKS.compare(view) &&
				!ViewName.ITEMLINKS.compare(view) &&
				!ViewName.TIMEEVENT.compare(view)) {
			return;
		}
		if (!view.isLoaded()) return;
		SbApp.trace("ViewFactory.saveTableDesign(" + view.getName() + ")");
		try {
			AbstractTable comp = (AbstractTable) view.getComponent();
			if (comp==null) return;
			JXTable table = comp.getTable();
			if (table==null) return;
			if (table.getColumns(true).isEmpty()) return;
			String x="";
			for (TableColumn col : table.getColumns(true)) {
				String l1 = (String) col.getHeaderValue();
				TableColumnExt ext = table.getColumnExt(col.getHeaderValue().toString());
				int modelIdx=col.getModelIndex();
				if (modelIdx==-1) continue;
				int ix=table.convertColumnIndexToView(modelIdx)+1;
				if (ix==-1) continue;
				String strVal=col.getPreferredWidth()+","+ix+(ext.isVisible()?"":",hide");
				x+="("+l1+"="+strVal+")";
			}
			SbApp.trace("save:"+"Table." + comp.getTableName()+"::"+x);
			BookUtil.removeAllTableKey(mainFrame,"Table." + comp.getTableName());
			BookUtil.store(mainFrame, "Table." + comp.getTableName(), x);
		} catch (Exception e) {
			System.err.println("saveTableDesign("+view.getName()+")\n"+" err="+e.getLocalizedMessage());
		}
	}

	private void loadTableDesign(SbView view) {
		SbApp.trace("ViewFactory.loadTableDesign("+view.getName()+")");
		AbstractTable comp = (AbstractTable) view.getComponent();
		JXTable table = comp.getTable();
		if (comp==null) return;
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		String tableKey = "Table." + comp.getTableName();
		if (!BookUtil.isKeyExist(mainFrame, tableKey)) {
			return;
		}
		String z=BookUtil.get(mainFrame, tableKey,"").getStringValue();
		String[] xlist=z.substring(1).split("\\(");
		List<TABCOL> tcol= new ArrayList<>();
		for (String x2 : xlist) {
			if ("".equals(x2)) continue;
			String[] xname=x2.substring(0,x2.length()-1).split("=");
			if (xname.length<2) continue;
			String[] x3=xname[1].split(",");
			boolean bhide=false;
			for (String x4:x3)
				if ("hide".equals(x4))
					bhide=true;
			Integer lng=Integer.parseInt(x3[0]);
			Integer idx=Integer.parseInt(x3[1]);
			TABCOL tabcol=new TABCOL(xname[0],lng,idx,bhide);
			SbApp.trace("load:"+tabcol.toString());
			tcol.add(tabcol);
		}
		for (TableColumn col : table.getColumns(true)) {
			String colName = (String) col.getHeaderValue();
			for (TABCOL c:tcol) {
				if (c.name.equals(colName)) {
					TableColumnExt ext = table.getColumnExt(colName);
					if (c.hide) {
						ext.setVisible(false);
					} else {
						ext.setVisible(true);
						col.setPreferredWidth(c.lng);
					}
					break;
				}
			}
		}
	}

	private static class TABCOL {
		public String name;
		public Integer lng;
		public Integer idx;
		public boolean hide;
		public TABCOL() {
		}
		public TABCOL(String n, Integer l, Integer i, boolean h) {
			name=n;
			lng=l;
			idx=i;
			hide=h;
		}
	}
}
