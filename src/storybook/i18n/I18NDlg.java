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
package storybook.i18n;

import java.awt.Insets;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SortOrder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.miginfocom.swing.MigLayout;

import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbPref;
import storybook.toolkit.IOUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;

/**
 *
 * @author favdb
 */
public class I18NDlg extends AbstractDialog {

	private JComboBox<String> cbLanguage;
	private JButton btSave;
	private JXTable table;
	private JTextArea text;
	private JTextArea txComment;
	private JTextPane infoPane1;
	private ArrayList<String> languages;
	private DefaultTableModel tableModel;
	boolean bModified=false;
	private String curLanguage;
	private boolean bFile;

	public I18NDlg(MainFrame m) {
		super(m);
		initAll();
	}
	
	public static void show(MainFrame m) {
		SwingUtil.showDialog(new I18NDlg(m),m,true);
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		Insets insets=new Insets(0,0,0,0);
		JToolBar buttons = new JToolBar();
		buttons.setRollover(true);

		JLabel lbLanguages = new JLabel(I18N.getMsg("language") + ":");
		buttons.add(lbLanguages);

		cbLanguage = new JComboBox<>();
		cbLanguage.addItemListener((ItemEvent evt) -> {
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				ChangeLanguage();
			}
		});
		buttons.add(cbLanguage);

		JToolBar.Separator separator = new JToolBar.Separator();
		buttons.add(separator);

		JButton btNew = getButton("","16x16/file-new","new");
		btNew.addActionListener((ActionEvent evt) -> {
			btNewAction();
		});
		buttons.add(btNew);

		JButton btOpen = getButton("","16x16/file-open","open");
		btOpen.addActionListener((ActionEvent evt) -> {
			btOpenAction();
		});
		buttons.add(btOpen);

		buttons.add(separator);

		JButton btRefresh = getButton("","16x16/refresh","refresh");
		btRefresh.addActionListener((ActionEvent evt) -> {
			bModified = false;
			ChangeLanguage();
		});
		buttons.add(btRefresh);

		buttons.add(separator);

		btSave = getButton("","16x16/file-save","save");
		btSave.addActionListener((ActionEvent evt) -> {
			save();
		});
		buttons.add(btSave);

		buttons.add(separator);

		JButton btExit = getButton("","16x16/exit","exit");
		btExit.addActionListener((ActionEvent evt) -> {
			onExit();
		});
		buttons.add(btExit);

		table = new JXTable();
		initTable();
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		model.addElement(I18N.getMsg("language.select"));
		languages = new ArrayList<>();
		for (SbConstants.Language lang : SbConstants.Language.values()) {
			model.addElement(lang.name().substring(0, 2) + " " + lang.getI18N());
			languages.add(lang.name().substring(0, 2));
		}
		cbLanguage.setModel(model);
		this.addWindowListener(new WindowAdapter() {
			private boolean bModified;
			@Override
			public void windowClosing(WindowEvent e) {
				if (bModified) {
					int confirmed = JOptionPane.showConfirmDialog(null,
						I18N.getMsg("language.confirm"),
						I18N.getMsg("confirm"),
						JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						dispose();
					}
				} else {
					dispose();
				}
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				SelectRow();
			}
		});
		JScrollPane scrollTable = new JScrollPane();
		scrollTable.setViewportView(table);

		JLabel lbText = new JLabel(I18N.getMsg("text"));
		text = new JTextArea();
		text.setColumns(20);
		text.setRows(5);
		text.setEditable(false);
		text.addCaretListener((javax.swing.event.CaretEvent evt) -> {
			textCaretUpdate(evt);
		});
		JScrollPane scrollInfoText = new JScrollPane();
		scrollInfoText.setViewportView(text);

