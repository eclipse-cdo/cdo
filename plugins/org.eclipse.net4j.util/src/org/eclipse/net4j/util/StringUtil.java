/*
 * Copyright (c) 2007-2013, 2015, 2016, 2018-2021, 2023-2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.util.StringParser.EnumStringParser;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various static helper methods for dealing with strings.
 *
 * @author Eike Stepper
 */
public final class StringUtil
{
  public static final String EMPTY = ""; //$NON-NLS-1$

  public static final String NL = OMPlatform.INSTANCE.getProperty("line.separator"); //$NON-NLS-1$

  /**
   * @since 3.22
   */
  public static final String TRUE = Boolean.TRUE.toString();

  /**
   * @since 3.22
   */
  public static final String FALSE = Boolean.FALSE.toString();

  private static final int NO_SEPARATOR = -1;

  private static final Pattern STRING_CONVERTER_PATTERN = Pattern.compile("\\$\\$\\$([^(]+)\\((.*)\\)\\$\\$\\$");

  private StringUtil()
  {
  }

  /**
   * @since 3.4
   */
  public static String create(char c, int length)
  {
    char[] chars = new char[length];
    Arrays.fill(chars, c);
    return new String(chars);
  }

  /**
   * @since 2.0
   */
  public static String formatException(Throwable t)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream s = new PrintStream(baos);
    t.printStackTrace(s);
    return baos.toString();
  }

  /**
   * @since 3.16
   */
  public static String replace(String text, String find, String replace)
  {
    return replace(text, new String[] { find }, new String[] { replace });
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

  /**
   * @since 3.23
   */
  public static String replace(String str, Map<String, String> replacements)
  {
    if (str != null && replacements != null)
    {
      for (Map.Entry<String, String> entry : replacements.entrySet())
      {
        String key = entry.getKey();
        if (!isEmpty(key))
        {
          String value = safe(entry.getValue());
          str = str.replace(key, value);
        }
      }
    }

    return str;
  }

  /**
   * @since 3.22
   */
  public static void tokenize(String text, String separators, Consumer<String> tokenConsumer)
  {
    StringTokenizer tokenizer = new StringTokenizer(text, separators);
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      tokenConsumer.accept(token);
    }
  }

  /**
   * @since 3.4
   */
  public static List<String> split(String text, String separators, String brackets)
  {
    List<String> result = new ArrayList<>();

    StringBuilder builder = new StringBuilder();
    int length = text.length();
    int bracketLevel = 0;

    for (int i = 0; i < length; i++)
    {
      char c = text.charAt(i);

      if (bracketLevel == 0 && separators.indexOf(c) != -1)
      {
        result.add(builder.toString());
        builder.setLength(0);
      }
      else
      {
        builder.append(c);
      }

      if (brackets != null)
      {
        int bracket = brackets.indexOf(c);
        if (bracket != -1)
        {
          if ((bracket & 1) == 0)
          {
            // Opening bracket
            ++bracketLevel;
          }
          else
          {
            // Closing bracket
            --bracketLevel;
          }
        }
      }
    }

    result.add(builder.toString());
    return result;
  }

  public static String safe(String str)
  {
    return safe(str, EMPTY);
  }

  /**
   * @since 3.4
   */
  public static String safe(String str, String def)
  {
    if (str == null)
    {
      return def;
    }

    return str;
  }

  /**
   * @since 3.13
   */
  public static String safe(Object object)
  {
    return safe(object, EMPTY);
  }

  /**
   * @since 3.13
   */
  public static String safe(Object object, String def)
  {
    if (object == null)
    {
      return def;
    }

    String str = object.toString();
    return safe(str, def);
  }

  /**
   * @since 3.22
   */
  public static String escape(String str)
  {
    return escape(str, NO_SEPARATOR);
  }

  /**
   * @since 3.22
   */
  public static String escape(String str, char separator)
  {
    return escape(str, (int)separator);
  }

  private static String escape(String str, int separator)
  {
    if (str == null)
    {
      return null;
    }

    int len = str.length();
    StringBuilder builder = new StringBuilder(len);

    for (int i = 0; i < len; i++)
    {
      char c = str.charAt(i);

      if (separator != NO_SEPARATOR && c == separator)
      {
        builder.append('\\');
        builder.append('s');
      }
      else if (c > 0xfff)
      {
        builder.append("\\u" + HexUtil.charToHex(c)); //$NON-NLS-1$
      }
      else if (c > 0xff)
      {
        builder.append("\\u0" + HexUtil.charToHex(c)); //$NON-NLS-1$
      }
      else if (c > 0x7f)
      {
        builder.append("\\u00" + HexUtil.charToHex(c)); //$NON-NLS-1$
      }
      else if (c < 32)
      {
        switch (c)
        {
        case '\r':
          builder.append('\\');
          builder.append('r');
          break;

        case '\n':
          builder.append('\\');
          builder.append('n');
          break;

        case '\t':
          builder.append('\\');
          builder.append('t');
          break;

        case '\f':
          builder.append('\\');
          builder.append('f');
          break;

        case '\b':
          builder.append('\\');
          builder.append('b');
          break;

        default:
          if (c > 0xf)
          {
            builder.append("\\u00" + HexUtil.charToHex(c)); //$NON-NLS-1$
          }
          else
          {
            builder.append("\\u000" + HexUtil.charToHex(c)); //$NON-NLS-1$
          }
        }
      }
      else if (c == '\\')
      {
        builder.append('\\');
        builder.append('\\');
      }
      else
      {
        builder.append(c);
      }
    }

    return builder.toString();
  }

  /**
   * @since 3.22
   */
  public static String unescape(String str)
  {
    return unescape(str, NO_SEPARATOR);
  }

  /**
   * @since 3.22
   */
  public static String unescape(String str, char separator)
  {
    return unescape(str, (int)separator);
  }

  private static String unescape(String str, int separator)
  {
    if (str == null)
    {
      return null;
    }

    int len = str.length();
    StringBuilder builder = new StringBuilder(len);

    StringBuilder unicodeBuilder = new StringBuilder(4);
    boolean unicode = false;
    boolean slash = false;

    for (int i = 0; i < len; i++)
    {
      char c = str.charAt(i);
      if (unicode)
      {
        unicodeBuilder.append(c);
        if (unicodeBuilder.length() == 4)
        {
          try
          {
            char value = HexUtil.hexToChar(unicodeBuilder.toString());
            builder.append(value);
            unicodeBuilder.setLength(0);
            unicode = false;
            slash = false;
          }
          catch (NumberFormatException ex)
          {
            builder.append('\\');
            builder.append('u');
            builder.append(unicodeBuilder);
          }
        }

        continue;
      }

      if (slash)
      {
        slash = false;

        switch (c)
        {
        case '\\':
          builder.append('\\');
          break;

        case 'r':
          builder.append('\r');
          break;

        case 'n':
          builder.append('\n');
          break;

        case 't':
          builder.append('\t');
          break;

        case 'f':
          builder.append('\f');
          break;

        case 'b':
          builder.append('\b');
          break;

        case 's':
          if (separator != NO_SEPARATOR)
          {
            builder.append((char)separator);
          }
          break;

        case 'u':
          unicode = true;
          break;

        case '0':
        case '1':
        case '2':
        case '3':
          if (i + 2 < len && str.charAt(i + 1) >= '0' && str.charAt(i + 1) <= '7' && str.charAt(i + 2) >= '0' && str.charAt(i + 2) <= '7')
          {
            builder.append((char)Integer.parseInt(str.substring(i, i + 3), 8));
            i += 2;
            continue;
          }

          //$FALL-THROUGH$
        default:
        {
          builder.append(c);
        }
        }

        continue;
      }
      else if (c == '\\')
      {
        slash = true;
        continue;
      }

      builder.append(c);
    }

    if (slash)
    {
      builder.append('\\');
    }

    return builder.toString();
  }

  /**
   * @since 3.26
   */
  public static String stripHTML(String html)
  {
    try
    {
      StringBuilder builder = new StringBuilder();

      new ParserDelegator().parse(new StringReader(html), new HTMLEditorKit.ParserCallback()
      {
        @Override
        public void handleText(char[] text, int pos)
        {
          builder.append(text);
        }

        @Override
        public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos)
        {
          if (t.breaksFlow())
          {
            builder.append("\n"); //$NON-NLS-1$
          }
        }
      }, Boolean.TRUE);

      return builder.toString();
    }
    catch (LinkageError ex)
    {
      // If the JDK doesn't provide AWT support this can happen.
      // https://bugs.eclipse.org/bugs/show_bug.cgi?id=544340
      return html;
    }
    catch (IOException ex)
    {
      return html;
    }
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

  /**
   * @since 3.23
   */
  public static int compareIgnoreCase(String s1, String s2)
  {
    if (s1 == null)
    {
      return s2 == null ? 0 : -1;
    }

    if (s2 == null)
    {
      return 1;
    }

    return s1.toLowerCase().compareTo(s2.toLowerCase());
  }

  /**
   * @since 3.23
   */
  public static boolean equalsIgnoreCase(String s1, String s2)
  {
    if (s1 == null)
    {
      return s2 == null;
    }

    return s1.equalsIgnoreCase(s2);
  }

  /**
   * @since 3.22
   */
  public static boolean isTrue(String s)
  {
    return TRUE.equalsIgnoreCase(s);
  }

  /**
   * @since 3.22
   */
  public static boolean isFalse(String s)
  {
    return FALSE.equalsIgnoreCase(s);
  }

  /**
   * @since 2.0
   */
  public static String capAll(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    boolean inWhiteSpace = true;
    StringBuilder builder = new StringBuilder(str);
    for (int i = 0; i < builder.length(); i++)
    {
      char c = builder.charAt(i);
      boolean isWhiteSpace = Character.isWhitespace(c);
      if (!isWhiteSpace && inWhiteSpace)
      {
        builder.setCharAt(i, Character.toUpperCase(c));
      }

      inWhiteSpace = isWhiteSpace;
    }

    return builder.toString();
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

  /**
   * @since 2.0
   */
  public static String uncapAll(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    boolean inWhiteSpace = true;
    StringBuilder builder = new StringBuilder(str);
    for (int i = 0; i < builder.length(); i++)
    {
      char c = builder.charAt(i);
      boolean isWhiteSpace = Character.isWhitespace(c);
      if (!isWhiteSpace && inWhiteSpace)
      {
        builder.setCharAt(i, Character.toLowerCase(c));
      }

      inWhiteSpace = isWhiteSpace;
    }

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

  /**
   * @since 3.8
   */
  public static String translate(String str, String from, String to)
  {
    int length = str == null ? 0 : str.length();
    if (length == 0)
    {
      return str;
    }

    int fromLength = from.length();
    int toLength = to.length();

    if (fromLength == 0)
    {
      return str;
    }

    if (fromLength > toLength)
    {
      throw new IllegalArgumentException("'from' is longer than 'to'");
    }

    StringBuilder builder = null;
    for (int i = 0; i < length; i++)
    {
      char c = str.charAt(i);

      int pos = from.indexOf(c);
      if (pos != -1)
      {
        c = to.charAt(pos);

        if (builder == null)
        {
          builder = new StringBuilder(str);
        }

        builder.setCharAt(i, c);
      }
    }

    if (builder == null)
    {
      return str;
    }

    return builder.toString();
  }

  /**
   * @since 3.23
   */
  public static String convert(String str, IManagedContainer container)
  {
    return convert(str, container, STRING_CONVERTER_PATTERN);
  }

  /**
   * @since 3.23
   */
  public static String convert(String str, IManagedContainer container, Pattern pattern)
  {
    if (str != null && container != null)
    {
      StringBuilder builder = new StringBuilder();
      int start = 0;

      for (Matcher matcher = pattern.matcher(str); matcher.find(start);)
      {
        builder.append(str.substring(start, matcher.start()));

        String stringConverterType = matcher.group(1);
        StringConverter stringConverter = container.getElementOrNull(StringConverter.PRODUCT_GROUP, stringConverterType);
        if (stringConverter != null)
        {
          String string = matcher.group(2);
          String result = stringConverter.apply(string);
          builder.append(safe(result));
        }
        else
        {
          builder.append(matcher.group());
        }

        start = matcher.end();
      }

      builder.append(str.substring(start));
      str = builder.toString();
    }

    return str;
  }

  /**
   * @since 3.23
   */
  public static <T> T parse(String str, IManagedContainer container, Class<T> type)
  {
    return parse(str, container, type, EnumStringParser.DEFAULT_CASE_SENSITIVE);
  }

  /**
   * @since 3.23
   */
  public static <T> T parse(String str, IManagedContainer container, Class<T> type, boolean enumCaseSensitive)
  {
    if (str != null && container != null && type != null)
    {
      String typeName = type.getName();

      StringParser<T> parser = container.getElementOrNull(StringParser.PRODUCT_GROUP, typeName);
      if (parser == null)
      {
        if (type.isEnum())
        {
          @SuppressWarnings({ "unchecked", "rawtypes" })
          StringParser<T> enumParser = new EnumStringParser(type, enumCaseSensitive);
          return enumParser.apply(str);
        }

        try
        {
          Constructor<T> constructor = type.getConstructor(String.class);
          return constructor.newInstance(str);
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }

        throw new IllegalStateException("A " + typeName + " could not be created for the string '" + str + "'");
      }

      return parser.apply(str);
    }

    return null;
  }

  public static boolean isEmpty(String str)
  {
    return ObjectUtil.isEmpty(str);
  }

  /**
   * @since 3.16
   */
  public static boolean appendSeparator(StringBuilder builder, String str)
  {
    if (builder.length() != 0)
    {
      builder.append(str);
      return true;
    }

    return false;
  }

  /**
   * @since 3.16
   */
  public static boolean appendSeparator(StringBuilder builder, char c)
  {
    if (builder.length() != 0)
    {
      builder.append(c);
      return true;
    }

    return false;
  }

  /**
   * Matches a string against a pattern.
   * <p>
   * Pattern description:
   * <ul>
   * <li><code>*</code> matches 0 or more characters
   * <li><code>?</code> matches a single character
   * <li><code>[...]</code> matches a set and/or range of characters
   * <li><code>\</code> escapes the following character
   * </ul>
   *
   * @since 2.0
   */
  public static boolean glob(String pattern, String string)
  {
    return glob(pattern, string, null);
  }

  /**
   * Matches a string against a pattern and fills an array with the sub-matches.
   * <p>
   * Pattern description:
   * <ul>
   * <li><code>*</code> matches 0 or more characters
   * <li><code>?</code> matches a single character
   * <li><code>[...]</code> matches a set and/or range of characters
   * <li><code>\</code> escapes the following character
   * </ul>
   *
   * @since 2.0
   */
  public static boolean glob(String pattern, String string, String[] subStrings)
  {
    return globRecurse(pattern, 0, string, 0, subStrings, 0);
  }

  private static boolean globRecurse(String pattern, int patternIndex, String string, int stringIndex, String[] subStrings, int subStringsIndex)
  {
    int patternLength = pattern.length();
    int stringLength = string.length();

    for (;;)
    {
      char patternChar = pattern.charAt(patternIndex);
      boolean endReached = stringIndex == stringLength;
      if (patternIndex == patternLength)
      {
        return endReached;
      }
      else if (endReached && patternChar != '*')
      {
        return false;
      }

      switch (patternChar)
      {
      case '*':
      {
        int startIndex = stringIndex;
        if (++patternIndex >= patternLength)
        {
          globRemember(string, startIndex, stringLength, subStrings, subStringsIndex);
          return true;
        }

        for (;;)
        {
          if (globRecurse(pattern, patternIndex, string, stringIndex, subStrings, subStringsIndex + 1))
          {
            globRemember(string, startIndex, stringIndex, subStrings, subStringsIndex);
            return true;
          }

          if (endReached)
          {
            return false;
          }

          ++stringIndex;
        }
      }

      case '?':
        ++patternIndex;
        globRemember(string, stringIndex, ++stringIndex, subStrings, subStringsIndex++);
        break;

      case '[':
        try
        {
          ++patternIndex;
          char stringChar = string.charAt(stringIndex);
          char rangeStartChar = patternChar;

          while (true)
          {
            if (rangeStartChar == ']')
            {
              return false;
            }

            if (rangeStartChar == stringChar)
            {
              break;
            }

            ++patternIndex;
            char nextPatternChar = patternChar;
            if (nextPatternChar == '-')
            {
              ++patternIndex;
              char rangeEndChar = patternChar;
              if (rangeStartChar <= stringChar && stringChar <= rangeEndChar)
              {
                break;
              }

              ++patternIndex;
              nextPatternChar = patternChar;
            }

            rangeStartChar = nextPatternChar;
          }

          patternIndex = pattern.indexOf(']', patternIndex) + 1;
          if (patternIndex <= 0)
          {
            return false;
          }

          globRemember(string, stringIndex, ++stringIndex, subStrings, subStringsIndex++);
        }
        catch (StringIndexOutOfBoundsException ex)
        {
          return false;
        }

        break;

      case '\\':
        if (++patternIndex >= patternLength)
        {
          return false;
        }

        //$FALL-THROUGH$
      default:
        if (patternChar++ != string.charAt(stringIndex++))
        {
          return false;
        }
      }
    }
  }

  private static void globRemember(String string, int start, int end, String[] subStrings, int subStringsIndex)
  {
    if (subStrings != null && subStringsIndex < subStrings.length)
    {
      subStrings[subStringsIndex] = string.substring(start, end);
    }
  }

  /**
   * @since 3.1
   * @deprecated As of 3.23 use {@link #equalsIgnoreCase(String, String)}.
   */
  @Deprecated
  public static boolean equalsUpperOrLowerCase(String s, String upperOrLowerCase)
  {
    return equalsIgnoreCase(s, upperOrLowerCase);
  }
}
