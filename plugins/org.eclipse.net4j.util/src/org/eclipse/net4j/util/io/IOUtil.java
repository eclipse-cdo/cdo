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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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

  public static IOException closeSilent(Closeable closeable)
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }

      return null;
    }
    catch (IOException ex)
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

  public static void run(Closeable io, IORunnable runnable) throws IORuntimeException
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

  public static void read(File file, IORunnable<FileReader> runnable) throws IORuntimeException
  {
    try
    {
      FileReader io = new FileReader(file);
      run(io, runnable);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void write(File file, IORunnable<FileWriter> runnable) throws IORuntimeException
  {
    try
    {
      FileWriter io = new FileWriter(file);
      run(io, runnable);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void input(File file, IORunnable<FileInputStream> runnable) throws IORuntimeException
  {
    try
    {
      FileInputStream io = new FileInputStream(file);
      run(io, runnable);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void output(File file, IORunnable<FileOutputStream> runnable) throws IORuntimeException
  {
    try
    {
      FileOutputStream io = new FileOutputStream(file);
      run(io, runnable);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }
}
