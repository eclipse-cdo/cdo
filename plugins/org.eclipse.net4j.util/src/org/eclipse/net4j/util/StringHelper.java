/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.nio.ByteBuffer;


public class StringHelper
{

  public static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
      'b', 'c', 'd', 'e', 'f',};

  public static final String UTF8_CHARS = "                                "
      + " !\"#$%&'()*+,-./0123456789:;<=>?" + "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_"
      + "`abcdefghijklmnopqrstuvwxyz{|}~";

  public static final char SEPARATOR = '\n';

  /**
   * This method is used to insert HTML block dynamically
   * 
   * @param source
   *          the HTML code to be processes
   * @param bReplaceNl
   *          if true '\n' will be replaced by <br>
   * @param bReplaceTag
   *          if true ' <' will be replaced by &lt; and '>' will be replaced by &gt;
   * @param bReplaceQuote
   *          if true '\"' will be replaced by &quot;
   */
  public static String formatHtml(String source, boolean bReplaceNl, boolean bReplaceTag,
      boolean bReplaceQuote)
  {

    StringBuffer sb = new StringBuffer();
    int len = source.length();
    for (int i = 0; i < len; i++)
    {
      char c = source.charAt(i);
      switch (c)
      {
        case '\"':
          if (bReplaceQuote)
            sb.append("&quot;");
          else
            sb.append(c);
          break;

        case '<':
          if (bReplaceTag)
            sb.append("&lt;");
          else
            sb.append(c);
          break;

        case '>':
          if (bReplaceTag)
            sb.append("&gt;");
          else
            sb.append(c);
          break;

        case '\n':
          if (bReplaceNl)
          {
            if (bReplaceTag)
              sb.append("&lt;br&gt;");
            else
              sb.append("<br>");
          }
          else
          {
            sb.append(c);
          }
          break;

        case '\r':
          break;

        case '&':
          sb.append("&amp;");
          break;

        default:
          sb.append(c);
          break;
      }
    }
    return sb.toString();
  }

  public static String getLastToken(String str, char separator)
  {
    int pos = str.lastIndexOf(separator);
    if (pos != -1)
    {
      str = str.substring(pos + 1);
    }
    return str;
  }

  public static String getSimpleClassName(Class aClass)
  {
    return getSimpleClassName(aClass.getName());
  }

  public static String getSimpleClassName(String qualifiedClassName)
  {
    return getLastToken(qualifiedClassName, '.');
  }

  /**
   * Pad string object
   */
  public static String pad(String src, char padChar, boolean rightPad, int totalLength)
  {

    int srcLength = src.length();
    if (srcLength >= totalLength)
    {
      return src;
    }

    int padLength = totalLength - srcLength;
    StringBuffer sb = new StringBuffer(padLength);
    for (int i = 0; i < padLength; ++i)
    {
      sb.append(padChar);
    }

    if (rightPad)
    {
      return src + sb.toString();
    }
    else
    {
      return sb.toString() + src;
    }
  }

  public static String removePrefix(String str, String prefix)
  {
    if (str.startsWith(prefix))
    {
      return str.substring(prefix.length());
    }
    return str;
  }

  public static String removeSuffix(String str, String suffix)
  {
    if (str.endsWith(suffix))
    {
      return str.substring(0, str.length() - suffix.length());
    }
    return str;
  }

  /**
   * Replace string
   */
  public static String replaceString(String source, Map args)
  {
    int startIndex = 0;
    int openIndex = source.indexOf('{', startIndex);
    if (openIndex == -1)
    {
      return source;
    }

    int closeIndex = source.indexOf('}', startIndex);
    if ((closeIndex == -1) || (openIndex > closeIndex))
    {
      return source;
    }

    StringBuffer sb = new StringBuffer();
    sb.append(source.substring(startIndex, openIndex));
    while (true)
    {
      String key = source.substring(openIndex + 1, closeIndex);
      Object val = args.get(key);
      if (val != null)
      {
        sb.append(val);
      }

      startIndex = closeIndex + 1;
      openIndex = source.indexOf('{', startIndex);
      if (openIndex == -1)
      {
        sb.append(source.substring(startIndex));
        break;
      }

      closeIndex = source.indexOf('}', startIndex);
      if ((closeIndex == -1) || (openIndex > closeIndex))
      {
        sb.append(source.substring(startIndex));
        break;
      }
      sb.append(source.substring(startIndex, openIndex));
    }
    return sb.toString();
  }

  /**
   * Replace string
   */
  public static String replaceString(String source, Object[] args)
  {
    int startIndex = 0;
    int openIndex = source.indexOf('{', startIndex);
    if (openIndex == -1)
    {
      return source;
    }

    int closeIndex = source.indexOf('}', startIndex);
    if ((closeIndex == -1) || (openIndex > closeIndex))
    {
      return source;
    }

    StringBuffer sb = new StringBuffer();
    sb.append(source.substring(startIndex, openIndex));
    while (true)
    {
      String intStr = source.substring(openIndex + 1, closeIndex);
      int index = Integer.parseInt(intStr);
      sb.append(args[index]);

      startIndex = closeIndex + 1;
      openIndex = source.indexOf('{', startIndex);
      if (openIndex == -1)
      {
        sb.append(source.substring(startIndex));
        break;
      }

      closeIndex = source.indexOf('}', startIndex);
      if ((closeIndex == -1) || (openIndex > closeIndex))
      {
        sb.append(source.substring(startIndex));
        break;
      }
      sb.append(source.substring(startIndex, openIndex));
    }
    return sb.toString();
  }

  /**
   * This is a string replacement method.
   */
  public static String replaceString(String source, String oldStr, String newStr)
  {
    StringBuffer sb = new StringBuffer(source.length());
    int sind = 0;
    int cind = 0;
    while ((cind = source.indexOf(oldStr, sind)) != -1)
    {
      sb.append(source.substring(sind, cind));
      sb.append(newStr);
      sind = cind + oldStr.length();
    }
    sb.append(source.substring(sind));
    return sb.toString();
  }

  public static String replaceWildcards(String source, String param, Object[] args)
  {
    String tmp = source;
    for (int i = 0; i < args.length; i++)
    {
      int pos = tmp.indexOf(param);
      if (pos == -1)
        throw new IllegalArgumentException("source '" + source + "' must contain at least "
            + args.length + " params '" + param + "'");

      String arg = args[i] == null ? "null" : args[i].toString();
      tmp = tmp.substring(0, pos) + arg + tmp.substring(pos + 1);
    }
    return tmp;
  }

  /**
   * Get byte array from hex string
   */
  public static byte[] toByteArray(String hexString)
  {
    int arrLength = hexString.length() >> 1;
    byte buff[] = new byte[arrLength];
    for (int i = 0; i < arrLength; i++)
    {
      int index = i << 1;
      String digit = hexString.substring(index, index + 2);
      buff[i] = (byte) Integer.parseInt(digit, 16);
    }
    return buff;
  }

  /**
   * Append hex string from byte to StringBuffer
   */
  public static void appendHexString(StringBuffer buffer, byte data)
  {
    int positive = data < 0 ? ~data : data;
    buffer.append(HEX_DIGITS[positive >> 4]);
    buffer.append(HEX_DIGITS[positive & 0xf]);
  }

  /**
   * Get hex string from byte
   */
  public static String toHexString(byte data)
  {
    StringBuffer buffer = new StringBuffer();
    appendHexString(buffer, data);
    return buffer.toString();
  }

  /**
   * Get hex string from byte array
   */
  public static String toHexString(byte[] data)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < data.length; i++)
    {
      appendHexString(buffer, data[i]);
      buffer.append(' ');
    }

    return buffer.toString();
  }

  public static String toHexString(ByteBuffer buffer)
  {
    StringBuffer line = new StringBuffer();

    while (buffer.hasRemaining())
    {
      byte dec = buffer.get();
      StringHelper.appendHexString(line, dec);
      line.append(' ');
    }

    return line.toString();
  }

  /**
   * Append hex string from byte to StringBuffer
   */
  public static void appendUTF8String(StringBuffer buffer, byte data, boolean flat)
  {
    if (32 <= data && data < 127)
    {
      char c = UTF8_CHARS.charAt(data);
      buffer.append(c);
    }
    else
    {
      if (!flat)
      {
        buffer.append(".");
      }
    }
  }

  /**
   * Get hex string from byte
   */
  public static String toUTF8String(byte data, boolean flat)
  {
    StringBuffer buffer = new StringBuffer();
    appendUTF8String(buffer, data, flat);
    return buffer.toString();
  }

  /**
   * Get hex string from byte array
   */
  public static String toUTF8String(byte[] data, boolean flat)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < data.length; i++)
    {
      appendUTF8String(buffer, data[i], flat);
    }

    return buffer.toString();
  }

  public static String toUTF8String(ByteBuffer buffer, boolean flat)
  {
    StringBuffer line = new StringBuffer();

    while (buffer.hasRemaining())
    {
      byte dec = buffer.get();
      StringHelper.appendUTF8String(line, dec, flat);
    }

    return line.toString();
  }

  public static boolean parseBoolean(String str) throws NumberFormatException
  {
    if (str.equals("true")) return true;
    if (str.equals("false")) return false;
    throw new NumberFormatException("'" + str + "' is neither 'true' nor 'false'");
  }

  public static String implode(Collection collection, String separator)
  {
    if (collection == null) return null;
    if (collection.size() == 0) return "";

    Iterator iter = collection.iterator();
    StringBuffer result = new StringBuffer(iter.next().toString());

    while (iter.hasNext())
    {
      if (separator != null) result.append(separator);
      result.append(iter.next().toString());
    }

    return result.toString();
  }

  public static String implode(Object[] array, String separator)
  {
    if (array == null) return null;
    if (array.length == 0) return "";

    StringBuffer result = new StringBuffer(array[0].toString());

    for (int i = 1; i < array.length; i++)
    {
      if (separator != null) result.append(separator);
      result.append(array[i].toString());
    }

    return result.toString();
  }

  public static int getChoice(String str, String[] choices)
  {
    if (choices == null) throw new IllegalArgumentException("choices should be non-null");

    for (int i = 0; i < choices.length; i++)
    {
      if ((str == null && choices[i] == null) || str.equals(choices[i]))
      {
        return i;
      }
    }

    return -1;
  }

  public static List parseName(String name, char separator)
  {
    List result = new ArrayList();
    StringBuffer currentWord = new StringBuffer();

    int length = name.length();
    boolean lastIsLower = false;

    for (int index = 0; index < length; index++)
    {
      char curChar = name.charAt(index);
      if (Character.isUpperCase(curChar) || (!lastIsLower && Character.isDigit(curChar))
          || curChar == separator)
      {
        if (lastIsLower || curChar == separator)
        {
          result.add(currentWord.toString());
          currentWord = new StringBuffer();
        }
        lastIsLower = false;
      }
      else
      {
        if (!lastIsLower)
        {
          int currentWordLength = currentWord.length();
          if (currentWordLength > 1)
          {
            char lastChar = currentWord.charAt(--currentWordLength);
            currentWord.setLength(currentWordLength);
            result.add(currentWord.toString());
            currentWord = new StringBuffer();
            currentWord.append(lastChar);
          }
        }
        lastIsLower = true;
      }
      if (curChar != separator)
      {
        currentWord.append(curChar);
      }
    }

    result.add(currentWord.toString());
    return result;
  }

  /**
   * @param text
   */
  public static String firstToLower(String text)
  {
    if (text == null || text.length() == 0) return text;
    if (Character.isUpperCase(text.charAt(0)))
    {
      return text.substring(0, 1).toLowerCase() + text.substring(1);
    }
    return text;
  }

  /**
   * @param text
   */
  public static String firstToUpper(String text)
  {
    if (text == null || text.length() == 0) return text;
    if (Character.isLowerCase(text.charAt(0)))
    {
      return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    return text;
  }

  public static int toInt(String intValue, int defaultValue)
  {
    try
    {
      return Integer.valueOf(intValue).intValue();
    }
    catch (Exception e)
    {
      return defaultValue;
    }
  }

  public static boolean equals(Object obj1, Object obj2)
  {
    if (obj1 == null) return obj2 == null;
    return obj1.equals(obj2);
  }

  public static String formatNull(String str, String nullReplacement)
  {
    return str == null ? nullReplacement : str;
  }

  public static String toNonNull(String str)
  {
    return formatNull(str, "");
  }
}
