/*
 * Copyright (c) 2007, 2009-2012, 2015, 2016, 2020, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.util.io.IORuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Locale;

/**
 * Provides static methods that convert to and from hexadecimal string formats.
 *
 * @author Eike Stepper
 */
public final class HexUtil
{
  public static final char DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };

  private HexUtil()
  {
  }

  /**
   * @since 3.22
   */
  public static String charToHex(char ch)
  {
    return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
  }

  /**
   * @since 3.22
   */
  public static char hexToChar(String s)
  {
    return (char)Integer.parseInt(s, 16);
  }

  /**
   * Converts a byte array into a string of lower case hex chars.
   *
   * @param bs
   *          A byte array
   * @param off
   *          The index of the first byte to read
   * @param length
   *          The number of bytes to read.
   * @return the string of hex chars.
   */
  public static String bytesToHex(byte[] bs, int off, int length)
  {
    if (bs == null)
    {
      return null;
    }

    if (bs.length <= off || bs.length < off + length)
    {
      throw new IllegalArgumentException();
    }

    StringBuilder sb = new StringBuilder(length * 2);
    bytesToHexAppend(bs, off, length, sb);
    return sb.toString();
  }

  public static void bytesToHexAppend(byte[] bs, int off, int length, Appendable appendable)
  {
    if (bs.length <= off || bs.length < off + length)
    {
      throw new IllegalArgumentException();
    }

    if (appendable instanceof StringBuffer)
    {
      StringBuffer buffer = (StringBuffer)appendable;
      buffer.ensureCapacity(buffer.length() + length * 2);
    }

    try
    {
      for (int i = off; i < off + length; i++)
      {
        appendable.append(Character.forDigit(bs[i] >>> 4 & 0xf, 16));
        appendable.append(Character.forDigit(bs[i] & 0xf, 16));
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * @since 3.7
   */
  public static void bytesToHex(InputStream bs, Writer writer)
  {
    try
    {
      int b;
      while ((b = bs.read()) != -1)
      {
        writer.write(Character.forDigit(b >>> 4 & 0xf, 16));
        writer.write(Character.forDigit(b & 0xf, 16));
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static String bytesToHex(byte[] bs)
  {
    if (bs == null)
    {
      return null;
    }

    return bytesToHex(bs, 0, bs.length);
  }

  public static byte[] hexToBytes(String s)
  {
    return hexToBytes(s, 0);
  }

  public static byte[] hexToBytes(String s, int off)
  {
    byte[] bs = new byte[off + (1 + s.length()) / 2];
    hexToBytes(s, bs, off);
    return bs;
  }

  /**
   * Converts a String of hex characters into an array of bytes.
   *
   * @param s
   *          A string of hex characters (upper case or lower) of even length.
   * @param out
   *          A byte array of length at least s.length()/2 + off
   * @param off
   *          The first byte to write of the array
   */
  public static void hexToBytes(String s, byte[] out, int off) throws NumberFormatException, IndexOutOfBoundsException
  {
    int slen = s.length();
    if ((slen & 1) == 1)
    {
      s = '0' + s;
    }

    if (out.length < off + (slen >> 1))
    {
      throw new IndexOutOfBoundsException("Output buffer too small for input (" + out.length + '<' + off + (slen >> 1) //$NON-NLS-1$
          + ')');
    }

    // Safe to assume the string is even length
    byte b1, b2;
    for (int i = 0; i < slen; i += 2)
    {
      b1 = (byte)Character.digit(s.charAt(i), 16);
      b2 = (byte)Character.digit(s.charAt(i + 1), 16);
      if (b1 < 0 || b2 < 0)
      {
        throw new NumberFormatException(s);
      }

      out[off + i / 2] = (byte)(b1 << 4 | b2);
    }
  }

  /**
   * @since 3.7
   */
  public static void hexToBytes(Reader s, OutputStream outputStream) throws NumberFormatException
  {
    try
    {
      int c1, c2;
      byte b1, b2;
      while ((c1 = s.read()) != -1 && (c2 = s.read()) != -1)
      {
        b1 = (byte)Character.digit(c1, 16);
        if (b1 < 0)
        {
          throw new NumberFormatException("Wrong character: " + c1);
        }

        b2 = (byte)Character.digit(c2, 16);
        if (b2 < 0)
        {
          throw new NumberFormatException("Wrong character: " + c2);
        }

        int b = b1 << 4 | b2;
        outputStream.write(b);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static String longToHex(long v)
  {
    String hex = Long.toHexString(v);
    if (hex.length() < 8)
    {
      return "00000000".substring(hex.length()) + hex; //$NON-NLS-1$
    }

    return hex;
  }

  /**
   * @since 3.13
   */
  public static String intToHex(int v)
  {
    String hex = Integer.toHexString(v);
    if (hex.length() < 8)
    {
      return "00000000".substring(hex.length()) + hex; //$NON-NLS-1$
    }

    return hex;
  }

  /**
   * @since 3.13
   */
  public static String identityHashCode(Object object)
  {
    return identityHashCode(object, false);
  }

  /**
   * @since 3.20
   */
  public static String identityHashCode(Object object, boolean trimLeadingZeros)
  {
    int identity = System.identityHashCode(object);
    if (trimLeadingZeros)
    {
      return Integer.toHexString(identity);
    }

    return intToHex(identity);
  }

  @Deprecated
  public static String formatByte(int b)
  {
    assertByte(b);
    return "" + DIGITS[b >> 4] + DIGITS[b & 0xf]; //$NON-NLS-1$
  }

  @Deprecated
  public static String formatBytes(byte[] bytes)
  {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes)
    {
      appendHex(builder, b - Byte.MIN_VALUE);
    }

    return builder.toString();
  }

  @Deprecated
  public static void appendHex(StringBuilder builder, int b)
  {
    assertByte(b);
    builder.append(DIGITS[b >> 4]);
    builder.append(DIGITS[b & 0xf]);
  }

  @Deprecated
  private static void assertByte(int b)
  {
    if (b < 0 || b > 255)
    {
      throw new IllegalArgumentException("b=" + b); //$NON-NLS-1$
    }
  }
}
