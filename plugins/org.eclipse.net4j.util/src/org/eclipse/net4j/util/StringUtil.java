/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;

/**
 * @author Eike Stepper
 */
public final class StringUtil
{
  public static final String EMPTY = "";

  public static final String NL = System.getProperty("line.separator"); //$NON-NLS-1$

  private StringUtil()
  {
  }

  public static String replace(String text, String[] find, String[] replace)
  {
    for (int i = 0; i < find.length; i++)
    {
      int end = 0;
      for (;;)
      {
        int start = text.indexOf(find[i], end);
        if (start == -1)
        {
          break;
        }

        end = start + find[i].length();
        text = text.substring(0, start) + replace[i] + text.substring(end);
      }
    }

    return text;
  }

  public static String safe(String str)
  {
    if (str == null)
    {
      return EMPTY;
    }

    return str;
  }

  public static int compare(String s1, String s2)
  {
    if (s1 == null)
    {
      return s2 == null ? 0 : -1;
    }

    if (s2 == null)
    {
      return 1;
    }

    return s1.compareTo(s2);
  }

  public static String cap(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    char first = str.charAt(0);
    if (Character.isUpperCase(first))
    {
      return str;
    }

    if (str.length() == 1)
    {
      return str.toUpperCase();
    }

    StringBuilder builder = new StringBuilder(str);
    builder.setCharAt(0, Character.toUpperCase(first));
    return builder.toString();
  }

  public static String uncap(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    char first = str.charAt(0);
    if (Character.isLowerCase(first))
    {
      return str;
    }

    if (str.length() == 1)
    {
      return str.toLowerCase();
    }

    StringBuilder builder = new StringBuilder(str);
    builder.setCharAt(0, Character.toLowerCase(first));
    return builder.toString();
  }

  public static int occurrences(String str, char c)
  {
    int count = 0;
    for (int i = 0; (i = str.indexOf(c, i)) != -1; ++i)
    {
      ++count;
    }

    return count;
  }

  public static int occurrences(String str, String c)
  {
    int count = 0;
    for (int i = 0; (i = str.indexOf(c, i)) != -1; i += c.length())
    {
      ++count;
    }

    return count;
  }

  public static boolean isEmpty(String str)
  {
    return str == null || str.length() == 0;
  }
}
