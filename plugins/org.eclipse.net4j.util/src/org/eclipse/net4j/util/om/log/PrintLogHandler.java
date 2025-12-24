/*
 * Copyright (c) 2007-2009, 2011-2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.log;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.log.OMLogger.Level;

import java.io.PrintStream;

/**
 * A {@link OMLogHandler log handler} that appends log events to {@link #getErr() error} and {@link #getOut() output} streams.
 *
 * @author Eike Stepper
 */
public class PrintLogHandler extends AbstractLogHandler
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

  /**
   * @since 3.2
   */
  public PrintStream getOut()
  {
    return out;
  }

  /**
   * @since 3.2
   */
  public PrintStream getErr()
  {
    return err;
  }

  @Override
  protected void writeLog(OMLogger logger, Level level, String msg, Throwable t) throws Throwable
  {
    PrintStream stream = level == Level.ERROR ? err : out;
    stream.println(toString(level) + " " + msg); //$NON-NLS-1$
    if (t != null)
    {
      IOUtil.print(t, stream);
    }
  }
}
