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
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.util.om.OMLogger.Level;

import java.io.PrintStream;

/**
 * @author Eike Stepper
 */
public class PrintStreamLogHandler implements OMLogHandler
{
  public static final PrintStreamLogHandler CONSOLE = new PrintStreamLogHandler();

  private PrintStream out;

  private PrintStream err;

  public PrintStreamLogHandler(PrintStream out, PrintStream err)
  {
    this.out = out;
    this.err = err;
  }

  private PrintStreamLogHandler()
  {
    this(System.out, System.err);
  }

  public void logged(OMLogger logger, Level level, String msg, Throwable t)
  {
    PrintStream stream = level == Level.ERROR ? err : out;
    stream.println((toString(level) + " ") + msg); //$NON-NLS-1$
    if (t != null)
    {
      t.printStackTrace(stream);
    }
  }

  private static String toString(Level level)
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
