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
package storybook.exim.importer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.miginfocom.swing.MigLayout;

import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbPref;
import storybook.i18n.I18N;
import storybook.model.EntityUtil;
import storybook.toolkit.BookUtil;
import static storybook.toolkit.BookUtil.getHomeDir;
import storybook.toolkit.FileFilter;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;

/**
 *
 * @author favdb
 */
public class ImportDlg  extends AbstractDialog {
	JCheckBox cbForce;
	JTextField txFile;
	private File fileImport=null;
	private Importer importer;
	private JTabbedPane tbPane;
	private JButton btImport;

	public static void show(MainFrame m) {
		ImportDlg dlg=new ImportDlg(m);
		dlg.setVisible(true);
	}

	public ImportDlg(MainFrame m){
		super(m);
		initAll();
	}

	@Override
	public void init() {
	}
	
	@Override
	public void initUi() {
		SbApp.trace("ExportDlg.initUi()");
		super.initUi();
		JLabel lbFolder = new JLabel(I18N.getMsg("import.dlg.file"));
        txFile = new JTextField();
		//txFile.setColumns(32);
        txFile.setEditable(false);
		JButton btFile = new JButton(new ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/open.png")));
		btFile.setMargin(new Insets(0, 0, 0, 0));
		btFile.addActionListener((java.awt.event.ActionEvent evt) -> {
			btFileAction(evt);
		});		

        cbForce = new javax.swing.JCheckBox();
        cbForce.setText(I18N.getMsg("import.dlg.force"));

		tbPane = new JTabbedPane();
		Dimension dim=new Dimension(460,360);
		tbPane.setMinimumSize(dim);

		//layout
		setLayout(new MigLayout("", "", ""));
		setBackground(Color.white);
		setTitle(I18N.getMsg("export"));
		add(lbFolder, "split 3");
		add(txFile,"growx");
		add(btFile, "wrap");
		add(cbForce, "span, wrap");
		add(tbPane,"span, growx, wrap");
		btImport=getOkButton("import");
		add(btImport, "span, split 2,sg,right");
		add(getCancelButton(), "sg");
		pack();
		setLocationRelativeTo(mainFrame);
		tasks=new ArrayList<>();
		tasks.add(new ImportDlg.ImportTask("gender"));
//		tasks.add(new ImportDlg.ImportTask("category"));
		tasks.add(new ImportDlg.ImportTask("strand"));
		tasks.add(new ImportDlg.ImportTask("part"));
		tasks.add(new ImportDlg.ImportTask("chapter"));
		tasks.add(new ImportDlg.ImportTask("scene"));
		tasks.add(new ImportDlg.ImportTask("person"));
		tasks.add(new ImportDlg.ImportTask("location"));
		tasks.add(new ImportDlg.ImportTask("item"));
		tasks.add(new ImportDlg.ImportTask("tag"));
//		tasks.add(new ImportDlg.ImportTask("timeevent"));
//		tasks.add(new ImportDlg.ImportTask("relationship"));
//		tasks.add(new ImportDlg.ImportTask("itemlink"));
//		tasks.add(new ImportDlg.ImportTask("taglink"));
		txFile.setText(BookUtil.getString(mainFrame,SbConstants.BookKey.IMPORT_FILE));
		tbPane.removeAll();
		java.awt.EventQueue.invokeLater(() -> {btFileAction(null);});
	}

	private void btFileAction(ActionEvent evt) {
		String nf=txFile.getText();
		if (!txFile.getText().isEmpty()) {
			nf=mainFrame.getPref().getString(SbPref.Key.LAST_OPEN_DIR, getHomeDir());
		}
        JFileChooser chooser = new JFileChooser(nf);
		File f=new File(nf);
		chooser.setCurrentDirectory(f);
		String x[]={/*".h2.db",".mv.db",*/".xml"};
		FileFilter filter = new FileFilter(FileFilter.XML, I18N.getMsg("file.import.type"));
		chooser.addChoosableFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);
		int i = chooser.showOpenDialog(this);
        if (i != JFileChooser.APPROVE_OPTION) return;
        fileImport = chooser.getSelectedFile();
		if (fileImport.getAbsolutePath().endsWith(".db") || fileImport.getAbsolutePath().endsWith(".xml")) {
			nf=fileImport.getParent();
	        txFile.setText(fileImport.getAbsolutePath());
			BookUtil.store(mainFrame,SbConstants.BookKey.IMPORT_FILE,fileImport.getAbsolutePath());
			mainFrame.getPref().setString(SbPref.Key.LAST_OPEN_DIR, nf);
			loadLists();
		}
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doImport();
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void doImport() {
		SbApp.trace("ImportDlg.doImport()");
		//TODO add date updating to check
		fileImport=new File(txFile.getText());
		if (!(fileImport.exists() && fileImport.isFile())) return;
		BookUtil.store(mainFrame, SbConstants.BookKey.IMPORT_FILE, txFile.getText());
		mainFrame.getDbFile().doBackup();
		importer=new Importer(txFile.getText(),mainFrame);
		if (importer.open()==false) {
			System.err.println("error Importer failed to open");
			return;
		}
		String msgCheck="";
		for (ImportDlg.ImportTask task:tasks) {
			msgCheck+=checkEntities(task.ls,task.lb);
		}
		if (!msgCheck.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				I18N.getMsg("import.dlg.exists",msgCheck),
				I18N.getMsg("import"),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		int rc=0;
		for (ImportDlg.ImportTask task:tasks) {
			int n=loadEntities(task.ls,task.lb);
			if (n==-1) {
				rc=n;
				break;
			}
			rc+=n;
		}
		importer.close();
		if (rc!=-1) {
			JOptionPane.showMessageDialog(this,
				I18N.getMsg("import.dlg.ok",txFile.getText()),
				I18N.getMsg("import"),
				JOptionPane.INFORMATION_MESSAGE);
			dispose();
		}
	}

	@SuppressWarnings("unchecked")
	private void loadLists() {
		SbApp.trace("ImportDlg.loadLists()");
		tbPane.removeAll();
		importer=new Importer(txFile.getText(),mainFrame);
		if (importer.open()==false) {
			return;
		}
		int b=0;
		for (ImportDlg.ImportTask task:tasks) {
			b+=listEntities(task.ls,task.pn,task.lb);
		}
		btImport.setEnabled(b>0);
		importer.close();
	}
	
	@SuppressWarnings("unchecked")
	private int listEntities(JList<JCheckBox> lst, JPanel tb, String tobj) {
		SbApp.trace("ImportDlg.listEntities(lst,tb,"+tobj+")");
		DefaultListModel<JCheckBox> model = new DefaultListModel<>();
		lst.setModel(model);
		lst.setCellRenderer(new ImportDlg.CheckboxListCellRenderer());
		List<ImportEntity> entities=importer.list(tobj);
		if (!entities.isEmpty()) {
			for (ImportEntity entity:entities) {
				model.addElement(new JCheckBox(EntityUtil.getEntityName(entity.entity)));
			}
			if (tb!=null) {
				tbPane.add(I18N.getMsg(tobj),tb);
			}
		}
		return(entities.size());
	}
	
	public class CheckboxListCellRenderer extends JCheckBox implements ListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList lst, Object val, int idx, boolean isSel, boolean hasFocus) {
			setComponentOrientation(lst.getComponentOrientation());
			setFont(lst.getFont());
			setBackground(lst.getBackground());
			setForeground(lst.getForeground());
			setSelected(isSel);
			setEnabled(lst.isEnabled());
			setText(val == null ? "null" : ((JCheckBox)val).getText());
			return this;
		}
	}
	
