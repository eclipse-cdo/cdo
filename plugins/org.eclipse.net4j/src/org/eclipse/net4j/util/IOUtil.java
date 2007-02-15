/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Eike Stepper
 */
public final class IOUtil
{
  private static final InputStream IN = System.in;

  private static final PrintStream OUT = System.out;

  private static final PrintStream ERR = System.err;

  private IOUtil()
  {
  }

  public static InputStream IN()
  {
    return IN;
  }

  public static PrintStream OUT()
  {
    return OUT;
  }

  public static PrintStream ERR()
  {
    return ERR;
  }

  public static void print(Throwable t, PrintStream stream)
  {
    t.printStackTrace(stream);
  }

  public static void print(Throwable t)
  {
    print(t, ERR);
  }

  public static Exception closeSilent(InputStream stream)
  {
    try
    {
      if (stream != null)
      {
        stream.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      return ex;
    }
  }

  public static Exception closeSilent(OutputStream stream)
  {
    try
    {
      if (stream != null)
      {
        stream.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      return ex;
    }
  }

  public static Exception closeSilent(Reader reader)
  {
    try
    {
      if (reader != null)
      {
        reader.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      return ex;
    }
  }

  public static Exception closeSilent(Writer writer)
  {
    try
    {
      if (writer != null)
      {
        writer.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      return ex;
    }
  }
}
