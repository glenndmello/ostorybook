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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import org.miginfocom.swing.MigLayout;
import storybook.SbConstants;
import storybook.i18n.I18N;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class ChooseFileDlg extends AbstractDialog {

	protected File file;
	private boolean forceExt = true;
	private String defaultExt = "mv.db";
	private boolean askForOverwrite;

	private JTextField tfName;
	private JTextField tfDir;
	private JButton btChooseDir;
	private JLabel lbWarning;

	public ChooseFileDlg(MainFrame m, boolean overwrite) {
		super(m);
		askForOverwrite = overwrite;
		init();
	}

	@Override
	public void init() {
		JLabel jLabel1 = new JLabel(I18N.getMsg("manage.projects.project.name"));
		tfName = new javax.swing.JTextField();
		tfName.setColumns(32);
		JLabel jLabel2 = new JLabel(I18N.getMsg("folder"));
		tfDir = new javax.swing.JTextField();
		tfDir.setColumns(32);
		tfDir.setEditable(false);
		btChooseDir = new JButton();
		btChooseDir.setMargin(new Insets(0,0,0,0));
		btChooseDir.setIcon(new ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/file-open.png")));
		btChooseDir.setToolTipText(I18N.getMsg("folder.choose"));
		btChooseDir.addActionListener((java.awt.event.ActionEvent evt) -> {
			btChooseDir();
		});
		lbWarning = new javax.swing.JLabel();
		lbWarning.setForeground(java.awt.Color.red);

		//layout
		MigLayout layout=new MigLayout();
		setLayout(layout);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle(I18N.getMsg("welcome.new.project"));
        setModal(true);
		setLocationRelativeTo(mainFrame);
		
		add(jLabel1,"split 2"); add(tfName,"wrap");
		
		add(jLabel2,"split 3"); add(tfDir); add(btChooseDir,"wrap");
		
		add(getCancelButton(), "split 2, right");
		add(getOkButton(), "right");
		this.pack();

	}

	private void btChooseDir() {
		final JFileChooser fc = new JFileChooser(tfDir.getText());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int ret = fc.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File dir = fc.getSelectedFile();
		tfDir.setText(dir.getAbsolutePath());
		lbWarning.setText(" ");
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applySettings();
			}
		};
	}

	private void applySettings() {
		File dir = new File(tfDir.getText());
		if (!dir.isDirectory() || !dir.canWrite() || !dir.canExecute()) {
			lbWarning.setText(I18N.getMsg("file.new.not.writable"));
			return;
		}
		String name = tfName.getText();
		if (forceExt) {
			String fileExtOld = SbConstants.Storybook.DB_FILE_EXT.toString();
			String fileExt = SbConstants.Storybook.DB_FILE_EXT2.toString();
			if ((!name.endsWith(fileExtOld)) && (!name.endsWith(fileExt))) {
				name += fileExt;
			}
		} else {
			name += defaultExt;
		}
		file = new File(tfDir.getText() + File.separator + name);
		if ((file.exists()) && (askForOverwrite)) {
			int ret = JOptionPane.showConfirmDialog(this,
				I18N.getMsg("file.save.overwrite.text", file.getName()),
				I18N.getMsg("file.save.overwrite.title"),
				JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.NO_OPTION) {
				lbWarning.setText(I18N.getMsg("file.new.file.exists"));
				return;
			}
		} else if (file.exists()) {
			lbWarning.setText(I18N.getMsg("file.new.file.exists"));
			return;
		}
		this.dispose();
	}

	public File getFile() {
		return file;
	}
	
	public void setForceDbExtension(boolean forced) {
		forceExt = forced;
	}
	
	public void setDefaultDBExt(String ext) {
		defaultExt=ext;
	}
	
	public void setDefaultPath(String p) {
		tfDir.setText(p);
	}
	
}
