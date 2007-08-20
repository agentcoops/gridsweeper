/*
	DateUtils.java
	
	Part of GridSweeper
	Copyright (c) 2006 - 2007 Ed Baskerville <software@edbaskerville.com>

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package edu.umich.lsa.cscs.gridsweeper;

import java.util.Calendar;
import static java.lang.String.format;

/**
 * A utility class with methods for converting date objects
 * into strings for use in directory names, etc.
 * @author Ed Baskerville
 *
 */
public class DateUtils
{
	/**
	 * Converts a {@code java.util.Calendar} object to a string representation
	 * of the date represented, in the format YYYY-MM-DD.
	 * @param cal The {@code Calendar} object representing the date.
	 * @return The string representation of the date.
	 */
	public static String getDateString(Calendar cal)
	{
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		return format("%d-%02d-%02d", year, month, day);
	}
	
	/**
	 * Converts a @{code java.util.Calendar} object to a string representation
	 * of the time represented, in the format HH-MM-SS.
	 * @param cal
	 * @return The string representation of the time.
	 */
	public static String getTimeString(Calendar cal)
	{
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		
		return format("%02d-%02d-%02d", hour, minute, second);
	}
}
