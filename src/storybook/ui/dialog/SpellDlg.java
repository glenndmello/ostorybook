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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.event.ListSelectionEvent;
import org.miginfocom.swing.MigLayout;
import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbPref;
import storybook.i18n.I18N;
import storybook.toolkit.EnvUtil;
import storybook.toolkit.SpellCheckerUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class SpellDlg extends AbstractDialog {

	private static List<SpellCheckerUtil.Language> languages;
	private static String installed;
	private static JFrame frmMain;
	private static Container pane;
	private static JProgressBar barDo;
	private static JButton btnCancelLoad;
	private static JLabel txTitle;
	static String language;
	static String inFile;
	private static boolean isLoadCanceled;
	private static SpellDlg thisDlg;
	private static URL urlDico;
	private static File destDico;
	private JList<String> lsInstalled;
	private static JList<String> lsLoadable;
	private JButton btSelectLang;
	private JButton btRefresh;
	private JButton btDownload;

	public SpellDlg(MainFrame m) {
		super(m);
		thisDlg=this;
		initAll();
	}

	@Override
	public void init() {
	}
	
	@Override
	@SuppressWarnings({"unchecked", "unchecked", "unchecked", "unchecked"})
	public void initUi() {
		languages = SpellCheckerUtil.getLanguages();
		lsInstalled=new JList();
		initInstalled();
        lsInstalled.addListSelectionListener((ListSelectionEvent evt) -> {
			btSelectLang.setEnabled(true);
		});
		btSelectLang=new JButton(I18N.getMsg("spelling.select"));
        btSelectLang.setIcon(new ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/ok.png")));
        btSelectLang.setEnabled(false);
        btSelectLang.addActionListener((java.awt.event.ActionEvent evt) -> {
			applySettings();
		});
		JScrollPane scroller1 = new JScrollPane();
        scroller1.setViewportView(lsInstalled);
		JPanel panel1 = new JPanel();
		panel1.setLayout(new MigLayout());
		panel1.setBorder(BorderFactory.createTitledBorder(I18N.getMsg("spelling.dicts.installed")));
		panel1.add(scroller1,"wrap");
		panel1.add(btSelectLang,"center");
		
        lsLoadable = new javax.swing.JList<>();
        lsLoadable.addListSelectionListener((ListSelectionEvent evt) -> {
			btDownload.setEnabled(true);
		});
		JScrollPane scroller2 = new javax.swing.JScrollPane();
        scroller2.setViewportView(lsLoadable);
		btRefresh = new JButton(I18N.getMsg("spelling.dicts.refresh"));
        btRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/12x12/refresh.png")));
        btRefresh.addActionListener((java.awt.event.ActionEvent evt) -> {
			refreshDicts();
		});
		btDownload = new JButton(I18N.getMsg("spelling.dicts.download"));
        btDownload.setIcon(new ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/arrowdown.png")));
        btDownload.setEnabled(false);
        btDownload.addActionListener((java.awt.event.ActionEvent evt) -> {
			loadDict();
		});
		JPanel panel2 = new javax.swing.JPanel();
        panel2.setBorder(BorderFactory.createTitledBorder(I18N.getMsg("spelling.dicts.downloadable")));
		panel2.setLayout(new MigLayout());
		panel2.add(scroller2,"grow,wrap");
		panel2.add(btRefresh,"split 2,center");
		panel2.add(btDownload);

		JButton btUserDict = new JButton(I18N.getMsg("jortho.userDictionary"));
        btUserDict.addActionListener((java.awt.event.ActionEvent evt) -> {
			EditDictionaryDlg dlg = new EditDictionaryDlg(mainFrame);
			dlg.setVisible(true);
		});
		
		//layout
		setLayout(new MigLayout("wrap 2","[][]","[][]"));
		setTitle("Spell checker");
		add(panel1); add(panel2);
		add(btUserDict,"left");
		add(getCloseButton(),"right");
		pack();
		setLocationRelativeTo(mainFrame);
		this.setModal(true);		
	}
	
	@SuppressWarnings("unchecked")
	private void initInstalled() {
		DefaultListModel model = new DefaultListModel();
		installed = SpellCheckerUtil.getAllDicts();
		String curLanguage = mainFrame.getPref().getString(SbPref.Key.SPELLING, SbConstants.Spelling.none.name());
		int selLanguage = -1;
		int i = 0;
		for (SpellCheckerUtil.Language lang : languages) {
			if (installed.contains(lang.getCode())) {
				model.addElement(lang.getCode() + "=" + lang.getName());
				if (lang.getCode().equals(curLanguage.substring(0, 2))) {
					selLanguage = i;
				}
				i++;
			}
		}
		lsInstalled.setModel(model);
		lsInstalled.setSelectedIndex(selLanguage);
	}
	
	private void applySettings() {
		String i = lsInstalled.getSelectedValue();
		for (SpellCheckerUtil.Language lang : languages) {
			if (i.equals(lang.getCode() + "=" + lang.getName())) {
				mainFrame.getPref().set(SbPref.Key.SPELLING.toString(), lang.getCode() + "," + lang.toString());
				break;
			}
		}
		SpellCheckerUtil.registerDictionaries();
		dispose();
	}

	private void loadDict() {
		//Create all components
		frmMain = new JFrame();
		frmMain.setUndecorated(true);
		frmMain.getRootPane().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
			BorderFactory.createRaisedBevelBorder()));
		frmMain.setTitle(I18N.getMsg("dlg.build.build.progress"));
		frmMain.setLocationRelativeTo(this);
		frmMain.setSize(310, 100);
		pane = frmMain.getContentPane();
		pane.setLayout(null); //Use the null layout
		frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit when X is clicked
		btnCancelLoad = new JButton("Cancel");
		barDo = new JProgressBar(0, 100); //Min value: 0 Max value: 100
		txTitle = new JLabel("");

		//Add components to pane
		pane.add(txTitle);
		pane.add(btnCancelLoad);
		pane.add(barDo);

		//Position controls (X, Y, width, height)
		txTitle.setBounds(10, 10, 280, 20);
		txTitle.setHorizontalAlignment(CENTER);
		barDo.setBounds(10, 30, 280, 20);
		btnCancelLoad.setBounds(100, 55, 100, 25);

		//Make frame visible
		frmMain.setResizable(false); //No resize
		frmMain.setVisible(true);

		//Add action listeners
		btnCancelLoad.addActionListener(new SpellDlg.btnCancelLoadAction()); //Add the button's action
		frmMain.setAlwaysOnTop(true);
		new Thread(new SpellDlg.TheThread()).start();
	}

	public static class btnCancelLoadAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			isLoadCanceled = true;
		}

	}

	@SuppressWarnings("unchecked")
	private void refreshDicts() {
		URL url;
		try {
			url = new URL(SbConstants.URL.SPELL_REMOTE_DIR.toString());
		} catch (MalformedURLException ex) {
			Logger.getLogger(SpellDlg.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}
		String dico = "dictionary_";
		String remote = "";
		try (@SuppressWarnings("null") BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains(dico) && inputLine.contains("ortho")) {
					String str = inputLine.substring(inputLine.indexOf(dico));
					str = str.substring(0, str.indexOf(".ortho"));
					str = str.replace(dico, "");
					//str=str.replace(".ortho", "");
					//if (!installed.contains(str))
					remote += str + ",";
				}
			}
			in.close();
		} catch (IOException ex) {
			Logger.getLogger(SpellDlg.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}
		if (remote.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				I18N.getMsg("spelling.no.more"),
				I18N.getMsg("warning"),
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		DefaultListModel model = new DefaultListModel();
		for (SpellCheckerUtil.Language l : languages) {
			if (remote.contains(l.getCode())) {
				//model.addElement(l.toString());
				model.addElement(l.getCode() + "=" + l.getName());
			}
		}
		lsLoadable.setModel(model);
	}

	public static class TheThread implements Runnable {

		@Override
		public void run() {
			int n = lsLoadable.getSelectedIndex();
			if (n == -1) {
				return;
			}
			String i = lsLoadable.getSelectedValue();
			String i2 = i.substring(0, 2);

			SpellCheckerUtil.Language language = null;
			for (SpellCheckerUtil.Language l : languages) {
				if (i2.equals(l.getCode())) {
					language = l;
					break;
				}
			}
			if (language == null) {
				return;
			}
			String dico = "dictionary_" + language.getCode() + ".ortho";
			try {
				urlDico = new URL(SbConstants.URL.SPELL_REMOTE_DIR.toString() + dico);
				File dir = EnvUtil.getPrefDir();
				destDico = new File(
					dir.getPath() + File.separator + SbConstants.SpellCheker.USER_DICTS + File.separator + dico);
				SbApp.trace("url=" + urlDico.toString() + "\ndest=" + destDico.getAbsolutePath());
				URLConnection connection = urlDico.openConnection();
				int total = connection.getContentLength();
				barDo.setMaximum(total);
				barDo.setStringPainted(true);
				SbApp.trace("url content size="+total);
				BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
				try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destDico))) {
					int j = 0;
					int curent = 0;
					int bytesRead;
					barDo.setString(curent + "/" + total);
					byte[] buffer = new byte[1024];
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						if (isLoadCanceled) {
							break;
						}
						outputStream.write(buffer,0,bytesRead);
						curent+=bytesRead;
						barDo.setString(curent + "/" + total);
						barDo.setValue(curent);
						barDo.repaint(); //Refresh graphics
						j = 0;
						Thread.sleep(1);
					}
					outputStream.flush();
					outputStream.close();
					SbApp.trace("toal downloaded="+curent);
				} catch (InterruptedException ex) {
					System.err.println(ex.getLocalizedMessage());
					System.err.println(Arrays.toString(ex.getStackTrace()));
				}
				if (isLoadCanceled) {
					if (destDico.exists()) {
						destDico.delete();
					}
				} else {
					thisDlg.initInstalled();
					thisDlg.repaint();
				}
				frmMain.setVisible(false);
			} catch (MalformedURLException ex) {
				System.err.println(ex.getLocalizedMessage());
				System.err.println(Arrays.toString(ex.getStackTrace()));
			} catch (IOException ex) {
				System.err.println(ex.getLocalizedMessage());
				System.err.println(Arrays.toString(ex.getStackTrace()));
			}
		}

	}

}
