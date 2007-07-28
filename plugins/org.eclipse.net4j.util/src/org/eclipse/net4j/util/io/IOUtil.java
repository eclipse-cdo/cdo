/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Eike Stepper
 */
public final class IOUtil
{
  private IOUtil()
  {
  }

  public static InputStream IN()
  {
    return System.in;
  }

  public static PrintStream OUT()
  {
    return System.out;
  }

  public static PrintStream ERR()
  {
    return System.err;
  }

  public static void print(Throwable t, PrintStream stream)
  {
    t.printStackTrace(stream);
  }

  public static void print(Throwable t)
  {
    print(t, System.err);
  }

  public static FileInputStream openInputStream(File file) throws IORuntimeException
  {
    try
    {
      return new FileInputStream(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static FileOutputStream openOutputStream(File file) throws IORuntimeException
  {
    try
    {
      return new FileOutputStream(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static FileReader openReader(File file) throws IORuntimeException
  {
    try
    {
      return new FileReader(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static FileWriter openWriter(File file) throws IORuntimeException
  {
    try
    {
      return new FileWriter(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static Exception closeSilent(Closeable closeable)
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      return ex;
    }
  }

  public static void close(Closeable closeable) throws IORuntimeException
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void copy(InputStream input, OutputStream output, byte buffer[]) throws IORuntimeException
  {
    try
    {
      int n = 0;
      while ((n = input.read(buffer)) != -1)
      {
        output.write(buffer, 0, n);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void copy(InputStream input, OutputStream output, int bufferSize) throws IORuntimeException
  {
    copy(input, output, new byte[bufferSize]);
  }

  public static void copy(InputStream input, OutputStream output) throws IORuntimeException
  {
    copy(input, output, 4096);
  }

  /**
   * @see NIOUtil#copyFile(File, File)
   */
  public static void copyFile(File source, File target) throws IORuntimeException
  {
    FileInputStream input = null;
    FileOutputStream output = null;

    try
    {
      input = new FileInputStream(source);
      output = new FileOutputStream(target);

      copy(input, output);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      closeSilent(input);
      closeSilent(output);
    }
  }

  public static byte[] readFile(File file) throws IORuntimeException
  {
    if (file.length() > Integer.MAX_VALUE)
    {
      throw new IllegalArgumentException("File too long: " + file.length());
    }

    int size = (int)file.length();
    FileInputStream input = openInputStream(file);

    try
    {
      ByteArrayOutputStream output = new ByteArrayOutputStream(size);
      copy(input, output);
      return output.toByteArray();
    }
    finally
    {
      closeSilent(input);
    }
  }

  public static void writeFile(File file, byte[] bytes) throws IORuntimeException
  {
    FileOutputStream output = openOutputStream(file);

    try
    {
      ByteArrayInputStream input = new ByteArrayInputStream(bytes);
      copy(input, output);
    }
    finally
    {
      closeSilent(output);
    }
  }

  public static void safeRun(Closeable io, IORunnable runnable) throws IORuntimeException
  {
    try
    {
      runnable.run(io);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      close(io);
    }
  }

  public static void safeInput(File file, IORunnable<FileInputStream> runnable) throws IORuntimeException
  {
    safeRun(openInputStream(file), runnable);
  }

  public static void safeOutput(File file, IORunnable<FileOutputStream> runnable) throws IORuntimeException
  {
    safeRun(openOutputStream(file), runnable);
  }

  public static void safeRead(File file, IORunnable<FileReader> runnable) throws IORuntimeException
  {
    safeRun(openReader(file), runnable);
  }

  public static void safeWrite(File file, IORunnable<FileWriter> runnable) throws IORuntimeException
  {
    safeRun(openWriter(file), runnable);
  }

  public static boolean equals(InputStream stream1, InputStream stream2) throws IORuntimeException
  {
    try
    {
      for (;;)
      {
        int byte1 = stream1.read();
        int byte2 = stream2.read();

        if (byte1 != byte2)
        {
          return false;
        }

        if (byte1 == -1)// Implies byte2 == -1
        {
          return true;
        }
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }
}
