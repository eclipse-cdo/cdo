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
package org.eclipse.net4j.util.om.log;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.log.OMLogger.Level;

import java.io.PrintStream;

/**
 * @author Eike Stepper
 */
public class PrintLogHandler implements OMLogHandler
{
  public static final PrintLogHandler CONSOLE = new PrintLogHandler();

  private PrintStream out;

  private PrintStream err;

  public PrintLogHandler(PrintStream out, PrintStream err)
  {
    this.out = out;
    this.err = err;
  }

  protected PrintLogHandler()
  {
    this(IOUtil.OUT(), IOUtil.ERR());
  }

  public void logged(OMLogger logger, Level level, String msg, Throwable t)
  {
    try
    {
      PrintStream stream = level == Level.ERROR ? err : out;
      stream.println(toString(level) + " " + msg); //$NON-NLS-1$
      if (t != null)
      {
        IOUtil.print(t, stream);
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  public static String toString(Level level)
  {
    switch (level)
    {
    case ERROR:
      return "[ERROR]"; //$NON-NLS-1$
    case WARN:
      return "[WARN]"; //$NON-NLS-1$
    case INFO:
      return "[INFO]"; //$NON-NLS-1$
    case DEBUG:
      return "[DEBUG]"; //$NON-NLS-1$
    default:
      throw new IllegalArgumentException("Illegal log level: " + level); //$NON-NLS-1$
    }
  }
}
