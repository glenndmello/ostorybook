package storybook.ui.panel.chrono;

import java.awt.Color;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import storybook.i18n.I18N;
import storybook.toolkit.DateUtil;
import storybook.toolkit.swing.SwingUtil;

@SuppressWarnings("serial")
public class DateLabel extends JLabel {

	private Date chapter;

	public DateLabel(Date chapter) {
		super();
		this.chapter = chapter;
		setText(getDateText());
		setToolTipText(getDateText());
		setIcon(I18N.getIcon("icon.small.chrono.view"));
		setBackground(new Color(240, 240, 240));
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	public final String getDateText() {
		if (chapter == null) {
			return "";
		}
		String dateStr = DateUtil.simpleDateTimeToString(chapter);
		String dayStr = SwingUtil.getDayName(chapter);
		return dayStr + " - " + dateStr;
	}

	public Date getDate() {
		return chapter;
	}

	public void setDate(Date date) {
		this.chapter = date;
	}
}
