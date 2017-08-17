/*
 * SbApp: Open Source software for novelists and authors.
 * Original idea 2008 - 2012 Martin Mustun
 * Copyrigth (C) Favdb
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
 */
package storybook.ui.chart;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;

import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.infonode.docking.View;

import org.miginfocom.swing.MigLayout;

import storybook.SbConstants;
import storybook.controller.BookController;
import storybook.model.hbn.entity.Internal;
import storybook.toolkit.BookUtil;
import storybook.toolkit.EnvUtil;
import storybook.i18n.I18N;
import storybook.toolkit.IOUtil;
import storybook.toolkit.FileFilter;
import storybook.toolkit.swing.PrintUtil;
import storybook.toolkit.swing.ScreenImage;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

public abstract class AbstractChartPanel extends AbstractPanel implements ActionListener {

	protected JPanel panel;
	protected JPanel optionsPanel;
	protected String chartTitle;
	protected boolean partRelated = false;
	protected boolean needsFullRefresh = false;
	private JScrollPane scroller;
	private AbstractAction exportAction;

	public AbstractChartPanel(MainFrame mainFrame, String param) {
		super(mainFrame);
		this.chartTitle = I18N.getMsg(param);
	}

	protected abstract void initChart();

	protected abstract void initChartUi();

	protected abstract void initOptionsUi();

	@Override
	public void modelPropertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
		Object localObject = paramPropertyChangeEvent.getNewValue();
		String str = paramPropertyChangeEvent.getPropertyName();
		View localView1;
		View localView2;
		if (BookController.CommonProps.REFRESH.check(str)) {
			localView1 = (View) localObject;
			localView2 = (View) getParent().getParent();
			if (localView2 == localView1) {
				refresh();
			}
			return;
		}
		if ((this.partRelated) && (BookController.PartProps.CHANGE.check(str))) {
			if (this.needsFullRefresh) {
				refresh();
			} else {
				refreshChart();
			}
			return;
		}
		if (BookController.CommonProps.EXPORT.check(str)) {
			localView1 = (View) localObject;
			localView2 = (View) getParent().getParent();
			if (localView2 == localView1) {
				getExportAction().actionPerformed(null);
			}
			return;
		}
		if (BookController.CommonProps.PRINT.check(str)) {
			localView1 = (View) localObject;
			localView2 = (View) getParent().getParent();
			if (localView2 == localView1) {
				PrintUtil.printComponent(this);
			}
			return;
		}
	}

	@Override
	public void init() {
		try {
			initChart();
		} catch (Exception e) {
			System.err.println("AbstractChartPanel.init() Exception"+e.getLocalizedMessage());
		}
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("flowy,fill,ins 0", "", ""));
		this.panel = new JPanel(new MigLayout("flowy,fill,ins 2", "", "[][grow][]"));
		this.panel.setBackground(Color.white);
		initChartUi();
		this.optionsPanel = new JPanel(new MigLayout("flowx,fill"));
		this.optionsPanel.setBackground(Color.white);
		this.optionsPanel.setBorder(SwingUtil.getBorderGray());
		initOptionsUi();
		this.scroller = new JScrollPane(this.panel);
		SwingUtil.setMaxPreferredSize(this.scroller);
		add(this.scroller, "grow");
		if (this.optionsPanel.getComponentCount() > 0) {
			add(this.optionsPanel, "growx");
		} else {
			add(new JLabel());
		}
	}

	protected void refreshChart() {
		this.panel.removeAll();
		initChartUi();
		this.panel.revalidate();
		this.panel.repaint();
	}

	private AbstractChartPanel getThis() {
		return this;
	}

	private AbstractAction getExportAction() {
		if (this.exportAction == null) {
			this.exportAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
					try {
						Internal internal = BookUtil.get(AbstractChartPanel.this.mainFrame,
							SbConstants.BookKey.EXPORT_DIRECTORY,
							EnvUtil.getDefaultExportDir(AbstractChartPanel.this.mainFrame));
						File file1 = new File(internal.getStringValue());
						JFileChooser fileChooser = new JFileChooser(file1);
						FileFilter filter=new FileFilter(FileFilter.PNG,I18N.getMsg("file.type.png"));
						fileChooser.setFileFilter(filter);
						fileChooser.setApproveButtonText(I18N.getMsg("export"));
						String str = AbstractChartPanel.this.mainFrame.getDbFile().getName() + " - " + AbstractChartPanel.this.chartTitle;
						str = IOUtil.cleanupFilename(str);
						fileChooser.setSelectedFile(new File(str));
						int i = fileChooser.showDialog(AbstractChartPanel.this.getThis(), I18N.getMsg("export"));
						if (i == 1) {
							return;
						}
						File file2 = fileChooser.getSelectedFile();
						if (!file2.getName().endsWith(".png")) {
							file2 = new File(file2.getPath() + ".png");
						}
						ScreenImage.createImage(AbstractChartPanel.this.panel, file2.toString());
						JOptionPane.showMessageDialog(AbstractChartPanel.this.getThis(),
							I18N.getMsg("export.success"),
							I18N.getMsg("export"), 1);
					} catch (HeadlessException | IOException localException) {
					}
				}
			};
		}
		return this.exportAction;
	}
}