/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;


import org.eclipse.net4j.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;


public class IOHelper
{
  /**
   * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
   * 
   * @param bufferSize
   *          Size of internal buffer to use.
   */
  public static void copy(Reader input, Writer output, int bufferSize) throws IOException
  {
    char buffer[] = new char[bufferSize];
    int n = 0;
    while ((n = input.read(buffer)) != -1)
    {
      output.write(buffer, 0, n);
    }
  }

  public static void copy(InputStream input, OutputStream output, byte buffer[]) throws IOException
  {
    int n = 0;
    while ((n = input.read(buffer)) != -1)
    {
      output.write(buffer, 0, n);
    }
  }

  public static void copy(InputStream input, OutputStream output, int bufferSize)
      throws IOException
  {
    copy(input, output, new byte[bufferSize]);
  }

  public static void copy(InputStream input, OutputStream output) throws IOException
  {
    copy(input, output, 4096);
  }

  public static void copy(File input, File output) throws IOException
  {
    FileInputStream fis = null;
    FileOutputStream fos = null;
    try
    {
      fis = new FileInputStream(input);
      fos = new FileOutputStream(output);

      copy(fis, fos);
    }
    finally
    {
      IOUtil.closeSilent(fis);
      IOUtil.closeSilent(fos);
    }
  }

  /**
   * Read fully from reader
   */
  public static String readFully(Reader reader) throws IOException
  {
    StringWriter writer = new StringWriter();
    copy(reader, writer, 1024);
    return writer.toString();
  }

  /**
   * Read fully from stream
   */
  public static String readFully(InputStream input) throws IOException
  {
    InputStreamReader reader = new InputStreamReader(input);
    return readFully(reader);
  }

  /**
   * Read fully from file
   */
  public static String readFully(File file) throws IOException
  {
    FileInputStream stream = null;
    try
    {
      stream = new FileInputStream(file);
      return readFully(stream);
    }
    finally
    {
      IOUtil.closeSilent(stream);
    }
  }
}