		JLabel lbComment = new JLabel(I18N.getMsg("language.comment"));
		JScrollPane scrollComment = new JScrollPane();
		txComment = new javax.swing.JTextArea();
		txComment.setEditable(false);
		txComment.setColumns(25);
		txComment.setRows(5);
		txComment.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent evt) {
				bModified=true;
			}
		});
		scrollComment.setViewportView(txComment);

		infoPane1 = new JTextPane();

		infoPane1.setEditable(false);
		infoPane1.setText("{0}, {1}, ... are for insertion of variables data, they are mandatory.\n\n\\n stands for line feed.");
		JScrollPane scrollInfo = new JScrollPane();
		scrollInfo.setViewportView(infoPane1);
		scrollInfo.setFocusable(false);
		
		//layout
		setLayout(new MigLayout("wrap 9"));
		setTitle("UI Translation");
		add(lbLanguages);add(cbLanguage);add(separator);add(btNew);add(btOpen);add(btRefresh);add(btSave);add(btExit,"wrap");
		add(lbComment,"wrap");
		add(scrollComment,"sg, span, split 2, grow");add(scrollInfo,"grow,wrap");
		add(scrollTable,"span,growx");
		add(lbText,"span");
		add(scrollInfoText,"span, growx");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);
	}

	private void btNewAction() {
		I18NnewDlg dlg = new I18NnewDlg(this, mainFrame, languages);
		dlg.setVisible(true);
		String str=dlg.getLanguage();
		if (str.isEmpty()) return;
		bFile=dlg.isFile();
		cbLanguage.addItem(str);
		cbLanguage.setSelectedItem(str);
		ChangeLanguage();
	}
	
	private void btOpenAction() {
        File f=IOUtil.selectFile(this,
			mainFrame.getPref().getString(SbPref.Key.LAST_OPEN_DIR, ""),
			"properties",
			"Properties file (*.properties)");
		if (f==null) return;
		bFile=true;
		curLanguage="file:"+f.getAbsolutePath();
		cbLanguage.addItem(curLanguage);
		cbLanguage.setSelectedItem(curLanguage);
		ChangeLanguage();
	}
	
	private void textCaretUpdate(javax.swing.event.CaretEvent evt) {
		String str = text.getText().replace("\n", "\\n");
		int row = table.getSelectedRow();
		if (row!=-1) table.setValueAt(str, row, 2);
	}
	
	private void initTable() {
		List<String> cols = new ArrayList<>();
		cols.add(I18N.getMsg("internal.key"));
		cols.add(I18N.getMsg("language.default"));
		cols.add(" ");
		tableModel = new DefaultTableModel(cols.toArray(), 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 2) {
					return (true);
				}
				return (false);
			}
		};
		// read all keys
		InputStream stream = SbApp.getInstance().getClass().getResourceAsStream("msg/messages.properties");
		BufferedReader txt = new BufferedReader(new InputStreamReader(stream));
		String line;
		try {
			line = txt.readLine();
			while (line != null) {
				if (!line.isEmpty() && !(line.startsWith("#") || line.startsWith(" ")) && line.contains("=")) {
					String[] s = line.split("=");
					s[0]=s[0].trim();
					s[1]=s[1].trim();
					if (!s[0].startsWith("language.")) tableModel.addRow(s);
				}
				line = txt.readLine();
			}
			txt.close();
		} catch (IOException ex) {
			SbApp.error("DlgT10N error", ex);
		}
		table.setModel(tableModel);
		table.setSortOrder(0, SortOrder.ASCENDING);
		CellEditorListener ChangeNotification = new CellEditorListener() {
			@Override
			public void editingCanceled(ChangeEvent e) {

			}

			@Override
			public void editingStopped(ChangeEvent e) {
				bModified = true;
				int row = table.getSelectedRow();
				text.setText((String) table.getValueAt(row, 2));
			}
		};
		table.getDefaultEditor(String.class).addCellEditorListener(ChangeNotification);
	}

	private void ChangeLanguage() {
		if (cbLanguage.getSelectedItem().equals(I18N.getMsg("language.select"))) {
			return;
		}
		if (tableModel.getRowCount() > 0) {
			for (int i = table.getRowCount() - 1; i > 0; i--) {
				tableModel.setValueAt("", i, 2);
			}
		}
		curLanguage = (String) cbLanguage.getSelectedItem();
		if (curLanguage.contains("=")) return;
		String bundler = "storybook.msg.messages_" + curLanguage.substring(0, 2).toLowerCase();
		Object src;
		if (curLanguage.startsWith("file:")) {
			bundler=curLanguage.replace("file:", "");
			bundler=bundler.replace(".properties","");
			String path=bundler.substring(0, bundler.lastIndexOf("/"));
			String name=bundler.substring(bundler.lastIndexOf("/")+1);
			URL resourceURL;
			File fl = new File(path);
			try {
				resourceURL = fl.toURI().toURL();
			} catch (MalformedURLException ex) {
				SbApp.error("ChangeLanguage", ex);
				return;
			}
			URLClassLoader urlLoader = new URLClassLoader(new java.net.URL[]{resourceURL});
			src= ResourceBundle.getBundle(name,java.util.Locale.getDefault(),urlLoader);
		} else {
			src = ResourceBundle.getBundle(bundler);
		}
		setLanguageHeader();
		bModified = false;
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String s1 = (String) tableModel.getValueAt(i, 0);
			String s2 = "";
			try {
				s2 = ((ResourceBundle)src).getString(s1).replace("\n","\\n");
			} catch (Exception e) {
			}
			tableModel.setValueAt(s2.trim(), i, 2);
		}
		readComment(curLanguage.substring(0, 2).toLowerCase());
		SelectRow();
		text.setEditable(true);
	}

	private void SelectRow() {
		int row = table.getSelectedRow();
		if (row == -1) {
			return;
		}
		int col = 2;
		if (table.getValueAt(row, col)!=null) {
			String str = ((String) table.getValueAt(row, col)).replace("\\n","\n");
			text.setText(str);
			text.setCaretPosition(0);
		}
	}

	private void save() {
		//select a valid file name, if the file exists override
		File dir = IOUtil.selectDirectory(this, mainFrame.getPref().getString(SbPref.Key.LAST_OPEN_DIR, ""));
		if (dir == null) {
			return;
		}
		String fileName;
		if (curLanguage.startsWith("file:")) {
			fileName=curLanguage.replace("file:", "");
		} else {
			fileName = dir.getAbsolutePath() + "/messages_" + curLanguage.substring(0, 2) + ".properties";
			File file = new File(fileName);
			if (file.exists()) {
				if (JOptionPane.showConfirmDialog(null,
					I18N.getMsg("language.overwrite"),
					I18N.getMsg("confirm"),
					JOptionPane.YES_NO_OPTION) == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(fileName);
			writeComment(out);
			for (int i = 0; i < table.getRowCount(); i++) {
				String val = (String) table.getValueAt(i, 2);
				if (val != null && !val.isEmpty()) {
					String bytes = table.getValueAt(i, 0) + "=" + val.trim();
					byte[] buffer = bytes.getBytes(/*Charset.forName("UTF-8")*/);
					out.write(buffer);
					out.write("\n".getBytes());
				}
			}
			
			JOptionPane.showMessageDialog(null,
				I18N.getMsg("language.saveok", fileName),
				I18N.getMsg("language"),
				JOptionPane.INFORMATION_MESSAGE);
			bModified = false;
		} catch (FileNotFoundException ex) {
			System.err.println(ex.getLocalizedMessage());
		} catch (IOException ex) {
			SbApp.error("save", ex);
		}
	}

	private void readComment(String lang) {
		String fileName="msg/messages_" + lang + ".properties";
		InputStream stream;
		if (curLanguage.startsWith("file:")) {
			try {
				stream=new FileInputStream(curLanguage.replace("file:",""));
			} catch (FileNotFoundException ex) {
				SbApp.error("readComment", ex);
				return;
			}
		}
		else stream = SbApp.getInstance().getClass().getResourceAsStream(fileName);
		BufferedReader txt = new BufferedReader(new InputStreamReader(stream));
		String line;
		String comment = "";
		try {
			line = txt.readLine();
			while (line != null) {
				if (!line.isEmpty() && line.startsWith("#")) {
					comment += line.replaceFirst("#", "").trim() + "\n";
				} else break;
				line = txt.readLine();
			}
			txt.close();
			txComment.setText(comment);
			txComment.setEditable(true);
			txComment.setCaretPosition(0);
		} catch (IOException ex) {
			SbApp.error("DlgT10N error", ex);
		}
	}

	private void writeComment(FileOutputStream out) {
		String str=txComment.getText();
		if (str!=null) {
			String s[]=str.split("\n");
			for (String x:s) {
				String bytes = "#"+x+"\n";
				byte[] buffer = bytes.getBytes();
				try {
					out.write(buffer);
				} catch (IOException ex) {
					SbApp.error("writeComment", ex);
				}
			}
		}
	}

	private int onExit() {
		if (bModified) {
			if (JOptionPane.showConfirmDialog(null,
				I18N.getMsg("language.confirm"),
				I18N.getMsg("confirm"),
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				dispose();
				return(JDialog.DISPOSE_ON_CLOSE);
			} else {
				//dispose();
				return(JDialog.DO_NOTHING_ON_CLOSE);
			}
		} else dispose();
		return(JDialog.DO_NOTHING_ON_CLOSE);
	}

	private void setLanguageHeader() {
		table.getTableHeader().getColumnModel().getColumn(2).setHeaderValue(curLanguage);
	}

}
