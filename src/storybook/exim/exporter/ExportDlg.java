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
package storybook.exim.exporter;

import storybook.exim.exporter.options.TXTpanel;
import storybook.exim.exporter.options.HTMLpanel;
import storybook.exim.exporter.options.BOOKpanel;
import storybook.exim.exporter.options.CSVpanel;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.miginfocom.swing.MigLayout;

import storybook.SbApp;
import storybook.SbConstants;
import storybook.i18n.I18N;
import storybook.toolkit.BookUtil;
import storybook.toolkit.H2_Script;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;
import storybook.ui.dialog.ExceptionDlg;

/**
 *
 * @author favdb
 */
public class ExportDlg extends AbstractDialog implements ActionListener, CaretListener {

	private JTextField txFolder;
	private JTabbedPane options;
	private JComboBox cbReport;
	public ArrayList<ExportType> exports;
	public ParamExport param;
	private HTMLpanel HTML;
	private CSVpanel CSV;
	private TXTpanel TXT;
	private BOOKpanel BOOK;
	private JRadioButton rbHtml, rbTxt, rbCsv, rbXml, rbSql;
	private JPanel pnFormat;

	public ExportDlg(MainFrame m) {
		super(m);
		initAll();
	}

	public static void show(MainFrame m) {
		SwingUtil.showModalDialog(new ExportDlg(m), m, true);
	}

