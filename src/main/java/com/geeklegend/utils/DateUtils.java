package com.geeklegend.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	public static Date getDate(long timeMillis) {
		return new Date(timeMillis);
	}

	public static boolean isNextDay(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
				&& calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
	}

	public static long getNextDay(int days) {
		final long currentTimeAsMs = System.currentTimeMillis();
		final long adjustedTimeAsMs = currentTimeAsMs + (1000 * 60 * 60 * 24 * days);
		return adjustedTimeAsMs;
	}

	public static int getDays(long timeMillis) {
		long dateBeforeInMs = getCurrentDate().getTime();
		long dateAfterInMs = getDate(timeMillis).getTime();
		long timeDiff = Math.abs(dateAfterInMs - dateBeforeInMs);
		long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
		return (int) daysDiff;
	}

}