	private String checkEntities(JList<JCheckBox> lst, String tobj) {
		if (lst.getSelectedValuesList().isEmpty()) return("");
		SbApp.trace("ImportDlg.checkEntities(lst,"+tobj+") "
			+(cbForce.isSelected()?"force replace":"only if not exists"));
		if (cbForce.isSelected()) return("");//no check because it is forced
		DefaultListModel<JCheckBox> model=(DefaultListModel<JCheckBox>) lst.getModel();
		String msg;
		//reload all entities list
		List<JCheckBox> lsSel = lst.getSelectedValuesList();
		List<ImportEntity> entities=importer.list(tobj);
		List<ImportEntity> selEntities=new ArrayList<>();
		for (ImportEntity entity:entities) {
			for (JCheckBox cb:lsSel) {
				if (cb.getText().equals(EntityUtil.getEntityName(entity.entity))) {
					selEntities.add(entity);
					break;
				}
			}
		}
		msg=importer.checkAll(selEntities);
		if (!msg.isEmpty()) {
			msg="- "+I18N.getMsg(tobj)+": "+msg;
		}
		return(msg);
	}
	
	private int loadEntities(JList<JCheckBox> lst, String tobj) {
		if (lst.getSelectedValuesList().isEmpty()) return(0);
		SbApp.trace("ImportDlg.loadEntities(lst,"+tobj+") "
			+(cbForce.isSelected()?"force replace":"only if not exists"));
		int rc=0;
		DefaultListModel<JCheckBox> model=(DefaultListModel<JCheckBox>) lst.getModel();
		List<JCheckBox> lsSel = lst.getSelectedValuesList();
		List<ImportEntity> entities=importer.list(tobj);
		List<ImportEntity> selEntities=new ArrayList<>();
		for (ImportEntity entity:entities) {
			for (JCheckBox cb:lsSel) {
				if (cb.getText().equals(EntityUtil.getEntityName(entity.entity))) {
					selEntities.add(entity);
					break;
				}
			}
		}
		if (importer.writeAll(selEntities,cbForce.isSelected())) {
			return(-1);
		}
		return(selEntities.size());
	}
	
	List<ImportTask> tasks=new ArrayList<>();
	private class ImportTask {
		JList<JCheckBox> ls;
		JPanel pn;
		String lb;

		public ImportTask(JList<JCheckBox> c, JPanel p, String l) {
			ls=c;
			pn=p;
			lb=l;
		}
		
		public ImportTask(String l) {
			ls=new JList<>();
			pn=new JPanel();
			lb=l;
			GroupLayout layout = new GroupLayout(pn);
			JScrollPane scroll = new JScrollPane();
			scroll.setViewportView(ls);
	        pn.setLayout(layout);
		    layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 406, Short.MAX_VALUE)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scroll, GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
					.addContainerGap()))
	        );
		    layout.setVerticalGroup(
			    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGap(0, 289, Short.MAX_VALUE)
				    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addContainerGap()))
	        );
		}
		
	}
	
}
