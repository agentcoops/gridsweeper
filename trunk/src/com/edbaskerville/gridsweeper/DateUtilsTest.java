package com.edbaskerville.gridsweeper;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import static com.edbaskerville.gridsweeper.DateUtils.*;

public class DateUtilsTest
{
	private Calendar cal;
	
	@Before
	public void setUp()
	{
		cal = new GregorianCalendar(2006, 9, 17);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 5);
		cal.set(Calendar.SECOND, 25);
	}
	
	@Test
	public void dateString()
	{
		assertEquals("2006-09-17", getDateString(cal));
	}
	
	@Test
	public void timeString()
	{
		assertEquals("12-05-25", getTimeString(cal));	
	}
}
