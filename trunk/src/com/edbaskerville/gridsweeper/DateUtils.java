package com.edbaskerville.gridsweeper;

import java.util.Calendar;
import static java.lang.String.format;

public class DateUtils
{
	public static String getDateString(Calendar cal)
	{
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		return format("%d-%02d-%02d", year, month, day);
	}
	
	public static String getTimeString(Calendar cal)
	{
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		
		return format("%02d-%02d-%02d", hour, minute, second);
	}
}
