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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.text.exception.ExceptionUtils;

import org.miginfocom.swing.MigLayout;

import storybook.i18n.I18N;
import storybook.toolkit.html.HtmlSelection;

/**
 *
 * @author favdb
 */
public class ExceptionDlg extends AbstractDialog {

	private final String msg;
	private final Exception e;
	private JTextArea ta;
	
	public ExceptionDlg() {
		super();
		this.msg="";
		this.e = null;
	}
	
	public ExceptionDlg(Exception e) {
		super();
		this.msg="";
		this.e = e;
		initAll();
	}
	
	public ExceptionDlg(String msg, Exception e) {
		super();
		this.msg=msg;
		this.e = e;
		initAll();
	}
	
	@Override
	public void init() {
		super.initUi();
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("flowx"));
		StringBuilder buf = new StringBuilder();
		if (!msg.isEmpty()) {
			buf.append(msg).append("\n\n");
		}
		if (e!=null) {
			buf.append("Exception Message:\n");
			buf.append(e.getLocalizedMessage());
			buf.append("\n\nStack Trace:\n");
			buf.append(ExceptionUtils.getStackTrace(e));
		}
		buf.append(I18N.getMsg("exception.to.report"));
		ta=new JTextArea();
		ta.setEditable(false);
        ta.setColumns(20);
        ta.setRows(5);
		ta.setText(buf.toString());
		ta.setCaretPosition(0);
		JScrollPane scroller = new JScrollPane();
        scroller.setViewportView(ta);
		scroller.setMinimumSize(new Dimension(676,430));
		add(scroller,"grow 100, wrap");
		JButton btCopy=new JButton(I18N.getMsg("file.info.copy.text"));
		btCopy.addActionListener((java.awt.event.ActionEvent evt) -> {
			HtmlSelection selection = new HtmlSelection(ta.getText());
			Clipboard clbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clbrd.setContents(selection, selection);
			JOptionPane.showMessageDialog(this,
				I18N.getMsg("copied.title"),
				"Exception",
				JOptionPane.INFORMATION_MESSAGE);
		});
		JButton btExit=new JButton(I18N.getMsg("close"));
		btExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/close.png")));
        btExit.addActionListener((java.awt.event.ActionEvent evt) -> {
			dispose();
		});
		add(btCopy,"split 2, right"); add(btExit);
		setTitle("Exception");
		setIconImage(I18N.getIconImage("icon.small.error"));
		pack();
		setLocationRelativeTo(null);
	}
	
	public static void show(String msg, Exception ex) {
		new ExceptionDlg(msg,ex).setVisible(true);
	}
	
}