	@Override
	public void init() {
		super.initUi();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initUi() {
		SbApp.trace("ExportDlg.initUi()");
		super.initUi();
		param = new ParamExport(mainFrame);

		JLabel lbFolder = new JLabel(I18N.getMsg("export.folder"));
		txFolder = new JTextField();
		txFolder.setText(BookUtil.getString(mainFrame, SbConstants.BookKey.EXPORT_DIRECTORY));
		txFolder.setColumns(32);
		txFolder.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				File f = new File(txFolder.getText());
				if (f.exists() && f.isDirectory()) {
					UIDefaults defaults = javax.swing.UIManager.getDefaults();
					txFolder.setForeground(defaults.getColor("TextField.foreground"));
				} else {
					txFolder.setForeground(Color.red);
				}
			}
		});
		JButton bt = new JButton(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/open.png")));
		bt.addActionListener((java.awt.event.ActionEvent evt) -> {
			JFileChooser chooser = new JFileChooser(txFolder.getText());
			chooser.setFileSelectionMode(1);
			int i = chooser.showOpenDialog(null);
			if (i != 0) {
				return;
			}
			File file = chooser.getSelectedFile();
			txFolder.setText(file.getAbsolutePath());
			txFolder.setBackground(Color.WHITE);
		});
		bt.setMargin(new Insets(0, 0, 0, 0));
		initExports();

		JLabel lbReport = new JLabel(I18N.getMsg("export.type"));
		initReport();
		initFormat();

		BOOK = new BOOKpanel(param);
		CSV = new CSVpanel(param);
		TXT = new TXTpanel(param);
		HTML = new HTMLpanel(param);
		initOptions();

		//layout
		setLayout(new MigLayout("", "", ""));
		setBackground(Color.white);
		setTitle(I18N.getMsg("export"));
		add(lbFolder, "split 3");
		add(txFolder);
		add(bt, "wrap");
		add(lbReport, "split 2");
		add(cbReport, "wrap");
		add(pnFormat, "span, wrap");
		add(options, "span, left, wrap");
		add(getOkButton(), "span, split 2,sg,right");
		add(getCancelButton(), "sg");
		pack();
		setLocationRelativeTo(mainFrame);
	}

	private void initExports() {
		exports = new ArrayList<>();
		exports.add(new ExportType("book", "export.book.text"));
		exports.add(new ExportType("summary", "export.book.summary"));
		exports.add(new ExportType("parts", "export.part.list"));
		exports.add(new ExportType("chapters", "export.chapter.list"));
		exports.add(new ExportType("scenes", "export.scene.list"));
		exports.add(new ExportType("persons", "export.person.list"));
		exports.add(new ExportType("locations", "export.location.list"));
		exports.add(new ExportType("tags", "export.tag.list"));
		exports.add(new ExportType("items", "export.item.list"));
		exports.add(new ExportType("ideas", "export.idea.list"));
		exports.add(new ExportType("all", "export.all.list"));
		exports.add(new ExportType("sql", "export.sql"));
	}

	private void initOptions() {
		options = new JTabbedPane();
		//options.setLayout(new MigLayout("wrap","[]","[][][][]"));
		//options.setMinimumSize(new Dimension(600, 300));
		options.setBorder(javax.swing.BorderFactory.createTitledBorder(I18N.getMsg("options")));
		options.add(BOOK, I18N.getMsg("export.book.text"));
		options.add(HTML, "HTML");
		options.add(TXT, "TXT");
		options.add(CSV, "CSV");
	}

	@SuppressWarnings("unchecked")
	private void initReport() {
		cbReport = new JComboBox();
		for (ExportType export : exports) {
			cbReport.addItem(export);
		}
		cbReport.setSelectedIndex(0);
		cbReport.addItemListener((java.awt.event.ItemEvent evt) -> {
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				initFormat();
				if (((ExportType) cbReport.getSelectedItem()).getName().equals("book")) {
					options.setSelectedComponent(BOOK);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void initFormat() {
		String m = "";
		if (pnFormat == null) {
			ButtonGroup group1 = new ButtonGroup();
			rbHtml = new JRadioButton("html");
			rbTxt = new JRadioButton("txt");
			rbCsv = new JRadioButton("csv");
			rbXml = new JRadioButton("xml");
			rbSql = new JRadioButton("sql");
			group1.add(rbHtml);
			group1.add(rbTxt);
			group1.add(rbCsv);
			group1.add(rbXml);
			group1.add(rbSql);
			pnFormat = new JPanel();
			pnFormat.setLayout(new MigLayout("", "", ""));
			JLabel lb3 = new JLabel(I18N.getMsg("export.format") + ":");
			pnFormat.add(lb3);
			pnFormat.add(rbHtml);
			pnFormat.add(rbCsv);
			pnFormat.add(rbTxt);
			pnFormat.add(rbXml);
			pnFormat.add(rbSql);
		} else {
			m = getFormat();
		}

		switch (getReportName()) {
			case "book":
				setAllowedFormat("html,txt,xml");
				if (m.equals("sql") || m.equals("csv")) {
					m = "html";
				}
				break;
			case "sql":
				setAllowedFormat("sql");
				m = "sql";
				break;
			default:
				setAllowedFormat("csv,txt,html,xml");
				if (m.equals("sql")) {
					m = "html";
				}
				break;
		}
		setFormat(m);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public void caretUpdate(CaretEvent e) {
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (doExport()) {
					dispose();
				}
			}
		};
	}

	private String getReportName() {
		return (((ExportType) cbReport.getSelectedItem()).getName().toLowerCase());
	}

	private boolean doExport() {
		SbApp.trace("ExportDlg.doExport()");
		boolean b = true;
		String title = BookUtil.get(mainFrame, SbConstants.BookKey.TITLE, "").getStringValue();
		if (title.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				I18N.getMsg("export.missing.title"),
				I18N.getMsg("export"), 1);
			return (false);
		}
		// check if the folder exists
		String dir = txFolder.getText();
		if (dir.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				I18N.getMsg("export.dir.missing"),
				I18N.getMsg("export"), 1);
			return (false);
		}
		//check if dir is a directory
		File f = new File(dir);
		if (!(f.exists() && f.isDirectory())) {
			JOptionPane.showMessageDialog(this,
				I18N.getMsg("export.dir.error"),
				I18N.getMsg("export"), 1);
			return (false);
		}
		saveParam();
		BookUtil.store(mainFrame, SbConstants.BookKey.EXPORT_DIRECTORY, dir);
		String exportName = ((ExportType) cbReport.getSelectedItem()).getName().toLowerCase();
		String exportTitle = ((ExportType) cbReport.getSelectedItem()).getTitle();
		String format = getFormat();
		if (format.equals("sql")) {
			System.out.println("doExportSQL");
			String url = "jdbc:h2:" + mainFrame.getDbFile().getDbName();
			String file = mainFrame.getDbFile().getDbName() + ".sql";
			System.out.println("export to " + file);
			try {
				H2_Script.process(url, "sa", "", file, "", "");
			} catch (SQLException ex) {
				ExceptionDlg.show("export SQL exception", ex);
				return (false);
			}
			return (true);
		}
		String ret = "";
		TableExporter exporter = new TableExporter(mainFrame, exportName, format);
		if ("all".equals(exportName)) {
			for (ExportType t:exports) {
				if (t.isList && (exporter.askFileExists(t.getTitle()))) {
					ret += I18N.getMsg(t.title) + "\n";
				}
			}
		} else if (exporter.askFileExists(exportName)) {
			ret += I18N.getMsg(exportTitle) + "\n";
		}
		if (!ret.replace("\n", "").isEmpty()) {
			if (JOptionPane.showConfirmDialog(this.getParent(),
				I18N.getMsg("export.replace", ret),
				I18N.getMsg("export"),
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
				return (false);
			}
		}
		SwingUtil.setWaitingCursor(this);
		if (exportName.equals("all")) {
			for (ExportType t:exports) {
				if (t.isList) TableExporter.exportTable(mainFrame,t.getName(),format);
			}
		} else {
			TableExporter.exportTable(mainFrame,exportName,format);
		}
		SwingUtil.setDefaultCursor(this);
		if (!ret.isEmpty()) {
			return (false);
		}
		return (true);
	}

	private void saveParam() {
		if (CSV.csvSingleQuotes.isSelected()) {
			param.csvQuote = "'";
		} else if (CSV.csvDoubleQuotes.isSelected()) {
			param.csvQuote = "\"";
		} else if (CSV.csvNoQuotes.isSelected()) {
			param.csvQuote = "";
		}

		if (CSV.csvComma.isSelected()) {
			param.csvComma = ";";
			param.txtTab = false;
		} else {
			param.txtTab = true;
		}

		if (!param.txtTab) {
			param.txtSeparator = TXT.txSeparator.getText();
		}

		param.htmlCssUse = HTML.cbUseCss.isSelected();
		if (param.htmlCssUse) {
			param.htmlCssFile = HTML.txCssFile.getText();
		}
		param.htmlNavImage = HTML.cbNavImage.isSelected();

		param.isExportChapterNumbers = BOOK.ckExportChapterNumbers.isSelected();
		param.isExportChapterNumbersRoman = BOOK.ckExportChapterNumbersRoman.isSelected();
		param.isExportChapterTitles = BOOK.ckExportChapterTitles.isSelected();
		param.isExportSceneTitles = BOOK.ckExportSceneTitles.isSelected();
		param.isExportSceneSeparator = BOOK.ckExportSceneSeparator.isSelected();
		param.htmlBookMulti = BOOK.htmlBookMultiFile.isSelected();
		param.isExportChapterBreakPage=HTML.ckExportChapterBreakPage.isSelected();

		param.save();
	}

	private String getFormat() {
		if (rbSql.isSelected()) {
			return ("sql");
		}
		if (rbTxt.isSelected()) {
			return ("txt");
		}
		if (rbCsv.isSelected()) {
			return ("csv");
		}
		if (rbXml.isSelected()) {
			return ("xml");
		}
		return ("html");
	}

	private void setFormat(String m) {
		switch (m) {
			case "txt":
				rbTxt.setSelected(true);
				return;
			case "csv":
				rbCsv.setSelected(true);
				return;
			case "xml":
				rbXml.setSelected(true);
				return;
			case "sql":
				rbSql.setSelected(true);
				return;
		}
		rbHtml.setSelected(true);
	}

	private void setAllowedFormat(String str) {
		rbHtml.setEnabled(str.contains("html"));
		rbCsv.setEnabled(str.contains("csv"));
		rbTxt.setEnabled(str.contains("txt"));
		rbXml.setEnabled(str.contains("xml"));
		rbSql.setEnabled(str.contains("sql"));
	}

	public class ExportType {

		private String name;
		private String title;
		private boolean isList;

		public ExportType(String name, String key) {
			this.name = name;
			this.title = I18N.getMsg(key);
			if (this.name.contains("list")) this.isList=true;
			else isList=false;
		}

		public void setExportName(String name) {
			this.name = name;
		}

		public void setKey(String title) {
			this.title = title;
		}

		@Override
		public String toString() {
			return getTitle();
		}

		public String getName() {
			return name;
		}

		public String getTitle() {
			return (title);
		}

	}
}
