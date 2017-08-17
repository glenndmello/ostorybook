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
package storybook.exim.exporter.options;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.miginfocom.swing.MigLayout;
import storybook.exim.exporter.ParamExport;
import storybook.i18n.I18N;
import storybook.toolkit.EnvUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class HTMLpanel extends JPanel {

	public JButton btCssFile;
	public JTextField txCssFile;
	public JCheckBox cbNavImage;
	public JCheckBox cbUseCss;
	private final MainFrame mainFrame;
	public JCheckBox ckExportChapterBreakPage;

	public HTMLpanel(ParamExport param) {
		mainFrame = param.mainFrame;

		cbUseCss = new JCheckBox(I18N.getMsg("export.options.html.css"));
		cbUseCss.setSelected(param.htmlCssUse);
		cbUseCss.addItemListener((java.awt.event.ItemEvent evt) -> {
			htmlUseCssChanged(evt);
		});

		txCssFile = new JTextField();
		txCssFile.setColumns(32);
		txCssFile.setEnabled(false);

		btCssFile = new JButton();
		btCssFile.setMargin(new Insets(0,0,0,0));
		btCssFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/file-open.png"))); // NOI18N
		btCssFile.addActionListener((java.awt.event.ActionEvent evt) -> {
			ChooseCssFile(evt);
		});
		if (param.htmlCssUse) {
			txCssFile.setText(param.htmlCssFile);
			txCssFile.setEnabled(true);
			btCssFile.setEnabled(true);
		} else {
			txCssFile.setEnabled(false);
			btCssFile.setEnabled(false);
		}

		cbNavImage = new JCheckBox(I18N.getMsg("export.options.html.navimage"));
		cbNavImage.setSelected(param.htmlNavImage);

		ckExportChapterBreakPage = new JCheckBox(I18N.getMsg("export.chapter.break_page"));
		ckExportChapterBreakPage.setSelected(param.isExportChapterBreakPage);
		
		//layout
		setLayout(new MigLayout("", "", ""));
		add(cbUseCss,"wrap");
		add(txCssFile,"split 2"); add(btCssFile,"wrap");
		add(cbNavImage,"wrap");
		add(ckExportChapterBreakPage,"wrap");
	}

	private void htmlUseCssChanged(ItemEvent evt) {
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			txCssFile.setEnabled(true);
			btCssFile.setEnabled(true);
		} else {
			txCssFile.setEnabled(false);
			btCssFile.setEnabled(false);
		}
	}

	private void ChooseCssFile(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser(txCssFile.getText());
		if (txCssFile.getText().isEmpty()) {
			chooser.setCurrentDirectory(new File(EnvUtil.getDefaultExportDir(mainFrame)));
		}
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new CssFileFilter());
		int i = chooser.showOpenDialog(this);
		if (i != 0) {
			return;
		}
		File file = chooser.getSelectedFile();
		txCssFile.setText(file.getAbsolutePath());
		txCssFile.setBackground(Color.WHITE);
	}

	public class CssFileFilter extends javax.swing.filechooser.FileFilter {

		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			}
			String filename = file.getName();
			return filename.endsWith(".css") || filename.endsWith(".css");
		}

		@Override
		public String getDescription() {
			return "CSS Files (*.css)";
		}
	}
}
