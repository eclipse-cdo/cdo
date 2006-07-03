/***************************************************************************
 * Copyright (c) 2004-2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * This is a timezone conversion utility class.
 */
public class DateHelper
{

  private static final String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
      "Sep", "Oct", "Nov", "Dec"};

  // as SimpleDateFormat is not thread-safe - we have to use ThreadLocal
  private final static ThreadLocal AFTER_SIX = new ThreadLocal()
  {

    protected Object initialValue()
    {
      return new SimpleDateFormat(" yyyy");
    }
  };

  private final static ThreadLocal BEFORE_SIX = new ThreadLocal()
  {

    protected Object initialValue()
    {
      return new SimpleDateFormat("HH:mm");
    }
  };

  /**
   * Get unix style date string.
   */
  public static String getUnixDate(Date date)
  {
    long dateTime = date.getTime();
    if (dateTime < 0)
    {
      return "------------";
    }

    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    String firstPart = MONTHS[cal.get(Calendar.MONTH)] + ' ';

    String dateStr = String.valueOf(cal.get(Calendar.DATE));
    if (dateStr.length() == 1)
    {
      dateStr = ' ' + dateStr;
    }
    firstPart += dateStr + ' ';

    long nowTime = System.currentTimeMillis();
    if (Math.abs(nowTime - dateTime) > 183L * 24L * 60L * 60L * 1000L)
    {
      DateFormat fmt = (DateFormat) AFTER_SIX.get();
      return firstPart + fmt.format(date);
    }
    else
    {
      DateFormat fmt = (DateFormat) BEFORE_SIX.get();
      return firstPart + fmt.format(date);
    }
  }

  /**
   * Get the timezone specific string.
   */
  public static String getString(Date dt, DateFormat df, TimeZone to)
  {
    df.setTimeZone(to);
    return df.format(dt);
  }

  /**
   * Get the timezone specific calendar.
   */
  public static Calendar getCalendar(Date dt, TimeZone to)
  {
    Calendar cal = Calendar.getInstance(to);
    cal.setTime(dt);
    return cal;
  }

  /**
   * Get date object.
   */
  public static Date getDate(String str, DateFormat df, TimeZone from)
      throws java.text.ParseException
  {
    df.setTimeZone(from);
    return df.parse(str);
  }

  /**
   * Get date difference => d1 - d2.
   */
  public static String getDifference(Date d1, Date d2)
  {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(d2);
    int year2 = calendar.get(Calendar.YEAR);
    int day2 = calendar.get(Calendar.DAY_OF_YEAR);
    int hour2 = calendar.get(Calendar.HOUR_OF_DAY);
    int min2 = calendar.get(Calendar.MINUTE);

    calendar.setTime(d1);
    int year1 = calendar.get(Calendar.YEAR);
    int day1 = calendar.get(Calendar.DAY_OF_YEAR);
    int hour1 = calendar.get(Calendar.HOUR_OF_DAY);
    int min1 = calendar.get(Calendar.MINUTE);

    int leftDays = (day1 - day2) + (year1 - year2) * 365;
    int leftHours = hour1 - hour2;
    int leftMins = min1 - min2;

    if (leftMins < 0)
    {
      leftMins += 60;
      --leftHours;
    }
    if (leftHours < 0)
    {
      leftHours += 24;
      --leftDays;
    }

    String interval = "";
    if (leftDays > 0)
    {
      interval = leftDays + " Days";
    }
    else if ((leftHours > 0) && (leftDays == 0))
    {
      interval = leftHours + " Hours";
    }
    else if ((leftMins > 0) && (leftHours == 0) && (leftDays == 0))
    {
      interval = leftMins + " Minutes";
    }
    else
    {
      interval = "";
    }
    return interval;
  }

}
