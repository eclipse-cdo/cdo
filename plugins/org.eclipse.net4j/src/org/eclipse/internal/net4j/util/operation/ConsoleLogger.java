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
package org.eclipse.internal.net4j.util.operation;

import java.io.PrintStream;

/**
 * @author Eike Stepper
 */
public class ConsoleLogger extends AbstractLogger
{
  private String loggerName;

  private PrintStream out;

  private PrintStream err;

  public ConsoleLogger(String loggerName, PrintStream out, PrintStream err)
  {
    this.loggerName = loggerName;
    this.out = out;
    this.err = err;
  }

  public ConsoleLogger(String loggerName)
  {
    this(loggerName, System.out, System.err);
  }

  public String getLoggerName()
  {
    return loggerName;
  }

  public void log(Level level, Object plastic)
  {
    PrintStream stream = level == Level.ERROR ? err : out;
    String prefix = toString(level) + " ";
    if (plastic instanceof Throwable)
    {
      Throwable t = (Throwable)plastic;
      stream.println(prefix + t.getLocalizedMessage());
      t.printStackTrace(stream);
    }
    else
    {
      stream.println(prefix + plastic.toString());
    }
  }

  private static String toString(Level level)
  {
    switch (level)
    {
    case ERROR:
      return "[ERROR]";
    case WARN:
      return "[WARN]";
    case INFO:
      return "[INFO]";
    case DEBUG:
      return "[DEBUG]";
    default:
      throw new IllegalArgumentException("Illegal log level: " + level);
    }
  }
}
