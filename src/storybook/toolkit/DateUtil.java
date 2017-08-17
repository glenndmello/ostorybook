/*
 Storybook: Scene-based software for novelists and authors.
 Copyright (C) 2008-2009 Martin Mustun

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package storybook.toolkit;

import storybook.i18n.I18N;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import storybook.SbApp;
import storybook.SbPref;

public class DateUtil {

	public static String getNiceDates(List<Date> dates) {
		DateFormat formatter = I18N.getLongDateFormatter();
		List<String> dateList = new ArrayList<>();
		for (Date date : dates) {
			dateList.add(formatter.format(date));
		}
		return StringUtils.join(dateList, ", ");
	}

	public static String calendarToString(Calendar cal) {
		SimpleDateFormat formatter = new SimpleDateFormat(
			"E yyyy.MM.dd 'at' hh:mm:ss a zzz");
		return formatter.format(cal.getTime());
	}

	public static Timestamp addTimeFromDate(Date date, Date time) {
		Calendar calTime = Calendar.getInstance();
		calTime.setTime(time);
		int h = calTime.get(Calendar.HOUR_OF_DAY);
		int m = calTime.get(Calendar.MINUTE);
		int s = calTime.get(Calendar.SECOND);
		date = DateUtil.setHours(date, h);
		date = DateUtil.setMinutes(date, m);
		date = DateUtil.setSeconds(date, s);
		return new Timestamp(date.getTime());
	}

	public static Date getZeroTimeDate() {
		Calendar cal = Calendar.getInstance();
		clearTime(cal);
		return cal.getTime();
	}

	public static boolean isZeroTimeDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY) == 0
			&& cal.get(Calendar.MINUTE) == 0
			&& cal.get(Calendar.SECOND) == 0
			&& cal.get(Calendar.MILLISECOND) == 0;
	}

	public static Date getZeroTimeDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		clearTime(cal);
		return cal.getTime();
	}

	public static int calculateDaysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

	public static void expandDates(List<Date> dates) {
		expandDates(dates, 1, 1);
	}

	public static void expandDates(List<Date> dates, int count) {
		expandDates(dates, count, count);
	}

	public static void expandDates(List<Date> dates, int countPast, int countFuture) {
		expandDatesToPast(dates, countPast);
		expandDatesToFuture(dates, countFuture);
	}

	public static void expandDatesToFuture(List<Date> dates) {
		expandDatesToFuture(dates, 1);
	}

	public static void expandDatesToFuture(List<Date> dates, int count) {
		dates.removeAll(Collections.singletonList(null));
		if (dates.isEmpty()) {
			return;
		}
		for (int i = 0; i < count; ++i) {
			Date lastDate = Collections.max(dates);
			if (lastDate == null) {
				return;
			}
			lastDate = new Date(DateUtil.addDays(lastDate, 1).getTime());
			dates.add(lastDate);
		}
	}

	public static void expandDatesToPast(List<Date> dates) {
		expandDatesToPast(dates, 1);
	}

	public static void expandDatesToPast(List<Date> dates, int count) {
		if (dates.isEmpty()) {
			return;
		}
		dates.removeAll(Collections.singletonList(null));
		for (int i = 0; i < count; ++i) {
			Date firstDate = Collections.min(dates);
			firstDate = new Date(DateUtil.addDays(firstDate, -1).getTime());
			dates.add(firstDate);
		}
	}

	public static Date stringToDate(String str) {
		Date d = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			d = formatter.parse(str, pos);
		} catch (RuntimeException e) {
		}
		return d;
	}

	public static Date stdStringToDate(String str) {
		Date d = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			d = formatter.parse(str, pos);
		} catch (RuntimeException e) {
		}
		return d;
	}

	public static Timestamp stdStringToTimestamp(String str) {
		Timestamp d = null;
		try {
			Date dt=stdStringToDate(str);
			d=new Timestamp(dt.getTime());
		} catch (RuntimeException e) {
		}
		return d;
	}

	public static String dateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		return (formatter.format(date));
	}

	public static String timeToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		return (formatter.format(date));
	}

	public static String simpleDateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(SbApp.getInstance().preferences.getString(SbPref.Key.DATEFORMAT, "MM-dd-yyyy"));
		return (formatter.format(date));
	}

	public static String simpleDateTimeToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(SbApp.getInstance().preferences.getString(SbPref.Key.DATEFORMAT, "MM-dd-yyyy") + " HH:mm:ss");
		return (formatter.format(date));
	}

	public static DateFormat simpleDateToString() {
		SimpleDateFormat formatter = new SimpleDateFormat(SbApp.getInstance().preferences.getString(SbPref.Key.DATEFORMAT, "MM-dd-yyyy"));
		return (formatter);
	}

	/**
	 *
	 * @param cal the value of cal
	 * @return calendar
	 */
	public static Calendar clearTime(Calendar cal) {
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	private static String dateToStandard(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return (formatter.format(date));

	}

	public static long toMinutes(Date date) {
		if (date == null) {
			return (0L);
		}
		long r = 0L;
		String[] d = dateToStandard(date).split(" ");
		int year = getYear(date);
		int month = getMonth(date);
		int day = getDay(date);
		int hh = getHour(date);
		int mm = getMinute(date);
		r = year * (365 * 24 * 60);
		r += howManyDays(month) * (24 * 60);
		r += day * (24 * 60);
		r += hh * (60);
		r += mm;
		return r;
	}

	public static int howManyDays(int month) {
		int r=0;
		int[] dm = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		for (int i = 0; i < month-1; i++) {
			r += dm[i];
		}
		return (r);
	}

	public static int getYear(Date date) {
		String[] d = dateToStandard(date).split(" ");
		String[] dx = d[0].split("-");
		return (Integer.parseInt(dx[0]));
	}

	public static int getMonth(Date date) {
		String[] d = dateToStandard(date).split(" ");
		String[] dx = d[0].split("-");
		return (Integer.parseInt(dx[1]));
	}

	public static int getDay(Date date) {
		String[] d = dateToStandard(date).split(" ");
		String[] dx = d[0].split("-");
		return (Integer.parseInt(dx[2]));
	}

	public static int getHour(Date date) {
		String[] d = dateToStandard(date).split(" ");
		String[] dx = d[1].split(":");
		return (Integer.parseInt(dx[0]));
	}

	public static int getMinute(Date date) {
		String[] d = dateToStandard(date).split(" ");
		String[] dx = d[1].split(":");
		return (Integer.parseInt(dx[1]));
	}
	
	/*
	From org.apache.commons.lang3
	*/

    //-----------------------------------------------------------------------
    /**
     * Adds a number of years to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addYears(final Date date, final int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a number of months to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a number of weeks to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addWeeks(final Date date, final int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a number of days to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a number of hours to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a number of minutes to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a number of seconds to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addSeconds(final Date date, final int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a number of milliseconds to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addMilliseconds(final Date date, final int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param calendarField  the calendar field to add to
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    private static Date add(final Date date, final int calendarField, final int amount) {
		if (date == null) {
			throw new IllegalArgumentException(String.format("Date most not be null"));
		}
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the years field to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount the amount to set
     * @return a new {@code Date} set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    public static Date setYears(final Date date, final int amount) {
        return set(date, Calendar.YEAR, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the months field to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount the amount to set
     * @return a new {@code Date} set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    public static Date setMonths(final Date date, final int amount) {
        return set(date, Calendar.MONTH, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the day of month field to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount the amount to set
     * @return a new {@code Date} set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    public static Date setDays(final Date date, final int amount) {
        return set(date, Calendar.DAY_OF_MONTH, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the hours field to a date returning a new object.  Hours range
     * from  0-23.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount the amount to set
     * @return a new {@code Date} set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    public static Date setHours(final Date date, final int amount) {
        return set(date, Calendar.HOUR_OF_DAY, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the minute field to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount the amount to set
     * @return a new {@code Date} set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    public static Date setMinutes(final Date date, final int amount) {
        return set(date, Calendar.MINUTE, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the seconds field to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount the amount to set
     * @return a new {@code Date} set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    public static Date setSeconds(final Date date, final int amount) {
        return set(date, Calendar.SECOND, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the milliseconds field to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount the amount to set
     * @return a new {@code Date} set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    public static Date setMilliseconds(final Date date, final int amount) {
        return set(date, Calendar.MILLISECOND, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the specified field to a date returning a new object.
     * This does not use a lenient calendar.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param calendarField  the {@code Calendar} field to set the amount to
     * @param amount the amount to set
     * @return a new {@code Date} set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    private static Date set(final Date date, final int calendarField, final int amount) {
		if (date == null) {
			throw new IllegalArgumentException(String.format("Date most not be null"));
		}
        // getInstance() returns a new object, so this method is thread safe.
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }

}
