/*
 * Copyright (C) 2017 favdb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import org.miginfocom.swing.MigLayout;
import storybook.SbPref;
import storybook.i18n.I18N;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class ToolBarDlg extends AbstractDialog {
	/* desciption des boutons
	paramètre 1 = groupe (file,new, table, view
	paramètre 2 = 
	paramètre 3 = nom de l'icone
	paramètre 4 = texte du tooltips
	*/
		String boutons[][] = {
			{"file", "0", "16x16/file-new", "file.new"}
			, {"file", "0", "16x16/file-open", "file.open"}
			, {"file", "0", "16x16/file-save", "file.save"}
			, {"new", "0", "16x16/strand", "strand.new"}
			, {"new", "0", "16x16/part", "part.new"}
			, {"new", "0", "16x16/chapter", "chapter.new"}
			, {"new", "0", "16x16/scene", "scene.new"}
			, {"new", "1", "16x16/person", "person.new"}
			, {"new", "1", "16x16/gender", "gender.new"}
			, {"new", "1", "16x16/group", "relationship.new"}
			, {"new", "2", "16x16/location", "location.new"}
			, {"new", "3", "16x16/item", "item.new"}
			, {"new", "3", "16x16/itemlink", "itemlink.new"}
			, {"new", "4", "16x16/tag", "tag.new"}
			, {"new", "4", "16x16/taglink", "taglink.new"}
			, {"new", "5", "16x16/memo", "memo.new"}
			, {"new", "6", "16x16/idea", "idea.new"}
			, {"table", "0", "16x32/manage_strands", "strand"}
			, {"table", "0", "16x32/manage_parts", "part"}
			, {"table", "0", "16x32/manage_chapters", "chapter"}
			, {"table", "0", "16x32/manage_scenes", "scene"}
			, {"table", "1", "16x32/manage_persons", "person"}
			, {"table", "1", "16x32/manage_genders", "gender"}
			, {"table", "1", "16x32/manage_groups", "relationship"}
			, {"table", "2", "16x32/manage_locations", "location"}
			, {"table", "3", "16x32/manage_items", "item"}
			, {"table", "3", "16x32/manage_item_links", "item.links"}
			, {"table", "4", "16x32/manage_tags", "tag"}
			, {"table", "4", "16x32/manage_tag_links", "tags.links"}
			, {"table", "5", "16x32/manage_memos", "memo"}
			, {"table", "6", "16x32/manage_ideas", "idea"}
			, {"view", "0", "16x16/chrono_view", "view.chrono"}
			, {"view", "1", "16x16/book_view", "view.book"}
			, {"view", "2", "16x16/manage_view", "view.manage"}
			, {"view", "3", "16x16/reading", "view.reading"}
			, {"view", "4", "16x16/memoria", "view.pov"}
			, {"view", "5", "16x16/storyboard", "view.storyboard"}
			, {"view", "6", "16x16/typist", "typist"}/*
			, {"end", "", "", ""}*/
		};
	private JPanel panelFile;
	private JPanel panelNew;
	private JPanel panelTable;
	private JPanel panelView;
	private JButton btSelectAll;
	private JButton btSelectNotAll;
	
	public ToolBarDlg(MainFrame m) {
		super(m);
		initAll();
	}

	@Override
	public void init() {
	}
	
	@Override
	public void initUi() {
		String param = mainFrame.getPref().getString(SbPref.Key.TOOLBAR, SbPref.Default.TOOLBAR.toString());
		while (param.length() < (boutons.length-1)) {
			param += "1";// default all button are visible
		}
		if (param.length()>boutons.length) param=param.substring(0,boutons.length);
		while (param.length()<boutons.length) param=param+"1";
		panelFile = initPanel("file",param);
		panelNew = initPanel("new",param);
		panelTable = initPanel("table",param);
		panelView = initPanel("view",param);
		btSelectAll = new JButton(I18N.getMsg("toolbar.select.all"));
        btSelectAll.addActionListener((java.awt.event.ActionEvent evt) -> {
			selectAll(true);
		});
		btSelectNotAll = new JButton(I18N.getMsg("toolbar.select.notall"));
        btSelectNotAll.addActionListener((java.awt.event.ActionEvent evt) -> {
			selectAll(false);
		});
		
		//layout
		setLayout(new MigLayout("wrap 4", "[]", "[]20[]"));
		setTitle(I18N.getMsg("toolbar.select"));
		add(btSelectAll,"span 2");
		add(btSelectNotAll, "span 2");
		add(panelFile);
		add(panelNew);
		add(panelTable);
		add(panelView);
		add(getOkButton(), "sg,span,split 2,right");
		add(getCancelButton(), "sg");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);		
	}
	
	public JPanel initPanel(String name,String param) {
		JPanel p=new JPanel();
        p.setBorder(
			BorderFactory.createTitledBorder(null,
				I18N.getMsg(name),
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION,
				new Font("Dialog", 1, 12)));
		p.setLayout(new MigLayout());
		String line = "0";
		int i = 0;
		for (String b[] : boutons) {
			if (b[0].equals(name)) {
				ImageIcon icon=new ImageIcon(getClass().getResource("/storybook/resources/icons/" + (b[2] + ".png")));
				JToggleButton bt = new JToggleButton(icon);
				bt.setName(b[2].substring(b[2].indexOf("/") + 1));
				bt.setToolTipText(I18N.getMsg(b[3]));
				bt.setMargin(new Insets(0, 0, 0, 0));
				bt.setSelected((param.charAt(i) == '1'));
				if (param.charAt(i) == '1') {
					bt.setBorder(BorderFactory.createLineBorder(Color.green, 3));
				} else {
					bt.setBorder(BorderFactory.createLineBorder(Color.red, 3));
				}
				if (!b[1].equals(line)) {
					p.add(new JLabel(""), "wrap");
					line = b[1];
				}
				bt.addActionListener((java.awt.event.ActionEvent evt) -> {
					JToggleButton bx = (JToggleButton)evt.getSource();
					if (bx.isSelected()) {
						bt.setBorder(BorderFactory.createLineBorder(Color.green, 3));
					} else {
						bt.setBorder(BorderFactory.createLineBorder(Color.red, 3));
					}
				});
				p.add(bt);
			}
			i++;
		}
		return(p);
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String param = "";
				param+=checkSel(panelFile);
				param+=checkSel(panelNew);
				param+=checkSel(panelTable);
				param+=checkSel(panelView);
				mainFrame.getPref().setString(SbPref.Key.TOOLBAR, param);
				canceled = false;
				dispose();
			}
		};
	}
	
	private String checkSel(JPanel panel) {
		String r="";
		Component comps[] = panel.getComponents();
		for (Component comp : comps) {
			if (!(comp instanceof JToggleButton)) continue;
			JToggleButton bt = (JToggleButton) comp;
			r+=((bt.isSelected()?"1":"0"));
		}
		return(r);
	}

	private void selectAll(boolean b) {
		setSel(panelFile,b);
		setSel(panelNew,b);
		setSel(panelTable,b);
		setSel(panelView,b);
	}

	private void setSel(JPanel panel, boolean b) {
		Component comps[] = panel.getComponents();
		for (Component comp : comps) {
			if (!(comp instanceof JToggleButton)) continue;
			JToggleButton bt = (JToggleButton) comp;
			bt.setSelected(b);
		}
	}
}
