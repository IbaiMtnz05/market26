package configuration;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class for date manipulation and formatting.
 * Provides methods to trim dates to specific precision and create dates.
 */
public class UtilDate {

	/**
	 * Trims a date to midnight (00:00:00.000) in CET timezone.
	 * 
	 * @param date the date to trim
	 * @return the date with time set to 00:00:00.000
	 */
	public static Date trim(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("CET"));
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return calendar.getTime();
	}
	
	/**
	 * Creates a new date from year, month, and day at midnight.
	 * 
	 * @param year the year
	 * @param month the month (0-11, where 0=January)
	 * @param day the day of the month
	 * @return the created date
	 */
	public static Date newDate(int year,int month,int day) {

	     Calendar calendar = Calendar.getInstance();
		 calendar.setTimeZone(TimeZone.getTimeZone("CET"));
	     calendar.set(year, month, day,0,0,0);
	     calendar.set(Calendar.MILLISECOND, 0);
	     return calendar.getTime();
	}
	
	/**
	 * Returns the first day of the month for the given date at midnight.
	 * 
	 * @param date the date to get the first day of its month
	 * @return the first day of the month at 00:00:00.000
	 */
	public static Date firstDayMonth(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("CET"));
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return calendar.getTime();
	}
	
	/**
	 * Returns the last day of the month for the given date at midnight.
	 * 
	 * @param date the date to get the last day of its month
	 * @return the last day of the month at 00:00:00.000
	 */
	public static Date lastDayMonth(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("CET"));
		//int month=calendar.get(Calendar.MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return calendar.getTime();

	}
	
	
}
