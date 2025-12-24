/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public final class TimeStamp extends Number implements Comparable<TimeStamp>
{
  public static final TimeStamp UNSPECIFIED_DATE = new TimeStamp(CDOBranchPoint.UNSPECIFIED_DATE);

  private static final Pattern PATTERN = Pattern.compile("(\\d{8}|\\d{6}|\\d{4})(-(\\d{2}(\\d{2}(\\d{2})?)?)(-(\\d{1,3}))?)?");

  private static final int NANOS_PER_MILLI = 1000 * 1000;

  private static final ZoneId ZONE_ID = ZoneId.systemDefault();

  private static final long serialVersionUID = 1L;

  private final long value;

  public TimeStamp()
  {
    this(System.currentTimeMillis());
  }

  public TimeStamp(String string) throws DateTimeException
  {
    this(parseTimeStamp(string));
  }

  public TimeStamp(long value)
  {
    this.value = value;
  }

  @Override
  public long longValue()
  {
    return value;
  }

  @Override
  public int intValue()
  {
    return (int)value;
  }

  @Override
  public float floatValue()
  {
    return value;
  }

  @Override
  public double doubleValue()
  {
    return value;
  }

  @Override
  public byte byteValue()
  {
    return (byte)value;
  }

  @Override
  public short shortValue()
  {
    return (short)value;
  }

  @Override
  public int compareTo(TimeStamp obj)
  {
    return compare(value, obj.value);
  }

  @Override
  public int hashCode()
  {
    return hashCode(value);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof TimeStamp)
    {
      return value == ((TimeStamp)obj).longValue();
    }

    return false;
  }

  @Override
  public String toString()
  {
    return toString(value);
  }

  private static void pad(StringBuilder builder, int value, int length)
  {
    String string = Integer.toString(value);

    int fill = length - string.length();
    for (int i = 0; i < fill; i++)
    {
      builder.append('0');
    }

    builder.append(string);
  }

  private static String exceptionMessage(String string)
  {
    return "Invalid time stamp format: \"" + string + "\"";
  }

  public static int compare(long x, long y)
  {
    return Long.compare(x, y);
  }

  public static int hashCode(long value)
  {
    return Long.hashCode(value);
  }

  public static String toString(long value)
  {
    LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZONE_ID);

    StringBuilder builder = new StringBuilder(19);
    pad(builder, localDateTime.getYear(), 4);
    pad(builder, localDateTime.getMonthValue(), 2);
    pad(builder, localDateTime.getDayOfMonth(), 2);
    builder.append('-');
    pad(builder, localDateTime.getHour(), 2);
    pad(builder, localDateTime.getMinute(), 2);
    pad(builder, localDateTime.getSecond(), 2);
    builder.append('-');
    pad(builder, localDateTime.getNano() / NANOS_PER_MILLI, 3);

    return builder.toString();
  }

  public static long parseTimeStamp(String string) throws DateTimeException
  {
    Matcher matcher = PATTERN.matcher(string);
    if (!matcher.matches())
    {
      throw new DateTimeException(exceptionMessage(string));
    }

    try
    {
      String date = matcher.group(1);
      if (date.length() == 4)
      {
        date = Integer.toString(LocalDate.now().getYear()) + date;
      }
      else if (date.length() == 6)
      {
        date = Integer.toString(LocalDate.now().getYear() / 100) + date;
      }

      String time = matcher.group(3);
      if (time == null)
      {
        time = "000000";
      }
      else if (time.length() == 2)
      {
        time += "0000";
      }
      else if (time.length() == 4)
      {
        time += "00";
      }

      String millis = matcher.group(7);
      if (millis == null)
      {
        millis = "000";
      }
      else if (millis.length() == 1)
      {
        millis = "00" + millis;
      }
      else if (millis.length() == 2)
      {
        millis = "0" + millis;
      }

      int year = Integer.parseInt(date.substring(0, 4));
      int month = Integer.parseInt(date.substring(4, 6));
      int day = Integer.parseInt(date.substring(6, 8));
      LocalDate localDate = LocalDate.of(year, month, day);

      int hour = Integer.parseInt(time.substring(0, 2));
      int minute = Integer.parseInt(time.substring(2, 4));
      int second = Integer.parseInt(time.substring(4, 6));
      int nanos = Integer.parseInt(millis) * NANOS_PER_MILLI;
      LocalTime localTime = LocalTime.of(hour, minute, second, nanos);

      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      ZonedDateTime zonedDateTime = localDateTime.atZone(ZONE_ID);
      return zonedDateTime.toInstant().toEpochMilli();
    }
    catch (DateTimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new DateTimeException(exceptionMessage(string), ex);
    }
  }
}
