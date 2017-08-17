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
package storybook.ui.dialog.chooser;

import java.awt.Color;
import java.awt.Dimension;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import jdatechooser.calendar.JDateChooser;
import jdatechooser.calendar.JTextFieldDateEditor;

import org.apache.commons.text.time.DateUtils;

import org.miginfocom.swing.MigLayout;

import storybook.i18n.I18N;
import storybook.model.EntityUtil;
import storybook.toolkit.DateUtil;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class DateChooserPanel extends JPanel {

	private final MainFrame mainFrame;
	private JDateChooser dateChooser;
	private JSpinner timeSpinner;
	private JButton btPrevDay;
	private JButton btNextDay;
	private JButton btLastDate;
	private JButton btFirstDate;
	private JButton btClearTime;
	
	public DateChooserPanel(MainFrame parent) {
		mainFrame=parent;
		init();
	}
	
	private void init() {
        dateChooser = new JDateChooser();
        timeSpinner = new javax.swing.JSpinner();
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, I18N.TIME_FORMAT);
		timeSpinner.setEditor(timeEditor);
		timeSpinner.setValue(DateUtil.getZeroTimeDate());
		timeSpinner.setPreferredSize(new Dimension(80, 30));
		btPrevDay = new javax.swing.JButton();
        btPrevDay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/previous.png"))); // NOI18N
        btPrevDay.addActionListener((java.awt.event.ActionEvent evt) -> {
			Date date;
			if (dateChooser.getDate() == null) {
				date = EntityUtil.findFirstDate(mainFrame);
			} else {
				date = DateUtils.addDays(dateChooser.getDate(), -1);
			}
			dateChooser.setDate(date);
		});

		btNextDay = new javax.swing.JButton();
        btNextDay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/next.png"))); // NOI18N
        btNextDay.addActionListener((java.awt.event.ActionEvent evt) -> {
			Date date;
			if (dateChooser.getDate() == null) {
				date = EntityUtil.findLastDate(mainFrame);
			} else {
				date = DateUtils.addDays(dateChooser.getDate(), 1);
			}
			dateChooser.setDate(date);
		});

		btLastDate = new javax.swing.JButton();
        btLastDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/last.png"))); // NOI18N
        btLastDate.addActionListener((java.awt.event.ActionEvent evt) -> {
			Date date = EntityUtil.findLastDate(mainFrame);
			dateChooser.setDate(date);
		});

		btFirstDate = new javax.swing.JButton();
        btFirstDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/first.png"))); // NOI18N
        btFirstDate.addActionListener((java.awt.event.ActionEvent evt) -> {
			Date date = EntityUtil.findFirstDate(mainFrame);
			dateChooser.setDate(date);
		});

		btClearTime = new javax.swing.JButton();
        btClearTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/clear.png"))); // NOI18N
        btClearTime.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btClearTime.addActionListener((java.awt.event.ActionEvent evt) -> {
			timeSpinner.setValue(DateUtil.getZeroTimeDate());
		});
		
		//layout
		setLayout(new MigLayout("insets 0"));
		add(dateChooser);
		add(btFirstDate);
		add(btPrevDay);
		add(btNextDay);
		add(btLastDate);
		add(timeSpinner);
		add(btClearTime);
	}
	
	public void setDate(Date date) {
		dateChooser.setDate(date);
		if (date != null) {
			if (timeSpinner.isVisible()) timeSpinner.setValue(date);
		}
	}
	
	public Date getDate() {
		return(dateChooser.getDate());
	}

	public Timestamp getTimestamp() {
		if (dateChooser.getDate() == null) {
			return null;
		}
		Date date = dateChooser.getDate();
		Date time = (Date) timeSpinner.getValue();
		return DateUtil.addTimeFromDate(date, time);
	}
	
	public void hideTime(){
		timeSpinner.setVisible(false);
		btClearTime.setVisible(false);
	}
	
	public void hideButtons() {
		btFirstDate.setVisible(false);
		btLastDate.setVisible(false);
		btPrevDay.setVisible(false);
		btNextDay.setVisible(false);

	}
	
	public void showOnlyDate() {
		hideTime();
		hideButtons();
	}

	public boolean hasError() {
		JTextFieldDateEditor tf = (JTextFieldDateEditor) dateChooser.getComponent(1);
		return tf.getForeground() == Color.red;
	}

	@Override
	public void setEnabled(boolean enabled) {
		dateChooser.setEnabled(enabled);
		timeSpinner.setEnabled(enabled);
		btClearTime.setEnabled(enabled);
		btFirstDate.setEnabled(enabled);
		btLastDate.setEnabled(enabled);
		btPrevDay.setEnabled(enabled);
		btNextDay.setEnabled(enabled);
	}
}
